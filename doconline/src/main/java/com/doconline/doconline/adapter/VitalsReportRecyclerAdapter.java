package com.doconline.doconline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.ehr.model.Vital;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnItemUpdated;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.github.ybq.android.spinkit.SpinKitView;

import java.text.SimpleDateFormat;
import java.util.List;


public class VitalsReportRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private VitalsReportRecyclerAdapter.OnItemClickListener clickListener;
    public OnLoadMoreListener mOnLoadMoreListener;

    private List<Vital> dateList;

    private OnItemUpdated updateListener;
    private OnItemDeleted deleteListener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public VitalsReportRecyclerAdapter(List<Vital> dateList, OnItemUpdated updateListener, OnItemDeleted deleteListener)
    {
        this.dateList = dateList;
        this.updateListener = updateListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        if (i == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_vitals_date, parent, false);
            return new ItemViewHolder(view);
        }

        else if (i == VIEW_TYPE_LOADING)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vHolder, int position)
    {
        if(vHolder instanceof ItemViewHolder)
        {
            ItemViewHolder holder = (ItemViewHolder) vHolder;


            holder.ib_edit.setTag(position);
            holder.ib_delete.setTag(position);

            holder.bindData(dateList.get(position));
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
        return dateList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return dateList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder
    {

        SpinKitView progressBar;

        private LoadingViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);
            progressBar = itemView.findViewById(R.id.spin_kit);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tv_date;

        ImageButton ib_edit;

        ImageButton ib_delete;


        public ItemViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            tv_date = itemView.findViewById(R.id.tv_date);
            ib_edit = itemView.findViewById(R.id.ib_edit);
             ib_delete = itemView.findViewById(R.id.ib_delete);

            itemView.setOnClickListener(this);
            ib_delete.setOnClickListener(onButtonClickListener);
            ib_edit.setOnClickListener(onButtonClickListener);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                int id = v.getId();
                if (id == R.id.ib_edit) {
                    updateListener.onItemUpdated(index, index);
                } else if (id == R.id.ib_delete) {
                    deleteListener.onItemDeleted(index, index);
                }
            }
        };
        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }

        public void bindData(Vital data)
        {
            try
            {
                long recorder_at = data.getRecordedAt() * 1000L;

                SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MMM_YYYY_HH_MM_A);
                String timestamp = formatter.format(recorder_at);

                tv_date.setText(timestamp);
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final VitalsReportRecyclerAdapter.OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}