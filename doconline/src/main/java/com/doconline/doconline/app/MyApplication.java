package com.doconline.doconline.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.request.target.ViewTarget;
import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.R;
import com.doconline.doconline.database.DBAdapter;
import com.doconline.doconline.medicalremainders.AlarmReceiver;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.AppointmentSummery;
import com.doconline.doconline.model.DiagnosticsCart;
import com.doconline.doconline.model.DiagnosticsCartItem;
import com.doconline.doconline.model.DiagnosticsHistoryItem;
import com.doconline.doconline.model.DiagnosticsItem;
import com.doconline.doconline.model.DiagnosticsUserAddress;
import com.doconline.doconline.model.FamilyMember;
import com.doconline.doconline.model.TimeSlot;
import com.doconline.doconline.session.SessionManager;
import com.doconline.doconline.sqlite.SQLiteHelper;
import com.doconline.doconline.utils.DocumentUtils;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.gotev.uploadservice.UploadService;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import static com.doconline.doconline.app.Constants.CALL_STATUS_CANCELLED;

/**
 * Created by chiranjit on 06/12/16.
 */
public class MyApplication extends Application
{
    public static final String TAG = MyApplication.class.getSimpleName();

    public static MyApplication mInstance;
    public static SharedPreferences prefs;

    public List<TimeSlot> timeSlotList = new ArrayList<>();
    public List<TimeSlot> calendarList = new ArrayList<>();
    public List<Appointment> appointmentList = new ArrayList<>();
    public List<FamilyMember> memberList = new ArrayList<>();
    public List<DocumentUtils> docCategoryList = new ArrayList<>();
    public List<Appointment> prescriptionList = new ArrayList<>();


    public ArrayList<DiagnosticsUserAddress> diagnosticsUserAddressList = new ArrayList<>();
    public ArrayList<DiagnosticsItem> list_DiagnosticsItems = new ArrayList<>();
    public ArrayList<DiagnosticsHistoryItem> list_DiagnosticsHistoryItems = new ArrayList<>();
    public ArrayList<DiagnosticsHistoryItem> list_DiagnosticsUpcomingHistoryItems = new ArrayList<>();
    public ArrayList<DiagnosticsHistoryItem> list_DiagnosticsPreviousHistoryItems = new ArrayList<>();
    public DiagnosticsCart diagnosticsCart = new DiagnosticsCart();

    public AppointmentSummery bookingSummery = new AppointmentSummery();

    private SessionManager mSession;
    private SQLiteHelper mSQLiteHelper;
    public static String parent_job_id;
    public static String sSession = "";

    private String family_member_message = "";

    private String AFSenderID = "706829133527";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DBAdapter db;

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    //AppsFlyer Variables
    public static String InstallConversionData = "";
    public static int sessionCount = 0;

    private static final String PROPERTY_ID = "UA-109032025-1";



    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     * <p>
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName
    {
        APP_TRACKER // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    public MyApplication()
    {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId)
    {
        if (!mTrackers.containsKey(trackerId))
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : null;
            mTrackers.put(trackerId, t);
        }

        return mTrackers.get(trackerId);
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance = MyApplication.this;
        mSession = new SessionManager(this);
        mSQLiteHelper = new SQLiteHelper(this);

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);

        /* Set to true to see the debug logs. Comment out or set to false to stop the function */


        AppsFlyerLib.getInstance().setDebugLog(true);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        MyApplication.prefs = getSharedPreferences(Constants.PREF, MODE_PRIVATE);

        /**
         * Facebook SDK Initialization
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getFacebookClientID());


        sharedPreferences = getSharedPreferences("SOCIALNWLOGIN", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.doconline.doconline",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.wtf("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/

        fetch_firebase_remote_config();
        ViewTarget.setTagId(R.id.glide_tag);

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;


        //Database copy and initialisation
        try {
            db = new DBAdapter(getApplicationContext());
            db.open();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("KAR", "IOException"
                    + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@222");
        }


        try {

            AppsFlyerLib.getInstance().enableUninstallTracking(AFSenderID); /* ADD THIS LINE HERE */

            AppsFlyerLib.getInstance().startTracking(MyApplication.getInstance(), Constants.AF_DEV_KEY);

