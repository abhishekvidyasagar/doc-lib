package com.doconline.doconline.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doconline.doconline.DashboardActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.OnForgetPasswordListener;
import com.doconline.doconline.helper.OnSignUpListener;
import com.doconline.doconline.model.User;

import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_PASS;


public class MainActivity extends BaseActivity implements View.OnClickListener, OnForgetPasswordListener, OnSignUpListener
{
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean back_pressed;
    private int position;
    private String mMessage = "";
    private String mMobileNo = "";
    private String mEmail = "";
    private String mMRNNo = "";
    private User mProfile = new User();

    public static final int HTTP_REQUEST_CODE_LOGIN = 1;
    public static final int HTTP_REQUEST_CODE_REGISTER = 2;
    public static final int HTTP_REQUEST_CODE_FORGOT_PASSWORD = 3;
    public static final int HTTP_REQUEST_CODE_UPDATE_MOBILE_NO = 4;
    public static final int HTTP_REQUEST_CODE_VERIFY_OTP = 5;
    public static final int HTTP_REQUEST_CODE_RESEND_OTP = 6;
    public static final int HTTP_REQUEST_CODE_UPDATE_EMAIL = 7;
    public static final int HTTP_REQUEST_CODE_SOCIAL_AUTHENTICATION = 8;

    private RelativeLayout layout_progress;
    private CoordinatorLayout layout_root_view;
    private ImageButton ib_back;
    private TextView tv_skip;
    private ImageView iv_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ib_back = findViewById(R.id.ib_back);
        tv_skip = findViewById(R.id.tv_skip);

        deepLink();
        onPageSelection(getIntent().getIntExtra("INDEX", 0), this.getAppTitle(0));

        layout_progress = findViewById(R.id.layout_loading);
        layout_root_view = findViewById(R.id.layout_root_view);
        iv_logo = findViewById(R.id.iv_logo);

