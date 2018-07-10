package com.uconnekt.ui.individual.home;
/*
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidmapsextensions.ClusterGroup;
import com.androidmapsextensions.ClusterOptions;
import com.androidmapsextensions.ClusterOptionsProvider;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.SpecialityListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.IndiSearchList;
import com.uconnekt.model.SpecialityList;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.fragment.IndiProfileFragment;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;*/

public class IndiMapFragmentOtherCluster {}/*extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private JobHomeActivity activity;
    //private MapView mapview;
    public GoogleMap map;
    private RelativeLayout mainlayout;
    private PermissionAll permissionAll;
    private FusedLocationProviderClient mFusedLocationClient;
    public ArrayList<IndiSearchList> searchLists = new ArrayList<>();
    public String specialtyId = "", ratingNo = "", company = "", address = "", city = "", state = "", country = "";
    private Double latitude, longitude, clatitude, clongitude;
    public RelativeLayout layout_for_list;
    public Boolean goneVisi = false;
    public ImageView iv_for_arrow;
    private RecyclerView recycler_list;
    private SpecialityListAdapter listAdapter;
    public EditText tv_for_speName;
    private ArrayList<SpecialityList> arrayList = new ArrayList<>();
    private ArrayList<SpecialityList> arrayListBackup = new ArrayList<>();
    private CardView card_for_viewPro;
    private TextView tv_for_fullName, tv_for_businessName, tv_for_specializationName, tv_for_address,tv_for_nodata;
    private ImageView iv_profile_image, iv_company_logo;
    private RatingBar ratingBar;
    public int position = 0;

    int i = 0;

    private OverlappingMarkerSpiderfier oms;
    private ClusteringSettings clusterSettings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_indi_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getExtendedMapAsync(this);
        initView(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        listAdapter = new SpecialityListAdapter(arrayList, this);

        onCreate(savedInstanceState);
      //  mapview.onResume();

        permissionAll = new PermissionAll();
        if (permissionAll.checkLocationPermission(activity)) //mapview.getMapAsync(this);

        recycler_list.setAdapter(listAdapter);
        getDropDownlist();

        tv_for_speName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goneVisi)filter(s.toString());
            }
        });

        tv_for_speName.setFocusableInTouchMode(false);

        return view;
    }

    private void filter(String text){
        if (text.isEmpty()){
            arrayList.clear();
            arrayList.addAll(arrayListBackup);
            listAdapter.notifyDataSetChanged();
            return;
        }
        ArrayList<SpecialityList> temp = new ArrayList<>();
        for(SpecialityList d: arrayListBackup){
            if(d.specializationName.toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        arrayList.clear();
        arrayList.addAll(temp);
        tv_for_nodata.setVisibility(arrayList.isEmpty()?View.VISIBLE:View.GONE);
        listAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
       // mapview = view.findViewById(R.id.mapview);
        tv_for_nodata = view.findViewById(R.id.tv_for_nodata);
        mainlayout = view.findViewById(R.id.mainlayout);
        layout_for_list = view.findViewById(R.id.layout_for_list);
        iv_for_arrow = view.findViewById(R.id.iv_for_arrow);
        recycler_list = view.findViewById(R.id.recycler_list);
        tv_for_speName = view.findViewById(R.id.tv_for_speName);
        card_for_viewPro = view.findViewById(R.id.card_for_viewPro);
        tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        iv_profile_image = view.findViewById(R.id.iv_profile_image);
        tv_for_businessName = view.findViewById(R.id.tv_for_businessName);
        iv_company_logo = view.findViewById(R.id.iv_company_logo);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        ratingBar = view.findViewById(R.id.ratingBar);
        tv_for_specializationName = view.findViewById(R.id.tv_for_specializationName);
        view.findViewById(R.id.tv_for_speName).setOnClickListener(this);
        view.findViewById(R.id.card_for_viewPro).setOnClickListener(this);
        view.findViewById(R.id.btn_for_profile).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobHomeActivity) context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_for_speName:
                if (!goneVisi) {
                    layout_for_list.setVisibility(View.VISIBLE);
                    iv_for_arrow.setImageResource(R.drawable.ic_up_arrow);
                    goneVisi = true;
                    tv_for_speName.setFocusableInTouchMode(true);
                } else {
                    layout_for_list.setVisibility(View.GONE);
                    iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                    goneVisi = false;
                    activity.hideKeyboard();
                }
                break;
            case R.id.btn_for_profile:
                if (position != -1) {
                    (activity).addFragment(IndiProfileFragment.newInstance(searchLists.get(position).userId));
                }
                break;
        }
    }

    @Override
    public void onResume() {
       // mapview.onResume();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        super.onResume();
        layout_for_list.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
       // mapview.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        updateClusteringRadius();
        intiMapView();
        try {
            if (permissionAll.checkLocationPermission(activity)) location();
        }catch (Exception e){
            e.printStackTrace();
        }

        *//*map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                position = (int) marker.getTag();
                if (position != -1) {
                    IndiSearchList indiSearchList = searchLists.get(position);
                    card_for_viewPro.setVisibility(View.VISIBLE);
                    if (!indiSearchList.profileImage.isEmpty())
                        Picasso.with(activity).load(indiSearchList.profileImage).into(iv_profile_image);
                    if (!indiSearchList.company_logo.isEmpty())
                        Picasso.with(activity).load(indiSearchList.company_logo).into(iv_company_logo);
                    tv_for_fullName.setText(indiSearchList.fullName.isEmpty() ? "NA" : indiSearchList.fullName);
                    tv_for_businessName.setText(indiSearchList.businessName.isEmpty() ? "NA" : indiSearchList.businessName);
                    tv_for_specializationName.setText(indiSearchList.specializationName.isEmpty() ? "NA" : indiSearchList.specializationName);
                    tv_for_address.setText(indiSearchList.address.isEmpty() ? "NA" : indiSearchList.address);
                    ratingBar.setRating(indiSearchList.rating.isEmpty() ? 0 : Float.parseFloat(indiSearchList.rating));
                } else {
                    card_for_viewPro.setVisibility(View.GONE);
                }
                return false;
            }
        });*//*

       map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick (Marker marker){
                // We need to figure out if it was a seperate marker or a cluster marker




               if (marker.isCluster()) {
                    if (map.getCameraPosition().zoom >= 15) //Play around with this. We assume the SPIDERFICATION_ZOOM_THRSH is constant and never changes.

                      if(marker!=null){
                          oms.spiderListener(marker);
                      }

                    else {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                marker.getPosition(),
                                map.getCameraPosition().zoom + dynamicZoomLevel()));
                        updateClusteringRadius();
                    }

                    return true;
                }
                else{
                   position = (int)marker.getTag();
                   if (position != -1) {
                       IndiSearchList indiSearchList = searchLists.get(position);
                       card_for_viewPro.setVisibility(View.VISIBLE);
                       if (!indiSearchList.profileImage.isEmpty())
                           Picasso.with(activity).load(indiSearchList.profileImage).into(iv_profile_image);
                       if (!indiSearchList.company_logo.isEmpty())
                           Picasso.with(activity).load(indiSearchList.company_logo).into(iv_company_logo);
                       tv_for_fullName.setText(indiSearchList.fullName.isEmpty() ? "NA" : indiSearchList.fullName);
                       tv_for_businessName.setText(indiSearchList.businessName.isEmpty() ? "NA" : indiSearchList.businessName);
                       tv_for_specializationName.setText(indiSearchList.specializationName.isEmpty() ? "NA" : indiSearchList.specializationName);
                       tv_for_address.setText(indiSearchList.address.isEmpty() ? "NA" : indiSearchList.address);
                       ratingBar.setRating(indiSearchList.rating.isEmpty() ? 0 : Float.parseFloat(indiSearchList.rating));
                   } else {
                       card_for_viewPro.setVisibility(View.GONE);
                   }
               }

                return false;
            }
        });

        }


    private void intiMapView() {
        map.getUiSettings().setMyLocationButtonEnabled(true);
        MapsInitializer.initialize(activity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                       // mapview.getMapAsync(this);
                    }
                } else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.parmission));
                }
            }
            break;
        }
    }

    private void location() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionAll.checkLocationPermission(activity);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = Double.valueOf(String.valueOf(location.getLatitude()));
                                longitude = Double.valueOf(String.valueOf(location.getLongitude()));
                                Uconnekt.latitude = latitude;
                                Uconnekt.longitude = longitude;
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
                                map.animateCamera(cameraUpdate);
                                clatitude = Uconnekt.latitude;
                                clongitude = Uconnekt.longitude;
                                createMarkerCurrent(clatitude, clongitude);
                                getList(specialtyId, ratingNo, company, address, 0.0, 0.0, city, state, country);
                            } else {
                                if (Uconnekt.latitude != 0.0) {
                                    latitude = Uconnekt.latitude;
                                    longitude = Uconnekt.longitude;
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
                                    map.animateCamera(cameraUpdate);
                                    clatitude = Uconnekt.latitude;
                                    clongitude = Uconnekt.longitude;
                                    createMarkerCurrent(clatitude, clongitude);
                                    getList(specialtyId, ratingNo, company, address, 0.0, 0.0, city, state, country);
                                }
                            }
                        }
                    });
        }
    }

    private void getDropDownlist() {

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list", true) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();
                        arrayListBackup.clear();
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("opposite_speciality_list");
                        SpecialityList specialityList1 = new SpecialityList();
                        specialityList1.specializationId = "";
                        specialityList1.specializationName = "All";
                        arrayList.add(specialityList1);
                        for (int i = 0; i < results.length(); i++) {
                            SpecialityList specialityList = new SpecialityList();
                            JSONObject object = results.getJSONObject(i);
                            specialityList.specializationId = object.getString("specializationId");
                            specialityList.specializationName = object.getString("specializationName");
                            arrayList.add(specialityList);
                        }
                        arrayListBackup.addAll(arrayList);
                        listAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(activity, NetworkActivity.class));
            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    public void getList(String specialtyIds, String ratingNos, String companys, String location, Double latitude, Double longitude, final String citys, String states, String countrys) {
        card_for_viewPro.setVisibility(View.GONE);
        if (clatitude != null && clongitude != null) createMarkerCurrent(clatitude, clongitude);
        specialtyId = specialtyIds;
        ratingNo = ratingNos;
        company = companys;
        address = location;
        city = citys;
        state = states;
        country = countrys;


        if (latitude != 0.0 && longitude != 0.0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
            map.animateCamera(cameraUpdate);
        }

        new VolleyGetPost(activity, AllAPIs.INDI_SEARCH_LIST, true, "List", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONArray array = object.getJSONArray("searchList");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                IndiSearchList indiSearchList = new Gson().fromJson(jsonObject.toString(), IndiSearchList.class);
                                searchLists.add(indiSearchList);

                            }
                            //offset = offset + 10;
                            multipleMarkers();
                            if (searchLists.size() != 0 && !searchLists.get(0).latitude.isEmpty()) {
                                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(searchLists.get(0).latitude), Double.parseDouble(searchLists.get(0).longitude)), 14);
                                //map.animateCamera(cameraUpdate);
                                LatLng lat = new LatLng(Double.parseDouble(searchLists.get(0).latitude),Double.parseDouble(searchLists.get(0).longitude));
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lat,12.5f));

                            }
                            if (searchLists.size() == 0)
                                MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.no_result_found));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(activity, NetworkActivity.class));
            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("speciality_id", specialtyId);
                params.put("rating", ratingNo);
                params.put("company", company);
                params.put("location", address);
                params.put("city", city == null ? "" : city);
                params.put("state", state == null ? "" : state);
                params.put("state", country == null ? "" : country);
                params.put("limit", "20");
                params.put("offset", "0");
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void multipleMarkers() {
        if (searchLists != null) {
            for (int i = 0; i < searchLists.size(); i++) {
                IndiSearchList indiSearchList = searchLists.get(i);
                //coordinateForMarker(indiSearchList,i);

                if (indiSearchList.latitude != null && !indiSearchList.latitude.isEmpty() && !indiSearchList.latitude.equals("null")) {
                    createMarker(indiSearchList.latitude, indiSearchList.longitude, searchLists.get(i).fullName, i);
                }
            }
        }
    }



    private void createMarker(String latitude, String longitude, final String fullName, int i) {
        if (map == null) {
            return;
        }
       *//* final LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(fullName);

        markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_map_icon))));*//*

         //  for(i=0;i<100;i++){

               MarkerOptions markerOptions = new MarkerOptions();
               Random r = new Random();
               double value = r.nextGaussian()*0.0001;

        Marker marker = map.addMarker(markerOptions
                       .title(fullName)
                       .position(new LatLng(Double.parseDouble(latitude) + r.nextGaussian() * value,
                               Double.parseDouble(longitude) + r.nextGaussian() * value))
                       .clusterGroup(ClusterGroup.FIRST_USER)
                       .icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_map_icon))))
               );
          // }
        marker.setTag(i);


    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
        assert (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)) != null;
        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.marker);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getWidth(), customMarkerView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private void createMarkerCurrent(Double latitude, Double longitude) {
        if (map == null) {
            return;
        }

        final LatLng latLng = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("current Location");

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.loctions_ic));
     //   Marker marker = map.addMarker(markerOptions);
       // marker.setTag(-1);
    }

    private void addDemoMarkersAround(GoogleMap map, LatLng center){
        MarkerOptions options = new MarkerOptions();
        Random r = new Random();
        for (int k = 0; k < 20; k++){
            map.addMarker(options
                    .title("Place " + k)
                    .position(new LatLng(center.latitude  + r.nextGaussian()*0.002,
                            center.longitude + r.nextGaussian()*0.002))
                    .clusterGroup(ClusterGroup.FIRST_USER)
            );
        }
    }

    private float dynamicZoomLevel() {
        float currZoomLvl = map.getCameraPosition().zoom;
        final float minZoomStepAtZoom = 17.3F, minZoomStep = 1.8F;
        final float maxZoomStepAtZoom = 7F, maxZoomStep = 2.8F;

        if (currZoomLvl >= minZoomStepAtZoom)
            return minZoomStep;
        else if (currZoomLvl <= maxZoomStepAtZoom)
            return maxZoomStep;
        else
            // simple interpolation:
            return (currZoomLvl - maxZoomStepAtZoom)
                    * (maxZoomStep - minZoomStep)
                    / (maxZoomStepAtZoom - minZoomStepAtZoom) + maxZoomStep;
    }

    private int clusterRadiusCalculation() {
        final int minRad = 0, maxRad = 150;
        final float minRadZoom = 10F, maxRadZoom = 7.333F;

        if (map.getCameraPosition().zoom >= minRadZoom) {

            return minRad;

        } else if (map.getCameraPosition().zoom <= maxRadZoom)
            return maxRad;
        else
            // simple interpolation:
            return (int) (maxRad - (maxRadZoom - map.getCameraPosition().zoom) *
                    (maxRad - minRad) / (maxRadZoom - minRadZoom));
    }

    private void updateClusteringRadius() {


        if (clusterSettings == null) {
            clusterSettings = new ClusteringSettings();
            clusterSettings.addMarkersDynamically(true);
            clusterSettings.clusterSize(clusterRadiusCalculation());
            *//** Based on pl.mg6.android.maps.extensions.demo.ClusterGroupsFragment *//*
            ClusterOptionsProvider provider = new ClusterOptionsProvider() {
                @Override public ClusterOptions getClusterOptions(List<Marker> markers) {
                    float hue;
                    switch (markers.get(0).getClusterGroup()) {
                        case ClusterGroup.FIRST_USER:
                            hue = BitmapDescriptorFactory.HUE_YELLOW;
                            break;
                        case ClusterGroup.DEFAULT: // The color of "spiderfied at least once" clusters
                            hue = BitmapDescriptorFactory.HUE_YELLOW;
                            break;
                        default: // ClusterGroup.NOT_CLUSTERED:
                            hue = BitmapDescriptorFactory.HUE_YELLOW;
                            break;
                    }
                    BitmapDescriptor defaultIcon = BitmapDescriptorFactory.defaultMarker(hue);
                    return new ClusterOptions().icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_map_icon)));                }
            };
            map.setClustering(clusterSettings.clusterOptionsProvider(provider));
        } else if (map.getCameraPosition().zoom > 13F){

        } else {
            clusterSettings.clusterSize(clusterRadiusCalculation());
        }
    }

}
*/