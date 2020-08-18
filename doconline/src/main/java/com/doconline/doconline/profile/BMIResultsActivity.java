package com.doconline.doconline.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.doconline.doconline.R;
import com.doconline.doconline.helper.BaseActivity;

import java.util.Locale;


/**
 * Created by Raghunath on 2017-11-28.
 */
public class BMIResultsActivity extends BaseActivity
{

    Toolbar toolbar;
    TextView toolbar_title;
    TextView txtView_Weight;
    TextView txtView_Height;
    TextView txtView_BMIResult;
    SpeedometerGauge bmiMeterGuage;


    int darkBlue = Color.rgb(10, 101, 142);
    int blue = Color.rgb(102, 194, 201);
    int yellow = Color.rgb(242, 174, 129);
    int orange = Color.rgb(240, 116, 54);
    int red = Color.rgb(201, 68, 79);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_bmi_results);
       // ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title= findViewById(R.id.toolbar_title);
        txtView_Weight= findViewById(R.id.txtView_WeightValue);
        txtView_Height= findViewById(R.id.txtView_HeightValue);
        txtView_BMIResult= findViewById(R.id.txtView_bmi_result);
        bmiMeterGuage= findViewById(R.id.bmiMeterGauge);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_title.setText(getResources().getString(R.string.text_bmi_results));

        Intent intent = getIntent();
        float bmiResult  = intent.getFloatExtra("BMI_RESULT", 0);
        txtView_Weight.setText("Weight: " + intent.getFloatExtra("WEIGHT_FOR_BMI", 0) + " Kg");
        txtView_Height.setText("Height: " + intent.getFloatExtra("HEIGHT_FOR_BMI", 0) + " cm");

        // Add label converter
        bmiMeterGuage.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        bmiMeterGuage.setMaxSpeed(50);
        bmiMeterGuage.setLabelTextSize(50);
        bmiMeterGuage.setMajorTickStep(10);
        bmiMeterGuage.setMinorTicks(9);

        bmiMeterGuage.addColoredRange(0, 18.4, darkBlue);
        bmiMeterGuage.addColoredRange(18.5, 24.9, blue);
        bmiMeterGuage.addColoredRange(25, 29.9, yellow);
        bmiMeterGuage.addColoredRange(30, 34.9, orange);
        bmiMeterGuage.addColoredRange(35, 50, red);


        bmiMeterGuage.setSpeed(bmiResult, 1000, 300);
        String result = String.format(Locale.US,"%.2f", bmiResult);
        txtView_BMIResult.setText( "BMI = " + result + " Kg/m²");


        if(bmiResult < 18.4f && bmiResult > 0){
            txtView_BMIResult.setTextColor(darkBlue);
        }
        else if(bmiResult < 24.9 && bmiResult > 18.5){
            txtView_BMIResult.setTextColor(blue);
        }
        else if(bmiResult < 29.9 && bmiResult > 25){
            txtView_BMIResult.setTextColor(yellow);
        }
        else if(bmiResult < 34.9 && bmiResult > 30){
            txtView_BMIResult.setTextColor(orange);
        }
        else if(bmiResult < 50 && bmiResult > 35){
            txtView_BMIResult.setTextColor(red);
        }
        else if(bmiResult > 50){
            txtView_BMIResult.setTextColor(Color.rgb(255, 0, 0));
        }
    }

    public static String formatNumber(final Locale LOCALE, final double MIN_VALUE,
                                      final double MAX_VALUE, final int DECIMALS,
                                      final double VALUE) {
        StringBuilder sb        = new StringBuilder("%.").append(DECIMALS).append("f");
        String        f         = sb.toString();
        int           minLength = String.format(Locale.US, f, MIN_VALUE).length();
        int           maxLength = String.format(Locale.US, f, MAX_VALUE).length();
        int           length    = Math.max(minLength, maxLength);

        StringBuilder formatStringBuilder = new StringBuilder("%").append(length).append(".").append(DECIMALS).append("f");
        String        formatString        = formatStringBuilder.toString();

        double value = VALUE;
        if (value > 0) {
            value = Math.floor(VALUE * Math.pow(10, DECIMALS)) / Math.pow(10, DECIMALS);
        } else if (value < 0) {
            value = Math.ceil(VALUE * Math.pow(10, DECIMALS)) / Math.pow(10, DECIMALS);
        }

        return String.format(LOCALE, formatString, value);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}