package com.doconline.doconline.model;

import android.util.Log;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_AGE;
import static com.doconline.doconline.app.Constants.KEY_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_AVATAR;
import static com.doconline.doconline.app.Constants.KEY_CAN_UPGRADE;
import static com.doconline.doconline.app.Constants.KEY_CONFIRM_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_COUPON_CODE;
import static com.doconline.doconline.app.Constants.KEY_CURRENCY;
import static com.doconline.doconline.app.Constants.KEY_CURRENT_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_DATE_OF_BIRTH;
import static com.doconline.doconline.app.Constants.KEY_DESCRIPTION;
import static com.doconline.doconline.app.Constants.KEY_DETAILS;
import static com.doconline.doconline.app.Constants.KEY_DISPLAY_NAME;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_ENABLE_COUPON;
import static com.doconline.doconline.app.Constants.KEY_EXTRA;
import static com.doconline.doconline.app.Constants.KEY_FAMILY_TYPE;
import static com.doconline.doconline.app.Constants.KEY_FIRST_NAME;
import static com.doconline.doconline.app.Constants.KEY_FITMEIN_ENABLED;
import static com.doconline.doconline.app.Constants.KEY_GENDER;
import static com.doconline.doconline.app.Constants.KEY_INTERVAL;
import static com.doconline.doconline.app.Constants.KEY_LANGUAGE_PREFERENCES;
import static com.doconline.doconline.app.Constants.KEY_LANGUAGE_PREFERENCES_VALUES;
import static com.doconline.doconline.app.Constants.KEY_LAST_NAME;
import static com.doconline.doconline.app.Constants.KEY_MASTERDATA;
import static com.doconline.doconline.app.Constants.KEY_MIDDLE_NAME;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_NAME;
import static com.doconline.doconline.app.Constants.KEY_NAME_PREFIX;
import static com.doconline.doconline.app.Constants.KEY_OTP_CODE;
import static com.doconline.doconline.app.Constants.KEY_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_PENDING_SUBSCRIPTION;
import static com.doconline.doconline.app.Constants.KEY_PERIOD;
import static com.doconline.doconline.app.Constants.KEY_PLAN_NAME;
import static com.doconline.doconline.app.Constants.KEY_RAZORPAY_PLAN_ID;
import static com.doconline.doconline.app.Constants.KEY_RAZORPAY_PLAN_ITEM;
import static com.doconline.doconline.app.Constants.KEY_SERVICES;
import static com.doconline.doconline.app.Constants.KEY_SHIPPING_ADDRESS;
import static com.doconline.doconline.app.Constants.KEY_STATUS;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION;
import static com.doconline.doconline.app.Constants.KEY_UNIT_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_USER;
import static com.doconline.doconline.app.Constants.KEY_USER_ID;
import static com.doconline.doconline.app.Constants.TYPE_B2C;
import static com.doconline.doconline.app.Constants.TYPE_CORPORATE;
import static com.doconline.doconline.app.Constants.TYPE_FAMILY;
import static com.doconline.doconline.app.Constants.TYPE_PROMO;

/**
 * Created by chiranjit on 28/12/16.
 */
public class User implements Serializable
{
    private UserAccount mAccount = new UserAccount();

    private HealthProfileMasterData mHealthProfileMasterData = new HealthProfileMasterData();

    private Address mAddress = new Address();

    private int mUserId;
    private String mNamePrefix = "";
    private String mFullName = "";
    private String mFirstName = "";
    private String mMiddleName = "";
    private String mLastName = "";
    private String mDateOfBirth = "";
    private String mAge;
    private String mProfilePic = "";
    private String mGender = "";
    private String mEmail = "";
    private String mPhoneNo = "";
    private String mAlternatePhoneNo = "";

    private String preferred_languages = "[]";
    private String preferred_language_value = "";

    private String mSymptoms = "";

    private Boolean fitmeInService = false;
    public Boolean hraServices = false;

    public User()
    {

    }

    public User(String mFullName)
    {
        this.mFullName = mFullName;
    }

    public User(int mUserId, String mFirstName, String mGender, String mDateOfBirth, String mProfilePic)
    {
        this.mUserId = mUserId;
        this.mFirstName = mFirstName;
        this.mGender = mGender;
        this.mDateOfBirth = mDateOfBirth;
        this.mProfilePic = mProfilePic;
    }

