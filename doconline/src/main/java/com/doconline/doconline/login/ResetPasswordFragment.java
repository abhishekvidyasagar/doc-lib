package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.model.UserAccount;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.MRN_PATTERN;
import static com.doconline.doconline.app.Constants.OTP_EXPIRE_DURATION_IN_MINUTE;
import static com.doconline.doconline.app.Constants.PASSWORD_PATTERN;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_RESEND_OTP;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_VERIFY_OTP;

/**
 * Created by chiranjit on 22/12/16.
 */
public class ResetPasswordFragment extends BaseFragment implements View.OnClickListener
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

    EditText edit_new_password;

    EditText edit_confirm_password;

    private Context context = null;
    private OnHttpResponse response_listener;
    private OnPageSelection page_listener;
    private String mMobileNo = "";
    private String mMRNNo = "";
    private String mMessage = "";
    private OTPTimer timer;


    public ResetPasswordFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public ResetPasswordFragment(Context context, OnPageSelection page_listener, OnHttpResponse response_listener, String mMobileNo, String mMRNNo, String mMessage)
    {
        this.context = context;
        this.response_listener = response_listener;
        this.page_listener = page_listener;
        this.mMobileNo = mMobileNo;
        this.mMRNNo = mMRNNo;
        this.mMessage = mMessage;
        this.timer = new OTPTimer((60 * 1000 * OTP_EXPIRE_DURATION_IN_MINUTE), 1000);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
      //  ButterKnife.bind(this, view);

        editFirstDigit = view.findViewById(R.id.editFirstDigit);
        editSecondDigit= view.findViewById(R.id.editSecondDigit);
        editThirdDigit= view.findViewById(R.id.editThirdDigit);
        editFourthDigit= view.findViewById(R.id.editFourthDigit);
        editFifthDigit= view.findViewById(R.id.editFifthDigit);
        editSixthDigit= view.findViewById(R.id.editSixthDigit);
        tv_timer= view.findViewById(R.id.tv_timer);
        tv_message= view.findViewById(R.id.tv_message);
        button_submit= view.findViewById(R.id.btnGo);
        button_resend_otp= view.findViewById(R.id.btnResendOTP);
        edit_new_password= view.findViewById(R.id.editNewPassword);
         edit_confirm_password= view.findViewById(R.id.editConfirmPassword);
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

        this.textChangeListener(view);
        this.addListener();
        this.startTimer();
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


    private boolean validate(String password, String confirm_password)
    {
        if(password.trim().length() < 8)
        {
            edit_new_password.setError("Minimum 8 Characters!");
            edit_new_password.requestFocus();
            return false;
        }

        if(password.contains(" "))
        {
            edit_new_password.setError("Blank spaces not allowed.");
            edit_new_password.requestFocus();
            return false;
        }

        if(!password.matches(PASSWORD_PATTERN))
        {
            edit_new_password.setError("Must contain at least one digit, uppercase letter, lowercase letter, and special character.");
            edit_new_password.requestFocus();
            return false;
        }

        if(!password.equals(confirm_password))
        {
            edit_confirm_password.setError("Password Confirmation Error!");
            edit_confirm_password.requestFocus();
            return false;
        }

        return true;
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

        if(this.mMRNNo.matches(MRN_PATTERN))
        {
            new HttpClient(HTTP_REQUEST_CODE_RESEND_OTP, MyApplication.HTTPMethod.POST.getValue(),
                    UserAccount.composePasswordResetOptionJSON(this.mMRNNo, "sms"),this)
                    .execute(mController.getForgotPasswordURL());
        }

        else
        {
            new HttpClient(HTTP_REQUEST_CODE_RESEND_OTP, MyApplication.HTTPMethod.POST.getValue(),
                    UserAccount.composeEmailJSON(this.mMobileNo),this)
                    .execute(mController.getForgotPasswordURL());
        }
    }


    private void resetPassword()
    {
        String OTP = new StringBuilder()
                .append(editFirstDigit.getText().toString())
                .append(editSecondDigit.getText().toString())
                .append(editThirdDigit.getText().toString())
                .append(editFourthDigit.getText().toString())
                .append(editFifthDigit.getText().toString())
                .append(editSixthDigit.getText().toString()).toString();


        if(OTP.length() != 6)
        {
            Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!validate(edit_new_password.getText().toString(), edit_confirm_password.getText().toString()))
        {
            return;
        }

        UserAccount account = new UserAccount();
        account.setPassword(edit_new_password.getText().toString());
        account.setConfirmPassword(edit_confirm_password.getText().toString());

        if(this.mMRNNo.matches(MRN_PATTERN))
        {
            account.setRegEmail(this.mMRNNo);
        }

        else
        {
            account.setRegEmail(this.mMobileNo);
        }

        new HttpClient(HTTP_REQUEST_CODE_VERIFY_OTP, MyApplication.HTTPMethod.POST.getValue(), UserAccount.composeOTPJSON(account, OTP), this)
                .execute(mController.getResetPasswordURL());
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
            this.resetPassword();
        } else if (id == R.id.btnResendOTP) {
            this.resendOTP();
        }
    }


    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();
    }


    private void clearFields()
    {
        edit_new_password.getText().clear();
        edit_confirm_password.getText().clear();

        this.clearOTPFields();
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
            if(requestCode == HTTP_REQUEST_CODE_VERIFY_OTP && responseCode == HttpClient.NO_RESPONSE)
            {
                this.clearFields();
                this.successAlert("Successful", "Password Reset Successful!");
                return;
            }

            // OTP is resent
            if(requestCode == HTTP_REQUEST_CODE_RESEND_OTP && (responseCode == HttpClient.ACCEPTED || responseCode == HttpClient.OK))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);

                    tv_message.setText(message);
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

            // If OTP max limit exceeds
            if(requestCode == HTTP_REQUEST_CODE_RESEND_OTP && responseCode == HttpClient.REQUEST_LIMIT_EXCEED)
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
            response_listener.onPostExecute(requestCode, responseCode, response);
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

    // Will be redirected to the login page
    private void successAlert(String title, String content)
    {
        if(getActivity() != null && !getActivity().isFinishing())
        {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(title);
            alert.setMessage(content);
            alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    page_listener.onPageSelection(0, "SIGN IN");
                }
            });
            alert.show();
            /*new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(title)
                    .setContentText(content)
                    .setConfirmText("Login")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog)
                        {
                            page_listener.onPageSelection(0, "SIGN IN");
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();*/
        }
    }



    private void textChangeListener(final View rootView) {

        edit_confirm_password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edit_new_password.getText().toString().equals(edit_confirm_password.getText().toString())) {

                    hideKeyboard(rootView);
                }
            }
        });
    }
}