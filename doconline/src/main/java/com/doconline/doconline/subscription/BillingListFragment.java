package com.doconline.doconline.subscription;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.SubscriptionBillingListRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.model.SubscriptionBilling;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_NEXT_PAGE_URL;
import static com.doconline.doconline.subscription.BillingHistoryActivity.HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL;
import static com.doconline.doconline.subscription.BillingHistoryActivity.mBillings;

/**
 * Created by chiranjitbardhan on 23/08/17.
 */

public class BillingListFragment extends BaseFragment {
    private OnPageSelection page_listener;
    private OnHttpResponse response_listener;

    private SubscriptionBillingListRecyclerAdapter mAdapter;


    LinearLayout layout_empty;
    TextView empty_message;
    RecyclerView mRecyclerView;

    private boolean isLoading;
    private int CURRENT_PAGE;
    private String NEXT_PAGE_URL;


    public static BillingListFragment newInstance() {
        return new BillingListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billing_list, container, false);
      //  ButterKnife.bind(this, view);

        layout_empty = view.findViewById(R.id.layout_empty);
        empty_message = view.findViewById(R.id.empty_message);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initBillingAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof OnHttpResponse) {
            response_listener = (OnHttpResponse) getActivity();
        }

        if (getActivity() instanceof OnPageSelection) {
            page_listener = (OnPageSelection) getActivity();
        }

        if (new InternetConnectionDetector(getContext()).isConnected()) {
            new HttpClient(HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getBillingURL());
        } else {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }

    private void initBillingAdapter() {
        mBillings.clear();

        if (mAdapter != null) {
            return;
        }

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SubscriptionBillingListRecyclerAdapter(getContext(), mBillings);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new SubscriptionBillingListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                BillingHistoryActivity.index = i;
                page_listener.onPageSelection(1, "Billing Summary");
            }
        });


        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                Log.v("onLoadMore", "Load More");

                if (!new InternetConnectionDetector(getContext()).isConnected()) {
                    Toast.makeText(getContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                isLoading = true;

                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        mBillings.add(null);
                        mAdapter.notifyItemInserted(mBillings.size() - 1);
                    }
                };

                handler.post(r);

                new HttpClient(HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL, MyApplication.HTTPMethod.GET.getValue(), BillingListFragment.this)
                        .execute(mController.getBillingURL() + "?page=" + (CURRENT_PAGE + 1));
            }
        });

        this.load_more_on_scroll();
    }


    private void load_more_on_scroll() {
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    Log.v("onScrolled", "onScrolled");

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                        if (!isLoading && !NEXT_PAGE_URL.isEmpty() /*CURRENT_PAGE != LAST_PAGE*/) {
                            if (mAdapter.mOnLoadMoreListener != null) {
                                mAdapter.mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    public void setLoaded() {
        isLoading = false;
    }


    private void json_data(String json_data) {
        if (isLoading) {
            mBillings.remove(mBillings.size() - 1);
            mAdapter.notifyItemRemoved(mBillings.size());
            setLoaded();
        }

        try {
            JSONObject json = new JSONObject(json_data);

            CURRENT_PAGE = json.getInt(Constants.KEY_CURRENT_PAGE);
            NEXT_PAGE_URL = (json.isNull(KEY_NEXT_PAGE_URL)) ? "" : json.getString(KEY_NEXT_PAGE_URL);

            mBillings.addAll(SubscriptionBilling.getSubscriptionBillingListFromJSON(json.getString(KEY_DATA)));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            this.adapter_refresh();
        }
    }


    private void adapter_refresh() {
        try {
            if (mBillings.size() == 0) {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No History Found");
            } else {
                layout_empty.setVisibility(View.GONE);
            }

            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPreExecute() {
        if (CURRENT_PAGE == 0) {
            response_listener.onPreExecute();
        }
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
            if (requestCode == HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.json_data(json.getString(KEY_DATA));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }


    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }
}