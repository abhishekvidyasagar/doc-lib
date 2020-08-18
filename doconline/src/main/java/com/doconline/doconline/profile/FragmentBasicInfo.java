package com.doconline.doconline.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.DatePickerFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.Address;
import com.doconline.doconline.model.PreferredLanguage;
import com.doconline.doconline.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.TYPE_FEMALE;
import static com.doconline.doconline.app.Constants.TYPE_MALE;
import static com.doconline.doconline.app.Constants.TYPE_TRANSGENDER;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_GET_NAME_PREFIX;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_GET_PROFILE;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_UPDATE_PROFILE;

/**
 * Created by chiranjit on 27/04/17.
 */
public class FragmentBasicInfo extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener
{

    LinearLayout language_layout;
    TextView tv_dob;
    EditText edit_first_name;
    EditText edit_last_name;
    EditText edit_middle_name;
    TextInputLayout input_layout_email;
    TextInputLayout input_layout_mobile_no;
    LinearLayout datepicker_layout;
    AppCompatSpinner spinner_gender;
    AppCompatSpinner spinner_name_prefix;
    TextView tv_language;
    TextView tv_default_language;
    RelativeLayout layout_loading;
    Button btn_save;
    TextView label_mrn;
    LinearLayout default_mrn_layout;
    TextView tv_mrn;


    private ArrayList<String> prefixes = new ArrayList<>();
    private boolean IS_AUTH_SYNC = true;
    private String dob;
    private User mUser;


    public static FragmentBasicInfo newInstance()
    {
        return new FragmentBasicInfo();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_basic_info, container, false);
       // ButterKnife.bind(this, view);
         language_layout = view.findViewById(R.id.language_layout);
         tv_dob= view.findViewById(R.id.tv_dob);
         edit_first_name= view.findViewById(R.id.editFirstName);
        edit_last_name= view.findViewById(R.id.editLastName);
         edit_middle_name= view.findViewById(R.id.editMiddleName);
       input_layout_email= view.findViewById(R.id.input_layout_email);
       input_layout_mobile_no= view.findViewById(R.id.input_layout_mobile_no);
         datepicker_layout= view.findViewById(R.id.datepicker_layout);
        spinner_gender= view.findViewById(R.id.spinner_gender);
        spinner_name_prefix= view.findViewById(R.id.spinner_name_prefix);
         tv_language= view.findViewById(R.id.tv_language);
         tv_default_language= view.findViewById(R.id.tv_default_language);
         layout_loading= view.findViewById(R.id.layout_loading);
         btn_save= view.findViewById(R.id.btnDone);
         label_mrn= view.findViewById(R.id.label_mrn);
         default_mrn_layout= view.findViewById(R.id.default_mrn_layout);
         tv_mrn= view.findViewById(R.id.tv_mrn);

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

        input_layout_email.setVisibility(View.GONE);
        input_layout_mobile_no.setVisibility(View.GONE);
        btn_save.setText("Update Personal Info");

        // get user's profile from SQLite
        this.mUser = mController.getSQLiteHelper().getUserProfile();

        // setting data in gender spinner
        this.populateGenderSpinner();

        // setting data in name prefix list
        this.init_prefix_list(this.mController.getNamePrefixList());
        // setting data in user profile form
        this.display_user_profile();

