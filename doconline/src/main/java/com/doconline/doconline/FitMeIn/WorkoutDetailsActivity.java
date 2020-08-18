package com.doconline.doconline.FitMeIn;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.doconline.doconline.app.Constants.ACCEPT;
import static com.doconline.doconline.app.Constants.AUTHORIZATION;
import static com.doconline.doconline.app.Constants.CONTENT_TYPE;
import static com.doconline.doconline.app.Constants.DOCONLINE_API;
import static com.doconline.doconline.app.Constants.DOCONLINE_API_VERSION;

public class WorkoutDetailsActivity extends BaseActivity {

    String workoutId = "";
    String selectedDate = "";
    String fromScreen = "";
    String fromTime = "";
    String toTime = "";
    String latLongs = "";
    String distance = "";
    int seatsCount = 0;

    String workoutBookingID = "";
    int HTTP_REQUEST_CODE_WORKOUTS_DETAILS = 1;
    int HTTP_REQUEST_CODE_WORKOUTS_BOOKING = 2;
    int HTTP_REQUEST_CODE_CANCEL_WORKOUT = 3;
    int REQUEST_CODE_SCANNER = 4;
    int HTTP_REQUEST_CODE_ATTENDANCE = 5;
    int CAMERA_PERMISSION_REQUEST_CODE = 6;

    TextView tvOutletName, tvWorkoutName, tvLocation, tvFromToTime, tvViewLocation, tvDescription,
            btnBook, btnCancel, tvSeatsCount, tvApproxTime, tvBookingId, tvBarcodetext, tvScanQR;
    LinearLayout content_layout, imp_layout;
    RelativeLayout layout_loading;
    ImageView imageViewDetails;
    Button btnScanQR;

    String outletName, workoutName, location, fromtimetotime, description, iscancelled;

    String userSelectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        doInitialisation();

        if (!workoutId.equalsIgnoreCase("")) {
            if (fromScreen.equalsIgnoreCase(Constants.MAIN_ACTIVITY)) {
                callWorkoutDetailsApi(workoutId);
            } else {
                workoutBookingID = getIntent().getStringExtra("bookingid");
                outletName = getIntent().getStringExtra("outletname");
                workoutName = getIntent().getStringExtra("workoutname");
                location = getIntent().getStringExtra("location");
                fromtimetotime = getIntent().getStringExtra("fromtime") + " To " + getIntent().getStringExtra("totime");
                description = getIntent().getStringExtra("description").replace("<br/>", "\n");
                latLongs = getIntent().getStringExtra("latlong");
                iscancelled = getIntent().getStringExtra("iscancelled");
                workoutId = getIntent().getStringExtra("workoutid");
                String date = getIntent().getStringExtra("date");
                String bookedtime = getIntent().getStringExtra("bookedtime");

                tvOutletName.setText(outletName);
                tvWorkoutName.setText("  " + workoutName);
                tvLocation.setText("  " + location);
                tvFromToTime.setText(date + "  " + fromtimetotime);
                tvDescription.setText("  " + description);
                tvSeatsCount.setVisibility(View.GONE);
                tvBookingId.setText("Booking ID : " + workoutBookingID);
                tvApproxTime.setClickable(false);
                tvApproxTime.setText("Slot booked at : "+date + " "+bookedtime);

                if (description.length() > 0) {
                    imp_layout.setVisibility(View.VISIBLE);
                } else {
                    imp_layout.setVisibility(View.GONE);
                }

                String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
                //String workoutIdforImage = dataObject.getString(Constants.KEY_WORKOUT_ID_);

                GlideUrl glideUrl = new GlideUrl(mController.getFitMeInWorkoutImageURL() + "/" + workoutId, new LazyHeaders.Builder()
                        .addHeader(AUTHORIZATION, "Bearer " + header)
                        .addHeader(ACCEPT, "application/json")
                        .addHeader(CONTENT_TYPE, "application/json")
                        .addHeader(DOCONLINE_API, DOCONLINE_API_VERSION)
                        .build());

                Glide.with(this)
                        .load(glideUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading)
                        .into(imageViewDetails);
            }
        }

