package com.doconline.doconline.HRA;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.BaseActivity;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HRAResultsActivity extends BaseActivity {

    TextView toolbar_title;
    Toolbar toolbar;

    public static JsonObject BMIObject = new JsonObject();
    public static JsonObject IdealBodyWeightObject = new JsonObject();
    public static JsonObject ReqiredCaloriesObject = new JsonObject();
    public static JsonObject DiabetesObject = new JsonObject();
    public static JsonObject HypertensionRiskObject = new JsonObject();
    public static JsonObject HypertensionOptimalObject = new JsonObject();
    public static JsonObject CVDYourRiskObject = new JsonObject();
    public static JsonObject CVDNormalObject = new JsonObject();
    public static JsonObject CVDOptimalObject = new JsonObject();
    public static JsonObject StrokeObject = new JsonObject();
    public static JSONObject requestObject = new JSONObject();


    CardView cvBMI, cvIBW, cvRC, cvDiabetes, cvHypertension, cvCVD, cvStroke;
    CardView cvCVDNO, cvHYPNO, cvStrokeNO;
    TextView tvCVDNO, tvHYPNO, tvStrokeNo;

    TextView tvBmiScore, tvBmiRiskLevel,
            tvIBWScore, tvIBWYourWeight,
            tvRCScore, tvRCYourcalories,
            tvDiabetesScore, tvDiabetesRiskLevel,
            tvHt4Risk, tvHt4Optimal, tvHt2Risk, tvHt2Optimal, tvHt1Risk, tvHt1Optimal,
            tvCVDRisk, tvCVDNormal, tvCVDOptimal, tvCVDHeartAge,
            tvStrokeScore, tvStrokeRisk, tvCVDRiskLevel;

    String isBPAvailable = "";
    double age = 0;

    LinearLayout llHypertensionYears;
    TextView tvHypertensionProne;

    TextView summary;
    int HTTP_REQUEST_HRA = 1;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title.setText(getResources().getString(R.string.text_hra_results));

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please Wait. \n " +
                "Loading..");
        progressDialog.setCancelable(false);

        isBPAvailable = getIntent().getExtras().get(Constants.STATUS_BLOOD_PRESSURE).toString();
        age = getIntent().getExtras().getDouble(Constants.KEY_AGE);

        doInitialise();

        if (isBPAvailable.equalsIgnoreCase("Yes")){
            cvHypertension.setVisibility(View.VISIBLE);
            cvHYPNO.setVisibility(View.GONE);
            //cvCVD.setVisibility(View.VISIBLE);
            //cvCVDNO.setVisibility(View.GONE);

            if (age < 30){
                cvCVD.setVisibility(View.GONE);
                cvCVDNO.setVisibility(View.VISIBLE);
                tvCVDNO.setText("Entered data is not appropriate for risk assessment. \n " +
                        "1. Age should be between 30 to 74 \n " +
                        "2. SBP should be between 90 to 200");
            }else {
                cvCVD.setVisibility(View.VISIBLE);
                cvCVDNO.setVisibility(View.GONE);
            }

        }else {
            cvHypertension.setVisibility(View.GONE);
            cvHYPNO.setVisibility(View.VISIBLE);
            cvCVD.setVisibility(View.GONE);
            cvCVDNO.setVisibility(View.VISIBLE);

            tvCVDNO.setText("Entered data is not appropriate for risk assessment. \n " +
                    "1. Age should be between 30 to 74 \n " +
                    "2. SBP should be between 90 to 200");

            tvHYPNO.setText("Entered data is not appropriate for risk assessment. \n " +
                    "1. Age should be between 20 to 80 \n " +
                    "2. SBP should be between 50 to 140");
        }
        Log.e("AAA","BMI : "+BMIObject);
        Log.e("AAA","IBW : "+IdealBodyWeightObject);
        Log.e("AAA","Calories : "+ReqiredCaloriesObject);
        Log.e("AAA","Diabetes : "+DiabetesObject);
        Log.e("AAA","Hypertension Risk : "+HypertensionRiskObject);
        Log.e("AAA","hypertension Optimal : "+HypertensionOptimalObject);
        Log.e("AAA","CVD Risk : "+CVDYourRiskObject);
        Log.e("AAA","CVD Normal : "+CVDNormalObject);
        Log.e("AAA","CVD Optimal : "+CVDOptimalObject);
        Log.e("AAA","Storke : "+StrokeObject);


    }
    public void doInitialise(){
        cvBMI = findViewById(R.id.bmi_cv);
        cvIBW = findViewById(R.id.ibw_cv);
        cvRC = findViewById(R.id.rc_cv);
        cvDiabetes = findViewById(R.id.diabetes_cv);
        cvHypertension = findViewById(R.id.hypertension_cv);
        cvCVD = findViewById(R.id.cvd_cv);
        cvStroke = findViewById(R.id.stroke_cv);

        tvBmiScore = findViewById(R.id.bmi_score_tv);
        tvBmiRiskLevel = findViewById(R.id.bmi_risklevel_tv);
        tvIBWScore = findViewById(R.id.ibw_score_tv);
        tvIBWYourWeight = findViewById(R.id.ibw_yourweight_tv);
        tvRCScore = findViewById(R.id.rc_score_tv);
        tvRCYourcalories = findViewById(R.id.rc_yourcalories_tv);
        tvDiabetesScore = findViewById(R.id.diabetes_score_tv);
        tvDiabetesRiskLevel = findViewById(R.id.diabetes_risklevel_tv);
        tvHt4Risk = findViewById(R.id.ht_4_risk_tv);
        tvHt4Optimal = findViewById(R.id.ht_4_optimal_tv);
        tvHt2Risk = findViewById(R.id.ht_2_risk_tv);
        tvHt2Optimal = findViewById(R.id.ht_2_optimal_tv);
        tvHt1Risk = findViewById(R.id.ht_1_risk_tv);
        tvHt1Optimal = findViewById(R.id.ht_1_optimal_tv);
        tvCVDRisk = findViewById(R.id.cvd_risk_tv);
        tvCVDNormal = findViewById(R.id.cvd_normal_tv);
        tvCVDOptimal = findViewById(R.id.cvd_optimal_tv);
        tvCVDHeartAge = findViewById(R.id.cvd_heartage_tv);
        tvStrokeScore = findViewById(R.id.stroke_score_tv);
        tvStrokeRisk = findViewById(R.id.stroke_risk_tv);
        tvCVDRiskLevel = findViewById(R.id.cvd_risk_leve_tv);

        cvCVDNO = findViewById(R.id.cvd_cv_no);
        cvHYPNO = findViewById(R.id.hypertension_cv_no);
        tvCVDNO = findViewById(R.id.cvd_no);
        tvHYPNO = findViewById(R.id.hyp_no);
        cvStrokeNO = findViewById(R.id.stroke_cv_no);
        tvStrokeNo = findViewById(R.id.stroke_tv_no);

        llHypertensionYears = findViewById(R.id.hypertension_years_ll);
        tvHypertensionProne = findViewById(R.id.hypertension_prone_tv);

        summary = findViewById(R.id.summary);
        /*summary.setText("BMI : "+BMIObject +
                "\n IBW : "+IdealBodyWeightObject +
                "\n Calories : "+ReqiredCaloriesObject+
                "\n Diabetes : "+DiabetesObject +
                "\n Hypertension Risk : "+HypertensionRiskObject+
                "\n hypertension Optimal : "+HypertensionOptimalObject +
                "\n CVD Risk : "+CVDYourRiskObject +
                "\n CVD Normal : "+CVDNormalObject+
                "\n CVD Optimal : "+CVDOptimalObject +
                "\n Storke : "+StrokeObject);*/

        //BMI
        double bmiscore = BMIObject.get(Constants.BMI_VALUE).getAsDouble();
        String bmiString = String.format("%.2f",bmiscore);
        tvBmiScore.setText(""+bmiString);
        tvBmiRiskLevel.setText(BMIObject.get(Constants.BMI_RISK_LEVEL).getAsString());
        tvBmiRiskLevel.setBackgroundColor(getResources().getColor(BMIObject.get(Constants.BMI_COLOR).getAsInt()));

        //IBW
        int ibwval = (int) Math.round(IdealBodyWeightObject.get(Constants.IBW_SCORE).getAsDouble());
        tvIBWScore.setText(""+ ibwval + " Kg");
        try {
            requestObject.put(Constants.KEY_IBW, String.format("%.4f",IdealBodyWeightObject.get(Constants.IBW_SCORE).getAsDouble()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Calories
        double calories = ReqiredCaloriesObject.get(Constants.RC_SCORE).getAsDouble();
        int intcalories = (int) Math.round(calories);
        tvRCScore.setText(""+intcalories + " Cal");
        try {
            requestObject.put(Constants.KEY_BMR, String.format("%.4f",ReqiredCaloriesObject.get(Constants.BMR).getAsDouble()));
            requestObject.put(Constants.KEY_CALORIES_REQUIRED, intcalories);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Diabetes
        tvDiabetesScore.setText(""+DiabetesObject.get(Constants.DIABETES_SCORE));
        tvDiabetesRiskLevel.setText(""+DiabetesObject.get(Constants.DIABETES_RISK_LEVEL).getAsString());
        if (DiabetesObject.get(Constants.DIABETES_RISK_LEVEL).getAsString().equalsIgnoreCase("Low")){
            tvDiabetesRiskLevel.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else {
            tvDiabetesRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
        }
        try {
            requestObject.put(Constants.KEY_DIABETES_RISK, DiabetesObject.get(Constants.DIABETES_SCORE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Hypertension
        if (isBPAvailable.equalsIgnoreCase("Yes")){
            try {
                if ((requestObject.getInt(Constants.KEY_SYSTOLIC) > 140 || requestObject.getInt(Constants.KEY_SYSTOLIC) == 140) &&
                        (requestObject.getInt(Constants.KEY_DIASTOLIC) > 90 || requestObject.getInt(Constants.KEY_DIASTOLIC) == 90)){
                    llHypertensionYears.setVisibility(View.GONE);
                    tvHypertensionProne.setVisibility(View.VISIBLE);

                }else {
                    llHypertensionYears.setVisibility(View.VISIBLE);
                    tvHypertensionProne.setVisibility(View.GONE);

                    tvHt1Risk.setText(""+ (int) Math.round(HypertensionRiskObject.get(Constants.HT_1).getAsDouble() * 100)  + "% , ");
                    tvHt1Optimal.setText(""+ (int) Math.round(HypertensionOptimalObject.get(Constants.HT_1).getAsDouble() * 100)  +"%");

                    tvHt2Risk.setText(""+ (int) Math.round(HypertensionRiskObject.get(Constants.HT_2).getAsDouble() * 100) + "% , ");
                    tvHt2Optimal.setText(""+ (int) Math.round(HypertensionOptimalObject.get(Constants.HT_2).getAsDouble() * 100) + "%");

                    tvHt4Risk.setText(""+ (int) Math.round(HypertensionRiskObject.get(Constants.HT_4).getAsDouble() * 100) + "% , ");
                    tvHt4Optimal.setText(""+ (int) Math.round(HypertensionOptimalObject.get(Constants.HT_4).getAsDouble() * 100)+ "%");

                    JSONObject oneYear = new JSONObject();
                    oneYear.put(Constants.KEY_SCORE, HypertensionRiskObject.get(Constants.HT_1).getAsDouble() * 100);
                    oneYear.put(Constants.KEY_OPTIMAL, HypertensionOptimalObject.get(Constants.HT_1).getAsDouble() * 100);

                    JSONObject twoYear = new JSONObject();
                    twoYear.put(Constants.KEY_SCORE, HypertensionRiskObject.get(Constants.HT_2).getAsDouble() * 100);
                    twoYear.put(Constants.KEY_OPTIMAL, HypertensionOptimalObject.get(Constants.HT_2).getAsDouble() * 100);

                    JSONObject fourYear = new JSONObject();
                    fourYear.put(Constants.KEY_SCORE, HypertensionRiskObject.get(Constants.HT_4).getAsDouble() * 100);
                    fourYear.put(Constants.KEY_OPTIMAL, HypertensionOptimalObject.get(Constants.HT_4).getAsDouble() * 100);

                    JSONObject htRiskObject = new JSONObject();
                    htRiskObject.put(Constants.KEY_1_YEAR, oneYear);
                    htRiskObject.put(Constants.KEY_2_YEAR, twoYear);
                    htRiskObject.put(Constants.KEY_4_YEAR, fourYear);
                    JSONArray htRiskArray = new JSONArray();
                    htRiskArray.put(htRiskObject);

                    requestObject.put(Constants.KEY_HYPERTENSION_RISK, htRiskArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //CVD
        if (isBPAvailable.equalsIgnoreCase("Yes")){
            int heartage = (int) Math.round(CVDYourRiskObject.get(Constants.CVD_YOUR_HEART_AGE).getAsDouble());
            tvCVDHeartAge.setText("Heart Age - "+ heartage);

            tvCVDRisk.setText("Risk \n "+
                    String.format("%.2f",CVDYourRiskObject.get(Constants.CVD_YOUR_RISK).getAsDouble() * 100));
            tvCVDOptimal.setText("Optimal \n "+
                    String.format("%.2f",CVDOptimalObject.get(Constants.CVD_YOUR_RISK).getAsDouble() * 100));
            tvCVDNormal.setText("Normal \n "+
                    String.format("%.2f", CVDNormalObject.get(Constants.CVD_YOUR_RISK).getAsDouble() * 100));
            double riskscore = CVDYourRiskObject.get(Constants.CVD_YOUR_RISK).getAsDouble() * 100;
            if (riskscore > 30 || riskscore == 30){
                tvCVDRiskLevel.setText("High");
                tvCVDRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            }

            try {
                JSONObject cvdObject = new JSONObject();
                cvdObject.put(Constants.KEY_SCORE,
                        String.format("%.2f",CVDYourRiskObject.get(Constants.CVD_YOUR_RISK).getAsDouble() * 100));
                cvdObject.put(Constants.KEY_OPTIMAL,
                        String.format("%.2f",CVDOptimalObject.get(Constants.CVD_YOUR_RISK).getAsDouble() * 100));
                cvdObject.put(Constants.KEY_NORMAL,
                        String.format("%.2f", CVDNormalObject.get(Constants.CVD_YOUR_RISK).getAsDouble() * 100));
                cvdObject.put(Constants.KEY_HEART_AGE,
                        String.format("%.1f", CVDYourRiskObject.get(Constants.CVD_YOUR_HEART_AGE).getAsDouble()));

                requestObject.put(Constants.KEY_CVD_RISK, cvdObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        //Stroke
        if (StrokeObject.get(Constants.STROKE_CAL_STATUS).getAsBoolean()){
            cvStroke.setVisibility(View.VISIBLE);
            cvStrokeNO.setVisibility(View.GONE);
            tvStrokeScore.setText("" + StrokeObject.get(Constants.STROKE_SCORE).getAsString() + "%");
            try {
                JSONObject strokeObject = new JSONObject();
                strokeObject.put(Constants.KEY_POINTS, StrokeObject.get(Constants.STROKE_POINTS).getAsString());
                strokeObject.put(Constants.KEY_PROPABILITY, StrokeObject.get(Constants.STROKE_SCORE).getAsString());
                requestObject.put(Constants.KEY_STROKE_RISK,strokeObject);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            cvStroke.setVisibility(View.GONE);
            cvStrokeNO.setVisibility(View.VISIBLE);
            tvStrokeNo.setText("Entered data is not appropriate for risk assessment. \n " +
                    "1. Age should be between 54 to 84");
        }


        Log.e("AAA", "Request Object : "+requestObject);

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

    public void doSave(View view) {
        progressDialog.show();

        String rawJSON = String.valueOf(requestObject);

        new HttpClient(HTTP_REQUEST_HRA, MyApplication.HTTPMethod.POST.getValue(), true, rawJSON,
                HRAResultsActivity.this).execute(mController.getHRASaveURL());
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        try{
            if (requestCode == HTTP_REQUEST_HRA ){
                progressDialog.dismiss();
                Log.e("AAA","HRA Save Respose : "+response);
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.getInt("code") == 200){
                    Toast.makeText(this, "Data Saved Successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(responseCode, response);

        }catch (Exception e){
            //new HttpResponseHandler(this, this, rootLayout.getRootView()).handle(0, response);
            e.printStackTrace();
        }

    }

    public void framinghanHearStudy(View view) {
        Uri uri = Uri.parse("https://www.framinghamheartstudy.org/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
