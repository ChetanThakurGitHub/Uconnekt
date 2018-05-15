package com.uconnekt.ui.authontication.login;

public interface LoginIntractor {

    interface OnLoginFinishedListener {

        void onEmailError();

        void onEmailVError();

        void onPassowrdError();

        void onSuccess();

        void onNavigator();
    }

    void login(String username, String password, OnLoginFinishedListener listener);
}
