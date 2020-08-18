package com.doconline.doconline;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.adapter.HomeRecyclerAdapter;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.model.Appointment;
import com.doconline.doconline.service.ProfileUpdateBroadcast;
import com.doconline.doconline.service.TileUpdateBroadcast;
import com.doconline.doconline.subscription.SubscriptionActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.doconline.doconline.app.Constants.TYPE_B2B;
import static com.doconline.doconline.app.Constants.TYPE_B2B_PAID;
import static com.doconline.doconline.app.Constants.TYPE_B2C;
import static com.doconline.doconline.app.Constants.TYPE_ONETIME;
import static com.doconline.doconline.app.Constants.TYPE_PROMO;

/**
 * Created by chiranjitbardhan on 08/01/18.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener
{
    private OnPageSelection listener;
    private Context context;
    private HomeRecyclerAdapter mAdapter;
    private Appointment mAppointment = new Appointment();


    CircleImageView profile_image;
    TextView user_name;
    TextView tv_subscription_details;
    Button btn_subscription;
    LinearLayout layout_subscription;
    RecyclerView mRecyclerView;
    NestedScrollView layout_scroll_view;


    public HomeFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public HomeFragment(Context context, OnPageSelection listener)
    {
        this.context = context;
        this.listener = listener;
        this.context.registerReceiver(mHandleReceiver, new IntentFilter(ProfileUpdateBroadcast.ACTION));
          this.context.registerReceiver(mHandleTileReceiver, new IntentFilter(TileUpdateBroadcast.ACTION));
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profile_image = view.findViewById(R.id.profile_image);
        user_name = view.findViewById(R.id.user_name);
        tv_subscription_details = view.findViewById(R.id.tv_subscription_details);
        btn_subscription = view.findViewById(R.id.btn_subscription);
        layout_subscription = view.findViewById(R.id.layout_subscription);
        mRecyclerView =view.findViewById(R.id.recycler_view);
        layout_scroll_view = view.findViewById(R.id.layout_scroll_view);
      //  ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.addListener();
        this.addMenuItems();

        this.scrollToTop();
    }


    private void addListener()
    {
        btn_subscription.setOnClickListener(this);
        layout_subscription.setOnClickListener(this);
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btn_subscription) {
            startActivity(new Intent(getActivity(), SubscriptionActivity.class));
        } else if (id == R.id.layout_subscription) {
            startActivity(new Intent(getActivity(), SubscriptionActivity.class));
        }
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

        mAdapter = new HomeRecyclerAdapter(context, this.mAppointment);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new HomeRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {
                listener.onPageSelection(i, "");
            }
        });
    }


    private void setUserProfile(final String name, final String avatar_url)
    {
        if(getActivity() == null)
        {
            return;
        }

        if(getActivity().isFinishing())
        {
            return;
        }

        if(getView() == null)
        {
            return;
        }

        try
        {
            if (name.trim().length() != 0)
            {
                user_name.setText(Helper.toCamelCase("Hello " + name.replaceAll("^\\s+", "").replaceAll("\\s+$", "") + "!"));
            }

            else
            {
                user_name.setText("Hello");
            }

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (!avatar_url.isEmpty())
                    {
                        ImageLoader.load(context, avatar_url, profile_image, R.drawable.ic_avatar, 150, 150);
                    }
                }
            });
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Receiving waiting time
     * */
    private final BroadcastReceiver mHandleTileReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {
                Appointment appointment = (Appointment) intent.getSerializableExtra("APPOINTMENT");

                if(appointment.getAppointmentID() != 0)
                {
                    mAppointment.setAppointmentID(appointment.getAppointmentID());
                    mAppointment.setScheduledAt(appointment.getScheduledAt());
                    mAppointment.getDoctor().setFullName(appointment.getDoctor().getFullName());
                    mAppointment.getDoctor().setSpecialization(appointment.getDoctor().getSpecialization());
                }

                mAppointment.setAppointmentStatus(appointment.getAppointmentStatus());

                if(mAdapter != null)
                {
                    mAdapter.notifyItemChanged(1);
                }
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    /**
     * Receiving waiting time
     * */
    private final BroadcastReceiver mHandleReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {
                setSubscriptionDetails();

                if (new InternetConnectionDetector(context).isConnected())
                {
                    setUserProfile(mController.getSession().getFullName(), mController.getSession().getAvatarLink());
                }
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    };


    private void setSubscriptionDetails()
    {
        try
        {
            if(mController.getSession().getSubscriptionStatus())
            {
                tv_subscription_details.setText("You are under DOCONLINE " + mController.getSession().getSubscriptionType().toUpperCase() + " membership");

                if(mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B))
                {
                    tv_subscription_details.setText("You are under DOCONLINE ENTERPRISE membership");
                }

                else if(mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2C))
                {
                    tv_subscription_details.setText("You are under " + mController.getSession().getSubscriptionPlanName().toUpperCase());
                }

                else if(mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_ONETIME)
                        || mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B_PAID))
                {
                    tv_subscription_details.setText("You have 1 consultation credit");
                }

                else if(mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_PROMO) &&
                        !mController.getSession().getTrialEndsDate().isEmpty())
                {
                    String date_data = Helper.getYearMonthDaysDiff(Helper.UTC_to_Local_TimeZone(mController.getSession().getTrialEndsDate()), "");
                    tv_subscription_details.setText(date_data);
                }
            }

            else
            {
                tv_subscription_details.setText("Pick a membership as per your convenience");
            }

            if(!mController.getSession().getSubscriptionStatus())
            {
                btn_subscription.setText("Choose");
                btn_subscription.setVisibility(View.VISIBLE);
            }

            else if(mController.getSession().getSubscriptionStatus() && mController.getSession().getSubscriptionUpgradeStatus())
            {
                btn_subscription.setText("Upgrade");
                btn_subscription.setVisibility(View.VISIBLE);
            }

            else if(mController.getSession().getSubscriptionStatus() && !mController.getSession().getSubscriptionUpgradeStatus())
            {
                btn_subscription.setText("Choose");
                btn_subscription.setVisibility(View.GONE);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void scrollToTop()
    {
        mRecyclerView.post(new Runnable() {

            @Override
            public void run()
            {
                layout_scroll_view.scrollTo(0, 0);
            }
        });
    }
}