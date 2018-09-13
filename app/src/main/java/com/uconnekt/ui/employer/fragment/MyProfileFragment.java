package com.uconnekt.ui.employer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.ui.common_activity.BothFavouriteActivity;
import com.uconnekt.ui.common_activity.BothRecommendedActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.activity.FavouriteActivity;
import com.uconnekt.ui.individual.activity.RecommendedActivity;
import com.uconnekt.ui.individual.activity.ReviewActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private HomeActivity activity;
    private View view;
    private static final String ARG_PARAM1 = "param1";

    public static MyProfileFragment newInstance(String notificationId) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, notificationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            String userID =  getArguments().getString(ARG_PARAM1);
            switch (userID) {
                case "user_review": {
                    Intent intent = new Intent(activity, ReviewActivity.class);
                    intent.putExtra("USERID", Uconnekt.session.getUserInfo().userId);
                    activity.startActivity(intent);
                    break;
                }
                case "user_favourites": {
                    Intent intent = new Intent(activity, FavouriteActivity.class);
                    intent.putExtra("USERID", Uconnekt.session.getUserInfo().userId);
                    activity.startActivity(intent);
                    break;
                }
                case "user_recommends": {
                    Intent intent = new Intent(activity, RecommendedActivity.class);
                    intent.putExtra("USERID", Uconnekt.session.getUserInfo().userId);
                    activity.startActivity(intent);
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        view.findViewById(R.id.card_for_favourite).setOnClickListener(this);
        view.findViewById(R.id.card_for_recommend).setOnClickListener(this);
        view.findViewById(R.id.tv_for_view).setOnClickListener(this);
        getMyprofileData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (HomeActivity) context;
    }

    public void getMyprofileData(){
        new VolleyGetPost(activity, AllAPIs.MY_PROFILE, false, "MyProfile", true) {
            @Override
            public void onVolleyResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String favourite_count = object.getString("favourite_count");
                    String recommend_count = object.getString("recommend_count");
                    if (status.equals("success")){
                        JSONObject jsonObject = object.getJSONObject("business_profile");
                        String fullName = jsonObject.getString("fullName");
                        String businessName = jsonObject.getString("businessName");
                        String jobTitleName = jsonObject.getString("jobTitleName");
                        String address = jsonObject.getString("address");
                        String bio = URLDecoder.decode(jsonObject.getString("bio"), "UTF-8");
                        String specializationName = jsonObject.getString("specializationName");
                        String rating = jsonObject.getString("rating");
                        String company_logo = jsonObject.getString("company_logo");
                        String profileImage = jsonObject.getString("profileImage");

                        setData(fullName,businessName,jobTitleName,address,view,rating,bio,specializationName,company_logo,profileImage,favourite_count,recommend_count);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(activity, NetworkActivity.class));
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

    private void badgeCount(){
        new VolleyGetPost(activity, AllAPIs.BADGE_COUNT, false, "BADGE_COUNT", false) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("data");

                    JSONObject object0 = array.getJSONObject(0);
                    TextView tvRecommendedBudge = view.findViewById(R.id.tvRecommendedBudge);
                    String user_recommends = object0.getString("user_recommends");
                    tvRecommendedBudge.setText(user_recommends);
                    tvRecommendedBudge.setVisibility(!user_recommends.isEmpty()&&!user_recommends.equals("0")?View.VISIBLE:View.GONE);

                    JSONObject object1 = array.getJSONObject(1);
                    TextView tvFacouriteBudge = view.findViewById(R.id.tvFacouriteBudge);
                    String user_favourites = object1.getString("user_favourites");
                    tvFacouriteBudge.setText(user_favourites);
                    tvFacouriteBudge.setVisibility(!user_favourites.isEmpty()&&!user_favourites.equals("0")?View.VISIBLE:View.GONE);

                    JSONObject object2 = array.getJSONObject(2);
                    TextView tvReviewBadge = view.findViewById(R.id.tvReviewBadge);
                    String user_review = object2.getString("user_review");
                    tvReviewBadge.setText(user_review);
                    tvReviewBadge.setVisibility(!user_review.isEmpty()&&!user_review.equals("0")?View.VISIBLE:View.GONE);

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
                params.put("authToken",Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void setData(String fullName, String businessName, String jobTitleName, String address, View view, String rating, String bio, String specializationName, String company_logo, String profileImage, String favourite_count, String recommend_count) {
        TextView tv_for_fullName = view.findViewById(R.id.tv_for_fullName);tv_for_fullName.setText(fullName);
        TextView tv_for_businessName = view.findViewById(R.id.tv_for_businessName);tv_for_businessName.setText(businessName);
        TextView tv_for_jobTitle = view.findViewById(R.id.tv_for_jobTitle);tv_for_jobTitle.setText(jobTitleName);
        TextView tv_for_address = view.findViewById(R.id.tv_for_address);tv_for_address.setText(address);
        TextView tv_for_bio = view.findViewById(R.id.tv_for_bio);tv_for_bio.setText(bio.isEmpty()?"NA":bio);
        TextView tv_for_aofs = view.findViewById(R.id.tv_for_aofs);tv_for_aofs.setText(specializationName);
        TextView tv_for_favourite = view.findViewById(R.id.tv_for_favourite);tv_for_favourite.setText(favourite_count.isEmpty()?"0":favourite_count);
        TextView tv_for_recomend = view.findViewById(R.id.tv_for_recomend);tv_for_recomend.setText(recommend_count.isEmpty()?"0":recommend_count);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);ratingBar.setRating(rating.isEmpty()?0:Float.parseFloat(rating));
        ImageView iv_company_logo = view.findViewById(R.id.iv_company_logo);Picasso.with(activity).load(company_logo).into(iv_company_logo);
        ImageView iv_profile_image = view.findViewById(R.id.iv_profile_image);Picasso.with(activity).load(profileImage).into(iv_profile_image);
    }

    @Override
    public void onResume() {
        super.onResume();
        badgeCount();
        activity.badgeCount();
        if (Constant.NETWORK_CHECK == 1){
           getMyprofileData();
            Constant.NETWORK_CHECK = 0;
        }
    }


    @Override
    public void onClick(View v) {
       switch (v.getId()){
            case R.id.card_for_favourite:
                startActivity(new Intent(activity,BothFavouriteActivity.class));
                break;
            case R.id.card_for_recommend:
                startActivity(new Intent(activity, BothRecommendedActivity.class));
                break;
            case R.id.tv_for_view:
                Intent intent = new Intent(activity,ReviewActivity.class);
                intent.putExtra("USERID",Uconnekt.session.getUserInfo().userId);
                activity.startActivity(intent);
                break;
        }
    }




}
