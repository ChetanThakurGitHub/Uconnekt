package com.uconnekt.shared_preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.uconnekt.model.FilterList;

public class Filter {

    private SharedPreferences mypref;
    private SharedPreferences.Editor editor;

    public Filter(Context context) {
        String PREF_NAME = "FILTER";
        mypref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();
    }

    public void createSession(FilterList filterList) {
        editor.putString("specialtyId", filterList.specialtyId);
        editor.putString("strengthId", filterList.strengthId);
        editor.putString("valueId", filterList.valueId);
        editor.putString("jobTitleId", filterList.jobTitleId);
        editor.putString("availabilityId", filterList.availabilityId);
        editor.putString("address", filterList.address);
        editor.putString("rating", filterList.rating);
        editor.putString("companyId", filterList.companyId);
        editor.putString("location", filterList.location);
        editor.commit();
    }

    public FilterList getUserInfo() {
        FilterList filterList = new FilterList();
        filterList.specialtyId=(mypref.getString("specialtyId", ""));
        filterList.strengthId=(mypref.getString("strengthId", ""));
        filterList.valueId=(mypref.getString("valueId", ""));
        filterList.jobTitleId=(mypref.getString("jobTitleId", ""));
        filterList.availabilityId=(mypref.getString("availabilityId", ""));
        filterList.address=(mypref.getString("address", ""));
        filterList.rating=(mypref.getString("rating", ""));
        filterList.companyId=(mypref.getString("companyId", ""));
        filterList.location=(mypref.getString("location", ""));
        return filterList;
    }

    public void logout(Activity activity) {
        editor.clear();
        editor.commit();
    }


}