        ib_back.setOnClickListener(this);
        tv_skip.setOnClickListener(this);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        iv_logo.startAnimation(animation);

        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
            return;
        }

        if (mController.getSession().getUpdateAlert()) {
            new CustomAlertDialog(this).showUpdateDialog();
        }
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.ib_back) {
            if (position == 8) {
                this.onPageSelection(0, getAppTitle(0));
            } else if (position == 10) {
                this.onPageSelection(1, getAppTitle(1));
            } else {
                finish();
            }
        } else if (id == R.id.tv_skip) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    private void deepLink()
    {
        try
        {
            Intent intent = getIntent();

            if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null)
            {
                Uri uri = intent.getData();

                Log.wtf("deeplink", "" + uri.getPath());

                if(uri.getPath().contains("/user/login"))
                {
                    if(uri.getQueryParameterNames().contains(KEY_EMAIL))
                    {
                        String email = uri.getQueryParameter(KEY_EMAIL) == null ? "" : uri.getQueryParameter(KEY_EMAIL);
                        mProfile.getUserAccount().setRegEmail(email);
                    }

                    if(uri.getQueryParameterNames().contains(KEY_PASS))
                    {
                        String password = uri.getQueryParameter(KEY_PASS) == null ? "" : uri.getQueryParameter(KEY_PASS);
                        mProfile.getUserAccount().setPassword(password);
                    }
                }

                Log.i("deeplink", "" + uri.toString());
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void navigate_fragment(int index, String title)
    {
        Fragment fragment = null;

        switch (index)
        {
            case 0:

                ib_back.setVisibility(View.GONE);
                tv_skip.setVisibility(View.VISIBLE);
                fragment = new LoginFragment(this, this, this, mProfile);
                break;

            case 1:

                ib_back.setVisibility(View.GONE);
                tv_skip.setVisibility(View.VISIBLE);
                fragment = new RegisterFragment(this, this, this, this, this, mProfile);
                break;

            case 2:

                ib_back.setVisibility(View.GONE);
                tv_skip.setVisibility(View.GONE);
                fragment = new ForgotPasswordFragment(this, this, this, this, this);
                break;

            case 3:

                ib_back.setVisibility(View.GONE);
                tv_skip.setVisibility(View.GONE);
                fragment = new EmailConfirmationFragment(this, this.mMessage);
                break;

            case 4:

                ib_back.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
                fragment = new MobileVerificationFragment(this, this, this, this, this);
                break;

            case 5:

                ib_back.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
                fragment = new MobileOTPFragment(this, this, this.mMessage, this.mMobileNo);
                break;

            case 6:

                ib_back.setVisibility(View.GONE);
                tv_skip.setVisibility(View.GONE);
                fragment = new RegistrationEmailConfirmationFragment(this);
                break;

            case 7:

                ib_back.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
                fragment = new ChangePasswordFragment(this, this);
                break;

            case 8:

                ib_back.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
                fragment = new ResetPasswordFragment(this, this, this, this.mMobileNo, this.mMRNNo, this.mMessage);
                break;

            case 9:

                ib_back.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
                fragment = new EmailVerificationFragment(this, this, this, this, this);
                break;

            case 10:

                ib_back.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
                fragment = new MobileOTPFragment(this,this, this.mProfile, this.mMessage);
                break;

            case 11:

                ib_back.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);
                fragment = new EmailOTPVerificationFragment(this,this, this.mMessage, this.mEmail);
                break;

            case 12:

                ib_back.setVisibility(View.GONE);
                tv_skip.setVisibility(View.GONE);
                fragment = new ResetPasswordOptionsFragment(this.mMobileNo, this.mEmail, this.mMRNNo);
                break;
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
            fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
    }


    private String getAppTitle(int index)
    {
        switch (index)
        {
            case 0:

                return "Sign In";

            case 1:

                return "Sign Up";

            case 2:

                return "Forgot Password";

            case 3:

                return "Email Confirmation";

            case 4:

                return "Update Mobile No";

            case  5:

                return "Verify OTP";

            case  6:

                return "Email Confirmation";

            case  7:

                return "Change Password";

            case  8:

                return "Reset Password";

            case  9:

                return "Email Verification";

            case  10:

                return "Sign Up";

            case 11:

                return "Verify OTP";

            case 12:

                return "Reset Password Options";

            default:

                return "";
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            if (fragment instanceof LoginFragment)
            {
                if(fragment.getUserVisibleHint())
                {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }

            if (fragment instanceof RegisterFragment )
            {
                if(fragment.getUserVisibleHint())
                {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(position == 8)
        {
            this.onPageSelection(0, getAppTitle(0));
            return;
        }

        if(position == 10)
        {
            this.onPageSelection(1, getAppTitle(1));
            return;
        }

        if (!back_pressed)
        {
            Toast.makeText(getApplicationContext(), R.string.text_back_button_press, Toast.LENGTH_SHORT).show();
            back_pressed = true;
            return;
        }

        finish();
    }


    @Override
    public void onPositiveAction()
    {
        finish();
    }

    @Override
    public void onNegativeAction()
    {
        finish();
    }


    @Override
    public void onPageSelection(int position, String title)
    {
        this.position = position;
        navigate_fragment(position, title);
    }


    @Override
    public void onPreExecute()
    {
        if(!isFinishing())
        {
            layout_progress.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if(!isFinishing())
        {
            layout_progress.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int index, String message)
    {
        this.mMessage = message;
    }

    @Override
    public void onMobileNumber(String mobile_no)
    {
        this.mMobileNo = mobile_no;
    }

    @Override
    public void onEmail(String mEmail)
    {
        this.mEmail = mEmail;
    }

    @Override
    public void onMRNNo(String mMRNNo)
    {
        this.mMRNNo = mMRNNo;
    }

    @Override
    public void onMobileSignUp(User mProfile, String mMessage)
    {
        this.mMessage = mMessage;
        this.mProfile = mProfile;

        onPageSelection(10, "Sign Up");
    }
}