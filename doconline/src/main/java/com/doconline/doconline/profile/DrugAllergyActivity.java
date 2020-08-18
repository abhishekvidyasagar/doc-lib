package com.doconline.doconline.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

public class DrugAllergyActivity extends BaseActivity implements OnHttpResponse,
        OnLoadingStatusChangedListener,
        OnDialogAction,
        SearchView.OnQueryTextListener,
        View.OnFocusChangeListener,
        SearchView.OnCloseListener,
        TextView.OnEditorActionListener {

    public static final int HTTP_REQUEST_CODE_MEDICINES_LIST = 1;
    public static final int HTTP_REQUEST_CODE_POST_STORE_DRUG_ALLERGY = 2;
    public static final int HTTP_REQUEST_CODE_EDIT_DRUG_ALLERGY = 3;

    Toolbar toolbar;
    TextView toolbar_title;
    ListView listviewMedicines;
    SearchView searchViewMedicines;
    Button buttonAddMedicines;
    LinearLayout llAddButton, llMedicinesList;
    ProgressBar pbProgressBar;

    JsonArray jsonArraymedicines = new JsonArray();

    String searchString = "";
    String actionType = "new";
    String drugName = "";
    String drugId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_allergy);

        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title.setText(getResources().getString(R.string.add_drug_allergy));

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        listviewMedicines = findViewById(R.id.medicines_listview);
        searchViewMedicines = findViewById(R.id.search_medicines);
        buttonAddMedicines = findViewById(R.id.add_medicines_btn);
        llAddButton = findViewById(R.id.add_button_layout);
        llMedicinesList = findViewById(R.id.medicines_list_layout);
        pbProgressBar = findViewById(R.id.progressBar);

        searchViewMedicines.setIconified(true);
        searchViewMedicines.setIconifiedByDefault(false);
        try {
            int searchTextId = searchViewMedicines.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchViewMedicines.findViewById(searchTextId);
            textView.setTextColor(Color.BLACK);
            textView.setHintTextColor(Color.LTGRAY);
            textView.setOnEditorActionListener(this);

            if (getIntent().hasExtra("drugname")){
                actionType = "edit";
                drugName = getIntent().getExtras().get("drugname").toString();
                drugId = getIntent().getExtras().get("drugid").toString();
                textView.setText(drugName);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        searchViewMedicines.setOnQueryTextListener(this);
        searchViewMedicines.setOnQueryTextFocusChangeListener(this);
        searchViewMedicines.setOnCloseListener(this);

        listviewMedicines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JsonObject medicine = jsonArraymedicines.get(position).getAsJsonObject();
                JsonObject requestObject = new JsonObject();
                requestObject.addProperty("name", medicine.get("medicineName").getAsString());
                if (new InternetConnectionDetector(DrugAllergyActivity.this).isConnected()) {
                    if (actionType.equalsIgnoreCase("new")){
                        new HttpClient(HTTP_REQUEST_CODE_POST_STORE_DRUG_ALLERGY, MyApplication.HTTPMethod.POST.getValue(), true,
                                requestObject.toString(), DrugAllergyActivity.this).
                                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getStoreDrugAllergyURL());
                    } else if (actionType.equalsIgnoreCase("edit")) {
                        new HttpClient(HTTP_REQUEST_CODE_EDIT_DRUG_ALLERGY, MyApplication.HTTPMethod.PATCH.getValue(), true,
                                requestObject.toString(), DrugAllergyActivity.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getDrugAlleryEditURL() + drugId);
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(DrugAllergyActivity.this);
                    alert.setTitle("Internet Connection Error!");
                    alert.setMessage("No internet connection detected please connect to internet");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
                    alert.show();
                }

            }
        });
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchString = newText;
        if (newText.length() >= 3) {
            currentSearchString(newText.replace(" ", ""));
        } else {
            jsonArraymedicines = new JsonArray();
            setMedicinesAdapter();
        }
        return false;
    }

    public void currentSearchString(String str) {
        if (str.length() >= 3) {

            if (new InternetConnectionDetector(this).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_MEDICINES_LIST, MyApplication.HTTPMethod.GET.getValue(), this)
                        .execute(mController.getMedicinesListUrl() + str);
            } else {
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
                        jsonValue.addProperty("id", "Others");
                        jsonValue.addProperty("medicineName", "Others");
                        jsonArraymedicines.add(jsonValue);
                        setMedicinesAdapter();
                    }
                });
                alert.show();
            }
        }
    }

    private void setMedicinesAdapter() {
        SelectMedicineListAdapter adapter = new SelectMedicineListAdapter(this, R.layout.inflater_select_medicine, jsonArraymedicines);
        listviewMedicines.setAdapter(adapter);
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
                    pbProgressBar.setVisibility(View.GONE);
                    listviewMedicines.setVisibility(View.VISIBLE);
                    if (response != null)
                        json = new JSONObject(response);

                    JSONObject jsd = null;
                    try {
                        jsd = json.getJSONObject("data");
                    } catch (Exception e) {
                        Log.e("AAA", "Exception" + e);
                    }

                    if (jsd != null) {
                        llAddButton.setVisibility(View.GONE);
                        llMedicinesList.setVisibility(View.VISIBLE);
                        Iterator<String> iter = jsd.keys();
                        try {
                            while (iter.hasNext()) {
                                String key = iter.next();

                                String value = String.valueOf(jsd.get(key));

                                JsonObject jsonValue = new JsonObject();
                                jsonValue.addProperty("id", key);
                                jsonValue.addProperty("medicineName", value);
                                jsonArraymedicines.add(jsonValue);
                            }
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    } else {
                        llAddButton.setVisibility(View.VISIBLE);
                        llMedicinesList.setVisibility(View.GONE);
                    }

                    setMedicinesAdapter();

                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage(new HttpResponseHandler(this, this, llMedicinesList).handle(responseCode, response));
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

        if (requestCode == HTTP_REQUEST_CODE_POST_STORE_DRUG_ALLERGY) {
            JSONObject responseObj = null;
            try {
                responseObj = new JSONObject(response);
                if (responseCode == 201 && responseObj.getString("status").equalsIgnoreCase("success")) {
                    finish();
                } else {
                    Toast.makeText(this, "response code is " + responseCode + " Please try again later ", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == HTTP_REQUEST_CODE_EDIT_DRUG_ALLERGY){
            Log.e("AAA","response for edit functionality : "+response);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

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

    public void addMedicinesManually(View view) {
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("name", searchString);
        if (!searchString.equalsIgnoreCase("")){
            if (searchString.length() > 2){
                if (new InternetConnectionDetector(DrugAllergyActivity.this).isConnected()) {
                    new HttpClient(HTTP_REQUEST_CODE_POST_STORE_DRUG_ALLERGY, MyApplication.HTTPMethod.POST.getValue(), true,
                            requestObject.toString(), DrugAllergyActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getStoreDrugAllergyURL());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(DrugAllergyActivity.this);
                    alert.setTitle("Internet Connection Error!");
                    alert.setMessage("No internet connection detected please connect to internet");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
                    alert.show();
                }
            }else {
                Toast.makeText(this, "Minimum 3 characters to add", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Please enter a value to add", Toast.LENGTH_SHORT).show();
        }
    }
}
