package com.uconnekt.ui.authentication.forgot_password;

/**
 * Created by mindiii on 17/4/18.
 */

public class ForgotPasswordPresenterImpl implements ForgotPasswordPresenter,ForgotPasswordIntractor.onForgotPasswordFinisedListener {

    private ForgotPasswordView forgotPasswordView;
    private ForgotPasswordIntractor forgotPasswordIntractor;

    ForgotPasswordPresenterImpl(ForgotPasswordView forgotPasswordView,ForgotPasswordIntractor forgotPasswordIntractor){
        this.forgotPasswordView = forgotPasswordView;
        this.forgotPasswordIntractor =forgotPasswordIntractor;
    }

    @Override
    public void validationCondition(String email) {
        forgotPasswordIntractor.forgotPassword(email,this);
    }

    @Override
    public void onDistroy() {
        forgotPasswordView = null;
    }

    @Override
    public void onEmailError() {
        if (forgotPasswordView != null){
            forgotPasswordView.setEmailError();
        }
    }

    @Override
    public void onEmailValidationError() {
        if (forgotPasswordView != null){
            forgotPasswordView.setEmailValidationError();
        }
    }


    @Override
    public void onSuccess() {
        if (forgotPasswordView != null){
            forgotPasswordView.callAPI();
        }
    }
}
