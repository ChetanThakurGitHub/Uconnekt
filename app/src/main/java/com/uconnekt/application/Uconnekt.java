package com.uconnekt.application;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.uconnekt.session.Session;
import com.uconnekt.util.Constant;

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
        instance = this;
        session = new Session(instance.getApplicationContext());
    }

}
