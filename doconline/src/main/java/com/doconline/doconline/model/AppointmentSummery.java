package com.doconline.doconline.model;

import android.util.Log;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.consultation.BookConsultationActivity;
import com.doconline.doconline.helper.Helper;
import com.doconline.doconline.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.doconline.doconline.app.Constants.CONSULTATION_FAMILY;
import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT;
import static com.doconline.doconline.app.Constants.KEY_ATTACHMENTS;
import static com.doconline.doconline.app.Constants.KEY_CONFIRM_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_DISCOUNT_CODE;
import static com.doconline.doconline.app.Constants.KEY_EMAIL;
import static com.doconline.doconline.app.Constants.KEY_FIRST_NAME;
import static com.doconline.doconline.app.Constants.KEY_LAST_NAME;
import static com.doconline.doconline.app.Constants.KEY_MOBILE_NO;
import static com.doconline.doconline.app.Constants.KEY_ONETIME_TYPE;
import static com.doconline.doconline.app.Constants.KEY_PASSWORD;
import static com.doconline.doconline.app.Constants.KEY_PLAN_ID;
import static com.doconline.doconline.app.Constants.KEY_PLAN_TYPE;
import static com.doconline.doconline.app.Constants.KEY_RECURRING_TYPE;
import static com.doconline.doconline.app.Constants.KEY_TIMEZONE;
import static com.doconline.doconline.app.Constants.KEY_TYPE;
import static com.doconline.doconline.app.Constants.LANGUAGE_PREFERENCES_YES;

/**
 * Created by chiranjit on 20/04/17.
 */
public class AppointmentSummery
{
    private int call_type = Constants.CALL_TYPE_VIDEO, booking_type = Constants.BOOKING_TYPE_INSTANCE,
            call_medium = Constants.CALL_MEDIUM_INTERNET, language_preferences = LANGUAGE_PREFERENCES_YES;
    private Boolean termsandConditionsStatus = false;
    private int for_whom = Constants.CONSULTATION_SELF, booked_for_user_id;
    private String notes = "", appointment_time = "", mPatientName = "", mPatientAge;
    private List<FileUtils> attachments = new ArrayList<>();
    private List<FileUtils> attachmentURL = new ArrayList<>();


    public void setAppointmentTime(String appointment_time)
    {
        this.appointment_time = appointment_time;
    }

    public String getAppointmentTime()
    {
        return this.appointment_time;
    }


    public void addFile(FileUtils image)
    {
        this.attachments.add(image);
    }


    public FileUtils getFile(int index)
    {
        return this.attachments.get(index);
    }

    public List<FileUtils> getFiles()
    {
        return this.attachments;
    }


    public FileUtils getFileURL(int index)
    {
        return this.attachmentURL.get(index);
    }

    public List<FileUtils> getAllFileURL()
    {
        return this.attachmentURL;
    }


    public void addFileURL(FileUtils imageModel)
    {
        this.attachmentURL.add(imageModel);
    }


    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getNotes()
    {
        return this.notes;
    }

    public void setCallType(int call_type)
    {
        this.call_type = call_type;
    }

    public int getCallType()
    {
        return this.call_type;
    }

    public void setCallMedium(int call_medium)
    {
        this.call_medium = call_medium;
    }

    public int getCallMedium()
    {
        return this.call_medium;
    }

    public void setLanguagePreferences(int language_preferences)
    {
        this.language_preferences = language_preferences;
    }

    public int getLanguagePreferences()
    {
        return this.language_preferences;
    }

    public void setForWhom(int for_whom)
    {
        this.for_whom = for_whom;
    }

    public int getForWhom()
    {
        return this.for_whom;
    }

    public void setBookedForUserId(int booked_for_user_id)
    {
        this.booked_for_user_id = booked_for_user_id;
    }

    public int getBookedForUserId()
    {
        return this.booked_for_user_id;
    }

    public void setBookingType(int booking_type)
    {
        this.booking_type = booking_type;
    }

    public int getBookingType()
    {
        return this.booking_type;
    }


    public static HashMap<String, Object> composeAttachmentJSON(List<FileUtils> imageModels)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        for(int i=0; i<imageModels.size(); i++)
        {
            if(Helper.fileExist(imageModels.get(i).getPath()))
            {
                hashMap.put("attachments[" + i + "]", imageModels.get(i).getPath());
            }
        }

