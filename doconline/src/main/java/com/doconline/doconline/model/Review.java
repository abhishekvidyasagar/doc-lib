package com.doconline.doconline.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.doconline.doconline.app.Constants.KEY_RATING;

/**
 * Created by chiranjit on 13/12/16.
 */
public class Review implements Serializable
{
    public double rating;
    public String title, description;

    public Review(double rating, String title, String description)
    {
        this.rating = rating;
        this.title = title;
        this.description = description;
    }


    public static String getRatingJSON(double rating)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put(KEY_RATING, rating);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }
}