            if (BuildConfig.DEBUG) {
                String appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(this);
                Log.d("DEVICE ID", "Device ID : " + appsFlyerId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


       // adGydeInitiallisations();

        try{
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.setDailyAlarmCheck(getApplicationContext(),false);
        }catch (Exception e){

        }
    }

  /*  private void adGydeInitiallisations() {
        //if (!BuildConfig.DEBUG) {
            PAgent.init(this, "H730562992944470", "Organic");//Default channel is Organic
            PAgent.flush();
            PAgent.setDebugEnabled(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMEdiaSource();
            }
        }, 10*1000);

        //}
    }*/
   /* private void getMEdiaSource(){

        String media_source = PAgent.getMediaSource();
        Log.d("AAA", "conversionlisteners adgyde : " + media_source);
        if(!TextUtils.isEmpty(media_source)){
            Log.d("AAA", "conversionlisteners adgyde TEST if: " + media_source);

            editor.putString("mediapartnername", media_source);
            editor.putString("mediasource", "AdGyde");

            editor.commit();
        }else {
            Log.d("AAA", "conversionlisteners adgyde TEST else : " + media_source);
        }
    }*/

    public static void setInstallData(Map<String, String> conversionData) {
        if (sessionCount == 0) {
            final String install_type = "Install Type: " + conversionData.get("af_status") + "\n";
            final String media_source = "Media Source: " + conversionData.get("media_source") + "\n";
            final String install_time = "Install Time(GMT): " + conversionData.get("install_time") + "\n";
            final String click_time = "Click Time(GMT): " + conversionData.get("click_time") + "\n";
            final String is_first_launch = "Is First Launch: " + conversionData.get("is_first_launch") + "\n";
            InstallConversionData += install_type + media_source + install_time + click_time + is_first_launch;
            sessionCount++;
        }
    }

    //Initialize Apps Flyer conversion listener
    public void InitializeAppsFlyerCallback() {
        /**  Set Up Conversion Listener to get attribution data **/


        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            /* Returns the attribution data. Note - the same conversion data is returned every time per install */
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                Log.d("AAA","conversionlisteners appsflyer : "+conversionData);

                Log.d("AAA","conversionlisteners appsflyer : "+conversionData.get("media_source").equalsIgnoreCase(""));
                if (conversionData.keySet().contains("media_source")) {

                    if (!conversionData.get("media_source").equalsIgnoreCase("")){

                        Log.d("AAA","conversionlisteners appsflyer : 2"+conversionData.get("media_source").equalsIgnoreCase(""));


                        editor.putString("mediapartnername", conversionData.get("media_source"));
                        editor.putString("mediasource", "Appsflyer");

                        editor.commit();
                    }

                }

                for (String attrName : conversionData.keySet()) {
                    Log.d("AAA", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }

                MyApplication.setInstallData(conversionData);
            }

            @Override
            public void onInstallConversionFailure(String errorMessage) {
                if (BuildConfig.DEBUG) {
                    Log.d("LOG", "error getting conversion data: " + errorMessage);
                }
            }

            /* Called only when a Deep Link is opened */
            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {

                for (String attrName : conversionData.keySet()) {
                    if (BuildConfig.DEBUG) {
                        Log.d("LOG", "attribute: " + attrName + " = " + conversionData.get(attrName));
                    }
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                if (BuildConfig.DEBUG) {
                    Log.d("LOG", "error onAttributionFailure : " + errorMessage);
                }
            }
        };

        AppsFlyerLib.getInstance().registerConversionListener(getApplicationContext(), conversionListener);

        /* This API enables AppsFlyer to detect installations, sessions, and updates. */
        AppsFlyerLib.getInstance().init(Constants.AF_DEV_KEY, conversionListener, getApplicationContext());
    }

    //End of Initializing Apps Flyer conversion listener

    /**
     * Call attachBaseContext method
     *
     * @param base application context
     *             Install multidex
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Call when configuration changed
     *
     * @param newConfig Pass configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Call when device memory low
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * Call on application terminate
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Return application instance
     *
     * @return instance
     */
    public static /*synchronized*/ MyApplication getInstance() {
        return mInstance;
    }


    /**
     * Return session instance
     *
     * @return instance
     */
    public /*synchronized*/ SessionManager getSession() {
        return this.mSession;
    }


    /**
     * Return SQLite instance
     *
     * @return instance
     */
    public /*synchronized*/ SQLiteHelper getSQLiteHelper() {
        return this.mSQLiteHelper;
    }


    public void setFamilyMemberMessage(String family_member_message)
    {
        this.family_member_message = family_member_message;
    }

    public String getFamilyMemberMessage()
    {
        return this.family_member_message;
    }


    /*Diagnostics User address */
    public DiagnosticsUserAddress getUserAddress(int pPosition) {

        return diagnosticsUserAddressList.get(pPosition);
    }

    public void addNewUserAddress(DiagnosticsUserAddress address) {

        diagnosticsUserAddressList.add(address);
    }

    public void setDiagnosticsUserAddressList(ArrayList<DiagnosticsUserAddress> mList) {

        diagnosticsUserAddressList.addAll(mList);
    }

    public int getDiagnosticsUserAddressCount() {

        return diagnosticsUserAddressList.size();
    }

    public DiagnosticsUserAddress getUserDefaultAddress() {
        if (diagnosticsUserAddressList.size() == 0)
            return null;

        for (DiagnosticsUserAddress addressItem : diagnosticsUserAddressList) {

            if (addressItem.isDefaultAddress()) {
                return addressItem;
            }
        }
        return getUserAddress(0);
    }

    public void removeDiagnosticsUserAddress(int index) {

        diagnosticsUserAddressList.remove(index);
    }

    public void clearDiagnosticsAddressList() {
        diagnosticsUserAddressList.clear();
    }

    /*Diagnostics Cart */
    public DiagnosticsCart getDiagnosticsCart() {
        return diagnosticsCart;
    }


    public void setDiagnosticsCart(DiagnosticsCart _cart) {
        _cart.getiCartItemQty();
        diagnosticsCart = _cart;
    }

    public ArrayList<DiagnosticsCartItem> getCartItemsList() {
        return diagnosticsCart.getCartItemsList();
    }

    public int getDiagnosticsCartItemListCount() {
        return diagnosticsCart.getCartItemsCount();
    }

    public DiagnosticsCartItem getCartItemAt(int position) {
        return diagnosticsCart.getCartItemsList().get(position);
    }

    public void clearDiagnosticsCart() {
        diagnosticsCart.clearCartItems();
    }

    public void addNewItemToCart(DiagnosticsCartItem cartItem) {

        diagnosticsCart.addNewCartItem(cartItem);
    }

    public void deleteCartItems(int packageID) {
        diagnosticsCart.deleteCartItem(packageID);
    }

    public void UpdateCart(int count, float subTotal) {
        diagnosticsCart.UpdateCartInformation(count, subTotal);
    }

    /*Diagnostics Tests and Packages*/
    public DiagnosticsItem getPackageDetails(int pPosition) {

        return list_DiagnosticsItems.get(pPosition);
    }

    public void setDiagnosticPackageAddedToCart(int position, boolean isAdded) {
        list_DiagnosticsItems.get(position).setAddedToCart(isAdded);
    }

    public DiagnosticsItem getIndexOfItemFromDiagnosticsItemList(int packageID) {

        int index = 0;
        for (DiagnosticsItem item : list_DiagnosticsItems) {
            if (packageID == item.getPackageID())
                break;
            else
                index++;
        }

        return list_DiagnosticsItems.get(index);
    }

//    public DiagnosticsItem getSelectedDiagnosticsItem() {
//        return selectedDiagnosticsItem;
//    }
//
//    public void setUserSelectedDiagnosticsPackage(int packagePosition){
//        selectedDiagnosticsItem = list_DiagnosticsItems.get(packagePosition);
//    }
//
//    public void addNewPackageToList(DiagnosticsItem item) {
//
//        list_DiagnosticsItems.add(item);
//    }

    public void setDiagnosticsPackageList(ArrayList<DiagnosticsItem> mPackageList) {

        list_DiagnosticsItems.addAll(mPackageList);
    }

    public void setDiagnosticsHistoryList(ArrayList<DiagnosticsHistoryItem> mHistoryList) {

        list_DiagnosticsHistoryItems.addAll(mHistoryList);
    }

    public DiagnosticsHistoryItem getHistoryDetails(int pPosition) {

        return list_DiagnosticsHistoryItems.get(pPosition);
    }

    public void setDiagnosticsUpcomingHistoryDetails() {

        for (int i = 0; i < getDiagnosticsHistoryCount(); i++) {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:s").format(new Date());
            DiagnosticsHistoryItem item = list_DiagnosticsHistoryItems.get(i);
            if (DiagnosticsHistoryItem.compareGreaterThanEqualDates(item.getAppointmentDate(), date)) {
                list_DiagnosticsUpcomingHistoryItems.add(item);
            }
        }
    }

    public void setDiagnosticsPreviousHistoryDetails() {

        for (int i = 0; i < getDiagnosticsHistoryCount(); i++) {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:s").format(new Date());
            DiagnosticsHistoryItem item = list_DiagnosticsHistoryItems.get(i);
            if (DiagnosticsHistoryItem.compareLessThanDates(item.getAppointmentDate(), date)) {
                list_DiagnosticsPreviousHistoryItems.add(item);
            }
        }
    }

    public DiagnosticsHistoryItem getDiagnosticsUpcomingHistoryDetails(int pPosition) {

        return list_DiagnosticsUpcomingHistoryItems.get(pPosition);
    }

    public DiagnosticsHistoryItem getDiagnosticsPreviousHistoryDetails(int pPosition) {

        return list_DiagnosticsPreviousHistoryItems.get(pPosition);
    }

    public int getDiagnosticsUpcomingHistoryCount() {

        return list_DiagnosticsUpcomingHistoryItems.size();
    }

    public int getDiagnosticsPreviousHistoryCount() {

        return list_DiagnosticsPreviousHistoryItems.size();
    }

    public int getDiagnosticsHistoryCount() {

        return list_DiagnosticsHistoryItems.size();
    }

    public int getDiagnosticsPackagesCount() {

        return list_DiagnosticsItems.size();
    }


    public void clearDiagnosticsPackageList() {
        list_DiagnosticsItems.clear();
    }

    public void clearDiagnosticsHistoryList() {
        list_DiagnosticsHistoryItems.clear();
    }

    public void clearDiagnosticsUpcomingHistoryList() {
        list_DiagnosticsUpcomingHistoryItems.clear();
    }

    public void clearDiagnosticsPreviousHistoryList() {
        list_DiagnosticsPreviousHistoryItems.clear();
    }


    /**
     * TimeSlot
     */
    public void setTimeSlot(TimeSlot timeSlot) {

        timeSlotList.add(timeSlot);
    }

    public int getTimeSlotListSize() {

        return timeSlotList.size();
    }

    public void clearTimeSlot() {

        timeSlotList.clear();
    }


    /**
     * Calendar
     */
    public TimeSlot getCalendar(int pPosition) {

        return calendarList.get(pPosition);
    }

    public void setCalendar(TimeSlot timeSlot) {

        calendarList.add(timeSlot);
    }

    public int getCalendarSize() {

        return calendarList.size();
    }

    public void clearCalendar() {

        calendarList.clear();
    }


    /**
     * Family
     */
    public FamilyMember getFamilyMember(int pPosition) {

        return memberList.get(pPosition);
    }

    public void setFamilyMember(FamilyMember member) {

        memberList.add(member);
    }

    public void setFamilyMemberList(List<FamilyMember> mList) {

        memberList.addAll(mList);
    }

    public int getFamilyMemberCount() {

        return memberList.size();
    }

    public void clearFamilyMember() {

        memberList.clear();
    }

    public void clearFamilyMember(int index) {

        memberList.remove(index);
    }

    /**
     * Set Appointment
     */
    public void setAppointment(Appointment appointment) {

        appointmentList.add(appointment);
    }

    /**
     * Clear Appointment
     */
    public void clearAppointment() {

        appointmentList.clear();
    }

    /**
     * Cancel Appointment
     */
    public void cancelAppointment(int appointment_id) {

        for (int i = 0; i < appointmentList.size(); i++) {
            if (appointmentList.get(i).getAppointmentID() == appointment_id) {
                appointmentList.get(i).setAppointmentStatus(CALL_STATUS_CANCELLED);
                break;
            }
        }
    }


    public AppointmentSummery getAppointmentBookingSummery() {

        return bookingSummery;
    }


    public enum HTTPMethod {
        GET(0), POST(1), PUT(2), DELETE(3), BDELETE(6), PATCH(7);

        private final int value;

        HTTPMethod(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public void fetch_firebase_remote_config() {
        /**
         * Get the instance of firebase remote config
         */
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        /**
         * Define the firebase remote config settings
         * BuildConfig.DEBUG For test purpose
         */
        /*remoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build()
        );*/

        /**
         * Set the defaults value for firebase remote config
         */
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put(Constants.KEY_RINGING_DURATION, Constants.DEFAULT_RINGING_DURATION);
        defaults.put(Constants.KEY_APPOINTMENT_CALLBACK_TIME_LIMIT, Constants.DEFAULT_APPOINTMENT_CALLBACK_TIME_LIMIT);
        defaults.put(Constants.KEY_FILE_ATTACHMENT_LIMIT, Constants.DEFAULT_FILE_ATTACHMENT_LIMIT);
        defaults.put(Constants.KEY_CLIENT_ID, Constants.DEFAULT_CLIENT_ID);
        defaults.put(Constants.KEY_CLIENT_SECRET, Constants.DEFAULT_CLIENT_SECRET);
        defaults.put(Constants.KEY_BASE_URL, Constants.DEFAULT_BASE_URL);
        defaults.put(Constants.KEY_OPENTOK_API_KEY, Constants.DEFAULT_OPENTOK_KEY);
        defaults.put(Constants.KEY_FB_CLIENT_ID, Constants.DEFAULT_FB_CLIENT_ID);
        defaults.put(Constants.KEY_ENV_PREFIX, Constants.DEFAULT_ENV_PREFIX);
        defaults.put(Constants.KEY_RAZORPAY_API_KEY, Constants.DEFAULT_RAZORPAY_KEY);
        defaults.put(Constants.KEY_PLAY_STORE_VERSION_CODE, 0);
        defaults.put(Constants.KEY_MAX_FILE_SIZE, Constants.DEFAULT_FILE_SIZE);
        defaults.put(Constants.KEY_CUSTOMER_CARE_NUMBER, getResources().getString(R.string.customer_care_number));

        remoteConfig.setDefaults(defaults);

        /**
         * Fetch the firebase remote configuration
         */
        final Task<Void> fetch = remoteConfig.fetch(0);

        /**
         * Fetch listener
         */
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                /**
                 * Do something when task success
                 */
                remoteConfig.activateFetched();

                /**
                 * Get the values from firebase remote configuration
                 */
                long ringing_duration = remoteConfig.getLong(Constants.KEY_RINGING_DURATION);
                long attachment_limit = remoteConfig.getLong(Constants.KEY_FILE_ATTACHMENT_LIMIT);
                long callback_time_limit = remoteConfig.getLong(Constants.KEY_APPOINTMENT_CALLBACK_TIME_LIMIT);
                String client_id = remoteConfig.getString(Constants.KEY_CLIENT_ID);
                String client_secret = remoteConfig.getString(Constants.KEY_CLIENT_SECRET);
                String base_url = remoteConfig.getString(Constants.KEY_BASE_URL);
                String opentok_api_key = remoteConfig.getString(Constants.KEY_OPENTOK_API_KEY);
                String fb_client_id = remoteConfig.getString(Constants.KEY_FB_CLIENT_ID);
                String env_prefix = remoteConfig.getString(Constants.KEY_ENV_PREFIX);
                String razorpay_api_key = remoteConfig.getString(Constants.KEY_RAZORPAY_API_KEY);
                long play_store_version_code = remoteConfig.getLong(Constants.KEY_PLAY_STORE_VERSION_CODE);
                String customer_care_number = remoteConfig.getString(Constants.KEY_CUSTOMER_CARE_NUMBER);
                long max_file_size = remoteConfig.getLong(Constants.KEY_MAX_FILE_SIZE);

                //if(BuildConfig.DEBUG) {
                    mSession.putBaseURL("https://demo.doconline.com/");
                    mSession.putOAuthClientSecret("test");
                    mSession.putOAuthClientID(String.valueOf(3));
                    mSession.putEnvPrefix("staging");
                    mSession.putRazorpayApiKey("rzp_test_pGc2kHLLywFzbe");

                    /*mSession.putBaseURL("https://33a1df3b.ngrok.io/");
                    mSession.putOAuthClientSecret("test");
                    mSession.putOAuthClientID(String.valueOf(2));
                    mSession.putEnvPrefix("staging");
                    mSession.putRazorpayApiKey("rzp_test_pGc2kHLLywFzbe");*/
                //}

                /*else
                {
                    mSession.putBaseURL(base_url);
                    mSession.putOAuthClientSecret(client_secret);
                    mSession.putOAuthClientID(String.valueOf(client_id));
                    mSession.putEnvPrefix(env_prefix);
                    mSession.putRazorpayApiKey(String.valueOf(razorpay_api_key));
                }*/

                mSession.putRingingDuration(ringing_duration);
                mSession.putAppointmentCallbackTimeLimit(callback_time_limit);
                mSession.putAttachmentLimit(attachment_limit);
                mSession.putOpentokApiKey(String.valueOf(opentok_api_key));
                mSession.putFacebookClientID(String.valueOf(fb_client_id));
                mSession.putPlayStoreVersionCode(play_store_version_code);
                mSession.putCustomerCareNumber(customer_care_number);

                if(BuildConfig.DEBUG)
                {
                    Log.i("REMOTE_CONFIG", String.valueOf(ringing_duration));
                    Log.i("REMOTE_CONFIG", String.valueOf(callback_time_limit));
                    Log.i("REMOTE_CONFIG", String.valueOf(attachment_limit));
                    Log.i("REMOTE_CONFIG", String.valueOf(max_file_size));
                    Log.i("REMOTE_CONFIG", client_secret);
                    Log.i("REMOTE_CONFIG", String.valueOf(client_id));
                    Log.i("REMOTE_CONFIG", base_url);
                    Log.i("REMOTE_CONFIG", opentok_api_key);
                    Log.i("REMOTE_CONFIG", fb_client_id);
                    Log.i("REMOTE_CONFIG", env_prefix);
                    Log.i("REMOTE_CONFIG", razorpay_api_key);
                    Log.i("REMOTE_CONFIG", String.valueOf(play_store_version_code));
                    Log.i("REMOTE_CONFIG", customer_care_number);
                }
            }
        });
    }


