package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.ChatMessage;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mMessages;
    private OnItemClickListener clickListener;
    public OnLoadMoreListener mOnLoadMoreListener;
    private OnTaskCompleted listener;
    private Context context;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public ChatListRecyclerAdapter(Context context, OnTaskCompleted listener, List<ChatMessage> mMessages) {
        super();

        this.context = context;
        this.listener = listener;
        this.mMessages = mMessages;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_chat_list, viewGroup, false);
            return new ItemViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_item, viewGroup, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;

            final ChatMessage msg = mMessages.get(i);

            if (/*msg.getDoctorFullName().trim().length() == 0*/msg.getDoctor_id() == 0) {
                holder.tv_mci_pno.setText(Helper.toCamelCase("Doctor Not Connected"));
                holder.tv_qualification.setVisibility(View.GONE);
            } else {
                holder.tv_mci_pno.setText("MCI: " + msg.getDoctor_mci()/* + ", Reg No: " + msg.getDoctor_praction_no()*/);
                holder.tv_qualification.setVisibility(View.VISIBLE);
                holder.tv_qualification.setText(msg.getDoctor_qualification());
            }

            holder.tv_date.setText(Helper.format_date(Helper.UTC_to_Local_TimeZone(msg.getTimestamp())));
            holder.tv_message.setText(msg.getMessage());

            holder.iv_thumbnail.setTag(i);
        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
      //  @BindView(R.id.spin_kit)
        SpinKitView progressBar;

        private LoadingViewHolder(View itemView) {
            super(itemView);
           // ButterKnife.bind(this, itemView);
            progressBar = itemView.findViewById(R.id.spin_kit);
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_mci_pno;

        TextView tv_qualification;

        TextView tv_date;

        TextView tv_message;

        CircleImageView iv_thumbnail;

        private ItemViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);

             tv_mci_pno = itemView.findViewById(R.id.tv_mci_pno);
            tv_qualification= itemView.findViewById(R.id.tv_qualification) ;
           tv_date= itemView.findViewById(R.id.tv_timestamp);
            tv_message= itemView.findViewById(R.id.tv_message);
            iv_thumbnail= itemView.findViewById(R.id.thumbnail);


            itemView.setOnClickListener(this);
            iv_thumbnail.setOnClickListener(onButtonClickListener);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.thumbnail) {
                }
            }
        };

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}