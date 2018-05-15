package com.uconnekt.ui.authontication.user_selection;

public class UserSelectionIntractorImpl implements UserSelectionIntractor {
    @Override
    public void userSelection(String userType, onUserSelectionFinishedListener listener) {

        if (userType.equals("")){
            listener.onUserSelectionError();
            return;
        }
        listener.onSuccess();
    }
}
