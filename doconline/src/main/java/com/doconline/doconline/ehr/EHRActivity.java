package com.doconline.doconline.ehr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.doconline.doconline.R;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.model.AppointmentSummery;
import com.doconline.doconline.utils.DocumentUtils;
import com.doconline.doconline.utils.FileUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.liveo.searchliveo.SearchLiveo;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.doconline.doconline.app.Constants.FILE_UPLOAD_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DOCUMENT_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_EHR_DOCUMENT_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;


public class EHRActivity extends BaseActivity implements SearchLiveo.OnSearchListener
{
    public static final int PERMISSION_REQUEST_CODE = 1;

    public static final int HTTP_REQUEST_CODE_UPDATE_CONSENT = 1;


    TextView toolbar_title;
    Toolbar toolbar;
    FrameLayout fabLayout;
    View fabBackground;
    FloatingActionButton fabMain;
    CoordinatorLayout layout_root_view;
    LinearLayout layout_dynamic_menu;
    LinearLayout layout_fab_more;
    LinearLayout layout_consent;
    AppCompatCheckBox check_consent;

    ViewPager viewPager;
    TabLayout tabLayout;

    SearchLiveo mSearchLiveo;
    ProgressBar progress;
    EditText mEdtSearch;


    private Uri imageUri;

    private boolean imeActionSearch;
    private boolean fabExpanded;
    private int categoryId;
    private int page_index;

