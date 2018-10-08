package com.uconnekt.ui.authentication.email_authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.UserInfo;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.authentication.login.LoginActivity;
import com.uconnekt.ui.authentication.user_selection.UserSelectionActivity;
import com.uconnekt.ui.employer.employer_profile.EmpProfileActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class EmailVerificationActivity extends AppCompatActivity implements View.OnClickListener {

    private String token = "" ,email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        initView();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            token = extras.getString("token");
            email = extras.getString("email");
        }

    }

    private void initView() {
        findViewById(R.id.layout_for_login).setOnClickListener(this);
        findViewById(R.id.iv_for_back).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyAPI();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, UserSelectionActivity.class));
        finish();
    }

    private void verifyAPI(){
        new VolleyGetPost(this, AllAPIs.VERIFIED_EMAIL, true, "VERIFIED_EMAIL", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){
                        UserInfo userInfo = Uconnekt.session.getUserInfo();
                        userInfo.isVerified = "1";
                        Uconnekt.session.createSession(userInfo);
                        intentActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {

            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("token",token);
                params.put("email",email);
                params.put("browser","1");
                params.put("id",Uconnekt.session.getUserInfo().userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                return params;
            }
        }.executeVolley();
    }

    private void resendEmail(){
        new VolleyGetPost(this, AllAPIs.RESEND, false, "RESEND", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");
                    if (status.equals("success")){
                        Toast.makeText(EmailVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(EmailVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {

            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken",Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void intentActivity(){
        if (Uconnekt.session.getUserInfo().userType.equals("business")) {
            if (Uconnekt.session.getUserInfo().isProfile.equals("1")) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, EmpProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            if (Uconnekt.session.getUserInfo().isProfile.equals("1")) {
                Intent intent = new Intent(this, JobHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, IndiEditProfileActivity.class);
                intent.putExtra("FROM", "First");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_back:
                onBackPressed();
                break;
            case R.id.layout_for_login:
                onBackPressed();
                break;
            case R.id.btnResend:
                resendEmail();
                break;
        }
    }
}
