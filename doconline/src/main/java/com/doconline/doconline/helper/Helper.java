package com.doconline.doconline.helper;

import android.content.res.Resources;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import com.doconline.doconline.app.Constants;
import com.doconline.doconline.app.MyApplication;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.doconline.doconline.app.Constants.DD_MM_YY;
import static com.doconline.doconline.app.Constants.HH_MM_A;

public class Helper
{

    public static String toCamelCase(String inputString) {

        String result = "";

        if (inputString.length() == 0) {
            return result;
        }

        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);

        result = result + firstCharToUpperCase;

        for (int i = 1; i < inputString.length(); i++) {

            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);

            if (previousChar == ' ') {

                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }

        return result;
    }

    public static String stripHtml(String html)
    {
        return Html.fromHtml(html).toString();
    }

    public static String formatNumber(int no)
    {

        if(no<10)
        {
            return "0" + no;
        }

        else
        {
            return String.valueOf(no);
        }
    }

    public static String format_date(int value)
    {

        if(value < 10)
        {
            return "0" + value;
        }

        return String.valueOf(value);
    }

    public static String dateTimeFormat(String str)
    {

        String datetime = "";
        String day;

        String[] part1 = str.split("-");

        int year = Integer.parseInt(part1[0]);
        int month = Integer.parseInt(part1[1]);
        int date = Integer.parseInt(part1[2]);

        if(date == 1 || date == 21 || date == 31)
        {
            day = "st";
        }

        else if(date == 2 || date == 22)
        {
            day = "nd";
        }

        else if(date == 3 || date == 23)
        {
            day = "rd";
        }

        else
        {
            day = "th";
        }

        switch (month)
        {

            case 1:

                datetime = date + day + " Jan " + year + " " + timeFormat(str);
                break;

            case 2:

                datetime = date + day + " Feb " + year + " " + timeFormat(str);
                break;

            case 3:

                datetime = date + day + " Mar " + year + " " + timeFormat(str);
                break;

            case 4:

                datetime = date + day + " Apr " + year + " " + timeFormat(str);
                break;

            case 5:

                datetime = date + day + " May " + year + " " + timeFormat(str);
                break;

            case 6:

                datetime = date + day + " Jun " + year + " " + timeFormat(str);
                break;

            case 7:

                datetime = date + day + " Jul " + year + " " + timeFormat(str);
                break;

            case 8:

                datetime = date + day + " Aug " + year + " " + timeFormat(str);
                break;

            case 9:

                datetime = date + day + " Sep " + year + " " + timeFormat(str);
                break;

            case 10:

                datetime = date + day + " Oct " + year + " " + timeFormat(str);
                break;

            case 11:

                datetime = date + day + " Nov " + year + " " + timeFormat(str);
                break;

            case 12:

                datetime = date + day + " Dec " + year + " " + timeFormat(str);
                break;
        }

        return datetime;
    }


    private static String timeFormat(String str)
    {
        String datetime = "";

        try
        {
            String[] time = str.split(" ")[1].split(":");
            return setDateTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return datetime;
    }

    public static String dateFormat(String str)
    {
        String datetime = "";

        try
        {
            if(str.isEmpty())
            {
                return datetime;
            }

            String[] date = str.split("-");
            datetime = date[2] + "/" + date[1] + "/" + date[0];
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return datetime;
    }

    public static String setDateTime(int hour, int minute)
    {

        String timeSet;

        if (hour > 12)
        {
            hour -= 12;
            timeSet = "PM";
        }

        else if (hour == 0)
        {
            hour += 12;
            timeSet = "AM";
        }

        else if (hour == 12)
        {
            timeSet = "PM";
        }

        else
        {
            timeSet = "AM";
        }


        String minutes;

        if (minute < 10)
        {
            minutes = "0" + minute;
        }

        else
        {
            minutes = String.valueOf(minute);
        }

        return hour + ":" + minutes + " " + timeSet;
    }


    public static boolean fileExist(String file_name)
    {

        File imgFile = new File(file_name);
        {
            return imgFile.exists();
        }
    }


    public static String UTC_to_Local_TimeZone(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            Log.v("OurDate", OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);

            Log.v("OurDate", OurDate);
        }

        catch (Exception e)
        {
            OurDate = "0000-00-00 00:00:00";
            Log.v("OurDate", OurDate);
        }

        return OurDate;
    }

    public static String UTC_to_Local_Date(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            Log.v("OurDate", OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);

            Log.v("OurDate", OurDate);
        }

        catch (Exception e)
        {
            OurDate = "0000-00-00 00:00:00";
            Log.v("OurDate", OurDate);
        }

        return OurDate;
    }


    public static String Current_to_UTC_TimeZone()
    {
        String utc_timestamp = "0000-00-00 00:00:00";

        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS); //this format changeable
            String timestamp = formatter.format(System.currentTimeMillis());
            Date current = formatter.parse(timestamp);

            return Helper.Local_to_UTC_TimeZone(formatter.format(current));
        }

        catch (Exception e)
        {
            return utc_timestamp;
        }
    }


    public static String Local_to_UTC_TimeZone(String OurDate)
    {
        try
        {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            Date value = dateFormatter.parse(OurDate);

            Log.v("OurDate", OurDate);

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            OurDate = formatter.format(value);

            Log.v("OurDate", OurDate);
        }

        catch (Exception e)
        {
            OurDate = "0000-00-00 00:00:00";
            Log.v("OurDate", OurDate);
        }

        return OurDate;
    }


    public static String UTC_to_Local_Calendar(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            Log.i("OurDate", OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);

            Log.i("OurDate", OurDate);
        }

        catch (Exception e)
        {
            OurDate = "0000-00-00 00:00:00";
            Log.v("OurDate", OurDate);
        }

        return OurDate;
    }

    public static int compare_date(String scheduled_at, long current)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

        try
        {
            Date date1 = sdf.parse(scheduled_at);
            Date date2 = sdf.parse(sdf.format(current));

            if (date1.equals(date2))
            {
                Log.v("OurDate", "Two dates are equal");
                return 0;
            }

            if(date1.before(date2))
            {
                Log.v("OurDate", "date1 is before date2");
                return -1;
            }

            if(date1.after(date2))
            {
                Log.v("OurDate", "date1 is after date2");
                return 1;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 2;
    }


    public static int compare_date(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD);

        try
        {
            Date date1 = sdf.parse(date);
            Date date2 = sdf.parse(sdf.format(System.currentTimeMillis()));

            if (date1.equals(date2))
            {
                Log.v("OurDate", "Two dates are equal");
                return 0;
            }

            if(date1.before(date2))
            {
                Log.v("OurDate", "date1 is before date2");
                return -1;
            }

            if(date1.after(date2))
            {
                Log.v("OurDate", "date1 is after date2");
                return 1;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 2;
    }


    public static int compare_date(String date_1, String date_2)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

        try
        {
            Date date1 = sdf.parse(date_1);
            Date date2 = sdf.parse(date_2);

            if (date1.equals(date2))
            {
                Log.v("OurDate", "Two dates are equal");
                return 0;
            }

            if(date1.before(date2))
            {
                Log.v("OurDate", "date1 is before date2");
                return -1;
            }

            if(date1.after(date2))
            {
                Log.v("OurDate", "date1 is after date2");
                return 1;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 2;
    }


    public static String date_format(String OurDate)
    {
        try
        {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS); //this format changeable
            Date value = dateFormatter.parse(OurDate);

            Log.v("OurDate", OurDate);

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY);
            OurDate = formatter.format(value);

            Log.v("OurDate", OurDate);
        }

        catch (Exception e)
        {
            OurDate = "0000-00-00 00:00:00";
            Log.v("OurDate", OurDate);
        }

        return OurDate;
    }


    public static String datetime_format(String OurDate)
    {
        try
        {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS); //this format changeable
            Date value = dateFormatter.parse(OurDate);

            Log.v("OurDate", OurDate);

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.EEE_MMM_DD_YYYY_HH_MM_A);
            OurDate = formatter.format(value);

            Log.v("OurDate", OurDate);
        }

        catch (Exception e)
        {
            OurDate = "0000-00-00 00:00:00";
            Log.v("OurDate", OurDate);
        }

        return OurDate;
    }


    public static String time_format(String OurDate)
    {
        try
        {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS); //this format changeable
            Date value = dateFormatter.parse(OurDate);

            Log.v("OurDate", OurDate);

            SimpleDateFormat formatter = new SimpleDateFormat(HH_MM_A);
            OurDate = formatter.format(value);

            Log.v("OurDate", OurDate);
        }

        catch (Exception e)
        {
            OurDate = "0000-00-00 00:00:00";
            Log.v("OurDate", OurDate);
        }

        return OurDate;
    }


    public static boolean isPasswordRequired(long time)
    {
        try
        {
            if(time == 0)
            {
                return true;
            }

            long minute_diff = (System.currentTimeMillis() - time)/(1000 * 60);

            Log.i("minute_diff", "" + minute_diff);

            if(minute_diff > 5)
            {
                return true;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean time_diff(String d1, String d2)
    {
        boolean is_cancelable = false;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

        try
        {
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            long millis = date1.getTime() - date2.getTime();

            //int hours = (int) (millis/(1000 * 60 * 60));
            int mins = (int) (millis/(1000 * 60));// % 60;

            if(mins >= 5)
            {
                is_cancelable = true;
            }

            //Log.v("OurDate", hours + ":" + mins);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return is_cancelable;
    }


    public static long time_diff(String d1)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

        try
        {
            Date date1 = sdf.parse(UTC_to_Local_TimeZone(d1));
            long millis = date1.getTime() - System.currentTimeMillis();
            return (millis/1000);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return -1;
    }


    public static int calculate_waiting_time(String slot_date, String slot_time)
    {
        try
        {
            //this format changeable
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);
            Date value = sdf.parse(slot_date);
            slot_date = new SimpleDateFormat(Constants.YYYY_MM_DD).format(value);

            String next_slot = Helper.UTC_to_Local_TimeZone(slot_date + " " + slot_time);

            Log.v("DATEDIFF", next_slot);

            Date date1 = sdf.parse(next_slot);
            long millis = date1.getTime() - System.currentTimeMillis();

            //int hours = (int) (millis/(1000 * 60 * 60));
            //int mins = (int) (millis/(1000 * 60)) % 60;

            int mins = (int) (millis/(1000 * 60));

            return mins;
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }


    public static int calculate_waiting_time()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("mm");
        int min = Integer.parseInt(formatter.format(System.currentTimeMillis()));
        Log.v("current_min", "" + min);

        for(int i = 1; i<=60/(MyApplication.getInstance().getSession().getAppointmentCallbackTimeLimit()); i++) {
            if(min >= ((i-1)*(MyApplication.getInstance().getSession().getAppointmentCallbackTimeLimit())) && min < i*(MyApplication.getInstance().getSession().getAppointmentCallbackTimeLimit()))
            {
                int remain = (int)(i*(MyApplication.getInstance().getSession().getAppointmentCallbackTimeLimit())) - min;
                Log.v("remaining_time", "" + remain);
                return remain;
            }
        }

//        if(min >= 0 && min < 10)
//        {
//            int remain = 10 - min;
//            Log.v("remaining_time", "" + remain);
//            return remain;
//        }
//
//        if(min >= 10 && min < 20)
//        {
//            int remain = 20 - min;
//            Log.v("remaining_time", "" + remain);
//            return remain;
//        }
//
//        if(min >= 20 && min < 30)
//        {
//            int remain = 30 - min;
//            Log.v("remaining_time", "" + remain);
//            return remain;
//        }
//
//        if(min >= 30 && min < 40)
//        {
//            int remain = 40 - min;
//            Log.v("remaining_time", "" + remain);
//            return remain;
//        }
//
//        if(min >= 40 && min < 50)
//        {
//            int remain = 50 - min;
//            Log.v("remaining_time", "" + remain);
//            return remain;
//        }
//
//        if(min >= 50 && min < 60)
//        {
//            int remain = 60 - min;
//            Log.v("remaining_time", "" + remain);
//            return remain;
//        }

        return 0;
    }


    public static int second_remaining(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

        try
        {
            /**
             * Remind before five minutes
             */
            Date date1 = sdf.parse(date);
            long millis = (date1.getTime() - (5 * 60 * 1000)) - System.currentTimeMillis();

            if(millis > 0)
            {
                return (int)(millis/1000);
            }

            return -1;
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return -1;
    }


    public static String find_otp(final String in)
    {
        final Pattern p = Pattern.compile( "(\\d{6})" );
        final Matcher m = p.matcher( in );

        if ( m.find() )
        {
            return m.group( 0 );
        }

        return "";
    }


    public static String find_timezone_offset()
    {
        //String timezone = TimeZone.getDefault().getDisplayName();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");

        return date.format(currentLocalTime);
    }


    public static String findTimeZone()
    {
        TimeZone tz = TimeZone.getDefault();

        String timezone = tz.getID();

        if(timezone.equalsIgnoreCase("Asia/Calcutta"))
        {
            return "Asia/Kolkata";
        }

        return timezone;
    }


    public static int getYearDiff(String date)
    {
        int diff = 0;

        try
        {
            String new_date = date + " 00:00:00";

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

            Date actual_date = formatter.parse(new_date);
            Calendar a = getCalendar(actual_date);

            String timestamp = formatter.format(System.currentTimeMillis());
            Date current = formatter.parse(timestamp);
            Calendar b = getCalendar(current);

            diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);

            if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE)))
            {
                diff--;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return diff;
    }


    public static int getYearDiff(String from_date, String to_date)
    {
        int diff = 0;

        try
        {
            from_date = from_date + " 00:00:00";
            to_date = to_date + " 00:00:00";

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

            Date fromDateObj = formatter.parse(from_date);
            Calendar a = getCalendar(fromDateObj);

            Date toDateObj = formatter.parse(to_date);
            Calendar b = getCalendar(toDateObj);

            diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);

            if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE)))
            {
                diff--;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return diff;
    }


    private static Calendar getCalendar(Date date)
    {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }


    public static String getYearMonthDaysDiff(String date)
    {
        int diff;

        try
        {
            String new_date = date + " 00:00:00";

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

            Date actual_date = formatter.parse(new_date);
            Calendar a = getCalendar(actual_date);

            String timestamp = formatter.format(System.currentTimeMillis());
            Date current = formatter.parse(timestamp);
            Calendar b = getCalendar(current);

            diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);

            if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE)))
            {
                diff--;
            }

            if(diff == 0)
            {
                int month = b.get(Calendar.MONTH) - a.get(Calendar.MONTH);

                if(month == 0)
                {
                    int days = b.get(Calendar.DATE) - a.get(Calendar.DATE);

                    if(days == 1)
                    {
                        return days + " Day";
                    }

                    return days + " Days";
                }

                else
                {
                    if(month == 1)
                    {
                        return month + " Month";
                    }

                    return month + " Months";
                }
            }

            else
            {
                if(diff == 1)
                {
                    return diff + " Year";
                }

                return diff + " Years";
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "N/A";
    }




    public static String getYearMonthDaysDiff(String date, String fake)
    {
        long diff;

        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

            Date actual_date = formatter.parse(date);
            //Calendar b = getCalendar(actual_date);

            String timestamp = formatter.format(System.currentTimeMillis());
            Date current = formatter.parse(timestamp);
            //Calendar a = getCalendar(current);

            diff = actual_date.getTime() - current.getTime();

            long day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if(day == 0)
            {
                return "Promo code will be expire today.";
            }

            if(day == 1)
            {
                return "Promo code will be expired in a day.";
            }

            return "Promo code will be expired in " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " days.";
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }


    public static String getRandomString()
    {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 20)
        {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        String saltStr = salt.toString();
        return saltStr;
    }


    public static String format_date(String date)
    {
        int diff;

        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.YY_MM_DD_HH_MM_SS);

            Date actual_date = formatter.parse(date);
            Calendar a = getCalendar(actual_date);

            String timestamp = formatter.format(System.currentTimeMillis());
            Date current = formatter.parse(timestamp);
            Calendar b = getCalendar(current);

            diff = b.get(Calendar.DATE) - a.get(Calendar.DATE);

            if(diff == 0)
            {
                formatter = new SimpleDateFormat(HH_MM_A);
                return String.valueOf(formatter.format(actual_date));
            }

            else if(diff == 1)
            {
                return "YESTERDAY";
            }

            else
            {
                formatter = new SimpleDateFormat(DD_MM_YY);
                return String.valueOf(formatter.format(actual_date));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "N/A";
    }


    public static int getRandomInteger(int maximum, int minimum)
    {
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }


    public static float convertPixelsToDp(float px)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}