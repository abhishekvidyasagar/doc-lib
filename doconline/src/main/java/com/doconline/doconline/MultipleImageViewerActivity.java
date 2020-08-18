package com.doconline.doconline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.doconline.doconline.adapter.MultipleImageViewerRecyclerAdapter;
import com.doconline.doconline.adapter.MyCustomPagerAdapter;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.utils.FileUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.IMAGE_VIEWER_REQUEST_CODE;

@SuppressWarnings("unchecked")
public class MultipleImageViewerActivity extends AppCompatActivity
{

    ViewPager viewPager;

    RecyclerView recyclerView;

    Toolbar toolbar;

    EditText editCaption;

    MyApplication mController;
    private MyCustomPagerAdapter pager;
    private MultipleImageViewerRecyclerAdapter mAdapter;
    private int position;
    private boolean fromAppointmentSummary = false;
    private List<FileUtils> files = new ArrayList<>();
ImageButton ibAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_image_viewer);
       // ButterKnife.bind(this);


        viewPager =findViewById(R.id.viewPager);
        recyclerView = findViewById(R.id.image_list);
        toolbar = findViewById(R.id.toolbar);
        editCaption =  findViewById(R.id.edit_caption);
        ibAdd = findViewById(R.id.ibAdd);

        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(v);
            }
        });
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle("");
        this.makeActivityAppearOnLockScreen();

        this.mController = MyApplication.getInstance();
        this.position = getIntent().getIntExtra("POSITION", 0);
        this.fromAppointmentSummary = getIntent().getBooleanExtra("FROM_APPOINTMENT_SUMMARY", false);

        if(!fromAppointmentSummary)
        {
            editCaption.setText(mController.getAppointmentBookingSummery().getFiles().get(position).getCaption());
            pager = new MyCustomPagerAdapter(MultipleImageViewerActivity.this, mController.getAppointmentBookingSummery().getFiles());
        }

        else
        {
            Intent intent = getIntent();
            Bundle args = intent.getBundleExtra("BUNDLE_ATTACHMENTS");
            files = (ArrayList<FileUtils>) args.getSerializable("ATTACHMENTS");

            editCaption.setText(files.get(position).getCaption());
            pager = new MyCustomPagerAdapter(MultipleImageViewerActivity.this, files);
        }

        viewPager.setAdapter(pager);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) { }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageSelected(int i) {

                position = i;

                if(!fromAppointmentSummary)
                {
                    editCaption.setText(mController.getAppointmentBookingSummery().getFiles().get(position).getCaption());
                }

                else
                {
                    editCaption.setText(files.get(position).getCaption());
                }
            }

        });

        this.initImageAdapter();
        this.onCaptionAdd();
    }


    private void initImageAdapter()
    {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        if(!fromAppointmentSummary)
        {
            mAdapter = new MultipleImageViewerRecyclerAdapter(getApplicationContext(), mController.getAppointmentBookingSummery().getFiles());
        }

        else
        {
            mAdapter = new MultipleImageViewerRecyclerAdapter(getApplicationContext(), files);
        }

        recyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new MultipleImageViewerRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {
                position = i;
                viewPager.setCurrentItem(i);
            }
        });
    }


    private void onCaptionAdd()
    {
        editCaption.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count)
            {
                if(!fromAppointmentSummary)
                {
                    mController.getAppointmentBookingSummery().getFiles().get(position).setCaption(c.toString());
                }

                else
                {
                    files.get(position).setCaption(c.toString());
                }
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after)
            {

            }

            public void afterTextChanged(Editable c)
            {

            }
        });
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

    private void makeActivityAppearOnLockScreen()
    {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }


    //@OnClick(R.id.ibAdd)
    public void add( View view)
    {
        if(fromAppointmentSummary)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("ATTACHMENTS", (Serializable) files);

            Intent intent = new Intent();
            intent.putExtra("BUNDLE_ATTACHMENTS", bundle);
            setResult(IMAGE_VIEWER_REQUEST_CODE, intent);
        }

        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        try
        {
            if (getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(imm != null)
                {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return super.dispatchTouchEvent(ev);
    }
}