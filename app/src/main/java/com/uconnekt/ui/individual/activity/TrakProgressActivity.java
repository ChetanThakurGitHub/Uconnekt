package com.uconnekt.ui.individual.activity;

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

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.activity.TrackInterviewActivity;
import com.uconnekt.util.Utils;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class TrakProgressActivity extends AppCompatActivity implements View.OnClickListener{

    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trak_progress);
        Bundle bundle = getIntent().getExtras();
        time(bundle.getString("startChat"));
        initView();
        userId = bundle.getString("requestBy");
        getUserInfo(userId);
        trackProcess();
    }

    private void initView() {
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        tv_for_tittle.setText(R.string.track_title);
    }

    private void setDataProfile(String profileImage, String fullName, String businessName, String specializationName) {

        ImageView iv_for_profile = findViewById(R.id.iv_for_profile);
        TextView tv_for_fullName = findViewById(R.id.tv_for_fullName);
        TextView tv_for_aofs = findViewById(R.id.tv_for_aofs);
        TextView tv_for_company = findViewById(R.id.tv_for_company);

        Picasso.with(this).load(profileImage).into(iv_for_profile);
        tv_for_fullName.setText(fullName);
        tv_for_company.setText(businessName);
        tv_for_aofs.setText(specializationName);

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
                        String businessName = object.getString("businessName");
                        String specializationName = object.getString("specializationName");
                        setDataProfile(profileImage,fullName,businessName,specializationName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(TrakProgressActivity.this, NetworkActivity.class));
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

    private void trackProcess(){
        new VolleyGetPost(this, AllAPIs.TRACK_PROCESS, true, "Process", true) {
            @SuppressLint("SetTextI18n")
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
                            interview_status1 = object3.getString("interview_status");
                        }

                        if (!is_delete.equals("1")) {
                            TextView tv_for_date3 = findViewById(R.id.tv_for_date3);
                            tv_for_date3.setText(date + " " + time);
                            TextView tv_for_date2 = findViewById(R.id.tv_for_date2);
                            tv_for_date2.setText(date + " " + time);
                        }

                       if (!is_finished.equals("2"))setData(interview_status,request_offer_status,type,interview_status1);

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
                params.put("requestBy",userId);
                params.put("requestFor", Uconnekt.session.getUserInfo().userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void setData(String interview_status, String request_offer_status, String type, String interview_status1){
        //dateTime(accept_declined_time);

        // sent request
        if (interview_status.equals("0")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.darkgray);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.GONE);
            findViewById(R.id.iv_for_second).setVisibility(View.GONE);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            tv_for_data.setText(R.string.pending_recruiter);
            findViewById(R.id.layout_for_request).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.iv_for_second).setVisibility(View.VISIBLE);
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
        }


        if (interview_status.equals("0")&& type.equals("Employer")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.darkgray);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_second).setVisibility(View.GONE);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            tv_for_data.setText(R.string.interview_with_recruiter);
            findViewById(R.id.tv_for_date2).setVisibility(View.GONE);
            findViewById(R.id.tv_for_date3).setVisibility(View.GONE);
            ImageView iv_for_second = findViewById(R.id.iv_for_second);
            iv_for_second.setImageResource(R.drawable.ic_tick_user_ico);
            findViewById(R.id.iv_for_second).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.GONE);
            findViewById(R.id.iv_for_image3).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_for_request).setBackgroundResource(R.drawable.inactive_img);
            findViewById(R.id.tv_for_date2).setVisibility(View.GONE);
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.layout_for_data).setBackgroundResource(R.drawable.active_img);
            TextView tv_for_type = findViewById(R.id.tv_for_type);
            tv_for_type.setText(R.string.pending_employer);
            findViewById(R.id.tv_for_date3).setVisibility(View.VISIBLE);
        }



        // decline employer
        if (interview_status.equals("2")&& type.equals("Employer")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.darkgray);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_second).setVisibility(View.GONE);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            tv_for_data.setText(R.string.interview_with_recruiter);
            findViewById(R.id.tv_for_date2).setVisibility(View.GONE);
            findViewById(R.id.tv_for_date3).setVisibility(View.GONE);
            ImageView iv_for_second = findViewById(R.id.iv_for_second);
            iv_for_second.setImageResource(R.drawable.ic_tick_user_ico);
            findViewById(R.id.iv_for_second).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.GONE);
            findViewById(R.id.iv_for_image3).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_for_request).setBackgroundResource(R.drawable.inactive_img);
            findViewById(R.id.tv_for_date2).setVisibility(View.GONE);
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.layout_for_data).setBackgroundResource(R.drawable.active_img);
            TextView tv_for_type = findViewById(R.id.tv_for_type);
            tv_for_type.setText(R.string.declined_employer);
            findViewById(R.id.tv_for_date3).setVisibility(View.VISIBLE);
        }


        // accept emp
        if (interview_status.equals("1")&& type.equals("Employer")|| interview_status1.equals("1")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.darkgray);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_second).setVisibility(View.GONE);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            tv_for_data.setText(R.string.interview_with_recruiter);
            findViewById(R.id.tv_for_date2).setVisibility(View.GONE);
            findViewById(R.id.tv_for_date3).setVisibility(View.GONE);
            ImageView iv_for_second = findViewById(R.id.iv_for_second);
            iv_for_second.setImageResource(R.drawable.ic_tick_user_ico);
            findViewById(R.id.iv_for_second).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.GONE);
            findViewById(R.id.iv_for_image3).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_for_request).setBackgroundResource(R.drawable.inactive_img);
            findViewById(R.id.tv_for_date2).setVisibility(View.GONE);
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.layout_for_data).setBackgroundResource(R.drawable.active_img);
            TextView tv_for_type = findViewById(R.id.tv_for_type);
            tv_for_type.setText(R.string.accepted_employer);
            findViewById(R.id.tv_for_date3).setVisibility(View.VISIBLE);
        }



        // accept recruter
        if (interview_status.equals("1")&& !type.equals("Employer")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            tv_for_data.setText(R.string.accepted_recruiter);
            ImageView iv_for_second = findViewById(R.id.iv_for_second);
            iv_for_second.setImageResource(R.drawable.ic_tick_user_ico);
            findViewById(R.id.iv_for_second).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.GONE);
            findViewById(R.id.tv_for_date3).setVisibility(View.GONE);
            findViewById(R.id.tv_for_date2).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_for_date3).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_for_request).setBackgroundResource(R.drawable.active_img);
        }

        // decline recruter
        if (interview_status.equals("2")&& !type.equals("Employer")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            tv_for_data.setText(R.string.decliend_recruiter);
            ImageView iv_for_second = findViewById(R.id.iv_for_second);
            iv_for_second.setImageResource(R.drawable.ic_tick_user_ico);
            findViewById(R.id.iv_for_second).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.GONE);
            findViewById(R.id.layout_for_request).setBackgroundResource(R.drawable.active_img);
        }

        // offered
        if (request_offer_status.equals("1")){
            findViewById(R.id.layout_for_data).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.layout_for_data2).setBackgroundResource(R.drawable.active_img);
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.offered_job);
            if(type.equals("Recruiter"))findViewById(R.id.tv_for_date2).setVisibility(View.VISIBLE);
            tv_for_status.setTextColor(Color.parseColor("#0d7d1d"));
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line3).setBackgroundResource(R.color.yellow);
            findViewById(R.id.iv_for_image3).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_image4).setVisibility(View.VISIBLE);
        }

        // not offered
        if (request_offer_status.equals("2")){
            findViewById(R.id.layout_for_data).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.layout_for_data2).setBackgroundResource(R.drawable.active_img);
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.not_offered_job);
            tv_for_status.setTextColor(Color.parseColor("#e21b1b"));
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line3).setBackgroundResource(R.color.yellow);
            findViewById(R.id.iv_for_image3).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_image4).setVisibility(View.VISIBLE);
        }
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

    @SuppressLint("SetTextI18n")
    private void time(String startChat) {
        String time ;
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
