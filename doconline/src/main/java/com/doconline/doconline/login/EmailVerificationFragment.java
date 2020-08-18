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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
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
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_OTP_SENT;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_UPDATE_EMAIL;

/**
 * Created by chiranjit on 22/12/16.
 */
public class EmailVerificationFragment extends BaseFragment implements View.OnClickListener
{

    EditText editEmail;

    Button btnSubmit;

    Button btnExistingUser;

    private Context context = null;
    private OnHttpResponse response_listener;
    private OnPageSelection page_listener;
    private OnTaskCompleted task_listener;
    private OnForgetPasswordListener password_listener;


    public EmailVerificationFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public EmailVerificationFragment(Context context, OnPageSelection page_listener, OnHttpResponse response_listener, OnTaskCompleted task_listener, OnForgetPasswordListener password_listener)
    {
        this.context = context;
        this.response_listener = response_listener;
        this.page_listener = page_listener;
        this.task_listener = task_listener;
        this.password_listener = password_listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_email_verification, container, false);
      //  ButterKnife.bind(this, view);
        editEmail = view.findViewById(R.id.editEmail);
        btnSubmit= view.findViewById(R.id.btnGo);
        btnExistingUser =  view.findViewById(R.id.btnExistingUser);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        btnSubmit.setText("Send OTP");
        editEmail.setText(mController.getSession().getEmail());
        this.addListener();
    }


    private void addListener()
    {
        btnSubmit.setOnClickListener(this);
        btnExistingUser.setOnClickListener(this);
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btnGo) {
            String mEmail = editEmail.getText().toString();

            if (!this.isValidEmail(mEmail)) {
                return;
            }

            if (new InternetConnectionDetector(context).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_UPDATE_EMAIL, MyApplication.HTTPMethod.POST.getValue(),
                        UserAccount.composeEmailJSON(mEmail), EmailVerificationFragment.this)
                        .execute(mController.getEmailVerificationURL());
            } else {
                Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnExistingUser) {
            page_listener.onPageSelection(0, "SIGN IN");
        }
    }


    private boolean isValidEmail(String email)
    {
        if(email == null || TextUtils.isEmpty(email))
        {
            editEmail.setError("Email Required");
            editEmail.requestFocus();
            return false;
        }

        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editEmail.setError("Invalid Email");
            editEmail.requestFocus();
            return false;
        }

        return true;
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
            if(requestCode == HTTP_REQUEST_CODE_UPDATE_EMAIL && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    json= json.getJSONObject(KEY_DATA);

                    if(json.getBoolean(KEY_OTP_SENT))
                    {
                        password_listener.onEmail(json.getString(KEY_EMAIL));
                        task_listener.onTaskCompleted(true, 0, String.valueOf(message));
                        page_listener.onPageSelection(11, "OTP");
                    }
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
}