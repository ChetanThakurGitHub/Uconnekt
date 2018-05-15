package com.uconnekt.ui.employer.employer_profile;

import android.graphics.Bitmap;

/**
 * Created by mindiii on 25/4/18.
 */

public interface EmpProfileIntractor {

    interface OnProfileEndListener{
        void onJobTitleError();
        void onSpecialtyError();
        void onAddressError();
        void onCompanyLogoError();
        void onSuccess();
        void onNevigator();
    }

    void profile(String jobTitle, String specialty, String address, Bitmap bitmap, OnProfileEndListener listener);
}
