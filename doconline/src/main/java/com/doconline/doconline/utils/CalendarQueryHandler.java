package com.doconline.doconline.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import java.util.TimeZone;

/**
 * Created by admin on 2018-01-24.
 */

public class CalendarQueryHandler extends AsyncQueryHandler
{
    private static final String TAG = CalendarQueryHandler.class.getSimpleName();

    private Context context;
    private long calendarID;

    private long getCalendarID()
    {
        return calendarID;
    }

    private void setCalendarID(long calendarID)
    {
        this.calendarID = calendarID;
    }

    // Projection arrays
    private static final String[] CALENDAR_PROJECTION = new String[]
            {
                    CalendarContract.Calendars._ID
            };

    // The indices for the projection array above.
    private static final int CALENDAR_ID_INDEX = 0;

    private static final int INSERT_EVENT = 1000011;
    private static final int REMINDER = 1000012;

    private CalendarQueryHandler queryHandler;
    private ContentResolver resolver;


    // QueryHandler
    CalendarQueryHandler(ContentResolver _resolver)
    {
        super(_resolver);
    }


    // Insert Event
    void insertEvent(Context _context, long startTime, long endTime, String doctor_name)
    {
        context = _context;
        resolver = _context.getContentResolver();

        String str_appointment_details = "Appointment scheduled with " + doctor_name;

        if (queryHandler == null)
        {
            queryHandler = new CalendarQueryHandler(resolver);
        }

        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.TITLE, "DocOnline Consultation");
        values.put(CalendarContract.Events.DESCRIPTION, str_appointment_details);

        Log.i(TAG, "Calendar Query Start");

        queryHandler.startQuery(CALENDAR_ID_INDEX, values, CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);
    }


    void deleteEvent(Context _context, long _eventID)
    {
        context = _context;
        setCalendarID(_eventID);

        if(resolver == null)
        {
            resolver = context.getContentResolver();
        }

        startDelete(0, null,
                ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,getCalendarID()),
                null, null);
    }


    @Override
    public void onQueryComplete(int token, Object object, Cursor cursor)
    {
        // Use the cursor to move through the returned records
        cursor.moveToFirst();

        // Get the field values
        long _calendarID = cursor.getLong(CALENDAR_ID_INDEX);

        Log.i(TAG, "Calendar Query Complete " + _calendarID);

        ContentValues values = (ContentValues) object;
        values.put(CalendarContract.Events.CALENDAR_ID, _calendarID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,TimeZone.getDefault().getDisplayName());

        startInsert(INSERT_EVENT, null, CalendarContract.Events.CONTENT_URI, values);
    }


    @Override
    public void onInsertComplete(int token, Object object, Uri uri)
    {
        if (uri != null)
        {
            switch (token)
            {
                case INSERT_EVENT:

                    long eventID = Long.parseLong(uri.getLastPathSegment());
                    setCalendarID(eventID);

                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Reminders.MINUTES, 30);
                    values.put(CalendarContract.Reminders.EVENT_ID, eventID);
                    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    startInsert(REMINDER, null, CalendarContract.Reminders.CONTENT_URI, values);

                    if(context != null)
                    {
                        Toast.makeText(context, "Event Added to Calendar", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Calendar Event Added " + uri.getLastPathSegment());

                        resolver = null;
                        context = null;
                    }

                    break;
            }
        }
    }


    @Override
    protected void onDeleteComplete(int token, Object cookie, int result)
    {
        deleteEventFromCalendar(getCalendarID());
    }


    private void deleteEventFromCalendar(long id)
    {
        Uri eventsUri = Uri.parse(String.valueOf(CalendarContract.Events.CONTENT_URI));


        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Uri deleteUri = ContentUris.withAppendedId(eventsUri, id);
            int rows = resolver.delete(deleteUri, null, null);
        }

        else
        {
            Cursor cursor = resolver.query(eventsUri, new String[]{ "_id" }, "calendar_id=" + id, null, null);

            if(cursor == null)
            {
                return;
            }

            while(cursor.moveToNext())
            {
                long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
                resolver.delete(ContentUris.withAppendedId(eventsUri, id), null, null);
            }

            cursor.close();
        }

        /*if(context != null)
        {
            Toast.makeText(context, "Event Removed from Calendar", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Calendar Event Removed");
        }*/
    }
}