    public String getOAuthClientSecret() {
        return mSession.getOAuthClientSecret();
    }

    public String getOAuthClientID() {
        return mSession.getOAuthClientID();
    }

    public String getOpentokApiKey() {
        return mSession.getOpentokApiKey();
    }

    public String getFacebookClientID() {
        return mSession.getFacebookClientID();
    }

    /**
     * Ringing Duration
     */
    public long getRingingDuration() {
        return mSession.getRingingDuration();
    }

    /**
     * Env Prefix
     */
    public String getEnvPrefix() {
        return mSession.getEnvPrefix();
    }


    /**
     * Get Country List
     */
    public String getCountryList() {
        return mSession.getCountryList();
    }

    /**
     * Get Name Prefix List
     */
    public String getNamePrefixList() {
        return mSession.getNamePrefixList();
    }

    /**
     * Attachment Limit
     */
    public long getAttachmentLimit() {
        return mSession.getAttachmentLimit();
    }

    /**
     * SignOut URL
     */
    public String getSignOutURL() {
        return mSession.getBaseURL() + "api/user/register";
    }

    /**
     * Appointment URL
     */
    public String getAppointmentURL() {
        return mSession.getBaseURL() + "api/user/appointments/";
    }

    /**
     * Guest Appointment URL
     */
    public String getGuestAppointmentURL() {
        return mSession.getBaseURL() + "api/unreg/book-an-appointment/";
    }

