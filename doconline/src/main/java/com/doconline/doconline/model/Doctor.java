package com.doconline.doconline.model;

import java.io.Serializable;

/**
 * Created by chiranjit on 16/05/17.
 */

public class Doctor implements Serializable
{
    private int doctor_id, practitioner_number;
    private String name_prefix, first_name, middle_name, last_name, full_name, avatar_url, specialization;
    private double ratings;

    public Doctor()
    {

    }

    public Doctor(String full_name, String specialization)
    {
        this.full_name = full_name;
        this.specialization = specialization;
    }

    public Doctor(String full_name, String specialization, String avatar_url, double ratings)
    {
        this.full_name = full_name;
        this.specialization = specialization;
        this.avatar_url = avatar_url;
        this.ratings = ratings;
    }

    public Doctor(int doctor_id, String name_prefix, String first_name, String middle_name, String last_name, String full_name, String avatar_url, int practitioner_number, String specialization, double ratings)
    {
        this.doctor_id = doctor_id;
        this.name_prefix = name_prefix;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.full_name = full_name;
        this.avatar_url = avatar_url;
        this.practitioner_number = practitioner_number;
        this.specialization = specialization;
        this.ratings = ratings;
    }


    public void setDoctorID(int doctor_id)
    {
        this.doctor_id = doctor_id;
    }

    public int getDoctorID()
    {
        return this.doctor_id;
    }


    public void setPractitionerNumber(int practitioner_number)
    {
        this.practitioner_number = practitioner_number;
    }

    public int getPractitionerNumber()
    {
        return this.practitioner_number;
    }


    public void setNamePrefix(String name_prefix)
    {
        this.name_prefix = name_prefix;
    }

    public void setFirstName(String first_name)
    {
        this.first_name = first_name;
    }

    public String getNamePrefix()
    {
        return this.name_prefix;
    }

    public String getFirstName()
    {
        return this.first_name;
    }

    public void setMiddleName(String middle_name)
    {
        this.middle_name = middle_name;
    }

    public String getMiddleName()
    {
        return this.middle_name;
    }


    public void setLastName(String last_name)
    {
        this.last_name = last_name;
    }

    public String getLastName()
    {
        return this.last_name;
    }


    public void setAvatarURL(String avatar_url)
    {
        this.avatar_url = avatar_url;
    }

    public String getAvatarURL()
    {
        return this.avatar_url;
    }

    public String getFullName()
    {
        return this.full_name;
    }

    public void setFullName(String full_name)
    {
        this.full_name = full_name;
    }

    public String getSpecialization()
    {
        return this.specialization;
    }

    public void setSpecialization(String specialization)
    {
        this.specialization = specialization;
    }

    public double getRatings()
    {
        return this.ratings;
    }

    public void setRatings(double ratings)
    {
        this.ratings = ratings;
    }
}