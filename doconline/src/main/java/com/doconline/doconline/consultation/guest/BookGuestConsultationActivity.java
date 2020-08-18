package com.doconline.doconline.consultation.guest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.doconline.doconline.DashboardActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.analytics.GATracker;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.MultipartFileUploadClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.consultation.AppointmentSlotFragment;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.helper.OnBookConsultationListener;
import com.doconline.doconline.helper.SingleUploadBroadcastReceiver;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.AppointmentSummery;
import com.doconline.doconline.model.Doctor;
import com.doconline.doconline.model.Subscription;
import com.doconline.doconline.model.SubscriptionPlan;
import com.doconline.doconline.model.TimeSlot;
import com.doconline.doconline.model.User;
import com.doconline.doconline.service.WaitngTimeBroadcast;
import com.doconline.doconline.subscription.guest.GuestSubscriptionFragment;
import com.doconline.doconline.utils.FileUtils;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_BOOKING_FAIL;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_FILE_UPLOAD_FAIL;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_SLOT_REQUIRE;
import static com.doconline.doconline.app.Constants.CONSULTATION_SELF;
import static com.doconline.doconline.app.Constants.EEE_MMM_DD_YYYY_HH_MM_A;
import static com.doconline.doconline.app.Constants.KEY_ACTIVATION_TYPE;
import static com.doconline.doconline.app.Constants.KEY_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT;
import static com.doconline.doconline.app.Constants.KEY_CODE;
import static com.doconline.doconline.app.Constants.KEY_CONTACT;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DESCRIPTION;
import static com.doconline.doconline.app.Constants.KEY_DISCOUNT;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_ERROR;
import static com.doconline.doconline.app.Constants.KEY_EXCEPTION;
import static com.doconline.doconline.app.Constants.KEY_EXCEPTION_TYPE_APPOINTMENT_EXISTS;
import static com.doconline.doconline.app.Constants.KEY_FILE_NAME;
import static com.doconline.doconline.app.Constants.KEY_IMAGE;
import static com.doconline.doconline.app.Constants.KEY_MEDIA_PARTNER_NAME;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_NAME;
import static com.doconline.doconline.app.Constants.KEY_NOTES;
import static com.doconline.doconline.app.Constants.KEY_NOTIFICATION_TYPE_NO_DOCTOR_AVAILABLE;
import static com.doconline.doconline.app.Constants.KEY_NOTIFICATION_TYPE_NO_DOCTOR_ONLINE;
import static com.doconline.doconline.app.Constants.KEY_ONETIME_TYPE;
import static com.doconline.doconline.app.Constants.KEY_ORDER_ID;
import static com.doconline.doconline.app.Constants.KEY_OTP_SENT;
import static com.doconline.doconline.app.Constants.KEY_PLAN_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_PLATFORM;
import static com.doconline.doconline.app.Constants.KEY_PREFILL;
import static com.doconline.doconline.app.Constants.KEY_RATINGS;
import static com.doconline.doconline.app.Constants.KEY_RECURRING_TYPE;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION_ID;
import static com.doconline.doconline.app.Constants.KEY_TITLE;
import static com.doconline.doconline.app.Constants.KEY_VALIDATE;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.service.ConsultationSlotBroadcast.slotBroadCast;


