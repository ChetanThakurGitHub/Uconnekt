package com.uconnekt.ui.authentication.forgot_password;

/**
 * Created by mindiii on 17/4/18.
 */

public interface ForgotPasswordPresenter {
    void validationCondition(String email);
    void onDistroy();
}
