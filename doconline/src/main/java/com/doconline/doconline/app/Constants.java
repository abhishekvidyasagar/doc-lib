package com.doconline.doconline.app;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * This class is designed to declare application variables
 */
public class Constants {
    /**
     * Application Tag
     */
    public static final String TAG = "DocOnline";

    /**
     * Preference Name
     */
    public static final String PREF = "doconline.pref";
    public static final String TOKEN = "push_token";
    public static final String FIRST_RUN = "first_run";
    public static final String RECEIVE_NOTIFICATION = "receive_notification";
    public static final String NOTIFICATION_SOUND = "notification_sound";
    public static final String NOTIFICATION_VIBRATION = "notification_vibration";

    /**
     *
     */
    public static final String SMS_BROADCAST = "sms_broadcast";
    public static final String VERIFICATION_CODE = "verification_code";

    /**
     * Tag
     */
    public static final String JSON_TAG = "json_data";
    public static final String SQL_QUERY_TAG = "sql_query";

    /**
     * Directory Name
     */
    public static final String MEDIA_DIRECTORY_NAME = "doconline";

    /**
     * Retry Policy
     */
    public static final int CONNECTION_TIMEOUT = 90;
    public static final int READ_TIMEOUT = 90;
    public static final int WRITE_TIMEOUT = 90;

    /**
     * Database version
     */
    public static final int DATABASE_VERSION = 4;

    /**
     * Database name
     */
    public static final String DATABASE_NAME = "DocOnlineDB";

    /**
     * Table Name
     */
    public static final String TABLE_PROFILE = "profile";
    public static final String TABLE_MEDICATIONS = "medications";
    public static final String TABLE_ALLERGIES = "allergies";
    public static final String TABLE_HEALTH_PROFILE = "health_profile";
    public static final String TABLE_NOTIFICATIONS = "notification";
    public static final String TABLE_LANGUAGES = "languages";

    public static final String KEY_PROFILE_PIC = "profile_pic";

    public static final String KEY_SYNC_STATUS = "sync_status";
    public static final String KEY_READ_STATUS = "read_status";
    public static final String KEY_ALARM_STATUS = "alarm_status";
    public static final String KEY_READ = "read";
    public static final String KEY_ALL = "all";

    public static final String KEY_GRANT_TYPE = "grant_type";
    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_DOCTOR_ID = "doctor_id";
    public static final String KEY_CLIENT_SECRET = "client_secret";
    public static final String KEY_FB_CLIENT_ID = "fb_client_id";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_SCOPE = "scope";
    public static final String KEY_TOKEN_TYPE = "token_type";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_OLD_ACCESS_TOKEN = "old_access_token";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_REMAINING_TIME = "remaining_time";

    public static final int CALL_TYPE_AUDIO = 1;
    public static final int CALL_TYPE_VIDEO = 2;
    public static final int CALL_MEDIUM_INTERNET = 1;
    public static final int CALL_MEDIUM_REGULAR = 2;
    public static final int CONSULTATION_SELF = 1;
    public static final int CONSULTATION_FAMILY = 2;
    public static final int BOOKING_TYPE_INSTANCE = 1;
    public static final int BOOKING_TYPE_SLOT = 2;
    public static final int CAMERA_REQUEST_CODE = 111;
    public static final int CHAT_IMAGE_REQUEST_CODE = 222;
    public static final int FAMILY_REQUEST_CODE = 333;
    public static final int IMAGE_VIEWER_REQUEST_CODE = 444;
    public static final int FILE_UPLOAD_REQUEST_CODE = 555;
    public static final int LANGUAGE_PREFERENCES_NO = 0;
    public static final int LANGUAGE_PREFERENCES_YES = 1;

    public static final int CALL_STATUS_PATIENT_ACKNOWLEDGED = 7;
    public static final int CALL_STATUS_PATIENT_ACCEPTED = 8;
    public static final int CALL_STATUS_PATIENT_REJECTED = 9;
    public static final int CALL_STATUS_CANCELLED = 0;
    public static final int CALL_STATUS_ACTIVE = 1;
    public static final int CALL_STATUS_COMPLETED = 2;
    public static final int CALL_STATUS_CALL_NOT_PLACED = 4;
    public static final int CALL_STATUS_NOT_ATTENDED = 9;
    public static final int CALL_STATUS_NO_PRESCRIPTION = 10;

    public static final String TYPE_TESTER = "tester";
    public static final String TYPE_PROMO = "promo";
    public static final String TYPE_B2B = "b2b";
    public static final String TYPE_B2B_PAID = "b2bpaid";
    public static final String TYPE_B2C = "b2c";
    public static final String TYPE_FAMILY = "family";
    public static final String TYPE_ONETIME = "onetime";
    public static final String TYPE_CORPORATE = "corporate_package";

    public static final String PAYMENT_HALTED = "halted";

    public static final String TYPE_MALE = "Male";
    public static final String TYPE_FEMALE = "Female";
    public static final String TYPE_TRANSGENDER = "Transgender";

    public static final int SUBSCRIPTION_PERMISSION_REQ_CODE = 100;
    public static final int OTP_EXPIRE_DURATION_IN_MINUTE = 2;

