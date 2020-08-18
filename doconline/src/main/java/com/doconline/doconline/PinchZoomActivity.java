package com.doconline.doconline;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.TouchImageView;

import java.io.File;


public class PinchZoomActivity extends AppCompatActivity
{

    TouchImageView image;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
       // ButterKnife.bind(this);

        image  =  findViewById(R.id.imgFullScreen);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle("");
        this.makeActivityAppearOnLockScreen();

        previewCapturedImage(getIntent().getStringExtra("URL"));
    }


    private void previewCapturedImage(final String path)
    {
        try
        {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);

            ImageLoader.loadImageWithZoomOption(getApplicationContext(), uri.getPath(), image);
        }

        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeActivityAppearOnLockScreen()
    {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}