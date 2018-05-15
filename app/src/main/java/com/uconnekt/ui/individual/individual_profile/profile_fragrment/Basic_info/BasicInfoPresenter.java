package com.uconnekt.ui.individual.individual_profile.profile_fragrment.Basic_info;

import android.graphics.Bitmap;

/**
 * Created by mindiii on 1/5/18.
 */

public interface BasicInfoPresenter {
    void validationCondition(String specialty,String value,String strength, String address);
    void onDistroy();
}
