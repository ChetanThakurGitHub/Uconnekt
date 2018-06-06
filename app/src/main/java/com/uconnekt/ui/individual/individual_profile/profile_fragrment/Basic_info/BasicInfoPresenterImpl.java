package com.uconnekt.ui.individual.individual_profile.profile_fragrment.Basic_info;

import android.support.design.widget.TabLayout;

/**
 * Created by mindiii on 1/5/18.
 */

public class BasicInfoPresenterImpl implements BasicInfoPresenter, BasicInfoIntractor.OnProfileEndListener {

    private BasicInfoView basicInfoView;
    private BasicInfoIntractor basicInfoIntractor;
    private TabLayout.Tab tab;

    BasicInfoPresenterImpl(BasicInfoView basicInfoView, BasicInfoIntractor basicInfoIntractor) {
        this.basicInfoView = basicInfoView;
        this.basicInfoIntractor = basicInfoIntractor;
    }

    @Override
    public void validationCondition(String specialty, String value, String strength, String address) {
        //this.tab = tab;
        basicInfoIntractor.basicInfo(specialty, value, strength, address, this);
    }

    @Override
    public void onDistroy() {
        basicInfoView = null;
    }

    @Override
    public void onSpecialtyError() {
        if (basicInfoView != null) {
            basicInfoView.setSpecialtyError();
        }
    }

    @Override
    public void onValueError() {
        if (basicInfoView != null) {
            basicInfoView.setValueError();
        }
    }

    @Override
    public void onStrengthError() {
        if (basicInfoView != null) {
            basicInfoView.setStrengthError();
        }
    }

    @Override
    public void onAddressError() {
        if (basicInfoView != null) {
            basicInfoView.setAddressError();
        }
    }

    @Override
    public void onSuccess() {
        if (basicInfoView != null) {
            basicInfoView.navigateToExperience();
        }
    }

    @Override
    public void onNevigator() {

    }
}
