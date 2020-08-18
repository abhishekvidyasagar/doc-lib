package com.doconline.doconline.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_OTP_SENT;
import static com.doconline.doconline.login.MainActivity.HTTP_REQUEST_CODE_FORGOT_PASSWORD;

/**
 * Created by chiranjit on 22/12/16.
 */
public class ResetPasswordOptionsFragment extends BaseFragment implements View.OnClickListener
{

    RelativeLayout layout_email;

    RelativeLayout layout_mobile;

    TextView tv_masked_email;

    TextView tv_masked_mobile_no;

    Button button_submit;

    Button button_existing_user;

    RadioButton radio_mobile;

    RadioButton radio_email;

    private OnPageSelection page_listener;
    private OnHttpResponse response_listener;
    private OnTaskCompleted task_listener;
    private OnForgetPasswordListener password_listener;

    private String masked_mobile_no;
    private String masked_email;
    private String mrn_no;


    public ResetPasswordOptionsFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public ResetPasswordOptionsFragment(String masked_mobile_no, String masked_email, String mrn_no)
    {
        this.masked_mobile_no = masked_mobile_no;
        this.masked_email = masked_email;
        this.mrn_no = mrn_no;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_reset_password_options, container, false);
       // ButterKnife.bind(this, view);

         layout_email = view.findViewById(R.id.layout_email);
         layout_mobile= view.findViewById(R.id.layout_mobile);
         tv_masked_email= view.findViewById(R.id.tv_masked_email);
         tv_masked_mobile_no= view.findViewById(R.id.tv_masked_mobile_no);
        button_submit= view.findViewById(R.id.btnGo);
         button_existing_user= view.findViewById(R.id.btnExistingUser);
        radio_mobile= view.findViewById(R.id.radio_mobile);
        radio_email= view.findViewById(R.id.radio_email);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.addListener();
        button_submit.setText(R.string.button_continue);

        tv_masked_email.setText(masked_email);
        tv_masked_mobile_no.setText(masked_mobile_no);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof OnHttpResponse) {
            this.response_listener = (OnHttpResponse) getActivity();
        }

        if (getActivity() instanceof OnTaskCompleted) {
            this.task_listener = (OnTaskCompleted) getActivity();
        }

        if(getActivity() instanceof OnPageSelection)
        {
            this.page_listener = (OnPageSelection) getActivity();
        }

        if(getActivity() instanceof OnForgetPasswordListener)
        {
            this.password_listener = (OnForgetPasswordListener) getActivity();
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
        button_existing_user.setOnClickListener(this);

        layout_email.setOnClickListener(this);
        layout_mobile.setOnClickListener(this);
        radio_email.setOnClickListener(this);
        radio_mobile.setOnClickListener(this);
    }


    private boolean isValid()
    {
        if(!radio_mobile.isChecked() && !radio_email.isChecked())
        {
            Toast.makeText(getContext(), "Select Reset Option", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // api is called
    private void reset_password_option(String reset_option)
    {
        if(!new InternetConnectionDetector(getContext()).isConnected())
        {
            new CustomAlertDialog(getContext(), ResetPasswordOptionsFragment.this, getView()).snackbarForInternetConnectivity();
            return;
        }

        new HttpClient(HTTP_REQUEST_CODE_FORGOT_PASSWORD, MyApplication.HTTPMethod.POST.getValue(),
                UserAccount.composePasswordResetOptionJSON(mrn_no, reset_option),this)
                .execute(mController.getForgotPasswordURL());
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btnGo) {
            if (!this.isValid()) {
                return;
            }

            if (radio_email.isChecked()) {
                reset_password_option("email");
            } else {
                reset_password_option("sms");
            }
        } else if (id == R.id.btnExistingUser) {
            page_listener.onPageSelection(0, "SIGN IN");
        } else if (id == R.id.layout_email) {
            radio_email.setChecked(true);
            radio_mobile.setChecked(false);
        } else if (id == R.id.layout_mobile) {
            radio_email.setChecked(false);
            radio_mobile.setChecked(true);
        } else if (id == R.id.radio_email) {
            radio_email.setChecked(true);
            radio_mobile.setChecked(false);
        } else if (id == R.id.radio_mobile) {
            radio_email.setChecked(false);
            radio_mobile.setChecked(true);
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

            if(requestCode == HTTP_REQUEST_CODE_FORGOT_PASSWORD && responseCode == HttpClient.ACCEPTED)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_MESSAGE);

                    Object json_obj = json.get(KEY_DATA);

                    if(json_obj instanceof JSONObject)
                    {
                        json = json.getJSONObject(KEY_DATA);

                        // accordingly it will be redirected to different pages
                        if(json.getBoolean(KEY_OTP_SENT))
                        {
                            password_listener.onMRNNo(mrn_no);
                            task_listener.onTaskCompleted(true, 0, message);
                            page_listener.onPageSelection(8, "RESET PASSWORD");
                        }

                        else
                        {
                            task_listener.onTaskCompleted(true, 0, message);
                            page_listener.onPageSelection(3, "EMAIL CONFIRMATION");
                        }
                    }

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

            new HttpResponseHandler(getContext(), ResetPasswordOptionsFragment.this, getView()).handle(responseCode, response);
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