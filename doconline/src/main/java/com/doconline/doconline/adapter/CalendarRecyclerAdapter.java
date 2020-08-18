package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.TimeSlot;


public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.VersionViewHolder>
{
    /**
     * For store view state use SparseBooleanArray
     * private SparseBooleanArray selectedItems = new SparseBooleanArray();
     */
    private Context context;
    private OnItemClickListener clickListener;

    /**
     * Get Constants Controller Class object (see application tag in AndroidManifest.xml)
     */
    private MyApplication mController;


    public CalendarRecyclerAdapter(Context context)
    {
        this.context = context;
        this.mController = MyApplication.getInstance();
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_calendar, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VersionViewHolder vHolder, int i)
    {
        vHolder.bindData(mController.calendarList.get(i));
    }


    @Override
    public int getItemCount()
    {
        return mController.calendarList == null ? 0 : mController.getCalendarSize();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder
    {


        TextView date;

        private VersionViewHolder(View itemView)
        {
            super(itemView);
            //ButterKnife.bind(this, itemView);

            date = itemView.findViewById(R.id.date);
        }


        private void bindData(TimeSlot ts)
        {
            try
            {
                date.setText(Helper.UTC_to_Local_Calendar(ts.slot_date));
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