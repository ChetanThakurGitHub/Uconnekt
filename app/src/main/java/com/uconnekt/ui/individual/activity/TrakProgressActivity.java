package com.uconnekt.ui.individual.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class TrakProgressActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trak_progress);
        Bundle bundle = getIntent().getExtras();
        time(bundle.getString("startChat"));
        initView();
        trackProcess(bundle.getString("requestBy"));
    }

    private void initView() {
        ImageView iv_for_profile = findViewById(R.id.iv_for_profile);
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        TextView tv_for_fullName = findViewById(R.id.tv_for_fullName);
        TextView tv_for_aofs = findViewById(R.id.tv_for_aofs);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);

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
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                   // String message = object.getString("message");
                    if (status.equals("success")){
                        JSONObject object1 = object.getJSONObject("data");
                        String request_offer_status = object1.getString("request_offer_status");
                        String type = object1.getString("type");
                        String interview_status = object1.getString("interview_status");
                        String interview_date = Utils.formatDate(object1.getString("interview_time"), "yyyy-MM-dd", "dd-MM-yyyy");
                        String interview_time = object1.getString("interview_date");
                        String accept_declined_time = object1.getString("accept_declined_time");

                        TextView tv_for_date3 = findViewById(R.id.tv_for_date3);
                        tv_for_date3.setText(interview_date+" "+interview_time);

                        TextView tv_for_type = findViewById(R.id.tv_for_type);
                        tv_for_type.setText("Interview with "+type);

                        setData(accept_declined_time,interview_status,request_offer_status);

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
                params.put("requestBy",requestBy);
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

    private void setData(String accept_declined_time, String interview_status, String request_offer_status){
        dateTime(accept_declined_time);

        if (interview_status.equals("0")){
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.darkgray);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_second).setVisibility(View.GONE);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            tv_for_data.setText(R.string.pending_interview);
        }

        if (interview_status.equals("1")){
            ImageView iv_for_second = findViewById(R.id.iv_for_second);
            iv_for_second.setImageResource(R.drawable.ic_tick_user_ico);
            findViewById(R.id.iv_for_second).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_secondIco).setVisibility(View.GONE);
            findViewById(R.id.iv_for_image3).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_for_request).setBackgroundResource(R.drawable.active_img);
            TextView tv_for_data = findViewById(R.id.tv_for_data);
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.layout_for_data).setBackgroundResource(R.drawable.active_img);
            tv_for_data.setText(R.string.acepted_inteview);
        }

        if (request_offer_status.equals("1")){
            findViewById(R.id.layout_for_data).setBackgroundResource(R.drawable.active_img);
            findViewById(R.id.layout_for_data2).setBackgroundResource(R.drawable.active_img);
            TextView tv_for_status = findViewById(R.id.tv_for_status);
            tv_for_status.setText(R.string.offered_job);
            tv_for_status.setTextColor(Color.parseColor("#0d7d1d"));
            findViewById(R.id.view_for_line1).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line2).setBackgroundResource(R.color.yellow);
            findViewById(R.id.view_for_line3).setBackgroundResource(R.color.yellow);
            findViewById(R.id.iv_for_image3).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_for_image4).setVisibility(View.VISIBLE);
        }

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
}
