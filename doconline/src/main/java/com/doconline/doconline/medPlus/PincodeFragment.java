package com.doconline.doconline.medPlus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnProcureMedicineListener;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.model.Address;
import com.doconline.doconline.model.Medicine;
import com.doconline.doconline.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_EMAIL_VERIFICATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_MOBILE_VERIFICATION;
import static com.doconline.doconline.app.Constants.KEY_ADDRESS_1;
import static com.doconline.doconline.app.Constants.KEY_ADDRESS_2;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_PIN_CODE;
import static com.doconline.doconline.app.Constants.KEY_SHIPPING_ADDRESS;
import static com.doconline.doconline.app.Constants.PINCODE_PATTERN;
import static com.doconline.doconline.app.MyApplication.prefs;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_CHECK_MEDICINE_AVAILABILITY;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_GET_ORDER_STATUS;

/**
 * Created by cbug on 25/9/17.
 */
public class PincodeFragment extends BaseFragment implements View.OnClickListener {


    EditText editPincode;

    Button button_go;

    TextView tv_step_one;

    private int appointment_id;
    private User mUser;

    private OnProcureMedicineListener medicine_listener;
    private OnHttpResponse response_listener;


    public PincodeFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public PincodeFragment(OnHttpResponse response_listener, OnProcureMedicineListener medicine_listener, int appointment_id)
    {
        this.appointment_id = appointment_id;
        this.response_listener = response_listener;
        this.medicine_listener = medicine_listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_enter_pincode, container, false);
       // ButterKnife.bind(this, view);

        editPincode = view.findViewById(R.id.editPincode);
         button_go= view.findViewById(R.id.btnGo);
        tv_step_one= view.findViewById(R.id.tv_step_one);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);

        button_go.setText(R.string.Next);

        this.bindAddress();
        this.addListener();
        this.hideKeyboard(view);

        this.getOrderStatus();
    }


    private void getOrderStatus()
    {
        if (new InternetConnectionDetector(getActivity()).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_ORDER_STATUS, MyApplication.HTTPMethod.GET.getValue(), this).
                    execute(mController.getMedicineOrderStatusURL() + appointment_id);
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }


    private void bindAddress()
    {
        if(!prefs.getString(KEY_SHIPPING_ADDRESS, "").isEmpty())
        {
            try
            {
                if(this.mUser == null)
                {
                    this.mUser = new User();
                }

                JSONObject json = new JSONObject(prefs.getString(KEY_SHIPPING_ADDRESS, ""));

                String pincode = json.getString(KEY_PIN_CODE);

                this.mUser.getAddress().setPincode(pincode);
                this.mUser.getAddress().setAddress_1(json.getString(KEY_ADDRESS_1));
                this.mUser.getAddress().setAddress_2(json.getString(KEY_ADDRESS_2));

                editPincode.setText(pincode);
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            this.mUser = mController.getSQLiteHelper().getUserProfile();
            editPincode.setText(mUser.getAddress().getPincode());
        }
    }


    private void addListener()
    {
        button_go.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnGo) {
            String pincode = editPincode.getText().toString();

            if (!isValidPincode(pincode)) {
                return;
            }

            if (new InternetConnectionDetector(getActivity()).isConnected()) {
                this.mUser.getAddress().setPincode(pincode);

                new HttpClient(HTTP_REQUEST_CODE_CHECK_MEDICINE_AVAILABILITY, MyApplication.HTTPMethod.POST.getValue(),
                        Medicine.composePincodeMap(pincode), this).
                        execute(mController.getMedicineAvailabilityURL() + appointment_id);
            } else {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }


    private boolean isValidPincode(String pincode)
    {
        if(pincode == null || TextUtils.isEmpty(pincode))
        {
            editPincode.setError("Pincode Required");
            editPincode.requestFocus();
            return false;
        }

        else if(pincode.length() != 6)
        {
            editPincode.setError("Pincode must be 6 digits");
            editPincode.requestFocus();
            return false;
        }

        else if (!pincode.matches(PINCODE_PATTERN))
        {
            editPincode.setError("Pincode should only contain digits");
            editPincode.requestFocus();
            return false;
        }

        return true;
    }


    private void hideKeyboard(final View rootView) {

        editPincode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editPincode.getText().toString().length() == 6)
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
            if(requestCode == HTTP_REQUEST_CODE_CHECK_MEDICINE_AVAILABILITY && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                ArrayList<Medicine> mList = Medicine.getMedicineFromJSON(response);

                Address address = new Address();
                address.setPincode(editPincode.getText().toString());
                address.setAddress_1(this.mUser.getAddress().getAddress_1());
                address.setAddress_2(this.mUser.getAddress().getAddress_2());

                medicine_listener.onDeliveryAddress(address);
                medicine_listener.onMedicineFound(mList);
                medicine_listener.onPageSelection(3, "Medicines");
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_GET_ORDER_STATUS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    if(json.getBoolean(KEY_DATA))
                    {
                        String message = json.isNull(KEY_MESSAGE) ? "" : json.getString(KEY_MESSAGE);

                        new CustomAlertDialog(getContext(), this).
                                showAlertDialogWithoutTitle(message, getActivity().getResources().getString(R.string.OK),true);
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }


            if(requestCode == HTTP_REQUEST_CODE_CHECK_MEDICINE_AVAILABILITY && responseCode == HttpClient.PRECONDITION_FAILED)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    if(json.has(KEY_MOBILE_NO))
                    {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_MOBILE_VERIFICATION, json.getString(KEY_MOBILE_NO));
                    }

                    else if(json.has(KEY_EMAIL))
                    {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_EMAIL_VERIFICATION, json.getString(KEY_EMAIL));
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(responseCode == HttpClient.UNPROCESSABLE_ENTITY)
            {
                JSONObject json = new JSONObject(response);
                new CustomAlertDialog(getContext(), this, getView()).showSnackbar(json.getString(KEY_MESSAGE), CustomAlertDialog.LENGTH_LONG);
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

    private void showAlertDialog(int requestCode, String message)
    {
        new CustomAlertDialog(getContext(), requestCode, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_account_info), message,
                        getResources().getString(R.string.Verify),
                        getResources().getString(R.string.Cancel), true);
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(getActivity() == null)
        {
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_MOBILE_VERIFICATION)
        {

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("INDEX", 4);
            startActivity(intent);
            return;
        }

        if(requestCode == DIALOG_REQUEST_CODE_EMAIL_VERIFICATION)
        {
            Intent intent3 = new Intent(getActivity(), MainActivity.class);
            intent3.putExtra("INDEX", 9);
            startActivity(intent3);
        }
    }
}