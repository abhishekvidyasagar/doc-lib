package com.doconline.doconline.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.model.Allergy;
import com.doconline.doconline.model.HealthProfile;
import com.doconline.doconline.model.Medication;
import com.doconline.doconline.model.PreferredLanguage;
import com.doconline.doconline.model.User;
import com.doconline.doconline.utils.NotificationUtils;

import java.util.ArrayList;

import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_LANGUAGE;
import static com.doconline.doconline.app.Constants.KEY_PREFERENCE;
import static com.doconline.doconline.app.Constants.KEY_READONLY;
import static com.doconline.doconline.app.Constants.KEY_STATUS;
import static com.doconline.doconline.app.Constants.SQL_QUERY_TAG;
import static com.doconline.doconline.app.Constants.TABLE_LANGUAGES;


public class SQLiteHelper extends SQLiteOpenHelper
{
    private static final String CREATE_TABLE_MEDICATIONS = "CREATE TABLE "
            + Constants.TABLE_MEDICATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_NAME + " TEXT," + Constants.KEY_INTAKE_TIME + " TEXT," + Constants.KEY_FROM_DATE + " TEXT,"
            + Constants.KEY_TO_DATE + " TEXT," + Constants.KEY_NOTES + " TEXT," + Constants.KEY_NO_OF_DAYS + " TEXT," +  Constants.KEY_STATUS + " TEXT," + Constants.KEY_ALARM_STATUS + " INTEGER DEFAULT 0," + Constants.KEY_SYNC_STATUS + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_ALLERGIES = "CREATE TABLE "
            + Constants.TABLE_ALLERGIES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_NAME + " TEXT," + Constants.KEY_SYNC_STATUS + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_HEALTH_PROFILE = "CREATE TABLE "
            + Constants.TABLE_HEALTH_PROFILE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Constants.KEY_WEIGHT + " REAL," + Constants.KEY_HEIGHT + " REAL," + Constants.KEY_DOES_SMOKE + " INTEGER,"
            + Constants.KEY_MEDICAL_HISTORY + " TEXT," + Constants.KEY_SYNC_STATUS + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + Constants.TABLE_PROFILE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_NAME_PREFIX + " TEXT," + Constants.KEY_FIRST_NAME + " TEXT," + Constants.KEY_MIDDLE_NAME + " TEXT,"
            + Constants.KEY_LAST_NAME + " TEXT," + Constants.KEY_DOB + " TEXT," + Constants.KEY_GENDER + " TEXT," + Constants.KEY_PROFILE_PIC + " TEXT," + Constants.KEY_ADDRESS_1 + " TEXT," + Constants.KEY_ADDRESS_2 + " TEXT,"
            + Constants.KEY_CITY + " TEXT," + Constants.KEY_STATE + " TEXT," + Constants.KEY_COUNTRY + " TEXT," + Constants.KEY_PINCODE + " TEXT," + Constants.KEY_PHONE_NO + " TEXT," + Constants.KEY_ALTERNATE_CONTACT_NO + " TEXT,"
            + Constants.KEY_SYNC_STATUS + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE "
            + Constants.TABLE_NOTIFICATIONS + "(" + KEY_ID + " TEXT PRIMARY KEY," + Constants.KEY_TYPE + " TEXT," + Constants.KEY_NOTIFIABLE_ID + " INTEGER," + Constants.KEY_NOTIFIABLE_TYPE + " TEXT," + Constants.KEY_MESSAGE + " TEXT,"
            + Constants.KEY_READ_AT + " TEXT," + Constants.KEY_CREATED_AT + " TEXT," + Constants.KEY_UPDATED_AT + " TEXT," + Constants.KEY_READ_STATUS + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_LANGUAGE = "CREATE TABLE "
            + TABLE_LANGUAGES + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_LANGUAGE + " TEXT," + KEY_PREFERENCE + " INTEGER," + KEY_READONLY + " INTEGER," + KEY_STATUS + " INTEGER DEFAULT 0)";


