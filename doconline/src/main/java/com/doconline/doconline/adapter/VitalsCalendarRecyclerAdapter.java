package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.ehr.model.Vital;

import java.text.SimpleDateFormat;
import java.util.List;


public class VitalsCalendarRecyclerAdapter extends RecyclerView.Adapter<VitalsCalendarRecyclerAdapter.ViewHolder>
{
    private VitalsCalendarRecyclerAdapter.OnItemClickListener clickListener;
    private List<Vital> dateList;
    private int selected;
    private Context context;


    public VitalsCalendarRecyclerAdapter(Context context, List<Vital> dateList)
    {
        this.context = context;
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_vitals_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.bindData(dateList.get(position), position);
    }


    @Override
    public int getItemCount()
    {
        return dateList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tv_date;

        TextView tv_month;

        TextView tv_year;

        public ViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            tv_date=itemView.findViewById(R.id.tv_date);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_year = itemView.findViewById(R.id.tv_year);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            selected = getAdapterPosition();
            clickListener.onItemClick(v, getAdapterPosition());
        }

        public void bindData(Vital data, int position)
        {
            try
            {
                if(position == selected)
                {
                    tv_date.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }

                else
                {
                    tv_date.setTextColor(context.getResources().getColor(R.color.myTextPrimaryColor));
                }

                long recorder_at = data.getRecordedAt() * 1000L;

                //SimpleDateFormat formatter = new SimpleDateFormat(Constants.MMM_DD_YYYY);
                //String timestamp = formatter.format(recorder_at);

                //tv_date.setText(timestamp);

                SimpleDateFormat formatter = new SimpleDateFormat("MMM");
                String value = formatter.format(recorder_at);
                tv_month.setText(value);

                formatter = new SimpleDateFormat("dd");
                value = formatter.format(recorder_at);
                tv_date.setText(value);

                formatter = new SimpleDateFormat("yyyy");
                value = formatter.format(recorder_at);
                tv_year.setText(value);
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

    public void SetOnItemClickListener(final VitalsCalendarRecyclerAdapter.OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}