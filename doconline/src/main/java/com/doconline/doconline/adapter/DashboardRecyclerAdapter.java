package com.doconline.doconline.adapter;

import android.content.Context;
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
import com.doconline.doconline.model.DocOnlineMenu;

import java.util.List;


public class DashboardRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private OnItemClickListener clickListener;
    private List<DocOnlineMenu> mMenu;
    private Context mContext;
    private int lastPosition = -1;

    public DashboardRecyclerAdapter(Context mContext)
    {
        super();
        this.mContext = mContext;
        this.mMenu = DocOnlineMenu.getDashboardMenuList();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_dashboard, viewGroup, false);
        return new ViewHolder(v);
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
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.bindData(mMenu.get(i));
        setAnimation(holder.card_main, i);
    }

    @Override
    public int getItemCount()
    {
        return this.mMenu == null ? 0 : this.mMenu.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tvTitle;

        TextView tvDescription;

        ImageView tvIcon;

        CardView card_main;

        View view_enable;

        private ViewHolder(View itemView)
        {
            super(itemView);
         //   ButterKnife.bind(this, itemView);

           tvTitle = itemView.findViewById(R.id.menu_title);
            tvDescription = itemView.findViewById(R.id.menu_description);
             tvIcon = itemView.findViewById(R.id.menu_icon);
            card_main = itemView.findViewById(R.id.card_main);
            view_enable = itemView.findViewById(R.id.view_enable);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }


        private void bindData(DocOnlineMenu menu)
        {
            if(menu.isEnable())
            {
                view_enable.setVisibility(View.GONE);
            }

            else
            {
                view_enable.setVisibility(View.VISIBLE);
            }

            tvTitle.setText(menu.getTitle());
            tvDescription.setText(menu.getDescription());
            tvIcon.setImageResource(mContext.getResources().getIdentifier(menu.getIcon(), "drawable", mContext.getPackageName()));
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