package com.uconnekt.ui.individual.home;

/*
import android.Manifest;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.SpecialityListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.IndiSearchList;
import com.uconnekt.model.SpecialityList;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.model.MyItem;
import com.uconnekt.ui.individual.fragment.IndiProfileFragment;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class IndiMapFragmentTest extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private Activity activity;
    private MapView mapview;
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
    public TextView tv_for_speName;
    private ArrayList<SpecialityList> arrayList = new ArrayList<>();
    private CardView card_for_viewPro;
    private TextView tv_for_fullName, tv_for_businessName, tv_for_specializationName, tv_for_address,tv_for_nodata;
    private ImageView iv_profile_image, iv_company_logo;
    private RatingBar ratingBar;
    private int position = 0;
    private ClusterManager<MyItem> mClusterManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_indi_map, container, false);
        initView(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        listAdapter = new SpecialityListAdapter(arrayList, this);

        mapview.onCreate(savedInstanceState);
        mapview.onResume();
        mapview.getMapAsync(this);

        permissionAll = new PermissionAll();
        if (permissionAll.checkLocationPermission(activity)) mapview.getMapAsync(this);

        recycler_list.setAdapter(listAdapter);
        getDropDownlist();


        return view;
    }

    private void initView(View view) {
        mapview = view.findViewById(R.id.mapview);
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
        view.findViewById(R.id.layout_for_spName).setOnClickListener(this);
        view.findViewById(R.id.card_for_viewPro).setOnClickListener(this);
        view.findViewById(R.id.btn_for_profile).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_for_spName:
                if (!goneVisi) {
                    layout_for_list.setVisibility(View.VISIBLE);
                    iv_for_arrow.setImageResource(R.drawable.ic_up_arrow);
                    goneVisi = true;
                } else {
                    layout_for_list.setVisibility(View.GONE);
                    iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                    goneVisi = false;
                }
                break;
            case R.id.btn_for_profile:
                if (position != -1) {
                    ((JobHomeActivity) activity).addFragment(IndiProfileFragment.newInstance(searchLists.get(position).userId));
                }
                break;
        }
    }

    @Override
    public void onResume() {
        mapview.onResume();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (map != null) return;
        map = googleMap;
        intiMapView();
        mClusterManager = new ClusterManager<>(getActivity(), map);
        map.setOnCameraIdleListener(mClusterManager);
        try {
            if (permissionAll.checkLocationPermission(activity)) location();
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
                        mapview.getMapAsync(this);
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
                               */
/* MyItem myItem = new MyItem(Double.parseDouble(indiSearchList.latitude), Double.parseDouble(indiSearchList.longitude));
                                mClusterManager.addItem(myItem);*//*

                            }

                            multipleMarkers();
                            //offset = offset + 10;
                         */
/*   multipleMarkers();
                            if (searchLists.size() != 0 && !searchLists.get(0).latitude.isEmpty()) {
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(searchLists.get(0).latitude), Double.parseDouble(searchLists.get(0).longitude)), 14);
                                map.animateCamera(cameraUpdate);
                            }*//*

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

    private void coordinateForMarker(IndiSearchList tmp, int i) {
        float COORDINATE_OFFSET = 0.00010f; // You can change this value according to your need
        double d = 0.00010;// your existing value;
        long bits = Double.doubleToLongBits(d);
        bits++;
        d = Double.longBitsToDouble(bits);
        if (searchLists.contains(tmp)) {
            tmp.latitude = (Float.parseFloat(tmp.latitude) + (d)) + "";
            tmp.longitude = (Float.parseFloat(tmp.longitude) + (COORDINATE_OFFSET)) + "";
        }

      */
/*  for (int i = 0; i <= searchLists.size(); i++) {

            IndiSearchList tmp = searchLists.get(i);
            if(mapAlreadyHasMarkerForLocation(tmp)){
                tmp.latitude = (Float.parseFloat(tmp.latitude) + (i * COORDINATE_OFFSET)) + "";
                tmp.longitude = (Float.parseFloat(tmp.latitude)+(i * COORDINATE_OFFSET)) + "";
            }
        }*//*


    }

    private void createMarker(String latitude, String longitude, final String fullName, int i) {
        if (map == null) {
            return;
        }
        final LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(fullName);

        markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_map_icon))));
        Marker marker = map.addMarker(markerOptions);
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
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(-1);
    }

}
*/
