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
    private RelativeLayout mainlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_profile);
        initView();

        addFragment(BasicInfoFragment.newInstance(), false, R.id.framlayout);
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

    private void setTab(TabLayout.Tab tab, boolean isSelected){
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
                replaceFragment(BasicInfoFragment.newInstance(), false, R.id.framlayout);
                setTab(tab,true);
                break;
            case 1:
                replaceFragment(ExperienceFragment.newInstance(), false, R.id.framlayout);
                setTab(tab,true);
                break;
            case 2:
                replaceFragment(ResumeFragment.newInstance(), false, R.id.framlayout);
                setTab(tab,true);
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
}
