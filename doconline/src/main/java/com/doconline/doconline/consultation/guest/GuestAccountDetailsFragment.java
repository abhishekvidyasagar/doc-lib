package com.doconline.doconline.consultation.guest;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnBookConsultationListener;
import com.doconline.doconline.model.User;

import static com.doconline.doconline.app.Constants.PASSWORD_PATTERN;

/**
 * Created by chiranjitbardhan on 14/02/18.
 */

public class GuestAccountDetailsFragment extends BaseFragment implements View.OnClickListener
{

    TextView tv_step_one;
    EditText edit_first_name;
    EditText edit_last_name;
    EditText edit_email;
    EditText edit_password;
    EditText edit_confirm_password;
    EditText edit_mobile_no;
    Button btnNext;
    TextView tv_otp_label;
    LinearLayout layout_otp;
    Button btnSendOTP;
    EditText editFirstDigit;
    EditText editSecondDigit;
    EditText editThirdDigit;
    EditText editFourthDigit;
    EditText editFifthDigit;
    EditText editSixthDigit;
    Button btnTogglePassword;

    private OnBookConsultationListener page_listener;
    private User mProfile = new User();
    private boolean isPasswordVisible;


    public static GuestAccountDetailsFragment newInstance()
    {
        return new GuestAccountDetailsFragment();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof OnBookConsultationListener)
        {
            page_listener = (OnBookConsultationListener) getActivity();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_guest_account_details, container, false);
       // ButterKnife.bind(this, view);

        tv_step_one = view.findViewById(R.id.tv_step_one);
        edit_first_name= view.findViewById(R.id.editFirstName);
        edit_last_name= view.findViewById(R.id.editLastName);
        edit_email= view.findViewById(R.id.editEmail);
        edit_password= view.findViewById(R.id.editPassword);
        edit_confirm_password= view.findViewById(R.id.editConfirmPassword);
        edit_mobile_no= view.findViewById(R.id.editMobileNo);
        btnNext= view.findViewById(R.id.btnGo);
        tv_otp_label= view.findViewById(R.id.tv_otp_label);
        layout_otp= view.findViewById(R.id.layout_otp);
        btnSendOTP= view.findViewById(R.id.btnSendOTP);
        editFirstDigit= view.findViewById(R.id.editFirstDigit);
        editSecondDigit= view.findViewById(R.id.editSecondDigit);
        editThirdDigit= view.findViewById(R.id.editThirdDigit);
        editFourthDigit= view.findViewById(R.id.editFourthDigit);
        editFifthDigit= view.findViewById(R.id.editFifthDigit);
        editSixthDigit= view.findViewById(R.id.editSixthDigit);
        btnTogglePassword= view.findViewById(R.id.btnTogglePassword);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.hideKeyboardOnPasswordMatch(view);
        this.addListener();

        this.focusEnterOTP(editFirstDigit);
        this.focusEnterOTP(editSecondDigit);
        this.focusEnterOTP(editThirdDigit);
        this.focusEnterOTP(editFourthDigit);
        this.focusEnterOTP(editFifthDigit);
        this.focusEnterOTP(editSixthDigit);
        this.setEnable(false);

