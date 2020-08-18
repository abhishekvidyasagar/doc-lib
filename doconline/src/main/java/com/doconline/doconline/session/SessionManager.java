package com.doconline.doconline.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.doconline.doconline.HomeActivity;
import com.doconline.doconline.R;
import com.doconline.doconline.api.MyOkHttpClient;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.model.User;
import com.doconline.doconline.sqlite.SQLiteHelper;
import com.doconline.doconline.utils.DeviceUtils;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.doconline.doconline.app.Constants.ACCEPT;
import static com.doconline.doconline.app.Constants.AUTHORIZATION;
import static com.doconline.doconline.app.Constants.CONTENT_TYPE;
import static com.doconline.doconline.app.Constants.DEFAULT_APPOINTMENT_CALLBACK_TIME_LIMIT;
import static com.doconline.doconline.app.Constants.DEFAULT_BASE_URL;
import static com.doconline.doconline.app.Constants.DEFAULT_CLIENT_ID;
import static com.doconline.doconline.app.Constants.DEFAULT_CLIENT_SECRET;
import static com.doconline.doconline.app.Constants.DEFAULT_ENV_PREFIX;
import static com.doconline.doconline.app.Constants.DEFAULT_FB_CLIENT_ID;
import static com.doconline.doconline.app.Constants.DEFAULT_FILE_ATTACHMENT_LIMIT;
import static com.doconline.doconline.app.Constants.DEFAULT_FILE_SIZE;
import static com.doconline.doconline.app.Constants.DEFAULT_OPENTOK_KEY;
import static com.doconline.doconline.app.Constants.DEFAULT_RAZORPAY_KEY;
import static com.doconline.doconline.app.Constants.DEFAULT_RINGING_DURATION;
import static com.doconline.doconline.app.Constants.DOCONLINE_API;
import static com.doconline.doconline.app.Constants.DOCONLINE_API_VERSION;
import static com.doconline.doconline.app.Constants.KEY_ACCESS_TOKEN;
import static com.doconline.doconline.app.Constants.KEY_AGE;
import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT_CALLBACK_TIME_LIMIT;
import static com.doconline.doconline.app.Constants.KEY_AVATAR_URL;
import static com.doconline.doconline.app.Constants.KEY_BADGE_COUNT;
import static com.doconline.doconline.app.Constants.KEY_BASE_URL;
import static com.doconline.doconline.app.Constants.KEY_CAN_UPGRADE;
import static com.doconline.doconline.app.Constants.KEY_CLIENT_ID;
import static com.doconline.doconline.app.Constants.KEY_CLIENT_SECRET;
import static com.doconline.doconline.app.Constants.KEY_COUNTRY_LIST;
import static com.doconline.doconline.app.Constants.KEY_CUSTOMER_CARE_NUMBER;
import static com.doconline.doconline.app.Constants.KEY_DEVICE_SYNCED;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CARTCOUNT;
import static com.doconline.doconline.app.Constants.KEY_DOB;
import static com.doconline.doconline.app.Constants.KEY_EHR_DOCUMENT_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_EMAIL_STATUS;
import static com.doconline.doconline.app.Constants.KEY_ENV_PREFIX;
import static com.doconline.doconline.app.Constants.KEY_EXPIRES_IN;
import static com.doconline.doconline.app.Constants.KEY_FAMILY_MEMBERS_ALLOWED;
import static com.doconline.doconline.app.Constants.KEY_FAMILY_MEMBERS_CONFIG;
import static com.doconline.doconline.app.Constants.KEY_FB_CLIENT_ID;
import static com.doconline.doconline.app.Constants.KEY_FILE_ATTACHMENT_LIMIT;
import static com.doconline.doconline.app.Constants.KEY_FITMEIN_ENABLED;
import static com.doconline.doconline.app.Constants.KEY_FULL_NAME;
import static com.doconline.doconline.app.Constants.KEY_GENDER;
import static com.doconline.doconline.app.Constants.KEY_HRA_ENABLED;
import static com.doconline.doconline.app.Constants.KEY_IS_HOTLINE;
import static com.doconline.doconline.app.Constants.KEY_LANGUAGE_PREFERENCES;
import static com.doconline.doconline.app.Constants.KEY_LANGUAGE_PREFERENCES_VALUES;
import static com.doconline.doconline.app.Constants.KEY_LOGOUT_SYNCED;
import static com.doconline.doconline.app.Constants.KEY_MAX_FILE_SIZE;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_STATUS;
import static com.doconline.doconline.app.Constants.KEY_MRN_NO;
import static com.doconline.doconline.app.Constants.KEY_NAME_PREFIX_LIST;
import static com.doconline.doconline.app.Constants.KEY_OLD_ACCESS_TOKEN;
import static com.doconline.doconline.app.Constants.KEY_OPENTOK_API_KEY;
import static com.doconline.doconline.app.Constants.KEY_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_PASSWORD_STATUS;
import static com.doconline.doconline.app.Constants.KEY_PLAY_STORE_VERSION_CODE;
import static com.doconline.doconline.app.Constants.KEY_RAZORPAY_API_KEY;
import static com.doconline.doconline.app.Constants.KEY_REFRESH_TOKEN;
import static com.doconline.doconline.app.Constants.KEY_RINGING_DURATION;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION_PAYMENT_STATUS;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION_PLAN_NAME;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION_STATUS;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION_TYPE;
import static com.doconline.doconline.app.Constants.KEY_TOKEN_TYPE;
import static com.doconline.doconline.app.Constants.KEY_TRIAL_ENDS_ON;
import static com.doconline.doconline.app.Constants.KEY_USER_ID;
import static com.doconline.doconline.app.Constants.KEY_USER_TYPE;
import static com.doconline.doconline.app.Constants.KEY_VITAL_INFO_URL;
import static com.doconline.doconline.app.Constants.TABLE_ALLERGIES;
import static com.doconline.doconline.app.Constants.TABLE_HEALTH_PROFILE;
import static com.doconline.doconline.app.Constants.TABLE_MEDICATIONS;
import static com.doconline.doconline.app.Constants.TABLE_NOTIFICATIONS;
import static com.doconline.doconline.app.Constants.TABLE_PROFILE;
import static com.doconline.doconline.app.Constants.VITAL_INFO_URL;
import static com.doconline.doconline.app.MyApplication.prefs;


