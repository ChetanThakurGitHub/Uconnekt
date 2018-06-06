package com.uconnekt.ui.individual.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.RecommededAdapter;
import com.uconnekt.adapter.listing.ReviewFullListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.RecommendedList;
import com.uconnekt.model.ReviewList;
import com.uconnekt.pagination.EndlessRecyclerViewScrollListener;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class RecommendedActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recycler_view;
    private String userId = "";
    private LinearLayout layout_for_noData;
    private int offset = 0;
    private ArrayList<RecommendedList> reviewLists = new ArrayList<>();
    private RecommededAdapter fullListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended);

        initView();
        Bundle extras = getIntent().getExtras();
        if(extras != null) userId = extras.getString("USERID");
        getReviewsList();

        fullListAdapter = new RecommededAdapter(this,reviewLists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(false);
        recycler_view.setAdapter(fullListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.yellow);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reviewLists.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                offset = 0;
                getReviewsList();
            }
        });

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getReviewsList();
            }
        };
        recycler_view.addOnScrollListener(scrollListener);
    }

    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.recommend);
        recycler_view = findViewById(R.id.recycler_view);
        layout_for_noData = findViewById(R.id.layout_for_noData);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getReviewsList(){
        new VolleyGetPost(this, AllAPIs.RECOMMENDS_LIST+userId+"&limit="+10+"&offset="+offset, false, "ReviewsList", false) {
            @Override
            public void onVolleyResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        mSwipeRefreshLayout.setRefreshing(false);
                        JSONArray array = jsonObject.getJSONArray("recommendList");
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
                startActivity(new Intent(RecommendedActivity.this, NetworkActivity.class));
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
