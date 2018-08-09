package com.uconnekt.ui.common_activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.ui.common_activity.fragment.FavouriteByMeFragment;
import com.uconnekt.ui.common_activity.fragment.FavouriteByOtherFragment;

import java.util.ArrayList;
import java.util.List;

public class BothFavouriteActivity extends AppCompatActivity implements View.OnClickListener{

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_both_favourite);
        initView();
    }

    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.favorites);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager, true);
        setCustomFont();
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


    private void setupViewPager(ViewPager viewPager) {
        BothFavouriteActivity.ViewPagerAdapter adapter = new BothFavouriteActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FavouriteByOtherFragment(), getString(R.string.favorites));
        adapter.addFragment(new FavouriteByMeFragment(), getString(R.string.my_favorite));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setCustomFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    Typeface typeface = ResourcesCompat.getFont(this, R.font.montserrat_medium);

                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }

}
