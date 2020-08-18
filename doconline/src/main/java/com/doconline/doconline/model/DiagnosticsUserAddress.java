package com.doconline.doconline.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_CITY;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_ID;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_ISDEFAULT;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_LANDMARK;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_LANE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_PIN;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_STATE;
import static com.doconline.doconline.app.Constants.KEY_DIAGNOSTICS_ADDRESS_TITLE;

/**
 * Created by A on 3/12/2018.
 */

public class DiagnosticsUserAddress implements Serializable {

    int iAddressID;
    String strAddressTitle;
    String strAddressLane;
    String strLandmark;
    String strCity;
    String strState;
    int iPincode;
    boolean bIsDefaultAddress;

    public DiagnosticsUserAddress(int addrID, String addressTitle, String addressLane, String landmark, String city, String State, int pincode, boolean isDefaultAddr) {

        iAddressID = addrID;
        strAddressTitle = addressTitle;
        strAddressLane = addressLane;
        strLandmark = landmark;
        strCity = city;
        strState = State;
        iPincode = pincode;
        bIsDefaultAddress = isDefaultAddr;
    }

    public DiagnosticsUserAddress(DiagnosticsUserAddress _address) {

        iAddressID          = _address.iAddressID;
        strAddressTitle     = _address.strAddressTitle;
        strAddressLane      = _address.strAddressLane;
        strLandmark         = _address.strLandmark;
        strCity             = _address.strCity;
        strState            = _address.strState;
        iPincode            = _address.iPincode;
        bIsDefaultAddress   = _address.bIsDefaultAddress;
    }

    public DiagnosticsUserAddress() {

    }


    public String getStrAddressTitle() {
        return strAddressTitle;
    }

    public String getStrAddressLane() {
        return strAddressLane;
    }

    public String getStrLandmark() {
        return strLandmark;
    }

    public String getStrCity() {
        return strCity;
    }

    public String getStrState() {
        return strState;
    }

    public Integer getiPincode() {
        return iPincode;
    }

    public boolean isDefaultAddress() {
        return bIsDefaultAddress;
    }

    public int getiAddressID() {
        return iAddressID;
    }

    public static ArrayList<DiagnosticsUserAddress> getUserAddressListFromJSON(JSONArray array) {

        ArrayList<DiagnosticsUserAddress> userAddressList = new ArrayList<>();

        try {
            DiagnosticsUserAddress address;// = new DiagnosticsUserAddress();

            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);

                boolean isAddressDefault = json.getInt(KEY_DIAGNOSTICS_ADDRESS_ISDEFAULT) != 0;
                address = new DiagnosticsUserAddress(
                        json.getInt(KEY_DIAGNOSTICS_ADDRESS_ID),
                        json.getString(KEY_DIAGNOSTICS_ADDRESS_TITLE),
                        json.getString(KEY_DIAGNOSTICS_ADDRESS_LANE),
                        json.getString(KEY_DIAGNOSTICS_ADDRESS_LANDMARK),
                        json.getString(KEY_DIAGNOSTICS_ADDRESS_CITY),
                        json.getString(KEY_DIAGNOSTICS_ADDRESS_STATE),
                        json.getInt(KEY_DIAGNOSTICS_ADDRESS_PIN),
                        isAddressDefault);
                userAddressList.add(address);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userAddressList;
    }

    public static HashMap<String, Object> composeUserJSON(DiagnosticsUserAddress address, boolean isAddressIDRequired)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        if(isAddressIDRequired)
            hashMap.put(KEY_DIAGNOSTICS_ADDRESS_ID, address.getiAddressID());

        hashMap.put(KEY_DIAGNOSTICS_ADDRESS_TITLE, address.getStrAddressTitle());
        hashMap.put(KEY_DIAGNOSTICS_ADDRESS_LANE, address.getStrAddressLane());
        hashMap.put(KEY_DIAGNOSTICS_ADDRESS_LANDMARK, address.getStrLandmark());
        hashMap.put(KEY_DIAGNOSTICS_ADDRESS_CITY, address.getStrCity());
        hashMap.put(KEY_DIAGNOSTICS_ADDRESS_STATE, address.getStrState());
        hashMap.put(KEY_DIAGNOSTICS_ADDRESS_PIN, String.valueOf(address.getiPincode()));

        boolean isDefault = address.isDefaultAddress();

        hashMap.put(KEY_DIAGNOSTICS_ADDRESS_ISDEFAULT,isDefault);

        return hashMap;
    }

    public void setbIsDefaultAddress(boolean bIsDefaultAddress) {
        this.bIsDefaultAddress = bIsDefaultAddress;
    }

    public void setiAddressID(int iAddressID) {
        this.iAddressID = iAddressID;
    }

    public void setStrAddressTitle(String strAddressTitle) {
        this.strAddressTitle = strAddressTitle;
    }

    public void setStrAddressLane(String strAddressLane) {
        this.strAddressLane = strAddressLane;
    }

    public void setStrLandmark(String strLandmark) {
        this.strLandmark = strLandmark;
    }

    public void setStrCity(String strCity) {
        this.strCity = strCity;
    }

    public void setStrState(String strState) {
        this.strState = strState;
    }

    public void setiPincode(int iPincode) {
        this.iPincode = iPincode;
    }
}
