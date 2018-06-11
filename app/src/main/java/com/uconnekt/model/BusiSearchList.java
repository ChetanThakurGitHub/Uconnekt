package com.uconnekt.model;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

public class BusiSearchList implements Serializable {
    public String userId = "";
    public String fullName = "";
    public String specializationName = "";
    public String company = "";
    public String jobTitleName = "";
    public String address = "";
    public String latitude = "";
    public String longitude = "";
    public String profileImage = "";
    public Marker marker;
}
