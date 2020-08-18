package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.model.Orders;
import com.github.ybq.android.spinkit.SpinKitView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.doconline.doconline.app.Constants.DD_MMM_YYYY_HH_MM_A;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;

/**
 * Created by cbug on 29/9/17.
 */

public class OrderItemsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ArrayList<Orders> orderLists = new ArrayList<>();
    private OnItemClickListener clickListener;
    public OnLoadMoreListener mOnLoadMoreListener;
    private Context context;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public OrderItemsRecyclerAdapter(Context context, ArrayList<Orders> orderLists)
    {
        this.context = context;
        this.orderLists = orderLists;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_order_items, parent, false);
            return new OrderListViewHolder(view);
        }

        else if (viewType == VIEW_TYPE_LOADING)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof OrderListViewHolder)
        {
            final OrderListViewHolder viewHolder = (OrderListViewHolder) holder;
            final Orders orderModel = orderLists.get(position);

            viewHolder.order_id.setText(orderModel.getOrderId());
            viewHolder.amount.setText((context.getResources().getString(R.string.Rs) + " " + Constants.df.format(Double.parseDouble(orderModel.getAmount()))));

            String date = Helper.UTC_to_Local_TimeZone(orderModel.getDate());

            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
                Date value = sdf.parse(date);
                sdf = new SimpleDateFormat(DD_MMM_YYYY_HH_MM_A);
                viewHolder.date.setText(sdf.format(value));
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        else if (holder instanceof LoadingViewHolder)
        {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder
    {

        SpinKitView progressBar;

        private LoadingViewHolder(View itemView)
        {
            super(itemView);
            //ButterKnife.bind(this, itemView);

            progressBar = itemView.findViewById(R.id.spin_kit);
        }
    }


    @Override
    public int getItemViewType(int position)
    {
        return orderLists.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount()
    {
        return orderLists.size();
    }

    class OrderListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView order_id;
        TextView date;
        TextView amount;

        private OrderListViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            order_id = itemView.findViewById(R.id.orderId);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}