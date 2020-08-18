package com.doconline.doconline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.doconline.doconline.app.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class DBAdapter {

    private Context context;
    private static SQLiteDatabase database;
    private static DataBase dbHelper;


    //below are table column names and same are used for jsonobject
    // formation and utilisation so they are staic variables same keys
    // used acros ic_medical remainder module
    //remainder table column names
    public static final String KEY_ID = "id";
    public static final String KEY_TABLET = "tablet_name";
    public static final String KEY_TIME = "timeofday";
    public static final String KEY_DOSAGE = "dosage";
    public static final String KEY_SUNDAY = "Sunday";
    public static final String KEY_MONDAY = "Monday";
    public static final String KEY_TUESDAY = "Tuesday";
    public static final String KEY_WEDNESDAY = "Wednesday";
    public static final String KEY_THURSDAY = "Thursday";
    public static final String KEY_FRIDAY = "Friday";
    public static final String KEY_SATURDAY = "Saturday";
    public static final String KEY_STARTDATE = "startdate";
    public static final String KEY_ENDDATE = "enddate";
    public static final String KEY_INSERTDATETIME = "inserteddatetime";
    public static final String KEY_IMAGEPATH = "imagepath";
    public static final String KEY_REMINDER_ID = "timerId";

    //skip take tanle columns
    public static final String KEY_TS_ID = "id";
    public static final String KEY_MEDICINE_REMAINDER_ID = "medicineremainderid";
    public static final String KEY_TS_STATUS = "status";
    public static final String KEY_DATETIME = "datetime";

    public static final String KEY_SKIP_TAKE_OBJECT = "SKIPTAKEOBJECT";


    public DBAdapter(Context ctx) throws SQLException, IOException {
        System.out.println("control in DBAdapter");
        this.context = ctx;
    }

    public DBAdapter open() throws SQLException, IOException {

        System.out.println("control in open");

        try {
            Log.i("KAR", "DBAdapter OPEN  ");
            dbHelper = new DataBase(context);
            database = dbHelper.getWritableDatabase();
            Log.i("KAR", "DBAdapter OPEN  getWritableDatabase");

            try {
                Log.i("KAR", "DBAdapter OPEN creating ");
                dbHelper.createDataBase();
                Log.i("KAR", "DBAdapter OPEN created");
                dbHelper.openDataBase();
                Log.i("KAR", "DBAdapter OPEN opened");
            } catch (IOException e) {
                Log.i("KAR", "DBAdapter OPEN IOException");
                e.printStackTrace();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return this;
    }

    public void close() {
        System.out.println("control in close");
        dbHelper.close();
    }

    public int getCheckCount() {
        int id;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Cursor query = database.rawQuery("SELECT COUNT (*) FROM t_userdetails", null);
        query.moveToFirst();
        id = query.getInt(0);
        Log.e("sese", "" + id);
        return id;
    }

    public void truncateTable(String tablename) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM " + tablename;
        database.execSQL(query);
        Log.e("DBADAPTER", "truncate successfuly");
    }

    public int insertNewReamainder(String medicinename, String dosage, String timeofday, String sun, String mon, String tues,
                                   String wed, String thurs, String fri, String sat,
                                   String startdate, String enddate, String inserteddatetime, String imagepath, String timerId) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_TABLET, medicinename);
        values.put(KEY_DOSAGE, dosage);
        values.put(KEY_TIME, timeofday);
        values.put(KEY_SUNDAY, sun);
        values.put(KEY_MONDAY, mon);
        values.put(KEY_TUESDAY, tues);
        values.put(KEY_WEDNESDAY, wed);
        values.put(KEY_THURSDAY, thurs);
        values.put(KEY_FRIDAY, fri);
        values.put(KEY_SATURDAY, sat);
        values.put(KEY_STARTDATE, startdate);
        values.put(KEY_ENDDATE, enddate);
        values.put(KEY_INSERTDATETIME, inserteddatetime);
        values.put(KEY_IMAGEPATH, imagepath);
        values.put(KEY_REMINDER_ID, timerId);
        long ID = database.insert("m_medicinesremainder", null, values);
        database.close();
        Log.e("DBA", "data inserted successfully");
        return (int) ID;
    }


    public JsonArray getRemaindersData(String day, String selecteddate) {
        JsonArray jsa = new JsonArray();
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "SELECT * FROM m_medicinesremainder WHERE " + day + "= 'yes'";
        Cursor query_data = database.rawQuery(query, null);
        while (query_data.moveToNext()) {
            String startDate = query_data.getString(11);
            String endDate = query_data.getString(12);
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
            try {
                if (endDate.equalsIgnoreCase("")) {
                    if (sdf.parse(selecteddate).after(sdf.parse(startDate)) || sdf.parse(selecteddate).equals(sdf.parse(startDate))) {
                        JsonObject jso = new JsonObject();
                        jso.addProperty(KEY_ID, query_data.getString(0));
                        jso.addProperty(KEY_TABLET, query_data.getString(1));
                        jso.addProperty(KEY_DOSAGE, query_data.getString(2));
                        jso.addProperty(KEY_TIME, query_data.getString(3));
                        jso.addProperty(KEY_SUNDAY, query_data.getString(4));
                        jso.addProperty(KEY_MONDAY, query_data.getString(5));
                        jso.addProperty(KEY_TUESDAY, query_data.getString(6));
                        jso.addProperty(KEY_WEDNESDAY, query_data.getString(7));
                        jso.addProperty(KEY_THURSDAY, query_data.getString(8));
                        jso.addProperty(KEY_FRIDAY, query_data.getString(9));
                        jso.addProperty(KEY_SATURDAY, query_data.getString(10));
                        jso.addProperty(KEY_STARTDATE, query_data.getString(11));
                        jso.addProperty(KEY_ENDDATE, query_data.getString(12));
                        jso.addProperty(KEY_INSERTDATETIME, query_data.getString(13));
                        jso.addProperty(KEY_IMAGEPATH, query_data.getString(14));
                        jso.addProperty(KEY_REMINDER_ID, query_data.getString(15));
                        jsa.add(jso);
                    }
                } else {
                    if ((sdf.parse(selecteddate).after(sdf.parse(startDate)) || sdf.parse(selecteddate).equals(sdf.parse(startDate))) &&
                            (sdf.parse(selecteddate).before(sdf.parse(endDate)) || sdf.parse(selecteddate).equals(sdf.parse(endDate)))) {
                        JsonObject jso = new JsonObject();
                        jso.addProperty(KEY_ID, query_data.getString(0));
                        jso.addProperty(KEY_TABLET, query_data.getString(1));
                        jso.addProperty(KEY_DOSAGE, query_data.getString(2));
                        jso.addProperty(KEY_TIME, query_data.getString(3));
                        jso.addProperty(KEY_SUNDAY, query_data.getString(4));
                        jso.addProperty(KEY_MONDAY, query_data.getString(5));
                        jso.addProperty(KEY_TUESDAY, query_data.getString(6));
                        jso.addProperty(KEY_WEDNESDAY, query_data.getString(7));
                        jso.addProperty(KEY_THURSDAY, query_data.getString(8));
                        jso.addProperty(KEY_FRIDAY, query_data.getString(9));
                        jso.addProperty(KEY_SATURDAY, query_data.getString(10));
                        jso.addProperty(KEY_STARTDATE, query_data.getString(11));
                        jso.addProperty(KEY_ENDDATE, query_data.getString(12));
                        jso.addProperty(KEY_INSERTDATETIME, query_data.getString(13));
                        jso.addProperty(KEY_IMAGEPATH, query_data.getString(14));
                        jso.addProperty(KEY_REMINDER_ID, query_data.getString(15));
                        jsa.add(jso);
                    }
                }
            } catch (Exception e) {
                Log.e("AAA", "Exception in DBA" + e);
            }
        }

        JsonArray withSkipTakeObject = getSkipTakeData(selecteddate, jsa);

        return withSkipTakeObject;
    }

    private JsonArray getSkipTakeData(String selecteddate, JsonArray jsa) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i=0; i<jsa.size(); i++){
            JsonObject jso = jsa.get(i).getAsJsonObject();
            String query = "SELECT * FROM t_takeskip WHERE " + KEY_MEDICINE_REMAINDER_ID + " = '"+ jso.get(KEY_ID).getAsString()+"' AND " + KEY_DATETIME + " = '" + selecteddate + "'" ;
            Cursor query_data = database.rawQuery(query, null);
            while (query_data.moveToNext()) {
                JsonObject takeskipobject = new JsonObject();
                takeskipobject.addProperty(KEY_TS_ID, query_data.getString(0));
                takeskipobject.addProperty(KEY_MEDICINE_REMAINDER_ID, query_data.getString(1));
                takeskipobject.addProperty(KEY_TS_STATUS, query_data.getString(2));
                takeskipobject.addProperty(KEY_DATETIME, query_data.getString(3));
                jso.add(KEY_SKIP_TAKE_OBJECT,takeskipobject);
            }
        }
        return jsa;
    }

    public boolean deleteRemainder(String tabletName, String dosage) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return database.delete("m_medicinesremainder", (KEY_TABLET + "='" + tabletName +"' AND "+ KEY_DOSAGE+ "='" + dosage +"'"), null) > 0;
    }

    public void insertSkipTake(String medicineremainderid, String status, String todayDate) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "INSERT INTO t_takeskip (medicineremainderid, status, datetime) " +
                "VALUES ('" + medicineremainderid + "','" + status + "','" + todayDate + "')";
        database.execSQL(query);
        Log.e("DBA", "data inserted successfully");

    }

    public void updateRemainder(String medicine, String dosage, String time, String sunday, String monday,
                                String tuesday, String wednesday, String thursday, String friday, String saturday,
                                String startdate, String enddate, String updateddate, String imagename, String recordid) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_TABLET, medicine);
        values.put(KEY_DOSAGE, dosage);
        values.put(KEY_TIME, time);
        values.put(KEY_SUNDAY, sunday);
        values.put(KEY_MONDAY, monday);
        values.put(KEY_TUESDAY, tuesday);
        values.put(KEY_WEDNESDAY, wednesday);
        values.put(KEY_THURSDAY, thursday);
        values.put(KEY_FRIDAY, friday);
        values.put(KEY_SATURDAY, saturday);
        values.put(KEY_STARTDATE, startdate);
        values.put(KEY_ENDDATE, enddate);
        values.put(KEY_IMAGEPATH, imagename);


        long ID = database.update("m_medicinesremainder", values, KEY_ID + "=" + recordid, null);

        database.close();

        Log.e("IN ADAPTER", "Update Successfully");
    }

    public JsonObject getRemaindersDataOnRecordId(String recordId) {

        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonObject jso = new JsonObject();
        String query = "SELECT * FROM m_medicinesremainder WHERE "+KEY_ID+"= '"+recordId+"'";
        Cursor query_data = database.rawQuery(query, null);
        while (query_data.moveToNext()){
            jso.addProperty("id",query_data.getString(0));
            jso.addProperty("tablet_name",query_data.getString(1));
            jso.addProperty("dosage",query_data.getString(2));
            jso.addProperty("timeofday",query_data.getString(3));
            jso.addProperty("sunday",query_data.getString(4));
            jso.addProperty("monday",query_data.getString(5));
            jso.addProperty("tuesday",query_data.getString(6));
            jso.addProperty("wednesday",query_data.getString(7));
            jso.addProperty("thursday",query_data.getString(8));
            jso.addProperty("friday",query_data.getString(9));
            jso.addProperty("saturday",query_data.getString(10));
            jso.addProperty("startdate",query_data.getString(11));
            jso.addProperty("enddate",query_data.getString(12));
            jso.addProperty("inserteddatetime",query_data.getString(13));
            jso.addProperty("imagepath",query_data.getString(14));
            jso.addProperty("timerid",query_data.getString(15));

        }
        return jso;
    }

    public JsonObject getTakeSkipData(String userseleteddate, String remainderId) {
        JsonObject jso = null;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "SELECT * FROM t_takeskip WHERE "+ KEY_MEDICINE_REMAINDER_ID + " = '" + remainderId + "' AND " + KEY_DATETIME + " = '" + userseleteddate +"'";
        Cursor query_data = database.rawQuery(query, null);
        while (query_data.moveToNext()) {
            jso = new JsonObject();
            jso.addProperty(KEY_TS_ID, query_data.getString(0));
            jso.addProperty(KEY_MEDICINE_REMAINDER_ID, query_data.getString(1));
            jso.addProperty(KEY_TS_STATUS, query_data.getString(2));
            jso.addProperty(KEY_DATETIME, query_data.getString(3));
        }
        return jso;
    }

    public boolean deleteSingleRemainder(String id) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return database.delete("m_medicinesremainder", KEY_ID + "=" + id, null) > 0;
    }
}