        btnNext.setText(R.string.Next);

        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPasswordVisible)
                {
                    edit_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnTogglePassword.setText("Hide");
                }

                else
                {
                    edit_confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnTogglePassword.setText("Show");
                }

                isPasswordVisible = !isPasswordVisible;
            }
        });
        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);

        if(!mProfile.getPhoneNo().isEmpty())
        {
            this.displayOTPOption();
        }

        else
        {
            this.hideOTPOption();
        }
    }


    private void addListener()
    {
        btnNext.setOnClickListener(this);
    }


    private void setEnable(boolean flag)
    {
        editSecondDigit.setEnabled(flag);
        editThirdDigit.setEnabled(flag);
        editFourthDigit.setEnabled(flag);
        editFifthDigit.setEnabled(flag);
        editSixthDigit.setEnabled(flag);
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

    private String getOTPCode()
    {
        return new StringBuilder()
                .append(editFirstDigit.getText().toString())
                .append(editSecondDigit.getText().toString())
                .append(editThirdDigit.getText().toString())
                .append(editFourthDigit.getText().toString())
                .append(editFifthDigit.getText().toString())
                .append(editSixthDigit.getText().toString()).toString();
    }


    @Override
    public void onClick(View view)
    {
        if(!new InternetConnectionDetector(getContext()).isConnected())
        {
            new CustomAlertDialog(getContext(), GuestAccountDetailsFragment.this, getView()).snackbarForInternetConnectivity();
            return;
        }

        if (view.getId() == R.id.btnGo) {
            this.initAccountData();

            if (!isValidFirstName(mProfile.getFirstName())) {
                return;
            }

            if (!isValidLastName(mProfile.getLastName())) {
                return;
            }

            if (mProfile.getEmail().isEmpty() && mProfile.getPhoneNo().isEmpty()) {
                Toast.makeText(getContext(), "Mobile No./Email Required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(mProfile.getEmail()) || !isValidMobileNo(mProfile.getPhoneNo())) {
                return;
            }

            if (isValidPassword(mProfile.getUserAccount().getPassword())
                    && isConfirmedPassword(mProfile.getUserAccount().getPassword(), edit_confirm_password.getText().toString())) {
                if (mProfile.getPhoneNo().isEmpty()) {
                    this.clearOTPFields();
                    this.hideOTPOption();
                }

                mProfile.getUserAccount().setOTPCode(getOTPCode());
                page_listener.onGuestDetails(mProfile);

                ((BookGuestConsultationActivity) getActivity()).onAcceptClick();
            }
        }
    }


    private void initAccountData()
    {
        mProfile.setFirstName(edit_first_name.getText().toString());
        mProfile.setLastName(edit_last_name.getText().toString());

        mProfile.setEmail(edit_email.getText().toString());
        mProfile.setPhoneNo(edit_mobile_no.getText().toString());
        mProfile.getUserAccount().setPassword(edit_password.getText().toString());
        mProfile.getUserAccount().setConfirmPassword(edit_confirm_password.getText().toString());
        mProfile.getUserAccount().setOTPCode(getOTPCode());
    }


    private boolean isValidMobileNo(String mobile_no)
    {
        if(!(mobile_no == null || TextUtils.isEmpty(mobile_no)) && mobile_no.length() != 10)
        {
            edit_mobile_no.setError("Must be 10 digits!");
            edit_mobile_no.requestFocus();
            return false;
        }

        /*if(!TextUtils.isEmpty(mobile_no) && mobile_no.length() != 10)
        {
            edit_mobile_no.setError("Must be 10 digits!");
            edit_mobile_no.requestFocus();
            return false;
        }*/

        return true;
    }


    private boolean isValidEmail(String email)
    {
        if(!(email == null || TextUtils.isEmpty(email)) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edit_email.setError("Invalid Email");
            edit_email.requestFocus();
            return false;
        }

        /*if(email == null || TextUtils.isEmpty(email))
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
        }*/

        return true;
    }

    private boolean isValidPassword(String password)
    {
        if(password.length() < 8)
        {
            edit_password.setError("Minimum 8 Characters");
            edit_password.requestFocus();
            return false;
        }

        if(password.contains(" "))
        {
            edit_password.setError("Blank spaces not allowed.");
            edit_password.requestFocus();
            return false;
        }

        if(!password.matches(PASSWORD_PATTERN))
        {
            edit_password.setError("Must contain at least one digit, uppercase letter, lowercase letter, and special character.");
            edit_password.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isConfirmedPassword(String password, String confirm_password)
    {
        if(!password.equals(confirm_password))
        {
            edit_confirm_password.setError("Password Confirmation Error");
            edit_confirm_password.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidFirstName(String f_name)
    {
        if(TextUtils.isEmpty(f_name))
        {
            edit_first_name.setError("Enter First Name");
            edit_first_name.requestFocus();
            return false;
        }

        if(f_name.trim().length() < 3)
        {
            edit_first_name.setError("Minimum 3 Characters!");
            edit_first_name.requestFocus();
            return false;
        }

        return true;
    }


    private boolean isValidLastName(String l_name)
    {
        if(TextUtils.isEmpty(l_name))
        {
            edit_last_name.setError("Enter Last Name");
            edit_last_name.requestFocus();
            return false;
        }

        return true;
    }


    private void hideKeyboardOnPasswordMatch(final View rootView) {

        edit_confirm_password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s)
            {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (edit_password.getText().toString().equals(edit_confirm_password.getText().toString()))
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


    public void displayOTPOption()
    {
        btnSendOTP.setVisibility(View.VISIBLE);
        layout_otp.setVisibility(View.VISIBLE);
        tv_otp_label.setVisibility(View.VISIBLE);
        editFirstDigit.requestFocus();
    }

    public void hideOTPOption()
    {
        btnSendOTP.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        tv_otp_label.setVisibility(View.GONE);
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

/*
    @OnClick(R.id.btnTogglePassword)
    public void onClick()
    {

    }*/
}