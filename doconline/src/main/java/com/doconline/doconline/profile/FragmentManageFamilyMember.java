package com.doconline.doconline.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.DatePickerFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.FamilyMember;
import com.doconline.doconline.model.User;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import static com.doconline.doconline.app.Constants.FAMILY_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_IS_MINOR;
import static com.doconline.doconline.app.Constants.KEY_NOTIFY;
import static com.doconline.doconline.app.Constants.TYPE_FEMALE;
import static com.doconline.doconline.app.Constants.TYPE_MALE;
import static com.doconline.doconline.app.Constants.TYPE_TRANSGENDER;
import static com.doconline.doconline.profile.FamilyMemberActivity.HTTP_REQUEST_CODE_GET_NAME_PREFIX;
import static com.doconline.doconline.profile.FamilyMemberActivity.HTTP_REQUEST_CODE_SAVE_PROFILE;
import static com.doconline.doconline.profile.FamilyMemberActivity.HTTP_REQUEST_CODE_UPDATE_PROFILE;

/**
 * Created by chiranjitbardhan on 10/08/17.
 */

public class FragmentManageFamilyMember extends BaseFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener
{

    LinearLayout language_layout;
    LinearLayout default_language_layout;
    TextView label_default_language;
    TextView label_preferred_language;
    TextView tv_dob;
    EditText edit_first_name;
    EditText edit_last_name;
    EditText edit_middle_name;
    EditText edit_email;
    EditText edit_mobile_no;
    TextView tv_email_validation_label;
    LinearLayout datepicker_layout;
    AppCompatSpinner spinner_gender;
    AppCompatSpinner spinner_name_prefix;
    Button btn_save;

    private OnHttpResponse response_listener;
    private String dob;
    private ArrayList<String> prefixes = new ArrayList<>();

    private FamilyMember mFamilyMember;
    private boolean add_new_member = false;


    public static FragmentManageFamilyMember newInstance(FamilyMember mFamilyMember)
    {
        FragmentManageFamilyMember myFragment =  new FragmentManageFamilyMember();

        Bundle args = new Bundle();
        args.putSerializable("MEMBER", mFamilyMember);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() == null)
        {
            return;
        }

