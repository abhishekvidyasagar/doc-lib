package com.doconline.doconline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.utils.FileUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;


public class EHRRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private EHRRecyclerAdapter.OnItemClickListener clickListener;
    public OnLoadMoreListener mOnLoadMoreListener;

    private List<FileUtils> fileUtilsList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public EHRRecyclerAdapter(List<FileUtils> fileUtilsList)
    {
        this.fileUtilsList = fileUtilsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        if (i == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_ehr_files, parent, false);
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
            holder.bindData(fileUtilsList.get(position));
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
        return fileUtilsList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return fileUtilsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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

    class ItemViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv_date;

        TextView tv_caption;

        ImageView iv_icon;


        public ItemViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

             tv_date  =itemView.findViewById(R.id.tv_date);
            tv_caption = itemView.findViewById(R.id.tv_caption);
             iv_icon = itemView.findViewById(R.id.iv_icon);
        }

        /*@Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }*/

        public void bindData(FileUtils data)
        {

        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final EHRRecyclerAdapter.OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}