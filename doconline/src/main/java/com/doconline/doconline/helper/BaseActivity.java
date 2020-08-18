package com.doconline.doconline.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.R;
import com.doconline.doconline.api.OnHttpResponse;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.utils.DeviceUtils;

import static com.doconline.doconline.helper.CustomStatusBar.setStatusBarBackground;

/**
 * Created by chiranjitbardhan on 20/01/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements OnHttpResponse,
        OnDialogAction, OnPageSelection, OnTaskCompleted
{
    public static final String TAG = BaseActivity.class.getSimpleName();

   // @BindView(R.id.toolbar)
    Toolbar toolbar;

    public MyApplication mController;

    SharedPreferences sharedPreferences;
    //this is to access colors from attr
    int[] attrs = {R.attr.colorPrimary};
    TypedArray typedArray;
    public int primaryColor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //setSupportActionBar(toolbar);
        //setTitle("");

        //assert getSupportActionBar() != null;
        //getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));

        //setStatusBarBackground(this);

        this.mController = MyApplication.getInstance();

        sharedPreferences = getSharedPreferences(Constants.APP_THEME, Context.MODE_PRIVATE);
        String currentTheme = sharedPreferences.getString(Constants.KEY_CURRENT_THEME, Constants.DOCONLINE_THEME);
        if (currentTheme.equalsIgnoreCase(Constants.BETTERPLACE_THEME)){
            setTheme(R.style.Theme_App_BetterPlace);
        } else if (currentTheme.equalsIgnoreCase(Constants.TATA_THEME)){
            setTheme(R.style.Theme_App_Tata);
        } else {
            setTheme(R.style.Theme_App_DocOnline);
        }

        typedArray = this.obtainStyledAttributes(attrs);
        primaryColor = typedArray.getResourceId(0, android.R.color.black);
        typedArray.recycle();

        if(BuildConfig.DEBUG)
        {
            Log.d(TAG, "Access Token: " + mController.getSession().getOAuthDetails().getAccessToken());
            Log.d(TAG, "Device Token: " + DeviceUtils.get_push_token());
        }
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {

    }

    @Override
    public void onPositiveAction() {

    }

    @Override
    public void onNegativeAction() {

    }

    @Override
    public void onPositiveAction(int requestCode) {

    }

    @Override
    public void onPositiveAction(int requestCode, String data) {

    }

    @Override
    public void onNegativeAction(int requestCode) {

    }


    @Override
    public void onPageSelection(int position, String title) {

    }

    @Override
    public void onTaskCompleted(boolean flag, int code, String message) {

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        try
        {
            if (getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(imm != null)
                {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return super.dispatchTouchEvent(ev);
    }
}