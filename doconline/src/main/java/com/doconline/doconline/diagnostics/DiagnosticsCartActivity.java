package com.doconline.doconline.diagnostics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.doconline.doconline.R;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.helper.OnDialogAction;

import java.util.ArrayList;
import java.util.List;


public class DiagnosticsCartActivity extends BaseActivity implements OnDialogAction {

    private static final String TAG = DiagnosticsCartActivity.class.getSimpleName();


    Toolbar toolbar;


    TextView toolbar_title;


    RelativeLayout layout_block_ui;


    RelativeLayout layout_progress;


    CoordinatorLayout layout_root_view;

    Button btn_Done;



    MyViewPager mViewPager;

    private DiagnosticsCartSectionsPagerAdapter adapter;

    private boolean is_processing = false;

    private class DiagnosticsCartSectionsPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private DiagnosticsCartSectionsPagerAdapter(FragmentManager fm)
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

        /*private void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }*/

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }

    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, DiagnosticsUserAddressActivity.class));
    }

    @Override
    public void onPageSelection(int position, String title)
    {
        mViewPager.setCurrentItem(position);
        toolbar_title.setText(title);
    }

    @Override
    public void onBackPressed()
    {
        if(!is_processing)
        {
            if(mViewPager.getCurrentItem() == 1)
            {
                onPageSelection(0, "Your Addresses");
            }
            else
            {
                super.onBackPressed();
                this.finish();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Processing", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosticscart);
        //ButterKnife.bind(this);
         toolbar = findViewById(R.id.toolbar);

         toolbar_title= findViewById(R.id.toolbar_title);

        layout_block_ui= findViewById(R.id.layout_block_ui);

         layout_progress= findViewById(R.id.layout_loading);


        layout_root_view= findViewById(R.id.layout_root_view);

        btn_Done= findViewById(R.id.btnDone);

        mViewPager= findViewById(R.id.pagerDiagnosticsCartItems);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //DiagnosticsCartSectionsPagerAdapter mSectionsPagerAdapter = new DiagnosticsCartSectionsPagerAdapter(getSupportFragmentManager());
        btn_Done.setText("BOOK APPOINTMENT");
    }

}
