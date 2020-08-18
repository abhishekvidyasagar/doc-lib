package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.model.Medicine;

import java.util.ArrayList;


/**
 * Created by cbug on 25/9/17.
 */

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>
{

    private ArrayList<Medicine> mMedicines;
    private Context context;

    public MedicineAdapter(ArrayList<Medicine> editMedicines, Context context)
    {
        this.mMedicines = editMedicines;
        this.context = context;
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MedicineViewHolder holder, final int position)
    {
        final Medicine medicine = mMedicines.get(position);

        holder.plus.setTag(position);
        holder.minus.setTag(position);

        holder.medicine_name.setText(medicine.getMedicineName().toUpperCase());
        holder.manufacturer.setText(medicine.getManufacturer().toUpperCase());
        holder.required_quantity.setText(String.valueOf(medicine.getRquiredQuantity()));
        holder.pack_size.setText(String.valueOf(medicine.getPackSize()));
        holder.mrp.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(Double.parseDouble(medicine.getMrp())));
        holder.discount.setText(String.valueOf(medicine.getDiscount()));
        holder.price.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(Double.parseDouble(medicine.getPrice())));
        holder.required_medicine_packs.setText(String.valueOf(medicine.getRequired_pack_size()));

        if(medicine.getAvailableQuantity() == 0)
        {
            holder.stock_availability.setTextColor(Color.RED);
            holder.stock_availability.setText("Out of Stock");
        }

        else
        {
            holder.stock_availability.setTextColor(Color.rgb(0,100,0));
            holder.stock_availability.setText("In Stock");
        }

        holder.minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                int index = (int) view.getTag();
                int changeVal = mMedicines.get(index).getRequired_pack_size();

                if (changeVal > 1)
                {
                    changeVal--;
                    mMedicines.get(index).setRequired_pack_size(changeVal);
                    notifyDataSetChanged();
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                int index = (int) view.getTag();
                int changeVal = mMedicines.get(index).getRequired_pack_size();

                if(changeVal == mMedicines.get(index).getDefault_required_pack_size())
                {
                    Toast.makeText(context,"You can not order more than " +
                            mMedicines.get(index).getDefault_required_pack_size()+ " packs", Toast.LENGTH_LONG).show();
                }

                else
                {
                    changeVal++;
                    mMedicines.get(index).setRequired_pack_size(changeVal);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mMedicines.size();
    }

    public void removeItem(int position)
    {
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()

        mMedicines.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void restoreItem(Medicine item, int position)
    {
        mMedicines.add(position, item);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }


    public class MedicineViewHolder extends RecyclerView.ViewHolder
    {

        TextView medicine_name;

        TextView stock_availability;

        TextView manufacturer;

        TextView required_quantity;

        TextView required_medicine_packs;

        TextView pack_size;

        TextView mrp;

        TextView discount;

        TextView price;

        ImageButton minus;

        ImageButton plus;

        RelativeLayout viewBackground;

        public RelativeLayout viewForeground;

        private MedicineViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

             medicine_name = itemView.findViewById(R.id.medicine_name);
             stock_availability= itemView.findViewById(R.id.stock_availability);
             manufacturer= itemView.findViewById(R.id.manufactutrer);
             required_quantity= itemView.findViewById(R.id.required_quantity);
             required_medicine_packs= itemView.findViewById(R.id.required_medicine_packs);
             pack_size= itemView.findViewById(R.id.pack_size);
             mrp= itemView.findViewById(R.id.mrp);
             discount= itemView.findViewById(R.id.discount);
             price= itemView.findViewById(R.id.price);
             minus= itemView.findViewById(R.id.minus);
             plus= itemView.findViewById(R.id.plus);
            viewBackground= itemView.findViewById(R.id.view_background);
            viewForeground= itemView.findViewById(R.id.view_foreground);
        }
    }
}