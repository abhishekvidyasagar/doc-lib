package com.doconline.doconline.ehr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.MultipleImageViewerActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.adapter.ImageWithCaptionRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.MultipartFileUploadClient;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.OnItemDeleted;
import com.doconline.doconline.helper.SingleUploadBroadcastReceiver;
import com.doconline.doconline.model.AppointmentSummery;
import com.doconline.doconline.utils.DocumentUtils;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.json.JSONObject;

import java.util.UUID;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_DISCARD_FILE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;


public class FileUploadActivity extends BaseActivity implements SingleUploadBroadcastReceiver.Delegate, OnItemDeleted
{
    RelativeLayout layout_refresh;
    RelativeLayout layout_block_ui;
    TextView toolbar_title;
    Toolbar toolbar;
    Button btnDone;
    CoordinatorLayout layout_root_view;
    RecyclerView recycler_view;

    private ImageWithCaptionRecyclerAdapter mAdapter;
    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();
    private static final int HTTP_REQUEST_CODE_FILE_ATTACH = 1;

    private int categoryId;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
       // ButterKnife.bind(this);
        layout_refresh =  findViewById(R.id.layout_refresh);
        layout_block_ui = findViewById(R.id.layout_block_ui);
        toolbar_title =  findViewById(R.id. toolbar_title);
        toolbar=  findViewById(R.id.toolbar );
        btnDone=  findViewById(R.id.btnDone );
       layout_root_view=  findViewById(R.id.layout_root_view );

        recycler_view=  findViewById(R.id.recycler_view );
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnDone.setText(getResources().getString(R.string.Upload));

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new InternetConnectionDetector(FileUploadActivity.this).isConnected())
                {
                    Toast.makeText(getApplicationContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mController.bookingSummery.getFiles().size() != 0)
                {
                    layout_block_ui.setVisibility(View.VISIBLE);
                    layout_refresh.setVisibility(View.VISIBLE);

                    String UPLOAD_URL = mController.getFileURL();
                    String uploadId = UUID.randomUUID().toString();
                    // to read status of upload(failed/pass) in app(5 methods are overridden).
                    uploadReceiver.setDelegate(FileUploadActivity.this);
                    uploadReceiver.setUploadID(uploadId);

                    //uploads data on server and shows notifications(isNotify).
                    new MultipartFileUploadClient(uploadId, categoryId, AppointmentSummery.composeAttachmentJSON(mController.bookingSummery.getFiles()), AppointmentSummery.composeCaptionJSON(mController.bookingSummery.getFiles()), true)
                            .execute(UPLOAD_URL);
                }
            }
        });

        this.categoryId = getIntent().getIntExtra("CATEGORY", 0);
        toolbar_title.setText(DocumentUtils.getCategoryName(mController.docCategoryList, this.categoryId));

        this.initAdapter();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        /**
         * Reinitialize appointment summary class
         */
        mController.bookingSummery = new AppointmentSummery();
    }

   /* @OnClick(R.id.btnDone)
    public void onClick()
    {

    }*/


    @Override
    public void onResume()
    {
        super.onResume();

        try
        {
            uploadReceiver.register(this);

            if(mAdapter == null)
            {
                return;
            }

            mAdapter.notifyDataSetChanged();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        try
        {
            uploadReceiver.unregister(this);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgress(UploadInfo uploadInfo) {

        Log.i("FILE_INFO", "PROGRESS");
    }

    @Override
    public void onError(UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

        if(isFinishing())
        {
            return;
        }

        Log.i("uploadInfo", "" + serverResponse.getHttpCode());
        Log.i("uploadInfo", "" + serverResponse.getBodyAsString());

        onPostExecute(HTTP_REQUEST_CODE_FILE_ATTACH, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
    }

    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse)
    {
        try
        {
            Log.i("uploadInfo", "" + serverResponse.getHttpCode());
            Log.i("uploadInfo", "" + serverResponse.getBodyAsString());
            onPostExecute(HTTP_REQUEST_CODE_FILE_ATTACH, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled(UploadInfo uploadInfo) {

        onPostExecute(0, 0, "Cancelled");
    }

    // on back, whether to save data or not.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                this.discard("Discard Files ?", "Files are not uploaded. Do you want to discard.");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void discard(String title, String content)
    {
        if(mController.bookingSummery.getFiles().size() == 0)
        {
            finish();
        }

        else
        {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_DISCARD_FILE, this).
                    showDialogWithAction(title, content, getResources().getString(R.string.Yes),
                            getResources().getString(R.string.No), true);
        }
    }

    @Override
    public void onBackPressed()
    {
        this.discard("Discard Files ?", "Files are not uploaded. Do you want to discard.");
    }

    @Override
    public void onPositiveAction(int requestCode)
    {
        if(requestCode == DIALOG_REQUEST_CODE_DISCARD_FILE)
        {
            mController.bookingSummery.getFiles().clear();
            finish();
        }
    }

    @Override
    public void onPreExecute()
    {

    }

    private void killActivity()
    {
        Intent intent = new Intent();
        setResult(200, intent);
        finish();
    }

    @Override
    public void onPostExecute(int requestCode, final int responseCode, String response)
    {
        if (isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_FILE_ATTACH && responseCode == HttpClient.CREATED)
            {
                JSONObject json = new JSONObject(response);
                String message = json.getString(KEY_MESSAGE);

                mController.bookingSummery.getFiles().clear();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                killActivity();
                return;
            }

            new HttpResponseHandler(getApplicationContext(), this, layout_root_view).handle(responseCode, response);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            layout_block_ui.setVisibility(View.GONE);
            layout_refresh.setVisibility(View.INVISIBLE);
        }
    }


    private void initAdapter()
    {
        if(mAdapter != null)
        {
            return;
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);

        mAdapter = new ImageWithCaptionRecyclerAdapter(this, this, mController.getAppointmentBookingSummery().getFiles(), true, false);
        recycler_view.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new ImageWithCaptionRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i)
            {
                Intent intent = new Intent(FileUploadActivity.this, MultipleImageViewerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("POSITION", i);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemDeleted(int index, int item_id)
    {
        if(mController.getAppointmentBookingSummery().getFiles().size() == 1)
        {
            Toast.makeText(getApplicationContext(), "All files can't be remove", Toast.LENGTH_SHORT).show();
            return;
        }

        mController.getAppointmentBookingSummery().getFiles().remove(index);
        Toast.makeText(getApplicationContext(), "File Removed", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }
}