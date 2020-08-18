package com.doconline.doconline.subscription;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.adapter.SubscriptionPlanRecyclerAdapter;
import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.api.HttpResponseHandler;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.connectivity.InternetConnectionDetector;
import com.doconline.doconline.helper.BaseFragment;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.Subscription;
import com.doconline.doconline.model.SubscriptionPlan;
import com.doconline.doconline.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_CANCEL_SUBSCRIPTION;
import static com.doconline.doconline.alert.CustomAlertDialog.DIALOG_REQUEST_CODE_RETRY_PAYMENT;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_FAMILY_TYPE;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;
import static com.doconline.doconline.app.Constants.KEY_ONETIME_TYPE;
import static com.doconline.doconline.app.Constants.MMM_DD_YYYY_HH_MM_A;
import static com.doconline.doconline.app.Constants.PAYMENT_HALTED;
import static com.doconline.doconline.app.Constants.TYPE_B2B;
import static com.doconline.doconline.app.Constants.TYPE_B2B_PAID;
import static com.doconline.doconline.app.Constants.TYPE_B2C;
import static com.doconline.doconline.app.Constants.TYPE_CORPORATE;
import static com.doconline.doconline.app.Constants.TYPE_FAMILY;
import static com.doconline.doconline.app.Constants.TYPE_ONETIME;
import static com.doconline.doconline.app.Constants.YY_MM_DD_HH_MM_SS;
import static com.doconline.doconline.subscription.SubscriptionActivity.HTTP_REQUEST_CODE_APPLY_PROMO_CODE;
import static com.doconline.doconline.subscription.SubscriptionActivity.HTTP_REQUEST_CODE_CANCEL_SUBSCRIPTION;
import static com.doconline.doconline.subscription.SubscriptionActivity.HTTP_REQUEST_CODE_GET_ONETIME_PLAN;
import static com.doconline.doconline.subscription.SubscriptionActivity.HTTP_REQUEST_CODE_GET_PLANS;
import static com.doconline.doconline.subscription.SubscriptionActivity.HTTP_REQUEST_CODE_GET_PROFILE_STATE;
import static com.doconline.doconline.subscription.SubscriptionActivity.HTTP_REQUEST_CODE_RETRY_PAYMENT;
import static com.doconline.doconline.subscription.SubscriptionActivity.mPlans;


public class SubscriptionFragment extends BaseFragment implements View.OnClickListener {
    private SubscriptionPlanRecyclerAdapter mAdapter;


    RecyclerView mRecyclerView;

    NestedScrollView layout_scroll_view;

    LinearLayout layout_empty;

    TextView empty_message;

    LinearLayout layout_subscribed_plan_details;

    LinearLayout layout_plan_details;

    LinearLayout layout_pending_subscription;

    LinearLayout layout_coupon_code;

    TextView tv_subscription_type;

    TextView tv_plan_name;

    TextView tv_subscribed_on;

    TextView tv_ends_on;

    TextView tv_next_due;

    TextView tv_amount;

    TextView tv_billing_frequency;

    TextView tv_plan_description;

    TextView tv_pending_subscription;

    EditText edit_coupon_code;

    Button btn_cancel;

    Button btn_apply;

    private OnTaskCompleted listener;
    private String active_plan_id = "";
    private int index;
    public static String currentPlanName = "";
    private boolean is_cancel_membership = false;

    String coupon_code = "";

    private SimpleDateFormat sdf = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
    private SimpleDateFormat sdf1 = new SimpleDateFormat(MMM_DD_YYYY_HH_MM_A);


    public static SubscriptionFragment newInstance() {
        return new SubscriptionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);
       // ButterKnife.bind(this, view);

