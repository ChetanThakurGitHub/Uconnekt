package com.uconnekt.ui.individual.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.ReviewListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.IndiSearchList;
import com.uconnekt.model.ReviewList;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.activity.FavouriteActivity;
import com.uconnekt.ui.individual.activity.RatingActivity;
import com.uconnekt.ui.individual.activity.RecommendedActivity;
import com.uconnekt.ui.individual.activity.ReviewActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class IndiProfileFragment extends Fragment implements View.OnClickListener {

    private JobHomeActivity activity;
    private IndiSearchList indiSearchLists;
    private static final String ARG_PARAM1 = "param1";
    private TextView tv_for_bio,tv_for_favofite,tv_for_review,tv_for_recomend,tv_for_noReview;
    private RatingBar ratingBar;
    private ImageView iv_for_favourite,iv_for_recommend;
    private int favourite_count = 0,recommend_count = 0;
    public ArrayList<ReviewList> list = new ArrayList<>();
    private ReviewListAdapter reviewListAdapter;
    private RecyclerView recycler_view;

    public static IndiProfileFragment newInstance(IndiSearchList indiSearchList) {
        IndiProfileFragment fragment = new IndiProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, indiSearchList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            indiSearchLists = (IndiSearchList) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_indi_profile, container, false);
        activity.setToolbarIcon(2);
        initView(view);
        apiCalling();
        reviewListAdapter = new ReviewListAdapter(activity,list);
        recycler_view.setAdapter(reviewListAdapter);

        iv_for_favourite.setOnClickListener(this);
        tv_for_favofite.setOnClickListener(this);
        tv_for_recomend.setOnClickListener(this);
        iv_for_recommend.setOnClickListener(this);
        tv_for_review.setOnClickListener(this);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.mainlayout).setOnClickListener(this);
        view.findViewById(R.id.iv_for_chat).setOnClickListener(this);
        view.findViewById(R.id.iv_for_share).setOnClickListener(this);
        view.findViewById(R.id.layout_for_rate).setOnClickListener(this);
        ImageView iv_profile_image = view.findViewById(R.id.iv_profile_image);
        ImageView iv_company_logo = view.findViewById(R.id.iv_company_logo);
        TextView iv_for_fullName = view.findViewById(R.id.iv_for_fullName);
        TextView tv_for_businessName = view.findViewById(R.id.tv_for_businessName);
        TextView tv_for_specializationName = view.findViewById(R.id.tv_for_specializationName);
        TextView tv_for_address = view.findViewById(R.id.tv_for_address);
        TextView tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        tv_for_bio = view.findViewById(R.id.tv_for_bio);
        tv_for_favofite = view.findViewById(R.id.tv_for_favofite);
        tv_for_review = view.findViewById(R.id.tv_for_review);
        tv_for_recomend = view.findViewById(R.id.tv_for_recomend);
        ratingBar = view.findViewById(R.id.ratingBar);
        iv_for_favourite = view.findViewById(R.id.iv_for_favourite);
        iv_for_recommend = view.findViewById(R.id.iv_for_recommend);
        recycler_view = view.findViewById(R.id.recycler_view);
        tv_for_noReview = view.findViewById(R.id.tv_for_noReview);

        setData(iv_profile_image,iv_company_logo,iv_for_fullName,tv_for_businessName,tv_for_specializationName,tv_for_address,tv_for_aofs);
    }

    private void setData(ImageView iv_profile_image, ImageView iv_company_logo, TextView iv_for_fullName, TextView tv_for_businessName, TextView tv_for_specializationName, TextView tv_for_address, TextView tv_for_aofs){
        Picasso.with(activity).load(indiSearchLists.profileImage).into(iv_profile_image);
        Picasso.with(activity).load(indiSearchLists.company_logo).into(iv_company_logo);
        iv_for_fullName.setText(indiSearchLists.fullName.isEmpty()?"NA":indiSearchLists.fullName);
        tv_for_businessName.setText(indiSearchLists.businessName.isEmpty()?"NA":indiSearchLists.businessName);
        tv_for_specializationName.setText(indiSearchLists.specializationName.isEmpty()?"NA":indiSearchLists.specializationName);
        tv_for_aofs.setText(indiSearchLists.specializationName.isEmpty()?"NA":indiSearchLists.specializationName);
        tv_for_address.setText(indiSearchLists.address.isEmpty()?"NA":indiSearchLists.address);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (JobHomeActivity) context;
        Constant.NETWORK_CHECK =0;
    }

    private void apiCalling(){
        new VolleyGetPost(activity, AllAPIs.PROFILE+"user_id="+indiSearchLists.userId,false,"Profile",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONArray array = jsonObject.getJSONArray("profile");
                        JSONObject object = array.getJSONObject(0);
                        String bio = object.getString("bio");
                        String rating = object.getString("rating");
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

                        setApiData(bio, rating, review_count);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_for_noReview.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_chat:
                MyCustomMessage.getInstance(activity).customToast(getString(R.string.under_development_mode));
                break;
            case R.id.iv_for_share:
                MyCustomMessage.getInstance(activity).customToast(getString(R.string.under_development_mode));
                break;
            case R.id.layout_for_rate:
                Intent intent = new Intent(activity,RatingActivity.class);
                intent.putExtra("USERID",indiSearchLists.userId);
                activity.startActivity(intent);
                break;
            case R.id.iv_for_favourite:
                favourite();
                break;
            case R.id.tv_for_favofite:
                intent = new Intent(activity,FavouriteActivity.class);
                intent.putExtra("USERID",indiSearchLists.userId);
                activity.startActivity(intent);
                break;
            case R.id.tv_for_recomend:
                intent = new Intent(activity,RecommendedActivity.class);
                intent.putExtra("USERID",indiSearchLists.userId);
                activity.startActivity(intent);
                break;
            case R.id.iv_for_recommend:
                recommend();
                break;
            case R.id.tv_for_review:
                intent = new Intent(activity,ReviewActivity.class);
                intent.putExtra("USERID",indiSearchLists.userId);
                activity.startActivity(intent);
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
                params.put("favourite_for",indiSearchLists.userId);
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
                params.put("recommend_for",indiSearchLists.userId);
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
