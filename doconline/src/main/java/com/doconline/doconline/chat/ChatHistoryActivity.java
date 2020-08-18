package com.doconline.doconline.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.MarqueeTextView;


public class ChatHistoryActivity extends BaseActivity {

    CoordinatorLayout layout_root_view;
    RelativeLayout layout_refresh;
    FrameLayout layout_thumbnail;
    ImageView iv_thumbnail;
    TextView toolbar_title;
    Toolbar toolbar;
    RelativeLayout toolbarDoctorLayout;
    MarqueeTextView mciPNoTextView;
    ImageView doctorInfoImageView;
    MarqueeTextView qualificationTextView;

    public static int session_id;
    private int position = 0;
    public static final int HTTP_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
       // ButterKnife.bind(this);


         layout_root_view = findViewById(R.id.layout_root_view);
         layout_refresh= findViewById(R.id.layout_refresh);
        layout_thumbnail= findViewById(R.id.layout_thumbnail);
         iv_thumbnail= findViewById(R.id.thumbnail);
        toolbar_title= findViewById(R.id.toolbar_title);
         toolbar= findViewById(R.id.toolbar);
         toolbarDoctorLayout= findViewById(R.id.toolbar_doctor_layout);
        mciPNoTextView= findViewById(R.id.tv_mci_pno);
       doctorInfoImageView= findViewById(R.id.iv_doctor_info);
        qualificationTextView= findViewById(R.id.tv_qualification);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(this, this, layout_root_view).snackbarForInternetConnectivity();
            return;
        }

        onPageSelection(0, getResources().getString(R.string.text_chat_history));
    }


    public static void start(final Activity activity) {
        activity.startActivity(new Intent(activity, ChatHistoryActivity.class));
    }


    private void fragment_redirect(int index, String title) {
        Fragment fragment = null;
        this.position = index;

        switch (index) {
            case 0:

                fragment = ChatListFragment.newInstance();
                toolbarDoctorLayout.setVisibility(View.GONE);
                toolbar_title.setVisibility(View.VISIBLE);
                toolbar_title.setText(getResources().getString(R.string.text_chat_history));
                break;

            case 1:

                fragment = ChatDetailsFragment.newInstance();

                if (Integer.parseInt(title) == 0) {
                    toolbar_title.setText(Helper.toCamelCase("Doctor Not Connected"));
                } else {
                    //toolbar_title.setText(Helper.toCamelCase(title));
                    toolbar_title.setVisibility(View.GONE);
                    toolbarDoctorLayout.setVisibility(View.VISIBLE);
                    /*mciPNoTextView.setText();
                    qualificationTextView.setText();*/
                }

                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        this.onBackButtonPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.onBackButtonPressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void onBackButtonPressed() {
        if (position == 0) {
            finish();
        } else {
            onPageSelection(0, getResources().getString(R.string.text_chat_history));
        }
    }


    @Override
    public void onPageSelection(int position, String title) {
        fragment_redirect(position, title);
    }


    @Override
    public void onPreExecute() {
        try {
            if (!isFinishing()) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        layout_refresh.setVisibility(View.VISIBLE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

        try {
            if (!isFinishing()) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        layout_refresh.setVisibility(View.INVISIBLE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPositiveAction() {
        finish();
    }

    @Override
    public void onNegativeAction() {
        finish();
    }

    @Override
    public void onTaskCompleted(boolean flag, int code, String avatar_url) {
        if (!isFinishing()) {
            try {
                layout_thumbnail.setVisibility(View.VISIBLE);

                if (!avatar_url.isEmpty()) {
                    ImageLoader.loadThumbnail(getApplicationContext(), avatar_url, iv_thumbnail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUserInteraction() {
        layout_thumbnail.setVisibility(View.GONE);
    }
}