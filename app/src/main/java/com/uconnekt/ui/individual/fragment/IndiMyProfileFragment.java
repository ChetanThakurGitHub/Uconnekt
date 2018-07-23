package com.uconnekt.ui.individual.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.RecommendedList;
import com.uconnekt.ui.common_activity.BothFavouriteActivity;
import com.uconnekt.ui.common_activity.BothRecommendedActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.activity.BasicInfoActivity;
import com.uconnekt.ui.employer.activity.ResumeActivity;
import com.uconnekt.ui.employer.activity.experience.ExpActivity;
import com.uconnekt.ui.employer.fragment.MyProfileFragment;
import com.uconnekt.ui.individual.activity.FavouriteActivity;
import com.uconnekt.ui.individual.activity.RecommendedActivity;
import com.uconnekt.ui.individual.activity.ReviewActivity;
import com.uconnekt.ui.individual.activity.ViewActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class IndiMyProfileFragment extends Fragment implements View.OnClickListener {

    private JobHomeActivity activity;
    private View view;
    private static final String ARG_PARAM1 = "param1";

    public static IndiMyProfileFragment newInstance(String notificationId) {
        IndiMyProfileFragment fragment = new IndiMyProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, notificationId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            String userID = "";
            userID =  getArguments().getString(ARG_PARAM1);
            if (userID.equals("profile_view")){
                activity.startActivity(new Intent(activity,ViewActivity.class));
            }else if (userID.equals("user_favourites")){
                Intent intent = new Intent(activity,FavouriteActivity.class);
                intent.putExtra("USERID",Uconnekt.session.getUserInfo().userId);
                activity.startActivity(intent);
            }else if (userID.equals("user_recommends")){
                Intent intent = new Intent(activity,RecommendedActivity.class);
                intent.putExtra("USERID",Uconnekt.session.getUserInfo().userId);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_indi_my_profile, container, false);
        initView();
        getMyprofileData();
        return view;
    }

    private void initView() {
        view.findViewById(R.id.card_for_favourite).setOnClickListener(this);
        view.findViewById(R.id.card_for_views).setOnClickListener(this);
        view.findViewById(R.id.card_for_recommend).setOnClickListener(this);
        view.findViewById(R.id.layout_for_basicInfo).setOnClickListener(this);
        view.findViewById(R.id.layout_for_Experience).setOnClickListener(this);
        view.findViewById(R.id.layout_for_Resume).setOnClickListener(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobHomeActivity) context;
    }

    public void getMyprofileData(){
        new VolleyGetPost(activity, AllAPIs.MY_PROFILE, false, "MyProfile", true) {
            @Override
            public void onVolleyResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String view_count = object.getString("view_count");
                    String favourite_count = object.getString("favourite_count");
                    String recommend_count = object.getString("recommend_count");
                    if (status.equals("success")){
                        JSONArray array = object.getJSONArray("indivisual_profile");
                        JSONObject jsonObject = array.getJSONObject(0);
                        String fullName = jsonObject.getString("fullName");
                        String specializationName = jsonObject.getString("specializationName");
                        String address = jsonObject.getString("address");
                        String jobTitleName = jsonObject.getString("jobTitleName");
                        String profileImage = jsonObject.getString("profileImage");

                        JSONObject object1 = object.getJSONObject("basic_info");
                        String bio = object1.getString("bio");
                        setData(fullName,specializationName,address,jobTitleName,profileImage,view_count,bio,favourite_count,recommend_count);
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

    private void setData(String fullName, String specializationName, String address, String jobTitleName, String profileImage, String view_count, String bio, String favourite_count, String recommend_count) throws UnsupportedEncodingException {
        ImageView bg_profile = view.findViewById(R.id.bg_profile);
        ImageView iv_profile_image = view.findViewById(R.id.iv_profile_image);
        TextView tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        TextView tv_for_company = view.findViewById(R.id.tv_for_company);
        TextView tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        TextView tv_for_address = view.findViewById(R.id.tv_for_address);

        Picasso.with(activity).load(profileImage).into(bg_profile);
        Picasso.with(activity).load(profileImage).into(iv_profile_image);
        tv_for_fullName.setText(fullName);
        tv_for_company.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
        tv_for_address.setText(address.isEmpty()?"NA":address);

        TextView tv_for_favourite = view.findViewById(R.id.tv_for_favourite);
        TextView tv_for_views = view.findViewById(R.id.tv_for_views);
        TextView tv_for_recomend = view.findViewById(R.id.tv_for_recomend);
        TextView tv_for_bio = view.findViewById(R.id.tv_for_bio);

        tv_for_favourite.setText(favourite_count.isEmpty()?"0":favourite_count);
        tv_for_views.setText(view_count.isEmpty()?"0":view_count);
        tv_for_recomend.setText(recommend_count.isEmpty()?"0":recommend_count);

        String bios = URLDecoder.decode(bio, "UTF-8");
        tv_for_bio.setText(bios.isEmpty()?"NA":bios);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getMyprofileData();
            Constant.NETWORK_CHECK = 0;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_for_favourite:
                startActivity(new Intent(activity, BothFavouriteActivity.class));
                break;
            case R.id.card_for_views:
                startActivity(new Intent(activity, ViewActivity.class));
                break;
            case R.id.card_for_recommend:
                startActivity(new Intent(activity, BothRecommendedActivity.class));
                break;
            case R.id.layout_for_basicInfo:
                Intent intent = new Intent(activity,BasicInfoActivity.class);
                intent.putExtra("USERID",Uconnekt.session.getUserInfo().userId);
                activity.startActivity(intent);
                break;
            case R.id.layout_for_Experience:
                intent = new Intent(activity,ExpActivity.class);
                intent.putExtra("USERID",Uconnekt.session.getUserInfo().userId);
                activity.startActivity(intent);
                break;
            case R.id.layout_for_Resume:
                intent = new Intent(activity,ResumeActivity.class);
                intent.putExtra("USERID",Uconnekt.session.getUserInfo().userId);
                activity.startActivity(intent);
                break;
        }
    }
}