    /**
     * Preference Name
     */
    public static final String APP_START = "app_start";
    public static final String RUNNING_ACTIVITY = "running_activity";
    public static final String NETWORK_COUNTRY = "network_country";
    public static final String ORIENTATION = "orientation";
    public static final String SCREEN_RES = "screen_res";
    public static final String SCREEN_DENSITY = "screen_density";
    public static final String SCREEN_SIZE = "screen_size";
    public static final String OS_VERSION = "os_version";
    public static final String OS_SDK_VERSION = "os_sdk_version";
    public static final String OS_RELEASE_VERSION = "os_release_version";
    public static final String MANUFACTURER = "manufacturer";
    public static final String MODEL = "model";
    public static final String DEVICE = "device";
    public static final String VERSION_CODE = "version_code";
    public static final String VERSION_NAME = "version_name";
    public static final String PACKAGE_NAME = "package_name";
    public static final String TARGET_SDK_VERSION = "target_sdk_version";
    public static final String IS_IN_BACKGROUND = "is_in_background";

    /**
     * Regular Expression
     */
    public static final String PINCODE_PATTERN = "[0-9]{6}";
    public static final String MOBILE_NUMBER_PATTERN = "[0-9]{10}";
    public static final String MRN_PATTERN = "[A-Z0-9]{10}";

    /**
     * (?=.*[0-9]) a digit must occur at least once
     * (?=.*[a-z]) a lower case letter must occur at least once
     * (?=.*[A-Z]) an upper case letter must occur at least once
     * (?=.*[@#$%^&+=]) a special character must occur at least once
     * (?=\\S+$) no whitespace allowed in the entire string
     * {8,} at least 8 characters
     */
    public static final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#?!@$%^&*-])(?=\\S+$).{8,}";
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    /**
     * Amount Format
     */
    public static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Supported File Formats
     */
    public static final String[] FILE_EXTENSIONS = new String[]{"doc", "docx", "xls", "xlsx", "pdf"};

    /**
     * Column Names
     */
    public static final String KEY_ID = "id";
    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_ORDER_AMOUNT = "order_amount";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_DOCONLINE_USER_ID = "doconline_user_id";
    public static final String KEY_PLATFORM = "platform";
    public static final String KEY_PREFERENCE = "preference";
    public static final String KEY_JOB_ID = "jobId";
    public static final String KEY_THREAD_ID = "threadId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TITLE = "title";
    public static final String KEY_EXCEPTION = "exception";

    public static final String KEY_CODE = "code";
    public static final String KEY_TYPE_OF = "type_of";

    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_INTAKE_TIME = "intake_time";
    public static final String KEY_FROM_DATE = "from_date";
    public static final String KEY_TO_DATE = "to_date";
    public static final String KEY_DOES_SMOKE = "does_smoke";
    public static final String KEY_MEDICAL_HISTORY = "medical_history";
    public static final String KEY_MEDICATIONS = "medications";
    public static final String KEY_ALLERGIES = "allergies";
    public static final String KEY_DRUG_ALLERGIES = "drug_allergies";
    public static final String KEY_ALLERGY = "allergy";
    public static final String KEY_MEDICATION = "medication";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_LANGUAGES = "languages";
    public static final String KEY_LANGUAGE_PREF = "lang_pref";
    public static final String KEY_ENGLISH_NAME = "english_name";

    public static final String KEY_EXTRA_DATA = "extra_data";
    public static final String KEY_EXTRA = "extra";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_PRACTITIONER_NUMBER = "practitioner_number";
    public static final String KEY_SPECIALIZATION = "specialization";
    public static final String KEY_ATTACHMENTS = "attachments";
    public static final String KEY_FILE_URL = "file_url";
    public static final String KEY_DOCTOR_NOTES = "doctor_notes";

    public static final String KEY_PROVISIONAL_DIAGNOSIS = "provisional_diagnosis";
    public static final String KEY_FOLLOWUPDATE_BY_DOCTOR = "follow_up_date";

    public static final String KEY_SUBSCRIPTION = "subscription";
    public static final String KEY_SUBSCRIPTION_TYPE = "subscription_type";
    public static final String KEY_SUBSCRIPTION_PLAN_NAME = "subscription_plan_name";
    public static final String KEY_TRIAL_ENDS_ON = "trial_ends_on";
    public static final String KEY_PENDING_SUBSCRIPTION = "pending_subscription";
    public static final String KEY_SUBSCRIBED = "subscribed";
    public static final String KEY_CAN_UPGRADE = "can_upgrade";
    public static final String KEY_BADGE_COUNT = "badge_count";
    public static final String KEY_OTP_SENT = "otp_sent";
    public static final String KEY_OTP_CODE = "otp_code";
    public static final String KEY_TIMEZONE = "timezone";
    public static final String KEY_MOBILE_VERIFIED = "mobile_verified";
    public static final String KEY_IS_VERIFIED = "is_verified";
    public static final String KEY_SHIPPING_ADDRESS = "shipping_address";

    public static final String KEY_NAME_PREFIX = "prefix";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_MIDDLE_NAME = "middle_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_AGE = "age";
    public static final String KEY_BOOKABLE = "bookable";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_DEFAULT_AVATAR = "default-avatar";
    public static final String KEY_AVATAR_URL = "avatar_url";

