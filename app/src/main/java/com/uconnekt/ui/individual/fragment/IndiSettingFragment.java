package com.uconnekt.ui.individual.fragment;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.UserInfo;
import com.uconnekt.ui.individual.activity.FavouriteActivity;
import com.uconnekt.ui.individual.activity.RecommendedActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class IndiSettingFragment extends Fragment implements View.OnClickListener{

    private JobHomeActivity activity;
    private ImageView iv_for_btn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indi_setting, container, false);
        initView(view);
        iv_for_btn.setImageResource(Uconnekt.session.getUserInfo().isNotify.equals("1")?R.drawable.on_ico:R.drawable.off_btn);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.layout_for_logout).setOnClickListener(this);
        view.findViewById(R.id.card_for_favourite).setOnClickListener(this);
        view.findViewById(R.id.card_for_recommend).setOnClickListener(this);
        view.findViewById(R.id.card_for_changePass).setOnClickListener(this);
        iv_for_btn = view.findViewById(R.id.iv_for_btn);
        iv_for_btn.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobHomeActivity) context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_for_logout:
                logout();
                break;
            case R.id.iv_for_btn:
                noticaitonOnOff();
                break;
            case R.id.card_for_favourite:
                Intent intent = new Intent(activity,FavouriteActivity.class);
                intent.putExtra("USERID","-1");
                activity.startActivity(intent);
                break;
            case R.id.card_for_recommend:
                intent = new Intent(activity,RecommendedActivity.class);
                intent.putExtra("USERID","-1");
                activity.startActivity(intent);
                break;
            case R.id.card_for_changePass:
                changePass();
                break;
        }
    }

    private void changePass(){
        Dialog dialog = new Dialog(activity);
        dialog.show();
    }


    private void logout(){
        new VolleyGetPost(activity, AllAPIs.LOGOUT, false, "Logout", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){

                        FirebaseDatabase.getInstance().getReference().child("users").child(Uconnekt.session.getUserInfo().userId).child("firebaseToken").setValue("");
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        NotificationManager notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        Uconnekt.session.logout(activity);
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

    private void noticaitonOnOff(){
        new VolleyGetPost(activity, AllAPIs.NOTIFICATION, false, "Notification", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){
                        String isNotify = object.getString("isNotify");
                        iv_for_btn.setImageResource(isNotify.equals("1")?R.drawable.on_ico:R.drawable.off_btn);
                        UserInfo userInfo = Uconnekt.session.getUserInfo();
                        userInfo.isNotify = isNotify;
                        Uconnekt.session.createSession(userInfo);
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
