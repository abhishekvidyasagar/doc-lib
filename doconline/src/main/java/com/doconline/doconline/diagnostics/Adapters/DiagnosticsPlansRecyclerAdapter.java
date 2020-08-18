package com.doconline.doconline.diagnostics.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.model.DiagnosticsItem;
import com.github.ybq.android.spinkit.SpinKitView;

/**
 * Created by admin on 2018-03-19.
 */

public class DiagnosticsPlansRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MyApplication mController;

    private OnItemClickListener clickListener;
    //private List<Appointment> uList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public OnLoadMoreListener mOnLoadMoreListener;

    public interface OnPlanSelectedListener  {
        void OnAddToCartClicked(int position);
        void OnCheckDetailsClicked(int postition);
    }

    private OnPlanSelectedListener planSelectedListener;

    private Context  context;

    public DiagnosticsPlansRecyclerAdapter(Context context, OnPlanSelectedListener _listener) {
        this.context = context;
        this.planSelectedListener = _listener;

        this.mController = MyApplication.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_diagnostics_row_product_item, parent, false);
            return new DiagnosticsPlansViewHolder(view);
        }
        else if(viewType == VIEW_TYPE_LOADING){

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new DiagnosticsPlansRecyclerAdapter.LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof DiagnosticsPlansViewHolder) {
            DiagnosticsItem item = mController.getPackageDetails(position);
            String imageURL = item.getImgURL();

//        if (!imageURL.isEmpty())
//            holder.imgViewProductItem.setImageURI(Uri.parse(imageURL));
            DiagnosticsPlansViewHolder viewHolder = (DiagnosticsPlansViewHolder)holder;

            Log.d("AAA",""+item.getPackagetype());
            if (item.getPackagetype().equalsIgnoreCase("btob")){
                viewHolder.btob.setVisibility(View.VISIBLE);
                viewHolder.general.setVisibility(View.GONE);
                viewHolder.testname_btob.setText(item.getStrPackage_name());
                viewHolder.btobcompany.setText(item.getCompany());
                viewHolder.validtill.setText("Valid Till :"+ item.getDateExpiresOn());
                Glide.with(context)
                        .load(item.getStrpartnerImgUrl()) // thumbnail url goes here
                        .dontAnimate()
                        .into(viewHolder.partner_img);

            }else {
                viewHolder.btob.setVisibility(View.GONE);
                viewHolder.general.setVisibility(View.VISIBLE);
            }

            viewHolder.textViewProductItemName.setText(item.getStrPackage_name());
            viewHolder.textViewProductItemPrice.setText(context.getResources().getString(R.string.Rupee) + " " + item.getPrice());

            if (item.getiItemsInCart() > 0 || item.isAddedToCart()) {
                viewHolder.btn_AddToCart.setBackgroundResource(R.drawable.shopping_cart_green);
            } else
                viewHolder.btn_AddToCart.setBackgroundResource(R.drawable.shopping_cart_white);

            viewHolder.btn_AddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    planSelectedListener.OnAddToCartClicked(position);
                }
            });

            viewHolder.btnProductItemCheckDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    planSelectedListener.OnCheckDetailsClicked(position);
                }
            });

            holder.itemView.setTag(item);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    planSelectedListener.OnCheckDetailsClicked(position);
                }
            });
        }
        else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mController.getDiagnosticsPackagesCount();
    }

    @Override
    public int getItemViewType(int position)
    {
        return mController.getPackageDetails(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder
    {
        private SpinKitView progressBar;

        private LoadingViewHolder(View itemView)
        {
            super(itemView);
            progressBar = itemView.findViewById(R.id.spin_kit);
        }
    }

    public class DiagnosticsPlansViewHolder extends RecyclerView.ViewHolder {

        ImageView imgViewProductItem;
        TextView textViewProductItemName;
        TextView textViewProductItemPrice;
        TextView testname_btob,validtill,btobcompany;
        Button btn_AddToCart;
        Button btnProductItemCheckDetails;
        ImageView partner_img;

        CardView general,btob;

        public DiagnosticsPlansViewHolder(View itemView) {
            super(itemView);

            general = itemView.findViewById(R.id.general);
            btob = itemView.findViewById(R.id.btob);

            imgViewProductItem   = itemView.findViewById(R.id.imgView_productItem);
            textViewProductItemName   = itemView.findViewById(R.id.tv_prodcutitem_name);
            textViewProductItemPrice   = itemView.findViewById(R.id.tv_productitem_price);
            btn_AddToCart   = itemView.findViewById(R.id.btn_addToCart);
            btnProductItemCheckDetails   = itemView.findViewById(R.id.btn_product_checkdetails);
            partner_img = itemView.findViewById(R.id.partner_img);

            /*btob*/
            testname_btob   = itemView.findViewById(R.id.testname_btob);
            validtill   = itemView.findViewById(R.id.validtill);
            btobcompany   = itemView.findViewById(R.id.btobcompany);

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


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}
