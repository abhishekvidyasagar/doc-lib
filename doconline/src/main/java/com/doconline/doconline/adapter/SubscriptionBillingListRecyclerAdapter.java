package com.doconline.doconline.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.model.SubscriptionBilling;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.Date;
import java.util.List;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;


public class SubscriptionBillingListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private OnItemClickListener clickListener;
    public OnLoadMoreListener mOnLoadMoreListener;
    private List<SubscriptionBilling> subscriptionBillingList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public SubscriptionBillingListRecyclerAdapter(Context context, List<SubscriptionBilling> subscriptionBillingList)
    {
        this.context = context;
        this.subscriptionBillingList = subscriptionBillingList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        if (i == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_subscription_billing_list, viewGroup, false);
            return new ItemViewHolder(view);
        }

        else if (i == VIEW_TYPE_LOADING)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_item, viewGroup, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, int i)
    {
        if(vHolder instanceof ItemViewHolder)
        {
            ItemViewHolder holder = (ItemViewHolder) vHolder;
            holder.bindData(subscriptionBillingList.get(i));
        }

        else if (vHolder instanceof LoadingViewHolder)
        {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) vHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount()
    {
        return subscriptionBillingList == null ? 0 : subscriptionBillingList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return subscriptionBillingList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView amount_total;

        TextView status;

        TextView issued_on;

        ItemViewHolder(View itemView)
        {
            super(itemView);
          //  ButterKnife.bind(this, itemView);
            amount_total = itemView.findViewById(R.id.amount_total);
            status = itemView.findViewById(R.id.status);
           issued_on =  itemView.findViewById(R.id.issued_on);

            itemView.setOnClickListener(this);
        }

        private void bindData(SubscriptionBilling billing)
        {
            amount_total.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(billing.getAmount() - billing.getDiscount()));
            status.setText(billing.getStatus().toUpperCase());

            if(billing.getStatus().equalsIgnoreCase("paid"))
            {
                status.setTextColor(ContextCompat.getColor(context, R.color.light_green));
            }

            else
            {
                status.setTextColor(ContextCompat.getColor(context, R.color.myTextSecondaryColor));
            }

            if(!billing.getIssuedAt().isEmpty())
            {
                issued_on.setVisibility(View.VISIBLE);

                try
                {
                    Date value = new Date((Long.valueOf(billing.getIssuedAt()) * 1000L));
                    issued_on.setText(DateUtils.getRelativeTimeSpanString(value.getTime(), System.currentTimeMillis(), MINUTE_IN_MILLIS));
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else
            {
                issued_on.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    static class LoadingViewHolder extends RecyclerView.ViewHolder
    {
     //   @BindView(R.id.spin_kit)
        SpinKitView progressBar;

        private LoadingViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            progressBar =  itemView.findViewById(R.id.spin_kit);
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

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}