         mRecyclerView = view.findViewById(R.id.recycler_view);
        layout_scroll_view= view.findViewById(R.id.layout_scroll_view);
        layout_empty= view.findViewById(R.id.layout_empty);
         empty_message= view.findViewById(R.id.empty_message);
         layout_subscribed_plan_details= view.findViewById(R.id.layout_subscribed_plan_details);
        layout_plan_details= view.findViewById(R.id.layout_plan_details);
         layout_pending_subscription= view.findViewById(R.id.layout_pending_subscription);
         layout_coupon_code= view.findViewById(R.id.layout_coupon_code);
         tv_subscription_type= view.findViewById(R.id.tv_subscription_type);
         tv_plan_name= view.findViewById(R.id.tvName);
         tv_subscribed_on= view.findViewById(R.id.tvSubscriptionDate);
        tv_ends_on= view.findViewById(R.id.tvEndDate);
         tv_next_due= view.findViewById(R.id.tvNextDueDate);
         tv_amount = view.findViewById(R.id.tvAmount);
         tv_billing_frequency= view.findViewById(R.id.billing_frequency);
         tv_plan_description= view.findViewById(R.id.plan_description);
         tv_pending_subscription= view.findViewById(R.id.tv_pending_subscription);
        edit_coupon_code= view.findViewById(R.id.editCouponCode);
         btn_cancel= view.findViewById(R.id.btnCancel);
         btn_apply= view.findViewById(R.id.btnApply);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout_empty.setVisibility(View.GONE);
        this.initPlanAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof OnTaskCompleted) {
            listener = (OnTaskCompleted) getActivity();
        }
        this.syncData();
        this.addListener();
    }


    private void addListener() {
        btn_cancel.setOnClickListener(this);
        btn_apply.setOnClickListener(this);
    }


    private void syncData() {
        if (new InternetConnectionDetector(getContext()).isConnected()) {
            listener.onTaskCompleted(true, -1, "");

            new HttpClient(HTTP_REQUEST_CODE_GET_ONETIME_PLAN, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSubscriptionPlanURL() + "onetime");

            /*if (!mController.getSession().getUserType().equalsIgnoreCase(TYPE_B2B_PAID)) {*/
            new HttpClient(HTTP_REQUEST_CODE_GET_PLANS, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSubscriptionPlanURL());
            /*}*/

            new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
        }
    }


    private void initPlanAdapter() {
        if (mAdapter != null) {
            return;
        }

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new SubscriptionPlanRecyclerAdapter(getContext(), this, mPlans);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new SubscriptionPlanRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int i) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        if (!new InternetConnectionDetector(getContext()).isConnected()) {
            new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            return;
        }

        int id = view.getId();
        if (id == R.id.btnCancel) {
            this.cancel_subscription("Cancel Membership ?", "Are you sure want to cancel membership ?");
        } else if (id == R.id.btnApply) {
            coupon_code = edit_coupon_code.getText().toString();

            if (coupon_code.length() < 3) {
                Toast.makeText(getContext(), "Coupon code must be at least 3 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (new InternetConnectionDetector(getContext()).isConnected()) {

                new HttpClient(HTTP_REQUEST_CODE_APPLY_PROMO_CODE, MyApplication.HTTPMethod.POST.getValue(), Subscription.composePromoCodeJSON(coupon_code), this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getPromoCodeURL());

                listener.onTaskCompleted(true, -2, "");
            }
        }
    }


    private void cancel_subscription(String title, String content) {
        new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_CANCEL_SUBSCRIPTION, this).
                showDialogWithAction(title, content, getResources().getString(R.string.Yes),
                        getResources().getString(R.string.No), true);
    }


    private void adapter_refresh() {
        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getView() == null) {
            return;
        }

        try {

            if (mPlans.size() == 0) {
                if (active_plan_id.isEmpty()) {
                    layout_empty.setVisibility(View.VISIBLE);
                    empty_message.setText("No Plans to Display");
                }
            } else {
                for (int index = 0; index < mPlans.size(); index++) {
                    if (!mPlans.get(index).getPlanId().isEmpty() &&
                            mPlans.get(index).getPlanId().equalsIgnoreCase(active_plan_id)) {
                        mPlans.get(index).setIsActive(true);
                    } else {
                        mPlans.get(index).setIsActive(false);
                    }
                }

                layout_empty.setVisibility(View.GONE);
            }

            mAdapter.notifyDataSetChanged();

            if (!mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_ONETIME_TYPE)
                    && mController.getSession().getSubscriptionPaymentStatus().equalsIgnoreCase(PAYMENT_HALTED)) {
                mRecyclerView.post(new Runnable() {

                    @Override
                    public void run() {
                        layout_scroll_view.fullScroll(NestedScrollView.FOCUS_DOWN);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void display_subscribed_plan(Subscription subscription, String pending_subscription) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getContext() == null) {
            return;
        }

        if (getView() == null) {
            return;
        }

        try {
            if (mController.getSession().getSubscriptionStatus() && mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2C)
                    && subscription.getEndsAt().isEmpty()) {
                btn_cancel.setVisibility(View.VISIBLE);
            } else {
                btn_cancel.setVisibility(View.GONE);
            }


            layout_subscribed_plan_details.setVisibility(View.VISIBLE);

            Animation animSlideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
            layout_subscribed_plan_details.startAnimation(animSlideDown);
            if (mController.getSession().getSubscriptionStatus()) {
                tv_subscription_type.setTextColor(ContextCompat.getColor(getContext(), R.color.light_green));
                tv_subscription_type.setText("You are under DOCONLINE " + mController.getSession().getSubscriptionType().toUpperCase() + " membership");
                currentPlanName = mController.getSession().getSubscriptionType().toUpperCase();

                if (mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B)) {
                    tv_subscription_type.setText("You are under DOCONLINE ENTERPRISE membership");
                    currentPlanName = "ENTERPRISE";
                }

                if (mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_ONETIME)
                        || mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B_PAID)) {
                    tv_subscription_type.setText("You have 1 consultation credit");
                    currentPlanName = "ONETIME";
                }
            } else {
                tv_subscription_type.setTextColor(ContextCompat.getColor(getContext(), R.color.light_red));
                tv_subscription_type.setText("CHOOSE YOUR PLAN & PAY");
                currentPlanName = "";
            }

            if (mController.getSession().getSubscriptionStatus() && mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_FAMILY_TYPE)) {
                layout_pending_subscription.setVisibility(View.GONE);

                layout_plan_details.setVisibility(View.VISIBLE);
                /* subscriptionPlan.getPlanName().toLowerCase() + " plan"*/
                tv_subscription_type.setText("You are under ");
                tv_plan_name.setText(subscription.getSubscriptionPlan().getPlanName().toUpperCase());

                if (!subscription.getChargeAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getChargeAt());
                    Date value = sdf.parse(date);
                    tv_subscribed_on.setText("Subscribed On : " + sdf1.format(value));
                }

                if (!subscription.getEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getEndsAt());
                    Date value = sdf.parse(date);
                    tv_ends_on.setText("Ends On : " + sdf1.format(value));
                    tv_ends_on.setVisibility(View.VISIBLE);
                } else {
                    tv_ends_on.setVisibility(View.GONE);
                }

                tv_next_due.setVisibility(View.GONE);
            } else if (mController.getSession().getSubscriptionStatus() && mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2C)) {
                layout_plan_details.setVisibility(View.VISIBLE);
                /* subscriptionPlan.getPlanName().toLowerCase() + " plan"*/
                tv_subscription_type.setText("You are under ");
                tv_amount.setText(getContext().getResources().getString(R.string.Rs) + " " + Constants.df.format(subscription.getSubscriptionPlan().getAmount()));
                tv_plan_name.setText(subscription.getSubscriptionPlan().getPlanName().toUpperCase());
                tv_billing_frequency.setText(Helper.toCamelCase(subscription.getSubscriptionPlan().getPeriod()));
                tv_plan_description.setText(subscription.getSubscriptionPlan().getPlanDescription());

                currentPlanName = subscription.getSubscriptionPlan().getPlanName().toUpperCase();

                if (!subscription.getTrialEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getTrialEndsAt());
                    Date value = sdf.parse(date);
                    tv_subscribed_on.setText("Subscribed On : " + sdf1.format(value));
                }

                if (!subscription.getEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getEndsAt());
                    Date value = sdf.parse(date);
                    tv_ends_on.setText("Ends On : " + sdf1.format(value));
                    tv_ends_on.setVisibility(View.VISIBLE);
                } else {
                    tv_ends_on.setVisibility(View.GONE);
                }

                if (!subscription.getChargeAt().isEmpty() && subscription.getEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getChargeAt());
                    Date value = sdf.parse(date);
                    tv_next_due.setText("Next Due : " + sdf1.format(value));

                    tv_next_due.setVisibility(View.VISIBLE);
                } else {
                    tv_next_due.setVisibility(View.GONE);
                }

                if (!pending_subscription.isEmpty()) {
                    layout_pending_subscription.setVisibility(View.VISIBLE);
                    tv_pending_subscription.setText("Your membership will be upgraded to the " +
                            pending_subscription.toUpperCase() + " Plan once the present plan ends.");
                } else {
                    layout_pending_subscription.setVisibility(View.GONE);
                }
            } else if (mController.getSession().getSubscriptionStatus() && mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_CORPORATE)) {
                layout_plan_details.setVisibility(View.VISIBLE);
                /* subscriptionPlan.getPlanName().toLowerCase() + " plan"*/
                tv_subscription_type.setText("You are under ");
                tv_amount.setText(getContext().getResources().getString(R.string.Rs) + " " + Constants.df.format(subscription.getSubscriptionPlan().getAmount()));
                tv_plan_name.setText(subscription.getSubscriptionPlan().getPlanName().toUpperCase());
                tv_billing_frequency.setText(Helper.toCamelCase(subscription.getSubscriptionPlan().getPeriod()));
                tv_plan_description.setText(subscription.getSubscriptionPlan().getPlanDescription());

                currentPlanName = subscription.getSubscriptionPlan().getPlanName().toUpperCase();

                if (!subscription.getEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getEndsAt());
                    Date value = sdf.parse(date);
                    tv_subscribed_on.setText("Subscribed On : " + sdf1.format(value));
                }

                if (!subscription.getEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getEndsAt());
                    Date value = sdf.parse(date);
                    tv_ends_on.setText("Ends On : " + sdf1.format(value));
                    tv_ends_on.setVisibility(View.VISIBLE);
                } else {
                    tv_ends_on.setVisibility(View.GONE);
                }

                if (!subscription.getChargeAt().isEmpty() && subscription.getEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getChargeAt());
                    Date value = sdf.parse(date);
                    tv_next_due.setText("Next Due : " + sdf1.format(value));

                    tv_next_due.setVisibility(View.VISIBLE);
                } else {
                    tv_next_due.setVisibility(View.GONE);
                }

                if (!pending_subscription.isEmpty()) {
                    layout_pending_subscription.setVisibility(View.VISIBLE);
                    tv_pending_subscription.setText("Your membership will be upgraded to the " +
                            pending_subscription.toUpperCase() + " Plan once the present plan ends.");
                } else {
                    layout_pending_subscription.setVisibility(View.GONE);
                }
            } else {
                layout_plan_details.setVisibility(View.GONE);
            }

            this.adapter_refresh();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mController.getSession().getSubscriptionStatus()){
                layout_coupon_code.setVisibility(View.GONE);
            }else{
                layout_coupon_code.setVisibility(View.VISIBLE);

            }

                    //removed consider coupon code functionality by doconline internal team
            /*if (mController.getSession().getUserType().equalsIgnoreCase(TYPE_B2B_PAID)) {
                layout_coupon_code.setVisibility(View.GONE);
            } else {
                layout_coupon_code.setVisibility(View.VISIBLE);
            }*/
        }
    }


    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(int requestCode, int responseCode, String response) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getView() == null) {
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

            if (requestCode == HTTP_REQUEST_CODE_GET_ONETIME_PLAN && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);

                    /*SubscriptionPlan sPlan = SubscriptionPlan.getOnetimePlanFromJSON(json.getString(KEY_DATA));

                    if(sPlan != null)
                    {
                        mPlans.add(0, sPlan);
                    }*/

                    mPlans.addAll(0, SubscriptionPlan.getOnetimePlansFromJSON(json.getString(KEY_DATA)));
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    this.adapter_refresh();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_GET_PLANS && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    mPlans.addAll(SubscriptionPlan.getPlanFromJSON(json.getString(KEY_DATA)));
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    this.adapter_refresh();
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_CANCEL_SUBSCRIPTION && responseCode == HttpClient.NO_RESPONSE) {
                btn_cancel.setVisibility(View.GONE);

                if (new InternetConnectionDetector(getContext()).isConnected()) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), SubscriptionFragment.this)
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
                        }
                    }, 3000);
                }

                return;
            }

            if (requestCode == HTTP_REQUEST_CODE_RETRY_PAYMENT && responseCode == HttpClient.NO_RESPONSE) {
                btn_cancel.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), SubscriptionFragment.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
                    }
                }, 1000);

                listener.onTaskCompleted(true, index, "");
                return;
            }
