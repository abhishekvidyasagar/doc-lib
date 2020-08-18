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

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.model.User;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.PASSWORD_PATTERN;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_UPDATE_PASSWORD;

/**
 * Created by chiranjit on 22/12/16.
 */
public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener
{

    EditText edit_new_password;

    EditText edit_current_password;

    EditText edit_confirm_password;

    Button button_change;

    TextInputLayout input_layout_current_password;

    TextInputLayout input_layout_new_password;

    TextInputLayout input_layout_confirm_password;

    private Context context = null;
    private OnHttpResponse response_listener;
    private String password;


    public ChangePasswordFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public ChangePasswordFragment(Context context, OnHttpResponse response_listener)
    {
        this.context = context;
        this.response_listener = response_listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        //ButterKnife.bind(this, view);

         edit_new_password = view.findViewById(R.id.editNewPassword);
         edit_current_password= view.findViewById(R.id.editCurrentPassword);
         edit_confirm_password= view.findViewById(R.id.editConfirmPassword);
         button_change= view.findViewById(R.id.btnDone);
         input_layout_current_password= view.findViewById(R.id.input_layout_current_password);
        input_layout_new_password= view.findViewById(R.id.input_layout_new_password);
        input_layout_confirm_password= view.findViewById(R.id.input_layout_confirm_password);
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

        this.hideKeyboard(view);
        this.setOnClickListener();
        this.display_current_password_view();
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


    private void display_current_password_view()
    {
        if(getActivity() != null && !getActivity().isFinishing() && getView() != null)
        {
            getActivity().runOnUiThread(new Runnable() {

                public void run() {

                    try
                    {
                        if(mController.getSession().getPasswordStatus())
                        {
                            button_change.setText("Set Password");
                            edit_current_password.setVisibility(View.GONE);
                        }

                        else
                        {
                            button_change.setText("Change Password");
                            edit_current_password.setVisibility(View.VISIBLE);
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void setOnClickListener()
    {
        button_change.setOnClickListener(this);
    }


    private void hideKeyboard(final View rootView) {

        edit_confirm_password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edit_new_password.getText().toString().equals(edit_confirm_password.getText().toString())) {

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
    public void onClick(View view)
    {
        if(!new InternetConnectionDetector(context).isConnected())
        {
            new CustomAlertDialog(getContext(), ChangePasswordFragment.this, getView()).snackbarForInternetConnectivity();
            return;
        }

        if (view.getId() == R.id.btnDone) {
            String current_password = edit_current_password.getText().toString();

            password = edit_new_password.getText().toString();
            String confirm_password = edit_confirm_password.getText().toString();

            if (validate(current_password, password, confirm_password)) {
                new HttpClient(HTTP_REQUEST_CODE_UPDATE_PASSWORD, MyApplication.HTTPMethod.PUT.getValue(),
                        User.composePasswordJSON(current_password, password, confirm_password), this)
                        .execute(mController.getProfileURL() + Constants.KEY_PASSWORD);
            }
        }
    }


    private boolean validate(String current_password, String password, String confirm_password)
    {
        if(current_password.isEmpty() && edit_current_password.getVisibility() == View.VISIBLE)
        {
            edit_current_password.setError("Current Password Required!");
            edit_current_password.requestFocus();
            return false;
        }

        else
        {
            input_layout_current_password.setErrorEnabled(false);
        }

        if(password.trim().length() < 8)
        {
            edit_new_password.setError("Minimum 8 Characters!");
            edit_new_password.requestFocus();
            return false;
        }

        else
        {
            input_layout_new_password.setErrorEnabled(false);
        }

        if(password.contains(" "))
        {
            edit_new_password.setError("Blank spaces not allowed.");
            edit_new_password.requestFocus();
            return false;
        }

        else
        {
            input_layout_new_password.setErrorEnabled(false);
        }

        if(!password.matches(PASSWORD_PATTERN))
        {
            edit_new_password.setError("Must contain at least one digit, uppercase letter, lowercase letter, and special character.");
            edit_new_password.requestFocus();
            return false;
        }

        else
        {
            input_layout_new_password.setErrorEnabled(false);
        }


        if(!password.equals(confirm_password))
        {
            edit_confirm_password.setError("Password Confirmation Error!");
            edit_confirm_password.requestFocus();
            return false;
        }

        else
        {
            input_layout_confirm_password.setErrorEnabled(false);
        }

        if(edit_current_password.getVisibility() == View.VISIBLE && password.equals(current_password))
        {
            new CustomAlertDialog(getContext(), this, getView()).showSnackbar("Password must be different", CustomAlertDialog.LENGTH_SHORT);
            return false;
        }

        return true;
    }


    private void clear_fields()
    {
        edit_new_password.getText().clear();
        edit_confirm_password.getText().clear();
        edit_current_password.getText().clear();
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
            if(requestCode == HTTP_REQUEST_CODE_UPDATE_PASSWORD && responseCode == HttpClient.OK)
            {
                mController.getSession().changePassword(password);
                mController.getSession().putPasswordStatus(false);

                this.display_current_password_view();
                this.clear_fields();

                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString(KEY_DATA);

                    new CustomAlertDialog(getContext(), this, getView()).showSnackbar(message, CustomAlertDialog.LENGTH_LONG);
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