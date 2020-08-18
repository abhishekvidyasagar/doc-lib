package com.doconline.doconline.consultation;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.appointment.AppointmentHistoryActivity;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.AppointmentSummery;
import com.doconline.doconline.model.Doctor;
import com.doconline.doconline.model.TimeSlot;
import com.doconline.doconline.model.User;
import com.doconline.doconline.service.WaitngTimeBroadcast;
import com.doconline.doconline.subscription.SubscriptionActivity;
import com.doconline.doconline.utils.CalendarUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_CALENDAR;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_EMAIL_VERIFICATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_MOBILE_VERIFICATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_SLOT_REQUIRE;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE;
import static com.doconline.doconline.app.Constants.CALL_MEDIUM_REGULAR;
import static com.doconline.doconline.app.Constants.CALL_TYPE_AUDIO;
import static com.doconline.doconline.app.Constants.CONSULTATION_FAMILY;
import static com.doconline.doconline.app.Constants.CONSULTATION_SELF;
import static com.doconline.doconline.app.Constants.EEE_MMM_DD_YYYY_HH_MM_A;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_RATINGS;
import static com.doconline.doconline.app.Constants.SUBSCRIPTION_PERMISSION_REQ_CODE;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.app.MyApplication.parent_job_id;
import static com.doconline.doconline.app.MyApplication.prefs;
import static com.doconline.doconline.service.ConsultationSlotBroadcast.slotBroadCast;


