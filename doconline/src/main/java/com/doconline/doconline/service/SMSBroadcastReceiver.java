package com.doconline.doconline.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.doconline.doconline.app.Constants;

/**
 * Created by chiranjitbardhan on 03/08/17.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        /**
         * Retrieves a map of extended data from the intent.
         */
        final Bundle bundle = intent.getExtras();

        try
        {
            if (bundle != null)
            {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++)
                {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    //Log.wtf("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                    //Toast.makeText(context, "senderNum: "+ senderNum + ", message: " + message, Toast.LENGTH_LONG).show();
                    //Log.v("OTP_MESSAGE", senderNum);

                    if (senderNum.length() > 6)
                    {
                        //Log.v("OTP_MESSAGE", senderNum.substring(senderNum.length() - 6));

                        if(senderNum.substring(senderNum.length() - 6).equalsIgnoreCase("DOCONL"))
                        {
                            Intent in = new Intent(Constants.SMS_BROADCAST);
                            in.putExtra(Constants.VERIFICATION_CODE, message);
                            context.sendBroadcast(in);
                        }
                    }
                }
            }
        }

        catch (Exception e)
        {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }
}