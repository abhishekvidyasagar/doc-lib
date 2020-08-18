package com.doconline.doconline.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.models.Image;
import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.FileViewerActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.adapter.MessageRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.api.MultipartFileUploadClient;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.MarqueeTextView;
import com.doconline.doconline.helper.OnDialogAction;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.helper.SingleUploadBroadcastReceiver;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.model.ChatMessage;
import com.doconline.doconline.subscription.SubscriptionActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_CHAT_CONFIRMATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_EMAIL_VERIFICATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_MOBILE_VERIFICATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE;
import static com.doconline.doconline.app.Constants.CHAT_IMAGE_REQUEST_CODE;
import static com.doconline.doconline.app.Constants.DEFAULT_FILE_SIZE;
import static com.doconline.doconline.app.Constants.JSON_TAG;
import static com.doconline.doconline.app.Constants.KEY_AVATAR_URL;
import static com.doconline.doconline.app.Constants.KEY_BODY;
import static com.doconline.doconline.app.Constants.KEY_CDN_PHOTO_URL;
import static com.doconline.doconline.app.Constants.KEY_CREATED_AT;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DOCTOR;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_MCI_CODE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGES;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_PRACTIONER_NO;
import static com.doconline.doconline.app.Constants.KEY_QUALIFICATION;
import static com.doconline.doconline.app.Constants.KEY_RATINGS;
import static com.doconline.doconline.app.Constants.KEY_SENDER_TYPE;
import static com.doconline.doconline.app.Constants.KEY_THREADS;
import static com.doconline.doconline.app.Constants.KEY_THREAD_ID;
import static com.doconline.doconline.app.Constants.KEY_USER;
import static com.doconline.doconline.app.Constants.SUBSCRIPTION_PERMISSION_REQ_CODE;
import static com.doconline.doconline.app.Constants.TYPE_B2B_PAID;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.helper.CustomStatusBar.setStatusBarBackground;


