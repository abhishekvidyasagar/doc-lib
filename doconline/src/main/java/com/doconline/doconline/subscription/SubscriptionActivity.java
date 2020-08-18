package com.doconline.doconline.subscription;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.doconline.doconline.Paytm.checksum;
import com.doconline.doconline.R;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.analytics.GATracker;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.MyViewPager;
import com.doconline.doconline.login.MainActivity;
import com.doconline.doconline.model.Subscription;
import com.doconline.doconline.model.SubscriptionPlan;
import com.doconline.doconline.model.User;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_EMAIL_VERIFICATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_INTERNET;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_MOBILE_VERIFICATION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_UPGRADE_SUBSCRIPTION;
import static com.doconline.doconline.api.HttpClient.REQUEST_LIMIT_EXCEED;
import static com.doconline.doconline.app.Constants.KEY_ACTION_TYPE;
import static com.doconline.doconline.app.Constants.KEY_ADDRESS;
import static com.doconline.doconline.app.Constants.KEY_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_CODE;
import static com.doconline.doconline.app.Constants.KEY_CONTACT;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_DESCRIPTION;
import static com.doconline.doconline.app.Constants.KEY_DISCOUNT;
import static com.doconline.doconline.app.Constants.KEY_DOCONLINE_USER_ID;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_FAMILY_TYPE;
import static com.doconline.doconline.app.Constants.KEY_IMAGE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_NAME;
import static com.doconline.doconline.app.Constants.KEY_NOTES;
import static com.doconline.doconline.app.Constants.KEY_ONETIME_TYPE;
import static com.doconline.doconline.app.Constants.KEY_ORDER_ID;
import static com.doconline.doconline.app.Constants.KEY_PLAN_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_PLATFORM;
import static com.doconline.doconline.app.Constants.KEY_PREFILL;
import static com.doconline.doconline.app.Constants.KEY_RAZORPAY_ID;
import static com.doconline.doconline.app.Constants.KEY_RECURRING_TYPE;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION_ID;
import static com.doconline.doconline.app.Constants.SUBSCRIPTION_PERMISSION_REQ_CODE;
import static com.doconline.doconline.app.Constants.TYPE_B2B;
import static com.doconline.doconline.app.Constants.TYPE_B2B_PAID;


public class SubscriptionActivity extends BaseActivity implements PaymentResultListener, ExternalWalletListener {
    private static final String TAG = SubscriptionActivity.class.getSimpleName();


    RelativeLayout layout_refresh;
    RelativeLayout layout_block_ui;
    RelativeLayout layout_progress;
    TextView tv_timing;
    TextView toolbar_title;
    CoordinatorLayout layout_root_view;
    Toolbar toolbar;
    MyViewPager mViewPager;

    /**
     * Onetime Discount Code Layout
     */

    NestedScrollView layout_discount_coupon_code;
    EditText edit_discount_coupon_code;
    TextView tv_plan_name;
    TextView tv_plan_amount;
    TextView tv_plan_discount;
    TextView tv_plan_total;
    Button btn_apply_coupon;
    TextView tv_coupon_status;

    Button btnDecline,btnProceedToPay;

    private SectionsPagerAdapter adapter;
    public static List<SubscriptionPlan> mPlans = new ArrayList<>();

    private int index = -1;
    private boolean is_processing = false;
    private boolean is_upgrade = false;
    private String mUpgradeURL = "";

    public static int user_id;
    public double amount;


    public static String address = "";
    private String razorpay_id = "";
    private String razorpay_payment_id = "";
    private String payment_type = "";
    private String discount_coupon = "";
    private boolean is_discount_coupon_applied = false;
    private SubscriptionPlan plan;
    private String payment_gateway = "";
    String appliedCouponCode = "";

    public static final int HTTP_REQUEST_CODE_GET_PROFILE_STATE = 1;
    public static final int HTTP_REQUEST_CODE_GET_PLANS = 2;
    public static final int HTTP_REQUEST_CODE_CANCEL_SUBSCRIPTION = 3;
    public static final int HTTP_REQUEST_CODE_SUBSCRIBE_PLAN = 4;
    public static final int HTTP_REQUEST_CODE_UPGRADE_SUBSCRIPTION = 5;
    public static final int HTTP_REQUEST_CODE_APPLY_PROMO_CODE = 6;
    public static final int HTTP_REQUEST_CODE_GET_ONETIME_PLAN = 7;
    public static final int HTTP_REQUEST_CODE_UPDATE_PAYMENT_INFO = 8;
    public static final int HTTP_REQUEST_CODE_RETRY_PAYMENT = 9;
    public static final int HTTP_REQUEST_CODE_APPLY_COUPON_CODE = 10;

