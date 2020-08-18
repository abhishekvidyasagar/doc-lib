package com.doconline.doconline.HRA;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.SpinnerAdapter;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.HealthProfile;
import com.doconline.doconline.model.Patient;
import com.doconline.doconline.profile.FamilyMemberActivity;
import com.google.gson.JsonObject;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.FAMILY_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.KEY_AGE;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DETAILS;
import static com.doconline.doconline.app.Constants.KEY_DOB;
import static com.doconline.doconline.app.Constants.KEY_GENDER;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;

public class HRAMainActivity extends BaseActivity implements View.OnClickListener {

    TextView toolbar_title;
    Toolbar toolbar;
    final int HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS = 1;
    final int HTTP_REQUEST_CODE_FIELD_VALIDATIONS = 2;

    public MyApplication mController;

    private List<Patient> mFamilyList = new ArrayList<>();
    private SpinnerAdapter mFamilyAdapter;
    Spinner spinner_appointment_for_who_diag;


    EditText etAge, etHeight, etWeight, etWaistCircumference, etSystolic, etDiastolic, etCigaresttesperDay, etCaloriesIntake;
    TextView spGender, spBloodPressure, spDiabetes, spHighBloodPressure, spHypertension, spHistoryCardioVascularD,
            sphistoryAtrialFibrillation, spVentricularHypertrophy, spParentDiabetes, spParentHypertension,
            spParentCardiac, spSmoking, spPhysicalActivity;

    LinearLayout llSystolicDiastolic, llTreatmentforhypertension;

    JSONArray spinnerYesNoOptions = new JSONArray();
    JSONArray spinnerParentialOptions = new JSONArray();
    int UserId = 0;

    int minHeight = 121, maxHeight = 275,
            minWeight = 30, maxWeight = 500,
            minWaist = 10, maxWaist = 999,
            minSys = 50, maxSys = 300,
            minDias = 30, maxDias = 300,
            minCigarettes = 1, maxCigarettes = 200,
            minCalories = 1000, maxCalories = 5000;

    ProgressDialog progressDialog;