        if (fromScreen.equalsIgnoreCase(Constants.KEY_HISTORY_UPCOMING) &&
                iscancelled.equalsIgnoreCase("0")) {
            btnScanQR.setVisibility(View.VISIBLE);
            tvScanQR.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnBook.setVisibility(View.GONE);

            int isAttended = getIntent().getIntExtra("isattended",0);
            if (isAttended == 1){
                btnScanQR.setVisibility(View.GONE);
                tvScanQR.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            }
        } else if (fromScreen.equalsIgnoreCase(Constants.MAIN_ACTIVITY)) {
            btnScanQR.setVisibility(View.GONE);
            tvScanQR.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnBook.setVisibility(View.VISIBLE);
        }
    }

    private void doInitialisation() {
        workoutId = getIntent().getStringExtra(Constants.WOORKOUT_DETAILS_ID);
        selectedDate = getIntent().getStringExtra(Constants.SELECTED_DATE);
        fromScreen = getIntent().getStringExtra(Constants.FROM_SCREEN_KEY);

        tvOutletName = findViewById(R.id.outlet_name);
        tvWorkoutName = findViewById(R.id.workout_name);
        tvLocation = findViewById(R.id.location);
        tvFromToTime = findViewById(R.id.time);
        tvViewLocation = findViewById(R.id.location_maps);
        tvDescription = findViewById(R.id.description);
        imageViewDetails = findViewById(R.id.image_view_details);
        btnBook = findViewById(R.id.btn_book);
        btnCancel = findViewById(R.id.btn_cancel);

        layout_loading = findViewById(R.id.layout_loading);
        content_layout = findViewById(R.id.content_layout);
        imp_layout = findViewById(R.id.imp_layout);

        tvSeatsCount = findViewById(R.id.seatscount_tv);
        tvApproxTime = findViewById(R.id.approxtime_tv);

        tvBookingId = findViewById(R.id.bookingid_tv);

        tvBarcodetext = findViewById(R.id.barcodetext);

        btnScanQR = findViewById(R.id.scanqr_btn);
        tvScanQR = findViewById(R.id.scanqr_tv);
    }

    private void callWorkoutDetailsApi(String workoutId) {
        layout_loading.setVisibility(View.VISIBLE);
        content_layout.setVisibility(View.GONE);
        if (new InternetConnectionDetector(this).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_WORKOUTS_DETAILS, MyApplication.HTTPMethod.GET.getValue(), this).
                    execute(mController.getWorkoutDetailsUrl() + workoutId);
        } else {
            //new CustomAlertDialog(WorkoutDetailsActivity.this, this).snackbarForInternetConnectivity();
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        layout_loading.setVisibility(View.GONE);
        try {
            if (requestCode == HTTP_REQUEST_CODE_WORKOUTS_DETAILS && responseCode == 200) {
                content_layout.setVisibility(View.VISIBLE);
                populateData(response);
            }

            if (requestCode == HTTP_REQUEST_CODE_WORKOUTS_BOOKING) {
                populateMessage(response);//can be replaced with handler
            }

            if (requestCode == HTTP_REQUEST_CODE_CANCEL_WORKOUT) {
                populateMessage(response);
                btnCancel.setVisibility(View.GONE);
                btnScanQR.setVisibility(View.GONE);
                tvScanQR.setVisibility(View.GONE);
            }

            if (requestCode == HTTP_REQUEST_CODE_ATTENDANCE) {
                populateMessage(response);
            }
        } catch (Exception e) {
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(0, response);
            e.printStackTrace();
        }

    }

    private void populateMessage(String response) {
        try {
            JSONObject responseObeject = new JSONObject(response);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Alert !");
            alert.setMessage("" + responseObeject.getString("message"));
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alert.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateData(String response) {
        try {
            JSONObject responseObject = new JSONObject(response);
            Log.e("AAA", "DETAILS RESPONSE : " + responseObject);
            JSONObject dataObject = responseObject.getJSONObject(Constants.KEY_DATA);

            fromTime = dataObject.getString(Constants.KEY_FROM_TIME_DETAILS);
            toTime = dataObject.getString(Constants.KEY_TO_TIME_DETAILS);

            outletName = dataObject.getString(Constants.KEY_OUTLET_NAME);
            workoutName = dataObject.getString(Constants.KEY_WORKOUT_NAME);
            location = dataObject.getString(Constants.KEY_STREET_ADDRESS_1) + ", " +
                    dataObject.getString(Constants.KEY_STREET_ADDRESS_2) + ", " +
                    dataObject.getString(Constants.KEY_ADDRESS_CITY) + ".";


            String workoutdate = dataObject.getString("WorkoutDate");

            //selectedDate = getIntent().getStringExtra(Constants.SELECTED_DATE);
            fromtimetotime = selectedDate + " " + dataObject.getString(Constants.KEY_FROM_TIME_DETAILS) + " TO " + dataObject.getString(Constants.KEY_TO_TIME_DETAILS);
            description = dataObject.getString(Constants.KEY_DESCRIPTION_DETAILS).replace("<br/><br/>", "");
            latLongs = dataObject.getString(Constants.KEY_LAT_LONG);
            distance = getIntent().getStringExtra(Constants.DISTANCE);

            seatsCount = dataObject.getInt(Constants.KEY_SEATS_COUNT);

            tvOutletName.setText(outletName);
            tvWorkoutName.setText("  " + workoutName);
            tvLocation.setText("  " + location);
            tvFromToTime.setText("  " + fromtimetotime);
            tvDescription.setText("  " + description);
            if (seatsCount > 0) {
                tvSeatsCount.setText(" Available seats - " + seatsCount);
            } else {
                tvSeatsCount.setVisibility(View.GONE);
                btnBook.setVisibility(View.GONE);
            }

            if (description.length() > 0) {
                imp_layout.setVisibility(View.VISIBLE);
            } else {
                imp_layout.setVisibility(View.GONE);
            }

            String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
            String workoutIdforImage = dataObject.getString(Constants.KEY_WORKOUT_ID_);

            GlideUrl glideUrl = new GlideUrl(mController.getFitMeInWorkoutImageURL() + "/" + workoutIdforImage, new LazyHeaders.Builder()
                    .addHeader(AUTHORIZATION, "Bearer " + header)
                    .addHeader(ACCEPT, "application/json")
                    .addHeader(CONTENT_TYPE, "application/json")
                    .addHeader(DOCONLINE_API, DOCONLINE_API_VERSION)
                    .build());

            Glide.with(this)
                    .load(glideUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_loading)
                    .into(imageViewDetails);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void goBack(View view) {
        finish();
    }

    public void bookWorkout(View view) {
        if (userSelectedTime.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please schedule your slot.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Book Workout");
            alert.setMessage("Are you sure you want to book workout");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    callBookingAPi();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alert.show();
        }
    }

    private void callBookingAPi() {
        String bookingtime = userSelectedTime.replace("AM", "am").replace("PM", "pm");
        //String fstchar = String.valueOf(bookingtime.charAt(0));

        //if (fstchar.equalsIgnoreCase("0")){
        //    bookingtime = bookingtime.replaceFirst("0","");
        //}
        layout_loading.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        HashMap<String, Object> requestObject = new HashMap<>();
        requestObject.put(Constants.KEY_WORKOUT_ID_BOOK, workoutId);
        requestObject.put(Constants.KEY_WORKOUT_BOOK_DATE, selectedDate);
        requestObject.put(Constants.KEY_WORKOUT_BOOK_TIME, bookingtime);
        requestObject.put(Constants.KEY_DISTANCE_BOOK, distance);
        Log.e("AAA", "Booking request object : " + requestObject);
        String rawRequestObject = gson.toJson(requestObject);
        if (new InternetConnectionDetector(this).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_WORKOUTS_BOOKING, MyApplication.HTTPMethod.POST.getValue(),
                    true, rawRequestObject, this).execute(mController.getBookingUrl());
        } else {
            new CustomAlertDialog(WorkoutDetailsActivity.this, this).snackbarForInternetConnectivity();
        }
    }

    public void cancelWorkout(final View view) {
        AlertDialog.Builder confirmationAlert = new AlertDialog.Builder(this);
        confirmationAlert.setTitle("Cancel Workout");
        confirmationAlert.setMessage("Are you sure you wish to cancel workout ?");
        confirmationAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showCancellationReasonAlert(view);
            }
        });

        confirmationAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        confirmationAlert.show();
    }

    private void showCancellationReasonAlert(final View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_cancel_workout);
        final EditText cancelWorkoutReason = dialog.findViewById(R.id.cancel_reason);
        TextView cancelWorkoutBtn = dialog.findViewById(R.id.cancel_btn);

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        cancelWorkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelWorkoutReason.getText().toString().length() > 4) {
                    dialog.dismiss();
                    callCancellationAPI("" + cancelWorkoutReason.getText());
                } else {
                    Snackbar.make(v, "Minimum 5 characters required", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void callCancellationAPI(String cancelReason) {
        layout_loading.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        HashMap<String, Object> requestObject = new HashMap<>();
        requestObject.put(Constants.KEY_WORKOUT_ID_BOOK, workoutId);
        requestObject.put(Constants.KEY_WORKOUT_BOOKING_ID, workoutBookingID);
        requestObject.put(Constants.KEY_WORKOUT_CANCEL_REASON, cancelReason);
        String rawRequestObject = gson.toJson(requestObject);
        new HttpClient(HTTP_REQUEST_CODE_CANCEL_WORKOUT, MyApplication.HTTPMethod.POST.getValue(),
                true, rawRequestObject, this).execute(mController.getBookingCancellationUrl());
    }

    public void getDirections(View view) {
        String url = "https://www.google.com/maps/dir/?api=1&destination=" + latLongs + "&travelmode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void approxTime(View view) {
        Log.e("AAA", "from time : " + fromTime);
        Log.e("AAA", "to time : " + toTime);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        final TimePickerDialog mTimePicker = new TimePickerDialog(WorkoutDetailsActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        try {
                            String _24HourTime = selectedHour + ":" + selectedMinute;
                            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat _12HourSDF = new SimpleDateFormat("h:mm a");
                            Date _24HourDt = _24HourSDF.parse(_24HourTime);
                            userSelectedTime = _12HourSDF.format(_24HourDt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            SimpleDateFormat receivedTimeFormat = new SimpleDateFormat("hh:mm a");
                            Date receivedFromTime = receivedTimeFormat.parse(fromTime);
                            Date receivedToTime = receivedTimeFormat.parse(toTime);
                            Date userSelectedTimee = receivedTimeFormat.parse(userSelectedTime);
                            Log.e("AAA", "user time : " + userSelectedTimee);
                            if (userSelectedTimee.after(receivedFromTime) && userSelectedTimee.before(receivedToTime)) {
                                Log.e("AAA", "VAlid Time");
                                tvApproxTime.setText("Selected slot : "+userSelectedTime);
                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(WorkoutDetailsActivity.this);
                                alert.setTitle("Invalid Time Selected !");
                                alert.setMessage("Please select time in between " + fromTime + " to " + toTime);
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        tvApproxTime.setText("Invalid Time");
                                        userSelectedTime = "";
                                    }
                                });
                                alert.show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void scanQRCode(View view) {
        PackageManager pm = this.getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent i = new Intent(WorkoutDetailsActivity.this, BarcodeScanner.class);
            startActivityForResult(i, REQUEST_CODE_SCANNER);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(WorkoutDetailsActivity.this, BarcodeScanner.class);
                startActivityForResult(i, REQUEST_CODE_SCANNER);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityResult ( int requestcode, int resultcode, Intent data) {

        super.onActivityResult(requestcode, resultcode, data);

        if (requestcode == REQUEST_CODE_SCANNER) {
            if (resultcode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String barcode = data.getStringExtra("barcode");
                    tvBarcodetext.setText(barcode);
                    //call api here
                    Gson gson = new Gson();
                    HashMap<String, Object> requestObject = new HashMap<>();
                    requestObject.put(Constants.KEY_WORKOUT_BOOKING_ID, workoutBookingID);
                    requestObject.put(Constants.KEY_WORKOUT_OUTLET_ID, barcode);
                    String rawRequestObject = gson.toJson(requestObject);
                    layout_loading.setVisibility(View.VISIBLE);
                    new HttpClient(HTTP_REQUEST_CODE_ATTENDANCE, MyApplication.HTTPMethod.POST.getValue(),
                            true, rawRequestObject, this).execute(mController.getWorkoutAttendance());
                } else {
                    tvBarcodetext.setText("NO DATA");
                }
            }
        }
    }
    }
