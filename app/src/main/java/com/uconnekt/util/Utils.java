package com.uconnekt.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.uconnekt.reciver.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String format12HourTime(String time, String pFormat, String dFormat) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(pFormat, Locale.US);
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.US);
            Date dTime = parseFormat.parse(time);
            return displayFormat.format(dTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDate(String date, String pFormat, String dFormat) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(pFormat, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.getDefault());
            Date dTime = parseFormat.parse(date);
            return displayFormat.format(dTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date getDateFormat(String date, String format) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(format, Locale.getDefault());
            return parseFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDateFormat(Date date, String dFormat) {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setAlarm(final Activity activity, String date, String time, final String userId){
        //2018-09-26  //11:25 AM

        String[] tempDate = date.split("-");
        String[] temp12Time = time.split("\\s+");

        String[] tempTime = temp12Time[0].split(":");

        int year = Integer.parseInt(tempDate[0]);
        int month = Integer.parseInt(tempDate[1]);
        int setdate = Integer.parseInt(tempDate[2]);

        int hour = Integer.parseInt(tempTime[0]);
        int minute = Integer.parseInt(tempTime[1]);
        String ampm = temp12Time[1];

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DATE,setdate);
        cal.set(Calendar.HOUR,hour);
        cal.set(Calendar.MINUTE,minute);
        cal.set(Calendar.AM_PM,ampm.equals("AM")?Calendar.AM:Calendar.PM);

        cal.add(Calendar.MINUTE, -30);
        //Sun Oct 21 03:00:41 GMT+05:30 2018

        String myDate = getDateFormat(cal.getTime(),"yyyy-MM-dd HH:mm:ss");
        String[] tempDateTime = myDate.split("\\s+");
        String[] tempMyDate = tempDateTime[0].split("-");
        String[] myTime = tempDateTime[1].split(":");
        // Mon Oct 08 00:37:50 GMT+05:30 2018

        int setYear =   Integer.parseInt(tempMyDate[0]);
        int setMonth =  Integer.parseInt(tempMyDate[1])-1;
        int setDate =   Integer.parseInt(tempMyDate[2]) ;
        int setTime =   Integer.parseInt(myTime[0]);
        int setMinute = Integer.parseInt(myTime[1]);

        Date selectedDate, currentDate;
        selectedDate = Utils.getDateFormat(setDate+"-"+setMonth+"-"+setYear+" "+setTime+":"+setMinute,"dd-MM-yyyy HH:mm");
        currentDate = Utils.getDateFormat(getCurrentDate()+" "+getCurrentTime(),"dd-MM-yyyy HH:mm");

        if (currentDate.before(selectedDate)){
            alarmOnTime(setYear,setMonth,setDate,setTime,setMinute,activity,userId);
        }
    }

    private static void alarmOnTime(int setYear, int setMonth, int setDate, int setTime, int setMinute, Activity activity, String userId){
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(System.currentTimeMillis());
        cal2.clear();
        cal2.set(setYear, setMonth-1, setDate, setTime,setMinute);
        AlarmManager alarmMgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("ID",userId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), pendingIntent);
    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yyyy h:mm a";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return (day + "-" + month + "-" + year);

    }

    public static String getCurrentTime() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return (hour + ":" + minute);
    }

    public static HashMap<String, String> getQueryString(String url) {
        Uri uri= Uri.parse(url);
        HashMap<String, String> map = new HashMap<>();
        for (String paramName : uri.getQueryParameterNames()) {
            if (paramName != null) {
                String paramValue = uri.getQueryParameter(paramName);
                if (paramValue != null) {
                    map.put(paramName, paramValue);
                }
            }
        }
        return map;
    }
}