        this.mFamilyMember = (FamilyMember) getArguments().getSerializable("MEMBER");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof OnHttpResponse)
        {
            response_listener = (OnHttpResponse) getActivity();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_manage_family_member, container, false);
       // ButterKnife.bind(this, view);
        language_layout =  view.findViewById(R.id.language_layout);
         default_language_layout=  view.findViewById(R.id.default_language_layout);
         label_default_language=  view.findViewById(R.id.label_default_language);
        label_preferred_language=  view.findViewById(R.id.label_preferred_language);
         tv_dob=  view.findViewById(R.id.tv_dob);
         edit_first_name=  view.findViewById(R.id.editFirstName);
        edit_last_name=  view.findViewById(R.id.editLastName);
         edit_middle_name=  view.findViewById(R.id.editMiddleName);
         edit_email=  view.findViewById(R.id.editEmail);
         edit_mobile_no=  view.findViewById(R.id.editMobileNo);
        tv_email_validation_label=  view.findViewById(R.id.tv_email_validation_label);
         datepicker_layout=  view.findViewById(R.id.datepicker_layout);
        spinner_gender=  view.findViewById(R.id.spinner_gender);
       spinner_name_prefix=  view.findViewById(R.id.spinner_name_prefix);
        btn_save=  view.findViewById(R.id.btnDone);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.setHasOptionsMenu(true);
        this.addListener();

        language_layout.setVisibility(View.GONE);
        default_language_layout.setVisibility(View.GONE);
        label_default_language.setVisibility(View.GONE);
        label_preferred_language.setVisibility(View.GONE);

        btn_save.setText("Save Family Member Info");

        this.init_prefix_list(mController.getNamePrefixList());
        this.populateGenderSpinner();

        if(mFamilyMember != null)
        {
            this.add_new_member = false;
            display_data();
        }

        else
        {
            this.add_new_member = true;
            this.mFamilyMember = new FamilyMember();
        }


        if(new InternetConnectionDetector(getContext()).isConnected() && spinner_name_prefix.getCount() <= 0)
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_NAME_PREFIX, MyApplication.HTTPMethod.GET.getValue(), this).execute(mController.getNamePrefixURL());
        }
    }


    private void addListener()
    {
        datepicker_layout.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    private void populateGenderSpinner()
    {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, getResources().getStringArray(R.array.gender_of_user));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinner_gender.setAdapter(adapter);
        this.spinner_gender.setOnItemSelectedListener(this);
    }


    private void showSnackbar(String msg)
    {
        if(getContext() == null)
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.datepicker_layout) {
            showDatePicker(tv_dob.getText().toString());
        } else if (id == R.id.btnDone) {
            if (validate()) {
                this.initFamilyMember();

                if (new InternetConnectionDetector(getContext()).isConnected()) {
                    if (add_new_member) {
                        new HttpClient(HTTP_REQUEST_CODE_SAVE_PROFILE, MyApplication.HTTPMethod.POST.getValue(), FamilyMember.composeFamilyMemberJSON(mFamilyMember), this).execute(mController.getFamilyURL());
                    } else {
                        new HttpClient(HTTP_REQUEST_CODE_UPDATE_PROFILE, MyApplication.HTTPMethod.PATCH.getValue(), FamilyMember.composeFamilyMemberJSON(mFamilyMember), this).execute(mController.getFamilyURL() + mFamilyMember.getMemberId());
                    }

                    return;
                }

                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }


    private void showDatePicker(String display_date)
    {
        try
        {
            DatePickerFragment date = new DatePickerFragment();

            Calendar calender = Calendar.getInstance();

            Bundle args = new Bundle();
            args.putInt("min_year", calender.get(Calendar.YEAR) - 100);
            args.putInt("max_year", calender.get(Calendar.YEAR) - 3);

            if(!display_date.isEmpty())
            {
                String[] temp = display_date.split("/");

                args.putInt("year", Integer.valueOf(temp[2]));
                args.putInt("month", (Integer.valueOf(temp[1])-1));
                args.putInt("day", Integer.valueOf(temp[0]));
            }

            else
            {
                args.putInt("year", calender.get(Calendar.YEAR));
                args.putInt("month", calender.get(Calendar.MONTH));
                args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
            }

            date.setArguments(args);

            date.setCallBack(ondate);
            date.show(getActivity().getSupportFragmentManager(), "Select Date of Birth");
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {

            Calendar now = Calendar.getInstance();

            Calendar choosen = Calendar.getInstance();
            choosen.set(year, monthOfYear, dayOfMonth);

            if (choosen.compareTo(now) >= 0)
            {
                Toast.makeText(getContext(), "Invalid Selection", Toast.LENGTH_SHORT).show();
                return;
            }

            // Initialize date variable to current date
            dob = new StringBuilder().append(Helper.format_date(year)).append("-").append(Helper.format_date(monthOfYear + 1)).append("-").append(Helper.format_date(dayOfMonth)).toString();
            tv_dob.setText(Helper.dateFormat(dob));

            if(Helper.getYearDiff(dob) < 16)
            {
                tv_email_validation_label.setVisibility(View.VISIBLE);
            }

            else
            {
                tv_email_validation_label.setVisibility(View.GONE);
            }
        }
    };


    private void display_data()
    {
        dob = mFamilyMember.getDateOfBirth();

        edit_first_name.setText(mFamilyMember.getFirstName());
        edit_middle_name.setText(mFamilyMember.getMiddleName());
        edit_last_name.setText(mFamilyMember.getLastName());
        edit_email.setText(mFamilyMember.getEmail());
        edit_mobile_no.setText(mFamilyMember.getPhoneNo());
        tv_dob.setText(Helper.dateFormat(dob));


        if(mFamilyMember.getGender().equalsIgnoreCase("Male"))
        {
            spinner_gender.setSelection(1, true);
        }

        else if(mFamilyMember.getGender().equalsIgnoreCase("Female"))
        {
            spinner_gender.setSelection(2, true);
        }

        else if(mFamilyMember.getGender().equalsIgnoreCase("Transgender"))
        {
            spinner_gender.setSelection(3, true);
        }

        else
        {
            spinner_gender.setSelection(0, true);
        }
    }


    private void initFamilyMember()
    {
        mFamilyMember.setNamePrefix(spinner_name_prefix.getSelectedItem().toString());
        mFamilyMember.setFirstName(edit_first_name.getText().toString());
        mFamilyMember.setMiddleName(edit_middle_name.getText().toString());
        mFamilyMember.setLastName(edit_last_name.getText().toString());
        mFamilyMember.setDateOfBirth(dob);
        mFamilyMember.setEmail(edit_email.getText().toString());
        mFamilyMember.setPhoneNo(edit_mobile_no.getText().toString());

        if(spinner_gender.getSelectedItemPosition() == 1)
        {
            mFamilyMember.setGender(TYPE_MALE);
        }

        else if(spinner_gender.getSelectedItemPosition() == 2)
        {
            mFamilyMember.setGender(TYPE_FEMALE);
        }

        else
        {
            mFamilyMember.setGender(TYPE_TRANSGENDER);
        }
    }


    private boolean isValidEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validate()
    {
        if(spinner_name_prefix.getSelectedItemPosition() == -1)
        {
            showSnackbar("Select Name Prefix!");
            return false;
        }

        if(edit_first_name.getText().toString().trim().length() < 3)
        {
            edit_first_name.setError("Minimum 3 Characters!");
            edit_first_name.requestFocus();
            return false;
        }

        if(edit_last_name.getText().toString().trim().isEmpty())
        {
            edit_last_name.setError("Last Name Required!");
            edit_last_name.requestFocus();
            return false;
        }

        if(tv_dob.getText().toString().trim().isEmpty())
        {
            showSnackbar("Select Date of Birth!");
            return false;
        }

        if(Helper.getYearDiff(dob) >= 16 && edit_email.getText().toString().isEmpty() && edit_mobile_no.getText().toString().isEmpty())
        {
            Toast.makeText(getContext(), "Email/Mobile No. Required!", Toast.LENGTH_SHORT).show();
            //edit_email.setError("Email Required!");
            edit_email.requestFocus();
            return false;
        }

        if(!edit_email.getText().toString().isEmpty() && !isValidEmail(edit_email.getText().toString()))
        {
            edit_email.setError("Invalid Email!");
            edit_email.requestFocus();
            return false;
        }

        if(!edit_mobile_no.getText().toString().isEmpty() && edit_mobile_no.getText().toString().length() != 10)
        {
            edit_mobile_no.setError("Must be 10 digits!");
            edit_mobile_no.requestFocus();
            return false;
        }

        if(spinner_gender.getSelectedItemPosition() == 0)
        {
            showSnackbar("Select Gender");
            return false;
        }

        return true;
    }


    private void init_prefix_list(String json_data)
    {
        try
        {
            prefixes.clear();

            JSONObject json = new JSONObject(json_data);

            Iterator<String> keys= json.keys();

            while (keys.hasNext())
            {
                String key = keys.next();
                String prefix = json.getString(key);

                prefixes.add(prefix);
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            if(getActivity() != null && !getActivity().isFinishing())
            {
                ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, prefixes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                this.spinner_name_prefix.setAdapter(adapter);
                this.spinner_name_prefix.setOnItemSelectedListener(FragmentManageFamilyMember.this);

                if(mFamilyMember != null)
                {
                    this.spinner_name_prefix.setSelection(User.get_name_prefix_index(prefixes, mFamilyMember.getNamePrefix()), true);
                }
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

    }

    public void onNothingSelected(AdapterView<?> arg0)
    {

    }


    @Override
    public void onPreExecute()
    {
        if(response_listener != null)
        {
            response_listener.onPreExecute();
        }
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
            if (requestCode == HTTP_REQUEST_CODE_GET_NAME_PREFIX && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    mController.getSession().putNamePrefixList(json.getString(KEY_DATA));
                    init_prefix_list(json.getString(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if((requestCode == HTTP_REQUEST_CODE_SAVE_PROFILE || requestCode == HTTP_REQUEST_CODE_UPDATE_PROFILE)
                    && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    if((json.has(KEY_NOTIFY) && json.getBoolean(KEY_NOTIFY))
                            || (json.has(KEY_IS_MINOR) && !json.getBoolean(KEY_IS_MINOR)))
                    {
                        this.success_alert("Successful", "Activation information is sent to your family member Mobile Number/Email ID. Please ensure your Family member activates his/her account in order to use DocOnline’s services.");
                    }

                    else
                    {
                        Toast.makeText(getContext(), "Successfully Saved", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent();
                        getActivity().setResult(FAMILY_REQUEST_CODE, intent);
                        getActivity().finish();
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                /*if(Helper.getYearDiff(dob) < 16 && this.mFamilyMember.getEmail().isEmpty())
                {
                    Toast.makeText(getContext(), "Successfully Added", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    getActivity().setResult(FAMILY_REQUEST_CODE, intent);
                    getActivity().finish();
                }

                else
                {
                    this.success_alert("Successful", "Activation link is sent to your family member mail id.");
                }*/

                return;
            }

            if(response_listener == null)
            {
                new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            if(response_listener != null)
            {
                response_listener.onPostExecute(requestCode, responseCode , response);
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


    private void success_alert(String title, String content)
    {
        if(getActivity() != null)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(title);
            alert.setMessage(content);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent();
                    getActivity().setResult(FAMILY_REQUEST_CODE, intent);
                    getActivity().finish();
                }
            });
            alert.show();


            /*new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(title)
                    .setContentText(content)
                    .setConfirmText("Ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog)
                        {
                            Intent intent = new Intent();
                            getActivity().setResult(FAMILY_REQUEST_CODE, intent);
                            getActivity().finish();

                            sDialog.dismissWithAnimation();
                            getActivity().finish();
                        }
                    })
                    .show();*/
        }
    }
}