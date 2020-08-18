package com.doconline.doconline.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.floating.TimerService;
import com.doconline.doconline.floating.Utils;
import com.doconline.doconline.session.SessionManager;

import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_REMAINING_TIME;
import static com.doconline.doconline.app.MyApplication.prefs;

/**
 * Created by chiranjit on 29/05/17.
 */
public class AlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
         int notificationId = 12345;

         if(!new SessionManager(context).isLoggedIn())
         {
             return;
         }

        /**
         * View History Intent
         */
         Intent viewIntent = new Intent(context, NotificationActionReceiver.class);
         viewIntent.setAction("OPEN_ACTIVITY_SUMMERY");
         viewIntent.putExtra("data", intent.getIntExtra("APPOINTMENT_ID", 0));
         viewIntent.putExtra("push.notificationId", notificationId);
         PendingIntent pendingIntentView = PendingIntent.getBroadcast(context, notificationId, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * Cancel intent
         */
         Intent closeIntent = new Intent(context, NotificationCancelReceiver.class);
         closeIntent.putExtra("push.notificationId", notificationId);
         PendingIntent pendingIntentOk = PendingIntent.getBroadcast(context, notificationId, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "channel_appointment_reminder";
        String channelName = "Appointment Reminder";

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            if(mNotificationManager != null)
            {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

         NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

         Notification notification;

         builder.setContentTitle("Appointment Reminder")
                .setContentText("You have an Appointment with DocOnline")
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(true)
                .addAction(R.drawable.ic_close_grey, "Dismiss", pendingIntentOk)
                .addAction(R.drawable.ic_alarm, "View", pendingIntentView);

         /**
          * Set notification big text style
          */
         builder.setStyle(new NotificationCompat.BigTextStyle()
                 .bigText("You have an appointment with DocOnline on " + intent.getStringExtra("APPOINTMENT_TIME") +  ". Make sure you are logged in and internet connection is working properly. Thank You")
                 .setBigContentTitle("Appointment Reminder")
                 .setSummaryText("You have an Appointment"))
                 .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000});

                   try
                   {
                        //Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ringtone);
                        final Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        builder.setSound(uri);
                   }

                   catch (Exception e)
                   {
                        e.printStackTrace();
                   }

                   builder.setContentIntent(pendingIntentView).build();

         notification = builder.build();

         if(mNotificationManager != null)
         {
             mNotificationManager.notify(notificationId, notification);
         }

         try
         {
             if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O)
             {
                 if(!MyApplication.getInstance().isServiceRunning())
                 {
                     if (Utils.canDrawOverlays(MyApplication.getInstance()))
                     {
                         prefs.edit().putInt(KEY_REMAINING_TIME, 300).apply();
                         prefs.edit().putInt(KEY_APPOINTMENT_ID, intent.getIntExtra("APPOINTMENT_ID", 0)).apply();
                         context.startService(new Intent(MyApplication.getInstance(), TimerService.class));
                     }
                 }
             }
         }

         catch (Exception e)
         {
             e.printStackTrace();
         }
    }
}