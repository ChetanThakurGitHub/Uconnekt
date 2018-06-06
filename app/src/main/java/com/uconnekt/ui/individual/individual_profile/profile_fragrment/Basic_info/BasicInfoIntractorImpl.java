package com.uconnekt.ui.individual.individual_profile.profile_fragrment.Basic_info;


public class BasicInfoIntractorImpl implements BasicInfoIntractor {

    @Override
    public void basicInfo(String specialty, String value, String strength, String address, OnProfileEndListener listener) {

        if (specialty.equalsIgnoreCase("")){
            listener.onSpecialtyError();
            return;
        }

        else if (value.equalsIgnoreCase("")){
            listener.onValueError();
            return;
        }

        else if (strength.equalsIgnoreCase("")){
            listener.onStrengthError();
            return;
        }

        else if (address.equalsIgnoreCase("")){
            listener.onAddressError();
            return;
        }

        listener.onSuccess();
    }
}
