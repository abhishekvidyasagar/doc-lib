package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.doconline.doconline.model.UserAccount;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_IS_VERIFIED;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_RESEND_OTP;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_VERIFY_OTP;

/**
 * Created by chiranjit on 22/12/16.
 */
public class EmailOTPVerificationFragment extends BaseFragment implements View.OnClickListener
{

    EditText editFirstDigit;
    //@BindView(R.id.editSecondDigit)
    EditText editSecondDigit;
    //@BindView(R.id.editThirdDigit)
    EditText editThirdDigit;
    //@BindView(R.id.editFourthDigit)
    EditText editFourthDigit;
    //@BindView(R.id.editFifthDigit)
    EditText editFifthDigit;
    ///@BindView(R.id.editSixthDigit)
    EditText editSixthDigit;
    //@BindView(R.id.tv_message)
    TextView tv_message;
    //@BindView(R.id.btnGo)
    Button button_submit;
    //@BindView(R.id.btnResendOTP)
    Button button_resend_otp;

    private Context context = null;
    private OnHttpResponse response_listener;
    private String mMessage = "";
    private String mEmail = "";


    public EmailOTPVerificationFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public EmailOTPVerificationFragment(Context context, OnHttpResponse response_listener, String mMessage, String mEmail)
    {
        this.context = context;
        this.response_listener = response_listener;
        this.mMessage = mMessage;
        this.mEmail = mEmail;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
      //  ButterKnife.bind(this, view);

         editFirstDigit = view.findViewById(R.id.editFirstDigit);
         editSecondDigit= view.findViewById(R.id.editSecondDigit);
         editThirdDigit= view.findViewById(R.id.editThirdDigit);
         editFourthDigit= view.findViewById(R.id.editFourthDigit);
         editFifthDigit= view.findViewById(R.id.editFifthDigit);
         editSixthDigit= view.findViewById(R.id.editSixthDigit);
         tv_message= view.findViewById(R.id.tv_message);
        button_submit= view.findViewById(R.id.btnGo);
        button_resend_otp= view.findViewById(R.id.btnResendOTP);
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
    }


    private void setEnable(boolean flag)
    {
        editSecondDigit.setEnabled(flag);
        editThirdDigit.setEnabled(flag);
        editFourthDigit.setEnabled(flag);
        editFifthDigit.setEnabled(flag);
        editSixthDigit.setEnabled(flag);
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


    @Override
    public void onDestroy()
    {
        super.onDestroy();
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


    private void clearOTPFields()
    {
        editFirstDigit.getText().clear();
        editSecondDigit.getText().clear();
        editThirdDigit.getText().clear();
        editFourthDigit.getText().clear();
        editFifthDigit.getText().clear();
        editSixthDigit.getText().clear();
    }

    private void resendOTP()
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            return;
        }

        new HttpClient(HTTP_REQUEST_CODE_RESEND_OTP, MyApplication.HTTPMethod.POST.getValue(),
                UserAccount.composeEmailJSON(mEmail), EmailOTPVerificationFragment.this)
                .execute(mController.getEmailVerificationURL());
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


        if(OTP.length() == 6)
        {
            new HttpClient(HTTP_REQUEST_CODE_VERIFY_OTP, MyApplication.HTTPMethod.POST.getValue(),
                    UserAccount.composeEmailOTPJSON(mEmail, OTP), this)
                    .execute(mController.getEmailVerificationURL());
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
            if(requestCode == HTTP_REQUEST_CODE_VERIFY_OTP && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    json = json.getJSONObject(KEY_DATA);

                    if(json.getInt(KEY_IS_VERIFIED) == 1)
                    {
                        mController.getSession().putEmailStatus(true);
                        mController.getSession().changeEmail(json.getString(KEY_EMAIL));

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                    else
                    {
                        mController.getSession().putEmailStatus(false);
                        mController.getSession().changeEmail("");
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
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
                    this.clearOTPFields();
                }

                return;
            }


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
}