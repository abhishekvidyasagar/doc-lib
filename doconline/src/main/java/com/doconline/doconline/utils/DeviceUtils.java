package com.doconline.doconline.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.doconline.doconline.app.Constants.KEY_DEVICE_TOKEN;
import static com.doconline.doconline.app.Constants.KEY_EXTRA_DATA;
import static com.doconline.doconline.app.Constants.KEY_TYPE_OF;

/**
 * This class is designed to capture device information
 * Send device information to server
 */
public class DeviceUtils {

    private static final String TAG = "DeviceUtils";

    /**
     * Return push message token stored in shared preferences
     * @return Push Token
     */
    public static String get_push_token()
    {
        return MyApplication.prefs.getString(Constants.TOKEN, "");
    }

    /**
     * Return Operating System Version
     * @return OS Version
     */
    private static String get_os_version()
    {
        return System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")";
    }

    /**
     * Return device manufacturer
     * @return device manufacturer
     */
    private static String get_manufacturer()
    {
        return Build.MANUFACTURER;
    }

    /**
     * Return device model information
     * @return device model and product
     */
    private static String get_model()
    {
        return Build.MODEL + " ("+ Build.PRODUCT + ")";
    }

    /**
     * Return device
     * @return device
     */
    private static String get_device()
    {
        return Build.DEVICE;
    }

    /**
     * Return Operating System SDK Version
     * @return OS SDK Version
     */
    private static int get_os_sdk_version()
    {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Return Operating System release version
     * @return OS release version
     */
    private static String get_os_release_version()
    {
        return Build.VERSION.RELEASE;
    }

    /**
     * Return connected network country
     * @return network country
     * This will return 'US' if your current connected network is United states. This works without
     * SIM card even. Hope this will solve your problem.
     */
    private static String get_network_country()
    {
        TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(TELEPHONY_SERVICE);

        if(tm != null)
        {
            return tm.getNetworkCountryIso().toUpperCase();
        }

        return "N/A";
    }

    /**
     * Compose device information in JSON format
     * @return JSON string
     */
    public static String composeDeviceUtilsJSON()
    {

        Map<String, String> params=new HashMap<>();

        PackageManager manager = MyApplication.getInstance().getPackageManager();

        try
        {
            /**
             * Project Package information
             * Project Version information etc.
             */
            PackageInfo info = manager.getPackageInfo(MyApplication.getInstance().getPackageName(), 0);

            params.put(Constants.VERSION_CODE, String.valueOf(info.versionCode));
            params.put(Constants.VERSION_NAME, info.versionName);
            params.put(Constants.TARGET_SDK_VERSION, String.valueOf(info.applicationInfo.targetSdkVersion));
        }

        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        params.put(Constants.OS_VERSION, get_os_version());
        params.put(Constants.MANUFACTURER, get_manufacturer());
        params.put(Constants.MODEL, get_model());
        params.put(Constants.DEVICE, get_device());
        params.put(Constants.OS_SDK_VERSION, String.valueOf(get_os_sdk_version()));
        params.put(Constants.OS_RELEASE_VERSION, get_os_release_version());
        params.put(Constants.NETWORK_COUNTRY, get_network_country());

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(params);
    }


    public static HashMap<String, Object> composeDeviceUtils()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_EXTRA_DATA, DeviceUtils.composeDeviceUtilsJSON());
        //hashMap.put(KEY_FCM_TOKEN, DeviceUtils.get_push_token());
        hashMap.put(KEY_DEVICE_TOKEN, DeviceUtils.get_push_token());
        hashMap.put(KEY_TYPE_OF, "1");

        return hashMap;
    }


    /**public static String device_info()
    {

        * String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        * int sdkVersion = android.os.Build.VERSION.SDK_INT;

        * String os_version = System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")";
        * String manufacturer = Build.MANUFACTURER;
        * String model = "Model (and Product): " + Build.MODEL + " ("+ Build.PRODUCT + ")";
        * String device = Build.DEVICE;
        * int version = Build.VERSION.SDK_INT;
        * String versionRelease = Build.VERSION.RELEASE;

        * String menu_info = "VersionOS :" + os_version
                + " \n Manufacturer : " + manufacturer
                + " \n Model : " + model
                + " \n Device : " + device
                + " \n OS API Level : " + version
                + " \n versionRelease : " + versionRelease;

        * Log.v(TAG, menu_info);


        * PackageInfo pInfo = null;

        try
        {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }

        catch (PackageManager.NameNotFoundException e)

        {
            e.printStackTrace();
        }

        * String version2 = pInfo.versionName;
        * display.setText(String.valueOf("Version: " + version2));

        * return menu_info;
    }*/
}