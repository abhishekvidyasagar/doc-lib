package com.doconline.doconline.api;

import android.os.AsyncTask;
import android.util.Log;

import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.HashMap;
import java.util.Map;

import static com.doconline.doconline.app.Constants.ACCEPT;
import static com.doconline.doconline.app.Constants.AUTHORIZATION;
import static com.doconline.doconline.app.Constants.CONTENT_TYPE;
import static com.doconline.doconline.app.Constants.DOCONLINE_API;
import static com.doconline.doconline.app.Constants.DOCONLINE_API_VERSION;
import static com.doconline.doconline.app.Constants.KEY_CATEGORY_ID;
import static com.doconline.doconline.app.Constants.KEY_SESSION_ID;

/**
 * Created by chiranjitbardhan on 12/01/18.
 */

public class MultipartFileUploadClient extends AsyncTask<String, String, String>
{
    private int categoryId;
    private String uploadId;
    private boolean isNotify;
    private HashMap<String, Object> fileData;
    private HashMap<String, Object> formData = new HashMap<>();


    public MultipartFileUploadClient(String uploadId, HashMap<String, Object> fileData, boolean isNotify)
    {
        this.uploadId = uploadId;
        this.fileData = fileData;
        this.isNotify = isNotify;
    }

    public MultipartFileUploadClient(String uploadId, HashMap<String, Object> fileData, HashMap<String, Object> formData, boolean isNotify)
    {
        this.uploadId = uploadId;
        this.fileData = fileData;
        this.formData = formData;
        this.isNotify = isNotify;
    }

    public MultipartFileUploadClient(String uploadId, int categoryId, HashMap<String, Object> fileData, HashMap<String, Object> formData, boolean isNotify)
    {
        this.uploadId = uploadId;
        this.categoryId = categoryId;
        this.fileData = fileData;
        this.formData = formData;
        this.isNotify = isNotify;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            MultipartUploadRequest m = new MultipartUploadRequest(MyApplication.getInstance(), uploadId, params[0]);

            for(Map.Entry map: fileData.entrySet())
            {
                m.addFileToUpload(map.getValue().toString(), map.getKey().toString());
                Log.i("FORM_DATA", map.getValue().toString() + "-" + map.getKey().toString());
            }

            for(Map.Entry map: formData.entrySet())
            {
                m.addParameter(map.getKey().toString(), map.getValue().toString());
                Log.i("FORM_DATA", map.getValue().toString() + "-" + map.getKey().toString());
            }

            if(categoryId != 0)
            {
                m.addParameter(KEY_CATEGORY_ID, String.valueOf(categoryId));
            }

            String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());

            if(!header.isEmpty())
            {
                m.addHeader(AUTHORIZATION, "Bearer " + header);
            }

            m.addHeader(CONTENT_TYPE, "application/json");
            m.addHeader(ACCEPT, "application/json");
            m.addHeader(DOCONLINE_API, DOCONLINE_API_VERSION);

            if(!MyApplication.sSession.isEmpty())
            {
                m.addHeader(KEY_SESSION_ID, MyApplication.sSession);
            }

            if(isNotify)
            {
                m.setNotificationConfig(new UploadNotificationConfig());
            }

            m.setMaxRetries(5);
            m.startUpload();
        }

        catch (Exception exc)
        {
            //Toast.makeText(MyApplication.getInstance(), String.valueOf(exc.getMessage()), Toast.LENGTH_LONG).show();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String response)
    {
        super.onPostExecute(response);
    }
}