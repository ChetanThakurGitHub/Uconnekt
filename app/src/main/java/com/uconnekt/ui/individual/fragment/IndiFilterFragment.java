package com.uconnekt.ui.individual.fragment;

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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.helper.GioAddressTask;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.JobTitle;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.sp.OnSpinerItemClick;
import com.uconnekt.sp.SpinnerDialog;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
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

public class IndiFilterFragment extends Fragment implements View.OnClickListener {

    private JobHomeActivity activity;
    private LinearLayout mainlayout;
    private TextView tv_for_address,tv_for_rating,tv_for_aofs,tv_for_company;
    private RelativeLayout layout_for_address;
    private ArrayList<JobTitle> arrayList = new ArrayList<>();
    private ArrayList<JobTitle> companyList = new ArrayList<>();
    private RatingBar ratingBar;
    private IndiSearchFragment searchFragment;
    private IndiMapFragment indiMapFragment;
    private String specialtyId = "",company = "",ratingNo = "",city = "",state = "",country = "";
    private Double latitude = 0.0,longitude = 0.0;
    private SpinnerDialog spinnerDialog,spinnerDialog2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_indi_filter, container, false);
        initView(view);

        getlist();
        layout_for_address.setOnClickListener(this);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv_for_rating.setText("("+rating+" Rating)");
                ratingBar.setRating(rating);
                ratingNo = rating+"";
            }
        });

        return view;
    }

    private void initView(View view) {
        mainlayout = view.findViewById(R.id.mainlayout);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        layout_for_address = view.findViewById(R.id.layout_for_address);
        tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        tv_for_company = view.findViewById(R.id.tv_for_company);
        ratingBar = view.findViewById(R.id.ratingBar);
        tv_for_rating = view.findViewById(R.id.tv_for_rating);
        mainlayout.setOnClickListener(this);
        view.findViewById(R.id.btn_for_search).setOnClickListener(this);
        view.findViewById(R.id.layout_for_aofs).setOnClickListener(this);
        view.findViewById(R.id.layout_for_company).setOnClickListener(this);
        activity.findViewById(R.id.iv_for_circular_arrow).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobHomeActivity) context;
    }

    private void getlist() {
        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list",true) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();
                        JSONObject result = jsonObject.getJSONObject("result");

                        JSONArray results = result.getJSONArray("opposite_speciality_list");
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("specializationId");
                            jobTitles.jobTitleName = object.getString("specializationName");
                            arrayList.add(jobTitles);
                        }
                        spinnerDialog = new SpinnerDialog(activity, arrayList, getString(R.string.area_of_specialty_s));
                        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                specialtyId = job.jobTitleId;
                                tv_for_aofs.setText(job.jobTitleName);
                                if (searchFragment != null) searchFragment.tv_for_speName.setText(job.jobTitleName);
                                if (indiMapFragment != null)indiMapFragment.tv_for_speName.setText(job.jobTitleName);
                            }
                        });

                        JSONArray companies = result.getJSONArray("company_list");
                        for (int i = 0; i < companies.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = companies.getJSONObject(i);
                            jobTitles.jobTitleName = object.getString("company_name");
                            companyList.add(jobTitles);
                        }
                        spinnerDialog2 = new SpinnerDialog(activity, companyList, getString(R.string.company));
                        spinnerDialog2.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                company = job.jobTitleName;
                                tv_for_company.setText(job.jobTitleName);
                            }
                        });
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

    private void addressClick() {
        try {
            layout_for_address.setEnabled(false);
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(activity);
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
            case R.id.layout_for_aofs:
                spinnerDialog.showSpinerDialog();
                break;
            case R.id.layout_for_company:
                spinnerDialog2.showSpinerDialog();
                break;
        }
    }

    private void refresh(){
        specialtyId = "";company = "";ratingNo = "";
       if (searchFragment != null)searchFragment.tv_for_speName.setText("");
        if (indiMapFragment != null) indiMapFragment.tv_for_speName.setText("");

        tv_for_aofs.setText("");
        tv_for_company.setText("");

        ratingBar.setRating(0);
        ratingNo = "";

        tv_for_address.setText("");
    }

    private  void btnClick(){
        String address = tv_for_address.getText().toString().trim();

        if (searchFragment != null)searchFragment.offset = 0;
        if (searchFragment != null)searchFragment.layout_for_list.setVisibility(View.GONE);
        if (searchFragment != null)searchFragment.searchLists.clear();
        if (searchFragment != null)searchFragment.mSwipeRefreshLayout.setRefreshing(true);
        if (searchFragment != null)searchFragment.getList(specialtyId,ratingNo,company,address,city,state,country);

       // indiMapFragment.offset = 0;
       // indiMapFragment.mSwipeRefreshLayout.setRefreshing(true);
        if (indiMapFragment != null)indiMapFragment.searchLists.clear();
        if (indiMapFragment != null)indiMapFragment.map.clear();
        if (indiMapFragment != null)indiMapFragment.mClusterManager.clearItems();
        if (indiMapFragment != null)indiMapFragment.getList(specialtyId,ratingNo,company,address,latitude,longitude,city,state,country);

        activity.onBackPressed();
    }

/*    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_for_specialty:
                JobTitle jobTitle = arrayList.get(position);
                specialtyId = jobTitle.jobTitleId;
                if (searchFragment != null) searchFragment.tv_for_speName.setText(jobTitle.jobTitleName);
                if (indiMapFragment != null)indiMapFragment.tv_for_speName.setText(jobTitle.jobTitleName);
                break;
            case R.id.sp_for_company:
                jobTitle = companyList.get(position);
                company = jobTitle.jobTitleName;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/

    public void setFragment(IndiSearchFragment searchFragment) {
        this.searchFragment=searchFragment;
    }

    public void setOtherFragment(IndiMapFragment indiMapFragment) {
        this.indiMapFragment=indiMapFragment;
    }
}
