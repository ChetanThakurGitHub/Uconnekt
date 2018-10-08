package com.uconnekt.ui.employer.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import io.apptik.widget.MultiSlider;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.uconnekt.util.Constant.RESULT_OK;

public class FilterFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private HomeActivity activity;
    private ArrayList<JobTitle> arrayList = new ArrayList<>();
    private ArrayList<JobTitle> strengthsList = new ArrayList<>();
    private ArrayList<JobTitle> valuesList = new ArrayList<>();
    private ArrayList<JobTitle> jobTitleList = new ArrayList<>();
    private ArrayList<Weeks> weekList = new ArrayList<>();
    private ArrayList<Weeks> empTypeList = new ArrayList<>();
    private Spinner sp_for_availability,sp_for_empType;
    private WeekSpAdapter weekSpAdapter,empTypeSpAdapter;
    private TextView tv_for_address,tv_for_jobTitle,tv_for_aofs,tv_for_value,tv_for_strength;
    private LinearLayout mainlayout;
    private RelativeLayout layout_for_address;
    private String specialtyId = "",strengthId = "",valueId = "",jobTitleId = "",availabilityId = "",employmentType = "",city = "",state = "" ,country;
    private SearchFragment searchFragment;
    private MapFragment mapFragment;
    private Double latitude = 0.0,longitude = 0.0;
    private SpinnerDialog spinnerDialog,spinnerDialog2,spinnerDialog3,spinnerDialog4;

    private MultiSlider sliderExperience,sliderSalary;
    private TextView tvMinExp,tvMaxExp,tvMinSalary,tvMaxSalary;
    private DecimalFormat formatter;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        initView(view);

        weekSpAdapter = new WeekSpAdapter(activity, weekList,R.layout.week_sp);
        empTypeSpAdapter = new WeekSpAdapter(activity, empTypeList,R.layout.custom_sp_speciality);
        sp_for_availability.setAdapter(weekSpAdapter);
        sp_for_empType.setAdapter(empTypeSpAdapter);
        formatter = new DecimalFormat("####,###");

        getlist();
        sp_for_availability.setOnItemSelectedListener(this);
        sp_for_empType.setOnItemSelectedListener(this);


        sliderExperience.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    int test = value==20?1:value==40?2:value==60?3:value==80?4:value==100?5:0;
                    tvMinExp.setText(String.valueOf(test));
                } else {
                    int test = value==20?1:value==40?2:value==60?3:value==80?4:value==100?5:0;
                    tvMaxExp.setText(String.valueOf(test));
                }
            }
        });

        sliderSalary.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    //int test = value==10?20000:value==20?40000:value==30?60000:value==40?80000:value==50?100000:value==60?120000:value==70?140000:value==80?160000:value==90?180000:value==100?200000:0;
                    tvMinSalary.setText("$"+String.valueOf(formatter.format(showsalary(value))));
                } else {
                    //int test = value==10?20000:value==20?40000:value==30?60000:value==40?80000:value==50?100000:value==60?120000:value==70?140000:value==80?160000:value==90?180000:value==100?200000:0;
                    tvMaxSalary.setText("$"+String.valueOf(formatter.format(showsalary(value))));
                }
            }
        });

        return view;
    }

    private int showsalary(int value){
        int test = 0;
        switch (value) {
            case 10:
                test = 20000;
                break;
            case 20:
                test = 40000;
                break;
            case 30:
                test = 60000;
                break;
            case 40:
                test = 80000;
                break;
            case 50:
                test = 100000;
                break;
            case 60:
                test = 120000;
                break;
            case 70:
                test = 140000;
                break;
            case 80:
                test = 160000;
                break;
            case 90:
                test = 180000;
                break;
            case 100:
                test = 200000;
                break;
        }
        return test;
    }

    public  void setFragment(SearchFragment searchFragment){
        this.searchFragment=searchFragment;
    }

    public  void setOtherFragment(MapFragment mapFragment){
        this.mapFragment=mapFragment;
    }

    @SuppressLint("SetTextI18n")
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
        sp_for_empType = view.findViewById(R.id.sp_for_empType);
        tv_for_jobTitle = view.findViewById(R.id.tv_for_jobTitle);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        mainlayout = view.findViewById(R.id.mainlayout);
        layout_for_address = view.findViewById(R.id.layout_for_address);
        layout_for_address.setOnClickListener(this);
        activity.findViewById(R.id.iv_for_circular_arrow).setOnClickListener(this);

        sliderExperience = view.findViewById(R.id.sliderExperience);
        sliderSalary = view.findViewById(R.id.sliderSalary);
        tvMinExp = view.findViewById(R.id.tvMinExp);
        tvMaxExp = view.findViewById(R.id.tvMaxExp);
        tvMinSalary = view.findViewById(R.id.tvMinSalary);
        tvMaxSalary = view.findViewById(R.id.tvMaxSalary);
        tvMinExp.setText("0");
        tvMaxExp.setText("5");
        tvMinSalary.setText("$0");
        tvMaxSalary.setText("$200,000");

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
        specialtyId = "";strengthId = "";valueId = "";jobTitleId = "";availabilityId = "";employmentType = "";

        if (searchFragment != null)searchFragment.tv_for_speName.setText("");
        if (mapFragment != null)mapFragment.tv_for_speName.setText("");

        sp_for_availability.setAdapter(weekSpAdapter);
        sp_for_empType.setAdapter(empTypeSpAdapter);
        tv_for_jobTitle.setText("");
        tv_for_aofs.setText("");
        tv_for_value.setText("");
        tv_for_strength.setText("");
        tvMinExp.setText("0");
        tvMaxExp.setText("5");
        tvMinSalary.setText("$0");
        tvMaxSalary.setText("$200,000");

        weekSpAdapter.notifyDataSetChanged();
        empTypeSpAdapter.notifyDataSetChanged();
        tv_for_address.setText("");

        sliderExperience.getThumb(0).setValue(0);
        sliderExperience.getThumb(1).setValue(100);
        sliderSalary.getThumb(0).setValue(0);
        sliderSalary.getThumb(1).setValue(100);
    }

    private void btnClick(){
        String address = tv_for_address.getText().toString().trim();
        String minExp = tvMinExp.getText().toString().trim();
        String maxExp = tvMaxExp.getText().toString().trim();
        String minSalary = tvMinSalary.getText().toString().trim();
        String maxSalary = tvMaxSalary.getText().toString().trim();
        if (searchFragment != null)searchFragment.layout_for_list.setVisibility(View.GONE);
        if (searchFragment != null)searchFragment.offset = 0;
        if (searchFragment != null)searchFragment.searchLists.clear();
        if (searchFragment != null)searchFragment.mSwipeRefreshLayout.setRefreshing(true);
        if (searchFragment != null)searchFragment.getList(specialtyId,jobTitleId,availabilityId,address,strengthId,valueId,city,state,country,employmentType,minExp,maxExp,minSalary,maxSalary);

        if (mapFragment != null)mapFragment.layout_for_list.setVisibility(View.GONE);
        if (mapFragment != null)mapFragment.searchLists.clear();
        if (mapFragment != null)mapFragment.map.clear();
        if (mapFragment != null)mapFragment.mClusterManager.clearItems();
        if (mapFragment != null)mapFragment.getList(specialtyId,jobTitleId,availabilityId,address,strengthId,valueId,latitude,longitude,city,state,country,true,employmentType,minExp,maxExp,minSalary,maxSalary);

        activity.onBackPressed();
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
                            String total = object.getString("total_registered");
                            jobTitles.jobTitleName = object.getString("jobTitleName")+" ("+total+")";
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

                        JSONObject emplyementType = result.getJSONObject("emplyement_type");

                        empTypeList.add(week0);
                        Weeks week14 = new Weeks();
                        week14.week = emplyementType.getString("full_time");
                        empTypeList.add(week14);
                        Weeks week15 = new Weeks();
                        week15.week = emplyementType.getString("part_time");
                        empTypeList.add(week15);
                        Weeks week16 = new Weeks();
                        week16.week = emplyementType.getString("casual");
                        empTypeList.add(week16);
                        Weeks week17 = new Weeks();
                        week17.week = emplyementType.getString("contract");
                        empTypeList.add(week17);
                        empTypeSpAdapter.notifyDataSetChanged();

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

            employmentType = searchFragment.employmentType;
            if (!employmentType.isEmpty()) {
                for (int i = 0; empTypeList.size() > i; i++) {
                    if (empTypeList.get(i).week.equals(employmentType)) {
                        sp_for_empType.setSelection(i);
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

            tvMinExp.setText("0");
            tvMaxExp.setText("5");
            tvMinSalary.setText("$0");
            tvMaxSalary.setText("$200,000");

            String minExp = searchFragment.minExperience.isEmpty()||searchFragment.minExperience.equals("0")?"0": searchFragment.minExperience;
            minExp = minExp.equals("1")?"20":minExp.equals("2")?"40":minExp.equals("3")?"60":minExp.equals("4")?"80":minExp.equals("5")?"100":"0";
            sliderExperience.getThumb(0).setValue(Integer.parseInt(minExp));
            String maxExp = searchFragment.maxExperience.isEmpty()||searchFragment.maxExperience.equals("5")?"100": searchFragment.maxExperience;
            maxExp = maxExp.equals("1")?"20":maxExp.equals("2")?"40":maxExp.equals("3")?"60":maxExp.equals("4")?"80":maxExp.equals("5")?"100":"100";
            sliderExperience.getThumb(1).setValue(Integer.parseInt(maxExp));
            String minSalary = searchFragment.minSalarys.isEmpty()?"0": searchFragment.minSalarys;
            // minSalary = minSalary.equals("$20,000")?"10":minSalary.equals("$40,000")?"20":minSalary.equals("$80,000")?"30":minSalary.equals("$100,000")?"40":minSalary.equals("$120,000")?"50":minSalary.equals("$140,000")?"60":minSalary.equals("$160,000")?"70":minSalary.equals("$180,000")?"80":minSalary.equals("$200,000")?"100":"0";
            sliderSalary.getThumb(0).setValue(Integer.parseInt(getsalary(minSalary)));
            String maxSalary = searchFragment.maxSalarys.isEmpty()?"100": searchFragment.maxSalarys;
            //maxSalary = maxSalary.equals("$20,000")?"10":maxSalary.equals("$40,000")?"20":maxSalary.equals("$80,000")?"30":maxSalary.equals("$100,000")?"40":maxSalary.equals("$120,000")?"50":maxSalary.equals("$140,000")?"60":maxSalary.equals("$160,000")?"70":maxSalary.equals("$180,000")?"80":maxSalary.equals("$200,000")?"100":"100";
            sliderSalary.getThumb(1).setValue(Integer.parseInt(maxSalary.equals("100")?"100":getsalary(maxSalary)));

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

            employmentType = mapFragment.employmentType;
            if (!employmentType.isEmpty()) {
                for (int i = 0; empTypeList.size() > i; i++) {
                    if (empTypeList.get(i).week.equals(employmentType)) {
                        sp_for_empType.setSelection(i);
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
            String minExp = mapFragment.minExperience.isEmpty()?"0": mapFragment.minExperience;
            minExp = minExp.equals("1")?"20":minExp.equals("2")?"40":minExp.equals("3")?"60":minExp.equals("4")?"80":minExp.equals("5")?"100":"0";
            sliderExperience.getThumb(0).setValue(Integer.parseInt(minExp));
            String maxExp = mapFragment.maxExperience.isEmpty()?"100": mapFragment.maxExperience;
            maxExp = maxExp.equals("1")?"20":maxExp.equals("2")?"40":maxExp.equals("3")?"60":maxExp.equals("4")?"80":maxExp.equals("5")?"100":"100";
            sliderExperience.getThumb(1).setValue(Integer.parseInt(maxExp));
            String minSalary = mapFragment.minSalarys.isEmpty()?"0": mapFragment.minSalarys;
           // minSalary = minSalary.equals("$20,000")?"10":minSalary.equals("$40,000")?"20":minSalary.equals("$80,000")?"30":minSalary.equals("$100,000")?"40":minSalary.equals("$120,000")?"50":minSalary.equals("$140,000")?"60":minSalary.equals("$160,000")?"70":minSalary.equals("$180,000")?"80":minSalary.equals("$200,000")?"100":"0";
            sliderSalary.getThumb(0).setValue(Integer.parseInt(getsalary(minSalary)));
            String maxSalary = mapFragment.maxSalarys.isEmpty()?"100": mapFragment.maxSalarys;
            //maxSalary = maxSalary.equals("$20,000")?"10":maxSalary.equals("$40,000")?"20":maxSalary.equals("$80,000")?"30":maxSalary.equals("$100,000")?"40":maxSalary.equals("$120,000")?"50":maxSalary.equals("$140,000")?"60":maxSalary.equals("$160,000")?"70":maxSalary.equals("$180,000")?"80":maxSalary.equals("$200,000")?"100":"100";
            sliderSalary.getThumb(1).setValue(Integer.parseInt(maxSalary.equals("100")?"100":getsalary(maxSalary)));
        }
    }


    private String getsalary(String value){
        String test = "0";
        switch (value) {
            case "$20,000":
                test = "10";
                break;
            case "$40,000":
                test = "20";
                break;
            case "$60,000":
                test = "30";
                break;
            case "$80,000":
                test = "40";
                break;
            case "$100,000":
                test = "50";
                break;
            case "$120,000":
                test = "60";
                break;
            case "$140,000":
                test = "70";
                break;
            case "$160,000":
                test = "80";
                break;
            case "$180,000":
                test = "90";
                break;
            case "$200,000":
                test = "100";
                break;
        }
        return test;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_for_availability:
                Weeks weeks = weekList.get(position);
                availabilityId = weeks.week;
                break;
            case R.id.sp_for_empType:
                Weeks empType = empTypeList.get(position);
                employmentType = empType.week;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
