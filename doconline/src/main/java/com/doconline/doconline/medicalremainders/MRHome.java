package com.doconline.doconline.medicalremainders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.database.DBAdapter;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.medicalremainders.adapter.RemaindersAdapter;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission_group.STORAGE;

public class MRHome extends BaseActivity implements DatePickerListener {

    TextView toolbar_title;
    Toolbar toolbar;
    private HorizontalPicker picker;
    public static final int PERMISSION_REQUEST_CODE = 2;
    DBAdapter db;

    SwipeMenuListView lvRemaindersList;

    public static String userSelectedDate = "";

    JsonArray remindersJsa;

    String todayDate;
    String todayDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrhome);



        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getResources().getString(R.string.text_medical_remainders_home));

        Date todayDateDefault = new Date();
        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        DateFormat dateFormat = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
        todayDate = dateFormat.format(todayDateDefault);
        todayDay = dayFormat.format(todayDateDefault);



        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AAA", "" + e);
        }

        picker = findViewById(R.id.datePicker);
        picker.setOffset(50);
        picker.setDays(350);
        picker.setListener(this);
        picker.showTodayButton(false);
        picker.setDateSelectedColor(getResources().getColor(primaryColor));
        picker.setDateSelectedTextColor(getResources().getColor(R.color.white));
        picker.setMonthAndYearTextColor(getResources().getColor(R.color.white));
        picker.setTodayDateTextColor(getResources().getColor(R.color.white));
        picker.setTodayDateBackgroundColor(getResources().getColor(primaryColor));
        picker.setUnselectedDayTextColor(getResources().getColor(R.color.white));
        picker.setDayOfWeekTextColor(getResources().getColor(R.color.white));
        picker.setBackground(getResources().getDrawable(R.drawable.action_bar_bg));
        picker.setDays(5000);
        picker.init();
        picker.setDate(new DateTime());


        lvRemaindersList = findViewById(R.id.remainderslist);
        lvRemaindersList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        lvRemaindersList.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                //REJECT
                SwipeMenuItem rejectItem = new SwipeMenuItem(MRHome.this);
                // set item background
                //rejectItem.setBackground(new ColorDrawable(context.getResources().getColor(R.color.colorPrimaryDark)));
                // set item width
                rejectItem.setWidth(250);
                // set a icon
                //rejectItem.setIcon(R.drawable.ic_delete);
                // set item title
                rejectItem.setTitle("DELETE");
                // set item title fontsize
                rejectItem.setTitleSize(18);
                // set item title font color
                rejectItem.setTitleColor(Color.RED);

                //rejectItem.setCol(Color.RED);
                // add to menu
                menu.addMenuItem(rejectItem);
            }
        });

        lvRemaindersList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MRHome.this);
                    alert.setTitle("DELETE ?");
                    alert.setMessage("Are you sure wish to delete this Remainder ? ");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (db.deleteSingleRemainder(remindersJsa.get(position).getAsJsonObject().get("id").getAsString())){
                                Toast.makeText(MRHome.this, "Remainder Delete Succesfully", Toast.LENGTH_SHORT).show();
                                //finish();
                                setRemaindersAdapter(todayDay, todayDate);

                            }else {
                                Toast.makeText(MRHome.this, "error while deleting the remainder. try again!", Toast.LENGTH_SHORT).show();
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
                return false;
            }
        });

        lvRemaindersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(remindersJsa.get(position).getAsJsonObject());
                Intent intent = new Intent(MRHome.this, RemainderDetails.class);
                intent.putExtra(Constants.REMAINDER_DATA, jsonString);
                intent.putExtra(Constants.USER_SELECTED_DATE, MRHome.userSelectedDate);
                startActivity(intent);
            }
        });



        if(!CheckingPermissionIsEnabledOrNot())
        {
            RequestMultiplePermission();
        }

    }

    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission() {
        /**
         *
         * Creating String Array with Permissions.
         */
        ActivityCompat.requestPermissions(MRHome.this, new String[]
                {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (!storagePermission||!cameraPermission) {
                        RequestMultiplePermission();
                    }
                }

                break;
        }
    }

    /**
     * Checking permission is enabled or not
     */
    public boolean CheckingPermissionIsEnabledOrNot() {
        int CameraPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int storagePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), STORAGE);

        return CameraPermissionResult == PackageManager.PERMISSION_GRANTED && storagePermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
    public void onBackPressed()
    {
                super.onBackPressed();
                this.finish();
    }

    private void setRemaindersAdapter(String dday, String ddate) {
        remindersJsa = db.getRemaindersData(dday, ddate);

        JsonObject jso = new JsonObject();
        jso.addProperty("tablet_name", "ADDD");
        remindersJsa.add(jso);

        RemaindersAdapter adapter = new RemaindersAdapter(MRHome.this, R.layout.inflater_remainders, remindersJsa) ;

        if(remindersJsa!=null&&remindersJsa.size()>0) {
            lvRemaindersList.setAdapter(adapter);
        }else{
            lvRemaindersList.setAdapter(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setRemaindersAdapter(todayDay, todayDate);
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        try {
            String dateStr = String.valueOf(dateSelected);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            SimpleDateFormat format2 = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
            SimpleDateFormat format3 = new SimpleDateFormat("EEEE");
            Date date = format1.parse(dateStr);
            System.out.println(format2.format(date));

            userSelectedDate = format2.format(date);

            setRemaindersAdapter(format3.format(date), format2.format(date));


        } catch (Exception e) {
            Log.e("AAA", "" + e);
        }


    }
}
