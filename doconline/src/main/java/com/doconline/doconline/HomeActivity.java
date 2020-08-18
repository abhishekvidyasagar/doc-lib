package com.doconline.doconline;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doconline.doconline.FitMeIn.FitMeInActivity;
import com.doconline.doconline.HRA.HRAMainActivity;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.analytics.GATracker;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.response.settings.FamilyConfig;
import com.doconline.doconline.api.response.settings.HotlineConfig;
import com.doconline.doconline.api.response.settings.SettingsResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.appointment.AppointmentHistoryActivity;
import com.doconline.doconline.appointment.AppointmentSummeryActivity;
import com.doconline.doconline.chat.ChatHistoryActivity;
import com.doconline.doconline.chat.FirebaseChatActivity;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.consultation.BookConsultationActivity;
import com.doconline.doconline.diagnostics.DiagnosticsActivity;
import com.doconline.doconline.diagnostics.DiagnosticsAppointmentConfirmationFragment;
import com.doconline.doconline.diagnostics.DiagnosticsHistoryActivity;
import com.doconline.doconline.ehr.EHRActivity;
import com.doconline.doconline.ehr.VitalsListActivity;
import com.doconline.doconline.floating.TimerService;
import com.doconline.doconline.floating.Utils;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.medicalremainders.MRHome;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.DiagnosticsCart;
import com.doconline.doconline.model.DiagnosticsUserAddress;
import com.doconline.doconline.model.PreferredLanguage;
import com.doconline.doconline.model.User;
import com.doconline.doconline.notification.NotificationActivity;
import com.doconline.doconline.order.OrderActivity;
import com.doconline.doconline.profile.ProfileActivity;
import com.doconline.doconline.profile.SettingsActivity;
import com.doconline.doconline.service.AlarmReceiver;
import com.doconline.doconline.service.ProfileUpdateBroadcast;
import com.doconline.doconline.service.TileUpdateBroadcast;
import com.doconline.doconline.subscription.BillingHistoryActivity;
import com.doconline.doconline.subscription.SubscriptionActivity;
import com.doconline.doconline.utils.DeviceUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_CHAT_CONFIRMATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_CONFIRM_PASSWORD;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_OVERLAY_PERMISSION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_PROCURE_MEDICINE;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_SET_PASSWORD;
import static com.doconline.doconline.app.Constants.CALL_STATUS_ACTIVE;
import static com.doconline.doconline.app.Constants.JSON_TAG;
import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_BOOKING_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_CARTFRAGMENT_INDEX;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DEVICE_SYNCED;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICSLISTFRAGMENT_INDEX;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CARTCOUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_FRAGMENT_INDEX;
import static com.doconline.doconline.app.Constants.KEY_ENGLISH_NAME;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_LAST_AUTHENTICATED_TIME;
import static com.doconline.doconline.app.Constants.KEY_MEDIA_PARTNER_NAME;
import static com.doconline.doconline.app.Constants.KEY_READONLY;
import static com.doconline.doconline.app.Constants.KEY_REMAINING_TIME;
import static com.doconline.doconline.app.Constants.KEY_SCHEDULED_AT;
import static com.doconline.doconline.app.Constants.TATA_THEME;
import static com.doconline.doconline.app.Constants.TYPE_B2B_PAID;
import static com.doconline.doconline.app.Constants.TYPE_ONETIME;
import static com.doconline.doconline.app.MyApplication.prefs;

//import com.adgyde.android.PAgent;


public class HomeActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        DiagnosticsAppointmentConfirmationFragment.OnAppointmentConfirmationFragmentListener {

    private int badgeCount = 0;
    private int cartBadgeCount = 0;
    public static final int PERMISSION_REQUEST_CODE = 2;

    private Menu menu;
    private AppointmentReminderTimer timer;
    private int mAppointmentId;
    private String mScheduledTime;

    DrawerLayout drawer;
    TextView tv_toolbar_title;
    RelativeLayout layout_refresh;
    RelativeLayout layout_block_ui;
    CoordinatorLayout layout_root_view;


    NavigationView navigationView;

    private int PROFILE_ACCESS_CODE;
    private boolean isParentUser;

    private static final int HTTP_REQUEST_CODE_GET_APPOINTMENT_HISTORY = 1;
    private static final int HTTP_REQUEST_CODE_GET_PASSWORD_STATUS = 2;
    private static final int HTTP_REQUEST_CODE_GET_NOTIFICATIONS = 3;
    private static final int HTTP_REQUEST_CODE_GET_PROFILE_STATE = 4;
    private static final int HTTP_REQUEST_CODE_GET_LANGUAGES = 5;
    private static final int HTTP_REQUEST_CODE_POST_DEVICE_INFORMATION = 6;
    private static final int HTTP_REQUEST_CODE_POST_PROFILE_PASSWORD = 7;
    private static final int HTTP_REQUEST_CODE_PROCURE_MEDICINE = 8;
    private static final int HTTP_REQUEST_CODE_LEAD_SOURCE = 9;
    private static final int HTTP_REQUEST_CODE_GET_CONSENT_STATUS = 10;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST = 11;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_CART_DATA = 12;
    private static final int HTTP_REQUEST_CODE_GET_SETTINGS = 13;

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 14;

