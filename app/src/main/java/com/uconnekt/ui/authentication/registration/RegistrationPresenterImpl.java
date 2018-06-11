package com.uconnekt.ui.authentication.registration;

public class RegistrationPresenterImpl implements RegistrationPresenter ,RegistrationIntractor.onRegitrationFinishedListener{

    private RegistrationView registrationView;
    private RegistrationIntractor registrationIntractor;

    public RegistrationPresenterImpl(RegistrationView registrationView , RegistrationIntractor registrationIntractor){
        this.registrationView = registrationView;
        this.registrationIntractor = registrationIntractor;
    }

    @Override
    public void validationCondition(String business, String fullname, String email, String password) {
        registrationIntractor.registration(business,fullname,email,password,this);
    }

    @Override
    public void onDistroy() {
        registrationView = null;
    }

    @Override
    public void onBusinessNameError() {
        if (registrationView != null){
            registrationView.setBusinessNameError();
        }

    }

    @Override
    public void onBusinessNameRequired() {
        if (registrationView != null){
            registrationView.setBusinessNameRequired();
        }
    }

    @Override
    public void onFullNameError() {
        if (registrationView != null){
            registrationView.setFullNameError();
        }
    }

    @Override
    public void onFullNameRequired() {
        if (registrationView != null){
            registrationView.setFullNameRequiredError();
        }
    }


    @Override
    public void onEmailError() {
        if (registrationView != null){
            registrationView.setEmailError();
        }
    }

    @Override
    public void onEmailErrorValidation() {
        if (registrationView != null){
            registrationView.setEmailErrorValidation();
        }
    }

    @Override
    public void onPasswordError() {
        if (registrationView != null){
            registrationView.setPasswordError();
        }
    }

    @Override
    public void onPasswordRequired() {
        if (registrationView != null){
            registrationView.setPasswordRequiredError();
        }
    }


    @Override
    public void onSuccess() {
        if (registrationView!= null) {
            registrationView.setNevigetToHome();
        }
    }

    @Override
    public void onNavigator() {
        registrationView.setNevigetToHome();
    }

}
