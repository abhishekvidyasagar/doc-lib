package com.doconline.doconline.model;

import com.doconline.doconline.app.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_APPLIED_COUPON_CODE;
import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT;
import static com.doconline.doconline.app.Constants.KEY_ATTACHMENTS;
import static com.doconline.doconline.app.Constants.KEY_COUPON_CODE;
import static com.doconline.doconline.app.Constants.KEY_DISCOUNT_CODE;
import static com.doconline.doconline.app.Constants.KEY_PAYMENT_GATEWAY;
import static com.doconline.doconline.app.Constants.KEY_PLAN_TYPE;
import static com.doconline.doconline.app.Constants.KEY_RAZORPAY_ORDER_ID;
import static com.doconline.doconline.app.Constants.KEY_RAZORPAY_PAYMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_RAZORPAY_SUBSCRIPTION_ID;
import static com.doconline.doconline.app.Constants.KEY_RECURRING_TYPE;

/**
 * Created by chiranjitbardhan on 16/01/18.
 */

public class Subscription implements Serializable
{
    private String customer_id = "";
    private boolean can_upgrade = false;
    private boolean subscription_status = false;
    private String subscription_type = "";
    private String user_type = "";
    private String pending_subscription = "";
    private String ends_at = "";
    private String charge_at = "";
    private String created_at = "";
    private String trial_ends_at = "";

    private SubscriptionPlan mPlan = new SubscriptionPlan();
    private SubscriptionBilling mBilling = new SubscriptionBilling();


    public boolean isUpgradable()
    {
        return this.can_upgrade;
    }

    public void setUpgradable(boolean can_upgrade)
    {
        this.can_upgrade = can_upgrade;
    }


    public String getSubscriptionType()
    {
        return this.subscription_type;
    }

    public void setSubscriptionType(String subscription_type)
    {
        this.subscription_type = subscription_type;
    }


    public String getUserType() {
        return this.user_type;
    }

    public void setUserType(String user_type) {
        this.user_type = user_type;
    }


    public boolean getSubscriptionStatus()
    {
        return this.subscription_status;
    }

    public void setSubscriptionStatus(boolean subscription_status)
    {
        this.subscription_status = subscription_status;
    }


    public String getEndsAt() {
        return ends_at;
    }

    public void setEndsAt(String ends_at) {
        this.ends_at = ends_at;
    }


    public String getChargeAt() {
        return charge_at;
    }

    public void setChargeAt(String charge_at) {
        this.charge_at = charge_at;
    }


    public String getTrialEndsAt()
    {
        return trial_ends_at;
    }

    public void setTrialEndsAt(String trial_ends_at)
    {
        this.trial_ends_at = trial_ends_at;
    }


    public String getCreatedAt() {
        return this.created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }


    public SubscriptionPlan getSubscriptionPlan()
    {
        return this.mPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan mPlan) {
        this.mPlan = mPlan;
    }


    public SubscriptionBilling getSubscriptionBilling()
    {
        return this.mBilling;
    }

    public void setSubscriptionBilling(SubscriptionBilling mPlan) {
        this.mBilling = mBilling;
    }


    public String getCustomerId()
    {
        return this.customer_id;
    }

    public void setCustomerId(String customer_id)
    {
        this.customer_id = customer_id;
    }


    public String getPendingSubscription()
    {
        return this.pending_subscription;
    }

    public void setPendingSubscription(String pending_subscription)
    {
        this.pending_subscription = pending_subscription;
    }


    public static HashMap<String, Object> composePromoCodeJSON(String promo_code)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_COUPON_CODE, promo_code);

        return hashMap;
    }


    public static HashMap<String, Object> composeDiscountCodeJSON(String promo_code, String plan_type)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_DISCOUNT_CODE, promo_code);
        hashMap.put(KEY_PLAN_TYPE, plan_type);

        return hashMap;
    }

    public static HashMap<String, Object> composeOrderJSON(String promo_code, String plan_type)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        if(!promo_code.isEmpty())
        {
            hashMap.put(KEY_DISCOUNT_CODE, promo_code);
        }

        hashMap.put(KEY_PLAN_TYPE, plan_type);

        return hashMap;
    }


    public static String composePaymentJSON(String order_id, String payment_id, String paymentgateway, String appliedCouponCode)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put(KEY_RAZORPAY_ORDER_ID, order_id);
            json.put(KEY_RAZORPAY_PAYMENT_ID, payment_id);
            json.put(KEY_PAYMENT_GATEWAY, paymentgateway);
            json.put(KEY_APPLIED_COUPON_CODE, appliedCouponCode);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }


    public static String getSubscriptionJSON(AppointmentSummery summery, String payment_id, String razorpay_id, String type)
    {
        JSONObject json = new JSONObject();

        try
        {
            if(type.equalsIgnoreCase(KEY_RECURRING_TYPE))
            {
                json.put(KEY_RAZORPAY_SUBSCRIPTION_ID, razorpay_id);
            }

            else
            {
                json.put(KEY_RAZORPAY_ORDER_ID, razorpay_id);
            }

            json.put(KEY_RAZORPAY_PAYMENT_ID, payment_id);

            JSONObject json_appointment = new JSONObject();

            json_appointment.put(Constants.KEY_CALL_TYPE, summery.getCallType());
            json_appointment.put(Constants.KEY_FOR_WHOM, summery.getForWhom());

            json_appointment.put(Constants.KEY_NOTES, summery.getNotes());
            json_appointment.put(Constants.KEY_BOOKING_TYPE, summery.getBookingType());
            json_appointment.put(Constants.KEY_SCHEDULED_AT, summery.getAppointmentTime());
            json_appointment.put(Constants.KEY_CALL_CHANNEL, summery.getCallMedium());

            json_appointment.put(KEY_ATTACHMENTS, new JSONArray(summery.getAllFileURL()));
            json.put(KEY_APPOINTMENT, json_appointment);

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }
}