package com.uconnekt.ui.employer.employer_profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.squareup.picasso.Picasso;
import com.uconnekt.BuildConfig;
import com.uconnekt.R;
import com.uconnekt.adapter.CustomSpAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.cropper.CropImage;
import com.uconnekt.cropper.CropImageView;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.helper.GioAddressTask;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.JobTitle;
import com.uconnekt.model.UserInfo;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class EmpProfileActivity extends BaseActivity implements View.OnClickListener,EmpProfileView, AdapterView.OnItemSelectedListener {

    private Spinner sp_for_jobTitle,sp_for_specialty;
    private CustomSpAdapter customSpAdapter,customSpAdapterSpecialty;
    private ArrayList<JobTitle> arrayList,specialityArrayList;
    private TextView tv_for_address,tv_for_businessName,tv_for_fullName,tv_for_txt,tv_for_logo;
   // private FusedLocationProviderClient mFusedLocationClient;
    public TextView tvTags;
    private Double latitude, longitude;
   // private PermissionAll permissionAll;
    private BottomSheetDialog dialog;
    private ImageView iv_for_profile;
    private Bitmap profileImageBitmap;
    private CusDialogProg cusDialogProg;
    private RelativeLayout mainlayout;
    private String jobTitleId,specialtyID,city = "";
    public String area_of_specialization = "";
    private EditText et_for_bio;
    private Boolean doubleBackToExitPressedOnce = false;
    private EmpProfilePresenter empProfilePresenter;
    private Uri imageUri;
    private RelativeLayout layout_for_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_profile);
        initView();

        empProfilePresenter = new EmpProfilePresenterImpl(this, new EmpProfileIntractorImpl());

        String image = Uconnekt.session.getUserInfo().profileImage;
        if (image != null && !image.equals("")) Picasso.with(this).load(image).into(iv_for_profile);

        tv_for_fullName.setText(Uconnekt.session.getUserInfo().fullName);
        tv_for_businessName.setText(Uconnekt.session.getUserInfo().businessName);

      //  mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      //  permissionAll = new PermissionAll();
       // location();

        arrayList = new ArrayList<>();
        customSpAdapter = new CustomSpAdapter(this, arrayList,R.layout.custom_sp);
        sp_for_jobTitle.setAdapter(customSpAdapter);

        specialityArrayList = new ArrayList<>();
        customSpAdapterSpecialty = new CustomSpAdapter(this, specialityArrayList,R.layout.custom_sp2);
        sp_for_specialty.setAdapter(customSpAdapterSpecialty);


        cusDialogProg = new CusDialogProg(this);

        getlist();

        sp_for_jobTitle.setOnItemSelectedListener(this);
        sp_for_specialty.setOnItemSelectedListener(this);

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

    }

    /*private void location() {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionAll.checkLocationPermission(this);
            }else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    latitude = Double.valueOf(String.valueOf(location.getLatitude()));
                                    longitude = Double.valueOf(String.valueOf(location.getLongitude()));

                                    try {
                                        latlong(latitude, longitude);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            }

    }*/

    private void initView(){
        sp_for_jobTitle = findViewById(R.id.sp_for_jobTitle);
        tv_for_logo = findViewById(R.id.tv_for_logo);
        sp_for_specialty = findViewById(R.id.sp_for_specialty);
        tv_for_address = findViewById(R.id.tv_for_address);
        iv_for_profile = findViewById(R.id.iv_for_profile);
        tv_for_fullName = findViewById(R.id.tv_for_fullName);
        mainlayout = findViewById(R.id.mainlayout);
        tv_for_txt = findViewById(R.id.tv_for_txt);
        et_for_bio = findViewById(R.id.et_for_bio);
        tv_for_businessName = findViewById(R.id.tv_for_businessName);
        layout_for_address = findViewById(R.id.layout_for_address);

        findViewById(R.id.layout_for_address).setOnClickListener(this);
        findViewById(R.id.layout_for_addLogo).setOnClickListener(this);
        findViewById(R.id.btn_for_next).setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.profile);
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
                    PermissionAll permissionAll = new PermissionAll();
                    if (permissionAll.chackCameraPermission(EmpProfileActivity.this))showBottomSheetDialog();
                    break;
                case R.id.layout_for_camera:
                    if (dialog!=null)dialog.dismiss();
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file= new File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg");

                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                            imageUri= FileProvider.getUriForFile(EmpProfileActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider",file);
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
                    String address = tv_for_address.getText().toString().trim();
                    empProfilePresenter.validationCondition(jobTitleId,specialtyID,address,profileImageBitmap);
                    break;
            }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_for_jobTitle:
                JobTitle jobTitle = arrayList.get(position);
                jobTitleId  = jobTitle.jobTitleId;
                break;
            case R.id.sp_for_specialty:
                JobTitle jobTitle1 = specialityArrayList.get(position);
                specialtyID  = jobTitle1.jobTitleId;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showBottomSheetDialog() {
        dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        dialog.setContentView(view);
        dialog.findViewById(R.id.layout_for_camera).setOnClickListener(this);
        dialog.findViewById(R.id.layout_for_gallery).setOnClickListener(this);
        dialog.findViewById(R.id.btn_for_close).setOnClickListener(this);
        dialog.show();
    }

   /* private void spaiceltyClick() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_spaicelty);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RecyclerView recycler_view = dialog.findViewById(R.id.recycler_view);
        Button btn_for_save = dialog.findViewById(R.id.btn_for_save);
        ImageView iv_for_crossDailog = dialog.findViewById(R.id.iv_for_crossDailog);

        iv_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_for_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        selectionAdapter = new MultipalSelectionAdapter(specialityArrayList,this);
        recycler_view.setAdapter(selectionAdapter);

        dialog.show();
    }
*/


    private void addressClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(EmpProfileActivity.this);
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
                Place place = PlaceAutocomplete.getPlace(EmpProfileActivity.this, data);

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
                    profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    tv_for_logo.setText(R.string.logo_attached);
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
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                        tv_for_logo.setText(R.string.logo_attached);
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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //location();
                    }
                } else {
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
                        JobTitle jobTitle = new JobTitle();
                        jobTitle.jobTitleId = "";
                        jobTitle.jobTitleName = "";
                        arrayList.add(jobTitle);
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("jobTitleId");
                            jobTitles.jobTitleName = object.getString("jobTitleName");
                            arrayList.add(jobTitles);
                        }
                        customSpAdapter.notifyDataSetChanged();

                        JSONArray speciality = result.getJSONArray("speciality_list");
                        JobTitle jobTitle1 = new JobTitle();
                        jobTitle1.jobTitleId = "";
                        jobTitle1.jobTitleName = "";
                        specialityArrayList.add(jobTitle1);

                        for (int i = 0; i<speciality.length(); i++){
                            JobTitle speciality1 = new JobTitle();
                            JSONObject object = speciality.getJSONObject(i);

                            speciality1.jobTitleId = object.getString("specializationId");
                            speciality1.jobTitleName = object.getString("specializationName");
                            specialityArrayList.add(speciality1);
                        }
                        customSpAdapterSpecialty.notifyDataSetChanged();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity( new Intent(EmpProfileActivity.this, NetworkActivity.class));
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

    private void doProfileUpdate(final String address, final String bio) {

        if (isNetworkAvailable()) {

            cusDialogProg.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllAPIs.BUSINESS_PROFILE, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        cusDialogProg.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {

                            UserInfo userInfo = Uconnekt.session.getUserInfo();
                            userInfo.isProfile = "1";
                            Uconnekt.session.createSession(userInfo);

                            startActivity(new Intent(EmpProfileActivity.this,HomeActivity.class));
                            finish();

                        } else {
                            MyCustomMessage.getInstance(EmpProfileActivity.this).snackbar(mainlayout,message);
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
                    MyCustomMessage.getInstance(EmpProfileActivity.this).snackbar(mainlayout,networkResponse+"");
                    cusDialogProg.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                        params.put("area_of_specialization", specialtyID);
                        params.put("address", address);
                        params.put("bio", bio);
                        params.put("city", city);
                        params.put("latitude", latitude+"");
                        params.put("longitude", longitude+"");
                        params.put("job_title", jobTitleId);

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
                    if (profileImageBitmap != null) {
                        params.put("company_logo", new VolleyMultipartRequest.DataPart("profilePic.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));
                    }
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(EmpProfileActivity.this).addToRequestQueue(multipartRequest);
        } else {
            startActivity(new Intent(EmpProfileActivity.this,NetworkActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            MyCustomMessage.getInstance(this).snackbar(mainlayout, getResources().getString(R.string.for_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constant.BackPressed_Exit);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getlist();
            Constant.NETWORK_CHECK = 0;
        }else  Constant.NETWORK_CHECK = 0;

    }

    @Override
    public void setJobTitleError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.jobtitle_v));
    }

    @Override
    public void setSpecialtyError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.specialty_v));
    }

    @Override
    public void setAddressError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.address_v));
    }

    @Override
    public void setCompanyLogoError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.logo_v));
    }

    @Override
    public void navigateToHome() {
        String bio = et_for_bio.getText().toString().trim();
        String address = tv_for_address.getText().toString().trim();
        doProfileUpdate(address,bio);
    }

}
