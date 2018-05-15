package com.uconnekt.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.uconnekt.R;
import com.uconnekt.adapter.CustomSpAdapter;
import com.uconnekt.adapter.listing.EmpSearchAdapter;
import com.uconnekt.adapter.listing.IndiSearchAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.BusiSearchList;
import com.uconnekt.model.IndiSearchList;
import com.uconnekt.model.JobTitle;
import com.uconnekt.pagination.EndlessRecyclerViewScrollListener;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SearchFragment extends Fragment {

    private HomeActivity activity;
    private ArrayList<BusiSearchList> searchLists = new ArrayList<>();
    private ArrayList<JobTitle> arrayList = new ArrayList<>();
    private EmpSearchAdapter empSearchAdapter;
    private RecyclerView recycler_view;
    private CustomSpAdapter customSpAdapter;
    private Spinner sp_for_specialty;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int offset = 0;
    private String specilityId = "";
    private LinearLayout layout_for_noData;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initView(view);
        empSearchAdapter = new EmpSearchAdapter(searchLists,activity);
        customSpAdapter = new CustomSpAdapter(activity, arrayList,R.layout.custom_sp3);
        sp_for_specialty.setAdapter(customSpAdapter);
        getDropDownlist();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(false);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.yellow);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchLists.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                offset = 0;
                getList(specilityId);
            }
        });

        recycler_view.setAdapter(empSearchAdapter);

        sp_for_specialty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JobTitle jobTitle = arrayList.get(position);
                specilityId = jobTitle.jobTitleId;
                searchLists.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                offset = 0;
                getList(specilityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getList(specilityId);
            }
        };
        recycler_view.addOnScrollListener(scrollListener);

        return view;
    }

    private void initView(View view){
        recycler_view = view.findViewById(R.id.recycler_view);
        sp_for_specialty = view.findViewById(R.id.sp_for_specialty);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }


    private void getList(final String specilityId){

        new VolleyGetPost(activity, AllAPIs.BUSI_SEARCH_LIST,true,"List",false ) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (status.equalsIgnoreCase("success")){
                        JSONArray array = object.getJSONArray("searchList");
                        if (array!= null){
                            for (int i=0; i< array.length(); i++){

                                JSONObject jsonObject = array.getJSONObject(i);
                                BusiSearchList busiSearchList = new Gson().fromJson(jsonObject.toString(),BusiSearchList.class);
                                searchLists.add(busiSearchList);
                            }

                            if (searchLists.size() == 0){
                                layout_for_noData.setVisibility(View.VISIBLE);
                            }else {
                                layout_for_noData.setVisibility(View.GONE);
                            }
                            offset = offset + 10;
                            empSearchAdapter.notifyDataSetChanged();
                        }
                    }else {
                        layout_for_noData.setVisibility(View.VISIBLE);
                        empSearchAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    layout_for_noData.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                mSwipeRefreshLayout.setRefreshing(false);
                startActivity(new Intent(activity, NetworkActivity.class));
            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("speciality_id",specilityId);
                params.put("job_title","");
                params.put("availability","");
                params.put("location","");
                params.put("strength","");
                params.put("value","");
                params.put("limit","10");
                params.put("offset",offset+"");
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);

                return params;
            }
        }.executeVolley();
    }

    private void getDropDownlist() {

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list",true) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("speciality_list");
                        JobTitle jobTitle = new JobTitle();
                        jobTitle.jobTitleId = "";
                        jobTitle.jobTitleName = "";
                        arrayList.add(jobTitle);
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("specializationId");
                            jobTitles.jobTitleName = object.getString("specializationName");
                            arrayList.add(jobTitles);
                        }
                        customSpAdapter.notifyDataSetChanged();
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

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getDropDownlist();
            getList(specilityId);
        }
        Constant.NETWORK_CHECK =0;
    }
}
