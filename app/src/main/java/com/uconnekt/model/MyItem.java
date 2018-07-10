package com.uconnekt.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.uconnekt.R;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    public  String name;
    public  int profilePhoto;
    public  int position;


    public MyItem(double lat, double lng, String name, int pictureResource,int position) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
        this.name = name;
        this.position = position;
        profilePhoto = pictureResource;
    }

  public MyItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() { return mTitle; }

    @Override
    public String getSnippet() { return mSnippet; }

    @Override
    public int getResourceId() {
        return R.drawable.ic_map_icon;
    }

    @Override
    public int positionIcon() {return position;
    }

    @Override
    public String getName() {
        return name;
    }

}
