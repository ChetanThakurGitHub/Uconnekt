package com.uconnekt.ui.employer.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.uconnekt.R;
import com.uconnekt.adapter.WeekSpAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.helper.GioAddressTask;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.JobTitle;
import com.uconnekt.model.Weeks;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.sp.OnSpinerItemClick;
import com.uconnekt.sp.SpinnerDialog;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.uconnekt.util.Constant.RESULT_OK;

public class FilterFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private HomeActivity activity;
    private ArrayList<JobTitle> arrayList = new ArrayList<>();
    private ArrayList<JobTitle> strengthsList = new ArrayList<>();
    private ArrayList<JobTitle> valuesList = new ArrayList<>();
    private ArrayList<JobTitle> jobTitleList = new ArrayList<>();
    private ArrayList<Weeks> weekList = new ArrayList<>();
    private Spinner sp_for_availability;
    private WeekSpAdapter weekSpAdapter;
    private TextView tv_for_address,tv_for_jobTitle,tv_for_aofs,tv_for_value,tv_for_strength;
    private LinearLayout mainlayout;
    private RelativeLayout layout_for_address;
    private String specialtyId = "",strengthId = "",valueId = "",jobTitleId = "",availabilityId = "",city = "",state = "" ,country;
    private SearchFragment searchFragment;
    private MapFragment mapFragment;
    private Double latitude = 0.0,longitude = 0.0;
   private SpinnerDialog spinnerDialog,spinnerDialog2,spinnerDialog3,spinnerDialog4;

    public static  FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        initView(view);

        weekSpAdapter = new WeekSpAdapter(activity, weekList,R.layout.week_sp);
        sp_for_availability.setAdapter(weekSpAdapter);

        getlist();
        sp_for_availability.setOnItemSelectedListener(this);

        return view;
    }

    public  void setFragment(SearchFragment searchFragment){
        this.searchFragment=searchFragment;
    }


    public  void setOtherFragment(MapFragment mapFragment){
        this.mapFragment=mapFragment;
    }

    private void initView(View view) {
        view.findViewById(R.id.mainlayout).setOnClickListener(this);
        view.findViewById(R.id.btn_for_search).setOnClickListener(this);
        view.findViewById(R.id.layout_for_jobTittle).setOnClickListener(this);
        view.findViewById(R.id.layout_for_aofs).setOnClickListener(this);
        view.findViewById(R.id.layout_for_values).setOnClickListener(this);
        view.findViewById(R.id.layout_for_strength).setOnClickListener(this);
        tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        tv_for_strength = view.findViewById(R.id.tv_for_strength);
        tv_for_value = view.findViewById(R.id.tv_for_value);
        sp_for_availability = view.findViewById(R.id.sp_for_availability);
        tv_for_jobTitle = view.findViewById(R.id.tv_for_jobTitle);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        mainlayout = view.findViewById(R.id.mainlayout);
        layout_for_address = view.findViewById(R.id.layout_for_address);
        layout_for_address.setOnClickListener(this);
        activity.findViewById(R.id.iv_for_circular_arrow).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_for_address:
                PermissionAll permissionAll = new PermissionAll();
                if (permissionAll.checkLocationPermission(activity))addressClick();
                break;
            case R.id.btn_for_search:
                btnClick();
                break;
            case R.id.iv_for_circular_arrow:
                refresh();
                break;
            case R.id.layout_for_jobTittle:
                spinnerDialog.showSpinerDialog();
                break;
            case R.id.layout_for_aofs:
                spinnerDialog2.showSpinerDialog();
                break;
            case R.id.layout_for_values:
                spinnerDialog4.showSpinerDialog();
                break;
            case R.id.layout_for_strength:
                spinnerDialog3.showSpinerDialog();
                break;
        }
    }

    private void refresh(){
        specialtyId = "";strengthId = "";valueId = "";jobTitleId = "";availabilityId = "";

        if (searchFragment != null)searchFragment.tv_for_speName.setText("");
        if (mapFragment != null)mapFragment.tv_for_speName.setText("");

        sp_for_availability.setAdapter(weekSpAdapter);
        tv_for_jobTitle.setText("");
        tv_for_aofs.setText("");
        tv_for_value.setText("");
        tv_for_strength.setText("");

        weekSpAdapter.notifyDataSetChanged();
        tv_for_address.setText("");
    }

    private  void btnClick(){
        String address = tv_for_address.getText().toString().trim();
        if (searchFragment != null)searchFragment.layout_for_list.setVisibility(View.GONE);
        if (searchFragment != null)searchFragment.offset = 0;
        if (searchFragment != null)searchFragment.searchLists.clear();
        if (searchFragment != null)searchFragment.mSwipeRefreshLayout.setRefreshing(true);
        if (searchFragment != null)searchFragment.getList(specialtyId,jobTitleId,availabilityId,address,strengthId,valueId,city,state,country);

        if (mapFragment != null)mapFragment.layout_for_list.setVisibility(View.GONE);
      //  if (mapFragment != null)mapFragment.offset = 0;
        if (mapFragment != null)mapFragment.searchLists.clear();
        if (mapFragment != null)mapFragment.map.clear();
        if (mapFragment != null)mapFragment.mClusterManager.clearItems();
        if (mapFragment != null)mapFragment.getList(specialtyId,jobTitleId,availabilityId,address,strengthId,valueId,latitude,longitude,city,state,country);

        activity.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addressClick() {
        try {
            layout_for_address.setEnabled(false);
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity);
            startActivityForResult(intent, Constant.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    } // method for address button click

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            layout_for_address.setEnabled(true);

            if (mainlayout != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(mainlayout.getWindowToken(), 0);
            }

            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(activity, data);

                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                try {
                    latlong(latitude,longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String placename = String.valueOf(place.getAddress());
                tv_for_address.setText(placename);
            }
        }

    } // onActivityResult

    private void latlong(Double latitude, Double longitude) throws IOException {

        LatLng latLng = new LatLng(latitude,longitude);

        new GioAddressTask(activity, latLng, new GioAddressTask.LocationListner() {
            @Override
            public void onSuccess(com.uconnekt.model.Address address) {
                city = address.getCity();
                state = address.getState();
                country = address.getCountry();
            }
        }).execute();

    } // latlog to address find

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        addressClick();
                    }
                } else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.parmission));
                }
            }
            break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getlist();
        }
        Constant.NETWORK_CHECK = 0;
    }

    private void getlist() {

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list",true) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject result = jsonObject.getJSONObject("result");

                        /*JobTitle jobTitle = new JobTitle();
                        jobTitle.jobTitleId = "";
                        jobTitle.jobTitleName = "";
                        strengthsList.add(jobTitle);
                        valuesList.add(jobTitle);*/

                        JSONArray results = result.getJSONArray("opposite_speciality_list");
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("specializationId");
                            jobTitles.jobTitleName = object.getString("specializationName");
                            arrayList.add(jobTitles);
                        }
                        spinnerDialog2 = new SpinnerDialog(activity, arrayList, getString(R.string.area_of_specialty_s));
                        spinnerDialog2.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                specialtyId = job.jobTitleId;
                                tv_for_aofs.setText(job.jobTitleName);
                            }
                        });

                        JSONArray results2 = result.getJSONArray("opposite_job_title");
                        for (int i = 0; i < results2.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results2.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("jobTitleId");
                            jobTitles.jobTitleName = object.getString("jobTitleName");
                            jobTitleList.add(jobTitles);
                        }
                        spinnerDialog = new SpinnerDialog(activity, jobTitleList, getString(R.string.select_job_tittle));
                        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                jobTitleId = job.jobTitleId;
                                tv_for_jobTitle.setText(job.jobTitleName);
                                if (searchFragment != null)searchFragment.tv_for_speName.setText(job.jobTitleName);
                            }
                        });

                        final JSONArray strenght = result.getJSONArray("strenght_list");
                        for (int i = 0; i < strenght.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = strenght.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("strengthId");
                            jobTitles.jobTitleName = object.getString("strengthName");
                            strengthsList.add(jobTitles);
                        }
                        spinnerDialog3 = new SpinnerDialog(activity, strengthsList, "Select strength");
                        spinnerDialog3.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                strengthId = job.jobTitleId;
                                tv_for_strength.setText(job.jobTitleName);
                            }
                        });

                        JSONArray values = result.getJSONArray("value_list");
                        for (int i = 0; i < values.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = values.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("valueId");
                            jobTitles.jobTitleName = object.getString("valueName");
                            valuesList.add(jobTitles);
                        }
                        spinnerDialog4 = new SpinnerDialog(activity, valuesList, "Select value");
                        spinnerDialog4.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                valueId = job.jobTitleId;
                                tv_for_value.setText(job.jobTitleName);
                            }
                        });

                        Weeks week0 = new Weeks();
                        week0.week = "";
                        weekList.add(week0);
                        JSONObject week = result.getJSONObject("availability_list");
                        Weeks week5 = new Weeks();
                        week5.week = week.getString("immediate");
                        weekList.add(week5);
                        Weeks week1 = new Weeks();
                        week1.week = week.getString("1-4");
                        weekList.add(week1);
                        Weeks week2 = new Weeks();
                        week2.week = week.getString("5-8");
                        weekList.add(week2);
                        Weeks week3 = new Weeks();
                        week3.week = week.getString("9-12");
                        weekList.add(week3);
                        Weeks week4 = new Weeks();
                        week4.week = week.getString("12+");
                        weekList.add(week4);
                        weekSpAdapter.notifyDataSetChanged();

                        setOldData();

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

    private void setOldData(){
        if(searchFragment != null) {
            jobTitleId = searchFragment.jobTitleId;
            if (!jobTitleId.isEmpty()) {
                for (int i = 0; jobTitleList.size() > i; i++) {
                    if (jobTitleList.get(i).jobTitleId.equals(jobTitleId)) {
                        tv_for_jobTitle.setText(jobTitleList.get(i).jobTitleName);
                        break;
                    }
                }
            }
            specialtyId = searchFragment.specialityID;
            if (!specialtyId.isEmpty()) {
                for (int i = 0; arrayList.size() > i; i++) {
                    if (arrayList.get(i).jobTitleId.equals(specialtyId)) {
                        tv_for_aofs.setText(arrayList.get(i).jobTitleName);
                        break;
                    }
                }
            }

            availabilityId = searchFragment.availabilityId;
            if (!availabilityId.isEmpty()) {
                for (int i = 0; weekList.size() > i; i++) {
                    if (weekList.get(i).week.equals(availabilityId)) {
                        sp_for_availability.setSelection(i);
                        break;
                    }
                }
            }

            strengthId = searchFragment.strengthId;
            if (!strengthId.isEmpty()) {
                for (int i = 0; strengthsList.size() > i; i++) {
                    if (strengthsList.get(i).jobTitleId.equals(strengthId)) {
                        tv_for_strength.setText(strengthsList.get(i).jobTitleName);
                        break;
                    }
                }
            }

            valueId = searchFragment.valueId;
            if (!valueId.isEmpty()) {
                for (int i = 0; valuesList.size() > i; i++) {
                    if (valuesList.get(i).jobTitleId.equals(valueId)) {
                        tv_for_value.setText(valuesList.get(i).jobTitleName);
                        break;
                    }
                }
            }

            tv_for_address.setText(searchFragment.location);
            city = searchFragment.city;
            state = searchFragment.state;
            country = searchFragment.country;

        }else if(mapFragment != null){

                jobTitleId = mapFragment.jobTitleId;
                if (!jobTitleId.isEmpty()) {
                    for (int i = 0; jobTitleList.size() > i; i++) {
                        if (jobTitleList.get(i).jobTitleId.equals(jobTitleId)) {
                            tv_for_jobTitle.setText(jobTitleList.get(i).jobTitleName);
                            break;
                        }
                    }
                }
                specialtyId = mapFragment.specialityID;
                if (!specialtyId.isEmpty()) {
                    for (int i = 0; arrayList.size() > i; i++) {
                        if (arrayList.get(i).jobTitleId.equals(specialtyId)) {
                            tv_for_aofs.setText(arrayList.get(i).jobTitleName);
                            break;
                        }
                    }
                }

                availabilityId = mapFragment.availabilityId;
            if (!availabilityId.isEmpty()) {
                for (int i = 0; weekList.size() > i; i++) {
                    if (weekList.get(i).week.equals(availabilityId)) {
                        sp_for_availability.setSelection(i);
                        break;
                    }
                }
            }

                strengthId = mapFragment.strengthId;
                if (!strengthId.isEmpty()) {
                    for (int i = 0; strengthsList.size() > i; i++) {
                        if (strengthsList.get(i).jobTitleId.equals(strengthId)) {
                            tv_for_strength.setText(strengthsList.get(i).jobTitleName);
                            break;
                        }
                    }
                }

                valueId = mapFragment.valueId;
                if (!valueId.isEmpty()) {
                    for (int i = 0; valuesList.size() > i; i++) {
                        if (valuesList.get(i).jobTitleId.equals(valueId)) {
                            tv_for_value.setText(valuesList.get(i).jobTitleName);
                            break;
                        }
                    }
                }

                tv_for_address.setText(mapFragment.locations);
                city = mapFragment.city;
                state = mapFragment.state;
                country = mapFragment.country;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_for_availability:
                Weeks weeks = weekList.get(position);
                availabilityId = weeks.week;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
