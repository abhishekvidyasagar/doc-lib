package com.doconline.doconline.diagnostics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.model.DiagnosticsUserAddress;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_SAVE_NEW_ADDRESS;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;

/**
 * Created by A on 3/12/2018.
 */

public class DiagnosticsUserAddressFormFragment extends BaseFragment implements View.OnClickListener, OnDialogAction {

    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_ADD = 1;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_UPDATE = 2;

    private Context         context = null;
    private OnHttpResponse response_listener;

    private TextInputLayout input_layout_user_address_title, input_layout_user_addresslane,
                            input_layout_user_address_landmark, input_layout_user_address_city,input_layout_user_address_pincode,
                            input_layout_user_address_state;
    private EditText        editAddressTitle,editAddressLane,editLandmark,editCity,editPincode,editState;

    private AppCompatCheckBox appCompatCheckBox_isDefaultAddress;
    private Button btn_SaveUserAddress, btn_ClearUserAddressForm;

    private boolean isEditingAddress = false;

    private OnSaveNewAddressListner saveNewAddressListner;

    private DiagnosticsUserAddress prefilledData;

    OnLoadingStatusChangedListener  loadingStatusChangedListener;

    public void setPrefilledData(DiagnosticsUserAddress prefilledData) {
        if (prefilledData == null)
        {
            clear_fields();
        }
        else {
            this.prefilledData = prefilledData;

            editAddressTitle.setText(prefilledData.getStrAddressTitle());
            editAddressLane.setText(prefilledData.getStrAddressLane());
            editLandmark.setText(prefilledData.getStrLandmark());
            editCity.setText(prefilledData.getStrCity());
            editState.setText(prefilledData.getStrState());
            editPincode.setText(prefilledData.getiPincode().toString());
            appCompatCheckBox_isDefaultAddress.setChecked(prefilledData.isDefaultAddress());
        }
    }

    public interface OnSaveNewAddressListner {
        void OnSaveNewAddress(DiagnosticsUserAddress address);
    }

    public void setOnSaveNewAddressListner(OnSaveNewAddressListner listner){
        saveNewAddressListner = listner;
    }

    public DiagnosticsUserAddressFormFragment() {
    }

