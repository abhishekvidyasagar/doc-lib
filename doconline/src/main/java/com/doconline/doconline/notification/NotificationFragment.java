package com.doconline.doconline.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.appointment.AppointmentSummeryActivity;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.dragable.RecyclerItemTouchHelper;
import com.doconline.doconline.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.notification.NotificationActivity.HTTP_REQUEST_CODE_DELETE_ALL_NOTIFICATIONS;
import static com.doconline.doconline.notification.NotificationActivity.HTTP_REQUEST_CODE_DELETE_NOTIFICATION;
import static com.doconline.doconline.notification.NotificationActivity.HTTP_REQUEST_CODE_GET_ALL_NOTIFICATIONS;
import static com.doconline.doconline.notification.NotificationActivity.HTTP_REQUEST_CODE_READ_ALL_NOTIFICATIONS;
import static com.doconline.doconline.notification.NotificationActivity.HTTP_REQUEST_CODE_READ_NOTIFICATION;

/**
 * Created by chiranjit on 31/05/17.
 */
public class NotificationFragment extends BaseFragment implements OnItemDeleted,
        OnDialogAction, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
{
    private NotificationRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    RecyclerView recyclerView;

    SwipeRefreshLayout swipeRefreshLayout;

    LinearLayout layout_empty;

    TextView empty_message;

    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int pageCount = 1;

    private List<NotificationUtils> mNotification;
    private OnHttpResponse listener;


    public static NotificationFragment newInstance()
    {
        return new NotificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
       // ButterKnife.bind(this, view);

        recyclerView = view.findViewById(R.id.list);
         swipeRefreshLayout= view.findViewById(R.id.swipeRefreshLayout);
        layout_empty= view.findViewById(R.id.layout_empty);
        empty_message= view.findViewById(R.id.empty_message);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.setHasOptionsMenu(true);
        this.initAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof OnHttpResponse)
        {
            listener = (OnHttpResponse) getActivity();
        }

        if(new InternetConnectionDetector(getContext()).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_ALL_NOTIFICATIONS, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getNotificationURL());
        }

        else
        {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
        }
    }


    private void initAdapter()
    {
        if(getContext() == null)
        {
            return;
        }

        this.mNotification = this.mController.getSQLiteHelper().getAllNotifications(0, 20);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new NotificationRecyclerAdapter(getContext(), this, mNotification);
        recyclerView.setAdapter(mAdapter);

        // adding custom_adapter_item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);

        mAdapter.SetOnItemClickListener(new NotificationRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {
                NotificationUtils utils = mNotification.get(position);

                if(new InternetConnectionDetector(getContext()).isConnected())
                {
                    String URL = mController.getNotificationURL() + utils.id + "/" + Constants.KEY_READ;
                    new HttpClient(HTTP_REQUEST_CODE_READ_NOTIFICATION, MyApplication.HTTPMethod.PATCH.getValue(), NotificationFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL);
                }

                else
                {
                    Toast.makeText(getContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
                }

                view_notification_details(utils);
            }
        });

        adapter_refresh();
        loadMoreOnScroll();

        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh()
            {
                if(new InternetConnectionDetector(getContext()).isConnected())
                {
                    new HttpClient(HTTP_REQUEST_CODE_GET_ALL_NOTIFICATIONS, MyApplication.HTTPMethod.GET.getValue(), NotificationFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getNotificationURL());
                    swipeRefreshLayout.setRefreshing(true);
                }

                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();

        try
        {
            if(getActivity() == null)
            {
                return;
            }

            NotificationManager nMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

            if(nMgr != null)
            {
                nMgr.cancelAll();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemDeleted(int index, int item_id)
    {

    }


    private void loadMoreOnScroll()
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                loading = true;

                int totalRecord = mController.getSQLiteHelper().dbRowCount(Constants.TABLE_NOTIFICATIONS);
                int totalPage;

                if (totalRecord % 10 == 0)
                {
                    totalPage = (totalRecord / 20);
                }

                else
                {
                    totalPage = (totalRecord / 20) + 1;
                }

                if (pageCount < totalPage)
                {

                    if (dy > 0) //check for scroll down
                    {

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (loading)
                        {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount)
                            {
                                loading = false;
                                Log.v("onScrolled: ", "Last Item Wow ! " + pageCount);

                                List<NotificationUtils> tempList = mController.getSQLiteHelper().getAllNotifications(pageCount * 20, 20);

                                for (NotificationUtils utils: tempList)
                                {
                                    mNotification.add(mNotification.size(), utils);
                                }

                                adapter_refresh();

                                recyclerView.scrollToPosition(totalItemCount - 1);
                                pageCount++;
                            }
                        }
                    }
                }
            }
        });
    }


    private void adapter_refresh()
    {
        try
        {
            if(mNotification.size() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No Notification Found");
            }

            else
            {
                layout_empty.setVisibility(View.GONE);
            }

            mAdapter.notifyDataSetChanged();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_clear, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.action_clear) {
            if (new InternetConnectionDetector(getContext()).isConnected()) {
                if (mNotification.size() != 0) {
                    this.mController.getSession().putBadgeCount(0);
                    this.mController.getSQLiteHelper().remove_all(Constants.TABLE_NOTIFICATIONS);
                    mNotification.clear();
                    this.adapter_refresh();

                    Toast.makeText(getContext(), "Removed all Notifications", Toast.LENGTH_LONG).show();
                    new HttpClient(HTTP_REQUEST_CODE_DELETE_ALL_NOTIFICATIONS, MyApplication.HTTPMethod.DELETE.getValue(), this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getNotificationURL() + Constants.KEY_ALL);
                } else {
                    Toast.makeText(getContext(), "No Notification Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.action_read_all) {
            if (new InternetConnectionDetector(getContext()).isConnected()) {
                if (mNotification.size() != 0) {
                    new HttpClient(HTTP_REQUEST_CODE_READ_ALL_NOTIFICATIONS, MyApplication.HTTPMethod.PATCH.getValue(), this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getNotificationURL() + Constants.KEY_ALL + "/" + Constants.KEY_READ);
                } else {
                    Toast.makeText(getContext(), "No Notification Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position)
    {
        if (viewHolder instanceof NotificationRecyclerAdapter.NotificationViewHolder)
        {
            final int deletedIndex = viewHolder.getAdapterPosition();
            final NotificationUtils deletedItem = mNotification.get(deletedIndex);

            if(new InternetConnectionDetector(getContext()).isConnected())
            {
                new HttpClient(HTTP_REQUEST_CODE_DELETE_NOTIFICATION, MyApplication.HTTPMethod.DELETE.getValue(), this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getNotificationURL() + deletedItem.id);
            }

            else
            {
                Toast.makeText(getContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            }

            if(getView() == null)
            {
                return;
            }

            mAdapter.removeItem(viewHolder.getAdapterPosition());

            if(deletedItem.read_at.isEmpty())
            {
                this.mController.getSession().putBadgeCount(this.mController.getSession().getBadgeCount() - 1);
            }

            this.mController.getSQLiteHelper().remove(Constants.TABLE_NOTIFICATIONS, KEY_ID, String.valueOf(deletedItem.id));

            if(mNotification.size() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No Notification Found");
            }

            else
            {
                layout_empty.setVisibility(View.GONE);
            }

            Toast.makeText(getContext(), "Notification Removed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPreExecute()
    {
        if(!swipeRefreshLayout.isRefreshing())
        {
            listener.onPreExecute();
        }
    }

    @Override
    public void onPostExecute(final int requestCode, final int responseCode, final String response)
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
            if(requestCode == HTTP_REQUEST_CODE_GET_ALL_NOTIFICATIONS
                    && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.json_data(json.getString(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_DELETE_ALL_NOTIFICATIONS && responseCode == HttpClient.NO_RESPONSE)
            {
                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_DELETE_NOTIFICATION && responseCode == HttpClient.NO_RESPONSE)
            {
                return;
            }

            if((requestCode == HTTP_REQUEST_CODE_READ_ALL_NOTIFICATIONS || requestCode == HTTP_REQUEST_CODE_READ_NOTIFICATION) && responseCode == HttpClient.NO_RESPONSE)
            {
                if(new InternetConnectionDetector(getContext()).isConnected())
                {
                    new HttpClient(HTTP_REQUEST_CODE_GET_ALL_NOTIFICATIONS, MyApplication.HTTPMethod.GET.getValue(), this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getNotificationURL());
                }

                return;
            }

            new HttpResponseHandler(getActivity(), this, getView()).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            getActivity().runOnUiThread(new Runnable() {

                public void run()
                {
                    listener.onPostExecute(requestCode, responseCode, response);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }


    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }


    private void json_data(String data)
    {
        int badgeCount = 0;

        try
        {
            JSONArray array = new JSONArray(data);
            JSONObject json;
            this.mController.getSQLiteHelper().remove_all(Constants.TABLE_NOTIFICATIONS);
            this.mNotification.clear();

            for(int index=0; index<array.length(); index++)
            {
                json = array.getJSONObject(index);

                String notification_id = json.getString(KEY_ID);
                String type = (json.isNull(Constants.KEY_TYPE)) ? "" : json.getString(Constants.KEY_TYPE);
                int notifiable_id = (json.isNull(Constants.KEY_NOTIFIABLE_ID)) ? 0 : json.getInt(Constants.KEY_NOTIFIABLE_ID);
                String notifiable_type = (json.isNull(Constants.KEY_NOTIFIABLE_TYPE)) ? "" : json.getString(Constants.KEY_NOTIFIABLE_TYPE);
                String body = (json.isNull(KEY_DATA)) ? "" : json.getString(KEY_DATA);
                String read_at = (json.isNull(Constants.KEY_READ_AT)) ? "" : json.getString(Constants.KEY_READ_AT);
                String created_at = (json.isNull(Constants.KEY_CREATED_AT)) ? "" : json.getString(Constants.KEY_CREATED_AT);
                String updated_at = (json.isNull(Constants.KEY_UPDATED_AT)) ? "" : json.getString(Constants.KEY_UPDATED_AT);

                NotificationUtils notification = new NotificationUtils(notification_id, type, notifiable_id, notifiable_type, body, read_at, created_at, updated_at);
                mNotification.add(notification);
                this.mController.getSQLiteHelper().insert(notification);

                if(read_at.isEmpty())
                {
                    badgeCount ++;
                }
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            this.mController.getSession().putBadgeCount(badgeCount);
            adapter_refresh();
        }
    }


    private void view_notification_details(NotificationUtils utils)
    {
        int y = utils.type.lastIndexOf("\\") + 1;
        String type = utils.type.substring(y);

        if(type.equalsIgnoreCase(Constants.KEY_NOTIFICATION_TYPE_INCOMING_CALL))
        {
            try
            {
                JSONObject json = new JSONObject(utils.body);

                Intent intent = new Intent(getActivity(), AppointmentSummeryActivity.class);
                intent.putExtra("ID", json.getInt(Constants.KEY_APPOINTMENT_ID));
                startActivity(intent);
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}