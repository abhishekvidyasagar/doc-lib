package com.doconline.doconline.medicalremainders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.database.DBAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.doconline.doconline.database.DBAdapter.KEY_DOSAGE;
import static com.doconline.doconline.database.DBAdapter.KEY_ENDDATE;
import static com.doconline.doconline.database.DBAdapter.KEY_FRIDAY;
import static com.doconline.doconline.database.DBAdapter.KEY_ID;
import static com.doconline.doconline.database.DBAdapter.KEY_IMAGEPATH;
import static com.doconline.doconline.database.DBAdapter.KEY_INSERTDATETIME;
import static com.doconline.doconline.database.DBAdapter.KEY_MONDAY;
import static com.doconline.doconline.database.DBAdapter.KEY_REMINDER_ID;
import static com.doconline.doconline.database.DBAdapter.KEY_SATURDAY;
import static com.doconline.doconline.database.DBAdapter.KEY_STARTDATE;
import static com.doconline.doconline.database.DBAdapter.KEY_SUNDAY;
import static com.doconline.doconline.database.DBAdapter.KEY_TABLET;
import static com.doconline.doconline.database.DBAdapter.KEY_THURSDAY;
import static com.doconline.doconline.database.DBAdapter.KEY_TIME;
import static com.doconline.doconline.database.DBAdapter.KEY_TUESDAY;
import static com.doconline.doconline.database.DBAdapter.KEY_WEDNESDAY;

public class DailyWakeUpReceiver extends BroadcastReceiver {

    DBAdapter db;
    private Calendar mCalendar;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

            JsonArray jsa = new JsonArray();

            Date todayDateDefault = new Date();
            DateFormat dayFormat = new SimpleDateFormat("EEEE");
            DateFormat dateFormat = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
            String todayDate = dateFormat.format(todayDateDefault);
            String todayDay = dayFormat.format(todayDateDefault);

            try {
                db = new DBAdapter(context);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("AAA", "" + e);
            }

            jsa = db.getRemaindersData(todayDay, todayDate);

            if (jsa != null && jsa.size() > 0) {

                for (int i = 0; i<jsa.size(); i++) {
                    JsonObject jso = jsa.get(i).getAsJsonObject();

                    int id = jso.get(KEY_ID).getAsInt();
                    String medicineName = jso.get(KEY_TABLET).getAsString();
                    String dosage = jso.get(KEY_DOSAGE).getAsString();
                    String time = jso.get(KEY_TIME).getAsString();
                    String sunday = jso.get(KEY_SUNDAY).getAsString();
                    String monday = jso.get(KEY_MONDAY).getAsString();
                    String tuesday = jso.get(KEY_TUESDAY).getAsString();
                    String wednesday = jso.get(KEY_WEDNESDAY).getAsString();
                    String thursday = jso.get(KEY_THURSDAY).getAsString();
                    String friday = jso.get(KEY_FRIDAY).getAsString();
                    String saturday = jso.get(KEY_SATURDAY).getAsString();
                    String startdate = jso.get(KEY_STARTDATE).getAsString();
                    String enddate = jso.get(KEY_ENDDATE).getAsString();
                    String createddate = jso.get(KEY_INSERTDATETIME).getAsString();
                    String imagePath = jso.get(KEY_IMAGEPATH).getAsString();
                    String timerId = jso.get(KEY_REMINDER_ID).getAsString();


                        String[] hoursandminutes = time.split(":");
                        String hour = hoursandminutes[0];
                        String minutes = hoursandminutes[1];
                        String[] minutesandampm = minutes.split(" ");
                        String minute = minutesandampm[0];
                        String amPm = minutesandampm[1];
                        int ampm;
                        if (amPm.equalsIgnoreCase("PM")) {
                            ampm = 1;
                        } else {
                            ampm = 0;
                        }

                        mCalendar = Calendar.getInstance();
                        mCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                        mCalendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                        mCalendar.set(Calendar.AM_PM, ampm);
                        mCalendar.set(Calendar.SECOND, 0);

                        new AlarmReceiver().setAlarms(context, mCalendar, Integer.parseInt(timerId),id);


                }
            }
        }


}
