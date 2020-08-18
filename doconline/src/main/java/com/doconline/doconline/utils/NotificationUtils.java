package com.doconline.doconline.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.doconline.doconline.R;
import com.doconline.doconline.app.Constants;
import com.doconline.doconline.service.ActionButtonReceiver;
import com.doconline.doconline.service.NotificationActionReceiver;
import com.doconline.doconline.service.NotificationCancelReceiver;
import com.doconline.doconline.service.NotificationDeleteReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.doconline.doconline.app.MyApplication.getInstance;
import static com.doconline.doconline.app.MyApplication.prefs;

/**
 * This class is designed to initialize notification object
 * Create notification style based on downstream message
 * Notify User
 * Send notification user behaviour to the server
 */

public class NotificationUtils
{

    private static String TAG = "NotificationUtils";

    /**
     * Declare NotificationUtils class variables
     */
    private static int count;
    public int notification_id, notifiable_id, read_status;
    public String action;
    public String id, type, notifiable_type, title, body, link, timestamp;
    private String color, icon, deep_link;
    private String big_title, big_text, summary_text, large_icon, remote_picture, data;
    public String action_name, read_at, created_at, updated_at;
    private boolean vibrate;

    private static boolean notification_sound, notification_vibrate;

    /**
     * Array list to store notification action buttons
     */
    private List<NotificationUtils> action_list = new ArrayList<>();

    public NotificationUtils()
    {

    }

    public NotificationUtils(String title, String body, String icon)
    {
        this.title = title;
        this.body = body;
        this.icon = icon;
    }

