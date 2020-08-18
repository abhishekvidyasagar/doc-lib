package com.doconline.doconline.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.TouchImageView;

import static com.doconline.doconline.app.Constants.CHAT_IMAGE_REQUEST_CODE;


public class ChatImagePreviewActivity extends AppCompatActivity
{


    Toolbar toolbar;
    TouchImageView image;
    ImageButton buttonSend;
    EditText chatText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image_preview_layout);
        //ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        image= findViewById(R.id.imgFullScreen);
        buttonSend= findViewById(R.id.ibSend);
        chatText= findViewById(R.id.editChat);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle("");

        this.makeActivityAppearOnLockScreen();

        final String image_path = getIntent().getStringExtra("IMAGE_PATH");

        if(!image_path.isEmpty())
        {
            ImageLoader.loadImageWithZoomOption(getApplicationContext(), image_path, image);
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                attemptSend(image_path);
            }
        });
    }


    private void attemptSend(String image_path)
    {
        String message = chatText.getText().toString().trim();

        Intent intent=new Intent();
        intent.putExtra("BODY",message);
        intent.putExtra("IMAGE_PATH", image_path);
        setResult(CHAT_IMAGE_REQUEST_CODE, intent);
        finish();
    }


    /*@Override
    public boolean onCreateOptionsMenu(DocOnlineMenu menu)
    {
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }*/


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


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        try
        {
            if (getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(imm != null)
                {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return super.dispatchTouchEvent(ev);
    }
}