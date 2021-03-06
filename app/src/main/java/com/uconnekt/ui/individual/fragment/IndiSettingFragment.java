package com.uconnekt.ui.individual.fragment;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.UserInfo;
import com.uconnekt.ui.common_activity.AboutUsActivity;
import com.uconnekt.ui.common_activity.DeactiateAccountActivity;
import com.uconnekt.ui.common_activity.DocViewActivity;
import com.uconnekt.ui.common_activity.HelpAndSupportActivity;
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
        view.findViewById(R.id.card_for_changePass).setOnClickListener(this);
        view.findViewById(R.id.card_for_aboutUs).setOnClickListener(this);
        view.findViewById(R.id.card_for_help).setOnClickListener(this);
        view.findViewById(R.id.card_for_share).setOnClickListener(this);
        view.findViewById(R.id.tvActiceStatus).setOnClickListener(this);
        view.findViewById(R.id.tvTermsAndCondi).setOnClickListener(this);
        view.findViewById(R.id.tvPrivacyPolicy).setOnClickListener(this);
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
            case R.id.card_for_changePass:
                changePasswordDailog();
                break;
            case R.id.card_for_aboutUs:
                Intent intent = new Intent(activity, AboutUsActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.card_for_help:
                intent = new Intent(activity, HelpAndSupportActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.card_for_share:
                share();
                break;
            case R.id.tvActiceStatus:
                startActivity(new Intent(activity, DeactiateAccountActivity.class));
                break;
            case R.id.tvTermsAndCondi:
                intent = new Intent(activity, DocViewActivity.class);
                intent.putExtra("title",true);
                activity.startActivity(intent);
                break;
            case R.id.tvPrivacyPolicy:
                intent = new Intent(activity, DocViewActivity.class);
                intent.putExtra("title",false);
                activity.startActivity(intent);
                break;
        }
    }

    private void share(){
        Intent sharIntent = new Intent(Intent.ACTION_SEND);
        sharIntent.setType("text/plain");
        sharIntent.putExtra(Intent.EXTRA_SUBJECT, "Download ConnektUs App");
        sharIntent.putExtra(Intent.EXTRA_TEXT, "Download the app using https://play.google.com/store Hurry, it doesn’t get better than this! Download the ConnektUs App NOW!");
        startActivity(Intent.createChooser(sharIntent, "Share:"));
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
                        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                        assert notificationManager != null; notificationManager.cancelAll();
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

    private void changePasswordDailog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        final EditText et_for_oldPass = dialog.findViewById(R.id.et_for_oldPass);
        final EditText et_for_newPass = dialog.findViewById(R.id.et_for_newPass);
        final EditText et_for_cPass = dialog.findViewById(R.id.et_for_cPass);

        dialog.findViewById(R.id.iv_for_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_for_newPass.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_for_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_for_newPass.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String oldPassword = et_for_oldPass.getText().toString().trim();
                String newPassword = et_for_newPass.getText().toString().trim();
                String cPassword = et_for_cPass.getText().toString().trim();
                if (oldPassword.isEmpty()){
                    Toast.makeText(activity, R.string.old_pass , Toast.LENGTH_SHORT).show();
                }else if (!oldPassword.equals(Uconnekt.session.getUserInfo().password)){
                    Toast.makeText(activity, R.string.passwrod_not_match, Toast.LENGTH_SHORT).show();
                }else if (newPassword.isEmpty()){
                    Toast.makeText(activity, R.string.new_pass , Toast.LENGTH_SHORT).show();
                }else if (newPassword.length() < 6){
                    Toast.makeText(activity,R.string.password_required , Toast.LENGTH_SHORT).show();
                }else if (newPassword.length() > 16){
                    Toast.makeText(activity, R.string.password_required , Toast.LENGTH_SHORT).show();
                } else if (cPassword.isEmpty()){
                    Toast.makeText(activity, R.string.c_pass , Toast.LENGTH_SHORT).show();
                }else if (cPassword.length() < 6){
                    Toast.makeText(activity,R.string.password_required , Toast.LENGTH_SHORT).show();
                }else if (cPassword.length() > 16){
                    Toast.makeText(activity, R.string.password_required , Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(cPassword)){
                    Toast.makeText(activity, R.string.cant_same_pass, Toast.LENGTH_SHORT).show();
                }else if (oldPassword.equals(newPassword)){
                    Toast.makeText(activity, R.string.cant_same, Toast.LENGTH_SHORT).show();
                }else {
                    changePasswordAPI(oldPassword,newPassword,dialog);
                }
            }
        });
        dialog.show();
    }

    private void changePasswordAPI(final String oldPassword, final String newPassword, final Dialog dialog) {
        new VolleyGetPost(activity, AllAPIs.CHANGE_PASSWOWD, true, "ChangePassword", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")){
                        String message = jsonObject.getString("message");
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

                        UserInfo userInfo = Uconnekt.session.getUserInfo();
                        userInfo.password = newPassword;
                        Uconnekt.session.createSession(userInfo);
                        Uconnekt.session.logoutMyPre();

                        dialog.dismiss();
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
                params.put("old_pass",oldPassword);
                params.put("new_pass",newPassword);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }
}
