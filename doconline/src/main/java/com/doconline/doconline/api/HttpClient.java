package com.doconline.doconline.api;

import android.os.AsyncTask;
import android.util.Log;

import com.doconline.doconline.BuildConfig;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.app.OAuth;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.doconline.doconline.app.Constants.ACCEPT;
import static com.doconline.doconline.app.Constants.AUTHORIZATION;
import static com.doconline.doconline.app.Constants.CONTENT_TYPE;
import static com.doconline.doconline.app.Constants.DOCONLINE_API;
import static com.doconline.doconline.app.Constants.DOCONLINE_API_VERSION;
import static com.doconline.doconline.app.Constants.KEY_SESSION_ID;
import static com.doconline.doconline.consultation.guest.BookGuestConsultationActivity.HTTP_REQUEST_CODE_BOOK_APPOINTMENT;
import static com.doconline.doconline.consultation.guest.BookGuestConsultationActivity.HTTP_REQUEST_CODE_UPDATE_PAYMENT;
import static com.doconline.doconline.consultation.guest.BookGuestConsultationActivity.HTTP_REQUEST_CODE_VALIDATE_APPOINTMENT;

/**
 * Created by chiranjitbardhan on 03/01/18.
 */
public class HttpClient extends AsyncTask<String, String, String> {
    private final String LOG_TAG = HttpClient.class.getSimpleName();

    /**
     * 2xx
     */
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    public static final int PARTIAL_INFO = 203;
    public static final int NO_RESPONSE = 204;

    /**
     * 3xx
     */
    public static final int MOVED = 301;
    public static final int FOUND = 302;
    public static final int METHOD = 303;
    public static final int NOT_MODIFIED = 304;

    /**
     * 4xx
     */
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int PAYMENT_REQUIRED = 402;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_FOUND = 405;
    public static final int RESOURCE_NOT_AVAILABLE = 410;
    public static final int PRECONDITION_FAILED = 412;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int RESOURCE_LOCKED = 423;
    public static final int REQUEST_LIMIT_EXCEED = 429;

    /**
     * 5xx
     */
    public static final int INTERNAL_ERROR = 500;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_NOT_AVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;

    private OkHttpClient client = MyOkHttpClient.getOkHttpClient();

    private int responseCode, requestCode, httpMethod;
    private boolean isRawData = false;
    private String rawJSON;
    private HashMap<String, Object> formData = new HashMap<>();
    private OnHttpResponse listener;


    public HttpClient(int requestCode, int httpMethod, OnHttpResponse listener) {
        this.requestCode = requestCode;
        this.httpMethod = httpMethod;
        this.listener = listener;
    }

    public HttpClient(int requestCode, int httpMethod, boolean isRawData, String rawJSON, OnHttpResponse listener) {
        this.requestCode = requestCode;
        this.httpMethod = httpMethod;
        this.isRawData = isRawData;
        this.rawJSON = rawJSON;
        this.listener = listener;
    }

    public HttpClient(int requestCode, int httpMethod, HashMap<String, Object> formData, OnHttpResponse listener) {
        this.requestCode = requestCode;
        this.httpMethod = httpMethod;
        this.formData = formData;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(params[0]);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "URL :" + params[0]);
        }

        if (MyApplication.HTTPMethod.GET.getValue() == httpMethod) {
            builder.get();
        }

        if (MyApplication.HTTPMethod.POST.getValue() == httpMethod) {
            if (!isRawData) {
                builder.post(getRequestBody(formData));
            } else {
                builder.post(getRequestBody(rawJSON));
            }
        }

        if (MyApplication.HTTPMethod.PUT.getValue() == httpMethod) {
            if (!isRawData) {
                builder.put(getRequestBody(formData));
            } else {
                builder.put(getRequestBody(rawJSON));
            }
        }

        if (MyApplication.HTTPMethod.DELETE.getValue() == httpMethod) {
            builder.delete();
        }

        if (MyApplication.HTTPMethod.BDELETE.getValue() == httpMethod) {
            if (isRawData) {
                builder.delete(getRequestBody(rawJSON));
            }
        }

        if (MyApplication.HTTPMethod.PATCH.getValue() == httpMethod) {
            if (!isRawData) {
                builder.patch(getRequestBody(formData));
            } else {
                builder.patch(getRequestBody(rawJSON));
            }
        }

        String header = OAuth.getOAuthHeader(MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
        Log.e("AAA","auth token "+ MyApplication.getInstance().getSession().getOAuthDetails().getAccessToken());
        if (!header.isEmpty()) {
            builder.addHeader(AUTHORIZATION, "Bearer " + header);
        }

        builder.addHeader(ACCEPT, "application/json");
        builder.addHeader(CONTENT_TYPE, "application/json");
        builder.addHeader(DOCONLINE_API, DOCONLINE_API_VERSION);

        if (requestCode == HTTP_REQUEST_CODE_BOOK_APPOINTMENT
                || requestCode == HTTP_REQUEST_CODE_UPDATE_PAYMENT
                || requestCode == HTTP_REQUEST_CODE_VALIDATE_APPOINTMENT) {
            if (!MyApplication.sSession.isEmpty()) {
                builder.addHeader(KEY_SESSION_ID, MyApplication.sSession);
            }
        }

        okhttp3.Request request = builder.build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            responseCode = response.code();

            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "RESPONSE DATA :" + response);
            Log.d(LOG_TAG, "RESPONSE CODE :" + responseCode);
        }

        listener.onPostExecute(requestCode, responseCode, String.valueOf(response));
    }

    private RequestBody getRequestBody(HashMap<String, Object> formData) {
        FormBody.Builder formBuilder = new FormBody.Builder();

        for (Map.Entry m : formData.entrySet()) {
            formBuilder.add(m.getKey().toString(), m.getValue().toString());
        }

        /*RequestBody body = new FormBody.Builder()
                        .add("KEY", "VALUE")
                        .build();*/

        return formBuilder.build();
    }


    private RequestBody getRequestBody(String rawJSON) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(JSON, rawJSON);
    }
}