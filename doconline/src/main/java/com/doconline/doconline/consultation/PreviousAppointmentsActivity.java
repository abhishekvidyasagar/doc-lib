package com.doconline.doconline.consultation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.PreviousAppointmentsAdapter;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.model.Appointment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.appointment.AppointmentHistoryActivity.HTTP_REQUEST_CODE;

public class PreviousAppointmentsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    TextView toolbar_title;


    ProgressBar layout_loading;
    //@BindView(R.id.layout_empty)
    //LinearLayout layout_empty;
    //@BindView(R.id.empty_message)
    //TextView empty_message;
    RecyclerView mRecyclerView;

    private PreviousAppointmentsAdapter mAdapter;
    private List<Appointment> uList = new ArrayList<>();

    private boolean isLoading;
    private int CURRENT_PAGE, LAST_PAGE;

    int lastselectedposition = -1;

    Spinner spinner;
    String followupreason = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_appointments);
        layout_loading = findViewById(R.id.progressBar);
        mRecyclerView =  findViewById(R.id.recycler_view);

        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title.setText("Previous Appointments");

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.initAdapter();

        if (new InternetConnectionDetector(this).isConnected()) {


            layout_loading.setVisibility(View.VISIBLE);
            //layout_empty.setVisibility(View.GONE);


            new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getAppointmentURL() + "previous");

        } else {
            this.adapter_refresh();
        }

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

       /* List<String> reasonforfollowup = new ArrayList<String>();
        reasonforfollowup.add("");
        reasonforfollowup.add("Doctor insisted for a follow up consultation");
        reasonforfollowup.add("I am satisfied but still looking for a second opinion");
        reasonforfollowup.add("I am dissatisfied and hence going for another consultation");*/

        /*ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, reasonforfollowup);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);*/

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.reasonforfollowups, R.layout.spinner_textview);
        spinner.setAdapter(adapter);
    }

    private void initAdapter() {
        if (mAdapter != null) {
            return;
        }

        /**
         * Calling the RecyclerView
         */
        //mRecyclerView.setHasFixedSize(true);

        /**
         * The number of Columns
         */
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PreviousAppointmentsAdapter(this, uList);
        mRecyclerView.setAdapter(mAdapter);
        PreviousAppointmentsAdapter.lastCheckedPosition = -1;


        /*mAdapter.SetOnItemClickListener(new PreviousAppointmentsAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i) {
                RadioButton rb = view.findViewById(R.id.select_rb);
                rb.setChecked(true);
                lastselectedposition = i;
            }
        });*/

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                Log.v("onLoadMore", "Load More");

                if (!new InternetConnectionDetector(PreviousAppointmentsActivity.this).isConnected()) {
                    Toast.makeText(PreviousAppointmentsActivity.this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }
                isLoading = true;

                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        uList.add(null);
                        mAdapter.notifyItemInserted(uList.size() - 1);
                    }
                };

                handler.post(r);
                new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), PreviousAppointmentsActivity.this)
                        .execute(mController.getAppointmentURL() + "upcoming?page=" + (CURRENT_PAGE + 1));
            }
        });

        this.load_more_on_scroll();
        this.adapter_refresh();
    }


    private void load_more_on_scroll() {
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    Log.v("onScrolled", "onScrolled");

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                        if (!isLoading && CURRENT_PAGE != LAST_PAGE) {
                            if (mAdapter.mOnLoadMoreListener != null) {
                                mAdapter.mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    public void setLoaded() {
        isLoading = false;
    }


    private void adapter_refresh() {
        if (uList.size() == 0) {


        } else {
            //layout_empty.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
    }


    private void displayAppointment(String json_data) {
        try {
            JSONObject json = new JSONObject(json_data);
            json = json.getJSONObject(KEY_DATA);

            /*if(json.has(Constants.KEY_SESSION_ID) && json.has(Constants.KEY_TOKEN_ID))
            {
                Intent intent = new Intent();
                intent.setAction("cb.doconline.VOIP");
                intent.putExtra("json_data", json_data);
                intent.putExtra("is_outgoing_call", true);
                context.sendBroadcast(intent);
                return;
            }*/


            if (isLoading) {
                uList.remove(uList.size() - 1);
                mAdapter.notifyItemRemoved(uList.size());
                setLoaded();
            }

            CURRENT_PAGE = json.getInt(Constants.KEY_CURRENT_PAGE);
            LAST_PAGE = json.getInt(Constants.KEY_LAST_PAGE);

            if (new JSONArray(json.getString(KEY_DATA)).length() < 1 && CURRENT_PAGE == 1){
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Alert !");
                alert.setMessage("You do not have any previous appointments");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //PreviousAppointmentsActivity.super.onBackPressed();
                    }
                });
                alert.show();
            }

            uList.addAll(Appointment.getAppointmentListFromJSON(json.getString(KEY_DATA)));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            this.adapter_refresh();
        }
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

        try {
            if (requestCode == HTTP_REQUEST_CODE && responseCode == HttpClient.OK) {
                this.displayAppointment(response);
                return;
            }

            new HttpResponseHandler(this, this, mRecyclerView).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            layout_loading.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //On selecting a spinner item
        followupreason = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void goCancel(View view) {
        finish();
    }

    public void goOk(View view) {
        int followupid = PreviousAppointmentsAdapter.followupid;
        if (followupid > -1 && PreviousAppointmentsAdapter.lastCheckedPosition > -1){
            if (!followupreason.equalsIgnoreCase("")){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("followupid", ""+followupid);
                returnIntent.putExtra("followreason", followupreason);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else {
                Toast.makeText(this, "Select follow up reason", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "select one previous appointment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: //Back arrow pressed
            {
                this.onBackPressed();
            }
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