    public String getBookingConfirmationURL(String parent_job_id) {
        return mSession.getBaseURL()+"api/user/appointments/"+parent_job_id+"/jobstatus";
    }

    /**
     * Payment Success URL
     */
    public String getPaymentSuccessURL() {
        return mSession.getBaseURL() + "api/unreg/payment-success/";
    }

    /**
     * Attachments URL
     */
    public String getAttachmentsURL() {
        return mSession.getBaseURL() + "api/unreg/attachments/";
    }

    /**
     * Guest Coupon Discount Code URL
     */
    public String getGuestCouponCodeURL() {
        return mSession.getBaseURL() + "api/unreg/discount-code/";
    }

    /**
     * Validate Appointment URL
     */
    public String getValidateAppointmentURL() {
        return mSession.getBaseURL() + "api/validate/booking/";
    }

    /**
     * Validate Appointment URL
     */
    public String getValidateGuestUserURL() {
        return mSession.getBaseURL() + "api/validate/unregistered/";
    }

    /**
     * Call State URL
     */
    public String getCallStateURL() {
        return mSession.getBaseURL() + "api/user/consultation/session/";
    }

    /**
     * Chat URL
     */
    public String getChatURL() {
        return mSession.getBaseURL() + "api/user/thread/";
    }

    /**
     * OAuth URL
     */
    public String getOAuthURL() {
        return mSession.getBaseURL() + "oauth/token/";
    }


