package com.doconline.doconline.model;

import java.io.Serializable;

/**
 * Created by chiranjit on 17/04/17.
 */

public class Disease implements Serializable
{
    public int id, status;
    public String disease, created_at;


    public Disease()
    {

    }

    public Disease(int id, String disease)
    {
        this.id = id;
        this.disease = disease;
    }


    public void setID(int id)
    {
        this.id = id;
    }

    public void setDisease(String disease)
    {
        this.disease = disease;
    }

    public void setCreatedAt(String created_at)
    {
        this.created_at = created_at;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }
}