    private MedicalRecordsFragment fragmentFiles;
    private List<DocumentUtils> documentUtilsList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ehr);
       // ButterKnife.bind(this);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fabLayout=findViewById(R.id.fabLayout);
        fabBackground=findViewById(R.id.fabBackground);
        fabMain=findViewById(R.id.fabMain);
        layout_root_view=findViewById(R.id.layout_root_view);
        layout_dynamic_menu=findViewById(R.id.layout_dynamic_menu);
        layout_fab_more=findViewById(R.id.layout_fab_more);
        layout_consent=findViewById(R.id.layout_consent);
        check_consent=findViewById(R.id.check_consent);

        viewPager=findViewById(R.id.viewpager);
        tabLayout=findViewById(R.id.tabs);

        mSearchLiveo=findViewById(R.id.search_liveo);
        progress=findViewById(R.id.progress);
        mEdtSearch=findViewById(R.id.edt_search);


        layout_fab_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSubMenusFab();
                display_all_options();
            }
        });
        layout_consent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_consent.setChecked(!check_consent.isChecked());

                if(!new InternetConnectionDetector(getApplicationContext()).isConnected())
                {
                    Toast.makeText(getApplicationContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                try
                {
                    JSONObject json = new JSONObject();
                    json.put(KEY_DOCUMENT_CONSENT, check_consent.isChecked() ? 1 : 0);

                    new HttpClient(HTTP_REQUEST_CODE_UPDATE_CONSENT, MyApplication.HTTPMethod.PATCH.getValue(), true, json.toString(), EHRActivity.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getEhrDocumentConsentURL());
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });



        // Document categories which comes in FAB (catoegories with deleted_at shouldn't be shown).
        this.documentUtilsList = DocumentUtils.getActiveCategory(mController.docCategoryList);
        Collections.reverse(this.documentUtilsList);

        toolbar_title.setText("Medical Records");
        // viewPager set-up.
        this.initViewPager();
        // add child into FAB
        this.addChildMenu(this.documentUtilsList.size());

        // voice search has been hidden
        mSearchLiveo.with(this).hideVoice().build();
        // on hiding the search, old data comes back
        this.hideSearchListener();

        // doctor should see my documents or not.
        this.checkConsent(mController.getSession().getDocumentConsent());

        // Searching data from list
        this.mEdtSearch.setOnEditorActionListener(onEditorActionListener);
    }


    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        hideKeybord();
                    }
                });

                imeActionSearch = true;

                if(page_index == 0)
                {
                    fragmentFiles.changedSearch(mEdtSearch.getText().toString());
                }

                return true;
            }

            return false;
        }
    };

    private void hideKeybord()
    {
        if (!isFinishing())
        {
            return;
        }

        try
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputMethodManager == null)
            {
                return;
            }

            inputMethodManager.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkConsent(int consent)
    {
        if(consent == 0)
        {
            check_consent.setChecked(false);
        }

        else
        {
            check_consent.setChecked(true);
        }
    }

    private void initViewPager()
    {
        fragmentFiles = MedicalRecordsFragment.newInstance();

        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position)
            {
                page_index = position;

                if(position == 0)
                {
                    fabLayout.setVisibility(View.VISIBLE);
                    //layout_consent.setVisibility(View.VISIBLE);
                }

                else
                {
                    fabLayout.setVisibility(View.GONE);
                    //layout_consent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(fragmentFiles, "Medical Records");
        adapter.addFrag(PrescriptionsFragment.newInstance(), "Prescriptions");

        viewPager.setAdapter(adapter);
    }


    private static class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
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


        void addFrag(Fragment fragment, String title)
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


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        /**
         * Reinitialize appointment summary class
         */
        mController.bookingSummery = new AppointmentSummery();

        /**
         * Clear prescription list
         */
        mController.prescriptionList.clear();
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
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

            /*case R.id.action_search:
            {
                mSearchLiveo.show();
                fragmentFiles.showSearch();
                break;
            }*/
        }

        return super.onOptionsItemSelected(item);
    }


    public void showSearch()
    {
        mSearchLiveo.show();

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
            imageUri = FileProvider.getUriForFile(this,
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
        int limit = (int) mController.getAttachmentLimit();

        Intent intent = new Intent(EHRActivity.this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, limit);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }


    private void viewMultipleFileChooser()
    {
        int limit = (int) mController.getAttachmentLimit();

        Intent intent4 = new Intent(EHRActivity.this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, limit);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, com.doconline.doconline.app.Constants.FILE_EXTENSIONS);
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }


    private void image_picker_dialog()
    {
        new MaterialDialog.Builder(this)
                .title("Options (Max " + mController.getSession().getMaxFileSize() + " MB / File)")
                .items(R.array.file_picker_options)
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

                                viewMultipleFileChooser();
                                break;
                        }
                    }
                })
                .show();
    }


    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission()
    {
        /**
         * Creating String Array with Permissions.
         */
        ActivityCompat.requestPermissions(EHRActivity.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0)
                {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadStoragePermission && WriteStoragePermission)
                    {
                        image_picker_dialog();
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(), "Permission Denied",Toast.LENGTH_LONG).show();
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
        int CameraPermissionResult = ContextCompat.checkSelfPermission(this, CAMERA);
        int WriteStoragePermissionResult = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int ReadStoragePermissionResult = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);

        return CameraPermissionResult == PackageManager.PERMISSION_GRANTED &&
                WriteStoragePermissionResult == PackageManager.PERMISSION_GRANTED &&
                ReadStoragePermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    //go to FileUploadActivity
    private void display_image()
    {
        if(mController.getAppointmentBookingSummery().getFiles().size() == 0)
        {
            return;
        }

        Intent intent = new Intent(EHRActivity.this, FileUploadActivity.class);
        intent.putExtra("CATEGORY", categoryId);
        startActivityForResult(intent, FILE_UPLOAD_REQUEST_CODE);
    }

    // get images
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

            for(Image img: images)
            {
                File file = new File(img.path);
                long length = file.length() / 1024; // Size in KB

                if(length <= 1024 * mController.getSession().getMaxFileSize())
                {
                    String fileName = String.valueOf(img.name);
                    int maxLength = (fileName.length() < 30) ? fileName.length() : 30;
                    fileName = fileName.substring(0, maxLength);

                    mController.getAppointmentBookingSummery().addFile(new FileUtils(img.path, fileName));
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                }
            }

            display_image();
            return;
        }

        if(resultCode == RESULT_OK && requestCode == com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE) {

            File file = new File(imageUri.getPath());
            long length = file.length() / 1024; // Size in KB

            if(length <= 1024 * mController.getSession().getMaxFileSize() )
            {
                mController.getAppointmentBookingSummery().addFile(new FileUtils(imageUri.getPath(), ""));
            }

            else
            {
                Toast.makeText(getApplicationContext(), "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
            }

            display_image();
            return;
        }

        if(requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null)
        {
            ArrayList<NormalFile> files = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);

            for(NormalFile file: files)
            {
                File tempFile = new File(file.getPath());
                long length = tempFile.length() / 1024; // Size in KB

                if(length <= 1024 * mController.getSession().getMaxFileSize())
                {
                    String fileName = String.valueOf(file.getName());
                    int maxLength = (fileName.length() < 30) ? fileName.length() : 30;
                    fileName = fileName.substring(0, maxLength);

                    mController.getAppointmentBookingSummery().addFile(new FileUtils(file.getPath(), fileName));
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Max allowed size " + mController.getSession().getMaxFileSize() + " MB", Toast.LENGTH_SHORT).show();
                }
            }

            display_image();
            return;
        }

        if(requestCode == FILE_UPLOAD_REQUEST_CODE && resultCode == 200)
        {
            fragmentFiles.syncData();
        }
    }


   // /*@OnClick(R.id.layout_fab_add)
    public void addClick(View view)
    {
        if(fabExpanded)
        {
            closeSubMenusFab();
        }

        else
        {
            openSubMenusFab(this.documentUtilsList.size());
        }
    }





    // add sub-menu for FAB
    private void addChildMenu(int size)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, 0, 15);

        for(int i=0; i < size && i < 5; i++)
        {
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_fab_menu, null);
            layout.setTag(i);

            layout.setLayoutParams(params);
            layout_dynamic_menu.addView(layout);

            TextView tvTitle = (TextView) (((CardView) layout.getChildAt(0)).getChildAt(0));
            tvTitle.setText(this.documentUtilsList.get(i).getTitle());

            this.addFabListener(layout);
        }
    }

    // FAB clickListener
    private void addFabListener(LinearLayout layout)
    {
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                int index = (int) view.getTag();
                categoryId = documentUtilsList.get(index).getId();

                closeSubMenusFab();
                displayOptions();
            }
        });
    }


    /**
     * Closes FAB submenus
     */
    private void closeSubMenusFab()
    {
        fabBackground.setVisibility(View.GONE);
        layout_fab_more.setVisibility(View.GONE);

        for(int index=0; index< layout_dynamic_menu.getChildCount(); index++)
        {
            LinearLayout layout = (LinearLayout) layout_dynamic_menu.getChildAt(index);
            layout.setVisibility(View.GONE);
        }

        this.rotate(45.0f, 0.0f);
        this.fabExpanded = false;
    }

    /**
     * Opens FAB submenus
     */
    private void openSubMenusFab(int size)
    {
        fabBackground.setVisibility(View.VISIBLE);

        if(size > 5)
        {
            layout_fab_more.setVisibility(View.VISIBLE);
        }

        for(int index=0; index< layout_dynamic_menu.getChildCount(); index++)
        {
            LinearLayout layout = (LinearLayout) layout_dynamic_menu.getChildAt(index);
            layout.setVisibility(View.VISIBLE);
        }

        this.rotate(0.0f, 45.0f);
        this.fabExpanded = true;
    }


    /**
     * Rotate on click
     * @param from_degree
     * @param to_degree
     */
    // FAB Animation
    private void rotate(float from_degree, float to_degree)
    {
        // Create an animation instance
        Animation an = new RotateAnimation(from_degree, to_degree, fabMain.getWidth()/2, fabMain.getHeight()/2);

        // Set the animation's parameters
        an.setDuration(500);        // duration in ms
        an.setRepeatCount(0);       // -1 = infinite repeated
        an.setFillAfter(true);      // keep rotation after animation
        an.setFillEnabled(true);

        // Apply animation to view
        fabMain.setAnimation(an);
        fabMain.startAnimation(an);
    }

    // on document upload selection
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


    private void display_all_options()
    {
        Collections.reverse(this.documentUtilsList);

        List<String> items = new ArrayList<>();

        for(DocumentUtils utils: this.documentUtilsList)
        {
            items.add(utils.getTitle());
        }

        new MaterialDialog.Builder(this)
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        categoryId = documentUtilsList.get(which).getId();

                        closeSubMenusFab();
                        displayOptions();
                    }
                })
                .show();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }*/

    // When closing the search, old data should come back
    private void hideSearchListener()
    {
        mSearchLiveo.hideSearch(new SearchLiveo.OnHideSearchListener() {

            @Override
            public void hideSearch()
            {
                if(page_index == 0)
                {
                    fragmentFiles.hideSearch();
                }
            }
        });
    }

    // When typed in the search bar.
    @Override
    public void changedSearch(CharSequence text)
    {
        if(page_index == 0 && !imeActionSearch)
        {
            fragmentFiles.changedSearch(text);
        }
    }

    public void showSearchProgressBar()
    {
        progress.setVisibility(View.VISIBLE);
    }

    public void hideSearchProgressBar()
    {
        progress.setVisibility(View.GONE);
        imeActionSearch = false;
    }

    public void hideFabBackground()
    {
        fabBackground.setVisibility(View.GONE);
    }

    public void showFabBackground()
    {
        fabBackground.setVisibility(View.VISIBLE);
    }



    @Override
    public void onPreExecute() {


    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response)
    {
        if (isFinishing())
        {
            return;
        }

        if(requestCode == HTTP_REQUEST_CODE_UPDATE_CONSENT && responseCode == HttpClient.OK)
        {
            try
            {
                JSONObject json = new JSONObject(response);

                String message = json.isNull(KEY_MESSAGE) ? "Updated Successfully" : json.getString(KEY_MESSAGE);

                json = json.getJSONObject(KEY_DATA);
                int ehr_document_consent = json.getInt(KEY_EHR_DOCUMENT_CONSENT);
                mController.getSession().putDocumentConsent(ehr_document_consent);

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return;
        }

        new HttpResponseHandler(this, this, layout_root_view).handle(responseCode, response);
    }
}