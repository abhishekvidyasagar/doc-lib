package com.doconline.doconline.medicalremainders.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.database.DBAdapter;
import com.doconline.doconline.medicalremainders.MRAddRemainder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;

public class RemaindersAdapter extends BaseAdapter{
    Context context;
    int inflater_remainders;
    JsonArray jsa;
    Holder holder;

    public RemaindersAdapter(Context context, int inflater_remainders, JsonArray jsa) {
        this.context = context;
        this.inflater_remainders = inflater_remainders;
        this.jsa = jsa;


    }

    @Override
    public int getCount() {
        return jsa.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(inflater_remainders, parent, false);

            holder = new Holder();

            holder.ivMedicineImage = row.findViewById(R.id.medicineimage);
            holder.tvMedicineName = row.findViewById(R.id.medicinename);
            holder.tvDosageTime = row.findViewById(R.id.dosagetime);
            holder.tvMedicineStatusIcon = row.findViewById(R.id.status_icon);
            holder.rlRemainderObject = row.findViewById(R.id.remainderobject);
            holder.rlAddRemainder = row.findViewById(R.id.remainderplus);

        }

        final JsonObject ojo = jsa.get(position).getAsJsonObject();

        if (ojo.get("tablet_name").getAsString().equalsIgnoreCase("ADDD")){
            holder.rlAddRemainder.setVisibility(View.VISIBLE);
            holder.rlRemainderObject.setVisibility(View.GONE);
            holder.rlAddRemainder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, MRAddRemainder.class);
                    i.putExtra(Constants.FROM_SCREEN_KEY, Constants.FROM_SCREEN_KEY_CREATE);
                    context.startActivity(i);
                }
            });
        }else {
            if (ojo.has(DBAdapter.KEY_SKIP_TAKE_OBJECT)){
                JsonObject skipTakeObject = ojo.get(DBAdapter.KEY_SKIP_TAKE_OBJECT).getAsJsonObject();
                if (skipTakeObject.get(DBAdapter.KEY_TS_STATUS).getAsString().equals("skip")){
                    holder.tvMedicineStatusIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_error_red));
                }else if (skipTakeObject.get(DBAdapter.KEY_TS_STATUS).getAsString().equals("take")){
                    holder.tvMedicineStatusIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_check_green));
                }
            }else {
                holder.tvMedicineStatusIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_check_grey));
            }

            holder.rlAddRemainder.setVisibility(View.GONE);

            holder.tvMedicineName.setText("" + ojo.get("tablet_name").getAsString());

            if (ojo.get(DBAdapter.KEY_DOSAGE).getAsString().equalsIgnoreCase("")){
                holder.tvDosageTime.setText(""+ojo.get(DBAdapter.KEY_TIME).getAsString());
            }else {
                holder.tvDosageTime.setText(ojo.get(DBAdapter.KEY_DOSAGE).getAsString() + " , " + ojo.get(DBAdapter.KEY_TIME).getAsString());
            }

            try {
                File imgFile = new File("" + ojo.get("imagepath").getAsString());
                if (imgFile.exists()) {
                    Glide.with(context).
                            load(new File(imgFile.getAbsolutePath())).
                            placeholder(context.getResources().
                                    getDrawable(R.drawable.ic_pill)).
                            into(holder.ivMedicineImage);
                }
            } catch (Exception e) {
                Log.e("Exception", "" + e);
            }

            /*holder.rlRemainderObject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(ojo);
                    Intent intent = new Intent(context, RemainderDetails.class);
                    intent.putExtra(Constants.REMAINDER_DATA, jsonString);
                    intent.putExtra(Constants.USER_SELECTED_DATE, MRHome.userSelectedDate);
                    context.startActivity(intent);
                }
            });*/

        }

        return row;
    }

    public class Holder {
        ImageView ivMedicineImage;
        TextView tvMedicineName, tvDosageTime, tvMedicineStatusIcon;
        RelativeLayout rlRemainderObject, rlAddRemainder;
    }



}
