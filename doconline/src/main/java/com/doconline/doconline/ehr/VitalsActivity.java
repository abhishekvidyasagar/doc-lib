package com.doconline.doconline.ehr;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.VitalRecyclerAdapter;
import com.doconline.doconline.adapter.VitalsCalendarRecyclerAdapter;
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
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DATA;


public class VitalsActivity extends BaseActivity
{

    Toolbar toolbar;

    TextView toolbar_title;

    CoordinatorLayout layout_root_view;

    RelativeLayout layout_refresh;

    RecyclerView recycler_view;

    RelativeLayout date_layout;

    TextView recorded_date;

    LinearLayout layout_empty;

    TextView empty_message;


    LinearLayout bottom_sheet;

    RecyclerView recycler_view_date;

    private VitalRecyclerAdapter mAdapter;
    // not used
    private VitalsCalendarRecyclerAdapter mDate;

    private List<VitalTemplate> vitalList = new ArrayList<>();
    private List<Vital> entryList = new ArrayList<>();

    private static final int HTTP_REQUEST_CODE_GET_VITAL_TEMPLATE = 1;
    private static final int HTTP_REQUEST_CODE_GET_VITAL = 2;

    private VitalTemplateResponse vitalTemplate;
    private Vital vitalData;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);
        //ButterKnife.bind(this);

         toolbar = findViewById(R.id.toolbar);
         toolbar_title= findViewById(R.id.toolbar_title);
         layout_root_view= findViewById(R.id.layout_root_view);
        layout_refresh= findViewById(R.id.layout_refresh);
         recycler_view= findViewById(R.id.recycler_view);
        date_layout= findViewById(R.id.date_layout);
        recorded_date= findViewById(R.id.recorded_date);
         layout_empty= findViewById(R.id.layout_empty);
         empty_message= findViewById(R.id.empty_message);

        bottom_sheet= findViewById(R.id.bottom_sheet);
        recycler_view_date= findViewById(R.id.recycler_view_date);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(getResources().getString(R.string.text_vital_details));
        empty_message.setText("No Records Found");

        this.initAdapter();
        this.initDateAdapter();

        if(getIntent().getSerializableExtra("VITAL") != null)
        {
            vitalData = (Vital) getIntent().getSerializableExtra("VITAL");
        }

        this.syncData();
    }


    private void initAdapter()
    {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        mAdapter = new VitalRecyclerAdapter(vitalList);
        recycler_view.setAdapter(mAdapter);
        recycler_view.setNestedScrollingEnabled(false);
    }

    // not used
    private void initDateAdapter()
    {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view_date.setLayoutManager(mLayoutManager);
        mDate = new VitalsCalendarRecyclerAdapter(this, entryList);
        recycler_view_date.setAdapter(mDate);

        mDate.SetOnItemClickListener(new VitalsCalendarRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {

            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    private void syncData()
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == 100)
        {
            if (new InternetConnectionDetector(this).isConnected())
            {
                new HttpClient(HTTP_REQUEST_CODE_GET_VITAL, MyApplication.HTTPMethod.GET.getValue(),this)
                        .execute(mController.getVitalURL() + "/" + vitalData.getId());
            }

            else
            {
                new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();

            /*case R.id.action_edit:

                Intent intent = new Intent(VitalsActivity.this, VitalsManagementActivity.class);
                intent.putExtra("VITAL", vitalData);
                startActivityForResult(intent, 100);
                break;*/
        } else if (itemId == R.id.action_info) {
            Intent intent1 = new Intent(VitalsActivity.this, VitalInfoActivity.class);
            intent1.putExtra("URL", Constants.VITAL_INFO_URL);
            startActivity(intent1);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_vital_edit, menu);
        return true;
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
            if(requestCode == HTTP_REQUEST_CODE_GET_VITAL_TEMPLATE && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    this.vitalTemplate = new Gson().fromJson(json.toString(), VitalTemplateResponse.class);
                    this.vitalList.addAll(vitalData.getVitals());
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_GET_VITAL && responseCode == HttpClient.OK)
            {
                JSONObject json = new JSONObject(response);
                json = json.getJSONObject(KEY_DATA);

                String id = this.vitalData.getId();
                this.vitalData = new Gson().fromJson(json.toString(), Vital.class);
                this.vitalData.setId(id);

                this.vitalList.clear();
                this.vitalList.addAll(vitalData.getVitals());
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
                    if(vitalTemplate != null && vitalData != null)
                    {
                        layout_refresh.setVisibility(View.INVISIBLE);

                        update();
                        displayDate(vitalData);

                        mAdapter.notifyDataSetChanged();
                        mDate.notifyDataSetChanged();

                        if(vitalData.getVitals().size() != 0)
                        {
                            layout_empty.setVisibility(View.GONE);
                        }

                        else
                        {
                            layout_empty.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }


    private void update()
    {
        for(int i=0; i<vitalData.getVitals().size(); i++)
        {
            vitalData.getVitals().set(i, template(vitalData.getVitals().get(i)));
        }
    }

    private VitalTemplate template(VitalTemplate vital)
    {
        for(VitalTemplate data: vitalTemplate.getRecordFields())
        {
            if(vital.getType().equalsIgnoreCase(data.getKey()))
            {
                vital.setUnit(data.getUnit());
                vital.setLabel(data.getLabel());
                vital.setKey(data.getKey());
                break;
            }
        }

        return vital;
    }


    private void displayDate(Vital vitalData)
    {
        try
        {
            if(vitalData.getRecordedAt() != 0)
            {
                date_layout.setVisibility(View.VISIBLE);

                long recorder_at = vitalData.getRecordedAt() * 1000L;

                SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A);
                String timestamp = formatter.format(recorder_at);

                recorded_date.setText(timestamp);
            }

            else
            {
                date_layout.setVisibility(View.GONE);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}