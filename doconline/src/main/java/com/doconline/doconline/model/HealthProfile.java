package com.doconline.doconline.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DOES_SMOKE;
import static com.doconline.doconline.app.Constants.KEY_EXERCISE_PER_WEEK_ID;
import static com.doconline.doconline.app.Constants.KEY_HEIGHT;
import static com.doconline.doconline.app.Constants.KEY_LIFE_STYLE_ACTIVITY_ID;
import static com.doconline.doconline.app.Constants.KEY_MARITAL_STATUS_ID;
import static com.doconline.doconline.app.Constants.KEY_MEDICAL_HISTORY;
import static com.doconline.doconline.app.Constants.KEY_MEDICAL_HISTORY_ID;
import static com.doconline.doconline.app.Constants.KEY_MEDICAL_INSURENCE;
import static com.doconline.doconline.app.Constants.KEY_SLEEP_DURATION_ID;
import static com.doconline.doconline.app.Constants.KEY_SLEEP_PATTERN_ID;
import static com.doconline.doconline.app.Constants.KEY_SWITCH_IN_MEDICINE_ID;
import static com.doconline.doconline.app.Constants.KEY_SWITCH_IN_MEDICINE_OTHERS;
import static com.doconline.doconline.app.Constants.KEY_WEIGHT;

/**
 * Created by chiranjit on 27/04/17.
 */
public class HealthProfile implements Serializable
{
    public int user_id, is_smoke = 0, status;
    public float height, weight;
    public String medical_history = "";
    public List<Allergy> allergies = new ArrayList<>();
    public List<Medication> mMedications = new ArrayList<>();

    public String anylifestyleactivities;
    public String sleepDuration;
    public String sleepPattern;
    public String exercisePerWeek;
    public String maritalStatus;
    public String reasonSwitchInMedicine;
    public String reasonSwitchInMedicineOthers;
    public String medicalHistory;
    public String medicalInsurence;

    JSONObject pregnancydetails;


    public void setUserId(int user_id)
    {
        this.user_id = user_id;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public void setWeight(float weight)
    {
        this.weight = weight;
    }

    public void setIsSmoke(int is_smoke)
    {
        this.is_smoke = is_smoke;
    }

    public void setMedicalHistory(String medical_history)
    {
        this.medical_history = medical_history;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public void setAllergies(List<Allergy> allergies)
    {
        this.allergies = allergies;
    }

    public void addAllergy(Allergy allergy)
    {
        this.allergies.add(allergy);
    }

    public  List<Allergy> getAllergies()
    {
        return  this.allergies;
    }

    public void setMedications(List<Medication> mMedications)
    {
        this.mMedications = mMedications;
    }

    public void addMedication(Medication medication)
    {
        this.mMedications.add(medication);
    }

    public Medication getMedication(int position)
    {
        return this.mMedications.get(position);
    }

    public List<Medication> getMedications()
    {
        return this.mMedications;
    }



    public static HashMap<String, Object> composeHealthProfileJSON(HealthProfile profile)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_HEIGHT, String.valueOf(profile.height));
        hashMap.put(KEY_WEIGHT, String.valueOf(profile.weight));
        hashMap.put(KEY_DOES_SMOKE, String.valueOf(profile.is_smoke));
        hashMap.put(KEY_MEDICAL_HISTORY, "Medical history");
        hashMap.put(KEY_LIFE_STYLE_ACTIVITY_ID, profile.anylifestyleactivities);
        hashMap.put(KEY_SLEEP_DURATION_ID, profile.sleepDuration);
        hashMap.put(KEY_SLEEP_PATTERN_ID, profile.sleepPattern);
        hashMap.put(KEY_EXERCISE_PER_WEEK_ID, profile.exercisePerWeek);
        hashMap.put(KEY_MARITAL_STATUS_ID, profile.maritalStatus);
        hashMap.put(KEY_SWITCH_IN_MEDICINE_ID, profile.reasonSwitchInMedicine);

        if (profile.reasonSwitchInMedicine.equalsIgnoreCase("24")){
            hashMap.put(KEY_SWITCH_IN_MEDICINE_OTHERS, profile.reasonSwitchInMedicineOthers);
        }else {
            hashMap.put(KEY_SWITCH_IN_MEDICINE_OTHERS, "");

        }
        hashMap.put(KEY_MEDICAL_HISTORY_ID, profile.medicalHistory);
        hashMap.put(KEY_MEDICAL_INSURENCE, profile.medicalInsurence);

        Log.e("AAA","request object : "+hashMap);

        return hashMap;
    }


    public static float calculateBMI(float ht, float wt)
    {

        float BMI;

        if((ht <= 0) || (wt <= 0))
        {
            return -1.f;
        }

        else if(ht < 51 || ht > 275.0)
        {
            return -2.f;
        }

        else if(wt < 2 || wt > 500.0)
        {
            return -3.f;
        }

        else
        {
            float height_in_mt = ht/100.0f;
            BMI = wt/(height_in_mt*height_in_mt);
        }

        Log.d("TAG_BMI", "calculateBMI: " + BMI);

        return BMI;
    }

    public void setPregnancyDetails(JSONObject pregnancydetails) {
        this.pregnancydetails = pregnancydetails;
    }

    public JSONObject getPregnancydetails(){
        return pregnancydetails;
    }
}