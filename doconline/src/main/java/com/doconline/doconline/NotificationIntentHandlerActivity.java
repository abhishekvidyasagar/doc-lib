package com.doconline.doconline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.doconline.doconline.tokbox.CallingActivity;

/**
 * Created by chiranjit on 18/05/17.
 */

public class NotificationIntentHandlerActivity extends Activity
{
    public static void startForCall(final Context context, final String data, final boolean is_outgoing_call)
    {
        final Intent intent = new Intent(context, NotificationIntentHandlerActivity.class);
        intent.putExtra("json_data", data);
        intent.putExtra("is_outgoing_call", is_outgoing_call);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("DOCONLINE", "onCreate");
        setIntent(getIntent());
    }


    @Override
    public void onNewIntent(final Intent intent)
    {
        super.onNewIntent(intent);
        Log.d("DOCONLINE", "onNewIntent");
        setIntent(intent);
        processIntents();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("DOCONLINE", "onResume");
        processIntents();
    }


    private void processIntents()
    {
        Log.d("DOCONLINE", "processIntents");

        final Intent currentIntent = getIntent();

        if (currentIntent != null)
        {
            CallingActivity.start(this, currentIntent.getStringExtra("json_data"), currentIntent.getBooleanExtra("is_outgoing_call", false));
        }

        finish();
    }
}