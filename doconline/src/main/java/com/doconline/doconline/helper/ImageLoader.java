package com.doconline.doconline.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.doconline.doconline.R;

import java.io.File;

public class ImageLoader {

    public static void load(final Context context, String url, final ImageView imageView, int placeholder, int size_x, int size_y)
    {
        try
        {
            Glide.with(context)
                    .load(url)
                    .placeholder(placeholder)
                    .dontAnimate()
                    .override(size_x, size_y)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .listener(new RequestListener<String, GlideDrawable>() {

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
                        {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                        {
                            Animation anim = AnimationUtils.loadAnimation(context, R.anim.network_image_view);
                            imageView.startAnimation(anim);

                            return false;
                        }
                    })
                    .into(imageView);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void loadThumbnail(final Context context, String url, final ImageView imageView, int placeholder, int size_x, int size_y)
    {
        try
        {
            Glide.with(context)
                    .load(url)
                    .placeholder(placeholder)
                    .dontAnimate()
                    .override(size_x, size_y)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(imageView);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void loadThumbnail(final Context context, String url, final ImageView imageView)
    {
        try
        {
            Glide.with(context)
                    .load(url)
                    .dontAnimate()
                    .thumbnail(0.1f)
                    .into(imageView);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void loadLocal(final Context context, int resource, final ImageView imageView, int placeholder, int size_x, int size_y)
    {
        try
        {
            Glide.with(context)
                    .load(resource)
                    .placeholder(placeholder)
                    .dontAnimate()
                    .override(size_x, size_y)
                    .centerCrop()
                    .into(imageView);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void loadImageWithZoomOption(final Context context, String url, final TouchImageView imageView)
    {
        try
        {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .fitCenter()
                    .thumbnail(0.1f)
                    .into(new BitmapImageViewTarget(imageView) {

                        @Override
                        public void onResourceReady(Bitmap drawable, GlideAnimation anim)
                        {
                            super.onResourceReady(drawable, anim);

                            try
                            {
                                imageView.setImageBitmap(drawable);
                                imageView.setMaxZoom(4f);
                            }

                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void loadLocalBitmap(File file, ImageView imageView)
    {
        File f = new File(file.getPath());

        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 8;
        options.inJustDecodeBounds = false;

        final Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), options);
        imageView.setImageBitmap(bitmap);
    }
}