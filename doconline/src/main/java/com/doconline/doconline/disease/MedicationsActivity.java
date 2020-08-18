package com.doconline.doconline.disease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.MedicationRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.OnMedicationAction;
import com.doconline.doconline.model.Medication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DELETE_MEDICATION;
import static com.doconline.doconline.app.Constants.KEY_DATA;


public class MedicationsActivity extends BaseActivity implements View.OnClickListener, OnMedicationAction
{
    private static final int HTTP_REQUEST_CODE_GET_MEDICATIONS = 1;
    private static final int HTTP_REQUEST_CODE_DELETE_MEDICATION = 2;

    private int index = -1;

    private MedicationRecyclerAdapter mAdapter;
    private List<Medication> mMedication;


    CoordinatorLayout layout_root_view;

    Button btnProceed;

    RelativeLayout layout_refresh;

    RelativeLayout layout_block_ui;

    TextView toolbar_title;

    Toolbar toolbar;

    LinearLayout layout_empty;

    TextView empty_message;

    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications);
       // ButterKnife.bind(this);


         layout_root_view =  findViewById(R.id.layout_root_view);
         btnProceed=  findViewById(R.id.btnDone);
        layout_refresh=  findViewById(R.id.layout_refresh);
         layout_block_ui=  findViewById(R.id.layout_block_ui);
        toolbar_title=  findViewById(R.id.toolbar_title);
         toolbar=  findViewById(R.id.toolbar);
         layout_empty=  findViewById(R.id.layout_empty);
         empty_message=  findViewById(R.id.empty_message);
        mRecyclerView=  findViewById(R.id.recycler_view);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(getResources().getString(R.string.text_medications));

        display_medications();

        btnProceed.setText("+ Add Medicine");

        if(new InternetConnectionDetector(this).isConnected())
        {
            new HttpClient(HTTP_REQUEST_CODE_GET_MEDICATIONS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .execute(mController.getHealthProfileURL());
        }
    }

    private void display_medications()
    {
        this.mMedication = mController.getSQLiteHelper().getAllMedications();

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MedicationRecyclerAdapter(getApplicationContext(), this, this.mMedication);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new MedicationRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {

            }
        });

        /**
         * Hide and Show Fab button on scroll
         */
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                {
                    if (btnProceed.isShown())
                    {
                        fab.hide();
                    }
                }

                else if (dy < 0)
                {
                    if (!fab.isShown())
                    {
                        fab.show();
                    }
                }
            }
        });*/

        this.adapter_refresh();
    }


    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, MedicationsActivity.class));
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


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btnDone) {
            Intent intent = new Intent(MedicationsActivity.this, MedicationManagementActivity.class);
            startActivityForResult(intent, 200);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == 100)
        {
            Medication medication = (Medication) data.getSerializableExtra("MEDICATION");
            this.mMedication.set(index, medication);
            this.adapter_refresh();
        }

        if(requestCode == 200 && resultCode == 200)
        {
            Medication medication = (Medication) data.getSerializableExtra("MEDICATION");
            this.mMedication.add(0, medication);
            this.adapter_refresh();
        }

        this.index = -1;
    }


    @Override
    public void onMedicationDeleted(int index, int item_id)
    {
        this.index = index;
        this.delete_confirmation();
    }


    @Override
    public void onMedicationUpdated(int index, int item_id)
    {
        this.index = index;

        Intent intent = new Intent(MedicationsActivity.this, MedicationManagementActivity.class);
        intent.putExtra("MEDICATION", mMedication.get(index));
        startActivityForResult(intent, 100);
    }


    private void delete_confirmation()
    {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_DELETE_MEDICATION, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_are_you_sure),
                        getResources().getString(R.string.dialog_content_delete_warning),
                        getResources().getString(R.string.Delete),
                        getResources().getString(R.string.Cancel), true);
    }


    private void adapter_refresh()
    {
        try
        {
            if(mMedication.size() == 0)
            {
                layout_empty.setVisibility(View.VISIBLE);
                empty_message.setText("No History Found");
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


    private void delete_medication()
    {
        if(isFinishing())
        {
            return;
        }

        try
        {
            mController.getSQLiteHelper().remove(Constants.TABLE_MEDICATIONS, Constants.KEY_ID, String.valueOf(mMedication.get(index).getId()));
            mMedication.remove(index);
            mAdapter.notifyItemRemoved(index);
            mAdapter.notifyItemRangeChanged(index, mMedication.size());

            Toast.makeText(getApplicationContext(), "Medication Removed", Toast.LENGTH_LONG).show();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            index = -1;

            runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    if(mMedication.size() == 0)
                    {
                        layout_empty.setVisibility(View.VISIBLE);
                        empty_message.setText("No History Found");
                    }

                    else
                    {
                        layout_empty.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    private void health_profile_data(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);

            if(json.has(Constants.KEY_MEDICATIONS))
            {
                JSONArray array = new JSONArray(json.getString(Constants.KEY_MEDICATIONS));

                List<Medication> temp = new ArrayList<>();
                temp.addAll(mMedication);

                this.mController.getSQLiteHelper().remove_all(Constants.TABLE_MEDICATIONS);
                this.mMedication.clear();

                for(int i=0; i<array.length(); i++)
                {
                    JSONObject obj = array.getJSONObject(i);

                    int id = obj.getInt(Constants.KEY_ID);
                    String name = obj.getString(Constants.KEY_NAME);
                    String intake_time = (obj.isNull(Constants.KEY_INTAKE_TIME)) ? "" : obj.getString(Constants.KEY_INTAKE_TIME);
                    String from_date = (obj.isNull(Constants.KEY_FROM_DATE)) ? "" : obj.getString(Constants.KEY_FROM_DATE);
                    String to_date = (obj.isNull(Constants.KEY_TO_DATE)) ? "" : obj.getString(Constants.KEY_TO_DATE);

                    String noofdays = (obj.isNull(Constants.KEY_NO_OF_DAYS)) ? "" : obj.getString(Constants.KEY_NO_OF_DAYS);
                    String status = (obj.isNull(Constants.KEY_MEDICATIONS_STATUS)) ? "" : obj.getString(Constants.KEY_MEDICATIONS_STATUS);

                    String notes = (obj.isNull(Constants.KEY_NOTES)) ? "" : obj.getString(Constants.KEY_NOTES);

                    Medication medication = new Medication(id, name, intake_time, from_date, to_date, noofdays, status, notes);

                    for(Medication m: temp)
                    {
                        if(m.getId() == id)
                        {
                            medication.setAlarmStatus(m.getAlarmStatus());
                            break;
                        }
                    }

                    if(this.mController.getSQLiteHelper().insert(medication))
                    {
                        this.mMedication.add(medication);
                    }

                    else
                    {
                        this.mController.getSQLiteHelper().update(medication);
                    }
                }
            }

            else
            {
                JSONObject obj = new JSONObject(json_data);

                int id = obj.getInt(Constants.KEY_ID);
                String name = obj.getString(Constants.KEY_NAME);
                String intake_time = (obj.isNull(Constants.KEY_INTAKE_TIME)) ? "" : obj.getString(Constants.KEY_INTAKE_TIME);
                String from_date = (obj.isNull(Constants.KEY_FROM_DATE)) ? "" : obj.getString(Constants.KEY_FROM_DATE);
                String to_date = (obj.isNull(Constants.KEY_TO_DATE)) ? "" : obj.getString(Constants.KEY_TO_DATE);

                String noofdays = (obj.isNull(Constants.KEY_NO_OF_DAYS)) ? "" : obj.getString(Constants.KEY_NO_OF_DAYS);
                String status = (obj.isNull(Constants.KEY_MEDICATIONS_STATUS)) ? "" : obj.getString(Constants.KEY_MEDICATIONS_STATUS);

                String notes = (obj.isNull(Constants.KEY_NOTES)) ? "" : obj.getString(Constants.KEY_NOTES);

                Medication medication = new Medication(id, name, intake_time, from_date, to_date, noofdays, status, notes);
                this.mController.getSQLiteHelper().update(medication);
                this.mMedication.get(index).setName(name);
                this.index = -1;
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finally
        {
            adapter_refresh();
        }
    }


    @Override
    public void onPositiveAction()
    {

    }

    @Override
    public void onNegativeAction()
    {

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
                layout_block_ui.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if (isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_GET_MEDICATIONS && responseCode == HttpClient.OK)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    this.health_profile_data(json.getString(KEY_DATA));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_DELETE_MEDICATION && responseCode == HttpClient.NO_RESPONSE)
            {
                this.delete_medication();
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
                }
            });
        }
    }


    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_DELETE_MEDICATION)
        {
            if(new InternetConnectionDetector(getApplicationContext()).isConnected())
            {
                String url = mController.getHealthProfileURL() + Constants.KEY_MEDICATION + "/" + mMedication.get(index).getId();
                new HttpClient(HTTP_REQUEST_CODE_DELETE_MEDICATION, MyApplication.HTTPMethod.DELETE.getValue(), MedicationsActivity.this).execute(url);
            }

            else
            {
                new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
            }
        }
    }
}