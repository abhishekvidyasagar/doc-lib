package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnForgetPasswordListener;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.UserAccount;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MASKED_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_MASKED_MOBILE_NUMBER;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_MRN;
import static com.doconline.doconline.app.Constants.KEY_OTP_SENT;
import static com.doconline.doconline.app.Constants.MOBILE_NUMBER_PATTERN;
import static com.doconline.doconline.app.Constants.MRN_PATTERN;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_FORGOT_PASSWORD;

/**
 * Created by chiranjit on 22/12/16.
 */
public class ForgotPasswordFragment extends BaseFragment implements View.OnClickListener
{

    EditText edit_email;

    Button button_submit;

    Button button_existing_user;

    private Context context = null;
    private OnPageSelection page_listener;
    private OnHttpResponse response_listener;
    private OnTaskCompleted task_listener;
    private OnForgetPasswordListener password_listener;


    public ForgotPasswordFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public ForgotPasswordFragment(Context context, OnPageSelection page_listener, OnHttpResponse response_listener, OnTaskCompleted task_listener, OnForgetPasswordListener password_listener)
    {
        this.context = context;
        this.page_listener = page_listener;
        this.task_listener = task_listener;
        this.response_listener = response_listener;
        this.password_listener = password_listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
      //  ButterKnife.bind(this, view);
        edit_email = view.findViewById(R.id.editEmail);
        button_submit = view.findViewById(R.id.btnGo);
         button_existing_user = view.findViewById(R.id.btnExistingUser);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.addListener();
        button_submit.setText(R.string.button_reset_link);
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
        button_existing_user.setOnClickListener(this);
    }

    private boolean isValidEmail(String email)
    {
        if(email == null || TextUtils.isEmpty(email))
        {
            edit_email.setError("Email Required");
            edit_email.requestFocus();
            return false;
        }

        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edit_email.setError("Invalid Email");
            edit_email.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValid_Email_MobileNo_MRN(String input)
    {
        if(input == null || TextUtils.isEmpty(input))
        {
            edit_email.setError("Email / Mobile No. / MRN Required");
            edit_email.requestFocus();
            return false;
        }

        /*else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
                && !TextUtils.isEmpty(input) && input.length() != 10)*/

        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
                && !input.matches(MOBILE_NUMBER_PATTERN) && !input.matches(MRN_PATTERN))
        {
            edit_email.setError("Invalid Email / Mobile No. / MRN");
            edit_email.requestFocus();
            return false;
        }

        return true;
    }

    //  reset password
    private void reset_password(String email)
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            new CustomAlertDialog(getContext(), ForgotPasswordFragment.this, getView()).snackbarForInternetConnectivity();
            return;
        }

        new HttpClient(HTTP_REQUEST_CODE_FORGOT_PASSWORD, MyApplication.HTTPMethod.POST.getValue(),
                UserAccount.composeEmailJSON(email),this)
                .execute(mController.getForgotPasswordURL());
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btnGo) {
            if (!isValid_Email_MobileNo_MRN(edit_email.getText().toString())) {
                return;
            }

            reset_password(edit_email.getText().toString());
        } else if (id == R.id.btnExistingUser) {
            page_listener.onPageSelection(0, "SIGN IN");
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
            if(requestCode == HTTP_REQUEST_CODE_FORGOT_PASSWORD && (responseCode == HttpClient.ACCEPTED || responseCode == HttpClient.OK))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);

                    Object json_obj = json.get(KEY_DATA);

                    if(json_obj instanceof JSONObject)
                    {
                        json = json.getJSONObject(KEY_DATA);

                        // If mobile no && email && mrn is there.
                        if(json.has(KEY_MASKED_EMAIL) && json.has(KEY_MASKED_MOBILE_NUMBER) && json.has(KEY_MRN))
                        {
                            String masked_email = json.getString(KEY_MASKED_EMAIL);
                            String masked_mobile_no = json.getString(KEY_MASKED_MOBILE_NUMBER);
                            String mrn = json.getString(KEY_MRN);

                            password_listener.onEmail(masked_email);
                            password_listener.onMobileNumber(masked_mobile_no);
                            password_listener.onMRNNo(mrn);
                            task_listener.onTaskCompleted(true, 0, message);
                            page_listener.onPageSelection(12, "RESET PASSWORD OPTIONS");
                        }

                        // If OTP is sent on mobile
                        else if(json.getBoolean(KEY_OTP_SENT))
                        {
                            password_listener.onMobileNumber(edit_email.getText().toString());
                            task_listener.onTaskCompleted(true, 0, message);
                            page_listener.onPageSelection(8, "RESET PASSWORD");
                        }

                        // link sent on email to reset password
                        else
                        {
                            task_listener.onTaskCompleted(true, 0, message);
                            page_listener.onPageSelection(3, "EMAIL CONFIRMATION");
                        }
                    }

                    // link sent on email to reset password
                    else
                    {
                        message = json.getString(KEY_DATA);
                        task_listener.onTaskCompleted(true, 0, message);
                        page_listener.onPageSelection(3, "EMAIL CONFIRMATION");
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(getContext(), ForgotPasswordFragment.this, getView()).handle(responseCode, response);
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
}