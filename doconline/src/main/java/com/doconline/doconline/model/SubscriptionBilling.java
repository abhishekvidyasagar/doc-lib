package com.doconline.doconline.model;

import com.doconline.doconline.app.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_AMOUNT_DUE;
import static com.doconline.doconline.app.Constants.KEY_AMOUNT_PAID;
import static com.doconline.doconline.app.Constants.KEY_BILLING_END_AT;
import static com.doconline.doconline.app.Constants.KEY_BILLING_START_AT;
import static com.doconline.doconline.app.Constants.KEY_CANCELLED_AT;
import static com.doconline.doconline.app.Constants.KEY_CURRENCY;
import static com.doconline.doconline.app.Constants.KEY_CUSTOMER_DETAILS;
import static com.doconline.doconline.app.Constants.KEY_CUSTOMER_ID;
import static com.doconline.doconline.app.Constants.KEY_DESCRIPTION;
import static com.doconline.doconline.app.Constants.KEY_DISCOUNT;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_EXPIRED_AT;
import static com.doconline.doconline.app.Constants.KEY_GROSS_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_ID;
import static com.doconline.doconline.app.Constants.KEY_ISSUED_AT;
import static com.doconline.doconline.app.Constants.KEY_LINE_ITEMS;
import static com.doconline.doconline.app.Constants.KEY_NAME;
import static com.doconline.doconline.app.Constants.KEY_NOTES;
import static com.doconline.doconline.app.Constants.KEY_ORDER_ID;
import static com.doconline.doconline.app.Constants.KEY_PAID_AT;
import static com.doconline.doconline.app.Constants.KEY_PAYMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_PLAN_AMOUNT;
import static com.doconline.doconline.app.Constants.KEY_QUANTITY;
import static com.doconline.doconline.app.Constants.KEY_STATUS;
import static com.doconline.doconline.app.Constants.KEY_SUBSCRIPTION_ID;
import static com.doconline.doconline.app.Constants.KEY_TYPE;
import static com.doconline.doconline.app.Constants.KEY_UNIT_AMOUNT;

/**
 * Created by chiranjitbardhan on 06/12/17.
 */

public class SubscriptionBilling implements Serializable{

    private List<SubscriptionPlan> itemList = new ArrayList<>();

    private String invoice_id = "";
    private String customer_id = "";
    private String order_id = "";
    private String subscription_id = "";
    private String payment_id = "";
    private String status = "";
    private String issued_at = "";
    private String paid_at = "";
    private String cancelled_at = "";
    private String expired_at = "";
    private String billing_start_at = "";
    private String billing_end_at = "";
    private double amount;
    private double amount_paid;
    private double amount_due;
    private double discount;
    private String currency = "";
    private String customer_name = "";
    private String customer_email = "";


    public SubscriptionBilling() {

    }

    public SubscriptionBilling(String invoice_id, String customer_id, String order_id, String payment_id,
                               String subscription_id, String status, String issued_at, String paid_at, String cancelled_at,
                               String expired_at, String billing_start_at, String billing_end_at, double amount, double discount, double amount_paid, double amount_due, String currency,
                               List<SubscriptionPlan> itemList, String customer_name, String customer_email) {
        this.invoice_id = invoice_id;
        this.customer_id = customer_id;
        this.order_id = order_id;
        this.payment_id = payment_id;
        this.subscription_id = subscription_id;
        this.status = status;
        this.issued_at = issued_at;
        this.paid_at = paid_at;
        this.cancelled_at = cancelled_at;
        this.expired_at = expired_at;
        this.billing_start_at = billing_start_at;
        this.billing_end_at = billing_end_at;
        this.amount = amount;
        this.discount = discount;
        this.amount_paid = amount_paid;
        this.amount_due = amount_due;
        this.currency = currency;
        this.itemList = itemList;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
    }

    public List<SubscriptionPlan> getItemList() {
        return this.itemList;
    }

    public void setItemList(List<SubscriptionPlan> itemList) {
        this.itemList = itemList;
    }

    public String getInvoiceId() {
        return invoice_id;
    }

