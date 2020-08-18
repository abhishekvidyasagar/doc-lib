package com.doconline.doconline.helper;

import com.doconline.doconline.model.SubscriptionPlan;
import com.doconline.doconline.model.User;

/**
 * Created by chiranjitbardhan on 15/02/18.
 */

public interface OnBookConsultationListener extends OnPageSelection
{
    void onGuestDetails(User mProfile);
    void onSubscribed(SubscriptionPlan mPlan);
}