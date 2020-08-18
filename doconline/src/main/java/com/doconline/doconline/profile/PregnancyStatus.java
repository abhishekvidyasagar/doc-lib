package com.doconline.doconline.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.model.HealthProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_ABORTIONS;
import static com.doconline.doconline.app.Constants.KEY_EXPECTING_MOTHER;
import static com.doconline.doconline.app.Constants.KEY_NO_OF_CONCEPTIONS;
import static com.doconline.doconline.app.Constants.KEY_PREGNANCY_RELATED_COMPLICATIONS;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_UPDATE_PROFILE;

public class PregnancyStatus extends BaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView toolbar_title;

    EditText noofConceptions, abortions;
    TextView expectingMother, complications;

    HealthProfile profile;

    Button savePregnancyStatus;

    ProgressBar pbProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregnancy_status);

        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_title.setText(getResources().getString(R.string.pregnancy_status));

        expectingMother = findViewById(R.id.expecting_mother);
        expectingMother.setOnClickListener(this);
        complications = findViewById(R.id.complications);
        complications.setOnClickListener(this);

        noofConceptions = findViewById(R.id.noofconceptions);
        abortions = findViewById(R.id.abortions);

        savePregnancyStatus = findViewById(R.id.save_pregnancy_status);
        savePregnancyStatus.setOnClickListener(this);

        pbProgressBar = findViewById(R.id.progressBar);

        populateData();

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

    private void populateData() {
        //profile = (HealthProfile) getIntent().getSerializableExtra("pregnancyObject");
        String objectstatus = (String) getIntent().getSerializableExtra("objectstatus");
        if (objectstatus.equalsIgnoreCase("no")){
            expectingMother.setTag(0);
            expectingMother.setText("No");

            noofConceptions.setText("0");
            abortions.setText("0");

            complications.setTag(0);
            complications.setText("No");
        }else {
            try {
                JSONObject pregnacncyObject = new JSONObject(getIntent().getStringExtra("pregnancyObject"));

                if (pregnacncyObject.has(KEY_EXPECTING_MOTHER) && !pregnacncyObject.isNull(Constants.KEY_EXPECTING_MOTHER)){
                    if (pregnacncyObject.getInt(KEY_EXPECTING_MOTHER) == 1){
                        expectingMother.setTag(1);
                        expectingMother.setText("Yes");
                    }else if (pregnacncyObject.getInt(KEY_EXPECTING_MOTHER) == 0){
                        expectingMother.setTag(0);
                        expectingMother.setText("No");
                    }else {
                        expectingMother.setTag(0);
                        expectingMother.setText("No");
                    }
                }
                if (pregnacncyObject.has(KEY_NO_OF_CONCEPTIONS) && !pregnacncyObject.isNull(Constants.KEY_NO_OF_CONCEPTIONS)){
                    noofConceptions.setText(""+pregnacncyObject.get(KEY_NO_OF_CONCEPTIONS));
                }

                if (pregnacncyObject.has(KEY_ABORTIONS) && !pregnacncyObject.isNull(Constants.KEY_ABORTIONS)){
                    abortions.setText(""+pregnacncyObject.get(KEY_ABORTIONS));
                }

                if (pregnacncyObject.has(KEY_PREGNANCY_RELATED_COMPLICATIONS) && !pregnacncyObject.isNull(Constants.KEY_PREGNANCY_RELATED_COMPLICATIONS)){
                    if (pregnacncyObject.getString(KEY_PREGNANCY_RELATED_COMPLICATIONS).equalsIgnoreCase("1")){
                        complications.setTag(1);
                        complications.setText("Yes");
                    }else if (pregnacncyObject.getString(KEY_PREGNANCY_RELATED_COMPLICATIONS).equalsIgnoreCase("0")){
                        complications.setTag(0);
                        complications.setText("No");
                    }else {
                        complications.setTag(0);
                        complications.setText("No");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.expecting_mother) {
            JSONArray expectingmotherObject = new JSONArray();
            try {
                JSONObject jso = new JSONObject();
                jso.put("id", "1");
                jso.put("first_level_value", "Yes");
                expectingmotherObject.put(jso);
                JSONObject jsoo = new JSONObject();
                jsoo.put("id", "0");
                jsoo.put("first_level_value", "No");
                expectingmotherObject.put(jsoo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            showList(expectingmotherObject, expectingMother, Constants.SPINNER_HEADER_EXPECTING_MOTHER);
        } else if (id == R.id.complications) {
            JSONArray complicationsObject = new JSONArray();
            try {
                JSONObject jso = new JSONObject();
                jso.put("id", "1");
                jso.put("first_level_value", "Yes");
                complicationsObject.put(jso);
                JSONObject jsoo = new JSONObject();
                jsoo.put("id", "0");
                jsoo.put("first_level_value", "No");
                complicationsObject.put(jsoo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            showList(complicationsObject, complications, Constants.SPINNER_HEADER_COMPLICATIONS);
        } else if (id == R.id.save_pregnancy_status) {
            callSaveAPi();
        }
    }

    private void callSaveAPi() {
        pbProgressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_EXPECTING_MOTHER, expectingMother.getTag());
        hashMap.put(Constants.KEY_NO_OF_CONCEPTIONS, noofConceptions.getText());
        hashMap.put(Constants.KEY_ABORTIONS, abortions.getText());
        hashMap.put(Constants.KEY_PREGNANCY_RELATED_COMPLICATIONS, complications.getTag());
        hashMap.put(Constants.KEY_GENDER_PREGNANCY, "Female");

        new HttpClient(HTTP_REQUEST_CODE_UPDATE_PROFILE, MyApplication.HTTPMethod.PATCH.getValue(),
                hashMap, this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getHealthProfileURL());
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

        try {
            if (requestCode == HTTP_REQUEST_CODE_UPDATE_PROFILE
                    && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED)) {
                pbProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Data stored successfully", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }catch (Exception e){
            Toast.makeText(this, "e", Toast.LENGTH_SHORT).show();
        }
            
    }

    

    private void showList(JSONArray optionsArrayObject, final TextView spinnerView, String headerText) {
        final CharSequence[] items = new String[optionsArrayObject.length()];
        final CharSequence[] itemsids = new String[optionsArrayObject.length()];

        for (int i = 0; i < optionsArrayObject.length(); i++) {
            JSONObject model = null;
            try {
                model = optionsArrayObject.getJSONObject(i);
                items[i] = model.get("first_level_value").toString();
                itemsids[i] = model.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("" + headerText);
        builder1.setCancelable(false);
        builder1.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int item) {
                spinnerView.setText(items[item]);
                spinnerView.setTag(itemsids[item]);
            }
        });
        AlertDialog selectionDialog = builder1.create();
        selectionDialog.setCancelable(true);
        if (!selectionDialog.isShowing()) {
            selectionDialog.show();
        }
    }

}