    public User(int mUserId, String mFullName, String mProfilePic)
    {
        this.mUserId = mUserId;
        this.mFullName = mFullName;
        this.mProfilePic = mProfilePic;
    }

    public User(int mUserId, String mFullName, String mAge, String mProfilePic)
    {
        this.mUserId = mUserId;
        this.mFullName = mFullName;
        this.mAge = mAge;
        this.mProfilePic = mProfilePic;
    }

    public User(int mUserId, String mFullName)
    {
        this.mUserId = mUserId;
        this.mFullName = mFullName;
    }


    public User(int mUserId, String mFullName, String mGender, String mDateOfBirth, String mEmail, String mProfilePic)
    {
        this.mUserId = mUserId;
        this.mFullName = mFullName;
        this.mGender = mGender;
        this.mDateOfBirth = mDateOfBirth;
        this.mEmail = mEmail;
        this.mProfilePic = mProfilePic;
    }

    public User(int mUserId, String mFullName, String mGender, String mDateOfBirth, String mAge, String mEmail, String mProfilePic)
    {
        this.mUserId = mUserId;
        this.mFullName = mFullName;
        this.mGender = mGender;
        this.mDateOfBirth = mDateOfBirth;
        this.mAge = mAge;
        this.mEmail = mEmail;
        this.mProfilePic = mProfilePic;
    }


    public void setUserId(int mUserId)
    {
        this.mUserId = mUserId;
    }

    public int getUserId()
    {
        return this.mUserId;
    }


    public void setNamePrefix(String mNamePrefix)
    {
        this.mNamePrefix = mNamePrefix;
    }

    public String getNamePrefix()
    {
        return this.mNamePrefix;
    }


    public void setFirstName(String mFirstName)
    {
        this.mFirstName = mFirstName;
    }

    public String getFirstName()
    {
        return this.mFirstName;
    }


    public void setMiddleName(String mMiddleName)
    {
        this.mMiddleName = mMiddleName;
    }

    public String getMiddleName()
    {
        return this.mMiddleName;
    }


    public void setLastName(String mLastName)
    {
        this.mLastName = mLastName;
    }

    public String getLastName()
    {
        return this.mLastName;
    }


    public void setFullName(String mFullName)
    {
        this.mFullName = mFullName;
    }

    public String getFullName()
    {
        return this.mFullName;
    }


    public void setDateOfBirth(String mDateOfBirth)
    {
        this.mDateOfBirth = mDateOfBirth;
    }

    public String getDateOfBirth()
    {
        return this.mDateOfBirth;
    }


    public void setGender(String mGender)
    {
        this.mGender = mGender;
    }

    public String getGender()
    {
        return this.mGender;
    }


    public void setProfilePic(String mProfilePic)
    {
        this.mProfilePic = mProfilePic;
    }

    public void setSymptoms(String mSymptoms){
        this.mSymptoms = mSymptoms;
    }

    public String getSymptoms(){
        return mSymptoms;
    }
    public String getProfilePic()
    {
        return this.mProfilePic;
    }



    public String getAge() {
        return this.mAge;
    }

    public void setAge(String mAge) {
        this.mAge = mAge;
    }


    public void setEmail(String mEmail)
    {
        this.mEmail = mEmail;
    }

    public String getEmail()
    {
        return this.mEmail;
    }


    public void setPhoneNo(String mPhoneNo)
    {
        this.mPhoneNo = mPhoneNo;
    }

    public String getPhoneNo()
    {
        return this.mPhoneNo;
    }


    public void setAlternatePhoneNo(String mAlternatePhoneNo)
    {
        this.mAlternatePhoneNo = mAlternatePhoneNo;
    }

    public String getAlternatePhoneNo()
    {
        return this.mAlternatePhoneNo;
    }


    public static int get_name_prefix_index(List<String> prefixes, String value)
    {
        for(int i=0; i<prefixes.size(); i++)
        {
            if(prefixes.get(i).equalsIgnoreCase(value))
            {
                return i;
            }
        }

        return 0;
    }


