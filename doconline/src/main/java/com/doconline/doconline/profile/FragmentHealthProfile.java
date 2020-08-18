package com.doconline.doconline.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.AllergyRecyclerAdapter;
import com.doconline.doconline.adapter.DrugAllergyRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.disease.AllergyManagementActivity;
import com.doconline.doconline.disease.MedicationsActivity;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.NumberInputFilter;
import com.doconline.doconline.helper.OnAllergyAction;
import com.doconline.doconline.helper.OnDrugAllergyAction;
import com.doconline.doconline.model.Allergy;
import com.doconline.doconline.model.HealthProfile;
import com.doconline.doconline.model.HealthProfileMasterData;
import com.doconline.doconline.model.Medication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DELETE_ALLERGY;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_GET_PROFILE;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_REMOVE_ALLERGY;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_UPDATE_PROFILE;

/**
 * Created by chiranjitbardhan on 25/01/18.
 */

public class FragmentHealthProfile extends BaseFragment implements
        AdapterView.OnItemSelectedListener, View.OnClickListener, OnAllergyAction, OnDrugAllergyAction {

    AppCompatSpinner spinnerSmoke;


    TextView spinnerLifeStyleActivity;


    TextView spinnerSleepDuration;

    TextView spinnerSleepPattern;


    TextView spinnerExercisePerWeek;

    /*this is new screen option*/

    TextView spinnerPregnancyStatus;


    TextView spinnerMaritalStatus;


    TextView spinnerSwitchInMedicine;

    TextView spinnerMedicalHistory;

    TextView spinnerMedicalInsurence;


    EditText editWeight;

    EditText editHeight;

    EditText editMedicalHistory;

    RecyclerView mRecyclerView;


    RecyclerView mDrugAllergyRecyclerView;

    LinearLayout medication_layout;

    Button btn_Calculate_BMI;

    Button btn_save;

    TextView tv_add_new;

    RelativeLayout layout_loading;


    TextView tvAddNewDrugAllergy;

    LinearLayout pregnancyLayout;


    private AllergyRecyclerAdapter mAdapter;
    private DrugAllergyRecyclerAdapter mDrugAllergyAdapter;
    private HealthProfile profile = new HealthProfile();

    private int index = -1;
    private boolean IS_AUTH_SYNC = true;

    public static final int CODE_DRUG_ALLERGY_ACTIVITY = 1;
    public static final int HTTP_REQUEST_CODE_REMOVE_DRUG_ALLERGY = 2;
    public static final int CODE_EDIT_DRUG_ALLERGY_ACTIVITY = 3;
    public static final int ACTIVITY_PREGNANCY_REQUEST_CODE = 4;

    List<JSONObject> drugAllergiesList = new ArrayList<>();

    String reasonforswitchinmedicineother = "";

    int listIndex = 0;

    public static FragmentHealthProfile newInstance() {
        return new FragmentHealthProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_profile, container, false);
       // ButterKnife.bind(this, view);

         spinnerSmoke =  view.findViewById(R.id.spinner_smoke);

        spinnerLifeStyleActivity=  view.findViewById(R.id.spinner_lifestyleactivity);

        spinnerSleepDuration=  view.findViewById(R.id.spinner_sleepduration);

         spinnerSleepPattern=  view.findViewById(R.id.spinner_sleeppattern);

        spinnerExercisePerWeek=  view.findViewById(R.id.spinner_exerciseperweek);

        /*this is new screen option*/
         spinnerPregnancyStatus=  view.findViewById(R.id.spinner_pregnancystatus);

        spinnerMaritalStatus=  view.findViewById(R.id.spinner_maritalstatus);

        spinnerSwitchInMedicine=  view.findViewById(R.id.spinner_switchinmedicine);
        spinnerMedicalHistory=  view.findViewById(R.id.spinner_medicalhistory);
         spinnerMedicalInsurence=  view.findViewById(R.id.spinner_medicalinsurence);

         editWeight=  view.findViewById(R.id.editWeight);
        editHeight=  view.findViewById(R.id.editHeight);
         editMedicalHistory=  view.findViewById(R.id.editMedicalHistory);
         mRecyclerView=  view.findViewById(R.id.recycler_view);

        mDrugAllergyRecyclerView=  view.findViewById(R.id.recycler_view_drug_allergy);

        medication_layout=  view.findViewById(R.id.medication_layout);
         btn_Calculate_BMI=  view.findViewById(R.id.btn_Calculate_BMI);
         btn_save=  view.findViewById(R.id.btnDone);
        tv_add_new=  view.findViewById(R.id.add_new);
        layout_loading=  view.findViewById(R.id.layout_loading);

       tvAddNewDrugAllergy=  view.findViewById(R.id.add_new_drug_alergy);

         pregnancyLayout=  view.findViewById(R.id.pregnancy_layout);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editWeight.setFilters(new InputFilter[]{new NumberInputFilter(5, 2)});
        editHeight.setFilters(new InputFilter[]{new NumberInputFilter(5, 2)});
        tv_add_new.setPaintFlags(tv_add_new.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        this.addListener();
        // get smoking spinner data
        this.populateSmokingSpinner();

        this.syncData();

        this.profile = mController.getSQLiteHelper().getHealthProfile();
        this.display_health_profile();
    }


    private void addListener() {
        medication_layout.setOnClickListener(this);
        btn_Calculate_BMI.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        tv_add_new.setOnClickListener(this);
        tvAddNewDrugAllergy.setOnClickListener(this);

        spinnerLifeStyleActivity.setOnClickListener(this);
        spinnerSleepDuration.setOnClickListener(this);
        spinnerSleepPattern.setOnClickListener(this);
        spinnerExercisePerWeek.setOnClickListener(this);
        spinnerPregnancyStatus.setOnClickListener(this);
        spinnerMaritalStatus.setOnClickListener(this);
        spinnerSwitchInMedicine.setOnClickListener(this);
        spinnerMedicalHistory.setOnClickListener(this);
        spinnerMedicalInsurence.setOnClickListener(this);

    }

    private void syncData() {
        if (new InternetConnectionDetector(getContext()).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getHealthProfileURL());
        }
    }

    private void populateSmokingSpinner() {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, getResources().getStringArray(R.array.smoke_status));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerSmoke.setAdapter(adapter);
        this.spinnerSmoke.setOnItemSelectedListener(this);
    }


    private void initAdapter() {
        this.profile.getAllergies().clear();
        this.profile.getAllergies().addAll(this.mController.getSQLiteHelper().getAllAllergies());

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            return;
        }

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AllergyRecyclerAdapter(this, this.profile.getAllergies());
        mRecyclerView.setAdapter(mAdapter);

       /* mAdapter.SetOnItemClickListener(new AllergyRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(getActivity(), AllergyManagementActivity.class);
                intent.putExtra("ALLERGY", profile.getAllergies().get(i));
                startActivity(intent);
            }
        });*/

    }


    @Override
    public void onResume() {
        super.onResume();
        this.initAdapter();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getView() == null) {
            return;
        }

        try {
            if ((requestCode == HTTP_REQUEST_CODE_GET_PROFILE || requestCode == HTTP_REQUEST_CODE_UPDATE_PROFILE)
                    && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED)) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.health_profile_data(json.getJSONObject(KEY_DATA));
                    if (requestCode == HTTP_REQUEST_CODE_UPDATE_PROFILE){
                        Toast.makeText(mController, "Saved Information", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_REMOVE_ALLERGY && responseCode == HttpClient.NO_RESPONSE) {
                this.delete_allergy();
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_REMOVE_DRUG_ALLERGY && responseCode == 204) {
                Toast.makeText(mController, "Allergy Deleted Successfully", Toast.LENGTH_SHORT).show();
            }


            new HttpResponseHandler(getActivity(), this, getView()).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IS_AUTH_SYNC = true;

            getActivity().runOnUiThread(new Runnable() {

                public void run() {

                    layout_loading.setVisibility(View.GONE);
                }
            });
        }
    }

    // add data into SQLite
    private void add_health_profile_data() {
        if (this.mController.getSQLiteHelper().dbRowCount(Constants.TABLE_HEALTH_PROFILE) > 0) {
            this.mController.getSQLiteHelper().update(profile);

            if (!IS_AUTH_SYNC) {
                Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_LONG).show();
            }
        } else {
            this.mController.getSQLiteHelper().insert(profile);
        }
    }

    // display health data
    private void display_health_profile() {
        if (profile != null) {
            if (profile.height != 0) {
                editWeight.setText(String.valueOf(profile.weight));
            }

            if (profile.weight != 0) {
                editHeight.setText(String.valueOf(profile.height));
            }

            if (profile.is_smoke == 1) {
                spinnerSmoke.setSelection(1, true);
            } else if (profile.is_smoke == 2) {
                spinnerSmoke.setSelection(2, true);
            } else {
                spinnerSmoke.setSelection(0, true);
            }

            editMedicalHistory.setText(String.valueOf(profile.medical_history));


            //pregnancy status make invisible if user is male
            Log.e("AAA","GENDER : "+mController.getSQLiteHelper().getUserProfile().getGender());
            if (mController.getSQLiteHelper().getUserProfile().getGender().equalsIgnoreCase("Female")){
                pregnancyLayout.setVisibility(View.VISIBLE);
            }else {
                pregnancyLayout.setVisibility(View.GONE);
            }

        }
    }

    // get user data
    private void health_profile_data(JSONObject json) {
        try {

            float weight = (json.isNull(Constants.KEY_WEIGHT)) ? 0 : (float) json.getDouble(Constants.KEY_WEIGHT);
            float height = (json.isNull(Constants.KEY_HEIGHT)) ? 0 : (float) json.getDouble(Constants.KEY_HEIGHT);
            int does_smoke = (json.isNull(Constants.KEY_DOES_SMOKE)) ? -1 : json.getInt(Constants.KEY_DOES_SMOKE);
            //String medical_history = (json.isNull(Constants.KEY_MEDICAL_HISTORY)) ? "" : json.getString(Constants.KEY_MEDICAL_HISTORY);

            this.profile.setHeight(height);
            this.profile.setWeight(weight);
            //this.profile.setMedicalHistory(medical_history);

            if (does_smoke == 0) {
                profile.setIsSmoke(1);
            } else if (does_smoke == 1) {
                profile.setIsSmoke(2);
            } else {
                this.profile.setIsSmoke(0);
            }

            JSONArray array = new JSONArray(json.getString(Constants.KEY_ALLERGIES));
            this.mController.getSQLiteHelper().remove_all(Constants.TABLE_ALLERGIES);
            this.profile.allergies.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int id = obj.getInt(Constants.KEY_ID);
                String name = obj.getString(Constants.KEY_NAME);

                if (this.mController.getSQLiteHelper().insert(new Allergy(id, name))) {
                    this.profile.addAllergy(new Allergy(id, name));
                } else {
                    this.mController.getSQLiteHelper().update(Constants.TABLE_ALLERGIES, id, name);
                }
            }


            /*********************drug allergies array*********************************************/
            JSONArray drugAllergiesArray = new JSONArray(json.getString(Constants.KEY_DRUG_ALLERGIES));
            drugAllergiesList.clear();
            for (int j = 0; j < drugAllergiesArray.length(); j++) {
                JSONObject oobj = drugAllergiesArray.getJSONObject(j);
                drugAllergiesList.add(oobj);
            }

            mDrugAllergyRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mDrugAllergyLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mDrugAllergyRecyclerView.setLayoutManager(mDrugAllergyLayoutManager);

            mDrugAllergyAdapter = new DrugAllergyRecyclerAdapter(this, drugAllergiesList);
            mDrugAllergyRecyclerView.setAdapter(mDrugAllergyAdapter);
            Log.e("AAA", "drug allergies list : " + drugAllergiesList);
            /*mDrugAllergyAdapter.SetOnItemClickListener(new DrugAllergyRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int i) {
                    String drugName = "";
                    String drugId = "";
                    try {
                        drugName = drugAllergiesList.get(i).get("name").toString();
                        drugId = drugAllergiesList.get(i).get("id").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("AAA", "DRUGNAME" + drugName);
                    startActivityForResult(new Intent(getActivity(), DrugAllergyActivity.class).
                            putExtra("drugname", drugName).putExtra("drugid", drugId), CODE_EDIT_DRUG_ALLERGY_ACTIVITY);
                }
            });*/


            /*********************drug allergies array*********************************************/


            array = new JSONArray(json.getString(Constants.KEY_MEDICATIONS));
            this.mController.getSQLiteHelper().remove_all(Constants.TABLE_MEDICATIONS);
            this.profile.getMedications().clear();

            // Medications taken
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int id = obj.getInt(Constants.KEY_ID);
                String name = obj.getString(Constants.KEY_NAME);
                String intake_time = (obj.isNull(Constants.KEY_INTAKE_TIME)) ? "" : obj.getString(Constants.KEY_INTAKE_TIME);
                String from_date = (obj.isNull(Constants.KEY_FROM_DATE)) ? "" : obj.getString(Constants.KEY_FROM_DATE);
                String to_date = (obj.isNull(Constants.KEY_TO_DATE)) ? "" : obj.getString(Constants.KEY_TO_DATE);

                String noofdays = (obj.isNull(Constants.KEY_NO_OF_DAYS)) ? "" : obj.getString(Constants.KEY_NO_OF_DAYS);
                String status = (obj.isNull(Constants.KEY_MEDICATIONS_STATUS)) ? "" : obj.getString(Constants.KEY_MEDICATIONS_STATUS);

                String notes = (obj.isNull(Constants.KEY_NOTES)) ? "" : obj.getString(Constants.KEY_NOTES);

                Medication medication = new Medication(id, name, intake_time, from_date, to_date, noofdays, status, notes);

                if (this.mController.getSQLiteHelper().insert(medication)) {
                    profile.addMedication(medication);
                } else {
                    this.mController.getSQLiteHelper().update(medication);
                }
            }


            //update feilds
            int lifestyleactivity = (json.isNull(Constants.KEY_LIFE_STYLE_ACTIVITY_ID)) ? 0 : json.getInt(Constants.KEY_LIFE_STYLE_ACTIVITY_ID);
            setTextAndIDs(lifestyleactivity, HealthProfileMasterData.lifeStyleActivityObject, spinnerLifeStyleActivity);

            int sleepduration = (json.isNull(Constants.KEY_SLEEP_DURATION_ID)) ? 0 : json.getInt(Constants.KEY_SLEEP_DURATION_ID);
            setTextAndIDs(sleepduration, HealthProfileMasterData.sleepDurationObject, spinnerSleepDuration);

            int sleeppattern = (json.isNull(Constants.KEY_SLEEP_PATTERN_ID)) ? 0 : json.getInt(Constants.KEY_SLEEP_PATTERN_ID);
            setTextAndIDs(sleeppattern, HealthProfileMasterData.sleepPatternObject, spinnerSleepPattern);

            int exerciceperweek = (json.isNull(Constants.KEY_EXERCISE_PER_WEEK_ID)) ? 0 : json.getInt(Constants.KEY_EXERCISE_PER_WEEK_ID);
            setTextAndIDs(exerciceperweek, HealthProfileMasterData.exercisePerWeekObject, spinnerExercisePerWeek);

            int maritalstatus = (json.isNull(Constants.KEY_MARITAL_STATUS_ID)) ? 0 : json.getInt(Constants.KEY_MARITAL_STATUS_ID);
            setTextAndIDs(maritalstatus, HealthProfileMasterData.maritalStatusObject, spinnerMaritalStatus);

            int reasonforswitchinmedicine = (json.isNull(Constants.KEY_SWITCH_IN_MEDICINE_ID)) ? 0 : json.getInt(Constants.KEY_SWITCH_IN_MEDICINE_ID);
            reasonforswitchinmedicineother = (json.isNull(Constants.KEY_SWITCH_IN_MEDICINE_OTHERS)) ? "" : json.getString(Constants.KEY_SWITCH_IN_MEDICINE_OTHERS);
            setTextAndIDsSwitchInMedicine(reasonforswitchinmedicine, HealthProfileMasterData.switchInMedicineObject, spinnerSwitchInMedicine, reasonforswitchinmedicineother);

            int medicalhistory = (json.isNull(Constants.KEY_MEDICAL_HISTORY_ID)) ? 0 : json.getInt(Constants.KEY_MEDICAL_HISTORY_ID);
            setTextAndIDs(medicalhistory, HealthProfileMasterData.medicalHistoryObject, spinnerMedicalHistory);

            int medicalinsurence = (json.isNull(Constants.KEY_MEDICAL_INSURENCE)) ? -1 : json.getInt(Constants.KEY_MEDICAL_INSURENCE);
            if (medicalinsurence == 1){
                spinnerMedicalInsurence.setText("Yes");
                spinnerMedicalInsurence.setTag("1");
            }else if (medicalinsurence == 0){
                spinnerMedicalInsurence.setText("No");
                spinnerMedicalInsurence.setTag("0");
            }

            //pregnancy details
            JSONObject pregnancydetails = null;
            try {
                pregnancydetails = (json.isNull(Constants.KEY_PREGNANCY_DETAILS)) ? null : json.getJSONObject(Constants.KEY_PREGNANCY_DETAILS);
                profile.setPregnancyDetails(pregnancydetails);
            }catch (Exception e){
                profile.setPregnancyDetails(pregnancydetails);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // add data into SQLite
            this.add_health_profile_data();
            this.display_health_profile();

            if (!IS_AUTH_SYNC) {
                Toast.makeText(getContext(), "Health Information Updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTextAndIDsSwitchInMedicine(int id, JSONArray arrayObject, TextView spinnerView, String reasonforswitchinmedicineother) {
        if (id > 0) {
            for (int i = 0; i < arrayObject.length(); i++) {
                try {
                    JSONObject jso = (JSONObject) arrayObject.get(i);
                    if (id == jso.getInt("id")) {
                        if (jso.get("first_level_value").toString().equalsIgnoreCase("Others")){
                            spinnerView.setText("" + reasonforswitchinmedicineother);
                        }else {
                            spinnerView.setText("" + jso.get("first_level_value"));
                        }
                        spinnerView.setTag("" + jso.get("id"));
                    }
                }catch (Exception e){

                }
            }
        }
    }

    private void setTextAndIDs(int id, JSONArray arrayObject, TextView spinnerView) {
        if (id > 0) {
            for (int i = 0; i < arrayObject.length(); i++) {
                try {
                    JSONObject jso = (JSONObject) arrayObject.get(i);
                    if (id == jso.getInt("id")) {
                        spinnerView.setText("" + jso.get("first_level_value"));
                        spinnerView.setTag("" + jso.get("id"));
                    }
                }catch (Exception e){

                }
            }
        }
    }


    @Override
    public void onAllergyDeleted(int index, int item_id) {
        this.index = index;
        this.delete_confirmation();
    }

    @Override
    public void onAllergyUpdated(int index, int item_id) {

    }


    private void delete_confirmation() {
        if (getContext() == null) {
            return;
        }

        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DELETE_ALLERGY, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_are_you_sure),
                        getResources().getString(R.string.dialog_content_delete_warning),
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), true);
    }

    // delete allergy from list and SQLite
    private void delete_allergy() {
        try {
            mController.getSQLiteHelper().remove(Constants.TABLE_ALLERGIES, Constants.KEY_ID, String.valueOf(profile.allergies.get(index).id));
            profile.getAllergies().remove(index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            index = -1;
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.medication_layout) {
            startActivity(new Intent(getActivity(), MedicationsActivity.class));
        } else if (id == R.id.btnDone) {
            if (!validate()) {
                return;
            }

            if (!this.init_profile()) {
                return;
            }

            if (new InternetConnectionDetector(getContext()).isConnected()) {
                IS_AUTH_SYNC = false;

                layout_loading.setVisibility(View.VISIBLE);

                new HttpClient(HTTP_REQUEST_CODE_UPDATE_PROFILE, MyApplication.HTTPMethod.PATCH.getValue(),
                        HealthProfile.composeHealthProfileJSON(profile), this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getHealthProfileURL());
                return;
            }

            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        } else if (id == R.id.add_new) {
            startActivity(new Intent(getActivity(), AllergyManagementActivity.class));
        } else if (id == R.id.add_new_drug_alergy) {
            startActivityForResult(new Intent(getActivity(), DrugAllergyActivity.class), CODE_DRUG_ALLERGY_ACTIVITY);
        } else if (id == R.id.btn_Calculate_BMI) {
            if (editHeight.getText().toString().trim().isEmpty()) {
                editHeight.setError("What's your height ?");
                return;
            }

            if (editWeight.getText().toString().trim().isEmpty()) {
                editWeight.setError("What's your weight ?");
                return;
            }

            try {

                float ht = Float.parseFloat(this.editHeight.getText().toString());
                float wt = Float.parseFloat(this.editWeight.getText().toString());

                float tBMI = HealthProfile.calculateBMI(ht, wt);

                if (tBMI > 0.f) {
                    Intent intent_BMIResults = new Intent(getActivity(), BMIResultsActivity.class);
                    intent_BMIResults.putExtra("BMI_RESULT", tBMI);
                    intent_BMIResults.putExtra("WEIGHT_FOR_BMI", wt);
                    intent_BMIResults.putExtra("HEIGHT_FOR_BMI", ht);

                    startActivity(intent_BMIResults);
                } else {
                    if (tBMI == -1.f) {
                        if (Float.valueOf(editHeight.getText().toString()) == 0) {
                            editHeight.setError("Should be 51cm to 275cm");
                            editHeight.requestFocus();
                            return;
                        }

                        if (Float.valueOf(editWeight.getText().toString()) == 0) {
                            editWeight.setError("Should be 2Kg to 500Kg");
                            editWeight.requestFocus();
                            return;
                        }
                    } else if (tBMI == -2.f) {
                        editHeight.setError("Should be 51cm to 275cm");
                        editHeight.requestFocus();
                    } else if (tBMI == -3.f) {
                        editWeight.setError("Should be 2Kg to 500Kg");
                        editWeight.requestFocus();
                    }
                }
            } catch (NumberFormatException nfx) {
                Toast.makeText(getContext(), "Invalid Value", Toast.LENGTH_SHORT).show();
                nfx.printStackTrace();
            }
        } else if (id == R.id.spinner_lifestyleactivity) {
            showList(HealthProfileMasterData.lifeStyleActivityObject, spinnerLifeStyleActivity, Constants.SPINNER_HEADER_LIFESTYLEACTIVITY);
        } else if (id == R.id.spinner_sleepduration) {
            showList(HealthProfileMasterData.sleepDurationObject, spinnerSleepDuration, Constants.SPINNER_HEADER_SLEEPDURATION);
        } else if (id == R.id.spinner_sleeppattern) {
            showList(HealthProfileMasterData.sleepPatternObject, spinnerSleepPattern, Constants.SPINNER_HEADER_SLEEPPATTERN);
        } else if (id == R.id.spinner_exerciseperweek) {
            showList(HealthProfileMasterData.exercisePerWeekObject, spinnerExercisePerWeek, Constants.SPINNER_HEADER_EXERCISEPERWEEK);
        } else if (id == R.id.spinner_maritalstatus) {
            showList(HealthProfileMasterData.maritalStatusObject, spinnerMaritalStatus, Constants.SPINNER_HEADER_MARITALSTATUS);
        } else if (id == R.id.spinner_switchinmedicine) {
            Log.e("AAA", "spinner tag is : " + spinnerSwitchInMedicine.getTag());
            showSwitchInMedicinesList(HealthProfileMasterData.switchInMedicineObject, spinnerSwitchInMedicine, Constants.SPINNER_HEADER_SWITCHINMEDICINE);
        } else if (id == R.id.spinner_medicalhistory) {
            showList(HealthProfileMasterData.medicalHistoryObject, spinnerMedicalHistory, Constants.SPINNER_HEADER_MEDICALHISTORY);
        } else if (id == R.id.spinner_medicalinsurence) {
            JSONArray medicalInsurenceObject = new JSONArray();
            try {
                JSONObject jso = new JSONObject();
                jso.put("id", "1");
                jso.put("first_level_value", "Yes");
                medicalInsurenceObject.put(jso);
                JSONObject jsoo = new JSONObject();
                jsoo.put("id", "0");
                jsoo.put("first_level_value", "No");
                medicalInsurenceObject.put(jsoo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            showList(medicalInsurenceObject, spinnerMedicalInsurence, Constants.SPINNER_HEADER_MEDICALINSURENCE);
        } else if (id == R.id.spinner_pregnancystatus) {
            Intent i = new Intent(getActivity(), PregnancyStatus.class);
            if (profile.getPregnancydetails() == null) {
                i.putExtra("objectstatus", "no");
                i.putExtra("pregnancyObject", "");
            } else {
                i.putExtra("objectstatus", "yes");
                i.putExtra("pregnancyObject", profile.getPregnancydetails().toString());
            }

            startActivityForResult(i, ACTIVITY_PREGNANCY_REQUEST_CODE);
        }
    }

    private void showSwitchInMedicinesList(JSONArray optionsArrayObject, final TextView spinnerView, String headerText) {
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("" + headerText);
        builder1.setCancelable(false);
        builder1.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int item) {
                if (items[item].toString().equalsIgnoreCase("Others")){
                    showOthersAlert(spinnerView, reasonforswitchinmedicineother);
                }else {
                    spinnerView.setText(items[item]);
                }
                spinnerView.setTag(itemsids[item]);
            }
        });
        AlertDialog selectionDialog = builder1.create();
        selectionDialog.setCancelable(true);
        if (!selectionDialog.isShowing()) {
            selectionDialog.show();
        }
    }

    private void showOthersAlert(final TextView spinnerView, String othersString) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Others");
        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5,5,5,5);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        input.setText(""+othersString);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        spinnerView.setText(""+input.getText());
                        reasonforswitchinmedicineother = input.getText().toString();
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    // Something into edit text. Enable the button.
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
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


    private boolean validate() {
        if (editHeight.getText().toString().trim().isEmpty()) {
            editHeight.setError("What's your height ?");
            editHeight.requestFocus();
            return false;
        }

        if (editWeight.getText().toString().trim().isEmpty()) {
            editWeight.setError("What's your weight ?");
            editWeight.requestFocus();
            return false;
        }

        //if (spinnerSmoke.getSelectedItemPosition() == 0) {
         //   Toast.makeText(getContext(), "Are you a smoker ?", Toast.LENGTH_SHORT).show();
         //   return false;
        //}

        //if (editMedicalHistory.getText().toString().trim().isEmpty()) {
        //    editMedicalHistory.setError("Medical history ?");
        //    editMedicalHistory.requestFocus();
        //    return false;
        //}

        return true;
    }


    private boolean init_profile() {
        try {
            profile.height = Float.parseFloat(editHeight.getText().toString());
            profile.weight = Float.parseFloat(editWeight.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(getContext(), "Invalid Value", Toast.LENGTH_SHORT).show();
            return false;
        }

        profile.medical_history = editMedicalHistory.getText().toString();

        if (spinnerSmoke.getSelectedItemPosition() == 1) {
            profile.is_smoke = 0;
        } else {
            profile.is_smoke = 1;
        }


        profile.anylifestyleactivities = (String) spinnerLifeStyleActivity.getTag();
        profile.sleepDuration = (String) spinnerSleepDuration.getTag();
        profile.sleepPattern = (String) spinnerSleepPattern.getTag();
        profile.exercisePerWeek = (String) spinnerExercisePerWeek.getTag();
        profile.maritalStatus = (String) spinnerMaritalStatus.getTag();
        profile.reasonSwitchInMedicine = (String) spinnerSwitchInMedicine.getTag();
        profile.reasonSwitchInMedicineOthers = reasonforswitchinmedicineother;
        profile.medicalHistory = (String) spinnerMedicalHistory.getTag();
        profile.medicalInsurence = (String) spinnerMedicalInsurence.getTag();

        return true;
    }


    @Override
    public void onPositiveAction(int requestCode) {
        if (requestCode == DIALOG_REQUEST_CODE_DELETE_ALLERGY) {
            // delete allery
            if (new InternetConnectionDetector(getContext()).isConnected()) {
                String url = mController.getHealthProfileURL() + Constants.KEY_ALLERGY + "/" + profile.allergies.get(index).id;

                new HttpClient(HTTP_REQUEST_CODE_REMOVE_ALLERGY, MyApplication.HTTPMethod.DELETE.getValue(), FragmentHealthProfile.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            } else {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRUG_ALLERGY_ACTIVITY) {
            syncData();
        }
        if (requestCode == CODE_EDIT_DRUG_ALLERGY_ACTIVITY) {
            syncData();
        }

        if (requestCode == ACTIVITY_PREGNANCY_REQUEST_CODE) {
            syncData();
        }
    }

    @Override
    public void onDrugAllergyDeleted(final int index, final int item_id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("DELETE");
        alert.setMessage("Are you sure you wish to delete");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String url = mController.getDeleteDrugAllergyURL() + item_id;

                new HttpClient(HTTP_REQUEST_CODE_REMOVE_DRUG_ALLERGY, MyApplication.HTTPMethod.DELETE.getValue(), FragmentHealthProfile.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                mDrugAllergyAdapter.notifyItemRemoved(index);
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    @Override
    public void onDrugAllergyUpdated(int index, int item_id) {

    }
}
