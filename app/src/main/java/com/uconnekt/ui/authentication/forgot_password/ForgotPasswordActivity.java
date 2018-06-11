
package com.uconnekt.ui.authentication.forgot_password;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.uconnekt.R;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.authentication.user_selection.UserSelectionActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener,ForgotPasswordView{

    private EditText tv_for_email;
    private RelativeLayout mainlayout;
    private ForgotPasswordPresenter forgotPasswordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
        forgotPasswordPresenter = new ForgotPasswordPresenterImpl(this,new ForgotPasswordIntractorImpl());
    }

    private void initView(){
        tv_for_email = findViewById(R.id.tv_for_email);
        mainlayout = findViewById(R.id.mainlayout);
        findViewById(R.id.btn_for_send).setOnClickListener(this);
        findViewById(R.id.iv_for_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()){
            case R.id.btn_for_send:
                forgotPasswordPresenter.validationCondition(tv_for_email.getText().toString().trim());
                break;
            case R.id.iv_for_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void setEmailError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.emailf_v));
    }

    @Override
    public void setEmailValidationError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.valid_email));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void callAPI() {
        forgotPasswordAPI(tv_for_email.getText().toString().trim());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.NETWORK_CHECK = 0;
    }

    private void forgotPasswordAPI(final String email){
        new VolleyGetPost(this, AllAPIs.FORGOT_PASS, true, "forgotPassword",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equalsIgnoreCase("success")){
                        startActivity(new Intent(ForgotPasswordActivity.this, UserSelectionActivity.class));
                        MyCustomMessage.getInstance(ForgotPasswordActivity.this).showToast(message,0);
                    }else {
                        MyCustomMessage.getInstance(ForgotPasswordActivity.this).snackbar(mainlayout,message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(ForgotPasswordActivity.this, NetworkActivity.class));
            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("email",email);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> header) {
                return header;
            }
        }.executeVolley();
    }
}
