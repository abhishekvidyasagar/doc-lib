package com.doconline.doconline.model;

import java.io.Serializable;
import java.util.HashMap;

import static com.doconline.doconline.app.Constants.KEY_NAME;

/**
 * Created by chiranjit on 27/04/17.
 */
public class Allergy extends Disease implements Serializable
{
    public Allergy()
    {

    }

    public Allergy(int id, String disease)
    {
        this.id = id;
        this.disease = disease;
    }


    public static HashMap<String, Object> composeAllergyJSON(String disease)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_NAME, disease);

        return hashMap;
    }
}