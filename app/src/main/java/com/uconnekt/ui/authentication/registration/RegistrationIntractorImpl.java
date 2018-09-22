package com.uconnekt.ui.authentication.registration;


import com.uconnekt.util.Constant;

public class RegistrationIntractorImpl implements RegistrationIntractor {

    @Override
    public void registration(String businessName, String fullname, String email, String password, String phone, onRegitrationFinishedListener listener) {

        if (businessName.equalsIgnoreCase("")){
            listener.onBusinessNameError();
            return;
        }

        if (businessName.length() < 3){
            listener.onBusinessNameRequired();
            return;
        }

        if (fullname.equalsIgnoreCase("")) {
            listener.onFullNameError();
            return;
        }
        if (fullname.length() < 3) {
            listener.onFullNameRequired();
            return;
        }

        if (phone.equalsIgnoreCase("")) {
            listener.onPhoneError();
            return;
        }

        if (phone.length() < 7 || phone.length() > 16) {
            listener.onPhoneErrorValidation();
            return;
        }

        if (email.equalsIgnoreCase("")) {
            listener.onEmailError();
            return;
        }

        if (!email.toLowerCase().matches(Constant.emailPattern)){
            listener.onEmailErrorValidation();
            return;
        }


        if (password.equalsIgnoreCase("")) {
            listener.onPasswordError();
            return;
        }

        if (password.length()<6){
            listener.onPasswordRequired();
            return;
        }

        listener.onSuccess();
    }


}
