package com.uconnekt.ui.authentication.registration;

/**
 * Created by mindiii on 5/4/18.
 */

public interface RegistrationIntractor {
    interface onRegitrationFinishedListener{
        void onBusinessNameError();
        void onBusinessNameRequired();
        void onFullNameError();
        void onFullNameRequired();
        void onEmailError();
        void onEmailErrorValidation();
        void onPasswordError();
        void onPasswordRequired();
        void onSuccess();
        void onNavigator();
    }

    void registration(String businessName, String fullname, String email, String password, onRegitrationFinishedListener listener);
}
