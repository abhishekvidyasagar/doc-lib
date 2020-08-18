package com.doconline.doconline.model;


import com.doconline.doconline.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chiranjit on 27/04/17.
 */
public class Medication implements Serializable
{
    private int id, alarm_status;
    private String name, intake_time = "", from_date = "", to_date = "", notes = "";
    private String noofdays = "",status = "";


    public Medication()
    {

    }

    public Medication(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Medication(int id, String name, String intake_time, String from_date, String to_date, String noofdays, String status, String notes)
    {
        this.id = id;
        this.name = name;
        this.intake_time = intake_time;
        this.from_date = from_date;
        this.to_date = to_date;

        this.noofdays = noofdays;
        this.status = status;

        this.notes = notes;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getAlarmStatus() {
        return this.alarm_status;
    }

    public void setAlarmStatus(int alarm_status) {
        this.alarm_status = alarm_status;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


    public String getIntakeTime() {
        return this.intake_time;
    }

    public void setIntakeTime(String intake_time) {
        this.intake_time = intake_time;
    }


    public String getFromDate() {
        return this.from_date;
    }

    public void setFromDate(String from_date) {
        this.from_date = from_date;
    }


    public String getToDate() {
        return this.to_date;
    }

    public void setToDate(String to_date) {
        this.to_date = to_date;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public String getStatus() {
        return status;
    }

    public String getNoOfDays() {
        return noofdays;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNoOfDays(String noofdays) {
        this.noofdays = noofdays;
    }

    public static Medication getMedicationFromJSON(String json_data)
    {
        Medication medication = new Medication();

        try
        {
            JSONObject json = new JSONObject(json_data);

            int id = json.getInt(Constants.KEY_ID);
            String name = json.getString(Constants.KEY_NAME);
            String intake_time = (json.isNull(Constants.KEY_INTAKE_TIME)) ? "" : json.getString(Constants.KEY_INTAKE_TIME);
            String from_date = (json.isNull(Constants.KEY_FROM_DATE)) ? "" : json.getString(Constants.KEY_FROM_DATE);
            String to_date = (json.isNull(Constants.KEY_TO_DATE)) ? "" : json.getString(Constants.KEY_TO_DATE);

            String noofdays = (json.isNull(Constants.KEY_NO_OF_DAYS)) ? "" : json.getString(Constants.KEY_NO_OF_DAYS);
            String status = (json.isNull(Constants.KEY_MEDICATIONS_STATUS)) ? "" : json.getString(Constants.KEY_MEDICATIONS_STATUS);

            String notes = (json.isNull(Constants.KEY_NOTES)) ? "" : json.getString(Constants.KEY_NOTES);

            medication.setId(id);
            medication.setName(name);
            medication.setIntakeTime(intake_time);
            medication.setFromDate(from_date);
            medication.setToDate(to_date);

            medication.setNoOfDays(noofdays);
            medication.setStatus(status);

            medication.setNotes(notes);

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return medication;
    }


    public static HashMap<String, Object> composeMedicationMap(Medication medication)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_NAME, medication.getName());
        hashMap.put(Constants.KEY_INTAKE_TIME, medication.getIntakeTime());
        hashMap.put(Constants.KEY_FROM_DATE, medication.getFromDate());
        hashMap.put(Constants.KEY_TO_DATE, medication.getToDate());
        hashMap.put(Constants.KEY_NOTES, medication.getNotes());
        if (medication.getNoOfDays().equalsIgnoreCase("")){
            hashMap.put(Constants.KEY_NO_OF_DAYS, "0");
        }else {
            hashMap.put(Constants.KEY_NO_OF_DAYS, medication.getNoOfDays());
        }


        hashMap.put(Constants.KEY_MEDICATIONS_STATUS, medication.getStatus());

        return hashMap;
    }

}