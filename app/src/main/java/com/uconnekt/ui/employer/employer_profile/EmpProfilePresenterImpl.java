package com.uconnekt.ui.employer.employer_profile;

import android.graphics.Bitmap;

/**
 * Created by mindiii on 25/4/18.
 */

public class EmpProfilePresenterImpl implements EmpProfilePresenter ,EmpProfileIntractor.OnProfileEndListener{

    private EmpProfileView empProfileView;
    private EmpProfileIntractor empProfileIntractor;

    EmpProfilePresenterImpl(EmpProfileView empProfileView,EmpProfileIntractor empProfileIntractor){
        this.empProfileView = empProfileView;
        this.empProfileIntractor= empProfileIntractor;
    }

    @Override
    public void validationCondition(String jobTitle, String specailty, String address, Bitmap bitmap) {
        empProfileIntractor.profile(jobTitle,specailty,address,bitmap, this);
    }

    @Override
    public void onDistroy() {
        empProfileView = null;
    }

    @Override
    public void onJobTitleError() {
        if (empProfileView != null){
            empProfileView.setJobTitleError();
        }
    }

    @Override
    public void onSpecialtyError() {
        if (empProfileView != null){
            empProfileView.setSpecialtyError();
        }
    }

    @Override
    public void onAddressError() {
        if (empProfileView != null){
            empProfileView.setAddressError();
        }
    }

    @Override
    public void onCompanyLogoError() {
        if (empProfileView != null){
            empProfileView.setCompanyLogoError();
        }
    }


    @Override
    public void onSuccess() {
        if (empProfileView != null){
            empProfileView.navigateToHome();
        }
    }

    @Override
    public void onNevigator() {

    }
}