    SharedPreferences sharedPreferences, medicinesAlertSharedPref;

    SharedPreferences appThemeSharedPreferences;
    SharedPreferences.Editor appThemeEditor;

    private String helpline_number;

    Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        tv_toolbar_title = findViewById(R.id.toolbar_title);
        layout_refresh =  findViewById(R.id.layout_refresh);
        layout_block_ui = findViewById(R.id.layout_block_ui);
        layout_root_view =  findViewById(R.id.layout_root_view);
        navigationView = findViewById(R.id.nav_view);

        sharedPreferences = getSharedPreferences("SOCIALNWLOGIN", Context.MODE_PRIVATE);
        medicinesAlertSharedPref = getSharedPreferences("MEDICINESALERT", Context.MODE_PRIVATE);

        //set Theme dynamically
        appThemeSharedPreferences = getSharedPreferences(Constants.APP_THEME, Context.MODE_PRIVATE);
        appThemeEditor = appThemeSharedPreferences.edit();

        //If not logged in redirect to login and finish(Redirected in checkLogin()).
        if (!mController.getSession().checkLogin())
        {
            finish();
            return;
        }

        //If a message comes and contains an URL of doconline then will be redirected to app or website.
        deepLink();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        this.helpline_number = mController.getSession().getCustomerCareNumber();
        setCustomerCareNumber(this.helpline_number, "Customer Care Number");



        DiagnosticsAppointmentConfirmationFragment.setListener(this);

