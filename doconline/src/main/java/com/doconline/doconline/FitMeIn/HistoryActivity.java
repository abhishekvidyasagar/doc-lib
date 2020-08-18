package com.doconline.doconline.FitMeIn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.helper.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.doconline.doconline.app.Constants.ACCEPT;
import static com.doconline.doconline.app.Constants.AUTHORIZATION;
import static com.doconline.doconline.app.Constants.CONTENT_TYPE;
import static com.doconline.doconline.app.Constants.DOCONLINE_API;
import static com.doconline.doconline.app.Constants.DOCONLINE_API_VERSION;

public class HistoryActivity extends BaseActivity implements View.OnClickListener {

    String upcomingWorkouts = "upcoming", previousWorkouts = "previous", cancelledWorkouts = "cancelled";
    TextView previousBtn, upcomingBtn, cancelledBtn;
    TextView nodatalabel;

    RelativeLayout layoutLoading;
    LinearLayout workoutList;
    String fromscreen = Constants.KEY_HISTORY_UPCOMING;
    String workoutType = upcomingWorkouts;

    int HTTP_REQUEST_CODE_HISTORY = 1;
    int pageNo = 1;
    int recordsPerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        previousBtn = findViewById(R.id.previous_btn);
        previousBtn.setOnClickListener(this);
        upcomingBtn = findViewById(R.id.upcoming_btn);
        upcomingBtn.setOnClickListener(this);
        cancelledBtn = findViewById(R.id.cancelled_btn);
        cancelledBtn.setOnClickListener(this);

        nodatalabel = findViewById(R.id.nodatalabel);

