package com.doconline.doconline.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;

import java.util.Locale;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTestActivity extends BaseActivity {

    private String mDownloadSpeedOutput = "";
    private String mUnits = "";

    TextView txtView_BMIResult;
    Toolbar toolbar;
    TextView toolbar_title;
    SpeedometerGauge speedMeterGauge;
    TextView txtView_status;
    LinearLayout status_layout;
    ImageView status_icon;

    private ProgressDialog pDialog;
    int green = Color.rgb(124, 252, 0);
    int orange = Color.rgb(240, 116, 54);
    int red = Color.rgb(201, 68, 79);
    String speed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
       // ButterKnife.bind(this);


       toolbar= findViewById(R.id.toolbar);
       toolbar_title= findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
       speedMeterGauge= findViewById(R.id.speedMeterGauge);
       txtView_status= findViewById(R.id.status_tv);
       status_layout= findViewById(R.id.status_layout);
       status_icon= findViewById(R.id.status_icon);

        txtView_BMIResult = findViewById(R.id.txtView_bmi_result);



        this.pDialog = new CustomAlertDialog(this, this).showLoadingAlertDialog(this, Color.parseColor("#f56234"), "Please Wait");
        toolbar_title.setText(getResources().getString(R.string.text_speed_test));

        speedMeterGauge.setMaxSpeed(500);
        speedMeterGauge.setLabelTextSize(50);
        speedMeterGauge.setMajorTickStep(100);
        speedMeterGauge.setMinorTicks(4);

        speedMeterGauge.addColoredRange(0, 100, red);
        speedMeterGauge.addColoredRange(101, 199, orange);
        speedMeterGauge.addColoredRange(200, 1000, green);


        speedMeterGauge.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

    }

    private void initProgressAlert() {
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void calculateSpeed(View view) {

       /* long mRxBytesPrevious = TrafficStats.getTotalRxBytes();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long mRxBytesCurrent = TrafficStats.getTotalRxBytes();

        long mDownloadSpeed = mRxBytesCurrent - mRxBytesPrevious;

        float mDownloadSpeedWithDecimals;

        if (mDownloadSpeed >= 1000000000) {
            mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000000000;
            mUnits = " GB";
        } else if (mDownloadSpeed >= 1000000) {
            mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000000;
            mUnits = " MB";

        } else {
            mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000;
            mUnits = " KB";
        }


        if (!mUnits.equals(" KB") && mDownloadSpeedWithDecimals < 100) {
            mDownloadSpeedOutput = String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals);
            System.out.println("Speed: "+mDownloadSpeedOutput +" "+ mUnits);
            speedMeterGauge.setSpeed(mDownloadSpeedWithDecimals, 1000, 300);
            txtView_BMIResult.setText( "Internet Speed = " +mDownloadSpeedOutput +" "+ mUnits);
        } else {
            mDownloadSpeedOutput = Integer.toString((int) mDownloadSpeedWithDecimals);
            System.out.println("Speed: "+mDownloadSpeedOutput +" "+ mUnits);
            speedMeterGauge.setSpeed(mDownloadSpeedWithDecimals, 1000, 300);
            txtView_BMIResult.setText( "Internet Speed = " +mDownloadSpeedOutput +" "+ mUnits);
        }*/

        txtView_BMIResult.setVisibility(View.VISIBLE);
        if (new InternetConnectionDetector(this).isConnected()) {
            new SpeedTestTask().execute();
            initProgressAlert();
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

    public class SpeedTestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(final SpeedTestReport report) {
                    // called when download/upload is finished
                    Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet().floatValue() / 1024 + " Kbps");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            status_layout.setVisibility(View.VISIBLE);
                            String status_text = "";
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            if (report.getTransferRateOctet().floatValue() / 1024 >= 1000) {
                                speed = "Bandwidth : " + String.format(Locale.US, "%.1f", report.getTransferRateOctet().floatValue() / (1024 * 1024)) + " Mbps";
                                speedMeterGauge.setSpeed(report.getTransferRateOctet().floatValue() / (1024 * 1024)*1000, 1000, 300);
                                status_text = "Your internet bandwidth supports Audio/Video calls";
                                txtView_status.setText(""+status_text);
                                //txtView_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.videocamera, 0, 0, 0);
                                status_icon.setBackgroundResource(R.drawable.ic_videocamera);
                            } else {
                                speed = "Bandwidth : " + String.format(Locale.US, "%.1f", report.getTransferRateOctet().floatValue() / 1024) + " Kbps";
                                speedMeterGauge.setSpeed(report.getTransferRateOctet().floatValue() / 1024, 1000, 300);

                                if ((report.getTransferRateOctet().floatValue() / 1024) >= 200.0) {
                                    status_text = "Your internet bandwidth supports Audio/Video calls";
                                    //txtView_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.videocamera, 0, 0, 0);
                                    status_icon.setBackgroundResource(R.drawable.ic_videocamera);
                                } else {
                                    //status_text = "Your internet bandwidth is not suitable for making Video call";
                                    status_text = "Your internet bandwidth is only suitable for making Audio calls";
                                    //txtView_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.speaker, 0, 0, 0);
                                    status_icon.setBackgroundResource(R.drawable.speaker);

                                    if ((report.getTransferRateOctet().floatValue() / 1024) < 50) {
                                        status_text = "Your internet bandwidth is not suitable for making Audio/Video calls ";
                                        //txtView_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.speakerno, 0, 0, 0);
                                        status_icon.setBackgroundResource(R.drawable.speakerno);
                                    }
                                }

                                if ((report.getTransferRateOctet().floatValue() / 1024)  >= 500.0) {
                                    status_text = "Your internet bandwidth is ideal for Video/Audio internet calls";
                                    //txtView_status.setCompoundDrawablesWithIntrinsicBounds( R.drawable.videocamera, 0, 0, 0);
                                    status_icon.setBackgroundResource(R.drawable.ic_videocamera);

                                }
                                txtView_status.setText(""+status_text);

                            }
                            txtView_BMIResult.setText(speed);
                        }
                    });

                }
                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    Log.v("speedtest", "[progress] rate in octet/s : " + report.getTransferRateOctet().floatValue() / 1024 + " Kbps");
                }
            });

            //speedTestSocket.startDownload("https://corpstaging.doconline.com/DocOnline_Brochure_1mb.pdf", 1500);
            speedTestSocket.startDownload("https://doconline-filestorage.s3.ap-south-1.amazonaws.com/WhiteLabelImages/DocOnline_Brochure_1mb.pdf", 1500);

            return speed;
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
