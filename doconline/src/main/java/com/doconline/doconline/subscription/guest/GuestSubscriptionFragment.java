package com.doconline.doconline.subscription.guest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.SubscriptionPlanRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnBookConsultationListener;
import com.doconline.doconline.model.SubscriptionPlan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.consultation.guest.BookGuestConsultationActivity.HTTP_REQUEST_CODE_GET_ONETIME_PLAN;
import static com.doconline.doconline.consultation.guest.BookGuestConsultationActivity.HTTP_REQUEST_CODE_GET_PLANS;


public class GuestSubscriptionFragment extends BaseFragment
{
    private SubscriptionPlanRecyclerAdapter mAdapter;


    RecyclerView mRecyclerView;

    LinearLayout layout_empty;

    TextView empty_message;

    TextView tv_step_one;

    private OnHttpResponse request_listener;
    private OnBookConsultationListener page_listener;

    private List<SubscriptionPlan> mPlans = new ArrayList<>();


    public static GuestSubscriptionFragment newInstance()
    {
        return new GuestSubscriptionFragment();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof OnHttpResponse)
        {
            request_listener = (OnHttpResponse) getActivity();
        }

        if (getActivity() instanceof OnBookConsultationListener)
        {
            page_listener = (OnBookConsultationListener) getActivity();
        }

        this.initPlanAdapter();
        this.syncData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_guest_subscription, container, false);
       // ButterKnife.bind(this, view);

         mRecyclerView = view.findViewById(R.id.recycler_view);
         layout_empty = view.findViewById(R.id.layout_empty);
        empty_message =  view.findViewById(R.id.empty_message);
        tv_step_one =  view.findViewById(R.id.tv_step_one);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        tv_step_one.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_check_icon, 0, 0);
        layout_empty.setVisibility(View.GONE);
    }


    private void syncData()
    {
        if(new InternetConnectionDetector(getContext()).isConnected() && mPlans.size() == 0)
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_ONETIME_PLAN, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSubscriptionPlanURL() + "onetime");

            new HttpClient(HTTP_REQUEST_CODE_GET_PLANS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSubscriptionPlanURL());
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown() && isVisibleToUser)
        {
            this.syncData();
        }
    }


    private void initPlanAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new SubscriptionPlanRecyclerAdapter(getContext(), this, mPlans);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new SubscriptionPlanRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {

            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


    private void adapter_refresh()
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
            if(mPlans.size() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No Plans to Display");
            }

            else
            {
                layout_empty.setVisibility(View.GONE);
            }

            mAdapter.notifyDataSetChanged();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onPreExecute()
    {
        request_listener.onPreExecute();
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
            if(requestCode == HTTP_REQUEST_CODE_GET_ONETIME_PLAN && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);

                    /*SubscriptionPlan sPlan = SubscriptionPlan.getOnetimePlanFromJSON(json.getString(KEY_DATA));

                    if(sPlan != null)
                    {
                        mPlans.add(0, sPlan);
                    }*/

                    mPlans.addAll(0, SubscriptionPlan.getOnetimePlansFromJSON(json.getString(KEY_DATA)));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    this.adapter_refresh();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_GET_PLANS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    mPlans.addAll(SubscriptionPlan.getPlanFromJSON(json.getString(KEY_DATA)));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    this.adapter_refresh();
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
            request_listener.onPostExecute(requestCode, responseCode, response);
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int index, String message)
    {
        if(!new InternetConnectionDetector(getContext()).isConnected())
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            return;
        }

        page_listener.onSubscribed(mPlans.get(index));
    }
}