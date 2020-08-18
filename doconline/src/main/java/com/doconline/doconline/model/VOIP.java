package com.doconline.doconline.model;

import com.doconline.doconline.helper.Helper;

import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_DOCTOR_ID;
import static com.doconline.doconline.app.Constants.KEY_STATUS;
import static com.doconline.doconline.app.Constants.KEY_TIMESTAMP;

/**
 * Created by chiranjitbardhan on 16/01/18.
 */

public class VOIP
{
    public static HashMap<String, Object> composeCallStateMap(int doctor_id, int status)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_DOCTOR_ID, String.valueOf(doctor_id));
        hashMap.put(KEY_STATUS, String.valueOf(status));
        hashMap.put(KEY_TIMESTAMP, Helper.Current_to_UTC_TimeZone());

        return hashMap;
    }
}