    public static User getUserFromJSON(String json_data)
    {
        User user = new User();

        try
        {
            JSONObject json = new JSONObject(json_data);

            int user_id = json.getInt(KEY_USER_ID);
            String name_prefix = (json.isNull(Constants.KEY_NAME_PREFIX)) ? "" : json.getString(Constants.KEY_NAME_PREFIX);
            String first_name = (json.isNull(Constants.KEY_FIRST_NAME)) ? "" : json.getString(Constants.KEY_FIRST_NAME);
            String middle_name = (json.isNull(Constants.KEY_MIDDLE_NAME)) ? "" : json.getString(Constants.KEY_MIDDLE_NAME);
            String last_name = (json.isNull(Constants.KEY_LAST_NAME)) ? "" : json.getString(Constants.KEY_LAST_NAME);
            String full_name = (json.isNull(Constants.KEY_FULL_NAME)) ? "" : json.getString(Constants.KEY_FULL_NAME);
            String date_of_birth = (json.isNull(Constants.KEY_DATE_OF_BIRTH)) ? "" : json.getString(Constants.KEY_DATE_OF_BIRTH);
            String gender = (json.isNull(Constants.KEY_GENDER)) ? "" : json.getString(Constants.KEY_GENDER);

            user.setUserId(user_id);
            user.setNamePrefix(name_prefix);
            user.setFirstName(first_name);
            user.setMiddleName(middle_name);
            user.setLastName(last_name);
            user.setFullName(full_name);
            user.setDateOfBirth(date_of_birth);
            user.setGender(gender);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return user;
    }


    public static User getUserProfileFromJSON(String json_data)
    {
        User profile = new User();

        try
        {
            JSONObject jsonObject = new JSONObject(json_data);
            JSONObject json = jsonObject.getJSONObject(KEY_USER);


            /*
            * Health profile master data
            * */
            JSONObject healthProfileMasterDataObject = jsonObject.getJSONObject(KEY_MASTERDATA);
            HealthProfileMasterData.lifeStyleActivityObject = healthProfileMasterDataObject.getJSONArray(Constants.KEY_LIFE_STYLE_ACTIVITY);
            HealthProfileMasterData.sleepDurationObject = healthProfileMasterDataObject.getJSONArray(Constants.KEY_SLEEP_DURATION);
            HealthProfileMasterData.exercisePerWeekObject = healthProfileMasterDataObject.getJSONArray(Constants.KEY_EXERCISE_PER_WEEK);
            HealthProfileMasterData.maritalStatusObject = healthProfileMasterDataObject.getJSONArray(Constants.KEY_MARITAL_STATUS);
            HealthProfileMasterData.medicalHistoryObject = healthProfileMasterDataObject.getJSONArray(Constants.KEY_MEDICAL_HISTORYY);
            HealthProfileMasterData.sleepPatternObject = healthProfileMasterDataObject.getJSONArray(Constants.KEY_SLEEP_PATTERN);
            HealthProfileMasterData.switchInMedicineObject = healthProfileMasterDataObject.getJSONArray(Constants.KEY_SWITCH_IN_MEDICINE);

            /*
            * Fitmein data
            * */
            if (jsonObject.has(KEY_SERVICES)){
                Object obj = jsonObject.get(KEY_SERVICES);
                if (obj instanceof JSONObject){
                    JSONObject servicesObject = jsonObject.getJSONObject(KEY_SERVICES);
                    Boolean fitmeInService = servicesObject.getBoolean(KEY_FITMEIN_ENABLED);
                    profile.setFitMeInServiceEnable(fitmeInService);
                    Boolean hraServices = servicesObject.getBoolean(Constants.KEY_HRA_ENABLED);
                    profile.setHRAServicesEnabled(hraServices);
                }else if (obj instanceof JSONArray){
                    Log.d("AAA","oops its empty");
                }

            }


            /**
             * Basic Details
             */
            final int mobile_verified = json.getInt(Constants.KEY_MOBILE_VERIFIED);
            final int email_verified = json.getInt(Constants.KEY_IS_VERIFIED);
            final String mrn_no = (json.isNull(Constants.KEY_MRN_NO)) ? "" : json.getString(Constants.KEY_MRN_NO);
            final String mobile_number = (json.isNull(Constants.KEY_MOBILE_NO)) ? "" : json.getString(Constants.KEY_MOBILE_NO);
            final String full_name = (json.isNull(Constants.KEY_FULL_NAME)) ? "" : json.getString(Constants.KEY_FULL_NAME);
            final String email = (json.isNull(Constants.KEY_EMAIL)) ? "" : json.getString(Constants.KEY_EMAIL);
            final String avatar_url = (json.isNull(Constants.KEY_AVATAR_URL)) ? "" : json.getString(Constants.KEY_AVATAR_URL);

            profile.getUserAccount().setMobileVerified(mobile_verified);
            profile.getUserAccount().setEmailVerified(email_verified);
            profile.getUserAccount().setMRNNo(mrn_no);
            profile.getUserAccount().setRegMobileNo(mobile_number);
            profile.getUserAccount().setRegEmail(email);

            profile.setFullName(full_name);
            profile.setProfilePic(avatar_url);

            /**
             * Language Preferences
             */
            json = json.getJSONObject(KEY_DETAILS);

            profile.setUserId(json.getInt(KEY_USER_ID));
            profile.setGender(json.getString(KEY_GENDER));
            profile.setDateOfBirth(json.getString(KEY_DATE_OF_BIRTH));
            profile.setAge(json.getString(KEY_AGE));
            profile.getUserAccount().setAccountId(json.getInt(KEY_USER_ID));
            profile.getAddress().setFullAddress((json.isNull(Constants.KEY_ADDRESS_1)) ? "" : json.getString(Constants.KEY_ADDRESS_1));
            profile.getAddress().setAddress_1((json.isNull(Constants.KEY_ADDRESS_1)) ? "" : json.getString(Constants.KEY_ADDRESS_1));
            profile.getAddress().setAddress_2((json.isNull(Constants.KEY_ADDRESS_2)) ? "" : json.getString(Constants.KEY_ADDRESS_2));
            profile.getAddress().setPincode((json.isNull(Constants.KEY_PINCODE)) ? "" : json.getString(Constants.KEY_PINCODE));

            final String language_preferences = (json.isNull(KEY_LANGUAGE_PREFERENCES)) ? "[]" : json.getString(KEY_LANGUAGE_PREFERENCES);
            profile.setPreferredLanguages(language_preferences);

            for (int i=0; i<json.getJSONArray(KEY_LANGUAGE_PREFERENCES_VALUES).length(); i++)
            {
                String value = json.getJSONArray(KEY_LANGUAGE_PREFERENCES_VALUES).getString(i);
                profile.setPreferredLanguageValue(value);
            }

            /**
             * Subscription Plan
             */
            json = jsonObject.getJSONObject(KEY_SUBSCRIPTION);
            final boolean subscribed = json.getBoolean(Constants.KEY_SUBSCRIBED);
            final String type = (json.isNull(Constants.KEY_TYPE)) ? "" : json.getString(Constants.KEY_TYPE);
            final String user_type = (json.isNull(Constants.KEY_USER_TYPE)) ? "" : json.getString(Constants.KEY_USER_TYPE);

            profile.getUserAccount().getSubscription().setSubscriptionStatus(subscribed);
            profile.getUserAccount().getSubscription().setSubscriptionType(type);
            profile.getUserAccount().getSubscription().setUserType(user_type);

            if(json.has(KEY_CAN_UPGRADE))
            {
                final boolean can_upgrade = json.getBoolean(KEY_CAN_UPGRADE);
                profile.getUserAccount().getSubscription().setUpgradable(can_upgrade);
            }

            if (subscribed && type.equalsIgnoreCase(TYPE_PROMO))
            {
                json = json.getJSONObject(KEY_DETAILS);

                final String ends_at = (json.isNull(Constants.KEY_ENDS_AT)) ? "" : json.getString(Constants.KEY_ENDS_AT);
                profile.getUserAccount().getSubscription().setEndsAt(ends_at);
            }

            else if(subscribed && (type.equalsIgnoreCase(TYPE_B2C) || type.equalsIgnoreCase(TYPE_FAMILY)))
            {
                JSONObject json_details = json.getJSONObject(KEY_DETAILS);

                String plan_id = (json_details.isNull(Constants.KEY_RAZORPAY_PLAN_ID)) ? "" : json_details.getString(KEY_RAZORPAY_PLAN_ID);
                int interval = (json_details.isNull(KEY_INTERVAL)) ? 1 : json_details.getInt(KEY_INTERVAL);
                String period = (json_details.isNull(KEY_PERIOD)) ? "" : json_details.getString(KEY_PERIOD);
                String payment_status = (json_details.isNull(KEY_STATUS)) ? "" : json_details.getString(KEY_STATUS);

                String created_at = (json_details.isNull(Constants.KEY_CREATED_AT)) ? "" : json_details.getString(Constants.KEY_CREATED_AT);
                String ends_at = (json_details.isNull(Constants.KEY_ENDS_AT)) ? "" : json_details.getString(Constants.KEY_ENDS_AT);
                String charge_at = (json_details.isNull(Constants.KEY_CHARGE_AT)) ? "" : json_details.getString(Constants.KEY_CHARGE_AT);
                String trial_ends_at = (json_details.isNull(Constants.KEY_TRIAL_ENDS_AT)) ? "" : json_details.getString(Constants.KEY_TRIAL_ENDS_AT);

                json_details = json_details.getJSONObject(KEY_RAZORPAY_PLAN_ITEM);

                String plan_name = json_details.getString(KEY_NAME);
                String plan_description = (json_details.isNull(KEY_DESCRIPTION)) ? "" : json_details.getString(KEY_DESCRIPTION);
                String currency = (json_details.isNull(KEY_CURRENCY)) ? "NIL" : json_details.getString(KEY_CURRENCY);

                double amount = json_details.getDouble(KEY_AMOUNT);
                double unit_amount = json_details.getDouble(KEY_UNIT_AMOUNT);

                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPlanId(plan_id);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setInterval(interval);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPeriod(period);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPaymentStatus(payment_status);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPlanName(plan_name);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPlanDescription(plan_description);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setCurrency(currency);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setAmount(amount);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setUnitAmount(unit_amount);

                profile.getUserAccount().getSubscription().setCreatedAt(created_at);
                profile.getUserAccount().getSubscription().setChargeAt(charge_at);
                profile.getUserAccount().getSubscription().setEndsAt(ends_at);
                profile.getUserAccount().getSubscription().setTrialEndsAt(trial_ends_at);
            }

            else if(subscribed && type.equalsIgnoreCase(KEY_FAMILY_TYPE))
            {
                JSONObject json_details = json.getJSONObject(KEY_DETAILS);

                String ends_at = (json_details.isNull(Constants.KEY_ENDS_AT)) ? "" : json_details.getString(Constants.KEY_ENDS_AT);
                String charge_at = (json_details.isNull(Constants.KEY_CHARGE_AT)) ? "" : json_details.getString(Constants.KEY_CHARGE_AT);
                String plan_name = json_details.getString(KEY_PLAN_NAME);

                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPlanName(plan_name);
                profile.getUserAccount().getSubscription().setChargeAt(charge_at);
                profile.getUserAccount().getSubscription().setEndsAt(ends_at);
            }

            else if(subscribed && type.equalsIgnoreCase(TYPE_CORPORATE)) {
                JSONObject json_details = json.getJSONObject(KEY_DETAILS);

                int interval = (json_details.isNull(KEY_INTERVAL)) ? 1 : json_details.getInt(KEY_INTERVAL);
                String period = (json_details.isNull(KEY_PERIOD)) ? "" : json_details.getString(KEY_PERIOD);
                String payment_status = (json_details.isNull(KEY_STATUS)) ? "" : json_details.getString(KEY_STATUS);

                String created_at = (json_details.isNull(Constants.KEY_CREATED_AT)) ? "" : json_details.getString(Constants.KEY_CREATED_AT);
                String ends_at = (json_details.isNull(Constants.KEY_ENDS_AT)) ? "" : json_details.getString(Constants.KEY_ENDS_AT);
                String charge_at = (json_details.isNull(Constants.KEY_CHARGE_AT)) ? "" : json_details.getString(Constants.KEY_CHARGE_AT);
                String trial_ends_at = (json_details.isNull(Constants.KEY_TRIAL_ENDS_AT)) ? "" : json_details.getString(Constants.KEY_TRIAL_ENDS_AT);
                String plan_name = json_details.getString(KEY_PLAN_NAME);

                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPlanName(plan_name);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setInterval(interval);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPeriod(period);
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPaymentStatus(payment_status);

                profile.getUserAccount().getSubscription().setCreatedAt(created_at);
                profile.getUserAccount().getSubscription().setChargeAt(charge_at);
                profile.getUserAccount().getSubscription().setEndsAt(ends_at);
                profile.getUserAccount().getSubscription().setTrialEndsAt(trial_ends_at);
            }

            else
            {
                profile.getUserAccount().getSubscription().setEndsAt("");
                profile.getUserAccount().getSubscription().getSubscriptionPlan().setPaymentStatus("");
            }

            if(json.has(KEY_PENDING_SUBSCRIPTION) && !json.isNull(KEY_PENDING_SUBSCRIPTION))
            {
                json = json.getJSONObject(KEY_PENDING_SUBSCRIPTION);
                json = json.getJSONObject(KEY_EXTRA);

                profile.getUserAccount().getSubscription().setPendingSubscription(json.getString(KEY_DISPLAY_NAME));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            MyApplication.prefs.edit().putString(KEY_SHIPPING_ADDRESS, new Gson().toJson(profile.getAddress())).apply();
            MyApplication.getInstance().getSession().update(profile);
        }

        return profile;
    }

    public void setHRAServicesEnabled(Boolean hraServices) {
        this.hraServices = hraServices;
    }
    public Boolean getHRAServicesEnabled(){
        return hraServices;
    }

    public void setFitMeInServiceEnable(Boolean fitmeInService) {
        this.fitmeInService = fitmeInService;
    }

    public Boolean getFitMeInServiceEnable() {
        return fitmeInService;
    }

    private HealthProfileMasterData getMasterData() {
        return mHealthProfileMasterData;
    }


    public static HashMap<String, Object> composeUserJSON(User profile)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        if(!profile.getNamePrefix().isEmpty())
        {
            hashMap.put(KEY_NAME_PREFIX, profile.getNamePrefix());
        }

        hashMap.put(KEY_FIRST_NAME, profile.getFirstName());
        hashMap.put(KEY_MIDDLE_NAME, profile.getMiddleName());
        hashMap.put(KEY_LAST_NAME, profile.getLastName());
        hashMap.put(KEY_DATE_OF_BIRTH, profile.getDateOfBirth());
        hashMap.put(KEY_GENDER, profile.getGender());

        hashMap.put(KEY_ENABLE_COUPON, String.valueOf(profile.getUserAccount().isCouponEnabled()));
        hashMap.put(KEY_COUPON_CODE, profile.getUserAccount().getCouponCode());

        return hashMap;
    }


    public static HashMap<String, Object> composeGuestUserJSON(User mProfile)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_FIRST_NAME, mProfile.getFirstName());
        hashMap.put(KEY_LAST_NAME, mProfile.getLastName());
        hashMap.put(KEY_EMAIL, mProfile.getEmail());
        hashMap.put(KEY_MOBILE_NO, mProfile.getPhoneNo());
        hashMap.put(KEY_PASSWORD, mProfile.getUserAccount().getPassword());
        hashMap.put(KEY_CONFIRM_PASSWORD, mProfile.getUserAccount().getConfirmPassword());