    @SuppressLint("ValidFragment")
    public DiagnosticsUserAddressFormFragment(Context context, OnHttpResponse response_listener, OnLoadingStatusChangedListener loadingStatusListener)
    {
        this.context = context;
        this.response_listener = response_listener;
        this.loadingStatusChangedListener = loadingStatusListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_diagnostics_user_address_form, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.initViews(view);
        //this.hideKeyboard(view);
        this.setOnClickListener();

        if(!new InternetConnectionDetector(getContext()).isConnected())
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    private void setOnClickListener()
    {
        btn_SaveUserAddress.setOnClickListener(this);
        btn_ClearUserAddressForm.setOnClickListener(this);
    }

    private void initViews(View view)
    {
        btn_SaveUserAddress = view.findViewById(R.id.btn_save_user_address);
        btn_ClearUserAddressForm = view.findViewById(R.id.btn_clear_user_address);

        input_layout_user_address_title = view.findViewById(R.id.input_layout_user_address_title);
        input_layout_user_addresslane   = view.findViewById(R.id.input_layout_user_addresslane);
        input_layout_user_address_landmark= view.findViewById(R.id.input_layout_user_address_landmark);
        input_layout_user_address_city= view.findViewById(R.id.input_layout_user_address_city);
        input_layout_user_address_pincode= view.findViewById(R.id.input_layout_user_address_pincode);
        input_layout_user_address_state = view.findViewById(R.id.input_layout_user_address_state);

        appCompatCheckBox_isDefaultAddress = view.findViewById(R.id.appCompatCheckBox_isDefaultAddress);

        editAddressTitle    = view.findViewById(R.id.editAddressTitle);
        editAddressLane     = view.findViewById(R.id.editAddressLane);
        editLandmark        = view.findViewById(R.id.editLandmark);
        editCity            = view.findViewById(R.id.editCity);
        editPincode         = view.findViewById(R.id.editPincode);
        editState           = view.findViewById(R.id.editState);

        /*input_layout_user_address_title.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(input_layout_user_address_title,0,10));
        input_layout_user_addresslane.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(input_layout_user_addresslane,0,100));
        input_layout_user_address_landmark.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(input_layout_user_address_landmark,0,50));
        input_layout_user_address_city.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(input_layout_user_address_city,0,25));
        input_layout_user_address_pincode.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(input_layout_user_address_pincode,0,6));
        input_layout_user_address_state.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(input_layout_user_address_state,0,20));*/
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
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPreExecute(){
        if(null != loadingStatusChangedListener)
            loadingStatusChangedListener.showLoadingActivity();
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getView() == null) {
            return;
        }

        try {
            if (HttpClient.OK == responseCode || HttpClient.CREATED == responseCode) {

                try {
                    JSONObject json = new JSONObject(response);
                    String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);

                    if (HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_ADD == requestCode) {

                        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DIAGNOSTICS_SAVE_NEW_ADDRESS, this)
                                .showAlertDialogWithPositiveAction("Information",
                                        message,
                                        getResources().getString(R.string.OK), true);
                        return;

                    } else if (HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_UPDATE == requestCode) {
                        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DIAGNOSTICS_SAVE_NEW_ADDRESS, this)
                                .showAlertDialogWithPositiveAction("Information",
                                        message,
                                        getResources().getString(R.string.OK), true);
                        return;
                    } else if ((responseCode == HttpClient.UNPROCESSABLE_ENTITY) || (responseCode == HttpClient.RESOURCE_LOCKED)) {
                        this.showDialog("Information", message);
                        return;
                    }

                    new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
                } catch (JSONException e) {
                    new HttpResponseHandler(getContext(), this, getView()).handle(0, response);
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        //layout_loading.setVisibility(View.GONE);
                        if(null != loadingStatusChangedListener)
                            loadingStatusChangedListener.hideProgressbarWithSuccess();
                    }
                });
            }
        }


    private void showDialog(String title, String content)
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

        new CustomAlertDialog(getContext(), this).
                showAlertDialogWithPositiveAction(title, content, context.getResources().getString(R.string.OK),true);
    }

    @Override
    public void onPositiveAction(int requestCode) {

        if(requestCode == DIALOG_REQUEST_CODE_DIAGNOSTICS_SAVE_NEW_ADDRESS) {
            this.clear_fields();
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.btn_save_user_address) {
            if (validate())
                this.saveAddress();
        } else if (id == R.id.btn_clear_user_address) {
            this.clear_fields();
        }
    }

    private boolean validate()
    {
//Address title
        if(editAddressTitle.getText().toString().trim().length() < 4)
        {
            editAddressTitle.setError("Minimum 4 Characters!");
            editAddressTitle.requestFocus();
            return false;
        }
        else if(editAddressTitle.getText().toString().trim().length() > 10)
        {
            editAddressTitle.setError("Cannot exceed more than 10 letters!");
            editAddressTitle.requestFocus();
            return false;
        }
//Address lane
        if(editAddressLane.getText().toString().trim().length() < 5)
        {
            editAddressLane.setError("Minimum 5 letters are required!");
            editAddressLane.requestFocus();
            return false;
        }
        else if(editAddressLane.getText().toString().trim().length() > 100)
        {
            editAddressLane.setError("Cannot exceed more than 100 letters!");
            editAddressLane.requestFocus();
            return false;
        }
//Landmark
        if(editLandmark.getText().toString().trim().length() < 5)
        {
            editLandmark.setError("Minimum 5 letters are required!");
            editLandmark.requestFocus();
            return false;
        }
        else if(editLandmark.getText().toString().trim().length() > 100)
        {
            editLandmark.setError("Cannot exceed more than 100 letters!");
            editLandmark.requestFocus();
            return false;
        }
//City
        /*if(editCity.getText().toString().isEmpty())
        {
            editCity.setError("City is required");
            editCity.requestFocus();
            return false;
        }
        else*/ if(editCity.getText().toString().trim().length() < 5)
        {
            editCity.setError("Minimum 5 letters are required!");
            editCity.requestFocus();
            return false;
        }
        else if(editCity.getText().toString().trim().length() > 25)
        {
            editCity.setError("Cannot exceed more than 25 letters!");
            editCity.requestFocus();
            return false;
        }

//Pincode
        /*if(editPincode.getText().toString().trim().isEmpty()){
            editPincode.setError("Pincode is required");
            editPincode.requestFocus();
            return false;
        }
        else*/ if(editPincode.getText().toString().trim().length() < 6)
        {
            editPincode.setError("All 6 digits are required!");
            editPincode.requestFocus();
            return false;
        }
        else if(editPincode.getText().toString().trim().length() > 6)
        {
            editPincode.setError("Cannot exceed more than 6 digits!");
            editPincode.requestFocus();
            return false;
        }
//State
        /*if(editState.getText().toString().trim().isEmpty()){
            editState.setError("State is required");
            editState.requestFocus();
            return false;
        }
        else */if(editState.getText().toString().trim().length() < 5)
        {
            editState.setError("Minimum 5 letters are required!");
            editState.requestFocus();
            return false;
        }
        else if(editState.getText().toString().trim().length() > 20)
        {
            editState.setError("Cannot exceed more than 20 letters!");
            editState.requestFocus();
            return false;
        }

        return true;
    }

    private void saveAddress(){

        if(new InternetConnectionDetector(context).isConnected())
        {
            DiagnosticsUserAddress updatedUserAddress = new DiagnosticsUserAddress( this.getUserAddressFormData() );
            //layout_loading.setVisibility(View.VISIBLE);
            if(prefilledData != null){ //PATCH existing address
                String saveExistingAddressURL = mController.getDiagnosticsUpdateAddressURL();
                Gson gsonObject = new Gson();

                updatedUserAddress.setbIsDefaultAddress(appCompatCheckBox_isDefaultAddress.isChecked());
                String rawJSON = gsonObject.toJson(DiagnosticsUserAddress.composeUserJSON(updatedUserAddress, true));

                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_UPDATE,
                        MyApplication.HTTPMethod.PATCH.getValue(),
                        true,
                        rawJSON,
                        this).execute(saveExistingAddressURL);
            }
            else { //POST new address
                String saveNewAddressURL = mController.getDiagnosticsAddUserAddressURL();

                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_ADD,
                        MyApplication.HTTPMethod.POST.getValue(),
                        DiagnosticsUserAddress.composeUserJSON(updatedUserAddress, false),
                        DiagnosticsUserAddressFormFragment.this).execute( saveNewAddressURL );
            }
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    private void clear_fields()
    {
        editAddressTitle.getText().clear();
        editAddressLane.getText().clear();
        editLandmark.getText().clear();
        editCity.getText().clear();
        editPincode.getText().clear();
        editState.getText().clear();
        appCompatCheckBox_isDefaultAddress.setChecked(false);
    }

    private DiagnosticsUserAddress getUserAddressFormData()
    {
        DiagnosticsUserAddress addr = new DiagnosticsUserAddress();

        if(prefilledData != null)
            addr.setiAddressID(prefilledData.getiAddressID());

        addr.setStrAddressTitle(editAddressTitle.getText().toString());
        addr.setbIsDefaultAddress(Boolean.parseBoolean(editAddressLane.getText().toString()));
        addr.setStrAddressLane(editAddressLane.getText().toString());
        addr.setiPincode(Integer.parseInt(editPincode.getText().toString()));
        addr.setStrCity(editCity.getText().toString());
        addr.setStrLandmark(editLandmark.getText().toString());
        addr.setStrState(editState.getText().toString());

        boolean i = appCompatCheckBox_isDefaultAddress.isChecked();
        addr.setbIsDefaultAddress(i);

        return addr;
    }
}
