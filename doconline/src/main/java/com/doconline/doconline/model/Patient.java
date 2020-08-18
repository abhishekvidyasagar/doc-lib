package com.doconline.doconline.model;

import java.io.Serializable;

/**
 * Created by chiranjit on 16/05/17.
 */

public class Patient extends User implements Serializable
{

    public Patient()
    {

    }

    public Patient(String mFullName)
    {
        super(mFullName);
    }

    public Patient(int mPatientId, String mFullName, String mProfilePic)
    {
        super(mPatientId, mFullName, mProfilePic);
    }

    public Patient(int mPatientId, String mFullName, String mAge, String mProfilePic)
    {
        super(mPatientId, mFullName, mAge, mProfilePic);
    }

    public Patient(int mPatientId, String mFirstName, String mGender, String mDateOfBirth, String mProfilePic)
    {
        super(mPatientId, mFirstName, mGender, mDateOfBirth, mProfilePic);
    }

    public Patient(int mPatientId, String mFirstName)
    {
        super(mPatientId, mFirstName);
    }

    public Patient(int mPatientId, String mFullName, String mGender, String mDateOfBirth, String mAge,String mEmail, String mProfilePic)
    {
        super(mPatientId, mFullName, mGender, mDateOfBirth, mAge, mEmail, mProfilePic);
    }

}