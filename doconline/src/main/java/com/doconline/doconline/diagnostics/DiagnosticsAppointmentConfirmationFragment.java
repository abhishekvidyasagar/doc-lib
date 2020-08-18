package com.doconline.doconline.diagnostics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_UPDATED_COUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.diagnostics.DiagnosticsCartListingFragment.HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE;

@SuppressLint("ValidFragment")
public class DiagnosticsAppointmentConfirmationFragment extends BaseFragment implements View.OnClickListener{


    TextView tv_DiagnosticsPartnerName;


    TextView tv_DiagnosticsAppointmentID;


    TextView tv_BookedPackageName;


    TextView tv_BookedDateAndTime;


    Button btnGotoDashboard;


    Button btnGotoAppointmentHistory;

    private OnLoadingStatusChangedListener loadingStatusChangedListener;
    private OnHttpResponse response_listener;


    public static void setListener(OnAppointmentConfirmationFragmentListener listener ) {
        DiagnosticsAppointmentConfirmationFragment.listener = listener;
    }

    private static OnAppointmentConfirmationFragmentListener listener;


    public interface OnAppointmentConfirmationFragmentListener {

        void gotoAppointmentHistory();
    }

    public DiagnosticsAppointmentConfirmationFragment(OnLoadingStatusChangedListener loadingStatusListener, OnHttpResponse response_listener){
        this.loadingStatusChangedListener = loadingStatusListener;
        this.response_listener = response_listener;
    }

    @Override
    public void setArguments(Bundle args) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View diagnosticsApptView = inflater.inflate(R.layout.fragment_diagnostics_appointment_confirmed, container, false);
        //ButterKnife.bind(this, diagnosticsApptView);

         tv_DiagnosticsPartnerName = diagnosticsApptView.findViewById(R.id.tv_DiagnosticsPartnerName);

        tv_DiagnosticsAppointmentID= diagnosticsApptView.findViewById(R.id.tv_BookedAppointmentID);

        tv_BookedPackageName= diagnosticsApptView.findViewById(R.id.tv_BookedPackageName);

         tv_BookedDateAndTime= diagnosticsApptView.findViewById(R.id.tv_BookedDateAndTime);

        btnGotoDashboard= diagnosticsApptView.findViewById(R.id.btnGotoDashboard);

        btnGotoAppointmentHistory= diagnosticsApptView.findViewById(R.id.btnGotoAppointmentHistory);


        return diagnosticsApptView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnGotoAppointmentHistory.setOnClickListener(this);
        btnGotoDashboard.setOnClickListener(this);
    }

    public void fillConfirmationDetails(String appointmentID, String packageName, String dateOfAppointment, Boolean isFromCart) {

        try {
            String formateddate = getRequiredDateFormatFromAvailableFormat(dateOfAppointment);

            tv_BookedDateAndTime.setText(""+formateddate);
            tv_DiagnosticsAppointmentID.setText(appointmentID);
            tv_BookedPackageName.setText(packageName);

            if (isFromCart) {
                if (new InternetConnectionDetector(getContext()).isConnected()) {
                    //layout_loading.setVisibility(View.VISIBLE);
                    String cartItemDeleteURL = mController.getDiagnosticsRemoveFromCartURL();
                    int index = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX, -1);

                    String rawJSON = "{\"" + Constants.KEY_DIAGNOSTICS_PACKAGEID + "\":" + "\"" + mController.getCartItemsList().get(index).getProductID() + "\"}";

                    new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE, MyApplication.HTTPMethod.BDELETE.getValue(),
                            true,
                            rawJSON,
                            DiagnosticsAppointmentConfirmationFragment.this)
                            .execute(cartItemDeleteURL);
                } else {
                    new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
                }
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getRequiredDateFormatFromAvailableFormat(String availabledate) {
        Date date = null;
        String str = null;

        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);


        try {
            date = inputFormat.parse(availabledate);
            str = outputFormat.format(date);
        } catch (android.net.ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.btnGotoDashboard) {//listener.gotoDashboard();
            getActivity().finish();
        } else if (id == R.id.btnGotoAppointmentHistory) {
            if (listener != null) {
                listener.gotoAppointmentHistory();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onPreExecute()
    {
        response_listener.onPreExecute();

        if(null != loadingStatusChangedListener)
            loadingStatusChangedListener.showLoadingActivity();
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if (getActivity() == null)
        {
            return;
        }

        if (getActivity().isFinishing())
        {
            return;
        }

        if (getView() == null)
        {
            return;
        }

        try
        {
            int updatedCartCount = 0;
            int currentSubTotal = 0;
            JSONObject json = new JSONObject(response);

            if (HttpClient.OK == responseCode) {
                try {
                    //String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);

                    if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE) {

                        JSONObject jsonData = json.getJSONObject(KEY_DATA);
                        int index = MyApplication.prefs.getInt(KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX,-1);
                        int packageIDToDeleteFromCart = mController.getCartItemsList().get(index).getProductID();
                        mController.deleteCartItems(packageIDToDeleteFromCart);

                        mController.getIndexOfItemFromDiagnosticsItemList(packageIDToDeleteFromCart).setAddedToCart(false);

                        updatedCartCount = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_COUNT);
                        currentSubTotal = jsonData.getInt(KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT);
                        int currentCartCount = mController.getDiagnosticsCartItemListCount();
                        MyApplication.prefs.edit().putInt(KEY_DIAGNOSTICS_CART_COUNT, updatedCartCount).apply();


                        if(currentCartCount == updatedCartCount) {
                            mController.getSession().putDiagnosticsCartCount(updatedCartCount);
                            mController.UpdateCart(updatedCartCount, (float) currentSubTotal);
                        }
                        Log.v("cart","item removed from cart");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    return;
                }
            }
            else if(HttpClient.UNPROCESSABLE_ENTITY == responseCode){
                String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);
                switch (requestCode){
                    case HTTP_REQUEST_CODE_DIAGNOSTICS_USER_CART_ITEM_DELETE:
                        //this.showDialog("Alert", message);
                        break;
                }
            }
            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
           getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    //layout_loading.setVisibility(View.GONE);
                    loadingStatusChangedListener.hideProgressbarWithSuccess();
                }
            });
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }
}