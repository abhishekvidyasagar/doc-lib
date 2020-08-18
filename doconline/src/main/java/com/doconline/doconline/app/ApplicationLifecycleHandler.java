package com.doconline.doconline.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import static com.doconline.doconline.app.MyApplication.prefs;

/**
 * Created by chiranjit on 21/11/16.
 */

class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();
    private static boolean isInBackground = false;


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.wtf(TAG, "app went to onCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.wtf(TAG, "app went to onStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.wtf(TAG, "app went to foreground");
        if(isInBackground){
            isInBackground = false;
        }

        prefs.edit().putBoolean(Constants.IS_IN_BACKGROUND, isInBackground).apply();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.wtf(TAG, "app went to onPause");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.wtf(TAG, "app went to onStop");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.wtf(TAG, "app went to onDestroy");
    }

    @Override
    public void onLowMemory() {
        Log.wtf(TAG, "app went to onLowMemory");
    }

    @Override
    public void onTrimMemory(int i) {

        if(i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
            Log.wtf(TAG, "app went to background");
            isInBackground = true;
            prefs.edit().putBoolean(Constants.IS_IN_BACKGROUND, isInBackground).apply();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.wtf(TAG, "app went to onConfigurationChanged");
    }
}