package com.doconline.doconline.diagnostics;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.model.DiagnosticsHistoryItem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DiagnosticsReportsStatusActivity extends BaseActivity implements View.OnClickListener{

    String diagnosticStringItem;
    DiagnosticsHistoryItem diagnosticItem;
    int HTTP_REQUEST_CODE_DIAGNOSTICS_REPORT_STATUS = 1;

    String tspName = "";
    String tspPhone = "";


    /******UIs*******/
    TextView toolbar_title;
    Toolbar toolbar;
    ImageView ivProductImage;
    TextView tvAppointmentID;
    TextView tvProductName;
    TextView tvAppointmentDate;
    LinearLayout lvBenificieriesList;
    LinearLayout llStatusLayout;
    ProgressBar pbProgressBar;
    ScrollView svContentLayout;

    TextView yetToAssignIcon;
    TextView yetToAssignName;
    TextView yetToAssignDate;

    TextView assignedIcon;
    TextView assignedName;
    TextView assignedDate;

    TextView startedIcon;
    TextView startedName;
    TextView startedDate;


    TextView doneStatusText;
    /*TextView arrivedIcon;
    TextView arrivedName;
    TextView arrivedDate;*/

    TextView servicedIcon;
    TextView servicedName;
    TextView servicedDate;

    RelativeLayout rlYetToAssign;
    RelativeLayout rlAssigned;
    RelativeLayout rlServiced;
    RelativeLayout rlArrived;

    /******UIs*******/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics_reports_status);

        doInitialise();

        toolbar_title.setText(getResources().getString(R.string.text_diagnostics_details));
        diagnosticStringItem = getIntent().getExtras().get(Constants.DIAGNOSTIC_ITEM).toString();
        Gson gson = new Gson();
        diagnosticItem = gson.fromJson(diagnosticStringItem, DiagnosticsHistoryItem.class);
        if(new InternetConnectionDetector(this).isConnected()) {
            executeReportsStatusApi();
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Please check your Internet connection");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        }
    }

    private void doInitialise() {
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (!MyApplication.getInstance().getSession().checkLogin()) {
            finish();
            return;
        }

        ivProductImage = findViewById(R.id.iv_package_offeredby);
        tvAppointmentID = findViewById(R.id.tv_appointmentid);
        tvProductName = findViewById(R.id.tv_productname);
        tvAppointmentDate = findViewById(R.id.tv_appointmentdate);
        lvBenificieriesList = findViewById(R.id.benificieries_list);
        pbProgressBar = findViewById(R.id.progressBar);
        svContentLayout = findViewById(R.id.content_layout);

        llStatusLayout = findViewById(R.id.status_layout);

        doneStatusText = findViewById(R.id.donestatustext);

        yetToAssignIcon = findViewById(R.id.yettoassign_icon);
        yetToAssignName = findViewById(R.id.yettoassign_name);
        yetToAssignDate = findViewById(R.id.yettoassign_date);

        assignedIcon = findViewById(R.id.assigned_icon);
        assignedName = findViewById(R.id.assigned_name);
        assignedDate = findViewById(R.id.assigned_date);

        startedIcon = findViewById(R.id.started_icon);
        startedName = findViewById(R.id.started_name);
        startedDate = findViewById(R.id.started_date);

        /*arrivedIcon = findViewById(R.id.arrived_icon);
        arrivedName = findViewById(R.id.arrived_name);
        arrivedDate = findViewById(R.id.arrived_date);*/

        servicedIcon = findViewById(R.id.serviced_icon);
        servicedName = findViewById(R.id.serviced_name);
        servicedDate = findViewById(R.id.serviced_date);

        rlYetToAssign = findViewById(R.id.yettoassign_rl);
        rlYetToAssign.setOnClickListener(this);
        rlAssigned = findViewById(R.id.assigned_rl);
        rlAssigned.setOnClickListener(this);
        rlServiced = findViewById(R.id.serviced_rl);
        rlServiced.setOnClickListener(this);
        rlArrived = findViewById(R.id.arrived_rl);
        rlArrived.setOnClickListener(this);

    }

    private void executeReportsStatusApi() {
        pbProgressBar.setVisibility(View.VISIBLE);
        svContentLayout.setVisibility(View.GONE);
        new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_REPORT_STATUS, MyApplication.HTTPMethod.GET.getValue(), this)
                .execute(mController.getDiagnosticsReportsStatusURL() + diagnosticItem.getAppointmentID());
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_REPORT_STATUS ) {
            if(responseCode == 200){
                Log.d("AAA", "response " + response + " response code : " + responseCode);
                pbProgressBar.setVisibility(View.GONE);
                svContentLayout.setVisibility(View.VISIBLE);
                autoPopulateData(response);
            }
            else{

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage(new HttpResponseHandler(this, this, svContentLayout).handle(responseCode, response));
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alert.show();

            }
        }
    }

    private void autoPopulateData(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONObject dataObject = json.getJSONObject(Constants.KEY_DATA);
            JSONObject appointmentDetailsObject = dataObject.getJSONObject(Constants.KEY_DIAG_APPOINTMENT_DETAILS);
            JSONArray reportsArray = dataObject.getJSONArray(Constants.KEY_DIAG_REPORTS);
            JSONObject trackingStatusesObject = dataObject.getJSONObject(Constants.KEY_DIAG_TRACKING_STATUS);
            JSONObject tspDetailsObject = dataObject.getJSONObject(Constants.KEY_DIAG_TSP_DETAILS);
            tspName = tspDetailsObject.getString("tsp_name");
            tspPhone = tspDetailsObject.getString("tsp_mobile");

            //setting package details
            tvAppointmentID.setText("" + appointmentDetailsObject.get(Constants.KEY_DIAG_APPOINTMENT_ID));
            tvProductName.setText("" + appointmentDetailsObject.get(Constants.KEY_DIAG_PACKAGE_NAME));
            tvAppointmentDate.setText("" + appointmentDetailsObject.get(Constants.KEY_DIAG_APPOINTMENT_DATE));

            //setting benificieries list
            //used custome add lls to over come fixed size and ui scroll issues
            lvBenificieriesList.removeAllViews();
            Log.e("REPORTS SIZE",""+reportsArray.length());
            if (reportsArray.length() > 0) {
                for (int i = 0; i < reportsArray.length(); i++) {
                    final JSONObject userObject = reportsArray.getJSONObject(i);
                    LinearLayout linearLayout = (LinearLayout) View.inflate(DiagnosticsReportsStatusActivity.this, R.layout.inflater_diag_beneficiaries, null);
                    ((TextView) linearLayout.findViewById(R.id.tv_benificary_name)).setText(""+userObject.getString(Constants.KEY_DIAG_USER_NAME));

                    //validation for completed or not
                    if (!TextUtils.isEmpty(userObject.getString(Constants.KEY_DIAG_REPORT_URL))){
                        ((Button)linearLayout.findViewById(R.id.report_btn)).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }else {
                        ((Button)linearLayout.findViewById(R.id.report_btn)).setTextColor(getResources().getColor(R.color.audiOnlyBackground));
                    }

                    //checking is cancelled status or generated
                    if (appointmentDetailsObject.getString(Constants.KEY_DIAG_APPOINTMENT_STATUS).equalsIgnoreCase("Cancelled") ||
                            !TextUtils.isEmpty(userObject.getString(Constants.KEY_DIAG_REPORT_URL))){
                        llStatusLayout.setVisibility(View.GONE);
                        doneStatusText.setVisibility(View.VISIBLE);
                        if (appointmentDetailsObject.getString(Constants.KEY_DIAG_APPOINTMENT_STATUS).equalsIgnoreCase("Cancelled")){
                            doneStatusText.setText("Your Appointment has been cancelled.");
                        }
                    }else {
                        llStatusLayout.setVisibility(View.VISIBLE);
                        doneStatusText.setVisibility(View.GONE);
                    }

                    linearLayout.findViewById(R.id.report_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (!userObject.getString(Constants.KEY_DIAG_REPORT_URL).equalsIgnoreCase("")){
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(userObject.getString(Constants.KEY_DIAG_REPORT_URL)));
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(DiagnosticsReportsStatusActivity.this, "No Reports Available For This User", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    lvBenificieriesList.addView(linearLayout);
                }
            }else{
                llStatusLayout.setVisibility(View.GONE);
            }

            //setting tracking status
            JSONObject yettoAssignObject = trackingStatusesObject.getJSONObject("6");
            yetToAssignName.setText(""+yettoAssignObject.getString("status"));
            yetToAssignDate.setText(""+yettoAssignObject.getString("date_time"));
            yetToAssignIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notyetassigned_b,0,0,0);

            JSONObject assignedObject = trackingStatusesObject.getJSONObject("9");
            assignedName.setText(""+assignedObject.getString("status"));
            if (!TextUtils.isEmpty(assignedObject.getString("date_time"))){
                //set greenm icon
                assignedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_assigned_b,0,0,0);
                assignedDate.setText(""+assignedObject.getString("date_time"));
                assignedName.setTextColor(getResources().getColor(R.color.black));
                rlAssigned.setClickable(true);
            }else {
                //setgrey icon
                assignedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_assigned_g,0,0,0);
                assignedDate.setText("");
                assignedName.setTextColor(getResources().getColor(R.color.audiOnlyBackground));
                rlAssigned.setClickable(false);
            }

            JSONObject startedObject = trackingStatusesObject.getJSONObject("17");
            startedName.setText(""+startedObject.getString("status"));
            if (!TextUtils.isEmpty(startedObject.getString("date_time"))){
                //set greenm icon
                startedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_started_b,0,0,0);
                startedDate.setText(""+startedObject.getString("date_time"));
                startedName.setTextColor(getResources().getColor(R.color.black));
                rlServiced.setClickable(true);
            }else {
                //setgrey icon
                startedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_started_g,0,0,0);
                startedDate.setText("");
                startedName.setTextColor(getResources().getColor(R.color.audiOnlyBackground));
                rlServiced.setClickable(false);
            }

            /*JSONObject arrivedObject = trackingStatusesObject.getJSONObject("14");
            arrivedName.setText(""+arrivedObject.getString("status"));
            if (!TextUtils.isEmpty(arrivedObject.getString("date_time"))){
                //set greenm icon
                arrivedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrived_b,0,0,0);
                arrivedDate.setText(""+arrivedObject.getString("date_time"));
            }else {
                //setgrey icon
                arrivedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrived_g,0,0,0);
                arrivedDate.setText("");
            }*/

            JSONObject servicedObject = trackingStatusesObject.getJSONObject("101");
            servicedName.setText(""+servicedObject.getString("status"));
            if (!TextUtils.isEmpty(servicedObject.getString("date_time"))){
                //set greenm icon
                servicedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_serviced_b,0,0,0);
                servicedDate.setText(""+servicedObject.getString("date_time"));
                servicedName.setTextColor(getResources().getColor(R.color.black));
                rlArrived.setClickable(true);
            }else {
                //setgrey icon
                servicedIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_serviced_g,0,0,0);
                servicedDate.setText("");
                servicedName.setTextColor(getResources().getColor(R.color.audiOnlyBackground));
                rlArrived.setClickable(false);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.yettoassign_rl) {
            setAlertDialog("Yet to Assign",
                    "Your Order will be assigned to one of the pick up Agents shortly",
                    "OK");
        } else if (id == R.id.assigned_rl) {
            setAlertDialog("Assigned",
                    "Your Order has been assigned to our pickup Agent '' " + tspName + " '' " + tspPhone,
                    "CALL");
        } else if (id == R.id.serviced_rl) {
            setAlertDialog("Serviced",
                    "Your Order has been serviced by a pick up Agent",
                    "OK");
        } else if (id == R.id.arrived_rl) {
            setAlertDialog("Processing",
                    "The sample collected has been sent to Laboratory for testing. Results will be available shortly",
                    "OK");
        }
    }

    private void setAlertDialog(String title, String alertMessage, final String positiveButtonText) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(""+alertMessage);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (positiveButtonText.equalsIgnoreCase("CALL")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + tspPhone));
                    startActivity(intent);
                }
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    public void callThyrocare(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Thyrocare Customer Care");
        alert.setMessage("would you really like to call thyrocare Customer care ? ");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "02230900000"));
                startActivity(intent);
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }
}