    public static final String KEY_SYMPTOMS = "symptom";
    public static final String KEY_PASSWORD_STATUS = "password_status";
    public static final String KEY_MOBILE_STATUS = "mobile_status";
    public static final String KEY_EMAIL_STATUS = "email_status";
    public static final String KEY_SUBSCRIPTION_STATUS = "subscription_status";
    public static final String KEY_SUBSCRIPTION_PAYMENT_STATUS = "subscription_payment_status";
    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MASKED_EMAIL = "masked_email";
    public static final String KEY_MASKED_MOBILE_NUMBER = "masked_mobile_no";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PASS = "pass";
    public static final String KEY_CONFIRM_PASSWORD = "password_confirmation";
    public static final String KEY_CURRENT_PASSWORD = "password_current";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_DOB = "dob";
    public static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_PHONE_NO = "phoneno";
    public static final String KEY_PINCODE = "pin_code";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_COUNTRY_ID = "country_id";
    public static final String KEY_CALL_TYPE = "call_type";
    public static final String KEY_TYPE = "type";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_RECURRING_TYPE = "recurring";
    public static final String KEY_ONETIME_TYPE = "onetime";
    public static final String KEY_FAMILY_TYPE = "familypack";
    public static final String KEY_FOR_WHOM = "booked_for";
    public static final String KEY_BOOKED_FOR_USER_ID = "booked_for_user_id";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_NO_OF_DAYS = "medicine_intake_days";
    public static final String KEY_ACTION_TYPE = "action_type";
    public static final String KEY_ACTIVATION_TYPE = "activation_type";
    public static final String KEY_PACKAGES = "packages";
    public static final String KEY_NAME = "name";
    public static final String KEY_BOOKING_CONSENT = "booking_consent";
    public static final String KEY_DISPLAY_NAME = "display_name";
    public static final String KEY_DISPLAY_PRICE = "display_price";
    public static final String KEY_DISPLAY_COLOR = "display_color";
    public static final String KEY_DISPLAY_PERIOD = "display_period";
    public static final String KEY_INTERNAL_ORDER = "internal_order";
    public static final String KEY_BODY = "body";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_CDN_PHOTO_URL = "cdn_photo_url";
    public static final String KEY_LAST_MESSAGE = "latestMessage";
    public static final String KEY_SENDER_TYPE = "s_type";
    public static final String KEY_MOBILE_NO = "mobile_no";
    public static final String KEY_MRN_NO = "mrn_no";
    public static final String KEY_MRN = "mrn";
    public static final String KEY_RESET_OPTION = "reset-option";
    public static final String KEY_ALTERNATE_CONTACT_NO = "alternate_contact_no";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_ADDRESS_1 = "address1";
    public static final String KEY_ADDRESS_2 = "address2";
    public static final String KEY_APPOINTMENT_ID = "appointment_id";
    public static final String KEY_PUBLIC_APPOINTMENT_ID = "public_appointment_id";
    public static final String KEY_APPOINTMENT = "appointment";
    public static final String KEY_SCHEDULED_AT = "scheduled_at";
    public static final String KEY_CALL_CHANNEL = "call_channel";
    public static final String KEY_BOOKING_TYPE = "booking_type";
    public static final String KEY_STARTED_AT = "started_at";
    public static final String KEY_FINISHED_AT = "finished_at";
    public static final String KEY_OPENTOK_API_KEY = "opentok_api_key";
    public static final String KEY_NOTIFIABLE_ID = "notifiable_id";
    public static final String KEY_NOTIFIABLE_TYPE = "notifiable_type";
    public static final String KEY_RATING = "rating";
    public static final String KEY_RATINGS = "ratings";
    public static final String KEY_RAZORPAY_API_KEY = "razorpay_api_key";
    public static final String KEY_RAZORPAY_API_SECRET = "razorpay_api_secret";
    public static final String KEY_PLAY_STORE_VERSION_CODE = "play_store_version_code";
    public static final String KEY_CUSTOMER_CARE_NUMBER = "customer_care_number";
    public static final String KEY_MAX_FILE_SIZE = "max_file_size";
    public static final String KEY_EHR_DOCUMENT_CONSENT = "ehr_document_consent";
    public static final String KEY_DOCUMENT_CONSENT = "document_consent";
    public static final String KEY_PLAN_ID = "plan_id";
    public static final String KEY_PLAN_TYPE = "plan_type";
    public static final String KEY_PLAN_NAME = "plan_name";
    public static final String KEY_CUSTOMER_ID = "customer_id";
    public static final String KEY_CUSTOMER_DETAILS = "customer_details";
    public static final String KEY_FEATURED = "featured";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String KEY_ICON_IMAGE = "icon_image";
    public static final String KEY_FAMILY_MEMBERS_ALLOWED = "family_members_allowed";
    public static final String KEY_FAMILY_MEMBERS_CONFIG = "family_members_config";

    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_ISSUED_AT = "issued_at";
    public static final String KEY_PAID_AT = "paid_at";
    public static final String KEY_CANCELLED_AT = "cancelled_at";
    public static final String KEY_EXPIRED_AT = "expired_at";
    public static final String KEY_UPDATED_AT = "updated_at";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_BILLING_START_AT = "billing_start";
    public static final String KEY_BILLING_END_AT = "billing_end";
    public static final String KEY_ENDS_AT = "ends_at";
    public static final String KEY_READ_AT = "read_at";
    public static final String KEY_CHARGE_AT = "charge_at";
    public static final String KEY_TRIAL_ENDS_AT = "trial_ends_at";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_IS_HOTLINE = "is_hotline";
    public static final String KEY_VITAL_INFO_URL = "vital_info_url";

    public static final String KEY_ITEM = "item";
    public static final String KEY_ITEMS = "items";
    public static final String KEY_LINE_ITEMS = "line_items";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_PERIOD = "period";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_GROSS_AMOUNT = "gross_amount";
    public static final String KEY_AMOUNT_PAID = "amount_paid";
    public static final String KEY_AMOUNT_DUE = "amount_due";
    public static final String KEY_UNIT_AMOUNT = "unit_amount";
    public static final String KEY_CROSS_PRICE = "cross_price";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_DISCOUNT_TEXT = "discount_text";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SUBSCRIPTION_ID = "subscription_id";
    public static final String KEY_RAZORPAY_SUBSCRIPTION_ID = "razorpay_subscription_id";
    public static final String KEY_RAZORPAY_ORDER_ID = "razorpay_order_id";
    public static final String KEY_RAZORPAY_PAYMENT_ID = "razorpay_payment_id";
    public static final String KEY_PAYMENT_ID = "payment_id";
    public static final String KEY_RAZORPAY_ID = "razorpay_id";
    public static final String KEY_RAZORPAY_PLAN_ITEM = "razorpay_plan_item";
    public static final String KEY_RAZORPAY_PLAN_ID = "razorpay_plan";
    public static final String KEY_PREFILL = "prefill";

