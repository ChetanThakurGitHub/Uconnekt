package com.uconnekt.ui.employer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.EmpSearchAdapter;
import com.uconnekt.adapter.listing.SpecialityListAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.BusiSearchList;
import com.uconnekt.model.SpecialityList;
import com.uconnekt.pagination.EndlessRecyclerViewScrollListener;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SearchFragment extends Fragment implements View.OnClickListener{

    private HomeActivity activity;
    public ArrayList<BusiSearchList> searchLists = new ArrayList<>();
    private ArrayList<SpecialityList> arrayList = new ArrayList<>();
    private ArrayList<SpecialityList> arrayListBackup = new ArrayList<>();
    private EmpSearchAdapter empSearchAdapter;
    private RecyclerView recycler_view,recycler_list;
    private SpecialityListAdapter listAdapter;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public int offset = 0;
    private LinearLayout layout_for_noData;
    public RelativeLayout layout_for_list;
    public EditText tv_for_speName;
    public Boolean goneVisi = false;
    private TextView tv_for_nodata;
    public ImageView iv_for_arrow;
    public String specialityID = "",jobTitleId = "",availabilityId = "", employmentType = "",location = "",strengthId = "" ,valueId = "",city = "",state = "",country="",minExperience = "", maxExperience = "", minSalarys = "", maxSalarys = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initView(view);
        empSearchAdapter = new EmpSearchAdapter(searchLists,activity);
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
                getList(specialityID, jobTitleId, availabilityId, location, strengthId, valueId, city, state, country, employmentType,minExperience , maxExperience , minSalarys , maxSalarys);
            }
        });

        recycler_view.setAdapter(empSearchAdapter);
        recycler_list.setAdapter(listAdapter);

        getList(specialityID, jobTitleId, availabilityId, location, strengthId, valueId, city, state, country, employmentType,minExperience , maxExperience , minSalarys , maxSalarys);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getList(specialityID, jobTitleId, availabilityId, location, strengthId, valueId, city, state, country, employmentType,minExperience , maxExperience , minSalarys , maxSalarys);
            }
        };
        recycler_view.addOnScrollListener(scrollListener);

        tv_for_speName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (goneVisi)filter(s.toString());
            }
        });

        tv_for_speName.setFocusableInTouchMode(false);

        return view;
    }

    private void filter(String text){
        if (text.isEmpty()){
            arrayList.clear();
            arrayList.addAll(arrayListBackup);
            listAdapter.notifyDataSetChanged();
            return;
        }
        ArrayList<SpecialityList> temp = new ArrayList<>();
        for(SpecialityList d: arrayListBackup){
            if(d.specializationName.toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        arrayList.clear();
        arrayList.addAll(temp);
        tv_for_nodata.setVisibility(arrayList.isEmpty()?View.VISIBLE:View.GONE);
        listAdapter.notifyDataSetChanged();
    }


    private void initView(View view){
        recycler_view = view.findViewById(R.id.recycler_view);
        tv_for_nodata = view.findViewById(R.id.tv_for_nodata);
        recycler_list = view.findViewById(R.id.recycler_list);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);
        layout_for_list = view.findViewById(R.id.layout_for_list);
        tv_for_speName = view.findViewById(R.id.tv_for_speName);
        iv_for_arrow = view.findViewById(R.id.iv_for_arrow);
        view.findViewById(R.id.tv_for_speName).setOnClickListener(this);
        if (!goneVisi)iv_for_arrow.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

    public void getList(String specialityIDs, String jobTitleIds, String availabilityIds, String address, String strengthIds, String valueIds, String citys, String states, String countrys, String employmentTypes, String minExp, String maxExp, String minSalary, String maxSalary){
        specialityID = specialityIDs;city = citys; country = countrys; state = states;
        jobTitleId = jobTitleIds;
        availabilityId = availabilityIds;employmentType = employmentTypes;
        location = address;
        strengthId = strengthIds;
        valueId = valueIds;
        minExperience = minExp; maxExperience = maxExp; minSalarys = minSalary; maxSalarys = maxSalary;

        new VolleyGetPost(activity, AllAPIs.BUSI_SEARCH_LIST,true,"List",true ) {
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
                params.put("speciality_id",specialityID);
                params.put("job_title",jobTitleId);
                params.put("availability",availabilityId);
                params.put("location",location);
                params.put("strength",strengthId);
                params.put("city",city==null?"":city);
                params.put("state",state==null?"":state);
                params.put("country",country==null?"":country);
                params.put("value",valueId);
                params.put("limit","10");
                params.put("pagination","1");
                params.put("offset",offset+"");
                params.put("employementType",employmentType);
                params.put("experienceFrom",minExperience);
                params.put("experienceTo",maxExperience.equals("10+")?"40":maxExperience);
                String first = "";
                if (!minSalarys.isEmpty()){
                    first = minSalarys.replace("$","");
                    first = first.replace(",","");
                }
                params.put("expectedSalaryFrom",first);
                String second = "";
                if (!maxSalarys.isEmpty()){
                    second = maxSalarys.replace("$","");
                    second = second.replace(",","");
                }
                params.put("expectedSalaryTo",second.equals("200000+")?"99999999":second);
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
                        arrayListBackup.clear();
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("opposite_job_title");
                        SpecialityList specialityList1 = new SpecialityList();
                        specialityList1.specializationId = "";
                        specialityList1.specializationName = "All";
                        arrayList.add(specialityList1);
                        for (int i = 0; i < results.length(); i++) {
                            SpecialityList specialityList = new SpecialityList();
                            JSONObject object = results.getJSONObject(i);
                            specialityList.specializationId = object.getString("jobTitleId");
                            specialityList.specializationName = object.getString("jobTitleName") + " ("+object.getString("total_registered")+")";
                            arrayList.add(specialityList);
                        }
                        arrayListBackup.addAll(arrayList);
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
    public void onResume() {
        super.onResume();
        if (Constant.NETWORK_CHECK == 1){
            getDropDownlist();
            getList(specialityID, jobTitleId, availabilityId, location, strengthId, valueId, city, state, country, employmentType, minExperience , maxExperience , minSalarys , maxSalarys);
        }
        Constant.NETWORK_CHECK =0;
        layout_for_list.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_for_speName:
                if (!goneVisi) {
                    layout_for_list.setVisibility(View.VISIBLE);
                    iv_for_arrow.setImageResource(R.drawable.ic_cross);
                    iv_for_arrow.setPadding(11,11,11,11);
                    goneVisi = true;
                    tv_for_speName.setFocusableInTouchMode(true);
                }else {
                    layout_for_list.setVisibility(View.GONE);
                    iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                    iv_for_arrow.setPadding(3,3,3,3);
                    goneVisi = false;
                    activity.hideKeyboard();
                }
                break;
            case R.id.iv_for_arrow:
                if (!goneVisi) {
                    layout_for_list.setVisibility(View.VISIBLE);
                    iv_for_arrow.setImageResource(R.drawable.ic_cross);
                    iv_for_arrow.setPadding(11,11,11,11);
                    goneVisi = true;
                    tv_for_speName.setFocusableInTouchMode(true);
                }else {
                    activity.hideKeyboard();
                    tv_for_speName.setText("");
                    goneVisi = false;
                    iv_for_arrow.setPadding(3,3,3,3);
                    iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                    layout_for_list.setVisibility(View.GONE);
                }
                break;
        }
    }
}
