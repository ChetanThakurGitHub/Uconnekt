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
import com.uconnekt.util.Utils;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CurrentRoleFragment extends Fragment {

    private ExpActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.fragment_current_rol, container, false);
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
                        JSONObject object = jsonObject1.getJSONObject("current_role");

                        String current_job_title_name = object.getString("current_job_title_name");
                        String current_company = object.getString("current_company");
                        String current_start_date = object.getString("current_start_date");
                        String current_finish_date = object.getString("current_finish_date");
                        String current_description = URLDecoder.decode(object.getString("current_description"), "UTF-8");
                        getDateDifferenceInDDMMYYYY(current_start_date,current_finish_date,view);

                        view.findViewById(R.id.layout_for_noData).setVisibility(View.GONE);
                        view.findViewById(R.id.layout_for_data).setVisibility(View.VISIBLE);

                        setData(current_job_title_name,current_company,current_start_date,current_finish_date,current_description,view);
                    }

                } catch (JSONException e) {
                    view.findViewById(R.id.layout_for_noData).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.layout_for_data).setVisibility(View.GONE);
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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

    private void setData(String current_job_title_name, String current_company, String current_start_date, String current_finish_date, String current_description, View view) {
        TextView tv_for_jobTitle = view.findViewById(R.id.tv_for_jobTitle);tv_for_jobTitle.setText(current_job_title_name.isEmpty()?"NA":current_job_title_name);
        TextView tv_for_company = view.findViewById(R.id.tv_for_company);tv_for_company.setText(current_company.isEmpty()?"NA":current_company);
        TextView tv_for_startD = view.findViewById(R.id.tv_for_startD);tv_for_startD.setText(current_start_date.isEmpty()?"NA":current_start_date);
        TextView tv_for_finishD = view.findViewById(R.id.tv_for_finishD);tv_for_finishD.setText(current_finish_date.isEmpty()?current_start_date.isEmpty()?"NA":"Still here":current_finish_date);
        TextView tv_for_description = view.findViewById(R.id.tv_for_description);tv_for_description.setText(current_description.isEmpty()?"NA":current_description);
    }

    //*****************calculation********************

    public void getDateDifferenceInDDMMYYYY(String departDateTime, String returnDateTime, View view) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        if (returnDateTime.isEmpty()){
            returnDateTime = Utils.getCurrentDate();
        }


        try {
            Date startDate = simpleDateFormat.parse(departDateTime);
            Date endDate = simpleDateFormat.parse(returnDateTime);


            Calendar fromDate = Calendar.getInstance();
            Calendar toDate = Calendar.getInstance();
            fromDate.setTime(startDate);
            toDate.setTime(endDate);
            int increment = 0;
            int year, month, day;
            System.out.println(fromDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
                increment = fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            System.out.println("increment" + increment);
// DAY CALCULATION
            if (increment != 0) {
                day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
                increment = 1;
            } else {
                day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
            }

// MONTH CALCULATION
            if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
                month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
                increment = 1;
            } else {
                month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
                increment = 0;
            }

// YEAR CALCULATION
            year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);

            setDate(year,month,day,view);

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }


    private void setDate(long elapsedYear, long elapsedMonth, long elapsedDays ,View view){
        String result ;
        if (elapsedYear != 0 | elapsedMonth != 0){
            result = elapsedYear+" Years, ";
            result = result +Math.abs( elapsedMonth) + " Months";
            TextView tv_for_totalExp = view.findViewById(R.id.tv_for_totalExp);tv_for_totalExp.setText(result);
        }

        if (elapsedMonth == 0 && elapsedYear == 0) {
            result = Math.abs(elapsedDays) + " Days";
            TextView tv_for_totalExp = view.findViewById(R.id.tv_for_totalExp);tv_for_totalExp.setText(result);
        }
    }
}
