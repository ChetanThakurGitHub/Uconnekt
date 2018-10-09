package com.uconnekt.ui.employer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uconnekt.BuildConfig;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.login_ragistartion.FirebaseLogin;
import com.uconnekt.cropper.CropImage;
import com.uconnekt.cropper.CropImageView;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.helper.GioAddressTask;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.Address;
import com.uconnekt.model.JobTitle;
import com.uconnekt.model.UserInfo;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.sp.OnSpinerItemClick;
import com.uconnekt.sp.SpinnerDialog;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.AppHelper;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.volleymultipart.VolleyMultipartRequest;
import com.uconnekt.volleymultipart.VolleySingleton;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<JobTitle> arrayList,specialityArrayList;
    private TextView tv_for_address,tv_for_businessName,tv_for_fullName,tv_for_txt,
            tv_for_logo,tv_for_jobTitle,tv_for_aofs,tv_for_txtCount,et_for_description;
    private Double latitude, longitude;
    private BottomSheetDialog dialog;
    private ImageView iv_for_profile;
    private Bitmap profileImageBitmap,profileImageBitmap1;
    private CusDialogProg cusDialogProg;
    private RelativeLayout mainlayout;
    private String jobTitleId,specialtyID,city = "",state = "",country = "";
    private EditText et_for_bio;
    private Uri imageUri;
    private RelativeLayout layout_for_address;
    private EditText et_for_businessName,et_for_fullname,et_for_email,et_for_contact;
    private int image = -1;

    private SpinnerDialog spinnerDialog,spinnerDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();

        String image = Uconnekt.session.getUserInfo().profileImage;
        if (image != null && !image.equals("")) Picasso.with(this).load(image).into(iv_for_profile);

        tv_for_fullName.setText(Uconnekt.session.getUserInfo().fullName);
        tv_for_businessName.setText(Uconnekt.session.getUserInfo().businessName);
        et_for_businessName.setText(Uconnekt.session.getUserInfo().businessName);
        et_for_fullname.setText(Uconnekt.session.getUserInfo().fullName);
        et_for_email.setText(Uconnekt.session.getUserInfo().email);

        arrayList = new ArrayList<>();
        specialityArrayList = new ArrayList<>();
        cusDialogProg = new CusDialogProg(this);

        getlist();


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

        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 500 - s.length();
                tv_for_txtCount.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et_for_description.addTextChangedListener(textWatcher1);

        iv_for_profile.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void initView(){
        tv_for_jobTitle = findViewById(R.id.tv_for_jobTitle);
        tv_for_aofs = findViewById(R.id.tv_for_aofs);
        tv_for_logo = findViewById(R.id.tv_for_logo);
        tv_for_address = findViewById(R.id.tv_for_address);
        iv_for_profile = findViewById(R.id.iv_for_profile);
        tv_for_fullName = findViewById(R.id.tv_for_fullName);
        mainlayout = findViewById(R.id.mainlayout);
        tv_for_txt = findViewById(R.id.tv_for_txt);
        tv_for_txtCount = findViewById(R.id.tv_for_txtCount);
        et_for_description = findViewById(R.id.et_for_description);
        et_for_bio = findViewById(R.id.et_for_bio);
        tv_for_businessName = findViewById(R.id.tv_for_businessName);
        layout_for_address = findViewById(R.id.layout_for_address);
        et_for_businessName = findViewById(R.id.et_for_businessName);
        et_for_fullname = findViewById(R.id.et_for_fullname);
        et_for_email = findViewById(R.id.et_for_email);
        et_for_contact = findViewById(R.id.et_for_contact);

        findViewById(R.id.layout_for_address).setOnClickListener(this);
        findViewById(R.id.layout_for_addLogo).setOnClickListener(this);
        findViewById(R.id.layout_for_jobTittle).setOnClickListener(this);
        findViewById(R.id.btn_for_next).setOnClickListener(this);
        findViewById(R.id.layout_for_aofs).setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.edit_profile);

        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);

        EditText et_for_bio = new EditText(this);
        et_for_bio.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        EditText et_for_description = new EditText(this);
        et_for_description.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        TextView tvBusDes = findViewById(R.id.tvBusDes);
        tvBusDes.setText("About "+Uconnekt.session.getUserInfo().businessName);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()){
            case R.id.layout_for_address:
                addressClick();
                layout_for_address.setEnabled(false);
                break;
            case R.id.layout_for_addLogo:
                image = 0;
                PermissionAll permissionAll = new PermissionAll();
                if (permissionAll.chackCameraPermission(EditProfileActivity.this))showBottomSheetDialog();
                break;
            case R.id.layout_for_camera:
                if (dialog!=null)dialog.dismiss();
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file= new File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg");
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        imageUri= FileProvider.getUriForFile(EditProfileActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider",file);
                    }else {
                        imageUri= Uri.fromFile(file);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent, Constant.CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_for_gallery:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.GALLERY);
                if (dialog!=null)dialog.dismiss();
                break;
            case R.id.btn_for_close:
                if (dialog!=null)dialog.dismiss();
                break;
            case R.id.btn_for_next:
                validation();
                break;
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.iv_for_profile:
                permissionAll = new PermissionAll();
                if (permissionAll.chackCameraPermission(EditProfileActivity.this))showBottomSheetDialog();
                image = 1;
                break;
            case R.id.layout_for_jobTittle:
                spinnerDialog.showSpinerDialog();
                break;
            case R.id.layout_for_aofs:
                spinnerDialog1.showSpinerDialog();
                break;
        }
    }

    private void validation(){
        String address = tv_for_address.getText().toString().trim();
        String businessName  = et_for_businessName.getText().toString().trim();
        String fullname  = et_for_fullname.getText().toString().trim();
        String phone  = et_for_contact.getText().toString().trim();
        if (businessName.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.business_v));
        }else if (businessName.length()<3){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.business_required));
        }else if (fullname.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.fullname_v));
        }else if (fullname.length()<3){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.fullname_required));
        }else if (phone.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.phone_v));
        } else if (phone.length() < 7 || phone.length() > 16){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.phone_required));
        }else if (jobTitleId.isEmpty()) {
            MyCustomMessage.getInstance(this).snackbar(mainlayout, getString(R.string.jobtitle_v));
        }else if (specialtyID.isEmpty()) {
            MyCustomMessage.getInstance(this).snackbar(mainlayout, getString(R.string.specialty_v));
        }else if (address.isEmpty()) {
            MyCustomMessage.getInstance(this).snackbar(mainlayout, getString(R.string.address_v));
        }else {
            if (!city.isEmpty()|!state.isEmpty()|!country.isEmpty())  doProfileUpdate(address,et_for_bio.getText().toString().trim(),businessName,fullname,phone);
            else MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.select_location_again));
        }
    }


    public void showBottomSheetDialog() {
        dialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        dialog.setContentView(view);
        dialog.findViewById(R.id.layout_for_camera).setOnClickListener(this);
        dialog.findViewById(R.id.layout_for_gallery).setOnClickListener(this);
        dialog.findViewById(R.id.btn_for_close).setOnClickListener(this);
        dialog.show();
    }

    private void addressClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(EditProfileActivity.this);
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
            hideKeyboard();
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(EditProfileActivity.this, data);

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

        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160, 160).setMaxCropResultSize(4000, 4000).setAspectRatio(400, 400).start(this);
            } else {
                MyCustomMessage.getInstance(this).customToast(getString(R.string.something_wrong));
            }}else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            try {
                if (result != null) {
                    if (image == 0){
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                        tv_for_logo.setText(R.string.logo_attached);
                    }
                    if (image == 1){
                        profileImageBitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                        iv_for_profile.setImageBitmap(profileImageBitmap1);}

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (requestCode == Constant.CAMERA && resultCode == RESULT_OK) {
                if (imageUri!=null){
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(this);
                }else{
                    MyCustomMessage.getInstance(this).customToast(getString(R.string.something_wrong));
                }
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result= CropImage.getActivityResult(data);
                try {
                    if (result != null) {
                        if (image == 0){
                            profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                            tv_for_logo.setText(R.string.logo_attached);}
                        if (image == 1){
                            profileImageBitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                            iv_for_profile.setImageBitmap(profileImageBitmap1);}

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    } // onActivityResult

    private void latlong(Double latitude, Double longitude) throws IOException {
        LatLng latLng = new LatLng(latitude,longitude);
        new GioAddressTask(this, latLng, new GioAddressTask.LocationListner() {
            @Override
            public void onSuccess(Address address) {
                city = ""; state = ""; country = "";
                city = address.getCity();
                city = city==null?"":city;
                state = address.getState();
                state = state==null?"":state;
                country = address.getCountry();
                country = country==null?"":country;
            }
        }).execute();
    } // latlog to address find

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.parmission));
                }
            }
            break;
        }
    }

    private void getlist(){
        new VolleyGetPost(this, AllAPIs.EMPLOYER_PROFILE,false,"list",true){

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("job_title");
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("jobTitleId");
                            jobTitles.jobTitleName = object.getString("jobTitleName");
                            arrayList.add(jobTitles);
                        }
                        spinnerDialog = new SpinnerDialog(EditProfileActivity.this, arrayList, getString(R.string.area_of_specialty));
                        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                jobTitleId = job.jobTitleId;
                                tv_for_jobTitle.setText(job.jobTitleName);
                            }
                        });

                        JSONArray speciality = result.getJSONArray("speciality_list");
                        for (int i = 0; i<speciality.length(); i++){
                            JobTitle speciality1 = new JobTitle();
                            JSONObject object = speciality.getJSONObject(i);

                            speciality1.jobTitleId = object.getString("specializationId");
                            speciality1.jobTitleName = object.getString("specializationName");
                            specialityArrayList.add(speciality1);
                        }
                        spinnerDialog1 = new SpinnerDialog(EditProfileActivity.this, specialityArrayList, getString(R.string.area_of_specialtys));
                        spinnerDialog1.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                specialtyID = job.jobTitleId;
                                tv_for_aofs.setText(job.jobTitleName);
                            }
                        });

                        showPrefilledData();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity( new Intent(EditProfileActivity.this, NetworkActivity.class));
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

    private void doProfileUpdate(final String address, final String bio, final String businessName, final String fullname, final String phone) {

        final String description = et_for_description.getText().toString().trim();

        if (isNetworkAvailable()) {
            cusDialogProg.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllAPIs.PROFILE_UPDATE, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            JSONObject array = jsonObject.getJSONObject("user_profile");
                            UserInfo userFullDetail = new Gson().fromJson(array.toString(), UserInfo.class);
                            userFullDetail.password = Uconnekt.session.getUserInfo().password;

                            if (userFullDetail.status.equals("1")) {
                                FirebaseLogin firebaseLogin = new FirebaseLogin();
                                firebaseLogin.firebaseLogin(userFullDetail,EditProfileActivity.this,false, cusDialogProg ,false,true, false);
                                Constant.NETWORK_CHECK = 1;
                            } else {
                                MyCustomMessage.getInstance(EditProfileActivity.this).snackbar(mainlayout, getString(R.string.inactive_user));
                            }
                        } else {
                            MyCustomMessage.getInstance(EditProfileActivity.this).snackbar(mainlayout, message);
                            cusDialogProg.dismiss();
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    MyCustomMessage.getInstance(EditProfileActivity.this).snackbar(mainlayout,networkResponse+"");
                    cusDialogProg.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("fullName", fullname);
                    params.put("businessName", businessName);
                    params.put("area_of_specialization", specialtyID);
                    params.put("address", address);
                    String  enCodedStatusCode = null,enCodedStatusCode1 = null;
                    try {
                        enCodedStatusCode = URLEncoder.encode(bio, "UTF-8");
                        enCodedStatusCode1 = URLEncoder.encode(description, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    params.put("bio", enCodedStatusCode==null?"":enCodedStatusCode);
                    params.put("description", enCodedStatusCode1==null?"":enCodedStatusCode1);
                    params.put("city", city==null?"":city);
                    params.put("state", state==null?"":state);
                    params.put("country", country==null?"":country);
                    params.put("latitude", latitude+"");
                    params.put("longitude", longitude+"");
                    params.put("job_title", jobTitleId);
                    params.put("phone", phone);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("authToken", Uconnekt.session.getUserInfo().authToken);
                    return headers;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if (profileImageBitmap != null) params.put("company_logo", new VolleyMultipartRequest.DataPart("logo.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));
                    if (profileImageBitmap1 != null) params.put("profileImage", new VolleyMultipartRequest.DataPart("profilePic.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap1), "image/jpeg"));
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(EditProfileActivity.this).addToRequestQueue(multipartRequest);
        } else {
            startActivity(new Intent(EditProfileActivity.this,NetworkActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getlist();
            Constant.NETWORK_CHECK = 0;
        }else  Constant.NETWORK_CHECK = 0;

    }

    private void showPrefilledData(){
        new VolleyGetPost(this, AllAPIs.SHOW_PREFILLED_DATA, false, "showPrefilledData", false) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONArray array = jsonObject.getJSONArray("business_profile");
                        JSONObject object = array.getJSONObject(0);
                        String address = object.getString("address");
                       // company_logo = object.getString("company_logo");
                        String bio = URLDecoder.decode(object.getString("bio"), "UTF-8");
                        String description = URLDecoder.decode(object.getString("description"), "UTF-8");
                        //String bio = object.getString("bio");
                        city = object.getString("city");
                        state = object.getString("state");
                        country = object.getString("country");
                        String specializationId = object.getString("specializationId");
                        String sdlf = object.getString("phone");
                        et_for_contact.setText(object.getString("phone"));
                        String jobTitle = object.getString("jobTitleId");
                        String lat = object.getString("latitude");
                        String lng = object.getString("longitude");

                        tv_for_logo.setText(R.string.logo_attached);

                        if (lat!= null && !lat.isEmpty() && !lat.equals("null")) latitude = Double.parseDouble(lat);
                        if (lng != null && !lng.isEmpty() && !lng.equals("null"))longitude = Double.parseDouble(lng);

                        if (!address.equals("null"))tv_for_address.setText(address);
                        if (!bio.equals("null"))et_for_bio.setText(bio);
                        if (!description.equals("null"))et_for_description.setText(description);

                        if (!jobTitle.isEmpty()) {
                            jobTitleId = jobTitle;
                            for (int i = 0; arrayList.size() > i; i++) {
                                if (arrayList.get(i).jobTitleId.equals(jobTitle)) {
                                    tv_for_jobTitle.setText(arrayList.get(i).jobTitleName);
                                    break;
                                }
                            }
                        }

                        if (!specializationId.isEmpty()) {
                            specialtyID = specializationId;
                            for (int i = 0; specialityArrayList.size() > i; i++) {
                                if (specialityArrayList.get(i).jobTitleId.equals(specializationId)) {
                                    tv_for_aofs.setText(specialityArrayList.get(i).jobTitleName);
                                    break;
                                }
                            }
                        }

                        dismissProg();

                    }else dismissProg();

                } catch (JSONException e) {
                    dismissProg();
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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

}
