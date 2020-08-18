package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.utils.FileUtils;

import java.io.File;
import java.util.List;


public class ImageWithCaptionRecyclerAdapter extends RecyclerView.Adapter<ImageWithCaptionRecyclerAdapter.ItemViewHolder>
{
    private Context context;
    private OnItemDeleted listener;
    private boolean isRemovable;
    private boolean isURL;
    private OnItemClickListener clickListener;

    private List<FileUtils> imageList;
    private MyApplication mController;


    public ImageWithCaptionRecyclerAdapter(Context context, OnItemDeleted listener, List<FileUtils> imageList, boolean isRemovable, boolean isURL)
    {
        this.context = context;
        this.listener = listener;
        this.isRemovable = isRemovable;
        this.isURL = isURL;
        this.imageList = imageList;
        this.mController = MyApplication.getInstance();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_image_with_caption, viewGroup, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder vHolder, int i)
    {
        vHolder.ib_remove.setTag(i);
        vHolder.edit_image_caption.setTag(i);
        vHolder.bindData(imageList.get(i));
    }


    @Override
    public int getItemCount()
    {
        return imageList == null ? 0 : imageList.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        ImageView image;

        ImageButton ib_remove;

        EditText edit_image_caption;


        private ItemViewHolder(View itemView)
        {
            super(itemView);
            //ButterKnife.bind(this, itemView);

             image = itemView.findViewById(R.id.attached_image);
             ib_remove = itemView.findViewById(R.id.ib_remove_attached_image);
             edit_image_caption = itemView.findViewById(R.id.edit_image_caption);

            itemView.setOnClickListener(this);
            ib_remove.setOnClickListener(onButtonClickListener);
        }


        private void bindData(final FileUtils imageModel)
        {
            if(isRemovable)
            {
                ib_remove.setVisibility(View.VISIBLE);
            }

            else
            {
                ib_remove.setVisibility(View.GONE);
            }

            edit_image_caption.setText(imageModel.getCaption());

            edit_image_caption.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence c, int start, int before, int count)
                {
                    mController.getAppointmentBookingSummery().getFiles().get(getAdapterPosition()).setCaption(c.toString());
                }

                public void beforeTextChanged(CharSequence c, int start, int count, int after)
                {

                }

                public void afterTextChanged(Editable c)
                {

                }
            });

            try
            {
                if(!isURL)
                {
                    if(Helper.fileExist(imageModel.getPath()))
                    {
                        if (FileUtils.isDocumentFile(imageModel.getPath()))
                        {
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_placeholder);
                            image.setImageBitmap(bitmap);
                        }

                        else
                        {
                            ImageLoader.loadLocalBitmap(new File(imageModel.getPath()), image);
                        }
                    }
                }

                else
                {
                    try
                    {
                        ImageLoader.loadThumbnail(context, imageModel.getPath(), image, R.drawable.ic_place_image, 120, 120);
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }


        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int index = (int) v.getTag();

                if (v.getId() == R.id.ib_remove_attached_image) {
                    listener.onItemDeleted(index, 0);
                }
            }
        };


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
}