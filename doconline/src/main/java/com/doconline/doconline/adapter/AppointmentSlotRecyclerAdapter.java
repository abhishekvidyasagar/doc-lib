package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.doconline.doconline.app.Constants.YYYY_MM_DD;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;


public class AppointmentSlotRecyclerAdapter extends RecyclerView.Adapter<AppointmentSlotRecyclerAdapter.VersionViewHolder>
{
    private Context context;
    private OnItemClickListener clickListener;

    /**
     * This array is used to store all the view references
     */
    private List<View> viewList = new ArrayList<>();
    private List<TimeSlot> mTimeSlot;
    private String date;


    public AppointmentSlotRecyclerAdapter(Context context, List<TimeSlot> mTimeSlot, String date)
    {
        this.context = context;
        this.mTimeSlot = mTimeSlot;
        this.date = date;

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
            Date value = sdf.parse(date);
            sdf = new SimpleDateFormat(YYYY_MM_DD);
            this.date = sdf.format(value);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_slot, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VersionViewHolder vHolder, int i)
    {
        vHolder.slot_main.setTag(i);
        vHolder.bindData(mTimeSlot.get(i));
    }

    @Override
    public int getItemCount()
    {
        return mTimeSlot == null ? 0 : mTimeSlot.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView slot_time;

        LinearLayout slot_main;


        private VersionViewHolder(View itemView)
        {
            super(itemView);
            //ButterKnife.bind(this, itemView);

            slot_time = itemView.findViewById(R.id.slot_time);
             slot_main = itemView.findViewById(R.id.slot_main);

            itemView.setOnClickListener(this);

            /**
             * Add view references in viewList
             */
            viewList.add(itemView);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }


        private void bindData(TimeSlot ts)
        {
            try
            {
                slot_time.setText(Helper.time_format(Helper.UTC_to_Local_TimeZone(date + " " + ts.start_time)));
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

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}