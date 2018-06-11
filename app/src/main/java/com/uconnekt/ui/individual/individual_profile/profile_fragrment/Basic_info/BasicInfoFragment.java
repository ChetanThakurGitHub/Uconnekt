package com.uconnekt.ui.individual.individual_profile.profile_fragrment.Basic_info;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.uconnekt.adapter.CustomSpAdapter;
import com.uconnekt.adapter.strengths.Strength1SpAdapter;
import com.uconnekt.adapter.strengths.Strength2SpAdapter;
import com.uconnekt.adapter.strengths.Strength3SpAdapter;
import com.uconnekt.adapter.values.Value1SpAdapter;
import com.uconnekt.adapter.values.Value2SpAdapter;
import com.uconnekt.adapter.values.Value3SpAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.helper.GioAddressTask;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.JobTitle;
import com.uconnekt.model.UserInfo;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.individual_profile.activity.JobProfileActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.uconnekt.util.Constant.RESULT_OK;

public class BasicInfoFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,BasicInfoView {

    private JobProfileActivity activity;
    private ArrayList<JobTitle> arrayList, strengthsList,strengthsList2,strengthsList3, valuesList, valuesList2, valuesList3;
    private CustomSpAdapter customSpAdapter;
    private Value1SpAdapter value1SpAdapter;
    private Value2SpAdapter value2SpAdapter;
    private Value3SpAdapter value3SpAdapter;
    private Strength1SpAdapter strength1SpAdapter;
    private Strength2SpAdapter strength2SpAdapter;
    private Strength3SpAdapter strength3SpAdapter;
    private Spinner sp_for_specialty, sp_for_value1, sp_for_value2, sp_for_value3,sp_for_strength1,sp_for_strength2,sp_for_strength3;
    private LinearLayout layout_for_values, mainlayout,layout_for_strengths;
    private Boolean opnClo = false,opnClo2 = false;
    private ImageView iv_for_up,iv_for_upDown;
    private EditText et_for_bio;
    private RelativeLayout layout_for_address;
    //private FusedLocationProviderClient mFusedLocationClient;
    public TextView tv_for_address;
    private TextView tv_for_txt;
    private PermissionAll permissionAll;
    private Double latitude;
    private Double longitude;
    public String specialtyId = "",value = "",strengthID ="";
    private String value1 = "", value2 = "", value3 = "",strength1 ="",strength2 = "",strength3 = "",city = "";
    public int spValue1 = -1,spValue2 = -1,spValue3 = -1,spStrength1 = -1,spStrength2 = -1,spStrength3 = -1;
    private TabLayout tablayout;
    public BasicInfoPresenter basicInfoPresenter;
    private CusDialogProg cusDialogProg;

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    public static BasicInfoFragment newInstance() {
        return new BasicInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);
        initView(view);

        basicInfoPresenter = new BasicInfoPresenterImpl(this,new BasicInfoIntractorImpl());

        permissionAll = new PermissionAll();
        if (permissionAll.checkLocationPermission(activity));//location();

        arrayList = new ArrayList<>();
        strengthsList = new ArrayList<>();
        strengthsList2 = new ArrayList<>();
        strengthsList3 = new ArrayList<>();
        valuesList = new ArrayList<>();
        valuesList2 = new ArrayList<>();
        valuesList3 = new ArrayList<>();
        customSpAdapter = new CustomSpAdapter(activity, arrayList,R.layout.custom_sp2);
        strength1SpAdapter = new Strength1SpAdapter(activity, strengthsList,this);
        strength2SpAdapter = new Strength2SpAdapter(activity, strengthsList2,this);
        strength3SpAdapter = new Strength3SpAdapter(activity, strengthsList3,this);
        value1SpAdapter = new Value1SpAdapter(activity, valuesList,this);
        value2SpAdapter = new Value2SpAdapter(activity, valuesList2,this);
        value3SpAdapter = new Value3SpAdapter(activity, valuesList3,this);
        sp_for_specialty.setAdapter(customSpAdapter);
        sp_for_strength1.setAdapter(strength1SpAdapter);
        sp_for_strength2.setAdapter(strength2SpAdapter);
        sp_for_strength3.setAdapter(strength3SpAdapter);
        sp_for_value1.setAdapter(value1SpAdapter);
        sp_for_value2.setAdapter(value2SpAdapter);
        sp_for_value3.setAdapter(value3SpAdapter);

        getlist();

        sp_for_specialty.setOnItemSelectedListener(this);
        sp_for_strength1.setOnItemSelectedListener(this);
        sp_for_strength2.setOnItemSelectedListener(this);
        sp_for_strength3.setOnItemSelectedListener(this);
        sp_for_value1.setOnItemSelectedListener(this);
        sp_for_value2.setOnItemSelectedListener(this);
        sp_for_value3.setOnItemSelectedListener(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 500 - s.length();
                tv_for_txt.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et_for_bio.addTextChangedListener(textWatcher);

        return view;
    }

