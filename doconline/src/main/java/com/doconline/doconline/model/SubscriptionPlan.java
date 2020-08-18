package com.doconline.doconline.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_CROSS_PRICE;
import static com.doconline.doconline.app.Constants.KEY_CURRENCY;
import static com.doconline.doconline.app.Constants.KEY_DESCRIPTION;
import static com.doconline.doconline.app.Constants.KEY_DISCOUNT_TEXT;
import static com.doconline.doconline.app.Constants.KEY_DISPLAY_COLOR;
import static com.doconline.doconline.app.Constants.KEY_DISPLAY_NAME;
import static com.doconline.doconline.app.Constants.KEY_DISPLAY_PERIOD;
import static com.doconline.doconline.app.Constants.KEY_DISPLAY_PRICE;
import static com.doconline.doconline.app.Constants.KEY_EXTRA;
import static com.doconline.doconline.app.Constants.KEY_FEATURED;
import static com.doconline.doconline.app.Constants.KEY_ICON_IMAGE;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_INTERNAL_ORDER;
import static com.doconline.doconline.app.Constants.KEY_INTERVAL;
import static com.doconline.doconline.app.Constants.KEY_ITEM;
import static com.doconline.doconline.app.Constants.KEY_NAME;
import static com.doconline.doconline.app.Constants.KEY_PACKAGES;
import static com.doconline.doconline.app.Constants.KEY_PLAN_TYPE;
import static com.doconline.doconline.app.Constants.KEY_TYPE;
import static com.doconline.doconline.app.Constants.KEY_UNIT_AMOUNT;

/**
 * Created by chiranjitbardhan on 23/11/17.
 */

public class SubscriptionPlan implements Serializable
{
    private String plan_id = "";
    private String subscription_id = "";
    private String order_id = "";
    private String plan_name = "";
    private String display_name = "";
    private String display_color = "";
    private String plan_description = "";
    private String type = "";
    private String plan_type = "";
    private String period = "";
    private String currency = "";
    private String discount_text = "";
    private String created_at = "";
    private String plan_icon;
    private int quantity;
    private double amount;
    private double unit_amount;
    private double gross_amount;
    private double cross_price;
    private int interval;
    private int internal_order;
    private boolean is_active = false;
    private boolean featured;
    private String payment_status = "";
    String couponCode = "";

    private List<String> packages = new ArrayList<>();

    SubscriptionPlan()
    {

    }

    SubscriptionPlan(String plan_name, String plan_description, String type, double amount,
                            double unit_amount, double gross_amount, int quantity, String currency)
    {
        this.plan_name = plan_name;
        this.plan_description = plan_description;
        this.type = type;
        this.amount = amount;
        this.unit_amount = unit_amount;
        this.gross_amount = gross_amount;
        this.quantity = quantity;
        this.currency = currency;
    }

    SubscriptionPlan(String plan_name, String plan_description, String display_name, List<String> packages, double amount, double cross_price, String discount_text,
                            String period, boolean featured, String display_color, int internal_order, String plan_icon, String type, String plan_type)
    {
        this.plan_name = plan_name;
        this.plan_description = plan_description;
        this.display_name = display_name;
        this.packages = packages;
        this.amount = amount;
        this.cross_price = cross_price;
        this.discount_text = discount_text;
        this.period = period;
        this.featured = featured;
        this.display_color = display_color;
        this.internal_order = internal_order;
        this.plan_icon = plan_icon;
        this.type = type;
        this.plan_type = plan_type;
    }


    SubscriptionPlan(String plan_id, String plan_name, String display_name, String plan_description, List<String> packages,
                     double amount, double unit_amount, double cross_price, String currency, String discount_text,
                     String period, int interval, boolean featured, String display_color, int internal_order, String plan_icon, String type)
    {
        this.type = type;
        this.plan_id = plan_id;
        this.plan_name = plan_name;
        this.display_name = display_name;
        this.plan_description = plan_description;
        this.packages = packages;
        this.amount = amount;
        this.unit_amount = unit_amount;
        this.cross_price = cross_price;
        this.currency = currency;
        this.discount_text = discount_text;
        this.period = period;
        this.interval = interval;
        this.featured = featured;
        this.display_color = display_color;
        this.internal_order = internal_order;
        this.plan_icon = plan_icon;
    }


