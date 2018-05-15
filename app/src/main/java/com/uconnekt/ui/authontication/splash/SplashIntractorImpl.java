package com.uconnekt.ui.authontication.splash;

import android.app.Activity;

import com.uconnekt.application.Uconnekt;

public class SplashIntractorImpl implements SplashIntractor {

    @Override
    public void splash(Activity activity, onSplashFinishedListener listener) {
        if (Uconnekt.session.isLoggedIn()){
            listener.navigetMainActivity();
        }

        if (!Uconnekt.session.isLoggedIn()){
            listener.navigetLoginActivity();
        }
    }
}
