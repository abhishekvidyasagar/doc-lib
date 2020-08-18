package com.doconline.doconline.helper;

import com.doconline.doconline.model.User;

/**
 * Created by chiranjitbardhan on 27/02/18.
 */

public interface OnSignUpListener
{
    void onMobileSignUp(User mProfile, String mMessage);
}