package com.doconline.doconline.model;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.Log;

import com.doconline.doconline.app.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_AVAILABLE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_CART_PACKAGE_PRICE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_EXPIRES_ON;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_FASTING;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_GENERICPACKAGE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_HANDLINGCHARGES;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_IMAGELINK;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_INCARTITEMSCOUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_NORMALVALUE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGECODE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGEID;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGENAME;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGEPRICE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PACKAGE_TESTS;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PARTNERID;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PARTNERIMAGEURL;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_PROFILETYPE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_REFERENCESTRING;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_TESTCOUNT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_UNITS;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_VOLUME;

/**
 * Created by admin on 2018-03-19.
 */

public class DiagnosticsItem implements Serializable{

    int     partnerId;
    int     packageID;
    int     iItemsInCart;
    int     iTestCount;
    float   price;
    float   handlingCharges;
    String  dateExpiresOn;
    String  strPackage_name;
    String  strPackage_code;
    String  imgURL;
    String  strNormalValue;
    String  strUnits;
    String  strVolume;
    String  strProfileType;
    String  strReferenceString;
    String  strpartnerImgUrl;
    boolean isFastingRequired;
    boolean bIsAvailable;
    boolean isAddedToCart = false;
    String packagetype;
    String company;

    public String getPackagetype() {
        return packagetype;
    }

    public void setPackagetype(String packagetype) {
        this.packagetype = packagetype;
    }

    public SpannableStringBuilder getListOfTests() {
        return listOfTests;
    }

    SpannableStringBuilder listOfTests = null;

//    public ArrayList<SpannableString> getListOfTests() {
//        return listOfTests;
//    }

    //ArrayList<SpannableString> listOfTests = new ArrayList<>();

    public boolean isAddedToCart() {
        return isAddedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        isAddedToCart = addedToCart;
    }

    public String getStrpartnerImgUrl() {
        return strpartnerImgUrl;
    }

