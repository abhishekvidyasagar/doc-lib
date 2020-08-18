package com.doconline.doconline.medicalremainders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.database.DBAdapter;
import com.doconline.doconline.helper.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RemainderDetails extends BaseActivity {

    TextView toolbar_title;
    Toolbar toolbar;

    String remainderObjectJsonString;
    Gson gson = new Gson();

    TextView tvMedicineName, tvDosagaTime;
    ImageView ivMedicineImage;

    DBAdapter db;
    JsonObject remainderObject;

    LinearLayout llSkipTake;

    String userseleteddate;

    SimpleDateFormat sdf = new SimpleDateFormat(Constants.MR_DATE_FORMAT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder_details);


        toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getResources().getString(R.string.remainders_details));
        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvMedicineName = findViewById(R.id.medicinename_tv);
        tvDosagaTime = findViewById(R.id.dosagetime_tv);
        ivMedicineImage = findViewById(R.id.medicine_image);

        llSkipTake = findViewById(R.id.skiptakelay);

        remainderObjectJsonString = getIntent().getStringExtra(Constants.REMAINDER_DATA);
        userseleteddate = getIntent().getStringExtra(Constants.USER_SELECTED_DATE);

        remainderObject = gson.fromJson(remainderObjectJsonString, JsonObject.class);
        autoPopulateData(remainderObject);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: //Back arrow pressed
            {
                this.onBackPressed();
            }
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void autoPopulateData(JsonObject jso) {
        try {
            File imgFile = new File("" + jso.get(DBAdapter.KEY_IMAGEPATH).getAsString());
            if (imgFile.exists()) {
                Glide.with(this).
                        load(new File(imgFile.getAbsolutePath())).
                        placeholder(getResources().getDrawable(R.drawable.ic_pill)).
                        into(ivMedicineImage);
            }
        } catch (Exception e) {
            Log.e("Exception", "" + e);
        }

        tvMedicineName.setText("" + jso.get(DBAdapter.KEY_TABLET).getAsString());


        if (jso.get(DBAdapter.KEY_DOSAGE).getAsString().equalsIgnoreCase("")) {
            tvDosagaTime.setText("" + jso.get(DBAdapter.KEY_TIME).getAsString());
        } else {
            tvDosagaTime.setText(jso.get(DBAdapter.KEY_DOSAGE).getAsString() + " , " + jso.get(DBAdapter.KEY_TIME).getAsString());
        }


        String pattern = "h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date remaindertime = sdf.parse(jso.get(DBAdapter.KEY_TIME).getAsString());

            Calendar calendar = Calendar.getInstance();
            String date = sdf.format(calendar.getTime());
            Date actualtime = sdf.parse(date);

            SimpleDateFormat defaultFormatter = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
            if (!userseleteddate.equalsIgnoreCase("") && userseleteddate.equals(defaultFormatter.format(new Date()))) {
                JsonObject jsa = db.getTakeSkipData(userseleteddate, jso.get(DBAdapter.KEY_ID).getAsString());
                if (jsa == null) {
                    llSkipTake.setVisibility(View.VISIBLE);
                } else {
                    llSkipTake.setVisibility(View.GONE);
                }
            } else {
                llSkipTake.setVisibility(View.GONE);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void doSkip(View view) {
        DateFormat dateFormat = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
        Date date = new Date();
        String todayDate = dateFormat.format(date);

        db.insertSkipTake(remainderObject.get("id").getAsString(), "skip", todayDate);
        Toast.makeText(this, "You have skipped this medication ", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void doTake(View view) {
        DateFormat dateFormat = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
        Date date = new Date();
        String todayDate = dateFormat.format(date);

        db.insertSkipTake(remainderObject.get("id").getAsString(), "take", todayDate);
        Toast.makeText(this, "You are following the medication perfectly ", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void doDelete(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Medication ?");
        alert.setMessage("Are you sure. You want to delete all reminders for this Medication ?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (db.deleteRemainder(remainderObject.get(DBAdapter.KEY_TABLET).getAsString(),
                        remainderObject.get(DBAdapter.KEY_DOSAGE).getAsString())) {
                    Toast.makeText(RemainderDetails.this, "Reminder Delete Succesfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RemainderDetails.this, "error while deleting the remainder. try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    public void doEdit(View view) {
        Intent i = new Intent(RemainderDetails.this, MRAddRemainder.class);
        i.putExtra(Constants.FROM_SCREEN_KEY, Constants.FROM_SCREEN_KEY_EDIT);
        i.putExtra(Constants.REMAINDER_DATA, remainderObjectJsonString);
        startActivity(i);
        finish();
    }

    /*public void doEdit(View view) {
        Intent i = new Intent(RemainderDetails.this, MRAddRemainder.class);
        i.putExtra("")
        startActivity(i);
    }*/
}
