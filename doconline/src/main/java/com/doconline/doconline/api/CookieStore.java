package com.doconline.doconline.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by chiranjitbardhan on 14/02/18.
 */

public class CookieStore implements CookieJar
{
    private final String LOG_TAG = CookieStore.class.getSimpleName();

    private final Set<Cookie> cookieStore = new HashSet<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        /**
         * Saves cookies from HTTP response
         * If the response includes a trailer this method is called second time
         */

        cookieStore.addAll(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url)
    {
        /**
         * Load cookies from the jar for an HTTP request.
         * This method returns cookies that have not yet expired
         */
        List<Cookie> validCookies = new ArrayList<>();

        for (Cookie cookie : cookieStore)
        {
            LogCookie(cookie);

            if (cookie.expiresAt() < System.currentTimeMillis())
            {
                // invalid cookies
            }

            else
            {
                validCookies.add(cookie);
            }
        }

        return validCookies;
    }

    private void LogCookie(Cookie cookie)
    {
        Log.i(LOG_TAG, "String: " + cookie.toString());
        Log.i(LOG_TAG, "Expires: " + cookie.expiresAt());
        Log.i(LOG_TAG, "Hash: " + cookie.hashCode());
        Log.i(LOG_TAG, "Path: " + cookie.path());
        Log.i(LOG_TAG, "Domain: " + cookie.domain());
        Log.i(LOG_TAG, "Name: " + cookie.name());
        Log.i(LOG_TAG, "Value: " + cookie.value());
    }
}