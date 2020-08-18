package com.doconline.doconline.tokbox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.doconline.doconline.NotificationIntentHandlerActivity;

/**
 * This class is designed to receive broadcast
 * on notification action click
 */
public class IncomingCallReceiver extends BroadcastReceiver
{
    private static String TAG = "VOIP_Service";

    /**
     * Call this method when broadcast received
     * @param context Pass application context
     * @param intent Pass Intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if(action.equals("cb.doconline.VOIP"))
        {
            try
            {
                NotificationIntentHandlerActivity
                        .startForCall(context, intent.getStringExtra("json_data"), intent.getBooleanExtra("is_outgoing_call", false));
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}