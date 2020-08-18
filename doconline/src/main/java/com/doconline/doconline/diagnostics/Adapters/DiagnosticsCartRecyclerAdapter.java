package com.doconline.doconline.diagnostics.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.model.DiagnosticsCartItem;

/**
 * Created by admin on 2018-03-18.
 */

public class DiagnosticsCartRecyclerAdapter extends RecyclerView.Adapter<DiagnosticsCartRecyclerAdapter.CartItemViewHolder>  {


    private MyApplication mController;
    private Context context;

    public interface OnCartItemSelectedListener{

        void OnCartItemDeleteClicked(int position);
        void OnBookCartItem(int position);
    }

    private OnCartItemSelectedListener onCartItemSelectedListener;

    public interface OnRecyclerViewCartItemClickListener<DiagnosticsCartItem> {
        void onItemClick(View view, com.doconline.doconline.model.DiagnosticsCartItem model);
    }

    public DiagnosticsCartRecyclerAdapter(Context ctx, OnCartItemSelectedListener _listener) {
        this.context = ctx;
        this.mController = MyApplication.getInstance();
        this.onCartItemSelectedListener = _listener;
    }


    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_diagnostics_row_cart_item, parent, false);
        CartItemViewHolder holder = new CartItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CartItemViewHolder holder, final int position) {

        final DiagnosticsCartItem item = mController.getDiagnosticsCart().getCartItemsList().get(position);

        holder.textViewCartItemName.setText(item.getProductName());
        String productPrice = context.getResources().getString(R.string.Rupee) + " " + item.getProductPrice();
        holder.textViewCartItemPrice.setText(productPrice);
        holder.itemView.setTag(item);

        holder.btnBookCartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Book an appointment for this package!", Toast.LENGTH_SHORT).show();
                if(null != onCartItemSelectedListener)
                    onCartItemSelectedListener.OnBookCartItem(position);
            }
        });

        holder.btnDeleteCartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Delete this package from cart!", Toast.LENGTH_SHORT).show();
                if(null != onCartItemSelectedListener)
                    onCartItemSelectedListener.OnCartItemDeleteClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mController.getDiagnosticsCartItemListCount();
    }


    public class CartItemViewHolder extends RecyclerView.ViewHolder{

        ImageView    imgViewCartItem;
        TextView     textViewCartItemName;
        TextView     textViewCartItemPrice;
        Button       btnBookCartItem;
        Button       btnDeleteCartItem;

        public CartItemViewHolder(final View itemView) {
            super(itemView);

            imgViewCartItem = itemView.findViewById(R.id.imgView_cartItem);
            textViewCartItemName = itemView.findViewById(R.id.tv_cartitem_name);
            textViewCartItemPrice = itemView.findViewById(R.id.tv_cartitem_price);
            btnBookCartItem = itemView.findViewById(R.id.btn_cartitem_booknow);
            btnDeleteCartItem = itemView.findViewById(R.id.btn_cartitem_delete);
        }
    }
}
