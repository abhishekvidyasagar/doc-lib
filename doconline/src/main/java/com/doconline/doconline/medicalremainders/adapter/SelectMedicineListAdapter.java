package com.doconline.doconline.medicalremainders.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doconline.doconline.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SelectMedicineListAdapter extends BaseAdapter {
    Context context; int inflater_select_medicine; JsonArray jsa;
    Holder holder;
    public SelectMedicineListAdapter(Context context, int inflater_select_medicine, JsonArray jsa) {
        this.context = context;
        this.inflater_select_medicine = inflater_select_medicine;
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
        if (row == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(inflater_select_medicine,parent,false);
            holder = new Holder();
            holder.tvMedicineName = row.findViewById(R.id.medicinename);
        }

        JsonObject ojo = jsa.get(position).getAsJsonObject();
        holder.tvMedicineName.setText(""+ojo.get("medicineName").getAsString());

        return row;
    }

    public class Holder{

        TextView tvMedicineName;
    }
}