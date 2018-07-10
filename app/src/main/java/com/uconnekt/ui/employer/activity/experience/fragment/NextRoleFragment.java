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

                        setData(view,next_availability,next_speciality_name,next_location);
                    }

                } catch (JSONException e) {
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

    private void setData(View view, String next_availability, String next_speciality_name, String next_location) {
        TextView tv_for_availability = view.findViewById(R.id.tv_for_availability);tv_for_availability.setText(next_availability);
        TextView tv_for_aofs = view.findViewById(R.id.tv_for_aofs);tv_for_aofs.setText(next_speciality_name);
        TextView tv_for_location = view.findViewById(R.id.tv_for_location);tv_for_location.setText(next_location);
    }

}
