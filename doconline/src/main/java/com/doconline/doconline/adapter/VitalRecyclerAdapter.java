package com.doconline.doconline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.ehr.model.VitalTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class VitalRecyclerAdapter extends RecyclerView.Adapter<VitalRecyclerAdapter.ViewHolder>
{
    private List<VitalTemplate> dataList;


    public VitalRecyclerAdapter(List<VitalTemplate> dataList)
    {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_vitals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.bindData(dataList.get(position));
    }


    @Override
    public int getItemCount()
    {
        return dataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView label;

        TextView value;


        public ViewHolder(View itemView)
        {
            super(itemView);
            //ButterKnife.bind(this, itemView);

             label =  itemView.findViewById(R.id.title);
            value = itemView.findViewById(R.id.value);
        }

        public void bindData(VitalTemplate data)
        {
            label.setText(String.valueOf(data.getLabel()));

            StringBuilder builder = new StringBuilder();

            if(data.getValue() != null && data.getKey().equalsIgnoreCase("blood_pressure"))
            {
                try
                {
                    JSONObject json = new JSONObject(data.getValue().toString());

                    String mm = json.getString("blood_pressure_mm");
                    String hg = json.getString("blood_pressure_hg");

                    if(!mm.equals("null") && !hg.equals("null"))
                    {
                        builder.append(mm).append("/").append(hg);
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            else
            {
                builder.append(data.getValue() == null ? "" : data.getValue());
            }

            if(!builder.toString().isEmpty() && data.getUnit() != null)
            {
                builder.append(" ").append(data.getUnit());
            }

            value.setText(builder.toString());
        }
    }
}