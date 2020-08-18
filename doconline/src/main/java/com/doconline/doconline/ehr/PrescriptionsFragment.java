package com.doconline.doconline.ehr;

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
import com.doconline.doconline.adapter.PrescriptionHistoryRecyclerAdapter;
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

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_NEXT_PAGE_URL;


public class PrescriptionsFragment extends BaseFragment
{

    RecyclerView mRecyclerView;
    RelativeLayout layout_loading;
    LinearLayout layout_empty;
    TextView empty_message;

    public static final int HTTP_REQUEST_CODE = 0;


    public PrescriptionsFragment()
    {

    }

    private PrescriptionHistoryRecyclerAdapter mAdapter;

    private boolean isLoading;
    private int CURRENT_PAGE, LAST_PAGE;
    private String NEXT_PAGE_URL;


    public static PrescriptionsFragment newInstance()
    {
        return new PrescriptionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ehr_prescriptions, container, false);
       // ButterKnife.bind(this, view);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        layout_loading= view.findViewById(R.id.layout_loading);
        layout_empty= view.findViewById(R.id.layout_empty);
        empty_message= view.findViewById(R.id.empty_message);
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
                        .execute(mController.getEhrPrescriptionURL());
            }
        }

        else
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

        mAdapter = new PrescriptionHistoryRecyclerAdapter(getContext(), mController.prescriptionList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new PrescriptionHistoryRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {
                if(!isLoading)
                {
                    Intent intent = new Intent(getActivity(), PrescriptionActivity.class);
                    intent.putExtra("INDEX", i);
                    intent.putExtra("NEXT_PAGE_URL", NEXT_PAGE_URL);
                    startActivity(intent);
                }
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
                        mController.prescriptionList.add(null);
                        mAdapter.notifyItemInserted(mController.prescriptionList.size() - 1);
                    }
                };

                handler.post(r);
                new HttpClient(HTTP_REQUEST_CODE, MyApplication.HTTPMethod.GET.getValue(), PrescriptionsFragment.this)
                        .execute(mController.getEhrPrescriptionURL() + "?page=" + (CURRENT_PAGE + 1));
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
        if(mController.prescriptionList.size() == 0)
        {
            layout_empty.setVisibility(View.VISIBLE);
            empty_message.setText("At a Glance: View all your past\nprescriptions here!");
        }

        else
        {
            layout_empty.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
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


    private void displayAppointment(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);
            json = json.getJSONObject(KEY_DATA);

            if(isLoading)
            {
                mController.prescriptionList.remove(mController.prescriptionList.size() - 1);
                mAdapter.notifyItemRemoved(mController.prescriptionList.size());
                setLoaded();
            }

            CURRENT_PAGE = json.getInt(Constants.KEY_CURRENT_PAGE);
            LAST_PAGE = json.getInt(Constants.KEY_LAST_PAGE);
            NEXT_PAGE_URL = json.isNull(KEY_NEXT_PAGE_URL) ? "" : json.getString(KEY_NEXT_PAGE_URL);

            mController.prescriptionList.addAll(Appointment.getPrescriptionListFromJSON(json.getString(KEY_DATA)));
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
}