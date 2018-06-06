package com.uconnekt.ui.individual.individual_profile.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.individual.individual_profile.profile_fragrment.Basic_info.BasicInfoFragment;
import com.uconnekt.ui.individual.individual_profile.profile_fragrment.ExperienceFragment;
import com.uconnekt.ui.individual.individual_profile.profile_fragrment.ResumeFragment;
import com.uconnekt.util.Constant;

public class JobProfileActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    public TabLayout tablayout;
    private Boolean doubleBackToExitPressedOnce = false;
    public Boolean isBasicInfo=false;
    private RelativeLayout mainlayout;
    private BasicInfoFragment infoFragment;
    public ExperienceFragment experienceFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_profile);
        initView();
        infoFragment=BasicInfoFragment.newInstance();
        experienceFragment=ExperienceFragment.newInstance();

        addFragment(experienceFragment, false, R.id.framlayout);
        addFragment(infoFragment, false, R.id.framlayout);
        setTab(tablayout.getTabAt(0),true);
    }

    private void initView(){
        tablayout = findViewById(R.id.tablayout);
        mainlayout = findViewById(R.id.mainlayout);
        ImageView iv_for_profile = findViewById(R.id.iv_for_profile);
        TextView tv_for_fullName = findViewById(R.id.tv_for_fullName);
        tablayout.addOnTabSelectedListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.profile);

        String image = Uconnekt.session.getUserInfo().profileImage;
        if (image != null && !image.equals("")) Picasso.with(this).load(image).into(iv_for_profile);
        tv_for_fullName.setText(Uconnekt.session.getUserInfo().fullName);
    }

    public void setTab(TabLayout.Tab tab, boolean isSelected){
        ((TextView) tab.getCustomView().findViewById(android.R.id.text1)).setTextColor(getResources().getColor(isSelected?R.color.yellow:R.color.darkgray));
    }

    @Override
    public void onBackPressed() {
        if (Uconnekt.session.isLoggedIn()) {
            if (tablayout.getSelectedTabPosition()>0) {
                if(tablayout.getSelectedTabPosition()>0)tablayout.getTabAt(tablayout.getSelectedTabPosition()-1).select();
                if (tablayout.getSelectedTabPosition() == 1){
                    replaceFragment(ExperienceFragment.newInstance(), false, R.id.framlayout);
                }else {
                    replaceFragment(BasicInfoFragment.newInstance(), false, R.id.framlayout);
                }
                getSupportFragmentManager().popBackStack();
            } else {
                if (!doubleBackToExitPressedOnce) {
                    this.doubleBackToExitPressedOnce = true;
                    MyCustomMessage.getInstance(this).snackbar(mainlayout, getResources().getString(R.string.for_exit));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, Constant.BackPressed_Exit);
                } else {
                    super.onBackPressed();
                }
            }

        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        switch (tablayout.getSelectedTabPosition()){
            case 0:
                isBasicInfo=true;
                infoFragment=BasicInfoFragment.newInstance();
                try {
                    if (experienceFragment != null){
                        experienceFragment.updateExperience(JobProfileActivity.this, true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                replaceFragment(infoFragment, false, R.id.framlayout);
                setTab(tab,true);
                break;
            case 1:
                isBasicInfo=false;
                experienceFragment=ExperienceFragment.newInstance();
                validation(tab);
                break;
            case 2:
                if (Uconnekt.session.getUserInfo().isProfile.equals("1")){
                    replaceFragment(ResumeFragment.newInstance(), false, R.id.framlayout);
                    setTab(tab,true);
                    if (isBasicInfo){
                        validation(tab);
                    }else if (experienceFragment != null){
                        experienceFragment.updateExperience(JobProfileActivity.this, false);
                    }
                }else {
                    tab = tablayout.getTabAt(0);
                    if (tab != null) {tab.select();}
                    MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.first));
                }
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tablayout.getSelectedTabPosition()){
            case 0:
                setTab(tab,false);
                break;
            case 1:
                setTab(tab,false);
                break;
            case 2:
                setTab(tab,false);
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    private void validation(TabLayout.Tab tab){
        if (infoFragment!=null) {
            if (infoFragment.specialtyId.isEmpty()) {
                MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.first));
                tab = tablayout.getTabAt(0);
                if (tab != null) {tab.select();}
            }else if(infoFragment.value.isEmpty()) {
                MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.first));
                tab = tablayout.getTabAt(0);
                if (tab != null) {tab.select();}
            }else if(infoFragment.strengthID.isEmpty()) {
                MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.first));
                tab = tablayout.getTabAt(0);
                if (tab != null) {tab.select();}
            }else if(infoFragment.tv_for_address.getText().toString().trim().isEmpty()) {
                MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.first));
                tab = tablayout.getTabAt(0);
                if (tab != null) {tab.select();}
            }else{
                infoFragment.spinnerHide();
                infoFragment.checkSpinnCheck();
                infoFragment.basicInfoPresenter.validationCondition(infoFragment.specialtyId, infoFragment.value, infoFragment.strengthID, infoFragment.tv_for_address.getText().toString().trim());
            }
        }
    }


    /*private void getlist() {

        new VolleyGetPost(this, AllAPIs.EMPLOYER_PROFILE, false, "list",false) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("job_title");
                        JobTitle jobTitle = new JobTitle();
                        jobTitle.jobTitleId = "";
                        jobTitle.jobTitleName = "";
                        arrayList.add(jobTitle);
                        arrayList2.add(jobTitle);
                        arrayList3.add(jobTitle);
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("jobTitleId");
                            jobTitles.jobTitleName = object.getString("jobTitleName");
                            arrayList.add(jobTitles);
                            arrayList3.add(jobTitles);
                        }
                        customSpAdapter.notifyDataSetChanged();
                        customSpAdapter2.notifyDataSetChanged();

                        JSONArray speciality = result.getJSONArray("speciality_list");
                        for (int i = 0; i < speciality.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = speciality.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("specializationId");
                            jobTitles.jobTitleName = object.getString("specializationName");
                            arrayList2.add(jobTitles);
                        }
                        specialitySpAdapter.notifyDataSetChanged();

                        Weeks week0 = new Weeks();
                        week0.week = "";
                        weekList.add(week0);
                        JSONObject week = result.getJSONObject("availability_list");
                        Weeks week1 = new Weeks();
                        week1.week = week.getString("1-4");
                        weekList.add(week1);
                        Weeks week2 = new Weeks();
                        week2.week = week.getString("5-8");
                        weekList.add(week2);
                        Weeks week3 = new Weeks();
                        week3.week = week.getString("9-12");
                        weekList.add(week3);
                        Weeks week4 = new Weeks();
                        week4.week = week.getString("12+");
                        weekList.add(week4);
                        weekSpAdapter.notifyDataSetChanged();

                        showPrefilledData();
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
    }*/
}