    public static final int PAYTM_PAYMENT_REQUEST_CODE = 11;

    // discount for one-time payment is included in this.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        layout_refresh = findViewById(R.id.layout_refresh);
        layout_block_ui= findViewById(R.id.layout_block_ui);
        layout_progress =findViewById(R.id.layout_loading);
        tv_timing =findViewById(R.id.tv_timing);
        toolbar_title=findViewById(R.id.toolbar_title);
        layout_root_view=findViewById(R.id.layout_root_view);
        toolbar=findViewById(R.id.toolbar);

        mViewPager=findViewById(R.id.pager);

        /**
         * Onetime Discount Code Layout
         */
        layout_discount_coupon_code=findViewById(R.id.layout_discount_coupon_code);
        edit_discount_coupon_code=findViewById(R.id.edit_discount_coupon_code);
        tv_plan_name=findViewById(R.id.tv_plan_name);
        tv_plan_amount=findViewById(R.id.tv_plan_amount);
        tv_plan_discount=findViewById(R.id.tv_plan_discount);
        tv_plan_total=findViewById(R.id.tv_plan_total);
        btn_apply_coupon=findViewById(R.id.btnCouponApply);
        tv_coupon_status=findViewById(R.id.tv_coupon_status);
        btnDecline = findViewById(R.id.btnDecline);
        btnProceedToPay = findViewById(R.id.btnProceedToPay);

        btnProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payOnetimePayment();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_discount_coupon_code.setVisibility(View.GONE);
               removeCoupon();
            }
        });

        btn_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new InternetConnectionDetector(SubscriptionActivity.this).isConnected()) {
                    /**
                     * If discount code not applied
                     * apply discount code
                     */
                    if (!is_discount_coupon_applied) {
                        tv_coupon_status.setVisibility(View.INVISIBLE);
                        String coupon_code = edit_discount_coupon_code.getText().toString();

                        if (coupon_code.length() < 3) {
                            Toast.makeText(getApplicationContext(), "Coupon code must be at least 3 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        onTaskCompleted(true, -2, "");
                        new HttpClient(HTTP_REQUEST_CODE_APPLY_COUPON_CODE, MyApplication.HTTPMethod.POST.getValue(), Subscription.composeOrderJSON(coupon_code, plan.getPlanType()), SubscriptionActivity.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getCouponCodeURL());
                    }

                    /**
                     * remove discount code
                     */
                    else {
                        removeCoupon();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }
            }
        });

       // ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mPlans.clear();

        /**
         * To ensure faster loading of the Checkout form,
         * call this method as early as possible in your checkout flow.
         */
        // initialize razorpay
        Checkout.preload(getApplicationContext());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Fixes bug for disappearing fragment content
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);

        setupViewPager(mViewPager);

        if (!new InternetConnectionDetector(this).isConnected()) {
            new CustomAlertDialog(this, DIALOG_REQUEST_CODE_INTERNET, this).showNetworkAlertDialog();
        }

        tv_plan_discount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(0));
    }


    private void setupViewPager(MyViewPager viewPager) {
        toolbar_title.setText(getResources().getString(R.string.text_subscription_plans));

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(SubscriptionFragment.newInstance(), getResources().getString(R.string.text_confirmation));

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:

                        toolbar_title.setText(getResources().getString(R.string.text_subscription_plans));
                        break;

                    case 1:

                        toolbar_title.setText(getResources().getString(R.string.text_confirmation));
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onPositiveAction(int requestCode) {
        // for upgrading the subscription
        if (requestCode == DIALOG_REQUEST_CODE_UPGRADE_SUBSCRIPTION) {
            if (new InternetConnectionDetector(SubscriptionActivity.this).isConnected()) {
                is_upgrade = true;
                is_processing = true;
                layout_progress.setVisibility(View.VISIBLE);

                new HttpClient(HTTP_REQUEST_CODE_UPGRADE_SUBSCRIPTION, MyApplication.HTTPMethod.POST.getValue(), SubscriptionActivity.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mUpgradeURL);
            } else {
                new CustomAlertDialog(getApplicationContext(), this, layout_root_view).snackbarForInternetConnectivity();
            }

            return;
        }

        //  If mobile is not verified
        if (requestCode == DIALOG_REQUEST_CODE_MOBILE_VERIFICATION) {
            Intent intent = new Intent(SubscriptionActivity.this, MainActivity.class);
            intent.putExtra("INDEX", 4);
            startActivity(intent);
            return;
        }

        // If email is not verified
        if (requestCode == DIALOG_REQUEST_CODE_EMAIL_VERIFICATION) {
            Intent intent3 = new Intent(SubscriptionActivity.this, MainActivity.class);
            intent3.putExtra("INDEX", 9);
            startActivity(intent3);
            return;
        }

        if (requestCode == DIALOG_REQUEST_CODE_INTERNET) {
            finish();
        }
    }


    @Override
    public void onNegativeAction() {

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        if (isFinishing()) {
            return;
        }

        try {
            if (requestCode == HTTP_REQUEST_CODE_GET_PROFILE_STATE && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(String.valueOf(response));
                    String data = (json.isNull(KEY_DATA)) ? "" : json.getString(KEY_DATA);
                    this.json_data_profile(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return;
            }

            // If 200 then call profile state otherwise keep calling payment update.
            if (requestCode == HTTP_REQUEST_CODE_UPDATE_PAYMENT_INFO) {
                if (responseCode == HttpClient.OK) {
                    new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), SubscriptionActivity.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
                } else {
                    onPaymentUpdate(Helper.getRandomInteger(1000, 3000), razorpay_payment_id);
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_ONETIME_PLAN && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    razorpay_id = json.getString(KEY_DATA);

                    SubscriptionPlan plan = mPlans.get(index);
                    plan.setOrderId(razorpay_id);

                    startPayment(plan, is_upgrade);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    layout_discount_coupon_code.setVisibility(View.GONE);
                    removeCoupon();
                    onTaskCompleted(false, -1, "");
                }

                return;
            }

            if ((requestCode == HTTP_REQUEST_CODE_SUBSCRIBE_PLAN || requestCode == HTTP_REQUEST_CODE_UPGRADE_SUBSCRIPTION) && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);
                    razorpay_id = json.getString(KEY_RAZORPAY_ID);

                    SubscriptionPlan plan = mPlans.get(index);
                    plan.setSubscriptionId(razorpay_id);

                    startPayment(plan, is_upgrade);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    onTaskCompleted(false, -1, "");
                }
                return;
            }

            if ((requestCode == HTTP_REQUEST_CODE_SUBSCRIBE_PLAN || requestCode == HTTP_REQUEST_CODE_UPGRADE_SUBSCRIPTION) && responseCode == HttpClient.PRECONDITION_FAILED) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    if (json.has(KEY_MOBILE_NO)) {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_MOBILE_VERIFICATION, json.getString(KEY_MOBILE_NO));
                    } else if (json.has(KEY_EMAIL)) {
                        this.showAlertDialog(DIALOG_REQUEST_CODE_EMAIL_VERIFICATION, json.getString(KEY_EMAIL));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_APPLY_COUPON_CODE && responseCode == HttpClient.OK) {
                JSONObject json = new JSONObject(response);
                json = json.getJSONObject(KEY_DATA);

                double plan_amount = json.getDouble(KEY_PLAN_AMOUNT);
                double discount = json.getDouble(KEY_DISCOUNT);
                double amount = json.getDouble(KEY_AMOUNT);
                discount_coupon = json.getString(KEY_CODE);

                tv_plan_total.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(amount));
                tv_plan_discount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(discount));

                btn_apply_coupon.setText("Remove");
                tv_coupon_status.setVisibility(View.VISIBLE);
                tv_coupon_status.setTextColor(getResources().getColor(R.color.light_green));
                tv_coupon_status.setText("Coupon Applied Successfully");
                this.is_discount_coupon_applied = true;
                return;
            }

            if (responseCode == HttpClient.UNPROCESSABLE_ENTITY) {
                try {
                    JSONObject json = new JSONObject(response);

                    if (requestCode == HTTP_REQUEST_CODE_APPLY_COUPON_CODE) {
                        tv_coupon_status.setTextColor(getResources().getColor(R.color.light_red));
                        tv_coupon_status.setVisibility(View.VISIBLE);
                        tv_coupon_status.setText(String.valueOf(json.getString(KEY_MESSAGE)));
                        edit_discount_coupon_code.setText("");
                    } else {
                        this.showDialog("Info", json.getString(KEY_MESSAGE));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(getApplicationContext(), this, layout_root_view).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (responseCode == REQUEST_LIMIT_EXCEED || requestCode == HTTP_REQUEST_CODE_APPLY_COUPON_CODE) {
                is_processing = false;
                layout_progress.setVisibility(View.GONE);
                layout_refresh.setVisibility(View.INVISIBLE);
            }
        }
    }

    // done by client
    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        if (s.equalsIgnoreCase("paytm")) {
            Intent i = new Intent(SubscriptionActivity.this, checksum.class);
            i.putExtra("orderid", plan.getOrderId());
            i.putExtra("url", mController.getCheckSumURL());
            i.putExtra("email", paymentData.getUserEmail());
            i.putExtra("phone", paymentData.getUserContact());
            i.putExtra("amount", amount);
            i.putExtra("userid", user_id);


            startActivityForResult(i, PAYTM_PAYMENT_REQUEST_CODE);
        }

    }

    // done by client
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PAYTM_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle requiredValue = data.getBundleExtra("paytmresponse");
                if (requiredValue == null) {
                    Toast.makeText(this, "something went wrong contact support", Toast.LENGTH_SHORT).show();
                }

                if (requiredValue.get("STATUS").toString().equalsIgnoreCase("TXN_FAILURE")) {
                    Toast.makeText(this, "Transaction failed try again", Toast.LENGTH_SHORT).show();
                    payment_gateway = "paytm";
                    onPaymentUpdate(Helper.getRandomInteger(1000, 3000), "");
                    is_processing = true;
                }
                if (requiredValue.get("STATUS").toString().equalsIgnoreCase("TXN_SUCCESS")) {//check this key once later
                    Toast.makeText(this, "Transaction Success", Toast.LENGTH_SHORT).show();
                    payment_gateway = "paytm";
                    this.razorpay_payment_id = requiredValue.get("TXNID").toString();

                    onPaymentUpdate(Helper.getRandomInteger(1000, 3000), razorpay_payment_id);


                    String amt = requiredValue.get("TXNAMOUNT").toString();
                    try {
                        //if (!BuildConfig.DEBUG) {
                       /* Map<String, Object> eventValue = new HashMap<>();
                        eventValue.put(AFInAppEventParameterName.REVENUE, amt);
                        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "category_a");
                        eventValue.put(AFInAppEventParameterName.CONTENT_ID, "1234567");
                        eventValue.put(AFInAppEventParameterName.CURRENCY, "INR");
                        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.PURCHASE, eventValue);*/

                        //for adgyde
                       /* HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Purchase_Successful", amt);//patrametre name,value
                        PAgent.onEvent("Purchase_Successful", params);//eventid
                        PAgent.flush();*/

                        //PAgent.onRevenue(Integer.parseInt(amt));
                        //PAgent.flush();
                        //}
                    } catch (Exception e) {
                        Log.d("AAA", "Exception" + e);
                    }

                }

            }
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public static void start(final Activity activity) {
        activity.startActivity(new Intent(activity, SubscriptionActivity.class));
    }


    @Override
    public void onBackPressed() {
        // if coupon layout is seen, hide it.
        if (layout_discount_coupon_code.getVisibility() == View.VISIBLE) {
            layout_discount_coupon_code.setVisibility(View.GONE);
            return;
        }

        // If payment is processing, don't go back
        if (!is_processing) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Processing", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (layout_discount_coupon_code.getVisibility() == View.VISIBLE) {
                    layout_discount_coupon_code.setVisibility(View.GONE);
                    return false;
                }

                if (!is_processing) {
                    this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Processing", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlans.clear();
    }

    public void startPayment(SubscriptionPlan plan, boolean is_upgrade) {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setKeyID(mController.getSession().getRazorpayApiKey());

        try {
            JSONObject options = new JSONObject();

            options.put(KEY_NAME, getResources().getString(R.string.app_name));
            options.put(KEY_DESCRIPTION, plan.getPlanName());

            options.put(KEY_IMAGE, "https://app.doconline.com/assets/images/logo.png");

            //options.put(KEY_CURRENCY, plan.getCurrency());
            //options.put(KEY_AMOUNT, (plan.getAmount() * 100));

            if (plan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE)) {
                options.put(KEY_SUBSCRIPTION_ID, plan.getSubscriptionId());
            } else {
                options.put(KEY_ORDER_ID, plan.getOrderId());
            }

            JSONObject preFill = new JSONObject();
            preFill.put(KEY_EMAIL, mController.getSession().getEmail());
            preFill.put(KEY_CONTACT, mController.getSession().getMobileNumber());

            JSONObject notesFill = new JSONObject();
            notesFill.put(KEY_ADDRESS, address);
            notesFill.put(KEY_PLATFORM, "android");
            notesFill.put(KEY_DOCONLINE_USER_ID, user_id);

            if (plan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE)) {
                notesFill.put(KEY_SUBSCRIPTION_ID, plan.getSubscriptionId());
            } else {
                notesFill.put(KEY_ORDER_ID, plan.getOrderId());

                amount = plan.getAmount();
                razorpay_id = plan.getOrderId();
                appliedCouponCode = plan.getAppliedCouponCode();

                // done by client
                JSONArray walletArray = new JSONArray();
                walletArray.put("paytm");
                JSONObject map = new JSONObject();
                map.put("wallets", walletArray);
                options.put("external", map);
            }

            if (is_upgrade) {
                notesFill.put(KEY_ACTION_TYPE, "upgrade");
            } else {
                notesFill.put(KEY_ACTION_TYPE, "fresh");
            }

            options.put(KEY_NOTES, notesFill);
            options.put(KEY_PREFILL, preFill);

            co.open(activity, options);
        } catch (Exception e) {
            this.showDialog("Error", "Error in payment : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void onPaymentUpdate(final int duration, final String razorpayPaymentID) {
        if (payment_gateway.equalsIgnoreCase("")) {
            payment_gateway = "razorpay";
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                is_processing = true;

                if (duration != 100) {
                    tv_timing.setVisibility(View.VISIBLE);
                }

                String json_data = Subscription.composePaymentJSON(razorpay_id, razorpayPaymentID, payment_gateway, appliedCouponCode);
                new HttpClient(HTTP_REQUEST_CODE_UPDATE_PAYMENT_INFO, MyApplication.HTTPMethod.POST.getValue(), true, json_data, SubscriptionActivity.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getPaymentURL() + "success");

            }
        }, duration);
    }


    private void onSubscriptionUpdate(int duration) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mController.getSession().getSubscriptionStatus()) {
                    redirectSuccess(razorpay_payment_id);
                } else {
                    tv_timing.setVisibility(View.VISIBLE);

                    if (new InternetConnectionDetector(SubscriptionActivity.this).isConnected()) {
                        is_processing = true;
                        new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), SubscriptionActivity.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
                    }
                }
            }
        }, duration);


    }


    private void redirectSuccess(String razorpayPaymentID) {
        is_processing = false;
        layout_progress.setVisibility(View.GONE);

        if (getIntent().getIntExtra("SUBSCRIPTION_VIA_BOOK_CONSULTATION", 0) == 1) {
            Intent intent = new Intent();
            setResult(SUBSCRIPTION_PERMISSION_REQ_CODE, intent);
            finish();
        } else {
            if (index == -1){
                String plan_name = "OneTime";
                adapter.addFrag(new SubscriptionSuccessFragment(this, plan_name, razorpay_id, razorpayPaymentID, payment_type, is_upgrade), "Subscription");
            }else {
                String plan_name = mPlans.get(index).getPlanName();
                adapter.addFrag(new SubscriptionSuccessFragment(this, plan_name, razorpay_id, razorpayPaymentID, payment_type, is_upgrade), "Subscription");
            }

            adapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(adapter.getCount() - 1);
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            /**
             * If Subscription/Payment from book consultation or chat
             * Display Processing until subscription status updated on profile
             */
            is_processing = true;
            layout_progress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        } finally {
            try {
                this.razorpay_payment_id = razorpayPaymentID;
                if (!isFinishing()) {
                    layout_refresh.setVisibility(View.INVISIBLE);

                    if (new InternetConnectionDetector(this).isConnected()) {
                        /**
                         * If recurring type payment call profile API
                         */
                        if (payment_type.equalsIgnoreCase(KEY_RECURRING_TYPE)) {
                            new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), SubscriptionActivity.this)
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());

                            /**
                             * Track payment success event (Google Analytics)
                             */
                            GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_SUBSCRIPTION_PAYMENT_SUCCESS, plan.getDisplayName(), (long) plan.getAmount());

                        }

                        /**
                         * If onetime payment then call payment update API
                         * On success response call profile API
                         * Else retry
                         */
                        if (payment_type.equalsIgnoreCase(KEY_ONETIME_TYPE) || payment_type.equalsIgnoreCase("")) {
                            onPaymentUpdate(100, razorpayPaymentID);

                            /**
                             * Track payment success event (Google Analytics)
                             */
                            GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_ONETIME_PAYMENT_SUCCESS, plan.getDisplayName(), (long) plan.getAmount());
                        }
                    }
                }
            }catch (Exception e){
                Log.e("AAA","ONPAYMENT SUCCESS ERROR"+e);
            }

        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        Log.e("onPaymentError", "" + response + " " + code);

        try {
            Toast.makeText(getApplicationContext(), "Subscription not completed", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        } finally {
            try {
                /**
                 * If recurring type payment call profile API
                 */
                if (payment_type.equalsIgnoreCase(KEY_RECURRING_TYPE)) {
                    /**
                     * Track payment success event (Google Analytics)
                     */
                    GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_SUBSCRIPTION_PAYMENT_FAIL, plan.getDisplayName(), (long) plan.getAmount());

                }

                if (payment_type.equalsIgnoreCase(KEY_ONETIME_TYPE)) {
                    /**
                     * Track payment success event (Google Analytics)
                     */
                    GATracker.trackEvent(GATracker.GA_CATEGORY_PAYMENT, GATracker.GA_ACTION_ONETIME_PAYMENT_FAIL, plan.getDisplayName(), (long) plan.getAmount());
                }

                if (!isFinishing()) {
                    onTaskCompleted(false, -1, "");
                    String amt = String.valueOf(mPlans.get(this.index).getAmount());
                }
            }catch (Exception e){
                Log.e("AAA","ONPAYMENTERROREXCEPTION"+e);
            }

        }
    }


    private void upgrade(String title, String content, final String url) {
        new CustomAlertDialog(this, DIALOG_REQUEST_CODE_UPGRADE_SUBSCRIPTION, this).
                showDialogWithAction(title, content, getResources().getString(R.string.Upgrade),
                        getResources().getString(R.string.NotNow), true);
    }


    private void showDialog(String title, String content) {
        if (!isFinishing()) {
            this.is_upgrade = false;
            is_processing = false;
            layout_progress.setVisibility(View.GONE);

            new CustomAlertDialog(this, this).
                    showAlertDialogWithPositiveAction(title, content, getResources().getString(R.string.OK), true);
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int index, String action) {
        Log.v("getUserType",""+mController.getSession().getUserType());
        if (flag) {
            // to show progress
            if (index == -2) {
                layout_progress.setVisibility(View.VISIBLE);
                return;
            }

            this.index = index;

            if (index != -1) {
                plan = mPlans.get(index);
                this.payment_type = plan.getType();

                if (new InternetConnectionDetector(this).isConnected()) {
                    // if you want to upgrade the current plan
                    if (action.equals("upgrade") && plan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE)) {
                        String message = "You are currently subscribed to a " + SubscriptionFragment.currentPlanName.toUpperCase() +
                                " plan. Do you want to upgrade to a " + plan.getDisplayName() + " plan";
                        this.mUpgradeURL = mController.getSubscriptionURL() + action + "/" + plan.getPlanId();

                        this.upgrade("Upgrade Plan", message, mController.getSubscriptionURL() + action + "/" + plan.getPlanId());
                        // if you want to subscribe(reoccuring) new plan
                    } else if (plan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE)) {
                        this.is_upgrade = false;
                        is_processing = true;
                        layout_progress.setVisibility(View.VISIBLE);

                        new HttpClient(HTTP_REQUEST_CODE_SUBSCRIBE_PLAN, MyApplication.HTTPMethod.POST.getValue(), this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSubscriptionURL() + plan.getPlanId());
                    }
                    // for one time payment
                    else if (plan.getType().equalsIgnoreCase(KEY_ONETIME_TYPE)) {

                        /**
                         * If B2B UNPAID user
                         * display coupon code UI
                         * and proceed to payment on button click
                         */
                        if (!(mController.getSession().getUserType().equalsIgnoreCase(TYPE_B2B_PAID)
                                || mController.getSession().getUserType().equalsIgnoreCase(TYPE_B2B))) {
                            layout_discount_coupon_code.setVisibility(View.VISIBLE);
                            tv_plan_name.setText(plan.getDisplayName().toUpperCase());
                            tv_plan_amount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(plan.getAmount()));
                            tv_plan_total.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(plan.getAmount()));
                        }

                        /**
                         * If b2B PAID user
                         * Proceed to payment
                         */
                        else {
                            this.payOnetimePayment();
                        }
                    }

                    // Family type plan
                    else if (plan.getPlanType().equalsIgnoreCase(KEY_FAMILY_TYPE)) {
                        this.payFamilyPayment();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
                }
            } else {
                layout_refresh.setVisibility(View.VISIBLE);
            }
        } else {
            if (!isFinishing()) {
                is_processing = false;
                layout_progress.setVisibility(View.GONE);
                layout_refresh.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void json_data_profile(String data) {
        try {
            User user = User.getUserProfileFromJSON(data);

            user_id = user.getUserId();
            address = user.getAddress().getFullAddress();

            if (user.getUserAccount().getSubscription().getSubscriptionStatus()) {
                redirectSuccess(this.razorpay_payment_id);
            } else {
                onSubscriptionUpdate(Helper.getRandomInteger(3000, 7000));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showAlertDialog(int requestCode, String message) {
        onTaskCompleted(false, -1, "");

        new CustomAlertDialog(this, requestCode, this)
                .showDialogWithAction(getResources().getString(R.string.dialog_title_account_info), message,
                        getResources().getString(R.string.Verify),
                        getResources().getString(R.string.Cancel), true);
    }

    /**
     * UI changes on coupon code
     */
    private void removeCoupon() {
        edit_discount_coupon_code.setText("");
        tv_coupon_status.setVisibility(View.INVISIBLE);
        btn_apply_coupon.setText("Apply");

        this.discount_coupon = "";
        this.is_discount_coupon_applied = false;

        tv_plan_amount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(mPlans.get(index).getAmount()));
        tv_plan_total.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(mPlans.get(index).getAmount()));
        tv_plan_discount.setText(getResources().getString(R.string.Rs) + " " + Constants.df.format(0));
    }


    public void payFamilyPayment() {
        if (new InternetConnectionDetector(this).isConnected()) {
            this.is_upgrade = false;
            is_processing = true;
            layout_progress.setVisibility(View.VISIBLE);

            new HttpClient(HTTP_REQUEST_CODE_GET_ONETIME_PLAN, MyApplication.HTTPMethod.POST.getValue(), Subscription.composeOrderJSON("", plan.getPlanType()), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getPaymentURL() + "orderid");
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
        }
    }


    private void payOnetimePayment() {
        if (new InternetConnectionDetector(this).isConnected()) {
            this.is_upgrade = false;
            is_processing = true;
            layout_progress.setVisibility(View.VISIBLE);

            /**
             * If coupon applied
             * POST data with coupon code and get razorpay_id from api
             */
            if (is_discount_coupon_applied && !discount_coupon.isEmpty()) {
                new HttpClient(HTTP_REQUEST_CODE_GET_ONETIME_PLAN, MyApplication.HTTPMethod.POST.getValue(), Subscription.composeOrderJSON(discount_coupon, plan.getPlanType()), this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getPaymentURL() + "orderid");
                return;
            }

            /**
             * If coupon not applied
             */
            new HttpClient(HTTP_REQUEST_CODE_GET_ONETIME_PLAN, MyApplication.HTTPMethod.POST.getValue(), Subscription.composeOrderJSON("", plan.getPlanType()), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getPaymentURL() + "orderid");
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
        }
    }

}