//working here
            if (requestCode == HTTP_REQUEST_CODE_APPLY_PROMO_CODE && responseCode == HttpClient.OK) {
                try {
                    JSONObject json = new JSONObject(response);
                    json = json.getJSONObject(KEY_DATA);

                    boolean hasPrice = json.isNull("has_price") ? false : json.getBoolean("has_price");
                    //boolean hasPrice = json.getBoolean("has_price");
                    if(hasPrice){
                        double price = Double.parseDouble(json.getString("price"));
                        boolean is_upgrade = false;
                        String orderId = json.getString("order_id");
                        String planName = "One Time";
                        SubscriptionPlan plan = new SubscriptionPlan(planName,orderId,price, coupon_code);
                        ((SubscriptionActivity)getActivity()).startPayment(plan, is_upgrade);
                    }else {
                        this.success_alert("Congratulation!", json.getString(KEY_MESSAGE));
                    }

                    /*if (new InternetConnectionDetector(getContext()).isConnected()) {
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                new HttpClient(HTTP_REQUEST_CODE_GET_PROFILE_STATE, MyApplication.HTTPMethod.GET.getValue(), SubscriptionFragment.this)
                                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getProfileStateURL());
                            }
                        }, 3000);
                    }*/
                    //this.success_alert("Congratulation!", json.getString(KEY_MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    edit_coupon_code.getText().clear();
                }

                return;
            }

            if (requestCode != HTTP_REQUEST_CODE_APPLY_PROMO_CODE && responseCode == HttpClient.UNPROCESSABLE_ENTITY) {
                try {
                    JSONObject json = new JSONObject(response);
                    this.showDialog("Info", json.getString(KEY_MESSAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            new HttpResponseHandler(getContext(), this, getView()).handle(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (requestCode != HTTP_REQUEST_CODE_CANCEL_SUBSCRIPTION
                    && requestCode != HTTP_REQUEST_CODE_RETRY_PAYMENT) {
                listener.onTaskCompleted(false, -1, "");
            }
        }
    }


    @Override
    public void onPositiveAction() {

    }


    @Override
    public void onPositiveAction(int requestCode) {
        if (requestCode == DIALOG_REQUEST_CODE_CANCEL_SUBSCRIPTION) {
            if (new InternetConnectionDetector(getContext()).isConnected()) {
                listener.onTaskCompleted(true, -2, "");
                this.is_cancel_membership = true;

                new HttpClient(HTTP_REQUEST_CODE_CANCEL_SUBSCRIPTION, MyApplication.HTTPMethod.POST.getValue(), SubscriptionFragment.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSubscriptionURL() + "cancel/current");
            } else {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }

        if (requestCode == DIALOG_REQUEST_CODE_RETRY_PAYMENT) {
            if (new InternetConnectionDetector(getContext()).isConnected()) {
                listener.onTaskCompleted(true, -2, "");

                new HttpClient(HTTP_REQUEST_CODE_RETRY_PAYMENT, MyApplication.HTTPMethod.POST.getValue(), SubscriptionFragment.this)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mController.getSubscriptionURL() + "cancel/current");
            } else {
                new CustomAlertDialog(getContext(), this, getView()).snackbarForInternetConnectivity();
            }
        }
    }


    @Override
    public void onNegativeAction() {

    }


    private void success_alert(String title, String content) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            /*new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(title)
                    .setContentText(content)
                    .show();*/
            new CustomAlertDialog(getContext(), this)
                    .showAlertDialogWithPositiveAction(title, content,
                            getResources().getString(R.string.OK), true);
        }
    }


    private void showDialog(String title, String content) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity().isFinishing()) {
            return;
        }

        if (getContext() == null) {
            return;
        }

        if (getView() == null) {
            return;
        }

        new CustomAlertDialog(getContext(), this).
                showAlertDialogWithPositiveAction(title, content, getContext().getResources().getString(R.string.OK), true);
    }

    @Override
    public void onTaskCompleted(boolean flag, int index, String action) {
        this.index = index;

        if (action.equalsIgnoreCase("retry")) {
            new CustomAlertDialog(getContext(), DIALOG_REQUEST_CODE_RETRY_PAYMENT, this).
                    showDialogWithAction("Note", "Your Current subscription plan will be canceled. However, on renewing your membership you can resume access to our services.", getResources().getString(R.string.RetryPayment),
                            getResources().getString(R.string.Cancel), true);

            return;
        }

        listener.onTaskCompleted(flag, index, action);
    }


    private void json_data_profile(String data) {
        Subscription subscription = new Subscription();
        String pending_subscription = "";

        try {
            User user = User.getUserProfileFromJSON(data);

            pending_subscription = user.getUserAccount().getSubscription().getPendingSubscription();
            subscription = user.getUserAccount().getSubscription();
            if (user.getUserAccount().getSubscription().getSubscriptionType().equals(TYPE_B2C)
                    || user.getUserAccount().getSubscription().getSubscriptionType().equals(TYPE_FAMILY)) {
                this.active_plan_id = user.getUserAccount().getSubscription().getSubscriptionPlan().getPlanId();
            }

            SubscriptionActivity.user_id = user.getUserId();
            SubscriptionActivity.address = user.getAddress().getFullAddress();

            if (this.is_cancel_membership) {
                String title = "Your Membership Plan has been cancelled";
                String message = "You will no longer be billed for " + subscription.getSubscriptionPlan().getPlanName()
                        + ". However, you will continue to have access to our services until the end of your current billing cycle : ";

                if (!subscription.getEndsAt().isEmpty()) {
                    String date = Helper.UTC_to_Local_TimeZone(subscription.getEndsAt());
                    Date value = sdf.parse(date);
                    message += sdf1.format(value);
                }

                new CustomAlertDialog(getContext(), this).
                        showAlertDialogWithPositiveAction(title, message, getResources().getString(R.string.OK), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.display_subscribed_plan(subscription, pending_subscription);
            this.is_cancel_membership = false;
        }
    }
}