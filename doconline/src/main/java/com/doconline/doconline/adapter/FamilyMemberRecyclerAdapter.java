package com.doconline.doconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnItemUpdated;
import com.doconline.doconline.helper.OnLinkAction;
import com.doconline.doconline.model.FamilyMember;

import de.hdodenhof.circleimageview.CircleImageView;


public class FamilyMemberRecyclerAdapter extends RecyclerView.Adapter<FamilyMemberRecyclerAdapter.ViewHolder>
{
    private OnItemClickListener clickListener;
    private Context context;
    private OnItemUpdated u_listener;
    private OnItemDeleted d_listener;
    private OnLinkAction l_listener;
    private MyApplication mController;


    public FamilyMemberRecyclerAdapter(Context context, OnItemUpdated u_listener, OnItemDeleted d_listener, OnLinkAction l_listener)
    {
        super();

        this.context = context;
        this.u_listener = u_listener;
        this.d_listener = d_listener;
        this.l_listener = l_listener;
        this.mController = MyApplication.getInstance();
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_family_member, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
    {
        viewHolder.bindData(mController.getFamilyMember(i));
        viewHolder.ib_delete.setTag(i);
        viewHolder.ib_edit.setTag(i);
        viewHolder.btn_send_link.setTag(i);
    }


    @Override
    public int getItemCount()
    {
        return mController.getFamilyMemberCount();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView tv_name;

        TextView tv_dob;

        TextView tv_email;

        TextView tv_mobile_no;

        TextView tv_gender;

        TextView tv_age;

        ImageButton ib_delete;

        ImageButton ib_edit;

        CircleImageView thumbnail;

        ImageView iv_divider;

        Button btn_send_link;


        Button btn_activated;

        public ViewHolder(View itemView)
        {
            super(itemView);
            //ButterKnife.bind(this, itemView);

             tv_name = itemView.findViewById(R.id.tvName);
            tv_dob= itemView.findViewById(R.id.tvDOB);
            tv_email= itemView.findViewById(R.id.tvEmail);
             tv_mobile_no= itemView.findViewById(R.id.tvMobileNo);
             tv_gender= itemView.findViewById(R.id.tvGender);
             tv_age= itemView.findViewById(R.id.tvAge);
             ib_delete= itemView.findViewById(R.id.ib_delete);
             ib_edit= itemView.findViewById(R.id.ib_edit);
             thumbnail= itemView.findViewById(R.id.thumbnail);
             iv_divider= itemView.findViewById(R.id.divider);
            btn_send_link= itemView.findViewById(R.id.btnSendLink);

            btn_activated= itemView.findViewById(R.id.btnActivated);

            itemView.setOnClickListener(this);
            ib_delete.setOnClickListener(onButtonClickListener);
            ib_edit.setOnClickListener(onButtonClickListener);
            btn_send_link.setOnClickListener(onButtonClickListener);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                int id = v.getId();
                if (id == R.id.ib_edit) {
                    u_listener.onItemUpdated(index, mController.getFamilyMember(index).getMemberId());
                } else if (id == R.id.ib_delete) {
                    d_listener.onItemDeleted(index, mController.getFamilyMember(index).getMemberId());
                } else if (id == R.id.btnSendLink) {
                    l_listener.onLinkClick(index, mController.getFamilyMember(index).getMemberId());
                }
            }
        };


        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }


        private void bindData(FamilyMember member)
        {
            tv_name.setText(Helper.toCamelCase(member.getFullName().replaceAll("^\\s+", "").replaceAll("\\s+$", "")));

            if(!member.getEmail().isEmpty())
            {
                tv_email.setText(member.getEmail());
            }

            else
            {
                tv_email.setText("N/A");
            }

            if(!member.getPhoneNo().isEmpty())
            {
                tv_mobile_no.setVisibility(View.VISIBLE);
                tv_mobile_no.setText(String.valueOf(member.getPhoneNo()));
            }

            else
            {
                tv_mobile_no.setVisibility(View.GONE);
            }

            tv_dob.setText(Helper.dateFormat(member.getDateOfBirth()));

            if(!member.getGender().isEmpty())
            {
                tv_gender.setText(member.getGender() + ")");
            }

            else
            {
                tv_gender.setText(")");
            }

            tv_age.setText(" (Age " + Helper.getYearMonthDaysDiff(member.getDateOfBirth()) + ", ");

            if(member.getIsActive() == 1)
            {
                ib_edit.setVisibility(View.GONE);
                iv_divider.setVisibility(View.GONE);
            }

            else
            {
                ib_edit.setVisibility(View.VISIBLE);
                iv_divider.setVisibility(View.VISIBLE);
            }

            if(member.getIsActive() == 0 && Helper.getYearDiff(member.getDateOfBirth()) >=16
                    && (!member.getEmail().isEmpty() || !member.getPhoneNo().isEmpty()))
            {
                btn_send_link.setVisibility(View.VISIBLE);
                btn_activated.setVisibility(View.GONE);
            }

            else
            {
                btn_send_link.setVisibility(View.GONE);
                btn_activated.setVisibility(View.VISIBLE);
            }

            this.load_avatar(member.getProfilePic());
        }


        private void load_avatar(final String avatar_url)
        {
            try
            {
                if(!avatar_url.isEmpty())
                {
                    ImageLoader.loadThumbnail(context, avatar_url, thumbnail, R.drawable.ic_avatar, 120, 120);
                }
            }

            catch (Exception e)
            {
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