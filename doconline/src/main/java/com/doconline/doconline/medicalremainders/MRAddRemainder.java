package com.doconline.doconline.medicalremainders;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.models.Image;
import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.database.DBAdapter;
import com.doconline.doconline.helper.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MRAddRemainder extends BaseActivity {

    TextView toolbar_title;
    Toolbar toolbar;
    DBAdapter db;
    int REQUEST_CODE_ADD_TIME = 1;
    int REQUEST_CODE_SELECT_MEDICINE = 2;
    int REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY = 3;
    int REQUEST_CODE_PICK_IMAGE_FROM_CAMERA = 5;
    int REQUEST_CODE_ADD_DOSAGE = 4;

    Gson gson = new Gson();

    TextView tvSelectedTime, tvSelectMedicines, tvSelectDosage;

    LinearLayout llMedicineImage;

    File destination;
    String imagePath = "";
    Uri selectedImage;

    ImageView ivMedicineImage;
    TextView tvMedicineImage;
    TextView tvSaveReminder;

    JsonArray timearray = new JsonArray();

    LinearLayout llTimeList;
    private Calendar mCalendar;
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";
    private static final long milDay = 86400000L;
    private int mYear, mMonth, mHour, mMinute, mDay;

    String fromScreen;

    String remainderDataJsonStringObject;
    JsonObject remainderDataJson;

    JsonObject timeDataObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mradd_remainder);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);

        toolbar_title.setText(getResources().getString(R.string.text_add_remainders));


        tvSelectedTime = findViewById(R.id.selected_time);
        tvSelectMedicines = findViewById(R.id.select_medicine);
        tvSelectMedicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MRAddRemainder.this, MRSelectMedicine.class);
                startActivityForResult(i, REQUEST_CODE_SELECT_MEDICINE);
            }
        });
        tvSelectDosage = findViewById(R.id.select_dosage);
        tvSelectDosage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MRAddRemainder.this, MRSelectDosage.class);
                startActivityForResult(i, REQUEST_CODE_ADD_DOSAGE);
            }
        });

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        llMedicineImage = findViewById(R.id.medicineimagelay);
        ivMedicineImage = findViewById(R.id.medicineimage);
        tvMedicineImage = findViewById(R.id.medicineimagename);

        tvSaveReminder = findViewById(R.id.save_reminder_tv);

        llTimeList = findViewById(R.id.timelist_lay);

        fromScreen = getIntent().getExtras().get(Constants.FROM_SCREEN_KEY).toString();
        if (fromScreen.equalsIgnoreCase(Constants.FROM_SCREEN_KEY_EDIT)) {
            remainderDataJsonStringObject = getIntent().getExtras().get(Constants.REMAINDER_DATA).toString();
            remainderDataJson = gson.fromJson(remainderDataJsonStringObject, JsonObject.class);
            tvSaveReminder.setVisibility(View.GONE);
            autopopulateAvailableData(remainderDataJson);
        } else if (fromScreen.equalsIgnoreCase(Constants.FROM_SCREEN_KEY_CREATE)) {

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

    private void autopopulateAvailableData(JsonObject remainderDataJson) {
        tvSelectMedicines.setText(remainderDataJson.get(DBAdapter.KEY_TABLET).getAsString());
        tvSelectDosage.setVisibility(View.VISIBLE);
        llMedicineImage.setVisibility(View.VISIBLE);
        tvSelectDosage.setText(remainderDataJson.get(DBAdapter.KEY_DOSAGE).getAsString());
        tvMedicineImage.setText("Click here to select or change the image");

        try{
            File imgFile = new File(""+remainderDataJson.get(DBAdapter.KEY_IMAGEPATH).getAsString());
            if(imgFile.exists()){
                Glide.with(this).
                        load(new File(imgFile.getAbsolutePath())).
                        placeholder(getResources().getDrawable(R.drawable.ic_pill)).
                        into(ivMedicineImage);
            }
        }catch (Exception e){
            Log.e("Exception",""+e);
        }
        tvMedicineImage.setTag(remainderDataJson.get(DBAdapter.KEY_IMAGEPATH).getAsString());
        tvSelectedTime.setText(remainderDataJson.get(DBAdapter.KEY_TIME).getAsString());

    }

    public void saveRemainder(View view) {

        if(tvMedicineImage.getTag()==null)
            tvMedicineImage.setTag("");

        if (fromScreen.equalsIgnoreCase(Constants.FROM_SCREEN_KEY_EDIT)){
            if (timeDataObject == null){
                timeDataObject = remainderDataJson;
            }


            db.updateRemainder(tvSelectMedicines.getText().toString(), tvSelectDosage.getText().toString(), timeDataObject.get(DBAdapter.KEY_TIME).getAsString(),
                    timeDataObject.get(DBAdapter.KEY_SUNDAY).getAsString(), timeDataObject.get(DBAdapter.KEY_MONDAY).getAsString(), timeDataObject.get(DBAdapter.KEY_TUESDAY).getAsString(),
                    timeDataObject.get(DBAdapter.KEY_WEDNESDAY).getAsString(), timeDataObject.get(DBAdapter.KEY_THURSDAY).getAsString(), timeDataObject.get(DBAdapter.KEY_FRIDAY).getAsString(),
                    timeDataObject.get(DBAdapter.KEY_SATURDAY).getAsString(), timeDataObject.get(DBAdapter.KEY_STARTDATE).getAsString(), timeDataObject.get(DBAdapter.KEY_ENDDATE).getAsString(),
                    timeDataObject.get(DBAdapter.KEY_INSERTDATETIME).getAsString(), tvMedicineImage.getTag().toString(), remainderDataJson.get(DBAdapter.KEY_ID).getAsString());
            Toast.makeText(this, "Remainder updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            if(timearray.size()>0&&tvSelectMedicines.getText().toString()!=null&&!tvSelectMedicines.getText().toString().equalsIgnoreCase("")){
            for (int i = 0; i < timearray.size(); i++) {
                JsonObject timeObject = timearray.get(i).getAsJsonObject();
                Calendar c = Calendar.getInstance();
                int mseconds = c.get(Calendar.MILLISECOND);
                int reminderId = db.insertNewReamainder(tvSelectMedicines.getText().toString(), tvSelectDosage.getText().toString(), timeObject.get(DBAdapter.KEY_TIME).getAsString(),
                        timeObject.get(DBAdapter.KEY_SUNDAY).getAsString(), timeObject.get(DBAdapter.KEY_MONDAY).getAsString(), timeObject.get(DBAdapter.KEY_TUESDAY).getAsString(),
                        timeObject.get(DBAdapter.KEY_WEDNESDAY).getAsString(), timeObject.get(DBAdapter.KEY_THURSDAY).getAsString(), timeObject.get(DBAdapter.KEY_FRIDAY).getAsString(),
                        timeObject.get(DBAdapter.KEY_SATURDAY).getAsString(), timeObject.get(DBAdapter.KEY_STARTDATE).getAsString(), timeObject.get(DBAdapter.KEY_ENDDATE).getAsString(),
                        timeObject.get(DBAdapter.KEY_INSERTDATETIME).getAsString(), tvMedicineImage.getTag().toString(),mseconds+"");
                }
                try{
                    AlarmReceiver alarmReceiver = new AlarmReceiver();
                    alarmReceiver.setDailyAlarmCheck(getApplicationContext(),true);
                }catch (Exception e){

                }
                Toast.makeText(this, "Reminder saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(this, "Please Select Medicine Name and Valid Time to Add Reminder", Toast.LENGTH_SHORT).show();
                }
            }
        }

    public void selectTime(View view) {
        Intent i = new Intent(MRAddRemainder.this, MRSelectTime.class);
        if (fromScreen.equalsIgnoreCase(Constants.FROM_SCREEN_KEY_EDIT)){
            i.putExtra(Constants.FROM_SCREEN_KEY, Constants.FROM_SCREEN_KEY_EDIT);
            i.putExtra(Constants.REMAINDER_TIME_DATA, remainderDataJsonStringObject);
        }else {
            i.putExtra(Constants.FROM_SCREEN_KEY, Constants.FROM_SCREEN_KEY_CREATE);
        }
        startActivityForResult(i, REQUEST_CODE_ADD_TIME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_ADD_TIME && resultCode == RESULT_OK) {
                String json = data.getStringExtra("timedata");
                timeDataObject = gson.fromJson(json, JsonObject.class);

                if (fromScreen.equalsIgnoreCase(Constants.FROM_SCREEN_KEY_EDIT)){
                    tvSelectedTime.setText(timeDataObject.get(DBAdapter.KEY_TIME).getAsString());
                }else {
                    timearray.add(timeDataObject);
                    llTimeList.removeAllViews();
                    for (int i = 0; i < timearray.size(); i++) {
                        final JsonObject timeObject = timearray.get(i).getAsJsonObject();
                        LinearLayout linearLayout = (LinearLayout) View.inflate(MRAddRemainder.this, R.layout.inflater_time_list, null);
                        ((TextView) linearLayout.findViewById(R.id.time)).setText(timeObject.get(DBAdapter.KEY_TIME).getAsString());

                        llTimeList.addView(linearLayout);
                    }
                }
                //tvSelectedTime.setText(jsonObject.get("time").getAsString());
                setSaveButtonVisibility();

            } else if (requestCode == REQUEST_CODE_SELECT_MEDICINE && resultCode == RESULT_OK) {
                tvSelectDosage.setVisibility(View.VISIBLE);
                llMedicineImage.setVisibility(View.VISIBLE);
                String medicinename = data.getStringExtra("medicinename");
                tvSelectMedicines.setText(medicinename);

                setSaveButtonVisibility();
            } else if (requestCode == REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
                ArrayList<Image> images = data.getParcelableArrayListExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);

                for(Image img: images)
                {
                    File file = new File(img.path);
                    long length = file.length() / 1024; // Size in KB

                    if(length <= 1024 * mController.getSession().getMaxFileSize())
                    {
                        Bitmap thumbnail = (BitmapFactory.decodeFile(img.path));
                        imagePath = img.path;
                        ivMedicineImage.setImageBitmap(thumbnail);
                        tvMedicineImage.setText("" + imagePath.substring(imagePath.lastIndexOf("/")+1));
                        tvMedicineImage.setTag("" + imagePath);
                        setSaveButtonVisibility();
                        //display_image(img.path);
                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(), "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                    }
                }


                /*selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();*/

            }else if (requestCode == REQUEST_CODE_PICK_IMAGE_FROM_CAMERA && resultCode == RESULT_OK){


                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");

                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();

                        ivMedicineImage.setImageBitmap(thumbnail);
                        tvMedicineImage.setText("");
                        tvMedicineImage.setTag(destination);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                setSaveButtonVisibility();

            }else if (requestCode == REQUEST_CODE_ADD_DOSAGE && resultCode == RESULT_OK) {
                tvSelectDosage.setVisibility(View.VISIBLE);
                String dosage = data.getStringExtra("dosage");
                tvSelectDosage.setText(dosage);

                setSaveButtonVisibility();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setSaveButtonVisibility() {
        if (tvSaveReminder.getVisibility() == View.GONE){
            tvSaveReminder.setVisibility(View.VISIBLE);
        }
    }

    public void selectMedicineImage(View view) {

        final CharSequence[] options = {"Open Gallery", "Open Camera"};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MRAddRemainder.this);
        builder.setTitle("Select!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Open Camera")) {

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_CODE_PICK_IMAGE_FROM_CAMERA);

                } else if (options[item].equals("Open Gallery")) {
                    /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY);*/

                    Intent intent = new Intent(MRAddRemainder.this, AlbumSelectActivity.class);
                    //set limit on number of images that can be selected, default is 10
                    intent.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 1);
                    startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }



}
