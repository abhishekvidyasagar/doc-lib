package com.doconline.doconline.appointment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class AppointmentHistoryActivity extends BaseActivity
{
    public static int HTTP_REQUEST_CODE = 1;


    TextView toolbar_title;

    Toolbar toolbar;

    CoordinatorLayout layout_root_view;

    ViewPager viewPager;

    TabLayout tabLayout;

    public static String fromScreen = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);
       // ButterKnife.bind(this);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar= findViewById(R.id.toolbar);
        layout_root_view= findViewById(R.id.layout_root_view);
        viewPager= findViewById(R.id.viewpager);
        tabLayout= findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /*if (getIntent().getExtras().containsKey("fromscreen") &&
                getIntent().getExtras().get("fromscreen").toString().equalsIgnoreCase("bookconsultaion")){
            fromScreen = getIntent().getExtras().get("fromscreen").toString();
        }*/


        if(!MyApplication.getInstance().getSession().checkLogin())
        {
            finish();
            return;
        }

        initViewPager();

        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, this, layout_root_view).snackbarForInternetConnectivity();
        }
    }


    private void initViewPager()
    {
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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


    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, AppointmentHistoryActivity.class));
    }


    private void setupViewPager(ViewPager viewPager)
    {
        toolbar_title.setText(getResources().getString(R.string.text_my_appointment));

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        /*if (getIntent().getExtras().containsKey("fromscreen") &&
                getIntent().getExtras().get("fromscreen").toString().equalsIgnoreCase("bookconsultaion")){
            adapter.addFrag(PreviousAppointmentFragment.newInstance(), "Previous");
        }else {
            adapter.addFrag(UpcomingAppointmentFragment.newInstance(), "Upcoming");
            adapter.addFrag(PreviousAppointmentFragment.newInstance(), "Previous");
        }*/

        adapter.addFrag(UpcomingAppointmentFragment.newInstance(), "Upcoming");
        adapter.addFrag(PreviousAppointmentFragment.newInstance(), "Previous");

        viewPager.setAdapter(adapter);
    }


    private static class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
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


        void addFrag(Fragment fragment, String title)
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
}