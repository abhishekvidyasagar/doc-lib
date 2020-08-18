package com.doconline.doconline.profile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.diagnostics.DiagnosticsUserAddressActivity;
import com.doconline.doconline.floating.TimerService;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.subscription.SubscriptionActivity;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_LOGOUT;
import static com.doconline.doconline.app.Constants.DOCONLINE_THEME;

/**
 * Created by chiranjitbardhan on 30/01/18.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener
{

    Toolbar toolbar;
    TextView toolbar_title;
    Button btnChangeMobileNo;
    Button btnChangeEmail;
    Button btnPasswordChange;
    Button btnSubscriptionPlans;
    Button btnLogout;
    TextView tvEmail;
    TextView tvMobileNo;
    TextView tvMobileVerified;
    TextView tvMRN;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
       // ButterKnife.bind(this);
        toolbar=findViewById(R.id.toolbar);
        toolbar_title=findViewById(R.id.toolbar_title);
        btnChangeMobileNo=findViewById(R.id.btnChangeMobileNo);
        btnChangeEmail=findViewById(R.id.btnChangeEmail);
        btnPasswordChange=findViewById(R.id.btnPasswordChange);
        btnSubscriptionPlans=findViewById(R.id.btnSubscriptionPlans);
        btnLogout=findViewById(R.id.btnLogout);
        tvEmail=findViewById(R.id.tv_email);
        tvMobileNo=findViewById(R.id.tv_mobile_no);
        tvMobileVerified=findViewById(R.id.tv_mobile_no_verified);
        tvMRN=findViewById(R.id.tv_mrn);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_title.setText(R.string.Settings);
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        try
        {
            if(!mController.getSession().getMRN().isEmpty())
            {
                tvMRN.setText(mController.getSession().getMRN());
            }

            if(this.mController.getSession().getEmailStatus())
            {
                btnChangeEmail.setVisibility(View.GONE);
            }

            else
            {
                btnChangeEmail.setVisibility(View.VISIBLE);

                if(this.mController.getSession().getEmail().isEmpty())
                {
                    btnChangeEmail.setText("Add");
                    btnChangeEmail.setTextColor(getResources().getColor(primaryColor));
                }

                else
                {
                    btnChangeEmail.setText("Verify");
                    btnChangeEmail.setTextColor(getResources().getColor(R.color.light_red));
                }
            }

            tvMobileNo.setText(String.valueOf(this.mController.getSession().getMobileNumber()));
            tvEmail.setText(String.valueOf(this.mController.getSession().getEmail()));
            btnSubscriptionPlans.setText("View");

            if(this.mController.getSession().getMobileStatus())
            {
                btnChangeMobileNo.setText("Change");
                tvMobileVerified.setText("Verified & Active");
                tvMobileVerified.setTextColor(getResources().getColor(primaryColor));
            }

            else
            {
                if(this.mController.getSession().getMobileNumber().isEmpty())
                {
                    btnChangeMobileNo.setText("Add");
                    tvMobileVerified.setText("");
                }

                else
                {
                    btnChangeMobileNo.setText("Verify");
                    tvMobileVerified.setText("Not Verified");
                    tvMobileVerified.setTextColor(getResources().getColor(R.color.light_red));
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btnChangeMobileNo) {
            Intent intent1 = new Intent(SettingsActivity.this, MainActivity.class);
            intent1.putExtra("INDEX", 4);
            startActivity(intent1);
        } else if (id == R.id.btnChangeEmail) {
            Intent intent3 = new Intent(SettingsActivity.this, MainActivity.class);
            intent3.putExtra("INDEX", 9);
            startActivity(intent3);
        } else if (id == R.id.btnPasswordChange) {
            Intent intent2 = new Intent(SettingsActivity.this, MainActivity.class);
            intent2.putExtra("INDEX", 7);
            startActivity(intent2);
        } else if (id == R.id.btnChangeAddress) {//                Intent intent50 = new Intent(SettingsActivity.this, MainActivity.class);
//                intent50.putExtra("INDEX", 50);
//                startActivity(intent50);
            DiagnosticsUserAddressActivity.start(SettingsActivity.this);
        } else if (id == R.id.btnSubscriptionPlans) {
            SubscriptionActivity.start(SettingsActivity.this);
        } else if (id == R.id.btnLogout) {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_LOGOUT, this)
                    .showDialogWithAction(getResources().getString(R.string.dialog_title_logout),
                            getResources().getString(R.string.dialog_content_logout),
                            getResources().getString(R.string.Yes),
                            getResources().getString(R.string.No), true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_LOGOUT)
        {
            for(Appointment appointment: mController.appointmentList)
            {
                cancel_alarm(appointment.getAppointmentID());
            }

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_THEME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Constants.KEY_CURRENT_THEME, DOCONLINE_THEME);
            editor.commit();
            //iv_logo.setBackground(getResources().getDrawable(R.drawable.logo));
            //main_layout.setBackground(getResources().getDrawable(R.drawable.splash_bg));

            this.stopChatHead();
            mController.getSession().logoutUser();
        }
    }


    private void cancel_alarm(int appointment_id)
    {
        Log.wtf("APPOINTMENT_STATUS", "OFF");

        Intent intent = new Intent("android.media.action.APPOINTMENT_REMINDER_NOTIFICATION");
        intent.addCategory("android.intent.category.DEFAULT");

        //PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        //Intent intent = new Intent(context, AlarmReceiver.class);
        //alarmManager.cancel(PendingIntent.getService(context, 100, intent, 0));

        PendingIntent pIntent = PendingIntent.getBroadcast(this, appointment_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(alarmManager == null)
        {
            return;
        }

        alarmManager.cancel(pIntent);
        pIntent.cancel();
    }


    private void stopChatHead()
    {
        try
        {
            if(mController.isServiceRunning())
            {
                stopService(new Intent(SettingsActivity.this, TimerService.class));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}