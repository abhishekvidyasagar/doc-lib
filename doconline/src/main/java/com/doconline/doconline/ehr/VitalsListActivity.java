package com.doconline.doconline.ehr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.VitalsReportRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.ehr.model.Vital;
import com.doconline.doconline.ehr.model.VitalResponse;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.OnItemUpdated;
import com.doconline.doconline.helper.OnLoadMoreListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DELETE_VITAL;
import static com.doconline.doconline.app.Constants.KEY_DATA;


public class VitalsListActivity extends BaseActivity implements OnItemDeleted, OnItemUpdated
{

    Toolbar toolbar;
    TextView toolbar_title;
    CoordinatorLayout layout_root_view;
    RelativeLayout layout_refresh;
    RelativeLayout layout_block_ui;
    RecyclerView recycler_view;
    LinearLayout layout_empty;
    TextView empty_message;


    private VitalsReportRecyclerAdapter mAdapter;
    private List<Vital> vitalList = new ArrayList<>();

    private int index;

    private static final int HTTP_REQUEST_CODE_GET_VITAL = 1;
    private static final int HTTP_REQUEST_CODE_DELETE_VITAL = 2;

    private boolean isLoading;
    private int CURRENT_PAGE;
    private int NEXT_PAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals_list);
       // ButterKnife.bind(this);
        toolbar=findViewById(R.id.toolbar);
        toolbar_title=findViewById(R.id.toolbar_title);
        layout_root_view=findViewById(R.id.layout_root_view);
        layout_refresh=findViewById(R.id.layout_refresh);
        layout_block_ui=findViewById(R.id.layout_block_ui);
        recycler_view=findViewById(R.id.recycler_view);
        layout_empty=findViewById(R.id.layout_empty);
        empty_message=findViewById(R.id.empty_message);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(getResources().getString(R.string.text_vitals));
        empty_message.setText("No Records Found");

        this.initAdapter();
    }


    private void initAdapter()
    {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        mAdapter = new VitalsReportRecyclerAdapter(vitalList, this, this);
        recycler_view.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new VitalsReportRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent = new Intent(VitalsListActivity.this, VitalsActivity.class);
                intent.putExtra("VITAL", vitalList.get(position));
                startActivity(intent);
            }
        });

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore()
            {
                Log.i("onLoadMore", "Load More");

                if(!new InternetConnectionDetector(getApplicationContext()).isConnected())
                {
                    Toast.makeText(getApplicationContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                isLoading = true;

                Handler handler = new Handler();
                final Runnable r = new Runnable()
                {
                    public void run()
                    {
                        vitalList.add(null);
                        mAdapter.notifyItemInserted(vitalList.size() - 1);
                    }
                };

                handler.post(r);

                new HttpClient(HTTP_REQUEST_CODE_GET_VITAL, MyApplication.HTTPMethod.GET.getValue(), VitalsListActivity.this)
                        .execute(mController.getVitalURL() + "?page=" + (CURRENT_PAGE + 1));
            }
        });

        this.load_more_on_scroll();
    }


    private void load_more_on_scroll()
    {
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) recycler_view.getLayoutManager();

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                {
                    Log.i("onScrolled", "onScrolled");

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    Log.i("onScrolled", "" + visibleItemCount + "-" + totalItemCount + "-" + pastVisibleItems);

                    if (pastVisibleItems + visibleItemCount >= totalItemCount)
                    {
                        if(!isLoading && NEXT_PAGE != 0)
                        {
                            if (mAdapter.mOnLoadMoreListener != null)
                            {
                                mAdapter.mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }


    public void setLoaded()
    {
        isLoading = false;
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        try
        {
            this.syncData();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void syncData()
    {
        if (new InternetConnectionDetector(this).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_VITAL, MyApplication.HTTPMethod.GET.getValue(),this)
                    .execute(mController.getVitalURL());
        }

        else
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_vital, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.action_add) {
            Intent intent = new Intent(VitalsListActivity.this, VitalsManagementActivity.class);
            startActivityForResult(intent, 100);
        } else if (itemId == R.id.action_info) {// shows image with from given URL.
            Intent intent1 = new Intent(VitalsListActivity.this, VitalInfoActivity.class);
            intent1.putExtra("URL", Constants.VITAL_INFO_URL);
            startActivity(intent1);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode == 100 && resultCode == 100)
        {
            this.syncData();
        }*/
    }

    @Override
    public void onPreExecute()
    {
        if(isFinishing())
        {
            return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_refresh.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if(isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_GET_VITAL && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    VitalResponse ehrResponse = new Gson().fromJson(json.toString(), VitalResponse.class);

                    if(!isLoading)
                    {
                        this.vitalList.clear();
                    }

                    if(isLoading)
                    {
                        vitalList.remove(vitalList.size() - 1);
                        mAdapter.notifyItemRemoved(vitalList.size());
                        setLoaded();
                    }

                    CURRENT_PAGE = ehrResponse.getCurrentPage();
                    NEXT_PAGE = ehrResponse.getNextPage();

                    this.vitalList.addAll(ehrResponse.getData());
                    this.mAdapter.notifyDataSetChanged();
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return;
            }

            // delete item from the list.
            if(requestCode == HTTP_REQUEST_CODE_DELETE_VITAL && responseCode == HttpClient.NO_RESPONSE)
            {
                vitalList.remove(index);
                mAdapter.notifyItemRemoved(index);
                mAdapter.notifyItemRangeChanged(index, this.vitalList.size());

                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                return;
            }

            new HttpResponseHandler(this, this, layout_root_view).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    layout_refresh.setVisibility(View.INVISIBLE);
                    layout_block_ui.setVisibility(View.GONE);

                    if(vitalList.size() != 0)
                    {
                        layout_empty.setVisibility(View.GONE);
                    }

                    else
                    {
                        layout_empty.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    @Override
    public void onItemDeleted(int index, int item_id)
    {
        this.index = index;
        this.delete_confirmation();
    }


    @Override
    public void onItemUpdated(int index, int item_id)
    {
        this.index = index;

        Intent intent = new Intent(VitalsListActivity.this, VitalsManagementActivity.class);
        // sending vital object
        intent.putExtra("VITAL", vitalList.get(index));
        startActivityForResult(intent, 100);
    }

    private void delete_confirmation()
    {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_DELETE_VITAL, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_are_you_sure),
                        getResources().getString(R.string.dialog_content_delete_warning),
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), true);
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(new InternetConnectionDetector(this).isConnected())
        {
            if(requestCode == DIALOG_REQUEST_CODE_DELETE_VITAL)
            {
                layout_block_ui.setVisibility(View.VISIBLE);

                new HttpClient(HTTP_REQUEST_CODE_DELETE_VITAL, MyApplication.HTTPMethod.DELETE.getValue(),this)
                        .execute(mController.getVitalURL() + "/" + vitalList.get(index).getId());
            }
        }

        else
        {
            new CustomAlertDialog(this, this, layout_root_view).snackbarForInternetConnectivity();
        }
    }


    @Override
    public void onNegativeAction()
    {

    }
}