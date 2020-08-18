package com.doconline.doconline.profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.model.Address;
import com.doconline.doconline.model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_SHIPPING_ADDRESS;
import static com.doconline.doconline.app.Constants.PINCODE_PATTERN;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_GET_COUNTRY_LIST;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_GET_PROFILE;
import static com.doconline.doconline.profile.ProfileActivity.HTTP_REQUEST_CODE_UPDATE_PROFILE;


/**
 * Created by chiranjit on 27/04/17.
 */
public class FragmentContactInfo extends BaseFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener
{

    EditText edit_address_1;

    EditText edit_address_2;

    EditText edit_city;

    EditText edit_state;

    EditText edit_pincode;

    EditText  edit_alternate_phone_no;

    AppCompatSpinner spinner_country;

    RelativeLayout layout_loading;

    TextInputLayout input_layout_address_1;

    TextInputLayout input_layout_city;

    TextInputLayout input_layout_state;

    TextInputLayout input_layout_pincode;

    Button btn_save;

    private List<Address> country_list = new ArrayList<>();

    private int country_index = 0;
    private boolean IS_AUTH_SYNC = true;
    private User mUser;


    public static FragmentContactInfo newInstance()
    {
        return new FragmentContactInfo();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contact_info, container, false);
       // ButterKnife.bind(this, view);
         edit_address_1 =  view.findViewById(R.id.editAddress_1) ;
        edit_address_2=  view.findViewById(R.id.editAddress_2);
         edit_city=  view.findViewById(R.id.editCity);
         edit_state=  view.findViewById(R.id.editState);
         edit_pincode=  view.findViewById(R.id.editPincode);
          edit_alternate_phone_no=  view.findViewById(R.id.editAlternatePhoneNumber);
         spinner_country=  view.findViewById(R.id.spinner_country);
         layout_loading=  view.findViewById(R.id.layout_loading);
        input_layout_address_1=  view.findViewById(R.id.input_layout_address_1);
        input_layout_city=  view.findViewById(R.id.input_layout_city);
         input_layout_state=  view.findViewById(R.id.input_layout_state);
        input_layout_pincode=  view.findViewById(R.id.input_layout_pincode);
        btn_save=  view.findViewById(R.id.btnDone);

        return  view;
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

        btn_save.setOnClickListener(this);

        btn_save.setText("Update Contact Info");

