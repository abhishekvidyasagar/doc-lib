package com.doconline.doconline;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.doconline.doconline.api.FileDownloadClient;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.OnDownloadProgress;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.TouchImageView;
import com.doconline.doconline.utils.FileUtils;


public class FileViewerActivity extends AppCompatActivity
        implements OnDownloadProgress
{

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int HTTP_REQUEST_CODE_DOWNLOAD_FILE = 2;


    Toolbar toolbar;
    TouchImageView image;
    TextView download_percent;
    RelativeLayout layout_download_progress;
    ProgressBar circularProgressBar;
    TextView tv_image_caption;

    private FileUtils fileObj;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer);
        //ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        image = findViewById(R.id.imgFullScreen);
        download_percent = findViewById(R.id.download_percent);
        layout_download_progress = findViewById(R.id.layout_download_progress);
        circularProgressBar = findViewById(R.id.progressBar2);
        tv_image_caption = findViewById(R.id.tv_image_caption);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("");

        this.makeActivityAppearOnLockScreen();

        fileObj = (FileUtils) getIntent().getSerializableExtra("FILE");

        if(!fileObj.getPath().isEmpty())
        {
            // If file is document.
            if (FileUtils.isDocumentFile(fileObj.getPath()))
            {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.file_placeholder);

                ViewGroup.LayoutParams params1 = image.getLayoutParams();

                image.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                image.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                image.setLayoutParams(params1);

                image.setImageBitmap(bitmap);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                download();
            }

            // If file is image
            else
            {
                ImageLoader.loadImageWithZoomOption(getApplicationContext(), fileObj.getPath(), image);
            }
        }

        if(fileObj.getCaption() == null || fileObj.getCaption().isEmpty())
        {
            tv_image_caption.setVisibility(View.GONE);
        }

        else
        {
            tv_image_caption.setVisibility(View.VISIBLE);
            tv_image_caption.setText(fileObj.getCaption());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            download();
        } else if (itemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // to download the file
    private void download()
    {
        if(new InternetConnectionDetector(this).isConnected())
        {
            if(permissionCheckerStorage())
            {
                if(!fileObj.getPath().isEmpty())
                {
                    FileDownloadClient client = new FileDownloadClient(HTTP_REQUEST_CODE_DOWNLOAD_FILE, FileViewerActivity.this);
                    client.execute(fileObj.getPath(), FileUtils.getFileName(fileObj.getPath()));
                }
            }
        }

        else
        {
            Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
        }
    }




    private void requestPermissionStorage(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            makeToast("Storage permission allows us to read or write data onto memory. Please allow in App Settings for read or write data.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }


    private boolean permissionCheckerStorage() {

        if (!checkPermissionStorage())
        {
            requestPermissionStorage();
            return false;
        }

        return true;
    }


    private boolean checkPermissionStorage()
    {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        switch (requestCode)
        {

            case STORAGE_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download();
                    makeToast("Permission Granted");
                } else {
                    makeToast("Permission Denied");
                }

                break;
        }
    }


    private void makeToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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


    @Override
    public void onProgressUpdate(final String... progress) {

        if(isFinishing())
        {
            return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                circularProgressBar.setProgress(Integer.parseInt(progress[0]));
                download_percent.setText(progress[0] + "%");
            }
        });
    }

    @Override
    public void onPreExecute() {

        if(isFinishing())
        {
            return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                layout_download_progress.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

        if(isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_DOWNLOAD_FILE && responseCode == HttpClient.OK)
            {
                circularProgressBar.setProgress(0);
                download_percent.setText(0 + "%");

                Toast.makeText(getApplicationContext(), "Downloaded Successfully", Toast.LENGTH_LONG).show();

                if (FileUtils.isDocumentFile(fileObj.getPath()))
                {
                    FileUtils.openFileBrowser(FileViewerActivity.this, FileUtils.getFileName(fileObj.getPath()) /*new File(fileObj.getPath()).getName()*/);
                }

                return;
            }

            Toast.makeText(getApplicationContext(), "Failed to Download", Toast.LENGTH_LONG).show();
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
                    layout_download_progress.setVisibility(View.GONE);
                }
            });
        }
    }
}