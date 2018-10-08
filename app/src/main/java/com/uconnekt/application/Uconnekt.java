package com.uconnekt.application;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.uconnekt.session.Session;
import com.uconnekt.util.Constant;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class Uconnekt extends Application {
    public static Uconnekt instance = null;
    public static double latitude=0.0;
    public static double longitude=0.0;
    @SuppressLint("StaticFieldLeak")
    public static Session session;

    public static synchronized Uconnekt getInstance() {
        if (instance != null) {
            return instance;
        }
        return new Uconnekt();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        session = new Session(instance.getApplicationContext());
        killAppBypackage("com.uconnekt");
    }

    private void killAppBypackage(String packageTokill){
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);
        ActivityManager mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = getApplicationContext().getPackageName();
        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1) {
                continue;
            }
            if(packageInfo.packageName.equals(myPackage)) {
                continue;
            }
            if(packageInfo.packageName.equals(packageTokill)) {
                mActivityManager.killBackgroundProcesses(packageInfo.packageName);
            }
        }
    }

}
