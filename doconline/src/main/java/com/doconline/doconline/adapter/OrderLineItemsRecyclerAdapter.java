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

import java.util.List;


/**
 * Created by cbug on 29/9/17.
 */

public class OrderLineItemsRecyclerAdapter extends RecyclerView.Adapter<OrderLineItemsRecyclerAdapter.OrderDetailsViewHolder> {

    private List<Medicine> mList;
    private Context context;

    public OrderLineItemsRecyclerAdapter(Context context, List<Medicine> mList)
    {
        this.mList = mList;
        this.context = context;
    }


    @Override
    public OrderDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item_order_line_items, parent, false);
        return new OrderDetailsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(OrderDetailsViewHolder holder, int position)
    {
        Medicine medicine = mList.get(position);

        try
        {
            holder.medicine_name.setText(medicine.getMedicineName().toUpperCase());
            holder.product_type.setText(medicine.getMedicineType().toUpperCase());
            holder.product_id.setText(medicine.getMedicineId());
            holder.manufacturer.setText(medicine.getManufacturer().toUpperCase());

            holder.quantity.setText(String.valueOf(medicine.getQuantity()));
            holder.pack_size.setText(medicine.getPackSize());
            holder.price.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(Double.parseDouble(medicine.getPrice())));
            holder.mrp.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(Double.parseDouble(medicine.getMrp())));
            holder.discount.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(medicine.getDiscount()));

            double total = Double.parseDouble(medicine.getPrice()) * medicine.getQuantity();
            holder.total.setText(context.getResources().getString(R.string.Rs) + " " + Constants.df.format(total));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount()
    {
        return mList.size();
    }


    class OrderDetailsViewHolder extends RecyclerView.ViewHolder
    {

        TextView medicine_name;

        TextView product_id;

        TextView product_type;

        TextView manufacturer;

        TextView discount;

        TextView quantity;

        TextView pack_size;

        TextView price;

        TextView mrp;

        TextView total;

        private OrderDetailsViewHolder(View itemView)
        {
            super(itemView);
          //  ButterKnife.bind(this, itemView);

             medicine_name =itemView.findViewById(R.id.medicineName);
             product_id=itemView.findViewById(R.id.productId);
           product_type=itemView.findViewById(R.id.productType);
            manufacturer=itemView.findViewById(R.id.manufacturer);
             discount=itemView.findViewById(R.id.discount);
            quantity=itemView.findViewById(R.id.quantity);
             pack_size=itemView.findViewById(R.id.packSize);
             price=itemView.findViewById(R.id.price);
             mrp=itemView.findViewById(R.id.mrp);
            total=itemView.findViewById(R.id.total);
        }
    }
}