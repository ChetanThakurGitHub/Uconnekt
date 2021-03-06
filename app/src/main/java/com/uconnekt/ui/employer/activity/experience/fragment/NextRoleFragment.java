package com.uconnekt.ui.employer.activity.experience.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.ui.employer.activity.experience.ExpActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class NextRoleFragment extends Fragment {

    private ExpActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.fragment_next_role, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showPrefilledData(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ExpActivity) context;
    }

    private void showPrefilledData(final View view){
        new VolleyGetPost(activity, AllAPIs.INDI_PROFILE+activity.userId, false, "getUserPersonalInfo", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("experience");
                        JSONObject object = jsonObject1.getJSONObject("next_role");

                        String next_availability = object.getString("next_availability");
                        String next_speciality_name = object.getString("next_speciality_name");
                        String next_location = object.getString("next_location");
                        String expectedSalary = object.getString("expectedSalaryShow");
                        String employementType = object.getString("employementType");

                        switch (expectedSalary) {
                            case "$any-$":
                                expectedSalary = "Any";
                                break;
                            case "$0-$20000":
                                expectedSalary = "$0-$20,000";
                                break;
                            case "$20000-$40000":
                                expectedSalary = "$20,000-$40,000";
                                break;
                            case "$40000-$60000":
                                expectedSalary = "$40,000-$60,000";
                                break;
                            case "$60000-$80000":
                                expectedSalary = "$60,000-$80,000";
                                break;
                            case "$80000-$100000":
                                expectedSalary = "$80,000-$100,000";
                                break;
                            case "$100000-$120000":
                                expectedSalary = "$100,000-$120,000";
                                break;
                            case "$120000-$150000":
                                expectedSalary = "$120,000-$150,000";
                                break;
                            case "$150000-$200000":
                                expectedSalary = "$150,000-$200,000";
                                break;
                            case "$200000-$99999999":
                                expectedSalary = "$200,000+";
                                break;
                        }

                        view.findViewById(R.id.layout_for_noData).setVisibility(View.GONE);
                        view.findViewById(R.id.layout_for_data).setVisibility(View.VISIBLE);

                        setData(view,next_availability,next_speciality_name,next_location,expectedSalary,employementType);
                    }

                } catch (JSONException e) {
                    view.findViewById(R.id.layout_for_noData).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.layout_for_data).setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {}

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

    private void setData(View view, String next_availability, String next_speciality_name, String next_location, String expectedSalary, String employementType) {
        TextView tv_for_availability = view.findViewById(R.id.tv_for_availability);tv_for_availability.setText(next_availability);
        TextView tv_for_aofs = view.findViewById(R.id.tv_for_aofs);tv_for_aofs.setText(next_speciality_name);
        TextView tv_for_location = view.findViewById(R.id.tv_for_location);tv_for_location.setText(next_location);
        TextView tv_for_salary = view.findViewById(R.id.tv_for_salary);tv_for_salary.setText(expectedSalary);
        TextView tv_for_empType = view.findViewById(R.id.tv_for_empType);tv_for_empType.setText(employementType);
    }
}
