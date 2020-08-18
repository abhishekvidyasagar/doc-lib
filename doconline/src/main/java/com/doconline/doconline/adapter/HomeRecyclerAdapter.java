package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.model.DocOnlineMenu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.doconline.doconline.app.Constants.CALL_STATUS_ACTIVE;
import static com.doconline.doconline.app.Constants.DD_MMM_EEE_HH_MM_A;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;

/*import butterknife.BindView;
import butterknife.ButterKnife;*/


public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private OnItemClickListener clickListener;
    private List<DocOnlineMenu> mMenu;
    private Appointment mAppointment = new Appointment();
    private Context mContext;
    private int lastPosition = -1;

    // All layouts are TYPE_MENU but if upcoming appointment is there then layout type is TYPE_TILE.
    private final int VIEW_TYPE_MENU = 0;
    private final int VIEW_TYPE_TILE = 1;

    public HomeRecyclerAdapter(Context mContext, Appointment mAppointment)
    {
        super();
        this.mContext = mContext;
        this.mMenu = DocOnlineMenu.getHomeMenuList();
        this.mAppointment = mAppointment;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        if(VIEW_TYPE_TILE == i)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_appointment_tile, viewGroup, false);
            return new TileViewHolder(v);
        }

        if(VIEW_TYPE_MENU == i)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_home, viewGroup, false);
            return new ViewHolder(v);
        }

        return null;
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
            Animation animation = AnimationUtils.loadAnimation(MyApplication.getInstance(), R.anim.slide_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i)
    {
        if(viewHolder instanceof  ViewHolder)
        {
            ViewHolder holder = (ViewHolder) viewHolder;
            holder.bindData(mMenu.get(i));
            setAnimation(holder.card_main, i);
        }

        if(viewHolder instanceof TileViewHolder)
        {
            TileViewHolder holder = (TileViewHolder) viewHolder;

            if(this.mAppointment.getAppointmentID() != 0 && this.mAppointment.getAppointmentStatus() == CALL_STATUS_ACTIVE)
            {
                holder.bindData(mMenu.get(i), this.mAppointment);
            }

            setAnimation(holder.card_main, i);
        }
    }

    @Override
    public int getItemCount()
    {
        return this.mMenu == null ? 0 : this.mMenu.size();
    }


    @Override
    public int getItemViewType(int position)
    {
        if(position == 1 && this.mAppointment.getAppointmentID() != 0
                && this.mAppointment.getAppointmentStatus() == CALL_STATUS_ACTIVE)
        {
            return VIEW_TYPE_TILE;
        }

        return VIEW_TYPE_MENU;
    }


    class TileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tvTitle;
        TextView tvScheduledAt;
        TextView tvDoctorName;
        TextView tvSpecialization;
        ImageView tvIcon;
        CardView card_main;
        View bottom_gradient;

        private TileViewHolder(View itemView)
        {
            super(itemView);
         //   ButterKnife.bind(this, itemView);

            tvTitle =  itemView.findViewById(R.id.menu_title);
            tvScheduledAt = itemView.findViewById(R.id.scheduled_at);
            tvDoctorName = itemView.findViewById(R.id.doctor_name);
            tvSpecialization = itemView.findViewById(R.id.specialization);
            tvIcon = itemView.findViewById(R.id.menu_icon);
            card_main = itemView.findViewById(R.id.card_main);
            bottom_gradient = itemView.findViewById(R.id.bottom_gradient);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }

        private void bindData(DocOnlineMenu menu, Appointment appointment)
        {
            tvTitle.setText(menu.getTitle());
            tvDoctorName.setText(Helper.toCamelCase(appointment.getDoctor().getFullName()));
            tvSpecialization.setText(Helper.toCamelCase(appointment.getDoctor().getSpecialization()));
            tvIcon.setImageResource(mContext.getResources().getIdentifier(menu.getIcon(), "drawable", mContext.getPackageName()));

            if (!appointment.getScheduledAt().isEmpty()) {
                String date = Helper.UTC_to_Local_TimeZone(appointment.getScheduledAt());

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
                    Date value = sdf.parse(date);
                    sdf = new SimpleDateFormat(DD_MMM_EEE_HH_MM_A);
                    tvScheduledAt.setText(sdf.format(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try
            {
                AnimationDrawable anim = (AnimationDrawable) bottom_gradient.getBackground();
                anim.setEnterFadeDuration(3000);
                anim.setExitFadeDuration(1000);
                anim.start();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tvTitle;
        TextView tvDescription;
        ImageView tvIcon;
        CardView card_main;


        private ViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            tvTitle = itemView.findViewById(R.id.menu_title);
            tvDescription = itemView.findViewById(R.id.menu_description);
            tvIcon = itemView.findViewById(R.id.menu_icon);
            card_main = itemView.findViewById(R.id.card_main);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }


        private void bindData(DocOnlineMenu menu)
        {
            try{
                tvTitle.setText(menu.getTitle());
                tvDescription.setText(menu.getDescription());
                tvIcon.setImageResource(mContext.getResources().getIdentifier(menu.getIcon(), "drawable", mContext.getPackageName()));
            }catch (Exception e){
                e.printStackTrace();
            }

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
}