    public static final String KEY_PAYMENT_GATEWAY = "payment_gateway";
    public static final String KEY_APPLIED_COUPON_CODE ="coupon_code";

    public static final String KEY_STATUS = "status";
    public static final String KEY_IS_ACTIVE = "is_active";
    public static final String KEY_NOTIFY = "notify";
    public static final String KEY_IS_MINOR = "is_minor";
    public static final String KEY_ERROR = "error";
    public static final String KEY_READONLY = "readonly";
    public static final String KEY_SUCCESS = "success";

    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String KEY_DEVICE_TOKEN = "device_token";
    public static final String KEY_DEVICE_SYNCED = "device_synced";
    public static final String KEY_LOGOUT_SYNCED = "logout_synced";
    public static final String KEY_DATA = "data";
    public static final String KEY_DOCTOR = "doctor";
    public static final String KEY_USER = "user";
    public static final String KEY_PATIENT = "patient";
    public static final String KEY_ERRORS = "errors";
    public static final String KEY_CREATE = "create";
    public static final String KEY_THREADS = "threads";
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_COUPON_CODE = "coupon_code";
    public static final String KEY_DISCOUNT_CODE = "discount_code";
    public static final String KEY_ENABLE_COUPON = "enable_coupon";
    public static final String KEY_PLAN_AMOUNT = "plan_amount";
    public static final String KEY_DISCOUNT = "discount";
    public static final String KEY_MCI_CODE = "mci_code";
    public static final String KEY_PRACTIONER_NO = "practitioner_number";
    public static final String KEY_QUALIFICATION = "qualification";

    public static final String KEY_NEXT_PAGE_URL = "next_page_url";
    public static final String KEY_PREV_PAGE_URL = "prev_page_url";
    public static final String KEY_TOTAL_ITEM_COUNT = "total";
    public static final String KEY_PER_PAGE = "per_page";
    public static final String KEY_CURRENT_PAGE = "current_page";
    public static final String KEY_LAST_PAGE = "last_page";
    public static final String KEY_TOTAL_PAGE = "total_pages";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_VALIDATE = "validated";

    public static final String KEY_SESSION_ID = "sessionId";
    public static final String KEY_TOKEN_ID = "tokenId";

    public static final String KEY_PIN_CODE = "pincode";

    public static final String KEY_NOTIFICATION_TYPE = "notification_type";
    public static final String KEY_NOTIFICATION_TYPE_INCOMING_CALL = "UserAppointmentIncomingCallNotification";
    public static final String KEY_NOTIFICATION_TYPE_BOOKING_SUCCESS = "BookAppointmentSuccess";
    public static final String KEY_NOTIFICATION_TYPE_BOOKING_FAILED = "BookAppointmentFailed";
    public static final String KEY_NOTIFICATION_TYPE_THREAD_CLOSE = "ThreadClosedNotification";
    public static final String KEY_PUSH_BROADCAST_RECEIVER = "KEY_PUSH_BROADCAST_RECEIVER";
    public static final String KEY_PUSH_INSTANT_BOOKING_BROADCAST_RECEIVER = "push_instant_booking_broadcast_receiver";
    public static final String KEY_CHAT_SESSION_DISCONNECT_BROADCAST_RECEIVER = "chat_session_disconnect_broadcast_receiver";


    public static final String KEY_EXCEPTION_TYPE_APPOINTMENT_EXISTS = "AppointmentExistsException";
    public static final String KEY_NOTIFICATION_TYPE_NO_DOCTOR_AVAILABLE = "NoDoctorsAvailableException";
    public static final String KEY_NOTIFICATION_TYPE_NO_DOCTOR_ONLINE = "NoDoctorsOnlineException";

    /**
     * Call Action
     */
    public static final String KEY_CALL_ACTION = "CALL_ACTION";
    public static final String KEY_ANSWER = "ANSWER";
    public static final String KEY_REJECT = "REJECT";
    public static final String KEY_HANG_UP = "HANG UP";

    /**
     * Firebase Remote Config Key
     */
    public static final String KEY_RINGING_DURATION = "ringing_duration";
    public static final String KEY_FILE_ATTACHMENT_LIMIT = "file_attachment_limit";
    public static final String KEY_COUNTRY_LIST = "country_list";
    public static final String KEY_LANGUAGE_PREFERENCES = "language_preferences";
    public static final String KEY_LANGUAGE_PREFERENCES_VALUES = "language_preferences_values";
    public static final String KEY_NAME_PREFIX_LIST = "name_prefix_list";
    public static final String KEY_APPOINTMENT_CALLBACK_TIME_LIMIT = "appointment_call_back_time_limit";
    public static final String KEY_ENV_PREFIX = "env_prefix";

    /**
     * OAuth Credential
     */
    public static final String KEY_BASE_URL = "base_url";
    public static final String GRANT_TYPE = "password";
    public static final String GRANT_TYPE_SOCIAL = "social";
    public static final String SCOPE = "*";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String DOCONLINE_API = "DocOnline-Api";
    public static final String KEY_LAST_AUTHENTICATED_TIME = "last_authenticated_time";


