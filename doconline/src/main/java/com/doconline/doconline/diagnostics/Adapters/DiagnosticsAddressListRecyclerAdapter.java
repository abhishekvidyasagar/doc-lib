package com.doconline.doconline.diagnostics.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.model.DiagnosticsUserAddress;

public class DiagnosticsAddressListRecyclerAdapter extends RecyclerView.Adapter<DiagnosticsAddressListRecyclerAdapter.DiagnosticsAddressViewHolder> {

    //private int lastSelectedPosition = -1;
    private int lastSelectedPosition = 0;
    private MyApplication mController;

    private OnItemClickListener clickListener;
    //private List<Appointment> uList;

    private Context context;

    public DiagnosticsAddressListRecyclerAdapter(Context context) {
        this.context = context;
        this.mController = MyApplication.getInstance();
    }

    @Override
    public DiagnosticsAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_diagnostics_spinner_tv, parent, false);
            return new DiagnosticsAddressViewHolder(view);

    }

    @Override
    public void onBindViewHolder(DiagnosticsAddressViewHolder holder, int position) {

        DiagnosticsUserAddress item = mController.getUserAddress(position);
        holder.txtView_user_address_title.setText(item.getStrAddressTitle());
        holder.btn_radio.setChecked(false);
        if (item.isDefaultAddress())
        {
            lastSelectedPosition = position;
            holder.btn_radio.setChecked(true);
        }

    }

    @Override
    public int getItemCount() {
        return mController.getDiagnosticsUserAddressCount();
    }



    public class DiagnosticsAddressViewHolder extends RecyclerView.ViewHolder {

        TextView txtView_user_address_title;
        RadioButton btn_radio;



        public DiagnosticsAddressViewHolder(View itemView) {
            super(itemView);

            txtView_user_address_title          = itemView.findViewById(R.id.textaddress);
            btn_radio = itemView.findViewById(R.id.radiobutton);
            btn_radio.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    if(false == btn_radio.isChecked()) {

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
                        if (clickListener != null)
                            clickListener.onItemClick(currentClickedPosition);
                    }
                    notifyDataSetChanged();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (btn_radio.isChecked()) {
                        btn_radio.setChecked(false);
                        mController.getUserAddress(lastSelectedPosition).setbIsDefaultAddress(true);
                    }
                    else
                    {
                        btn_radio.setChecked(true);
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
                        if (clickListener != null)
                            clickListener.onItemClick(currentClickedPosition);
                    }
                    notifyDataSetChanged();
                }
            });

        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }

}
