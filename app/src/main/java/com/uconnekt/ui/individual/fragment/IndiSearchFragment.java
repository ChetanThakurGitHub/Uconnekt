package com.uconnekt.ui.individual.fragment;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.IndiSearchAdapter;
import com.uconnekt.adapter.listing.SpecialityListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.IndiSearchList;
import com.uconnekt.model.SpecialityList;
import com.uconnekt.pagination.EndlessRecyclerViewScrollListener;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class IndiSearchFragment extends Fragment implements View.OnClickListener {

    private JobHomeActivity activity;
    public ArrayList<IndiSearchList> searchLists = new ArrayList<>();
    private ArrayList<SpecialityList> arrayList = new ArrayList<>();
    private IndiSearchAdapter indiSearchAdapter;
    private RecyclerView recycler_view,recycler_list;
    private SpecialityListAdapter listAdapter;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public int offset = 0;
    private LinearLayout layout_for_noData;
    public RelativeLayout layout_for_list;
    public TextView tv_for_speName;
    public Boolean goneVisi = false;
    public ImageView iv_for_arrow;
    public String specialtyId = "",ratingNo = "",company = "",address = "" ,city = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indi_search, container, false);
        initView(view);
        indiSearchAdapter = new IndiSearchAdapter(searchLists,activity);
        listAdapter = new SpecialityListAdapter(arrayList,this);
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
                getList(specialtyId, ratingNo, company, address, city);
            }
        });

        recycler_view.setAdapter(indiSearchAdapter);
        recycler_list.setAdapter(listAdapter);
        getList(specialtyId, ratingNo, company, address, city);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getList(specialtyId, ratingNo, company, address, city);
            }
        };
        recycler_view.addOnScrollListener(scrollListener);

        return view;
    }

    private void initView(View view){
        recycler_view = view.findViewById(R.id.recycler_view);
        recycler_list = view.findViewById(R.id.recycler_list);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);
        layout_for_list = view.findViewById(R.id.layout_for_list);
        tv_for_speName = view.findViewById(R.id.tv_for_speName);
        iv_for_arrow = view.findViewById(R.id.iv_for_arrow);
        view.findViewById(R.id.card_for_spName).setOnClickListener(this);
        view.findViewById(R.id.mainlayout).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobHomeActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getDropDownlist();
            getList(specialtyId, ratingNo, company, address, city);
        }
        Constant.NETWORK_CHECK =0;
    }

    public void getList(String specialtyIds, String ratingNos, String companys, String location, String citys){

        specialtyId = specialtyIds; ratingNo = ratingNos; company = companys; address = location ;city = citys;

        new VolleyGetPost(activity, AllAPIs.INDI_SEARCH_LIST,true,"List",true ) {
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
                                IndiSearchList indiSearchList = new Gson().fromJson(jsonObject.toString(),IndiSearchList.class);
                                searchLists.add(indiSearchList);
                            }
                            if (searchLists.size() == 0){
                                layout_for_noData.setVisibility(View.VISIBLE);
                            }else {
                                layout_for_noData.setVisibility(View.GONE);
                            }
                            offset = offset + 10;
                            indiSearchAdapter.notifyDataSetChanged();
                        }
                    }else {
                        layout_for_noData.setVisibility(View.VISIBLE);
                        indiSearchAdapter.notifyDataSetChanged();
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
                params.put("speciality_id",specialtyId);
                params.put("rating",ratingNo);
                params.put("company",company);
                params.put("location",address);
                params.put("city",city);
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
                        JSONArray results = result.getJSONArray("opposite_speciality_list");
                        SpecialityList specialityList1 = new SpecialityList();
                        specialityList1.specializationId = "";
                        specialityList1.specializationName = "All";
                        arrayList.add(specialityList1);
                        for (int i = 0; i < results.length(); i++) {
                            SpecialityList specialityList = new SpecialityList();
                            JSONObject object = results.getJSONObject(i);
                            specialityList.specializationId = object.getString("specializationId");
                            specialityList.specializationName = object.getString("specializationName");
                            arrayList.add(specialityList);
                        }
                        listAdapter.notifyDataSetChanged();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_for_spName:
                if (!goneVisi) {
                    layout_for_list.setVisibility(View.VISIBLE);
                    iv_for_arrow.setImageResource(R.drawable.ic_up_arrow);
                    goneVisi = true;
                }else {
                    layout_for_list.setVisibility(View.GONE);
                    iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                    goneVisi = false;
                }
                break;
        }
    }
}
