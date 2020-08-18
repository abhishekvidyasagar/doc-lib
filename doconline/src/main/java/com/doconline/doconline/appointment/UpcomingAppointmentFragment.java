package com.doconline.doconline.appointment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.AppointmentHistoryRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.model.Appointment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.appointment.AppointmentHistoryActivity.HTTP_REQUEST_CODE;


public class UpcomingAppointmentFragment extends BaseFragment
{

    RelativeLayout layout_loading;
    LinearLayout layout_empty;
    TextView empty_message;
    RecyclerView mRecyclerView;

    private AppointmentHistoryRecyclerAdapter mAdapter;
    private List<Appointment> uList = new ArrayList<>();

    private boolean isLoading;
    private int CURRENT_PAGE, LAST_PAGE;


    public static UpcomingAppointmentFragment newInstance()
    {
        return new UpcomingAppointmentFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_upcoming_appointment, container, false);
       // ButterKnife.bind(this, view);

       layout_loading =  view.findViewById(R.id.layout_loading);
        layout_empty=  view.findViewById(R.id.layout_empty);
        empty_message=  view.findViewById(R.id.empty_message);
       mRecyclerView=  view.findViewById(R.id.recycler_view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.initAdapter();

        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            if(getActivity() != null)
            {
                getActivity().runOnUiThread(new Runnable() {

                    public void run() {

                        layout_loading.setVisibility(View.VISIBLE);
                        layout_empty.setVisibility(View.GONE);
                    }
                });

                new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), this)
                        .execute(mController.getAppointmentURL() + "upcoming");
            }
        }

        else
        {
            this.adapter_refresh();
        }
    }


    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getActivity() != null && getView() != null && getView().isShown() && isVisibleToUser)
        {
            if(!new InternetConnectionDetector(getContext()).isConnected())
            {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }


    private void initAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        /**
         * Calling the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /**
         * The number of Columns
         */
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AppointmentHistoryRecyclerAdapter(getContext(), uList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new AppointmentHistoryRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {
                Intent intent = new Intent(getActivity(), AppointmentSummeryActivity.class);
                intent.putExtra("ID", uList.get(i).getAppointmentID());
                startActivity(intent);
            }
        });

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore()
            {
                Log.v("onLoadMore", "Load More");

                if(!new InternetConnectionDetector(getContext()).isConnected())
                {
                    Toast.makeText(getContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }
                isLoading = true;

                Handler handler = new Handler();
                final Runnable r = new Runnable()
                {
                    public void run()
                    {
                        uList.add(null);
                        mAdapter.notifyItemInserted(uList.size() - 1);
                    }
                };

                handler.post(r);
                new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), UpcomingAppointmentFragment.this)
                        .execute(mController.getAppointmentURL() + "upcoming?page=" + (CURRENT_PAGE + 1));
            }
        });

        this.load_more_on_scroll();
        this.adapter_refresh();
    }


    private void load_more_on_scroll()
    {
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                {
                    Log.v("onScrolled", "onScrolled");

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisibleItems + visibleItemCount >= totalItemCount)
                    {
                        if(!isLoading && CURRENT_PAGE != LAST_PAGE)
                        {
                            if (mAdapter.mOnLoadMoreListener != null)
                            {
                                mAdapter.mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    public void setLoaded()
    {
        isLoading = false;
    }


    private void adapter_refresh()
    {
        if(uList.size() == 0)
        {
            layout_empty.setVisibility(View.VISIBLE);
            empty_message.setText("Knock ! Knock! \n" +
                    "Your scheduled upcoming  appointments await you here!");
        }

        else
        {
            layout_empty.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
    }


    private void displayAppointment(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);
            json = json.getJSONObject(KEY_DATA);

            /*if(json.has(Constants.KEY_SESSION_ID) && json.has(Constants.KEY_TOKEN_ID))
            {
                Intent intent = new Intent();
                intent.setAction("cb.doconline.VOIP");
                intent.putExtra("json_data", json_data);
                intent.putExtra("is_outgoing_call", true);
                context.sendBroadcast(intent);
                return;
            }*/

            if(isLoading)
            {
                uList.remove(uList.size() - 1);
                mAdapter.notifyItemRemoved(uList.size());
                setLoaded();
            }

            CURRENT_PAGE = json.getInt(Constants.KEY_CURRENT_PAGE);
            LAST_PAGE = json.getInt(Constants.KEY_LAST_PAGE);

            uList.addAll(Appointment.getAppointmentListFromJSON(json.getString(KEY_DATA)));
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            this.adapter_refresh();
        }
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
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
            if(requestCode == HTTP_REQUEST_CODE && responseCode == HttpClient.OK)
            {
                this.displayAppointment(response);
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

                public void run() {

                    layout_loading.setVisibility(View.GONE);
                }
            });
        }
    }
}