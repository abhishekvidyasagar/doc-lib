package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.analytics.GATracker;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.User;
import com.doconline.doconline.model.UserAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MEDIA_PARTNER_NAME;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_VERIFIED;
import static com.doconline.doconline.app.Constants.OTP_EXPIRE_DURATION_IN_MINUTE;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_LOGIN;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_REGISTER;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_RESEND_OTP;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_VERIFY_OTP;

/**
 * Created by chiranjit on 22/12/16.
 */
public class MobileOTPFragment extends BaseFragment implements View.OnClickListener
{

    EditText editFirstDigit;

    EditText editSecondDigit;

    EditText editThirdDigit;

    EditText editFourthDigit;

    EditText editFifthDigit;

    EditText editSixthDigit;

    TextView tv_timer;

    TextView tv_message;

    Button button_submit;

    Button button_resend_otp;

    private Context context = null;
    private OnHttpResponse response_listener;
    private String mMessage = "";
    private String mMobileNo = "";
    private User mProfile;
    private OTPTimer timer;
    private boolean isSignUpProcess;


    public MobileOTPFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public MobileOTPFragment(Context context, OnHttpResponse response_listener, String mMessage, String mMobileNo)
    {
        this.context = context;
        this.response_listener = response_listener;
        this.mMessage = mMessage;
        this.mMobileNo = mMobileNo;
        this.timer = new OTPTimer((60 * 1000 * OTP_EXPIRE_DURATION_IN_MINUTE), 1000);
        this.isSignUpProcess = false;
    }


    @SuppressLint("ValidFragment")
    public MobileOTPFragment(Context context, OnHttpResponse response_listener, User mProfile, String mMessage)
    {
        this.context = context;
        this.response_listener = response_listener;
        this.mProfile = mProfile;
        this.mMessage = mMessage;
        this.timer = new OTPTimer((60 * 1000 * OTP_EXPIRE_DURATION_IN_MINUTE), 1000);
        this.isSignUpProcess = true;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        //ButterKnife.bind(this, view);

         editFirstDigit = view.findViewById(R.id.editFirstDigit);
        editSecondDigit = view.findViewById(R.id.editSecondDigit);
         editThirdDigit = view.findViewById(R.id.editThirdDigit);
         editFourthDigit = view.findViewById(R.id.editFourthDigit);
        editFifthDigit = view.findViewById(R.id.editFifthDigit);
        editSixthDigit = view.findViewById(R.id.editSixthDigit);
         tv_timer = view.findViewById(R.id.tv_timer);
         tv_message = view.findViewById(R.id.tv_message);
         button_submit = view.findViewById(R.id.btnGo);
       button_resend_otp = view.findViewById(R.id.btnResendOTP);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.focusEnterOTP(editFirstDigit);
        this.focusEnterOTP(editSecondDigit);
        this.focusEnterOTP(editThirdDigit);
        this.focusEnterOTP(editFourthDigit);
        this.focusEnterOTP(editFifthDigit);
        this.focusEnterOTP(editSixthDigit);
        this.setEnable(false);

        tv_message.setText(mMessage);
        button_submit.setText("Verify OTP");

        this.addListener();
        this.startTimer();
//
//        AppsFlyerLib.getInstance().registerConversionListener(getContext(), new AppsFlyerConversionListener() {
//
//            /* Returns the attribution data. Note - the same conversion data is returned every time per install */
//            @Override
//            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
//                for (String attrName : conversionData.keySet()) {
//                    Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
//                }
//            }
//
//            @Override
//            public void onInstallConversionFailure(String errorMessage) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(AppsFlyerLib.LOG_TAG, "error getting conversion data: " + errorMessage);
//                }
//            }
//
//            /* Called only when a Deep Link is opened */
//            @Override
//            public void onAppOpenAttribution(Map<String, String> conversionData) {
//                for (String attrName : conversionData.keySet()) {
//                    if (BuildConfig.DEBUG) {
//                        Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
//                    }
//                }
//            }
//
//            @Override
//            public void onAttributionFailure(String errorMessage) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(AppsFlyerLib.LOG_TAG, "error onAttributionFailure : " + errorMessage);
//                }
//            }
//        });
    }


