package com.doconline.doconline.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.BaseActivity;

/*import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;*/


public class ConsentFormActivity extends BaseActivity
{

    Toolbar toolbar;
    TextView toolbar_title;
    Button btn_accept,btn_decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_form);
       // ButterKnife.bind(this);
        toolbar =  findViewById(R.id.toolbar);
        toolbar_title =  findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        btn_accept =  findViewById(R.id.btnAccept);

        btn_decline = findViewById(R.id.btnDecline);
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(100, intent);
                finish();
            }
        });

        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(200, intent);
                finish();
            }
        });
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        toolbar_title.setText(getResources().getString(R.string.text_terms_conditions));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                Intent intent = new Intent();
                setResult(200, intent);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

   /* @OnClick(R.id.btnAccept)
    public void onAcceptClick(View view)
    {
        Intent intent = new Intent();
        setResult(100, intent);
        finish();
    }

    @OnClick(R.id.btnDecline)
    public void onDeclineClick(View view)
    {
        Intent intent = new Intent();
        setResult(200, intent);
        finish();
    }*/


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        setResult(200, intent);
        finish();

        super.onBackPressed();
    }
}