public class BookGuestConsultationActivity extends BaseActivity implements View.OnClickListener,
        OnBookConsultationListener, PaymentResultListener,
        SingleUploadBroadcastReceiver.Delegate
{
    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();


    Toolbar toolbar;
    CoordinatorLayout layout_root_view;
    RelativeLayout layout_block_ui;
    TextView tv_timing;
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

    /**
     * Onetime Discount Code Layout
     */

    NestedScrollView layout_discount_coupon_code;
    EditText edit_discount_coupon_code;
    TextView tv_plan_name;
    TextView tv_plan_amount;
    TextView tv_plan_discount;
    TextView tv_plan_total;
    Button btn_apply_coupon;
    TextView tv_coupon_status;

    private SectionsPagerAdapter adapter;
    private SlotRefreshDownTimer timer;

    private boolean is_booking = false;
    private boolean isOTPSent = false;
    public static boolean is_instant_booking_available = false;
    private boolean is_slot_synced = false;
    private boolean calculate_waiting_time = true;
    private boolean is_file_uploaded = true;
    private String razorpayPaymentID = "";
    private String discount_coupon = "";
    private boolean is_discount_coupon_applied = false;
    private String payment_type = "";

    private User mProfile;
    private SubscriptionPlan mPlan;

    public static final int HTTP_REQUEST_CODE_BOOK_APPOINTMENT = 1;
    public static final int HTTP_REQUEST_CODE_GET_SLOTS = 2;
    public static final int HTTP_REQUEST_CODE_VALIDATE_APPOINTMENT = 3;
    public static final int HTTP_REQUEST_CODE_VALIDATE_USER = 4;
    public static final int HTTP_REQUEST_CODE_UPDATE_PAYMENT = 5;
    public static final int HTTP_REQUEST_CODE_GET_PLANS = 6;
    public static final int HTTP_REQUEST_CODE_GET_ONETIME_PLAN = 7;
    public static final int HTTP_REQUEST_CODE_ATTACH_IMAGES = 8;
    public static final int HTTP_REQUEST_CODE_GET_SESSION = 9;
    public static final int HTTP_REQUEST_CODE_APPLY_COUPON_CODE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_guest_consultation);

        tv_consultation_date_time = findViewById(R.id.tv_consultation_date_time);

         layout_booking_date = findViewById(R.id.layout_booking_date);
         webview =  findViewById(R.id.webView);

        toolbar = findViewById(R.id.toolbar);
        layout_root_view= findViewById(R.id.layout_root_view);
        layout_block_ui= findViewById(R.id.layout_block_ui);
        tv_timing= findViewById(R.id.tv_timing);
        toolbar_title= findViewById(R.id.toolbar_title);
       layout_progress= findViewById(R.id.layout_loading);
        layout_refresh= findViewById(R.id.layout_refresh);
        layout_terms_and_condition= findViewById(R.id.layout_terms_and_condition);
        tv_patient_name= findViewById(R.id.tv_patient_name);
        tv_phone_number= findViewById(R.id.tv_phone_number);
        tv_date_of_consultation= findViewById(R.id.tv_date_of_consultation);
        webview= findViewById(R.id.webView);
        mViewPager= findViewById(R.id.pager);
       btnDone= findViewById(R.id.btnDone);

        /**
         * Onetime Discount Code Layout
         */
        layout_discount_coupon_code= findViewById(R.id.layout_discount_coupon_code);
        edit_discount_coupon_code= findViewById(R.id.edit_discount_coupon_code);
        tv_plan_name= findViewById(R.id.tv_plan_name);
        tv_plan_amount= findViewById(R.id.tv_plan_amount);
        tv_plan_discount= findViewById(R.id.tv_plan_discount);
        tv_plan_total= findViewById(R.id.tv_plan_total);
        btn_apply_coupon= findViewById(R.id.btnCouponApply);
        tv_coupon_status= findViewById(R.id.tv_coupon_status);

        btn_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new InternetConnectionDetector(BookGuestConsultationActivity.this).isConnected())
                {
                    if(!is_discount_coupon_applied)
                    {
                        tv_coupon_status.setVisibility(View.INVISIBLE);
                        String coupon_code = edit_discount_coupon_code.getText().toString();

                        if(coupon_code.length() < 3)
                        {
                            Toast.makeText(getApplicationContext(), "Coupon code must be at least 3 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        layout_progress.setVisibility(View.VISIBLE);
                        new HttpClient(HTTP_REQUEST_CODE_APPLY_COUPON_CODE, MyApplication.HTTPMethod.POST.getValue(), Subscription.composeDiscountCodeJSON(coupon_code, mPlan.getPlanType()), BookGuestConsultationActivity.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getGuestCouponCodeURL());
                    }

                    else
                    {
                        removeCoupon();
                    }
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }
            }
        });

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

        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
            return;
        }

        this.syncData();

        timer = new SlotRefreshDownTimer((60 * 1000 * 60), (1000 * 60));
        timer.start();

        tv_plan_discount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(0));
    }


    private void syncData()
    {
        if(new InternetConnectionDetector(this).isConnected())
        {
            layout_refresh.setVisibility(View.VISIBLE);

            HttpClient httpClient = new HttpClient(HTTP_REQUEST_CODE_GET_SESSION, MyApplication.HTTPMethod.GET.getValue(), this);
            httpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSessionURL());
        }
    }


    public void load_terms_and_conditions()
    {
        layout_terms_and_condition.setVisibility(View.VISIBLE);
        tv_patient_name.setText(mController.getAppointmentBookingSummery().getPatientName());

        String date = "";

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY);
            date = sdf.format(System.currentTimeMillis());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            if (mController.getAppointmentBookingSummery().getBookingType() == 1) {
                layout_booking_date.setVisibility(View.GONE);
                tv_consultation_date_time.setText(String.valueOf(date));
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
            tv_phone_number.setText("Not Available");

            if(mController.getAppointmentBookingSummery().getForWhom() == CONSULTATION_SELF)
            {
                webview.loadUrl("file:///android_asset/MajorConsentForm.html");
            }

            else
            {
                webview.loadUrl("file:///android_asset/MinorConsentForm.html");
            }
        }
    }

    public void onAcceptClick()
    {
        layout_terms_and_condition.setVisibility(View.GONE);

        if(isOTPSent && mProfile.getUserAccount().getOTPCode().length() != 6)
        {
            Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
            this.otpOptions();
            return;
        }

        this.validate_details();
    }

    private void setupViewPager(MyViewPager viewPager)
    {
        toolbar_title.setText(getResources().getString(R.string.text__book_consultation));

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new BookGuestConsultationFragment(this, this), "Book a Consultation");
        adapter.addFrag(new AppointmentSlotFragment(this, this, this), "Appointment Slots");
        adapter.addFrag(GuestAccountDetailsFragment.newInstance(), "Guest Account");
        adapter.addFrag(GuestSubscriptionFragment.newInstance(), "Book a Consultation");

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:

                        btnDone.setText("Book Now");
                        btnDone.setVisibility(View.VISIBLE);
                        break;

                    case 1:

                        btnDone.setText("Back");
                        btnDone.setVisibility(View.VISIBLE);
                        break;

                    case 2:

                        isOTPSent = false;
                        btnDone.setText("Back");
                        btnDone.setVisibility(View.GONE);
                        break;

                    case 3:

                        timer.cancel();
                        btnDone.setText("View Appointments");
                        btnDone.setVisibility(View.GONE);
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
    protected void onResume()
    {
        super.onResume();

        try
        {
            uploadReceiver.register(this);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        try
        {
            uploadReceiver.unregister(this);
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

            public void run()
            {
                layout_refresh.setVisibility(View.VISIBLE);
            }

        });
    }


    @Override
    public void onPostExecute(final int requestCode, final int responseCode, String response)
    {
        if(isFinishing())
        {
            return;
        }

        try
        {
            if((requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT || requestCode == HTTP_REQUEST_CODE_ATTACH_IMAGES
                    || requestCode == HTTP_REQUEST_CODE_UPDATE_PAYMENT) && responseCode == HttpClient.BAD_REQUEST)
            {
                String message = HttpResponseHandler.getMessage(response);

                new CustomAlertDialog(this,this, layout_root_view)
                        .showSnackbar(message + ". Try again after sometime.", CustomAlertDialog.LENGTH_SHORT);

                this.syncData();
                return;
            }


            if(requestCode == HTTP_REQUEST_CODE_UPDATE_PAYMENT)
            {
                if(responseCode == HttpClient.OK)
                {
                    /**
                     * Track registration success event (Google Analytics)
                     */
                    GATracker.trackEvent(GATracker.GA_CATEGORY_REGISTRATION, GATracker.GA_ACTION_REGISTRATION_SUCCESS, GATracker.GA_LABEL_ANDROID, 0);

                    try
                    {
                        JSONObject json = new JSONObject(response);
                        json = json.getJSONObject(KEY_DATA);
                        json = json.getJSONObject(KEY_APPOINTMENT);

                        int appointment_id = json.getInt(Constants.KEY_APPOINTMENT_ID);
                        String scheduled_at = json.getString(Constants.KEY_SCHEDULED_AT);
                        int call_type = json.getInt(Constants.KEY_CALL_TYPE);
                        int status = json.getInt(Constants.KEY_STATUS);

                        Appointment appointment = new Appointment(appointment_id, scheduled_at, call_type, status);

                        JSONObject json_details = new JSONObject(json.getString(Constants.KEY_DOCTOR));
                        String specialization = (json_details.isNull(Constants.KEY_SPECIALIZATION)) ? "" : json_details.getString(Constants.KEY_SPECIALIZATION);
                        String full_name = (json_details.isNull(Constants.KEY_FULL_NAME)) ? "" : json_details.getString(Constants.KEY_FULL_NAME);
                        String avatar_url = (json_details.isNull(Constants.KEY_AVATAR_URL)) ? "" : json_details.getString(Constants.KEY_AVATAR_URL);
                        double ratings = json_details.getDouble(KEY_RATINGS);

                        appointment.setDoctor(new Doctor(full_name, specialization, avatar_url, ratings));

                        json_details = new JSONObject(json.getString(Constants.KEY_PATIENT));
                        full_name = (json_details.isNull(Constants.KEY_FULL_NAME)) ? "" : json_details.getString(Constants.KEY_FULL_NAME);
                        appointment.setPatientName(full_name);

                        is_booking = false;

                        adapter.addFrag(new GuestAppointmentConfirmationFragment(this, appointment, mProfile), "Confirmation");
                        adapter.notifyDataSetChanged();

                        onPageSelection(adapter.getCount()-1, "Confirmation");
                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    return;
                }

                if(responseCode == HttpClient.UNPROCESSABLE_ENTITY)
                {
                    try
                    {
                        JSONObject json = new JSONObject(response);
                        json = json.getJSONObject(KEY_DATA);

                        if(json.has(KEY_EXCEPTION) && json.getBoolean(KEY_ERROR))
                        {
                            String exception = json.getString(KEY_EXCEPTION);
                            String message;

                            switch (exception)
                            {
                                case KEY_EXCEPTION_TYPE_APPOINTMENT_EXISTS:

                                    message = "An appointment already exists under your account.";
                                    break;

                                case KEY_NOTIFICATION_TYPE_NO_DOCTOR_AVAILABLE:

                                    message = "No doctors available for the selected slot Please choose a different time slot while booking.";
                                    break;

                                case KEY_NOTIFICATION_TYPE_NO_DOCTOR_ONLINE:

                                    message = "No doctors online at the moment, Please schedule an appointment with a different time slot.";
                                    break;

                                default:

                                    message = "Unknown Error: " + exception;
                                    break;
                            }

                            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_BOOKING_FAIL, this)
                            .showAlertDialogWithPositiveAction("Booking Fail!", message, getResources().getString(R.string.OK), false);
                        }
                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    finally
                    {
                        is_booking = false;
                    }

                    return;
                }

                else
                {
                    tv_timing.setVisibility(View.VISIBLE);
                    syncPaymentStatus(razorpayPaymentID, Helper.getRandomInteger(2000, 5000));
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_GET_SESSION && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    MyApplication.sSession = json.getString(KEY_DATA);
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }


            if(requestCode == HTTP_REQUEST_CODE_GET_SLOTS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    this.add_timeslot(json.toString());
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_VALIDATE_APPOINTMENT && responseCode == HttpClient.UNPROCESSABLE_ENTITY)
            {
                String message = HttpResponseHandler.getValidationErrorMessage(response);

                new CustomAlertDialog(this,this)
                        .showAlertDialogWithPositiveAction("Sorry!", message, getResources().getString(R.string.OK), true);
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_VALIDATE_USER && responseCode == HttpClient.ACCEPTED)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    json = json.getJSONObject(KEY_DATA);

                    boolean isValid = json.getBoolean(KEY_VALIDATE);
                    boolean isOTPSent = json.getBoolean(KEY_OTP_SENT);

                    if(isValid)
                    {
                        this.onPageSelection(3, "Book a Consultation");
                    }

                    else if(isOTPSent)
                    {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                    this.isOTPSent = isOTPSent;
                    this.otpOptions();
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_VALIDATE_USER && responseCode == HttpClient.REQUEST_LIMIT_EXCEED)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_VALIDATE_APPOINTMENT && responseCode == HttpClient.ACCEPTED)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    boolean isValid = json.getBoolean(KEY_VALIDATE);

                    if(isValid)
                    {
                        this.onPageSelection(2, "Book a Consultation");
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    if(mController.bookingSummery.getFiles().size() != 0)
                    {
                        this.is_file_uploaded = false;
                        mController.getAppointmentBookingSummery().getAllFileURL().clear();

                        String uploadId = UUID.randomUUID().toString();
                        uploadReceiver.setDelegate(this);
                        uploadReceiver.setUploadID(uploadId);

                        MultipartFileUploadClient mClient = new MultipartFileUploadClient(uploadId, AppointmentSummery.composeAttachmentJSON(mController.bookingSummery.getFiles()), AppointmentSummery.composeCaptionJSON(mController.bookingSummery.getFiles()), true);
                        mClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAttachmentsURL());
                    }

                    else
                    {
                        this.is_file_uploaded = true;
                    }
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    if(mPlan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE))
                    {
                        json = json.getJSONObject(KEY_DATA);

                        String subscription_id = json.getString(KEY_SUBSCRIPTION_ID);

                        mPlan.setSubscriptionId(subscription_id);
                        mPlan.setOrderId("");
                    }

                    else
                    {
                        String order_id = json.getString(KEY_DATA);

                        mPlan.setOrderId(order_id);
                        mPlan.setSubscriptionId("");

                    }

                    startPayment(mPlan);
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    layout_discount_coupon_code.setVisibility(View.GONE);
                    removeCoupon();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT && responseCode == HttpClient.UNPROCESSABLE_ENTITY)
            {
                is_booking = false;
                String message = HttpResponseHandler.getValidationErrorMessage(response);

                new CustomAlertDialog(this,this, layout_root_view)
                        .showSnackbar(message, CustomAlertDialog.LENGTH_SHORT);
                onPageSelection(2, "Book a Consultation");
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_VALIDATE_USER && responseCode == HttpClient.UNPROCESSABLE_ENTITY)
            {
                String message = HttpResponseHandler.getValidationErrorMessage(response);
                new CustomAlertDialog(this,this, layout_root_view)
                        .showSnackbar(message, CustomAlertDialog.LENGTH_SHORT);
                onPageSelection(2, "Book a Consultation");
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_ATTACH_IMAGES && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray = json.getJSONArray(KEY_DATA);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        json = jsonArray.getJSONObject(i);
                        String file_path = json.getString(KEY_FILE_NAME);
                        String title = json.isNull(KEY_TITLE) ? "" : json.getString(KEY_TITLE);
                        mController.getAppointmentBookingSummery().addFileURL(new FileUtils(file_path, title));
                    }
                }

                catch(JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_APPLY_COUPON_CODE && responseCode == HttpClient.BAD_REQUEST)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    tv_coupon_status.setTextColor(getResources().getColor(R.color.light_red));
                    tv_coupon_status.setVisibility(View.VISIBLE);
                    tv_coupon_status.setText(String.valueOf(json.getString(KEY_MESSAGE)));
                    edit_discount_coupon_code.setText("");
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_APPLY_COUPON_CODE && responseCode == HttpClient.OK)
            {
                JSONObject json = new JSONObject(response);
                json = json.getJSONObject(KEY_DATA);

                double plan_amount = json.getDouble(KEY_PLAN_AMOUNT);
                double discount = json.getDouble(KEY_DISCOUNT);
                double amount = json.getDouble(KEY_AMOUNT);
                discount_coupon = json.getString(KEY_CODE);

                tv_plan_total.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(amount));
                tv_plan_discount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(discount));

                btn_apply_coupon.setText("Remove");
                tv_coupon_status.setVisibility(View.VISIBLE);
                tv_coupon_status.setTextColor(getResources().getColor(R.color.light_green));
                tv_coupon_status.setText("Coupon Applied Successfully");
                this.is_discount_coupon_applied = true;
                return;
            }

            new HttpResponseHandler(getApplicationContext(), this, layout_root_view).handle(responseCode, response);
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

                    if(requestCode == HTTP_REQUEST_CODE_VALIDATE_APPOINTMENT
                            || requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT
                            || requestCode == HTTP_REQUEST_CODE_VALIDATE_USER
                            || requestCode == HTTP_REQUEST_CODE_APPLY_COUPON_CODE)
                    {
                        layout_block_ui.setVisibility(View.GONE);
                    }

                    if((requestCode == HTTP_REQUEST_CODE_UPDATE_PAYMENT || requestCode == HTTP_REQUEST_CODE_APPLY_COUPON_CODE )
                            && (responseCode == HttpClient.OK || responseCode == HttpClient.UNPROCESSABLE_ENTITY || responseCode == HttpClient.BAD_REQUEST))
                    {
                        layout_progress.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }


    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, BookGuestConsultationActivity.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                if(layout_terms_and_condition.getVisibility() == View.VISIBLE)
                {
                    layout_terms_and_condition.setVisibility(View.GONE);
                    return false;
                }

                if(layout_discount_coupon_code.getVisibility() == View.VISIBLE)
                {
                    layout_discount_coupon_code.setVisibility(View.GONE);
                    return false;
                }

                if (mViewPager.getCurrentItem() == 1 || mViewPager.getCurrentItem() == 2)
                {
                    onPageSelection(0, "Book a Consultation");
                    return false;
                }

                if (mViewPager.getCurrentItem() == 3 && !is_booking)
                {
                    onPageSelection(2, "Book a Consultation");
                    return false;
                }

                if(mViewPager.getCurrentItem() == 4)
                {
                    startActivity(new Intent(BookGuestConsultationActivity.this, MainActivity.class));
                    DashboardActivity.activity.finish();
                    finish();
                    return false;
                }

                if(!is_booking)
                {
                    finish();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Booking is in progress", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void validate_details()
    {
        HashMap<String, Object> hashMap = User.composeGuestUserJSON(mProfile);
        layout_block_ui.setVisibility(View.VISIBLE);

        HttpClient httpClient = new HttpClient(HTTP_REQUEST_CODE_VALIDATE_USER, MyApplication.HTTPMethod.POST.getValue(), hashMap, this);
        httpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getValidateGuestUserURL());
    }







    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btnSendOTP) {
            if (new InternetConnectionDetector(this).isConnected()) {
                mProfile.getUserAccount().setOTPCode("");
                HashMap<String, Object> hashMap = User.composeGuestUserJSON(mProfile);

                layout_block_ui.setVisibility(View.VISIBLE);

                HttpClient httpClient = new HttpClient(HTTP_REQUEST_CODE_VALIDATE_USER, MyApplication.HTTPMethod.POST.getValue(), hashMap, this);
                httpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getValidateGuestUserURL());
            }
        } else if (id == R.id.btnAccept) {
            layout_terms_and_condition.setVisibility(View.GONE);

            if (isOTPSent && mProfile.getUserAccount().getOTPCode().length() != 6) {
                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                this.otpOptions();
                return;
            }

            this.validate_details();
        } else if (id == R.id.btnDecline) {
            layout_terms_and_condition.setVisibility(View.GONE);
        } else if (id == R.id.btnDone) {
            if (mViewPager.getCurrentItem() == 1) {
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
                            .showAlertDialogWithPositiveAction("Not Available", "No slots available.",
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

                if (!new InternetConnectionDetector(this).isConnected()) {
                    new CustomAlertDialog(this, this, layout_root_view).snackbarForInternetConnectivity();
                    return;
                }

                if (!mController.getAppointmentBookingSummery().getTermsandConditionsStatus()) {
                    new CustomAlertDialog(this, this)
                            .showAlertDialogWithPositiveAction("Terms and conditions", "Please accept terms and conditions to proceed.",
                                    getResources().getString(R.string.OK), true);
                    return;
                }


                if (razorpayPaymentID.isEmpty()) {
                    layout_block_ui.setVisibility(View.VISIBLE);

                    new HttpClient(HTTP_REQUEST_CODE_VALIDATE_APPOINTMENT, MyApplication.HTTPMethod.POST.getValue(),
                            AppointmentSummery.composeAppointmentSummaryMap(mController.getAppointmentBookingSummery()), this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getValidateAppointmentURL());

                    return;
                }

                is_booking = true;
                tv_timing.setVisibility(View.GONE);
                syncPaymentStatus(razorpayPaymentID, Helper.getRandomInteger(2000, 5000));
            }
        }
    }


    @Override
    public void onBackPressed()
    {
        if(layout_terms_and_condition.getVisibility() == View.VISIBLE)
        {
            layout_terms_and_condition.setVisibility(View.GONE);
            return;
        }

        if(layout_discount_coupon_code.getVisibility() == View.VISIBLE)
        {
            layout_discount_coupon_code.setVisibility(View.GONE);
            return;
        }

        if (mViewPager.getCurrentItem() == 1 || mViewPager.getCurrentItem() == 2)
        {
            onPageSelection(0, "Book a Consultation");
        }

        else if (mViewPager.getCurrentItem() == 3 && !is_booking)
        {
            onPageSelection(2, "Book a Consultation");
        }

        else if(mViewPager.getCurrentItem() == 4)
        {
            startActivity(new Intent(BookGuestConsultationActivity.this, MainActivity.class));
            DashboardActivity.activity.finish();
            finish();
        }

        else if(!is_booking)
        {
            super.onBackPressed();
        }

        else
        {
            Toast.makeText(getApplicationContext(), "Booking is in progress", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPageSelection(int position, String title)
    {
        mViewPager.setCurrentItem(position);
        toolbar_title.setText(title);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        /**
         * Clear booking details and timeSlot list
         */
        mController.bookingSummery = new AppointmentSummery();
        mController.clearTimeSlot();
        mController.clearCalendar();

        /**
         * Clear Session on Destroy
         */
        MyApplication.sSession = "";

        if(timer != null)
        {
            timer.cancel();
        }
    }


    private void add_timeslot(String data)
    {
        try
        {
            int count = 0;

            JSONObject json = new JSONObject(data);

            List<String> key_list = new ArrayList<>();
            Iterator<String> keys= json.keys();

            mController.clearCalendar();
            mController.clearTimeSlot();

            while (keys.hasNext())
            {
                key_list.add(keys.next());
            }

            Collections.sort(key_list);

            for (String key: key_list)
            {
                count++;

                String value = json.getString(key);

                JSONArray array = new JSONArray(value);

                if(array.length() == 0 && count == 1)
                {
                    calculate_waiting_time = false;
                    continue;
                }

                if(array.length() > 0)
                {
                    mController.setCalendar(new TimeSlot(key));

                    for(int i=0;i< array.length(); i++)
                    {
                        String time = array.getString(i);
                        mController.setTimeSlot(new TimeSlot(key, time));
                    }
                }
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            this.is_slot_synced = true;
            slotBroadCast(getApplicationContext());
            this.waiting_time();
        }
    }


    /**
     * Calculate waiting time if slot available for first date
     */
    private void waiting_time()
    {
        if(mController.getCalendarSize() != 0)
        {
            if(mController.getTimeSlotListSize() != 0)
            {
                if(!calculate_waiting_time)
                {
                    return;
                }

                String time = slot_time(mController.timeSlotList, mController.getCalendar(0).slot_date);

                if(!time.isEmpty())
                {
                    WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), Helper.calculate_waiting_time());
                }

                else
                {
                    WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), -1);
                }
            }

            else
            {
                WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), -1);
            }
        }

        else
        {
            WaitngTimeBroadcast.waitingTimeBroadCast(getApplicationContext(), -1);
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {
        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, this, layout_root_view).snackbarForInternetConnectivity();
            return;
        }

        new HttpClient(HTTP_REQUEST_CODE_GET_SLOTS, MyApplication.HTTPMethod.GET.getValue(), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + Constants.KEY_CREATE);
    }


    public String slot_time(List<TimeSlot> mSlots, String date)
    {
        for(TimeSlot ts: mSlots)
        {
            if(ts.slot_date.equalsIgnoreCase(date))
            {
                return ts.start_time;
            }
        }

        return "";
    }


    private class SlotRefreshDownTimer extends CountDownTimer
    {
        SlotRefreshDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Log.i("onTimerTick", "onTick");
            waiting_time();

            onTaskCompleted(true, 0, "REFRESH");
        }

        @Override
        public void onFinish()
        {

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



    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_SLOT_REQUIRE)
        {
            onPageSelection(1, "Appointment Slots");
            return;
        }

        if(requestCode == DIALOG_REQUEST_CODE_BOOKING_FAIL || requestCode == DIALOG_REQUEST_CODE_FILE_UPLOAD_FAIL)
        {
            onPageSelection(0, "Book a Consultation");
        }

        if(requestCode == DIALOG_REQUEST_CODE_INTERNET)
        {
            finish();
        }
    }


    @Override
    public void onGuestDetails(User mProfile)
    {
        this.mProfile = mProfile;

        if(this.mProfile.getPhoneNo().isEmpty()
                || (!isOTPSent && !this.mProfile.getPhoneNo().isEmpty() && this.mProfile.getUserAccount().getOTPCode().isEmpty()))
        {
            this.isOTPSent = false;
            //this.load_terms_and_conditions();
            return;
        }

        if(isOTPSent && mProfile.getUserAccount().getOTPCode().length() != 6)
        {
            Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
            this.otpOptions();
            return;
        }

        this.validate_details();
    }


    @Override
    public void onSubscribed(SubscriptionPlan mPlan)
    {
        this.mPlan = mPlan;
        this.payment_type = mPlan.getType();

        if(!this.is_file_uploaded)
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_FILE_UPLOAD_FAIL, this).
                    showAlertDialogWithPositiveAction("Info", "All files are not uploaded. Please remove to continue booking.", getResources().getString(R.string.OK),true);
            return;
        }

        if(mPlan.getType().equalsIgnoreCase(KEY_ONETIME_TYPE))
        {
            layout_discount_coupon_code.setVisibility(View.VISIBLE);
            tv_plan_name.setText(mPlan.getDisplayName().toUpperCase());
            tv_plan_amount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(mPlan.getAmount()));
            tv_plan_total.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(mPlan.getAmount()));
            return;
        }

        layout_block_ui.setVisibility(View.VISIBLE);

        is_booking = true;

        String jsonData = AppointmentSummery.getAppointmentSummaryJSON(mController.getAppointmentBookingSummery(), mProfile, mPlan, discount_coupon);
        SharedPreferences sharedPreferences = getSharedPreferences ("SOCIALNWLOGIN", Context.MODE_PRIVATE);

        try
        {
            JSONObject jso = new JSONObject(jsonData);
            jso.put(KEY_MEDIA_PARTNER_NAME, sharedPreferences.getString("mediapartnername",""));
            jsonData = String.valueOf(jso);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        HttpClient httpClient = new HttpClient(HTTP_REQUEST_CODE_BOOK_APPOINTMENT, MyApplication.HTTPMethod.POST.getValue(), true, jsonData, this);
        httpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getGuestAppointmentURL());
    }


    private void syncPaymentStatus(final String razorpayPaymentID, int duration)
    {
        layout_progress.setVisibility(View.VISIBLE);

        if(new InternetConnectionDetector(BookGuestConsultationActivity.this).isConnected())
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if(mPlan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE))
                    {
                        String razorpay_id = mPlan.getSubscriptionId();
                        String jsonData = Subscription.getSubscriptionJSON(mController.getAppointmentBookingSummery(), razorpayPaymentID, razorpay_id, mPlan.getType());
                        HttpClient httpClient = new HttpClient(HTTP_REQUEST_CODE_UPDATE_PAYMENT, MyApplication.HTTPMethod.POST.getValue(),
                                true, jsonData, BookGuestConsultationActivity.this);
                        httpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getPaymentSuccessURL());
                    }

                    else
                    {
                        String razorpay_id = mPlan.getOrderId();
                        String jsonData = Subscription.getSubscriptionJSON(mController.getAppointmentBookingSummery(), razorpayPaymentID, razorpay_id, mPlan.getType());

                        HttpClient httpClient = new HttpClient(HTTP_REQUEST_CODE_UPDATE_PAYMENT, MyApplication.HTTPMethod.POST.getValue(),
                                true, jsonData, BookGuestConsultationActivity.this);
                        httpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getPaymentSuccessURL() + "onetime");
                    }
                }
            }, duration);
        }
    }


    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(final String razorpayPaymentID)
    {
        this.razorpayPaymentID = razorpayPaymentID;
        this.syncPaymentStatus(razorpayPaymentID, 12000);

        /**
         * If recurring type payment call profile API
         */
        if(payment_type.equalsIgnoreCase(KEY_RECURRING_TYPE))
        {
            /**
             * Track payment success event (Google Analytics)
             */
            GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_SUBSCRIPTION_PAYMENT_SUCCESS, mPlan.getDisplayName(), (long) mPlan.getAmount());
        }

        if(payment_type.equalsIgnoreCase(KEY_ONETIME_TYPE))
        {
            /**
             * Track payment success event (Google Analytics)
             */
            GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_ONETIME_PAYMENT_SUCCESS, mPlan.getDisplayName(), (long) mPlan.getAmount());
        }
    }


    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response)
    {
        Log.e("onPaymentError", "" + response + " " + code);

        try
        {
            Toast.makeText(getApplicationContext(), "Subscription Not Completed", Toast.LENGTH_LONG).show();
        }

        catch (Exception e)
        {
            Log.e(TAG, "Exception in onPaymentError", e);
        }

        finally
        {
            is_booking = false;

            /**
             * If recurring type payment call profile API
             */
            if(payment_type.equalsIgnoreCase(KEY_RECURRING_TYPE))
            {
                /**
                 * Track payment success event (Google Analytics)
                 */
                GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_SUBSCRIPTION_PAYMENT_FAIL, mPlan.getDisplayName(), (long) mPlan.getAmount());
            }

            if(payment_type.equalsIgnoreCase(KEY_ONETIME_TYPE))
            {
                /**
                 * Track payment success event (Google Analytics)
                 */
                GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_ONETIME_PAYMENT_FAIL, mPlan.getDisplayName(), (long) mPlan.getAmount());
            }
        }
    }


    public void startPayment(SubscriptionPlan plan)
    {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setKeyID(mController.getSession().getRazorpayApiKey());

        try
        {
            JSONObject options = new JSONObject();

            options.put(KEY_NAME, getResources().getString(R.string.app_name));
            options.put(KEY_DESCRIPTION, plan.getPlanName());

            options.put(KEY_IMAGE, "https://app.doconline.com/assets/images/logo.png");

            if(plan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE))
            {
                options.put(KEY_SUBSCRIPTION_ID, plan.getSubscriptionId());
            }

            else
            {
                options.put(KEY_ORDER_ID, plan.getOrderId());
            }

            JSONObject preFill = new JSONObject();
            preFill.put(KEY_EMAIL, mProfile.getEmail());
            preFill.put(KEY_CONTACT, mProfile.getPhoneNo());

            JSONObject notesFill = new JSONObject();

            notesFill.put(KEY_PLATFORM, "android");

            if(plan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE))
            {
                notesFill.put(KEY_SUBSCRIPTION_ID, plan.getSubscriptionId());
            }

            else
            {
                notesFill.put(KEY_ORDER_ID, plan.getOrderId());
            }

            notesFill.put(KEY_ACTIVATION_TYPE, "unregistered");

            options.put(KEY_NOTES, notesFill);
            options.put(KEY_PREFILL, preFill);

            co.open(activity, options);
        }

        catch (Exception e)
        {
            this.showDialog("Error", "Error in payment : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showDialog(String title, String content)
    {
        if(!isFinishing())
        {
            new CustomAlertDialog(this, this).
                    showAlertDialogWithPositiveAction(title, content, getResources().getString(R.string.OK),true);
        }
    }


    @Override
    public void onProgress(final UploadInfo uploadInfo)
    {
        Log.i("PROGRESS", "progress = " + uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(final UploadInfo uploadInfo, final ServerResponse serverResponse, final Exception exception)
    {
        if(isFinishing())
        {
            return;
        }

        Log.i("uploadInfo", "" + serverResponse.getHttpCode());
        Log.i("uploadInfo", "" + serverResponse.getBodyAsString());

        try
        {
            onPostExecute(HTTP_REQUEST_CODE_ATTACH_IMAGES, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public  void onCompleted(final UploadInfo uploadInfo, final ServerResponse serverResponse)
    {
        try
        {
            Log.i("HTTP_REQUEST_CODE", "" + serverResponse.getHttpCode());
            Log.i("HTTP_REQUEST_CODE", "" + serverResponse.getBodyAsString());

            if(serverResponse.getHttpCode() == HttpClient.OK || serverResponse.getHttpCode() == HttpClient.CREATED)
            {
                this.is_file_uploaded = true;
            }

            onPostExecute(HTTP_REQUEST_CODE_ATTACH_IMAGES, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled(final UploadInfo uploadInfo)
    {

    }


    private void otpOptions()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        for (Fragment fragment : fragmentManager.getFragments())
        {
            if (fragment != null && fragment.isVisible() && fragment instanceof GuestAccountDetailsFragment)
            {
                if(isOTPSent)
                {
                    ((GuestAccountDetailsFragment) fragment).displayOTPOption();
                }

                else
                {
                    ((GuestAccountDetailsFragment) fragment).hideOTPOption();
                }
            }
        }
    }


    private void removeCoupon()
    {
        edit_discount_coupon_code.setText("");
        tv_coupon_status.setVisibility(View.INVISIBLE);
        btn_apply_coupon.setText("Apply");

        this.discount_coupon = "";
        this.is_discount_coupon_applied = false;

        tv_plan_amount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(mPlan.getAmount()));
        tv_plan_total.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(mPlan.getAmount()));
        tv_plan_discount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(0));
    }

    /**
     * Onetime Payment Discount Code
     */
   /* @OnClick(R.id.btnDecline)
    public void cancel(View view)
    {
        layout_discount_coupon_code.setVisibility(View.GONE);
        this.removeCoupon();
    }*/

   // @OnClick(R.id.btnProceedToPay)
    public void proceedToPay(View view)
    {
        if(new InternetConnectionDetector(this).isConnected())
        {

            String jsonData = AppointmentSummery.getAppointmentSummaryJSON(mController.getAppointmentBookingSummery(), mProfile, mPlan, discount_coupon);
            SharedPreferences sharedPreferences = getSharedPreferences ("SOCIALNWLOGIN", Context.MODE_PRIVATE);

            try
            {
                JSONObject jso = new JSONObject(jsonData);
                jso.put(KEY_MEDIA_PARTNER_NAME, sharedPreferences.getString("mediapartnername",""));
                jsonData = String.valueOf(jso);
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }

            Log.d("AAA","SKIPMEDIASOURCE proceedtopay"+jsonData);

            layout_block_ui.setVisibility(View.VISIBLE);

            is_booking = true;

            HttpClient httpClient = new HttpClient(HTTP_REQUEST_CODE_BOOK_APPOINTMENT, MyApplication.HTTPMethod.POST.getValue(), true, jsonData, this);
            httpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getGuestAppointmentURL());
        }

        else
        {
            Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
        }
    }


}