package com.doconline.doconline.medicalremainders;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.medicalremainders.adapter.SelectMedicineListAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MRSelectMedicine extends BaseActivity implements OnHttpResponse,
        OnLoadingStatusChangedListener,
        OnDialogAction,
        SearchView.OnQueryTextListener,
        View.OnFocusChangeListener,
        SearchView.OnCloseListener,
        TextView.OnEditorActionListener {

    public static final int HTTP_REQUEST_CODE_MEDICINES_LIST = 1;
    public MyApplication mController;
    LinearLayout layout_search;
    ProgressBar pbProgressBar;
    ListView medicineList;
    SearchView search_medicines;
    JsonArray jsonArraymedicines = new JsonArray();
    TextView toolbar_title;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrselect_medicine);


        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getResources().getString(R.string.text_select_medicine));

        this.mController = MyApplication.getInstance();
        layout_search = findViewById(R.id.layout_search);
        medicineList = findViewById(R.id.medicinelist);
        search_medicines = findViewById(R.id.search_medicines);
        pbProgressBar = findViewById(R.id.progressBar);

        medicineList = findViewById(R.id.medicinelist);

        medicineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JsonObject medicine = jsonArraymedicines.get(position).getAsJsonObject();
                if(medicine.get("medicineName").getAsString().equalsIgnoreCase("Others")){
                    final EditText edittext = new EditText(MRSelectMedicine.this);
                    AlertDialog.Builder alert = new AlertDialog.Builder(MRSelectMedicine.this);
                    alert.setMessage("Please enter the medicine name");
                    alert.setView(edittext);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            intent.putExtra("medicinename", edittext.getText().toString());
                            setResult(RESULT_OK, intent);
                            jsonArraymedicines = new JsonArray();
                            finish();
                        }
                    });
                    alert.show();
                }else{
                    Intent intent = getIntent();
                    intent.putExtra("medicinename", medicine.get("medicineName").getAsString());
                    setResult(RESULT_OK, intent);
                    jsonArraymedicines = new JsonArray();
                    finish();
                }
            }
        });

        /*Expanding the search view */
        search_medicines.setIconified(true);
        search_medicines.setIconifiedByDefault(false);


        try {
            int searchTextId = search_medicines.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = search_medicines.findViewById(searchTextId);
            textView.setTextColor(Color.BLACK);
            textView.setHintTextColor(Color.LTGRAY);

            textView.setOnEditorActionListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //search_diagnostics.setOnClickListener(this);
        search_medicines.setOnQueryTextListener(this);
        search_medicines.setOnQueryTextFocusChangeListener(this);
        search_medicines.setOnCloseListener(this);


    }

    public void currentSearchString(String str) {
        //getDiagnosticsSearchURL
        //if (searchString)
        if (str.length() >= 3) {

            if (new InternetConnectionDetector(this).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_MEDICINES_LIST, MyApplication.HTTPMethod.GET.getValue(), this)
                        .execute(mController.getMedicinesListUrl() + str);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Internet Connection Error!");
                alert.setMessage("No internet connection detected please connect to internet");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsonArraymedicines = new JsonArray();
                        JsonObject jsonValue = new JsonObject();
                        jsonValue.addProperty("id","Others");
                        jsonValue.addProperty("medicineName","Others");
                        jsonArraymedicines.add(jsonValue);
                        setRemaindersAdapter();

                    }
                });
                alert.show();
            }
        }
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

    @Override
    public void onPreExecute() {
        pbProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        if (requestCode == HTTP_REQUEST_CODE_MEDICINES_LIST) {
            try {
                if (responseCode == 200) {
                    JSONObject json = null;
                    jsonArraymedicines = new JsonArray();
                    Log.d("AAA", "response " + response + " response code : " + responseCode);
                    pbProgressBar.setVisibility(View.GONE);
                    medicineList.setVisibility(View.VISIBLE);
                    if (response != null)
                        json = new JSONObject(response);

                    JSONObject jsd = null;
                    try{
                        jsd = json.getJSONObject("data");
                    }catch (Exception e){
                        Log.e("AAA","Exception"+e);
                    }

                    if (jsd != null){
                        Iterator<String> iter = jsd.keys();
                        try {
                            while (iter.hasNext()) {
                                String key = iter.next();

                                String value = String.valueOf(jsd.get(key));

                                JsonObject jsonValue = new JsonObject();
                                jsonValue.addProperty("id",key);
                                jsonValue.addProperty("medicineName",value);
                                jsonArraymedicines.add(jsonValue);
                            }
                        } catch (JSONException e) {
                            // Something went wrong!
                        }finally {
                            JsonObject jsonValue = new JsonObject();
                            jsonValue.addProperty("id","Others");
                            jsonValue.addProperty("medicineName","Others");
                            jsonArraymedicines.add(jsonValue);
                        }
                    }else {
                        jsonArraymedicines = new JsonArray();
                        JsonObject jsonValue = new JsonObject();
                        jsonValue.addProperty("id","Others");
                        jsonValue.addProperty("medicineName","Others");
                        jsonArraymedicines.add(jsonValue);
                    }

                    setRemaindersAdapter();

                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage(new HttpResponseHandler(this, this, layout_search).handle(responseCode, response));
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.show();

                }
            } catch (Exception r) {

            }
        }
    }


    private void setRemaindersAdapter() {
        SelectMedicineListAdapter adapter = new SelectMedicineListAdapter(this, R.layout.inflater_select_medicine, jsonArraymedicines);
        medicineList.setAdapter(adapter);
    }


    @Override
    public void showLoadingActivity() {

    }

    @Override
    public void hideProgressbarWithSuccess() {

    }

    @Override
    public void hideProgressbarWithFailure() {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() >= 3) {
            currentSearchString(newText.replace(" ",""));
        }else{
            jsonArraymedicines = new JsonArray();
            setRemaindersAdapter();
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }


    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }

    @Override
    public void onPositiveAction(int requestCode) {

    }

    @Override
    public void onPositiveAction(int requestCode, String data) {

    }

    @Override
    public void onNegativeAction(int requestCode) {

    }
}
