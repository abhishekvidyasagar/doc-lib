package com.doconline.doconline.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.model.Patient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chiranjitbardhan on 29/01/18.
 */

public class SpinnerAdapter extends BaseAdapter
{
    private Context context;
    private List<Patient> mList;
    String fromScreen;

    public SpinnerAdapter(Context context, List<Patient> mList, int count, String fromScreen)
    {
        this.mList = mList;
        this.fromScreen = fromScreen;

        // If total family members less than 4 then add button(that's why added null in the list to show +members).
        if(count < 4)
        {
            this.mList.add(null);
        }

        /*if(mList.size() < 4)
        {
            this.mList.add(null);
        }*/

        this.context = context;
    }

    @Override
    public Object getItem(int pos)
    {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @NonNull
    public View getView(int position, View itemView, @NonNull ViewGroup parent)
    {
        return initView(position, itemView, parent);
    }


    private View initView(int position, View itemView, ViewGroup parent) {

        Log.i("initView", "" + position);
        Patient patient = mList.get(position);

        if (itemView == null)
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_dynamic, null /*parent, false*/);
        }

        CircleImageView thumbnail = itemView.findViewById(R.id.patient_image);
        TextView textView = itemView.findViewById(R.id.patient_name);

        if(position == (mList.size() - 1) && mList.get(position) == null)
        {
            textView.setText("Add New");
            thumbnail.setImageResource(R.drawable.ic_plus);
            return itemView;
        }


        else {
            textView.setText(Helper.toCamelCase(patient.getFullName()));
            try {
                if(!patient.getProfilePic().isEmpty()) {
                    ImageLoader.load(context, patient.getProfilePic(), thumbnail, R.drawable.ic_avatar, 50, 50);
                }
                else {
                    ImageLoader.loadLocal(context, R.drawable.ic_avatar, thumbnail, R.drawable.ic_avatar, 50, 50);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        }

        return itemView;
    }

    /*@Override
    public View getDropDownView(int position, View itemView, @NonNull ViewGroup parent)
    {
        return getView(position, itemView, parent);
    }*/

    @Override
    public int getCount() {
        return mList.size();
        /*if (fromScreen.equalsIgnoreCase("")){
            return mList.size();
        }
        return mList.size()-1;*/
    }

    public void setItems(int count)
    {
        if(count < 4)
        {
            this.mList.add(null);
        }

        notifyDataSetChanged();
    }
}