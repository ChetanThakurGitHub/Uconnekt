package com.uconnekt.ui.employer.fragment;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.SpecialityListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.BusiSearchList;
import com.uconnekt.model.IndiSearchList;
import com.uconnekt.model.MyItem;
import com.uconnekt.model.SpecialityList;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class MapFragment extends Fragment implements View.OnClickListener,
        OnMapReadyCallback , ClusterManager.OnClusterClickListener<MyItem>,
        ClusterManager.OnClusterInfoWindowClickListener<MyItem>,
        ClusterManager.OnClusterItemClickListener<MyItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {
    private HomeActivity activity;
    private MapView mapview;
    public GoogleMap map;
    private RelativeLayout mainlayout;
    private PermissionAll permissionAll;
    private FusedLocationProviderClient mFusedLocationClient;
    public ArrayList<BusiSearchList> searchLists = new ArrayList<>();
    public String specialityID = "",jobTitleId = "",availabilityId = "",locations = "",strengthId = "" ,valueId = "",city = "",state ="",country = "";
    private Double latitude,longitude,clatitude,clongitude;
    public RelativeLayout layout_for_list;
    public Boolean goneVisi = false;
    public ImageView iv_for_arrow;
    private RecyclerView recycler_list;
    private SpecialityListAdapter listAdapter;
    public EditText tv_for_speName;
    private ArrayList<SpecialityList> arrayList = new ArrayList<>();
    private ArrayList<SpecialityList> arrayListBackup = new ArrayList<>();
    private CardView card_for_viewPro;
    private TextView tv_for_fullName,tv_for_address,tv_for_jobTitle,tv_for_nodata;
    private ImageView iv_profile_image;
    private int position = 0;

    private ClusterManager<MyItem> mClusterManager;
    private Random r = new Random();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initView(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        listAdapter = new SpecialityListAdapter(arrayList,this);

        mapview.onCreate(savedInstanceState);
        mapview.onResume();
        mapview.getMapAsync(this);

        permissionAll = new PermissionAll();
        if (permissionAll.checkLocationPermission(activity)) mapview.getMapAsync(this);

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


    private void initView(View view){
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
        tv_for_address = view.findViewById(R.id.tv_for_address);
        tv_for_jobTitle = view.findViewById(R.id.tv_for_jobTitle);
        view.findViewById(R.id.tv_for_speName).setOnClickListener(this);
        view.findViewById(R.id.card_for_viewPro).setOnClickListener(this);
        view.findViewById(R.id.tv_for_profile).setOnClickListener(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_for_speName:
                if (!goneVisi) {
                    layout_for_list.setVisibility(View.VISIBLE);
                    iv_for_arrow.setImageResource(R.drawable.ic_up_arrow);
                    goneVisi = true;
                    tv_for_speName.setFocusableInTouchMode(true);
                }else {
                    layout_for_list.setVisibility(View.GONE);
                    iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                    goneVisi = false;
                    activity.hideKeyboard();
                }
                break;
            case R.id.tv_for_profile:
                if (position!=-1){((HomeActivity)activity).addFragment(ProfileFragment.newInstance(searchLists.get(position).userId));}
                break;
        }
    }

    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
        layout_for_list.setVisibility(View.GONE);
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
        mClusterManager = new ClusterManager<>(activity, map);
        mClusterManager.setAlgorithm(new GridBasedAlgorithm<MyItem>());
        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        try {
            if (permissionAll.checkLocationPermission(activity)) location();
        } catch (Exception e) {
            e.printStackTrace();
        }


       /* map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                position = (int) marker.getTag();
                if (position != -1) {
                    BusiSearchList busiSearchList = searchLists.get(position);
                    card_for_viewPro.setVisibility(View.VISIBLE);
                    if (!busiSearchList.profileImage.isEmpty())
                        Picasso.with(activity).load(busiSearchList.profileImage).into(iv_profile_image);
                    tv_for_fullName.setText(busiSearchList.fullName.isEmpty() ? "NA" : busiSearchList.fullName);
                    tv_for_jobTitle.setText(busiSearchList.jobTitleName.isEmpty() ? "NA" : busiSearchList.jobTitleName);
                    tv_for_address.setText(busiSearchList.address.isEmpty() ? "NA" : busiSearchList.address);
                }else {
                    card_for_viewPro.setVisibility(View.GONE);
                }
                return false;
            }
        });*/
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
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.parmission));
                    getList(specialityID, jobTitleId, availabilityId, locations, strengthId, valueId, 0.0, 0.0, city, state, country);
                }
            }
            break;
        }
    }



   /* private void location() {
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
                                if (latitude != null && longitude != null) {
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
                                    map.animateCamera(cameraUpdate);
                                    createMarkerCurrent(latitude,longitude);
                                    getList(specialityID, jobTitleId, availabilityId, locations, strengthId, valueId, 0.0, 0.0, "");
                                }
                            }
                        }
                    });
        }

    }*/



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
                                Uconnekt.latitude=latitude;
                                Uconnekt.longitude=longitude;
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
                                map.animateCamera(cameraUpdate);
                                clatitude = Uconnekt.latitude;
                                clongitude = Uconnekt.longitude;
                                createMarkerCurrent(clatitude,clongitude);
                                getList(specialityID, jobTitleId, availabilityId, locations, strengthId, valueId, 0.0, 0.0, city, state, country);
                            }else if (Uconnekt.latitude!=0.0){
                                latitude=Uconnekt.latitude;
                                longitude=Uconnekt.longitude;
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
                                map.animateCamera(cameraUpdate);
                                clatitude = Uconnekt.latitude;
                                clongitude = Uconnekt.longitude;
                                createMarkerCurrent(clatitude,clongitude);
                                getList(specialityID, jobTitleId, availabilityId, locations, strengthId, valueId, 0.0, 0.0, city, state, country);
                            }
                        }
                    });
        }

    }

    private void getDropDownlist() {

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();
                        arrayListBackup.clear();
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("opposite_job_title");
                        SpecialityList specialityList1 = new SpecialityList();
                        specialityList1.specializationId = "";
                        specialityList1.specializationName = "All";
                        arrayList.add(specialityList1);
                        for (int i = 0; i < results.length(); i++) {
                            SpecialityList specialityList = new SpecialityList();
                            JSONObject object = results.getJSONObject(i);
                            specialityList.specializationId = object.getString("jobTitleId");
                            specialityList.specializationName = object.getString("jobTitleName");
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

    public void getList(String specialityIDs, String jobTitleIds, String availabilityIds, String address, String strengthIds, String valueIds, Double latitude, Double longitude, String citys, String states, String countrys){
        specialityID = specialityIDs;city = citys;state = states;country=countrys;
        jobTitleId = jobTitleIds;
        availabilityId = availabilityIds;
        locations = address;
        strengthId = strengthIds;
        valueId = valueIds;
       if (clatitude!=null&&clongitude!=null)createMarkerCurrent(clatitude,clongitude);
        card_for_viewPro.setVisibility(View.GONE);

        if (latitude != 0.0 && longitude != 0.0){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
            map.animateCamera(cameraUpdate);
        }

        new VolleyGetPost(activity, AllAPIs.BUSI_SEARCH_LIST,true,"List",false ) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equalsIgnoreCase("success")){
                        JSONArray array = object.getJSONArray("searchList");
                        if (array!= null){
                            for (int i=0; i< array.length(); i++){

                                JSONObject jsonObject = array.getJSONObject(i);
                                BusiSearchList busiSearchList = new Gson().fromJson(jsonObject.toString(),BusiSearchList.class);
                                searchLists.add(busiSearchList);
                            }
                            //offset = offset + 10;
                            multipleMarkers();
                        /*    if (searchLists.size() != 0 && !searchLists.get(0).latitude.isEmpty()) {
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(searchLists.get(0).latitude), Double.parseDouble(searchLists.get(0).longitude)), 10);
                                map.animateCamera(cameraUpdate);
                            }*/
                            if (searchLists.size() == 0)MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.no_map_data));
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
                params.put("speciality_id",specialityID);
                params.put("job_title",jobTitleId);
                params.put("availability",availabilityId);
                params.put("location",locations);
                params.put("strength",strengthId);
                params.put("city",city==null?"":city);
                params.put("state",state==null?"":state);
                params.put("country",country==null?"":country);
                params.put("value",valueId);
                params.put("limit","20");
                params.put("offset","0");
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
        if (searchLists == null) {
            return;
        } else {
            for (int i = 0; i < searchLists.size(); i++) {
                BusiSearchList busiSearchList=searchLists.get(i);

                if (busiSearchList.latitude != null && !busiSearchList.latitude.isEmpty() && !busiSearchList.latitude.equals("null")) {
                    createMarker(busiSearchList.latitude, busiSearchList.longitude, searchLists.get(i).fullName, i);
                }
            }
        }
    }

    private void createMarker(String latitude, String longitude, final String fullName, int i) {
        if (map == null) {
            return;
        }

        double lat = Double.parseDouble(latitude)+ r.nextGaussian()*0.00003;
        double lng = Double.parseDouble(longitude)+ r.nextGaussian()*0.00003;

        MyItem myItem = new MyItem(lat, lng,fullName,R.drawable.ic_map_icon,i);
        mClusterManager.addItem(myItem);

        mClusterManager.cluster();
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

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        // Show a toast with some info when the cluster is clicked.
        // String firstName = cluster.getItems().iterator().next().name;
        //Toast.makeText(activity, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

      /*  position = (int) cluster.getItems().iterator().next().position;
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
        }*/

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 17));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MyItem> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(MyItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
        position = item.positionIcon();
        if (position != -1) {
            BusiSearchList busiSearchList = searchLists.get(position);
            card_for_viewPro.setVisibility(View.VISIBLE);
            if (!busiSearchList.profileImage.isEmpty())
                Picasso.with(activity).load(busiSearchList.profileImage).into(iv_profile_image);
            tv_for_fullName.setText(busiSearchList.fullName.isEmpty() ? "NA" : busiSearchList.fullName);
            tv_for_jobTitle.setText(busiSearchList.jobTitleName.isEmpty() ? "NA" : busiSearchList.jobTitleName);
            tv_for_address.setText(busiSearchList.address.isEmpty() ? "NA" : busiSearchList.address);
        }else {
            card_for_viewPro.setVisibility(View.GONE);
        }

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MyItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }


}