    /**
     * Device URL
     */
    public String getDeviceURL() {
        return mSession.getBaseURL() + "api/user/devices/";
    }

    /**
     * Logout URL
     */
    public String getLogoutURL() {
        return mSession.getBaseURL() + "api/user/logout/";
    }


    /**
     * Promo Code URL
     */
    public String getPromoCodeURL() {
        return mSession.getBaseURL() + "api/user/promo-code/";
    }

    /**
     * Coupon Code Code URL
     */
    public String getCouponCodeURL() {
        return mSession.getBaseURL() + "api/user/discount-code/";
    }

    /**
     * Forgot password URL
     */
    public String getForgotPasswordURL() {
        return mSession.getBaseURL() + "api/user/password/email/";
    }

    /**
     * Health Profile URL
     */
    public String getHealthProfileURL() {
        return mSession.getBaseURL() + "api/user/health-profile/";
    }

    /**
     * Subscription Plan URL
     */
    public String getSubscriptionPlanURL() {
        return mSession.getBaseURL() + "api/plans/";
    }

    /**
     * Subscription Plan URL
     */
    public String getSubscriptionURL() {
        return mSession.getBaseURL() + "api/user/subscription/";
    }

    /**
     * Payment URL
     */
    public String getPaymentURL() {
        return mSession.getBaseURL() + "api/user/payment/subscription/";
    }


