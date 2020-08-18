package com.doconline.doconline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.OnAllergyAction;
import com.doconline.doconline.model.Allergy;
import com.doconline.doconline.model.Disease;

import java.util.List;


public class AllergyRecyclerAdapter extends RecyclerView.Adapter<AllergyRecyclerAdapter.ViewHolder>
{

    private List<Allergy> mItems;
    private OnItemClickListener clickListener;
    private OnAllergyAction listener;


    public AllergyRecyclerAdapter(OnAllergyAction listener, List<Allergy> mItems)
    {
        super();

        this.listener = listener;
        this.mItems = mItems;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_allergies, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i)
    {

        final Disease disease = mItems.get(i);

        viewHolder.tv_allergies.setText(disease.disease.toUpperCase());

        viewHolder.ib_delete.setTag(i);
    }


    @Override
    public int getItemCount()
    {
        return mItems.size();
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
            ib_delete = itemView.findViewById(R.id.ib_delete);


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
                    listener.onAllergyUpdated(index, mItems.get(index).id);
                } else if (id == R.id.ib_delete) {
                    listener.onAllergyDeleted(index, mItems.get(index).id);
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


    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}