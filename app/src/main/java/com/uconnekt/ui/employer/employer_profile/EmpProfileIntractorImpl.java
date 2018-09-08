package com.uconnekt.ui.employer.employer_profile;

import android.graphics.Bitmap;

/**
 * Created by mindiii on 25/4/18.
 */

public class EmpProfileIntractorImpl implements EmpProfileIntractor {
    @Override
    public void profile(String jobTitle, String specialty, String address, Bitmap bitmap, OnProfileEndListener listener) {

        if (jobTitle == null || jobTitle.equalsIgnoreCase("")){
            listener.onJobTitleError();
            return;
        }

        if (specialty == null || specialty.equalsIgnoreCase("")){
            listener.onSpecialtyError();
            return;
        }

        if (address == null || address.equalsIgnoreCase("")){
            listener.onAddressError();
            return;
        }

        if (bitmap == null){
            listener.onCompanyLogoError();
            return;
        }


        listener.onSuccess();
    }
}
