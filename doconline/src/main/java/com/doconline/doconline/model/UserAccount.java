package com.doconline.doconline.model;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.Helper;

import java.io.Serializable;
import java.util.HashMap;

import static com.doconline.doconline.app.Constants.GRANT_TYPE;
import static com.doconline.doconline.app.Constants.GRANT_TYPE_SOCIAL;
import static com.doconline.doconline.app.Constants.KEY_CONFIRM_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_OTP_CODE;
import static com.doconline.doconline.app.Constants.KEY_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_RESET_OPTION;
import static com.doconline.doconline.app.Constants.SCOPE;

/**
 * Created by chiranjitbardhan on 17/01/18.
 */

public class UserAccount implements Serializable
{
    private Subscription mSubscription = new Subscription();

    private int mAccountId;
    private int mEnableCoupon = 0;
    private int mMobileVerified = 0;
    private int mEmailVerified = 0;

    private String mPassword = "";
    private String mConfirmPassword = "";
    private String mRegEmail = "";
    private String mRegMobileNo = "";
    private String mMRNNo = "";
    private String mFcmRegId = "";
    private String mCouponCode = "";
    private String mOTPCode = "";
    private String mMediaPartnerName = "";


    public static HashMap<String, Object> composeSocialAuthenticationJSON(String access_token, String network)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_GRANT_TYPE, GRANT_TYPE_SOCIAL);
        hashMap.put(Constants.KEY_ACCESS_TOKEN, access_token);
        hashMap.put(Constants.KEY_NETWORK, network);
        hashMap.put(Constants.KEY_CLIENT_ID, MyApplication.getInstance().getOAuthClientID());
        hashMap.put(Constants.KEY_CLIENT_SECRET, MyApplication.getInstance().getOAuthClientSecret());
        hashMap.put(Constants.KEY_SCOPE, SCOPE);
        hashMap.put(Constants.KEY_TIMEZONE, Helper.find_timezone_offset());

        return hashMap;
    }


    public static HashMap<String, Object> composeBasicAuthenticationJSON(UserAccount account)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_GRANT_TYPE, GRANT_TYPE);
        hashMap.put(Constants.KEY_USER_NAME, account.getRegEmail());
        hashMap.put(Constants.KEY_PASSWORD, account.getPassword());
        hashMap.put(Constants.KEY_CLIENT_ID, MyApplication.getInstance().getOAuthClientID());
        hashMap.put(Constants.KEY_CLIENT_SECRET, MyApplication.getInstance().getOAuthClientSecret());
        hashMap.put(Constants.KEY_SCOPE, SCOPE);
        hashMap.put(Constants.KEY_TIMEZONE, Helper.find_timezone_offset());

        return hashMap;
    }


    public static HashMap<String, Object> composeBasicAuthenticationJSON(String mobileNo, String password)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_GRANT_TYPE, GRANT_TYPE);
        hashMap.put(Constants.KEY_USER_NAME, mobileNo);
        hashMap.put(Constants.KEY_PASSWORD, password);
        hashMap.put(Constants.KEY_CLIENT_ID, MyApplication.getInstance().getOAuthClientID());
        hashMap.put(Constants.KEY_CLIENT_SECRET, MyApplication.getInstance().getOAuthClientSecret());
        hashMap.put(Constants.KEY_SCOPE, SCOPE);
        hashMap.put(Constants.KEY_TIMEZONE, Helper.findTimeZone());

        return hashMap;
    }

    public static HashMap<String, Object> composeBasicAuthenticationJSON(User profile, String otp_code)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_FIRST_NAME, profile.getFirstName());
        hashMap.put(Constants.KEY_LAST_NAME, profile.getLastName());
        hashMap.put(Constants.KEY_EMAIL, profile.getUserAccount().getRegEmail());
        hashMap.put(Constants.KEY_MOBILE_NO, profile.getUserAccount().getRegMobileNo());
        hashMap.put(Constants.KEY_PASSWORD, profile.getUserAccount().getPassword());
        hashMap.put(Constants.KEY_CONFIRM_PASSWORD, profile.getUserAccount().getConfirmPassword());
        hashMap.put(Constants.KEY_FCM_TOKEN, profile.getUserAccount().getFcmRegId());
        hashMap.put(Constants.KEY_ENABLE_COUPON, String.valueOf(profile.getUserAccount().isCouponEnabled()));

        if(!profile.getUserAccount().getCouponCode().isEmpty())
        {
            hashMap.put(Constants.KEY_COUPON_CODE, profile.getUserAccount().getCouponCode());
        }

        hashMap.put(Constants.KEY_TIMEZONE, Helper.findTimeZone());

        if(!otp_code.isEmpty())
        {
            hashMap.put(Constants.KEY_OTP_CODE, otp_code);
        }

        return hashMap;
    }


    public static HashMap<String, Object> composeEmailJSON(String email)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_EMAIL, email);

        return hashMap;
    }


    public static HashMap<String, Object> composePasswordResetOptionJSON(String mrn, String reset_option)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_EMAIL, mrn);
        hashMap.put(KEY_RESET_OPTION, reset_option);

        return hashMap;
    }

    public static HashMap<String, Object> composeEmailOTPJSON(String email, String otp_code)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_EMAIL, email);
        hashMap.put(Constants.KEY_OTP_CODE, otp_code);

        return hashMap;
    }


    public int getAccountId()
    {
        return this.mAccountId;
    }

    public void setAccountId(int mAccountId)
    {
        this.mAccountId = mAccountId;
    }


    public String getPassword()
    {
        return this.mPassword;
    }

    public void setPassword(String mPassword)
    {
        this.mPassword = mPassword;
    }


    public String getRegEmail()
    {
        return this.mRegEmail;
    }

    public void setRegEmail(String mRegEmail)
    {
        this.mRegEmail = mRegEmail;
    }


    public String getRegMobileNo()
    {
        return this.mRegMobileNo;
    }

    public void setRegMobileNo(String mRegMobileNo)
    {
        this.mRegMobileNo = mRegMobileNo;
    }


    public String getFcmRegId()
    {
        return this.mFcmRegId;
    }

    public void setFcmRegId(String mFcmRegId)
    {
        this.mFcmRegId = mFcmRegId;
    }


    public String getCouponCode()
    {
        return this.mCouponCode;
    }

    public void setCouponCode(String mCouponCode)
    {
        this.mCouponCode = mCouponCode;
    }


    public int isCouponEnabled()
    {
        return this.mEnableCoupon;
    }

    public void setCouponEnabled(int mEnableCoupon)
    {
        this.mEnableCoupon = mEnableCoupon;
    }


    public int isMobileVerified()
    {
        return this.mMobileVerified;
    }

    public void setMobileVerified(int mMobileVerified)
    {
        this.mMobileVerified = mMobileVerified;
    }

    public Subscription getSubscription()
    {
        return this.mSubscription;
    }

    public void setSubscription(Subscription mSubscription)
    {
        this.mSubscription = mSubscription;
    }


    public static HashMap<String, Object> composeOTPJSON(UserAccount account, String otp_code)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_OTP_CODE, otp_code);
        hashMap.put(KEY_EMAIL, account.getRegEmail());
        hashMap.put(KEY_PASSWORD, account.getPassword());
        hashMap.put(KEY_CONFIRM_PASSWORD, account.getConfirmPassword());

        return hashMap;
    }


    public static HashMap<String, Object> composeMobileJSON(String mobile_no)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_MOBILE_NO, mobile_no);

        return hashMap;
    }


    public static HashMap<String, Object> composeMobileOTPJSON(String mobile_no, String otp_code)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_MOBILE_NO, mobile_no);
        hashMap.put(KEY_OTP_CODE, otp_code);

        return hashMap;
    }

    public String getConfirmPassword()
    {
        return this.mConfirmPassword;
    }

    public void setConfirmPassword(String mConfirmPassword)
    {
        this.mConfirmPassword = mConfirmPassword;
    }

    public int isEmailVerified()
    {
        return this.mEmailVerified;
    }

    public void setEmailVerified(int mEmailVerified)
    {
        this.mEmailVerified = mEmailVerified;
    }

    public String getOTPCode()
    {
        return this.mOTPCode;
    }

    public void setOTPCode(String mOTPCode)
    {
        this.mOTPCode = mOTPCode;
    }

    public String getMRNNo()
    {
        return this.mMRNNo;
    }

    public void setMRNNo(String mMRNNo)
    {
        this.mMRNNo = mMRNNo;
    }
}