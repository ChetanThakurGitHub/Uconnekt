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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.ArrayList;
import java.util.Map;

public class PreviousRoleFragment extends Fragment implements View.OnClickListener {

    private ExpActivity activity;
    private Boolean opnClo1 = false, opnClo2 = false, opnClo3 = false;
    private ImageView iv_for_arrow1, iv_for_arrow2, iv_for_arrow3;
    private LinearLayout layout_for_show1, layout_for_show2, layout_for_show3, layout_for_noData;
    private CardView card1, card2, card3;
    private ArrayList<PreviousRole> roleArrayList = new ArrayList<>();
    private View view;
    private Animation animationUp;
    private Animation animationDown;

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

        animationUp = AnimationUtils.loadAnimation(activity, R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(activity, R.anim.slide_down);
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
                        JSONArray array = jsonObject1.getJSONArray("previous_role");
                        for (int i = 0; i < array.length(); i++) {
                            PreviousRole previousRole = new PreviousRole();
                            JSONObject jsonObject2 = array.getJSONObject(i);
                            previousRole.previous_company_name = jsonObject2.getString("previousCompanyName");
                            previousRole.previous_description = URLDecoder.decode(jsonObject2.getString("previousDescription"), "UTF-8");
                            previousRole.previous_job_title = jsonObject2.getString("previous_job_title_id");
                            previousRole.experience = jsonObject2.getString("experience") + " Years";
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
                TextView tv_for_description1 = view.findViewById(R.id.tv_for_description1);
                tv_for_description1.setText(previousRole.previous_description);
                TextView tv_for_totalExp1 = view.findViewById(R.id.tv_for_totalExp1);
                tv_for_totalExp1.setText(previousRole.experience);
                TextView tv_for_total1 = view.findViewById(R.id.tv_for_total1);
                tv_for_total1.setText(previousRole.experience);
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
                TextView tv_for_totalExp2 = view.findViewById(R.id.tv_for_totalExp2);
                tv_for_totalExp2.setText(previousRole1.experience);
                TextView tv_for_description2 = view.findViewById(R.id.tv_for_description2);
                tv_for_description2.setText(previousRole1.previous_description);
                TextView tv_for_total2 = view.findViewById(R.id.tv_for_total2);
                tv_for_total2.setText(previousRole1.experience);
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
                TextView tv_for_totalExp3 = view.findViewById(R.id.tv_for_totalExp3);
                tv_for_totalExp3.setText(previousRole2.experience);
                TextView tv_for_description3 = view.findViewById(R.id.tv_for_description3);
                tv_for_description3.setText(previousRole2.previous_description);
                TextView tv_for_total3 = view.findViewById(R.id.tv_for_total3);
                tv_for_total3.setText(previousRole2.experience);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_for_company1:
                if (!opnClo1) {
                    layout_for_show1.setVisibility(View.VISIBLE);
                    layout_for_show1.startAnimation(animationDown);
                    iv_for_arrow1.setImageResource(R.drawable.ic_up_arrow);
                    opnClo1 = true;
                } else {
                    opnClo1 = false;
                    layout_for_show1.startAnimation(animationUp);
                    layout_for_show1.setVisibility(View.GONE);
                    iv_for_arrow1.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_company2:
                if (!opnClo2) {
                    layout_for_show2.setVisibility(View.VISIBLE);
                    layout_for_show2.startAnimation(animationDown);
                    iv_for_arrow2.setImageResource(R.drawable.ic_up_arrow);
                    opnClo2 = true;
                } else {
                    opnClo2 = false;
                    layout_for_show2.setVisibility(View.GONE);
                    layout_for_show2.startAnimation(animationUp);
                    iv_for_arrow2.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_company3:
                if (!opnClo3) {
                    layout_for_show3.setVisibility(View.VISIBLE);
                    layout_for_show3.startAnimation(animationDown);
                    iv_for_arrow3.setImageResource(R.drawable.ic_up_arrow);
                    opnClo3 = true;
                } else {
                    opnClo3 = false;
                    layout_for_show3.setVisibility(View.GONE);
                    layout_for_show3.startAnimation(animationUp);
                    iv_for_arrow3.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
        }
    }

}
