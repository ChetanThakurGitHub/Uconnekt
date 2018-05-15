package com.uconnekt.ui.authontication.user_selection;

public interface UserSelectionIntractor {
    interface onUserSelectionFinishedListener{
        void onUserSelectionError();
        void onSuccess();
    }
    void userSelection(String userType,onUserSelectionFinishedListener listener);
}
