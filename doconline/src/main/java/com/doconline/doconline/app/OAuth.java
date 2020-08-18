package com.doconline.doconline.app;

import org.json.JSONException;
import org.json.JSONObject;

import static com.doconline.doconline.app.Constants.KEY_LAST_AUTHENTICATED_TIME;

/**
 * Created by chiranjit on 12/05/17.
 */

public class OAuth
{
    private String token_type, access_token, refresh_token, client_secret;
    private long client_id, expires_in;

    public OAuth() {

    }


    public OAuth(long client_id, String client_secret)
    {
        this.client_id = client_id;
        this.client_secret = client_secret;
    }


    public OAuth(String token_type, String access_token, String refresh_token, long expires_in)
    {
        this.token_type = token_type;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
    }


    public String getTokenType()
    {
        return this.token_type;
    }

    public String getAccessToken()
    {
        return this.access_token;
    }

    public String getRefreshToken()
    {
        return this.refresh_token;
    }

    public long getExpiresIn()
    {
        return this.expires_in;
    }


    public static String getOAuthHeader(String access_token)
    {
        return /*"Bearer " +*/ access_token;
    }


    public void setOAuthClientID(int client_id)
    {
        this.client_id = client_id;
    }

    public long getOAuthClientID()
    {
        return this.client_id;
    }


    public String getOAuthClientSecret()
    {
        return this.client_secret;
    }

    public void getOAuthClientSecret(String client_secret)
    {
        this.client_secret = client_secret;
    }


    public static OAuth saveOAuthCredential(JSONObject json)
    {
        OAuth oAuth = new OAuth();

        try
        {
            String token_type = json.getString(Constants.KEY_TOKEN_TYPE);
            int expires_in = json.getInt(Constants.KEY_EXPIRES_IN);
            String access_token = json.getString(Constants.KEY_ACCESS_TOKEN);
            String refresh_token = json.has(Constants.KEY_REFRESH_TOKEN) ? json.getString(Constants.KEY_REFRESH_TOKEN) : "";

            MyApplication.getInstance().getSession().putOAuthDetails(new OAuth(token_type, access_token, refresh_token, expires_in));
            MyApplication.prefs.edit().putLong(KEY_LAST_AUTHENTICATED_TIME, System.currentTimeMillis()).apply();
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return oAuth;
    }
}