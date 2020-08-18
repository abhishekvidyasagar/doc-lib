package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.model.Medicine;

import java.util.ArrayList;


/**
 * Created by cbug on 27/9/17.
 */

public class MedicationsAdapter extends RecyclerView.Adapter<MedicationsAdapter.MedicationsViewHolder>{

    private Context context;
    private ArrayList<Medicine> mMedicines;

    public MedicationsAdapter(Context context, ArrayList<Medicine> mMedicines)
    {
        this.context = context;
        this.mMedicines = mMedicines;
    }

    @Override
    public MedicationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_medicine_review,parent,false);
        return new MedicationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationsViewHolder holder, int position)
    {
        Medicine medicine = mMedicines.get(position);

        holder.medicine_name.setText(medicine.getMedicineName().toUpperCase());
        holder.no_of_pack.setText(String.valueOf(medicine.getRequired_pack_size()));

        int quantity = medicine.getRequired_pack_size();
        double price = Double.parseDouble(medicine.getPrice());
        double totalPrice = Math.floor(quantity * price * 100)/100;

        holder.price.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(Double.parseDouble(medicine.getPrice())));
        holder.total.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(totalPrice));
    }

    @Override
    public int getItemCount()
    {
        return mMedicines.size();
    }

    class MedicationsViewHolder extends RecyclerView.ViewHolder
    {

        TextView medicine_name;

        TextView no_of_pack;

        TextView total;

        TextView price;

        MedicationsViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

             medicine_name = itemView.findViewById(R.id.medicine_name);
             no_of_pack= itemView.findViewById(R.id.no_of_pack);
            total= itemView.findViewById(R.id.total);
            price= itemView.findViewById(R.id.price);
        }
    }
}