    private void setEnable(boolean flag)
    {
        editSecondDigit.setEnabled(flag);
        editThirdDigit.setEnabled(flag);
        editFourthDigit.setEnabled(flag);
        editFifthDigit.setEnabled(flag);
        editSixthDigit.setEnabled(flag);
    }

    private void startTimer()
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        tv_timer.setVisibility(View.VISIBLE);
        button_resend_otp.setVisibility(View.INVISIBLE);

        if(timer != null)
        {
            timer.start();
        }
    }

    private void resetTimer()
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        tv_timer.setVisibility(View.VISIBLE);
        button_resend_otp.setVisibility(View.INVISIBLE);

        if(timer != null)
        {
            timer.start();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void addListener()
    {
        button_submit.setOnClickListener(this);
        button_resend_otp.setOnClickListener(this);
    }


    private void otpExpired()
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        try
        {
            tv_timer.setVisibility(View.INVISIBLE);
            button_resend_otp.setVisibility(View.VISIBLE);

            this.clearOTPFields();
            editFirstDigit.requestFocus();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void clearOTPFields()
    {
        editFirstDigit.getText().clear();
        editSecondDigit.getText().clear();
        editThirdDigit.getText().clear();
        editFourthDigit.getText().clear();
        editFifthDigit.getText().clear();
        editSixthDigit.getText().clear();
    }

    private class OTPTimer extends CountDownTimer
    {
        OTPTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            String v = String.format("%02d", millisUntilFinished / 60000);
            int va = (int)((millisUntilFinished % 60000) / 1000);
            tv_timer.setText("Resend in " + v + ":" + String.format("%02d", va) + " Minutes");

            Log.i("onTimerTick", "" + v + ":" + String.format("%02d", va));
        }

        @Override
        public void onFinish()
        {
            if(getActivity() != null && !getActivity().isFinishing())
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        stopTimer();
                        otpExpired();
                    }
                });
            }
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        try
        {
            stopTimer();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void stopTimer()
    {
        try
        {
            if(timer != null)
            {
                timer.cancel();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void focusEnterOTP(final EditText view)
    {
        view.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s)
            {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(view.getId() == R.id.editFirstDigit && view.getText().toString().length() > 0)
                {
                    editSecondDigit.setEnabled(true);
                    editSecondDigit.requestFocus();
                }

                if(view.getId() == R.id.editSecondDigit && view.getText().toString().length() > 0)
                {
                    editThirdDigit.setEnabled(true);
                    editThirdDigit.requestFocus();
                }

                else if(view.getId() == R.id.editSecondDigit && view.getText().toString().length() == 0)
                {
                    editFirstDigit.requestFocus();
                    showKeyboard(view);
                }

                if(view.getId() == R.id.editThirdDigit && view.getText().toString().length() > 0)
                {
                    editFourthDigit.setEnabled(true);
                    editFourthDigit.requestFocus();
                }

                else if(view.getId() == R.id.editThirdDigit && view.getText().toString().length() == 0)
                {
                    editSecondDigit.requestFocus();
                    showKeyboard(view);
                }

                if(view.getId() == R.id.editFourthDigit && view.getText().toString().length() > 0)
                {
                    editFifthDigit.setEnabled(true);
                    editFifthDigit.requestFocus();
                }

                else if(view.getId() == R.id.editFourthDigit && view.getText().toString().length() == 0)
                {
                    editThirdDigit.requestFocus();
                    showKeyboard(view);
                }

                if(view.getId() == R.id.editFifthDigit && view.getText().toString().length() > 0)
                {
                    editSixthDigit.setEnabled(true);
                    editSixthDigit.requestFocus();
                }

                else if(view.getId() == R.id.editFifthDigit && view.getText().toString().length() == 0)
                {
                    editFourthDigit.requestFocus();
                    showKeyboard(view);
                }

                if(view.getId() == R.id.editSixthDigit && view.getText().toString().length() > 0)
                {
                    hideKeyboard(view);
                }

                else if(view.getId() == R.id.editSixthDigit && view.getText().toString().length() == 0)
                {
                    editFifthDigit.requestFocus();
                }

                if(editFirstDigit.getText().toString().length() > 0
                        && editSecondDigit.getText().toString().length() > 0
                        && editThirdDigit.getText().toString().length() > 0
                        && editFourthDigit.getText().toString().length() > 0
                        && editFifthDigit.getText().toString().length() > 0
                        && editSixthDigit.getText().toString().length() > 0)
                {
                    hideKeyboard(view);
                }
            }
        });
    }


    private void showKeyboard(View view)
    {
        if(getActivity() == null)
        {
            return;
        }

        try
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(imm != null)
            {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void hideKeyboard(View view)
    {
        if(getActivity() == null)
        {
            return;
        }

        try
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(imm != null)
            {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void resendOTP()
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isSignUpProcess)
        {
            new HttpClient(HTTP_REQUEST_CODE_RESEND_OTP, MyApplication.HTTPMethod.POST.getValue(),
                    UserAccount.composeMobileJSON(this.mMobileNo), MobileOTPFragment.this)
                    .execute(mController.getMobileVerificationURL());
            return;
        }

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences ("SOCIALNWLOGIN", Context.MODE_PRIVATE);
        HashMap<String, Object> map = new HashMap<>();
        map = UserAccount.composeBasicAuthenticationJSON(mProfile, "");
        map.put(KEY_MEDIA_PARTNER_NAME,sharedPreferences.getString("mediapartnername",""));
        try{
            Log.d("AAA","MEDIASOURCEREGISTRATION MOBILE"+ map);
        }catch (Exception e){
            Log.d("AAA",""+e);
        }

        new HttpClient(HTTP_REQUEST_CODE_REGISTER, MyApplication.HTTPMethod.POST.getValue(), map,this)
                .execute(mController.getSignOutURL());
    }


    private void verifyOTP()
    {
        String OTP = new StringBuilder()
                .append(editFirstDigit.getText().toString())
                .append(editSecondDigit.getText().toString())
                .append(editThirdDigit.getText().toString())
                .append(editFourthDigit.getText().toString())
                .append(editFifthDigit.getText().toString())
                .append(editSixthDigit.getText().toString()).toString();


        if(OTP.length() == 6 && !isSignUpProcess)
        {
            new HttpClient(HTTP_REQUEST_CODE_VERIFY_OTP, MyApplication.HTTPMethod.POST.getValue(), UserAccount.composeMobileOTPJSON(this.mMobileNo, OTP), this)
                    .execute(mController.getMobileVerificationURL());
        }

        else if(OTP.length() == 6 && isSignUpProcess)
        {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences ("SOCIALNWLOGIN", Context.MODE_PRIVATE);
            HashMap<String, Object> map = new HashMap<>();
            map = UserAccount.composeBasicAuthenticationJSON(mProfile, OTP);
            map.put(KEY_MEDIA_PARTNER_NAME,sharedPreferences.getString("mediapartnername",""));
            try{
                Log.d("AAA","MEDIASOURCEREGISTRATION MOBILEOTP"+ map);
            }catch (Exception e){
                Log.d("AAA",""+e);
            }

            new HttpClient(HTTP_REQUEST_CODE_REGISTER, MyApplication.HTTPMethod.POST.getValue(), map,this)
                    .execute(mController.getSignOutURL());
        }

        else
        {
            Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view)
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = view.getId();
        if (id == R.id.btnGo) {
            this.verifyOTP();
        } else if (id == R.id.btnResendOTP) {
            this.resendOTP();
        }
    }

    public void TrackAppsFlyerUserRegistrationCompletedEvent(){

       /* Map<String, Object> eventValue = new HashMap<String, Object>();

        eventValue.put(AFInAppEventParameterName.REGSITRATION_METHOD, "Sign up Completed");
        AppsFlyerLib.getInstance().trackEvent(getContext(), AFInAppEventType.COMPLETE_REGISTRATION, eventValue);*/

        //for adgyde
        /*HashMap<String, String> params = new HashMap<String, String>();
        params.put("Registrations", "Mobile");//patrametre name,value
        PAgent.onEvent("Registrations", params);//eventid
        PAgent.flush();*/
    }

    private void initSession(User profile)
    {
        try
        {
            mController.getSession().putMobileStatus(true);
            mController.getSession().createLoginSession(profile);

            //if(!BuildConfig.DEBUG) {
                //Send AppsFlyer Analytics registered tracking event
                TrackAppsFlyerUserRegistrationCompletedEvent();
            //}
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            if(getActivity() != null)
            {
                getActivity().finish();
            }
        }
    }


    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_REGISTER && responseCode == HttpClient.CREATED)
            {
                /**
                 * Track registration success event (Google Analytics)
                 */
                GATracker.trackEvent(GATracker.GA_CATEGORY_REGISTRATION, GATracker.GA_ACTION_REGISTRATION_SUCCESS, GATracker.GA_LABEL_ANDROID, 0);

                syncAutoLogin(1000);
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_LOGIN && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    if(json.has(Constants.KEY_ERROR) && json.has(KEY_MESSAGE))
                    {
                        new CustomAlertDialog(getContext(), this, getView()).showSnackbar(json.getString(KEY_MESSAGE), CustomAlertDialog.LENGTH_LONG);
                        return;
                    }

                    if(json.has(Constants.KEY_TOKEN_TYPE) && json.has(Constants.KEY_EXPIRES_IN) && json.has(Constants.KEY_ACCESS_TOKEN) && json.has(Constants.KEY_REFRESH_TOKEN))
                    {
                        OAuth.saveOAuthCredential(json);
                        this.initSession(mProfile);
                        return;
                    }

                    new CustomAlertDialog(getContext(), this, getView()).
                            showSnackbar(getResources().getString(R.string.error_http_default), CustomAlertDialog.LENGTH_LONG);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return;
            }


            if(requestCode == HTTP_REQUEST_CODE_VERIFY_OTP && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    json = json.getJSONObject(KEY_DATA);

                    if(json.getInt(KEY_MOBILE_VERIFIED) == 1)
                    {
                        mController.getSession().putMobileStatus(true);
                        mController.getSession().changeMobileNo(this.mMobileNo);

                        this.stopTimer();
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                    else
                    {
                        mController.getSession().putMobileStatus(false);
                        mController.getSession().changeMobileNo("");

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_REGISTER && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    tv_message.setText(String.valueOf(message));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    this.resetTimer();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_RESEND_OTP && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    tv_message.setText(String.valueOf(message));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    this.resetTimer();
                }

                return;
            }


            if((requestCode == HTTP_REQUEST_CODE_RESEND_OTP || requestCode == HTTP_REQUEST_CODE_REGISTER)
                && responseCode == HttpClient.REQUEST_LIMIT_EXCEED)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String data = json.getString(KEY_MESSAGE);
                    tv_message.setText(String.valueOf(data));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    tv_timer.setVisibility(View.INVISIBLE);
                    button_resend_otp.setVisibility(View.VISIBLE);
                    this.stopTimer();
                }

                return;
            }

            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            if(requestCode == HTTP_REQUEST_CODE_LOGIN && responseCode != HttpClient.OK)
            {
                syncAutoLogin(Helper.getRandomInteger(1000, 3000));
            }

            else
            {
                response_listener.onPostExecute(requestCode, responseCode, response);
            }
        }
    }


    @Override
    public void onPositiveAction()
    {

    }

    @Override
    public void onNegativeAction()
    {

    }


    private void syncAutoLogin(int duration)
    {
        if(new InternetConnectionDetector(context).isConnected())
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    new HttpClient(HTTP_REQUEST_CODE_LOGIN, MyApplication.HTTPMethod.POST.getValue(),
                            UserAccount.composeBasicAuthenticationJSON(mProfile.getUserAccount().getRegMobileNo(), mProfile.getUserAccount().getPassword()),
                            MobileOTPFragment.this).execute(mController.getOAuthURL());
                }
            }, duration);
        }

        else
        {
            Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_SHORT).show();
        }
    }
}