    LinearLayout footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hramain);

        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title.setText(getResources().getString(R.string.text_hra));

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mController = MyApplication.getInstance();

        spinner_appointment_for_who_diag = findViewById(R.id.spinner_appointment_for_who_diag);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please Wait. \n " +
                "Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (new InternetConnectionDetector(this).isConnected()) {
            //validations api
            new HttpClient(HTTP_REQUEST_CODE_FIELD_VALIDATIONS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFieldValidationsURL());
        }

        doInitialise();

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // write your code
                        if (isOpen) {
                            footer.setVisibility(View.GONE);
                        } else {
                            footer.setVisibility(View.VISIBLE);
                        }

                    }
                });

        setUserName();

        if (new InternetConnectionDetector(this).isConnected()) {
            //family members api
            new HttpClient(HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getFamilyURL());
        }

        setArrays();

    }

    private void setArrays() {
        JSONObject yesObject = new JSONObject();
        JSONObject noObject = new JSONObject();
        try {
            yesObject.put("id", "1");
            yesObject.put("value", "Yes");
            spinnerYesNoOptions.put(yesObject);
            noObject.put("id", "2");
            noObject.put("value", "No");
            spinnerYesNoOptions.put(noObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject oneParentObject = new JSONObject();
        JSONObject bothParentObject = new JSONObject();
        JSONObject noneParentObject = new JSONObject();
        try {
            oneParentObject.put("id", "1");
            oneParentObject.put("value", "One Parent");
            spinnerParentialOptions.put(oneParentObject);
            bothParentObject.put("id", "2");
            bothParentObject.put("value", "Both Parents");
            spinnerParentialOptions.put(bothParentObject);
            noneParentObject.put("id", "3");
            noneParentObject.put("value", "None");
            spinnerParentialOptions.put(noneParentObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doInitialise() {
        etAge = findViewById(R.id.age_et);
        etHeight = findViewById(R.id.height_et);
        etWeight = findViewById(R.id.weight_et);
        etWaistCircumference = findViewById(R.id.waistcircum_et);
        etSystolic = findViewById(R.id.systolic_et);
        etDiastolic = findViewById(R.id.diastolic_et);
        etCigaresttesperDay = findViewById(R.id.cigarettesperday_et);
        etCaloriesIntake = findViewById(R.id.caloriesinteake_et);
        spGender = findViewById(R.id.spinner_gender);
        spGender.setOnClickListener(this);
        spBloodPressure = findViewById(R.id.spinner_bp);
        spBloodPressure.setOnClickListener(this);
        spDiabetes = findViewById(R.id.spinner_diabetes);
        spDiabetes.setOnClickListener(this);
        spHighBloodPressure = findViewById(R.id.spinner_highbp);
        spHighBloodPressure.setOnClickListener(this);
        spHypertension = findViewById(R.id.spinner_treatmentbp);
        spHypertension.setOnClickListener(this);
        spHistoryCardioVascularD = findViewById(R.id.spinner_history_cardiovascular);
        spHistoryCardioVascularD.setOnClickListener(this);
        sphistoryAtrialFibrillation = findViewById(R.id.spinner_atrial_fibrillation);
        sphistoryAtrialFibrillation.setOnClickListener(this);
        spVentricularHypertrophy = findViewById(R.id.spinner_ventricular_hypertrophy);
        spVentricularHypertrophy.setOnClickListener(this);
        spParentDiabetes = findViewById(R.id.spinner_parent_diabetes);
        spParentDiabetes.setOnClickListener(this);
        spParentHypertension = findViewById(R.id.spinner_parentbp);
        spParentHypertension.setOnClickListener(this);
        spParentCardiac = findViewById(R.id.spinner_parentcardiac);
        spParentCardiac.setOnClickListener(this);
        spSmoking = findViewById(R.id.spinner_smoke);
        spSmoking.setOnClickListener(this);
        spPhysicalActivity = findViewById(R.id.spinner_physicalactivity);
        spPhysicalActivity.setOnClickListener(this);

        llSystolicDiastolic = findViewById(R.id.sysdias_ll);
        llTreatmentforhypertension = findViewById(R.id.hypertensiontreatment_ll);

        footer = findViewById(R.id.footer);
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

    private void setUserName() {
        this.mFamilyList.add(new Patient(MyApplication.getInstance().getSession().getUserId(),
                Helper.toCamelCase(MyApplication.getInstance().getSession().getFullName()) + " (Myself)",
                MyApplication.getInstance().getSession().getUserGender(),
                MyApplication.getInstance().getSession().getUserDOB(),
                MyApplication.getInstance().getSession().getUserAge(),
                "",
                mController.getSession().getAvatarLink()));

        this.mFamilyAdapter = new SpinnerAdapter(this, this.mFamilyList, 1, "HRA");
        spinner_appointment_for_who_diag.setAdapter(this.mFamilyAdapter);
        spinner_appointment_for_who_diag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                clearFields();

                if (i == (mFamilyList.size() - 1) && mFamilyAdapter.getItem(i) == null) {
                    spinner_appointment_for_who_diag.setSelection(0);

                    Intent intent = new Intent(HRAMainActivity.this, FamilyMemberActivity.class);
                    startActivityForResult(intent, FAMILY_REQUEST_CODE);
                } else {
                    Patient patient = mFamilyList.get(i);
                    etAge.setText("" + patient.getAge().replace(" Years", ""));
                    spGender.setText("" + patient.getGender());
                    UserId = patient.getUserId();

                    if (Integer.parseInt(etAge.getText().toString()) < 20) {
                        showAgeRistrictionAlert();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void clearFields() {
        spBloodPressure.setText("");
        spDiabetes.setText("");
        spHighBloodPressure.setText("");
        spHypertension.setText("");

        spHistoryCardioVascularD.setText("");
        sphistoryAtrialFibrillation.setText("");
        spVentricularHypertrophy.setText("");

        spParentDiabetes.setText("");
        spParentHypertension.setText("");
        spParentCardiac.setText("");
        spSmoking.setText("");
        spPhysicalActivity.setText("");
        spPhysicalActivity.setTag(0);

        etWeight.getText().clear();
        etWaistCircumference.getText().clear();
        etSystolic.getText().clear();
        etDiastolic.getText().clear();
        etCigaresttesperDay.getText().clear();
        etCaloriesIntake.getText().clear();
        etHeight.getText().clear();

    }

    private void showAgeRistrictionAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(HRAMainActivity.this);
        alert.setTitle("Age");
        alert.setMessage("For calculating Health Risk Assessment(HRA) the minimum age must be 20 years");
        alert.setCancelable(false);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    /*Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    overridePendingTransition(0,0);*/


            }
        });
        alert.show();
    }

    public void onPostExecute(int requestCode, int responseCode, String response) {
        try {
            JSONObject json = new JSONObject(response);
            String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);

            if (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED) {
                switch (requestCode) {

                    case HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS:
                        try {
                            JSONObject jsonnn = new JSONObject(response);
                            Log.e("AAA", "Family member response : " + jsonnn);
                            this.familyMemberSpinner(jsonnn.getJSONArray(KEY_DATA));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;

                    case HTTP_REQUEST_CODE_FIELD_VALIDATIONS:
                        Log.e("AAA", "VALIDATIONS RESPONSE : " + response);
                        JSONObject totalResponse = new JSONObject(response);
                        JSONObject responseObject = totalResponse.getJSONObject(Constants.KEY_DATA);
                        minHeight = responseObject.getInt("height_min");
                        maxHeight = responseObject.getInt("height_max");
                        minWeight = responseObject.getInt("weight_min");
                        maxWeight = responseObject.getInt("weight_max");
                        minWaist = responseObject.getInt("waist_min");
                        maxWaist = responseObject.getInt("waist_max");
                        minSys = responseObject.getInt("sbp_min");
                        maxSys = responseObject.getInt("sbp_max");
                        minDias = responseObject.getInt("dbp_min");
                        maxDias = responseObject.getInt("dbp_max");
                        minCigarettes = responseObject.getInt("cigarettes_min");
                        maxCigarettes = responseObject.getInt("cigarettes_max");
                        minCalories = responseObject.getInt("calories_min");
                        maxCalories = responseObject.getInt("calories_max");

                        break;

                }
                progressDialog.dismiss();
            } else if (responseCode == HttpClient.UNPROCESSABLE_ENTITY) {
                Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            progressDialog.dismiss();
        }
    }

    private void familyMemberSpinner(JSONArray array) {
        int count = 1;

        try {
            this.mFamilyList.clear();

            this.mFamilyList.add(new Patient(MyApplication.getInstance().getSession().getUserId(),
                    Helper.toCamelCase(MyApplication.getInstance().getSession().getFullName()) + " (Myself)",
                    MyApplication.getInstance().getSession().getUserGender(),
                    MyApplication.getInstance().getSession().getUserDOB(),
                    MyApplication.getInstance().getSession().getUserAge(),
                    "",
                    mController.getSession().getAvatarLink()));

            for (int i = 0; i < array.length(); i++) {
                count++;
                JSONObject json = array.getJSONObject(i);
                int user_id = json.getJSONObject("pivot").getInt(com.doconline.doconline.app.Constants.KEY_USER_ID);
                String full_name = json.getString(com.doconline.doconline.app.Constants.KEY_FULL_NAME);
                String dob = json.isNull(KEY_DOB) ? "" : json.getString(KEY_DOB);
                String gender = json.isNull(KEY_GENDER) ? "" : json.getString(KEY_GENDER);
                JSONObject detailsObj = json.isNull(KEY_DETAILS) ? new JSONObject() : json.getJSONObject(KEY_DETAILS);
                String age = detailsObj.isNull(KEY_AGE) ? "" : detailsObj.getString(KEY_AGE);
                this.mFamilyList.add(new Patient(user_id,
                        full_name,
                        gender,
                        dob,
                        age,
                        "",
                        ""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (mFamilyAdapter != null) {
                this.mFamilyAdapter.setItems(count);
            }
        }
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.spinner_gender) {
            JSONArray genderArray = new JSONArray();
            JSONObject maleObject = new JSONObject();
            JSONObject femaleObject = new JSONObject();
            try {
                maleObject.put("id", "1");
                maleObject.put("value", "Male");
                genderArray.put(maleObject);
                femaleObject.put("id", "2");
                femaleObject.put("value", "Female");
                genderArray.put(femaleObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showList(genderArray, spGender, "Select Gender ");
        } else if (id == R.id.spinner_bp) {
            showList(spinnerYesNoOptions, spBloodPressure, "Do you have recent Blood Pressure readings?");
        } else if (id == R.id.spinner_diabetes) {
            showList(spinnerYesNoOptions, spDiabetes, "Diagnosed with Diabetes?");
        } else if (id == R.id.spinner_highbp) {
            showList(spinnerYesNoOptions, spHighBloodPressure, "Have you diagnosed with High BP?");
        } else if (id == R.id.spinner_treatmentbp) {
            showList(spinnerYesNoOptions, spHypertension, "Treatment for Hypertension?");
        } else if (id == R.id.spinner_history_cardiovascular) {
            showList(spinnerYesNoOptions, spHistoryCardioVascularD, "History of Cardiovascular Disease?");
        } else if (id == R.id.spinner_atrial_fibrillation) {
            showList(spinnerYesNoOptions, sphistoryAtrialFibrillation, "History of Atrial Fibrillation");
        } else if (id == R.id.spinner_ventricular_hypertrophy) {
            showList(spinnerYesNoOptions, spVentricularHypertrophy, "History of Ventricular Hypertrophy ");
        } else if (id == R.id.spinner_parent_diabetes) {
            showList(spinnerParentialOptions, spParentDiabetes, "Family history of Diabetes? ");
        } else if (id == R.id.spinner_parentbp) {
            showList(spinnerParentialOptions, spParentHypertension, "Family history of Hypertension ");
        } else if (id == R.id.spinner_parentcardiac) {
            showList(spinnerParentialOptions, spParentCardiac, "Family history of Cardiac problem ");
        } else if (id == R.id.spinner_smoke) {
            showList(spinnerYesNoOptions, spSmoking, "Do you currently smoke? ");
        } else if (id == R.id.spinner_physicalactivity) {
            JSONArray paArray = new JSONArray();
            JSONObject sedentaryObject = new JSONObject();
            JSONObject mildexerciseObject = new JSONObject();
            JSONObject moderateexerciseObject = new JSONObject();
            JSONObject vigourousexerciseObject = new JSONObject();
            try {
                sedentaryObject.put("id", "1");
                sedentaryObject.put("value", "Sedentary Lifestyle");
                paArray.put(sedentaryObject);

                mildexerciseObject.put("id", "2");
                mildexerciseObject.put("value", "Mild Exercise");
                paArray.put(mildexerciseObject);

                moderateexerciseObject.put("id", "3");
                moderateexerciseObject.put("value", "Moderate Exercise");
                paArray.put(moderateexerciseObject);

                vigourousexerciseObject.put("id", "4");
                vigourousexerciseObject.put("value", "Vigorous Exercise");
                paArray.put(vigourousexerciseObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            showList(paArray, spPhysicalActivity, "Select Physical Activity type ");
        }
    }

    private void showList(JSONArray optionsArrayObject, final TextView spinnerView, String headerText) {
        final CharSequence[] items = new String[optionsArrayObject.length()];
        final CharSequence[] itemsids = new String[optionsArrayObject.length()];

        for (int i = 0; i < optionsArrayObject.length(); i++) {
            JSONObject model = null;
            try {
                model = optionsArrayObject.getJSONObject(i);
                items[i] = model.get("value").toString();
                itemsids[i] = model.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setTitle("" + headerText);
        builder1.setCancelable(false);
        builder1.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int item) {
                spinnerView.setText(items[item]);
                spinnerView.setTag(itemsids[item]);
                checkSpinnerValidations(spinnerView, "" + items[item], "" + itemsids[item]);
                spinnerView.setError(null);
            }
        });
        android.app.AlertDialog selectionDialog = builder1.create();
        selectionDialog.setCancelable(true);
        if (!selectionDialog.isShowing()) {
            selectionDialog.show();
        }

    }

    private void checkSpinnerValidations(TextView spinnerView, String value, String id) {
        if (spinnerView == spBloodPressure) {
            if (value.equalsIgnoreCase("Yes")) {
                llSystolicDiastolic.setVisibility(View.VISIBLE);
            } else {
                llSystolicDiastolic.setVisibility(View.GONE);
                etSystolic.setText("");
                etDiastolic.setText("");
            }
        }

        if (spinnerView == spHypertension) {
            if (value.equalsIgnoreCase("Yes")) {
                llTreatmentforhypertension.setVisibility(View.VISIBLE);
            } else {
                llTreatmentforhypertension.setVisibility(View.GONE);
                spHistoryCardioVascularD.setText("");
                sphistoryAtrialFibrillation.setText("");
                spVentricularHypertrophy.setText("");
            }
        }

        if (spinnerView == spSmoking) {
            if (value.equalsIgnoreCase("Yes")) {
                etCigaresttesperDay.setVisibility(View.VISIBLE);
            } else {
                etCigaresttesperDay.setVisibility(View.GONE);
                etCigaresttesperDay.setText("");
            }
        }
    }

    public void doCalculations(View view) {
        if (Integer.parseInt(etAge.getText().toString()) < 20) {
            showAgeRistrictionAlert();
        } else {
            if (checkAllValidations()) {
                try {
                    doCalculationsandStartNewIntent();
                } catch (Exception e) {
                    Toast.makeText(this, "something went wrong try again", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkAllValidations() {
        Boolean checkHeight = checkETS(etHeight, minHeight, maxHeight, "Height");
        Boolean checkWeight = checkETS(etWeight, minWeight, maxWeight, "Weight");
        Boolean checkWaist = checkETS(etWaistCircumference, minWaist, maxWaist, "Waist Circumference");
        Boolean checkBloodPressure = checkBloodPressureWithSysandDiasAll();
        Boolean checkDiabetes = checkSPS(spDiabetes);
        Boolean checkHighBloodPressure = checkSPS(spHighBloodPressure);
        Boolean checkTreatmentHyTnsn = checkTrtmntHyprTnsnAll();
        Boolean checkParentDiabetes = checkSPS(spParentDiabetes);
        Boolean checkParentHypertension = checkSPS(spParentHypertension);
        Boolean checkParentCardiac = checkSPS(spParentCardiac);
        Boolean checkSmoking = checkSmokingAll();
        Boolean checkPhysicalActivity = checkSPS(spPhysicalActivity);
        Boolean checkCalorieIntake = checkETS(etCaloriesIntake, minCalories, maxCalories, "Calories");

        return checkHeight && checkWeight && checkWaist &&
                checkBloodPressure &&
                checkHighBloodPressure && checkTreatmentHyTnsn &&
                checkDiabetes && checkParentDiabetes && checkParentHypertension && checkParentCardiac &&
                checkSmoking && checkPhysicalActivity && checkCalorieIntake;
    }

    @NonNull
    private Boolean checkSmokingAll() {
        if (!spSmoking.getText().toString().equalsIgnoreCase("")) {
            if (spSmoking.getText().toString().equalsIgnoreCase("Yes")) {
                spSmoking.setError(null);
                Boolean checkCigrPerDay = checkETS(etCigaresttesperDay, minCigarettes, maxCigarettes, "Cigarettes");
                return checkCigrPerDay;
            } else {
                spSmoking.setError(null);
                return true;
            }
        }
        spSmoking.setError("Mandatory");
        return false;
    }

    private Boolean checkTrtmntHyprTnsnAll() {
        if (!spHypertension.getText().toString().equalsIgnoreCase("")) {
            if (spHypertension.getText().toString().equalsIgnoreCase("Yes")) {
                spHypertension.setError(null);
                Boolean checkCVD = checkSPS(spHistoryCardioVascularD);
                Boolean checkAF = checkSPS(sphistoryAtrialFibrillation);
                Boolean checkVHT = checkSPS(spVentricularHypertrophy);
                return checkCVD && checkAF && checkVHT;
            } else {
                spHypertension.setError(null);
                return true;
            }
        }
        spHypertension.setError("Mandatory");
        return false;
    }

    private Boolean checkSPS(TextView sp) {
        if (!sp.getText().toString().equalsIgnoreCase("")) {
            sp.setError(null);
            return true;
        }
        sp.setError("Mandatory");
        return false;
    }

    private Boolean checkBloodPressureWithSysandDiasAll() {
        if (!spBloodPressure.getText().toString().equalsIgnoreCase("")) {
            if (spBloodPressure.getText().toString().equalsIgnoreCase("Yes")) {
                spBloodPressure.setError(null);
                Boolean checkSys = checkETS(etSystolic, minSys, maxSys, "Systolic");
                Boolean checkDias = checkETS(etDiastolic, minDias, maxDias, "Diastolic");
                return checkSys && checkDias;
            } else {
                spBloodPressure.setError(null);
                return true;
            }
        }
        spBloodPressure.setError("Mandatory");
        return false;

    }

    public boolean checkETS(EditText et, int minValue, int maxValue, String valueName) {
        String strVal = et.getText().toString();
        if (!strVal.equalsIgnoreCase("")) {
            int intVal = Integer.parseInt(strVal);
            if (intVal < minValue || intVal > maxValue) {
                et.setError(valueName + " range should be in between " + minValue + " and " + maxValue);
                return false;
            }
        } else {
            et.setError("Mandatory");
            return false;
        }

        return true;
    }

    private void doCalculationsandStartNewIntent() {
        double age = Double.parseDouble(etAge.getText().toString());
        String gender = spGender.getText().toString();
        double height = Double.parseDouble(etHeight.getText().toString());
        double weight = Double.parseDouble(etWeight.getText().toString());
        double waist = Double.parseDouble(etWaistCircumference.getText().toString());
        String bloodPressure = spBloodPressure.getText().toString();
        double systolic = etSystolic.getText().toString().trim().isEmpty() ? 0.0 : Double.parseDouble(etSystolic.getText().toString());
        double diastolic = etDiastolic.getText().toString().trim().isEmpty() ? 0.0 : Double.parseDouble(etDiastolic.getText().toString());
        String diabetes = spDiabetes.getText().toString();
        String highBloodPressure = spHighBloodPressure.getText().toString();
        String treatmentHypertension = spHypertension.getText().toString();
        String historyCardioVascularDisease = spHistoryCardioVascularD.getText().toString().trim().isEmpty() ? "" : spHistoryCardioVascularD.getText().toString();
        String historyAtrilFribrillation = sphistoryAtrialFibrillation.getText().toString().trim().isEmpty() ? "" : sphistoryAtrialFibrillation.getText().toString();
        String historyVentricularHypertrophy = spVentricularHypertrophy.getText().toString().trim().isEmpty() ? "" : spVentricularHypertrophy.getText().toString();
        String parentalDiabetes = spParentDiabetes.getText().toString();
        String parentialHypertension = spParentHypertension.getText().toString();
        String parentCardiac = spParentCardiac.getText().toString();
        String smoke = spSmoking.getText().toString();
        double cigarettesPerDay = etCigaresttesperDay.getText().toString().trim().isEmpty() ? 0 : Double.parseDouble(etCigaresttesperDay.getText().toString());
        String exerciselevel = spPhysicalActivity.getText().toString();
        double caloriesIntake = Double.parseDouble(etCaloriesIntake.getText().toString());

        //BMI calculations
        JsonObject BMIObject = calculateBMI(height, weight);
        HRAResultsActivity.BMIObject = BMIObject;
        float BMI = BMIObject.get(Constants.BMI_VALUE).getAsFloat();

        //Ideal Body Weight
        HRAResultsActivity.IdealBodyWeightObject = calculateIdealBodyWeight(height);

        //Daily calory requirement
        HRAResultsActivity.ReqiredCaloriesObject = calculateDailyCalorieRequirement(height, weight, age, caloriesIntake);

        //diabetes risk
        HRAResultsActivity.DiabetesObject = calculateDiabetisRisk(age, waist, gender, exerciselevel, parentalDiabetes);

        //hypertension
        if (bloodPressure.equalsIgnoreCase("Yes")) {
            String noOptimal = "No";
            String yesOptimal = "Yes";
            HRAResultsActivity.HypertensionRiskObject = calculateHypertensionRisk(age, gender, systolic, diastolic, smoke, parentialHypertension, noOptimal, BMI, bloodPressure);
            HRAResultsActivity.HypertensionOptimalObject = calculateHypertensionRisk(age, gender, 110.0, 70.0, "No", "None", yesOptimal, BMI, bloodPressure);
        }

        //cardio vascular disease
        if (bloodPressure.equalsIgnoreCase("Yes")) {
            HRAResultsActivity.CVDYourRiskObject = calculateCardiovascularDisease(age, gender, systolic, smoke, treatmentHypertension, diabetes, "No", BMI);
            HRAResultsActivity.CVDOptimalObject = calculateCardiovascularDisease(age, gender, 110.0, "No", "No", "No", "Yes", BMI);
            HRAResultsActivity.CVDNormalObject = calculateCardiovascularDisease(age, gender, 125.0, "No", "No", "No", "Normal", BMI);
        }

        //storke
        HRAResultsActivity.StrokeObject = calculateStroke(age, gender, systolic, smoke, treatmentHypertension, diabetes,
                historyCardioVascularDisease, historyAtrilFribrillation, historyVentricularHypertrophy, bloodPressure);

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put(Constants.KEY_USER_ID_HRA, UserId);
            requestObject.put(Constants.KEY_HEIGHT_HRA, (int) height);
            requestObject.put(Constants.KEY_WEIGHT_HRA, (int) weight);
            requestObject.put(Constants.KEY_WAIST, (int) waist);
            requestObject.put(Constants.KEY_KNOW_BLOOD_PRESSURE, getIDfromKey(bloodPressure));
            requestObject.put(Constants.KEY_SYSTOLIC, (int) systolic);
            requestObject.put(Constants.KEY_DIASTOLIC, (int) diastolic);
            requestObject.put(Constants.KEY_DIABETES, getIDfromKey(diabetes));
            requestObject.put(Constants.KEY_HIGH_BLOOD_PRESSURE, getIDfromKey(highBloodPressure));
            requestObject.put(Constants.KEY_HYPERTENSION, getIDfromKey(treatmentHypertension));
            requestObject.put(Constants.KEY_CVD, getIDfromKey(historyCardioVascularDisease));
            requestObject.put(Constants.KEY_AF, getIDfromKey(historyAtrilFribrillation));
            requestObject.put(Constants.KEY_VHT, getIDfromKey(historyVentricularHypertrophy));
            requestObject.put(Constants.KEY_PARENT_DIABETIC, getIDfromKey(parentalDiabetes));
            requestObject.put(Constants.KEY_PARENT_HBP, getIDfromKey(parentialHypertension));
            requestObject.put(Constants.KEY_PARENT_CARDIAC, getIDfromKey(parentCardiac));
            requestObject.put(Constants.KEY_SMOKING, getIDfromKey(smoke));
            requestObject.put(Constants.KEY_CIGARETES_PER_DAY, (int) cigarettesPerDay);
            requestObject.put(Constants.KEY_PHYSICAL_ACTIVITY, spPhysicalActivity.getTag());
            requestObject.put(Constants.KEY_CALORIES_INTAKE, (int) caloriesIntake);
            requestObject.put(Constants.KEY_BMI, String.format("%.4f", BMI));

            HRAResultsActivity.requestObject = requestObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(HRAMainActivity.this, HRAResultsActivity.class);
        i.putExtra(Constants.STATUS_BLOOD_PRESSURE, spBloodPressure.getText().toString());
        i.putExtra(Constants.KEY_AGE, age);
        startActivity(i);
    }

    private int getIDfromKey(String bloodPressure) {
        if (bloodPressure.equalsIgnoreCase("Yes")) {
            return 1;
        }
        return 0;
    }

    public JsonObject calculateStroke(double age, String gender, double systoli, String smoke,
                                      String treatmentHypertension, String sugar, String historyCardioVascularDisease,
                                      String historyAtrilFribrillation, String historyVentricularHypertrophy, String bloodPressure) {
        boolean doCalculateStroke = true;
        int systolic = (int) systoli;
        JsonObject strokeObject = new JsonObject();
        double ageValue = 0.0;
        double maxAge = gender.equals("Male") ? 85.0 : 84.0;
        if ((age > 54.0 || age == 54.0) && (age < maxAge || age == maxAge)) {
            ageValue = Double.parseDouble(gender.equals("Male") ? StrokeChart.dictMenAge().get("" + (int) age) : StrokeChart.dictWomenAge().get("" + (int) age));
        } else {
            doCalculateStroke = false;
        }
        double trtValue = 0.0;
        double sbpValue = bloodPressure.equalsIgnoreCase("Yes") ? systolic : 0.0;
        if ((sbpValue > 97.0 || sbpValue == 97.0) && (sbpValue < 205 || sbpValue == 205) && gender.equalsIgnoreCase("Male")) {
            trtValue = Double.parseDouble(treatmentHypertension.equalsIgnoreCase("Yes") ?
                    StrokeChart.dictMenTreated().get(systolic + "") : StrokeChart.dictMenUnTreated().get(systolic + ""));
        } else if ((sbpValue > 95.0 || sbpValue == 95.0) && (sbpValue < 216 || sbpValue == 216) && gender.equalsIgnoreCase("Female")) {
            trtValue = Double.parseDouble(treatmentHypertension.equalsIgnoreCase("Yes") ?
                    StrokeChart.dictWomenTreated().get(systolic + "") : StrokeChart.dictWomenUnTreated().get(systolic + ""));
        }

        double sugarValue = (sugar.equalsIgnoreCase("Yes") ? (gender.equalsIgnoreCase("Male") ? 2.0 : 3.0) : 0);
        double smokeValue = (smoke.equalsIgnoreCase("Yes") ? 3.0 : 0);
        double cvdValue = (historyCardioVascularDisease.equalsIgnoreCase("Yes") ? (gender.equalsIgnoreCase("Male") ? 4.0 : 2.0) : 0);
        double afValue = (historyAtrilFribrillation.equalsIgnoreCase("Yes") ? (gender.equalsIgnoreCase("Male") ? 4.0 : 6.0) : 0);
        double vhValue = (historyVentricularHypertrophy.equalsIgnoreCase("Yes") ? (gender.equalsIgnoreCase("Male") ? 5.0 : 9.0) : 0);
        double sumTotal = ageValue + trtValue + sugarValue + smokeValue + cvdValue + afValue + vhValue;
        //print("sumTotal of stroke ::=> ",String(format:"%.2f", sumTotal))
        //let intSum = Int(sumTotal)
        int intSum = (int) sumTotal;
        String strResults = "";
        if ((intSum > 30 && gender.equalsIgnoreCase("Male")) || (intSum > 27 && gender.equalsIgnoreCase("Female"))) {
            strResults = (gender.equalsIgnoreCase("Male") ? "88" : "84");
        } else if (intSum != 0) {
            strResults = (gender.equalsIgnoreCase("Male") ?
                    StrokeChart.dictMenStrokeMap().get("" + intSum) : StrokeChart.dictWomenStroke().get("" + intSum));
        } else {
            strResults = "0";
        }

        if (strResults == null) {
            strResults = "0";
        }

        strokeObject.addProperty(Constants.STROKE_SCORE, strResults);
        strokeObject.addProperty(Constants.STROKE_POINTS, intSum);
        strokeObject.addProperty(Constants.STROKE_CAL_STATUS, doCalculateStroke);
        return strokeObject;
    }

    public JsonObject calculateCardiovascularDisease(double age, String gender, double sbp, String smoke, String hyperTRT, String sugar, String isOptimal, double bmi) {
        //1-0.94833exp(ΣßX – 26.0145) ---> Female
        //1-0.88431exp(ΣßX – 23.9388) ---> Male
        JsonObject jso = new JsonObject();
        double ageBeta = (gender.equalsIgnoreCase("Male") ? 3.11296 : 2.72107) * log(age); //1
        double sbpBeta = log(sbp) * (hyperTRT.equalsIgnoreCase("Yes") ?
                (gender.equalsIgnoreCase("Male") ? 1.92672 : 2.88267) : (gender.equalsIgnoreCase("Male") ? 1.85508 : 2.81291)); //2
        double smokeBeta = (smoke.equalsIgnoreCase("Yes") ? 1 : 0) * (gender.equalsIgnoreCase("Male") ? 0.70953 : 0.61868); //3
        double bmiBeta = log((isOptimal.equalsIgnoreCase("No") ? bmi : (isOptimal.equalsIgnoreCase("Normal") ? 22.5 : 22.0))) * (gender.equalsIgnoreCase("Male") ? 0.79277 : 0.51125); //4
        double sugarBeta = (sugar.equalsIgnoreCase("Yes") ? 1 : 0) * (gender.equalsIgnoreCase("Male") ? 0.5316 : 0.77763); //5
        double sumOfBeta = ageBeta + sbpBeta + smokeBeta + bmiBeta + sugarBeta - (gender.equalsIgnoreCase("Male") ? 23.9388 : 26.0145);
        double doubleResults = 1 - pow((gender.equalsIgnoreCase("Male") ? 0.88431 : 0.94833), exp(sumOfBeta));
        if (isOptimal.equalsIgnoreCase("No")) {
            double sbpHeartBeta = log(sbp) * (gender.equalsIgnoreCase("Male") ? 1.85508 : 2.81291);
            double sumOfHeartBeta = sbpHeartBeta + smokeBeta + bmiBeta + sugarBeta - (gender.equalsIgnoreCase("Male") ? 23.9388 : 26.0145);
            double consti_num = exp(-(sumOfHeartBeta) / (gender.equalsIgnoreCase("Male") ? 3.11296 : 2.72107));
            double consti_denom = pow(-(log(gender.equalsIgnoreCase("Male") ? 0.88431 : 0.94833)), 1 / (gender.equalsIgnoreCase("Male") ? 3.11296 : 2.72107));
            double consti = consti_num * (1 / consti_denom);
            double expo = 1 * (1 / (gender.equalsIgnoreCase("Male") ? 3.11296 : 2.72107));
            double term = pow(-(log(1 - doubleResults)), expo);
            double heartAge = consti * term;

            jso.addProperty(Constants.CVD_YOUR_RISK, doubleResults);
            jso.addProperty(Constants.CVD_YOUR_HEART_AGE, heartAge);
            return jso;
        }

        jso.addProperty(Constants.CVD_YOUR_RISK, doubleResults);
        jso.addProperty(Constants.CVD_YOUR_HEART_AGE, 0.0);
        return jso;
    }

    private JsonObject calculateHypertensionRisk(double age, String gender, double sbp, double dbp,
                                                 String smoke, String parentalHyper, String isOptimal,
                                                 float bmi, String bloodPressure) {
        JsonObject jso = new JsonObject();
        try {
            //1 – exp[ – exp((ln(4) – [22.94954 + ∑ Xß])/0.87692)
            double interceptBeta = 22.949536; //1
            double ageBeta = -0.156412 * age; //2
            double genderBeta = gender.equalsIgnoreCase("Male") ? 0.0 : (1.0 * -0.202933); //3
            double bmiBeta = (isOptimal.equalsIgnoreCase("No") ? bmi : 22.5) * -0.033881;//4
            double sbpBeta = bloodPressure.equalsIgnoreCase("Yes") ? sbp * -0.05933 : 0.0; //5
            double dbpBeta = bloodPressure.equalsIgnoreCase("Yes") ? dbp * -0.128468 : 0.0; //6
            double smokeBeta = (smoke.equalsIgnoreCase("Yes") ? (1 * -0.190731) : 0.0); //7
            double parentalBeta = (parentalHyper.equalsIgnoreCase("One Parent") ? 1.0 : (parentalHyper.equalsIgnoreCase("Both Parents") ? 2.0 : 0.0)) * -0.166121; //8
            double ageDbpBeta = (age * dbp * 0.001624); //9
            double sumOfBeta = interceptBeta + ageBeta + genderBeta + bmiBeta + sbpBeta + dbpBeta + smokeBeta + parentalBeta + ageDbpBeta;

            double double4Results = 1 - exp(-exp((log(4) - (sumOfBeta)) / 0.876925));
            double double2Results = 1 - exp(-exp((log(2) - (sumOfBeta)) / 0.876925));
            double double1Results = 1 - exp(-exp((log(1) - (sumOfBeta)) / 0.876925));

            /*jso.addProperty("double4Results",double4Results);
            jso.addProperty("double2Results",double2Results);
            jso.addProperty("double1Results",double1Results);*/

            jso.addProperty(Constants.HT_4, double4Results);
            jso.addProperty(Constants.HT_2, double2Results);
            jso.addProperty(Constants.HT_1, double1Results);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jso;
    }

    private JsonObject calculateDiabetisRisk(double age, double waist, String gender, String exerciselevel, String parentalDiabetes) {
        JsonObject jso = new JsonObject();
        int ageScore = 0;
        int waistScore = 0;
        int physicalScore = 0;
        int parentalScore = 0;
        try {
            if (age < 35) {
                ageScore = 0;
            }
            if (age >= 35) {
                ageScore = 20;
            }
            if (age >= 50) {
                ageScore = 30;
            }

            if (waist < 80) {
                waistScore = 0;
            }
            if (waist >= 80) {
                waistScore = 10;
                if (gender.equalsIgnoreCase("Male")) {
                    waistScore = 0;
                }
            }
            if (waist >= 90) {
                waistScore = 20;
                if (gender.equalsIgnoreCase("Male")) {
                    waistScore = 10;
                }
            }
            if (waist >= 100) {
                if (gender.equalsIgnoreCase("Male")) {
                    waistScore = 20;
                }
            }

            if (exerciselevel.equalsIgnoreCase("Sedentary Lifestyle")) {
                physicalScore = 30;
            } else if (exerciselevel.equalsIgnoreCase("Mild Exercise")) {
                physicalScore = 20;
            } else if (exerciselevel.equalsIgnoreCase("Moderate Exercise")) {
                physicalScore = 10;
            } else if (exerciselevel.equalsIgnoreCase("Vigorous Exercise")) {
                physicalScore = 0;
            }

            if (parentalDiabetes.equalsIgnoreCase("None")) {
                parentalScore = 0;
            } else if (parentalDiabetes.equalsIgnoreCase("One Parent")) {
                parentalScore = 10;
            } else if (parentalDiabetes.equalsIgnoreCase("Both Parents")) {
                parentalScore = 20;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        int score = ageScore + waistScore + physicalScore + parentalScore;
        String risklevel = "";
        if (score < 60) {
            risklevel = "Low";
        } else {
            risklevel = "High";
        }
        jso.addProperty(Constants.DIABETES_SCORE, score);
        jso.addProperty(Constants.DIABETES_RISK_LEVEL, risklevel);
        return jso;
    }

    private JsonObject calculateDailyCalorieRequirement(double height, double weight, double age, double yourCaloriesIntake) {
        JsonObject jso = new JsonObject();
        double dailyCalorieRequirement = 0.0;
        try {
            Double BMR = 0.0;
            if (spGender.getText().toString().equalsIgnoreCase("Male")) {
                BMR = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
            } else if (spGender.getText().toString().equalsIgnoreCase("Female")) {
                BMR = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
            }

            if (spPhysicalActivity.getText().toString().equalsIgnoreCase("Sedentary Lifestyle")) {
                dailyCalorieRequirement = BMR * 1.2;
            } else if (spPhysicalActivity.getText().toString().equalsIgnoreCase("Mild Exercise")) {
                dailyCalorieRequirement = BMR * 1.375;
            } else if (spPhysicalActivity.getText().toString().equalsIgnoreCase("Moderate Exercise")) {
                dailyCalorieRequirement = BMR * 1.55;
            } else if (spPhysicalActivity.getText().toString().equalsIgnoreCase("Vigorous Exercise")) {
                dailyCalorieRequirement = BMR * 1.725;
            }

            jso.addProperty(Constants.RC_SCORE, dailyCalorieRequirement);
            jso.addProperty(Constants.RC_YOUR_CALORIES, yourCaloriesIntake);
            jso.addProperty(Constants.BMR, BMR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jso;
    }

    private JsonObject calculateIdealBodyWeight(double height) {
        JsonObject jso = new JsonObject();
        double idealBodyWeight = 0;
        try {
            idealBodyWeight = 0.9 * (height - 100);
            jso.addProperty(Constants.IBW_SCORE, idealBodyWeight);
            jso.addProperty(Constants.IBW_YOUR_WEIGHT, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jso;
    }

    private JsonObject calculateBMI(double height, double weight) {
        JsonObject jso = new JsonObject();
        try {
            float tBMI = HealthProfile.calculateBMI(Float.parseFloat(String.valueOf(height)), Float.parseFloat(String.valueOf(weight)));
            if (tBMI > 0.f) {
                String risklevel = "";
                int color = R.color.colorPrimaryDark;
                if (tBMI < 18.5) {
                    risklevel = "Under Weight";
                    color = R.color.bmi_under_weight;
                } else if ((18.5 < tBMI || 18.5 == tBMI) && (tBMI < 23.0)) {
                    risklevel = "Normal Weight";
                    color = R.color.bmi_normal_weight;
                } else if ((23.0 < tBMI || 23 == tBMI) && (tBMI < 25.0)) {
                    risklevel = "Over Weight";
                    color = R.color.bmi_over_weight;
                } else if ((25 < tBMI || 25 == tBMI) && (tBMI < 30.0)) {
                    risklevel = "Obese I";
                    color = R.color.bmi_obase;
                } else if (tBMI > 30 || tBMI == 30) {
                    risklevel = "Obese II";
                    color = R.color.bmi_extreme_obase;
                }

                jso.addProperty(Constants.BMI_VALUE, tBMI);
                jso.addProperty(Constants.BMI_RISK_LEVEL, risklevel);
                jso.addProperty(Constants.BMI_COLOR, color);
                return jso;
            } else {
                if (tBMI == -1.f) {
                    if (height == 0) {
                        etHeight.setError("Should be 51cm to 275cm");
                        etHeight.requestFocus();
                    }

                    if (weight == 0) {
                        etWeight.setError("Should be 2Kg to 500Kg");
                        etWeight.requestFocus();
                    }
                } else if (tBMI == -2.f) {
                    etHeight.setError("Should be 51cm to 275cm");
                    etHeight.requestFocus();
                } else if (tBMI == -3.f) {
                    etWeight.setError("Should be 2Kg to 500Kg");
                    etWeight.requestFocus();
                }
            }
        } catch (NumberFormatException nfx) {
            Toast.makeText(this, "Invalid Value", Toast.LENGTH_SHORT).show();
            nfx.printStackTrace();
        }
        return jso;
    }
}
