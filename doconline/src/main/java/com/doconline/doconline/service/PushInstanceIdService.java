package com.doconline.doconline.service;

import android.util.Log;

import com.doconline.doconline.app.Constants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.doconline.doconline.app.MyApplication.prefs;

/**
 * This class is designed to receive device registration id
 */
public class PushInstanceIdService extends FirebaseInstanceIdService { //  replaced FirebaseInstanceIdService with FirebaseMessagingService

    private static final String TAG = "PushInstanceIdService";

    /**
     * Call this method when device receive registration id
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
/*   @Override
    public void onTokenRefresh() {

        *//**//**//**//**//**//**//**
         * Get hold of the registration token
         *//**//**//**//**//**//**//*
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        *//**//**//**//**//**//**//**
         * Log the token
         *//**//**//**//**//**//**//*
        Log.d(TAG, "Refreshed token: " + refreshedToken);

       *//**//**//**//**//**//**//**
         * Call this method to send token to server
         *//**//**//**//**//**//**//*
        sendRegistrationToServer(refreshedToken);
    }*/

    /*@Override
    public void onNewToken(String s) {
        super.onNewToken(s);
       //  Get hold of the registration token

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

      // Log the token

                Log.d(TAG, "Refreshed token: " + refreshedToken);


        //Call this method to send token to server

        sendRegistrationToServer(refreshedToken);
    }*/
    /**
     * Call this method to synchronize device information
     * @param token Pass generated token
     */
    private void sendRegistrationToServer(String token)
    {
        /**
         * Implement this method if you want to store the token on your server
         * Storing the token id on shared preferences called push_token
         * Synchronize device information
         */
        prefs.edit().putString(Constants.TOKEN, token).apply();
        prefs.edit().putBoolean(Constants.KEY_DEVICE_SYNCED, false).apply();
    }
}