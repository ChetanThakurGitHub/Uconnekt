package com.uconnekt.ui.employer.activity.experience.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.JobTitle;
import com.uconnekt.model.PreviousRole;
import com.uconnekt.ui.employer.activity.experience.ExpActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

public class PreviousRoleFragment extends Fragment implements View.OnClickListener {

    private ExpActivity activity;
    private Boolean opnClo1 = false, opnClo2 = false, opnClo3 = false;
    private ImageView iv_for_arrow1, iv_for_arrow2, iv_for_arrow3;
    private LinearLayout layout_for_show1, layout_for_show2, layout_for_show3, layout_for_noData;
    private CardView card1, card2, card3;
    private ArrayList<PreviousRole> roleArrayList = new ArrayList<>();
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_previous_role, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.layout_for_company1).setOnClickListener(this);
        view.findViewById(R.id.layout_for_company2).setOnClickListener(this);
        view.findViewById(R.id.layout_for_company3).setOnClickListener(this);
        iv_for_arrow1 = view.findViewById(R.id.iv_for_arrow1);
        iv_for_arrow2 = view.findViewById(R.id.iv_for_arrow2);
        iv_for_arrow3 = view.findViewById(R.id.iv_for_arrow3);
        layout_for_show1 = view.findViewById(R.id.layout_for_show1);
        layout_for_show2 = view.findViewById(R.id.layout_for_show2);
        layout_for_show3 = view.findViewById(R.id.layout_for_show3);
        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        card3 = view.findViewById(R.id.card3);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showPrefilledData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ExpActivity) context;
    }

    private void showPrefilledData() {

        new VolleyGetPost(activity, AllAPIs.INDI_PROFILE + activity.userId, false, "getUserPersonalInfo", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    roleArrayList.clear();
                    if (status.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("experience");
                        JSONObject jsonObject3 = jsonObject1.getJSONObject("previous_role");
                        JSONArray array = jsonObject3.getJSONArray("previous_experience");
                        for (int i = 0; i < array.length(); i++) {
                            PreviousRole previousRole = new PreviousRole();
                            JSONObject jsonObject2 = array.getJSONObject(i);
                            previousRole.previous_company_name = jsonObject2.getString("previous_company_name");
                           // previousRole.previous_description = jsonObject2.getString("previous_description");
                            previousRole.previous_description = URLDecoder.decode(jsonObject2.getString("previous_description"), "UTF-8");
                            previousRole.previous_finish_date = jsonObject2.getString("previous_finish_date");
                            previousRole.previous_job_title = jsonObject2.getString("previous_job_title");
                            previousRole.previous_start_date = jsonObject2.getString("previous_start_date");
                            roleArrayList.add(previousRole);
                        }
                        getlist();
                    } else {
                        layout_for_noData.setVisibility(View.VISIBLE);
                    }

                    layout_for_noData.setVisibility(roleArrayList.size() == 0 ? View.VISIBLE : View.GONE);

                } catch (JSONException e) {
                    layout_for_noData.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void getlist() {

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list", false) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results;
                        if (activity.userId.equals(Uconnekt.session.getUserInfo().userId)) {
                            results = result.getJSONArray("job_title");
                        }else {
                            results = result.getJSONArray("opposite_job_title");
                        }

                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("jobTitleId");
                            jobTitles.jobTitleName = object.getString("jobTitleName");

                            for (int j=0;j<roleArrayList.size();j++){

                                if (jobTitles.jobTitleId.equals(roleArrayList.get(j).previous_job_title)) {
                                    roleArrayList.get(j).previous_job_title = jobTitles.jobTitleName;
                                    setData(view,j, roleArrayList);
                                }
                            }
                        }
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
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    @SuppressLint("SetTextI18n")
    private void setData(View view, int j, ArrayList<PreviousRole> roleArrayList) {
        switch (j) {
            case 0:
                PreviousRole previousRole = roleArrayList.get(0);
                card1.setVisibility(View.VISIBLE);
                TextView tv_for_company1 = view.findViewById(R.id.tv_for_company1);
                tv_for_company1.setText(previousRole.previous_job_title);
                TextView tv_for_txt1 = view.findViewById(R.id.tv_for_txt1);
                tv_for_txt1.setText(previousRole.previous_company_name.substring(0,1).toUpperCase() + previousRole.previous_company_name.substring(1));
                TextView tv_for_jobTitle1 = view.findViewById(R.id.tv_for_jobTitle1);
                tv_for_jobTitle1.setText(previousRole.previous_job_title);
                TextView layout_for_startDP1 = view.findViewById(R.id.layout_for_startDP1);
                layout_for_startDP1.setText(previousRole.previous_start_date);
                TextView tv_for_finishDP1 = view.findViewById(R.id.tv_for_finishDP1);
                tv_for_finishDP1.setText(previousRole.previous_finish_date);
                TextView tv_for_description1 = view.findViewById(R.id.tv_for_description1);
                tv_for_description1.setText(previousRole.previous_description);

                if (!previousRole.previous_start_date.isEmpty()&&!previousRole.previous_finish_date.isEmpty()) {
                   getDayDifference(previousRole.previous_start_date, previousRole.previous_finish_date,0);
                }

                break;
            case 1:
                PreviousRole previousRole1 = roleArrayList.get(1);
                card2.setVisibility(View.VISIBLE);
                TextView tv_for_company2 = view.findViewById(R.id.tv_for_company2);
                tv_for_company2.setText(previousRole1.previous_job_title);
                TextView tv_for_txt2 = view.findViewById(R.id.tv_for_txt2);
                tv_for_txt2.setText(previousRole1.previous_company_name.substring(0,1).toUpperCase() + previousRole1.previous_company_name.substring(1));
                TextView tv_for_jobTitle2 = view.findViewById(R.id.tv_for_jobTitle2);
                tv_for_jobTitle2.setText(previousRole1.previous_job_title);
                TextView layout_for_startDP2 = view.findViewById(R.id.layout_for_startDP2);
                layout_for_startDP2.setText(previousRole1.previous_start_date);
                TextView tv_for_finishDP2 = view.findViewById(R.id.tv_for_finishDP2);
                tv_for_finishDP2.setText(previousRole1.previous_finish_date);
                TextView tv_for_description2 = view.findViewById(R.id.tv_for_description2);
                tv_for_description2.setText(previousRole1.previous_description);

                if (!previousRole1.previous_start_date.isEmpty()&&!previousRole1.previous_finish_date.isEmpty()) {
                    getDayDifference(previousRole1.previous_start_date, previousRole1.previous_finish_date,1);
                }
                break;
            case 2:
                PreviousRole previousRole2 = roleArrayList.get(2);
                card3.setVisibility(View.VISIBLE);
                TextView tv_for_company3 = view.findViewById(R.id.tv_for_company3);
                tv_for_company3.setText(previousRole2.previous_job_title);
                TextView tv_for_txt3 = view.findViewById(R.id.tv_for_txt3);
                tv_for_txt3.setText(previousRole2.previous_company_name.substring(0,1).toUpperCase() + previousRole2.previous_company_name.substring(1));
                TextView tv_for_jobTitle3 = view.findViewById(R.id.tv_for_jobTitle3);
                tv_for_jobTitle3.setText(previousRole2.previous_job_title);
                TextView layout_for_startDP3 = view.findViewById(R.id.layout_for_startDP3);
                layout_for_startDP3.setText(previousRole2.previous_start_date);
                TextView tv_for_finishDP3 = view.findViewById(R.id.tv_for_finishDP3);
                tv_for_finishDP3.setText(previousRole2.previous_finish_date);
                TextView tv_for_description3 = view.findViewById(R.id.tv_for_description3);
                tv_for_description3.setText(previousRole2.previous_description);

                if (!previousRole2.previous_start_date.isEmpty()&&!previousRole2.previous_finish_date.isEmpty()) {
                    getDayDifference(previousRole2.previous_start_date, previousRole2.previous_finish_date,2);
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_for_company1:
                if (!opnClo1) {
                    layout_for_show1.setVisibility(View.VISIBLE);
                    iv_for_arrow1.setImageResource(R.drawable.ic_up_arrow);
                    opnClo1 = true;
                } else {
                    opnClo1 = false;
                    layout_for_show1.setVisibility(View.GONE);
                    iv_for_arrow1.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_company2:
                if (!opnClo2) {
                    layout_for_show2.setVisibility(View.VISIBLE);
                    iv_for_arrow2.setImageResource(R.drawable.ic_up_arrow);
                    opnClo2 = true;
                } else {
                    opnClo2 = false;
                    layout_for_show2.setVisibility(View.GONE);
                    iv_for_arrow2.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_company3:
                if (!opnClo3) {
                    layout_for_show3.setVisibility(View.VISIBLE);
                    iv_for_arrow3.setImageResource(R.drawable.ic_up_arrow);
                    opnClo3 = true;
                } else {
                    opnClo3 = false;
                    layout_for_show3.setVisibility(View.GONE);
                    iv_for_arrow3.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
        }
    }



    private void getDayDifference(String departDateTime, String returnDateTime, int i) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.US);

        try {
            Date startDate = simpleDateFormat.parse(departDateTime);
            Date endDate = simpleDateFormat.parse(returnDateTime);

            Calendar startCalendar = new GregorianCalendar();
            startCalendar.setTime(startDate);
            Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(endDate);

            int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

            if (diffMonth != 0){
                diffMonth = diffMonth - 12*diffYear;
            }

            long days = 0;

            if (diffMonth == 0 && diffYear == 0) {

                long different = endDate.getTime() - startDate.getTime();

                System.out.println("startDate : " + startDate);
                System.out.println("endDate : " + endDate);
                System.out.println("different : " + different);


                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;
                long daysInMilli = hoursInMilli * 24;
                long monthInMilli = daysInMilli * 30;
                long yearInMilli = monthInMilli * 12;

                long elapsedYear = different / yearInMilli;
                different = different % yearInMilli;

                long elapsedMonth = different / monthInMilli;
                different = different % monthInMilli;

                long elapsedDays = different / daysInMilli;
                
                days = elapsedDays;
            }
   

            setDate(diffYear,diffMonth,i,days,departDateTime,returnDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDate(long elapsedYear, long elapsedMonth, int i, long elapsedDays, String startDate, String endDate){
        String result ;
        if (elapsedYear != 0 | elapsedMonth != 0){
            result = elapsedYear+" Years, ";
            result = result +Math.abs( elapsedMonth) + " Months";
            dateSet(i,result,startDate,endDate);
        }

        if (elapsedMonth == 0 && elapsedYear == 0) {
            result = Math.abs(elapsedDays) + " Days";
            dateSet(i,result,startDate,endDate);
        }
    }




    @SuppressLint("SetTextI18n")
    private void dateSet(int i, String result, String startDate, String endDate){
        TextView tv_for_totalExp1 = view.findViewById(R.id.tv_for_totalExp1);
        TextView tv_for_totalExp2 = view.findViewById(R.id.tv_for_totalExp2);
        TextView tv_for_totalExp3 = view.findViewById(R.id.tv_for_totalExp3);
        TextView tv_for_total1 = view.findViewById(R.id.tv_for_total1);
        TextView tv_for_total2 = view.findViewById(R.id.tv_for_total2);
        TextView tv_for_total3 = view.findViewById(R.id.tv_for_total3);
        switch (i) {
            case 0:
                tv_for_totalExp1.setText(result);
                tv_for_total1.setText(startDate+" - "+endDate+" ( "+result+" )");
                break;
            case 1:
                tv_for_totalExp2.setText(result);
                tv_for_total2.setText(startDate+" - "+endDate+" ( "+result+" )");
                break;
            case 2:
                tv_for_totalExp3.setText(result);
                tv_for_total3.setText(startDate+" - "+endDate+" ( "+result+" )");
                break;
        }
    }
}
