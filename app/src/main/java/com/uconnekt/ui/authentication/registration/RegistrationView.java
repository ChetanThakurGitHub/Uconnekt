package com.uconnekt.ui.authentication.registration;

public interface RegistrationView {

    void setBusinessNameError();
    void setBusinessNameRequired();
    void setFullNameError();
    void setFullNameRequiredError();
    void setPhoneError();
    void setPhoneErrorValidation();
    void setEmailError();
    void setEmailErrorValidation();
    void setPasswordError();
    void setPasswordRequiredError();
    void setNevigetToHome();
}
