package com.uconnekt.ui.authentication.splash;

import android.app.Activity;

/**
 * Created by mindiii on 10/4/18.
 */

public interface SplashIntractor {

    interface onSplashFinishedListener{

    void navigetLoginActivity();

    void navigetMainActivity(); }

    void splash(Activity activity, SplashIntractor.onSplashFinishedListener listener);
}