    public NotificationUtils(String id, String type, int notifiable_id, String notifiable_type,
                             String body, String read_at, String created_at, String updated_at)
    {
        this.id = id;
        this.type = type;
        this.notifiable_id = notifiable_id;
        this.notifiable_type = notifiable_type;
        this.body = body;
        this.read_at = read_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public NotificationUtils(String title, String body, String color, String icon, boolean vibrate, String deep_link)
    {
        this.title = title;
        this.body = body;
        this.color = color;
        this.icon = icon;
        this.vibrate = vibrate;
        this.deep_link = deep_link;
    }


    /**
     * Call constructor
     * @param title - Notification title
     * @param body - Notification body
     * @param color - Notification icon background color
     * @param icon - Notification small icon
     * @param vibrate - Notification vibration
     * @param deep_link - Notification action link to activity
     * @param data - Pass data to activity on click
     * @param large_icon - Notification large icon URL/Resource
     * @param action_list - Notification action button array list
     */
    private NotificationUtils(String title, String body, String color, String icon, boolean vibrate,
                              String deep_link, String data, String large_icon, List<NotificationUtils> action_list)
    {
        this.title          = title;
        this.body           = body;
        this.color          = color;
        this.icon           = icon;
        this.vibrate        = vibrate;
        this.deep_link      = deep_link;
        this.data           = data;
        this.large_icon     = large_icon;
        this.action_list    = action_list;
    }

    /**
     * Call constructor
     * @param title - Notification title
     * @param body - Notification body
     * @param color - Notification icon background color
     * @param icon - Notification small icon
     * @param vibrate - Notification vibration
     * @param deep_link - Notification action link to activity
     * @param data - Pass data to activity on click
     * @param large_icon - Notification large icon URL/Resource
     * @param big_title - Notification big title
     * @param big_text - Big notification text
     * @param summary_text - Notification summery text
     * @param remote_picture - Picture notification URL/Resource
     * @param action_list - Notification action button array list
     */
    private NotificationUtils(String title, String body, String color, String icon, boolean vibrate,
                              String deep_link, String data, String large_icon, String big_title, String big_text,
                              String summary_text, String remote_picture, List<NotificationUtils> action_list)
    {
        this.title          = title;
        this.body           = body;
        this.color          = color;
        this.icon           = icon;
        this.vibrate        = vibrate;
        this.deep_link      = deep_link;
        this.data           = data;
        this.large_icon     = large_icon;
        this.big_title      = big_title;
        this.big_text       = big_text;
        this.summary_text   = summary_text;
        this.remote_picture = remote_picture;
        this.action_list    = action_list;
    }

    /**
     * Call constructor
     * @param action_name - Notification action button name
     * @param icon - Notification action button icon
     * @param deep_link - Notification action click target activity
     * @param data - Pass data to activity on action button click
     */
    private NotificationUtils(String action_name, String icon, String deep_link, String data)
    {
        this.action_name    = action_name;
        this.icon           = icon;
        this.deep_link      = deep_link;
        this.data           = data;
    }


    public static void notify(final Context mContext, final RemoteMessage remoteMessage)
    {
        notification_sound = true;
        notification_vibrate = true;
        count = 0;

        int notificationId = (int) (Math.random() * 100);

        Map<String, String> map = remoteMessage.getData();

        String color    = "#f56234";
        String icon     = "ic_notification";

        String large_icon       = map.get("large_icon");
        String action_list      = map.get("b_action");
        String deep_link        = map.get("deep_link");
        String data             = map.get("data");
        String notification_type = map.get("notification_type");

        try
        {
            if (notification_type.equalsIgnoreCase("Blogs")){
                notificationId = 99486;//this is unique for blog click action
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                NotificationUtils utils = new NotificationUtils(title, body, color, icon, true, deep_link, data, large_icon, "", "", "", "", getActionButton(action_list));
                big_picture_notification(mContext, notificationId, utils);
            }else {
                JSONObject json = new JSONObject(map);

                String type = json.getString(Constants.KEY_TYPE);
                int y = type.lastIndexOf("\\") + 1;
                type = type.substring(y);

                if(type.equalsIgnoreCase(Constants.KEY_NOTIFICATION_TYPE_BOOKING_SUCCESS))
                {
                    String title = "Booking Successful";
                    String body = "Your appointment has been booked successfully.";

                    String big_text = "Your appointment has been fixed with DocOnline. Thank You";
                    String big_title = "Appointment booked successfully";
                    String summary_text = "Appointment booked successfully";

                    int appointment_id = json.getInt(Constants.KEY_APPOINTMENT_ID);

                    if(json.has(Constants.KEY_JOB_ID))
                    {
                        String job_id = json.getString(Constants.KEY_JOB_ID);

                        if (job_id.equals(prefs.getString(Constants.KEY_JOB_ID, "UNKNOWN")))
                        {
                            NotificationUtils utils = new NotificationUtils(title, body, color, icon, true, deep_link, data, large_icon, big_title, big_text, summary_text, "", getActionButton(action_list));
                            create_booking_notification(mContext, notificationId, utils, appointment_id);
                        }
                    }

                    else
                    {
                        NotificationUtils utils = new NotificationUtils(title, body, color, icon, true, deep_link, data, large_icon, big_title, big_text, summary_text, "", getActionButton(action_list));
                        create_booking_notification(mContext, notificationId, utils, appointment_id);
                    }
                }

                else if(type.equalsIgnoreCase(Constants.KEY_NOTIFICATION_TYPE_BOOKING_FAILED))
                {
                    String title = "Booking Fail";
                    String body = String.valueOf(json.getString(Constants.KEY_ERROR));

                    if(json.has(Constants.KEY_JOB_ID))
                    {
                        String job_id = json.getString(Constants.KEY_JOB_ID);

                        if (job_id.equals(prefs.getString(Constants.KEY_JOB_ID, "UNKNOWN")))
                        {
                            NotificationUtils utils = new NotificationUtils(title, body, color, icon, true, deep_link, data, large_icon, getActionButton(action_list));
                            small_notification(mContext, notificationId, utils);
                        }
                    }

                    else
                    {
                        NotificationUtils utils = new NotificationUtils(title, body, color, icon, true, deep_link, data, large_icon, getActionButton(action_list));
                        small_notification(mContext, notificationId, utils);
                    }
                }

                else
                {
                    int appointment_id = json.getInt(Constants.KEY_APPOINTMENT_ID);

                    Log.wtf(TAG, "App ID" + appointment_id);

                    String title = String.valueOf(json.getString(Constants.KEY_TITLE));
                    String body = String.valueOf(json.getString(Constants.KEY_MESSAGE));

                    String big_text = "";
                    String big_title = "";
                    String summary_text = "";

                    NotificationUtils utils = new NotificationUtils(title, body, color, icon, true, deep_link, data, large_icon, big_title, big_text, summary_text, "", getActionButton(action_list));
                    create_booking_notification(mContext, notificationId, utils, appointment_id);

                }
            }

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param mContext - Pass application context
     * @param notificationId - Pass notification id
     * @param utils - Pass notification object
     */
    public static void small_notification(final Context mContext, final int notificationId, NotificationUtils utils)
    {
        notification_sound = true;
        notification_vibrate = true;

        /** Start activity when user taps on notification.
        * Intent gotoIntent = new Intent(this, NotificationDeleteReceiver.class);
        * gotoIntent.setClassName(mContext, "convertifier.sample.ChatActivity");
        * PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        */

        /**
         * Send to Broadcast for Notification Click
         */
        Intent gotoIntent = new Intent(mContext, NotificationActionReceiver.class);
        gotoIntent.setAction(utils.deep_link);
        gotoIntent.putExtra("data", utils.data);
        gotoIntent.putExtra("push.notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder;

        final long when = System.currentTimeMillis();

        String channelId = "channel_doconline_notification";
        String channelName = "DocOnline";

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            if(mNotificationManager != null)
            {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        notificationBuilder = new NotificationCompat.Builder(mContext, channelId);

        /**
         * Set notification property
         */
        Notification notification;
        notificationBuilder
                .setWhen(when)
                .setAutoCancel(false)
                .setContentTitle(utils.title)
                .setContentText(utils.body)
                .setContentIntent(contentIntent)
                .setNumber(count);

        /**
         * .setContentInfo("xyz")
         * .setSmallIcon(icon)
         * .setTicker("ticker")
         */

        try
        {
            /**
             * If notification object contains color
             */
            if(utils.color != null)
            {
                notificationBuilder.setColor(Color.parseColor(utils.color));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            /**
             * Check if notification contains small icon set from resource
             */
            if(utils.icon != null)
            {
                final int small_icon = getInstance().getResources().getIdentifier(utils.icon, "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }

            /**
             * Else set default notification small icon from resource
             */
            else
            {
                final int small_icon = getInstance().getResources().getIdentifier("ic_bell", "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            if(notification_sound)
            {
                //final int sound = getInstance().getResources().getIdentifier("sound", "raw", getInstance().getPackageName());
                final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //Uri uri = Uri.parse("android.resource://" + getInstance().getPackageName() + "/" + sound);
                notificationBuilder.setSound(defaultSoundUri);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /**
         * If notification contains large icon
         */
        if(utils.large_icon != null)
        {
            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.large_icon);
                Bitmap large_icon = BitmapFactory.decodeStream((InputStream) url.getContent());
                notificationBuilder.setLargeIcon(large_icon);
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int resID = getInstance().getResources().getIdentifier(utils.large_icon, "drawable", getInstance().getPackageName());
                Bitmap largeIcon = BitmapFactory.decodeResource(getInstance().getResources(), resID);
                notificationBuilder.setLargeIcon(largeIcon);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Yes intent
         * Intent yesReceive = new Intent(this, NotificationActionReceiver.class);
         * yesReceive.setAction(YES_ACTION);
         * PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Maybe intent
         * Intent maybeReceive = new Intent(this, NotificationActionReceiver.class);
         * maybeReceive.setAction(MAYBE_ACTION);
         * PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12345, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * No intent
         * Intent noReceive = new Intent(this, NotificationActionReceiver.class);
         * noReceive.setAction("convertifier.sample.SecondActivity");
         * PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * .setNumber(++count);
         * .setTicker("Reload.in")
         * .setStyle(new NotificationCompat.BigTextStyle().bigText("big text"))
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Yes", pendingIntentYes)
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Partly", pendingIntentMaybe) * .addAction(R.drawable.ic_alarm_check_black_18dp, "No", pendingIntentNo)
         * .build();
         */

        /**
         * Dynamic Action Button
         */
        for(NotificationUtils nUtils: utils.action_list)
        {
            /**
             * Send to Broadcast for Action Button Click
             */
            Intent aIntent = new Intent(mContext, ActionButtonReceiver.class);
            aIntent.setAction(nUtils.deep_link);
            aIntent.putExtra("data", nUtils.data);
            aIntent.putExtra("push.notificationId", notificationId);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, notificationId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            /**
             * Set action button icon from resource and add to notification
             */
            int resID = getInstance().getResources().getIdentifier(nUtils.icon, "drawable", getInstance().getPackageName());
            notificationBuilder.addAction(resID, nUtils.action_name, pIntent);
        }

        /**
         * Send to Broadcast for Delete
         * Add notificationBuilder.setDeleteIntent(contentIntent);
         */
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(mContext, notificationId));
        notification = notificationBuilder.build();

        /**
         * This will cancel notification on swipe.
         */
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        /**
         * Vibration is set to true vibrate default on notification
         */
        Log.v("notification_vibrate", "" + notification_vibrate );

        if(notification_vibrate)
        {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        /**
         * Finally notify user
         */
        if(mNotificationManager != null)
        {
            mNotificationManager.notify(notificationId, notification);
        }
    }


    private static void big_text_notification(final Context mContext, final int notificationId, NotificationUtils utils)
    {

        /** Start activity when user taps on notification.
         * Intent gotoIntent = new Intent(this, NotificationDeleteReceiver.class);
         * gotoIntent.setClassName(mContext, "convertifier.sample.ChatActivity");
         * PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Send to Broadcast for Notification Click
         */
        Intent gotoIntent = new Intent(mContext, NotificationActionReceiver.class);
        gotoIntent.setAction(utils.deep_link);
        gotoIntent.putExtra("data", utils.data);
        gotoIntent.putExtra("push.notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        final long when = System.currentTimeMillis();

        notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        /**
         * Set notification property
         */
        Notification notification;
        notificationBuilder
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(utils.title)
                .setContentText(utils.body)
                .setNumber(count)
                .setContentIntent(contentIntent);

        /**
         * Set notification big text style
         */
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(utils.big_text)
                .setBigContentTitle(utils.big_title)
                .setSummaryText(utils.summary_text));

        /**
         * .setContentInfo("xyz")
         * .setSmallIcon(icon)
         * .setTicker("ticker")
         */

        try
        {
            /**
             * If notification object contains color
             */
            if(utils.color != null)
            {
                notificationBuilder.setColor(Color.parseColor(utils.color));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            /**
             * Check if notification contains small icon set from resource
             */
            if(utils.icon != null)
            {
                final int small_icon = getInstance().getResources().getIdentifier(utils.icon, "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }

            /**
             * Else set default notification small icon from resource
             */
            else
            {
                final int small_icon = getInstance().getResources().getIdentifier("ic_bell", "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            if(notification_sound)
            {
                final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //Uri uri = Uri.parse("android.resource://" + getInstance().getPackageName() + "/" + R.raw.icon);
                notificationBuilder.setSound(defaultSoundUri);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /**
         * If notification contains large icon
         */
        if(utils.large_icon != null)
        {

            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.large_icon);
                Bitmap large_icon = BitmapFactory.decodeStream((InputStream) url.getContent());
                notificationBuilder.setLargeIcon(large_icon);
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int resID = getInstance().getResources().getIdentifier(utils.large_icon, "drawable", getInstance().getPackageName());
                Bitmap largeIcon = BitmapFactory.decodeResource(getInstance().getResources(), resID);
                notificationBuilder.setLargeIcon(largeIcon);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Yes intent
         * Intent yesReceive = new Intent(this, NotificationActionReceiver.class);
         * yesReceive.setAction(YES_ACTION);
         * PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Maybe intent
         * Intent maybeReceive = new Intent(this, NotificationActionReceiver.class);
         * maybeReceive.setAction(MAYBE_ACTION);
         * PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12345, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * No intent
         * Intent noReceive = new Intent(this, NotificationActionReceiver.class);
         * noReceive.setAction("convertifier.sample.SecondActivity");
         * PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * .setNumber(++count);
         * .setTicker("Reload.in")
         * .setStyle(new NotificationCompat.BigTextStyle().bigText("big text"))
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Yes", pendingIntentYes)
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Partly", pendingIntentMaybe) * .addAction(R.drawable.ic_alarm_check_black_18dp, "No", pendingIntentNo)
         * .build();
         */

        /**
         * Dynamic Action Button
         */
        for(NotificationUtils nUtils: utils.action_list)
        {
            /**
             * Send to broadcast for action button click
             */
            Intent aIntent = new Intent(mContext, ActionButtonReceiver.class);
            aIntent.setAction(nUtils.deep_link);
            aIntent.putExtra("data", nUtils.data);
            aIntent.putExtra("push.notificationId", notificationId);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, notificationId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            /**
             * Set action button icon from resource and add to notification
             */
            int resID = getInstance().getResources().getIdentifier(nUtils.icon, "drawable", getInstance().getPackageName());
            notificationBuilder.addAction(resID, nUtils.action_name, pIntent);
        }

        /**
         * Send to Broadcast for Delete
         * Add notificationBuilder.setDeleteIntent(contentIntent);
         */
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(mContext, notificationId));
        notification = notificationBuilder.build();

        /**
         * This will cancel notification on swipe.
         */
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        /**
         * Vibration is set to true vibrate default on notification
         */
        if(notification_vibrate)
        {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        /**
         * Finally notify user
         */
        notificationManager.notify(notificationId, notification);
    }


    private static void big_picture_notification(final Context mContext, final int notificationId, NotificationUtils utils)
    {
        Log.e("AAA","came in bigpicture");
        /** Start activity when user taps on notification.
         * Intent gotoIntent = new Intent(this, NotificationDeleteReceiver.class);
         * gotoIntent.setClassName(mContext, "convertifier.sample.ChatActivity");
         * PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Send to Broadcast for Notification Click
         */
        Intent gotoIntent = new Intent(mContext, NotificationActionReceiver.class);
        gotoIntent.setAction(utils.deep_link);
        gotoIntent.putExtra("data", utils.data);
        gotoIntent.putExtra("push.notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        final long when = System.currentTimeMillis();

        notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        /**
         * Set notification property
         */
        Notification notification;
        notificationBuilder
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(utils.title)
                .setContentText(utils.body)
                .setNumber(count)
                .setContentIntent(contentIntent);

        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle());
        /**
         * .setContentInfo("xyz")
         * .setSmallIcon(icon)
         * .setTicker("ticker")
         */

        try
        {
            /**
             * If notification object contains color
             */
            if(utils.color != null)
            {
                notificationBuilder.setColor(Color.parseColor(utils.color));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            /**
             * Check if notification contains small icon set from resource
             */
            if(utils.icon != null)
            {
                final int small_icon = getInstance().getResources().getIdentifier(utils.icon, "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }

            /**
             * Else set default notification small icon from resource
             */
            else
            {
                final int small_icon = getInstance().getResources().getIdentifier("ic_bell", "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            if(notification_sound)
            {
                final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //Uri uri = Uri.parse("android.resource://" + getInstance().getPackageName() + "/" + R.raw.icon);
                notificationBuilder.setSound(defaultSoundUri);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /**
         * If notification contains large icon
         */
        if(utils.large_icon != null)
        {

            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.large_icon);
                Bitmap large_icon = BitmapFactory.decodeStream((InputStream) url.getContent());
                notificationBuilder.setLargeIcon(large_icon);
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int resID = getInstance().getResources().getIdentifier(utils.large_icon, "drawable", getInstance().getPackageName());
                Bitmap largeIcon = BitmapFactory.decodeResource(getInstance().getResources(), resID);
                notificationBuilder.setLargeIcon(largeIcon);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * If notification contains picture
         */
        if(utils.remote_picture != null)
        {

            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.remote_picture);
                Bitmap remote_picture = BitmapFactory.decodeStream((InputStream) url.getContent());

                /**
                 * Set picture notification style
                 */
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(remote_picture)
                        .setBigContentTitle(utils.big_title)
                        .setSummaryText(utils.summary_text));
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int res_id = getInstance().getResources().getIdentifier(utils.remote_picture, "drawable", getInstance().getPackageName());
                Bitmap remote_picture = BitmapFactory.decodeResource(getInstance().getResources(), res_id);

                /**
                 * Set picture notification style
                 */
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(remote_picture)
                        .setBigContentTitle(utils.big_title)
                        .setSummaryText(utils.summary_text));
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Yes intent
         * Intent yesReceive = new Intent(this, NotificationActionReceiver.class);
         * yesReceive.setAction(YES_ACTION);
         * PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Maybe intent
         * Intent maybeReceive = new Intent(this, NotificationActionReceiver.class);
         * maybeReceive.setAction(MAYBE_ACTION);
         * PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12345, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * No intent
         * Intent noReceive = new Intent(this, NotificationActionReceiver.class);
         * noReceive.setAction("convertifier.sample.SecondActivity");
         * PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * .setNumber(++count);
         * .setTicker("Reload.in")
         * .setStyle(new NotificationCompat.BigTextStyle().bigText("big text"))
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Yes", pendingIntentYes)
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Partly", pendingIntentMaybe) * .addAction(R.drawable.ic_alarm_check_black_18dp, "No", pendingIntentNo)
         * .build();
         */

        /**
         * Dynamic Action Button
         */
        for(NotificationUtils nUtils: utils.action_list)
        {
            /**
             * Send to Broadcast for Action Button Click
             */
            Intent aIntent = new Intent(mContext, ActionButtonReceiver.class);
            aIntent.setAction(nUtils.deep_link);
            aIntent.putExtra("data", nUtils.data);
            aIntent.putExtra("push.notificationId", notificationId);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, notificationId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            /**
             * Set action button icon from resource and add to notification
             */
            int resID = getInstance().getResources().getIdentifier(nUtils.icon, "drawable", getInstance().getPackageName());
            notificationBuilder.addAction(resID, nUtils.action_name, pIntent);
        }

        /**
         * Send to Broadcast for Delete
         * Add notificationBuilder.setDeleteIntent(contentIntent);
         */
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(mContext, notificationId));
        notification = notificationBuilder.build();

        /**
         * This will cancel notification on swipe.
         */
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        /**
         * Vibration is set to true vibrate default on notification
         */
        if(notification_vibrate)
        {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        /**
         * Finally notify user
         */
        notificationManager.notify(notificationId, notification);
    }


    /**
     * Create pending intent for delete notification
     * @param context - Pass application context
     * @param notificationId - Pass unique notification id
     * @return - Return dismiss pending intent
     */
    private static PendingIntent createOnDismissedIntent(Context context, int notificationId)
    {
        Intent intent = new Intent(context, NotificationDeleteReceiver.class);
        intent.putExtra("push.notificationId", notificationId);

        return PendingIntent.getBroadcast(context.getApplicationContext(),
                notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     *
     * @param json - Pass JSON downstream string received from server
     * @return Array of notification action button
     */
    private static List<NotificationUtils> getActionButton(String json)
    {
        try
        {
            List<NotificationUtils> action_list = new ArrayList<>();

            /**
             * If invalid JSON return empty array list
             */
            if (json == null)
            {
                return action_list;
            }

            /**
             * Create JSONArray Object
             */
            final JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject;
            /**
             * Loop through JSON Array
             */
            for (int i=0; i< jsonArray.length(); i++)
            {

                /**
                 * Create JSON Object from JSON Array element
                 */
                jsonObject = jsonArray.getJSONObject(i);

                String name         = jsonObject.getString("action_name");
                String icon         = jsonObject.getString("icon");
                String deep_link    = null;
                String data         = null;

                if(jsonObject.has("deep_link"))
                {
                    deep_link = jsonObject.getString("deep_link");
                }

                if(jsonObject.has("data"))
                {
                    data = jsonObject.getString("data");
                }

                action_list.add(new NotificationUtils(name, icon, deep_link, data));
            }

            return action_list;
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Call this method receive for notification topic subscription
     * @param topic - Name of the subscription topic
     */
    public static void subscribe(String topic)
    {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    /**
     * Call this method receive for notification topic un subscription
     * @param topic - Name of the subscription topic
     */
    public static void unsubscribe(String topic)
    {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }


    private static void create_booking_notification(final Context context, final int notificationId, NotificationUtils utils, int appointment_id)
    {
        notification_sound = true;
        notification_vibrate = true;
        count = 0;

        //Intent notificationIntent = new Intent(context, AppointmentSummeryActivity.class);
        //notificationIntent.putExtra("ID", appointment_id);

        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addParentStack(AppointmentHistoryActivity.class);
        //stackBuilder.addNextIntent(notificationIntent);

        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * View History Intent
         */
        Intent viewIntent = new Intent(context, NotificationActionReceiver.class);
        viewIntent.setAction("OPEN_ACTIVITY_SUMMERY");
        viewIntent.putExtra("data", appointment_id);
        viewIntent.putExtra("push.notificationId", notificationId);
        PendingIntent pendingIntentView = PendingIntent.getBroadcast(context, notificationId, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * Cancel intent
         */
        Intent closeIntent = new Intent(context, NotificationCancelReceiver.class);
        closeIntent.putExtra("push.notificationId", notificationId);
        PendingIntent pendingIntentOk = PendingIntent.getBroadcast(context, notificationId, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "channel_appointment_booking";
        String channelName = "Booking Status";

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            if(mNotificationManager != null)
            {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

        Notification notification;

        builder.setContentTitle(utils.title)
                .setContentText(utils.body)
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(true)
                .addAction(R.drawable.ic_close_grey, "Dismiss", pendingIntentOk)
                .addAction(R.drawable.ic_alarm, "View", pendingIntentView);

        /**
         * Set notification big text style
         */
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(utils.big_text)
                .setBigContentTitle(utils.big_title)
                .setSummaryText(utils.summary_text));

        try
        {
            //Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ringtone);
            final Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        builder.setContentIntent(pendingIntentView).build();

        notification = builder.build();

        if(mNotificationManager != null)
        {
            mNotificationManager.notify(notificationId, notification);
        }
    }




    /**
     *
     * @param mContext - Pass application context
     * @param utils - Pass notification object
     */
    public static void notify(final Context mContext, NotificationUtils utils)
    {
        notification_sound = true;
        notification_vibrate = true;

        count = 0;

        NotificationCompat.Builder notificationBuilder;

        int notificationId = (int) (Math.random() * 100);

        final long when = System.currentTimeMillis();

        String channelId = "channel_doconline_notification";
        String channelName = "DocOnline";

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            if(mNotificationManager != null)
            {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        notificationBuilder = new NotificationCompat.Builder(mContext, channelId);

        /**
         * Set notification property
         */
        Notification notification;
        notificationBuilder
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(utils.title)
                .setContentText(utils.body)
                .setContentIntent(PendingIntent.getActivity(mContext.getApplicationContext(), 0, new Intent(), 0))
                .setNumber(count);

        try
        {
            /**
             * Check if notification contains small icon set from resource
             */
            if(utils.icon != null)
            {
                final int small_icon = getInstance().getResources().getIdentifier(utils.icon, "mipmap", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }

            /**
             * Else set default notification small icon from resource
             */
            else
            {
                final int small_icon = getInstance().getResources().getIdentifier("ic_bell", "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            if(notification_sound)
            {
                final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notificationBuilder.setSound(defaultSoundUri);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        notification = notificationBuilder.build();

        /**
         * This will cancel notification on swipe.
         */
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        if(notification_vibrate)
        {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        /**
         * Finally notify user
         */
        if(mNotificationManager != null)
        {
            mNotificationManager.notify(notificationId, notification);
        }
    }
}