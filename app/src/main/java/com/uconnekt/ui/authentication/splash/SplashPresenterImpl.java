package com.uconnekt.ui.authentication.splash;

import android.app.Activity;

/**
 * Created by mindiii on 10/4/18.
 */

public class SplashPresenterImpl implements SplashPresenter,SplashIntractor.onSplashFinishedListener {

    private SplashView splashView;
    private SplashIntractor splashIntractor;
    private Activity activity;

    public SplashPresenterImpl(SplashView splashView ,SplashIntractor splashIntractor,Activity activity){
        this.splashView = splashView;
        this.splashIntractor = splashIntractor;
        this.activity = activity;
    }

    @Override
    public void validationCondition(Activity activity) {
        splashIntractor.splash(activity,this);

    }

    @Override
    public void onDistroy() {
        splashView = null;
    }

    @Override
    public void navigetLoginActivity() {
        if (splashView != null){
            splashView.openLoginActivity();
        }
    }

    @Override
    public void navigetMainActivity() {
        if (splashView != null){
            splashView.openMainActivity();
        }
    }
}