public class BookConsultationActivity extends BaseActivity implements View.OnClickListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 2;


    Toolbar toolbar;
    CoordinatorLayout layout_root_view;
    TextView toolbar_title;
    RelativeLayout layout_progress;
    RelativeLayout layout_refresh;
    RelativeLayout layout_terms_and_condition;
    TextView tv_patient_name;
    TextView tv_phone_number;
    TextView tv_date_of_consultation;
    TextView tv_consultation_date_time;
    LinearLayout layout_booking_date;
    WebView webview;
    MyViewPager mViewPager;
    Button btnDone;
    LinearLayout layout_consent_patient_name;
    LinearLayout layout_consent_attendant_name;
    LinearLayout layout_consent_age;
    LinearLayout layout_consent_date;
    LinearLayout layout_consent_phone_number;
    TextView tv_patient_age;
    TextView tv_patient_attendant_name;

    private SectionsPagerAdapter adapter;
    private SlotRefreshDownTimer timer;
    private boolean is_booking = false;
    public static boolean is_instant_booking_available = false;
    private boolean is_slot_synced = false;
    private boolean calculate_waiting_time = true;
    private Appointment appointment;
    private JSONObject fcmConfJson;
    private JSONObject apiConfJson;

    public static final int HTTP_REQUEST_CODE_GET_PROFILE = 1;
    public static final int HTTP_REQUEST_CODE_BOOK_APPOINTMENT = 2;
    public static final int HTTP_REQUEST_CODE_GET_SLOTS = 3;
    public static final int HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS = 4;
    public static final int HTTP_REQUEST_CODE_ATTACH_IMAGES = 5;
    public static final int HTTP_REQUEST_CODE_GET_APPOINTMENT = 6;
    public static final int HTTP_REQUEST_CODE_GET_COFIRMATION = 7;

    public static final int ACTIVITY_REQUEST_CODE = 8;

    public static final JSONObject map = new JSONObject();

    Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_consultation);
       // ButterKnife.bind(this);

        toolbar =  findViewById(R.id.toolbar);
        layout_root_view=  findViewById(R.id.layout_root_view);
        toolbar_title=  findViewById(R.id.toolbar_title);
        layout_progress=  findViewById(R.id.layout_loading);
        layout_refresh=  findViewById(R.id.layout_refresh);
        layout_terms_and_condition=  findViewById(R.id.layout_terms_and_condition);
        tv_patient_name=  findViewById(R.id.tv_patient_name);
        tv_phone_number=  findViewById(R.id.tv_phone_number);
        tv_date_of_consultation=  findViewById(R.id.tv_date_of_consultation);

        tv_consultation_date_time=  findViewById(R.id.tv_consultation_date_time);
        layout_booking_date=  findViewById(R.id.layout_booking_date);

        webview=  findViewById(R.id.webView);
        mViewPager=  findViewById(R.id.pager);
        btnDone=  findViewById(R.id.btnDone);

        layout_consent_patient_name=  findViewById(R.id.layout_consent_patient_name);

        layout_consent_attendant_name=  findViewById(R.id.layout_consent_attendant_name);

        layout_consent_age=  findViewById(R.id.layout_consent_age);

        layout_consent_date=  findViewById(R.id.layout_consent_date);

        layout_consent_phone_number=  findViewById(R.id.layout_consent_phone_number);

        tv_patient_age=  findViewById(R.id.tv_patient_age);
        tv_patient_attendant_name=  findViewById(R.id.tv_patient_attendant_name);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);

        setupViewPager(mViewPager);
        btnDone.setText("Book Now");

        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
            return;
        }

        //Get personal info.
        syncData();

        layout_refresh.setVisibility(View.VISIBLE);

        // to get job_id
        registerReceiver(mHandleNotificationReceiver, new IntentFilter(Constants.KEY_PUSH_INSTANT_BOOKING_BROADCAST_RECEIVER));
        //called every minute
        timer = new SlotRefreshDownTimer((60 * 1000 * 60), (1000 * 60));
        timer.start();

        if (!CheckingPermissionIsEnabledOrNot()) {
            RequestMultiplePermission();
        }
    }


    private void syncData() {
        if (new InternetConnectionDetector(this).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileURL());
        }
    }

    //Setting data to terms and conditions layout.
    public void load_terms_and_conditions() {
        layout_terms_and_condition.setVisibility(View.VISIBLE);
        tv_patient_name.setText(mController.getAppointmentBookingSummery().getPatientName());

        String date = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A);
            date = sdf.format(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (mController.getAppointmentBookingSummery().getBookingType() == 1) {
                layout_booking_date.setVisibility(View.GONE);
            } else {
                layout_booking_date.setVisibility(View.VISIBLE);
                String daate = Helper.UTC_to_Local_TimeZone(mController.getAppointmentBookingSummery().getAppointmentTime());
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
                    Date value = sdf.parse(daate);
                    sdf = new SimpleDateFormat(EEE_MMM_DD_YYYY_HH_MM_A);
                    tv_consultation_date_time.setText(sdf.format(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            tv_date_of_consultation.setText(String.valueOf(date));
            tv_phone_number.setText(String.valueOf(mController.getSession().getMobileNumber()));

            if (mController.getAppointmentBookingSummery().getForWhom() == CONSULTATION_SELF) {
                layout_consent_attendant_name.setVisibility(View.GONE);
                layout_consent_age.setVisibility(View.GONE);
                layout_consent_phone_number.setVisibility(View.VISIBLE);

                webview.loadUrl("file:///android_asset/MajorConsentForm.html");
            } else {
                int age = 0;

                if (!mController.getAppointmentBookingSummery().getPatientAge().isEmpty()) {
                    try {
                        age = Integer.parseInt(mController.getAppointmentBookingSummery().getPatientAge().split(" ")[0]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                if (age >= 16) {
                    layout_consent_attendant_name.setVisibility(View.VISIBLE);
                    layout_consent_age.setVisibility(View.VISIBLE);
                    layout_consent_phone_number.setVisibility(View.GONE);

                    tv_patient_attendant_name.setText(mController.getSession().getFullName());
                    tv_patient_age.setText(mController.getAppointmentBookingSummery().getPatientAge());

                    webview.loadUrl("file:///android_asset/FamilyConsentForm.html");
                } else {
                    layout_consent_attendant_name.setVisibility(View.GONE);
                    layout_consent_age.setVisibility(View.GONE);
                    layout_consent_phone_number.setVisibility(View.VISIBLE);

                    webview.loadUrl("file:///android_asset/MinorConsentForm.html");
                }
            }
        }
    }


    private void setupViewPager(MyViewPager viewPager) {
        toolbar_title.setText(getResources().getString(R.string.text__book_consultation));

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new BookConsultationFragment(this, this, this, primaryColor), "Book a Consultation");
        adapter.addFrag(new AppointmentSlotFragment(this, this, this), "Appointment Slots");
        adapter.addFrag(new PreferredLanguageFragment(this), "Languages");

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:

                        btnDone.setText("Book Now");
                        btnDone.setVisibility(View.VISIBLE);
                        break;

                    case 1:

                        btnDone.setText("Back");
                        btnDone.setVisibility(View.VISIBLE);
                        break;

                    case 2:

                        btnDone.setText("Back");
                        btnDone.setVisibility(View.VISIBLE);
                        break;

                    case 3:

                        timer.cancel();
                        btnDone.setText("View Appointments");
                        btnDone.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(final int requestCode, final int responseCode, final String response) {
        if (isFinishing()) {
            return;
        }

        try {
            runOnUiThread(new Runnable() {

                public void run() {
                    if (responseCode != HttpClient.ACCEPTED && is_booking && requestCode != HTTP_REQUEST_CODE_GET_SLOTS) {
                        is_booking = false;
                        layout_progress.setVisibility(View.GONE);
                        layout_refresh.setVisibility(View.INVISIBLE);
                    } else if (!is_booking) {
                        layout_refresh.setVisibility(View.INVISIBLE);
                    }
                }
            });

            if (requestCode == HTTP_REQUEST_CODE_GET_PROFILE && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    final User user = User.getUserFromJSON(json.toString());

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            tv_patient_name.setText(Helper.toCamelCase(user.getFullName()));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return;
            }


            if (requestCode == HTTP_REQUEST_CODE_GET_SLOTS && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    this.add_timeslot(json.toString());

                    is_booking = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT && responseCode == HttpClient.CREATED) {
                try {
                    JSONObject json = new JSONObject(response);

                    int appointment_id = json.getInt(Constants.KEY_APPOINTMENT_ID);
                    String scheduled_at = json.getString(Constants.KEY_SCHEDULED_AT);
                    int call_type = json.getInt(Constants.KEY_CALL_TYPE);
                    int status = json.getInt(Constants.KEY_STATUS);

                    appointment = new Appointment(appointment_id, scheduled_at, call_type, status);

                    JSONObject json_details = new JSONObject(json.getString(Constants.KEY_DOCTOR));
                    String specialization = (json_details.isNull(Constants.KEY_SPECIALIZATION)) ? "" : json_details.getString(Constants.KEY_SPECIALIZATION);
                    String full_name = (json_details.isNull(Constants.KEY_FULL_NAME)) ? "" : json_details.getString(Constants.KEY_FULL_NAME);
                    String avatar_url = (json_details.isNull(Constants.KEY_AVATAR_URL)) ? "" : json_details.getString(Constants.KEY_AVATAR_URL);
                    double ratings = json_details.getDouble(KEY_RATINGS);

                    appointment.setDoctor(new Doctor(full_name, specialization, avatar_url, ratings));

                    json_details = new JSONObject(json.getString(Constants.KEY_PATIENT));
                    full_name = (json_details.isNull(Constants.KEY_FULL_NAME)) ? "" : json_details.getString(Constants.KEY_FULL_NAME);
                    appointment.setPatientName(full_name);

                    adapter.addFrag(new AppointmentConfirmationFragment(this, this, appointment), "Appointment Confirmation");

                    adapter.notifyDataSetChanged();
                    onPageSelection(adapter.getCount() - 1, "Confirmation");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    is_booking = false;

                    runOnUiThread(new Runnable() {

                        public void run() {
                            layout_progress.setVisibility(View.GONE);
                            layout_refresh.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT && responseCode == HttpClient.ACCEPTED) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);
                    parent_job_id = json.getString(Constants.KEY_JOB_ID);
                    prefs.edit().putString(Constants.KEY_JOB_ID, parent_job_id).apply();

                    startTimer();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {

                        public void run() {
                            layout_progress.setVisibility(View.VISIBLE);
                            layout_refresh.setVisibility(View.VISIBLE);
                        }
                    });
                }
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT && responseCode == HttpClient.PAYMENT_REQUIRED) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.subscribe("Membership", json.getString(KEY_MESSAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT && responseCode == HttpClient.PRECONDITION_FAILED) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    if (json.has(KEY_MOBILE_NO)) {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_MOBILE_VERIFICATION, json.getString(KEY_MOBILE_NO));
                    } else if (json.has(KEY_EMAIL)) {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_EMAIL_VERIFICATION, json.getString(KEY_EMAIL));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_COFIRMATION && responseCode == HttpClient.OK) {
                apiConfJson = new JSONObject(response);
                goToConfirmationPage(apiConfJson);
                //onPostExecute(HTTP_REQUEST_CODE_BOOK_APPOINTMENT, HttpClient.CREATED, counsultationConfJson.toString());


                is_booking = false;
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {

                        public void run() {
                            layout_progress.setVisibility(View.GONE);
                            layout_refresh.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_COFIRMATION && responseCode == HttpClient.NOT_FOUND) {
                apiConfJson = new JSONObject(response);
                bookingFailWithRetry("Booking Fail", "Please check your internet connection and try again");

                is_booking = false;
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {

                        public void run() {
                            layout_progress.setVisibility(View.GONE);
                            layout_refresh.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                return;
            }

            new HttpResponseHandler(getApplicationContext(), this, layout_root_view).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (new InternetConnectionDetector(getApplicationContext()).isConnected()) {
                    if (fcmConfJson == null) {
                        new HttpClient(HTTP_REQUEST_CODE_GET_COFIRMATION, MyApplication.HTTPMethod.GET.getValue(),
                                BookConsultationActivity.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getBookingConfirmationURL(parent_job_id));
                    }
                }
            }

        }.start();
    }

    private void goToConfirmationPage(JSONObject response) {

        try {
            JSONObject json = response.getJSONObject("data");

            int appointment_id = json.getInt(Constants.KEY_APPOINTMENT_ID);
            String scheduled_at = json.getString(Constants.KEY_SCHEDULED_AT);
            int call_type = json.getInt(Constants.KEY_CALL_TYPE);
            int status = json.getInt(Constants.KEY_STATUS);

            appointment = new Appointment(appointment_id, scheduled_at, call_type, status);

            JSONObject json_details = new JSONObject(json.getString(Constants.KEY_DOCTOR));
            String specialization = (json_details.isNull(Constants.KEY_SPECIALIZATION)) ? "" : json_details.getString(Constants.KEY_SPECIALIZATION);
            String full_name = (json_details.isNull(Constants.KEY_FULL_NAME)) ? "" : json_details.getString(Constants.KEY_FULL_NAME);
            String avatar_url = (json_details.isNull(Constants.KEY_AVATAR_URL)) ? "" : json_details.getString(Constants.KEY_AVATAR_URL);
            double ratings = json_details.getDouble(KEY_RATINGS);

            appointment.setDoctor(new Doctor(full_name, specialization, avatar_url, ratings));

            json_details = new JSONObject(json.getString(Constants.KEY_PATIENT));
            full_name = (json_details.isNull(Constants.KEY_FULL_NAME)) ? "" : json_details.getString(Constants.KEY_FULL_NAME);
            appointment.setPatientName(full_name);

            adapter.addFrag(new AppointmentConfirmationFragment(this, this, appointment), "Appointment Confirmation");

            adapter.notifyDataSetChanged();
            onPageSelection(adapter.getCount() - 1, "Confirmation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public static void start(final Activity activity) {
        activity.startActivity(new Intent(activity, BookConsultationActivity.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (layout_terms_and_condition.getVisibility() == View.VISIBLE) {
                    layout_terms_and_condition.setVisibility(View.GONE);
                    return false;
                }

                if (mViewPager.getCurrentItem() == 1 || mViewPager.getCurrentItem() == 2) {
                    onPageSelection(0, "Book a Consultation");
                    return false;
                }

                if (!is_booking) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Booking is in progress", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void book_appointment() {
        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(this, this).showNetworkAlertDialog();
            return;
        }

        is_booking = true;
        layout_progress.setVisibility(View.VISIBLE);
        layout_refresh.setVisibility(View.VISIBLE);
        layout_terms_and_condition.setVisibility(View.GONE);

        new HttpClient(HTTP_REQUEST_CODE_BOOK_APPOINTMENT, MyApplication.HTTPMethod.POST.getValue(),
                AppointmentSummery.composeAppointmentSummaryMap(mController.getAppointmentBookingSummery()), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL());
    }


/*    @OnClick(R.id.btnAccept)
    public void onAcceptClick(View view) {
        book_appointment();
    }*/

/*    @OnClick(R.id.btnDecline)
    public void onDeclineClick(View view) {
        layout_terms_and_condition.setVisibility(View.GONE);
    }*/

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnAccept) {
            book_appointment();
        } else if (id == R.id.btnDecline) {
            layout_terms_and_condition.setVisibility(View.GONE);
        } else if (id == R.id.btnDone) {
            if (mViewPager.getCurrentItem() == 1 || mViewPager.getCurrentItem() == 2) {
                onPageSelection(0, "Book a Consultation");
                return;
            }

            if (mViewPager.getCurrentItem() == 0) {
                if (!new InternetConnectionDetector(this).isConnected()) {
                    new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
                    return;
                }

                if (!this.is_slot_synced) {
                    Toast.makeText(getApplicationContext(), "Syncing in progress ... ", Toast.LENGTH_LONG).show();
                    return;
                }

                if (mController.getAppointmentBookingSummery().getBookingType() == Constants.BOOKING_TYPE_SLOT && mController.timeSlotList.size() == 0) {
                    new CustomAlertDialog(this, this)
                            .showAlertDialogWithPositiveAction("Not Available", "We regret to inform there are no doctors available at this moment. Please try after sometime.",
                                    "Ok", true);
                    return;
                }

                if (mController.getAppointmentBookingSummery().getBookingType() == Constants.BOOKING_TYPE_INSTANCE && !is_instant_booking_available) {
                    new CustomAlertDialog(this, this)
                            .showAlertDialogWithPositiveAction("Not Available", "Instant booking not available for the day.",
                                    "Ok", true);
                    return;
                }

                if (mController.getAppointmentBookingSummery().getBookingType() == Constants.BOOKING_TYPE_SLOT &&
                        mController.getAppointmentBookingSummery().getAppointmentTime().isEmpty()) {
                    new CustomAlertDialog(this, DIALOG_REQUEST_CODE_SLOT_REQUIRE, this)
                            .showDialogWithAction("Consultation Time ?", "Select your suitable date and time for consultation.",
                                    "View Slots", getResources().getString(R.string.Cancel), false);

                    return;
                }

                if (mController.getAppointmentBookingSummery().getForWhom() == CONSULTATION_FAMILY &&
                        mController.getAppointmentBookingSummery().getBookedForUserId() == 0) {
                    new CustomAlertDialog(this, this)
                            .showAlertDialogWithPositiveAction("For Whom ?", "Select for whom you want to book consultation.",
                                    getResources().getString(R.string.OK), true);
                    return;
                }

                if (mController.getAppointmentBookingSummery().getCallType() == CALL_TYPE_AUDIO
                        && mController.getAppointmentBookingSummery().getCallMedium() == CALL_MEDIUM_REGULAR
                        && !mController.getSession().getMobileStatus()) {
                    this.showAlertDialog(DIALOG_REQUEST_CODE_MOBILE_VERIFICATION, getResources().getString(R.string.dialog_content_mobile_verification));
                    return;
                }

                if (!mController.getAppointmentBookingSummery().getTermsandConditionsStatus()) {
                    new CustomAlertDialog(this, this)
                            .showAlertDialogWithPositiveAction("Terms and conditions", "Please accept terms and conditions to proceed.",
                                    getResources().getString(R.string.OK), true);
                    return;
                }

                dialog = new Dialog(this);
                dialog.setContentView(R.layout.alert_followup);
                //dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                TextView cancel_followup = dialog.findViewById(R.id.cancel_followup);
                final RadioGroup followup_rg = dialog.findViewById(R.id.followup_rg);
                RadioButton followup = dialog.findViewById(R.id.followup);
                RadioButton second_opinion = dialog.findViewById(R.id.second_opinion);
                RadioButton new_consultation = dialog.findViewById(R.id.new_consultation);
                Button button_ok = dialog.findViewById(R.id.button_ok);
                // if button is clicked, close the custom dialog
                button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (followup_rg.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(BookConsultationActivity.this, "Please select one option", Toast.LENGTH_SHORT).show();
                        } else {
                            String tag = (String) dialog.findViewById(followup_rg.getCheckedRadioButtonId()).getTag();
                            if (tag.equalsIgnoreCase("1")) {
                                Intent i = new Intent(BookConsultationActivity.this, PreviousAppointmentsActivity.class);
                                i.putExtra("fromscreen", "bookconsultaion");
                                startActivityForResult(i, ACTIVITY_REQUEST_CODE);
                            } else if (tag.equalsIgnoreCase("2")) {
                                try {
                                    map.put("follow_up_id", "0");
                                    map.put("is_follow_up", "0");
                                    map.put("follow_up_reason", "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                book_appointment();
                                dialog.dismiss();
                            }
                        }

                    }
                });

                cancel_followup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                return;
            }

            if (mViewPager.getCurrentItem() == 3) {
                startActivity(new Intent(BookConsultationActivity.this, AppointmentHistoryActivity.class));
                finish();

                    /*if(CheckingCalendarPermissionIsEnabledOrNot())
                    {
                        new CalendarUtils(this, appointment).addCalendarEvent();
                    }

                    else
                    {
                        RequestCalendarPermission();
                    }*/
            }
        }
    }


    public boolean CheckingCalendarPermissionIsEnabledOrNot() {
        int ReadCalendarPermissionResult = ContextCompat.checkSelfPermission(this, READ_CALENDAR);
        int WriteCalendarPermissionResult = ContextCompat.checkSelfPermission(this, WRITE_CALENDAR);

        return ReadCalendarPermissionResult == PackageManager.PERMISSION_GRANTED &&
                WriteCalendarPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestCalendarPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        READ_CALENDAR,
                        WRITE_CALENDAR
                }, CALENDAR_PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CALENDAR_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean ReadCalendarPermissionResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteCalendarPermissionResult = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (ReadCalendarPermissionResult && WriteCalendarPermissionResult) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                        new CalendarUtils(this, appointment).addCalendarEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }

                break;

            case CAMERA_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean AudioPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && AudioPermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (layout_terms_and_condition.getVisibility() == View.VISIBLE) {
            layout_terms_and_condition.setVisibility(View.GONE);
            return;
        }

        if (mViewPager.getCurrentItem() == 1 || mViewPager.getCurrentItem() == 2) {
            onPageSelection(0, "Book a Consultation");
        } else if (!is_booking) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Booking is in progress", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageSelection(int position, String title) {
        mViewPager.setCurrentItem(position);
        toolbar_title.setText(title);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * Clear booking details and timeSlot list
         */
        mController.bookingSummery = new AppointmentSummery();

        mController.clearTimeSlot();
        mController.clearCalendar();

        if (timer != null) {
            timer.cancel();
        }

        try {
            unregisterReceiver(mHandleNotificationReceiver);
        } catch (Exception e) {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }
    }


    private void add_timeslot(String data) {
        try {
            int count = 0;

            JSONObject json = new JSONObject(data);

            List<String> key_list = new ArrayList<>();
            Iterator<String> keys = json.keys();

            mController.clearCalendar();
            mController.clearTimeSlot();

            while (keys.hasNext()) {
                key_list.add(keys.next());
            }

            Collections.sort(key_list);

            for (String key : key_list) {
                count++;

                String value = json.getString(key);

                JSONArray array = new JSONArray(value);

                if (array.length() == 0 && count == 1) {
                    calculate_waiting_time = false;
                    continue;
                }

                if (array.length() > 0) {
                    mController.setCalendar(new TimeSlot(key));

                    for (int i = 0; i < array.length(); i++) {
                        String time = array.getString(i);
                        mController.setTimeSlot(new TimeSlot(key, time));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            this.is_slot_synced = true;
            slotBroadCast(getApplicationContext());
            this.waiting_time();
        }
    }


    /**
     * Calculate waiting time if slot available for first date
     */
    // recieve broadcast in fragment and set time.

    private void waiting_time() {
        if (mController.getCalendarSize() != 0) {
            if (mController.getTimeSlotListSize() != 0) {
                if (!calculate_waiting_time) {
                    return;
                }

                String time = slot_time(mController.timeSlotList, mController.getCalendar(0).slot_date);

                if (!time.isEmpty()) {
                    WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), Helper.calculate_waiting_time());
                } else {
                    WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), -1);
                }
            } else {
                WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), -1);
            }
        } else {
            WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), -1);
        }
    }

    private void bookingFail(String title, String content) {
        if (!isFinishing()) {
            new CustomAlertDialog(this, this)
                    .showAlertDialogWithPositiveAction(title, content,
                            getResources().getString(R.string.OK), true);
        }
    }

    private void bookingFailWithRetry(String title, String content) {
        if (!isFinishing()) {
            new CustomAlertDialog(this, this)
                    .showDialogWithAction(title, content,
                            getResources().getString(R.string.OK),getResources().getString(R.string.Retry), true);
        }
    }

    private void subscribe(String title, String content) {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE, this)
                .showDialogWithAction(title, content,
                        getResources().getString(R.string.Choose),
                        getResources().getString(R.string.Cancel), true);
    }

    private void showAlertDialog(int requestCode, String message) {
        new CustomAlertDialog(this, requestCode, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_account_info), message,
                        getResources().getString(R.string.Verify),
                        getResources().getString(R.string.Cancel), true);
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message) {
        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(this, this).showNetworkAlertDialog();
            return;
        }

        layout_refresh.setVisibility(View.VISIBLE);

        new HttpClient(HTTP_REQUEST_CODE_GET_SLOTS, MyApplication.HTTPMethod.GET.getValue(), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + Constants.KEY_CREATE);
    }


    public String slot_time(List<TimeSlot> mSlots, String date) {
        for (TimeSlot ts : mSlots) {
            if (ts.slot_date.equalsIgnoreCase(date)) {
                return ts.start_time;
            }
        }

        return "";
    }

    // Every minute checks the slot available.
    private class SlotRefreshDownTimer extends CountDownTimer {
        SlotRefreshDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d("onTimerTick", "onTick");
            waiting_time();

            new HttpClient(HTTP_REQUEST_CODE_GET_SLOTS, MyApplication.HTTPMethod.GET.getValue(), BookConsultationActivity.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + Constants.KEY_CREATE);
        }

        @Override
        public void onFinish() {

        }
    }


    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }


    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (apiConfJson == null) {
                if (intent.getAction() == null) {
                    return;
                }

                String action = intent.getAction();

                if (action.equalsIgnoreCase(Constants.KEY_PUSH_INSTANT_BOOKING_BROADCAST_RECEIVER)) {
                    Log.d("INSTANT_DATA", intent.getStringExtra("json_data"));

                    try {
                        fcmConfJson = new JSONObject(intent.getStringExtra("json_data"));

                        if (fcmConfJson.has(Constants.KEY_JOB_ID)) {
                            String job_id = fcmConfJson.getString(Constants.KEY_JOB_ID);

                            if (job_id.equals(parent_job_id)) {
                                String type = fcmConfJson.getString(Constants.KEY_TYPE);
                                int y = type.lastIndexOf("\\") + 1;
                                type = type.substring(y);

                                if (type.equalsIgnoreCase(Constants.KEY_NOTIFICATION_TYPE_BOOKING_SUCCESS)) {
                                    onPostExecute(HTTP_REQUEST_CODE_BOOK_APPOINTMENT, HttpClient.CREATED, fcmConfJson.toString());
                                } else if (type.equalsIgnoreCase(Constants.KEY_NOTIFICATION_TYPE_BOOKING_FAILED)) {
                                    bookingFail("Booking Fail", String.valueOf(fcmConfJson.getString(Constants.KEY_ERROR)));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        is_booking = false;

                        if (!isFinishing()) {
                            runOnUiThread(new Runnable() {

                                public void run() {
                                    layout_progress.setVisibility(View.GONE);
                                    layout_refresh.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                }
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SUBSCRIPTION_PERMISSION_REQ_CODE && resultCode == SUBSCRIPTION_PERMISSION_REQ_CODE) {
            if (!new InternetConnectionDetector(this).isConnected()) {
                new CustomAlertDialog(this, this).showNetworkAlertDialog();
                return;
            }

            book_appointment();
        }

        if (requestCode == ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.e("AAA","RECEIVED ID IS : "+data.getStringExtra("followupid"));
            try {
                map.put("follow_up_id",data.getStringExtra("followupid"));
                map.put("is_follow_up","1");
                map.put("follow_up_reason",data.getStringExtra("followreason"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            book_appointment();
            dialog.dismiss();
        }
    }


    @Override
    public void onPositiveAction(int requestCode) {
        if (requestCode == DIALOG_REQUEST_CODE_MOBILE_VERIFICATION) {
            Intent intent = new Intent(BookConsultationActivity.this, MainActivity.class);
            intent.putExtra("INDEX", 4);
            startActivity(intent);
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_EMAIL_VERIFICATION) {
            Intent intent3 = new Intent(BookConsultationActivity.this, MainActivity.class);
            intent3.putExtra("INDEX", 9);
            startActivity(intent3);
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE) {
            Intent intent = new Intent(BookConsultationActivity.this, SubscriptionActivity.class);
            intent.putExtra("SUBSCRIPTION_VIA_BOOK_CONSULTATION", 1);
            startActivityForResult(intent, SUBSCRIPTION_PERMISSION_REQ_CODE);
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_SLOT_REQUIRE) {
            onPageSelection(1, "Appointment Slots");
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_INTERNET) {
            finish();
        }
    }

    @Override
    public void onNegativeAction(int requestCode) {
        book_appointment();
    }


    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission() {
        /**
         * Creating String Array with Permissions.
         */
        ActivityCompat.requestPermissions(BookConsultationActivity.this, new String[]
                {
                        CAMERA,
                        RECORD_AUDIO
                }, CAMERA_PERMISSION_REQUEST_CODE);
    }

    /**
     * Checking permission is enabled or not
     */
    public boolean CheckingPermissionIsEnabledOrNot() {
        int CameraPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int AudioPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return CameraPermissionResult == PackageManager.PERMISSION_GRANTED &&
                AudioPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}