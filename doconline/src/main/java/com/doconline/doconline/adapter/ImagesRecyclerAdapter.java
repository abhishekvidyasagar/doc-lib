package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.utils.FileUtils;

import java.io.File;
import java.util.List;


public class ImagesRecyclerAdapter extends RecyclerView.Adapter<ImagesRecyclerAdapter.ItemViewHolder>
{
    /**
     * For store view state use SparseBooleanArray
     * private SparseBooleanArray selectedItems = new SparseBooleanArray();
     */
    private Context context;
    private OnItemDeleted listener;
    public boolean isRemovable;
    private boolean isURL;
    private OnItemClickListener clickListener;

    private List<FileUtils> imageList;


    public ImagesRecyclerAdapter(Context context, OnItemDeleted listener, List<FileUtils> imageList, boolean isRemovable, boolean isURL)
    {
        this.context = context;
        this.listener = listener;
        this.isRemovable = isRemovable;
        this.isURL = isURL;
        this.imageList = imageList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_images, viewGroup, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder vHolder, int i)
    {
        vHolder.ib_remove.setTag(i);
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

        private ItemViewHolder(View itemView)
        {
            super(itemView);
          //  ButterKnife.bind(this, itemView);

            image = itemView.findViewById(R.id.attached_image);
             ib_remove =  itemView.findViewById(R.id.ib_remove_attached_image);

            itemView.setOnClickListener(this);
            ib_remove.setOnClickListener(onButtonClickListener);
        }


        private void bindData(final FileUtils imageModel)
        {
            // Whether upcoming appointment or previous appointment.
            if(isRemovable)
            {
                ib_remove.setVisibility(View.VISIBLE);
            }

            else
            {
                ib_remove.setVisibility(View.GONE);
            }

            try
            {
                if(!isURL)
                {
                    if(Helper.fileExist(imageModel.getPath()))
                    {
                        ImageLoader.loadLocalBitmap(new File(imageModel.getPath()), image);
                    }
                }

                else
                {
                    try
                    {
                        if (FileUtils.isDocumentFile(imageModel.getPath()))
                        {
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_placeholder);
                            image.setImageBitmap(bitmap);
                        }

                        else
                        {
                            ImageLoader.loadThumbnail(context, imageModel.getPath(), image, R.drawable.ic_place_image, 120, 120);
                        }
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
                    listener.onItemDeleted(index, imageList.get(index).getId());
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