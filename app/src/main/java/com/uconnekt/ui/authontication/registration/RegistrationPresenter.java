package com.uconnekt.ui.authontication.registration;

public interface RegistrationPresenter {

    void validationCondition(String businessName, String fullname, String email, String password);

    void onDistroy();

}
