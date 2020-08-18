package com.doconline.doconline.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.OnMedicationAction;
import com.doconline.doconline.model.Medication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.doconline.doconline.app.Constants.MMM_DD_YYYY;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;


public class MedicationRecyclerAdapter extends RecyclerView.Adapter<MedicationRecyclerAdapter.ViewHolder>
{

    private List<Medication> mItems;
    private OnItemClickListener clickListener;
    private Context context;
    private OnMedicationAction listener;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf1;


    public MedicationRecyclerAdapter(Context context, OnMedicationAction listener, List<Medication> mItems)
    {
        super();

        this.context = context;
        this.listener = listener;

        this.mItems = mItems;

        this.sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
        this.sdf1 = new SimpleDateFormat(MMM_DD_YYYY);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_medications, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i)
    {
        final Medication medication = mItems.get(i);

        Log.e("AAA", "medication object : "+medication.getNoOfDays());

        viewHolder.tv_name.setText(medication.getName().toUpperCase());

        if(!medication.getIntakeTime().isEmpty())
        {
            viewHolder.tv_intake_time.setText("Intake Time - " + medication.getIntakeTime());
        }

        else
        {
            viewHolder.tv_intake_time.setText("Intake Time - N/A");
        }

        try
        {
            if(!medication.getFromDate().isEmpty())
            {
                Date value = sdf.parse(medication.getFromDate() + " 00:00:00");
                viewHolder.tv_from_date.setText(sdf1.format(value));
            }

            else
            {
                viewHolder.tv_from_date.setText("N/A");
            }

            if(!medication.getToDate().isEmpty())
            {
                Date value = sdf.parse(medication.getToDate() + " 00:00:00");
                viewHolder.tv_to_date.setText(sdf1.format(value));
            }

            else
            {
                viewHolder.tv_to_date.setText("N/A");
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(!medication.getNotes().isEmpty())
        {
            viewHolder.tv_notes.setVisibility(View.VISIBLE);
            viewHolder.tv_notes.setText("Note : " + medication.getNotes());
        }

        else
        {
            //viewHolder.tv_notes.setVisibility(View.GONE);
            viewHolder.tv_notes.setText("Note : N/A");
        }

        if(!medication.getNoOfDays().isEmpty())
        {
            viewHolder.tvNoOfDays.setText("No of Days - " + medication.getNoOfDays());
        }

        else if (medication.getNoOfDays().equalsIgnoreCase("0")){
            viewHolder.tvNoOfDays.setText("No of Days - N/A");
        }
        else
        {
            viewHolder.tvNoOfDays.setText("No of Days - N/A");
        }


        if(!medication.getStatus().isEmpty())
        {
            if (medication.getStatus().equalsIgnoreCase("2")){
                viewHolder.tvStatus.setText("Status - Past");
            }
            else if (medication.getStatus().equalsIgnoreCase("1")){
                viewHolder.tvStatus.setText("Status - Present");
            }
            else if (medication.getStatus().equalsIgnoreCase("0")){
                viewHolder.tvStatus.setText("Status - N/A");
            }
            //viewHolder.tvStatus.setText(String.valueOf("Status - " + medication.getStatus()));
        }

        else
        {
            viewHolder.tvStatus.setText("Status - N/A");
        }

        viewHolder.ib_edit.setTag(i);
        viewHolder.ib_delete.setTag(i);
    }


    @Override
    public int getItemCount()
    {
        return mItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tv_from_date;

        TextView tv_to_date;

        TextView tv_intake_time;

        TextView tv_notes;

        TextView tv_name;

        ImageButton ib_edit;

        ImageButton ib_delete;


        TextView tvNoOfDays;

        TextView tvStatus;


        public ViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

             tv_from_date = itemView.findViewById(R.id.tvFromDate);
             tv_to_date= itemView.findViewById(R.id.tvToDate);
             tv_intake_time= itemView.findViewById(R.id.tvIntakeTime);
            tv_notes= itemView.findViewById(R.id.tvNotes);
             tv_name= itemView.findViewById(R.id.tvName);
            ib_edit= itemView.findViewById(R.id.ib_edit);
            ib_delete= itemView.findViewById(R.id.ib_delete);

             tvNoOfDays= itemView.findViewById(R.id.tvNoOfDays);

            tvStatus= itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(this);
            ib_edit.setOnClickListener(onButtonClickListener);
            ib_delete.setOnClickListener(onButtonClickListener);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                int id = v.getId();
                if (id == R.id.ib_edit) {
                    listener.onMedicationUpdated(index, mItems.get(index).getId());
                } else if (id == R.id.ib_delete) {
                    listener.onMedicationDeleted(index, mItems.get(index).getId());
                }
            }
        };

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