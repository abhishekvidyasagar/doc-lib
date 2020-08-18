/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.doconline.doconline.medicalremainders;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.database.DBAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.doconline.doconline.database.DBAdapter.KEY_TABLET;


public class AlarmReceiver extends WakefulBroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int mReceivedRecordID = intent.getIntExtra(MRAddRemainder.EXTRA_REMINDER_ID,0);
        try {
            DBAdapter dbAdapter = new DBAdapter(context);
            JsonObject jsonObject = dbAdapter.getRemaindersDataOnRecordId(mReceivedRecordID+"");
            // Get notification title from Reminder Database
            // Create intent to open ReminderEditActivity on notification click
            Intent editIntent = new Intent(context, RemainderDetails.class);
            SimpleDateFormat defaultFormatter = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
            Gson gson = new Gson();
            String jsonString = gson.toJson(jsonObject);
            editIntent.putExtra(Constants.REMAINDER_DATA, jsonString);
            editIntent.putExtra(Constants.USER_SELECTED_DATE, defaultFormatter.format(new Date()));
            editIntent.putExtra(MRAddRemainder.EXTRA_REMINDER_ID, Integer.toString(mReceivedRecordID));
            PendingIntent mClick = PendingIntent.getActivity(context, mReceivedRecordID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Create Notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    /*.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notification))*/

                    .setSmallIcon(R.mipmap.ic_notification)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_alarm, "View", mClick)
                    .setContentTitle(""+jsonObject.get(KEY_TABLET).getAsString())
                    .setTicker("Medicine Reminder")
                    .setContentText(context.getResources().getString(R.string.remider_Title_medi))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(mClick)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true);

            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify(1, mBuilder.build());
        }catch(Exception e){

        }
    }

    public void setAlarms(Context context, Calendar calendar, int timerId, int recordId) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(MRAddRemainder.EXTRA_REMINDER_ID, recordId);
        mPendingIntent = PendingIntent.getBroadcast(context, timerId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Start alarm using notification time
        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                mPendingIntent);


    }

    public void setWeekdayAlarm(Context context, Calendar calendar, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(MRAddRemainder.EXTRA_REMINDER_ID, ID);
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Calculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + diffTime, AlarmManager.INTERVAL_DAY * 7, mPendingIntent);


    }

    public void setDailyAlarm(Context context, Calendar calendar, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(MRAddRemainder.EXTRA_REMINDER_ID, ID);
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Start alarm using notification time
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mPendingIntent);

    }

    public void setRepeatAlarm(Context context, Calendar calendar, int ID, long RepeatTime) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(MRAddRemainder.EXTRA_REMINDER_ID, ID);
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification timein
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Start alarm using initial notification time and repeat interval time
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                RepeatTime , mPendingIntent);


    }

    public void cancelAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);


    }

    public void setDailyAlarmCheck(Context context,boolean itemAdded) {

        PendingIntent pi = PendingIntent.getBroadcast(context, 999, new Intent(context, DailyWakeUpReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if(itemAdded){
            calendar.setTimeInMillis(System.currentTimeMillis());
        }else{
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 *60 *24, pi);
        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, DailyWakeUpReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}