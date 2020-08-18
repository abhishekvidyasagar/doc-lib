package com.doconline.doconline.diagnostics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.diagnostics.Adapters.DiagnosticsUserAddressRecyclerAdapter;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnChangeDiagnosticsAddressListener;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.model.DiagnosticsUserAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;

/**
 * Created by A on 3/12/2018.
 */

public class DiagnosticsUserAddressListFragment extends BaseFragment implements View.OnClickListener,
        DiagnosticsUserAddressRecyclerAdapter.OnItemSetDefaultListener,
        DiagnosticsUserAddressRecyclerAdapter.OnItemDeletedListener,
        DiagnosticsUserAddressRecyclerAdapter.OnItemEditListener,
        DiagnosticsUserAddressFormFragment.OnSaveNewAddressListner{

    private static final String TAG = DiagnosticsUserAddressListFragment.class.getSimpleName();

    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST = 1;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_SETASDEFAULT = 11;
    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_DELETE = 12;

    //public static ArrayList<DiagnosticsUserAddress> mDiagnosticsUserAddressList = new ArrayList<>();

    private Context         context = null;
    private OnHttpResponse response_listener;
    private OnChangeDiagnosticsAddressListener address_listener;


    RecyclerView mRecyclerView_DiagnosticsAddressList;

    //LinearLayoutManager layoutManager;
    DiagnosticsUserAddressRecyclerAdapter mUserAddressAdapter;


    LinearLayout layout_empty;


    TextView empty_message;

    OnLoadingStatusChangedListener loadingStatusChangedListener;


    private int index = -1;

    public DiagnosticsUserAddressListFragment() {

    }

    @SuppressLint("ValidFragment")
    public DiagnosticsUserAddressListFragment(Context context, OnHttpResponse response_listener, OnChangeDiagnosticsAddressListener addressListener, OnLoadingStatusChangedListener loadingStatusListener)
    {
        this.context = context;
        this.response_listener = response_listener;
        address_listener = addressListener;
        this.loadingStatusChangedListener = loadingStatusListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_diagnostics_address_list, container, false);
       // ButterKnife.bind(this, view);


         mRecyclerView_DiagnosticsAddressList = view.findViewById(R.id.recyclerView_diagnostics_user_addresses);



        layout_empty = view.findViewById(R.id.layout_empty);

         empty_message = view.findViewById(R.id.empty_message);

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

        this.initAdapter();
        this.refreshAdapter();
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
        loadUserAddressList();
    }

    public void loadUserAddressList() {

        if(new InternetConnectionDetector(context).isConnected())
        {
            layout_empty.setVisibility(View.GONE);
            //layout_loading.setVisibility(View.VISIBLE);

            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST,
                    MyApplication.HTTPMethod.GET.getValue(), this).execute(mController.getDiagnosticsUserAddressListURL());
        }
    }


    @Override
    public void onClick(View view) {
        Log.d(TAG, "Clicked on view");
    }

    private void deleteUserAddress(){

        if(getActivity() == null)
        {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                //layout_loading.setVisibility(View.GONE);
                mController.removeDiagnosticsUserAddress(index);
                mUserAddressAdapter.notifyItemRemoved(index);
                mUserAddressAdapter.notifyItemRangeChanged(index, mController.getDiagnosticsUserAddressCount());

                if(mController.getDiagnosticsUserAddressCount() == 0)
                {
                    layout_empty.setVisibility(View.VISIBLE);
                }
                else
                {
                    layout_empty.setVisibility(View.GONE);
                    refreshAdapter();
                }

//                if(mController.getDiagnosticsUserAddressCount() < 3)
//                {
//                    btn_save.setVisibility(View.VISIBLE);
//                }
//
//                else
//                {
//                    btn_save.setVisibility(View.GONE);
//                }

                //Toast.makeText(context, "User address is removed!", Toast.LENGTH_LONG).show();
            }
        });

        index = -1;
        if(mController.getDiagnosticsUserAddressCount() == 0)
        {
            layout_empty.setVisibility(View.VISIBLE);
            empty_message.setText("No addresses are added!");
        }

        else
        {
            layout_empty.setVisibility(View.GONE);
        }
    }

    private void confirmUserAddressDeletion()
    {
        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS, this)
                .showDialogWithAction("Delete this address!",
                        getResources().getString(R.string.dialog_diagnostics_address_delete_warning),
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), true);
    }

    private void setDefaultUserAddress(String message) {

        if(mUserAddressAdapter != null) {
            mUserAddressAdapter.notifyItemRangeChanged(index, mController.getDiagnosticsUserAddressCount());
            this.showDialog("Information", message);
        }
    }

    private void initAdapter()
    {
        if(mUserAddressAdapter != null)
        {
            return;
        }

        mRecyclerView_DiagnosticsAddressList.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView_DiagnosticsAddressList.setLayoutManager(mLayoutManager);

        mUserAddressAdapter = new DiagnosticsUserAddressRecyclerAdapter(this, this, this);
        mRecyclerView_DiagnosticsAddressList.setAdapter(mUserAddressAdapter);
    }

    private void refreshAdapter()
    {
        try
        {
            if(mController.getDiagnosticsUserAddressCount() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No addresses are added!");
            }
            else
            {
                layout_empty.setVisibility(View.GONE);
            }

            mUserAddressAdapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            if(HttpClient.OK == responseCode || HttpClient.CREATED == responseCode) {
                try {
                    //Capture the message to be displayed in the alert dialog.
                    JSONObject json = new JSONObject(response);

                    String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);

                    if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_LIST) {

                        this.parseUserAddressList(json.getJSONArray(KEY_DATA));
                        return;
                    } else if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_SETASDEFAULT) {
                        this.setDefaultUserAddress(message);
                        return;
                    } else if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_DELETE) {
                        this.deleteUserAddress();
                        return;
                    }
                }
                catch (JSONException e)
                {
                    new HttpResponseHandler(getContext(), this, getView()).handle(0, response);
                    e.printStackTrace();
                }
                return;
            }
            else  if((responseCode == HttpClient.UNPROCESSABLE_ENTITY) || (responseCode == HttpClient.RESOURCE_LOCKED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);
                    this.showDialog("Information", message);
                }

                catch (JSONException e)
                {
                    new HttpResponseHandler(getContext(), this, getView()).handle(0, response);
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

    private void parseUserAddressList(JSONArray array)
    {
        try
        {
            mController.clearDiagnosticsAddressList();
            mController.setDiagnosticsUserAddressList(DiagnosticsUserAddress.getUserAddressListFromJSON(array));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            refreshAdapter();
        }
    }

    @Override
    public void OnItemSetDefault(int pos) {
        this.index = pos;

        if(new InternetConnectionDetector(context).isConnected())
        {
            //layout_loading.setVisibility(View.VISIBLE);
            String addressToSetDefault = mController.getDiagnosticsDefaultUserAddressURL();

            String rawJSON = "{\""+ Constants.KEY_DIAGNOSTICS_ADDRESS_ID + "\":" + "\""+ mController.getUserAddress(index).getiAddressID() + "\"}";

            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_SETASDEFAULT, MyApplication.HTTPMethod.PATCH.getValue(),
                    true,
                    rawJSON,
                    DiagnosticsUserAddressListFragment.this)
                    .execute( addressToSetDefault );
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    @Override
    public void OnItemDeleted(int position) {

        this.index = position;
        this.confirmUserAddressDeletion();
    }

    @Override
    public void OnItemEdited(int position) {

        this.index = index;

        address_listener.OnEditDiagnosticsAddress(position);
        address_listener.onPageSelection(position+1,"Edit address");
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
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS)
        {
            if(new InternetConnectionDetector(context).isConnected())
            {
                //layout_loading.setVisibility(View.VISIBLE);
                String addressToDeleteURL = mController.getDiagnosticsDeleteUserAddressURL();

                String rawJSON = "{\""+ Constants.KEY_DIAGNOSTICS_ADDRESS_ID + "\":" + "\""+ mController.getUserAddress(index).getiAddressID() + "\"}";

                new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_USER_ADDRESS_DELETE, MyApplication.HTTPMethod.BDELETE.getValue(),
                        true,
                        rawJSON,
                        DiagnosticsUserAddressListFragment.this)
                        .execute( addressToDeleteURL );
            }

            else
            {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }

    @Override
    public void OnSaveNewAddress(DiagnosticsUserAddress address) {

    }
}