public class SessionManager
{
	private SQLiteHelper helper;
	private MyApplication mController;
	private SharedPreferences pref; // Shared Preferences
	private SharedPreferences oauth_pref; // Shared Preferences
	private SharedPreferences fcm_pref; // Shared Preferences
	private SharedPreferences updation_pref; //Shared Preferences

	private Editor editor; // Editor for Shared preferences
	private Editor oauth_editor; // Editor for Shared preferences
	private Editor fcm_editor; // Editor for Shared preferences
	private Editor updation_editor; //Editor for shared preferences

	private Context _context; // Context
	
	private static final String PREF_NAME = "DocOnlinePref"; // Shared Pref file name
	private static final String OAUTH_PREF_NAME = "OauthPref"; // Shared Pref file name
	private static final String FCM_PREF_NAME = "FCMPref"; // Shared Pref file name
	private static final String UPDATION_PREF_NAME = "UpdationPref"; // Shared Pref file name
	private static final String IS_LOGIN = "IsLoggedIn"; // All Shared Preferences Keys


	@SuppressLint("CommitPrefEdits") 
	public SessionManager(Context context) // Constructor
	{
		this._context = context;
		this.helper = new SQLiteHelper(context);
		this.mController = MyApplication.getInstance();

		this.pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		this.oauth_pref = _context.getSharedPreferences(OAUTH_PREF_NAME, Context.MODE_PRIVATE);
		this.fcm_pref = _context.getSharedPreferences(FCM_PREF_NAME, Context.MODE_PRIVATE);
		this.updation_pref = _context.getSharedPreferences(UPDATION_PREF_NAME, Context.MODE_PRIVATE);

		this.editor = pref.edit();
		this.oauth_editor = oauth_pref.edit();
		this.fcm_editor = fcm_pref.edit();
		this.updation_editor = updation_pref.edit();
	}