        //If less then Oreo, show countdown head
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O)
        {
            if (!Utils.canDrawOverlays(HomeActivity.this))
            {
                needPermissionDialog();
            }
        }

        registerReceiver(mHandleNotificationReceiver, new IntentFilter(Constants.KEY_PUSH_BROADCAST_RECEIVER));
        timer = new AppointmentReminderTimer((60 * 1000 * 60), (1000 * 60));
        timer.start();

        if (!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
            return;
        }

        //All apis which are required in home, are called.
        this.syncData();

        //If newest version is not installed then alert is shown.
        if (mController.getSession().getUpdateAlert())
        {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run()
                {
                    new CustomAlertDialog(HomeActivity.this).showUpdateDialog();
                }
            }, 1500);
        }
    }


    private void syncData()
    {
        if (!prefs.getBoolean(KEY_DEVICE_SYNCED, false))
        {
            if (!DeviceUtils.get_push_token().isEmpty())
            {
                new HttpClient(HTTP_REQUEST_CODE_POST_DEVICE_INFORMATION, MyApplication.HTTPMethod.POST.getValue(),
                        DeviceUtils.composeDeviceUtils(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getDeviceURL());
            }
        }

        // Consent status(Whether you can book for your family members or not).
        new HttpClient(HTTP_REQUEST_CODE_GET_CONSENT_STATUS, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getConsentURL());
        // Upcoming appointment is shown.
        new HttpClient(HTTP_REQUEST_CODE_GET_APPOINTMENT_HISTORY, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + "upcoming");
        // Bell notificcation in home.
        new HttpClient(HTTP_REQUEST_CODE_GET_NOTIFICATIONS, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getNotificationURL());
        // Password is set or not.
        new HttpClient(HTTP_REQUEST_CODE_GET_PASSWORD_STATUS, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileURL() + Constants.KEY_PASSWORD);
        // User information.
        new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
        // For ordering medicines.
        new HttpClient(HTTP_REQUEST_CODE_PROCURE_MEDICINE, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getMedicineAvailabilityURL() + "pending");
        // Done by client.
        new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getDiagnosticsUserAddressListURL());
    }


    private void setCustomerCareNumber(String hotline_number, String number_type)
    {
        try
        {
            View header = navigationView.getHeaderView(0);
            TextView tv_customer_care_number = header.findViewById(R.id.tv_customer_care_number);
            String number = number_type + "\n" + hotline_number;
            tv_customer_care_number.setText(number);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            this.badgeCount = mController.getSession().getBadgeCount();
            this.cartBadgeCount = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_CART_COUNT, -1);

            if (menu != null)
            {
                onCreateOptionsMenu(menu);
            }

            this.displayUpcomingAppointmentTile();
            ProfileUpdateBroadcast.send(HomeActivity.this);

            if (new InternetConnectionDetector(this).isConnected())
            {
                new HttpClient(HTTP_REQUEST_CODE_GET_SETTINGS, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSettingsURL());
            }

            if (dialog != null){
                dialog.dismiss();
            }

            /*if (new InternetConnectionDetector(this).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
            }*/
        }


        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        try
        {
            unregisterReceiver(mHandleNotificationReceiver);

            if (timer != null)
            {
                timer.cancel();
            }
        }

        catch (Exception e)
        {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            if (menu != null) {
                menu.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getMenuInflater().inflate(R.menu.home, menu);

            if (menu != null) {
                this.menu = menu;

                if (badgeCount > 0) {
                    ActionItemBadge.update(this, menu.findItem(R.id.item_notification), FontAwesome.Icon.faw_bell, ActionItemBadge.BadgeStyles.RED, badgeCount);
                }

                if (cartBadgeCount > 0) {
                    ActionItemBadge.update(this, menu.findItem(R.id.item_cart_diagnostics), FontAwesome.Icon.faw_shopping_cart, ActionItemBadge.BadgeStyles.RED, cartBadgeCount);
                } else {
                    ActionItemBadge.update(this, menu.findItem(R.id.item_cart_diagnostics), FontAwesome.Icon.faw_shopping_cart, Integer.MIN_VALUE);
                }
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item_notification) {
            openActivity(8);
        } else if (itemId == R.id.item_cart_diagnostics) {
            Bundle sendBundle = new Bundle();
            sendBundle.putInt(KEY_DIAGNOSTICS_FRAGMENT_INDEX, KEY_CARTFRAGMENT_INDEX);

            Intent diagnosticsIntent = new Intent(HomeActivity.this, DiagnosticsActivity.class);
            diagnosticsIntent.putExtras(sendBundle);
            startActivity(diagnosticsIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_my_appointments) {
            openActivity(0);
        } else if (itemId == R.id.nav_chat_history) {
            openActivity(1);
        } else if (itemId == R.id.nav_order_history) {
            openActivity(2);
        } else if (itemId == R.id.nav_subscription_plan) {
            openActivity(3);
        } else if (itemId == R.id.nav_billing_history) {
            openActivity(4);
        } else if (itemId == R.id.nav_family) {
            openActivity(5);
        } else if (itemId == R.id.nav_profile) {
            openActivity(6);
        } else if (itemId == R.id.nav_blogs) {/*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.doconline.com/blog"));
                startActivity(browserIntent);*/

            startActivity(new Intent(HomeActivity.this, Blogs.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (itemId == R.id.nav_diagnosis_history) {
            startActivity(new Intent(HomeActivity.this, DiagnosticsHistoryActivity.class));
        } else if (itemId == R.id.nav_vitals) {
            startActivity(new Intent(HomeActivity.this, VitalsListActivity.class));
        } else if (itemId == R.id.nav_my_records) {
            startActivity(new Intent(HomeActivity.this, EHRActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void access_profile_info(String password) {
        if (password.isEmpty()) {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view)
                    .showSnackbar("Enter Password", CustomAlertDialog.LENGTH_SHORT);
            return;
        }

        if (new InternetConnectionDetector(this).isConnected()) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    layout_refresh.setVisibility(View.VISIBLE);
                    layout_block_ui.setVisibility(View.VISIBLE);
                }
            });

            new HttpClient(HTTP_REQUEST_CODE_POST_PROFILE_PASSWORD, MyApplication.HTTPMethod.POST.getValue(),
                    User.composePasswordJSON(password), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAccessPasswordURL());
        } else {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_image) {
            openActivity(6);
        }
    }

    // Sending broadcast to home fragment to show in the tile.
    private void displayUpcomingAppointmentTile() {
        if (!isFinishing()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Appointment appointment = Appointment.getNextAppointment(mController.appointmentList);
                    TileUpdateBroadcast.send(HomeActivity.this, appointment);
                }
            }, 1000);
        }
    }


    private void displayAppointmentNotificationAndTimer() {
        if (!isFinishing()) {
            String appointment = Appointment.getAppointmentTimer(mController.appointmentList);

            int appointment_id = Integer.valueOf(String.valueOf(appointment.split("\\|")[0]));
            long appointment_time = Long.valueOf(String.valueOf(appointment.split("\\|")[1]));

            if (appointment_id != -1 && appointment_time != -1 && !mController.isServiceRunning()) {
                startChatHead(appointment_id, appointment_time);
            }

            this.setAppointmentNotificationReminder();
        }
    }


    private void startChatHead(int appointment_id, long appointment_time) {
        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                if (!MyApplication.getInstance().isServiceRunning()) {
                    if (Utils.canDrawOverlays(MyApplication.getInstance())) {
                        prefs.edit().putInt(KEY_REMAINING_TIME, (int) appointment_time).apply();
                        prefs.edit().putInt(KEY_APPOINTMENT_ID, appointment_id).apply();
                        startService(new Intent(HomeActivity.this, TimerService.class));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void requestPermission(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DIALOG_REQUEST_CODE_OVERLAY_PERMISSION) {
            if (!Utils.canDrawOverlays(HomeActivity.this)) {
                needPermissionDialog();
                return;
            }

            if (new InternetConnectionDetector(this).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_GET_APPOINTMENT_HISTORY, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + "upcoming");
            }
        }
    }


    private void needPermissionDialog() {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_OVERLAY_PERMISSION, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_overlay_permission),
                        getResources().getString(R.string.dialog_content_overlay_permission),
                        getResources().getString(R.string.Settings),
                        getResources().getString(R.string.NotNow), false);
    }


    private void openActivity(int index) {
        switch (index) {
            case 0:

                this.PROFILE_ACCESS_CODE = 1;
                this.display_password_layout();
                break;

            case 1:

                ChatHistoryActivity.start(HomeActivity.this);
                break;

            case 2:

                OrderActivity.start(HomeActivity.this);
                break;

            case 3:

                SubscriptionActivity.start(HomeActivity.this);
                break;

            case 4:

                BillingHistoryActivity.start(HomeActivity.this);
                break;

            case 5:

                this.PROFILE_ACCESS_CODE = 2;
                this.display_password_layout();
                break;

            case 6:

                this.PROFILE_ACCESS_CODE = 3;
                this.display_password_layout();
                break;

            case 7:

                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;

            case 8:

                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
                break;

            case 9:

                BookConsultationActivity.start(HomeActivity.this);
                break;

            case 10:

                this.procureMedicine();
                break;

            case 11:

                this.chat_confirmation();
                break;

            case 12:

                Bundle sendBundle = new Bundle();
                sendBundle.putInt(KEY_DIAGNOSTICS_FRAGMENT_INDEX, KEY_DIAGNOSTICSLISTFRAGMENT_INDEX);

                Intent diagnosticsIntent = new Intent(HomeActivity.this, DiagnosticsActivity.class);
                diagnosticsIntent.putExtras(sendBundle);
                startActivity(diagnosticsIntent);
                break;

            case 13:

                AddonsActivity.start(HomeActivity.this);
                break;

        }
    }

    private void chat_confirmation() {
        if (mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_ONETIME)
                || mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B_PAID)) {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).showSnackbar("You cannot use chat feature in current plan", CustomAlertDialog.LENGTH_SHORT);
            return;
        }

        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_CHAT_CONFIRMATION, this)
                .showDialogWithActionAndIcon(getResources().getString(R.string.dialog_title_chat_confirmation),
                        getResources().getString(R.string.dialog_content_chat_confirmation),
                        getResources().getString(R.string.StartNow),
                        getResources().getString(R.string.NoThanks), true, R.drawable.ic_chat);
    }


    private void procure_medicine() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                showDialog();
            }
        }, 1000);
    }


    private void showDialog() {
        //new requirement
        if (mAppointmentId != medicinesAlertSharedPref.getInt("appointmentid",0)){
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.alert_order_medicines);
            TextView ordernow_tv = dialog.findViewById(R.id.ordernow_tv);
            TextView nothanks_tv = dialog.findViewById(R.id.nothanks_tv);

            TextView appointment_id_alert = dialog.findViewById(R.id.appointment_id_alert);
            TextView appointment_time_alert = dialog.findViewById(R.id.appointment_time_alert);

            try
            {
                appointment_id_alert.setText(""+mAppointmentId);


                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = null;
                try {
                    date = df.parse(mScheduledTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                df.setTimeZone(TimeZone.getDefault());
                String formattedDate = df.format(date);



                appointment_time_alert.setText(""+formattedDate);
            }catch (Exception e){
                Log.e("AAA","appointment id exception at medicines popup : "+e);
            }


            final CheckBox dontshow_cb = dialog.findViewById(R.id.dontshow_cb);

            ordernow_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procureMedicine();
                }
            });

            nothanks_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dontshow_cb.isChecked()){
                        SharedPreferences.Editor editor = medicinesAlertSharedPref.edit();
                        editor.putInt("appointmentid",mAppointmentId);
                        editor.commit();
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();
        }


    }

    private void json_data(String data) {
        try {
            JSONArray array = new JSONArray(data);
            JSONObject json;
            badgeCount = 0;

            for (int index = 0; index < array.length(); index++) {
                json = array.getJSONObject(index);
                String read_at = (json.isNull(Constants.KEY_READ_AT)) ? "" : json.getString(Constants.KEY_READ_AT);

                if (read_at.isEmpty()) {
                    badgeCount++;
                }
            }

            if (menu != null) {
                onCreateOptionsMenu(menu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            mController.getSession().putBadgeCount(badgeCount);
        }
    }


    private void json_data_profile(String data) {
        try {
            User.getUserProfileFromJSON(data);
            JSONObject jsonObject = new JSONObject(data);
            cartBadgeCount = jsonObject.getInt(KEY_DIAGNOSTICS_CARTCOUNT);

            if (menu != null)
                invalidateOptionsMenu();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mController.getSession().putDiagnosticsCartCount(cartBadgeCount);
            new HttpClient(HTTP_REQUEST_CODE_GET_LANGUAGES, MyApplication.HTTPMethod.GET.getValue(),
                    this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getLanguageURL());
            ProfileUpdateBroadcast.send(HomeActivity.this);
        }
    }


    @Override
    public void onPageSelection(int position, String title) {
        switch (position) {
            case 0:

                openActivity(9);
                break;

            case 1:

                openActivity(0);
                break;

            case 2:

                openActivity(10);
                break;

            case 3:

                openActivity(11);
                break;

            case 4:

                openActivity(12);
                break;

            case 5:

                startActivity(new Intent(HomeActivity.this, EHRActivity.class));
                break;

            case 6:

                startActivity(new Intent(HomeActivity.this, MRHome.class));
                break;
            case 7:

                openActivity(13);
                break;
            case 8:
                if (MyApplication.getInstance().getSession().getFitMeInStatus()){
                    if(this.mController.getSession().getEmailStatus() && this.mController.getSession().getMobileStatus())
                    {
                        if (!checkingPermissionIsEnabledOrNot()){
                            requestLocationMultiplePermission();
                        }else {
                            startActivity(new Intent(HomeActivity.this, FitMeInActivity.class));
                        }
                    }else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setTitle("Email is Required");
                        alert.setMessage("Looks like you haven't updated you e-mail address or Mobile no. Please update to continue");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent ii = new Intent(HomeActivity.this, SettingsActivity.class);
                                startActivity(ii);
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alert.show();
                    }
                }else {
                    Toast.makeText(this, "This feature is not Available for you Contact your Hr Dept", Toast.LENGTH_SHORT).show();
                }

                break;
            case 9:
                if (MyApplication.getInstance().getSession().getHRAServices()){
                    callHRA();
                }else {
                    Toast.makeText(this, "This feature is not Available for you Contact your Hr Dept", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void callHRA() {
        Log.e("AAA","DOB : "+ this.mController.getSession().getUserDOB());
        Log.e("AAA","Gender : "+ this.mController.getSession().getUserGender());
        if (!this.mController.getSession().getUserDOB().equals("null") &&
                !this.mController.getSession().getUserGender().equals("null")){
            startActivity(new Intent(HomeActivity.this, HRAMainActivity.class));
        }else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Date of birth and Gender is Required");
            alert.setMessage("Looks like you haven't updated your Date of Birth or Gender. Please update to continue");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent ii = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(ii);
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

    public boolean checkingPermissionIsEnabledOrNot() {
        int GPSFineLocationPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int GPSCoarseLocationPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return GPSFineLocationPermissionResult == PackageManager.PERMISSION_GRANTED &&
                GPSCoarseLocationPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationMultiplePermission() {
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION

                }, PERMISSION_LOCATION_REQUEST_CODE);
    }

    public void procureMedicine() {
        if (this.mAppointmentId != 0) {
            Intent intent = new Intent(HomeActivity.this, AppointmentSummeryActivity.class);
            intent.putExtra("PAGE", 2);
            intent.putExtra("ID", this.mAppointmentId);
            startActivity(intent);
        } else {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view)
                    .showSnackbar("No medicine to order", CustomAlertDialog.LENGTH_SHORT);
        }
    }


    private void set_password_confirmation() {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_SET_PASSWORD, this)
                .showAlertDialogWithPositiveAction(getResources().getString(R.string.dialog_title_account_info),
                        getResources().getString(R.string.dialog_content_set_password),
                        getResources().getString(R.string.SetNow), true);
    }


    private void display_family_limit_alert(String message) {
        new CustomAlertDialog(this, this)
                .showAlertDialogWithoutTitle(message,
                        getResources().getString(R.string.OK), true);
    }

    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission() {
        /**
         * Creating String Array with Permissions.
         */
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]
                {
                        CAMERA,
                        RECORD_AUDIO
                }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

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

            case PERMISSION_LOCATION_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean fineLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (fineLocationPermission && coarseLocationPermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(HomeActivity.this, FitMeInActivity.class));
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
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


    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                badgeCount++;
                mController.getSession().putBadgeCount(badgeCount);

                if (menu != null) {
                    onCreateOptionsMenu(menu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onPositiveAction() {

    }


    public void onPositiveAction(int requestCode, String data) {
        if (requestCode == DIALOG_REQUEST_CODE_CONFIRM_PASSWORD) {
            this.access_profile_info(data);
        }
    }


    @Override
    public void onPositiveAction(int requestCode) {
        if (requestCode == DIALOG_REQUEST_CODE_CHAT_CONFIRMATION) {
            startActivity(new Intent(HomeActivity.this, FirebaseChatActivity.class));
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_PROCURE_MEDICINE) {
            this.procureMedicine();
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_SET_PASSWORD) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("INDEX", 7);
            startActivity(intent);
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_OVERLAY_PERMISSION) {
            this.requestPermission(DIALOG_REQUEST_CODE_OVERLAY_PERMISSION);
        }
    }

    @Override
    public void onNegativeAction() {

    }

    private void appointment_json_data(String json_data) {
        try {
            mController.clearAppointment();
            mController.appointmentList.addAll(Appointment.getAppointmentListFromJSON(json_data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // set timer and notification for upcoming appointment.
            this.displayAppointmentNotificationAndTimer();
            // Display upcoming appointmrnt in the tile.
            this.displayUpcomingAppointmentTile();
        }
    }

    @Override
    public void gotoAppointmentHistory() {
        startActivity(new Intent(HomeActivity.this, DiagnosticsHistoryActivity.class));
    }

    private class AppointmentReminderTimer extends CountDownTimer {
        AppointmentReminderTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            displayAppointmentNotificationAndTimer();
            Log.wtf("onTick", "onTick-");
        }

        @Override
        public void onFinish() {

        }
    }


    private void init_language_list(String json_data) {
        Log.wtf(JSON_TAG, json_data);

        try {
            JSONArray json_array = new JSONArray(json_data);

            this.mController.getSQLiteHelper().remove_all(Constants.TABLE_LANGUAGES);

            List<Integer> preferences = new ArrayList<>();
            JSONArray array = new JSONArray(mController.getSession().getLanguagePreferences());

            for (int i = 0; i < array.length(); i++) {
                preferences.add(array.getInt(i));
            }

            for (int i = 0; i < json_array.length(); i++) {
                JSONObject json = json_array.getJSONObject(i);

                int id = json.getInt(KEY_ID);
                String language = json.getString(KEY_ENGLISH_NAME);
                boolean read_only = json.getBoolean(KEY_READONLY);

                PreferredLanguage pLanguage = new PreferredLanguage(id, language, 0, read_only);

                for (int index = 1; index <= preferences.size(); index++) {
                    if (preferences.get(index - 1) == pLanguage.getID()) {
                        pLanguage.setStatus(1);
                        pLanguage.setPreference(index);
                    }
                }

                if (pLanguage.getReadOnly()) {
                    pLanguage.setStatus(1);
                }

                this.mController.getSQLiteHelper().insert(pLanguage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void display_password_layout()
    {
        this.startActivity();
    }


    private boolean getAlarmStatus(int appointment_id) {
        try {
            Intent notificationIntent = new Intent("android.media.action.APPOINTMENT_REMINDER_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            boolean alarmUp = (PendingIntent.getBroadcast(this, appointment_id, notificationIntent,
                    PendingIntent.FLAG_NO_CREATE)) != null;

            Log.wtf("APPOINTMENT_STATUS", "Alarm is active - " + alarmUp + " - " + appointment_id);
            return alarmUp;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    private void appointment_reminder(int time_remaining, String date, int appointment_id) {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.APPOINTMENT_REMINDER_NOTIFICATION");
            notificationIntent.putExtra("APPOINTMENT_TIME", date);
            notificationIntent.putExtra("APPOINTMENT_ID", appointment_id);
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationIntent.setClass(this, AlarmReceiver.class);
            }

            PendingIntent broadcast = PendingIntent.getBroadcast(this, appointment_id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, time_remaining);

            if (alarmManager == null) {
                return;
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAppointmentNotificationReminder() {
        for (Appointment appointment : mController.appointmentList) {
            String date = Helper.UTC_to_Local_TimeZone(appointment.getScheduledAt());

            if (appointment.getAppointmentStatus() == CALL_STATUS_ACTIVE) {
                if (!getAlarmStatus(appointment.getAppointmentID())) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
                        Date value = sdf.parse(date);
                        sdf = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY_HH_MM_A);

                        int time_remaining = Helper.second_remaining(date);

                        if (time_remaining > 0) {
                            appointment_reminder(time_remaining, sdf.format(value), appointment.getAppointmentID());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void initFragment() {
        tv_toolbar_title.setText(getResources().getString(R.string.text_dashboard));

        Fragment fragment = new HomeFragment(HomeActivity.this, this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {

        if (isFinishing())
        {
            return;
        }

        try
        {
            if (requestCode == HTTP_REQUEST_CODE_GET_APPOINTMENT_HISTORY && responseCode == HttpClient.OK)
            {
                JSONObject json = new JSONObject(response);
                json = json.getJSONObject(KEY_DATA);
                appointment_json_data(json.getString(KEY_DATA));
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_NOTIFICATIONS && responseCode == HttpClient.OK)
            {
                JSONObject json = new JSONObject(response);
                json_data(json.getString(KEY_DATA));
                return;
            }

            // all manipulations done by client.
            if (requestCode == HTTP_REQUEST_CODE_GET_PROFILE_STATE && responseCode == HttpClient.OK) {
                initFragment();
                JSONObject json = new JSONObject(response);
                String data = (json.isNull(KEY_DATA)) ? "" : json.getString(KEY_DATA);

                JSONObject jsonObject = new JSONObject(data);
                int userstate = jsonObject.getJSONObject("user").getInt("new_user");

                Log.e("AAA","user state response : "+jsonObject);

                if (userstate == 0) {
                    Log.d("AAA", "already registered appsflyer not tracking");
                } else if (userstate == 1) {
                    /**
                     * Track registration success event (Google Analytics)
                     */
                    GATracker.trackEvent(GATracker.GA_CATEGORY_REGISTRATION, GATracker.GA_ACTION_REGISTRATION_SUCCESS, GATracker.GA_LABEL_ANDROID, 0);

                    Log.d("AAA", "not registered appsflyer tracking");

                    if (sharedPreferences.getBoolean("status", false)) {
                        //for appsflyer
                        //Map<String, Object> eventValue = new HashMap<>();
                       // eventValue.put(AFInAppEventParameterName.REGSITRATION_METHOD, "" + sharedPreferences.getString("type", ""));
                      //  AppsFlyerLib.getInstance().trackEvent(this, AFInAppEventType.COMPLETE_REGISTRATION, eventValue);

                        //for adgyde
                       /* HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Registrations", sharedPreferences.getString("type", ""));//patrametre name,value
                        PAgent.onEvent("Registrations", params);//eventid
                        PAgent.flush();*/

                        if (!sharedPreferences.getString("mediapartnername", "").equalsIgnoreCase("")){
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(KEY_MEDIA_PARTNER_NAME, sharedPreferences.getString("mediapartnername", ""));
                            map.put("utm_medium", "");
                            map.put("utm_campaign", "");// if required sharedPreferences.getString("mediasource", "")
                            new HttpClient(HTTP_REQUEST_CODE_LEAD_SOURCE, MyApplication.HTTPMethod.POST.getValue(), map, this).execute(mController.getLeadSourceURL());
                        }
                    } else {
                        Log.d("AAA", "sharedpref false");
                    }
                }

                if (jsonObject.has("employer") && jsonObject.get("employer") instanceof JSONObject){
                    Log.e("AAA", "whitelabel data : "+jsonObject.getJSONObject("employer"));
                    JSONObject employerObject = jsonObject.getJSONObject("employer");
                    //String client = employerObject.getString("company_name");
                    boolean isWhiteLabelEnabled = employerObject.getBoolean("white_label_enabled");
                    if (isWhiteLabelEnabled){
                        String currentTheme = appThemeSharedPreferences.getString(Constants.KEY_CURRENT_THEME, Constants.DOCONLINE_THEME);
                        Log.e("AAA","current theme is "+currentTheme);
                        if (!currentTheme.equalsIgnoreCase(TATA_THEME)){
                            appThemeEditor.putString(Constants.KEY_CURRENT_THEME, TATA_THEME);
                            appThemeEditor.commit();
                            Intent intent = new Intent(this, SplashScreenActivity.class);
                            intent.putExtra("tata","tata");
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                }

                this.json_data_profile(data);
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_LEAD_SOURCE && responseCode == HttpClient.OK)
            {
                Log.d("AAA", "Lead Source Response : " + response);
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_LANGUAGES && responseCode == HttpClient.OK)
            {
                JSONObject json = new JSONObject(response);
                String data = (json.isNull(KEY_DATA)) ? "" : json.getString(KEY_DATA);
                this.init_language_list(data);
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_PROCURE_MEDICINE && responseCode == HttpClient.OK)
            {
                JSONObject json = new JSONObject(response);
                json = json.getJSONObject(KEY_DATA);
                this.mAppointmentId = json.getInt(KEY_ID);
                this.mScheduledTime = json.getString(KEY_SCHEDULED_AT);
                //Log.e("AAA", "respose : "+json);

                this.procure_medicine();
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_POST_PROFILE_PASSWORD && responseCode == HttpClient.NO_RESPONSE)
            {
                MyApplication.prefs.edit().putLong(KEY_LAST_AUTHENTICATED_TIME, System.currentTimeMillis()).apply();
                this.startActivity();

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_POST_DEVICE_INFORMATION && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                prefs.edit().putBoolean(KEY_DEVICE_SYNCED, true).apply();
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_PASSWORD_STATUS && responseCode == HttpClient.OK) {
                JSONObject json = new JSONObject(response);
                boolean status = (json.isNull(KEY_DATA)) || json.getBoolean(KEY_DATA);
                mController.getSession().putPasswordStatus(status);

                if(mController.getSession().getPasswordStatus())
                {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            set_password_confirmation();
                        }
                    }, 1500);
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST && responseCode == HttpClient.OK) {

                JSONObject json = new JSONObject(response);
                mController.clearDiagnosticsAddressList();
                mController.setDiagnosticsUserAddressList(DiagnosticsUserAddress.getUserAddressListFromJSON(json.getJSONArray(KEY_DATA)));

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_CART_DATA && responseCode == HttpClient.OK) {

                JSONObject json = new JSONObject(response);
                mController.clearDiagnosticsCart();
                mController.setDiagnosticsCart(DiagnosticsCart.getDiagnosticsCartDetailsFromJSON(json.getJSONObject(KEY_DATA)));
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_GET_SETTINGS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    final SettingsResponse settings = new Gson().fromJson(json.toString(), SettingsResponse.class);

                    if(settings.getHotlineConfig() != null)
                    {
                        HotlineConfig config = settings.getHotlineConfig();

                        this.helpline_number = config.getHotlineNumber();
                        String number_type = config.getNumberType();

                        mController.getSession().putIsHotline(config.getIsHotline());

                        setCustomerCareNumber(this.helpline_number, number_type);
                    }

                    if(settings.getFamilyConfig() != null)
                    {
                        FamilyConfig config = settings.getFamilyConfig();

                        mController.setFamilyMemberMessage(config.getMessage() == null ? "" : config.getMessage());

                        mController.getSession().putFamilyMemberAllowed(config.getFamilyMembersAllowed());
                        mController.getSession().putFamilyMemberConfig(config.getFamilyMembersConfig());

                        if(config.getFamilyMembersConfig() && config.getFamilyMembersCount() > config.getFamilyMembersAllowed())
                        {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    display_family_limit_alert(mController.getFamilyMemberMessage());
                                }
                            }, 1000);
                        }
                    }

                    mController.docCategoryList.clear();

                    if(settings.getDocCategories() != null)
                    {
                        mController.docCategoryList.addAll(settings.getDocCategories());
                    }

                    mController.getSession().putDocumentConsent(settings.getDocumentConsent());
                    mController.getSession().putMaxFileSize(settings.getMaxFileSize());

                    if(settings.getVitalInfoUrl() != null)
                    {
                        mController.getSession().putVitalInfoUrl(settings.getVitalInfoUrl());
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_CONSENT_STATUS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    /**
                     * Child User
                     */
                    if (!json.isNull(KEY_DATA))
                    {
                        json = json.getJSONObject(KEY_DATA);

                        if (json.isNull(KEY_BOOKING_CONSENT))
                        {

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                                    intent.putExtra("PAGE", 2);
                                    intent.putExtra("AUTO_UPDATE_CONSENT", true);
                                    startActivity(intent);
                                }
                            }, 2000);
                        }
                    }

                    else
                    {
                        this.isParentUser = true;
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(this, this, layout_root_view).handle(responseCode, response);
        }

        catch (JSONException e)
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


    private void startActivity()
    {
        switch (PROFILE_ACCESS_CODE)
        {
            case 1:

                AppointmentHistoryActivity.start(HomeActivity.this);
                break;

            case 2:

                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("PAGE", 2);
                intent.putExtra("IS_PARENT_USER", isParentUser);
                startActivity(intent);
                break;

            case 3:

                Intent intent1 = new Intent(HomeActivity.this, ProfileActivity.class);
                intent1.putExtra("IS_PARENT_USER", isParentUser);
                startActivity(intent1);
                break;
        }
    }


    public void call(View view)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + this.helpline_number));
            startActivity(intent);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //If URL contains /dl/appointments then will be redirected to appointments otherwise normally will open the app.
    private void deepLink()
    {
        try
        {
            Intent intent = getIntent();

            if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null)
            {
                Uri uri = intent.getData();

                Log.d("deeplink", "" + uri.getPath());

                if(uri.getPath().contains("/dl/appointments"))
                {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run()
                        {
                            openActivity(0);
                        }
                    }, 2000);

                }

                Log.d("deeplink", "" + uri.toString());
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