        return hashMap;
    }


    public static HashMap<String, Object> composeCaptionJSON(List<FileUtils> imageModels)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        for(int i=0; i<imageModels.size(); i++)
        {
            if(Helper.fileExist(imageModels.get(i).getPath()))
            {
                hashMap.put("titles[" + i + "]", imageModels.get(i).getCaption());
            }
        }

        return hashMap;
    }

    public static HashMap<String, Object> composeAppointmentSummaryMap(AppointmentSummery summery)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_CALL_TYPE, String.valueOf(summery.getCallType()));
        hashMap.put(Constants.KEY_FOR_WHOM, String.valueOf(summery.getForWhom()));

        if(!summery.getNotes().isEmpty())
        {
            hashMap.put(Constants.KEY_NOTES, summery.getNotes());
        }

        hashMap.put(Constants.KEY_BOOKING_TYPE, String.valueOf(summery.getBookingType()));
        hashMap.put(Constants.KEY_SCHEDULED_AT, summery.getAppointmentTime());
        hashMap.put(Constants.KEY_CALL_CHANNEL, String.valueOf(summery.getCallMedium()));

        if(summery.getForWhom() == CONSULTATION_FAMILY)
        {
            hashMap.put(Constants.KEY_BOOKED_FOR_USER_ID, String.valueOf(summery.getBookedForUserId()));
        }

        if(summery.getLanguagePreferences() == LANGUAGE_PREFERENCES_YES)
        {
            hashMap.put(Constants.KEY_LANGUAGE_PREF, String.valueOf(summery.getLanguagePreferences()));
        }

        hashMap.put(Constants.KEY_FOLLOW_UP_DATA, BookConsultationActivity.map);
        Log.e("AAA","after follow up request object is : "+hashMap);

        return hashMap;
    }

    public String getPatientName()
    {
        return this.mPatientName;
    }

    public void setPatientName(String mPatientName) {
        this.mPatientName = mPatientName;
    }



    public String getPatientAge()
    {
        return this.mPatientAge;
    }

    public void setPatientAge(String mPatientAge)
    {
        this.mPatientAge = mPatientAge;
    }

    public static String getAppointmentSummaryJSON(AppointmentSummery summery, User mProfile, SubscriptionPlan mPlan, String discount_coupon)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put(KEY_FIRST_NAME, mProfile.getFirstName());
            json.put(KEY_LAST_NAME, mProfile.getLastName());
            json.put(KEY_EMAIL, mProfile.getEmail());
            json.put(KEY_MOBILE_NO, mProfile.getPhoneNo());
            json.put(KEY_PASSWORD, mProfile.getUserAccount().getPassword());
            json.put(KEY_CONFIRM_PASSWORD, mProfile.getUserAccount().getConfirmPassword());

            json.put(KEY_TYPE, mPlan.getType());
            json.put(KEY_PLAN_TYPE, mPlan.getPlanType());

            if(mPlan.getType().equalsIgnoreCase(KEY_RECURRING_TYPE))
            {
                json.put(KEY_PLAN_ID, mPlan.getPlanId());
            }

            else if(mPlan.getType().equalsIgnoreCase(KEY_ONETIME_TYPE) && !discount_coupon.isEmpty())
            {
                json.put(KEY_DISCOUNT_CODE, discount_coupon);
            }


            json.put(KEY_TIMEZONE, Helper.findTimeZone());

            JSONObject json_appointment = new JSONObject();

            json_appointment.put(Constants.KEY_CALL_TYPE, summery.getCallType());
            json_appointment.put(Constants.KEY_FOR_WHOM, summery.getForWhom());

            json_appointment.put(Constants.KEY_NOTES, summery.getNotes());
            json_appointment.put(Constants.KEY_BOOKING_TYPE, summery.getBookingType());
            json_appointment.put(Constants.KEY_SCHEDULED_AT, summery.getAppointmentTime());
            json_appointment.put(Constants.KEY_CALL_CHANNEL, summery.getCallMedium());

            json_appointment.put(KEY_ATTACHMENTS, FileUtils.getFileJsonArray(summery.getAllFileURL()));
            json.put(KEY_APPOINTMENT, json_appointment);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }

    public void setTermsandConditionsStatus(boolean termsandConditionsStatus) {
        this.termsandConditionsStatus = termsandConditionsStatus;
    }
    public Boolean getTermsandConditionsStatus(){
        return termsandConditionsStatus;
    }
}