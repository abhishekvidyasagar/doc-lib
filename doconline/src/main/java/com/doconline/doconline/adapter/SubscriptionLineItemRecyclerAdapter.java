package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.model.SubscriptionPlan;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;


public class SubscriptionLineItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private List<SubscriptionPlan> itemList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public SubscriptionLineItemRecyclerAdapter(Context context, List<SubscriptionPlan> itemList)
    {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        if (i == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_subscription_biliing_line_item, viewGroup, false);
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
            holder.bindData(i);
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
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return itemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView amount;

        TextView item_description;

        TextView item_name;

        TextView item_serial_no;

        ItemViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);
             amount  = itemView.findViewById(R.id.amount);
            item_description= itemView.findViewById(R.id.item_description);
             item_name= itemView.findViewById(R.id.item_name);
             item_serial_no= itemView.findViewById(R.id.item_serial_no);
            itemView.setOnClickListener(this);
        }

        private void bindData(int i)
        {
            SubscriptionPlan plan = itemList.get(i);

            item_serial_no.setText("#" + (i + 1));
            item_name.setText(plan.getPlanName());
            item_description.setText(plan.getPlanDescription());
            amount.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(plan.getAmount()));
        }

        @Override
        public void onClick(View v)
        {

        }
    }


    static class LoadingViewHolder extends RecyclerView.ViewHolder
    {
        //@BindView(R.id.spin_kit)
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
}