    /*Diagnostics User Address*/
    public static final String KEY_DIAGNOSTICS_ADDRESS_ID = "address_id";
    public static final String KEY_DIAGNOSTICS_ADDRESS_TITLE = "title";
    public static final String KEY_DIAGNOSTICS_ADDRESS_LANE = "address";
    public static final String KEY_DIAGNOSTICS_ADDRESS_LANDMARK = "landmark";
    public static final String KEY_DIAGNOSTICS_ADDRESS_CITY = "city";
    public static final String KEY_DIAGNOSTICS_ADDRESS_STATE = "state";
    public static final String KEY_DIAGNOSTICS_ADDRESS_PIN = "pincode";
    public static final String KEY_DIAGNOSTICS_ADDRESS_ISDEFAULT = "is_default";


    /*Diagnostics Items */
    public static final String KEY_DIAGNOSTICS_B2BPACKAGE = "b2b";
    public static final String KEY_DIAGNOSTICS_CARTCOUNT = "cart";
    public static final String KEY_DIAGNOSTICS_GENERICPACKAGE = "general";
    public static final String KEY_DIAGNOSTICS_PARTNERID = "partner_id";
    public static final String KEY_DIAGNOSTICS_PACKAGEID = "package_id";
    public static final String KEY_DIAGNOSTICS_PACKAGENAME = "package_name";
    public static final String KEY_DIAGNOSTICS_PACKAGECODE = "package_code";
    public static final String KEY_DIAGNOSTICS_PACKAGEPRICE = "price";
    public static final String KEY_DIAGNOSTICS_TESTCOUNT = "tests_count";
    public static final String KEY_DIAGNOSTICS_HANDLINGCHARGES = "handling_charges";
    public static final String KEY_DIAGNOSTICS_PARTNERIMAGE = "partner_image";
    public static final String KEY_DIAGNOSTICS_IMAGELINK = "img_link";
    public static final String KEY_DIAGNOSTICS_PARTNERIMAGEURL = "partner_img_url";
    public static final String KEY_DIAGNOSTICS_FASTING = "fasting";
    public static final String KEY_DIAGNOSTICS_NORMALVALUE = "normal_value";
    public static final String KEY_DIAGNOSTICS_UNITS = "units";
    public static final String KEY_DIAGNOSTICS_VOLUME = "volume";
    public static final String KEY_DIAGNOSTICS_PROFILETYPE = "profile_type";
    public static final String KEY_DIAGNOSTICS_REFERENCESTRING = "refer_str";
    public static final String KEY_DIAGNOSTICS_INCARTITEMSCOUNT = "in_cart";
    public static final String KEY_DIAGNOSTICS_EXPIRES_ON = "expires_on";
    public static final String KEY_DIAGNOSTICS_AVAILABLE = "available";
    public static final String KEY_DIAGNOSTICS_SELECTED_PACKAGE_INDEX = "SELECTED_PACKAGE_INDEX";
    public static final String KEY_DIAGNOSTICS_PACKAGE_TESTS = "package_tests";

    public static final String KEY_DIAGNOSTICS_BTOB = "btobobject";


    /* Diagnostics Cart items*/
    public static final String KEY_DIAGNOSTICS_CART_DATA = "cart_data";
    public static final String KEY_DIAGNOSTICS_CART_COUNT = "cart_count";
    public static final String KEY_DIAGNOSTICS_CART_AMOUNT = "cart_amount";
    public static final String KEY_DIAGNOSTICS_CART_PACKAGEID = "package_id";       // "4",
    public static final String KEY_DIAGNOSTICS_CART_PACKAGE_NAME = "package_name";  //: "25-OH VITAMIN D (TOTAL)",
    public static final String KEY_DIAGNOSTICS_CART_PACKAGE_PRICE = "price";        //: 360,
    public static final String KEY_DIAGNOSTICS_CART_PACKAGE_QUANTITY = "quantity";  //: "1
    public static final String KEY_DIAGNOSTICS_CART_UPDATED_COUNT = "updated_cart_count";
    public static final String KEY_DIAGNOSTICS_CART_UPDATED_AMOUNT = "updated_cart_amount";

    /* Diagnostics Book appointment*/
    public static final String KEY_DIAGNOSTICS_ADDRESS_FULL = "adrs";
    public static final String KEY_DIAGNOSTICS_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_DIAGNOSTICS_ORDER_BY = "order_by";
    public static final String KEY_DIAGNOSTICS_APPOINTMENT_DATE = "apt_dt";
    public static final String KEY_DIAGNOSTICS_APPOINTMENT_ID = "apt_id";
    public static final String KEY_DIAGNOSTICS_TRANSACTION_ID = "trans_id";
    public static final String KEY_DIAGNOSTICS_PRINT_OUT = "print_out";
    public static final String KEY_DIAGNOSTICS_BENEFICIARY_ID = "beneficiary_id";
    public static final String KEY_DIAGNOSTICS_BENEFICIARY_COUNT = "beneficiary_count";
    public static final String KEY_DIAGNOSTICS_PARTNER_ID = "partner_id";
    public static final String KEY_DIAGNOSTICS_B2BAVAILABLE = "b2b_available";

    public static final String KEY_DIAGNOSTICS_FRAGMENT_INDEX = "FRAGMENT_TO_LOAD_INDEX";
    public static final int KEY_DIAGNOSTICSLISTFRAGMENT_INDEX = 0;
    public static final int KEY_CARTFRAGMENT_INDEX = 4;