    /**
     * Billing URL
     */
    public String getBillingURL() {
        return mSession.getBaseURL() + "api/user/billings/";
    }

    /**
     * Notification URL
     */
    public String getNotificationURL() {
        return mSession.getBaseURL() + "api/user/notifications/";
    }

    /**
     * Prescription URL
     */
    public String getPrescriptionURL() {
        return mSession.getBaseURL() + "api/user/prescription/";
    }

    /**
     * EHR Prescription URL
     */
    public String getEhrPrescriptionURL() {
        return mSession.getBaseURL() + "api/user/ehr/prescription";
    }

    /**
     * Family Member URL
     */
    public String getFamilyMemberURL() {
        return mSession.getBaseURL() + "api/user/family-members/";
    }

    /**
     * Family URL
     */
    public String getFamilyURL() {
        return mSession.getBaseURL() + "api/user/family/";
    }

    /**
     * Access Password URL
     */
    public String getAccessPasswordURL() {
        return mSession.getBaseURL() + "api/user/check-access/password/";
    }

    /**
     * Profile URL
     */
    public String getProfileURL() {
        return mSession.getBaseURL() + "api/user/account/";
    }

    /**
     * Coupon Code URL
     */
    public String getProfileStateURL() {
        return mSession.getBaseURL() + "api/user/state/";
    }

