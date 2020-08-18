package com.doconline.doconline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.HRA.HRAMainActivity;
import com.doconline.doconline.adapter.AddOnsRecyclerAdapter;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.model.HealthProfile;
import com.doconline.doconline.profile.BMIResultsActivity;
import com.doconline.doconline.profile.SpeedTestActivity;


/**
 * Created by chiranjitbardhan on 03/11/17.
 */

public class AddonsActivity extends BaseActivity implements
        BMIDialogFragment.BMICalculationFragmentListener
{
    private AddOnsRecyclerAdapter mAdapter;


    RecyclerView recyclerView;
    TextView tv_toolbar_title;
    Toolbar toolbar;
    CoordinatorLayout layout_root_view;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addons);
        toolbar  =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        layout_root_view = findViewById(R.id.layout_root_view);
        recyclerView = findViewById(R.id.recycler_view);
        tv_toolbar_title = findViewById(R.id.toolbar_title);
        tv_toolbar_title.setText(getResources().getString(R.string.text_add_ons));

        this.initAdapter();
    }

    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, AddonsActivity.class));
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

    private void initAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddOnsRecyclerAdapter(getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new AddOnsRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {
                switch (position)
                {
                    case 0:

                        HealthProfile profile = mController.getSQLiteHelper().getHealthProfile();

                        FragmentManager fm = getSupportFragmentManager();

                        BMIDialogFragment dialog = new BMIDialogFragment(getApplicationContext(), profile.height, profile.weight);
                        dialog.setListener(AddonsActivity.this);
                        dialog.setRetainInstance(true);
                        dialog.setCancelable(false);
                        dialog.show(fm, "");

                        break;

                    case 1:
                        Intent intentSpeedTest = new Intent(AddonsActivity.this, SpeedTestActivity.class);
                        startActivity(intentSpeedTest);
                        break;

                    case 2:
                        Intent intentHRA = new Intent(AddonsActivity.this, HRAMainActivity.class);
                        startActivity(intentHRA);
                        break;
                }
            }
        });
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, float height, float weight, float tBMI)
    {
        Intent intent_BMIResults = new Intent(AddonsActivity.this, BMIResultsActivity.class);
        intent_BMIResults.putExtra("BMI_RESULT", tBMI);
        intent_BMIResults.putExtra("WEIGHT_FOR_BMI", weight);
        intent_BMIResults.putExtra("HEIGHT_FOR_BMI", height);

        startActivity(intent_BMIResults);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }
}