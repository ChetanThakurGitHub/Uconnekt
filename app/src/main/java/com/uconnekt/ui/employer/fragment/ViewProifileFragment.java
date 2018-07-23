package com.uconnekt.ui.employer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.uconnekt.model.ReviewList;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

public class ViewProifileFragment extends Fragment {

    private HomeActivity activity;
    private TextView tv_for_bio,tv_for_favofite,tv_for_review,tv_for_recomend,tv_for_aofs,tv_for_noReview,tv_for_address,tv_for_specializationName;
    private RatingBar ratingBar;
    private ImageView iv_for_favourite,iv_for_recommend,iv_company_logo;
    private int favourite_count = 0,recommend_count = 0;
    public ArrayList<ReviewList> list = new ArrayList<>();
    private ReviewListAdapter reviewListAdapter;
    private RecyclerView recycler_view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_view_proifile, container, false);

        initView(view);
        apiCalling();
        reviewListAdapter = new ReviewListAdapter(activity,list,"");
        recycler_view.setAdapter(reviewListAdapter);
        recycler_view.setHasFixedSize(true);
        recycler_view.setNestedScrollingEnabled(false);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (HomeActivity) context;
    }

    private void initView(View view) {
        ImageView iv_profile_image = view.findViewById(R.id.iv_profile_image);
        iv_company_logo = view.findViewById(R.id.iv_company_logo);
        TextView iv_for_fullName = view.findViewById(R.id.iv_for_fullName);
        TextView tv_for_businessName = view.findViewById(R.id.tv_for_businessName);
        tv_for_specializationName = view.findViewById(R.id.tv_for_specializationName);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        tv_for_bio = view.findViewById(R.id.tv_for_bio);
        tv_for_favofite = view.findViewById(R.id.tv_for_favofite);
        tv_for_review = view.findViewById(R.id.tv_for_review);
        tv_for_recomend = view.findViewById(R.id.tv_for_recomend);
        ratingBar = view.findViewById(R.id.ratingBar);
        iv_for_favourite = view.findViewById(R.id.iv_for_favourite);
        iv_for_recommend = view.findViewById(R.id.iv_for_recommend);
        recycler_view = view.findViewById(R.id.recycler_view);
        tv_for_noReview = view.findViewById(R.id.tv_for_noReview);


        if (!Uconnekt.session.getUserInfo().profileImage.isEmpty())Picasso.with(activity).load(Uconnekt.session.getUserInfo().profileImage).into(iv_profile_image);
        iv_for_fullName.setText(Uconnekt.session.getUserInfo().fullName);
        tv_for_businessName.setText(Uconnekt.session.getUserInfo().businessName);
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
                        String bio = URLDecoder.decode(object.getString("bio"), "UTF-8");
                        String address = object.getString("address");
                        String jobTitleName = object.getString("jobTitleName");
                        String specializationName = object.getString("specializationName");
                        String company_logo = object.getString("company_logo");
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

                        setApiData(bio, rating, review_count,address,jobTitleName,specializationName,company_logo);
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

    private void setApiData(String bio, String rating, String review_count, String address, String jobTitleName, String specializationName, String company_logo) {
        tv_for_bio.setText(bio.isEmpty()?"NA":bio);
        tv_for_favofite.setText(favourite_count==0?"0 Favourite":favourite_count+ " Favourite");
        tv_for_review.setText(review_count.isEmpty()?"0 Reviews":review_count+ " Reviews");
        tv_for_recomend.setText(recommend_count==0?"0 Recommend":recommend_count+ " Recommend");
        ratingBar.setRating(rating.isEmpty()?0:Float.parseFloat(rating));
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
        tv_for_specializationName.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        Picasso.with(activity).load(company_logo).into(iv_company_logo);
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

}
