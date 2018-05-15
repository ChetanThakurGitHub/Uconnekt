package com.uconnekt.ui.authontication.registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.uconnekt.R;
import com.uconnekt.chat.login_ragistartion.FirebaseLogin;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.UserInfo;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.AppHelper;
import com.uconnekt.volleymultipart.VolleyMultipartRequest;
import com.uconnekt.volleymultipart.VolleySingleton;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_CAMERA;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener,RegistrationView{

    private EditText et_for_fullname, et_for_email, et_for_password, et_for_businessName;
    private ImageView iv_profile_image;
    private Bitmap profileImageBitmap;
    private RelativeLayout mainlayout;
    private BottomSheetDialog dialog;
    private RegistrationPresenter registrationPresenter;
    private String userType = "";
    private CusDialogProg cusDialogProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();
        userType = getIntent().getStringExtra("userType");
        registrationPresenter= new RegistrationPresenterImpl(this,new RegistrationIntractorImpl());

        cusDialogProg = new CusDialogProg(this);

        if (userType.equals("individual")){
            findViewById(R.id.layout_for_business).setVisibility(View.GONE);
            findViewById(R.id.view_for_bLine).setVisibility(View.GONE);
        }
    }

    private void initView() {
        findViewById(R.id.layout_for_signup).setOnClickListener(this);
        et_for_fullname = findViewById(R.id.et_for_fullname);
        et_for_email = findViewById(R.id.et_for_email);
        et_for_password = findViewById(R.id.et_for_password);
        et_for_businessName = findViewById(R.id.et_for_businessName);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        mainlayout = findViewById(R.id.mainlayout);
        findViewById(R.id.btn_for_signup).setOnClickListener(this);
        findViewById(R.id.layout_for_userImg).setOnClickListener(this);
        findViewById(R.id.iv_for_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {return;}
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId()) {
            case R.id.layout_for_signup:
                onBackPressed();
                break;
            case R.id.btn_for_signup:
                if (!userType.equalsIgnoreCase("individual")){
                registrationPresenter.validationCondition(et_for_businessName.getText().toString().trim(),et_for_fullname.getText().toString().trim(),
                        et_for_email.getText().toString().trim(),et_for_password.getText().toString().trim());
                }else {
                    registrationPresenter.validationCondition("hii",et_for_fullname.getText().toString().trim(),
                            et_for_email.getText().toString().trim(),et_for_password.getText().toString().trim());
                }
                break;
            case R.id.layout_for_userImg:
                PermissionAll permissionAll = new PermissionAll();
                if (permissionAll.chackCameraPermission(RegistrationActivity.this))showBottomSheetDialog();
                break;
            case R.id.iv_for_back:
                onBackPressed();
                break;
            case R.id.layout_for_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Constant.CAMERA);
                if (dialog!=null)dialog.dismiss();
                break;
            case R.id.layout_for_gallery:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.GALLERY);
                if (dialog!=null)dialog.dismiss();
                break;
            case R.id.btn_for_close:
                if (dialog!=null)dialog.dismiss();
                break;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            try {
                profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (profileImageBitmap != null) {
                    iv_profile_image.setPadding(0, 0, 0, 0);
                    iv_profile_image.setImageBitmap(profileImageBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (requestCode == Constant.CAMERA && resultCode == RESULT_OK) {
                profileImageBitmap = (Bitmap) data.getExtras().get("data");
                if (profileImageBitmap != null) {
                    iv_profile_image.setPadding(0, 0, 0, 0);
                    iv_profile_image.setImageBitmap(profileImageBitmap);
                }
            }
        }
    }

    @Override
    public void setBusinessNameError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.business_v));
    }

    @Override
    public void setBusinessNameRequired() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.business_required));
    }

    @Override
    public void setFullNameError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.fullname_v));
    }

    @Override
    public void setFullNameRequiredError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.fullname_required));
    }

    @Override
    public void setEmailError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.email_v));
    }

    @Override
    public void setEmailErrorValidation() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.valid_email));
    }

    @Override
    public void setPasswordError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.password_v));
    }

    @Override
    public void setPasswordRequiredError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.password_required));
    }

    @Override
    protected void onDestroy() {
        registrationPresenter.onDistroy();
        super.onDestroy();
    }

    @Override
    public void setNevigetToHome() {
        if (userType.equals("individual")){
            doRegistration("hii",et_for_fullname.getText().toString().trim(),
                    et_for_email.getText().toString().trim(),et_for_password.getText().toString().trim());
        }else {
        doRegistration(et_for_businessName.getText().toString().trim(),et_for_fullname.getText().toString().trim(),
                et_for_email.getText().toString().trim(),et_for_password.getText().toString().trim());}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CAMERA: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        showBottomSheetDialog();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Deny Location Permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.NETWORK_CHECK = 0;
    }

    private void doRegistration(final String bName, final String fullName, final String email, final String password) {

        if (isNetworkAvailable()) {

            cusDialogProg.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllAPIs.REGISTRATION, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {

                            String userDetail = jsonObject.getString("userDetail");

                            UserInfo userFullDetail = new Gson().fromJson(userDetail, UserInfo.class);
                            userFullDetail.password = password;

                            FirebaseLogin firebaseLogin = new FirebaseLogin();
                            firebaseLogin.firebaseLogin(userFullDetail,RegistrationActivity.this,false,cusDialogProg);

                        } else {
                            MyCustomMessage.getInstance(RegistrationActivity.this).snackbar(mainlayout,message);
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
                    MyCustomMessage.getInstance(RegistrationActivity.this).snackbar(mainlayout,networkResponse+"");
                    cusDialogProg.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    if (FirebaseInstanceId.getInstance().getToken() != null) {
                        params.put("fullName", fullName);
                        params.put("userType", userType);
                        params.put("password", password);
                        params.put("email", email);
                        if (userType.equals("individual")){
                            params.put("businessName", "");
                        }else {
                            params.put("businessName", bName);
                        }
                        params.put("deviceType", "0");
                        params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                    }else {
                        MyCustomMessage.getInstance(RegistrationActivity.this).snackbar(mainlayout, getString(R.string.wrong));
                    }
                    if (profileImageBitmap == null) {
                        params.put("profileImage", "");
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if (profileImageBitmap != null) {
                        params.put("profileImage", new VolleyMultipartRequest.DataPart("profilePic.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));
                    }
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(RegistrationActivity.this).addToRequestQueue(multipartRequest);
        } else {
            startActivity(new Intent(RegistrationActivity.this, NetworkActivity.class));
        }
    }

}
