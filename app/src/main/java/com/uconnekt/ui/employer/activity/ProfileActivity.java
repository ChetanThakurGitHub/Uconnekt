package com.uconnekt.ui.employer.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.activity.experience.ExpActivity;
import com.uconnekt.ui.employer.fragment.ProfileFragment;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.activity.FavouriteActivity;
import com.uconnekt.ui.individual.activity.RecommendedActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private String userId = "",profileImage = "",fullName = "",jobTitleName = "",specializationName = "",address = "";
    private ImageView iv_for_favourite,iv_for_recommend,iv_profile_image,profile;
    private int favourite_count = 0,recommend_count = 0;
    private TextView tv_for_bio,tv_for_review_count,tv_for_favourite_count,tv_for_recomend,
            tv_for_aofs,tv_for_address,tv_for_company,tv_for_fullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("UserId");

        initView();
        view();

        iv_for_favourite.setOnClickListener(this);
        iv_for_recommend.setOnClickListener(this);
        
    }

    private void initView(){
        profile = findViewById(R.id.profile);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        tv_for_fullName = findViewById(R.id.tv_for_fullName);
        tv_for_company = findViewById(R.id.tv_for_company);
        tv_for_address = findViewById(R.id.tv_for_address);
        tv_for_aofs = findViewById(R.id.tv_for_aofs);
        iv_for_favourite = findViewById(R.id.iv_for_favourite);
        iv_for_recommend = findViewById(R.id.iv_for_recommend);
        tv_for_bio = findViewById(R.id.tv_for_bio);
        tv_for_review_count = findViewById(R.id.tv_for_review_count);
        tv_for_favourite_count = findViewById(R.id.tv_for_favourite_count);
        tv_for_recomend = findViewById(R.id.tv_for_recomend);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(R.string.profile);
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        ImageView iv_for_share = findViewById(R.id.iv_for_share);
        iv_for_share.setVisibility(View.VISIBLE);iv_for_share.setOnClickListener(this);
        findViewById(R.id.layout_for_basicInfo).setOnClickListener(this);
        findViewById(R.id.layout_for_Experience).setOnClickListener(this);
        findViewById(R.id.layout_for_Resume).setOnClickListener(this);
        findViewById(R.id.card_for_chat).setOnClickListener(this);
    }

    private void setData(String profileImage, String fullName, String jobTitleName, String specializationName, String address) {
        Picasso.with(this).load(profileImage).into(profile);
        Picasso.with(this).load(profileImage).into(iv_profile_image);
        tv_for_fullName.setText(fullName.isEmpty()?"NA":fullName);
        tv_for_company.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
    }

    private void apiCalling(){
        new VolleyGetPost(this, AllAPIs.PROFILE+"user_id="+userId,false,"Profile",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONArray array = jsonObject.getJSONArray("profile");
                        JSONObject object = array.getJSONObject(0);
                        // String bio = object.getString("bio");
                        String bio = URLDecoder.decode(object.getString("bio"), "UTF-8");
                        // String company_logo = object.getString("company_logo");
                        profileImage = object.getString("profileImage");
                        fullName = object.getString("fullName");
                        jobTitleName = object.getString("jobTitleName");
                        specializationName = object.getString("specializationName");
                        address = object.getString("address");
                        tv_for_bio.setText(bio.isEmpty()?"NA":bio);
                        String view_count = jsonObject.getString("view_count");
                        tv_for_review_count.setText(view_count.isEmpty()?"0":view_count);
                        String favourite = jsonObject.getString("favourite_count");
                        if (!favourite.isEmpty())favourite_count = Integer.parseInt(favourite);
                        String recommend = jsonObject.getString("recommend_count");
                        if (!favourite.isEmpty())recommend_count = Integer.parseInt(recommend);
                        String is_favourite = jsonObject.getString("is_favourite");
                        iv_for_favourite.setImageResource(is_favourite.equals("1")?R.drawable.ic_love:R.drawable.ic_like_blank);
                        String is_recommend = jsonObject.getString("is_recommend");
                        iv_for_recommend.setImageResource(is_recommend.equals("1")?R.drawable.ic_thumbs:R.drawable.ic_like);
                        tv_for_favourite_count.setText(favourite_count==0?"0":favourite_count+"");
                        tv_for_recomend.setText(recommend_count==0?"0":recommend_count+"");
                        setData(profileImage,fullName,jobTitleName,specializationName,address);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(ProfileActivity.this, NetworkActivity.class));

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

    private void favourite(){
        new VolleyGetPost(this, AllAPIs.FACOURITES, true, "Favourite", true) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        String isFavourite = jsonObject.getString("isFavourite");

                        iv_for_favourite.setImageResource(isFavourite.equals("1")?R.drawable.ic_love:R.drawable.ic_like_blank);

                        if (isFavourite.equals("1")){
                            favourite_count = favourite_count + 1;
                            tv_for_favourite_count.setText(favourite_count+"");
                        }else {
                            favourite_count = favourite_count - 1;
                            tv_for_favourite_count.setText(favourite_count+"");
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
                params.put("favourite_for",userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void recommend(){
        new VolleyGetPost(this, AllAPIs.RECOMMEND, true, "Recommend", true) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        String isRecommend = jsonObject.getString("isRecommend");
                        iv_for_recommend.setImageResource(isRecommend.equals("1")?R.drawable.ic_thumbs:R.drawable.ic_like);

                        if (isRecommend.equals("1")){
                            recommend_count = recommend_count + 1;
                            tv_for_recomend.setText(recommend_count+"");
                        }else {
                            recommend_count = recommend_count - 1;
                            tv_for_recomend.setText(recommend_count+"");
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
                params.put("recommend_for",userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void view(){
        new VolleyGetPost(this, AllAPIs.VIEW, true, "Recommend", false) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        apiCalling();
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
                params.put("view_for",userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_favourite:
                favourite();
                break;
            case R.id.iv_for_recommend:
                recommend();
                break;
            case R.id.layout_for_basicInfo:
                Intent intent = new Intent(this,BasicInfoActivity.class);
                intent.putExtra("USERID",userId);
                startActivity(intent);
                break;
            case R.id.layout_for_Experience:
                intent = new Intent(this,ExpActivity.class);
                intent.putExtra("USERID",userId);
                startActivity(intent);
                break;
            case R.id.layout_for_Resume:
                intent = new Intent(this,ResumeActivity.class);
                intent.putExtra("USERID",userId);
                startActivity(intent);
                break;
            case R.id.card_for_chat:
                MyCustomMessage.getInstance(this).customToast(getString(R.string.under_development_mode));
                break;
            case R.id.iv_for_share:
                deletelDailog();
                break;
            case R.id.iv_for_backIco:
                finish();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void deletelDailog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_indi_profile);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        final LinearLayout layout_for_share = dialog.findViewById(R.id.layout_for_share);
        CardView card_for_close = dialog.findViewById(R.id.card_for_close);
        CardView card_for_share = dialog.findViewById(R.id.card_for_share);
        dailogData(dialog);

        card_for_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        card_for_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenShot(layout_for_share,"Testing");
            }
        });
        dialog.show();
    }

    private void dailogData(Dialog dialog){
        ImageView iv_profile_image = dialog.findViewById(R.id.iv_profile_image);
        TextView tv_for_fullName = dialog.findViewById(R.id.tv_for_fullName);
        TextView tv_for_company = dialog.findViewById(R.id.tv_for_company);
        TextView tv_for_aofs = dialog.findViewById(R.id.tv_for_aofs);
        TextView tv_for_address = dialog.findViewById(R.id.tv_for_address);
        TextView tv_for_review = dialog.findViewById(R.id.tv_for_review_count);
        TextView tv_for_favourite = dialog.findViewById(R.id.tv_for_favourite_count);
        TextView tv_for_recomended = dialog.findViewById(R.id.tv_for_recomend);
        TextView tv_for_bios = dialog.findViewById(R.id.tv_for_bio);

        Picasso.with(this).load(profileImage).into(iv_profile_image);
        tv_for_fullName.setText(fullName.isEmpty()?"NA":fullName);
        tv_for_company.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
        tv_for_review.setText(tv_for_review_count.getText().toString());
        tv_for_favourite.setText(tv_for_favourite_count.getText().toString());
        tv_for_recomended.setText(tv_for_recomend.getText().toString());
        tv_for_bios.setText(tv_for_bio.getText().toString());
    }

    /*.................................screenShot()...................................*/
    private void screenShot(LinearLayout scr_shot_view, String text) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".png";
            scr_shot_view.setDrawingCacheEnabled(true);
            scr_shot_view.buildDrawingCache(true);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap bitmap = Bitmap.createBitmap(scr_shot_view.getDrawingCache());
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
            scr_shot_view.destroyDrawingCache();
            sharOnsocial(imageFile,text);
            //onShareClick(imageFile,text);
            //doShareLink(text,otherProfileInfo.UserDetail.profileUrl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sharOnsocial(File imageFile, String text) {
        Uri uri;
        Intent sharIntent = new Intent(Intent.ACTION_SEND);
        String ext = imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(ext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider",imageFile);
            sharIntent.setDataAndType(uri, type);
        } else {
            uri = Uri.fromFile(imageFile);
            sharIntent.setDataAndType(uri, type);
        }

        sharIntent.setType("image/png");
        //sharIntent.setType("text/plain");
        sharIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharIntent.putExtra(Intent.EXTRA_SUBJECT, "Uconnekt");
        sharIntent.putExtra(Intent.EXTRA_TEXT, text+"\n"+"https://play.google.com/store");
        startActivity(Intent.createChooser(sharIntent, "Share:"));

    }
}