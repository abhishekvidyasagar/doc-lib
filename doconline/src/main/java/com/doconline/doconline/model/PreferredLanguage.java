package com.doconline.doconline.model;

import com.doconline.doconline.app.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.doconline.doconline.app.Constants.KEY_LANGUAGE_PREFERENCES;

/**
 * Created by chiranjitbardhan on 31/10/17.
 */
public class PreferredLanguage
{
    private int id;
    private int status;
    private int preference;
    private String language;
    private boolean read_only, is_enabled = true;


    public PreferredLanguage()
    {

    }

    public PreferredLanguage(int id, int status)
    {
        this.id = id;
        this.status = status;
    }

    public PreferredLanguage(int id, String language, int status, boolean read_only)
    {
        this.id = id;
        this.language = language;
        this.status = status;
        this.read_only = read_only;
    }

    public int getID()
    {
        return id;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getPreference()
    {
        return preference;
    }

    public void setPreference(int preference)
    {
        this.preference = preference;
    }

    public boolean getReadOnly()
    {
        return read_only;
    }

    public void setReadOnly(boolean read_only)
    {
        this.read_only = read_only;
    }

    public boolean getIsEnabled()
    {
        return is_enabled;
    }

    public void setIsEnabled(boolean is_enabled)
    {
        this.is_enabled = is_enabled;
    }


    public static String getLanguageName()
    {
        List<PreferredLanguage> languages = MyApplication.getInstance().getSQLiteHelper().getAllLanguages(true);

        try
        {
            for(PreferredLanguage p: languages)
            {
                if(p.getStatus() == 1)
                {
                    return p.getLanguage();
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "Select Language";
    }


    public static String getDefaultLanguageName()
    {
        List<PreferredLanguage> languages = MyApplication.getInstance().getSQLiteHelper().getAllLanguages(false);

        try
        {
            for(PreferredLanguage p: languages)
            {
                if(p.getReadOnly())
                {
                    return p.getLanguage();
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "English";
    }


    public static String composeLanguageJSON(JSONArray json_value)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put(KEY_LANGUAGE_PREFERENCES, json_value);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json.toString();
    }
}