	/**
	 * Create login session
	 */
	public void createLoginSession(User profile)
	{
		/**
		 * Storing login value as TRUE
		 */
		editor.putBoolean(IS_LOGIN, true);

		editor.putString(KEY_FULL_NAME, profile.getFullName());
		editor.putString(KEY_EMAIL, profile.getUserAccount().getRegEmail());
		editor.putString(KEY_PASSWORD, profile.getUserAccount().getPassword());
		editor.putString(KEY_MOBILE_NO, profile.getUserAccount().getRegMobileNo());

		editor.commit();

		/**
		 * After login redirect user to HomeActivity
 		 */
		Intent i = new Intent(_context, HomeActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		_context.startActivity(i);
	}

	/**
	 * Change Password
	 */
	public void changePassword(String password)
	{
		editor.putString(KEY_PASSWORD, password);
		editor.commit(); // commit changes
	}

	/**
	 * Change MRN No
	 */
	public void changeMRN(String mrn)
	{
		editor.putString(KEY_MRN_NO, mrn);
		editor.commit(); // commit changes
	}

	/**
	 * Change Mobile No
	 */
	public void changeMobileNo(String mobile_no)
	{
		editor.putString(KEY_MOBILE_NO, mobile_no);
		editor.commit(); // commit changes
	}

	/**
	 * Change Email
	 */
	public void changeEmail(String email)
	{
		editor.putString(KEY_EMAIL, email);
		editor.commit(); // commit changes
	}

	public void setUpdateAlert(Boolean value)
	{
		updation_editor.putBoolean(UPDATION_PREF_NAME, value);
		updation_editor.commit(); // commit changes
	}

	public boolean getUpdateAlert()
	{
		return updation_pref.getBoolean(UPDATION_PREF_NAME,false);
	}

	/**
	 * Create Oauth session
	 */
	public void putOAuthDetails(OAuth oAuth)
	{
		oauth_editor.putString(KEY_TOKEN_TYPE, oAuth.getTokenType());
		oauth_editor.putLong(KEY_EXPIRES_IN, oAuth.getExpiresIn());
		oauth_editor.putString(KEY_ACCESS_TOKEN, oAuth.getAccessToken());
		oauth_editor.putString(KEY_REFRESH_TOKEN, oAuth.getRefreshToken());
		oauth_editor.commit();
	}


	public OAuth getOAuthDetails()
	{
		HashMap<String, String> oauth = new HashMap<>();

		oauth.put(KEY_TOKEN_TYPE, oauth_pref.getString(KEY_TOKEN_TYPE, ""));
		oauth.put(KEY_EXPIRES_IN, String.valueOf(oauth_pref.getLong(KEY_EXPIRES_IN, 0)));
		oauth.put(KEY_ACCESS_TOKEN, oauth_pref.getString(KEY_ACCESS_TOKEN, ""));
		oauth.put(KEY_REFRESH_TOKEN, oauth_pref.getString(KEY_REFRESH_TOKEN, ""));

		return new OAuth(oauth.get(KEY_TOKEN_TYPE), oauth.get(KEY_ACCESS_TOKEN), oauth.get(KEY_REFRESH_TOKEN), Long.valueOf(oauth.get(KEY_EXPIRES_IN)));
	}


	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 */
	public boolean checkLogin()
	{
		if(!this.isLoggedIn()) // Check login status
		{
			
			Intent i = new Intent(_context, MainActivity.class); // user is not logged in redirect him to Login Activity
			
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Closing all the Activities
			
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add new Flag to start new Activity
			
			_context.startActivity(i); // Staring Login Activity*/
			
			return false;
			
		}
		
		return true;
	}

	/**
	 * Clear session details
	 */
	public void logoutUser()
	{

		if(new InternetConnectionDetector(_context).isConnected())
		{
			new OkHttpHandler(MyApplication.HTTPMethod.POST.getValue(), getOAuthDetails().getAccessToken()).execute(mController.getLogoutURL());
		}

	}


	public void clearSession()
	{
		/**
		 * Logout sync status
		 * Store old token for logout again if fail
		 */
		prefs.edit().putBoolean(KEY_LOGOUT_SYNCED, false).apply();
		prefs.edit().putString(KEY_OLD_ACCESS_TOKEN, getOAuthDetails().getAccessToken()).apply();

		/**
		 * Clearing all data from Shared Preferences
		 */
		editor.clear();
		editor.commit();

		/**
		 * Clear Oauth details
		 */
		oauth_editor.clear();
		oauth_editor.commit();

		clear_device_info();

		/**
		 * Clear SQLite table data
		 */
		helper.remove_all(TABLE_MEDICATIONS);
		helper.remove_all(TABLE_ALLERGIES);
		helper.remove_all(TABLE_HEALTH_PROFILE);
		helper.remove_all(TABLE_PROFILE);
		helper.remove_all(TABLE_NOTIFICATIONS);

		/**
		 * Clear array list values
		 */
		mController.clearAppointment();
		mController.clearFamilyMember();
	}


	/**
	 * Clear device menu_info sync status
	 */
	private void clear_device_info()
	{
		prefs.edit().putBoolean(KEY_DEVICE_SYNCED, false).apply();
	}

	/**
	 * Quick check for login
	 */
	public boolean isLoggedIn() // Get Login State
	{
		return pref.getBoolean(IS_LOGIN, false);
	}


	/**
	 * Set Avatar Link
	 */
	public void putAvatarLink(String link)
	{
		editor.putString(KEY_AVATAR_URL, link);
		editor.commit(); // commit changes
	}

	/**
	 * Get Avatar Link
	 */
	public String getAvatarLink()
	{
		return pref.getString(KEY_AVATAR_URL, "");
	}

	/**
	 * Set Badge Count
	 */
	public void putBadgeCount(int count)
	{
		editor.putInt(KEY_BADGE_COUNT, count);
		editor.commit(); // commit changes
	}

	/**
	 * Get Badge Count
	 */
	public int getBadgeCount()
	{
		return pref.getInt(KEY_BADGE_COUNT, 0);
	}

	/**
	 * Set Badge Count
	 */
	public void putDiagnosticsCartCount(int count)
	{
		editor.putInt(KEY_DIAGNOSTICS_CARTCOUNT, count);
		editor.commit(); // commit changes
	}

	/* Get Cart Count */
	public int getDiagnosticsCartCount()
	{
		return pref.getInt(KEY_DIAGNOSTICS_CARTCOUNT, 0);
	}

	/**
	 * Set Password Status
	 */
	public void putPasswordStatus(boolean status)
	{
		editor.putBoolean(KEY_PASSWORD_STATUS, status);
		editor.commit(); // commit changes
	}

	/**
	 * Get Password Status
	 */
	public boolean getPasswordStatus()
	{
		return pref.getBoolean(KEY_PASSWORD_STATUS, false);
	}

	/**
	 * Get Mobile Status
	 */
	public boolean getMobileStatus()
	{
		return pref.getBoolean(KEY_MOBILE_STATUS, false);
	}


	/**
	 * Set Mobile Status
	 */
	public void putMobileStatus(boolean status)
	{
		editor.putBoolean(KEY_MOBILE_STATUS, status);
		editor.commit(); // commit changes
	}


	/**
	 * Get Email Status
	 */
	public boolean getEmailStatus()
	{
		return pref.getBoolean(KEY_EMAIL_STATUS, false);
	}


	/**
	 * Set Email Status
	 */
	public void putEmailStatus(boolean status)
	{
		editor.putBoolean(KEY_EMAIL_STATUS, status);
		editor.commit(); // commit changes
	}

	/**
	 * Get Subscription Status
	 */
	public boolean getSubscriptionStatus()
	{
		return pref.getBoolean(KEY_SUBSCRIPTION_STATUS, false);
	}

	/**
	 * Set Subscription Status
	 */
	public void putSubscriptionStatus(boolean status)
	{
		editor.putBoolean(KEY_SUBSCRIPTION_STATUS, status);
		editor.commit(); // commit changes
	}


	/**
	 * Get Subscription Payment Status
	 */
	public String getSubscriptionPaymentStatus()
	{
		return pref.getString(KEY_SUBSCRIPTION_PAYMENT_STATUS, "");
	}

	/**
	 * Set Subscription Payment Status
	 */
	public void putSubscriptionPaymentStatus(String status)
	{
		editor.putString(KEY_SUBSCRIPTION_PAYMENT_STATUS, status);
		editor.commit(); // commit changes
	}

	/**
	 * Get Subscription Upgrade Status
	 */
	public boolean getSubscriptionUpgradeStatus()
	{
		return pref.getBoolean(KEY_CAN_UPGRADE, false);
	}

	/**
	 * Set Subscription Upgrade Status
	 */
	public void putSubscriptionUpgradeStatus(boolean status)
	{
		editor.putBoolean(KEY_CAN_UPGRADE, status);
		editor.commit(); // commit changes
	}

	/**
	 * Get Subscription Type
	 */
	public String getSubscriptionType()
	{
		return pref.getString(KEY_SUBSCRIPTION_TYPE, "");
	}

	/**
	 * Set Subscription Type
	 */
	public void putSubscriptionType(String type)
	{
		editor.putString(KEY_SUBSCRIPTION_TYPE, type);
		editor.commit(); // commit changes
	}

	/**
	 * Get User Type
	 */
	public String getUserType()
	{
		return pref.getString(KEY_USER_TYPE, "");
	}

	/**
	 * Set User Type
	 */
	public void putUserType(String type)
	{
		editor.putString(KEY_USER_TYPE, type);
		editor.commit(); // commit changes
	}

	/**
	 * Get Subscription Plan Name
	 */
	public String getSubscriptionPlanName()
	{
		return pref.getString(KEY_SUBSCRIPTION_PLAN_NAME, "");
	}

	/**
	 * Set Subscription Plan Name
	 */
	public void putSubscriptionPlanName(String name)
	{
		editor.putString(KEY_SUBSCRIPTION_PLAN_NAME, name);
		editor.commit(); // commit changes
	}

	/**
	 * Get Trail Ends Date
	 */
	public String getTrialEndsDate()
	{
		return pref.getString(KEY_TRIAL_ENDS_ON, "");
	}

	/**
	 * Set Trial Ends Date
	 */
	public void putTrialEndsDate(String date)
	{
		editor.putString(KEY_TRIAL_ENDS_ON, date);
		editor.commit(); // commit changes
	}

	/**
	 * Get Mobile Number
	 */
	public String getMobileNumber()
	{
		return pref.getString(KEY_MOBILE_NO, "");
	}

	/**
	 * Get Email
	 */
	public String getEmail()
	{
		return pref.getString(KEY_EMAIL, "");
	}

	/**
	 * Get MRN
	 */
	public String getMRN()
	{
		return pref.getString(KEY_MRN_NO, "");
	}

	/**
	 * Set Mobile Number
	 */
	public void putMobileNumber(String mobile)
	{
		editor.putString(KEY_MOBILE_NO, mobile);
		editor.commit();
	}


	/**
	 * Set Full Name
	 */
	public void changeFullName(String full_name)
	{
		editor.putString(KEY_FULL_NAME, full_name);
		editor.commit();
	}

	/**
	 * Get Full Name
	 */
	public String getFullName()
	{
		return pref.getString(KEY_FULL_NAME, "");
	}

	/**
	 * Set File Attachment Limit
	 */
	public void putAttachmentLimit(long limit)
	{
		fcm_editor.putLong(KEY_FILE_ATTACHMENT_LIMIT, limit);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get File Attachment Limit
	 */
	public long getAttachmentLimit()
	{
		return fcm_pref.getLong(KEY_FILE_ATTACHMENT_LIMIT, DEFAULT_FILE_ATTACHMENT_LIMIT);
	}

	/**
	 * Set Max File Size
	 */
	public void putMaxFileSize(long size)
	{
		fcm_editor.putLong(KEY_MAX_FILE_SIZE, size);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Max File Size
	 */
	public long getMaxFileSize()
	{
		return (fcm_pref.getLong(KEY_MAX_FILE_SIZE, DEFAULT_FILE_SIZE) / 1000);
	}

	/**
	 * Set Play Store Version Code
	 */
	public void putPlayStoreVersionCode(long code)
	{
		fcm_editor.putLong(KEY_PLAY_STORE_VERSION_CODE, code);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Play Store Version Code
	 */
	public long getPlayStoreVersionCode()
	{
		return fcm_pref.getLong(KEY_PLAY_STORE_VERSION_CODE, 0);
	}

	/**
	 * Set Customer Care Number
	 */
	public void putCustomerCareNumber(String number)
	{
		fcm_editor.putString(KEY_CUSTOMER_CARE_NUMBER, number);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Customer Care Number
	 */
	public String getCustomerCareNumber()
	{
		return fcm_pref.getString(KEY_CUSTOMER_CARE_NUMBER, _context.getResources().getString(R.string.customer_care_number));
	}

	/**
	 * Set is hotline number
	 */
	public void putIsHotline(int is_hotline)
	{
		fcm_editor.putInt(KEY_IS_HOTLINE, is_hotline);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get is hotline number
	 */
	public int getIsHotline()
	{
		return fcm_pref.getInt(KEY_IS_HOTLINE, 0);
	}

	/**
	 * Set is vital info url
	 */
	public void putVitalInfoUrl(String url)
	{
		fcm_editor.putString(KEY_VITAL_INFO_URL, url);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get is vital info url
	 */
	public String getVitalInfoUrl()
	{
		return fcm_pref.getString(KEY_VITAL_INFO_URL, VITAL_INFO_URL);
	}

	/**
	 * Set Appointment Callback Time Limit
	 */
	public void putAppointmentCallbackTimeLimit(long limit)
	{
		fcm_editor.putLong(KEY_APPOINTMENT_CALLBACK_TIME_LIMIT, limit);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Appointment Callback Time Limit
	 */
	public long getAppointmentCallbackTimeLimit()
	{
		return fcm_pref.getLong(KEY_APPOINTMENT_CALLBACK_TIME_LIMIT, DEFAULT_APPOINTMENT_CALLBACK_TIME_LIMIT);
	}

	/**
	 * Set Ringing Duration
	 */
	public void putRingingDuration(long duration)
	{
		fcm_editor.putLong(KEY_RINGING_DURATION, duration);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Ringing Duration
	 */
	public long getRingingDuration()
	{
		return fcm_pref.getLong(KEY_RINGING_DURATION, DEFAULT_RINGING_DURATION);
	}

	/**
	 * Set ENV Prefix
	 */
	public void putEnvPrefix(String prefix)
	{
		fcm_editor.putString(KEY_ENV_PREFIX, prefix);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get ENV Prefix
	 */
	public String getEnvPrefix()
	{
		return fcm_pref.getString(KEY_ENV_PREFIX, DEFAULT_ENV_PREFIX);
	}

	/**
	 * Set Client Secret
	 */
	public void putOAuthClientSecret(String client_secret)
	{
		fcm_editor.putString(KEY_CLIENT_SECRET, client_secret);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Client Secret
	 */
	public String getOAuthClientSecret()
	{
		return fcm_pref.getString(KEY_CLIENT_SECRET, DEFAULT_CLIENT_SECRET);
	}


	/**
	 * Set Client ID
	 */
	public void putOAuthClientID(String client_id)
	{
		fcm_editor.putString(KEY_CLIENT_ID, client_id);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Client ID
	 */
	public String getOAuthClientID()
	{
		return fcm_pref.getString(KEY_CLIENT_ID, DEFAULT_CLIENT_ID);
	}


	/**
	 * Set Opentok API KEY
	 */
	public void putOpentokApiKey(String api_key)
	{
		fcm_editor.putString(KEY_OPENTOK_API_KEY, api_key);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Opentok API KEY
	 */
	public String getOpentokApiKey()
	{
		return fcm_pref.getString(KEY_OPENTOK_API_KEY, DEFAULT_OPENTOK_KEY);
	}


	/**
	 * Set Razorpay API KEY
	 */
	public void putRazorpayApiKey(String api_key)
	{
		fcm_editor.putString(KEY_RAZORPAY_API_KEY, api_key);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Razorpay API KEY
	 */
	public String getRazorpayApiKey()
	{
		return fcm_pref.getString(KEY_RAZORPAY_API_KEY, DEFAULT_RAZORPAY_KEY);
	}


	/**
	 * Set Facebook API KEY
	 */
	public void putFacebookClientID(String client_id)
	{
		fcm_editor.putString(KEY_FB_CLIENT_ID, client_id);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Opentok API KEY
	 */
	public String getFacebookClientID()
	{
		return fcm_pref.getString(KEY_FB_CLIENT_ID, DEFAULT_FB_CLIENT_ID);
	}

	/**
	 * Set Base URL
	 */
	public void putBaseURL(String base_url)
	{
		fcm_editor.putString(KEY_BASE_URL, base_url);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Base URL
	 */
	public String getBaseURL()
	{
		return fcm_pref.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
	}


	/**
	 * Set Country List
	 */
	public void putCountryList(String list)
	{
		fcm_editor.putString(KEY_COUNTRY_LIST, list);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Set Country List
	 */
	public void putNamePrefixList(String list)
	{
		fcm_editor.putString(KEY_NAME_PREFIX_LIST, list);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Country List
	 */
	public String getCountryList()
	{
		return fcm_pref.getString(KEY_COUNTRY_LIST, "{}");
	}

	/**
	 * Set Language Preferences
	 */
	public void putLanguagePreferences(String list)
	{
		fcm_editor.putString(KEY_LANGUAGE_PREFERENCES, list);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Language Preferences
	 */
	public String getLanguagePreferences()
	{
		return fcm_pref.getString(KEY_LANGUAGE_PREFERENCES, "[]");
	}


	/**
	 * Set Language Preference Value
	 */
	public void putLanguagePreferenceValue(String value)
	{
		fcm_editor.putString(KEY_LANGUAGE_PREFERENCES_VALUES, value);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Language Preference Value
	 */
	public String getLanguagePreferenceValue()
	{
		return fcm_pref.getString(KEY_LANGUAGE_PREFERENCES_VALUES, "English");
	}

	/**
	 * Get Prefix List
	 */
	public String getNamePrefixList()
	{
		return fcm_pref.getString(KEY_NAME_PREFIX_LIST, "{}");
	}


	/**
	 * Set EHR document consent
	 */
	public void putDocumentConsent(int ehr_document_consent)
	{
		fcm_editor.putInt(KEY_EHR_DOCUMENT_CONSENT, ehr_document_consent);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get EHR document consent
	 */
	public int getDocumentConsent()
	{
		return fcm_pref.getInt(KEY_EHR_DOCUMENT_CONSENT, 0);
	}

	/**
	 * Set Family Member Config
	 */
	public void putFamilyMemberConfig(boolean family_members_config)
	{
		fcm_editor.putBoolean(KEY_FAMILY_MEMBERS_CONFIG, family_members_config);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Family Member Config
	 */
	public boolean getFamilyMemberConfig()
	{
		return fcm_pref.getBoolean(KEY_FAMILY_MEMBERS_CONFIG, false);
	}


	/**
	 * Set Family Member Allowed
	 */
	public void putFamilyMemberAllowed(int family_members_allowed)
	{
		fcm_editor.putInt(KEY_FAMILY_MEMBERS_ALLOWED, family_members_allowed);
		fcm_editor.commit(); // commit changes
	}

	/**
	 * Get Family Member Allowed
	 */
	public int getFamilyMemberAllowed()
	{
		return fcm_pref.getInt(KEY_FAMILY_MEMBERS_ALLOWED, 0);
	}


	/**
	 * Update User
	 */
	public void update(User profile)
	{
		changeMobileNo(profile.getUserAccount().getRegMobileNo());
		changeEmail(profile.getUserAccount().getRegEmail());
		changeMRN(profile.getUserAccount().getMRNNo());
		changeFullName(profile.getFullName());

		if (!getAvatarLink().equals(profile.getProfilePic()))
		{
			putAvatarLink(profile.getProfilePic());
		}

		if (profile.getUserAccount().isMobileVerified() == 0)
		{
			putMobileStatus(false);
		}

		else
		{
			putMobileStatus(true);
		}

		if (profile.getUserAccount().isEmailVerified() == 0)
		{
			putEmailStatus(false);
		}

		else
		{
			putEmailStatus(true);
		}

		putSubscriptionUpgradeStatus(profile.getUserAccount().getSubscription().isUpgradable());
		putSubscriptionStatus(profile.getUserAccount().getSubscription().getSubscriptionStatus());
		putSubscriptionPaymentStatus(profile.getUserAccount().getSubscription().getSubscriptionPlan().getPaymentStatus());
		putSubscriptionType(profile.getUserAccount().getSubscription().getSubscriptionType());
		putUserType(profile.getUserAccount().getSubscription().getUserType());
		putSubscriptionPlanName(profile.getUserAccount().getSubscription().getSubscriptionPlan().getPlanName());
		putTrialEndsDate(profile.getUserAccount().getSubscription().getEndsAt());
		putLanguagePreferences(profile.getPreferredLanguages());
		putLanguagePreferenceValue(profile.getPreferredLanguageValue());
		putFitMeInStatus(profile.getFitMeInServiceEnable());
		putUserId(profile.getUserId());
		putUserGender(profile.getGender());
		putUserDOB(profile.getDateOfBirth());
		putUserAge(profile.getAge());
		putHRAServices(profile.getHRAServicesEnabled());
	}

	public void putHRAServices(Boolean hraServicesEnabled) {
		editor.putBoolean(KEY_HRA_ENABLED, hraServicesEnabled);
		editor.commit();
	}

	public Boolean getHRAServices(){
		return pref.getBoolean(KEY_HRA_ENABLED, false);
	}

	public void putUserAge(String age) {
		editor.putString(KEY_AGE, age);
		editor.commit();
	}

	public String getUserAge(){
		return pref.getString(KEY_AGE, "");
	}

	public void putUserDOB(String dateOfBirth) {
		editor.putString(KEY_DOB, dateOfBirth);
		editor.commit();
	}

	public String getUserDOB(){
		return pref.getString(KEY_DOB, "");
	}

	public void putUserGender(String gender) {
		editor.putString(KEY_GENDER, gender);
		editor.commit();
	}

	public String getUserGender(){
		return pref.getString(KEY_GENDER, "");
	}

	public void putUserId(int userId) {
		editor.putInt(KEY_USER_ID, userId);
		editor.commit();
	}

	public int getUserId(){
		return pref.getInt(KEY_USER_ID, 0);
	}

	public void putFitMeInStatus(Boolean fitMeInServiceEnable) {
		editor.putBoolean(KEY_FITMEIN_ENABLED, fitMeInServiceEnable);
		editor.commit();
	}

	public Boolean getFitMeInStatus(){
		return pref.getBoolean(KEY_FITMEIN_ENABLED, false);
	}


	public class OkHttpHandler extends AsyncTask<String, String, String>
	{
		private OkHttpClient client = MyOkHttpClient.getOkHttpClient();

		private int responseCode, httpMethod;
		private String access_token;


		public OkHttpHandler(int httpMethod, String access_token)
		{
			this.httpMethod = httpMethod;
			this.access_token = access_token;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String...params)
		{
			okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
			builder.url(params[0]);

			if(MyApplication.HTTPMethod.POST.getValue() == httpMethod)
			{
				RequestBody body = new FormBody.Builder()
						.add(Constants.KEY_FCM_TOKEN,  DeviceUtils.get_push_token())
						.build();

				builder.post(body);
			}

			builder.addHeader(AUTHORIZATION, "Bearer " + OAuth.getOAuthHeader(access_token));
			builder.addHeader(ACCEPT, "application/json");
			builder.addHeader(CONTENT_TYPE, "application/json");
			builder.addHeader(DOCONLINE_API, DOCONLINE_API_VERSION);
			okhttp3.Request request = builder.build();

			try
			{
				okhttp3.Response response = client.newCall(request).execute();
				responseCode = response.code();
				return response.body().string();
			}

			catch (Exception e)
			{
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String response)
		{
			Log.wtf("STATUS_CODE", "" + responseCode);

			//if(responseCode == 204)
			//{
				prefs.edit().putBoolean(KEY_LOGOUT_SYNCED, true).apply();

				/**
				 * Clearing all data from Shared Preferences
				 */
				editor.clear();
				editor.commit();

/**
 * Clear Oauth details
 */
				oauth_editor.clear();
				oauth_editor.commit();

				clear_device_info();

/**
 * Clear SQLite table data
 */
				helper.remove_all(TABLE_MEDICATIONS);
				helper.remove_all(TABLE_ALLERGIES);
				helper.remove_all(TABLE_HEALTH_PROFILE);
				helper.remove_all(TABLE_PROFILE);
				helper.remove_all(TABLE_NOTIFICATIONS);

/**
 * Clear array list values
 */
				mController.clearAppointment();
				mController.clearFamilyMember();

// After logout redirect user to Logging Activity
				Intent i = new Intent(_context, MainActivity.class);

// Closing all the Activities
// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

// Add new Flag to start new Activity
// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

// Staring Login Activity
				_context.startActivity(i);
			//}

			super.onPostExecute(response);
		}
	}
}