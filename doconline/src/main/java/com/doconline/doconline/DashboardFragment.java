package com.doconline.doconline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.adapter.DashboardRecyclerAdapter;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnPageSelection;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chiranjitbardhan on 08/01/18.
 */

public class DashboardFragment extends BaseFragment
{
    private OnPageSelection listener;
    private Context context;


    CircleImageView profile_image;
    TextView user_name;
    RecyclerView mRecyclerView;


    public DashboardFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public DashboardFragment(Context context, OnPageSelection listener)
    {
        this.context = context;
        this.listener = listener;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
      // ButterKnife.bind(this, view);

        profile_image = view.findViewById(R.id.profile_image);
        user_name = view.findViewById(R.id.user_name);
        mRecyclerView = view.findViewById(R.id.recycler_view);
      //  profile_image.setOnClickListener();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.addMenuItems();
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }




    private void addMenuItems()
    {
        /**
         * Calling the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /**
         * The number of Columns
         */

        GridLayoutManager manager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        DashboardRecyclerAdapter mAdapter = new DashboardRecyclerAdapter(context);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new DashboardRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {
                listener.onPageSelection(i, "");
            }
        });
    }
public void onClick(View v){

}

}