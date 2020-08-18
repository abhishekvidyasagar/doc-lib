package com.doconline.doconline.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.SingleUploadBroadcastReceiver;
import com.doconline.doconline.model.Address;
import com.doconline.doconline.model.FamilyMember;
import com.doconline.doconline.model.User;
import com.doconline.doconline.utils.FileUtils;
import com.google.android.material.tabs.TabLayout;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.doconline.doconline.app.Constants.KEY_AVATAR;
import static com.doconline.doconline.app.Constants.KEY_AVATAR_URL;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DEFAULT_AVATAR;


public class ProfileActivity extends BaseActivity implements View.OnClickListener,
        SingleUploadBroadcastReceiver.Delegate

{
    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();

    public static final int PERMISSION_REQUEST_CODE = 1;


    Toolbar toolbar;

    ImageButton ib_browse;

    TabLayout tabLayout;

    RelativeLayout layout_loading;

    CircleImageView profile_image;

    CoordinatorLayout layout_root_view;

    ViewPager mViewPager;

    private Uri imageUri;

    private static List<Address> country_list = new ArrayList<>();

    public static final int HTTP_REQUEST_CODE_GET_PROFILE = 1;
    public static final int HTTP_REQUEST_CODE_UPDATE_PROFILE = 2;
    public static final int HTTP_REQUEST_CODE_GET_NAME_PREFIX = 3;
    public static final int HTTP_REQUEST_CODE_GET_COUNTRY_LIST = 4;
    public static final int HTTP_REQUEST_CODE_GET_FAMILY_MEMBERS = 5;
    public static final int HTTP_REQUEST_CODE_REMOVE_FAMILY_MEMBERS = 6;
    public static final int HTTP_REQUEST_CODE_UPDATE_PASSWORD = 7;
    public static final int HTTP_REQUEST_CODE_GET_PROFILE_AVATAR = 8;
    public static final int HTTP_REQUEST_CODE_REMOVE_PROFILE_AVATAR = 9;
    public static final int HTTP_REQUEST_CODE_REMOVE_ALLERGY = 10;
    public static final int HTTP_REQUEST_CODE_GET_CONSENT_STATUS = 11;
    public static final int HTTP_REQUEST_CODE_UPDATE_CONSENT_STATUS = 12;
    public static final int HTTP_REQUEST_CODE_SEND_ACTIVATION_LINK = 13;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       // ButterKnife.bind(this);


        toolbar= findViewById(R.id.toolbar);
        ib_browse= findViewById(R.id.ib_browse);
        tabLayout= findViewById(R.id.tabs);
        layout_loading= findViewById(R.id.layout_loading);
        profile_image= findViewById(R.id.profile_image);
        layout_root_view= findViewById(R.id.layout_root_view);
        mViewPager= findViewById(R.id.pager);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.addListener();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        setupViewPager(mViewPager);

        tabLayout.setupWithViewPager(mViewPager, true);

        // load avatar url.
        this.load_avatar(mController.getSession().getAvatarLink());

        mViewPager.setCurrentItem(getIntent().getIntExtra("PAGE", 0), true);

        if (!new InternetConnectionDetector(this).isConnected())
        {
            new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
            return;
        }

        init_country_list(mController.getCountryList());
        // To get country list, needed in Contact.
        new HttpClient(HTTP_REQUEST_CODE_GET_COUNTRY_LIST, MyApplication.HTTPMethod.GET.getValue(), this).execute(mController.getCountryURL());

        if(getIntent().getBooleanExtra("AUTO_UPDATE_CONSENT", false))
        {
            /**
             * Update Consent
             */
            String json_data = FamilyMember.composeBookingConsent(false);

            new HttpClient(HTTP_REQUEST_CODE_UPDATE_CONSENT_STATUS, MyApplication.HTTPMethod.PATCH.getValue(), true, json_data, this).
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getConsentURL());
        }
    }


    private void addListener()
    {
        profile_image.setOnClickListener(this);
        ib_browse.setOnClickListener(this);
    }


    public static void start(final Activity activity)
    {
        activity.startActivity(new Intent(activity, ProfileActivity.class));
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


    private void displayOptions()
    {
        if(CheckingPermissionIsEnabledOrNot())
        {
            image_picker_dialog();
        }

        else
        {
            RequestMultiplePermission();
        }
    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.profile_image) {
            this.displayOptions();
        } else if (id == R.id.ib_browse) {
            this.displayOptions();
        }
    }


    private void image_picker_dialog()
    {
        new MaterialDialog.Builder(this)
            .title(R.string.dialog_title_file_picker)
            .items(!mController.getSession().getAvatarLink().toLowerCase().contains(KEY_DEFAULT_AVATAR) ? R.array.image_picker_with_view_and_remove_options : R.array.image_picker_options)
            .itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                    switch (which)
                    {
                        case 0:

                            viewMultipleImageChooser();
                            break;

                        case 1:

                            openCamera();
                            break;

                        case 2:
                            // to see profile image
                            Intent intent = new Intent(ProfileActivity.this, FileViewerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("FILE", new FileUtils(mController.getSession().getAvatarLink(), ""));
                            startActivity(intent);
                            break;

                        case 3:
                            // to remove profile image
                            if(new InternetConnectionDetector(getApplicationContext()).isConnected())
                            {
                                layout_loading.setVisibility(View.VISIBLE);
                                new HttpClient(HTTP_REQUEST_CODE_REMOVE_PROFILE_AVATAR, MyApplication.HTTPMethod.DELETE.getValue(), ProfileActivity.this)
                                        .execute(mController.getProfileURL() + KEY_AVATAR);
                            }

                            else

                            {
                                Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
                            }

                            break;
                    }
                }
            })
            .show();
    }


    // take image with camera
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
            imageUri = FileProvider.getUriForFile(ProfileActivity.this,
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

    // take image from gallery
    private void viewMultipleImageChooser()
    {
        Intent intent = new Intent(ProfileActivity.this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // result from gallery
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

            for(Image img: images)
            {
                File file = new File(img.path);
                long length = file.length() / 1024; // Size in KB

                if(length <= 1024 * mController.getSession().getMaxFileSize())
                {
                    display_image(img.path);
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // result from camera image
        if(resultCode == RESULT_OK && requestCode == com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE) {

            File file = new File(imageUri.getPath());
            long length = file.length() / 1024; // Size in KB

            if(length <= 1024 * mController.getSession().getMaxFileSize() )
            {
                display_image(imageUri.getPath());
            }

            else
            {
                Toast.makeText(getApplicationContext(), "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // display image from the URI.
    private void display_image(String path)
    {
        if(Helper.fileExist(path))
        {
            if(!new InternetConnectionDetector(ProfileActivity.this).isConnected())
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
                profile_image.setImageBitmap(bitmap);
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
                        layout_loading.setVisibility(View.VISIBLE);
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

    // setting toolbar
    private void setupViewPager(ViewPager viewPager)
    {

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(FragmentBasicInfo.newInstance(), "Personal");
        adapter.addFrag(FragmentContactInfo.newInstance(), "Contact");
        adapter.addFrag(FragmentDisplayFamily.newInstance(), "Family");
        adapter.addFrag(FragmentHealthProfile.newInstance(), "Health");

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
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
        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]
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

    // set the avatar received from api.
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
                        if (!avatar_url.isEmpty())
                        {
                            ImageLoader.load(getApplicationContext(), avatar_url, profile_image, R.drawable.ic_avatar, 150, 150);
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
    protected void onResume()
    {
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
            Log.i("uploadInfo", "" + serverResponse.getHttpCode());
            Log.i("uploadInfo", "" + serverResponse.getBodyAsString());

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    layout_loading.setVisibility(View.GONE);
                    onPostExecute(HTTP_REQUEST_CODE_GET_PROFILE_AVATAR, serverResponse.getHttpCode(), serverResponse.getBodyAsString());
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

            Log.i("uploadInfo", "" + serverResponse.getHttpCode());
            Log.i("uploadInfo", "" + serverResponse.getBodyAsString());

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
                    layout_loading.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Upload Cancelled", Toast.LENGTH_LONG).show();
                }
            });
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
            if(requestCode == HTTP_REQUEST_CODE_GET_COUNTRY_LIST && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED))
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    mController.getSession().putCountryList(json.getString(KEY_DATA));

                    init_country_list(json.getString(KEY_DATA));
                }

                catch(JSONException e)
                {
                    e.printStackTrace();
                }

                return;
            }

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
                            if(!mController.getSession().getAvatarLink().equals(avatar_url))
                            {
                                mController.getSession().putAvatarLink(avatar_url);
                                // set the avatar received from api.
                                load_avatar(avatar_url);
                            }
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

                // get avatar url from api.
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
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    layout_loading.setVisibility(View.GONE);
                }
            });
        }
    }


    public static List<Address> init_country_list(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);

            Iterator<String> keys= json.keys();

            country_list.clear();

            while (keys.hasNext())
            {
                String key = keys.next();
                String country = json.getString(key);
                country_list.add(new Address(country, key));
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return country_list;
    }
}