    /**
     * Mobile Verification URL
     */
    public String getMobileVerificationURL() {
        return mSession.getBaseURL() + "api/user/settings/mobile-no/";
    }


    /**
     * Email Verification URL
     */
    public String getEmailVerificationURL() {
        return mSession.getBaseURL() + "api/user/settings/email/";
    }

    /**
     * Profile Settings URL
     */
    public String getSettingsURL() {
        return mSession.getBaseURL() + "api/user/settings/";
    }

    /**
     * Reset Password URL
     */
    public String getResetPasswordURL() {
        return mSession.getBaseURL() + "api/user/password/reset/";
    }

    /**
     * CountryList URL
     */
    public String getCountryURL() {
        return mSession.getBaseURL() + "api/countries";
    }

    /**
     * NamePrefixList URL
     */
    public String getNamePrefixURL() {
        return mSession.getBaseURL() + "api/name-prefixes";
    }

    /**
     * NamePrefixList URL
     */
    public String getMedicineAvailabilityURL() {
        return mSession.getBaseURL() + "api/user/procure/prescription/";
    }

    public String getMedicineOrderStatusURL() {
        return mSession.getBaseURL() + "api/user/procure/prescription/hasorder/";
    }

    public String getPlaceOrderURL() {
        return mSession.getBaseURL() + "api/user/order/prescription/";
    }

    public String getOrderListURL() {
        return mSession.getBaseURL() + "api/user/orders/";
    }

    public String getLanguageURL() {
        return mSession.getBaseURL() + "api/languages/";
    }

    public String getSessionURL() {
        return mSession.getBaseURL() + "api/unreg/session/";
    }

    public String getConsentURL() {
        return mSession.getBaseURL() + "api/user/consent/status/";
    }

    public String getLeadSourceURL() {
        return mSession.getBaseURL() + "api/user/lead-source-update/";
    }

    public String getActivationURL() {
        return mSession.getBaseURL() + "api/user/activation/notification/";
    }

    public String getCheckSumURL() {
        return mSession.getBaseURL() + "api/user/payment/subscription/generateChecksum";
    }


    public String getFileURL() {
        return mSession.getBaseURL() + "api/user/ehr/file";
    }

    public String getEhrDocumentConsentURL() {
        return mSession.getBaseURL() + "api/user/ehr/consent";
    }

    public String getVitalURL() {
        return mSession.getBaseURL() + "api/user/ehr/vitals";
    }

    public String getVitalTemplateURL() {
        return mSession.getBaseURL() + "api/ehr/templates/vitals/";
    }


    /**
     * Thyrocare APIs
     **/
    //1 - GET
    public String getDiagnosticsPlansURL() {
        return mSession.getBaseURL() + "api/user/diagnostics/packages/get-list/";
    }

    //2 - GET
    public String getDiagnosticsHistoryURL() {
        return mSession.getBaseURL() + "api/user/diagnostics/appointment/history/";
    }

