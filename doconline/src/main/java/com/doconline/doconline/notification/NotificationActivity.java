package com.doconline.doconline.notification;

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
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.MyViewPager;

import java.util.ArrayList;
import java.util.List;


public class NotificationActivity extends BaseActivity
{
    MyViewPager mViewPager;
    TextView toolbar_title;
    RelativeLayout layout_refresh;
    Toolbar toolbar;

    public static final int HTTP_REQUEST_CODE_GET_ALL_NOTIFICATIONS = 1;
    public static final int HTTP_REQUEST_CODE_DELETE_ALL_NOTIFICATIONS = 2;
    public static final int HTTP_REQUEST_CODE_READ_ALL_NOTIFICATIONS = 3;
    public static final int HTTP_REQUEST_CODE_DELETE_NOTIFICATION = 4;
    public static final int HTTP_REQUEST_CODE_READ_NOTIFICATION = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
       // ButterKnife.bind(this);

        mViewPager =findViewById(R.id.pager);
        toolbar_title =  findViewById(R.id.toolbar_title);
        layout_refresh = findViewById(R.id.layout_refresh);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(getResources().getString(R.string.text_notification));

        if(!MyApplication.getInstance().getSession().checkLogin())
        {
            finish();
            return;
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Fixes bug for disappearing fragment content
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);

        setupViewPager(mViewPager);
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


    private void setupViewPager(MyViewPager viewPager)
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(NotificationFragment.newInstance(), "Notifications");

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


    @Override
    public void onPreExecute()
    {
        layout_refresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        layout_refresh.setVisibility(View.INVISIBLE);
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
}