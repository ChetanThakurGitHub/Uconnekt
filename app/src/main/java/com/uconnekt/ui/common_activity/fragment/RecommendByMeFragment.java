package com.uconnekt.ui.common_activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.RecommededAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.RecommendedList;
import com.uconnekt.pagination.EndlessRecyclerViewScrollListener;
import com.uconnekt.ui.common_activity.BothRecommendedActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.volleymultipart.VolleySingleton;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class RecommendByMeFragment extends Fragment {

    private BothRecommendedActivity activity;
    private RecyclerView recycler_view;
    private String userId = "";
    private LinearLayout layout_for_noData;
    private int offset = 0;
    private ArrayList<RecommendedList> reviewLists = new ArrayList<>();
    private RecommededAdapter fullListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_by_me, container, false);

        userId = "-1";
        initView(view);
        getReviewsList(true);

        fullListAdapter = new RecommededAdapter(activity,reviewLists,userId.equals("-1")? Uconnekt.session.getUserInfo().userId:userId,1, true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(false);
        recycler_view.setAdapter(fullListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.yellow);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reviewLists.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                fullListAdapter.notifyDataSetChanged();
                offset = 0;
                VolleySingleton.getInstance(activity).cancelPendingRequests("Recommended");
              try {
                  getReviewsList(true);
              }catch (Exception e){
                  e.printStackTrace();
              }
            }
        });

        pagination(linearLayoutManager);

        return view;
    }

    private void pagination(LinearLayoutManager linearLayoutManager){
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getReviewsList(false);
            }
        };
        recycler_view.addOnScrollListener(scrollListener);
    }

    private void initView(View view) {
        recycler_view = view.findViewById(R.id.recycler_view);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BothRecommendedActivity) context;
    }

    private void getReviewsList(Boolean loader){
        new VolleyGetPost(activity, userId.equals("-1")? AllAPIs.RECOMMENDS_LIST_BY_ME+"&limit="+10+"&offset="+offset:AllAPIs.RECOMMENDS_LIST+userId+"&limit="+10+"&offset="+offset, false, "Recommended", loader) {
            @Override
            public void onVolleyResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        mSwipeRefreshLayout.setRefreshing(false);
                        JSONArray array = jsonObject.getJSONArray(userId.equals("-1")?"RecommandsList":"recommendList");
                        for (int i = 0; i < array.length(); i ++){
                            JSONObject object =  array.getJSONObject(i);
                            RecommendedList recommendedList = new Gson().fromJson(object.toString(),RecommendedList.class);
                            reviewLists.add(recommendedList);
                        }
                        if (reviewLists.size() == 0){
                            layout_for_noData.setVisibility(View.VISIBLE);
                        }else {
                            layout_for_noData.setVisibility(View.GONE);
                        }
                        offset = offset + 10;
                        fullListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    layout_for_noData.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                layout_for_noData.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
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
