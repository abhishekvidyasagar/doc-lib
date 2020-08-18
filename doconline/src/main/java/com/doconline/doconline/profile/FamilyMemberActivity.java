package com.doconline.doconline.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.FileViewerActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.MultipartFileUploadClient;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.SingleUploadBroadcastReceiver;
import com.doconline.doconline.model.FamilyMember;
import com.doconline.doconline.model.User;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.doconline.doconline.app.Constants.KEY_AVATAR;
import static com.doconline.doconline.app.Constants.KEY_AVATAR_URL;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DEFAULT_AVATAR;


public class FamilyMemberActivity extends BaseActivity implements View.OnClickListener,
        SingleUploadBroadcastReceiver.Delegate
{
    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();

    public static final int PERMISSION_REQUEST_CODE = 1;

    public static final int HTTP_REQUEST_CODE_SAVE_PROFILE = 1;
    public static final int HTTP_REQUEST_CODE_UPDATE_PROFILE = 2;
    public static final int HTTP_REQUEST_CODE_GET_NAME_PREFIX = 3;
    public static final int HTTP_REQUEST_CODE_GET_PROFILE_AVATAR = 4;
    public static final int HTTP_REQUEST_CODE_REMOVE_PROFILE_AVATAR = 5;


    Toolbar toolbar;

    TextView toolbar_title;

    CoordinatorLayout layout_root_view;

    RelativeLayout layout_refresh;

    RelativeLayout layout_block_ui;

    //@BindView(R.id.profile_image)
    //CircleImageView profile_image;
    //@BindView(R.id.layout_loading)
    //RelativeLayout layout_loading;
    //@BindView(R.id.ib_delete)
    //ImageButton ib_delete;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);
       // ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title= findViewById(R.id.toolbar_title);
        layout_root_view= findViewById(R.id.layout_root_view);
        layout_refresh= findViewById(R.id.layout_refresh);
        layout_block_ui= findViewById(R.id.layout_block_ui);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.addListener();

        toolbar_title.setText(R.string.text_family);
        initFragment((FamilyMember) getIntent().getSerializableExtra("MEMBER"));
    }

    private void addListener()
    {
        //profile_image.setOnClickListener(this);
        //ib_delete.setOnClickListener(this);
    }

    private void initFragment(FamilyMember member)
    {
        Fragment fragment = FragmentManageFamilyMember.newInstance(member);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
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
        int id = view.getId();
        if (id == R.id.profile_image) {/*if(CheckingPermissionIsEnabledOrNot())
                {
                    image_picker_dialog();
                }

                else
                {
                    RequestMultiplePermission();
                }*/
        } else if (id == R.id.ib_delete) {
            if (new InternetConnectionDetector(this).isConnected()) {
                //layout_loading.setVisibility(View.VISIBLE);
                //OkHttpHandler handler = new OkHttpHandler(MyApplication.HTTPMethod.DELETE.getValue());
                //handler.execute(mController.getProfileURL() + KEY_AVATAR);
            } else {
                Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void image_picker_dialog()
    {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");

        if(!mController.getSession().getAvatarLink().toLowerCase().contains(KEY_DEFAULT_AVATAR))
        {
            String[] pictureDialogItems = { "View Profile Picture", "Pick from Gallery", "Capture from Camera" };
            pictureDialog.setItems(pictureDialogItems,

                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case 0:

                                    Intent intent = new Intent(FamilyMemberActivity.this, FileViewerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("URL", mController.getSession().getAvatarLink());
                                    startActivity(intent);
                                    break;

                                case 1:

                                    viewMultipleImageChooser();
                                    break;

                                case 2:

                                    openCamera();
                                    break;
                            }
                        }
                    });
        }

        else
        {
            String[] pictureDialogItems = { "Pick from Gallery", "Capture from Camera" };
            pictureDialog.setItems(pictureDialogItems,

                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case 0:

                                    viewMultipleImageChooser();
                                    break;

                                case 1:
                                    openCamera();
                                    break;
                            }
                        }
                    });
        }

        pictureDialog.show();
    }


    private void openCamera()
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), com.doconline.doconline.app.Constants.MEDIA_DIRECTORY_NAME);

        if(!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdir();
        }

        /**
         * Check if we're running on Android 5.0 or higher
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            imageUri = FileProvider.getUriForFile(FamilyMemberActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }

        else
        {
            imageUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }


        try
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE);
        }

        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Failed to Open Camera", Toast.LENGTH_LONG).show();
        }
    }


    private void viewMultipleImageChooser()
    {
        Intent intent = new Intent(FamilyMemberActivity.this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

            for(Image img: images)
            {
                File file = new File(img.path);
                long length = file.length() / 1024; // Size in KB

                if(length <= 1024 * 5)
                {
                    display_image(img.path);
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Max allowed size 5 MB", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(resultCode == RESULT_OK && requestCode == com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE) {

            File file = new File(imageUri.getPath());
            long length = file.length() / 1024; // Size in KB

            if(length <= 1024 * 5)
            {
                display_image(imageUri.getPath());
            }

            else
            {
                Toast.makeText(getApplicationContext(), "Max allowed size 5 MB", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void display_image(String path)
    {
        if(Helper.fileExist(path))
        {
            if(!new InternetConnectionDetector(FamilyMemberActivity.this).isConnected())
            {
                new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
                return;
            }

            try
            {
                File f = new File(path);
                // bitmap factory
                BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 4;
                final Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), options);
                //profile_image.setImageBitmap(bitmap);
            }

            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "Failed to Set Image", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            finally
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //layout_loading.setVisibility(View.VISIBLE);
                    }
                });

                String uploadId = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(uploadId);

                new MultipartFileUploadClient(uploadId, User.composeAvatarJSON(path), true).execute(mController.getProfileURL() + KEY_AVATAR);
            }
        }

        else
        {
            Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission()
    {
        /**
         * Creating String Array with Permissions.
         */
        ActivityCompat.requestPermissions(FamilyMemberActivity.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0)
                {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && WriteStoragePermission && ReadStoragePermission)
                    {
                        image_picker_dialog();
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    /**
     * Checking permission is enabled or not
     */
    public boolean CheckingPermissionIsEnabledOrNot()
    {
        int CameraPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int WriteStoragePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ReadStoragePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return CameraPermissionResult == PackageManager.PERMISSION_GRANTED &&
                WriteStoragePermissionResult == PackageManager.PERMISSION_GRANTED &&
                ReadStoragePermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    private void load_avatar(final String avatar_url)
    {
        try
        {
            if(!isFinishing())
            {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        if(!avatar_url.isEmpty())
                        {
                            if(avatar_url.toLowerCase().contains(KEY_DEFAULT_AVATAR))
                            {
                                //ib_delete.setVisibility(View.GONE);
                            }

                            else
                            {
                                //ib_delete.setVisibility(View.VISIBLE);
                            }

                            /*if(!avatar_url.isEmpty())
                            {
                                Glide.with(FamilyMemberActivity.this)
                                        .load(avatar_url) // thumbnail url goes here
                                        .placeholder(R.drawable.anonymous)
                                        .dontAnimate()
                                        .override(120, 120)
                                        .centerCrop()
                                        .listener(new RequestListener<String, GlideDrawable>() {

                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
                                            {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                                            {
                                                if(!isFinishing())
                                                {
                                                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.network_image_view);
                                                    profile_image.startAnimation(anim);
                                                }

                                                return false;
                                            }
                                        })
                                        .into(profile_image);
                            }*/
                        }
                    }
                });
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        try
        {
            uploadReceiver.register(this);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
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


    /*@Override
    public void onProgress(int progress)
    {
        Log.d("PROGRESS", "progress = " + progress);
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes)
    {

    }

    @Override
    public void onError(Exception exception)
    {
        if(!isFinishing())
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //ib_delete.setVisibility(View.GONE);
                    //layout_loading.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onCompleted(int responseCode, byte[] serverResponseBody)
    {
        try
        {
            if(responseCode == HttpClient.OK)
            {
                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
            }

            onPostExecute(HTTP_REQUEST_CODE_GET_PROFILE_AVATAR, responseCode, new String(serverResponseBody, "UTF-8"));

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled()
    {
        if(!isFinishing())
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //layout_loading.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Upload Cancelled", Toast.LENGTH_LONG).show();
                }
            });
        }
    }*/


    @Override
    public void onProgress(final UploadInfo uploadInfo)
    {
        Log.i("PROGRESS", "progress = " + uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(final UploadInfo uploadInfo, final ServerResponse serverResponse, final Exception exception)
    {
        if(!isFinishing())
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //ib_delete.setVisibility(View.GONE);
                    //layout_loading.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public  void onCompleted(final UploadInfo uploadInfo, final ServerResponse serverResponse)
    {
        try
        {
            if(serverResponse.getHttpCode() == HttpClient.OK)
            {
                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
            }

            onPostExecute(HTTP_REQUEST_CODE_GET_PROFILE_AVATAR, serverResponse.getHttpCode(), serverResponse.getBodyAsString());

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled(final UploadInfo uploadInfo)
    {
        if(!isFinishing())
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //layout_loading.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Upload Cancelled", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    @Override
    public void onPositiveAction()
    {
        finish();
    }

    @Override
    public void onNegativeAction()
    {
        finish();
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
        if(isFinishing())
        {
            return;
        }

        try
        {
            if(requestCode == HTTP_REQUEST_CODE_GET_PROFILE_AVATAR && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    json = new JSONObject(json.getString(KEY_DATA));

                    final String avatar_url = (json.isNull(KEY_AVATAR_URL)) ? "" : json.getString(KEY_AVATAR_URL);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run()
                        {
                            load_avatar(avatar_url);
                        }
                    });
                }

                catch(JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            if(requestCode == HTTP_REQUEST_CODE_REMOVE_PROFILE_AVATAR && responseCode == HttpClient.NO_RESPONSE)
            {
                mController.getSession().putAvatarLink("");

                new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_AVATAR, MyApplication.HTTPMethod.GET.getValue(), this)
                        .execute(mController.getProfileURL() + KEY_AVATAR);

                Toast.makeText(getApplicationContext(), "Removed Successfully", Toast.LENGTH_LONG).show();

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
}