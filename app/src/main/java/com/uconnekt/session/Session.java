package com.uconnekt.session;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.UserInfo;
import com.uconnekt.ui.authentication.user_selection.UserSelectionActivity;

public class Session {

    @SuppressLint("StaticFieldLeak")
    private static Session instance;

    private final String IS_LOGGEDIN = "isLoggedIn";

    private final String USER_ID = "userId";
    private final String FULLNAME = "fullName";
    private final String IS_VERIFIED = "isVerified";
    private final String BUSINESS_NAME = "businessName";
    private final String EMAIL = "email";
    private final String USER_TYPE = "userType";
    private final String AUTHTOKEN = "authToken";
    private final String STATUS = "status";
    private final String CRD = "crd";
    private final String DEVICE_TOKEN = "deviceToken";
    private final String DEVICETYPE = "deviceType";
    private final String IS_PROFILE = "isProfile";
    private final String PROFILE_IMAGE = "profileImage";
    private final String PASSWORD = "password";
    private final String IS_NOTIFY = "isNotify";
    private final String JOB_TITLE = "jobTitleName";
    private final String RATING = "rating";
    private final String COMPANY_LOGO = "company_logo";
    private final String SPECIALIXANTION_NAME = "specializationName";

    private final String EMAIL_R = "email_r";
    private final String PASSWORD_R = "password_r";
    private final String USER_TYPE_R = "userType_r";

    private SharedPreferences mypref;
    private SharedPreferences remember_pref;

    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor2;


    public Session(Context context) {
        String PREF_NAME = "Uconnekt";
        mypref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String REMEMBER_ME = "Uconneckt_Remember";
        remember_pref = context.getSharedPreferences(REMEMBER_ME, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor2 = remember_pref.edit();
        editor.apply();
        editor2.apply();
    }

    public static Session getInstance() {
        if ((instance != null)) {
            return instance;
        }
        instance = new Session(Uconnekt.getInstance().getApplicationContext());
        return instance;
    }


    public void createSession(UserInfo userInfo) {
        editor.putString(USER_ID, userInfo.userId);
        editor.putString(FULLNAME, userInfo.fullName);
        editor.putString(IS_VERIFIED, userInfo.isVerified);
        editor.putString(BUSINESS_NAME, userInfo.businessName);
        editor.putString(EMAIL, userInfo.email);
        editor.putString(USER_TYPE, userInfo.userType);
        editor.putString(AUTHTOKEN, userInfo.authToken);
        editor.putString(STATUS, userInfo.status);
        editor.putString(CRD, userInfo.crd);

        editor.putString(DEVICE_TOKEN, userInfo.deviceToken);
        editor.putString(DEVICETYPE, userInfo.deviceType);
        editor.putString(IS_PROFILE, userInfo.isProfile);
        editor.putString(PROFILE_IMAGE, userInfo.profileImage);
        editor.putString(PASSWORD, userInfo.password);
        editor.putString(IS_NOTIFY, userInfo.isNotify);
        editor.putString(JOB_TITLE, userInfo.jobTitleName);
        editor.putString(RATING, userInfo.rating);
        editor.putString(COMPANY_LOGO, userInfo.company_logo);
        editor.putString(SPECIALIXANTION_NAME, userInfo.specializationName);

        editor.putBoolean(IS_LOGGEDIN, true);
        editor.commit();

        editor2.putString(EMAIL_R,userInfo.email);
        editor2.putString(PASSWORD_R,userInfo.password);
        editor2.putString(USER_TYPE_R, userInfo.userType);
        editor2.commit();
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.userId=(mypref.getString(USER_ID, ""));
        userInfo.fullName=(mypref.getString(FULLNAME, ""));
        userInfo.isVerified=(mypref.getString(IS_VERIFIED, ""));
        userInfo.businessName=(mypref.getString(BUSINESS_NAME, ""));
        userInfo.email=(mypref.getString(EMAIL, ""));
        userInfo.userType=(mypref.getString(USER_TYPE, ""));
        userInfo.authToken=(mypref.getString(AUTHTOKEN, ""));
        userInfo.status=(mypref.getString(STATUS, ""));
        userInfo.crd=(mypref.getString(CRD, ""));
        userInfo.deviceToken=(mypref.getString(DEVICE_TOKEN, ""));
        userInfo.deviceType=(mypref.getString(DEVICETYPE, ""));
        userInfo.isProfile=(mypref.getString(IS_PROFILE, ""));
        userInfo.profileImage=(mypref.getString(PROFILE_IMAGE, ""));
        userInfo.password=(mypref.getString(PASSWORD, ""));
        userInfo.isNotify=(mypref.getString(IS_NOTIFY, ""));
        userInfo.jobTitleName=(mypref.getString(JOB_TITLE, ""));
        userInfo.rating=(mypref.getString(RATING, ""));
        userInfo.company_logo=(mypref.getString(COMPANY_LOGO, ""));
        userInfo.specializationName=(mypref.getString(SPECIALIXANTION_NAME, ""));

        return userInfo;
    }

    public UserInfo getRememberMeInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.email=(remember_pref.getString(EMAIL_R, ""));
        userInfo.password=(remember_pref.getString(PASSWORD_R, ""));
        userInfo.userType =(remember_pref.getString(USER_TYPE_R, ""));
        return userInfo;
    }

    public boolean isLoggedIn() {
        return mypref.getBoolean(IS_LOGGEDIN, false);
    }

    public void logout(Activity activity) {
        editor.putBoolean(IS_LOGGEDIN, false);
        editor.clear();
        editor.commit();

        Intent intent = new Intent(activity, UserSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public void logoutMyPre() {
        editor2.clear();
        editor2.apply();
    }

    public void setLogin(boolean value){
        editor.putBoolean(IS_LOGGEDIN, value);
        editor.commit();
    }
}