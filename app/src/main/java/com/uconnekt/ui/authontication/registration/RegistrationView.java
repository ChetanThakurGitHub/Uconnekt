package com.uconnekt.ui.authontication.registration;

/**
 * Created by mindiii on 4/4/18.
 */

public interface RegistrationView {

    void setBusinessNameError();
    void setBusinessNameRequired();
    void setFullNameError();
    void setFullNameRequiredError();
    void setEmailError();
    void setEmailErrorValidation();
    void setPasswordError();
    void setPasswordRequiredError();
    void setNevigetToHome();
}
