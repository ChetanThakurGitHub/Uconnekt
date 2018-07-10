package com.uconnekt.model;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

public class IndiSearchList implements Serializable{
    public String userId = "";
    public String fullName = "";
    public String businessName = "";
    public String specializationName = "";
    public String jobTitleName = "";
    public String address = "";
    public String latitude = "";
    public String longitude = "";
    public String rating = "";
    public String profileImage = "";
    public String company_logo = "";
    public Marker marker ;

   /* @Override
    public int hashCode() {

        float value = Float.parseFloat(latitude)+Float.parseFloat(longitude);
        return (int) value;
    }*/
}
