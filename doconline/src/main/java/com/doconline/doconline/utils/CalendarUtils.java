package com.doconline.doconline.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;

import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;

/**
 * Created by chiranjitbardhan on 23/02/18.
 */

public class CalendarUtils
{
    private Context context;
    private Appointment appointment;
    private SimpleDateFormat sdf;
    private CalendarQueryHandler asyncQueryHandler;

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;
    private static final int PROJECTION_ORGANIZER_INDEX = 3;


    public CalendarUtils(Context context, Appointment appointment)
    {
        this.context = context;
        this.appointment = appointment;
        this.sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
        this.asyncQueryHandler = new CalendarQueryHandler(context.getContentResolver());
    }


    public boolean isEventAlreadyExists()
    {
        boolean status = false;
        List<String> events = getCalendarEvents();

        for(int i = 0; i<events.size(); i++)
        {
            String [] _events = events.get(i).split("__");
            String date = _events[0];

            if(date.equals(appointment.getScheduledAt()))
            {
                status = true;
            }
        }

        return status;
    }


    public void addCalendarEvent()
    {
        if (isEventAlreadyExists())
        {
            Toast.makeText(context, "Event Already Added to Calendar!", Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            String date = Helper.UTC_to_Local_TimeZone(appointment.getScheduledAt());
            Date startTime = sdf.parse(date);
            long startMillis = startTime.getTime();

            long endMillis = startMillis + 15 * 60 * 1000;

            if(asyncQueryHandler != null)
            {
                asyncQueryHandler.insertEvent(context, startMillis, endMillis, Helper.toCamelCase(appointment.getDoctor().getFullName()));
                Toast.makeText(context, "Event Added to Calendar!", Toast.LENGTH_SHORT).show();
            }

            else
            {
                Toast.makeText(context,"Failed to Add Calendar Event!", Toast.LENGTH_SHORT).show();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private int deleteIfEventExists()
    {
        int eventIDToDelete = -1;
        List<String> events = getCalendarEvents();

        for(int i = 0; i<events.size(); i++)
        {
            String [] _events = events.get(i).split("__");
            String date = _events[0];

            if(date.equals(appointment.getScheduledAt()))
            {
                eventIDToDelete = Integer.parseInt(_events[1]);
            }
        }

        return eventIDToDelete;
    }


    public void deleteCalendarEvent()
    {
        long event_id = deleteIfEventExists();

        if (event_id != -1)
        {
            if(asyncQueryHandler != null)
            {
                asyncQueryHandler.deleteEvent(context, event_id);
            }
        }

        else
        {
            Toast.makeText(context, "Calendar Event Not Exists!", Toast.LENGTH_SHORT).show();
        }
    }


    private List<String> getCalendarEvents()
    {
        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.ORGANIZER
        };

        Calendar beginTime = Calendar.getInstance();
        beginTime.add(Calendar.DATE, -1);

        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(beginTime.getTime());
        endTime.add(Calendar.DATE,10);

        long endMillis = endTime.getTimeInMillis();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        Cursor cur = context.getContentResolver().query(builder.build(), INSTANCE_PROJECTION, null, null, null);

        ArrayList<String> events = new ArrayList<>();

        if(cur == null)
        {
            return events;
        }

        while (cur.moveToNext())
        {
            long eventID = cur.getLong(PROJECTION_ID_INDEX);
            long beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
            String title = cur.getString(PROJECTION_TITLE_INDEX);
            String organizer = cur.getString(PROJECTION_ORGANIZER_INDEX);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(beginVal);

            if(title.contentEquals("DocOnline Consultation"))
            {
                events.add(Helper.Local_to_UTC_TimeZone(sdf.format(calendar.getTime())) + "__" + eventID);
            }
        }

        cur.close();
        return events;
    }
}