package com.uconnekt.ui.employer.fragment;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.BusiSearchList;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.activity.BasicInfoActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.activity.FavouriteActivity;
import com.uconnekt.ui.individual.activity.RecommendedActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private HomeActivity activity;
    private static final String ARG_PARAM1 = "param1";
    private BusiSearchList busiSearchList;
    private ImageView iv_for_favourite,iv_for_recommend;
    private int favourite_count = 0,recommend_count = 0;
    private TextView tv_for_bio,tv_for_review_count,tv_for_favourite_count,tv_for_recomend;

    public static ProfileFragment newInstance(BusiSearchList busiSearchList) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, busiSearchList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            busiSearchList = (BusiSearchList) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);
        activity.setToolbarIcon(2);
        initView(view);
        view();

        iv_for_favourite.setOnClickListener(this);
        iv_for_recommend.setOnClickListener(this);
        return view;
    }

    private void initView(View view){
        ImageView profile = view.findViewById(R.id.profile);
        ImageView iv_profile_image = view.findViewById(R.id.iv_profile_image);
        TextView tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        TextView tv_for_company = view.findViewById(R.id.tv_for_company);
        TextView tv_for_address = view.findViewById(R.id.tv_for_address);
        TextView tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        iv_for_favourite = view.findViewById(R.id.iv_for_favourite);
        iv_for_recommend = view.findViewById(R.id.iv_for_recommend);
        tv_for_bio = view.findViewById(R.id.tv_for_bio);
        tv_for_review_count = view.findViewById(R.id.tv_for_review_count);
        tv_for_favourite_count = view.findViewById(R.id.tv_for_favourite_count);
        tv_for_recomend = view.findViewById(R.id.tv_for_recomend);
        view.findViewById(R.id.layout_for_basicInfo).setOnClickListener(this);
        view.findViewById(R.id.layout_for_Experience).setOnClickListener(this);
        view.findViewById(R.id.layout_for_Resume).setOnClickListener(this);
        view.findViewById(R.id.card_for_chat).setOnClickListener(this);
        view.findViewById(R.id.tv_for_favourite).setOnClickListener(this);
        view.findViewById(R.id.tv_for_recomendTxt).setOnClickListener(this);

        setData(profile,iv_profile_image,tv_for_fullName,tv_for_company,tv_for_address,tv_for_aofs);

    }

    private void setData(ImageView profile, ImageView iv_profile_image, TextView tv_for_fullName, TextView tv_for_company, TextView tv_for_address, TextView tv_for_aofs) {
        Picasso.with(activity).load(busiSearchList.profileImage).into(profile);
        Picasso.with(activity).load(busiSearchList.profileImage).into(iv_profile_image);
        tv_for_fullName.setText(busiSearchList.fullName.isEmpty()?"NA":busiSearchList.fullName);
        tv_for_company.setText(busiSearchList.jobTitleName.isEmpty()?"NA":busiSearchList.jobTitleName);
        tv_for_address.setText(busiSearchList.address.isEmpty()?"NA":busiSearchList.address);
        tv_for_aofs.setText(busiSearchList.specializationName.isEmpty()?"NA":busiSearchList.specializationName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (HomeActivity) context;
    }

    private void apiCalling(){
        new VolleyGetPost(activity, AllAPIs.PROFILE+"user_id="+busiSearchList.userId,false,"Profile",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONArray array = jsonObject.getJSONArray("profile");
                        JSONObject object = array.getJSONObject(0);
                        String bio = object.getString("bio");
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
                    }
                } catch (JSONException e) {
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

    private void favourite(){
        new VolleyGetPost(activity, AllAPIs.FACOURITES, true, "Favourite", true) {
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
                params.put("favourite_for",busiSearchList.userId);
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
        new VolleyGetPost(activity, AllAPIs.RECOMMEND, true, "Recommend", true) {
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
                params.put("recommend_for",busiSearchList.userId);
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
        new VolleyGetPost(activity, AllAPIs.VIEW, true, "Recommend", false) {
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
                params.put("view_for",busiSearchList.userId);
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
                Intent intent = new Intent(activity,BasicInfoActivity.class);
                intent.putExtra("USERID",busiSearchList.userId);
                activity.startActivity(intent);
                break;
            case R.id.layout_for_Experience:
                MyCustomMessage.getInstance(activity).customToast(getString(R.string.under_development_mode));
                break;
            case R.id.layout_for_Resume:
                MyCustomMessage.getInstance(activity).customToast(getString(R.string.under_development_mode));
                break;
            case R.id.card_for_chat:
                MyCustomMessage.getInstance(activity).customToast(getString(R.string.under_development_mode));
                break;
            case R.id.tv_for_favourite:
                intent = new Intent(activity,FavouriteActivity.class);
                intent.putExtra("USERID",busiSearchList.userId);
                activity.startActivity(intent);
                break;
            case R.id.tv_for_recomendTxt:
                intent = new Intent(activity,RecommendedActivity.class);
                intent.putExtra("USERID",busiSearchList.userId);
                activity.startActivity(intent);
                break;
        }
    }
}
