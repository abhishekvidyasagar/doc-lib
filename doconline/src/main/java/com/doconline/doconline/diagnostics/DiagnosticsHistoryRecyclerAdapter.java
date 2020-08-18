package com.doconline.doconline.diagnostics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.model.DiagnosticsHistoryItem;
import com.google.gson.Gson;

/**
 * Created by Developer on 3/28/2018.
 */

public class DiagnosticsHistoryRecyclerAdapter extends RecyclerView.Adapter<DiagnosticsHistoryRecyclerAdapter.DiagnosticsHistoryViewHolder> {

    private MyApplication mController;

    private Context context;

    Boolean isUpcoming = true;

    public interface OnHistoryItemSelectedListener {
        void OnDiagnosticsHistoryItemSelected(int postition);
    }

    private OnHistoryItemSelectedListener historySelectedListener;

    public DiagnosticsHistoryRecyclerAdapter(Context context, OnHistoryItemSelectedListener _listener) {
        this.context = context;
        this.historySelectedListener = _listener;

        this.mController = MyApplication.getInstance();
    }

    @Override
    public DiagnosticsHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_diagnostics_row_history, parent, false);
        DiagnosticsHistoryViewHolder holder = new DiagnosticsHistoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DiagnosticsHistoryViewHolder holder, int position) {

        DiagnosticsHistoryItem item = null;
        if (isUpcoming == true)
            item = mController.getDiagnosticsUpcomingHistoryDetails(position);
        else
            item = mController.getDiagnosticsPreviousHistoryDetails(position);

        String imageURL = item.getPartnerImgURL();

//        if (!imageURL.isEmpty())
//            holder.imgViewProductItem.setImageURI(Uri.parse(imageURL));

        holder.textViewProductItemName.setText(item.getProductName());
        holder.textViewAppointmentID.setText(item.getAppointmentID());
        holder.textViewAppointmentDate.setText(item.getAppointmentDate());
    }

    @Override
    public int getItemCount() {

        if (isUpcoming == true)
            return mController.getDiagnosticsUpcomingHistoryCount();
        else
            return mController.getDiagnosticsPreviousHistoryCount();
    }

    public class DiagnosticsHistoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imgViewProductItem;
        TextView textViewAppointmentID;
        TextView textViewProductItemName;
        TextView textViewAppointmentDate;

        ConstraintLayout orderDetailsLayout;


        public DiagnosticsHistoryViewHolder(View itemView) {
            super(itemView);

            imgViewProductItem   = itemView.findViewById(R.id.imgView_Package_OfferedBy);
            textViewProductItemName   = itemView.findViewById(R.id.tv_product_name);
            textViewAppointmentID   = itemView.findViewById(R.id.tv_appointment_id);
            textViewAppointmentDate   = itemView.findViewById(R.id.tv_appointment_date);

            orderDetailsLayout = itemView.findViewById(R.id.details_layout);
            orderDetailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUpcoming){
                        //Toast.makeText(context, "this is upcomming cannot check status", Toast.LENGTH_SHORT).show();
                    }else {
                        DiagnosticsHistoryItem diagnosticItems = mController.getDiagnosticsPreviousHistoryDetails(getAdapterPosition());
                        Gson gson = new Gson();
                        String diagnosticStringItem = gson.toJson(diagnosticItems);

                        Intent i = new Intent(context, DiagnosticsReportsStatusActivity.class);
                        i.putExtra(Constants.DIAGNOSTIC_ITEM, diagnosticStringItem);
                        context.startActivity(i);
                    }

                }
            });
        }
    }
}
