package com.doconline.doconline;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.consultation.guest.BookGuestConsultationActivity;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.login.MainActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


public class DashboardActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener
{
    DrawerLayout drawer;
    NavigationView navigationView;
    TextView tv_toolbar_title;
    CoordinatorLayout layout_root_view;
    Toolbar toolbar;

    public static Activity activity;

    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       // ButterKnife.bind(this);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tv_toolbar_title = findViewById(R.id.toolbar_title);
        layout_root_view = findViewById(R.id.layout_root_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        activity = DashboardActivity.this;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        setCustomerCareNumber();

        initFragment();

        if (!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
        }
    }

    private void setCustomerCareNumber()
    {
        try
        {
            View header = navigationView.getHeaderView(0);
            TextView tv_customer_care_number = header.findViewById(R.id.tv_customer_care_number);
            String number = "Customer Care Number\n" + mController.getSession().getCustomerCareNumber();
            tv_customer_care_number.setText(number);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        snackbarSignUp();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    @Override
    public void onPageSelection(int position, String title)
    {
        switch (position)
        {
            case 0:

                startActivity(new Intent(DashboardActivity.this, BookGuestConsultationActivity.class));
                //BookGuestConsultationActivity.start(DashboardActivity.this);
                break;

            default:

                this.snackbarSignUp();
                break;
        }
    }


    @Override
    public void onPositiveAction() {

    }


    public void onPositiveAction(int requestCode, String data)
    {

    }


    @Override
    public void onPositiveAction(int requestCode)
    {

    }

    @Override
    public void onNegativeAction() {

    }


    private void initFragment()
    {
        tv_toolbar_title.setText(getResources().getString(R.string.text_dashboard));

        Fragment fragment = new DashboardFragment(this, this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }


    private void snackbarSignUp()
    {
        Snackbar bar = Snackbar.make(layout_root_view, "Sign up to avail the feature.", Snackbar.LENGTH_LONG);

        bar.setAction("SIGN UP", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                intent.putExtra("INDEX", 1);
                startActivity(intent);

                finish();
            }
        });

        bar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));

        TextView textView = bar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        bar.setActionTextColor(Color.WHITE);
        bar.show();
    }


    public void call(View view)
    {
        try
        {
            String number = mController.getSession().getCustomerCareNumber();

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {

        snackbarSignUp();
    }
}