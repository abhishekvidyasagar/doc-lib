package com.doconline.doconline.api;

import android.content.Context;
import android.view.View;

import com.doconline.doconline.alert.CustomAlertDialog;
import com.doconline.doconline.helper.OnDialogAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static com.doconline.doconline.api.HttpClient.ACCEPTED;
import static com.doconline.doconline.api.HttpClient.BAD_GATEWAY;
import static com.doconline.doconline.api.HttpClient.BAD_REQUEST;
import static com.doconline.doconline.api.HttpClient.CREATED;
import static com.doconline.doconline.api.HttpClient.FORBIDDEN;
import static com.doconline.doconline.api.HttpClient.GATEWAY_TIMEOUT;
import static com.doconline.doconline.api.HttpClient.INTERNAL_ERROR;
import static com.doconline.doconline.api.HttpClient.METHOD_NOT_FOUND;
import static com.doconline.doconline.api.HttpClient.NOT_FOUND;
import static com.doconline.doconline.api.HttpClient.NOT_IMPLEMENTED;
import static com.doconline.doconline.api.HttpClient.NO_RESPONSE;
import static com.doconline.doconline.api.HttpClient.OK;
import static com.doconline.doconline.api.HttpClient.REQUEST_LIMIT_EXCEED;
import static com.doconline.doconline.api.HttpClient.RESOURCE_LOCKED;
import static com.doconline.doconline.api.HttpClient.SERVICE_NOT_AVAILABLE;
import static com.doconline.doconline.api.HttpClient.UNAUTHORIZED;
import static com.doconline.doconline.api.HttpClient.UNPROCESSABLE_ENTITY;
import static com.doconline.doconline.app.Constants.KEY_DATA;
import static com.doconline.doconline.app.Constants.KEY_ERRORS;
import static com.doconline.doconline.app.Constants.KEY_MESSAGE;

/**
 * Created by chiranjitbardhan on 09/01/18.
 */

public class HttpResponseHandler
{
    private CustomAlertDialog dialog;

    public HttpResponseHandler(Context context, OnDialogAction listener, View view)
    {
        this.dialog = new CustomAlertDialog(context, listener, view);
    }


    public String handle(int responseCode, String responseData)
    {
        switch (responseCode)
        {
            /**
             * 2xx
             */
            case OK: //200

                return "OK";

            case CREATED: //201

                return "CREATED";

            case ACCEPTED: //202

                return "ACCEPTED";

            case NO_RESPONSE: //204

                return "NO RESPONSE";

            /**
             * 4xx
             */
            case BAD_REQUEST: //400

                {
                    String message = getMessage(responseData);
                    this.dialog.showSnackbar(message, CustomAlertDialog.LENGTH_LONG);
                }

                return "BAD REQUEST";

            case UNAUTHORIZED: //401

                this.dialog.unauthorized("UNAUTHORIZED", "You are not authorized to access DocOnline app. Please login again", "Log In", false);
                return "UNAUTHORIZED";

            case FORBIDDEN: //403

                {
                    String message = getMessage(responseData);
                    this.dialog.showAlertDialogWithPositiveAction("INFO", message, "OK", false);
                }

                return "FORBIDDEN";

            case NOT_FOUND: //404

                this.dialog.showSnackbarWithAction("Resource Not Found", CustomAlertDialog.LENGTH_LONG);
                return "RESOURCE NOT FOUND";

            case METHOD_NOT_FOUND: //405

                this.dialog.showSnackbarWithAction("Method Not Found", CustomAlertDialog.LENGTH_LONG);
                return "METHOD NOT FOUND";

            case UNPROCESSABLE_ENTITY: //422

                {
                    String message = getValidationErrorMessage(responseData);
                    this.dialog.showSnackbar(message, CustomAlertDialog.LENGTH_LONG);
                }

                return "UN PROCESSABLE ENTITY";

            case RESOURCE_LOCKED: //423

                {
                    String message = this.getDataMessage(responseData);
                    this.dialog.showSnackbarWithAction(message, CustomAlertDialog.LENGTH_LONG);
                }

                return "RESOURCE LOCKED";

            case REQUEST_LIMIT_EXCEED: //429

                this.dialog.showSnackbarWithAction("Request Limit Exceed. Try Later", CustomAlertDialog.LENGTH_INDEFINITE);
                return "TOO MANY REQUEST";

            /**
             * 5xx
             */
            case INTERNAL_ERROR: //500

                this.dialog.showSnackbarWithAction("Internal Server Error", CustomAlertDialog.LENGTH_INDEFINITE);
                return "INTERNAL SERVER ERROR";

            case NOT_IMPLEMENTED: //501

                this.dialog.showSnackbarWithAction("Method Not Implemented", CustomAlertDialog.LENGTH_INDEFINITE);
                return "NOT IMPLEMENTED";

            case BAD_GATEWAY: //502

                this.dialog.showSnackbarWithAction("Bad Gateway", CustomAlertDialog.LENGTH_INDEFINITE);
                return "BAD GATEWAY";

            case SERVICE_NOT_AVAILABLE: //503

                this.dialog.showSnackbarWithAction("Application Under Maintenance", CustomAlertDialog.LENGTH_INDEFINITE);
                return "SERVICE NOT AVAILABLE";

            case GATEWAY_TIMEOUT: //504

                this.dialog.showSnackbarWithAction("Gateway Timeout", CustomAlertDialog.LENGTH_INDEFINITE);
                return "GATEWAY TIMEOUT";

            default:

                this.dialog.showSnackbarWithAction("Oops.. Something Went Wrong", CustomAlertDialog.LENGTH_LONG);
                return "SOMETHING WENT WRONG";
        }
    }


    public static String getValidationErrorMessage(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);

            if (json.has("message")){
                return String.valueOf(json.get("message"));
            }
            json = json.getJSONObject(KEY_DATA);

            json = json.getJSONObject(KEY_ERRORS);

            Iterator<String> keys= json.keys();

            if (keys.hasNext())
            {
                String key = keys.next();
                String value = json.getString(key);

                JSONArray array = new JSONArray(value);

                if(array.length() > 0)
                {
                    return array.getString(0);
                }
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return "Oops.. Something Went Wrong";
    }


    private String getDataMessage(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);
            json = json.getJSONObject(KEY_DATA);

            return (json.isNull(KEY_MESSAGE)) ? "Oops.. Something Went Wrong" : json.getString(KEY_MESSAGE);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return "Oops.. Something Went Wrong";
    }


    public static String getMessage(String json_data)
    {
        try
        {
            JSONObject json = new JSONObject(json_data);
            return (json.isNull(KEY_MESSAGE)) ? "Oops.. Something Went Wrong" : json.getString(KEY_MESSAGE);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return "Oops.. Something Went Wrong";
    }
}