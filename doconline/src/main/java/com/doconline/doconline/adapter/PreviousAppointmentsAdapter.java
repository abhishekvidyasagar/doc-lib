package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.doconline.doconline.model.Appointment;
import com.github.ybq.android.spinkit.SpinKitView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.doconline.doconline.app.Constants.DD;
import static com.doconline.doconline.app.Constants.HH_MM_A;
import static com.doconline.doconline.app.Constants.MMM;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;

public class PreviousAppointmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    //private PreviousAppointmentsAdapter.OnItemClickListener clickListener;
    private List<Appointment> uList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public OnLoadMoreListener mOnLoadMoreListener;

    private int lastPosition = -1;

    private SimpleDateFormat sdf;
    private SimpleDateFormat sdfMonth;
    private SimpleDateFormat sdfDate;
    private SimpleDateFormat sdfTime;

    public static int lastCheckedPosition = -1;
    public static int followupid = -1;
    public PreviousAppointmentsAdapter(Context context, List<Appointment> uList)
    {
        this.context = context;
        this.uList = uList;
        this.sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
        this.sdfMonth = new SimpleDateFormat(MMM);
        this.sdfDate = new SimpleDateFormat(DD);
        this.sdfTime = new SimpleDateFormat(HH_MM_A);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        if (i == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_previous_appointments, viewGroup, false);
            return new PreviousAppointmentsAdapter.ItemViewHolder(view);
        }

        else if (i == VIEW_TYPE_LOADING)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_item, viewGroup, false);
            return new PreviousAppointmentsAdapter.LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, final int i)
    {
        if(vHolder instanceof PreviousAppointmentsAdapter.ItemViewHolder)
        {
            final PreviousAppointmentsAdapter.ItemViewHolder holder = (PreviousAppointmentsAdapter.ItemViewHolder) vHolder;

            holder.patient_image.setTag(i);
            holder.bindData(uList.get(i), i);

            holder.select_rb.setChecked(i == lastCheckedPosition);

            setAnimation(holder.card_main, i);

        }

        else if (vHolder instanceof AppointmentHistoryRecyclerAdapter.LoadingViewHolder)
        {
            AppointmentHistoryRecyclerAdapter.LoadingViewHolder loadingViewHolder = (AppointmentHistoryRecyclerAdapter.LoadingViewHolder) vHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        /**
         * If the bound view wasn't previously displayed on screen, it's animated
         */
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount()
    {
        return uList == null ? 0 : uList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return uList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder
    {

        SpinKitView progressBar;

        private LoadingViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            progressBar = itemView.findViewById(R.id.spin_kit);
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder
    {

        TextView date;

        TextView month;

        TextView time;

        TextView doctor_name;

        TextView patient_name;

        TextView patient_gender;

        TextView patient_age;

        TextView specialization;

        CircleImageView patient_image;

        CardView card_main;


        LinearLayout layout;


        RadioButton select_rb;

        private ItemViewHolder(final View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

             date = itemView.findViewById(R.id.date);
             month= itemView.findViewById(R.id.month);
            time= itemView.findViewById(R.id.time);
             doctor_name= itemView.findViewById(R.id.doctor_name);
             patient_name= itemView.findViewById(R.id.patient_name);
             patient_gender= itemView.findViewById(R.id.patient_gender);
            patient_age= itemView.findViewById(R.id.patient_age);
             specialization= itemView.findViewById(R.id.specialization);
            patient_image= itemView.findViewById(R.id.patient_image);
             card_main= itemView.findViewById(R.id.card_main);

             layout= itemView.findViewById(R.id.layout);

             select_rb= itemView.findViewById(R.id.select_rb);

            //itemView.setOnClickListener(this);

            select_rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastCheckedPosition = getAdapterPosition();
                    followupid = uList.get(getAdapterPosition()).getAppointmentID();
                    notifyDataSetChanged();
                }
            });


        }


        /*@Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }*/

        private void bindData(final Appointment ts, final int position)
        {
            String scheduled_time = Helper.UTC_to_Local_TimeZone(ts.getScheduledAt());

            try
            {
                Date value = sdf.parse(scheduled_time);

                time.setText(sdfTime.format(value));
                date.setText(sdfDate.format(value));
                month.setText(sdfMonth.format(value).toUpperCase());
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            patient_name.setText(Helper.toCamelCase(ts.getPatient().getFirstName()));

            if(ts.getPatient().getGender().length() > 0)
            {
                if(!ts.getPatient().getDateOfBirth().isEmpty())
                {
                    patient_gender.setText(ts.getPatient().getGender().substring(0, 1) + ", ");
                }

                else
                {
                    patient_gender.setText(ts.getPatient().getGender().substring(0, 1));
                }
            }

            else
            {
                patient_gender.setText(String.valueOf(ts.getPatient().getGender()));
            }

            if(!ts.getPatient().getDateOfBirth().isEmpty())
            {
                patient_age.setText(String.valueOf(Helper.getYearDiff(ts.getPatient().getDateOfBirth())));
            }

            else
            {
                patient_age.setText(ts.getPatient().getDateOfBirth());
            }

            doctor_name.setText(Helper.toCamelCase(ts.getDoctor().getFullName()));
            specialization.setText(ts.getDoctor().getSpecialization());

            load_avatar(ts.getPatient().getProfilePic());


        }


        private void load_avatar(final String avatar_url)
        {
            try
            {
                if (!avatar_url.isEmpty())
                {
                    ImageLoader.load(context, avatar_url, patient_image, R.drawable.ic_avatar, 60, 60);
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    /*public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }*/


    /*public void SetOnItemClickListener(final PreviousAppointmentsAdapter.OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }*/


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}