    public static final long DEFAULT_RINGING_DURATION = 30;
    public static final long DEFAULT_FILE_ATTACHMENT_LIMIT = 5;
    public static final long DEFAULT_FILE_SIZE = 25000;
    public static final long DEFAULT_LANGUAGE_SELECTION_LIMIT = 2;
    public static final long DEFAULT_APPOINTMENT_CALLBACK_TIME_LIMIT = 10;
    public static final int DEFAULT_SUBSCRIBER_CONNECTING_TIMEOUT_DURATION = 10;
    public static final int DEFAULT_SESSION_CONNECTING_TIMEOUT_DURATION = 30;
    public static final String DEFAULT_ENV_PREFIX = "production";
    public static final String DEFAULT_CLIENT_ID = "2";
    public static final String DEFAULT_CLIENT_SECRET = "CKjZKNQMfIWGWQCaaYWECsV5sV0qqDA9wuvalI9b";
    public static final String DEFAULT_BASE_URL = "https://app.doconline.com/";
    public static final String DEFAULT_OPENTOK_KEY = "45750702";
    public static final String DEFAULT_FB_CLIENT_ID = "1622284567783206";
    public static final String DEFAULT_RAZORPAY_KEY = "rzp_live_x7EYqVpFpaC5RY";
    public static final String DOCONLINE_API_VERSION = "2018-08-13";
    public static final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.doconline.doconline";
    public static final String VITAL_INFO_URL = "https://app.doconline.com/assets/doconline-vitals-info.png";

    public static final String YY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DD_MMM_YYYY_HH_MM_A = "dd-MMM-yyyy hh:mm a";
    public static final String EEE_MMM_DD_YYYY = "EEE MMM dd, yyyy";
    public static final String MMM_DD_YYYY = "MMM dd, yyyy";
    public static final String EEE_MMM_DD_YYYY_HH_MM_A = "EEE MMM dd, yyyy hh:mm a";
    public static final String DD_MMM_EEE_HH_MM_A = "dd MMM EEE, hh:mm a";
    public static final String MMM_DD_YYYY_HH_MM_A = "MMM dd, yyyy hh:mm a";
    public static final String HH_MM_A = "hh:mm a";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DD_MM_YY = "dd/MM/yy";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
    public static final String MMM = "MMM";
    public static final String DD = "dd";

    //Diagnostics Reports and Status
    public static final String KEY_DIAG_APPOINTMENT_DETAILS = "appointment_details";
    public static final String KEY_DIAG_APPOINTMENT_ID = "do_apt_id";
    public static final String KEY_DIAG_PACKAGE_NAME = "package_name";
    public static final String KEY_DIAG_APPOINTMENT_DATE = "appointment_on";
    public static final String KEY_DIAG_REPORTS = "reports";
    public static final String KEY_DIAG_TRACKING_STATUS = "tracking_statuses";
    public static final String KEY_DIAG_TSP_DETAILS = "tsp_details";

    public static final String KEY_DIAG_APPOINTMENT_STATUS = "status";

    public static final String KEY_DIAG_USER_NAME = "user";
    public static final String KEY_DIAG_REPORT_ID = "report_id";
    public static final String KEY_DIAG_REPORT_URL = "report_url";


    //AppsFlyer Developer Key
    public static final String AF_DEV_KEY = "KTgzJFt5nZAaVDveB9y7VQ";

    public static final String KEY_MEDIA_PARTNER_NAME = "media_source";


    //keys used in paytm module
    public static final String CALLBACK_URL = "CALLBACK_URL";
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String CUST_ID = "CUST_ID";
    public static final String INDUSTRY_TYPE_ID = "INDUSTRY_TYPE_ID";
    public static final String MID = "MID";
    public static final String ORDER_ID = "ORDER_ID";
    public static final String TXN_AMOUNT = "TXN_AMOUNT";
    public static final String WEBSITE = "WEBSITE";
    public static final String EMAIL = "EMAIL";
    public static final String MOBILE_NO = "MOBILE_NO";
    public static final String CHECK_SUM_HASH = "checksumhash";
    public static final String PAYTM_STATUS = "paytmstatus";
    public static final String PAYTM_REQUEST_OBJECT = "paymentrequestobject";

    public static final String DIAGNOSTIC_ITEM = "diagnostic_item_object";
    public static final String MR_DATE_FORMAT = "dd/MM/yyyy";
    public static final String REMAINDER_DATA = "remainderdata";

    public static final String USER_SELECTED_DATE = "userselectedate";

    public static final String FROM_SCREEN_KEY = "fromscreen";

    public static final String FROM_SCREEN_KEY_EDIT = "edit";
    public static final String FROM_SCREEN_KEY_CREATE = "create";

    public static final String REMAINDER_TIME_DATA = "remaindertimedata";

    public static final String KEY_MASTERDATA = "master_data";
    public static final String KEY_LIFE_STYLE_ACTIVITY = "life_style_activity";
    public static final String KEY_SLEEP_DURATION = "sleep_duration";
    public static final String KEY_EXERCISE_PER_WEEK = "exercise_per_week";
    public static final String KEY_MARITAL_STATUS = "marital_status";
    public static final String KEY_MEDICAL_HISTORYY = "medical_history";
    public static final String KEY_SLEEP_PATTERN = "sleep_pattern";
    public static final String KEY_SWITCH_IN_MEDICINE = "switch_in_medicine";
    public static final String KEY_MEDICAL_INSURENCE = "medical_insurance";

