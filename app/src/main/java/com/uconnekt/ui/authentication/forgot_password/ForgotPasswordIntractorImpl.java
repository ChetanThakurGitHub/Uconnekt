package com.uconnekt.ui.authentication.forgot_password;

import com.uconnekt.util.Constant;

/**
 * Created by mindiii on 17/4/18.
 */

public class ForgotPasswordIntractorImpl implements ForgotPasswordIntractor {
    @Override
    public void forgotPassword(String email, onForgotPasswordFinisedListener listener) {
        if (email.equalsIgnoreCase("")){
            listener.onEmailError();
            return;
        }

        if (!email.matches(Constant.emailPattern)){
            listener.onEmailValidationError();
            return;
        }
        listener.onSuccess();
    }
}
