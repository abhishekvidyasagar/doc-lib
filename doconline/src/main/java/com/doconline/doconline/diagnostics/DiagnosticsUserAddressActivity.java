package com.doconline.doconline.diagnostics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.helper.OnChangeDiagnosticsAddressListener;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnLoadingStatusChangedListener;
import com.doconline.doconline.model.DiagnosticsUserAddress;

import java.util.ArrayList;
import java.util.List;

import static android.app.ProgressDialog.STYLE_SPINNER;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;

/**
 * Created by A on 3/13/2018.
 */

public class DiagnosticsUserAddressActivity extends BaseActivity implements OnChangeDiagnosticsAddressListener,
        OnDialogAction, OnLoadingStatusChangedListener {

    private static final String TAG = DiagnosticsUserAddressActivity.class.getSimpleName();

//    @BindView(R.id.layout_block_ui)
//    RelativeLayout layout_block_ui;


    TextView toolbar_title;


    CoordinatorLayout layout_root_view;


    Toolbar toolbar;


    Button btn_Done;


    MyViewPager mViewPager;

//    @BindView(R.id.layout_progress)
//    FrameLayout layout_progress;


    RelativeLayout layout_progress;

    boolean isUpdatingUserAddress = false;

    private SectionsPagerAdapter adapter;
    private DiagnosticsUserAddressFormFragment addressFormFragment;
    private DiagnosticsUserAddressListFragment addressListFragment;


    private int currentAddressIndexToDelete = -1;
    private boolean is_processing = false;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics_user_address);
       // ButterKnife.bind(this);

         toolbar_title = findViewById(R.id.toolbar_title);

         layout_root_view= findViewById(R.id.layout_root_view);

        toolbar= findViewById(R.id.toolbar);

         btn_Done= findViewById(R.id.btnDone);

         mViewPager= findViewById(R.id.pagerDiagnosticsUserAddress);

//    @BindView(R.id.layout_progress)
//    FrameLayout layout_progress;

        layout_progress= findViewById(R.id.layout_loading);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        btn_Done.setText("ADD NEW ADDRESS");

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);

        setupViewPager(mViewPager);

        if(!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
        }

        mDialog = createProgressDialog(DiagnosticsUserAddressActivity.this);
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


    public void onClick(View view) {
        if (view.getId() == R.id.btnDone) {
            int numAddressList = mController.getDiagnosticsUserAddressCount();
            if (numAddressList >= 10) {
                new CustomAlertDialog(this, this, layout_root_view)
                        .showSnackbar("Cannot add more than 10 addresses", CustomAlertDialog.LENGTH_SHORT);
            } else {
                mViewPager.setCurrentItem(1);
                toolbar_title.setText("Add new address");
                addressFormFragment.setPrefilledData(null);
            }
        }
    }

    private void setupViewPager(MyViewPager viewPager)
    {
        toolbar_title.setText(getResources().getString(R.string.text_diagnostics_user_addresses));
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        addressListFragment = new DiagnosticsUserAddressListFragment(this, this, this,this);
        addressFormFragment = new DiagnosticsUserAddressFormFragment(this,this,this);

        adapter.addFrag(addressListFragment, getResources().getString(R.string.text_diagnostics_user_addresses));
        adapter.addFrag(addressFormFragment, "Add new address");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        btn_Done.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        btn_Done.setVisibility(View.GONE);
                        break;
//                    case 2:
//                        btn_Done.setVisibility(View.GONE);
//                        break;
                }
                toolbar_title.setText(adapter.getPageTitle(position));
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
    public void showLoadingActivity() {
        //layout_progress.setVisibility(View.VISIBLE);
        mDialog.show();
    }

    @Override
    public void hideProgressbarWithSuccess() {
        mDialog.dismiss();
        //layout_progress.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressbarWithFailure() {

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
                addressListFragment.loadUserAddressList();
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                this.onBackPressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void OnAddDiagnosticsAddress(DiagnosticsUserAddress address) {

    }

    @Override
    public void OnEditDiagnosticsAddress(int position) {
        //Toast.makeText(this, "Position:" + Integer.toString(position), Toast.LENGTH_SHORT).show();

        addressFormFragment.setPrefilledData(mController.getUserAddress(position));
    }

    @Override
    public void OnDeleteDiagnosticsAddress(int position) {
        //Toast.makeText(this, "Position:" + Integer.toString(position), Toast.LENGTH_SHORT).show();
        showAlertDialog(DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS,
                getResources().getString(R.string.dialog_diagnostics_address_delete_warning));
    }

    @Override
    public void OnSetDiagnosticsAddressAsDefault(int position) {
        //Toast.makeText(this, "Position:" + Integer.toString(position), Toast.LENGTH_SHORT).show();

    }

    private void showAlertDialog(int requestCode, String message)
    {
//        if(DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS == requestCode) {
        new CustomAlertDialog(this, requestCode, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_are_you_sure),
                        message,
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), true);
//        }
//        else if(DIALOG_REQUEST_CODE_DIAGNOSTICS_SAVE_NEW_ADDRESS == requestCode){
//
//        }
//        else if(DIALOG_REQUEST_CODE_DIAGNOSTICS_UPDATE_ADDRESS == requestCode){
//
//        }
//        else if(DIALOG_REQUEST_CODE_DIAGNOSTICS_SET_DEFAULT_ADDRESS== requestCode){
//
//        }
    }

//    @Override
//    public void onPositiveAction(int requestCode)
//    {
//        if(requestCode == DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS)
//        {
//            if(-1 != currentAddressIndexToDelete){
//                DiagnosticsUserAddressListFragment.mDiagnosticsUserAddressList.remove(currentAddressIndexToDelete);
//            }
//        }
//    }
//
//    @Override
//    public void onNegativeAction(int requestCode){
//
//        switch(requestCode){
//            case DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS:
//                currentAddressIndexToDelete = -1;
//        }
//    }

    @Override
    public void onNegativeAction()
    {

    }
}