    public void setStrpartnerImgUrl(String strpartnerImgUrl) {
        this.strpartnerImgUrl = strpartnerImgUrl;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public DiagnosticsItem(int partnerId, int packageID, int iItemsInCart, int iTestCount, float price, float handlingCharges, String dateExpiresOn, String strPackage_name, String strPackage_code, String imgURL, String partnerImageUrl, String strNormalValue, String strUnits, String strVolume, String strProfileType, String strReferenceString, boolean isFastingRequired, boolean bIsAvailable, String packagetype, String companyName) {

        this.partnerId = partnerId;
        this.packageID = packageID;
        this.iItemsInCart = iItemsInCart;
        if(iItemsInCart > 0)
            isAddedToCart = true;
        this.iTestCount = iTestCount;
        this.price = price;
        this.handlingCharges = handlingCharges;
        this.dateExpiresOn = dateExpiresOn;
        this.strPackage_name = strPackage_name;
        this.strPackage_code = strPackage_code;
        this.imgURL = imgURL;
        this.strpartnerImgUrl= partnerImageUrl;
        this.strNormalValue = strNormalValue;
        this.strUnits = strUnits;
        this.strVolume = strVolume;
        this.strProfileType = strProfileType;
        this.strReferenceString = strReferenceString;
        this.isFastingRequired = isFastingRequired;
        this.bIsAvailable = bIsAvailable;
        this.packagetype = packagetype;
        this.company = companyName;
    }

    public static ArrayList<DiagnosticsItem> getDiagnosticsItemListFromJSON(JSONObject jsonObject) {

        ArrayList<DiagnosticsItem> mDiagnosticsItemList = new ArrayList<>();

        JSONArray diagnosticsPlans = null;
        JSONArray btobplan = null;


        try {
            diagnosticsPlans = jsonObject.getJSONArray(KEY_DIAGNOSTICS_GENERICPACKAGE);

            DiagnosticsItem item;

            btobplan = jsonObject.getJSONArray("b2b");

            int currentpage = jsonObject.getInt(Constants.KEY_CURRENT_PAGE);
            Log.e("AAA","Currentpage :  "+currentpage);
            if (currentpage ==1 && btobplan!=null && btobplan.length()>0){
                for (int j=0; j < btobplan.length(); j++){



                    JSONObject json = btobplan.getJSONObject(j);
                    boolean isPlanAvailable = json.getInt(KEY_DIAGNOSTICS_AVAILABLE) == 1;
                    boolean isFastingRequired = json.getString(KEY_DIAGNOSTICS_FASTING) != null;

                    String expiresOn = json.getString(KEY_DIAGNOSTICS_EXPIRES_ON);

                    item = new DiagnosticsItem( json.getInt(KEY_DIAGNOSTICS_PARTNERID),
                            json.getInt(KEY_DIAGNOSTICS_PACKAGEID),
                            json.getInt(KEY_DIAGNOSTICS_INCARTITEMSCOUNT),
                            json.getInt(KEY_DIAGNOSTICS_TESTCOUNT),
                            (float)json.getDouble(KEY_DIAGNOSTICS_PACKAGEPRICE),
                            (float)json.getDouble(KEY_DIAGNOSTICS_HANDLINGCHARGES),
                            expiresOn,
                            json.getString(KEY_DIAGNOSTICS_PACKAGENAME),
                            json.getString(KEY_DIAGNOSTICS_PACKAGECODE),
                            json.getString(KEY_DIAGNOSTICS_IMAGELINK),
                            json.getString(KEY_DIAGNOSTICS_PARTNERIMAGEURL),
                            json.getString(KEY_DIAGNOSTICS_NORMALVALUE),
                            json.getString(KEY_DIAGNOSTICS_UNITS),
                            json.getString(KEY_DIAGNOSTICS_VOLUME),
                            json.getString(KEY_DIAGNOSTICS_PROFILETYPE),
                            json.getString(KEY_DIAGNOSTICS_REFERENCESTRING),
                            isFastingRequired,
                            isPlanAvailable,"btob",jsonObject.getString("b2b_company")
                    );

                    if(item.getiTestCount() > 0){
                        JSONArray testsList = json.getJSONArray(KEY_DIAGNOSTICS_PACKAGE_TESTS);
                        if (testsList != null) {
                            item.listOfTests = new SpannableStringBuilder("");
                            for (int k = 0;k <testsList.length();k++){
                                SpannableString str_Item = new SpannableString(testsList.getString(k)+"\n\n");
                                str_Item.setSpan(new BulletSpan(30, Color.GREEN),0,str_Item.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                //Concatenate into one string
                                item.listOfTests.append(str_Item);
                            }
                        }
                    }
                    mDiagnosticsItemList.add(item);
                }
            }



            for(int i = 0; i < diagnosticsPlans.length(); i++){

                JSONObject json = diagnosticsPlans.getJSONObject(i);

                boolean isPlanAvailable = json.getInt(KEY_DIAGNOSTICS_AVAILABLE) == 1;
                boolean isFastingRequired = json.getString(KEY_DIAGNOSTICS_FASTING) != null;

                String expiresOn = json.getString(KEY_DIAGNOSTICS_EXPIRES_ON);


                item = new DiagnosticsItem( json.getInt(KEY_DIAGNOSTICS_PARTNERID),
                        json.getInt(KEY_DIAGNOSTICS_PACKAGEID),
                        json.getInt(KEY_DIAGNOSTICS_INCARTITEMSCOUNT),
                        json.getInt(KEY_DIAGNOSTICS_TESTCOUNT),
                        (float)json.getDouble(KEY_DIAGNOSTICS_PACKAGEPRICE),
                        (float)json.getDouble(KEY_DIAGNOSTICS_HANDLINGCHARGES),
                        expiresOn,
                        json.getString(KEY_DIAGNOSTICS_PACKAGENAME),
                        json.getString(KEY_DIAGNOSTICS_PACKAGECODE),
                        json.getString(KEY_DIAGNOSTICS_IMAGELINK),
                        json.getString(KEY_DIAGNOSTICS_PARTNERIMAGEURL),
                        json.getString(KEY_DIAGNOSTICS_NORMALVALUE),
                        json.getString(KEY_DIAGNOSTICS_UNITS),
                        json.getString(KEY_DIAGNOSTICS_VOLUME),
                        json.getString(KEY_DIAGNOSTICS_PROFILETYPE),
                        json.getString(KEY_DIAGNOSTICS_REFERENCESTRING),
                        isFastingRequired,
                        isPlanAvailable,
                        "general",""
                );

                if(item.getiTestCount() > 0){
                    JSONArray testsList = json.getJSONArray(KEY_DIAGNOSTICS_PACKAGE_TESTS);
                    if (testsList != null) {
                        item.listOfTests = new SpannableStringBuilder("");
                        for (int j = 0;j <testsList.length();j++){
                            SpannableString str_Item = new SpannableString(testsList.getString(j)+"\n\n");
                            str_Item.setSpan(new BulletSpan(30, Color.GREEN),0,str_Item.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //Concatenate into one string
                            item.listOfTests.append(str_Item);
                        }
                    }
                }
                mDiagnosticsItemList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mDiagnosticsItemList;
    }
    public int getPartnerId() {
        return partnerId;
    }

    public int getPackageID() {
        return packageID;
    }

    public int getiItemsInCart() {
        return iItemsInCart;
    }

    public int getiTestCount() {
        return iTestCount;
    }

    public float getPrice() {
        return price;
    }

    public float getHandlingCharges() {
        return handlingCharges;
    }

    public String getDateExpiresOn() {
        return dateExpiresOn;
    }

    public String getStrPackage_name() {
        return strPackage_name;
    }

    public String getStrPackage_code() {
        return strPackage_code;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getStrNormalValue() {
        return strNormalValue;
    }

    public String getStrUnits() {
        return strUnits;
    }

    public String getStrVolume() {
        return strVolume;
    }

    public String getStrProfileType() {
        return strProfileType;
    }

    public String getStrReferenceString() {
        return strReferenceString;
    }

    public boolean isFastingRequired() {
        return isFastingRequired;
    }

    public boolean isbIsAvailable() {
        return bIsAvailable;
    }

    public static HashMap<String, Object> composeUserJSON(DiagnosticsItem item)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_DIAGNOSTICS_PACKAGENAME, item.strPackage_name);
        hashMap.put(KEY_DIAGNOSTICS_PACKAGEID, item.packageID);
        hashMap.put(KEY_DIAGNOSTICS_CART_PACKAGE_PRICE, item.price);
        hashMap.put(KEY_DIAGNOSTICS_PARTNERID, item.partnerId);

        return hashMap;
    }
}
