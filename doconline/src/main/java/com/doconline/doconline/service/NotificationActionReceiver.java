package com.doconline.doconline.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.doconline.doconline.Blogs;

/**
 * This class is designed to receive broadcast
 * on notification action click
 */
public class NotificationActionReceiver extends BroadcastReceiver
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
        Log.d(TAG, "Clicked - " + notificationId);
        Log.v(TAG, "Data - " + "" + intent.getExtras().getInt("data"));

        String action_time = String.valueOf(System.currentTimeMillis());
        Log.v(TAG, "Action Time - " + action_time);

        if (notificationId == 99486){
            context.startActivity(new Intent(context.getApplicationContext(), Blogs.class));
        }
        /**
         * If action not null
         * Start activity on action button click
         * gotoIntent.setClassName(context, action);
         */
        if(intent.getAction() != null)
        {
            Intent gotoIntent = new Intent(intent.getAction());
            gotoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            gotoIntent.putExtra("ID", intent.getExtras().getInt("data"));
            context.startActivity(gotoIntent);
        }

        /**
         * if you want cancel notification
         */
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}