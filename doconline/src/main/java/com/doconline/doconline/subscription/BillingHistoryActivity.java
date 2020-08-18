package com.doconline.doconline.subscription;

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
import androidx.fragment.app.FragmentPagerAdapter;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.model.SubscriptionBilling;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;


public class BillingHistoryActivity extends BaseActivity
{
    private static final String TAG = BillingHistoryActivity.class.getSimpleName();


    RelativeLayout layout_refresh;

    MyViewPager mViewPager;

    TextView toolbar_title;

    Toolbar toolbar;

    public static int index = -1;
    public static List<SubscriptionBilling> mBillings = new ArrayList<>();

    public static final int HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL = 1;
    public static final int HTTP_REQUEST_CODE_GET_SUBSCRIPTION_BILL_DETAILS = 2;
    public static final int HTTP_REQUEST_CODE_DOWNLOAD_BILL = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_history);
        //ButterKnife.bind(this);
         layout_refresh =  findViewById(R.id.layout_refresh);
         mViewPager =  findViewById(R.id.pager);
         toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initViewPager();

        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
        }
    }


    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, BillingHistoryActivity.class));
    }


    private void initViewPager()
    {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);

        setupViewPager(mViewPager);
    }


    private void setupViewPager(MyViewPager viewPager)
    {
        toolbar_title.setText(getResources().getString(R.string.text_billing_history));

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(BillingListFragment.newInstance(), getResources().getString(R.string.text_billing_history));
        adapter.addFrag(BillingDetailsFragment.newInstance(), getResources().getString(R.string.text_billing_summary));

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }


    private void fragment_redirect(int index, String title)
    {
        mViewPager.setCurrentItem(index);
        toolbar_title.setText(title);
    }


    @Override
    public void onBackPressed()
    {
        if(mViewPager.getCurrentItem() == 0)
        {
            finish();
        }

        else
        {
            onPageSelection(0, getResources().getString(R.string.text_billing_history));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                if(mViewPager.getCurrentItem() == 0)
                {
                    finish();
                }

                else
                {
                    onPageSelection(0, getResources().getString(R.string.text_billing_history));
                }

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageSelection(int position, String title)
    {
        fragment_redirect(position, title);
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
    public void onPositiveAction()
    {
        finish();
    }

    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_INTERNET)
        {
            finish();
        }
    }

    @Override
    public void onNegativeAction()
    {
        finish();
    }
}