        layoutLoading = findViewById(R.id.layout_loading);
        workoutList = findViewById(R.id.workout_list);

    }

    @Override
    public void onResume() {
        super.onResume();
        upcomingBtnAction();
    }

    private void callHistoryApi(String category, int pageno) {
        enableUserIntaraction(false);
        layoutLoading.setVisibility(View.VISIBLE);
        new HttpClient(HTTP_REQUEST_CODE_HISTORY, MyApplication.HTTPMethod.GET.getValue(), this)
                .execute(mController.getFitMeInHistoryURL() + "/" + category + "/" + pageno);
    }

    private void enableUserIntaraction(Boolean userIntaraction) {
        if (userIntaraction) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        layoutLoading.setVisibility(View.GONE);
        try {
            if (requestCode == HTTP_REQUEST_CODE_HISTORY && responseCode == 200) {
                populateList(response);
            }
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(responseCode, response);

        } catch (Exception e) {
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(0, response);
            e.printStackTrace();
        }

    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.previous_btn) {
            previousBtnAction();
        } else if (id == R.id.upcoming_btn) {
            upcomingBtnAction();
        } else if (id == R.id.cancelled_btn) {
            cancelledBtnAction();
        }
    }

    private void cancelledBtnAction() {
        nodatalabel.setVisibility(View.GONE);
        previousBtn.setBackgroundColor(getResources().getColor(R.color.white));
        previousBtn.setTextColor(getResources().getColor(R.color.colorPrimary));

        upcomingBtn.setBackgroundColor(getResources().getColor(R.color.white));
        upcomingBtn.setTextColor(getResources().getColor(R.color.colorPrimary));

        cancelledBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        cancelledBtn.setTextColor(getResources().getColor(R.color.white));

        fromscreen = Constants.KEY_HISTORY_CANCELLED;
        workoutType = cancelledWorkouts;
        callHistoryApi(cancelledWorkouts, 1);
    }

    private void previousBtnAction() {
        nodatalabel.setVisibility(View.GONE);
        previousBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        previousBtn.setTextColor(getResources().getColor(R.color.white));

        upcomingBtn.setBackgroundColor(getResources().getColor(R.color.white));
        upcomingBtn.setTextColor(getResources().getColor(R.color.colorPrimary));

        cancelledBtn.setBackgroundColor(getResources().getColor(R.color.white));
        cancelledBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        fromscreen = Constants.KEY_HISTORY_PREVIOUS;
        workoutType = previousWorkouts;
        callHistoryApi(previousWorkouts, 1);
    }

    private void upcomingBtnAction() {
        nodatalabel.setVisibility(View.GONE);
        previousBtn.setBackgroundColor(getResources().getColor(R.color.white));
        previousBtn.setTextColor(getResources().getColor(R.color.colorPrimary));

        upcomingBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        upcomingBtn.setTextColor(getResources().getColor(R.color.white));

        cancelledBtn.setBackgroundColor(getResources().getColor(R.color.white));
        cancelledBtn.setTextColor(getResources().getColor(R.color.colorPrimary));

        fromscreen = Constants.KEY_HISTORY_UPCOMING;
        workoutType = upcomingWorkouts;
        callHistoryApi(upcomingWorkouts, 1);
    }

    private void populateList(String response) {
        try {
            enableUserIntaraction(true);
            JSONObject responseObject = new JSONObject(response);
            final JSONArray listArray = responseObject.getJSONObject(Constants.KEY_DATA).getJSONArray("records");
            pageNo = Integer.parseInt(responseObject.getJSONObject(Constants.KEY_DATA).getString("page_no"));
            recordsPerPage = Integer.parseInt(responseObject.getJSONObject(Constants.KEY_DATA).getString("records_per_page"));
            int totalCount = Integer.parseInt(responseObject.getJSONObject(Constants.KEY_DATA).getString("total_count"));
            Log.e("AAA", "object : " + listArray);
            if (pageNo == 1){
                workoutList.removeAllViews();
            }

            if (listArray.length() > 0) {
                nodatalabel.setVisibility(View.GONE);
                workoutList.setVisibility(View.VISIBLE);
                for (final int[] i = {0}; i[0] < (listArray.length() +1); i[0]++) {
                    final LinearLayout linearLayout = (LinearLayout) View.inflate(HistoryActivity.this, R.layout.inflater_workout_list, null);
                    if ((i[0] == listArray.length())  &&  (totalCount > recordsPerPage || totalCount == recordsPerPage)){
                        ((CardView) linearLayout.findViewById(R.id.content_view)).setVisibility(View.GONE);
                        ((LinearLayout) linearLayout.findViewById(R.id.loadmore_ll)).setVisibility(View.VISIBLE);
                        linearLayout.findViewById(R.id.loadmore_ll).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                workoutList.removeView(linearLayout);
                                callHistoryApi(workoutType, pageNo+1);
                            }
                        });
                    }else {
                        final JSONObject eachObject = listArray.getJSONObject(i[0]);
                        ((TextView) linearLayout.findViewById(R.id.name)).setText(eachObject.get(Constants.KEY_OUTLET_NAME_HIS).toString());
                        ((TextView) linearLayout.findViewById(R.id.workoutname)).setText(eachObject.get("workout_name").toString());
                        ((TextView) linearLayout.findViewById(R.id.location)).setText(" " + eachObject.get(Constants.KEY_ADDRESS).toString() + ", " + eachObject.get(Constants.KEY_CITY).toString());
                        ((TextView) linearLayout.findViewById(R.id.distance)).setText(eachObject.get(Constants.DISTANCE).toString() + "kms");
                        ((TextView) linearLayout.findViewById(R.id.timings)).setText(getRequiredDateFormatFromAvailableFormat(eachObject.get(Constants.KEY_BOOKED_FOR_DATE).toString())
                                + " " + eachObject.get(Constants.KEY_WORKOUT_BOOK_TIME).toString());

                        String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
                        String workoutID = eachObject.get(Constants.KEY_WORKOUT_ID_BOOK).toString();
                        final GlideUrl glideUrl = new GlideUrl(mController.getFitMeInWorkoutImageURL() + "/" + workoutID, new LazyHeaders.Builder()
                                .addHeader(AUTHORIZATION, "Bearer " + header)
                                .addHeader(ACCEPT, "application/json")
                                .addHeader(CONTENT_TYPE, "application/json")
                                .addHeader(DOCONLINE_API, DOCONLINE_API_VERSION)
                                .build());

                        Glide.with(HistoryActivity.this).load(glideUrl).centerCrop().placeholder(R.drawable.ic_loading).dontAnimate()
                                .into((ImageView) linearLayout.findViewById(R.id.image_view));

                        ((RelativeLayout) linearLayout.findViewById(R.id.root_layout)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HistoryActivity.this, WorkoutDetailsActivity.class);
                                try {
                                    intent.putExtra(Constants.WOORKOUT_DETAILS_ID, eachObject.get("workout_id").toString());
                                    intent.putExtra(Constants.SELECTED_DATE, "");
                                    intent.putExtra(Constants.FROM_SCREEN_KEY, fromscreen);
                                    if (!fromscreen.equalsIgnoreCase(Constants.MAIN_ACTIVITY)) {
                                        intent.putExtra("outletname", eachObject.get("outlet_name").toString());
                                        intent.putExtra("workoutname", eachObject.get("workout_name").toString());
                                        intent.putExtra("location", eachObject.get("address").toString());
                                        intent.putExtra("fromtime", eachObject.get("from_time").toString());
                                        intent.putExtra("totime", eachObject.get("to_time").toString());
                                        intent.putExtra("latlong", eachObject.get("lat_long").toString());
                                        intent.putExtra("description", eachObject.get("description").toString());
                                        intent.putExtra("bookingid", eachObject.get("booking_id").toString());
                                        intent.putExtra("workoutid", eachObject.get("workout_id").toString());
                                        intent.putExtra("iscancelled", eachObject.get("is_cancelled").toString());
                                        intent.putExtra("date", getRequiredDateFormatFromAvailableFormat(eachObject.get("booked_for_date").toString()));
                                        intent.putExtra("bookedtime", eachObject.get(Constants.KEY_WORKOUT_BOOK_TIME).toString());
                                        intent.putExtra("isattended", eachObject.getInt("is_attended"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //this if clicked previous should not take to details screen
                                if (fromscreen.equalsIgnoreCase(Constants.KEY_HISTORY_PREVIOUS) || fromscreen.equalsIgnoreCase(Constants.KEY_HISTORY_CANCELLED)) {

                                } else {
                                    startActivity(intent);
                                }

                            }
                        });

                    }
                    workoutList.addView(linearLayout);

                    if (pageNo > 1){
                        Log.e("AAA","request focus : "+(workoutList.getChildCount()-recordsPerPage));
                        //Log.e("AAA","request and : "+workoutList.getChildAt(5));
                        workoutList.getParent().requestChildFocus(workoutList.getChildAt(workoutList.getChildCount()-recordsPerPage),
                                workoutList.getChildAt(workoutList.getChildCount()-recordsPerPage));
                    }
                }

            } else {
                nodatalabel.setVisibility(View.VISIBLE);
                workoutList.setVisibility(View.GONE);
                workoutList.removeAllViews();

                String text = "";
                if (fromscreen.equalsIgnoreCase(Constants.KEY_HISTORY_CANCELLED)) {
                    text = "No records found ";
                } else if (fromscreen.equalsIgnoreCase(Constants.KEY_HISTORY_UPCOMING)) {
                    text = "Knock! Knock! \n Your scheduled upcoming workouts await you here! ";
                } else if (fromscreen.equalsIgnoreCase(Constants.KEY_HISTORY_PREVIOUS)) {
                    text = "At a Glance: View all your past workout details here! ";
                }
                nodatalabel.setVisibility(View.VISIBLE);
                nodatalabel.setText(text);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getRequiredDateFormatFromAvailableFormat(String availabledate) {
        Date date = null;
        String str = null;

        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);


        try {
            date = inputFormat.parse(availabledate);
            str = outputFormat.format(date);
        } catch (android.net.ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void goWorkoutsList(View view) {
        Intent i = new Intent(HistoryActivity.this, FitMeInActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

    public void goStudios(View view) {
        Intent i = new Intent(HistoryActivity.this, StudiosListActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

    private class HistoryListAdapter extends BaseAdapter {
        Context context;
        int inflater_workout_list;
        JSONArray listArray;
        Holder holder;

        public HistoryListAdapter(Context context, int inflater_workout_list, JSONArray listArray) {
            this.context = context;
            this.inflater_workout_list = inflater_workout_list;
            this.listArray = listArray;

        }

        @Override
        public int getCount() {
            return listArray.length();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View row = view;
            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(inflater_workout_list, viewGroup, false);

                holder = new Holder();
                holder.mName = row.findViewById(R.id.name);
                holder.mLocation = row.findViewById(R.id.location);
                holder.mDistance = row.findViewById(R.id.distance);
                holder.mTimings = row.findViewById(R.id.timings);
                holder.imageView = row.findViewById(R.id.image_view);
                holder.rootLayout = row.findViewById(R.id.root_layout);
            }

            try {
                final JSONObject eachObject = listArray.getJSONObject(position);
                holder.mName.setText(eachObject.get(Constants.KEY_OUTLET_NAME_HIS).toString());
                holder.mLocation.setText(eachObject.get(Constants.KEY_ADDRESS).toString());
                holder.mDistance.setText(eachObject.get(Constants.KEY_DISTANCE_HIS).toString() + "kms");
                holder.mTimings.setText(eachObject.get(Constants.KEY_BOOKING_DATE).toString() + " " + eachObject.get(Constants.KEY_FROM_TIME_HIS).toString());


                String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
                String workoutIdforImage = eachObject.getString(Constants.KEY_WORKOUT_ID_BOOK);

                GlideUrl glideUrl = new GlideUrl(mController.getFitMeInWorkoutImageURL() + "/" + workoutIdforImage, new LazyHeaders.Builder()
                        .addHeader(AUTHORIZATION, "Bearer " + header)
                        .addHeader(ACCEPT, "application/json")
                        .addHeader(CONTENT_TYPE, "application/json")
                        .addHeader(DOCONLINE_API, DOCONLINE_API_VERSION)
                        .build());

                Glide.with(context).load(glideUrl).centerCrop().placeholder(R.drawable.ic_loading).into(holder.imageView);

                holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, WorkoutDetailsActivity.class);
                        try {
                            i.putExtra(Constants.WOORKOUT_DETAILS_ID, eachObject.get("workout_id").toString());
                            i.putExtra(Constants.SELECTED_DATE, "");
                            i.putExtra(Constants.FROM_SCREEN_KEY, fromscreen);
                            if (!fromscreen.equalsIgnoreCase(Constants.MAIN_ACTIVITY)) {
                                i.putExtra("outletname", eachObject.get("outlet_name").toString());
                                i.putExtra("workoutname", eachObject.get("workout_name").toString());
                                i.putExtra("location", eachObject.get("address").toString());
                                i.putExtra("fromtime", eachObject.get("from_time").toString());
                                i.putExtra("totime", eachObject.get("to_time").toString());
                                i.putExtra("latlong", eachObject.get("lat_long").toString());
                                i.putExtra("description", eachObject.get("description").toString());
                                i.putExtra("bookingid", eachObject.get("booking_id").toString());
                                i.putExtra("workoutid", eachObject.get("workout_id").toString());
                                i.putExtra("iscancelled", eachObject.get("is_cancelled").toString());
                                i.putExtra("date", eachObject.get("booked_for_date").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //this if clicked previous should not take to details screen
                        if (fromscreen.equalsIgnoreCase(Constants.KEY_HISTORY_PREVIOUS) || fromscreen.equalsIgnoreCase(Constants.KEY_HISTORY_CANCELLED)) {

                        } else {
                            context.startActivity(i);
                        }

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return row;
        }

        public class Holder {
            TextView mName, mLocation, mDistance, mTimings;
            ImageView imageView;
            RelativeLayout rootLayout;
        }
    }



}
