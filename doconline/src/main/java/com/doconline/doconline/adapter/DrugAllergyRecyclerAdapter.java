package com.doconline.doconline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.OnDrugAllergyAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class DrugAllergyRecyclerAdapter extends RecyclerView.Adapter<DrugAllergyRecyclerAdapter.ViewHolder>
{

    private List<JSONObject> mItems;
    private DrugAllergyRecyclerAdapter.OnItemClickListener clickListener;
    private OnDrugAllergyAction listener;


    public DrugAllergyRecyclerAdapter(OnDrugAllergyAction listener, List<JSONObject> mItems)
    {
        super();

        this.listener = listener;
        this.mItems = mItems;
    }


    @Override
    public DrugAllergyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_allergies, viewGroup, false);
        return new DrugAllergyRecyclerAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final DrugAllergyRecyclerAdapter.ViewHolder viewHolder, int i)
    {

        final JSONObject drug = mItems.get(i);

        try {
            viewHolder.tv_allergies.setText(drug.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.ib_delete.setTag(i);
    }


    @Override
    public int getItemCount()
    {
        if (mItems != null){
            return mItems.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tv_allergies;

        ImageButton ib_delete;

        public ViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);
             tv_allergies = itemView.findViewById(R.id.tv_allergies);
            ib_delete =  itemView.findViewById(R.id.ib_delete);

            itemView.setOnClickListener(this);
            ib_delete.setOnClickListener(onButtonClickListener);
        }


        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                int id = v.getId();
                if (id == R.id.ib_edit) {
                    try {
                        listener.onDrugAllergyUpdated(index, Integer.parseInt(mItems.get(index).getString("id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (id == R.id.ib_delete) {
                    try {
                        listener.onDrugAllergyDeleted(index, Integer.parseInt(mItems.get(index).getString("id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        @Override
        public void onClick(View v)
        {
            //clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }


    public void SetOnItemClickListener(final DrugAllergyRecyclerAdapter.OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}