    public SubscriptionPlan(String planName, String orderId, double price, String couponCode) {
        this.plan_name = planName;
        this.order_id = orderId;
        this.amount = price;
        this.couponCode = couponCode;
    }

    public String getAppliedCouponCode(){
        return couponCode;
    }

    public void setAppliedCouponCode(String appliedCouponCode){
        this.couponCode = appliedCouponCode;
    }
    public String getPlanId() {
        return plan_id;
    }

    public void setPlanId(String plan_id) {
        this.plan_id = plan_id;
    }


    public String getSubscriptionId() {
        return subscription_id;
    }

    public void setSubscriptionId(String subscription_id) {
        this.subscription_id = subscription_id;
    }


    public String getPlanName() {
        return plan_name;
    }

    public void setPlanName(String plan_name) {
        this.plan_name = plan_name;
    }


    public String getPlanDescription() {
        return plan_description;
    }

    public void setPlanDescription(String plan_description) {
        this.plan_description = plan_description;
    }


    public double getAmount() {
        return (amount/100);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getUnitAmount() {
        return (unit_amount/100);
    }

    public void setUnitAmount(double unit_amount) {
        this.unit_amount = unit_amount;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public List<String> getPackages()
    {
        return packages;
    }

    public void setPackages(List<String> packages)
    {
        this.packages = packages;
    }

    public String getDiscountText() {
        return discount_text;
    }

    public void setDiscountText(String discount_text) {
        this.discount_text = discount_text;
    }

    public double getCrossPrice() {
        return (cross_price/100);
    }

    public void setCrossPrice(double cross_price) {
        this.cross_price = cross_price;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public double getGrossAmount() {
        return (gross_amount/100);
    }

    public void setGrossAmount(double gross_amount) {
        this.gross_amount = gross_amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return this.is_active;
    }

    public void setIsActive(boolean is_active) {
        this.is_active = is_active;
    }

    public boolean isFeatured() {
        return this.featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getDisplayName() {
        return this.display_name;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplayColor() {
        return this. display_color;
    }

    public void setDisplayColor(String display_color) {
        this.display_color = display_color;
    }

    public int getInternalOrder() {
        return this.internal_order;
    }

    public void setInternalOrder(int internal_order) {
        this.internal_order = internal_order;
    }

    public String getPaymentStatus()
    {
        return this.payment_status;
    }

    public void setPaymentStatus(String payment_status)
    {
        this.payment_status = payment_status;
    }

    public static int getActivePlanInternalOrder(List<SubscriptionPlan> plans)
    {
        for(SubscriptionPlan plan: plans)
        {
            if(plan.isActive())
            {
                return plan.getInternalOrder();
            }
        }

        return -1;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }


    public static List<SubscriptionPlan> getPlanFromJSON(String json_data)
    {
        List<SubscriptionPlan> planList = new ArrayList<>();

        try
        {
            JSONArray array = new JSONArray(json_data);

            for(int i=0; i< array.length(); i++)
            {
                JSONObject json = array.getJSONObject(i);

                String plan_id = json.getString(KEY_ID);
                int interval = (json.isNull(KEY_INTERVAL)) ? 1 : json.getInt(KEY_INTERVAL);

                JSONObject json_item = json.getJSONObject(KEY_ITEM);

                String plan_name = json_item.getString(KEY_NAME);
                String plan_description = (json_item.isNull(KEY_DESCRIPTION)) ? "" : json_item.getString(KEY_DESCRIPTION);
                String currency = (json_item.isNull(KEY_CURRENCY)) ? "NIL" : json_item.getString(KEY_CURRENCY);
                double unit_amount = json_item.getDouble(KEY_UNIT_AMOUNT);

                json_item = json.getJSONObject(KEY_EXTRA);

                String discount_text = "";
                double cross_price = 0;

                boolean featured = !json_item.isNull(KEY_FEATURED) && json_item.getBoolean(KEY_FEATURED);
                String display_name = (json_item.isNull(KEY_DISPLAY_NAME)) ? "N/A" : json_item.getString(KEY_DISPLAY_NAME);
                double display_price = json_item.getDouble(KEY_DISPLAY_PRICE);
                String display_period = (json_item.isNull(KEY_DISPLAY_PERIOD)) ? "NIL" : json_item.getString(KEY_DISPLAY_PERIOD);
                String display_color = (json_item.isNull(KEY_DISPLAY_COLOR)) ? "#5db761" : json_item.getString(KEY_DISPLAY_COLOR);
                int internal_order = (json_item.isNull(KEY_INTERNAL_ORDER)) ? 0 : json_item.getInt(KEY_INTERNAL_ORDER);
                String plan_icon = (json_item.isNull(KEY_ICON_IMAGE)) ? "" : json_item.getString(KEY_ICON_IMAGE);
                String type = (json_item.isNull(KEY_TYPE)) ? "" : json_item.getString(KEY_TYPE);

                if(json_item.has(KEY_DISCOUNT_TEXT))
                {
                    discount_text = (json_item.isNull(KEY_DISCOUNT_TEXT)) ? "" : json_item.getString(KEY_DISCOUNT_TEXT);
                }

                if(json_item.has(KEY_CROSS_PRICE))
                {
                    cross_price = (json_item.isNull(KEY_CROSS_PRICE)) ? 0 : json_item.getDouble(KEY_CROSS_PRICE);
                }

                List<String> packages = new ArrayList<>();

                if(json_item.has(KEY_PACKAGES) && !json_item.isNull(KEY_PACKAGES))
                {
                    Object json_packages = json_item.get(KEY_PACKAGES);

                    if(json_packages instanceof JSONArray)
                    {
                        for(int j=0; j< ((JSONArray)json_packages).length(); j++)
                        {
                            packages.add(((JSONArray)json_packages).getString(j));
                        }
                    }
                }

                planList.add(new SubscriptionPlan(plan_id, plan_name, display_name, plan_description, packages, /*amount*/ display_price, unit_amount, cross_price, currency, discount_text, /*period*/ display_period, interval, featured, display_color, internal_order, plan_icon, type));
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return planList;
    }


    /*public static SubscriptionPlan getOnetimePlanFromJSON(String json_data)
    {
        SubscriptionPlan planObj = null;

        try
        {
            JSONObject json = new JSONObject(json_data);

            String plan_name = json.getString(KEY_NAME);
            String plan_description = (json.isNull(KEY_DESCRIPTION)) ? "" : json.getString(KEY_DESCRIPTION);

            JSONObject json_item = json.getJSONObject(KEY_EXTRA);

            String discount_text = "";
            double cross_price = 0;

            boolean featured = !json_item.isNull(KEY_FEATURED) && json_item.getBoolean(KEY_FEATURED);
            String display_name = (json_item.isNull(KEY_DISPLAY_NAME)) ? "N/A" : json_item.getString(KEY_DISPLAY_NAME);
            double display_price = json_item.getDouble(KEY_DISPLAY_PRICE);
            String display_period = (json_item.isNull(KEY_DISPLAY_PERIOD)) ? "NIL" : json_item.getString(KEY_DISPLAY_PERIOD);
            String display_color = (json_item.isNull(KEY_DISPLAY_COLOR)) ? "#5db761" : json_item.getString(KEY_DISPLAY_COLOR);
            int internal_order = (json_item.isNull(KEY_INTERNAL_ORDER)) ? 0 : json_item.getInt(KEY_INTERNAL_ORDER);
            String plan_icon = (json_item.isNull(KEY_ICON_IMAGE)) ? "" : json_item.getString(KEY_ICON_IMAGE);
            String type = (json_item.isNull(KEY_TYPE)) ? "" : json_item.getString(KEY_TYPE);

            if(json_item.has(KEY_DISCOUNT_TEXT))
            {
                discount_text = (json_item.isNull(KEY_DISCOUNT_TEXT)) ? "" : json_item.getString(KEY_DISCOUNT_TEXT);
            }

            if(json_item.has(KEY_CROSS_PRICE))
            {
                cross_price = (json_item.isNull(KEY_CROSS_PRICE)) ? 0 : json_item.getDouble(KEY_CROSS_PRICE);
            }

            List<String> packages = new ArrayList<>();

            if(json_item.has(KEY_PACKAGES) && !json_item.isNull(KEY_PACKAGES))
            {
                Object json_packages = json_item.get(KEY_PACKAGES);

                if(json_packages instanceof JSONArray)
                {
                    for(int j=0; j< ((JSONArray)json_packages).length(); j++)
                    {
                        packages.add(((JSONArray)json_packages).getString(j));
                    }
                }
            }

            planObj = new SubscriptionPlan(plan_name, plan_description, display_name, packages, display_price, cross_price, discount_text,
                display_period, featured, display_color, internal_order, plan_icon, type);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return planObj;
    }*/



    public static List<SubscriptionPlan> getOnetimePlansFromJSON(String json_data)
    {
        List<SubscriptionPlan> planList = new ArrayList<>();

        try
        {
            JSONArray array = new JSONArray(json_data);

            for(int i=0; i< array.length(); i++)
            {
                JSONObject json = array.getJSONObject(i);

                String plan_name = json.getString(KEY_NAME);
                String plan_description = (json.isNull(KEY_DESCRIPTION)) ? "" : json.getString(KEY_DESCRIPTION);

                JSONObject json_item = json.getJSONObject(KEY_EXTRA);

                String discount_text = "";
                double cross_price = 0;

                boolean featured = !json_item.isNull(KEY_FEATURED) && json_item.getBoolean(KEY_FEATURED);
                String display_name = (json_item.isNull(KEY_DISPLAY_NAME)) ? "N/A" : json_item.getString(KEY_DISPLAY_NAME);
                double display_price = json_item.getDouble(KEY_DISPLAY_PRICE);
                String display_period = (json_item.isNull(KEY_DISPLAY_PERIOD)) ? "NIL" : json_item.getString(KEY_DISPLAY_PERIOD);
                String display_color = (json_item.isNull(KEY_DISPLAY_COLOR)) ? "#5db761" : json_item.getString(KEY_DISPLAY_COLOR);
                int internal_order = (json_item.isNull(KEY_INTERNAL_ORDER)) ? 0 : json_item.getInt(KEY_INTERNAL_ORDER);
                String plan_icon = (json_item.isNull(KEY_ICON_IMAGE)) ? "" : json_item.getString(KEY_ICON_IMAGE);
                String type = (json_item.isNull(KEY_TYPE)) ? "" : json_item.getString(KEY_TYPE);
                String plan_type = (json_item.isNull(KEY_PLAN_TYPE)) ? "" : json_item.getString(KEY_PLAN_TYPE);

                if(json_item.has(KEY_DISCOUNT_TEXT))
                {
                    discount_text = (json_item.isNull(KEY_DISCOUNT_TEXT)) ? "" : json_item.getString(KEY_DISCOUNT_TEXT);
                }

                if(json_item.has(KEY_CROSS_PRICE))
                {
                    cross_price = (json_item.isNull(KEY_CROSS_PRICE)) ? 0 : json_item.getDouble(KEY_CROSS_PRICE);
                }

                List<String> packages = new ArrayList<>();

                if(json_item.has(KEY_PACKAGES) && !json_item.isNull(KEY_PACKAGES))
                {
                    Object json_packages = json_item.get(KEY_PACKAGES);

                    if(json_packages instanceof JSONArray)
                    {
                        for(int j=0; j< ((JSONArray)json_packages).length(); j++)
                        {
                            packages.add(((JSONArray)json_packages).getString(j));
                        }
                    }
                }

                SubscriptionPlan planObj = new SubscriptionPlan(plan_name, plan_description, display_name, packages, display_price, cross_price, discount_text,
                        display_period, featured, display_color, internal_order, plan_icon, type, plan_type);

                planList.add(planObj);
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return planList;
    }


    public String getPlanIcon()
    {
        return this.plan_icon;
    }

    public void setPlanIcon(String plan_icon)
    {
        this.plan_icon = plan_icon;
    }

    public String getOrderId()
    {
        return order_id;
    }

    public void setOrderId(String order_id)
    {
        this.order_id = order_id;
    }

    public String getPlanType()
    {
        return this.plan_type;
    }

    public void setPlanType(String plan_type)
    {
        this.plan_type = plan_type;
    }
}