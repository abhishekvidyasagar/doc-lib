package com.doconline.doconline.FitMeIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.doconline.doconline.app.Constants.ACCEPT;
import static com.doconline.doconline.app.Constants.AUTHORIZATION;
import static com.doconline.doconline.app.Constants.CONTENT_TYPE;
import static com.doconline.doconline.app.Constants.DOCONLINE_API;
import static com.doconline.doconline.app.Constants.DOCONLINE_API_VERSION;

public class StudiosWorkoutsList extends BaseActivity {

    String studioName = "", studioAddress = "", studioPartnerName = "", studioDistance = "";

    TextView tvStudioName, tvStudioAddress, tvMonthandYear;
    LinearLayout llDatesList, llWorkoutsList, llContentLayout, llNoWorkouts;
    RelativeLayout llLoadinglayout;
    int HTTP_REQUEST_CODE_WORKOUTS_IN_STUDIO = 1;

    String selectedDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studios_workouts_list);

        studioName = getIntent().getExtras().getString(Constants.KEY_STUDIO_OUTLET_NAME,"");
        studioAddress = getIntent().getExtras().getString(Constants.KEY_STUDIO_ADDRESS,"");
        studioPartnerName = getIntent().getExtras().getString(Constants.KEY_STUDIO_PARTNER_NAME,"");
        studioDistance =  getIntent().getExtras().getString(Constants.KEY_STUDIO_DISTANCE,"");

        tvStudioName = findViewById(R.id.studioname_tv);
        tvStudioAddress = findViewById(R.id.studioaddress_tv);
        tvMonthandYear = findViewById(R.id.monthandyear);

        llDatesList = findViewById(R.id.dateslist_ll);
        llWorkoutsList = findViewById(R.id.workout_list);
        llLoadinglayout = findViewById(R.id.layout_loading);
        llContentLayout = findViewById(R.id.content_layout);
        llNoWorkouts = findViewById(R.id.no_workouts_ll);

        tvStudioName.setText(studioName);
        tvStudioAddress.setText(studioAddress);

        callWorkoutsListByStudio();
    }

    public void goBack(View view) {
        finish();
    }

    public void callWorkoutsListByStudio() {
        if (new InternetConnectionDetector(this).isConnected()) {
            llLoadinglayout.setVisibility(View.VISIBLE);
            llContentLayout.setVisibility(View.GONE);
            new HttpClient(HTTP_REQUEST_CODE_WORKOUTS_IN_STUDIO, MyApplication.HTTPMethod.GET.getValue(), this).
                    execute(mController.getWorkoutListForStudio() + studioPartnerName);
        } else {
            //new CustomAlertDialog(WorkoutDetailsActivity.this, this).snackbarForInternetConnectivity();
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        try {
            if (requestCode == HTTP_REQUEST_CODE_WORKOUTS_IN_STUDIO && responseCode == 200) {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.get(Constants.KEY_DATA) instanceof JSONObject){
                    JSONObject dataObject = responseObject.getJSONObject(Constants.KEY_DATA);
                    Log.e("AAA", "object : " + dataObject);
                    populateDate(dataObject);
                }else {
                    llNoWorkouts.setVisibility(View.VISIBLE);
                    llLoadinglayout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(0, response);
            e.printStackTrace();
        }

    }

    private void populateDate(final JSONObject dataObject) {
        llLoadinglayout.setVisibility(View.GONE);
        llContentLayout.setVisibility(View.VISIBLE);
        final List<String> datesList = new ArrayList<>();
        for (Iterator<String> it = dataObject.keys(); it.hasNext(); ) {
            String date = it.next();
            datesList.add(date);
        }
        Log.e("AAA","dates : " + datesList);
        try {
            selectedDate = datesList.get(0);
            populateWorkoutsList(datesList.get(0), dataObject.getJSONArray(datesList.get(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        llDatesList.removeAllViews();
        for (int i = 0; i < datesList.size(); i++) {
            String dateee = datesList.get(i);
            final LinearLayout linearLayout = (LinearLayout) View.inflate(StudiosWorkoutsList.this, R.layout.inflater_dates, null);
            TextView tvDayOfWeek = linearLayout.findViewById(R.id.dayofweek_tv);
            TextView tvDateOfMonth = linearLayout.findViewById(R.id.dateofmonth_tv);
            final LinearLayout llThumbnailDate = linearLayout.findViewById(R.id.thumbnail_date);

            String dateofmonth = dateee.split("-")[2];
            tvDateOfMonth.setText(dateofmonth);
            tvDayOfWeek.setText(getDayOfWeek(dateee));

            llThumbnailDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //int itemposition = ((ViewGroup) llThumbnailDate.getParent()).indexOfChild(llThumbnailDate);
                    //Log.e("AAA","  clicked item position : "+itemposition);
                    Log.e("AAA","ll child count : "+llDatesList.getChildCount());
                    for (int i = 0; i < llDatesList.getChildCount(); i++) {
                        final View child = llDatesList.getChildAt(i);
                        if (child == llThumbnailDate){
                            Log.e("AAA","true");
                            child.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            try {
                                populateWorkoutsList(datesList.get(i), dataObject.getJSONArray(datesList.get(i)));
                                selectedDate = datesList.get(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Log.e("AAA","false");
                            child.setBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }
                }
            });
            llDatesList.addView(linearLayout);
            llDatesList.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void populateWorkoutsList(String dateee, JSONArray jsonArray) {
        String year = dateee.split("-")[0];
        String month = getMonthName(dateee);
        tvMonthandYear.setText(month + " " +year);

        try {
            Log.e("AAA", "object : " + jsonArray);
            llWorkoutsList.removeAllViews();
            if (jsonArray.length() > 0) {
                llWorkoutsList.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject eachObject = jsonArray.getJSONObject(i);
                    final LinearLayout linearLayout = (LinearLayout) View.inflate(StudiosWorkoutsList.this, R.layout.inflater_workout_list, null);
                    linearLayout.findViewById(R.id.name).setVisibility(View.GONE);
                    ((TextView) linearLayout.findViewById(R.id.workoutname)).setText(eachObject.get("cn").toString());
                    linearLayout.findViewById(R.id.location).setVisibility(View.GONE);
                    ((TextView) linearLayout.findViewById(R.id.distance)).setText(studioDistance + "kms");
                    ((TextView) linearLayout.findViewById(R.id.timings)).setText(eachObject.get(Constants.KEY_FROM_TIME).toString() + " To " + eachObject.get(Constants.KEY_TO_TIME).toString());
                    String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
                    String workoutID = eachObject.get(Constants.KEY_WORKOUT_ID_).toString();
                    final GlideUrl glideUrl = new GlideUrl(mController.getFitMeInWorkoutImageURL() + "/" + workoutID, new LazyHeaders.Builder()
                            .addHeader(AUTHORIZATION, "Bearer " + header)
                            .addHeader(ACCEPT, "application/json")
                            .addHeader(CONTENT_TYPE, "application/json")
                            .addHeader(DOCONLINE_API, DOCONLINE_API_VERSION)
                            .build());
                    Glide.with(StudiosWorkoutsList.this).load(glideUrl).centerCrop().placeholder(R.drawable.ic_loading).dontAnimate()
                            .into((ImageView) linearLayout.findViewById(R.id.image_view));
                    linearLayout.findViewById(R.id.root_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(StudiosWorkoutsList.this, WorkoutDetailsActivity.class);
                            try {
                                i.putExtra(Constants.WOORKOUT_DETAILS_ID, eachObject.get(Constants.KEY_WORKOUT_ID).toString());
                                i.putExtra(Constants.SELECTED_DATE, selectedDate);
                                i.putExtra(Constants.FROM_SCREEN_KEY, Constants.MAIN_ACTIVITY);
                                i.putExtra(Constants.DISTANCE, studioDistance);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            StudiosWorkoutsList.this.startActivity(i);
                        }
                    });
                    llWorkoutsList.addView(linearLayout);
                }
            } else {
                llWorkoutsList.removeAllViews();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getMonthName(String dateee) {
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
        Date dt1= null;
        try {
            dt1 = format1.parse(dateee);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2=new SimpleDateFormat("MMMM");
        return format2.format(dt1);
    }

    private String getDayOfWeek(String dateee) {
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
        Date dt1= null;
        try {
            dt1 = format1.parse(dateee);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2=new SimpleDateFormat("EEE");
        return format2.format(dt1).toUpperCase();
    }
}
