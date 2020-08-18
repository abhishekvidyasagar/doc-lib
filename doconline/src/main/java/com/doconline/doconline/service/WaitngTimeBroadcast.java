package com.doconline.doconline.service;

import android.content.Context;
import android.content.Intent;

public final class WaitngTimeBroadcast
{

    /**
     * Tag used on log messages.
     */
    static final String TAG = "DocOnline Application";

    public static final String WAITING_TIME_ACTION =
            "cb.doconline.WAITING";

    public static final String WAITING_MINUTES = "minutes";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     */
    public static void waitingTimeBroadCast(Context context, int minutes)
    {
        Intent intent = new Intent(WAITING_TIME_ACTION);
        intent.putExtra(WAITING_MINUTES, minutes);
        context.sendBroadcast(intent);
    }
}