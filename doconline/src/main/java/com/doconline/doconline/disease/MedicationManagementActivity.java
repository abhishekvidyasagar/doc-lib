package com.doconline.doconline.disease;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.DatePickerFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.TimePickerFragment;
import com.doconline.doconline.medicalremainders.adapter.SelectMedicineListAdapter;
import com.doconline.doconline.model.Medication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MEDICATION;
import static com.doconline.doconline.app.Constants.YYYY_MM_DD;


public class MedicationManagementActivity extends BaseActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        View.OnFocusChangeListener,
        SearchView.OnCloseListener,
        TextView.OnEditorActionListener
{

    Toolbar toolbar;

    TextView toolbar_title;

    CoordinatorLayout layout_root_view;

    RelativeLayout layout_refresh;

    RelativeLayout layout_block_ui;


    EditText edit_notes;

    TextView tv_from;

    TextView tv_till;

    TextView tv_intake_time;

    LinearLayout datepicker_layout_from;

    LinearLayout datepicker_layout_till;

    LinearLayout timepicker_layout;

    SwitchCompat switch_alarm;

    Button btnDone;


    EditText editNoOfDays;


    TextView editStatus;


    ListView listviewMedicines;
    SearchView searchViewMedicines;
    ProgressBar pbProgressBar;
    TextView textView;

    private int date_selector_type = -1;
    private Medication mMedication;
    private String till_date = "", from_date = "";

    public static final int HTTP_REQUEST_CODE_ADD_MEDICATION = 1;
    public static final int HTTP_REQUEST_CODE_UPDATE_MEDICATION = 2;
    public static final int HTTP_REQUEST_CODE_MEDICINES_LIST = 3;

    JsonArray jsonArraymedicines = new JsonArray();

    Boolean clickedStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_management);
        //ButterKnife.bind(this);

         toolbar = findViewById(R.id.toolbar);
         toolbar_title = findViewById(R.id.toolbar_title);
        layout_root_view = findViewById(R.id.layout_root_view);
         layout_refresh = findViewById(R.id.layout_refresh);
         layout_block_ui = findViewById(R.id.layout_block_ui);

         edit_notes = findViewById(R.id.editNotes);
        tv_from = findViewById(R.id.tv_from);
         tv_till = findViewById(R.id.tv_till);
         tv_intake_time = findViewById(R.id.tv_intake_time);
        datepicker_layout_from = findViewById(R.id.datepicker_layout_from);
       datepicker_layout_till = findViewById(R.id.datepicker_layout_till);
        timepicker_layout = findViewById(R.id.timepicker_layout);
        switch_alarm = findViewById(R.id.switch_alarm);
         btnDone = findViewById(R.id.btnDone);

        editNoOfDays = findViewById(R.id.editNoOfDays);

        editStatus = findViewById(R.id.editStatus);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.addListener();

        if(getIntent().getSerializableExtra("MEDICATION") != null)
        {
            mMedication = (Medication) getIntent().getSerializableExtra("MEDICATION");
            display_medication();
        }

        toolbar_title.setText(getResources().getString(R.string.text_medications));
        btnDone.setText(R.string.Save);

        listviewMedicines = findViewById(R.id.medicines_listview);
        searchViewMedicines = findViewById(R.id.search_medicines);
        pbProgressBar = findViewById(R.id.progressBar);

        searchViewMedicines.setIconified(true);
        searchViewMedicines.setIconifiedByDefault(false);
        try {
            int searchTextId = searchViewMedicines.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            textView = searchViewMedicines.findViewById(searchTextId);
            textView.setTextColor(Color.BLACK);
            textView.setHintTextColor(Color.LTGRAY);
            textView.setOnEditorActionListener(this);
            /*if (getIntent().hasExtra("drugname")){
                actionType = "edit";
                drugName = getIntent().getExtras().get("drugname").toString();
                drugId = getIntent().getExtras().get("drugid").toString();
                textView.setText(drugName);

            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        searchViewMedicines.setOnQueryTextListener(this);
        searchViewMedicines.setOnQueryTextFocusChangeListener(this);
        searchViewMedicines.setOnCloseListener(this);

        listviewMedicines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JsonObject medicine = jsonArraymedicines.get(position).getAsJsonObject();
                JsonObject requestObject = new JsonObject();
                clickedStatus = true;
                //requestObject.addProperty("name", medicine.get("medicineName").getAsString());
                textView.setText(""+medicine.get("medicineName").getAsString());
                listviewMedicines.setVisibility(View.GONE);

            }
        });


    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (clickedStatus){
            listviewMedicines.setVisibility(View.GONE);
        }else {
            if (newText.length() >= 3) {
                currentSearchString(newText.replace(" ", ""));
            }
            return false;
        }
        return false;
    }


    public void currentSearchString(String str) {
        if (str.length() >= 3) {

            if (new InternetConnectionDetector(this).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_MEDICINES_LIST, MyApplication.HTTPMethod.GET.getValue(), this)
                        .execute(mController.getMedicinesListUrl() + str);
            } else {
                androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
                alert.setTitle("Internet Connection Error!");
                alert.setMessage("No internet connection detected please connect to internet");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                });
                alert.show();
            }
        }
    }

    private void setMedicinesAdapter() {
        //listviewMedicines.setVisibility(View.VISIBLE);
        SelectMedicineListAdapter adapter = new SelectMedicineListAdapter(this, R.layout.inflater_select_medicine, jsonArraymedicines);
        listviewMedicines.setAdapter(adapter);
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    private void addListener()
    {
        datepicker_layout_from.setOnClickListener(this);
        datepicker_layout_till.setOnClickListener(this);
        timepicker_layout.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        editStatus.setOnClickListener(this);
    }


    private void switch_alarm_listener()
    {
        switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(getIntent().getSerializableExtra("MEDICATION") != null)
                {
                    mMedication = (Medication) getIntent().getSerializableExtra("MEDICATION");

                    if(isChecked)
                    {
                        mMedication.setAlarmStatus(1);
                    }

                    else
                    {
                        mMedication.setAlarmStatus(0);
                    }
                }
            }
        });
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.datepicker_layout_from) {
            tv_from.setError(null);

            this.date_selector_type = 0;
            this.showDatePicker(tv_from.getText().toString());
        } else if (id == R.id.datepicker_layout_till) {
            tv_till.setError(null);

            //if(!tv_from.getText().toString().isEmpty())
            //{
            this.date_selector_type = 1;
            this.showDatePicker(tv_till.getText().toString());
            //}

                /*else
                {
                    tv_from.setError("From date required");
                    tv_from.requestFocus();
                }*/
        } else if (id == R.id.timepicker_layout) {
            tv_intake_time.setError(null);
            this.showTimePicker();
        } else if (id == R.id.ib_clear_date1) {
            tv_from.setText("");
        } else if (id == R.id.ib_clear_date2) {
            tv_till.setText("");
        } else if (id == R.id.ib_clear_time) {
            tv_intake_time.setText("");
        } else if (id == R.id.btnDone) {
            this.save();
        } else if (id == R.id.editStatus) {
            JSONArray medicalInsurenceObject = new JSONArray();
            try {
                JSONObject jso = new JSONObject();
                jso.put("id", "2");
                jso.put("first_level_value", "Past");
                medicalInsurenceObject.put(jso);
                JSONObject jsoo = new JSONObject();
                jsoo.put("id", "1");
                jsoo.put("first_level_value", "Present");
                medicalInsurenceObject.put(jsoo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            showList(medicalInsurenceObject, editStatus, "Status of Medication");
        }
    }

    private void showList(JSONArray optionsArrayObject, final TextView spinnerView, String headerText) {
        final CharSequence[] items = new String[optionsArrayObject.length()];
        final CharSequence[] itemsids = new String[optionsArrayObject.length()];

        for (int i = 0; i < optionsArrayObject.length(); i++) {
            JSONObject model = null;
            try {
                model = optionsArrayObject.getJSONObject(i);
                items[i] = model.get("first_level_value").toString();
                itemsids[i] = model.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("" + headerText);
        builder1.setCancelable(false);
        builder1.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int item) {
                spinnerView.setText(items[item]);
                spinnerView.setTag(itemsids[item]);
            }
        });
        AlertDialog selectionDialog = builder1.create();
        selectionDialog.setCancelable(true);
        if (!selectionDialog.isShowing()) {
            selectionDialog.show();
        }
    }


    private void save()
    {
        if(!validate())
        {
            return;
        }

        if(new InternetConnectionDetector(this).isConnected())
        {
            initMedication();

            if(getIntent().getSerializableExtra("MEDICATION") == null)
            {
                new HttpClient(HTTP_REQUEST_CODE_ADD_MEDICATION, MyApplication.HTTPMethod.POST.getValue(),
                        Medication.composeMedicationMap(mMedication), this)
                        .execute(mController.getHealthProfileURL() + KEY_MEDICATION);
            }

            else
            {
                String URL = mController.getHealthProfileURL() + KEY_MEDICATION + "/" + this.mMedication.getId();

                new HttpClient(HTTP_REQUEST_CODE_UPDATE_MEDICATION, MyApplication.HTTPMethod.PATCH.getValue(),
                        Medication.composeMedicationMap(mMedication), this)
                        .execute(URL);
            }
        }

        else
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
        }
    }


    private boolean validate()
    {
        if(textView.getText().toString().trim().length() < 3)
        {
            textView.setError("Minimum 3 characters");
            textView.requestFocus();
            return false;
        }

        /*if(tv_from.getText().toString().trim().length() == 0)
        {
            tv_from.setError("From date required");
            tv_from.requestFocus();
            return false;
        }

        if(tv_till.getText().toString().trim().length() == 0)
        {
            tv_till.setError("To date required");
            tv_till.requestFocus();
            return false;
        }

        if(tv_intake_time.getText().toString().trim().length() == 0)
        {
            tv_intake_time.setError("Intake time required");
            tv_intake_time.requestFocus();
            return false;
        }*/

        String[] from_date = tv_from.getText().toString().split("/");
        String[] to_date = tv_till.getText().toString().split("/");

        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);

        try
        {
            Date fromDate = sdf.parse(from_date[2] + "-" + from_date[1] + "-" + from_date[0]);
            Date tillDate = sdf.parse(to_date[2] + "-" + to_date[1] + "-" + to_date[0]);

            if(fromDate.after(tillDate))
            {
                Toast.makeText(getApplicationContext(), "To Date must be greater than or equal to From date.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        if ((!tv_from.getText().toString().equalsIgnoreCase("") && tv_till.getText().toString().equalsIgnoreCase(""))){
            Toast.makeText(this, "To Date is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tv_from.getText().toString().equalsIgnoreCase("") && !tv_till.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(this, "From Date is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void initMedication()
    {
        if(getIntent().getSerializableExtra("MEDICATION") == null)
        {
            mMedication = new Medication();
        }

        mMedication.setName(textView.getText().toString());
        mMedication.setFromDate(from_date);
        mMedication.setToDate(till_date);
        mMedication.setIntakeTime(tv_intake_time.getText().toString());
        mMedication.setNotes(edit_notes.getText().toString());

        mMedication.setNoOfDays(editNoOfDays.getText().toString());
        mMedication.setStatus(editStatus.getTag().toString());

        /*if(switch_alarm.isChecked())
        {
            mMedication.setAlarmStatus(1);
        }

        else
        {
            mMedication.setAlarmStatus(0);
        }*/
    }


    private void display_medication()
    {
        this.from_date = mMedication.getFromDate();
        this.till_date = mMedication.getToDate();

        textView.setText(mMedication.getName().toUpperCase());
        tv_from.setText(Helper.dateFormat(mMedication.getFromDate()));
        tv_till.setText(Helper.dateFormat(mMedication.getToDate()));
        tv_intake_time.setText(mMedication.getIntakeTime());
        edit_notes.setText(mMedication.getNotes());

        /*if(mMedication.alarm_status == 1)
        {
            switch_alarm.setChecked(true);
        }

        else
        {
            switch_alarm.setChecked(false);
        }*/
    }


    private void showDatePicker(String display_date)
    {
        try
        {
            DatePickerFragment date = new DatePickerFragment();

            Calendar calender = Calendar.getInstance();

            Bundle args = new Bundle();
            args.putInt("min_year", 0);
            args.putInt("max_year", 0);

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

            if(date_selector_type == 0)
            {
                from_date = new StringBuilder().append(Helper.format_date(year)).append("-").append(Helper.format_date(monthOfYear + 1)).append("-").append(Helper.format_date(dayOfMonth)).toString();
                tv_from.setText(Helper.dateFormat(from_date));
            }

            else if(date_selector_type == 1)
            {
                till_date = new StringBuilder().append(Helper.format_date(year)).append("-").append(Helper.format_date(monthOfYear + 1)).append("-").append(Helper.format_date(dayOfMonth)).toString();
                tv_till.setText(Helper.dateFormat(till_date));
            }
        }
    };


    /**
     * Launch Time Picker Dialog
     */
    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            String time = new StringBuilder().append(Helper.formatNumber(hourOfDay)).append(":").append(Helper.formatNumber(minute)).toString();
            tv_intake_time.setText(time);
        }
    };


    private void saveMedication(int requestCode, String json_data)
    {
        try
        {
            Medication medication = Medication.getMedicationFromJSON(json_data);

            if(mMedication.getAlarmStatus() == 1)
            {
                medication.setAlarmStatus(1);
            }

            else
            {
                medication.setAlarmStatus(0);
            }

            if(requestCode == HTTP_REQUEST_CODE_ADD_MEDICATION)
            {
                this.mController.getSQLiteHelper().insert(medication);
                Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.putExtra("MEDICATION", medication);
                setResult(200, intent);
                finish();
            }

            if(requestCode == HTTP_REQUEST_CODE_UPDATE_MEDICATION)
            {
                this.mController.getSQLiteHelper().update(medication);
                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.putExtra("MEDICATION", medication);
                intent.putExtra("INDEX", getIntent().getIntExtra("INDEX", 0));
                setResult(100, intent);
                finish();
            }

            //this.alarm_reminder(id, from_date, to_date, intake_time, mMedication.alarm_status);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


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
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if(isFinishing())
        {
            return;
        }

        try
        {
            if (requestCode == HTTP_REQUEST_CODE_MEDICINES_LIST) {
                try {
                    if (responseCode == 200) {
                        JSONObject json = null;
                        jsonArraymedicines = new JsonArray();
                        pbProgressBar.setVisibility(View.GONE);
                        listviewMedicines.setVisibility(View.VISIBLE);
                        if (response != null)
                            json = new JSONObject(response);

                        JSONObject jsd = null;
                        try {
                            jsd = json.getJSONObject("data");
                        } catch (Exception e) {
                            Log.e("AAA", "Exception" + e);
                        }

                        if (jsd != null) {
                            //llAddButton.setVisibility(View.GONE);
                            listviewMedicines.setVisibility(View.VISIBLE);
                            Iterator<String> iter = jsd.keys();
                            try {
                                while (iter.hasNext()) {
                                    String key = iter.next();

                                    String value = String.valueOf(jsd.get(key));

                                    JsonObject jsonValue = new JsonObject();
                                    jsonValue.addProperty("id", key);
                                    jsonValue.addProperty("medicineName", value);
                                    jsonArraymedicines.add(jsonValue);
                                }
                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        } else {
                            //llAddButton.setVisibility(View.VISIBLE);
                            listviewMedicines.setVisibility(View.GONE);
                        }

                        setMedicinesAdapter();

                    } else {
                        listviewMedicines.setVisibility(View.GONE);

                        /*android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
                        alert.setMessage(new HttpResponseHandler(this, this, listviewMedicines).handle(responseCode, response));
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listviewMedicines.setVisibility(View.GONE);
                            }
                        });
                        alert.show();*/

                    }
                } catch (Exception r) {

                }
            }

            if((requestCode == HTTP_REQUEST_CODE_ADD_MEDICATION || requestCode == HTTP_REQUEST_CODE_UPDATE_MEDICATION)
                && (responseCode == HttpClient.CREATED || responseCode == HttpClient.OK))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.saveMedication(requestCode, json.getString(KEY_DATA));
                }

                catch (JSONException e)
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
    public void onPositiveAction()
    {

    }

    @Override
    public void onNegativeAction()
    {

    }


    private void alarm_reminder(int id, String from_date, String till_date, String intake_time, int alarm_status)
    {
        if(!intake_time.isEmpty() && alarm_status == 1)
        {
            int hour = Integer.parseInt(intake_time.split(":")[0]);
            int minute = Integer.parseInt(intake_time.split(":")[1]);

            intake_time = Helper.setDateTime(hour, minute);

            Log.wtf("ALARM_STATUS", "ON");

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent = new Intent("android.media.action.MEDICATION_REMINDER_NOTIFICATION");
            intent.putExtra("ALARM_ID", id);
            intent.putExtra("FROM_DATE", from_date);
            intent.putExtra("TILL_DATE", till_date);
            intent.putExtra("MEDICATION_TIME", intake_time);
            intent.addCategory("android.intent.category.DEFAULT");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            /*calendar.setTimeInMillis(System.currentTimeMillis());*/
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if(alarmManager == null)
            {
                return;
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        else
        {
            cancel_alarm(id);
        }
    }


    private void cancel_alarm(int req_code)
    {
        Log.wtf("ALARM_STATUS", "OFF");

        Intent intent = new Intent("android.media.action.MEDICATION_REMINDER_NOTIFICATION");
        //PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), req_code, intent, 0);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), req_code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(alarmManager == null)
        {
            return;
        }

        alarmManager.cancel(PendingIntent.getService(this, req_code, intent, 0));
    }
}