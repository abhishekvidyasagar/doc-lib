package com.doconline.doconline.diagnostics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static android.app.ProgressDialog.STYLE_SPINNER;

/**
 * Created by admin on 2018-02-26.
 */

public class DiagnosticsHistoryActivity extends BaseActivity implements OnLoadingStatusChangedListener /*, TabLayout.OnTabSelectedListener*/ {

    public static int HTTP_REQUEST_CODE = 1;


    TextView toolbar_title;
    Toolbar toolbar;
    DiagnosticsHistoryListingFragment diagnosticsHistoryListingFragment1, diagnosticsHistoryListingFragment2;
    ProgressDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);
       // ButterKnife.bind(this);
        toolbar_title=findViewById(R.id.toolbar_title);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!MyApplication.getInstance().getSession().checkLogin()) {
            finish();
            return;
        }

        initViewPager();

        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(this, this, findViewById(R.id.layout_root_view)).snackbarForInternetConnectivity();
        }

        mDialog = createProgressDialog(DiagnosticsHistoryActivity.this);
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(STYLE_SPINNER);
    }

    public ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    private void initViewPager() {
        final ViewPager viewPager = findViewById(R.id.viewpager);

        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static void start(final Activity activity) {
        activity.startActivity(new Intent(activity, DiagnosticsHistoryActivity.class));
    }


    private void setupViewPager(ViewPager viewPager) {
        toolbar_title.setText(getResources().getString(R.string.diagnostics_history));

        DiagnosticsHistoryActivity.ViewPagerAdapter adapter = new DiagnosticsHistoryActivity.ViewPagerAdapter(getSupportFragmentManager());

        diagnosticsHistoryListingFragment1 = new DiagnosticsHistoryListingFragment(this, this, this, this);
        diagnosticsHistoryListingFragment2 = new DiagnosticsHistoryListingFragment(this, this, this, this);
        adapter.addFrag(diagnosticsHistoryListingFragment1, "Upcoming");
        adapter.addFrag(diagnosticsHistoryListingFragment2, "History");

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0: //Diagnostics product listing
                        if (diagnosticsHistoryListingFragment1 != null) {
                            diagnosticsHistoryListingFragment1.upComingAppointments();
                        }

                        break;

                    case 1: //Diagnostics product details
                        if (diagnosticsHistoryListingFragment2 != null) {
                            diagnosticsHistoryListingFragment2.previousAppointments();
                        }
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void showLoadingActivity() {
        //layout_progress.setVisibility(View.VISIBLE);
        //layout_block_ui.setVisibility(View.VISIBLE);
        mDialog.show();
    }

    @Override
    public void hideProgressbarWithSuccess() {
        //layout_progress.setVisibility(View.GONE);
        //layout_block_ui.setVisibility(View.GONE);
        mDialog.dismiss();
    }

    @Override
    public void hideProgressbarWithFailure() {

    }

    /*@Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.v("GetFragment",""+tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }*/


    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