        this.mUser = mController.getSQLiteHelper().getUserProfile();
        // set country list into spinner
        this.init_country_list(this.mController.getCountryList());
        // set data in the form
        this.display_user_profile();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    // when fragment is shown, load the data.
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown() && isVisibleToUser)
        {
            if(new InternetConnectionDetector(getContext()).isConnected())
            {
                new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getProfileURL());

                if(spinner_country.getCount() <= 1)
                {
                    new HttpClient(HTTP_REQUEST_CODE_GET_COUNTRY_LIST, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getCountryURL());
                }
            }
        }
    }

    // set country list into spinner
    private void init_country_list(String json_data)
    {
        ArrayList<String> countries = new ArrayList<>();
        countries.add("Select Country");

        try
        {
            country_list.clear();
            country_list.addAll(ProfileActivity.init_country_list(json_data));

            for(Address address: country_list)
            {
                countries.add(address.getCountry());
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
                ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, countries);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                this.spinner_country.setAdapter(adapter);
                this.spinner_country.setOnItemSelectedListener(this);
                this.spinner_country.setSelection(Address.get_country_index(country_list, mUser.getAddress().getCountryCode()));
            }
        }
    }

    private void display_user_profile()
    {
        try
        {
            if(mUser != null)
            {
                edit_address_1.setText(mUser.getAddress().getAddress_1());
                edit_address_2.setText(mUser.getAddress().getAddress_2());
                edit_city.setText(mUser.getAddress().getCity());
                edit_state.setText(mUser.getAddress().getState());
                edit_pincode.setText(mUser.getAddress().getPincode());
                edit_alternate_phone_no.setText(mUser.getAlternatePhoneNo());

                spinner_country.setSelection(Address.get_country_index(country_list, mUser.getAddress().getCountryCode()));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        this.country_index = position;
    }

    public void onNothingSelected(AdapterView<?> arg0)
    {

    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnDone) {
            if (!this.init_profile()) {
                return;
            }

            if (!new InternetConnectionDetector(getContext()).isConnected()) {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
                return;
            }

            IS_AUTH_SYNC = false;
            layout_loading.setVisibility(View.VISIBLE);
            // update the profile
            new HttpClient(HTTP_REQUEST_CODE_UPDATE_PROFILE, MyApplication.HTTPMethod.PATCH.getValue(), Address.composeAddressMap(mUser), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mController.getProfileURL());
        }
    }


    private boolean validate()
    {
        if(edit_address_1.getText().toString().trim().length() < 10)
        {
            edit_address_1.setError("Minimum 10 Characters Required!");
            edit_address_1.requestFocus();
            return false;
        }

        else
        {
            input_layout_address_1.setErrorEnabled(false);
        }

        if(edit_city.getText().toString().trim().length() < 3)
        {
            edit_city.setError("Minimum 3 Characters Required!");
            edit_city.requestFocus();
            return false;
        }

        else
        {
            input_layout_city.setErrorEnabled(false);
        }

        if(edit_state.getText().toString().trim().length() < 3)
        {
            edit_state.setError("Minimum 3 Characters Required!");
            edit_state.requestFocus();
            return false;
        }

        else
        {
            input_layout_state.setErrorEnabled(false);
        }

        if(country_index == 0)
        {
            new CustomAlertDialog(getContext(), FragmentContactInfo.this, getView()).showSnackbar("Select Country", CustomAlertDialog.LENGTH_SHORT);
            return false;
        }

        if(edit_pincode.getText().toString().trim().isEmpty())
        {
            edit_pincode.setError("Pincode Required!");
            edit_pincode.requestFocus();
            return false;
        }

        else if(edit_pincode.getText().toString().length() < 6)
        {
            edit_pincode.setError("Pincode must be 6 digits!");
            edit_pincode.requestFocus();
            return false;
        }

        else if (!edit_pincode.getText().toString().matches(PINCODE_PATTERN))
        {
            edit_pincode.setError("Pincode should only contain digits!");
            edit_pincode.requestFocus();
            return false;
        }

        else
        {
            input_layout_pincode.setErrorEnabled(false);
        }

        return true;
    }


    private boolean init_profile()
    {
        if(!validate())
        {
            return false;
        }

        mUser.getAddress().setAddress_1(edit_address_1.getText().toString());
        mUser.getAddress().setAddress_2(edit_address_2.getText().toString());
        mUser.getAddress().setCity(edit_city.getText().toString());
        mUser.getAddress().setState(edit_state.getText().toString());
        mUser.getAddress().setCountry(country_list.get(country_index-1).getCountry());
        mUser.getAddress().setCountryCode(country_list.get(country_index-1).getCountryCode());
        mUser.getAddress().setPincode(edit_pincode.getText().toString());
        mUser.setPhoneNo(this.mController.getSession().getMobileNumber());
        mUser.setAlternatePhoneNo(edit_alternate_phone_no.getText().toString());

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
            if(requestCode == HTTP_REQUEST_CODE_GET_COUNTRY_LIST && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.mController.getSession().putCountryList(json.getString(KEY_DATA));
                    init_country_list(json.getString(KEY_DATA));
                }

                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }

            if((requestCode == HTTP_REQUEST_CODE_GET_PROFILE || requestCode == HTTP_REQUEST_CODE_UPDATE_PROFILE)
                && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
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

                    if(!this.mController.getSQLiteHelper().insert(mUser))
                    {
                        this.mController.getSQLiteHelper().update(mUser);

                        if(!IS_AUTH_SYNC)
                        {
                            Toast.makeText(getContext(), "Contact Information Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    MyApplication.prefs.edit().putString(KEY_SHIPPING_ADDRESS, new Gson().toJson(mUser.getAddress())).apply();
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
            IS_AUTH_SYNC = true;

            getActivity().runOnUiThread(new Runnable() {

                public void run()
                {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}