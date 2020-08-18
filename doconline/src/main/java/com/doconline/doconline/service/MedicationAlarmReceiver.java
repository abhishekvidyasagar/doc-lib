package com.doconline.doconline.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.appointment.AppointmentHistoryActivity;
import com.doconline.doconline.disease.MedicationsActivity;
import com.doconline.doconline.helper.Helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chiranjitbardhan on 01/08/17.
 */

public class MedicationAlarmReceiver extends BroadcastReceiver
{
    private Context context;

    /*@Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "Time Up... Now Vibrating !!!", Toast.LENGTH_LONG).show();
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
    }*/


    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;

        Log.wtf("ALARM_TIME", "Alarm Received - " + intent.getStringExtra("FROM_DATE"));

        int notificationId = 280192;

        Intent notificationIntent = new Intent(context, MedicationsActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AppointmentHistoryActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        /**
         * View History Intent
         */
        Intent viewIntent = new Intent(context, NotificationActionReceiver.class);
        viewIntent.setAction("OPEN_ACTIVITY_MEDICATION");
        viewIntent.putExtra("data", 0);
        viewIntent.putExtra("push.notificationId", notificationId);
        PendingIntent pendingIntentView = PendingIntent.getBroadcast(context, notificationId, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * Cancel intent
         */
        Intent closeIntent = new Intent(context, NotificationCancelReceiver.class);
        closeIntent.putExtra("push.notificationId", notificationId);
        PendingIntent pendingIntentOk = PendingIntent.getBroadcast(context, notificationId, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification;

        builder.setContentTitle("Medication Reminder")
                .setContentText("You have to take medicine now")
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
                .bigText("Hello, You have to take medicine on " + intent.getStringExtra("MEDICATION_TIME"))
                .setBigContentTitle("Medication Reminder")
                .setSummaryText("You have to take medicine now"))
                .setVibrate(new long[] { 0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500 });

        try
        {
            //Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ringtone);
            final Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            builder.setSound(uri);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        builder.setContentIntent(pendingIntent).build();

        notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
            Date value = sdf.parse(intent.getStringExtra("TILL_DATE") + " 00:00:00");
            Log.wtf("DATE_DIFF", "" + Helper.compare_date(sdf.format(value)));

            if(Helper.compare_date(sdf.format(value)) <= 0)
            {
                cancel_alarm(intent.getIntExtra("ALARM_ID", 0));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void cancel_alarm(int req_code)
    {
        Log.wtf("ALARM_STATUS", "OFF");

        Intent intent = new Intent("android.media.action.MEDICATION_REMINDER_NOTIFICATION");
        //PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), req_code, intent, 0);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), req_code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getService(context, req_code, intent, 0));
    }
}