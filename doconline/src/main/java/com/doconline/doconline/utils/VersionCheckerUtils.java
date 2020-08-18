package com.doconline.doconline.utils;

import android.os.AsyncTask;

import org.jsoup.Jsoup;

import static com.doconline.doconline.app.Constants.PLAYSTORE_URL;

/**
 * Created by cbug on 7/10/17.
 */

public class VersionCheckerUtils extends AsyncTask<String, String, String>
{

    private String newVersion = "";

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            /*newVersion = Jsoup.connect(PLAYSTORE_URL + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();*/

            newVersion = Jsoup.connect(PLAYSTORE_URL + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(3)
                    .ownText();

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return newVersion;
    }
}