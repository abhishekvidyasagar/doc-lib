package com.doconline.doconline.Paytm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.doconline.doconline.api.HttpClient;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.BaseActivity;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.doconline.doconline.app.Constants.CALLBACK_URL;
import static com.doconline.doconline.app.Constants.CHANNEL_ID;
import static com.doconline.doconline.app.Constants.CUST_ID;
import static com.doconline.doconline.app.Constants.EMAIL;
import static com.doconline.doconline.app.Constants.INDUSTRY_TYPE_ID;
import static com.doconline.doconline.app.Constants.MID;
import static com.doconline.doconline.app.Constants.MOBILE_NO;
import static com.doconline.doconline.app.Constants.ORDER_ID;
import static com.doconline.doconline.app.Constants.TXN_AMOUNT;
import static com.doconline.doconline.app.Constants.WEBSITE;

/**
 */

public class checksum extends BaseActivity implements PaytmPaymentTransactionCallback {

    String orderId;
    String url;

    String mid = "DocOnl36440033614123";
    String channelid = "WAP";
    String website = "APPPROD";
    String industrytype = "Retail109";


    String custid = "";
    String txnamount = "";
    String email = "";
    String mobile = "";
    String varifyurl = "";


    //private ProgressDialog dialog = new ProgressDialog(checksum.this);

    public static final int HTTP_REQUEST_CODE_CHECKSUM = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        orderId = getIntent().getExtras().get("orderid").toString();
        url = getIntent().getExtras().get("url").toString();
        email = getIntent().getExtras().get("email").toString();
        mobile = getIntent().getExtras().get("phone").toString();
        custid = getIntent().getExtras().get("userid").toString();
        txnamount = getIntent().getExtras().get("amount").toString();

        varifyurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;

        try {

            String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;

            JSONObject jsoobject = new JSONObject();
            jsoobject.put(CALLBACK_URL, callbackurl);
            jsoobject.put(CHANNEL_ID, channelid);
            jsoobject.put(CUST_ID, custid);
            jsoobject.put(INDUSTRY_TYPE_ID, industrytype);
            jsoobject.put(MID, mid);
            jsoobject.put(ORDER_ID, orderId);
            jsoobject.put(TXN_AMOUNT, txnamount);
            jsoobject.put(WEBSITE, website);
            jsoobject.put(EMAIL, email);
            jsoobject.put(MOBILE_NO, mobile);//need to change
            String requestjsonstring = jsoobject.toString();

            new HttpClient(HTTP_REQUEST_CODE_CHECKSUM, MyApplication.HTTPMethod.POST.getValue(), true, requestjsonstring, checksum.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

        } catch (Exception e) {
            Log.e("Execption", "" + e);
        }
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
            if (requestCode == HTTP_REQUEST_CODE_CHECKSUM && responseCode == HttpClient.OK) {
                JSONObject responseObject = new JSONObject(response);

                PaytmPGService Service = PaytmPGService.getProductionService();
                // when app is ready to publish use production service
                // PaytmPGService  Service = PaytmPGService.getProductionService();

                // now call paytm service here
                //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
                Map<String, String> paramMap = new HashMap<String, String>();
                //these are mandatory parameters
                // ye sari valeu same hon achaiye

                //MID provided by paytm
                paramMap.put("MID", mid);
                paramMap.put("ORDER_ID", orderId);
                paramMap.put("CUST_ID", custid);
                paramMap.put("CHANNEL_ID", channelid);
                paramMap.put("TXN_AMOUNT", txnamount);
                paramMap.put("WEBSITE", website);
                paramMap.put("CALLBACK_URL", varifyurl);
                paramMap.put("EMAIL", email);
                paramMap.put("MOBILE_NO", mobile);
                paramMap.put("CHECKSUMHASH", responseObject.get("CHECKSUMHASH").toString());
                //paramMap.put("PAYMENT_TYPE_ID" ,"CC");
                paramMap.put("INDUSTRY_TYPE_ID", industrytype);

                PaytmOrder Order = new PaytmOrder(paramMap);

                Log.e("checksum ", paramMap.toString());


                Service.initialize(Order, null);
                // start payment service call here
                Service.startPaymentTransaction(checksum.this, true, true, checksum.this);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        Intent intent = getIntent();
        intent.putExtra("paytmresponse", bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String s) {

    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  " + s);
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true " + s + "  s1 " + s1);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  ");
        finish();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel ");
    }


}
