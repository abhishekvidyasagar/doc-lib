package com.doconline.doconline.service;

import android.content.Context;
import android.content.Intent;

import com.doconline.doconline.model.Appointment;

public final class TileUpdateBroadcast
{

    /**
     * Tag used on log messages.
     */
    static final String TAG = "DocOnline Application";

    public static final String ACTION =
            "com.doconline.TILE";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     */
    public static void send(Context context, Appointment appointment)
    {
        Intent intent = new Intent(ACTION);
        intent.putExtra("APPOINTMENT", appointment);
        context.sendBroadcast(intent);
    }
}