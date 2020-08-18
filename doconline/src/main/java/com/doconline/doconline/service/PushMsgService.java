package com.doconline.doconline.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.doconline.doconline.R;
import com.doconline.doconline.SplashScreenActivity;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.tokbox.service.IncomingCallReceiver;
import com.doconline.doconline.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import static com.doconline.doconline.app.Constants.KEY_NOTIFICATION_TYPE_BOOKING_FAILED;
import static com.doconline.doconline.app.Constants.KEY_NOTIFICATION_TYPE_BOOKING_SUCCESS;
import static com.doconline.doconline.app.Constants.KEY_NOTIFICATION_TYPE_THREAD_CLOSE;
import static com.doconline.doconline.app.MyApplication.prefs;

/**
 * This class is designed to receive downstream message
 */
public class PushMsgService extends FirebaseMessagingService
{

    private static final String TAG = "PushMsgService";

    /**
     * Call this method when message arrived
     * @param remoteMessage Pass downstream data
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.wtf(TAG, "From: " + remoteMessage.getFrom());

        Log.e("AAA","inreciver"+remoteMessage);
        Log.e("AAA","in receiver"+remoteMessage.getData());
        /**
         * Check if message contains a data payload.
         */
        if (remoteMessage.getData().size() > 0)
        {
            Log.wtf(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> map = remoteMessage.getData();

            if(map.containsKey(Constants.KEY_NOTIFICATION_TYPE))
            {
                if(map.get(Constants.KEY_NOTIFICATION_TYPE).equalsIgnoreCase("CALL"))
                {
                    Log.e("AAA","inreciver 1");
                    String json_data = new JSONObject(map).toString();
                    Log.v(TAG, json_data);

                    Intent intent = new Intent();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        intent.setClass(this, IncomingCallReceiver.class);
                    }

                    intent.setAction("cb.doconline.VOIP");
                    intent.putExtra("json_data", json_data);
                    sendBroadcast(intent);

                    /**
                     * Send broadcast for badge count
                     */
                    sendBroadcast(new Intent(Constants.KEY_PUSH_BROADCAST_RECEIVER));
                }


                else if(map.get(Constants.KEY_NOTIFICATION_TYPE).equalsIgnoreCase("DATA"))
                {
                    Log.e("AAA","inreciver 2");
                    String json_data = new JSONObject(map).toString();

                    int y = map.get(Constants.KEY_TYPE).lastIndexOf("\\") + 1;
                    String type = map.get(Constants.KEY_TYPE).substring(y);

                    Log.wtf(TAG, type);

                    if(type.equalsIgnoreCase(KEY_NOTIFICATION_TYPE_BOOKING_FAILED) || type.equalsIgnoreCase(KEY_NOTIFICATION_TYPE_BOOKING_SUCCESS))
                    {
                        Log.wtf(TAG, "Inside If");

                        Intent intent = new Intent();
                        intent.setAction(Constants.KEY_PUSH_INSTANT_BOOKING_BROADCAST_RECEIVER);
                        intent.putExtra("json_data", json_data);
                        sendBroadcast(intent);

                        if(prefs.getBoolean(Constants.IS_IN_BACKGROUND, false))
                        {
                            NotificationUtils.notify(MyApplication.getInstance(), remoteMessage);
                            Log.wtf(TAG, "App is in background");
                        }

                        else
                        {
                            Log.wtf(TAG, "App is in forground");
                        }
                    }

                    else
                    {
                        Log.wtf(TAG, "Inside Else");
                        NotificationUtils.notify(MyApplication.getInstance(), remoteMessage);
                    }
                }
                else if(map.get(Constants.KEY_NOTIFICATION_TYPE).equalsIgnoreCase("Blogs")) {
                    Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("notification_type", "Blogs");
                    intent.putExtra("blog_url", map.get("blog_url"));
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    String channelId = "Default";
                    NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);;
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                        manager.createNotificationChannel(channel);
                    }
                    manager.notify(0, builder.build());
                }
                else
                {
                    Log.e("AAA","inreciver 3");
                    String json_data = new JSONObject(map).toString();

                    int y = map.get(Constants.KEY_NOTIFICATION_TYPE).lastIndexOf("\\") + 1;
                    String type = map.get(Constants.KEY_NOTIFICATION_TYPE).substring(y);

                    if(type.equalsIgnoreCase(KEY_NOTIFICATION_TYPE_THREAD_CLOSE))
                    {
                        Intent intent = new Intent();
                        intent.setAction(Constants.KEY_CHAT_SESSION_DISCONNECT_BROADCAST_RECEIVER);
                        intent.putExtra("json_data", json_data);
                        sendBroadcast(intent);
                    }

                    else
                    {
                        NotificationUtils.notify(MyApplication.getInstance(), remoteMessage);
                    }
                }
            }
        }


        /**
         * Check if message contains a notification payload.
         */
        /*if (remoteMessage.getNotification() != null)
        {
            Log.wtf(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.wtf(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.wtf(TAG, "Message Notification Click Action: " + remoteMessage.getNotification().getClickAction());
            Log.wtf(TAG, "Message Notification Icon: " + remoteMessage.getNotification().getIcon());

            Log.wtf(TAG, "Message Notification Icon 2: " + remoteMessage.getData());

            if(!prefs.getBoolean(Constants.IS_IN_BACKGROUND, false))
            {
                NotificationUtils utils = new NotificationUtils(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), "ic_launcher");
                NotificationUtils.notify(MyApplication.getInstance(), utils);
            }
        }*/
    }
}