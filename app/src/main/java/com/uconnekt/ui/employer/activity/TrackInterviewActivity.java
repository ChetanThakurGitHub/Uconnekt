package com.uconnekt.ui.employer.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.util.Utils;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class TrackInterviewActivity extends AppCompatActivity implements View.OnClickListener {

    private String interviewID = "",deleteNode = "",chatNode = "",interviewID1= "",userId= "";
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
        userId = bundle.getString("requestBy");
        getUserInfo(userId);
        trackProcess();
    }


    private void getUserInfo(String requestBy){
        new VolleyGetPost(this, AllAPIs.PROFILE+"user_id="+requestBy,false,"Profile",false) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONObject object = jsonObject.getJSONObject("profile");
                        String profileImage = object.getString("profileImage");
                        String fullName = object.getString("fullName");
                        String jobTitleName = object.getString("jobTitleName");
                        String specializationName = object.getString("specializationName");
                        setData(profileImage,fullName,jobTitleName,specializationName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(TrackInterviewActivity.this, NetworkActivity.class));
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


    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_delete = findViewById(R.id.tv_for_delete);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        tv_for_tittle.setText(R.string.track_title);
    }

    private void setData(String profileImage, String fullName, String jobTitleName, String specializationName) {
        ImageView iv_for_profile = findViewById(R.id.iv_for_profile);
        TextView tv_for_fullName = findViewById(R.id.tv_for_fullName);
        TextView tv_for_aofs = findViewById(R.id.tv_for_aofs);
        TextView tv_for_jobTitle = findViewById(R.id.tv_for_jobTitle);


        Picasso.with(this).load(profileImage).into(iv_for_profile);
        tv_for_fullName.setText(fullName);
        tv_for_aofs.setText(specializationName);
        tv_for_jobTitle.setText(jobTitleName);
    }

    private void trackProcess(){
        new VolleyGetPost(this, AllAPIs.TRACK_PROCESS, true, "Process", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){

                        JSONObject object1 = object.getJSONObject("data");
                        String requestId = object1.getString("requestId");
                        String request_offer_status = object1.getString("request_offer_status");
                        String is_finished = object1.getString("is_finished");
                        String interviewData = object1.getString("interviewData");
                        String createdDate = object1.getString("Created Date");
                        String count = object1.getString("count");
                        JSONArray array = object1.getJSONArray("interviewData");
                        JSONObject object2 = array.getJSONObject(0);
                        String type = object2.getString("type");
                        String is_delete = object2.getString("is_delete");
                        String interview_status = object2.getString("interview_status");
                        String upd = object2.getString("upd");
                        String date = Utils.formatDate(object2.getString("date"), "yyyy-MM-dd", "dd-MM-yyyy");
                        String time = object2.getString("time");

                        String interview_status1 = "";
                        if (count.equals("2")){
                            JSONObject object3 = array.getJSONObject(1);
                            interviewID1 = object3.getString("interviewId");
                            interviewID = interviewID1;
                            interview_status1 = object3.getString("interview_status");
                        }

                        if (!is_finished.equals("2"))setData(type,interview_status,date,time,interview_status1,request_offer_status);

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
                params.put("requestFor", userId);
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
    private void setData(String type, String interview_status, String date, String time, String interview_status1, String request_offer_status) {

        if (type.equals("Recruiter")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.iv_for_process).setVisibility(View.VISIBLE);
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.pending_recruiter);
            tv_for_delete.setVisibility(View.VISIBLE);
            tv_for_delete.setOnClickListener(this);
            tv_for_delete.setBackgroundResource(R.drawable.button_bg_not_offered);
            findViewById(R.id.layout_for_delete).setBackgroundResource(R.drawable.active_img);
        }else {
            View view_for_line1 = findViewById(R.id.view_for_line1);
            view_for_line1.setBackgroundResource(R.color.yellow);
            findViewById(R.id.iv_for_process).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_for_date4).setVisibility(View.GONE);
            findViewById(R.id.tv_for_delete).setVisibility(View.GONE);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            ImageView iv_for_process = findViewById(R.id.iv_for_process);
            findViewById(R.id.iv_for_dactive).setVisibility(View.VISIBLE);
            iv_for_process.setImageResource(R.drawable.ic_add_user);
            TextView tv_for_type =findViewById(R.id.tv_for_type);
            tv_for_type.setText(R.string.pending_employer);
            TextView tv_for_delete2 = findViewById(R.id.tv_for_delete2);
            tv_for_delete2.setVisibility(View.VISIBLE);
            tv_for_delete2.setOnClickListener(this);
            findViewById(R.id.layout_for_interview).setBackgroundResource(R.drawable.active_img);
            if (interview_status.equals("1")){
                tv_for_type.setText(R.string.accepted_employer);
                tv_for_delete2.setText(R.string.delete_request);
            }
            if (interview_status.equals("2")){
                tv_for_type.setText(R.string.declined_employer);
                tv_for_delete2.setText(R.string.delete_request);
            }
        }

        //declined interview
        if (interview_status.equals("2")&& type.equals("Recruiter")){
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.decliend_recruiter);
            tv_for_delete.setText(R.string.delete_request);
            ImageView iv_for_process = findViewById(R.id.iv_for_process);
            iv_for_process.setImageResource(R.drawable.ic_user_decline);
        }

        //accept interview
        if (interview_status.equals("1") && !interviewID1.isEmpty()){
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.accepted_recruiter);
            View view_for_line2 = findViewById(R.id.view_for_line2);
            view_for_line2.setBackgroundResource(R.color.yellow);
            ImageView iv_for_process = findViewById(R.id.iv_for_process);
            findViewById(R.id.iv_for_dactive).setVisibility(View.VISIBLE);
            tv_for_delete.setVisibility(View.GONE);
            iv_for_process.setImageResource(R.drawable.ic_add_user);
            findViewById(R.id.layout_for_interview).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.iv_for_confirm).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_confirm).setOnClickListener(this);
        }else if (interview_status.equals("1") && interviewID1.isEmpty()){
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line3).setBackgroundResource(R.color.yellow);
            ImageView iv_for_process = findViewById(R.id.iv_for_process);
            findViewById(R.id.iv_for_dactive).setVisibility(View.VISIBLE);
            tv_for_delete.setVisibility(View.GONE);
            findViewById(R.id.tv_for_delete2).setVisibility(View.GONE);
            iv_for_process.setImageResource(R.drawable.ic_add_user);
            findViewById(R.id.layout_for_interview).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.layout_for_status).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.tv_for_offered).setBackgroundResource(R.drawable.button_bg_offered);
            findViewById(R.id.tv_for_notOffered).setBackgroundResource(R.drawable.button_bg_not_offered);
            findViewById(R.id.tv_for_offered).setOnClickListener(TrackInterviewActivity.this);
            findViewById(R.id.tv_for_notOffered).setOnClickListener(TrackInterviewActivity.this);
            findViewById(R.id.ivLastImage).setVisibility(View.VISIBLE);
        }

        // after confirm
       if (interview_status1.equals("1")){
           confirmChangeInUI();
        }

        //offered jod
        if (request_offer_status.equals("1")){
            findViewById(R.id.layoutStatus).setVisibility(View.GONE);
            TextView tv_for_statusJob = findViewById(R.id.tv_for_statusJob);
            tv_for_statusJob.setVisibility(View.VISIBLE);
            tv_for_statusJob.setText(R.string.offered_job);
            findViewById(R.id.tv_for_delete2).setVisibility(View.GONE);
            tv_for_statusJob.setTextColor(Color.parseColor("#0d7d1d"));
        }

        // not offered job
        if (request_offer_status.equals("2")){
            findViewById(R.id.layoutStatus).setVisibility(View.GONE);
            TextView tv_for_statusJob = findViewById(R.id.tv_for_statusJob);
            tv_for_statusJob.setVisibility(View.VISIBLE);
            tv_for_statusJob.setText(R.string.not_offered_job);
            findViewById(R.id.tv_for_delete2).setVisibility(View.GONE);
            tv_for_statusJob.setTextColor(Color.parseColor("#e21b1b"));
        }

        TextView tv_for_date3 = findViewById(R.id.tv_for_date3);
        TextView tv_for_date4 = findViewById(R.id.tv_for_date4);
        tv_for_date4.setText(date+" "+time);
        tv_for_date3.setText(date+" "+time);
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
            case R.id.iv_for_confirm:
                interviewRequestAPI();
                break;
            case R.id.tv_for_delete2:
                deleteProcess();
                break;
        }
    }

    private void confirmChangeInUI(){
        TextView tv_for_type =findViewById(R.id.tv_for_type);
        tv_for_type.setText(R.string.accepted_employer);

        findViewById(R.id.ivLastImage).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_for_confirm).setVisibility(View.GONE);
        findViewById(R.id.view_for_line3).setBackgroundResource(R.color.yellow);
        findViewById(R.id.layout_for_status).setBackgroundResource(R.drawable.active_img);
        findViewById(R.id.layout_for_interview).setBackgroundResource(R.drawable.active_img);
        findViewById(R.id.layout_for_status).setBackgroundResource(R.drawable.active_img);
        findViewById(R.id.tv_for_offered).setBackgroundResource(R.drawable.button_bg_offered);
        findViewById(R.id.tv_for_notOffered).setBackgroundResource(R.drawable.button_bg_not_offered);
        findViewById(R.id.tv_for_offered).setOnClickListener(TrackInterviewActivity.this);
        findViewById(R.id.tv_for_notOffered).setOnClickListener(TrackInterviewActivity.this);
    }

    private void interviewRequestAPI() {
        new VolleyGetPost(this, AllAPIs.A_D_REQUEST, true, "REQUESTUPDATE", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")){

                        confirmChangeInUI();

                    }else {
                        Toast.makeText(TrackInterviewActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put("action", "1");
                params.put("interviewId", interviewID);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
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
                                .child("chat_rooms/" + chatNode).child(deleteNode).removeValue();
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
            trackProcess();
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
