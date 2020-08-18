package com.doconline.doconline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.model.User;
import com.doconline.doconline.utils.VersionCheckerUtils;

import org.json.JSONObject;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

import static com.doconline.doconline.app.Constants.BETTERPLACE_THEME;
import static com.doconline.doconline.app.Constants.KEY_LOGOUT_SYNCED;
import static com.doconline.doconline.app.Constants.KEY_OLD_ACCESS_TOKEN;
import static com.doconline.doconline.app.MyApplication.prefs;


public class SplashScreenActivity extends AppCompatActivity {
    private final String LOG_TAG = SplashScreenActivity.class.getSimpleName();

    private MyApplication mController;

   /* @BindView(R.id.iv_logo)
    ImageView iv_logo;*/

    ImageView iv_logo;
    RelativeLayout main_layout;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private User mProfile = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For Sentry
        Context ctx = this.getApplicationContext();
        // Use the Sentry DSN (client key) from the Project Settings page on Sentry
        String sentryDsn = "https://299148c5de9548cda2be57a55e1ab8ea@sentry.conversionlab.online/6";
        Sentry.init(sentryDsn, new AndroidSentryClientFactory(ctx));
        // Alternatively, if you configured your DSN in a `sentry.properties`
        // file (see the configuration documentation).
        Sentry.init(new AndroidSentryClientFactory(ctx));


        if (getIntent().getBooleanExtra("CALLING_ACTIVITY", false)) {
            finishAffinity();
            return;
        }

        setContentView(R.layout.activity_splash_screen);
        //    ButterKnife.bind(this);

        this.makeActivityAppearOnLockScreen();
        this.mController = MyApplication.getInstance();

        iv_logo = findViewById(R.id.iv_logo);
        main_layout = findViewById(R.id.main_layout);

        //set Theme dynamically
        sharedPreferences = getSharedPreferences(Constants.APP_THEME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        try {
            if (getIntent().getExtras() != null) {
                if (getIntent().hasExtra("notification_type") &&
                        getIntent().getExtras().get("notification_type").toString().equalsIgnoreCase("Blogs")) {
                    Intent i = new Intent(SplashScreenActivity.this, Blogs.class);
                    i.putExtra("blog_url", getIntent().getExtras().get("blog_url").toString());
                    startActivity(i);
                    finishAffinity();
                }

                else if (getIntent().hasExtra("REQUEST_OBJECT") &&
                        getIntent().hasExtra("RESPONSE_OBJECT")){
                    String requestString = getIntent().getExtras().get("REQUEST_OBJECT").toString();
                    String responseString = getIntent().getExtras().get("RESPONSE_OBJECT").toString();
                    JSONObject requestObject = new JSONObject(requestString);
                    JSONObject responsObject = new JSONObject(responseString);
                    Log.e("AAA","request object "+requestObject);
                    Log.e("AAA","respone object "+responsObject);

                    editor.putString(Constants.KEY_CURRENT_THEME, BETTERPLACE_THEME);
                    editor.commit();
                    iv_logo.setBackground(getResources().getDrawable(R.drawable.betterplac_logo));
                    main_layout.setBackground(getResources().getDrawable(R.drawable.ic_banner));

                    mProfile.getUserAccount().setRegEmail(requestObject.get("email").toString());
                    mProfile.getUserAccount().setRegMobileNo(requestObject.get("mobile_no").toString());
                    OAuth.saveOAuthCredential(responsObject.getJSONObject("data"));
                    this.initSession(mProfile);
                    //redirect_home();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }


                else {
                    redirect_home();
                }
            }else{
                redirect_home();
            }
        }catch (Exception e){
            e.printStackTrace();
            redirect_home();
        }


        /*try
        {
            Rollbar.init(this);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        //Checking the version from firebase and setting in SharedPreferences.
        if (new InternetConnectionDetector(this).isConnected()) {
            checkVersion();

            if (!prefs.getBoolean(KEY_LOGOUT_SYNCED, false) && !prefs.getString(KEY_OLD_ACCESS_TOKEN, "").isEmpty()) {
                mController.getSession().new OkHttpHandler(MyApplication.HTTPMethod.POST.getValue(), prefs.getString(KEY_OLD_ACCESS_TOKEN, "")).execute(mController.getLogoutURL());
            }
        }


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        iv_logo.startAnimation(animation);


    }

    private void initSession(User profile)
    {
        try
        {

                profile.getUserAccount().setRegMobileNo(profile.getUserAccount().getRegEmail());
                profile.getUserAccount().setRegEmail("");


            mController.getSession().createLoginSession(profile);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*finally
        {
            finish();
        }*/
    }

    public void checkVersion() {
        try {
            final long versionCodeInMobile = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

            Log.i("VERSION_CODE", String.valueOf(versionCodeInMobile));
            Log.i("VERSION_CODE", String.valueOf(mController.getSession().getPlayStoreVersionCode()));

            if (mController.getSession().getPlayStoreVersionCode() != 0
                    && mController.getSession().getPlayStoreVersionCode() > versionCodeInMobile) {
                mController.getSession().setUpdateAlert(true);
            } else {
                mController.getSession().setUpdateAlert(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void versionChecking() {
        try {
            final String versionInMobile = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.i(LOG_TAG, "mobile version: " + versionInMobile);

            Thread t = new Thread(new Runnable() {

                public void run() {
                    try {
                        VersionCheckerUtils checkerUtils = new VersionCheckerUtils();
                        String playStoreVersion = checkerUtils.execute().get();

                        if (!playStoreVersion.isEmpty()) {
                            Log.i(LOG_TAG, "play store version: " + playStoreVersion);

                            int result = playStoreVersion.compareTo(versionInMobile);
                            Log.i(LOG_TAG, "Result" + result);

                            if (result > 0) {
                                mController.getSession().setUpdateAlert(true);
                            } else {
                                mController.getSession().setUpdateAlert(false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the activity is about to become visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Inside : ", "onStart() event");
    }

    /**
     * Called when the activity has become visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Inside : ", "onResume() event");
    }

    /**
     * Called when another activity is taking focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Inside : ", "onPause() event");
    }

    /**
     * Called when the activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Inside : ", "onStop() event");
    }

    /**
     * Called just before the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
    }

    @Override
    public void onBackPressed() {

    }


    private void makeActivityAppearOnLockScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }


    private void redirect_home() {
        new Handler().postDelayed(new Runnable() {

            /**
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                /**
                 * This method will be executed once the timer is over
                 * Start your app main activity
                 */
                Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(i);

                /**
                 * close this activity
                 */
                finish();
            }
        }, 3000);
    }

    public static void start(final Activity activity, String requestObject, String responseObject) {
        Intent i = new Intent(activity, SplashScreenActivity.class);
        i.putExtra("REQUEST_OBJECT", String.valueOf(requestObject));
        i.putExtra("RESPONSE_OBJECT", String.valueOf(responseObject));
        activity.startActivity(i);
    }
}