    public SQLiteHelper(Context context)
    {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_MEDICATIONS);
        database.execSQL(CREATE_TABLE_ALLERGIES);
        database.execSQL(CREATE_TABLE_HEALTH_PROFILE);
        database.execSQL(CREATE_TABLE_PROFILE);
        database.execSQL(CREATE_TABLE_NOTIFICATION);
        database.execSQL(CREATE_TABLE_LANGUAGE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_MEDICATIONS);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ALLERGIES);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_HEALTH_PROFILE);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_PROFILE);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NOTIFICATIONS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGES);

        // create new tables
        onCreate(database);
    }


    public boolean insert(User user)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, user.getUserId());
        values.put(Constants.KEY_NAME_PREFIX, user.getNamePrefix());
        values.put(Constants.KEY_FIRST_NAME, user.getFirstName());
        values.put(Constants.KEY_MIDDLE_NAME, user.getMiddleName());
        values.put(Constants.KEY_LAST_NAME, user.getLastName());
        values.put(Constants.KEY_DOB, user.getDateOfBirth());
        values.put(Constants.KEY_GENDER, user.getGender());
        values.put(Constants.KEY_PROFILE_PIC, user.getProfilePic());
        values.put(Constants.KEY_ADDRESS_1, user.getAddress().getAddress_1());
        values.put(Constants.KEY_ADDRESS_2, user.getAddress().getAddress_2());
        values.put(Constants.KEY_CITY, user.getAddress().getCity());
        values.put(Constants.KEY_STATE, user.getAddress().getState());
        values.put(Constants.KEY_COUNTRY, user.getAddress().getCountryCode());
        values.put(Constants.KEY_PINCODE, user.getAddress().getPincode());
        values.put(Constants.KEY_PHONE_NO, user.getPhoneNo());
        values.put(Constants.KEY_ALTERNATE_CONTACT_NO, user.getAlternatePhoneNo());

        boolean createSuccessful = database.insert(Constants.TABLE_PROFILE, null, values) > 0;

        database.close();

        Log.v("createSuccessful", "" + createSuccessful);
        return createSuccessful;
    }


    public boolean insert(NotificationUtils utils)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, utils.id);
        values.put(Constants.KEY_TYPE, utils.type);
        values.put(Constants.KEY_NOTIFIABLE_ID, utils.notifiable_id);
        values.put(Constants.KEY_NOTIFIABLE_TYPE, utils.notifiable_type);
        values.put(Constants.KEY_MESSAGE, utils.body);
        values.put(Constants.KEY_READ_AT, utils.read_at);
        values.put(Constants.KEY_CREATED_AT, utils.created_at);
        values.put(Constants.KEY_UPDATED_AT, utils.updated_at);

        boolean createSuccessful = database.insert(Constants.TABLE_NOTIFICATIONS, null, values) > 0;

        database.close();

        Log.v("createSuccessful", "" + createSuccessful);
        return createSuccessful;
    }


    public boolean insert(Medication medication)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, medication.getId());
        values.put(Constants.KEY_NAME, medication.getName());
        values.put(Constants.KEY_INTAKE_TIME, medication.getIntakeTime());
        values.put(Constants.KEY_FROM_DATE, medication.getFromDate());
        values.put(Constants.KEY_TO_DATE, medication.getToDate());
        values.put(Constants.KEY_NOTES, medication.getNotes());

        values.put(Constants.KEY_NO_OF_DAYS, medication.getNoOfDays());
        values.put(Constants.KEY_STATUS, medication.getStatus());

        values.put(Constants.KEY_ALARM_STATUS, medication.getAlarmStatus());

        boolean createSuccessful = database.insert(Constants.TABLE_MEDICATIONS, null, values) > 0;

        database.close();

        Log.v("createSuccessful", "" + createSuccessful);
        return createSuccessful;
    }


    public boolean insert(Allergy allergy)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, allergy.id);
        values.put(Constants.KEY_NAME, allergy.disease);

        boolean createSuccessful = database.insert(Constants.TABLE_ALLERGIES, null, values) > 0;

        database.close();

        Log.v("createSuccessful", "" + createSuccessful);
        return createSuccessful;
    }


    public boolean insert(HealthProfile profile)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, profile.user_id);
        values.put(Constants.KEY_HEIGHT, profile.height);
        values.put(Constants.KEY_WEIGHT, profile.weight);
        values.put(Constants.KEY_DOES_SMOKE, profile.is_smoke);
        values.put(Constants.KEY_MEDICAL_HISTORY, profile.medical_history);

        boolean createSuccessful = database.insert(Constants.TABLE_HEALTH_PROFILE, null, values) > 0;

        database.close();

        return createSuccessful;
    }


    public boolean insert(PreferredLanguage language)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, language.getID());
        values.put(KEY_LANGUAGE, language.getLanguage());
        values.put(KEY_PREFERENCE, language.getPreference());

        if(language.getReadOnly())
        {
            values.put(KEY_READONLY, 1);
        }

        else
        {
            values.put(KEY_READONLY, 0);
        }

        values.put(KEY_STATUS, language.getStatus());

        boolean createSuccessful = database.insert(TABLE_LANGUAGES, null, values) > 0;

        database.close();

        Log.v("createSuccessful", "" + createSuccessful);
        return createSuccessful;
    }


    public ArrayList<PreferredLanguage> getAllLanguages(boolean is_preferred)
    {
        String selectQuery = "SELECT * FROM " + TABLE_LANGUAGES;

        ArrayList<PreferredLanguage> list = new ArrayList<>();

        /*if(is_preferred)
        {
            selectQuery = "SELECT * FROM " + TABLE_LANGUAGES + " WHERE " + KEY_STATUS + " = '1' AND " + KEY_READONLY + " = '0' ORDER BY " + KEY_PREFERENCE + " ASC";
        }

        else
        {
            selectQuery = "SELECT * FROM " + TABLE_LANGUAGES + " WHERE " + KEY_STATUS + " = '0' OR " + KEY_READONLY + " = '1' ORDER BY " + KEY_PREFERENCE + " DESC";
        }*/

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                PreferredLanguage language = new PreferredLanguage();

                language.setID(cursor.getInt(0));
                language.setLanguage(cursor.getString(1));
                language.setPreference(cursor.getInt(2));

                if(cursor.getInt(3) == 1)
                {
                    language.setReadOnly(true);
                }

                else
                {
                    language.setReadOnly(false);
                }

                language.setStatus(cursor.getInt(4));

                list.add(language);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public HealthProfile getHealthProfile()
    {
        String selectQuery= "SELECT * FROM " + Constants.TABLE_HEALTH_PROFILE + " ORDER BY " + KEY_ID + " DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        HealthProfile profile = new HealthProfile();

        if(cursor.moveToFirst())
        {
            profile.setUserId(cursor.getInt(0));
            profile.setWeight(cursor.getFloat(1));
            profile.setHeight(cursor.getFloat(2));
            profile.setIsSmoke(cursor.getInt(3));
            profile.setMedicalHistory(cursor.getString(4));
        }

        profile.setAllergies(getAllAllergies());
        profile.setMedications(getAllMedications());

        cursor.close();
        return profile;
    }


    public User getUserProfile()
    {
        String selectQuery= "SELECT * FROM " + Constants.TABLE_PROFILE + " ORDER BY " + KEY_ID + " DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        User profile = new User();

        if(cursor.moveToFirst())
        {
            profile.setUserId(cursor.getInt(0));
            profile.setNamePrefix(cursor.getString(1));
            profile.setFirstName(cursor.getString(2));
            profile.setMiddleName(cursor.getString(3));
            profile.setLastName(cursor.getString(4));
            profile.setDateOfBirth(cursor.getString(5));
            profile.setGender(cursor.getString(6));
            profile.setProfilePic(cursor.getString(7));

            profile.getAddress().setAddress_1(cursor.getString(8));
            profile.getAddress().setAddress_2(cursor.getString(9));
            profile.getAddress().setCity(cursor.getString(10));
            profile.getAddress().setState(cursor.getString(11));
            profile.getAddress().setCountryCode(cursor.getString(12));
            profile.getAddress().setPincode(cursor.getString(13));

            profile.setPhoneNo(cursor.getString(14));
            profile.setAlternatePhoneNo(cursor.getString(15));
        }

        cursor.close();
        return profile;
    }


    public ArrayList<Medication> getAllMedications()
    {
        ArrayList<Medication> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_MEDICATIONS + " ORDER BY " + Constants.KEY_NAME + " DESC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                Medication medication = new Medication();

                medication.setId(cursor.getInt(0));
                medication.setName(cursor.getString(1));
                medication.setIntakeTime(cursor.getString(2));
                medication.setFromDate(cursor.getString(3));
                medication.setToDate(cursor.getString(4));
                medication.setNotes(cursor.getString(5));

                medication.setNoOfDays(cursor.getString(6));
                medication.setStatus(cursor.getString(7));

                medication.setAlarmStatus(cursor.getInt(8));

                list.add(medication);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public ArrayList<NotificationUtils> getAllNotifications(int x, int y)
    {
        ArrayList<NotificationUtils> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NOTIFICATIONS + " ORDER BY " + Constants.KEY_CREATED_AT + " DESC LIMIT " + x + "," + y;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                NotificationUtils utils = new NotificationUtils();

                utils.id = cursor.getString(0);
                utils.type = cursor.getString(1);
                utils.notifiable_id = cursor.getInt(2);
                utils.notifiable_type = cursor.getString(3);
                utils.body = cursor.getString(4);
                utils.read_at = cursor.getString(5);
                utils.created_at = cursor.getString(6);
                utils.updated_at = cursor.getString(7);

                list.add(utils);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public ArrayList<Allergy> getAllAllergies()
    {
        ArrayList<Allergy> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_ALLERGIES + " ORDER BY " + Constants.KEY_ID + " DESC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                Allergy allergy = new Allergy();

                allergy.setID(cursor.getInt(0));
                allergy.setDisease(cursor.getString(1));

                list.add(allergy);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public int dbRowCount(String TABLE_NAME)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    /*public int dbRowCount(String TABLE_NAME, String COLUMN_NAME, String value)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + "='" + value + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }*/


    /*public int unreadMessageCount(String TABLE_NAME)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_READ_STATUS + "='0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }*/


    /*public void updateSyncStatus(String TABLE_NAME, int id, int status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " + KEY_SYNC_STATUS + " = '" + status + "' WHERE " + KEY_ID + " = '" + id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }*/


    /*public void updateSyncStatus(String TABLE_NAME, String COLUMN_NAME, String id, int status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " + KEY_SYNC_STATUS + " = '" + status + "' WHERE " + COLUMN_NAME + " = '" + id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }*/


    public void update(HealthProfile profile)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + Constants.TABLE_HEALTH_PROFILE + " SET " + Constants.KEY_WEIGHT + " = '" + profile.weight + "',"
                + Constants.KEY_HEIGHT + " = '" + profile.height + "'," + Constants.KEY_DOES_SMOKE + " = '" + profile.is_smoke + "',"
                + Constants.KEY_MEDICAL_HISTORY + " = '" + profile.medical_history + "'";
        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void update(User profile)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        String updateQuery = "UPDATE " + Constants.TABLE_PROFILE + " SET " + Constants.KEY_NAME_PREFIX + " = '" + profile.getNamePrefix() + "',"
                + Constants.KEY_FIRST_NAME + " = '" + profile.getFirstName() + "'," + Constants.KEY_MIDDLE_NAME + " = '" + profile.getMiddleName() + "',"
                + Constants.KEY_LAST_NAME + " = '" + profile.getLastName() + "'," + Constants.KEY_DOB + " = '" + profile.getDateOfBirth() + "',"
                + Constants.KEY_PROFILE_PIC + " = '" + profile.getProfilePic() + "'," + Constants.KEY_GENDER + " = '" + profile.getGender() + "',"
                + Constants.KEY_ADDRESS_1 + " = '" + profile.getAddress().getAddress_1() + "'," + Constants.KEY_ADDRESS_2 + " = '" + profile.getAddress().getAddress_2() + "',"
                + Constants.KEY_CITY + " = '" + profile.getAddress().getCity() + "'," + Constants.KEY_STATE + " = '" + profile.getAddress().getState() + "',"
                + Constants.KEY_COUNTRY + " = '" + profile.getAddress().getCountryCode() + "'," + Constants.KEY_PINCODE + " = '" + profile.getAddress().getPincode() + "',"
                + Constants.KEY_PHONE_NO + " = '" + profile.getPhoneNo() + "'," + Constants.KEY_ALTERNATE_CONTACT_NO + " = '" + profile.getAlternatePhoneNo() + "'";

        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void remove(String table, String column, String value)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + table + " WHERE " + column + "='" + value + "'";
        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(updateQuery);
        database.close();
    }


    public void remove_all(String table)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + table;
        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(updateQuery);
        database.close();
    }

    public void update(String table_name, int id, String value)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + table_name + " SET " + Constants.KEY_NAME + " = '" + value +
                "' WHERE " + KEY_ID + " = '" + id + "'";
        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void update(Medication medication)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + Constants.TABLE_MEDICATIONS + " SET " + Constants.KEY_NAME + " = '" + medication.getName() + "',"
                + Constants.KEY_INTAKE_TIME + " = '" + medication.getIntakeTime() + "'," + Constants.KEY_FROM_DATE + " = '" + medication.getFromDate() + "',"
                + Constants.KEY_TO_DATE + " = '" + medication.getToDate() + "'," + Constants.KEY_NOTES + " = '" + medication.getName() + "',"
                + Constants.KEY_ALARM_STATUS + " = '" + medication.getAlarmStatus() + "' WHERE " + KEY_ID + " = '" + medication.getId() + "'";
        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void update(PreferredLanguage language)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_LANGUAGES + " SET " + Constants.KEY_STATUS + " = '" + language.getStatus() +
                "' WHERE " + KEY_ID + " = '" + language.getID() + "'";
        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void update(int id, int preference)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_LANGUAGES + " SET " + KEY_PREFERENCE + " = '" + preference +
                "' WHERE " + KEY_ID + " = '" + id + "'";
        Log.wtf(SQL_QUERY_TAG, updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
}