public class FirebaseChatActivity extends AppCompatActivity implements OnHttpResponse, OnTaskCompleted,
        OnDialogAction, SingleUploadBroadcastReceiver.Delegate, View.OnClickListener {
    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();

    public static final int PERMISSION_REQUEST_CODE = 2;

    private List<ChatMessage> mMessages = new ArrayList<>();
    private MessageRecyclerAdapter mAdapter;


    Toolbar toolbar;
    FrameLayout layout_root_view;
    ImageButton buttonSend;
    RecyclerView recyclerView;
    EditText chatText;
    RelativeLayout layout_progress;
    RelativeLayout layout_refresh;
    MarqueeTextView toolbar_action;
    MarqueeTextView toolbar_title;
    FrameLayout layout_thumbnail;
    ImageView iv_thumbnail;
    ImageButton ibCamera;
    CircleImageView doctor_image;
    ImageView doctorInfoImageView;
    LinearLayout doctorInfoLayout;
    CircleImageView docotorAvatarCIV;
    RatingBar ratingBar;
    TextView ratingTextView;
    TextView mciCodeTextView;
    TextView practitionerNoTextView;
    TextView qualificationTextView;
    Button dismissButton;
    TextView tvBandwidth;
    ImageView refresh;

    private ValueEventListener mDatabaseListener;
    private DatabaseReference mDatabase;

    private int thread_id = -1;
    private UserInactivityTimer timer;
    private int counter;
    private boolean is_activity_active = true;
    private boolean is_doctor_details_loaded = false;
    private String avatar_url = "";
    private Uri imageUri;

    public MyApplication mController;

    private static final int HTTP_REQUEST_CODE_THREAD_REQUEST = 1;
    private static final int HTTP_REQUEST_CODE_POST_MESSAGE = 2;
    private static final int HTTP_REQUEST_CODE_THREAD_CLOSE = 3;
    private static final int HTTP_REQUEST_CODE_GET_MESSAGE = 4;
    private JSONObject json_doctor;
    String speed = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        //ButterKnife.bind(this);
        toolbar = findViewById(R.id.tabanim_toolbar);
         layout_root_view= findViewById(R.id.layout_root_view);
         buttonSend= findViewById(R.id.ibSend);
        recyclerView= findViewById(R.id.recycler_view);
        chatText= findViewById(R.id.editChat);
        layout_progress= findViewById(R.id.layout_loading);
         layout_refresh= findViewById(R.id.layout_refresh);
        toolbar_action= findViewById(R.id.toolbar_action);
        toolbar_title= findViewById(R.id.toolbar_title);
        layout_thumbnail= findViewById(R.id.layout_thumbnail);
         iv_thumbnail= findViewById(R.id.thumbnail);
        ibCamera= findViewById(R.id.ibCamera);
        doctor_image= findViewById(R.id.doctor_image);
        doctorInfoImageView= findViewById(R.id.iv_doctor_info);
         doctorInfoLayout= findViewById(R.id.layout_doctor_info);
        docotorAvatarCIV= findViewById(R.id.civ_doctor_avatar);
        ratingBar= findViewById(R.id.rating_bar);
        ratingTextView= findViewById(R.id.tv_rating);
        mciCodeTextView= findViewById(R.id.tv_mci_code);
         practitionerNoTextView= findViewById(R.id.tv_practitioner_no);
        qualificationTextView= findViewById(R.id.tv_qualification);
        dismissButton= findViewById(R.id.btn_dismiss);
        tvBandwidth= findViewById(R.id.tvBandwidth);
        refresh= findViewById(R.id.img);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setStatusBarBackground(this);

        this.mController = MyApplication.getInstance();
        this.addListener();

        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
            return;
        }

        this.initChatAdapter();
        this.initChat();

        registerReceiver(mHandleNotificationReceiver, new IntentFilter(Constants.KEY_CHAT_SESSION_DISCONNECT_BROADCAST_RECEIVER));
        tvBandwidth.setVisibility(View.VISIBLE);
        new SpeedTestTask().execute();
        //initProgressAlert();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.startAnimation(
                        AnimationUtils.loadAnimation(FirebaseChatActivity.this, R.anim.rotate) );
                new SpeedTestTask().execute();
            }
        });
    }



    private void initProgressAlert() {
        Toast.makeText(mController, "effected area need to change", Toast.LENGTH_SHORT).show();
    }

    public class SpeedTestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(final SpeedTestReport report) {
                    // called when download/upload is finished
                    Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet().floatValue() / 1024 + " Kbps");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            refresh.clearAnimation();
                            if (report.getTransferRateOctet().floatValue() / 1024 >= 1000) {
                                speed = "Bandwidth : " + String.format(Locale.US, "%.1f", report.getTransferRateOctet().floatValue() / (1024 * 1024)) + " Mbps";
                                tvBandwidth.setText(speed);
                                tvBandwidth.setTextColor(getResources().getColor(R.color.light_green));
                            } else {
                                speed = "Bandwidth : " + String.format(Locale.US, "%.1f", report.getTransferRateOctet().floatValue() / 1024) + " Kbps";
                                String status_text = "";
                                if ((report.getTransferRateOctet().floatValue() / 1024) >= 200.0) {
                                    status_text = "Your internet bandwidth supports Audio/Video calls";
                                    tvBandwidth.setText(speed);
                                    tvBandwidth.setTextColor(getResources().getColor(R.color.book_consultation_orage_button));
                                } else {
                                    tvBandwidth.setTextColor(getResources().getColor(R.color.red));
                                    status_text = "Your internet bandwidth is not suitable for making Video call";
                                    tvBandwidth.setText(speed);
                                    if ((report.getTransferRateOctet().floatValue() / 1024) < 50) {
                                        status_text = "Your internet bandwidth is not suitable for making Audio/Video calls ";
                                    }
                                }


                            }
                        }
                    });

                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                    //refresh.clearAnimation();

                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    Log.v("speedtest", "[progress] rate in octet/s : " + report.getTransferRateOctet().floatValue() / 1024 + " Kbps");
                }
            });

            //speedTestSocket.startDownload("https://corpstaging.doconline.com/DocOnline_Brochure_1mb.pdf", 1500);
            speedTestSocket.startDownload("https://doconline-filestorage.s3.ap-south-1.amazonaws.com/WhiteLabelImages/DocOnline_Brochure_1mb.pdf", 1500);

            return speed;
        }


    }

    private void addListener() {
        ibCamera.setOnClickListener(this);
        doctor_image.setOnClickListener(this);
        iv_thumbnail.setOnClickListener(this);
        doctorInfoImageView.setOnClickListener(this);
        dismissButton.setOnClickListener(this);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!chatText.getText().toString().isEmpty()) {
                    attemptSend();
                }
            }
        });
    }


    private void initChat() {
        layout_progress.setVisibility(View.VISIBLE);
        layout_refresh.setVisibility(View.VISIBLE);

        // thread request
        new HttpClient(HTTP_REQUEST_CODE_THREAD_REQUEST, MyApplication.HTTPMethod.POST.getValue(), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getChatURL());
    }

    @Override
    public void onUserInteraction() {
        layout_thumbnail.setVisibility(View.GONE);
        resetTimer();
    }

    private void initChatAdapter() {
        if (mAdapter != null) {
            return;
        }

        mAdapter = new MessageRecyclerAdapter(this, this, mMessages);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        // not working
        chatText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptSend();
                    return true;
                }

                return false;
            }
        });

        //  not used
        chatText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void attemptSend() {
        String message = chatText.getText().toString().trim();

        // if message empty then bring focus to edittext.
        if (TextUtils.isEmpty(message)) {
            chatText.requestFocus();
            return;
        }

        if (thread_id == -1) {
            Toast.makeText(getApplicationContext(), "Session Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!is_doctor_details_loaded) {
            Toast.makeText(getApplicationContext(), "Please Wait for Doctor to Connect", Toast.LENGTH_SHORT).show();
            return;
        }

        // clear edittext
        chatText.setText("");
        // add message in recyclerview
        addMessage(message, "", ChatMessage.TYPE_MESSAGE_OUT);
    }

    //  adds message in recyclerview and does api request
    private void addMessage(String message, String image_path, int mType) {
        if (!new InternetConnectionDetector(this).isConnected()) {
            Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mType == ChatMessage.TYPE_MESSAGE_IN) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
                String timestamp = Helper.Local_to_UTC_TimeZone(sdf.format(System.currentTimeMillis()));

                ChatMessage chat = new ChatMessage(Helper.getRandomString(), message, "user", String.valueOf(timestamp), ChatMessage.TYPE_MESSAGE_IN);
                chat.setCDNPhotoURL(image_path);
                mMessages.add(0, chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mType == ChatMessage.TYPE_MESSAGE_OUT) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
                String timestamp = Helper.Local_to_UTC_TimeZone(sdf.format(System.currentTimeMillis()));

                ChatMessage chat = new ChatMessage(Helper.getRandomString(), message, "user", String.valueOf(timestamp), ChatMessage.TYPE_MESSAGE_OUT);
                chat.setCDNPhotoURL(image_path);
                mMessages.add(0, chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mAdapter.notifyItemInserted(0);
        scrollToBottom();

        if (!image_path.isEmpty()) {
            String uploadId = UUID.randomUUID().toString();

            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);

            String UPLOAD_URL = mController.getChatURL() + thread_id + "/message";

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                new MultipartFileUploadClient(uploadId, ChatMessage.composeChatAttachmentJSON(image_path), ChatMessage.composeMessageJSON(message), false)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UPLOAD_URL);
            } else {
                new MultipartFileUploadClient(uploadId, ChatMessage.composeChatAttachmentJSON(image_path), ChatMessage.composeMessageJSON(message), true)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UPLOAD_URL);
            }
        } else {
            new HttpClient(HTTP_REQUEST_CODE_POST_MESSAGE, MyApplication.HTTPMethod.POST.getValue(), ChatMessage.composeMessageJSON(message), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getChatURL() + thread_id + "/message");
        }
    }


    private void scrollToBottom() {
        recyclerView.smoothScrollToPosition(0);
        resetTimer();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ibCamera) {
            this.hideKeyboard();

            if (!CheckingPermissionIsEnabledOrNot()) {
                RequestMultiplePermission();
                return;
            }

            if (thread_id == -1) {
                Toast.makeText(getApplicationContext(), "Session Not Connected", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!is_doctor_details_loaded) {
                Toast.makeText(getApplicationContext(), "Please Wait for Doctor to Connect", Toast.LENGTH_SHORT).show();
                return;
            }

            image_picker_dialog();
        } else if (id == R.id.doctor_image) {// has been hidden(not being used)
            onTaskCompleted(true, 0, avatar_url);
        } else if (id == R.id.thumbnail) {// not being used
            Toast.makeText(getApplicationContext(), avatar_url, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FirebaseChatActivity.this, FileViewerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("URL", avatar_url);
            startActivity(intent);
        } else if (id == R.id.iv_doctor_info) {// doctor_layout is shown
            enableDoctorInfo();
        } else if (id == R.id.btn_dismiss) {// doctor_layout is hidden
            Animation exitToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.exit_to_top);
            doctorInfoLayout.startAnimation(exitToTop);
            doctorInfoLayout.setVisibility(View.GONE);
        }
    }


    private void image_picker_dialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_title_file_picker)
                .items(R.array.image_picker_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0:

                                viewMultipleImageChooser();
                                break;

                            case 1:

                                openCamera();
                                break;
                        }
                    }
                })
                .show();
    }


    private void viewMultipleImageChooser() {
        Intent intent = new Intent(FirebaseChatActivity.this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 1);
        startActivityForResult(intent, com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE);
    }


    private void openCamera() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), com.doconline.doconline.app.Constants.MEDIA_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        /**
         * Check if we're running on Android 5.0 or higher
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageUri = FileProvider.getUriForFile(FirebaseChatActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        } else {
            imageUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
        }


        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to Open Camera", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            /**
             * The array list has the image paths of the selected images
             */
            ArrayList<Image> images = data.getParcelableArrayListExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);

            for (Image img : images) {
                String image_path = img.path;
                Log.wtf("IMAGE_PATH", "" + img.path);

                File file = new File(img.path);
                long length = file.length() / 1024; // Size in KB

                if (length <= 1024 * DEFAULT_FILE_SIZE) {
                    Intent intent = new Intent(FirebaseChatActivity.this, ChatImagePreviewActivity.class);
                    intent.putExtra("IMAGE_PATH", image_path);
                    startActivityForResult(intent, CHAT_IMAGE_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Max allowed size 8 MB", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (resultCode == RESULT_OK && requestCode == com.doconline.doconline.app.Constants.CAMERA_REQUEST_CODE) {

            String image_path = imageUri.getPath();
            Log.wtf("IMAGE_PATH", "" + imageUri.getPath());

            File file = new File(image_path);
            long length = file.length() / 1024; // Size in KB

            if (length <= 1024 * DEFAULT_FILE_SIZE) {
                Intent intent = new Intent(FirebaseChatActivity.this, ChatImagePreviewActivity.class);
                intent.putExtra("IMAGE_PATH", image_path);
                startActivityForResult(intent, CHAT_IMAGE_REQUEST_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "Max allowed size 8 MB", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CHAT_IMAGE_REQUEST_CODE && resultCode == CHAT_IMAGE_REQUEST_CODE) {
            String body = data.getStringExtra("BODY");
            String image_path = data.getStringExtra("IMAGE_PATH");

            if (thread_id == -1) {
                Toast.makeText(getApplicationContext(), "Session Not Connected", Toast.LENGTH_SHORT).show();
                return;
            }

            addMessage(body, image_path, ChatMessage.TYPE_MESSAGE_OUT);
        }

        if (requestCode == SUBSCRIPTION_PERMISSION_REQ_CODE && resultCode == SUBSCRIPTION_PERMISSION_REQ_CODE) {
            if (!new InternetConnectionDetector(this).isConnected()) {
                new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
                return;
            }

            initChat();
        }
    }


    private void showAlertDialog(int requestCode, String message) {
        new CustomAlertDialog(this, requestCode, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_account_info), message,
                        getResources().getString(R.string.Verify),
                        getResources().getString(R.string.Cancel), false);
    }


    private void subscribe(String title, String content) {
        if (mController.getSession().getSubscriptionStatus() && mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B_PAID)) {
            new CustomAlertDialog(this, this)
                    .showAlertDialogWithoutTitle(content, getResources().getString(R.string.OK), false);

            return;
        }

        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE, this)
                .showDialogWithAction(title, content,
                        getResources().getString(R.string.Choose),
                        getResources().getString(R.string.Close), false);
    }

    // starts timer for inactive user.
    private void thread_json_data(String json_data) {
        try {
            JSONObject json = new JSONObject(json_data);
            this.thread_id = json.getInt(KEY_ID);

            if (this.thread_id != -1) {
                // if user is inactive for 6 minutes, chat window will be closed.
                timer = new UserInactivityTimer((60 * 1000 * 5), 1000);
                timer.start();
                Toast.makeText(getApplicationContext(), "Doctor will join you soon", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Fail to connect. Please retry", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (this.thread_id != -1) {
                firebase_realtime_database();
            }
        }
    }


    private void showAlertDialog(String title, String content) {
        new CustomAlertDialog(this, this)
                .showAlertDialogWithPositiveAction(title, content, getResources().getString(R.string.OK), false);
    }


    @Override
    public void onPositiveAction() {
        finish();
    }

    @Override
    public void onNegativeAction() {
        finish();
    }

    @Override
    public void onNegativeAction(int requestCode) {
        if (requestCode == DIALOG_REQUEST_CODE_MOBILE_VERIFICATION
                || requestCode == DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE
                || requestCode == DIALOG_REQUEST_CODE_EMAIL_VERIFICATION) {
            finish();
        }
    }

    @Override
    public void onPositiveAction(int requestCode) {

        if (requestCode == DIALOG_REQUEST_CODE_MOBILE_VERIFICATION) {
            Intent intent = new Intent(FirebaseChatActivity.this, MainActivity.class);
            intent.putExtra("INDEX", 4);
            startActivity(intent);

            finish();
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_EMAIL_VERIFICATION) {
            Intent intent3 = new Intent(FirebaseChatActivity.this, MainActivity.class);
            intent3.putExtra("INDEX", 9);
            startActivity(intent3);

            finish();
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE) {
            Intent intent = new Intent(FirebaseChatActivity.this, SubscriptionActivity.class);
            intent.putExtra("SUBSCRIPTION_VIA_BOOK_CONSULTATION", 1);
            startActivityForResult(intent, SUBSCRIPTION_PERMISSION_REQ_CODE);
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_CHAT_CONFIRMATION) {
            if (thread_id != -1) {
                new HttpClient(HTTP_REQUEST_CODE_THREAD_CLOSE, MyApplication.HTTPMethod.PATCH.getValue(), FirebaseChatActivity.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getChatURL() + thread_id + "/close");
            }

            finish();
            return;
        }

        finish();
    }

    @Override
    public void onPositiveAction(int requestCode, String data) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                this.endSessionConfirmationDialog("End Session", "Do you want to end session ?");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        this.endSessionConfirmationDialog("End Session", "Do you want to end session ?");
    }


    private void endSessionConfirmationDialog(String title, String content) {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_CHAT_CONFIRMATION, this)
                .showDialogWithAction(title, content,
                        getResources().getString(R.string.EndNow),
                        getResources().getString(R.string.NoThanks), true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (timer != null) {
                timer.cancel();
            }

            unregisterReceiver(mHandleNotificationReceiver);
            mDatabase.removeEventListener(mDatabaseListener);
            DatabaseReference.goOffline();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        try {
            is_activity_active = false;
            DatabaseReference.goOffline();
            uploadReceiver.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            is_activity_active = false;
            DatabaseReference.goOffline();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            is_activity_active = true;
            DatabaseReference.goOnline();
            uploadReceiver.register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    // Plays when message comes and goes.
    private void playMessageSound() {
        MediaPlayer mediaPlayer = new MediaPlayer();

        final Uri ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.newmessage);

        try {
            mediaPlayer.setDataSource(this, ringtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.prepare();

            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    private void load_doctor_details(final String full_name, final String avatar_url) {
        try {
            if (!isFinishing()) {
                if (!avatar_url.isEmpty()) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String mciCode = "";
                            String practitionorNo = "";
                            String qualification = "";
                            try {
                                mciCode = json_doctor.isNull(KEY_MCI_CODE) ? "NA" : json_doctor.getString(KEY_MCI_CODE);
                                practitionorNo = json_doctor.isNull(KEY_PRACTIONER_NO) ? "NA" : json_doctor.getString(KEY_PRACTIONER_NO);
                                qualification = json_doctor.isNull(KEY_QUALIFICATION) ? "" : json_doctor.getString(KEY_QUALIFICATION);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            doctorInfoImageView.setVisibility(View.VISIBLE);
                            toolbar_title.setText("MCI: " + mciCode/* + ", Reg No: " + practitionorNo*/);
                            toolbar_action.setText(qualification);

                            Log.i("avatar_url_doctor", "url:" + avatar_url);

                            /*Glide.with(FirebaseChatActivity.this)
                                    .load(avatar_url) // thumbnail url goes here
                                    .placeholder(R.drawable.doctor)
                                    .dontAnimate()
                                    .override(40, 40)
                                    .centerCrop()
                                    .into(doctor_image);*/

                            try {
                                ImageLoader.load(FirebaseChatActivity.this, avatar_url, doctor_image, R.drawable.doctor, 40, 40);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_doctor_details_loaded = true;
        }
    }

    private void enableDoctorInfo() {
        try {
            if (doctorInfoLayout.getVisibility() == View.GONE) {
                Animation enterFromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_bottom);
                doctorInfoLayout.startAnimation(enterFromBottom);
                doctorInfoLayout.setVisibility(View.VISIBLE);

                //String avatarUrl = json_doctor.getString(KEY_AVATAR_URL);
                String mciCode = json_doctor.isNull(KEY_MCI_CODE) ? "NA" : json_doctor.getString(KEY_MCI_CODE);
                String practitionorNo = json_doctor.isNull(KEY_PRACTIONER_NO) ? "NA" : json_doctor.getString(KEY_PRACTIONER_NO);
                String qualification = json_doctor.isNull(KEY_QUALIFICATION) ? "" : json_doctor.getString(KEY_QUALIFICATION);
                double avgRating = json_doctor.getDouble(KEY_RATINGS);

                //ImageLoader.load(getApplicationContext(), avatarUrl, docotorAvatarCIV, R.drawable.doctor, 80, 80);
                mciCodeTextView.setText(mciCode);
                practitionerNoTextView.setText(practitionorNo);
                qualificationTextView.setText(qualification);
                ratingBar.setRating((float) avgRating);
                ratingTextView.setText(String.valueOf(avgRating));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // in onDataChanged, get values.
    private void firebase_realtime_database() {
        mDatabase = FirebaseDatabase.getInstance().getReference(mController.getEnvPrefix());

        mDatabaseListener = mDatabase.child(KEY_THREADS).child(String.valueOf(thread_id)).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (new InternetConnectionDetector(getApplicationContext()).isConnected()) {
                    if(thread_id !=-1) {
                        new HttpClient(HTTP_REQUEST_CODE_GET_MESSAGE, MyApplication.HTTPMethod.GET.getValue(), FirebaseChatActivity.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getChatURL() + thread_id);
                    }
                }

                //try
                {
                    //long created_at = Long.valueOf(dataSnapshot.child("created_at").getValue().toString());
                    //int user_id = Integer.valueOf(dataSnapshot.child("user_id").getValue().toString());

                    /*String full_name = "";

                    try
                    {
                        avatar_url = dataSnapshot.child(KEY_DOCTOR).child(KEY_AVATAR_URL).getValue().toString();
                        full_name = dataSnapshot.child(KEY_DOCTOR).child(KEY_FULL_NAME).getValue().toString();

                        if(!is_doctor_details_loaded)
                        {
                            load_doctor_details(full_name, avatar_url);
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    Log.wtf(JSON_TAG, String.valueOf(avatar_url));
                    Log.wtf(JSON_TAG, String.valueOf(full_name));

                    if(dataSnapshot.child(KEY_MESSAGES).getChildrenCount() == 0)
                    {
                        return;
                    }

                    mMessages.clear();
                    playMessageSound();

                    boolean notify = false;*/

                    //for (DataSnapshot snapshot :dataSnapshot.child(KEY_MESSAGES).getChildren())
                    {
                        //ChatMessage country = noteDataSnapshot.child("body").getValue(ChatMessage.class);
                        //Log.wtf(JSON_TAG, "Code: " + country.body + ", Name " + country.s_type);
                        //Log.wtf(JSON_TAG, "" + noteDataSnapshot.child("body").getValue());

                        /*String message_id = snapshot.getKey();
                        String body = "";

                        if(snapshot.child(KEY_BODY).exists())
                        {
                            body = snapshot.child(KEY_BODY).getValue().toString();
                        }

                        String sender_type = snapshot.child(KEY_SENDER_TYPE).getValue().toString();
                        String timestamp = snapshot.child(KEY_CREATED_AT).getValue().toString();*/

                        //int sender_id = Integer.valueOf(snapshot.child("sender_id").getValue().toString());

                        /*if(sender_type.equalsIgnoreCase(KEY_USER))
                        {
                            ChatMessage chat = new ChatMessage(message_id, body, sender_type, timestamp, ChatMessage.TYPE_MESSAGE_OUT);

                            chat.setDoctorAvatarURL(avatar_url);
                            chat.setDoctorFullName(full_name);

                            if(snapshot.child(KEY_CDN_PHOTO_URL).exists())
                            {
                                String cdn_photo_url = snapshot.child(KEY_CDN_PHOTO_URL).getValue().toString();
                                chat.setCDNPhotoURL(cdn_photo_url);
                                Log.wtf(JSON_TAG, "Photo URL: " + String.valueOf(cdn_photo_url));
                            }

                            mMessages.add(chat);
                        }

                        else
                        {
                            ChatMessage chat = new ChatMessage(message_id, body, sender_type, timestamp, ChatMessage.TYPE_MESSAGE_IN);

                            chat.setDoctorAvatarURL(avatar_url);
                            chat.setDoctorFullName(full_name);

                            if(snapshot.child(KEY_CDN_PHOTO_URL).exists())
                            {
                                String cdn_photo_url = snapshot.child(KEY_CDN_PHOTO_URL).getValue().toString();
                                chat.setCDNPhotoURL(cdn_photo_url);
                                Log.wtf(JSON_TAG, "Photo URL: " + String.valueOf(cdn_photo_url));
                            }

                            mMessages.add(chat);

                            if(!is_activity_active && !notify)
                            {
                                Log.wtf(JSON_TAG, "NOTIFY USER");
                                notify = true;
                            }
                        }*/

                        //ChatMessage chat = new ChatMessage(message_id, body, sender_type, String.valueOf(created_at));
                        //mMessages.add(chat);
                    }
                }

                /*catch (Exception e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    if(dataSnapshot.child(KEY_MESSAGES).getChildrenCount() != 0)
                    {
                        Collections.reverse(mMessages);
                        mAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                }*/
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Log.wtf(JSON_TAG, "Failed to read value.", error.toException());
            }
        });


        /*Query queryOrderByChild = mDatabase.orderByChild("messages");

        queryOrderByChild.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {

                Log.wtf(JSON_TAG, "onChildAdded");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.wtf(JSON_TAG, "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.wtf(JSON_TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                Log.wtf(JSON_TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    // file upload in progress
    @Override
    public void onProgress(final UploadInfo uploadInfo) {
        Log.i("PROGRESS", "progress = " + uploadInfo.getProgressPercent());
    }

    // failed to upload the file
    @Override
    public void onError(final UploadInfo uploadInfo, final ServerResponse serverResponse, final Exception exception) {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // file upload completed
    @Override
    public void onCompleted(final UploadInfo uploadInfo, final ServerResponse serverResponse) {
        try {
            Log.i("COMPLETED", "" + serverResponse.getBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // file upload cancelled
    @Override
    public void onCancelled(final UploadInfo uploadInfo) {
        Log.i("CANCELLED", "cancelled = ");
    }

    // parse message and set in recyclerview.
    private void parseMessage(JSONObject data) {
        try {
            json_doctor = data.getJSONObject(KEY_DOCTOR);
            JSONArray json_messages = data.getJSONArray(KEY_MESSAGES);
            String full_name = "";

            try {
                avatar_url = json_doctor.has(KEY_AVATAR_URL) ? json_doctor.getString(KEY_AVATAR_URL) : "";
                full_name = ""/*json_doctor.getString(KEY_FULL_NAME)*/;
                int doctor_id = 0;
                Object id = json_doctor.get(KEY_ID);
                if (id instanceof String) {
                    doctor_id = 0;
                } else if (id instanceof Integer) {
                    doctor_id = ((Integer) id).intValue();
                }

                if (doctor_id != 0 && !is_doctor_details_loaded) {
                    load_doctor_details(full_name, avatar_url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.wtf(JSON_TAG, String.valueOf(avatar_url));
            Log.wtf(JSON_TAG, full_name);

            if (json_messages.length() == 0) {
                return;
            }

            mMessages.clear();
            playMessageSound();

            boolean notify = false;

            for (int i = 0; i < json_messages.length(); i++) {
                JSONObject json_msg = json_messages.getJSONObject(i);

                String message_id = "";
                String body = "";

                if (json_msg.has(KEY_BODY)) {
                    body = json_msg.isNull(KEY_BODY) ? "" : json_msg.getString(KEY_BODY);
                }

                String sender_type = json_msg.getString(KEY_SENDER_TYPE);
                String timestamp = json_msg.getString(KEY_CREATED_AT);

                // patient's messages
                if (sender_type.equalsIgnoreCase(KEY_USER)) {
                    ChatMessage chat = new ChatMessage(message_id, body, sender_type, timestamp, ChatMessage.TYPE_MESSAGE_OUT);

                    chat.setDoctorAvatarURL(avatar_url);
                    chat.setDoctorFullName(full_name);

                    if (json_msg.has(KEY_CDN_PHOTO_URL)) {
                        String cdn_photo_url = json_msg.isNull(KEY_CDN_PHOTO_URL) ? "" : json_msg.getString(KEY_CDN_PHOTO_URL);
                        chat.setCDNPhotoURL(cdn_photo_url);
                        Log.wtf(JSON_TAG, "Photo URL: " + cdn_photo_url);
                    }

                    mMessages.add(chat);
                }
                 // doctor's messages
                else {
                    ChatMessage chat = new ChatMessage(message_id, body, sender_type, timestamp, ChatMessage.TYPE_MESSAGE_IN);

                    chat.setDoctorAvatarURL(avatar_url);
                    chat.setDoctorFullName(full_name);

                    if (json_msg.has(KEY_CDN_PHOTO_URL)) {
                        String cdn_photo_url = json_msg.isNull(KEY_CDN_PHOTO_URL) ? "" : json_msg.getString(KEY_CDN_PHOTO_URL);
                        chat.setCDNPhotoURL(cdn_photo_url);
                        Log.wtf(JSON_TAG, "Photo URL: " + cdn_photo_url);
                    }

                    mMessages.add(chat);

                    if (!is_activity_active && !notify) {
                        Log.wtf(JSON_TAG, "NOTIFY USER");
                        notify = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mMessages.size() != 0) {
                Collections.reverse(mMessages);
                mAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        }
    }


    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        if (isFinishing()) {
            return;
        }

        try {
            if (requestCode == HTTP_REQUEST_CODE_THREAD_REQUEST && (responseCode == HttpClient.OK || responseCode == HttpClient.CREATED)) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.thread_json_data(json.getString(KEY_DATA));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_MESSAGE && responseCode == HttpClient.OK) {
                JSONObject json = new JSONObject(response);
                parseMessage(json.getJSONObject(KEY_DATA));
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_POST_MESSAGE && responseCode == HttpClient.CREATED) {
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_THREAD_CLOSE && responseCode == HttpClient.OK) {
                return;
            }

            // Any kind of subscription is required to chat with the doctor
            if (responseCode == HttpClient.PAYMENT_REQUIRED) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.subscribe("Membership", json.getString(KEY_MESSAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (responseCode == HttpClient.RESOURCE_NOT_AVAILABLE) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.showAlertDialog("Info", json.getString(KEY_MESSAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (responseCode == HttpClient.PRECONDITION_FAILED) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    if (json.has(KEY_MOBILE_NO)) {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_MOBILE_VERIFICATION, json.getString(KEY_MOBILE_NO));
                    } else if (json.has(KEY_EMAIL)) {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_EMAIL_VERIFICATION, json.getString(KEY_EMAIL));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (responseCode == HttpClient.UNPROCESSABLE_ENTITY) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.showAlertDialog("Info", json.getString(KEY_MESSAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(this, this, layout_root_view).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    layout_progress.setVisibility(View.GONE);
                    layout_refresh.setVisibility(View.GONE);
                }
            });
        }
    }


    private void resetTimer() {
        if (timer != null) {
            counter = 0;
            timer.cancel();
            timer.start();
        }
    }


    private class UserInactivityTimer extends CountDownTimer {
        UserInactivityTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            counter++;
            Log.wtf("onTick", "onTick-" + counter);
        }

        @Override
        public void onFinish() {
            try {
                if (thread_id != -1) {
                    // to close the thread.
                    new HttpClient(HTTP_REQUEST_CODE_THREAD_CLOSE, MyApplication.HTTPMethod.PATCH.getValue(), FirebaseChatActivity.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getChatURL() + thread_id + "/close");
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                showAlertDialog("Expired", "Chat session is expired due to inactivity.");
            }
        }
    }


    /**
     * Receiving push messages
     */
    // if chat is cancelled from doctor's side
    private final BroadcastReceiver mHandleNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            String action = intent.getAction();

            if (action.equalsIgnoreCase(Constants.KEY_CHAT_SESSION_DISCONNECT_BROADCAST_RECEIVER)) {
                Log.wtf("INSTANT_DATA", intent.getStringExtra("json_data"));

                try {
                    JSONObject json = new JSONObject(intent.getStringExtra("json_data"));

                    final String threadId = json.getString(KEY_THREAD_ID);
                    final String message = json.getString(KEY_MESSAGE);

                    if (threadId.equals(String.valueOf(thread_id))) {
                        runOnUiThread(new Runnable() {

                            public void run() {
                                thread_id = -1;
                                showAlertDialog("CHAT DISCONNECTED!", "Your chat session has been disconnected by doctor");
                            }
                        });
                    }
                    Log.v("getMessage",""+message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // has been hidden(not being used).
    @Override
    public void onTaskCompleted(boolean flag, int code, String avatar_url) {
        try {
            if (!avatar_url.isEmpty()) {
                layout_thumbnail.setVisibility(View.VISIBLE);

                Glide.with(this)
                        .load(avatar_url) // thumbnail url goes here
                        .dontAnimate()
                        .into(iv_thumbnail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Permission function starts from here
     */
    private void RequestMultiplePermission() {
        /**
         * Creating String Array with Permissions.
         */
        ActivityCompat.requestPermissions(FirebaseChatActivity.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadStoragePermission && WriteStoragePermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();

                        if (thread_id == -1) {
                            Toast.makeText(getApplicationContext(), "Session Not Connected", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!is_doctor_details_loaded) {
                            Toast.makeText(getApplicationContext(), "Please Wait for Doctor to Connect", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        image_picker_dialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    /**
     * Checking permission is enabled or not
     */
    public boolean CheckingPermissionIsEnabledOrNot() {
        int CameraPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int WriteStoragePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ReadStoragePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return CameraPermissionResult == PackageManager.PERMISSION_GRANTED &&
                WriteStoragePermissionResult == PackageManager.PERMISSION_GRANTED &&
                ReadStoragePermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    private void hideKeyboard() {
        try {
            if (isFinishing()) {
                return;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(chatText.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}