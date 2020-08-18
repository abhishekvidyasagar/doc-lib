package com.doconline.doconline.tokbox;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.doconline.doconline.R;
import com.doconline.doconline.SplashScreenActivity;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.floating.TimerService;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.model.Review;
import com.doconline.doconline.model.User;
import com.doconline.doconline.model.VOIP;
import com.doconline.doconline.tokbox.service.ClosingService;
import com.doconline.doconline.tokbox.service.IncomingCallActionReceiver;
import com.doconline.doconline.tokbox.util.RingerFragmentCallbackListener;
import com.doconline.doconline.tokbox.util.RingtonePlayer;
import com.doconline.doconline.utils.NotificationUtils;
import com.opentok.android.AudioDeviceManager;
import com.opentok.android.BaseAudioDevice;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static com.doconline.doconline.app.Constants.CALL_STATUS_PATIENT_ACCEPTED;
import static com.doconline.doconline.app.Constants.CALL_STATUS_PATIENT_ACKNOWLEDGED;
import static com.doconline.doconline.app.Constants.CALL_STATUS_PATIENT_REJECTED;
import static com.doconline.doconline.app.Constants.CALL_TYPE_VIDEO;
import static com.doconline.doconline.app.Constants.DEFAULT_SESSION_CONNECTING_TIMEOUT_DURATION;
import static com.doconline.doconline.app.Constants.DEFAULT_SUBSCRIBER_CONNECTING_TIMEOUT_DURATION;
import static com.doconline.doconline.app.Constants.KEY_ANSWER;
import static com.doconline.doconline.app.Constants.KEY_CALL_ACTION;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DOCUMENT_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_EHR_DOCUMENT_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_HANG_UP;
import static com.doconline.doconline.app.Constants.KEY_MCI_CODE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_PRACTITIONER_NUMBER;
import static com.doconline.doconline.app.Constants.KEY_QUALIFICATION;
import static com.doconline.doconline.app.Constants.KEY_RATING;
import static com.doconline.doconline.app.Constants.KEY_RATINGS;
import static com.doconline.doconline.app.Constants.KEY_REJECT;
import static com.doconline.doconline.app.Constants.KEY_SPECIALIZATION;
import static com.doconline.doconline.appointment.AppointmentSummeryActivity.HTTP_REQUEST_CODE_POST_RATING;


