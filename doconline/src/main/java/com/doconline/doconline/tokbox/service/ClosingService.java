package com.doconline.doconline.tokbox.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ClosingService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);

        Log.i("onTaskRemoved", "onTaskRemoved");

        try
        {
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if(nMgr != null)
            {
                nMgr.cancel(1000);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        stopSelf();
    }
}