package com.uconnekt.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

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

}