    public void setInvoiceId(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getCustomerId() {
        return customer_id;
    }

    public void setCustomerId(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }

    public String getPaymentId() {
        return payment_id;
    }

    public void setPaymentId(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssuedAt() {
        return issued_at;
    }

    public void setIssuedAt(String issued_at) {
        this.issued_at = issued_at;
    }

    public String getPaidAt() {
        return paid_at;
    }

    public void setPaidAt(String paid_at) {
        this.paid_at = paid_at;
    }

    public String getCancelledAt() {
        return cancelled_at;
    }

    public void setCancelledAt(String cancelled_at) {
        this.cancelled_at = cancelled_at;
    }

    public String getExpiredAt() {
        return expired_at;
    }

    public void setExpiredAt(String expired_at) {
        this.expired_at = expired_at;
    }

    public double getAmount() {
        return (amount / 100);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountPaid() {
        return (amount_paid / 100);
    }

    public void setAmountPaid(double amount_paid) {
        this.amount_paid = amount_paid;
    }

    public double getAmountDue() {
        return (amount_due / 100);
    }

    public void setAmountDue(double amount_due) {
        this.amount_due = amount_due;
    }


    public double getDiscount()
    {
        return (this.discount / 100);
    }

    public void setDiscount(double discount)
    {
        this.discount = discount;
    }

    public String getSubscriptionId() {
        return subscription_id;
    }

    public void setSubscriptionId(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBillingStartAt() {
        return billing_start_at;
    }

    public void setBillingStartAt(String billing_start_at) {
        this.billing_start_at = billing_start_at;
    }

    public String getBillingEndAt() {
        return billing_end_at;
    }

    public void setBillingEndAt(String billing_end_at) {
        this.billing_end_at = billing_end_at;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomerEmail() {
        return customer_email;
    }

    public void setCustomerEmail(String customer_email) {
        this.customer_email = customer_email;
    }


    public static List<SubscriptionBilling> getSubscriptionBillingListFromJSON(String json_data)
    {
        List<SubscriptionBilling> billingList = new ArrayList<>();

        try
        {
            JSONArray data_array = new JSONArray(json_data);

            for (int i = 0; i < data_array.length(); i++)
            {
                JSONObject json = data_array.getJSONObject(i);

                String invoice_id = (json.isNull(Constants.KEY_ID)) ? "" : json.getString(KEY_ID);
                String order_id = (json.isNull(Constants.KEY_ORDER_ID)) ? "" : json.getString(KEY_ORDER_ID);
                String subscription_id = (json.isNull(Constants.KEY_SUBSCRIPTION_ID)) ? "" : json.getString(KEY_SUBSCRIPTION_ID);
                String payment_id = (json.isNull(Constants.KEY_PAYMENT_ID)) ? "" : json.getString(KEY_PAYMENT_ID);
                String customer_id = (json.isNull(KEY_CUSTOMER_ID)) ? "" : json.getString(KEY_CUSTOMER_ID);

                String currency = (json.isNull(Constants.KEY_CURRENCY)) ? "" : json.getString(KEY_CURRENCY);
                double amount = (json.isNull(Constants.KEY_AMOUNT)) ? 0 : json.getDouble(KEY_AMOUNT);
                double amount_paid = (json.isNull(Constants.KEY_AMOUNT_PAID)) ? 0 : json.getDouble(KEY_AMOUNT_PAID);
                double amount_due = (json.isNull(Constants.KEY_AMOUNT_DUE)) ? 0 : json.getDouble(KEY_AMOUNT_DUE);

                double discount = 0;

                String status = (json.isNull(Constants.KEY_STATUS)) ? "" : json.getString(KEY_STATUS);
                String issued_at = (json.isNull(Constants.KEY_ISSUED_AT)) ? "" : json.getString(KEY_ISSUED_AT);
                String paid_at = (json.isNull(Constants.KEY_PAID_AT)) ? "" : json.getString(KEY_PAID_AT);
                String cancelled_at = (json.isNull(Constants.KEY_CANCELLED_AT)) ? "" : json.getString(KEY_CANCELLED_AT);
                String expired_at = (json.isNull(Constants.KEY_EXPIRED_AT)) ? "" : json.getString(KEY_EXPIRED_AT);
                String billing_start = (json.isNull(KEY_BILLING_START_AT)) ? "" : json.getString(KEY_BILLING_START_AT);
                String billing_end = (json.isNull(KEY_BILLING_END_AT)) ? "" : json.getString(KEY_BILLING_END_AT);

                List<SubscriptionPlan> subscriptionPlanList = new ArrayList<>();

                JSONArray json_array = json.getJSONArray(KEY_LINE_ITEMS);

                for (int j = 0; j < json_array.length(); j++)
                {
                    JSONObject item_json = json_array.getJSONObject(j);

                    String name = (item_json.isNull(KEY_NAME)) ? "" : item_json.getString(KEY_NAME);
                    String description = (item_json.isNull(KEY_DESCRIPTION)) ? "" : item_json.getString(KEY_DESCRIPTION);
                    String type = (item_json.isNull(KEY_TYPE)) ? "" : item_json.getString(KEY_TYPE);
                    String itemCurrency = (item_json.isNull(Constants.KEY_CURRENCY)) ? "" : item_json.getString(KEY_CURRENCY);
                    double itemAmount = (item_json.isNull(Constants.KEY_AMOUNT)) ? 0 : item_json.getDouble(KEY_AMOUNT);
                    double itemAmountUnit = (item_json.isNull(KEY_UNIT_AMOUNT)) ? 0 : item_json.getDouble(KEY_UNIT_AMOUNT);
                    double itemGrossAmount = (item_json.isNull(KEY_GROSS_AMOUNT)) ? 0 : item_json.getDouble(KEY_GROSS_AMOUNT);
                    int quantity = (item_json.isNull(KEY_QUANTITY)) ? 1 : item_json.getInt(KEY_QUANTITY);

                    subscriptionPlanList.add(new SubscriptionPlan(name, description, type, itemAmount, itemAmountUnit,
                            itemGrossAmount, quantity, itemCurrency));
                }

                if(json.has(KEY_NOTES))
                {
                    Object json_notes = new JSONTokener(json.getString(KEY_NOTES)).nextValue();

                    if (json_notes instanceof JSONObject)
                    {
                        json_notes = json.getJSONObject(KEY_NOTES);
                        discount = ((JSONObject) json_notes).has(KEY_DISCOUNT) ? ((JSONObject) json_notes).getDouble(KEY_DISCOUNT) : 0;
                        amount = ((JSONObject) json_notes).has(KEY_PLAN_AMOUNT) ? ((JSONObject) json_notes).getDouble(KEY_PLAN_AMOUNT) : amount;
                    }
                }

                json = json.getJSONObject(KEY_CUSTOMER_DETAILS);

                String customer_name = (json.isNull(KEY_NAME)) ? "" : json.getString(KEY_NAME);
                String customer_email = (json.isNull(KEY_EMAIL)) ? "" : json.getString(KEY_EMAIL);

                billingList.add(new SubscriptionBilling(invoice_id, customer_id, order_id, payment_id, subscription_id,
                        status, issued_at, paid_at, cancelled_at, expired_at, billing_start, billing_end, amount, discount, amount_paid, amount_due,
                        currency, subscriptionPlanList, customer_name, customer_email));
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return billingList;
    }
}