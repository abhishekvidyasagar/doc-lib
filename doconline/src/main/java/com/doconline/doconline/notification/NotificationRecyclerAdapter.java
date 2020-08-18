package com.doconline.doconline.notification;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.NotificationViewHolder>
{
    private Context context;
    private OnItemDeleted listener;
    private OnItemClickListener clickListener;
    private List<NotificationUtils> mNotification;
    private SimpleDateFormat sdf, formatter;


    NotificationRecyclerAdapter(Context context, OnItemDeleted listener, List<NotificationUtils> mNotification)
    {
        this.context = context;
        this.listener = listener;
        this.mNotification = mNotification;
        this.sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
        this.formatter = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY_HH_MM_A);
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_notification, viewGroup, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder vHolder, int i)
    {
        vHolder.bindData(mNotification.get(i));
        vHolder.delete_item.setTag(i);
    }

    @Override
    public int getItemCount()
    {
        return mNotification == null ? 0 : mNotification.size();
    }


    public void removeItem(int position)
    {
        // notify the custom_adapter_item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()

        mNotification.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(NotificationUtils item, int position)
    {
        mNotification.add(position, item);
        notifyItemInserted(position);
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView notification_title;

        TextView notification_body;

        TextView timestamp;

        ImageButton delete_item;

        RelativeLayout viewBackground;

        public RelativeLayout viewForeground;


        NotificationViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

             notification_title = itemView.findViewById(R.id.notification_title);
           notification_body= itemView.findViewById(R.id.notification_body);
             timestamp= itemView.findViewById(R.id.timestamp);
             delete_item= itemView.findViewById(R.id.delete_item);
            viewBackground= itemView.findViewById(R.id.view_background);
            viewForeground= itemView.findViewById(R.id.view_foreground);

            itemView.setOnClickListener(this);
            delete_item.setOnClickListener(onButtonClickListener);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                if (v.getId() == R.id.delete_item) {
                    listener.onItemDeleted(index, mNotification.get(index).notification_id);
                }
            }
        };

        private void bindData(NotificationUtils utils)
        {
            if(utils.read_at.isEmpty())
            {
                notification_title.setTypeface(null, Typeface.BOLD);
            }

            else
            {
                notification_title.setTypeface(null, Typeface.NORMAL);
            }

            try
            {
                Date value = sdf.parse(Helper.UTC_to_Local_TimeZone(utils.created_at));
                timestamp.setText(formatter.format(value));
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            int y = utils.type.lastIndexOf("\\") + 1;
            String type = utils.type.substring(y);

            if(type.equalsIgnoreCase(Constants.KEY_NOTIFICATION_TYPE_INCOMING_CALL))
            {
                String scheduled_at = Helper.UTC_to_Local_TimeZone(view_notification_details(utils));
                int date_diff = Helper.compare_date(scheduled_at, System.currentTimeMillis());

                try
                {
                    Date value = sdf.parse(scheduled_at);
                    scheduled_at = formatter.format(value);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                if(date_diff == 0)
                {
                    notification_title.setText("You have an Appointment Now");
                    notification_body.setText("You have an appointment with DocOnline on " + scheduled_at + " ");
                }

                else if(date_diff == 1)
                {
                    notification_title.setText("You have an Appointment");
                    notification_body.setText("You have an appointment with DocOnline on " + scheduled_at + " ");
                }

                else
                {
                    notification_title.setText("You had an Appointment");
                    notification_body.setText("You had an appointment with DocOnline on " + scheduled_at + " ");
                }
            }
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
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


    private String view_notification_details(NotificationUtils utils)
    {
        int y = utils.type.lastIndexOf("\\") + 1;
        String type = utils.type.substring(y);

        if(type.equalsIgnoreCase(Constants.KEY_NOTIFICATION_TYPE_INCOMING_CALL))
        {
            try
            {
                JSONObject json = new JSONObject(utils.body);
                return json.getString(Constants.KEY_SCHEDULED_AT);
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return "";
    }
}