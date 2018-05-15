package com.uconnekt.ui.individual.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.fragment.ChatFragment;
import com.uconnekt.ui.fragment.MapFragment;
import com.uconnekt.ui.fragment.ProfileFragment;
import com.uconnekt.ui.fragment.SearchFragment;
import com.uconnekt.ui.fragment.SettingFragment;
import com.uconnekt.ui.individual.fragment.IndiSearchFragment;
import com.uconnekt.util.Constant;

public class JobHomeActivity extends BaseActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private RelativeLayout mainlayout;
    private TabLayout tabs;
    private TextView tv_for_tittle;
    private int click = 0;
    private ImageView iv_for_backIco,iv_for_filter,iv_for_circular_arrow,iv_for_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_home);
        initView();

        setTab(tabs.getTabAt(0),R.drawable.ic_search_yellow,true);
        tabs.getChildAt(0).setScaleY(-1);
        tv_for_tittle.setText(R.string.search);
        for(int i = 0 ;i<tabs.getTabCount();i++){
            tabs.getTabAt(i).getCustomView().findViewById(R.id.mainlayout_tab).setScaleY(-1);
        }

        addFragment(IndiSearchFragment.newInstance(), false, R.id.framlayout);
    }



    private void initView(){
        mainlayout = findViewById(R.id.mainlayout);
        tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(this);
        tv_for_tittle = findViewById(R.id.tv_for_tittle);
        iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_filter = findViewById(R.id.iv_for_filter);
        iv_for_circular_arrow = findViewById(R.id.iv_for_circular_arrow);
        iv_for_menu = findViewById(R.id.iv_for_menu);
    }


    private void setTab(TabLayout.Tab tab, int imageResource , boolean isSelected){
        ( (ImageView)tab.getCustomView().findViewById(android.R.id.icon)).setImageResource(imageResource);
        ((TextView) tab.getCustomView().findViewById(android.R.id.text1)).setTextColor(getResources().getColor(isSelected?R.color.yellow:R.color.darkgray));

    }


    @Override
    public void onBackPressed() {
        if (Uconnekt.session.isLoggedIn()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
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
    public void onClick(View v) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tabs.getSelectedTabPosition() != click) {
            switch (tabs.getSelectedTabPosition()) {
                case 0:
                    click = 0;
                    replaceFragment(IndiSearchFragment.newInstance(), false, R.id.framlayout);
                    setTab(tab, R.drawable.ic_search_yellow, true);
                    tv_for_tittle.setText(R.string.search);
                    break;
                case 1:
                    click = 1;
                    replaceFragment(MapFragment.newInstance(), false, R.id.framlayout);
                    setTab(tab, R.drawable.ic_map_yellow, true);
                    tv_for_tittle.setText(R.string.map);
                    break;
                case 2:
                    click = 2;
                    replaceFragment(ChatFragment.newInstance(), false, R.id.framlayout);
                    setTab(tab, R.drawable.ic_chat_yellow, true);
                    tv_for_tittle.setText(R.string.messages);
                    break;
                case 3:
                    click = 3;
                    replaceFragment(ProfileFragment.newInstance(), false, R.id.framlayout);
                    setTab(tab, R.drawable.ic_user_yellow, true);
                    tv_for_tittle.setText(R.string.profile);
                    break;
                case 4:
                    click = 4;
                    replaceFragment(SettingFragment.newInstance(), false, R.id.framlayout);
                    setTab(tab, R.drawable.ic_settings_yellow, true);
                    tv_for_tittle.setText(R.string.setting);
                    break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tabs.getSelectedTabPosition()){
            case 0:
                setTab(tab,R.drawable.ic_search,false);
                toolbarGone();
                break;
            case 1:
                setTab(tab,R.drawable.ic_map_placeholder,false);
                toolbarGone();
                break;
            case 2:
                setTab(tab,R.drawable.ic_chat,false);
                toolbarGone();
                break;
            case 3:
                setTab(tab,R.drawable.ic_user,false);
                toolbarGone();
                break;
            case 4:
                setTab(tab,R.drawable.ic_settings,false);
                toolbarGone();
                break;
        }

    }

    private void toolbarGone(){
        iv_for_backIco.setVisibility(View.GONE);
        iv_for_filter.setVisibility(View.GONE);
        iv_for_circular_arrow.setVisibility(View.GONE);
        iv_for_menu.setVisibility(View.GONE);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
