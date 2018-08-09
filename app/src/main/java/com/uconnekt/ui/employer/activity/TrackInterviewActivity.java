package com.uconnekt.ui.employer.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.util.Utils;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class TrackInterviewActivity extends AppCompatActivity implements View.OnClickListener {

    private String interviewID = "",deleteNode = "",chatNode = "";
    private TextView tv_for_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_interview);
        Bundle bundle = getIntent().getExtras();
        time(bundle.getString("startChat"));
        initView();
        interviewID = bundle.getString("interviewID");
        deleteNode = bundle.getString("deleteNode");
        chatNode = bundle.getString("chatNode");
        trackProcess(bundle.getString("requestBy"));
    }

    private void initView() {
        ImageView iv_for_profile = findViewById(R.id.iv_for_profile);
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        TextView tv_for_fullName = findViewById(R.id.tv_for_fullName);
        TextView tv_for_aofs = findViewById(R.id.tv_for_aofs);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_delete = findViewById(R.id.tv_for_delete);

        tv_for_delete.setOnClickListener(this);

        setData(iv_for_profile,tv_for_fullName,tv_for_aofs,iv_for_backIco,tv_for_tittle);
    }

    private void setData(ImageView iv_for_profile, TextView tv_for_fullName, TextView tv_for_aofs, ImageView iv_for_backIco, TextView tv_for_tittle) {
        Picasso.with(this).load(Uconnekt.session.getUserInfo().profileImage).into(iv_for_profile);
        tv_for_fullName.setText(Uconnekt.session.getUserInfo().fullName);
        tv_for_aofs.setText(Uconnekt.session.getUserInfo().specializationName);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        tv_for_tittle.setText(R.string.track_title);

    }

    private void trackProcess(final String requestBy){
        new VolleyGetPost(this, AllAPIs.TRACK_PROCESS, true, "Process", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){

                        JSONObject object1 = object.getJSONObject("data");
                        String type = object1.getString("type");
                        String interview_status = object1.getString("interview_status");
                        String interview_date = Utils.formatDate(object1.getString("interview_time"), "yyyy-MM-dd", "dd-MM-yyyy");
                        String interview_time = object1.getString("interview_date");
                        String accept_declined_time = object1.getString("accept_declined_time");

                        setData(accept_declined_time,interview_status,interview_date,interview_time,type);
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
                params.put("requestBy",Uconnekt.session.getUserInfo().userId);
                params.put("requestFor", requestBy);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    @SuppressLint("SetTextI18n")
    private void setData(String accept_declined_time, String interview_status, String interview_date, String interview_time, String type){

        dateTime(accept_declined_time);

        if (interview_status.equals("2")){
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.interview_request);
            tv_for_delete.setText(R.string.delete_request);
            ImageView iv_for_process = findViewById(R.id.iv_for_process);
            iv_for_process.setImageResource(R.drawable.ic_user_decline);
        }

        if (interview_status.equals("1")){
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.acepted_inteview);
            View view_for_line2 = findViewById(R.id.view_for_line2);
            view_for_line2.setBackgroundResource(R.color.yellow);
            ImageView iv_for_process = findViewById(R.id.iv_for_process);
            findViewById(R.id.iv_for_dactive).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_for_date2).setVisibility(View.VISIBLE);
            tv_for_delete.setVisibility(View.GONE);
            iv_for_process.setImageResource(R.drawable.ic_add_user);
            findViewById(R.id.layout_for_interview).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.tv_for_offered).setBackgroundResource(R.drawable.button_bg_offered);
            findViewById(R.id.tv_for_notOffered).setBackgroundResource(R.drawable.button_bg_not_offered);
            findViewById(R.id.tv_for_offered).setOnClickListener(TrackInterviewActivity.this);
            findViewById(R.id.tv_for_notOffered).setOnClickListener(TrackInterviewActivity.this);
        }

        TextView tv_for_type = findViewById(R.id.tv_for_type);
        tv_for_type.setText("Interview with "+type);

        TextView tv_for_date3 = findViewById(R.id.tv_for_date3);
        tv_for_date3.setText(interview_date+" "+interview_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.tv_for_delete:
                deleteProcess();
                break;
            case R.id.tv_for_offered:
                jobOffer(1);
                break;
            case R.id.tv_for_notOffered:
                jobOffer(2);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void deleteProcess(){
        new VolleyGetPost(this, AllAPIs.DELETE_INTERVIEW, true, "DeleteProcess", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");
                    if (status.equals("success")){
                        FirebaseDatabase.getInstance().getReference()
                                .child("chat_rooms/" + chatNode).child(deleteNode).setValue(null);
                        Toast.makeText(TrackInterviewActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
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
                params.put("interviewId",interviewID);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void jobOffer(final int i){
        new VolleyGetPost(this, AllAPIs.OFFER_OR_NOT, true, "Offer", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){finish();}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {

            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("interviewId",interviewID);
                params.put("action", String.valueOf(i));
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    @SuppressLint("SetTextI18n")
    private void time(String startChat) {
        String time;
        try {
            long timeStamp = Long.parseLong(startChat);

            @SuppressLint("SimpleDateFormat")
            DateFormat f = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.mmm'Z'");
            System.out.println(f.format(timeStamp));

            String CurrentString = f.format(timeStamp);
            String date = CurrentString.substring(0,10);
            int hourOfDay = Integer.parseInt(CurrentString.substring(11, 13));
            int minute = Integer.parseInt(CurrentString.substring(14, 16));

            String status, minutes;

            if (hourOfDay > 12) {
                hourOfDay -= 12;
                status = "PM";
            } else if (hourOfDay == 0) {
                hourOfDay += 12;
                status = "AM";
            } else if (hourOfDay == 12) {
                status = "PM";
            } else {
                status = "AM";
            }

            minutes = (minute < 10) ? "0" + minute : String.valueOf(minute);
            time = hourOfDay + ":" + minutes + " " + status;

            TextView tv_for_date1 = findViewById(R.id.tv_for_date1);
            tv_for_date1.setText(date+" "+time+"");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void dateTime(String accept_declined_time){
        String date = accept_declined_time.substring(0, 10);
        date = Utils.formatDate(date, "yyyy-MM-dd", "dd-MM-yyyy");
        String hourOfDay = accept_declined_time.substring(11, 13);
        String minute = accept_declined_time.substring(14, 16);
        String time = Utils.format12HourTime(hourOfDay + ":" + minute, "HH:mm", "hh:mm a");
        TextView tv_for_date2 = findViewById(R.id.tv_for_date2);
        tv_for_date2.setText(date + " " + time + "");
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("IntentFilterTracking");
        this.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