        if(!mProfile.getUserAccount().getOTPCode().isEmpty())
        {
            hashMap.put(KEY_OTP_CODE, mProfile.getUserAccount().getOTPCode());
        }
        return hashMap;
    }


    public static HashMap<String, Object> composePasswordJSON(String current_password, String password, String confirm_password)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_CURRENT_PASSWORD, current_password);
        hashMap.put(KEY_PASSWORD, password);
        hashMap.put(KEY_CONFIRM_PASSWORD, confirm_password);

        return hashMap;
    }


    public static HashMap<String, Object> composePasswordJSON(String password)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_PASSWORD, password);

        return hashMap;
    }


    public static HashMap<String, Object> composeAvatarJSON(String path)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_AVATAR, path);

        return hashMap;
    }


    public UserAccount getUserAccount()
    {
        return this.mAccount;
    }

    public void setUserAccount(UserAccount mAccount)
    {
        this.mAccount = mAccount;
    }


    public Address getAddress()
    {
        return this.mAddress;
    }

    public void setAddress(Address mAddress)
    {
        this.mAddress = mAddress;
    }


    public String getPreferredLanguages()
    {
        return this.preferred_languages;
    }

    public void setPreferredLanguages(String preferred_languages)
    {
        this.preferred_languages = preferred_languages;
    }

    public String getPreferredLanguageValue()
    {
        return this.preferred_language_value;
    }

    public void setPreferredLanguageValue(String preferred_language_value)
    {
        this.preferred_language_value = preferred_language_value;
    }
}
