package com.uconnekt.ui.authentication.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.login_ragistartion.FirebaseLogin;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.UserInfo;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.authentication.forgot_password.ForgotPasswordActivity;
import com.uconnekt.ui.authentication.registration.RegistrationActivity;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyMultipartRequest;
import com.uconnekt.volleymultipart.VolleySingleton;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener,LoginView {

    private EditText username;
    private EditText password;
    private LoginPresenter loginPrasenter;
    private Boolean isChecked = false;
    private ImageView iv_uncheck;
    private RelativeLayout mainlayout;
    private String userType = "";
    private CusDialogProg cusDialogProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        cusDialogProg = new CusDialogProg(this);
       if(getIntent().getExtras()!= null){
           userType = getIntent().getStringExtra("userType");
           loginPrasenter = new LoginPresenterImpl(this,new LoginIntractorImpl());

           UserInfo userInfo = Uconnekt.session.getRememberMeInfo();
           if (userInfo.userType.equals(userType)) {
               if (!userInfo.email.equals("") && userInfo.email != null) {
                   isChecked = true;
                   username.setText(userInfo.email);
                   password.setText(userInfo.password);
                   iv_uncheck.setImageResource(R.drawable.ic_checked);
               }
           }
       }

    }

    private void initView(){
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        mainlayout = findViewById(R.id.mainlayout);
        iv_uncheck = findViewById(R.id.iv_uncheck);
        findViewById(R.id.btn_for_login).setOnClickListener(this);
        findViewById(R.id.layout_for_signup).setOnClickListener(this);
        findViewById(R.id.tv_for_forgotPassword).setOnClickListener(this);
        findViewById(R.id.layout_for_remember).setOnClickListener(this);
        findViewById(R.id.iv_for_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()){
            case R.id.btn_for_login:
                loginPrasenter.validationCondition(username.getText().toString().trim(),password.getText().toString().trim());
                break;
            case R.id.layout_for_signup:
                navigateToRegistration();
                break;
            case R.id.tv_for_forgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.layout_for_remember:
                if (!isChecked){
                    iv_uncheck.setImageResource(R.drawable.ic_checked);
                    isChecked = true;
                }else {
                    iv_uncheck.setImageResource(R.drawable.ic_uncheck);
                    isChecked = false;
                }
                break;
            case R.id.iv_for_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.NETWORK_CHECK = 0;
    }

    @Override
    public void setEmailError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.email_v));
    }

    @Override
    public void setEmailVError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.valid_email));
    }

    @Override
    public void setPasswordError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.password_v));
    }

    @Override
    public void navigateToHome() {
        if (isNetworkAvailable()){
            doLogin(username.getText().toString(), password.getText().toString());
        }else{
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.check_net_connection));}
    }

    @Override
    public void navigateToRegistration() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        loginPrasenter.onDistroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void doLogin(final String email, final String password) {

        if (isNetworkAvailable()) {
            cusDialogProg.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllAPIs.LOGIN, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");

                            UserInfo userFullDetail = new Gson().fromJson(userDetail.toString(), UserInfo.class);
                            userFullDetail.password = password;
                            if (userFullDetail.status.equals("1")) {

                                FirebaseLogin firebaseLogin = new FirebaseLogin();
                                firebaseLogin.firebaseLogin(userFullDetail,LoginActivity.this,isChecked, cusDialogProg,true,false);

                            } else {
                                MyCustomMessage.getInstance(LoginActivity.this).snackbar(mainlayout, "Your account has been inactivated by admin, please contact to activate");
                            }

                        } else {
                            MyCustomMessage.getInstance(LoginActivity.this).snackbar(mainlayout, message);
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
                    MyCustomMessage.getInstance(LoginActivity.this).snackbar(mainlayout,networkResponse+"");
                    cusDialogProg.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    if (FirebaseInstanceId.getInstance().getToken() != null) {
                        params.put("email", email);
                        params.put("password", password);
                        params.put("userType", userType);
                        params.put("deviceType", "0");
                        params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                    } else {
                        MyCustomMessage.getInstance(LoginActivity.this).snackbar(mainlayout, getString(R.string.wrong));
                    }
                    return params;
                }

            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(multipartRequest);
        } else {
            startActivity(new Intent(LoginActivity.this, NetworkActivity.class));
        }
    }

}
