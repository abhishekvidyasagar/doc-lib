package com.doconline.doconline.analytics;

import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.app.MyApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GATracker
{
    public static final String GA_ACTION_REGISTRATION_SUCCESS = "account_created";

    public static final String GA_ACTION_ONETIME_PAYMENT_SUCCESS = "one_time_success";
    public static final String GA_ACTION_ONETIME_PAYMENT_FAIL = "one_time_failed";
    public static final String GA_ACTION_SUBSCRIPTION_PAYMENT_SUCCESS = "subscription_success";
    public static final String GA_ACTION_SUBSCRIPTION_PAYMENT_FAIL = "subscription_failure";

    public static final String GA_CATEGORY_PAYMENT = "payment";
    public static final String GA_CATEGORY_REGISTRATION = "registration";

    public static final String GA_LABEL_ANDROID = "Android";


    public static void trackEvent(String category, String action, String label, long value)
    {
        if(BuildConfig.DEBUG)
        {
            return;
        }

        try
        {
            Tracker mTracker = MyApplication.getInstance().getTracker(MyApplication.TrackerName.APP_TRACKER);

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value)
                    .build());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}