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
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_OTP_SENT;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_UPDATE_MOBILE_NO;

/**
 * Created by chiranjit on 22/12/16.
 */
public class MobileVerificationFragment extends BaseFragment implements View.OnClickListener
{

    EditText editMobileNo;

    Button btnSendOTP;

    Button btnExistingUser;

    private Context context = null;
    private OnHttpResponse response_listener;
    private OnPageSelection page_listener;
    private OnTaskCompleted task_listener;
    private OnForgetPasswordListener password_listener;

    private String mMobileNo = "";


    public MobileVerificationFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public MobileVerificationFragment(Context context, OnPageSelection page_listener, OnHttpResponse response_listener, OnTaskCompleted task_listener, OnForgetPasswordListener password_listener)
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
        View view = inflater.inflate(R.layout.fragment_mobile_verification, container, false);
       // ButterKnife.bind(this, view);
        editMobileNo = view.findViewById(R.id.editMobileNo);
        btnSendOTP = view.findViewById(R.id.btnGo);
         btnExistingUser = view.findViewById(R.id.btnExistingUser);

        hideKeyboard(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        btnSendOTP.setText("Send OTP");
        editMobileNo.setText(mController.getSession().getMobileNumber());

        this.addListener();
    }


    private void addListener()
    {
        btnSendOTP.setOnClickListener(this);
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
            this.mMobileNo = editMobileNo.getText().toString();

            if (!this.isValidMobileNo()) {
                return;
            }

            if (new InternetConnectionDetector(context).isConnected()) {
                new HttpClient(HTTP_REQUEST_CODE_UPDATE_MOBILE_NO, MyApplication.HTTPMethod.POST.getValue(),
                        UserAccount.composeMobileJSON(this.mMobileNo), MobileVerificationFragment.this)
                        .execute(mController.getMobileVerificationURL());
            } else {
                Toast.makeText(context, "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnExistingUser) {
            page_listener.onPageSelection(0, "SIGN IN");
        }
    }


    private boolean isValidMobileNo()
    {
        if(this.mMobileNo.isEmpty())
        {
            editMobileNo.setError("Enter mobile no");
            editMobileNo.requestFocus();
            return false;
        }

        if(this.mMobileNo.length() != 10)
        {
            editMobileNo.setError("Should be 10 digits");
            editMobileNo.requestFocus();
            return false;
        }

        return true;
    }


    private void hideKeyboard(final View rootView) {

        editMobileNo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editMobileNo.getText().toString().length() == 10)
                {
                    try
                    {
                        if(getActivity() != null)
                        {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                            if(imm != null)
                            {
                                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                            }
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
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
            if(requestCode == HTTP_REQUEST_CODE_UPDATE_MOBILE_NO && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);
                    json= json.getJSONObject(KEY_DATA);

                    if(json.getBoolean(KEY_OTP_SENT))
                    {
                        password_listener.onMobileNumber(this.mMobileNo);
                        task_listener.onTaskCompleted(true, 0, String.valueOf(message));
                        page_listener.onPageSelection(5, "OTP");
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