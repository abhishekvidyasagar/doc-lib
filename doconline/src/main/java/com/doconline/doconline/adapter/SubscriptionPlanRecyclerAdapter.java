package com.doconline.doconline.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.ImageLoader;
import com.doconline.doconline.helper.OnTaskCompleted;
import com.doconline.doconline.model.SubscriptionPlan;

import java.text.DecimalFormat;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_FAMILY_TYPE;
import static com.doconline.doconline.app.Constants.KEY_ONETIME_TYPE;
import static com.doconline.doconline.app.Constants.KEY_RECURRING_TYPE;
import static com.doconline.doconline.app.Constants.PAYMENT_HALTED;
import static com.doconline.doconline.app.Constants.TYPE_B2B_PAID;
import static com.doconline.doconline.app.Constants.TYPE_CORPORATE;


public class SubscriptionPlanRecyclerAdapter extends RecyclerView.Adapter<SubscriptionPlanRecyclerAdapter.SubscriptionViewHolder> {
    private Context context;
    private OnTaskCompleted listener;
    private OnItemClickListener clickListener;
    private List<SubscriptionPlan> sPlans;
    private MyApplication mController;
    private int lastPosition = -1;

    public SubscriptionPlanRecyclerAdapter(Context context, OnTaskCompleted listener, List<SubscriptionPlan> sPlans) {
        this.context = context;
        this.listener = listener;
        this.mController = MyApplication.getInstance();
        this.sPlans = sPlans;
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_subscription_plan, viewGroup, false);
        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder vHolder, int i) {
        vHolder.bindData(sPlans.get(i));
        vHolder.button_subscribe.setTag(i);
        vHolder.button_upgrade.setTag(i);
        vHolder.plan_status.setTag(i);
        vHolder.plan_logo.setTag(i);

        setAnimation(vHolder.card_main, i);
    }


    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        /**
         * If the bound view wasn't previously displayed on screen, it's animated
         */
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return sPlans == null ? 0 : sPlans.size();
    }

    class SubscriptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        CardView card_main;

        TextView plan_name;

        ImageView plan_logo;

        TextView plan_status;

        TextView billing_frequency;

        TextView price;

        TextView cross_price;

        LinearLayout packages_layout;

        Button button_subscribe;

        Button button_upgrade;

        ImageView limited_offer;

        SubscriptionViewHolder(View itemView) {
            super(itemView);
           // ButterKnife.bind(this, itemView);

             card_main = itemView.findViewById(R.id.card_item);
             plan_name= itemView.findViewById(R.id.plan_name);
             plan_logo= itemView.findViewById(R.id.plan_logo);
             plan_status= itemView.findViewById(R.id.plan_status);
             billing_frequency= itemView.findViewById(R.id.billing_frequency);
             price= itemView.findViewById(R.id.price);
             cross_price= itemView.findViewById(R.id.cross_price);
            packages_layout= itemView.findViewById(R.id.packages_layout);
             button_subscribe= itemView.findViewById(R.id.btn_subscribe_now);
             button_upgrade= itemView.findViewById(R.id.btn_upgrade_plan);
            limited_offer= itemView.findViewById(R.id.limited_offer);

            itemView.setOnClickListener(this);

            button_subscribe.setOnClickListener(onButtonClickListener);
            button_upgrade.setOnClickListener(onButtonClickListener);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();

                int id = v.getId();
                if (id == R.id.btn_subscribe_now) {
                    String amt = String.valueOf(sPlans.get(index).getAmount());

                    //if (!BuildConfig.DEBUG)
                    //{


                   /*     Map<String, Object> eventValue = new HashMap<>();

                        eventValue.put(AFInAppEventParameterName.PRICE, amt);
                        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, "category_a");
                        eventValue.put(AFInAppEventParameterName.CONTENT_ID, "1234567");
                        eventValue.put(AFInAppEventParameterName.CURRENCY, "INR");

                        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.INITIATED_CHECKOUT, eventValue);*/

                    //for adgyde
                      /*  HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Purchase_Initiated", amt);//patrametre name,value
                        PAgent.onEvent("Purchase_Initiated", params);//eventid
                        PAgent.flush();*/

                    //}

                    // If payment is halted then retry . in else, all plans will be executed.
                    if (!mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_ONETIME_TYPE)
                            && mController.getSession().getSubscriptionPaymentStatus().equalsIgnoreCase(PAYMENT_HALTED)) {
                        listener.onTaskCompleted(true, index, "retry");
                    } else {
                        listener.onTaskCompleted(true, index, "");
                    }
                } else if (id == R.id.btn_upgrade_plan) {// for upgrading the plan
                    listener.onTaskCompleted(true, index, "upgrade");

                    String amtt = String.valueOf(sPlans.get(index).getAmount());
                    //for adgyde
                    //if (!BuildConfig.DEBUG) {
                    /*    Map<String, Object> eventValuee = new HashMap<>();
                        eventValuee.put(AFInAppEventParameterName.PRICE, amtt);
                        eventValuee.put(AFInAppEventParameterName.CONTENT_TYPE, "category_a");
                        eventValuee.put(AFInAppEventParameterName.CONTENT_ID, "1234567");
                        eventValuee.put(AFInAppEventParameterName.CURRENCY, "INR");

                        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.INITIATED_CHECKOUT, eventValuee);*/


                     /*   HashMap<String, String> paramss = new HashMap<String, String>();
                        paramss.put("Purchase_Initiated", amtt);//patrametre name,value
                        PAgent.onEvent("Purchase_Initiated", paramss);//eventid
                        PAgent.flush();*/
                    //}
                }
            }
        };

        private void bindData(SubscriptionPlan plan) {
            int active_plan_internal_order = SubscriptionPlan.getActivePlanInternalOrder(sPlans);

            plan_name.setText(plan.getDisplayName().toUpperCase());

            if (!plan.getPeriod().isEmpty()) {
                billing_frequency.setText("/" + plan.getPeriod());
            } else {
                billing_frequency.setText("");
            }

            price.setText(new DecimalFormat("0").format(plan.getAmount()));

            if (packages_layout.getChildCount() > 0) {
                packages_layout.removeAllViews();
            }
            // textview in plans
            for (String packages : plan.getPackages()) {
                TextView text = (TextView) LayoutInflater.from(context).inflate(R.layout.subscription_package_textview_layout, null);
                text.setText(packages);
                packages_layout.addView(text);
            }
            //  If user already has a plan then button is gone and active state is shown
            if (mController.getSession().getSubscriptionStatus()) {
                button_subscribe.setVisibility(View.GONE);
                plan_status.setText("ACTIVE");
                plan_status.setTextColor(ContextCompat.getColor(context, R.color.light_green));
            } else {
                button_subscribe.setVisibility(View.VISIBLE);
                plan_status.setVisibility(View.GONE);
                button_subscribe.setText(R.string.Choose);
            }

            /*if(mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_ONETIME_TYPE) && !plan.getType().equalsIgnoreCase(KEY_ONETIME_TYPE))
            {
                button_subscribe.setVisibility(View.VISIBLE);
            }*/
            //  price without discount, strikethru
            if (plan.getType().equalsIgnoreCase(KEY_ONETIME_TYPE) && plan.isFeatured()) {
                String price = context.getResources().getString(R.string.Rs) + new DecimalFormat("0").format(plan.getCrossPrice());

                cross_price.setVisibility(View.VISIBLE);
                cross_price.setText(price);
                cross_price.setPaintFlags(cross_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                limited_offer.setVisibility(View.VISIBLE);
            } else {
                cross_price.setVisibility(View.GONE);
                limited_offer.setVisibility(View.GONE);
            }

            if (plan.isActive() || (plan.getPlanId().isEmpty()
                    && (mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_ONETIME_TYPE)
                    || mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_FAMILY_TYPE)
                    || mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B_PAID)
                    || mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_CORPORATE)))) {
                if (mController.getSession().getSubscriptionStatus()) {
                    plan_status.setVisibility(View.VISIBLE);

                    if (!(mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_ONETIME_TYPE)
                            || mController.getSession().getSubscriptionType().equalsIgnoreCase(KEY_FAMILY_TYPE)) &&
                            mController.getSession().getSubscriptionPaymentStatus().equalsIgnoreCase(PAYMENT_HALTED)) {
                        plan_status.setText(R.string.text_payment_halted);
                        plan_status.setTextColor(ContextCompat.getColor(context, R.color.light_red));
                        button_subscribe.setVisibility(View.VISIBLE);
                        button_subscribe.setText(R.string.RetryPayment);
                    } else {
                        plan_status.setText("ACTIVE");
                        plan_status.setTextColor(ContextCompat.getColor(context, R.color.light_green));
                        button_subscribe.setVisibility(View.GONE);
                        button_subscribe.setText(R.string.Choose);

                        if (plan.getPlanType().equalsIgnoreCase(mController.getSession().getSubscriptionType())
                                || (plan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE)
                                || (plan.getPlanType().equalsIgnoreCase(KEY_ONETIME_TYPE) && mController.getSession().getSubscriptionType().equalsIgnoreCase(TYPE_B2B_PAID)))) {
                            plan_status.setVisibility(View.VISIBLE);
                        } else {
                            plan_status.setVisibility(View.GONE);
                        }
                    }
                } else {
                    plan_status.setVisibility(View.GONE);
                }
            } else {
                plan_status.setVisibility(View.GONE);
            }

            if (mController.getSession().getSubscriptionStatus() && mController.getSession().getSubscriptionUpgradeStatus() && !plan.isActive() &&
                    active_plan_internal_order != -1 && plan.getInternalOrder() > active_plan_internal_order) {
                button_upgrade.setVisibility(View.VISIBLE);
            } else {
                button_upgrade.setVisibility(View.GONE);
            }

            this.load_icon(mController.getSession().getBaseURL() + plan.getPlanIcon());
        }

        private void load_icon(final String icon_url) {
            try {
                if (!icon_url.isEmpty()) {
                    ImageLoader.loadThumbnail(context, icon_url, plan_logo, R.drawable.quater_icon, 80, 80);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}