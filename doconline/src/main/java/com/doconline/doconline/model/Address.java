package com.doconline.doconline.model;

import com.doconline.doconline.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_ADDRESS_1;
import static com.doconline.doconline.app.Constants.KEY_ADDRESS_2;
import static com.doconline.doconline.app.Constants.KEY_ALTERNATE_CONTACT_NO;
import static com.doconline.doconline.app.Constants.KEY_CITY;
import static com.doconline.doconline.app.Constants.KEY_COUNTRY_ID;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_PINCODE;
import static com.doconline.doconline.app.Constants.KEY_STATE;

/**
 * Created by chiranjit on 27/04/17.
 */

public class Address implements Serializable
{
    private int address_id;

    private String street = "";
    private String city = "";
    private String state = "";
    private String address1 = "";
    private String address2 = "";
    private String country = "";
    private String country_code = "";
    private String pincode = "";
    private String phone_no = "";
    private String full_address = "";


    public Address()
    {

    }


    public Address(String pincode, String address1, String address2, String phone_no)
    {
        this.pincode = pincode;
        this.address1 = address1;
        this.address2 = address2;
        this.phone_no = phone_no;
    }

    public Address(String country, String country_code)
    {
        this.country = country;
        this.country_code = country_code;
    }


    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getStreet()
    {
        return this.street;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return this.city;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return this.state;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCountry()
    {
        return this.country;
    }

    public void setCountryCode(String country_code)
    {
        this.country_code = country_code;
    }

    public String getCountryCode()
    {
        return this.country_code;
    }

    public void setPincode(String pincode)
    {
        this.pincode = pincode;
    }

    public String getPincode()
    {
        return this.pincode;
    }

    public void setAddress_1(String address_1)
    {
        this.address1 = address_1;
    }

    public String getAddress_1()
    {
        return this.address1;
    }

    public void setAddress_2(String address_2)
    {
        this.address2 = address_2;
    }

    public String getAddress_2()
    {
        return this.address2;
    }

    public void setPhoneNo(String phone_no)
    {
        this.phone_no = phone_no;
    }

    public String getPhoneNo()
    {
        return this.phone_no;
    }


    public static int get_country_index(List<Address> countries, String code)
    {
        for(int i=1; i<=countries.size(); i++)
        {
            if(countries.get(i-1).country_code.equalsIgnoreCase(code))
            {
                return i;
            }
        }

        return 0;
    }


    public static User getUserAddressFromJSON(String json_data)
    {
        User user = new User();

        try
        {
            JSONObject json = new JSONObject(json_data);

            String address1 = (json.isNull(Constants.KEY_ADDRESS_1)) ? "" : json.getString(Constants.KEY_ADDRESS_1);
            String address2 = (json.isNull(Constants.KEY_ADDRESS_2)) ? "" : json.getString(Constants.KEY_ADDRESS_2);
            String state = (json.isNull(Constants.KEY_STATE)) ? "" : json.getString(Constants.KEY_STATE);
            String city = (json.isNull(Constants.KEY_CITY)) ? "" : json.getString(Constants.KEY_CITY);
            String mobile_no = (json.isNull(Constants.KEY_MOBILE_NO)) ? "" : json.getString(Constants.KEY_MOBILE_NO);
            String alternate_contact_no = (json.isNull(Constants.KEY_ALTERNATE_CONTACT_NO)) ? "" : json.getString(Constants.KEY_ALTERNATE_CONTACT_NO);
            String pin_code = (json.isNull(KEY_PINCODE)) ? "" : json.getString(KEY_PINCODE);
            String country_code = (json.isNull(KEY_COUNTRY_ID)) ? "" : json.getString(KEY_COUNTRY_ID);

            user.getAddress().setAddress_1(address1);
            user.getAddress().setAddress_2(address2);
            user.getAddress().setState(state);
            user.getAddress().setCity(city);
            user.setPhoneNo(mobile_no);
            user.setAlternatePhoneNo(alternate_contact_no);
            user.getAddress().setPincode(pin_code);
            user.getAddress().setCountryCode(country_code);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return user;
    }

    public static HashMap<String, Object> composeAddressMap(User user)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_ADDRESS_1, user.getAddress().getAddress_1());
        hashMap.put(KEY_ADDRESS_2, user.getAddress().getAddress_2());
        hashMap.put(KEY_STATE, user.getAddress().getState());
        hashMap.put(KEY_CITY, user.getAddress().getCity());
        hashMap.put(KEY_PINCODE, user.getAddress().getPincode());
        hashMap.put(KEY_COUNTRY_ID, user.getAddress().getCountryCode());
        hashMap.put(KEY_MOBILE_NO, user.getPhoneNo());
        hashMap.put(KEY_ALTERNATE_CONTACT_NO, user.getAlternatePhoneNo());

        return hashMap;
    }

    public String getFullAddress() {
        return this.full_address;
    }

    public void setFullAddress(String full_address) {
        this.full_address = full_address;
    }

    public int getAddressId() {
        return this.address_id;
    }

    public void setAddressId(int address_id) {
        this.address_id = address_id;
    }
}