    public static final String KEY_LIFE_STYLE_ACTIVITY_ID = "life_style_activity_id";
    public static final String KEY_SLEEP_DURATION_ID = "sleep_duration_id";
    public static final String KEY_SLEEP_PATTERN_ID = "sleep_pattern_id";
    public static final String KEY_EXERCISE_PER_WEEK_ID = "exercise_per_week_id";
    public static final String KEY_MARITAL_STATUS_ID = "marital_status_id";
    public static final String KEY_SWITCH_IN_MEDICINE_ID = "switch_in_medicine_id";
    public static final String KEY_SWITCH_IN_MEDICINE_OTHERS = "switch_in_medicine_reason";
    public static final String KEY_MEDICAL_HISTORY_ID = "medical_history_id";
    //public static final String KEY_MEDICAL_INSURENCE_ID = "medical_insurance";

    public static final String SPINNER_HEADER_LIFESTYLEACTIVITY = "Any Life Style Activities ? ";
    public static final String SPINNER_HEADER_SLEEPDURATION = "Sleep Duration ? ";
    public static final String SPINNER_HEADER_EXERCISEPERWEEK = "Exercise per week ? ";
    public static final String SPINNER_HEADER_MARITALSTATUS = "Marital status ? ";
    public static final String SPINNER_HEADER_MEDICALHISTORY = "Medical history ? ";
    public static final String SPINNER_HEADER_SLEEPPATTERN = "Sleep pattern ? ";
    public static final String SPINNER_HEADER_SWITCHINMEDICINE = "Reason for Switch in Medicine ? ";
    public static final String SPINNER_HEADER_MEDICALINSURENCE = "Do you have any ic_medical insurance ? ";

    public static final String SPINNER_HEADER_COMPLICATIONS = "Do you have any pregnancy related complications ? ";
    public static final String SPINNER_HEADER_EXPECTING_MOTHER = "Expecting mother ? ";

    public static final String KEY_EXPECTING_MOTHER = "expecting_mother";
    public static final String KEY_NO_OF_CONCEPTIONS = "no_of_conceptions";
    public static final String KEY_ABORTIONS = "no_of_abortions";
    public static final String KEY_PREGNANCY_RELATED_COMPLICATIONS = "complications_if_any";
    public static final String KEY_PREGNANCY_DETAILS = "pregnancy_details";
    public static final String KEY_FOLLOW_UP_DATA = "follow_up_data";

    public static final String KEY_MEDICATIONS_STATUS = "medication_state";

    public static final String KEY_GENDER_PREGNANCY = "gender";

    //fitmein
    public static final String WOORKOUT_DETAILS_ID = "workout_id";

    public static final String KEY_FITMEIN_WORKOUT_DATE = "workout_date";
    public static final String KEY_LATITUDE = "point_lat";
    public static final String KEY_LONGITUDE = "point_long";
    public static final String KEY_LOCATION = "location";

    public static final String KEY_OUTLETNAME = "len";
    public static final String KEY_AREA = "a2";
    public static final String KEY_CITY_ = "c";
    public static final String KEY_DISTANCE = "dist";
    public static final String KEY_FROM_TIME = "ft";
    public static final String KEY_TO_TIME = "tt";
    public static final String KEY_WORKOUT_ID = "id";

    public static final String KEY_PREDICTIONS = "predictions";
    public static final String KEY_PLACE_ID = "place_id";

    public static final String KEY_OUTLET_NAME = "OutletName";
    public static final String KEY_WORKOUT_ID_ = "WorkoutID";
    public static final String KEY_WORKOUT_NAME = "WorkoutName";
    public static final String KEY_FROM_TIME_DETAILS = "FromTime";
    public static final String KEY_TO_TIME_DETAILS = "ToTime";
    public static final String KEY_DESCRIPTION_DETAILS = "Description";
    public static final String KEY_GOOGLE_MAPS_COORDINATES = "GoogleMapsCoordiates";
    public static final String KEY_DURATION = "Duration";
    public static final String KEY_WORKOUT_DETAILS_ID = "WorkoutDetailsID";

    public static final String KEY_WORKOUT_ID_BOOK = "workout_id";
    public static final String KEY_WORKOUT_BOOK_DATE = "booked_for_date";
    public static final String KEY_WORKOUT_BOOK_TIME = "expected_time";

    public static final String SELECTED_DATE = "selecteddatefromdatepicker";
    public static final String KEY_HISTORY_PREVIOUS = "history_previous";
    public static final String KEY_HISTORY_UPCOMING = "history_upcoming";
    public static final String KEY_HISTORY_CANCELLED = "history_cancelled";
    public static final String MAIN_ACTIVITY = "main_activity";
    public static final String KEY_STREET_ADDRESS_1 = "StreetAddress";
    public static final String KEY_STREET_ADDRESS_2 = "StreetAddress2";
    public static final String KEY_ADDRESS_CITY = "City";
    public static final String KEY_LAT_LONG = "GoogleMapsCoordinates";
    public static final String KEY_OUTLET_NAME_HIS = "outlet_name";
    public static final String KEY_DISTANCE_HIS = "distance";
    public static final String KEY_FROM_TIME_HIS = "from_time";
    public static final String KEY_TO_TIME_HIS = "to_time";
    public static final String KEY_BOOKED_FOR_DATE = "booked_for_date";
    public static final String KEY_WORKOUT_BOOKING_ID = "booking_id";
    public static final String KEY_WORKOUT_CANCEL_REASON = "cancel_reason";
    public static final String KEY_DISTANCE_BOOK = "distance";
    public static final String KEY_WORKOUT_OUTLET_ID = "outlet_id";

    public static final String KEY_SERVICES = "services";
    public static final String KEY_FITMEIN_ENABLED = "fitmein";
    public static final String KEY_IS_CANCELLED = "is_cancelled";
    public static final String KEY_BOOKING_DATE = "booked_for_date";
    public static final String DISTANCE = "distance";

