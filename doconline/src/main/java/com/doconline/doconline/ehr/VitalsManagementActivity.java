package com.doconline.doconline.ehr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.ehr.model.Vital;
import com.doconline.doconline.ehr.model.VitalTemplate;
import com.doconline.doconline.ehr.model.VitalTemplateResponse;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.DatePickerFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.NumberInputFilter;
import com.doconline.doconline.helper.TimePickerFragment;
import com.doconline.doconline.profile.ProfileActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DOB_REQUIRE;
import static com.doconline.doconline.app.Constants.KEY_DATA;


public class VitalsManagementActivity extends BaseActivity implements View.OnClickListener
{
    Toolbar toolbar;
    TextView toolbar_title;
    CoordinatorLayout layout_root_view;
    RelativeLayout layout_refresh;
    RelativeLayout layout_block_ui;
    LinearLayout datepicker_layout;
    TextView tv_date;

    Button btnDone;

    LinearLayout layout_main;


    private List<VitalTemplate> dataList;
    private List<LinearLayout> formFieldList = new ArrayList<>();

    private static final int HTTP_REQUEST_CODE_POST_VITAL = 1;
    private static final int HTTP_REQUEST_CODE_PUT_VITAL = 2;
    private static final int HTTP_REQUEST_CODE_GET_VITAL_TEMPLATE = 3;