public class CallingActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks,
        Session.SessionListener,
        Session.ConnectionListener,
        Session.ReconnectionListener,
        PublisherKit.PublisherListener,
        PublisherKit.AudioStatsListener,
        PublisherKit.VideoStatsListener,
        SubscriberKit.SubscriberListener,
        SubscriberKit.StreamListener,
        SubscriberKit.VideoListener,
        SubscriberKit.VideoStatsListener,
        SubscriberKit.AudioStatsListener,
        RingerFragmentCallbackListener,
        OnHttpResponse/*,
        RatingFragment.AddReviewFragmentListener*/ {
    private final String LOG_TAG = CallingActivity.class.getSimpleName();

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private static final int HTTP_REQUEST_CODE_GET_CONNECTION_STATE = 1;
    private static final int HTTP_REQUEST_CODE_UPDATE_CALL_STATE = 2;
    private static final int HTTP_REQUEST_CODE_GET_PROFILE_STATE = 3;
    private static final int HTTP_REQUEST_CODE_UPDATE_CONSENT = 4;
    public static final int HTTP_REQUEST_CODE_GET_RATING = 5;
    //private static final int HTTP_REQUEST_CODE_POST_RATING = 5;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private MyApplication mController;

    private long RINGING_DURATION;
    private int CALL_TYPE = CALL_TYPE_VIDEO;
    private int APPOINTMENT_ID, DOCTOR_ID;
    private String DOCTOR_NAME = "";
    private String AVATAR_URL = "";
    private String SPECIALIZATION = "";
    private long startTimeInMillis, stopTimeInMillis;

    // Replace with a generated Session ID
    public String SESSION_ID = "";
    // Replace with a generated token
    public String TOKEN = "";
    // Replace with your OpenTok API key
    public String API_KEY = "";


    private long mPrevPublisherVideoPacketsLost = 0;
    private long mPrevPublisherVideoPacketsSent = 0;
    private double mPrevPublisherVideoTimestamp = 0;
    private long mPrevPublisherVideoBytes = 0;

    private long mPrevPublisherAudioPacketsLost = 0;
    private long mPrevPublisherAudioPacketsSent = 0;
    private double mPrevPublisherAudioTimestamp = 0;
    private long mPrevPublisherAudioBytes = 0;

    private double mVideoPublisherPLRatio = 0.0;
    private long mVideoBwPublisher = 0;

    private double mAudioPublisherPLRatio = 0.0;
    private long mAudioBwPublisher = 0;


    //test quality duration in seconds
    private static final int TEST_DURATION = 20;
    //3 seconds
    private static final int TIME_WINDOW = 3;
    //time interval to check the video quality in seconds
    private static final int TIME_VIDEO_TEST = 15;

    private double mVideoPLRatio = 0.0;
    private long mVideoBw = 0;

    private double mAudioPLRatio = 0.0;
    private long mAudioBw = 0;

    private long mPrevVideoPacketsLost= 0;
    private long mPrevVideoPacketsRcvd = 0;
    private double mPrevVideoTimestamp = 0;
    private long mPrevVideoBytes = 0;

    private long mPrevAudioPacketsLost = 0;
    private long mPrevAudioPacketsRcvd = 0;
    private double mPrevAudioTimestamp = 0;
    private long mPrevAudioBytes = 0;

    private long mStartTestTime = 0;

    private boolean isSubscriberConnected = false;
    private boolean isSessionConnected = false;
    private boolean isActive = false;

    //Communication status
    private boolean isCallInProgress = false;
    private boolean isCallAccepted = false;
    private boolean notifyOngoingCall = true;

    private boolean isNavBarHidden = true;
    private Point mScreenSize;

    private RingingTimer ringing_timer;

    private RingtonePlayer ringtonePlayer;
    private Vibrator vibrator;
    private RingerFragmentCallbackListener ringer_listener;

    //Alert
    private boolean is_dialog_visible = false;


    CoordinatorLayout mParentLayout;

    LinearLayout mLayoutCallSummary;

    LinearLayout mLayoutConnecting;

    TextView text_connection_time;

    TextView text_bandWidth;

    TextView text_publish_bandWidth;

    LinearLayout mLayoutCallerInfo;

    FrameLayout mPublisherViewContainer;

    FrameLayout mSubscriberViewContainer;

    ImageButton mAudioBtn;

    ImageButton mVideoBtn;

    ImageButton mCallBtn;

    ImageButton mCameraBtn;

    ImageButton mRemoteAudioBtn;

    ImageButton mRemoteVideoBtn;

    RelativeLayout actionbar_preview_fragment_container;

    RelativeLayout remote_preview_fragment_container;

    RelativeLayout ringer_fragment_container;

    RelativeLayout layout_answer_button;

    TextView text_doctor_name;

    TextView text_call_type;

    TextView mAlert;

    CircleImageView profile_image;

    Chronometer chrono_call_duration;

    Chronometer chrono_call_timer;

    TextView text_full_name;

    TextView text_mci_code;

    TextView text_practitioner_no;

    TextView text_specialization;

    TextView text_qualification;

    RatingBar doctorRatingStar;

    TextView text_ratings;

    CircleImageView iv_avatar;

    LinearLayout layout_doctor_profile;

    TextView tvDoctorNameToolbar;

    TextView tvInfoMenu;

    ImageButton ibCloseInfoMenu;

    RelativeLayout layout_toolbar;

    SwitchCompat switch_consent;

    //Binding for call duration layout

    RelativeLayout rateDoctorLayout;

    CircleImageView doctorImageCiv;

    TextView nameTextView;

    TextView doctorTypeTextView;

    TextView callDurationTextView;

    Button submitButton;

    Button notNowButton;

    RelativeLayout layoutCallDuration;

    CircleImageView doctorAvatarCiv;

    TextView doctorNameTextView;

    TextView timeTextView;

    TextView dismissButton;

    //for new rating style

    TextView veryPoor;

    TextView poor;

    TextView good;

    TextView satisfied;

    TextView awesome;

    LinearLayout userFeedBackLayout;

    private double userFeedBack = 0.0;

    public static void start(final Context context, final String data, final boolean is_outgoing_call) {
        Intent intent = new Intent(context, CallingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("json_data", data);
        // outgoing call is not needed.
        intent.putExtra("is_outgoing_call", is_outgoing_call);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);

        this.mController = MyApplication.getInstance();

        if (!mController.getSession().isLoggedIn()) {
            finishActivity();
            return;
        }

        if (isActive) {
            finishActivity();
            return;
        }

        setContentView(R.layout.activity_calling);
      //  ButterKnife.bind(this);

         mParentLayout =  findViewById(R.id.layout_parent);

        mLayoutCallSummary =  findViewById(R.id.layout_call_summary);
        mLayoutConnecting =  findViewById(R.id.layout_connecting);
        text_connection_time =  findViewById(R.id.text_connection_time);
        text_bandWidth =  findViewById(R.id.tvBandwidth);
       text_publish_bandWidth =  findViewById(R.id.tvPublisherBandwidth);
         mLayoutCallerInfo =  findViewById(R.id.layout_caller_info);
         mPublisherViewContainer =  findViewById(R.id.publisher_container);
         mSubscriberViewContainer =  findViewById(R.id.subscriber_container);
         mAudioBtn =  findViewById(R.id.localAudio);
         mVideoBtn =  findViewById(R.id.localVideo);
         mCallBtn =  findViewById(R.id.call);
         mCameraBtn =  findViewById(R.id.camera);
         mRemoteAudioBtn =  findViewById(R.id.remoteAudio);
         mRemoteVideoBtn =  findViewById(R.id.remoteVideo);
         actionbar_preview_fragment_container =  findViewById(R.id.actionbar_preview_fragment_container);
         remote_preview_fragment_container =  findViewById(R.id.remote_preview_fragment_container);
         ringer_fragment_container =  findViewById(R.id.ringer_fragment_container);
         layout_answer_button =  findViewById(R.id.layout_answer_button);
       text_doctor_name =  findViewById(R.id.doctor_name);
        text_call_type =  findViewById(R.id.call_type);
         mAlert =  findViewById(R.id.quality_warning);
        profile_image =  findViewById(R.id.profile_image);
         chrono_call_duration =  findViewById(R.id.chrono_call_duration);
        chrono_call_timer =  findViewById(R.id.chrono_call_timer);
        text_full_name =  findViewById(R.id.tvFullName);
        text_mci_code =  findViewById(R.id.tvMciCode);
         text_practitioner_no =  findViewById(R.id.tvPractitionerNo);
         text_specialization =  findViewById(R.id.tvSpecialization);
         text_qualification =  findViewById(R.id.tvQualification);
        doctorRatingStar =  findViewById(R.id.doctor_rating_star);
         text_ratings =  findViewById(R.id.tvRatings);
        iv_avatar =  findViewById(R.id.avatar);
         layout_doctor_profile =  findViewById(R.id.layout_doctor_profile);
         tvDoctorNameToolbar =  findViewById(R.id.tvDoctorNameToolbar);
         tvInfoMenu =  findViewById(R.id.tvInfoMenu);
         ibCloseInfoMenu =  findViewById(R.id.ibCloseInfoMenu);
        layout_toolbar =  findViewById(R.id.layout_toolbar);
         switch_consent =  findViewById(R.id.switch_consent);

        //Binding for call duration layout
        rateDoctorLayout =  findViewById(R.id.layout_rate_doctor);
         doctorImageCiv =  findViewById(R.id.civ_doctor_image);
         nameTextView =  findViewById(R.id.tv_name);
         doctorTypeTextView =  findViewById(R.id.tv_doctor_type);
         callDurationTextView =  findViewById(R.id.tv_call_duration);

         submitButton =  findViewById(R.id.btn_submit);

         submitButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (userFeedBack == 0.0) {
                     Toast.makeText(getApplicationContext(), "Please provide your rating", Toast.LENGTH_LONG).show();
                 } else {
                     if (new InternetConnectionDetector(CallingActivity.this).isConnected()) {
                         if (APPOINTMENT_ID > 0) {
                             new HttpClient(HTTP_REQUEST_CODE_POST_RATING, MyApplication.HTTPMethod.POST.getValue(), true, Review.getRatingJSON(userFeedBack), CallingActivity.this)
                                     .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + APPOINTMENT_ID + "/" + KEY_RATING);
                         }
                     } else {
                         finishActivity();
                     }
                 }
             }
         });
        notNowButton =  findViewById(R.id.btn_not_now);


        notNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

         layoutCallDuration =  findViewById(R.id.layout_call_duration);
        doctorAvatarCiv =  findViewById(R.id.civ_doctor_avatar);
         doctorNameTextView =  findViewById(R.id.tv_doctor_name);
         timeTextView =  findViewById(R.id.tv_time);
         dismissButton =  findViewById(R.id.btn_dismiss);
         dismissButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dismissDialog();
             }
         });

        //for new rating style
        veryPoor =  findViewById(R.id.tv_verypoor);

        veryPoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack = 1.0;
                veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor), null, null);

                poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
                good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
                satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
                awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
            }
        });
        poor =  findViewById(R.id.tv_poor);

        poor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack = 2.0;
                veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);

                poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor), null, null);

                good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
                satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
                awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
            }
        });
         good =  findViewById(R.id.tv_good);

         good.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 userFeedBack = 3.0;
                 veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
                 poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);

                 good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good), null, null);

                 satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
                 awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
             }
         });
        satisfied =  findViewById(R.id.tv_satisfied);

        satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFeedBack = 4.0;
                veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
                poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
                good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);

                satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied), null, null);

                awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
            }
        });
         awesome =  findViewById(R.id.tv_awesome);

         awesome.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 userFeedBack = 5.0;
                 veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
                 poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
                 good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
                 satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);

                 awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied), null, null);
             }
         });
        userFeedBackLayout =  findViewById(R.id.rating_layout);


        // to remove status bar ,turn screen on etc(see properties).
        makeActivityAppearOnLockScreen();

        // permissions required in order to use opentok.
        requestPermissions();

        // prepares ringtone, plays in loop and stops.
        ringtonePlayer = new RingtonePlayer(this);

        // listener for different call actions.
        ringer_listener = this;

        Log.v("CALLER_INFO", String.valueOf(getIntent().getStringExtra("json_data")));

        // the data you got from FCM for call.


        try {
            JSONObject json = new JSONObject(String.valueOf(getIntent().getStringExtra("json_data")));

            this.SESSION_ID = json.getString(Constants.KEY_SESSION_ID);
            this.TOKEN = json.getString(Constants.KEY_TOKEN_ID);
            this.API_KEY = mController.getOpentokApiKey();

            this.CALL_TYPE = json.getInt(Constants.KEY_CALL_TYPE);
            this.APPOINTMENT_ID = json.getInt(Constants.KEY_APPOINTMENT_ID);

            if (!isVideoCall()) {
                text_call_type.setText(R.string.text_incoming_audio_call);
            }

            load_caller_details(new JSONObject(json.getString(Constants.KEY_DOCTOR)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // to initialize the openTok session.
        initializeSession(API_KEY, SESSION_ID);

        // never will be true...always false
        if (getIntent().getBooleanExtra("is_outgoing_call", false)) {
            ringer_listener.onCallAccepted();
        } else {
            // everytime this will be executed.
            ringer_listener.onRinging();
        }

        // navigation doctor info screen width is set to 70%.
        this.navDoctorProfile();
        // chatHead reminder is removed, if it's there.
        this.stopChatHead();
        this.addListener();
        // registering the broadcast receiver for call action which is minimized.
        this.registerReceiver(mHandleCallReceiver, new IntentFilter(KEY_CALL_ACTION));
        // setting switch
        this.checkConsent(mController.getSession().getDocumentConsent());
        // whether to share document with the doctor or not.
        this.switchConsentListener();
        // starting a service so that if call is disconnected, remove the activity(just don't minimize).
        startService(new Intent(getBaseContext(), ClosingService.class));
    }


    private void checkConsent(int consent) {
        if (consent == 0) {
            switch_consent.setChecked(false);
        } else {
            switch_consent.setChecked(true);
        }
    }

    // whether to share document with the doctor or not.
    private void switchConsentListener() {
        switch_consent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!new InternetConnectionDetector(getApplicationContext()).isConnected()) {
                    Toast.makeText(getApplicationContext(), "Internet Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject json = new JSONObject();
                    json.put(KEY_DOCUMENT_CONSENT, isChecked ? 1 : 0);

                    new HttpClient(HTTP_REQUEST_CODE_UPDATE_CONSENT, MyApplication.HTTPMethod.PATCH.getValue(), true, json.toString(), CallingActivity.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getEhrDocumentConsentURL());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addListener() {
        // to get doctor info screen
        tvInfoMenu.setOnClickListener(this);
        // to close doctor info screen
        ibCloseInfoMenu.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tvInfoMenu) {
            if (isNavBarHidden) {
                this.showDoctorProfile((int) (mScreenSize.x * 0.30));
            }
        } else if (id == R.id.ibCloseInfoMenu) {
            if (!isNavBarHidden) {
                this.hideDoctorProfile((layout_doctor_profile.getWidth() + (int) (mScreenSize.x * 0.30)));
            }
        }
    }

    private void navDoctorProfile() {
        Display display = getWindowManager().getDefaultDisplay();
        this.mScreenSize = new Point();
        display.getSize(this.mScreenSize);

        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        params.width = (int) (mScreenSize.x * 0.70);

        layout_doctor_profile.setLayoutParams(params);
        layout_doctor_profile.setX(mScreenSize.x);
    }


    private boolean isVideoCall() {
        return CALL_TYPE == CALL_TYPE_VIDEO;
    }

    // enable and disable views when user is talking to the doctor.
    public void setEnabled(boolean enabled) {
        if (mVideoBtn != null && mAudioBtn != null && mCallBtn != null && mCameraBtn != null) {
            if (enabled) {
                mAudioBtn.setOnClickListener(mBtnClickListener);
                mCallBtn.setOnClickListener(mBtnClickListener);
                mRemoteAudioBtn.setOnClickListener(mBtnClickListener);

                if (isVideoCall()) {
                    mVideoBtn.setOnClickListener(mBtnClickListener);
                    mCameraBtn.setOnClickListener(mBtnClickListener);
                    mRemoteVideoBtn.setOnClickListener(mBtnClickListener);
                }
            } else {
                mAudioBtn.setOnClickListener(null);
                mCallBtn.setOnClickListener(null);
                mRemoteAudioBtn.setOnClickListener(null);

                if (isVideoCall()) {
                    mVideoBtn.setOnClickListener(null);
                    mCameraBtn.setOnClickListener(null);
                    mRemoteVideoBtn.setOnClickListener(null);
                }
            }

            mVideoBtn.setImageResource(R.drawable.ic_video_white);
            mCameraBtn.setImageResource(R.drawable.camera);
            mRemoteVideoBtn.setImageResource(R.drawable.ic_video_white);

            mAudioBtn.setImageResource(R.drawable.ic_microphone);
            mCallBtn.setImageResource(R.drawable.ic_phone_hangup_white);
            mRemoteAudioBtn.setImageResource(R.drawable.ic_audio);

            if (isVideoCall()) {
                mVideoBtn.setVisibility(View.VISIBLE);
                mCameraBtn.setVisibility(View.VISIBLE);

                mPublisherViewContainer.setVisibility(View.VISIBLE);
            } else {
                mVideoBtn.setVisibility(View.GONE);
                mCameraBtn.setVisibility(View.GONE);

                mPublisherViewContainer.setVisibility(View.GONE);
            }
        }
    }


    private View.OnClickListener mBtnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.localAudio) {
                updateLocalAudio();
            } else if (id == R.id.localVideo) {
                updateLocalVideo();
            } else if (id == R.id.remoteAudio) {
                updateRemoteAudio();
            } else if (id == R.id.remoteVideo) {
                updateRemoteVideo();
            } else if (id == R.id.call) {
                updateCall();
            } else if (id == R.id.camera) {
                swapCamera();
            }
        }
    };

    // swaps camera
    private void swapCamera() {
        try {
            if (mPublisher != null) {
                mPublisher.cycleCamera();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // mute and unmute your voice for the doctor
    private void updateLocalAudio() {
        try {
            if (mPublisher != null) {
                boolean isAudioEnable = mPublisher.getPublishAudio();
                mPublisher.setPublishAudio(!isAudioEnable);

                if (isAudioEnable) {
                    mAudioBtn.setImageResource(R.drawable.ic_microphone_off);
                } else {
                    mAudioBtn.setImageResource(R.drawable.ic_microphone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // subscriber's voice won't come to publisher.
    private void updateRemoteAudio() {
        try {
            if (mSubscriber != null) {
                boolean isAudioEnable = mSubscriber.getSubscribeToAudio();
                mSubscriber.setSubscribeToAudio(!isAudioEnable);

                if (isAudioEnable) {
                    mRemoteAudioBtn.setImageResource(R.drawable.no_audio);
                } else {
                    mRemoteAudioBtn.setImageResource(R.drawable.ic_audio);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  shows and hides my video to the subscriber
    private void updateLocalVideo() {
        try {
            if (mPublisher != null) {
                boolean isVideoEnable = mPublisher.getPublishVideo();
                mPublisher.setPublishVideo(!isVideoEnable);

                if (isVideoEnable) {
                    mVideoBtn.setImageResource(R.drawable.ic_video_off_white);

                    if (mPublisher.getView() != null) {
                        mPublisher.getView().setVisibility(View.GONE);
                    }
                } else {
                    mVideoBtn.setImageResource(R.drawable.ic_video_white);

                    if (mPublisher.getView() != null) {
                        mPublisher.getView().setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // subscriber's video won't come to publisher.
    private void updateRemoteVideo() {
        try {
            if (mSubscriber != null) {
                boolean isVideoEnable = mSubscriber.getSubscribeToVideo();
                mSubscriber.setSubscribeToVideo(!isVideoEnable);

                if (isVideoEnable) {
                    mRemoteVideoBtn.setImageResource(R.drawable.ic_video_off);

                    if (mSubscriber.getView() != null) {
                        mSubscriber.getView().setVisibility(View.GONE);
                    }
                } else {
                    mRemoteVideoBtn.setImageResource(R.drawable.ic_video);

                    if (mSubscriber.getView() != null) {
                        mSubscriber.getView().setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // disconnects call
    private void updateCall() {
        try {
            if (mSubscriber != null) {
                mSubscriber = null;
            }

            mSubscriberViewContainer.removeAllViews();
            mPublisherViewContainer.removeAllViews();

            onCallDisconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // shows the call duration just after call is disonnected(removed now i guess)
    private void displayCallSummeryView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                actionbar_preview_fragment_container.setVisibility(View.GONE);
                remote_preview_fragment_container.setVisibility(View.GONE);
                ringer_fragment_container.setVisibility(View.VISIBLE);
                layout_doctor_profile.setVisibility(View.VISIBLE);
                layout_toolbar.setVisibility(View.GONE);

                mLayoutCallSummary.setVisibility(View.GONE);

                mLayoutConnecting.setVisibility(View.GONE);
                text_connection_time.setVisibility(View.GONE);

                mAlert.setVisibility(View.GONE);
            }
        });
    }

    /*private void showReviewDoctorAlert() {
        FragmentManager fm = getSupportFragmentManager();
        RatingFragment dialog = new RatingFragment(getApplicationContext(), 0);
        dialog.setListener(CallingActivity.this);
        dialog.setRetainInstance(true);
        dialog.setCancelable(false);

        if (!is_dialog_visible) {
            dialog.show(fm, "Write Review");
            is_dialog_visible = true;
        }
    }*/

    // to rate the doctor
    private void showReviewDoctorAlert1() {
        if (rateDoctorLayout.getVisibility() == View.GONE) {
            Animation enterFrombottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_bottom);
            rateDoctorLayout.startAnimation(enterFrombottom);
            rateDoctorLayout.setVisibility(View.VISIBLE);
        }
        nameTextView.setText(this.DOCTOR_NAME);
        doctorTypeTextView.setText(this.SPECIALIZATION);
        callDurationTextView.setText("Call Duration : " + getTalkTimeDuration());
        ImageLoader.load(getApplicationContext(), AVATAR_URL, doctorImageCiv, R.drawable.ic_avatar, 80, 80);
    }

    // time duration alert
    private void showTimeDurationAlert() {
        if (layoutCallDuration.getVisibility() == View.GONE) {
            Animation enterFrombottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_bottom);
            layoutCallDuration.startAnimation(enterFrombottom);
            layoutCallDuration.setVisibility(View.VISIBLE);
        }
        ImageLoader.load(getApplicationContext(), AVATAR_URL, doctorAvatarCiv, R.drawable.ic_avatar, 80, 80);
        doctorNameTextView.setText(this.DOCTOR_NAME);
        timeTextView.setText(String.valueOf(getTalkTimeDuration()));
    }

    // to calculate the call duration time
    private String getTalkTimeDuration() {
        long totalTimeInMillis = stopTimeInMillis - startTimeInMillis;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalTimeInMillis),
                TimeUnit.MILLISECONDS.toMinutes(totalTimeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalTimeInMillis)),
                TimeUnit.MILLISECONDS.toSeconds(totalTimeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTimeInMillis)));
        return hms;
    }

    // rating for the doctor gets submitted
   /* @OnClick(R.id.btn_submit)
    public void submitRating() {
        if (userFeedBack == 0.0) {
            Toast.makeText(getApplicationContext(), "Please provide your rating", Toast.LENGTH_LONG).show();
        } else {
            if (new InternetConnectionDetector(this).isConnected()) {
                if (APPOINTMENT_ID > 0) {
                    new HttpClient(HTTP_REQUEST_CODE_POST_RATING, MyApplication.HTTPMethod.POST.getValue(), true, Review.getRatingJSON(userFeedBack), CallingActivity.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + APPOINTMENT_ID + "/" + KEY_RATING);
                }
            } else {
                finishActivity();
            }
        }
    }*/

    /*@OnClick(R.id.tv_verypoor)
    public void onclickVeryPoor() {
        userFeedBack = 1.0;
        veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor), null, null);

        poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
        good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
        satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
        awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
    }*/

    /*@OnClick(R.id.tv_poor)
    public void onclickPoor() {
        userFeedBack = 2.0;
        veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);

        poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor), null, null);

        good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
        satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
        awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
    }*/

    /*@OnClick(R.id.tv_good)
    public void good(){
        userFeedBack = 3.0;
        veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
        poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);

        good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good), null, null);

        satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);
        awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
    }*/

   /* @OnClick(R.id.tv_satisfied)
    public void satisfied(){
        userFeedBack = 4.0;
        veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
        poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
        good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);

        satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied), null, null);

        awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied_grey), null, null);
    }*/

    /*@OnClick(R.id.tv_awesome)
    public void awesome(){
        userFeedBack = 5.0;
        veryPoor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_poor_grey), null, null);
        poor.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_poor_grey), null, null);
        good.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_good_grey), null, null);
        satisfied.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_satisfied_grey), null, null);

        awesome.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_very_satisfied), null, null);
    }
*/

    // dismisses the alert and finishes activity
   // @OnClick({R.id.btn_not_now, R.id.btn_dismiss})
    public void dismissDialog() {
        userFeedBack = 0.0;
        finishActivity();
    }

    /*@Override
    public void onDialogPositiveClick(DialogFragment dialog, Review _review) {
        if (new InternetConnectionDetector(this).isConnected()) {
            if (APPOINTMENT_ID > 0) {
                new HttpClient(HTTP_REQUEST_CODE_POST_RATING, MyApplication.HTTPMethod.POST.getValue(), true, Review.getRatingJSON(_review.rating), CallingActivity.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + APPOINTMENT_ID + "/" + KEY_RATING);
            }
        } else {
            finishActivity();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        finishActivity();
    }

    @Override
    public void onDialogRatingChange(DialogFragment dialog, Review _review) {

    }*/

    // hides the connecting view
    private void hideConnectingView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                actionbar_preview_fragment_container.setVisibility(View.VISIBLE);
                mCameraBtn.setVisibility(View.VISIBLE);
                remote_preview_fragment_container.setVisibility(View.VISIBLE);
                ringer_fragment_container.setVisibility(View.GONE);
                layout_doctor_profile.setVisibility(View.VISIBLE);
                layout_toolbar.setVisibility(View.VISIBLE);
                mLayoutConnecting.setVisibility(View.VISIBLE);

                setEnabled(true);
            }
        });
    }

    // displays connecting view when call is disconnected
    private void displayConnectingView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mLayoutCallerInfo.setVisibility(View.GONE);
                layout_answer_button.setVisibility(View.GONE);

                mLayoutConnecting.setVisibility(View.VISIBLE);
            }
        });
    }

    // removes countdown timer, if there
    private void stopChatHead() {
        try {
            if (mController.isServiceRunning()) {
                stopService(new Intent(CallingActivity.this, TimerService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }


    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }

    // ringing phone, vibrating
    private void startCallNotification() {
        ringtonePlayer.play(true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final long[] vibrationCycle = {0, 1000, 1000};

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }

        RINGING_DURATION = MyApplication.getInstance().getRingingDuration();
        ringing_timer = new RingingTimer((RINGING_DURATION * 1000), 1000);
        ringing_timer.start();
    }


    /**
     * This method is used for stopping the ringing & vibration on phone.
     */
    public void stopCallNotification() {
        Log.d(LOG_TAG, "stopCallNotification()");

        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }

        if (ringing_timer != null) {
            ringing_timer.cancel();
        }
    }

    // for landscape and portrait view (hiding doctor info).
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tvInfoMenu.setVisibility(View.GONE);
            this.hideDoctorProfile(mScreenSize.y);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            tvInfoMenu.setVisibility(View.VISIBLE);
            this.hideDoctorProfile((layout_doctor_profile.getWidth() + (int) (mScreenSize.x * 0.30)));
        }
    }

    // notification is created, and session is paused
    @Override
    protected void onPause() {
        super.onPause();

        try {
            if (mSession != null) {
                mSession.onPause();
            }

            notify_background_call(DOCTOR_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("onPause", "onPause");
    }

    //  remove notification (if there), session onResume.
    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (mSession != null) {
                mSession.onResume();
            }

            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (nMgr != null) {
                nMgr.cancel(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  do nothing
    @Override
    public void onBackPressed() {
        //finishActivity();
    }

    // whether the call is active or not
    public boolean isCallInProgress() {
        return isCallInProgress;
    }

    // remove broadcastreceiver and notification
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(mHandleCallReceiver);

            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (nMgr != null) {
                nMgr.cancel(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // when call is disconnected.
    @Override
    public void onCallDisconnect() {
        if (mSession != null && isSessionConnected) {
            try {
                mSession.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // to get user profile data(has been used later).
                new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());

                stopCallDurationTimer();
                displayCallSummeryView();
                playDisconnectRingtone();
                //showReviewDoctorAlert();
                if (new InternetConnectionDetector(getApplicationContext()).isConnected()) {
                    getRatingStatus();
                } else {
                    //dismissCallSummery();
                    showTimeDurationAlert();
                }

                isCallInProgress = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        notifyOngoingCall = false;
    }

    // tells the time when call is going on
    private void startCallDurationTimer() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                /**
                 * Start Timer
                 */
                chrono_call_timer.setVisibility(View.VISIBLE);

                chrono_call_duration.setBase(SystemClock.elapsedRealtime());
                chrono_call_duration.start();

                chrono_call_timer.setBase(SystemClock.elapsedRealtime());
                chrono_call_timer.start();

                startTimeInMillis = Calendar.getInstance().getTimeInMillis();
            }
        });
    }

    // stops call timer
    private void stopCallDurationTimer() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                /**
                 * Stop Timer
                 */
                chrono_call_duration.setVisibility(View.VISIBLE);
                chrono_call_timer.setVisibility(View.GONE);
                chrono_call_duration.stop();
                chrono_call_timer.stop();

                stopTimeInMillis = Calendar.getInstance().getTimeInMillis();
            }
        });
    }

    // When call accept button is pressed
    public void onClickAccept(View view) {
        ringer_listener.onCallAccepted();
    }

    // When call reject button is pressed
    public void onClickReject(View view) {
        ringer_listener.onCallRejected();
    }

    private void makeActivityAppearOnLockScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }


    /**
     * Callback which gets called when the call is ignored.
     */
    @Override
    public void onCallIgnored() {
        stopCallNotification();

        /**
         * Call default notification method
         */
        NotificationUtils utils = new NotificationUtils("Missed Call", "You have missed a call from DocOnline", "#e42b19", "ic_missed_call", false, "OPEN_ACTIVITY_HISTORY");
        NotificationUtils.small_notification(this, 1, utils);

        notifyOngoingCall = false;
        finishActivity();
    }

    /**
     * Callback which gets called when the call is answered/accepted.
     */
    @Override
    public void onCallAccepted() {
        this.isCallAccepted = true;

        /*
        * if two or more users are coneected with the same id,
        * then it will check whether the caller has already accepted the call
        * */
        new HttpClient(HTTP_REQUEST_CODE_GET_CONNECTION_STATE, MyApplication.HTTPMethod.GET.getValue(), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getCallStateURL() + SESSION_ID);

        String url = mController.getAppointmentURL() + APPOINTMENT_ID + "/" + Constants.KEY_STATUS;

        // acknowledging whether call came or not.
        new HttpClient(HTTP_REQUEST_CODE_UPDATE_CALL_STATE, MyApplication.HTTPMethod.POST.getValue(),
                VOIP.composeCallStateMap(DOCTOR_ID, CALL_STATUS_PATIENT_ACCEPTED), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

        if (isVideoCall()) {
            /**
             * Make to run your application only in portrait/landscape mode
             */
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            /**
             * Make to run your application only in portrait mode
             */
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        stopCallNotification();
        displayConnectingView();
        checkSessionConnectionState();
    }

    // connecting to opentok session
    private void connectSession() {
        try {
            if (mSession != null) {
                mSession.connect(TOKEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Callback which gets called when the call is rejected.
     */
    @Override
    public void onCallRejected() {
        try {
            // Acknowledgement that call came.
            String url = mController.getAppointmentURL() + APPOINTMENT_ID + "/" + Constants.KEY_STATUS;
            new HttpClient(HTTP_REQUEST_CODE_UPDATE_CALL_STATE, MyApplication.HTTPMethod.POST.getValue(),
                    VOIP.composeCallStateMap(DOCTOR_ID, CALL_STATUS_PATIENT_REJECTED), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            notifyOngoingCall = false;
        }

        playDisconnectRingtone();
        stopCallNotification();
        finishActivity();
    }

    /**
     * Callback which gets called when the phone is ringing.
     */
    @Override
    public void onRinging() {
        startCallNotification();
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
            if (requestCode == HTTP_REQUEST_CODE_UPDATE_CONSENT && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);

                    json = json.getJSONObject(KEY_DATA);
                    int ehr_document_consent = json.getInt(KEY_EHR_DOCUMENT_CONSENT);
                    mController.getSession().putDocumentConsent(ehr_document_consent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_PROFILE_STATE && responseCode == HttpClient.OK) {
                JSONObject json = new JSONObject(response);
                String data = (json.isNull(KEY_DATA)) ? "" : json.getString(KEY_DATA);
                User.getUserProfileFromJSON(data);
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_UPDATE_CALL_STATE && responseCode == HttpClient.NO_RESPONSE) {
                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_CONNECTION_STATE) {
                if (responseCode == HttpClient.ACCEPTED) {
                    connectSession();
                } else {
                    try {
                        JSONObject json = new JSONObject(String.valueOf(response));
                        String message = json.getString(KEY_MESSAGE);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        playDisconnectRingtone();
                        stopCallNotification();
                        finishActivity();
                    }
                }
            }


            if (requestCode == HTTP_REQUEST_CODE_GET_RATING) {
                double rating = 0;

                if (responseCode == HttpClient.OK) {
                    JSONObject json = new JSONObject(response);
                    rating = json.getDouble(KEY_DATA);
                    if (rating != 0.0) {
                        //finishActivity();
                        showTimeDurationAlert();
                    } else {
                        showReviewDoctorAlert1();
                    }
                } else {
                    //finishActivity();
                    showTimeDurationAlert();
                }
            }

            if (requestCode == HTTP_REQUEST_CODE_POST_RATING) {
                if (responseCode == HttpClient.NO_RESPONSE) {
                    Toast.makeText(getApplicationContext(), "Thank you for your feedback", Toast.LENGTH_LONG).show();
                    finishActivity();
                } else {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(KEY_MESSAGE)) {
                        Toast.makeText(getApplicationContext(), "" + jsonObject.getString(KEY_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                    finishActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // how many seconds to ring
    private class RingingTimer extends CountDownTimer {
        RingingTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            RINGING_DURATION--;
            Log.d("onTick", "onTick-" + RINGING_DURATION);
        }

        @Override
        public void onFinish() {
            ringer_listener.onCallIgnored();
        }
    }


    private void setHeadSetModeEnable() {
        try {
            AudioDeviceManager.getAudioDevice().setOutputMode(
                    BaseAudioDevice.OutputMode.Handset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void publishMedia() {
        try {
            // initialize Publisher and set this object to listen to Publisher events
            mPublisher = new Publisher.Builder(this)
                    .videoTrack(isVideoCall())
                    .name("Patient")
                    .resolution(Publisher.CameraCaptureResolution.HIGH)
                    .frameRate(Publisher.CameraCaptureFrameRate.FPS_30)
                    .build();

            mPublisher.setPublisherListener(this);
            if(!isVideoCall()) {
                mPublisher.setAudioStatsListener(this);
            }
            mPublisher.setVideoStatsListener(this);


            // set publisher video style to fill view
            mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FILL);

            // if video call, show publisherViewContainer....if not, hide it(for audio call).
            if (isVideoCall()) {
                mPublisherViewContainer.addView(mPublisher.getView());

                if (mPublisher.getView() instanceof GLSurfaceView) {
                    ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
                }
            }

            mSession.publish(mPublisher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // checking whether subscriber is connected or not
    private void checkSubscriberConnectionState(final int duration) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isSessionConnected && !isSubscriberConnected) {
                    if (duration == DEFAULT_SUBSCRIBER_CONNECTING_TIMEOUT_DURATION) {
                        text_connection_time.setVisibility(View.VISIBLE);
                        checkSubscriberConnectionState(2 * DEFAULT_SUBSCRIBER_CONNECTING_TIMEOUT_DURATION);
                    } else {
                        connectionTimeout();
                    }
                }
            }
        }, duration * 1000);
    }

    // if session is not connected within the time(30 secs) then Connection timeout.
    private void checkSessionConnectionState() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isSessionConnected) {
                    connectionTimeout();
                }
            }
        }, DEFAULT_SESSION_CONNECTING_TIMEOUT_DURATION * 1000);
    }


    private void connectionTimeout() {
        if (isFinishing()) {
            return;
        }

        try {
            if (mSession != null && isSessionConnected) {
                try {
                    mSession.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isSessionConnected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Toast.makeText(getApplicationContext(), "Connection Timeout", Toast.LENGTH_LONG).show();
            playDisconnectRingtone();
            stopCallNotification();
            finishActivity();
        }
    }


    private void load_caller_details(JSONObject json) {
        try {
            String url = mController.getAppointmentURL() + APPOINTMENT_ID + "/" + Constants.KEY_STATUS;

            int doctor_id = json.getInt(Constants.KEY_ID);
            final String full_name = json.getString(Constants.KEY_FULL_NAME);
            final String specialization = json.getString(KEY_SPECIALIZATION);
            final String qualification = json.has(KEY_QUALIFICATION) ? json.getString(KEY_QUALIFICATION) : "";

            final String mci_code = json.has(KEY_MCI_CODE) ? json.getString(KEY_MCI_CODE) : "";
            final String practitioner_no = json.has(KEY_PRACTITIONER_NUMBER) ? json.getString(KEY_PRACTITIONER_NUMBER) : "";
            final double ratings = json.getDouble(KEY_RATINGS);
            final String avatar_url = json.getString(Constants.KEY_AVATAR_URL);

            this.DOCTOR_ID = doctor_id;
            this.DOCTOR_NAME = Helper.toCamelCase(full_name);
            this.AVATAR_URL = avatar_url;
            this.SPECIALIZATION = specialization;

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    text_doctor_name.setText(Helper.toCamelCase(full_name));
                    text_full_name.setText(Helper.toCamelCase(full_name));
                    tvDoctorNameToolbar.setText(Helper.toCamelCase(full_name));
                    doctorRatingStar.setRating((float) ratings);
                    text_ratings.setText(new DecimalFormat("0.0").format(ratings));
                    text_mci_code.setText(mci_code);
                    text_practitioner_no.setText(practitioner_no);
                    text_specialization.setText(specialization);
                    text_qualification.setText(qualification);

                    if (!avatar_url.isEmpty()) {
                        ImageLoader.loadThumbnail(getApplicationContext(), avatar_url, iv_avatar, R.drawable.doctor, 130, 130);
                        ImageLoader.loadThumbnail(getApplicationContext(), avatar_url, profile_image, R.drawable.doctor, 250, 250);
                    }
                }
            });

            // Acknowledging that call came.
            new HttpClient(HTTP_REQUEST_CODE_UPDATE_CALL_STATE, MyApplication.HTTPMethod.POST.getValue(),
                    VOIP.composeCallStateMap(DOCTOR_ID, CALL_STATUS_PATIENT_ACKNOWLEDGED), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void dismissCallSummery() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                finishActivity();
            }
        }, 3000);
    }


    private void playDisconnectRingtone() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        final Uri ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep);

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

    private void getRatingStatus() {
        new HttpClient(HTTP_REQUEST_CODE_GET_RATING, MyApplication.HTTPMethod.GET.getValue(), CallingActivity.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getAppointmentURL() + APPOINTMENT_ID + "/" + KEY_RATING);
    }

    private void finishActivity() {
        try {
            if (mSession != null && isSessionConnected) {
                try {
                    mSession.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                // After finish redirect user to Launcher Activity
                Intent i = new Intent(CallingActivity.this, SplashScreenActivity.class);

                i.putExtra("CALLING_ACTIVITY", true);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Starting Launcher Activity
                startActivity(i);

                finishAffinity();
            }
        }
    }


    private void notify_background_call(String title) {
        if (!notifyOngoingCall) {
            return;
        }

        String content;

        if (isVideoCall()) {
            if (isCallInProgress()) {
                content = "Ongoing Video Call";
            } else if (isCallAccepted) {
                content = "Connecting";
            } else {
                content = "Incoming Video Call";
            }
        } else {
            if (isCallInProgress()) {
                content = "Ongoing Audio Call";
            } else if (isCallAccepted) {
                content = "Connecting";
            } else {
                content = "Incoming Audio Call";
            }
        }

        String channelId = "channel_incoming_call";
        String channelName = "DocOnline Call";

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentText(content);

        if (isCallInProgress()) {
            mBuilder.setSmallIcon(R.drawable.ic_phone_in_talk_white);
            mBuilder.setUsesChronometer(true);
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_phone_incoming_white);
            mBuilder.setWhen(System.currentTimeMillis());
        }

        Intent resultIntent = new Intent(this, CallingActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        mBuilder.setContentIntent(pendingIntent);


        if (!isCallAccepted) {
            /**
             * Answer intent
             */
            Intent answer = new Intent(this, IncomingCallActionReceiver.class);
            answer.setAction(KEY_ANSWER);
            PendingIntent pAnswer = PendingIntent.getBroadcast(this, 1000, answer, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.addAction(R.drawable.ic_phone_grey, KEY_ANSWER, pAnswer);

            /**
             * Reject intent
             */
            Intent reject = new Intent(this, IncomingCallActionReceiver.class);
            reject.setAction(KEY_REJECT);
            PendingIntent pReject = PendingIntent.getBroadcast(this, 1000, reject, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.addAction(R.drawable.ic_phone_hangup_grey, KEY_REJECT, pReject);
        } else if (isCallInProgress()) {
            /**
             * Hangup intent
             */
            Intent hangup = new Intent(this, IncomingCallActionReceiver.class);
            hangup.setAction(KEY_HANG_UP);
            PendingIntent pHangup = PendingIntent.getBroadcast(this, 1000, hangup, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.addAction(R.drawable.ic_phone_hangup_grey, KEY_HANG_UP, pHangup);
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(1000, mBuilder.build());
        }

        Log.d("NOTIFY_CALL", "NOTIFY");
    }


    /**
     * Receiving Call Action from background
     */
    private final BroadcastReceiver mHandleCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getStringExtra(KEY_CALL_ACTION).equals(KEY_ANSWER)) {
                    ringer_listener.onCallAccepted();
                    bring_to_front();
                } else if (intent.getStringExtra(KEY_CALL_ACTION).equals(KEY_REJECT)) {
                    ringer_listener.onCallRejected();
                } else if (intent.getStringExtra(KEY_CALL_ACTION).equals(KEY_HANG_UP)) {
                    updateCall();
                    bring_to_front();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void bring_to_front() {
        try {
            Intent resultIntent = new Intent(CallingActivity.this, CallingActivity.class);
            resultIntent.setAction(Intent.ACTION_MAIN);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resultIntent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(resultIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // permission work
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.Settings))
                    .setNegativeButton(getString(R.string.Cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }


    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }

    private void initializeSession(String apiKey, String sessionId) {
        try {
            mSession = new Session.Builder(this, apiKey, sessionId).build();
            mSession.setSessionListener(this);
            mSession.setConnectionListener(this);
            mSession.setReconnectionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Session Listener methods
     *
     * @param session
     */
    @Override
    public void onConnected(Session session) {
        Log.d(LOG_TAG, "onConnected: Connected to session: " + session.getSessionId());

        if (isFinishing()) {
            return;
        }

        this.isSessionConnected = true;
        this.isCallInProgress = true;

        // checks whether subscriber is connected or not
        checkSubscriberConnectionState(DEFAULT_SUBSCRIBER_CONNECTING_TIMEOUT_DURATION);
        // if connected hide connecting view
        hideConnectingView();
        // show calling screen
        publishMedia();
    }

    @Override
    public void onDisconnected(Session session) {
        Log.d(LOG_TAG, "onDisconnected: Disconnected from session: " + session.getSessionId());
    }

    // if net is slow.
    @Override
    public void onReconnecting(Session session) {
        if (!isFinishing() && isSubscriberConnected) {
            mAlert.setText("Reconnecting ...");
            mAlert.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warning_text));
            mAlert.setVisibility(View.VISIBLE);
        }

        Log.d(LOG_TAG, "onReconnecting: Session Reconnecting");
    }

    // If reconnected.
    @Override
    public void onReconnected(Session session) {
        if (!isFinishing() && isSubscriberConnected) {
            mAlert.setText("Reconnected");
            mAlert.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.quality_warning_lifted));
            mAlert.setVisibility(View.VISIBLE);
        }

        Log.d(LOG_TAG, "onReconnected: Session Reconnected");
    }

    @Override
    public void onConnectionCreated(Session session, Connection connection) {
        Log.d(LOG_TAG, "onConnectionCreated: Connection created: " + session.getSessionId());
    }

    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {
        Log.d(LOG_TAG, "onConnectionDestroyed: Connection destroyed: " + session.getSessionId());
    }

    // when subscriber connects, subscriber is initialized and then subscriber view is displayed(if video call is there).
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.d(LOG_TAG, "onStreamReceived: New Stream Received " + stream.getStreamId() + " in session: " + session.getSessionId());

        if (isFinishing()) {
            return;
        }

        try {
            if (mSubscriber == null) {
                mSubscriber = new Subscriber.Builder(this, stream).build();
                mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                mSubscriber.setSubscriberListener(this);
                mSubscriber.setVideoListener(this);

                if (isVideoCall()) {
                    mSubscriber.setVideoStatsListener(this);
                }else{
                    mSubscriber.setAudioStatsListener(this);
                }

                mSubscriber.setSubscribeToVideo(isVideoCall());
                mSession.subscribe(mSubscriber);

                mSubscriberViewContainer.addView(mSubscriber.getView());

                if (stream.hasVideo()) {
                    mSubscriber.getView().setVisibility(View.VISIBLE);
                } else {
                    mSubscriber.getView().setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // if call disconnected, subscriber view is removed and subscriber is made null.
    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.d(LOG_TAG, "onStreamDropped: Stream Dropped: " + stream.getStreamId() + " in session: " + session.getSessionId());

        if (isFinishing()) {
            return;
        }

        try {
            if (mSubscriber != null) {
                mSubscriber = null;
                mSubscriberViewContainer.removeAllViews();
                mPublisherViewContainer.removeAllViews();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            onCallDisconnect();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: " + opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - " + opentokError.getMessage() + " in session: " + session.getSessionId());

        if (isFinishing()) {
            return;
        }

        showOpenTokError(opentokError);
    }


    /**
     * Publisher Listener methods
     *
     * @param publisherKit
     * @param stream
     */
    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.d(LOG_TAG, "onStreamCreated: Publisher Stream Created. Own stream " + stream.getStreamId());
        publisherKit.setAudioStatsListener(this);
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.d(LOG_TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream " + stream.getStreamId());
        text_publish_bandWidth.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: " + opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - " + opentokError.getMessage());
    }

    // to check video quality
    @Override
    public void onVideoStats(SubscriberKit subscriberKit, SubscriberKit.SubscriberVideoStats subscriberVideoStats) {
        if (!isFinishing()) {
            if (mStartTestTime == 0) {
                mStartTestTime = System.currentTimeMillis() / 1000;
            }

            checkVideoStats(subscriberVideoStats);
            checkVideoBandWidth();

            /**
             * check quality of the video call after TIME_VIDEO_TEST seconds
             */
            if (((System.currentTimeMillis() / 1000 - mStartTestTime) > TIME_VIDEO_TEST)) {
                checkVideoQuality();
            }
        }
    }

    private void checkPublisherVideoBandWidth() {

        try {

            text_publish_bandWidth.setVisibility(View.VISIBLE);

            System.out.println("BandWidth: "+ mVideoBwPublisher);

            if (mVideoBwPublisher / 1024 >= 1000) {
                text_publish_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                text_publish_bandWidth.setText("Streaming Stats :" + mVideoBwPublisher / (1024 *1024 ) + " mbps");
            }else{
                if (mVideoBwPublisher / 1024 >= 200) {
                    text_publish_bandWidth.setTextColor(getResources().getColor(R.color.book_consultation_orage_button));
                    text_publish_bandWidth.setText("Streaming Stats:" + mVideoBwPublisher / 1024 + " kbps");
                }else {
                    text_publish_bandWidth.setTextColor(getResources().getColor(R.color.red));
                    text_publish_bandWidth.setText("Streaming Stats :" +  mVideoBwPublisher / 1024 + " kbps");
                }
                if (mVideoBwPublisher / 1024 >= 500) {
                    text_publish_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                    text_publish_bandWidth.setText("Streaming Stats :" +mVideoBwPublisher / 1024 + " kbps");
                }

            }
        }catch(Exception e){

        }finally{

        }
    }

    private void checkVideoBandWidth() {

        try {

            text_bandWidth.setVisibility(View.INVISIBLE);

            System.out.println("BandWidth: "+ mVideoBw);

            if (mVideoBw / 1024 >= 1000) {
                text_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                text_bandWidth.setText("Subscriber Stream :" + mVideoBw / (1024 *1024 ) + " mbps");
            }else{
                if (mVideoBw / 1024 >= 200) {
                    text_bandWidth.setTextColor(getResources().getColor(R.color.book_consultation_orage_button));
                    text_bandWidth.setText("Subscriber Stream :" + mVideoBw / 1024 + " kbps");
                }else {
                    text_bandWidth.setTextColor(getResources().getColor(R.color.red));
                    text_bandWidth.setText("Subscriber Stream :" +  mVideoBw / 1024 + " kbps");
                }
                if (mVideoBw / 1024 >= 500) {
                    text_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                    text_bandWidth.setText("Subscriber Stream :" +mVideoBw / 1024 + " kbps");
                }

            }
        }catch(Exception e){

        }finally{

        }
    }


    @Override
    public void onAudioStats(SubscriberKit subscriberKit, SubscriberKit.SubscriberAudioStats subscriberAudioStats) {
        if (!isFinishing()) {
            checkAudioStats(subscriberAudioStats);
            checkAudioBandWidth();
            checkAudioQuality();
        }
    }

    private void checkAudioBandWidth() {

        try {

            text_bandWidth.setVisibility(View.INVISIBLE);

            System.out.println("BandWidth: "+ mAudioBw);

            if (mAudioBw / 1024 >= 1000) {
                text_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                text_bandWidth.setText("Subscriber Stream :" + mAudioBw / (1024 *1024 ) + " mbps");
            }else{
                if (mAudioBw / 1024 >= 25&&mAudioBw / 1024 <= 30) {
                    text_bandWidth.setTextColor(getResources().getColor(R.color.book_consultation_orage_button));
                    text_bandWidth.setText("");
                }/*else {
                   // text_bandWidth.setTextColor(getResources().getColor(R.color.red));
                    //text_bandWidth.setText("Subscriber Stream :" +  mAudioBw / 1024 + " kbps");
                }
                if (mAudioBw / 1024 >= 30) {
                    //text_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                    //text_bandWidth.setText("Subscriber Stream :" +mAudioBw / 1024 + " kbps");
                }*/

            }
        }catch(Exception e){

        }finally{

        }
    }

    private void checkPublisherAudioBandWidth() {

        try {

            text_publish_bandWidth.setVisibility(View.VISIBLE);

            System.out.println("BandWidth: "+ mAudioBwPublisher);

            if (mAudioBwPublisher / 1024 >= 1000) {
                text_publish_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                text_publish_bandWidth.setText("Streaming Stats :" + mAudioBwPublisher / (1024 *1024 ) + " mbps");
            }else{
                if (mAudioBwPublisher / 1024 >= 25) {
                    text_publish_bandWidth.setTextColor(getResources().getColor(R.color.book_consultation_orage_button));
                    text_publish_bandWidth.setText("Streaming Stats :" + mAudioBwPublisher / 1024 + " kbps");
                }else {
                    text_publish_bandWidth.setTextColor(getResources().getColor(R.color.red));
                    text_publish_bandWidth.setText("Streaming Stats :" +  mAudioBwPublisher / 1024 + " kbps");
                }

                if (mAudioBwPublisher / 1024 >= 30) {
                    text_publish_bandWidth.setTextColor(getResources().getColor(R.color.light_green));
                    text_publish_bandWidth.setText("Streaming Stats :" +mAudioBwPublisher / 1024 + " kbps");
                }

            }
        }catch(Exception e){

        }finally{

        }
    }

    @Override
    public void onAudioStats(PublisherKit publisherKit, PublisherKit.PublisherAudioStats[] publisherAudioStats) {
        if (!isFinishing()) {
            checkPublisherAudioStats(publisherAudioStats[0]);
            checkPublisherAudioBandWidth();
        }
    }

    @Override
    public void onVideoStats(PublisherKit publisherKit, PublisherKit.PublisherVideoStats[] publisherVideoStats) {
        if (!isFinishing()) {
            checkPublisherVideoStats(publisherVideoStats[0]);
            checkPublisherVideoBandWidth();
        }
    }

    // when subscriber is connected(called after onstreamCreated in session).
    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        if (!isFinishing()) {
            Log.d(LOG_TAG, "onConnected: Subscriber connected. Stream: " + subscriberKit.getStream().getStreamId());
            this.isSubscriberConnected = true;


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mLayoutConnecting.setVisibility(View.GONE);
                    text_connection_time.setVisibility(View.GONE);
                    startCallDurationTimer();
                }
            });
        }
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.d(LOG_TAG, "onDisconnected: Subscriber disconnected. Stream: " + subscriberKit.getStream().getStreamId());
        text_bandWidth.setVisibility(View.INVISIBLE);
    }

    // If subscriber is reconnected
    @Override
    public void onReconnected(SubscriberKit subscriber) {
        if (!isFinishing()) {
            mAlert.setText("Reconnected");
            mAlert.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.quality_warning_lifted));
            mAlert.setVisibility(View.VISIBLE);
        }

        Log.d(LOG_TAG, "onReconnected: Subscriber Reconnected");
    }


    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: " + opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - " + opentokError.getMessage());

        if (isFinishing()) {
            return;
        }

        showOpenTokError(opentokError);
    }

    @Override
    public void onVideoDataReceived(SubscriberKit subscriberKit) {

    }

    // If subscriber disables the video
    @Override
    public void onVideoDisabled(SubscriberKit subscriber, String reason) {

        try {
            if (mSubscriber.getView() != null) {
                mSubscriber.getView().setVisibility(View.GONE);
            }

            if (isVideoCall()) {
                displayToast("Remote Video Disabled");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "onVideoDisabled");
    }

    // If subscriber enables the video
    @Override
    public void onVideoEnabled(SubscriberKit subscriberKit, String s) {

        try {
            if (mSubscriber.getView() != null) {
                mSubscriber.getView().setVisibility(View.VISIBLE);
            }

            displayToast("Remote Video Enabled");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "onVideoEnabled");
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriberKit) {

        Log.d(LOG_TAG, "onVideoDisableWarning");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {

        Log.d(LOG_TAG, "onVideoDisableWarningLifted");
    }

    private void showOpenTokError(OpentokError opentokError) {
        //Toast.makeText(this, opentokError.getErrorDomain().name() + ": " + opentokError.getMessage() + " Please, see the logcat.", Toast.LENGTH_LONG).show();

        try {
            /*opentokError.getMessage()*/
            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();

            playDisconnectRingtone();
            stopCallNotification();
            finishActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void displayToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
    }

    // to be done(to check video quality).
    private void checkVideoStats(SubscriberKit.SubscriberVideoStats stats) {
        try {
            double videoTimestamp = stats.timeStamp / 1000;

            //initialize values
            if (mPrevVideoTimestamp == 0) {
                mPrevVideoTimestamp = videoTimestamp;
                mPrevVideoBytes = stats.videoBytesReceived;
            }

            if (videoTimestamp - mPrevVideoTimestamp >= TIME_WINDOW) {
                //calculate video packets lost ratio
                if (mPrevVideoPacketsRcvd != 0) {
                    long pl = stats.videoPacketsLost - mPrevVideoPacketsLost;
                    long pr = stats.videoPacketsReceived - mPrevVideoPacketsRcvd;
                    long pt = pl + pr;

                    if (pt > 0) {
                        mVideoPLRatio = (double) pl / (double) pt;
                    }
                }

                mPrevVideoPacketsLost = stats.videoPacketsLost;
                mPrevVideoPacketsRcvd = stats.videoPacketsReceived;

                //calculate video bandwidth
                mVideoBw = (long) ((8 * (stats.videoBytesReceived - mPrevVideoBytes)) / (videoTimestamp - mPrevVideoTimestamp));

                mPrevVideoTimestamp = videoTimestamp;
                mPrevVideoBytes = stats.videoBytesReceived;

                //Log.i(LOG_TAG, "Video bandwidth (bps): " + mVideoBw + " Video Bytes received: " + stats.videoBytesReceived + " Video packet lost: " + stats.videoPacketsLost + " Video packet loss ratio: " + mVideoPLRatio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // to be done(to check audio quality).
    private void checkAudioStats(SubscriberKit.SubscriberAudioStats stats) {
        try {
            double audioTimestamp = stats.timeStamp / 1000;

            //initialize values
            if (mPrevAudioTimestamp == 0) {
                mPrevAudioTimestamp = audioTimestamp;
                mPrevAudioBytes = stats.audioBytesReceived;
            }

            if (audioTimestamp - mPrevAudioTimestamp >= TIME_WINDOW) {
                //calculate audio packets lost ratio
                if (mPrevAudioPacketsRcvd != 0) {
                    long pl = stats.audioPacketsLost - mPrevAudioPacketsLost;
                    long pr = stats.audioPacketsReceived - mPrevAudioPacketsRcvd;
                    long pt = pl + pr;

                    if (pt > 0) {
                        mAudioPLRatio = (double) pl / (double) pt;
                    }
                }

                mPrevAudioPacketsLost = stats.audioPacketsLost;
                mPrevAudioPacketsRcvd = stats.audioPacketsReceived;

                //calculate audio bandwidth
                mAudioBw = (long) ((8 * (stats.audioBytesReceived - mPrevAudioBytes)) / (audioTimestamp - mPrevAudioTimestamp));

                mPrevAudioTimestamp = audioTimestamp;
                mPrevAudioBytes = stats.audioBytesReceived;

                Log.i(LOG_TAG, "Audio bandwidth (bps): " + mAudioBw + " Audio Bytes received: " + stats.audioBytesReceived + " Audio packet lost: " + stats.audioPacketsLost + " Audio packet loss ratio: " + mAudioPLRatio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPublisherVideoStats(PublisherKit.PublisherVideoStats stats) {
        try {
            double videoTimestamp = stats.timeStamp / 1000;

            //initialize values
            if (mPrevPublisherVideoTimestamp == 0) {
                mPrevPublisherVideoTimestamp = videoTimestamp;
                mPrevPublisherVideoBytes= stats.videoBytesSent;
            }

            if (videoTimestamp - mPrevPublisherVideoTimestamp >= TIME_WINDOW) {
                //calculate video packets lost ratio
                if (mPrevPublisherVideoPacketsSent != 0) {
                    long pl = stats.videoPacketsLost - mPrevPublisherVideoPacketsLost;
                    long pr = stats.videoPacketsSent - mPrevPublisherVideoPacketsSent;
                    long pt = pl + pr;

                    if (pt > 0) {
                        mVideoPublisherPLRatio = (double) pl / (double) pt;
                    }
                }

                mPrevPublisherVideoPacketsLost = stats.videoPacketsLost;
                mPrevPublisherVideoPacketsSent = stats.videoPacketsSent;

                //calculate video bandwidth
                mVideoBwPublisher= (long) ((8 * (stats.videoBytesSent - mPrevPublisherVideoBytes)) / (videoTimestamp - mPrevPublisherVideoTimestamp));

                mPrevPublisherVideoTimestamp = videoTimestamp;
                mPrevPublisherVideoBytes = stats.videoBytesSent;

                //Log.i(LOG_TAG, "Video bandwidth (bps): " + mVideoBw + " Video Bytes received: " + stats.videoBytesReceived + " Video packet lost: " + stats.videoPacketsLost + " Video packet loss ratio: " + mVideoPLRatio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkPublisherAudioStats(PublisherKit.PublisherAudioStats stats) {
        try {
            double audioTimestamp = stats.timeStamp / 1000;

            //initialize values
            if (mPrevPublisherAudioTimestamp == 0) {
                mPrevPublisherAudioTimestamp = audioTimestamp;
                mPrevPublisherAudioBytes = stats.audioBytesSent;
            }

            if (audioTimestamp - mPrevPublisherAudioTimestamp >= TIME_WINDOW) {
                //calculate audio packets lost ratio
                if (mPrevPublisherAudioPacketsSent != 0) {
                    long pl = stats.audioPacketsLost - mPrevPublisherAudioPacketsLost;
                    long pr = stats.audioPacketsSent - mPrevPublisherAudioPacketsSent;
                    long pt = pl + pr;

                    if (pt > 0) {
                        mAudioPLRatio = (double) pl / (double) pt;
                    }
                }

                mPrevPublisherAudioPacketsLost = stats.audioPacketsLost;
                mPrevPublisherAudioPacketsSent = stats.audioPacketsSent;

                //calculate audio bandwidth
                mAudioBwPublisher = (long) ((8 * (stats.audioBytesSent - mPrevPublisherAudioBytes)) / (audioTimestamp - mPrevPublisherAudioTimestamp));

                mPrevPublisherAudioTimestamp = audioTimestamp;
                mPrevPublisherAudioBytes = stats.audioBytesSent;

                Log.i(LOG_TAG, "Audio bandwidth (bps): " + mAudioBw + " Audio Bytes received: " + stats.audioBytesSent + " Audio packet lost: " + stats.audioPacketsLost + " Audio packet loss ratio: " + mAudioPLRatio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // to be done(to check video quality).
    private void checkVideoQuality() {
        try {
            if (mSession != null) {
                if (mVideoBw < 150000 || mVideoPLRatio > 0.03) {
                    if (mSubscriber != null && mSubscriber.getSubscribeToVideo() && mSubscriber.getStream().hasVideo()) {
                        Log.d(LOG_TAG, "Your bandwidth is too low for video");

                        mAlert.setText("Your data bandwidth is low, please book an audio appointment. Else our doctor will call you shortly using regular call");
                        mAlert.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.endCall));
                        mAlert.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(LOG_TAG, "Your bandwidth is is good for video call");
                    mAlert.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d(LOG_TAG, "Bandwidth " + mVideoBw);
        }
    }

    private void checkAudioQuality() {
        try {
            if (mSession != null) {
                Log.d(LOG_TAG, "Check audio quality stats data");

                if (mAudioBw < 25000 || mAudioPLRatio > 0.05) {
                    mAlert.setText("Your data bandwidth is low. Else our doctor will call you shortly using regular call");
                    mAlert.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.endCall));
                    mAlert.setVisibility(View.VISIBLE);
                } else {
                   // showAlert("Voice-only", "Your bandwidth is too low for video");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showDoctorProfile(int x) {
        try {
            isNavBarHidden = false;
            layout_doctor_profile.animate().translationX(x).setInterpolator(new DecelerateInterpolator(2)).start();

            if (mPublisher.getView() != null) {
                mPublisher.getView().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideDoctorProfile(int x) {
        try {
            isNavBarHidden = true;
            layout_doctor_profile.animate().translationX(x).setInterpolator(new AccelerateInterpolator(2)).start();

            if (mPublisher.getView() != null) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mPublisher.getView().setVisibility(View.VISIBLE);
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}