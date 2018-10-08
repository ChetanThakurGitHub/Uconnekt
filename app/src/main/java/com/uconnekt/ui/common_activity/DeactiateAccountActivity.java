package com.uconnekt.ui.common_activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DeactiateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactiate_account);

        initView();
    }

    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(R.string.deactivate_account);
        findViewById(R.id.btnDeactive).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.btnDeactive:
                deactivateAccountDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void deactivateAccountDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        TextView tv_for_txt = dialog.findViewById(R.id.tv_for_txt);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.deactivate_account);
        tv_for_txt.setText("Are you sure you want to deactivate your account?");

        dialog.findViewById(R.id.btn_for_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_for_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivateAccount();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deactivateAccount(){
        new VolleyGetPost(this, AllAPIs.INACTIVE_USER, false, "INACTIVE_USER", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){
                        logout();
                        Uconnekt.session.logoutMyPre();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNetError() {}
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


    private void logout(){
        new VolleyGetPost(this, AllAPIs.LOGOUT, false, "Logout", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){
                        FirebaseDatabase.getInstance().getReference().child("users").child(Uconnekt.session.getUserInfo().userId).child("firebaseToken").setValue("");
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        assert notificationManager != null; notificationManager.cancelAll();
                        Uconnekt.session.logout(DeactiateAccountActivity.this);
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
}
