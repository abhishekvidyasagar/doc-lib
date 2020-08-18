package com.doconline.doconline.model;

import android.util.Log;

import com.doconline.doconline.app.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_BOOKING_CONSENT;
import static com.doconline.doconline.app.Constants.KEY_DATE_OF_BIRTH;
import static com.doconline.doconline.app.Constants.KEY_DETAILS;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_FIRST_NAME;
import static com.doconline.doconline.app.Constants.KEY_GENDER;
import static com.doconline.doconline.app.Constants.KEY_IS_ACTIVE;
import static com.doconline.doconline.app.Constants.KEY_LAST_NAME;
import static com.doconline.doconline.app.Constants.KEY_MIDDLE_NAME;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_NAME_PREFIX;
import static com.doconline.doconline.app.Constants.KEY_USER_ID;

/**
 * Created by chiranjitbardhan on 18/01/18.
 */

public class FamilyMember extends Patient implements Serializable
{
    private int mFamilyId;
    private int mMemberId;
    private int isActive;


    public static List<FamilyMember> getFamilyMemberListFromJSON(String json_data)
    {
        List<FamilyMember> memberList = new ArrayList<>();

        try
        {
            JSONArray array = new JSONArray(json_data);

            for(int i=0; i<array.length(); i++)
            {
                FamilyMember member = new FamilyMember();

                JSONObject json = array.getJSONObject(i);

                String email = (json.isNull(KEY_EMAIL)) ? "" : json.getString(KEY_EMAIL);
                String mobile_no = (json.isNull(KEY_MOBILE_NO)) ? "" : json.getString(KEY_MOBILE_NO);
                int is_active =  json.getInt(KEY_IS_ACTIVE);

                final String avatar_url = (json.isNull(Constants.KEY_AVATAR_URL)) ? "" : json.getString(Constants.KEY_AVATAR_URL);

                json = new JSONObject(json.getString(KEY_DETAILS));

                int member_id = json.getInt(KEY_USER_ID);
                String name_prefix = (json.isNull(Constants.KEY_NAME_PREFIX)) ? "" : json.getString(Constants.KEY_NAME_PREFIX);
                String first_name = (json.isNull(Constants.KEY_FIRST_NAME)) ? "" : json.getString(Constants.KEY_FIRST_NAME);
                String middle_name = (json.isNull(Constants.KEY_MIDDLE_NAME)) ? "" : json.getString(Constants.KEY_MIDDLE_NAME);
                String last_name = (json.isNull(Constants.KEY_LAST_NAME)) ? "" : json.getString(Constants.KEY_LAST_NAME);
                String full_name = (json.isNull(Constants.KEY_FULL_NAME)) ? "" : json.getString(Constants.KEY_FULL_NAME);
                String date_of_birth = (json.isNull(Constants.KEY_DATE_OF_BIRTH)) ? "" : json.getString(Constants.KEY_DATE_OF_BIRTH);
                String gender = (json.isNull(Constants.KEY_GENDER)) ? "" : json.getString(Constants.KEY_GENDER);

                member.setMemberId(member_id);
                member.setNamePrefix(name_prefix);
                member.setFirstName(first_name);
                member.setMiddleName(middle_name);
                member.setLastName(last_name);
                member.setFullName(full_name);
                member.setEmail(email);
                member.setPhoneNo(mobile_no);
                member.setDateOfBirth(date_of_birth);
                member.setGender(gender);
                member.setProfilePic(avatar_url);
                member.setIsActive(is_active);

                memberList.add(member);
            }

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.i("FAMILY_SIZE", "" + memberList.size());

        return memberList;
    }


    public static HashMap<String, Object> composeFamilyMemberJSON(FamilyMember member)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        if(!member.getNamePrefix().isEmpty())
        {
            hashMap.put(KEY_NAME_PREFIX, member.getNamePrefix());
        }

        hashMap.put(KEY_FIRST_NAME, member.getFirstName());
        hashMap.put(KEY_MIDDLE_NAME, member.getMiddleName());
        hashMap.put(KEY_LAST_NAME, member.getLastName());
        hashMap.put(KEY_DATE_OF_BIRTH, member.getDateOfBirth());
        hashMap.put(KEY_GENDER, member.getGender());
        hashMap.put(KEY_EMAIL, member.getEmail());
        hashMap.put(KEY_MOBILE_NO, member.getPhoneNo());

        return hashMap;
    }

    public int getFamilyId()
    {
        return this.mFamilyId;
    }

    public void setFamilyId(int mFamilyId)
    {
        this.mFamilyId = mFamilyId;
    }


    public int getMemberId()
    {
        return this.mMemberId;
    }

    public void setMemberId(int mMemberId)
    {
        this.mMemberId = mMemberId;
    }


    public static String composeBookingConsent(boolean booking_consent)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put(KEY_BOOKING_CONSENT, booking_consent);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }

    public int getIsActive()
    {
        return this.isActive;
    }

    public void setIsActive(int isActive)
    {
        this.isActive = isActive;
    }
}