package com.uconnekt.ui.employer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class BasicInfoActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
        initView();
        String userId = "";
        Bundle extras = getIntent().getExtras();
        if(extras != null)userId = extras.getString("USERID");
        showPrefilledData(userId);
    }

    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.basic_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showPrefilledData(String userId){
        new VolleyGetPost(this, AllAPIs.INDI_PROFILE+userId, false, "getUserPersonalInfo", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject object = jsonObject.getJSONObject("basic_info");
                        String address = object.getString("address");
                        //String bio = object.getString("bio");
                        String bio = URLDecoder.decode(object.getString("bio"), "UTF-8");
                        String specializationName = object.getString("specializationName");
                        String valueName = object.getString("valueName");
                        String strengthName = object.getString("strengthName");

                        setData(address,bio,specializationName,valueName,strengthName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void setData(String address, String bio, String specializationName, String valueName, String strengthName) {
        TextView tv_for_aofs = findViewById(R.id.tv_for_aofs);
        TextView tv_for_value1 = findViewById(R.id.tv_for_value1);
        TextView tv_for_value2 = findViewById(R.id.tv_for_value2);
        TextView tv_for_value3 = findViewById(R.id.tv_for_value3);
        TextView tv_for_strength1 = findViewById(R.id.tv_for_strength1);
        TextView tv_for_strength2 = findViewById(R.id.tv_for_strength2);
        TextView tv_for_strength3 = findViewById(R.id.tv_for_strength3);
        TextView tv_for_address = findViewById(R.id.tv_for_address);
        TextView tv_for_bio = findViewById(R.id.tv_for_bio);

        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        tv_for_bio.setText(bio.isEmpty()?"NA":bio);

        String value1 = "",value2 = "",value3 = "";
        if (!valueName.isEmpty()){
            if (valueName.contains(",")){
                String[] values = valueName.split(",");
                if (values.length == 3){
                    value3 = values[2];
                }
                value1 = values[0];
                value2 = values[1];
            }else {
                value1 = valueName;
            }
        }
        if (value1.isEmpty())tv_for_value1.setVisibility(View.GONE);else {tv_for_value1.setVisibility(View.VISIBLE);tv_for_value1.setText(value1);}
        if (value2.isEmpty())tv_for_value2.setVisibility(View.GONE);else {tv_for_value2.setVisibility(View.VISIBLE);tv_for_value2.setText(value2);}
        if (value3.isEmpty())tv_for_value3.setVisibility(View.GONE);else {tv_for_value3.setVisibility(View.VISIBLE);tv_for_value3.setText(value3);}

        String strength1 = "",strength2 = "",strength3 = "";
        if (!strengthName.isEmpty()){
            if (strengthName.contains(",")){
                String[] strength = strengthName.split(",");
                if (strength.length == 3){
                    strength3 = strength[2];
                }
                strength1 = strength[0];
                strength2 = strength[1];
            }else {
                strength1 = strengthName;
            }
        }
        if (strength1.isEmpty())tv_for_strength1.setVisibility(View.GONE);else{tv_for_strength1.setVisibility(View.VISIBLE); tv_for_strength1.setText(strength1);}
        if (strength2.isEmpty())tv_for_strength2.setVisibility(View.GONE);else {tv_for_strength2.setVisibility(View.VISIBLE);tv_for_strength2.setText(strength2);}
        if (strength3.isEmpty())tv_for_strength3.setVisibility(View.GONE);else {tv_for_strength3.setVisibility(View.VISIBLE);tv_for_strength3.setText(strength3);}
    }
}
