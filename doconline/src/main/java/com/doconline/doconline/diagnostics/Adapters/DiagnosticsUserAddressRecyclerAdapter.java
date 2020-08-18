package com.doconline.doconline.diagnostics.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.model.DiagnosticsUserAddress;

/**
 * Created by A on 3/12/2018.
 */

public class DiagnosticsUserAddressRecyclerAdapter extends RecyclerView.Adapter<DiagnosticsUserAddressRecyclerAdapter.AddressItemViewHolder> {

    private int lastSelectedPosition = 0;

    private OnItemSetDefaultListener    s_listener;
    private OnItemDeletedListener       d_listener;
    private OnItemEditListener          e_listener;

    private MyApplication mController;

    public interface OnItemSetDefaultListener {
        void OnItemSetDefault(int pos);
    }

    public interface OnItemDeletedListener {
        void OnItemDeleted(int position);
    }

    public interface OnItemEditListener {
        void OnItemEdited(int position);
    }

    public DiagnosticsUserAddressRecyclerAdapter(OnItemSetDefaultListener setDefaultListener, OnItemDeletedListener deleteListener, OnItemEditListener editListener) {

        s_listener = setDefaultListener;
        d_listener = deleteListener;
        e_listener = editListener;

        this.mController = MyApplication.getInstance();
    }


    @Override
    public AddressItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_diagnostics_row_user_address, parent, false);
        AddressItemViewHolder holder = new AddressItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AddressItemViewHolder holder, int position) {

        //Log.d("onBindViewHoler ", mController.getDiagnosticsUserAddressCount() + "");

        DiagnosticsUserAddress item = mController.getUserAddress(position);
        holder.txtView_user_address_title.setText(item.getStrAddressTitle());
        holder.txtView_user_address_addresslane.setText(item.getStrAddressLane());
        holder.txtView_user_address_landmark.setText(item.getStrLandmark());
        holder.txtView_user_address_city.setText(item.getStrCity());
        holder.txtView_user_address_state.setText(item.getStrState());
        holder.txtView_user_address_pincode.setText(item.getiPincode().toString());
        holder.checkBox_isDefaultAddress.setOnCheckedChangeListener(null);
        holder.checkBox_isDefaultAddress.setChecked(item.isDefaultAddress());
        if (item.isDefaultAddress())
        {
            lastSelectedPosition = position;
        }

//        if(lastSelectedPosition == -1)
//            holder.checkBox_isDefaultAddress.setChecked(item.isbIsDefaultAddress());
//        else
//            holder.checkBox_isDefaultAddress.setChecked(lastSelectedPosition == position);
        //

        //Picasso.with(holder.image.getContext()).cancelRequest(holder.image);
        //Picasso.with(holder.image.getContext()).load(item.getImage()).into(holder.image);
        holder.itemView.setTag(item);

//        mLastPosition =position;
    }

    @Override
    public int getItemCount() {
        return mController.getDiagnosticsUserAddressCount();
    }


//    public void add(DiagnosticsUserAddress item, int position) {
//        list_UserAddress.add(position, item);
//        notifyItemInserted(position);
//    }

//    public void remove(DiagnosticsUserAddress item) {
//        int position = list_UserAddress.indexOf(item);
//        list_UserAddress.remove(position);
//        notifyItemRemoved(position);
//    }

    public class AddressItemViewHolder extends RecyclerView.ViewHolder {

        public TextView txtView_user_address_title;
        public TextView txtView_user_address_addresslane;
        public TextView txtView_user_address_landmark;
        public TextView txtView_user_address_city;
        public TextView txtView_user_address_state;
        public TextView txtView_user_address_pincode;

        public AppCompatCheckBox checkBox_isDefaultAddress;

        private Button btn_edit_user_address, btn_delete_user_address;

        public AddressItemViewHolder(final View itemView) {
            super(itemView);

            txtView_user_address_title          = itemView.findViewById(R.id.tv_user_address_title);
            txtView_user_address_addresslane    = itemView.findViewById(R.id.tv_user_address_addresslane1);
            txtView_user_address_landmark       = itemView.findViewById(R.id.tv_user_address_landmark);
            txtView_user_address_city           = itemView.findViewById(R.id.tv_user_address_city);
            txtView_user_address_state          = itemView.findViewById(R.id.tv_user_address_state);
            txtView_user_address_pincode        = itemView.findViewById(R.id.tv_user_address_pincode);

            checkBox_isDefaultAddress           = itemView.findViewById(R.id.appCompatCheckBox_isDefaultAddress);

            checkBox_isDefaultAddress.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(false == checkBox_isDefaultAddress.isChecked()) {
                        Toast.makeText(itemView.getContext(), "Please select another address!", Toast.LENGTH_SHORT).show();
                        mController.getUserAddress(lastSelectedPosition).setbIsDefaultAddress(true);
                    }
                    else {
                        int currentClickedPosition = getAdapterPosition();
                        //if(lastSelectedPosition !=  currentClickedPosition) {
                        lastSelectedPosition = currentClickedPosition;
                        // mController.getUserAddress(currentClickedPosition).setbIsDefaultAddress(true);
                        for (DiagnosticsUserAddress address : mController.diagnosticsUserAddressList) {
                            if (address.getiAddressID() == mController.getUserAddress(currentClickedPosition).getiAddressID()) {
                                address.setbIsDefaultAddress(true);
                            } else {
                                address.setbIsDefaultAddress(false);
                            }
                        }

                        if( s_listener!= null)
                            s_listener.OnItemSetDefault(currentClickedPosition);
                    }
                    notifyDataSetChanged();


                }
            });

            btn_edit_user_address               = itemView.findViewById(R.id.btn_edit_user_address);
            btn_edit_user_address.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if( e_listener!= null)
                        e_listener.OnItemEdited(pos);
                }
            });

            btn_delete_user_address             = itemView.findViewById(R.id.btn_delete_user_address);
            btn_delete_user_address.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(d_listener != null)
                        d_listener.OnItemDeleted(pos);
                    //Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(pos), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
