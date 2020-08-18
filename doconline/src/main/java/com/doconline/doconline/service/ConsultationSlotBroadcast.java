package com.doconline.doconline.service;

import android.content.Context;
import android.content.Intent;

public final class ConsultationSlotBroadcast
{

    /**
     * Tag used on log messages.
     */
    static final String TAG = "DocOnline Application";

    public static final String SLOT_ACTION =
            "cb.doconline.SLOT";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     */
    public static void slotBroadCast(Context context)
    {
        Intent intent = new Intent(SLOT_ACTION);
        context.sendBroadcast(intent);
    }
}