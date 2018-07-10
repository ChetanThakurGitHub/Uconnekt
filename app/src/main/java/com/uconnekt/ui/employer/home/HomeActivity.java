package com.uconnekt.ui.employer.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.employer.activity.EditProfileActivity;
import com.uconnekt.ui.employer.fragment.ChatFragment;
import com.uconnekt.ui.employer.fragment.FilterFragment;
import com.uconnekt.ui.employer.fragment.MapFragment;
import com.uconnekt.ui.employer.fragment.MyProfileFragment;
import com.uconnekt.ui.employer.fragment.ProfileFragment;
import com.uconnekt.ui.employer.fragment.SearchFragment;
import com.uconnekt.ui.employer.fragment.SettingFragment;
import com.uconnekt.ui.employer.fragment.ViewProifileFragment;
import com.uconnekt.ui.individual.activity.ReviewActivity;
import com.uconnekt.ui.individual.fragment.FavouriteFragment;
import com.uconnekt.ui.individual.fragment.IndiMapFragment;
import com.uconnekt.util.Constant;

import java.text.ParseException;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class HomeActivity extends BaseActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private RelativeLayout mainlayout;
    private TabLayout tabs;
    private TextView tv_for_tittle;
    private int click = 0;
    private ImageView iv_for_backIco,iv_for_filter,iv_for_circular_arrow,iv_for_menu,iv_for_share,iv_for_edit,iv_for_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        setTab(tabs.getTabAt(0),R.drawable.ic_search_yellow,true);
        tabs.getChildAt(0).setScaleY(-1);
        tv_for_tittle.setText(R.string.search);
        setToolbarIcon(0);
        for(int i = 0 ;i<tabs.getTabCount();i++){
            tabs.getTabAt(i).getCustomView().findViewById(R.id.mainlayout_tab).setScaleY(-1);
        }

        replaceFragment(new SearchFragment());
        iv_for_filter.setOnClickListener(this);
        iv_for_backIco.setOnClickListener(this);
        iv_for_share.setOnClickListener(this);
        iv_for_edit.setOnClickListener(this);
        iv_for_view.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String userType = extras.getString("type");
            notificationManage(userType);
        }
    }

    private void notificationManage( String userType){
        tabs.getTabAt(3).select();
        replaceFragment(MyProfileFragment.newInstance(userType));
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
        iv_for_share = findViewById(R.id.iv_for_share);
        iv_for_edit = findViewById(R.id.iv_for_edit);
        iv_for_view = findViewById(R.id.iv_for_view);
    }

    private void setTab(TabLayout.Tab tab, int imageResource , boolean isSelected){
        ((ImageView)tab.getCustomView().findViewById(android.R.id.icon)).setImageResource(imageResource);
        ((TextView) tab.getCustomView().findViewById(android.R.id.text1)).setTextColor(getResources().getColor(isSelected?R.color.yellow:R.color.darkgray));
    }



    public void setToolbarIcon(int visi){
        switch (visi) {
            case 0:
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_filter.setVisibility(View.VISIBLE);
                iv_for_circular_arrow.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_view.setVisibility(View.GONE);
                break;
            case 1:
                iv_for_backIco.setVisibility(View.VISIBLE);
                iv_for_filter.setVisibility(View.GONE);
                iv_for_circular_arrow.setVisibility(View.VISIBLE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_view.setVisibility(View.GONE);
                break;
            case 2:
                tv_for_tittle.setText(R.string.profile);
                iv_for_backIco.setVisibility(View.VISIBLE);
                iv_for_filter.setVisibility(View.GONE);
                iv_for_circular_arrow.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_view.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.VISIBLE);
                break;
            case 3:
                tv_for_tittle.setText(R.string.my_profile);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_filter.setVisibility(View.GONE);
                iv_for_circular_arrow.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.VISIBLE);
                iv_for_view.setVisibility(View.VISIBLE);
                break;
            case 4:
                tv_for_tittle.setText(R.string.my_profile);
                iv_for_backIco.setVisibility(View.VISIBLE);
                iv_for_filter.setVisibility(View.GONE);
                iv_for_circular_arrow.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_view.setVisibility(View.GONE);
                break;
            case 5:
                tv_for_tittle.setText(R.string.setting);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_filter.setVisibility(View.GONE);
                iv_for_circular_arrow.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_view.setVisibility(View.GONE);
                break;
            case 6:
                tv_for_tittle.setText(R.string.chat);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_filter.setVisibility(View.GONE);
                iv_for_circular_arrow.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_view.setVisibility(View.GONE);
                break;
            case 7:
                tv_for_tittle.setText(R.string.favorites);
                iv_for_backIco.setVisibility(View.VISIBLE);
                iv_for_filter.setVisibility(View.GONE);
                iv_for_circular_arrow.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.GONE);
                iv_for_share.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_view.setVisibility(View.GONE);
                break;
        }
    }


   /* @Override
    public void onBackPressed() {
        if (Uconnekt.session.isLoggedIn()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                setToolbarIcon(0);
                //if (getCurrentFragment() instanceof SearchFragment) ((SearchFragment)getCurrentFragment()).getList();
                tv_for_tittle.setText(R.string.search);

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
*/

    @Override
    public void onBackPressed() {
        hideKeyboard();
        Handler handler = new Handler();
        Runnable runnable;

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
            setToolbarIcon(0);
            android.support.v4.app.Fragment fragment=getCurrentFragment();
            if (fragment!=null &&fragment instanceof IndiMapFragment)tv_for_tittle.setText(R.string.map);
            if (fragment!=null &&fragment instanceof SearchFragment) tv_for_tittle.setText(R.string.search);
            if (fragment!=null &&fragment instanceof MyProfileFragment)setToolbarIcon(3);
            if (fragment!=null &&fragment instanceof FavouriteFragment)setToolbarIcon(7);
            if (fragment!=null &&fragment instanceof ProfileFragment)setToolbarIcon(2);
        } else {
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
            if (doubleBackToExitPressedOnce) {
                handler.removeCallbacks(runnable);
                finish();
            } else {
                MyCustomMessage.getInstance(this).snackbar(mainlayout, getResources().getString(R.string.for_exit));
                doubleBackToExitPressedOnce = true;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_filter:
                tv_for_tittle.setText(R.string.filter);
                setToolbarIcon(1);
                FilterFragment fragment=new FilterFragment();
                if (getCurrentFragment() instanceof  SearchFragment)fragment.setFragment((SearchFragment) getCurrentFragment());
                if (getCurrentFragment() instanceof  MapFragment)fragment.setOtherFragment((MapFragment) getCurrentFragment());
                addFragment(fragment);
                break;
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.iv_for_edit:
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                break;
            case R.id.iv_for_view:
                setToolbarIcon(4);
                addFragment(new ViewProifileFragment());
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tabs.getSelectedTabPosition() != click) {
            switch (tabs.getSelectedTabPosition()) {
                case 0:
                    click = 0;
                    replaceFragment(new SearchFragment());
                    setTab(tab, R.drawable.ic_search_yellow, true);
                    tv_for_tittle.setText(R.string.search);
                    setToolbarIcon(0);
                    break;
                case 1:
                    click = 1;
                    replaceFragment(new MapFragment());
                    setTab(tab, R.drawable.ic_map_yellow, true);
                    setToolbarIcon(0);
                    tv_for_tittle.setText(R.string.map);
                    break;
                case 2:
                    click = 2;
                    replaceFragment(new ChatFragment());
                    setTab(tab, R.drawable.ic_chat_yellow, true);
                    setToolbarIcon(6);
                    break;
                case 3:
                    click = 3;
                    replaceFragment(new MyProfileFragment());
                    setTab(tab, R.drawable.ic_user_yellow, true);
                    setToolbarIcon(3);
                    break;
                case 4:
                    click = 4;
                    replaceFragment(new SettingFragment());
                    setTab(tab, R.drawable.ic_settings_yellow, true);
                    setToolbarIcon(5);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (getCurrentFragment() instanceof MapFragment)getCurrentFragment().onRequestPermissionsResult(requestCode,permissions,grantResults);
            }
            break;
        }
    }

}
