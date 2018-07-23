package com.uconnekt.ui.individual.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.ReviewListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.ReviewList;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.util.Constant;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class IndiProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private String rating = "",userId = "",profileImage = "",fullName = "",jobTitleName = "",specializationName = "",address = "",company_logo = "",businessName = "";
    private TextView tv_for_bio,tv_for_favofite,tv_for_review,tv_for_aofs,tv_for_address,tv_for_recomend,tv_for_noReview,tv_for_specializationName,iv_for_fullName,tv_for_businessName;
    private RatingBar ratingBar;
    private ImageView iv_for_favourite,iv_for_recommend,iv_profile_image,iv_company_logo;
    private int favourite_count = 0,recommend_count = 0;
    public ArrayList<ReviewList> list = new ArrayList<>();
    private ReviewListAdapter reviewListAdapter;
    private RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("UserId");

        initView();
        apiCalling();
        reviewListAdapter = new ReviewListAdapter(this,list,userId);
        recycler_view.setAdapter(reviewListAdapter);
        recycler_view.setHasFixedSize(true);
        recycler_view.setNestedScrollingEnabled(false);

        iv_for_favourite.setOnClickListener(this);
        iv_for_recommend.setOnClickListener(this);
        tv_for_review.setOnClickListener(this);
    }


    private void initView() {
        findViewById(R.id.iv_for_chat).setOnClickListener(this);
        findViewById(R.id.iv_for_share).setOnClickListener(this);
        findViewById(R.id.layout_for_rate).setOnClickListener(this);
        findViewById(R.id.iv_for_backIco).setOnClickListener(this);
        findViewById(R.id.iv_for_backIco).setVisibility(View.VISIBLE);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        iv_company_logo = findViewById(R.id.iv_company_logo);
        iv_for_fullName = findViewById(R.id.iv_for_fullName);
        tv_for_businessName = findViewById(R.id.tv_for_businessName);
        tv_for_specializationName = findViewById(R.id.tv_for_specializationName);
        tv_for_address = findViewById(R.id.tv_for_address);
        tv_for_aofs = findViewById(R.id.tv_for_aofs);
        tv_for_bio = findViewById(R.id.tv_for_bio);
        tv_for_favofite = findViewById(R.id.tv_for_favofite);
        tv_for_review = findViewById(R.id.tv_for_review);
        tv_for_recomend = findViewById(R.id.tv_for_recomend);
        ratingBar = findViewById(R.id.ratingBar);
        iv_for_favourite = findViewById(R.id.iv_for_favourite);
        iv_for_recommend = findViewById(R.id.iv_for_recommend);
        recycler_view = findViewById(R.id.recycler_view);
        tv_for_noReview = findViewById(R.id.tv_for_noReview);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(R.string.profile);
    }

    private void setData(){
        Picasso.with(this).load(profileImage).into(iv_profile_image);
        Picasso.with(this).load(company_logo).into(iv_company_logo);
        iv_for_fullName.setText(fullName.isEmpty()?"NA":fullName);
        tv_for_businessName.setText(businessName.isEmpty()?"NA":businessName);
        tv_for_specializationName.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
                        company_logo = object.getString("company_logo");
                        profileImage = object.getString("profileImage");
                        fullName = object.getString("fullName");
                        jobTitleName = object.getString("jobTitleName");
                        businessName = object.getString("businessName");
                        specializationName = object.getString("specializationName");
                        address = object.getString("address");
                        // String bio = object.getString("bio");
                        String bio = URLDecoder.decode(object.getString("bio"), "UTF-8");
                        rating = object.getString("rating");
                        String review_count = jsonObject.getString("review_count");
                        String favourite = jsonObject.getString("favourite_count");
                        if (!favourite.isEmpty())favourite_count = Integer.parseInt(favourite);
                        String recommend = jsonObject.getString("recommend_count");
                        if (!favourite.isEmpty())recommend_count = Integer.parseInt(recommend);
                        String is_favourite = jsonObject.getString("is_favourite");
                        iv_for_favourite.setImageResource(is_favourite.equals("1")?R.drawable.ic_love:R.drawable.ic_like_blank);
                        String is_recommend = jsonObject.getString("is_recommend");
                        iv_for_recommend.setImageResource(is_recommend.equals("1")?R.drawable.ic_thumbs:R.drawable.ic_like);
                        JSONArray array2 = jsonObject.getJSONArray("review_list");
                        list.clear();
                        for (int i = 0; i < array2.length();i++){
                            JSONObject jsonObject1 = array2.getJSONObject(i);
                            ReviewList reviewList = new Gson().fromJson(jsonObject1.toString(),ReviewList.class);
                            list.add(reviewList);
                        }
                        reviewListAdapter.notifyDataSetChanged();
                        tv_for_noReview.setVisibility(list.size()==0?View.VISIBLE:View.GONE);

                        setData();
                        setApiData(bio, rating, review_count);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_for_noReview.setVisibility(View.VISIBLE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(IndiProfileActivity.this, NetworkActivity.class));

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_chat:
                MyCustomMessage.getInstance(this).customToast(getString(R.string.under_development_mode));
                break;
            case R.id.iv_for_share:
                deletelDailog();
                break;
            case R.id.layout_for_rate:
                Intent intent = new Intent(this,RatingActivity.class);
                intent.putExtra("USERID",userId);
                startActivity(intent);
                break;
            case R.id.iv_for_favourite:
                favourite();
                break;
            case R.id.iv_for_recommend:
                recommend();
                break;
            case R.id.tv_for_review:
                intent = new Intent(this,ReviewActivity.class);
                intent.putExtra("USERID",userId);
                startActivity(intent);
                break;
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
        }
    }

    private void setApiData(String bio, String rating, String review_count) {
        tv_for_bio.setText(bio.isEmpty()?"NA":bio);
        tv_for_favofite.setText(favourite_count==0?"0 Favourite":favourite_count+ " Favourite");
        tv_for_review.setText(review_count.isEmpty()?"0 Reviews":review_count+ " Reviews");
        tv_for_recomend.setText(recommend_count==0?"0 Recommend":recommend_count+ " Recommend");
        ratingBar.setRating(rating.isEmpty()?0:Float.parseFloat(rating));
    }

    @Override
    public void onResume() {
        if (Constant.NETWORK_CHECK == 1){
            apiCalling();
        }else if (Constant.API == 1){
            Constant.API =0;
            apiCalling();
        }
        super.onResume();
    }

    private void favourite(){
        new VolleyGetPost(this, AllAPIs.FACOURITES, true, "Favourites", true) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        String isFavourite = jsonObject.getString("isFavourite");

                        iv_for_favourite.setImageResource(isFavourite.equals("1")?R.drawable.ic_love:R.drawable.ic_like_blank);
                        //tv_for_favofite.setText(isFavourite.equals("1")?(favourite_count+1)+ " Favourite":(favourite_count!=0)?(favourite_count-1)+ " Favourite":(favourite_count)+ " Favourite");

                        if (isFavourite.equals("1")){
                            favourite_count = favourite_count + 1;
                            tv_for_favofite.setText(favourite_count+ " Favourite");
                        }else {
                            favourite_count = favourite_count - 1;
                            tv_for_favofite.setText(favourite_count+ " Favourite");
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
                        //tv_for_recomend.setText(isRecommend.equals("1")?(recommend_count+1)+ " Recommend":(recommend_count!=0)?(recommend_count-1)+ " Recommend":(recommend_count)+ " Recommend");
                        if (isRecommend.equals("1")){
                            recommend_count = recommend_count + 1;
                            tv_for_recomend.setText(recommend_count+ " Recommend");
                        }else {
                            recommend_count = recommend_count - 1;
                            tv_for_recomend.setText(recommend_count+ " Recommend");
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


    private void deletelDailog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_emp_profile);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        CardView card_for_close = dialog.findViewById(R.id.card_for_close);
        final LinearLayout layout_for_share = dialog.findViewById(R.id.layout_for_share);
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
                //MyCustomMessage.getInstance(activity).customToast(getString(R.string.under_development_mode));
                screenShot(layout_for_share);
            }
        });
        dialog.show();
    }

    private void dailogData(Dialog dialog){
        ImageView iv_profile_image = dialog.findViewById(R.id.iv_profile_image);
        TextView tv_for_fullName = dialog.findViewById(R.id.tv_for_fullName);
        TextView tv_for_businessName = dialog.findViewById(R.id.tv_for_businessName);
        TextView tv_for_aofs = dialog.findViewById(R.id.tv_for_aofs);
        TextView tv_for_address = dialog.findViewById(R.id.tv_for_address);
        TextView tv_for_rating = dialog.findViewById(R.id.tv_for_rating);
        TextView tv_for_favourite = dialog.findViewById(R.id.tv_for_favourite_count);
        TextView tv_for_recomended = dialog.findViewById(R.id.tv_for_recomend);
        TextView tv_for_bios = dialog.findViewById(R.id.tv_for_bio);
        TextView tv_for_reviews = dialog.findViewById(R.id.tv_for_review);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);

        Picasso.with(this).load(profileImage).into(iv_profile_image);
        tv_for_fullName.setText(fullName.isEmpty()?"NA":fullName);
        tv_for_businessName.setText(businessName.isEmpty()?"NA":businessName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
        ratingBar.setRating(rating.isEmpty()?0:Float.parseFloat(rating));
        tv_for_rating.setText(rating.isEmpty()?"0":rating);
        tv_for_favourite.setText(String.valueOf(favourite_count));
        tv_for_reviews.setText(tv_for_review.getText().toString());
        tv_for_recomended.setText(String.valueOf(recommend_count));
        tv_for_bios.setText(tv_for_bio.getText().toString());

    }

    /*.................................screenShot()...................................*/
    private void screenShot(LinearLayout scr_shot_view) {
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
            sharOnsocial(imageFile,"Testing");
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