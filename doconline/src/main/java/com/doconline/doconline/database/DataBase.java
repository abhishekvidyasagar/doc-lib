package com.doconline.doconline.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBase extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.doconline.doconline/databases/";
	private static String DATABASE_NAME = "doconline.db";
	private static int DB_VERSION = 1;

	private static SQLiteDatabase myDataBase;

	private final Context myContext;
	FileOutputStream myOutput;

	public DataBase(Context context) throws IOException {

		super(context, DATABASE_NAME, null, DB_VERSION);

		this.myContext = context;
        Log.d("construtor", "" + this.myContext);


		boolean dbexist = checkDataBase(); 
		if (dbexist) {
			System.out.println("Database exists");
		} else {
			System.out.println("Database doesn't exist");
			createDataBase();
		}

	}

	public void createDataBase() throws IOException {
		Log.i("createDataBase", "dbExist ");
		boolean dbExist = checkDataBase();
		if(dbExist){
			Log.i("createDataBase", "dbExist " + dbExist);
		}else{
			Log.i("createDataBase", "dbNotExist " + dbExist);
			this.getReadableDatabase();
			this.close();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private boolean checkDataBase() {

		SQLiteDatabase checkDb = null;
		Log.i("checkDataBase", "dbExist ");
		try {
			String path = DB_PATH + DATABASE_NAME;
			Log.i("checkDataBase", "dbExist " + path);
			checkDb = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			Log.i("checkDataBase", "dbExist SQLException");
			// Database does not exist yet 
			e.printStackTrace();
		}
		
		if(checkDb!=null){
			checkDb.close();
		}
		return checkDb != null;
	}

	private void copyDataBase() throws IOException {
		Log.d("*****", "&&&&&&");
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open("doconline.db");
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(DB_PATH + DATABASE_NAME);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DATABASE_NAME;
		try {
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
			if(myDataBase!=null){
				Log.d("Database open ", "database open successfully");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("openDataBase", myPath);
		Log.d("openDataBase MyDataBase", myDataBase.toString());
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("onUpgrade called ");
		Log.i("KAR", "DBAbase onUpgrade  ");
		switch (oldVersion){
			case 1:
				String addphotoQuery = "ALTER TABLE tt_surveyor_dtls ADD COLUMN photo_added INEGER";
				db.execSQL(addphotoQuery);
				String sIdQuery = "ALTER TABLE tt_surveyor_dtls ADD COLUMN server_id TEXT";
				db.execSQL(sIdQuery);
		}
	}
}
