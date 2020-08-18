package com.doconline.doconline.diagnostics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.model.DiagnosticsHistoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;

/**
 * Created by Developer on 3/28/2018.
 */

public class DiagnosticsHistoryListingFragment extends BaseFragment implements View.OnClickListener,
        DiagnosticsHistoryRecyclerAdapter.OnHistoryItemSelectedListener {

    public static final int HTTP_REQUEST_CODE_DIAGNOSTICS_GET_HISTORY = 1;
    private Context context;
    private OnPageSelection listener;
    private OnHttpResponse response_listener;
    private OnLoadingStatusChangedListener loadingStatusChangedListener;
    LinearLayoutManager layoutManager;
    DiagnosticsHistoryRecyclerAdapter mHistoryAdapter;


    RecyclerView recyclerView_DiagnosticsHistory;
    Boolean isUpcoming = true;


    TextView noitems_diag_history;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_diagnostics_history, container, false);
      //  ButterKnife.bind(this, view);

       recyclerView_DiagnosticsHistory = view.findViewById(R.id.recyclerView_DiagnosticsHistory);

        noitems_diag_history= view.findViewById(R.id.noitems_diag_historyy);

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (new InternetConnectionDetector(context).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_DIAGNOSTICS_GET_HISTORY, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getDiagnosticsHistoryURL());
        }
        else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }

        this.initAdapter(true);

    }

    @Override
    public void onClick(View v) {

    }

    public DiagnosticsHistoryListingFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public DiagnosticsHistoryListingFragment(Context context, OnPageSelection listener, OnHttpResponse response_listener, OnLoadingStatusChangedListener loadingStatusListener)
    {
        this.context = context;
        this.listener = listener;
        this.response_listener = response_listener;
        this.loadingStatusChangedListener = loadingStatusListener;

    }

    public void upComingAppointments()
    {
        if (mController.getDiagnosticsUpcomingHistoryCount() > 0){
            noitems_diag_history.setVisibility(View.GONE);
        }else {
            noitems_diag_history.setVisibility(View.VISIBLE);
            noitems_diag_history.setText("Knock ! Knock! \n" +
                    "Your scheduled upcoming appointments await you here!");
        }

        isUpcoming = true;
        mHistoryAdapter.isUpcoming = true;
        mHistoryAdapter.notifyDataSetChanged();
    }

    public void previousAppointments()
    {
        if (mController.getDiagnosticsHistoryCount() > 0){
            noitems_diag_history.setVisibility(View.GONE);
        }else {
            noitems_diag_history.setVisibility(View.VISIBLE);
            noitems_diag_history.setText("At a Glance: View all your past appointment details here!");
        }
        isUpcoming = false;
        mHistoryAdapter.isUpcoming = false;
        mHistoryAdapter.notifyDataSetChanged();
    }

    private void initAdapter(Boolean history)
    {
        if(mHistoryAdapter != null)
        {
            return;
        }

        recyclerView_DiagnosticsHistory.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView_DiagnosticsHistory.setLayoutManager(mLayoutManager);

        mHistoryAdapter = new DiagnosticsHistoryRecyclerAdapter(context, this);
        mHistoryAdapter.isUpcoming = history;
        recyclerView_DiagnosticsHistory.setAdapter(mHistoryAdapter);

    }

    @Override
    public void OnDiagnosticsHistoryItemSelected(int postition) {

    }

    @Override
    public void onPreExecute()
    {

        response_listener.onPreExecute();
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
            JSONObject json = new JSONObject(response);
            String message = (json.isNull(KEY_MESSAGE)) ? "Oops...Something Went Wrong" : json.getString(KEY_MESSAGE);
            if (requestCode == HTTP_REQUEST_CODE_DIAGNOSTICS_GET_HISTORY && responseCode == HttpClient.OK) {
                this.parseDiagnosticsHistory(json.getJSONArray(KEY_DATA));
            }
            if (mController.getDiagnosticsUpcomingHistoryCount() > 0){
                noitems_diag_history.setVisibility(View.GONE);
            }else {
                noitems_diag_history.setVisibility(View.VISIBLE);
                noitems_diag_history.setText("Knock ! Knock! \n" +
                        "Your scheduled upcoming appointments await you here!");
            }
            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        }


        catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally
        {
           /* runOnUiThread(new Runnable() {

                public void run() {
                    //layout_loading.setVisibility(View.GONE);
                    loadingStatusChangedListener.hideProgressbarWithSuccess();
                }
            });*/

            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    //layout_loading.setVisibility(View.GONE);
                    loadingStatusChangedListener.hideProgressbarWithSuccess();
                }
            });


            response_listener.onPostExecute(requestCode, responseCode, response);
        }

    }

    private void parseDiagnosticsHistory(JSONArray jsonObject) {

        if(null != mController.list_DiagnosticsHistoryItems) {
            mController.clearDiagnosticsHistoryList();
            mController.clearDiagnosticsUpcomingHistoryList();
            mController.clearDiagnosticsPreviousHistoryList();
        }

        mController.setDiagnosticsHistoryList(DiagnosticsHistoryItem.getDiagnosticsHistoryItemListFromJSON(jsonObject));

        mController.setDiagnosticsUpcomingHistoryDetails();

        mController.setDiagnosticsPreviousHistoryDetails();

        if(mController.getDiagnosticsHistoryCount() > 0)
        {
            if(mHistoryAdapter != null){
                mHistoryAdapter.notifyDataSetChanged();
            }
        }
    }
}
