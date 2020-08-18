package com.doconline.doconline.medicalremainders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.database.DBAdapter;
import com.doconline.doconline.helper.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MRSelectTime extends BaseActivity implements View.OnClickListener {


    TextView toolbar_title;
    Toolbar toolbar;

    TextView tvYes, tvNo, tvShowHideAdvOptions;
    List<TextView> textViewList = new ArrayList<>();

    LinearLayout llEndDateLayout, llAdvancedOptions;

    TextView tvSunday, tvMonday, tvTuesday, tvWednesday, tvThursday, tvFriday, tvSaturday, tvSelectTime, tvSelectStartDate, tvSelectEndDate;

    Gson gson = new Gson();

    String fromScreen;
    JsonObject remainderDataJson;

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrselect_time);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);

        toolbar_title.setText(getResources().getString(R.string.text_select_time));
        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvYes = findViewById(R.id.yes);
        tvNo = findViewById(R.id.no);
        textViewList.add(tvYes);
        textViewList.add(tvNo);

        llEndDateLayout = findViewById(R.id.enddate_layout);
        llAdvancedOptions = findViewById(R.id.advancedoptions);
        tvShowHideAdvOptions = findViewById(R.id.showhideadvancedoptions_tv);

        tvSunday = findViewById(R.id.sunday);
        tvSunday.setOnClickListener(this);
        tvMonday = findViewById(R.id.monday);
        tvMonday.setOnClickListener(this);
        tvTuesday = findViewById(R.id.tuesday);
        tvTuesday.setOnClickListener(this);
        tvWednesday = findViewById(R.id.wednesday);
        tvWednesday.setOnClickListener(this);
        tvThursday = findViewById(R.id.thursday);
        tvThursday.setOnClickListener(this);
        tvFriday = findViewById(R.id.friday);
        tvFriday.setOnClickListener(this);
        tvSaturday = findViewById(R.id.saturday);
        tvSaturday.setOnClickListener(this);

        tvSelectTime = findViewById(R.id.select_time);
        tvSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime(tvSelectTime);
            }
        });
        tvSelectStartDate = findViewById(R.id.start_date);
        tvSelectStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(tvSelectStartDate);
            }
        });
        tvSelectEndDate = findViewById(R.id.end_date);
        tvSelectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(tvSelectEndDate);
            }
        });

        fromScreen = getIntent().getExtras().get(Constants.FROM_SCREEN_KEY).toString();
        if (fromScreen.equalsIgnoreCase(Constants.FROM_SCREEN_KEY_EDIT)) {
            String remainderDataJsonStringObject = getIntent().getExtras().get(Constants.REMAINDER_TIME_DATA).toString();
            remainderDataJson = gson.fromJson(remainderDataJsonStringObject, JsonObject.class);
            autopopulateAvailableData(remainderDataJson);

        }
    }

    private void autopopulateAvailableData(JsonObject remainderDataJson) {
        tvSelectTime.setText(remainderDataJson.get(DBAdapter.KEY_TIME).getAsString());
        if (remainderDataJson.get(DBAdapter.KEY_SUNDAY).getAsString().equalsIgnoreCase("no")) {
            weekDayOnclick(tvSunday);
        }
        if (remainderDataJson.get(DBAdapter.KEY_MONDAY).getAsString().equalsIgnoreCase("no")) {
            weekDayOnclick(tvMonday);
        }
        if (remainderDataJson.get(DBAdapter.KEY_TUESDAY).getAsString().equalsIgnoreCase("no")) {
            weekDayOnclick(tvTuesday);
        }
        if (remainderDataJson.get(DBAdapter.KEY_WEDNESDAY).getAsString().equalsIgnoreCase("no")) {
            weekDayOnclick(tvWednesday);
        }
        if (remainderDataJson.get(DBAdapter.KEY_THURSDAY).getAsString().equalsIgnoreCase("no")) {
            weekDayOnclick(tvThursday);
        }
        if (remainderDataJson.get(DBAdapter.KEY_FRIDAY).getAsString().equalsIgnoreCase("no")) {
            weekDayOnclick(tvFriday);
        }
        if (remainderDataJson.get(DBAdapter.KEY_SATURDAY).getAsString().equalsIgnoreCase("no")) {
            weekDayOnclick(tvSaturday);
        }

        tvSelectStartDate.setText(remainderDataJson.get(DBAdapter.KEY_STARTDATE).getAsString());

        if (!remainderDataJson.get(DBAdapter.KEY_ENDDATE).getAsString().equalsIgnoreCase("")){
            tvSelectEndDate.setText(remainderDataJson.get(DBAdapter.KEY_ENDDATE).getAsString());
            showHideAdvancedOptions();
            changeYesNoColor(tvYes);
        }

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

    private void selectTime(final TextView selecttime) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time = hour + ":" + minute;
                try {
                    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    final Date dateObj = sdf.parse(time);
                    time = new SimpleDateFormat("hh:mm aa").format(dateObj);
                } catch (Exception e) {
                    Log.e("AAA", "timeexception " + e);
                }
                selecttime.setText(time);
            }
        };

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        // Whether show time in 24 hour format or not.
        boolean is24Hour = false;

        TimePickerDialog timePickerDialog = new TimePickerDialog(MRSelectTime.this, onTimeSetListener, hour, minute, is24Hour);

        timePickerDialog.setIcon(R.drawable.ic_time_icon);
        timePickerDialog.setTitle("Please select time.");

        timePickerDialog.show();
    }

    private void selectDate(final TextView selectdate) {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                selectdate.setText(date);
            }
        };

        // Get current year, month and day.
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);

        // Create the new DatePickerDialog instance.
        DatePickerDialog datePickerDialog = new DatePickerDialog(MRSelectTime.this, onDateSetListener, year, month, day);

        // Set dialog icon and title.
        datePickerDialog.setIcon(R.drawable.ic_icon_calender);
        datePickerDialog.setTitle("Please select date.");
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());


        // Popup the dialog.
        datePickerDialog.show();
    }

    public void saveTime(View view) {
        DateFormat dateFormat = new SimpleDateFormat(Constants.MR_DATE_FORMAT);
        Date date = new Date();
        String todayDate = dateFormat.format(date);
        if ((!tvSelectTime.getText().toString().equalsIgnoreCase(""))){

            if(((tvSunday.getTag().toString().equalsIgnoreCase("yes"))||
                    (tvMonday.getTag().toString().equalsIgnoreCase("yes"))||(tvTuesday.getTag().toString().equalsIgnoreCase("yes"))||
                    (tvWednesday.getTag().toString().equalsIgnoreCase("yes"))||(tvThursday.getTag().toString().equalsIgnoreCase("yes"))||
                    (tvFriday.getTag().toString().equalsIgnoreCase("yes"))||(tvSaturday.getTag().toString().equalsIgnoreCase("yes")))) {

                JsonObject jso = new JsonObject();
                jso.addProperty(DBAdapter.KEY_TIME, tvSelectTime.getText().toString());
                jso.addProperty(DBAdapter.KEY_INSERTDATETIME, todayDate);
                jso.addProperty(DBAdapter.KEY_SUNDAY, tvSunday.getTag().toString());
                jso.addProperty(DBAdapter.KEY_MONDAY, tvMonday.getTag().toString());
                jso.addProperty(DBAdapter.KEY_TUESDAY, tvTuesday.getTag().toString());
                jso.addProperty(DBAdapter.KEY_WEDNESDAY, tvWednesday.getTag().toString());
                jso.addProperty(DBAdapter.KEY_THURSDAY, tvThursday.getTag().toString());
                jso.addProperty(DBAdapter.KEY_FRIDAY, tvFriday.getTag().toString());
                jso.addProperty(DBAdapter.KEY_SATURDAY, tvSaturday.getTag().toString());

                //place validation if user not select start date only
                if (tvSelectStartDate.getText().toString().equalsIgnoreCase("")) {
                    jso.addProperty(DBAdapter.KEY_STARTDATE, todayDate);
                } else {
                    jso.addProperty(DBAdapter.KEY_STARTDATE, tvSelectStartDate.getText().toString());
                }

                jso.addProperty(DBAdapter.KEY_ENDDATE, tvSelectEndDate.getText().toString());
                finishActivityy(jso);
            /*if (llEndDateLayout.getVisibility() == View.VISIBLE) {
                jso.addProperty(DBAdapter.KEY_ENDDATE, tvSelectEndDate.getText().toString());
                finishActivityy(jso);
            } else {
                jso.addProperty(DBAdapter.KEY_ENDDATE, "");
                finishActivityy(jso);
            }*/
            }else{
                Toast.makeText(this, "Please select atleast one day", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please select time ", Toast.LENGTH_SHORT).show();
        }

    }

    private void finishActivityy(JsonObject jso) {
        Intent intent = getIntent();
        String json = gson.toJson(jso, JsonObject.class);
        intent.putExtra("timedata", json);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void yesSelect(View view) {
        changeYesNoColor(tvYes);
    }

    public void noSelect(View view) {
        changeYesNoColor(tvNo);
    }

    private void changeYesNoColor(TextView textView) {
        for (int i = 0; i < textViewList.size(); i++) {
            if (textViewList.get(i) == textView) {
                textViewList.get(i).setBackgroundColor(getResources().getColor(primaryColor));
                textViewList.get(i).setTextColor(getResources().getColor(R.color.white));
                llEndDateLayout.setVisibility(View.GONE);
            } else {
                textViewList.get(i).setBackgroundColor(getResources().getColor(R.color.white));
                textViewList.get(i).setTextColor(getResources().getColor(R.color.black));
                llEndDateLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showHideAdvancedOptions(View view) {
        showHideAdvancedOptions();
    }

    public void showHideAdvancedOptions(){
        if (llAdvancedOptions.getVisibility() == View.VISIBLE) {
            llAdvancedOptions.setVisibility(View.GONE);
            tvShowHideAdvOptions.setText("Show Advanced Options");
        } else {
            llAdvancedOptions.setVisibility(View.VISIBLE);
            tvShowHideAdvOptions.setText("Hide Advanced Options");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.sunday) {
            weekDayOnclick(tvSunday);
        } else if (id == R.id.monday) {
            weekDayOnclick(tvMonday);
        } else if (id == R.id.tuesday) {
            weekDayOnclick(tvTuesday);
        } else if (id == R.id.wednesday) {
            weekDayOnclick(tvWednesday);
        } else if (id == R.id.thursday) {
            weekDayOnclick(tvThursday);
        } else if (id == R.id.friday) {
            weekDayOnclick(tvFriday);
        } else if (id == R.id.saturday) {
            weekDayOnclick(tvSaturday);
        }
    }

    public void weekDayOnclick(TextView weekDay) {
        if (weekDay.getTag().toString().equalsIgnoreCase("yes")) {
            weekDay.setBackground(getResources().getDrawable(R.drawable.round_button_white));
            weekDay.setTag("no");
            weekDay.setTextColor(getResources().getColor(R.color.black));
        } else if (weekDay.getTag().toString().equalsIgnoreCase("no")) {
            weekDay.setBackground(getResources().getDrawable(R.drawable.round_button_green));
            weekDay.setTag("yes");
            weekDay.setTextColor(getResources().getColor(R.color.white));
        }

    }

}
