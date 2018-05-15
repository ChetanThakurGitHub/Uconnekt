package com.uconnekt.ui.authontication.login;

/**
 * Created by mindiii on 2/4/18.
 */

public interface LoginView {
    void setEmailError();
    void setEmailVError();
    void setPasswordError();
    void navigateToHome();
    void navigateToRegistration();
}
