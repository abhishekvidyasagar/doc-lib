package com.doconline.doconline.consultation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.AppointmentSlotRecyclerAdapter;
import com.doconline.doconline.adapter.CalendarRecyclerAdapter;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnPageSelection;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.doconline.doconline.app.Constants.YYYY_MM_DD;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.service.ConsultationSlotBroadcast.SLOT_ACTION;

/**
 * Created by chiranjit on 14/12/16.
 */
public class AppointmentSlotFragment extends BaseFragment implements View.OnClickListener
{

    private Context context = null;

    private AppointmentSlotRecyclerAdapter sAdapter;
    private CalendarRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    ImageButton btn_next;

    ImageButton btn_prev;

    RecyclerView cRecyclerView;

    RecyclerView sRecyclerView;

    LinearLayout layout_empty;


    private int counter = 0;
    private List<TimeSlot> mTimeSlot = new ArrayList<>();

    private OnTaskCompleted task_listener;
    private OnPageSelection listener;


    public AppointmentSlotFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public AppointmentSlotFragment(Context context, OnTaskCompleted task_listener, OnPageSelection listener)
    {
        this.context = context;
        this.task_listener = task_listener;
        this.listener = listener;
        this.context.registerReceiver(mHandleSlotReceiver, new IntentFilter(SLOT_ACTION));
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_slot, container, false);
      //  ButterKnife.bind(this, view);

        btn_next =  view.findViewById(R.id.btn_next);
        btn_prev = view.findViewById(R.id.btn_prev);
        cRecyclerView = view.findViewById(R.id.calendar_list);
        sRecyclerView = view.findViewById(R.id.slot_list);
        layout_empty = view.findViewById(R.id.layout_empty);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        btn_next.setOnClickListener(this);
        btn_prev.setOnClickListener(this);

        setHasOptionsMenu(true);
    }

    // change date
    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btn_next) {
            if (counter < mController.getCalendarSize() - 1) {
                counter++;
                refresh_slotlist();
            }
        } else if (id == R.id.btn_prev) {
            if (counter > 0) {
                counter--;
                refresh_slotlist();
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null && getView().isShown())
        {
            if (isVisibleToUser)
            {
                refresh_slotlist();
            }
        }
    }


    private void refresh_slotlist()
    {
        if(mController.getCalendarSize() == 0)
        {
            return;
        }

        if(mController.getAppointmentBookingSummery().getBookingType() != Constants.BOOKING_TYPE_SLOT)
        {
            mController.getAppointmentBookingSummery().setAppointmentTime("");
        }

        mLayoutManager.scrollToPosition(counter);
        int count = slot_list(mController.timeSlotList, mController.getCalendar(counter).slot_date);

        if(count > 0)
        {
            layout_empty.setVisibility(View.GONE);
        }

        else
        {
            layout_empty.setVisibility(View.VISIBLE);
        }

        sAdapter.notifyDataSetChanged();
    }


    private void add_calendar_list()
    {
        // Calling the RecyclerView
        cRecyclerView.setHasFixedSize(true);

        // Disable recyclerView scroll
        cRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // The number of Columns
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        cRecyclerView.setLayoutManager(mLayoutManager);

        if(mAdapter == null)
        {
            mAdapter = new CalendarRecyclerAdapter(context);
            cRecyclerView.setAdapter(mAdapter);

            mAdapter.SetOnItemClickListener(new CalendarRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int pos)
                {

                }
            });
        }

        mAdapter.notifyDataSetChanged();

        if(mController.getCalendarSize() != counter)
        {
            add_slot_list(mController.getCalendar(counter).slot_date);
            refresh_slotlist();
        }
    }


    private void add_slot_list(String date)
    {
        int count = slot_list(mController.timeSlotList, date);

        // Calling the RecyclerView
        sRecyclerView.setHasFixedSize(true);

        // The number of Columns
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        sRecyclerView.setLayoutManager(mLayoutManager);

        if(sAdapter == null)
        {
            sAdapter = new AppointmentSlotRecyclerAdapter(context, mTimeSlot, date);
            sRecyclerView.setAdapter(sAdapter);

            sAdapter.SetOnItemClickListener(new AppointmentSlotRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int i)
                {
                    try
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
                        Date value = sdf.parse(mController.getCalendar(counter).slot_date);
                        sdf = new SimpleDateFormat(YYYY_MM_DD);
                        mController.getAppointmentBookingSummery().setAppointmentTime(sdf.format(value) + " " + mTimeSlot.get(i).start_time);
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    listener.onPageSelection(0, "Book a Consultation");
                }
            });
        }

        if(count > 0)
        {
            layout_empty.setVisibility(View.GONE);
        }

        else
        {
            layout_empty.setVisibility(View.VISIBLE);
        }

        sAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_refresh, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_refresh) {
            task_listener.onTaskCompleted(true, 0, "");
        }

        return super.onOptionsItemSelected(item);
    }


    public int slot_list(List<TimeSlot> mSlots, String date)
    {
        mTimeSlot.clear();

        for(TimeSlot ts: mSlots)
        {
            if(ts.slot_date.equalsIgnoreCase(date))
            {
                mTimeSlot.add(ts);
            }
        }

        return mTimeSlot.size();
    }

    /**
     * Receiving waiting time
     * */
    private final BroadcastReceiver mHandleSlotReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {
                add_calendar_list();
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    };
}