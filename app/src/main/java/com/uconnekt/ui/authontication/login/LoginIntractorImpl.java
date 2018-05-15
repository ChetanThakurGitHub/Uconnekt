package com.uconnekt.ui.authontication.login;


import com.uconnekt.util.Constant;

public class LoginIntractorImpl implements LoginIntractor {

    @Override
    public void login(final String username, final String password, final OnLoginFinishedListener listener) {

                if (username.equals("")) {
                    listener.onEmailError();
                    return;
                }

                if (!username.matches(Constant.emailPattern)){
                    listener.onEmailVError();
                    return;
                }

                if (password.equals("")) {
                    listener.onPassowrdError();
                    return;
                }

        listener.onSuccess();
    }
}