    public static final String KEY_STUDIO_OUTLET_NAME = "OutletName";
    public static final String KEY_STUDIO_ADDRESS = "StreetAddress1";
    public static final String KEY_STUDIO_DISTANCE = "Distance";
    public static final String KEY_STUDIO_PARTNER_NAME = "PartnerName";
    public static final String KEY_RECORDS = "records";


    //HRA
    public static final String KEY_HRA_ENABLED = "hra";

    public static final String BMI = "bmi";
    public static final String IDEAL_BODY_WEIGHT = "ideal_body_weight";
    public static final String DAILY_CALORIES_REQUIRED = "daily_calories_required";
    public static final String DIABETES_RISK = "diabetes_risk";
    public static final String HYPERTENSION_RISK = "hypertension_risk";
    public static final String OPTIMAL_HYPERTENSION_RISK = "optimal_hypertension_risk";

    // HRA intent keys
    public static final String STATUS_BLOOD_PRESSURE = "bp_results_available";

    public static final String BMI_VALUE = "bmi_value";
    public static final String BMI_RISK_LEVEL = "bmi_risk_level";
    public static final String BMI_COLOR = "bmi_color";

    public static final String IBW_SCORE = "ibw_score";
    public static final String IBW_YOUR_WEIGHT = "ibw_your_weight";

    public static final String RC_SCORE = "rc_score";
    public static final String RC_YOUR_CALORIES = "rc_your_calories";

    public static final String DIABETES_SCORE = "diabetes_score";
    public static final String DIABETES_RISK_LEVEL = "diabetes_risk_level";

    public static final String HT_4_YOUR_RISK = "ht_4_your_risk";
    public static final String HT_4_OPTIMAL = "ht_4_optimal";
    public static final String HT_2_YOUR_RISK = "ht_2_your_risk";
    public static final String HT_2_OPTIMAL = "ht_2_optimal";
    public static final String HT_1_YOUR_RISK = "ht_1_your_risk";
    public static final String HT_1_OPTIMAL = "ht_1_optimal";

    public static final String HT_1 = "ht_1";
    public static final String HT_2 = "ht_2";
    public static final String HT_4 = "ht_4";

    public static final String CVD_YOUR_RISK = "cvd_your_risk";
    public static final String CVD_NORMAL = "cvd_normal";
    public static final String CVD_OPTIMAL = "cvd_optimal";
    public static final String CVD_YOUR_HEART_AGE = "cvd_your_heart_age";

    public static final String STROKE_SCORE = "stroke_score";
    public static final String STROKE_RISK_LEVEL = "stroke_risk_level";
    public static final String BMR = "bmr";
    public static final String STROKE_POINTS = "stroke_points";

    public static final String KEY_SEATS_COUNT = "FitmeSeat";


    //HRA SAVE API KEYS

    public static final String KEY_USER_ID_HRA = "user_id";
    public static final String KEY_HEIGHT_HRA = "height";
    public static final String KEY_WEIGHT_HRA = "weight";
    public static final String KEY_WAIST = "waist_circumference";
    public static final String KEY_KNOW_BLOOD_PRESSURE = "know_bp_readings";
    public static final String KEY_SYSTOLIC = "sbp";
    public static final String KEY_DIASTOLIC = "dbp";
    public static final String KEY_DIABETES = "diabetes";
    public static final String KEY_HIGH_BLOOD_PRESSURE = "blood_pressure";
    public static final String KEY_HYPERTENSION = "hypertension";
    public static final String KEY_CVD = "cardiovascular";
    public static final String KEY_AF = "atrial_fibrillation";
    public static final String KEY_VHT = "ventricular_hypertrophy";
    public static final String KEY_PARENT_DIABETIC = "parents_diabetic";
    public static final String KEY_PARENT_HBP = "parents_hbp";
    public static final String KEY_PARENT_CARDIAC = "parents_cardiac_condition";
    public static final String KEY_SMOKING = "smoking_condition";
    public static final String KEY_CIGARETES_PER_DAY = "cigarettes_per_day";
    public static final String KEY_PHYSICAL_ACTIVITY = "physical_activity_state";
    public static final String KEY_CALORIES_INTAKE = "calories_intake";
    public static final String KEY_BMI = "bmi";
    public static final String KEY_IBW = "ibw";
    public static final String KEY_BMR = "bmr";
    public static final String KEY_CALORIES_REQUIRED = "calories_required";
    public static final String KEY_DIABETES_RISK = "diabetes_risk";
    public static final String KEY_HYPERTENSION_RISK = "hypertention_risk";
    public static final String KEY_1_YEAR = "1-year";
    public static final String KEY_2_YEAR = "2-years";
    public static final String KEY_4_YEAR = "4-years";
    public static final String KEY_SCORE = "score";
    public static final String KEY_OPTIMAL = "optimal";
    public static final String KEY_NORMAL = "normal";
    public static final String KEY_CVD_RISK = "cvd_risk";
    public static final String KEY_STROKE_RISK = "stroke_risk";
    public static final String KEY_POINTS = "points";
    public static final String KEY_PROPABILITY = "probability";
    public static final String KEY_HEART_AGE = "heart_age";
    public static final String STROKE_CAL_STATUS = "calculate_stroke";

    public static final String APP_THEME = "app_theme";
    public static final String KEY_CURRENT_THEME = "current_theme";
    public static final String DOCONLINE_THEME = "doconline";
    public static final String BETTERPLACE_THEME = "betterplace";
    public static final String TATA_THEME = "tata";
}

