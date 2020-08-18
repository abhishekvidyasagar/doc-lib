package com.doconline.doconline.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.doconline.doconline.adapter.OrderItemsRecyclerAdapter;
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
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.Orders;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_NEXT_PAGE_URL;
import static com.doconline.doconline.order.OrderActivity.HTTP_REQUEST_CODE_GET_ORDERS;
import static com.doconline.doconline.subscription.BillingHistoryActivity.mBillings;

/**
 * Created by cbug on 29/9/17.
 */

public class OrderListFragment extends BaseFragment
{
    private Context context;
    private OnPageSelection page_listener;
    private OnTaskCompleted task_listener;
    private OnHttpResponse response_listener;

    RecyclerView mRecyclerView;

    LinearLayout layout_empty;

    TextView empty_message;

    private OrderItemsRecyclerAdapter mAdapter;
    private ArrayList<Orders> mList = new ArrayList<>();

    private boolean isLoading;
    private int CURRENT_PAGE;
    private String NEXT_PAGE_URL;


    public OrderListFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public OrderListFragment(Context context, OnPageSelection page_listener, OnHttpResponse response_listener, OnTaskCompleted task_listener)
    {
        this.context = context;
        this.page_listener = page_listener;
        this.task_listener = task_listener;
        this.response_listener = response_listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
       // ButterKnife.bind(this, view);

       mRecyclerView = view.findViewById(R.id.recycler_view);
        layout_empty = view.findViewById(R.id.layout_empty);
        empty_message = view.findViewById(R.id.empty_message);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        layout_empty.setVisibility(View.GONE);
        empty_message.setText("No Order Found");

        this.iniAdapter();

        if (new InternetConnectionDetector(getActivity()).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_ORDERS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getOrderListURL());
        }
    }


    private void iniAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new OrderItemsRecyclerAdapter(context, mList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new OrderItemsRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {
                task_listener.onTaskCompleted(true, position, mList.get(position).getId());
                page_listener.onPageSelection(1, "Order Details");
            }
        });

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore()
            {
                isLoading = true;

                Handler handler = new Handler();
                final Runnable r = new Runnable() {

                    public void run()
                    {
                        mList.add(null);
                        mAdapter.notifyItemInserted(mBillings.size() - 1);
                    }
                };

                handler.post(r);

                if (new InternetConnectionDetector(getActivity()).isConnected())
                {
                    new HttpClient(HTTP_REQUEST_CODE_GET_ORDERS, MyApplication.HTTPMethod.GET.getValue(), OrderListFragment.this)
                            .execute(mController.getOrderListURL() + "?page=" + (CURRENT_PAGE + 1));
                }

                else
                {
                    new CustomAlertDialog(getContext(), OrderListFragment.this, getView()).snackbarForInternetConnectivity();
                }
            }
        });

        this.loadMoreOnScroll();
    }


    private void loadMoreOnScroll()
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
                        if(!isLoading && !NEXT_PAGE_URL.isEmpty() /*CURRENT_PAGE != LAST_PAGE*/)
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


    public void json_data(String json_data)
    {
        if(isLoading)
        {
            mList.remove(mList.size() - 1);
            mAdapter.notifyItemRemoved(mList.size());
            setLoaded();
        }

        try
        {
            JSONObject json = new JSONObject(json_data);

            CURRENT_PAGE = json.getInt(Constants.KEY_CURRENT_PAGE);
            NEXT_PAGE_URL = (json.isNull(KEY_NEXT_PAGE_URL)) ? "" : json.getString(KEY_NEXT_PAGE_URL);

            mList.addAll(Orders.getOrderListFromJSON(json.getString(KEY_DATA)));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            mAdapter.notifyDataSetChanged();

            if (mList.size() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
            }
        }
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
            if(requestCode == HTTP_REQUEST_CODE_GET_ORDERS && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.json_data(json.getString(KEY_DATA));
                }

                catch (Exception e)
                {
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
            response_listener.onPostExecute(requestCode, responseCode, response);
        }
    }
}