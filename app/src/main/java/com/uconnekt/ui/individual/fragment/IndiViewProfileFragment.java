package com.uconnekt.ui.individual.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class IndiViewProfileFragment extends Fragment {

    private JobHomeActivity activity;
    private ImageView iv_for_favourite,iv_for_recommend,iv_profile_image,profile;
    private int favourite_count = 0,recommend_count = 0;
    private TextView tv_for_bio,tv_for_review_count,tv_for_favourite_count,tv_for_recomend,
            tv_for_aofs,tv_for_address,tv_for_company,tv_for_fullName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        activity.setToolbarIcon(7);
        initView(view);
        apiCalling();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobHomeActivity) context;
    }

    private void initView(View view){
        profile = view.findViewById(R.id.profile);
        iv_profile_image = view.findViewById(R.id.iv_profile_image);
        tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        tv_for_company = view.findViewById(R.id.tv_for_company);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        iv_for_favourite = view.findViewById(R.id.iv_for_favourite);
        iv_for_recommend = view.findViewById(R.id.iv_for_recommend);
        tv_for_bio = view.findViewById(R.id.tv_for_bio);
        tv_for_review_count = view.findViewById(R.id.tv_for_review_count);
        tv_for_favourite_count = view.findViewById(R.id.tv_for_favourite_count);
        tv_for_recomend = view.findViewById(R.id.tv_for_recomend);
    }

    private void setData(String profileImage, String fullName, String jobTitleName, String specializationName, String address) {
        Picasso.with(activity).load(profileImage).into(profile);
        Picasso.with(activity).load(profileImage).into(iv_profile_image);
        tv_for_fullName.setText(fullName.isEmpty()?"NA":fullName);
        tv_for_company.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
    }

    private void apiCalling(){
        new VolleyGetPost(activity, AllAPIs.PROFILE+"user_id="+Uconnekt.session.getUserInfo().userId,false,"Profile",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONArray array = jsonObject.getJSONArray("profile");
                        JSONObject object = array.getJSONObject(0);
                        String bio = object.getString("bio");
                        String profileImage = object.getString("profileImage");
                        String fullName = object.getString("fullName");
                        String jobTitleName = object.getString("jobTitleName");
                        String specializationName = object.getString("specializationName");
                        String address = object.getString("address");
                        String bios = URLDecoder.decode(bio, "UTF-8");
                        tv_for_bio.setText(bios.isEmpty()?"NA":bios);
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

}