    private Vital vitalData;
    private String date = "";
    private StringBuilder datetime = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals_management);
        //ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title= findViewById(R.id.toolbar_title);
        layout_root_view= findViewById(R.id.layout_root_view);
        layout_refresh= findViewById(R.id.layout_refresh);
        layout_block_ui= findViewById(R.id.layout_block_ui);
        datepicker_layout= findViewById(R.id.datepicker_layout);
       tv_date= findViewById(R.id.tv_date);
        btnDone= findViewById(R.id.btnDone);

       layout_main= findViewById(R.id.layout_main);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.addListener();

        btnDone.setText(getResources().getString(R.string.Add));
        toolbar_title.setText("Add New Vital");
        btnDone.setVisibility(View.GONE);
        layout_main.setVisibility(View.GONE);

        if(getIntent().getSerializableExtra("VITAL") != null)
        {
            this.vitalData = (Vital) getIntent().getSerializableExtra("VITAL");
            btnDone.setText(getResources().getString(R.string.Update));
            toolbar_title.setText("Edit Vital");
        }

        this.syncTemplate();
    }


    @Override
    public void onResume()
    {
        super.onResume();

        /*try
        {
            this.checkDOB();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }


    /*private void checkDOB()
    {
        User mUser = mController.getSQLiteHelper().getUserProfile();

        if(mUser.getDateOfBirth().isEmpty())
        {
            this.displayDOBAlert();
        }

        this.date_of_birth = mUser.getDateOfBirth();
    }*/


    /*private int getYearDiff()
    {
        if(!this.date_of_birth.isEmpty() && !this.date.isEmpty())
        {
            return Helper.getYearDiff(this.date_of_birth, this.date);
        }

        return -1;
    }*/


    /*private void displayDOBAlert()
    {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_DOB_REQUIRE, this)
                .showDialogWithAction("Info", "DOB is require for vital entry. Please update DOB from your account.",
                        getResources().getString(R.string.OK), getResources().getString(R.string.Cancel), false);
    }*/


    private void displayDate(Vital vitalData)
    {
        if(vitalData == null)
        {
            return;
        }

        try
        {
            if(vitalData.getRecordedAt() != 0)
            {
                long recorded_at = vitalData.getRecordedAt() * 1000L;

                SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A);
                String timestamp = formatter.format(recorded_at);

                tv_date.setText(timestamp);

                formatter = new SimpleDateFormat(Constants.YYYY_MM_DD);
                date = formatter.format(recorded_at);

                formatter = new SimpleDateFormat(Constants.DD_MM_YYYY_HH_MM_SS);
                datetime = new StringBuilder().append(formatter.format(recorded_at));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void addListener()
    {
        datepicker_layout.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    // to get the label and unit and type.
    private void syncTemplate()
    {
        if (new InternetConnectionDetector(this).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_VITAL_TEMPLATE, MyApplication.HTTPMethod.GET.getValue(),this)
                    .execute(mController.getVitalTemplateURL());
        }

        else
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.action_info) {// to show the static image
            Intent intent1 = new Intent(VitalsManagementActivity.this, VitalInfoActivity.class);
            intent1.putExtra("URL", Constants.VITAL_INFO_URL);
            startActivity(intent1);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.datepicker_layout) {
            tv_date.setError(null);

            this.showDatePicker("");
        } else if (id == R.id.ib_clear_date) {
            tv_date.setText("");
        } else if (id == R.id.btnDone) {
            this.save();
        }
    }


    private void save()
    {
        JSONObject recordJSON = getRecordJSON();

        if(!validate() || !validateJSON(recordJSON))
        {
            return;
        }

        if(new InternetConnectionDetector(this).isConnected())
        {
            JSONObject jsonRoot = new JSONObject();

            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MM_YYYY_HH_MM_SS);
                Date dateObj = sdf.parse(datetime.toString());

                jsonRoot.put("records", recordJSON);
                jsonRoot.put("recorded_at", (dateObj.getTime() / 1000L));
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(getIntent().getSerializableExtra("VITAL") != null)
            {
                // sending updated data to the server if vital obj is not null
                Vital data = (Vital) getIntent().getSerializableExtra("VITAL");

                new HttpClient(HTTP_REQUEST_CODE_PUT_VITAL, MyApplication.HTTPMethod.PUT.getValue(), true, jsonRoot.toString(), this)
                        .execute(mController.getVitalURL() + "/" + data.getId());
            }

            else
            {
                // sending updated data to the server if vital obj is null
                new HttpClient(HTTP_REQUEST_CODE_POST_VITAL, MyApplication.HTTPMethod.POST.getValue(), true, jsonRoot.toString(), this)
                        .execute(mController.getVitalURL());
            }
        }

        else
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
        }
    }


    private boolean validate()
    {
        if(datetime.toString().length() == 0)
        {
            tv_date.setError("Date and Time Require");
            tv_date.requestFocus();
            Toast.makeText(getApplicationContext(), "Select Date and Time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void showDatePicker(String display_date)
    {
        try
        {
            DatePickerFragment date = new DatePickerFragment();

            Calendar calender = Calendar.getInstance();

            Bundle args = new Bundle();
            args.putInt("min_year", calender.get(Calendar.YEAR) - 100);
            args.putInt("max_year", calender.get(Calendar.YEAR));

            if(!display_date.isEmpty())
            {
                String[] temp = display_date.split("/");

                args.putInt("year", Integer.valueOf(temp[2]));
                args.putInt("month", (Integer.valueOf(temp[1])-1));
                args.putInt("day", Integer.valueOf(temp[0]));
            }

            else
            {
                args.putInt("year", calender.get(Calendar.YEAR));
                args.putInt("month", calender.get(Calendar.MONTH));
                args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
            }

            date.setArguments(args);

            date.setCallBack(ondate);
            date.show(getSupportFragmentManager(), "Select Date");
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void showTimePicker()
    {
        TimePickerFragment time = new TimePickerFragment();

        final Calendar calender = Calendar.getInstance();

        Bundle args = new Bundle();

        args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        time.setArguments(args);

        time.setCallBack(onTime);
        time.show(getSupportFragmentManager(), "Select Time");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Calendar choosen = Calendar.getInstance();
            choosen.set(year, monthOfYear, dayOfMonth);

            date = new StringBuilder().append(Helper.format_date(year)).append("-").append(Helper.format_date(monthOfYear + 1)).append("-").append(Helper.format_date(dayOfMonth)).toString();
            showTimePicker();
        }
    };

    /**
     * Launch Time Picker Dialog
     */
    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            String time = new StringBuilder().append(Helper.formatNumber(hourOfDay)).append(":").append(Helper.formatNumber(minute)).append(":00").toString();
            datetime = new StringBuilder().append(Helper.dateFormat(date)).append(" ").append(time);

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MM_YYYY_HH_MM_SS);

            try
            {
                Date date1 = sdf.parse(datetime.toString());
                Date date2 = sdf.parse(sdf.format(System.currentTimeMillis()));

                if(date1.after(date2))
                {
                    Toast.makeText(getApplicationContext(), "Future Date and Time Not Allowed", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A);
                    tv_date.setText(formatter.format(date1));
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onPreExecute()
    {
        if(isFinishing())
        {
            return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_refresh.setVisibility(View.VISIBLE);
                layout_block_ui.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPostExecute(final int requestCode, int responseCode, String response)
    {
        if(isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_POST_VITAL && (responseCode == HttpClient.CREATED || responseCode == HttpClient.OK))
            {
                Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                setResult(100, intent);
                finish();
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_PUT_VITAL && (responseCode == HttpClient.CREATED || responseCode == HttpClient.OK))
            {
                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                setResult(100, intent);
                finish();
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_GET_VITAL_TEMPLATE && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    VitalTemplateResponse templateData = new Gson().fromJson(json.toString(), VitalTemplateResponse.class);

                    this.dataList = templateData.getRecordFields();
                    // creating form with label
                    this.createForm();
                    // setting the data in edittext
                    update(vitalData);
                    // display date data which is static
                    displayDate(vitalData);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(this, this, layout_root_view).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    layout_refresh.setVisibility(View.INVISIBLE);
                    layout_block_ui.setVisibility(View.GONE);
                }
            });
        }
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_DOB_REQUIRE)
        {
            ProfileActivity.start(VitalsManagementActivity.this);
        }
    }


    @Override
    public void onNegativeAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_DOB_REQUIRE)
        {
            finish();
        }
    }

    private void update(Vital vitalData)
    {
        if(vitalData == null || dataList == null)
        {
            return;
        }

        for(int i=0; i<dataList.size(); i++)
        {
            dataList.set(i, template(vitalData.getVitals(), dataList.get(i)));
        }

        for(int parent=0; parent<dataList.size(); parent++)
        {
            if(dataList.get(parent).hasChild() && dataList.get(parent).getKey().equalsIgnoreCase("blood_pressure"))
            {
                try
                {
                    JSONObject json = new JSONObject(dataList.get(parent).getValue().toString());

                    Object mm = json.get("blood_pressure_mm");
                    Object hg = json.get("blood_pressure_hg");

                    setFieldValue((FrameLayout) formFieldList.get(parent).getChildAt(0), mm);
                    setFieldValue((FrameLayout) formFieldList.get(parent).getChildAt(1), hg);
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            else
            {
                setFieldValue((FrameLayout) formFieldList.get(parent).getChildAt(0), dataList.get(parent).getValue());
            }
        }
    }


    private VitalTemplate template(List<VitalTemplate> dataList, VitalTemplate vital)
    {
        for(VitalTemplate data: dataList)
        {
            if(data.getType().equalsIgnoreCase(vital.getKey()))
            {
                vital.setValue(data.getValue());
                break;
            }
        }

        return vital;
    }

    private void setEditTextValue(EditText editValue, VitalTemplate data)
    {
        editValue.setText(String.valueOf(data.getValue() == null ? "" : data.getValue()));
    }


    private void createForm()
    {
        btnDone.setVisibility(View.VISIBLE);
        layout_main.setVisibility(View.VISIBLE);

        for(VitalTemplate data: this.dataList)
        {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            if(data.hasChild())
            {
                layout.setOrientation(LinearLayout.HORIZONTAL);

                for(VitalTemplate child: data.getChildField())
                {
                    // sets label to edittext dynamically.
                    child.setLabel(data.getLabel());
                    layout.addView(getLayout(child));
                }

                for(int i=0; i<layout.getChildCount(); i++)
                {
                    layout.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                }
            }

            else
            {
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(getLayout(data));
            }

            layout_main.addView(layout);
            formFieldList.add(layout);
        }
    }


    private FrameLayout getLayout(VitalTemplate data)
    {
        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_edittext_vital, null);

        TextInputLayout inputLayout = (TextInputLayout) layout.getChildAt(0);

        EditText editValue = inputLayout.getEditText();
        editValue.setFilters(new InputFilter[] { new NumberInputFilter(5, 2)} );

        setEditTextValue(editValue, data);

        TextView tvUnit = (TextView) layout.getChildAt(1);

        if(data.getUnit() != null)
        {
            tvUnit.setVisibility(View.VISIBLE);
            tvUnit.setText(data.getUnit());
        }

        else
        {
            tvUnit.setVisibility(View.GONE);
        }

        inputLayout.setHint(data.getLabel());

        return layout;
    }


    private Object getFieldValue(FrameLayout layout)
    {
        TextInputLayout inputLayout = (TextInputLayout) layout.getChildAt(0);
        return inputLayout.getEditText().getText();
    }

    private void setFieldValue(FrameLayout layout, Object data)
    {
        TextInputLayout inputLayout = (TextInputLayout) layout.getChildAt(0);
        inputLayout.getEditText().setText(String.valueOf(data == null ? "" : data));
        inputLayout.getEditText().getText();
    }


    private JSONObject getRecordJSON()
    {
        JSONObject jsonRecord = new JSONObject();

        try
        {
            for(int parent=0; parent<formFieldList.size(); parent++)
            {
                if(dataList.get(parent).hasChild())
                {
                    JSONObject jsonChild = new JSONObject();

                    for(int child=0; child<dataList.get(parent).getChildField().size(); child++)
                    {
                        jsonChild.put(dataList.get(parent).getChildField().get(child).getKey(), getFieldValue((FrameLayout) formFieldList.get(parent).getChildAt(child)));
                    }

                    jsonRecord.put(dataList.get(parent).getKey(), jsonChild);
                }

                else
                {
                    jsonRecord.put(dataList.get(parent).getKey(), getFieldValue((FrameLayout) formFieldList.get(parent).getChildAt(0)));
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return jsonRecord;
    }


    private boolean validateJSON(JSONObject json)
    {
        Iterator<String> keys = json.keys();

        try
        {
            JSONObject bpJSON = json.getJSONObject("blood_pressure");

            Object mm = bpJSON.get("blood_pressure_mm");
            Object hg = bpJSON.get("blood_pressure_hg");

            if(!mm.toString().isEmpty() && !hg.toString().isEmpty())
            {
                return true;
            }

            else if(mm.toString().isEmpty() && hg.toString().isEmpty())
            {
                while (keys.hasNext())
                {
                    String key = keys.next();
                    String value = json.getString(key);

                    if(!key.equalsIgnoreCase("blood_pressure") && !value.isEmpty())
                    {
                        return true;
                    }
                }

                Toast.makeText(getApplicationContext(), "Please fill atleast one field", Toast.LENGTH_SHORT).show();
            }

            else
            {
                Toast.makeText(getApplicationContext(), "Please enter values in both mm and Hg fields", Toast.LENGTH_SHORT).show();
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return false;
    }
}