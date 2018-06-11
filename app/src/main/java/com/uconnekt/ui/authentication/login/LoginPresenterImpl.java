package com.uconnekt.ui.authentication.login;

public class LoginPresenterImpl implements LoginPresenter,LoginIntractor.OnLoginFinishedListener{

    private LoginView loginView;
    private LoginIntractor loginIntractor;

    public LoginPresenterImpl(LoginView loginView, LoginIntractor loginIntractor){
        this.loginView = loginView;
        this.loginIntractor = loginIntractor;
    }

    @Override
    public void validationCondition(String username, String password) {
        loginIntractor.login(username,password,this);
    }

    @Override
    public void onDistroy() {
        loginView = null;
    }

    @Override
    public void onEmailError() {
        if (loginView != null){
            loginView.setEmailError();
        }
    }

    @Override
    public void onEmailVError() {
        if (loginView != null){
            loginView.setEmailVError();
        }
    }

    @Override
    public void onPassowrdError() {
        if (loginView != null){
            loginView.setPasswordError();
        }
    }

    @Override
    public void onSuccess() {
            if (loginView != null){
        loginView.navigateToHome();
            }
    }

    @Override
    public void onNavigator() {
        loginView.navigateToRegistration();
    }


}
