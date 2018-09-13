package com.uconnekt.ui.authentication.registration;

public interface RegistrationPresenter {

    void validationCondition(String businessName, String fullname, String email, String password, String phone);

    void onDistroy();

}