        this.addListener();
        // sync the data which are needed
        this.syncData();
    }


    private void populateGenderSpinner()
    {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, getResources().getStringArray(R.array.gender_of_user));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinner_gender.setAdapter(adapter);
        this.spinner_gender.setOnItemSelectedListener(this);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        tv_language.setText(mController.getSession().getLanguagePreferenceValue());
        tv_default_language.setText(PreferredLanguage.getDefaultLanguageName());
    }


    private void addListener()
    {
        language_layout.setOnClickListener(this);
        datepicker_layout.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }


    private void syncData()
    {
        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            // get profile data
            new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getProfileURL());
            // to get name prefix list
            new HttpClient(HTTP_REQUEST_CODE_GET_NAME_PREFIX, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getNamePrefixURL());
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
    }


    private void display_user_profile()
    {
        if(mUser != null)
        {
            dob = mUser.getDateOfBirth();

            edit_first_name.setText(mUser.getFirstName());
            edit_middle_name.setText(mUser.getMiddleName());
            edit_last_name.setText(mUser.getLastName());

            tv_dob.setText(Helper.dateFormat(dob));

            /*if(!mController.getSession().getMRN().isEmpty())
            {
                label_mrn.setVisibility(View.VISIBLE);
                default_mrn_layout.setVisibility(View.VISIBLE);
                tv_mrn.setText(mController.getSession().getMRN());
            }*/

            if(mUser.getGender().equalsIgnoreCase(TYPE_MALE))
            {
                spinner_gender.setSelection(1, true);
            }

            else if(mUser.getGender().equalsIgnoreCase(TYPE_FEMALE))
            {
                spinner_gender.setSelection(2, true);
            }

            else if(mUser.getGender().equalsIgnoreCase(TYPE_TRANSGENDER))
            {
                spinner_gender.setSelection(3, true);
            }

            else
            {
                spinner_gender.setSelection(0, true);
            }

            spinner_name_prefix.setSelection(User.get_name_prefix_index(prefixes, mUser.getNamePrefix()), true);
        }
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

        catch (Exception e)
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
                this.spinner_name_prefix.setOnItemSelectedListener(this);
                this.spinner_name_prefix.setSelection(User.get_name_prefix_index(prefixes, mUser.getNamePrefix()), true);
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
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.language_layout) {
            startActivity(new Intent(getActivity(), PreferredLanguageActivity.class));
        } else if (id == R.id.datepicker_layout) {
            showDatePicker(tv_dob.getText().toString());
        } else if (id == R.id.btnDone) {
            if (!this.init_profile()) {
                return;
            }

            if (!new InternetConnectionDetector(getContext()).isConnected()) {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
                return;
            }

            IS_AUTH_SYNC = false;

            layout_loading.setVisibility(View.VISIBLE);
            new HttpClient(HTTP_REQUEST_CODE_UPDATE_PROFILE, MyApplication.HTTPMethod.PATCH.getValue(), User.composeUserJSON(mUser), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getProfileURL());
        } else if (id == R.id.ib_edit) {
            startActivity(new Intent(getActivity(), PreferredLanguageActivity.class));
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
            args.putInt("max_year", calender.get(Calendar.YEAR) - 18);

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

            Log.i("CURRENT_DATE", calender.get(Calendar.YEAR) + "/" + calender.get(Calendar.MONTH) + "/" + calender.get(Calendar.DAY_OF_MONTH));

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
        }
    };


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

        if(spinner_gender.getSelectedItemPosition() == 0)
        {
            showSnackbar("Select Gender!");
            return false;
        }

        return true;
    }


    private boolean init_profile()
    {
        if(!validate())
        {
            return false;
        }

        mUser.setNamePrefix(spinner_name_prefix.getSelectedItem().toString());
        mUser.setFirstName(edit_first_name.getText().toString());
        mUser.setMiddleName(edit_middle_name.getText().toString());
        mUser.setLastName(edit_last_name.getText().toString());
        mUser.setDateOfBirth(dob);

        if(spinner_gender.getSelectedItemPosition() == 1)
        {
            mUser.setGender(TYPE_MALE);
        }

        else if(spinner_gender.getSelectedItemPosition() == 2)
        {
            mUser.setGender(TYPE_FEMALE);
        }
        else
        {
            mUser.setGender(TYPE_TRANSGENDER);
        }

        return true;
    }


    @Override
    public void onPreExecute()
    {

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
            if(requestCode == HTTP_REQUEST_CODE_GET_NAME_PREFIX && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.mController.getSession().putNamePrefixList(json.getString(KEY_DATA));
                    init_prefix_list(json.getString(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if((requestCode == HTTP_REQUEST_CODE_GET_PROFILE || requestCode == HTTP_REQUEST_CODE_UPDATE_PROFILE)
                    && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                JSONObject json = new JSONObject(response);

                User user = User.getUserFromJSON(json.getString(KEY_DATA));
                User userAddress = Address.getUserAddressFromJSON(json.getString(KEY_DATA));

                mUser.setUserId(user.getUserId());
                mUser.setNamePrefix(user.getNamePrefix());
                mUser.setFirstName(user.getFirstName());
                mUser.setMiddleName(user.getMiddleName());
                mUser.setLastName(user.getLastName());
                mUser.setFullName(user.getFullName());
                mUser.setDateOfBirth(user.getDateOfBirth());
                mUser.setGender(user.getGender());

                mUser.getAddress().setAddress_1(userAddress.getAddress().getAddress_1());
                mUser.getAddress().setAddress_2(userAddress.getAddress().getAddress_2());
                mUser.getAddress().setState(userAddress.getAddress().getState());
                mUser.getAddress().setCity(userAddress.getAddress().getCity());
                mUser.getAddress().setPincode(userAddress.getAddress().getPincode());
                mUser.getAddress().setCountryCode(userAddress.getAddress().getCountryCode());

                mUser.setPhoneNo(userAddress.getPhoneNo());
                mUser.setAlternatePhoneNo(userAddress.getAlternatePhoneNo());

                this.mController.getSession().changeFullName(mUser.getFullName());

                if(!this.mController.getSQLiteHelper().insert(mUser))
                {
                    this.mController.getSQLiteHelper().update(mUser);

                    if(!IS_AUTH_SYNC)
                    {
                        Toast.makeText(getContext(), "Profile Information Updated", Toast.LENGTH_SHORT).show();
                    }
                }

                return;
            }

            new HttpResponseHandler(getActivity(), this, getView()).handle(responseCode, response);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            IS_AUTH_SYNC = true;

            getActivity().runOnUiThread(new Runnable() {

                public void run() {

                    display_user_profile();
                    layout_loading.setVisibility(View.GONE);
                }
            });
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