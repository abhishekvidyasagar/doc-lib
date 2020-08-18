package com.doconline.doconline.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;

/**
 * Created by cbug on 29/9/17.
 */

public class OrderActivity extends BaseActivity
{

    public static final int HTTP_REQUEST_CODE_GET_ORDERS = 1;
    public static final int HTTP_REQUEST_CODE_GET_ORDER_DETAILS = 2;

    private int position = 0;
    private String mOrderNo = "";


    Toolbar toolbar;
    TextView toolbar_title;
    RelativeLayout layout_refresh;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
       // ButterKnife.bind(this);
         toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        layout_refresh =  findViewById(R.id.layout_refresh);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initFragment(0, "Order History");

        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
        }
    }


    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, OrderActivity.class));
    }


    public void initFragment(int index, String title)
    {
        toolbar_title.setText(title);

        this.position = index;
        Fragment fragment = null;

        switch (index)
        {
            case 0:

                fragment = new OrderListFragment(getApplicationContext(), this, this, this);
                break;

            case 1:

                fragment = new OrderDetailsFragment(getApplicationContext(), this);
                Bundle args = new Bundle();
                args.putString("ORDER_ID", mOrderNo);
                fragment.setArguments(args);
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed()
    {
        this.onBackButtonPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                this.onBackButtonPressed();
                break;
        }

        return true;
    }


    private void onBackButtonPressed()
    {
        if(position == 1)
        {
            this.initFragment(0, "Order History");
        }

        else
        {
            finish();
        }
    }


    @Override
    public void onPreExecute()
    {
        try
        {
            if(!isFinishing())
            {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        layout_refresh.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        try
        {
            if(!isFinishing())
            {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        layout_refresh.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onPageSelection(int position, String title)
    {
        this.initFragment(position, title);
    }

    @Override
    public void onTaskCompleted(boolean flag, int index, String mOrderNo)
    {
        this.mOrderNo = mOrderNo;
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_INTERNET)
        {
            finish();
        }
    }
}