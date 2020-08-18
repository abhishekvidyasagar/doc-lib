package com.doconline.doconline.tokbox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.doconline.doconline.app.Constants.KEY_CALL_ACTION;

/**
 * This class is designed to receive broadcast
 * on notification action click
 */
public class IncomingCallActionReceiver extends BroadcastReceiver
{
    /**
     * Call this method when broadcast received
     * @param context Pass application context
     * @param intent Pass Intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        /**
         * If action not null
         * Start activity on action button click
         * gotoIntent.setClassName(context, action);
         */
        if(intent.getAction() != null)
        {
            Intent intent1 = new Intent(KEY_CALL_ACTION);
            intent1.putExtra(KEY_CALL_ACTION, intent.getAction());
            context.sendBroadcast(intent1);
        }
    }
}