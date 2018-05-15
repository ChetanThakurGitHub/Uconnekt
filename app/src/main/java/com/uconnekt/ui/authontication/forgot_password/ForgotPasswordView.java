package com.uconnekt.ui.authontication.forgot_password;

/**
 * Created by mindiii on 17/4/18.
 */

public interface ForgotPasswordView {
    void setEmailError();
    void setEmailValidationError();
    void callAPI();
}
