package com.uconnekt.ui.individual.edit_profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.BuildConfig;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.cropper.CropImage;
import com.uconnekt.cropper.CropImageView;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.individual.edit_profile.fragment.EditBasicInfoFragment;
import com.uconnekt.ui.individual.edit_profile.fragment.EditExpFragment;
import com.uconnekt.ui.individual.edit_profile.fragment.EditResumeFragment;
import com.uconnekt.util.Constant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class IndiEditProfileActivity extends BaseActivity implements View.OnClickListener, EditListener, TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private RelativeLayout mainlayout;
    private LinearLayout tabStrip;
    public String check = "";
    private BottomSheetDialog dialog;
    public Bitmap profileImageBitmap;
    private ImageView iv_for_profile,iv_for_camera;
    private Uri imageUri;
    public boolean Isuserfilldata = false;

    @Override
    public void onSwitchFragment(int pos) {
        tabLayout.getTabAt(pos).select();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indi_edit_profile);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) check = bundle.getString("FROM");

        initView();

        tabLayout.addOnTabSelectedListener(this);

        if (!check.equals("Edit")) {
            tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        MyCustomMessage.getInstance(IndiEditProfileActivity.this).snackbar(mainlayout, "Please press next button for upload basic info");
                        return true;
                    }
                });
            }
        }
    }

    private void manageClick(){
        hideKeyboard();
        Fragment fragment = adapter.getItem(tabLayout.getSelectedTabPosition());
        if(fragment instanceof EditBasicInfoFragment){
            EditBasicInfoFragment tmp = (EditBasicInfoFragment) fragment;
            tmp.onSubmit();
        }else  if(fragment instanceof EditExpFragment){
            EditExpFragment tmp = (EditExpFragment) fragment;
            Constant.NETWORK_CHECK = 1;
            tmp.onSubmit();
        }else if(fragment instanceof EditResumeFragment){
            EditResumeFragment tmp = (EditResumeFragment) fragment;
            tmp.onSubmit();
        }
    }

    private void initView() {
        mainlayout =findViewById(R.id.mainlayout);
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_profile = findViewById(R.id.iv_for_profile);
        iv_for_camera = findViewById(R.id.iv_for_camera);
        TextView tv_for_fullName = findViewById(R.id.tv_for_fullName);
        iv_for_backIco.setVisibility(check.equals("Edit")?View.VISIBLE:View.GONE);
        iv_for_camera.setVisibility(check.equals("Edit")?View.VISIBLE:View.GONE);
        iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(check.equals("Edit")?R.string.edit_profile:R.string.profile);

        String image = Uconnekt.session.getUserInfo().profileImage;
        if (image != null && !image.equals("")) Picasso.with(this).load(image).into(iv_for_profile);
        tv_for_fullName.setText(Uconnekt.session.getUserInfo().fullName);

        final CustomViewPager viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        setCustomFont();

        if (check.equals("Edit"))iv_for_profile.setOnClickListener(this);

        Button btn_for_next = findViewById(R.id.btn_for_next);
        if (check.equals("Edit"))btn_for_next.setText(R.string.save);
        findViewById(R.id.btn_for_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageClick();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.reMeasureCurrentPage(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void clickable(){
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.iv_for_profile:
                if (tabLayout.getSelectedTabPosition() == 0) {
                    PermissionAll permissionAll = new PermissionAll();
                    if (permissionAll.RequestMultiplePermission1(IndiEditProfileActivity.this))
                        showBottomSheetDialog();
                }
                break;
            case R.id.layout_for_camera:
                try {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file= new File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg");

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        imageUri= FileProvider.getUriForFile(IndiEditProfileActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider",file);
                    }else {
                        imageUri= Uri.fromFile(file);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    //  intent.putExtra("android.intent.extras.CAMERA_FACING", 1); //for front camera
                    startActivityForResult(intent, Constant.CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.layout_for_gallery:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.GALLERY);
                if (dialog!=null)dialog.dismiss();
                break;
            case R.id.btn_for_close:
                if (dialog!=null)dialog.dismiss();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            dialog.dismiss();
            if (imageUri != null) {
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(160, 160).setMaxCropResultSize(4000, 4000).setAspectRatio(400, 400).start(IndiEditProfileActivity.this);
            } else {
                MyCustomMessage.getInstance(this).customToast(getString(R.string.something_wrong));
            }}else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            try {
                if (result != null) {
                    profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    iv_for_profile.setImageBitmap(profileImageBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            if (requestCode == Constant.CAMERA && resultCode == RESULT_OK) {
                dialog.dismiss();
                if (imageUri!=null){
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(IndiEditProfileActivity.this);
                }else{
                    MyCustomMessage.getInstance(this).customToast(getString(R.string.something_wrong));
                }
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result= CropImage.getActivityResult(data);
                try {
                    if (result != null) {
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                        iv_for_profile.setImageBitmap(profileImageBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void showBottomSheetDialog() {
        dialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        dialog.setContentView(view);
        dialog.findViewById(R.id.layout_for_camera).setOnClickListener(this);
        dialog.findViewById(R.id.layout_for_gallery).setOnClickListener(this);
        dialog.findViewById(R.id.btn_for_close).setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EditBasicInfoFragment(), getString(R.string.basic_info));
        adapter.addFragment(new EditExpFragment(), getString(R.string.experience));
        adapter.addFragment(new EditResumeFragment(), getString(R.string.resume));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:
                iv_for_camera.setVisibility(View.VISIBLE);
                break;

            case 1:
                iv_for_camera.setVisibility(View.GONE);
                break;

            case 2:
                iv_for_camera.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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

                    Typeface typeface = ResourcesCompat.getFont(this, R.font.montserrat_medium);

                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }
}