    //3 - POST
    public String getDiagnosticsCreateAppointmentURL() {
        return mSession.getBaseURL() + "api/user/diagnostics/appointment/create/";
    }

    //4 - POST
    public String getDiagnosticsConfirmAppointmentURL() {
        return mSession.getBaseURL() + "api/user/diagnostics/appointment/booking-confirm/";
    }

    //5 - GET
    public String getDiagnosticsSearchURL() {
        return mSession.getBaseURL() + "api/user/diagnostics/packages/search/";
    }

    //6 - GET
    public String getDiagnosticsCartItemsURL() {
        return mSession.getBaseURL() + "api/user/cart/get-collection/";
    }

    //7 - POST
    public String getDiagnosticsAddToCartURL() {
        return mSession.getBaseURL() + "api/user/cart/add-to-cart/";
    }

    //8 - DELETE
    public String getDiagnosticsRemoveFromCartURL() {
        return mSession.getBaseURL() + "api/user/cart/remove-from-cart/";
    }

    //9 - POST
    public String getDiagnosticsAddUserAddressURL() {
        return mSession.getBaseURL() + "api/user/address/add/";
    }

    //10 - GET
    public String getDiagnosticsUserAddressListURL() {
        return mSession.getBaseURL() + "api/user/address/get-list/";
    }

    //11 - PATCH
    public String getDiagnosticsDefaultUserAddressURL() {
        return mSession.getBaseURL() + "api/user/address/make-default/";
    }

    //12 - DELETE
    public String getDiagnosticsDeleteUserAddressURL() {
        return mSession.getBaseURL() + "api/user/address/delete/";
    }

    //13 - PATCH
    public String getDiagnosticsUpdateAddressURL() {
        return mSession.getBaseURL() + "api/user/address/update/";
    }

    //13 - GET
    public String getDiagnosticsPinCodeVerification() {
        return mSession.getBaseURL() + "api/user/diagnostics/service-availability/";
    }

    public String getDiagnosticsAvailableSlots() {
        return mSession.getBaseURL() + "api/user/diagnostics/appointment/time-slots/";
    }

    public String getDiagnosticsReportsStatusURL() {
        return mSession.getBaseURL() + "api/user/diagnostics/appointment/summery/";
    }

    public String getMedicinesListUrl() {
        return mSession.getBaseURL() + "api/user/medicine/search/?search=";
    }

    //POST
    public String getStoreDrugAllergyURL(){
        return mSession.getBaseURL() + "api/user/health-profile/drug-allergy/";
    }

    //Delete
    public String getDeleteDrugAllergyURL() {
        return mSession.getBaseURL() + "api/user/health-profile/drug-allergy/";
    }

    //PATCH
    public String getDrugAlleryEditURL() {
        return mSession.getBaseURL() + "api/user/health-profile/drug-allergy/";
    }

    //POST
    public String getWorkoutlistUrl() {
        return mSession.getBaseURL() + "api/user/workout/list/";
    }

    //POST
    public String getStudiolistUrl() {
        return mSession.getBaseURL() + "api/user/workout/outlets/list/";
    }

    //GET
    public String getLocationSearchUrl() {
        return mSession.getBaseURL() + "api/user/workout/location-search/";
    }

    //GET
    public String getWorkoutDetailsUrl() {
        return mSession.getBaseURL() + "api/user/workout/details/";
    }

    //POST
    public String getBookingUrl() {
        return mSession.getBaseURL() + "api/user/workout/register";
    }

    //GET
    public String getFitMeInHistoryURL() {
        return mSession.getBaseURL() + "api/user/workout/history";
    }

    //POST
    public String getBookingCancellationUrl() {
        return mSession.getBaseURL() + "api/user/workout/cancel";
    }


    public String getFitMeInWorkoutImageURL() {
        return mSession.getBaseURL() + "api/user/workout/image";
    }

    //GET
    public String getWorkoutListForStudio() {
        return mSession.getBaseURL() + "api/user/workout/outlet/classes/";
    }

    //POST
    public String getWorkoutAttendance(){
        return mSession.getBaseURL() + "api/user/workout/mark-attendance";
    }

    public String getHRASaveURL() {
        return mSession.getBaseURL() + "api/user/hra/save-results";
    }

    public String getFieldValidationsURL() {
        return mSession.getBaseURL() + "api/user/hra/input-limits";
    }
    public boolean isServiceRunning() {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            if (manager != null) {
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if ("com.doconline.doconline.floating.TimerService".equals(service.service.getClassName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
