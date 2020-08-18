package com.doconline.doconline.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.FileViewerActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.ChatMessage;
import com.doconline.doconline.utils.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.doconline.doconline.app.Constants.HH_MM_A;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.model.ChatMessage.TYPE_MESSAGE_IN;


public class MessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private OnItemClickListener clickListener;

    private List<ChatMessage> mMessages;
    private OnTaskCompleted listener;

    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf1;


    public MessageRecyclerAdapter(Context context, OnTaskCompleted listener, List<ChatMessage> mMessages)
    {
        this.context = context;
        this.listener = listener;
        this.mMessages = mMessages;

        this.sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
        this.sdf1 = new SimpleDateFormat(HH_MM_A);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        switch (i)
        {
            case TYPE_MESSAGE_IN:

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_chat_message_text_incoming, viewGroup, false);
                return new IncomingMessageViewHolder(view);

            case ChatMessage.TYPE_MESSAGE_OUT:

                View view3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_chat_message_text_outgoing, viewGroup, false);
                return new OutgoingMessageViewHolder(view3);
        }

        return null;
    }


    @Override
    public int getItemViewType(int position)
    {
        return mMessages.get(position).getType();
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
    {
        ChatMessage message = mMessages.get(i);

        if(holder instanceof OutgoingMessageViewHolder)
        {
            OutgoingMessageViewHolder mViewHolder = (OutgoingMessageViewHolder) holder;

            mViewHolder.cdn_photo.setTag(i);

            mViewHolder.bindData(message);
        }

        if(holder instanceof IncomingMessageViewHolder)
        {
            IncomingMessageViewHolder mViewHolder = (IncomingMessageViewHolder) holder;

            mViewHolder.iv_doctor.setTag(i);

            mViewHolder.bindData(message);

            Log.v("getImageUrl",""+message.getDoctorAvatarURL());
        }
    }


    @Override
    public int getItemCount()
    {
        return mMessages == null ? 0 : mMessages.size();
    }


    class OutgoingMessageViewHolder extends RecyclerView.ViewHolder
    {

        TextView chatText;

        TextView time;

        ImageView cdn_photo;

        RelativeLayout layout_cdn_photo;

        LinearLayout layout_message;


        OutgoingMessageViewHolder(View itemView)
        {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            chatText = itemView.findViewById(R.id.singleMessage);
             time = itemView.findViewById(R.id.time);
             cdn_photo =  itemView.findViewById(R.id.cdn_photo);
             layout_cdn_photo= itemView.findViewById(R.id.layout_cdn_photo);
             layout_message = itemView.findViewById(R.id.layout_message);
            cdn_photo.setOnClickListener(onButtonClickListener);
        }


        void bindData(ChatMessage message)
        {
            chatText.setText(message.getMessage());

            try
            {
                String date = Helper.UTC_to_Local_TimeZone(message.getTimestamp());

                try
                {
                    Date value = sdf.parse(date);
                    time.setText(sdf1.format(value));
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            if(!message.getCDNPhotoURL().isEmpty())
            {
                layout_cdn_photo.setVisibility(View.VISIBLE);

                try
                {
                    ImageLoader.loadThumbnail(context, message.getCDNPhotoURL(), cdn_photo, R.drawable.ic_place_image, 200, 200);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else
            {
                layout_cdn_photo.setVisibility(View.GONE);
            }

            if(!message.getMessage().isEmpty())
            {
                chatText.setVisibility(View.VISIBLE);
            }

            else
            {
                chatText.setVisibility(View.GONE);
            }

            // if message and image both are there then custom layout.
            if(!message.getMessage().isEmpty() && !message.getCDNPhotoURL().isEmpty())
            {
                ViewGroup.LayoutParams params = cdn_photo.getLayoutParams();
                ViewGroup.LayoutParams params1 = layout_message.getLayoutParams();

                layout_message.getLayoutParams().width = params.width + (int) Helper.convertDpToPixel(20);
                layout_message.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layout_message.setLayoutParams(params1);
            }

            else
            {
                ViewGroup.LayoutParams params1 = layout_message.getLayoutParams();

                layout_message.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layout_message.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layout_message.setLayoutParams(params1);
            }
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                if (v.getId() == R.id.cdn_photo) {
                    Intent intent = new Intent(context, FileViewerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FILE", new FileUtils(mMessages.get(index).getCDNPhotoURL(), ""));
                    context.startActivity(intent);
                }
            }
        };
    }


    class IncomingMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView chatText;

        CircleImageView iv_doctor;

        TextView time;

        IncomingMessageViewHolder(View itemView)
        {
            super(itemView);
          //  ButterKnife.bind(this, itemView);
            chatText= itemView.findViewById(R.id.singleMessage);
            iv_doctor= itemView.findViewById(R.id.doctor_image);
             time = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
            iv_doctor.setOnClickListener(onButtonClickListener);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                if (v.getId() == R.id.doctor_image) {
                    listener.onTaskCompleted(true, index, mMessages.get(index).getDoctorAvatarURL());
                }
            }
        };

        void bindData(ChatMessage message)
        {
            chatText.setText(message.getMessage());

            try
            {
                String date = Helper.UTC_to_Local_TimeZone(message.getTimestamp());

                try
                {
                    Date value = sdf.parse(date);
                    time.setText(sdf1.format(value));
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            /*if(!message.getDoctorAvatarURL().isEmpty())
            {
                try
                {
                    ImageLoader.load(context, message.getDoctorAvatarURL(), iv_doctor, R.drawable.doctor, 60, 60);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }*/
        }


        @Override
        public void onClick(View v)
        {
            //clickListener.onItemClick(v, getAdapterPosition());
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