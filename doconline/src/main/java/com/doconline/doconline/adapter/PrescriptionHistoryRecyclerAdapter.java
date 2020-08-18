package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.model.Appointment;
import com.github.ybq.android.spinkit.SpinKitView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.doconline.doconline.app.Constants.DD;
import static com.doconline.doconline.app.Constants.HH_MM_A;
import static com.doconline.doconline.app.Constants.MMM;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;


public class PrescriptionHistoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private OnItemClickListener clickListener;
    private List<Appointment> uList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public OnLoadMoreListener mOnLoadMoreListener;

    private SimpleDateFormat sdf;
    private SimpleDateFormat sdfMonth;
    private SimpleDateFormat sdfDate;
    private SimpleDateFormat sdfTime;

    public PrescriptionHistoryRecyclerAdapter(Context context, List<Appointment> uList)
    {
        this.context = context;
        this.uList = uList;
        this.sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
        this.sdfMonth = new SimpleDateFormat(MMM);
        this.sdfDate = new SimpleDateFormat(DD);
        this.sdfTime = new SimpleDateFormat(HH_MM_A);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        if (i == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_prescription_history, viewGroup, false);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vHolder, int i)
    {
        if(vHolder instanceof ItemViewHolder)
        {
            ItemViewHolder holder = (ItemViewHolder) vHolder;
            holder.bindData(uList.get(i));
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
        return uList == null ? 0 : uList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return uList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView date;

        TextView month;

        TextView time;

        TextView appointment_id;

        TextView patient_name;

        CardView card_main;

        private ItemViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);
            date = itemView.findViewById(R.id.date);
             month= itemView.findViewById(R.id.month);
             time= itemView.findViewById(R.id.time);
             appointment_id= itemView.findViewById(R.id.appointment_id);
             patient_name= itemView.findViewById(R.id.patient_name);
            card_main= itemView.findViewById(R.id.card_main);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }

        private void bindData(Appointment ts)
        {
            String scheduled_time = Helper.UTC_to_Local_TimeZone(ts.getScheduledAt());

            try
            {
                Date value = sdf.parse(scheduled_time);

                time.setText(sdfTime.format(value));
                date.setText(sdfDate.format(value));
                month.setText(sdfMonth.format(value).toUpperCase());
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            patient_name.setText(Helper.toCamelCase(ts.getPatient().getFullName()));
            appointment_id.setText("BOOKING ID: " + ts.getPublicAppointmentId());
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