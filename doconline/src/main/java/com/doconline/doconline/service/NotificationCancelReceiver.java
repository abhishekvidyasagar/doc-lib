package com.doconline.doconline.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class is designed to receive broadcast
 * on notification action delete
 */
public class NotificationCancelReceiver extends BroadcastReceiver
{

    private static String TAG = "NotificationService";

    /**
     * Call this method when broadcast received
     * @param context Pass application context
     * @param intent Pass Intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int notificationId = intent.getExtras().getInt("push.notificationId");
        Log.d(TAG, "Deleted - " + notificationId);

        String action_time = String.valueOf(System.currentTimeMillis());
        Log.v(TAG, "Action Time - " + action_time);

        /**
         * if you want cancel notification
         */
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
