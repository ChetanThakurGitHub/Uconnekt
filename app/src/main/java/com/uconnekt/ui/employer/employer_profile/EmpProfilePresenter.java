package com.uconnekt.ui.employer.employer_profile;

import android.graphics.Bitmap;

/**
 * Created by mindiii on 25/4/18.
 */

public interface EmpProfilePresenter {
    void validationCondition(String jobTitle, String specailty, String address, Bitmap bitmap);
    void onDistroy();
}