    public void spinnerHide(){
        layout_for_values.setVisibility(View.GONE);
        layout_for_strengths.setVisibility(View.GONE);
        iv_for_upDown.setImageResource(R.drawable.ic_down_arrow);
        iv_for_up.setImageResource(R.drawable.ic_down_arrow);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobProfileActivity) context;
        activity.isBasicInfo=true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_for_specialty:
                spinnerHide();
                JobTitle jobTitle = arrayList.get(position);
                specialtyId = jobTitle.jobTitleId;
                break;
            case R.id.sp_for_strength1:
                JobTitle jobTitle1 = strengthsList.get(position);
                strength1 = jobTitle1.jobTitleId;
                spStrength1 = position;
                break;
            case R.id.sp_for_strength2:
                JobTitle jobTitle5 = strengthsList.get(position);
                strength2 = jobTitle5.jobTitleId;
                spStrength2 = position;
                break;
            case R.id.sp_for_strength3:
                JobTitle jobTitle6 = strengthsList.get(position);
                strength3 = jobTitle6.jobTitleId;
                spStrength3 = position;
                break;
            case R.id.sp_for_value1:
                JobTitle jobTitle2 = valuesList.get(position);
                value1 = jobTitle2.jobTitleId;
                spValue1=position;
                break;
            case R.id.sp_for_value2:
                JobTitle jobTitle3 = valuesList2.get(position);
                value2 = jobTitle3.jobTitleId;
                spValue2=position;
                break;
            case R.id.sp_for_value3:
                JobTitle jobTitle4 = valuesList3.get(position);
                value3 = jobTitle4.jobTitleId;
                spValue3=position;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initView(View view) {
        view.findViewById(R.id.mainlayout).setOnClickListener(this);
        sp_for_specialty = view.findViewById(R.id.sp_for_specialty);
        mainlayout = view.findViewById(R.id.mainlayout);
        iv_for_up = view.findViewById(R.id.iv_for_up);
        tv_for_txt = view.findViewById(R.id.tv_for_txt);
        iv_for_upDown = view.findViewById(R.id.iv_for_upDown);
        et_for_bio = view.findViewById(R.id.et_for_bio);
        sp_for_value1 = view.findViewById(R.id.sp_for_value1);
        sp_for_value2 = view.findViewById(R.id.sp_for_value2);
        sp_for_value3 = view.findViewById(R.id.sp_for_value3);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        sp_for_strength1 = view.findViewById(R.id.sp_for_strength1);
        sp_for_strength2 = view.findViewById(R.id.sp_for_strength2);
        sp_for_strength3 = view.findViewById(R.id.sp_for_strength3);
        layout_for_address = view.findViewById(R.id.layout_for_address);
        tablayout = activity.findViewById(R.id.tablayout);
        layout_for_address.setOnClickListener(this);
        layout_for_values = view.findViewById(R.id.layout_for_values);
        layout_for_strengths = view.findViewById(R.id.layout_for_strengths);
        view.findViewById(R.id.layout_for_value).setOnClickListener(this);
        view.findViewById(R.id.layout_for_strength).setOnClickListener(this);
        activity.findViewById(R.id.btn_for_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        reSetTab();
        switch (v.getId()) {
            case R.id.layout_for_value:
                iv_for_upDown.setImageResource(R.drawable.ic_down_arrow);
                layout_for_strengths.setVisibility(View.GONE);
                if (!opnClo) {
                    layout_for_values.setVisibility(View.VISIBLE);
                    iv_for_up.setImageResource(R.drawable.ic_up_arrow);
                    opnClo = true;
                } else {
                    opnClo = false;
                    layout_for_values.setVisibility(View.GONE);
                    iv_for_up.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_strength:
                iv_for_up.setImageResource(R.drawable.ic_down_arrow);
                layout_for_values.setVisibility(View.GONE);
                if (!opnClo2) {
                    layout_for_strengths.setVisibility(View.VISIBLE);
                    iv_for_upDown.setImageResource(R.drawable.ic_up_arrow);
                    opnClo2 = true;
                } else {
                    opnClo2 = false;
                    layout_for_strengths.setVisibility(View.GONE);
                    iv_for_upDown.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_address:
                spinnerHide();
                addressClick();
                layout_for_address.setEnabled(false);
                break;
            case R.id.btn_for_next:
                //activity.onTabSelected(tablayout.getTabAt(1));
                spinnerHide();
                checkSpinnCheck();
                activity.isBasicInfo=false;
                basicInfoPresenter.validationCondition(specialtyId,value,strengthID,tv_for_address.getText().toString().trim());
                break;
        }
    }

    public void checkSpinnCheck() {
        value = "";

        if (!value1.equals("")){
            value = value + "," + value1;
        }
        if (!value2.equals("")){
            value = value + "," + value2;
        }
        if (!value3.equals("")){
            value = value + ","+ value3;
        }
        if (value.startsWith(",")){
            value = value.substring(1, value.length());
        }
        if (!strength1.equals("")){
            strengthID = strengthID + "," + strength1;
        }
        if (!strength2.equals("")){
            strengthID = strengthID + "," + strength2;
        }
        if (!strength3.equals("")){
            strengthID = strengthID + ","+ strength3;
        }
        if (strengthID.startsWith(",")){
            strengthID = strengthID.substring(1, strengthID.length());
        }
    }

    private void getlist() {
        cusDialogProg = new CusDialogProg(activity);
        cusDialogProg.show();

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list",false) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();

                        TabLayout.Tab tab = tablayout.getTabAt(0);
                        if (tab != null) {tab.select();}

                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("speciality_list");
                        JobTitle jobTitle = new JobTitle();
                        jobTitle.jobTitleId = "";
                        jobTitle.jobTitleName = "";
                        arrayList.add(jobTitle);
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("specializationId");
                            jobTitles.jobTitleName = object.getString("specializationName");
                            arrayList.add(jobTitles);
                        }
                        customSpAdapter.notifyDataSetChanged();

                        JobTitle jobTitle1 = new JobTitle();
                        jobTitle1.jobTitleId = "";
                        jobTitle1.jobTitleName = "";
                        strengthsList.add(jobTitle1);
                        strengthsList2.add(jobTitle1);
                        strengthsList3.add(jobTitle1);

                        JSONArray strenght = result.getJSONArray("strenght_list");
                        for (int i = 0; i < strenght.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = strenght.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("strengthId");
                            jobTitles.jobTitleName = object.getString("strengthName");
                            strengthsList.add(jobTitles);
                            strengthsList2.add(jobTitles);
                            strengthsList3.add(jobTitles);
                        }
                        strength1SpAdapter.notifyDataSetChanged();
                        strength2SpAdapter.notifyDataSetChanged();
                        strength3SpAdapter.notifyDataSetChanged();

                        JobTitle jobTitle2 = new JobTitle();
                        jobTitle2.jobTitleId = "";
                        jobTitle2.jobTitleName = "";
                        valuesList.add(jobTitle2);
                        valuesList2.add(jobTitle2);
                        valuesList3.add(jobTitle2);

                        JSONArray values = result.getJSONArray("value_list");
                        for (int i = 0; i < values.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = values.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("valueId");
                            jobTitles.jobTitleName = object.getString("valueName");
                            valuesList.add(jobTitles);
                            valuesList2.add(jobTitles);
                            valuesList3.add(jobTitles);
                        }
                        value1SpAdapter.notifyDataSetChanged();
                        value2SpAdapter.notifyDataSetChanged();
                        value3SpAdapter.notifyDataSetChanged();


                        showPrefilledData();
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

    public void updateBasicInfoOnServer() {

        new VolleyGetPost(activity, AllAPIs.BASIC_INFO, true, "Basic Info",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        if (activity.isBasicInfo){
                            activity.isBasicInfo=false;
                            UserInfo userInfo = Uconnekt.session.getUserInfo();
                            userInfo.isProfile = "1";
                            Uconnekt.session.createSession(userInfo);
                        }  else if(tablayout.getSelectedTabPosition() < 2) {
                            UserInfo userInfo = Uconnekt.session.getUserInfo();
                            userInfo.isProfile = "1";
                            Uconnekt.session.createSession(userInfo);
                            activity.replaceFragment(activity.experienceFragment, false, R.id.framlayout);
                        }
                    } else {
                        MyCustomMessage.getInstance(activity).snackbar(mainlayout,message);
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
                params.put("area_of_specialization", specialtyId);
                params.put("address", tv_for_address.getText().toString().trim());
                params.put("bio", et_for_bio.getText().toString().trim());
                params.put("latitude", latitude + "");
                params.put("longitude", longitude + "");
                params.put("city",city);
                params.put("value", value);
                params.put("strength", strengthID);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void showPrefilledData(){
        new VolleyGetPost(activity, AllAPIs.SHOW_PREFILLED_DATA, false, "showPrefilledData", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    //cusDialogProg.dismiss();

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject object = jsonObject.getJSONObject("basic_info");
                        String address = object.getString("address");
                        String bio = object.getString("bio");
                        city = object.getString("city");
                        String specializationId = object.getString("specializationId");
                        String values = object.getString("value");
                        String strength = object.getString("strength");
                        String lat = object.getString("latitude");
                        String lng = object.getString("longitude");

                       if (lat!= null && !lat.isEmpty() && !lat.equals("null")) {
                           latitude = Double.parseDouble(lat);
                       }


                        if (lng != null && !lng.isEmpty() && !lng.equals("null"))longitude = Double.parseDouble(lng);

                        setdata(values,strength);

                        if (!address.equals("null"))tv_for_address.setText(address);
                        if (!bio.equals("null"))et_for_bio.setText(bio);

                        if (!specializationId.isEmpty()) {
                            specialtyId = specializationId;
                            for (int i = 0; arrayList.size() > i; i++) {
                                if (arrayList.get(i).jobTitleId.equals(specializationId)) {
                                    sp_for_specialty.setSelection(i);
                                    break;
                                }
                            }
                        }

                    }else dismissProg();

                } catch (JSONException e) {
                   dismissProg();
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
               dismissProg();
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

    private void dismissProg(){
        if (cusDialogProg!=null)cusDialogProg.dismiss();
    }

    private void setdata(String valu, String strength){
        String value11 = "",value22 = "",value33 = "";
        if (!valu.isEmpty()){
            if (valu.contains(",")){
                String[] values = valu.split(",");
                if (values.length == 3){
                    value33 = values[2];
                }
                value11 = values[0];
                value22 = values[1];
            }else {
                value11 = valu;
            }
        }

        if (!value11.isEmpty()){
            value1 = value11;
            value = value1;
            for (int i = 0; valuesList.size() > i; i++) {
                if (valuesList.get(i).jobTitleId.equals(value11)) {
                    sp_for_value1.setSelection(i);
                    break;
                }
            }
        }

        if (!value22.isEmpty()){
            value2 = value22;
            for (int i = 0; valuesList2.size() > i; i++) {
                if (valuesList2.get(i).jobTitleId.equals(value22)) {
                    sp_for_value2.setSelection(i);
                    break;
                }
            }
        }

        if (!value33.isEmpty()){
            value3 =value33;
            for (int i = 0; valuesList3.size() > i; i++) {
                if (valuesList3.get(i).jobTitleId.equals(value33)) {
                    sp_for_value3.setSelection(i);
                   break;
                }
            }
        }

        String strength11 = "",strength22 = "",strength33 = "";
        if (!strength.isEmpty()){
            if (strength.contains(",")){
                String[] strengths = strength.split(",");
                if (strengths.length == 3){
                    strength33 = strengths[2];
                    strength3 = strengths[2];
                }
                strength11 = strengths[0];
                strength1 = strengths[0];
                strength22 = strengths[1];
                strength3 = strengths[1];
            }else {
                strength11 = strength;
                strength1 = strength;
            }
        }

        if (!strength11.isEmpty()){
            strengthID = strength11;
            for (int i = 0; strengthsList.size() > i; i++) {
                if (strengthsList.get(i).jobTitleId.equals(strength11)) {
                    sp_for_strength1.setSelection(i);
                    break;
                }
            }
        }

        if (!strength22.isEmpty()){
            for (int i = 0; strengthsList2.size() > i; i++) {
                if (strengthsList2.get(i).jobTitleId.equals(strength22)) {
                    sp_for_strength2.setSelection(i);
                    break;
                }
            }
        }

        if (!strength33.isEmpty()){
            for (int i = 0; strengthsList3.size() > i; i++) {
                if (strengthsList3.get(i).jobTitleId.equals(strength33)) {
                    sp_for_strength3.setSelection(i);
                    break;
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProg();
            }
        },2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getlist();
        }
        Constant.NETWORK_CHECK = 0;
    }

    private void addressClick() {
        try {
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
            TabLayout.Tab tab = tablayout.getTabAt(0);
            activity.setTab(tab, true);
            if (tab != null) tab.select();

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
                city = (city == null)?(address.getState()== null)?address.getCountry():address.getState():address.getCountry();

            }
        }).execute();

    } // latlog to address find

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //location();
                    }
                } else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.parmission));
                }
            }
            break;
        }
    }
      /*location zoom end*/

    @Override
    public void setSpecialtyError() {
        MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.specialty_v));
    }

    @Override
    public void setValueError() {
        MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.value_v));
    }

    @Override
    public void setStrengthError() {
        MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.strength_v));
    }

    @Override
    public void setAddressError() {
        MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.address_v));
    }

    private void reSetTab(){
        TabLayout.Tab tab = tablayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public void navigateToExperience() {
        if (activity.isBasicInfo) {
            TabLayout.Tab tab = tablayout.getTabAt(2);
            activity.setTab(tab, true);
            if (tab != null) {
                tab.select();
            }
        }else{
            TabLayout.Tab tab = tablayout.getTabAt(1);
            activity.setTab(tab, true);
            if (tab != null) {
                tab.select();
            }
        }
        updateBasicInfoOnServer();
    }
}
