package com.uconnekt.ui.authontication.forgot_password;

/**
 * Created by mindiii on 17/4/18.
 */

public interface ForgotPasswordIntractor {
    interface onForgotPasswordFinisedListener{
        void onEmailError();
        void onEmailValidationError();
        void onSuccess();
    }
    void forgotPassword(String email, onForgotPasswordFinisedListener listener);
}
