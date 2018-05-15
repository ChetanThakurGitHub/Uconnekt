package com.uconnekt.ui.authontication.user_selection;

/**
 * Created by mindiii on 17/4/18.
 */

public class UserSelectionPresenterImpl implements UserSelectionPresenter,UserSelectionIntractor.onUserSelectionFinishedListener {

    private UserSelectionView userSelectionView;
    private UserSelectionIntractor userSelectionIntractor;


    public UserSelectionPresenterImpl(UserSelectionView userSelectionView,UserSelectionIntractor userSelectionIntractor){
        this.userSelectionView = userSelectionView;
        this.userSelectionIntractor = userSelectionIntractor;
    }

    @Override
    public void validationCondition(String userType) {
        userSelectionIntractor.userSelection(userType,this);
    }

    @Override
    public void onDistroy() {
        userSelectionView = null;
    }

    @Override
    public void onUserSelectionError() {
        if (userSelectionView != null){
            userSelectionView.setUserSelectionError();
        }
    }

    @Override
    public void onSuccess() {
        if (userSelectionView != null){
            userSelectionView.onMoveForward();
        }
    }
}
