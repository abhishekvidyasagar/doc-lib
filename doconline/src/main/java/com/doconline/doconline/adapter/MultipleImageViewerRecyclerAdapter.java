package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.utils.FileUtils;

import java.io.File;
import java.util.List;


public class MultipleImageViewerRecyclerAdapter extends RecyclerView.Adapter<MultipleImageViewerRecyclerAdapter.ItemViewHolder>
{
    private Context context;
    private OnItemClickListener clickListener;

    private List<FileUtils> imageList;


    public MultipleImageViewerRecyclerAdapter(Context context, List<FileUtils> imageList)
    {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_image_multiple_viewer, viewGroup, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder vHolder, int i)
    {
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

        private ItemViewHolder(View itemView)
        {
            super(itemView);
         //   ButterKnife.bind(this, itemView);

            image = itemView.findViewById(R.id.attached_image);

            itemView.setOnClickListener(this);
        }

        private void bindData(final FileUtils imageModel)
        {
            try
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

            catch(Exception e)
            {
                e.printStackTrace();
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
}