package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.TouchImageView;
import com.doconline.doconline.utils.FileUtils;

import java.io.File;
import java.util.List;

public class MyCustomPagerAdapter extends PagerAdapter
{
    private Context context;
    private List<FileUtils> images;
    private LayoutInflater layoutInflater;


    public MyCustomPagerAdapter(Context context, List<FileUtils> images)
    {
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position)
    {
        View itemView = layoutInflater.inflate(R.layout.custom_adapter_item, container, false);

        final TouchImageView imageView = itemView.findViewById(R.id.imgFullScreen);

        File file = new File(images.get(position).getPath());
        Uri uri = Uri.fromFile(file);

        if (FileUtils.isDocumentFile(images.get(position).getPath()))
        {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_placeholder);

            ViewGroup.LayoutParams params1 = imageView.getLayoutParams();

            imageView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageView.setLayoutParams(params1);

            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        else
        {
            ImageLoader.loadImageWithZoomOption(context, uri.getPath(), imageView);
        }

        container.addView(itemView);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((RelativeLayout) object);
    }
}