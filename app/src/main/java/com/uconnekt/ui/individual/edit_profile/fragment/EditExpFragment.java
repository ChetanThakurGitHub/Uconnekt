package com.uconnekt.ui.individual.edit_profile.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uconnekt.R;
import com.uconnekt.adapter.WeekSpAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.login_ragistartion.FirebaseLogin;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.JobTitle;
import com.uconnekt.model.PreviousRole;
import com.uconnekt.model.UserInfo;
import com.uconnekt.model.Weeks;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.sp.OnSpinerItemClick;
import com.uconnekt.sp.SpinnerDialog;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.edit_profile.EditListener;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.uconnekt.util.Constant.RESULT_OK;

public class EditExpFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,DatePickerDialog.OnDateSetListener  {

    private IndiEditProfileActivity activity;
    private EditListener listener;
    private WeekSpAdapter weekSpAdapter,salarySpAdapter,empTypeSpAdapter;
    private ArrayList<JobTitle> arrayList,arrayList2,arrayList3;
    private ArrayList<PreviousRole> roleArrayList = new ArrayList<>();
    private ArrayList<Weeks> weekList,salaryList,empTypeList;
    private Spinner sp_for_weeklist,sp_for_salary,sp_for_empType;
    private LinearLayout mainlayout,layout_for_cRole,layout_for_nextR,layout_for_pRole,layout_for_preRole;
    private String jobTitleId = "",interestId = "",availability = "",jobTitleId2 = "",salary = "",employmentType = "";
    private int setValue = -1,index = -1,checkMange = -1,tempUpdateIndex = -1;
    private EditText et_for_companyName,et_for_cdescription,et_for_pdescription,et_for_compyName;
    private TextView tv_for_startD,tv_for_finishD,tv_for_txt,tv_for_address,tv_for_startDP,tv_for_finishDP,
            tv_for_txt2,tv_for_role1,tv_for_role2,tv_for_role3,tv_for_jobTitle,tv_for_jobTitle2,tv_for_aofs;
    private ImageView iv_for_checkBox,iv_for_currentRole,iv_for_nextRole,iv_for_previousRole;
    private Boolean checkBox = false,opnClo = false,opnClo2 = false,opnClo3 =false;
    private RelativeLayout layout_for_address;
    private CardView card_for_pRole1,card_for_pRole2,card_for_pRole3;
    private CusDialogProg cusDialogProg;
    private String cCompanyName = "",startDate = "",finshdate = "",address = "";
    private SpinnerDialog spinnerDialog,spinnerDialog2,spinnerDialog3;

    private SeekBar sliderExperience;
    private TextView tvYear;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_exp, container, false);

        initView(view);

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        weekList = new ArrayList<>();
        salaryList = new ArrayList<>();
        empTypeList = new ArrayList<>();
        weekSpAdapter = new WeekSpAdapter(activity, weekList,R.layout.custom_sp_week);
        salarySpAdapter = new WeekSpAdapter(activity, salaryList,R.layout.custom_sp);
        empTypeSpAdapter = new WeekSpAdapter(activity, empTypeList,R.layout.custom_sp2);
        sp_for_weeklist.setAdapter(weekSpAdapter);
        sp_for_salary.setAdapter(salarySpAdapter);
        sp_for_empType.setAdapter(empTypeSpAdapter);

        getlist();

        sp_for_weeklist.setOnItemSelectedListener(this);
        sp_for_salary.setOnItemSelectedListener(this);
        sp_for_empType.setOnItemSelectedListener(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 500 - s.length();
                tv_for_txt.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et_for_cdescription.addTextChangedListener(textWatcher);

        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 500 - s.length();
                tv_for_txt2.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et_for_pdescription.addTextChangedListener(textWatcher1);

        card_for_pRole1.setOnClickListener(this);
        card_for_pRole2.setOnClickListener(this);
        card_for_pRole3.setOnClickListener(this);

        return view;
    }

    private void showPrefilledData(){
        new VolleyGetPost(activity, AllAPIs.SHOW_PREFILLED_DATA, false, "showPrefilledData", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject object = jsonObject.getJSONObject("experience");
                        JSONObject object1 = object.getJSONObject("current_role");
                        jobTitleId = object1.getString("current_job_title");
                        String current_company = object1.getString("current_company");
                        String current_start_date = object1.getString("current_start_date");
                        String current_finish_date = object1.getString("current_finish_date");
                        String current_description = URLDecoder.decode(object1.getString("current_description"), "UTF-8");

                        if (!jobTitleId.isEmpty()) {
                            for (int i = 0; arrayList.size() > i; i++) {
                                if (arrayList.get(i).jobTitleId.equals(jobTitleId)) {
                                    tv_for_jobTitle.setText(arrayList.get(i).jobTitleName);
                                    break;
                                }
                            }
                        }

                        et_for_companyName.setText(current_company);
                        tv_for_startD.setText(current_start_date);
                        tv_for_finishD.setText(current_finish_date);
                        et_for_cdescription.setText(current_description);

                        if (!current_start_date.isEmpty()){
                            if (current_finish_date.isEmpty()){
                                iv_for_checkBox.setImageResource(R.drawable.ic_checked_gray);
                                checkBox = true;
                            }
                        }

                        JSONArray array = object.getJSONArray("previous_role");
                        for (int i = 0; i < array.length();i++){
                            PreviousRole previousRole = new PreviousRole();
                            JSONObject jsonObject1 = array.getJSONObject(i);
                            previousRole.previous_role_id = jsonObject1.getString("previousRoleId");
                            previousRole.previous_job_title = jsonObject1.getString("previous_job_title_id");
                            previousRole.previous_company_name = jsonObject1.getString("previousCompanyName");
                            previousRole.previous_description = jsonObject1.getString("previousDescription");
                            previousRole.experience = jsonObject1.getString("experience");
                            roleArrayList.add(previousRole);

                            switch (i){
                                case 0:
                                    card_for_pRole1.setVisibility(View.VISIBLE);
                                    tv_for_role1.setText(jsonObject1.getString("previousCompanyName"));
                                    break;
                                case 1:
                                    card_for_pRole2.setVisibility(View.VISIBLE);
                                    tv_for_role2.setText(jsonObject1.getString("previousCompanyName"));
                                    break;
                                case 2:
                                    card_for_pRole3.setVisibility(View.VISIBLE);
                                    tv_for_role3.setText(jsonObject1.getString("previousCompanyName"));
                                    layout_for_preRole.setVisibility(View.GONE);
                                    break;
                            }
                        }

                        JSONObject object3 = object.getJSONObject("next_role");
                        String next_availability = object3.getString("next_availability");
                        String next_speciality = object3.getString("next_speciality");
                        String next_location = object3.getString("next_location");
                        String expectedSalary = object3.getString("expectedSalary");
                        String employementType = object3.getString("employementType");

                        if (!next_availability.isEmpty()) {
                            availability = next_availability;
                            for (int i = 0; weekList.size() > i; i++) {
                                if (weekList.get(i).week.equals(next_availability)) {
                                    sp_for_weeklist.setSelection(i);
                                    break;
                                }
                            }
                        }

                        if (!expectedSalary.isEmpty()) {
                            salary = expectedSalary;
                            for (int i = 0; salaryList.size() > i; i++) {
                                if (salaryList.get(i).id.equals(expectedSalary)) {
                                    sp_for_salary.setSelection(i);
                                    break;
                                }
                            }
                        }

                        if (!employementType.isEmpty()) {
                            employmentType = employementType;
                            for (int i = 0; empTypeList.size() > i; i++) {
                                if (empTypeList.get(i).week.equals(employementType)) {
                                    sp_for_empType.setSelection(i);
                                    break;
                                }
                            }
                        }

                        if (!next_speciality.isEmpty()) {
                            interestId = next_speciality;
                            for (int i = 0; arrayList2.size() > i; i++) {
                                if (arrayList2.get(i).jobTitleId.equals(next_speciality)) {
                                    tv_for_aofs.setText(arrayList2.get(i).jobTitleName);
                                    break;
                                }
                            }
                        }

                        tv_for_address.setText(next_location);

                        cusDialogProg.dismiss();
                    }else {
                        cusDialogProg.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    cusDialogProg.dismiss();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                cusDialogProg.dismiss();
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

    private void initView(View view) {
        view.findViewById(R.id.mainlayout).setOnClickListener(this);
        view.findViewById(R.id.layout_for_startD).setOnClickListener(this);
        view.findViewById(R.id.layout_for_finishD).setOnClickListener(this);
        view.findViewById(R.id.layout_for_stillThere).setOnClickListener(this);
        view.findViewById(R.id.layout_for_address).setOnClickListener(this);
        view.findViewById(R.id.layout_for_currentRole).setOnClickListener(this);
        view.findViewById(R.id.layout_for_nextRole).setOnClickListener(this);
        view.findViewById(R.id.layout_for_previousRole).setOnClickListener(this);
        view.findViewById(R.id.iv_for_add).setOnClickListener(this);
        view.findViewById(R.id.layout_for_jobTittle).setOnClickListener(this);
        view.findViewById(R.id.layout_for_jobTittle2).setOnClickListener(this);
        view.findViewById(R.id.layout_for_aofs).setOnClickListener(this);
        sp_for_salary = view.findViewById(R.id.sp_for_salary);
        sp_for_empType = view.findViewById(R.id.sp_for_empType);
        tv_for_jobTitle = view.findViewById(R.id.tv_for_jobTitle);
        card_for_pRole2 = view.findViewById(R.id.card_for_pRole2);
        tv_for_role3 = view.findViewById(R.id.tv_for_role3);
        card_for_pRole3 = view.findViewById(R.id.card_for_pRole3);
        tv_for_role2 = view.findViewById(R.id.tv_for_role2);
        tv_for_role1 = view.findViewById(R.id.tv_for_role1);
        card_for_pRole1 = view.findViewById(R.id.card_for_pRole1);
        layout_for_preRole = view.findViewById(R.id.layout_for_preRole);
        tv_for_jobTitle2 = view.findViewById(R.id.tv_for_jobTitle2);
        et_for_compyName = view.findViewById(R.id.et_for_compyName);
        tv_for_txt2 = view.findViewById(R.id.tv_for_txt2);
        et_for_pdescription = view.findViewById(R.id.et_for_pdescription);
        layout_for_pRole = view.findViewById(R.id.layout_for_pRole);
        iv_for_previousRole = view.findViewById(R.id.iv_for_previousRole);
        layout_for_nextR = view.findViewById(R.id.layout_for_nextR);
        iv_for_nextRole = view.findViewById(R.id.iv_for_nextRole);
        tv_for_finishD = view.findViewById(R.id.tv_for_finishD);
        layout_for_cRole = view.findViewById(R.id.layout_for_cRole);
        tv_for_startD = view.findViewById(R.id.tv_for_startD);
        tv_for_startDP = view.findViewById(R.id.tv_for_startDP);
        iv_for_currentRole = view.findViewById(R.id.iv_for_currentRole);
        layout_for_address = view.findViewById(R.id.layout_for_address);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        tv_for_txt = view.findViewById(R.id.tv_for_txt);
        tv_for_finishDP = view.findViewById(R.id.tv_for_finishDP);
        tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        iv_for_checkBox = view.findViewById(R.id.iv_for_checkBox);
        et_for_cdescription = view.findViewById(R.id.et_for_cdescription);
        sp_for_weeklist = view.findViewById(R.id.sp_for_weeklist);
        mainlayout = view.findViewById(R.id.mainlayout);
        et_for_companyName = view.findViewById(R.id.et_for_companyName);

        EditText et_for_cdescription = new EditText(activity);
        et_for_cdescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        EditText et_for_pdescription = new EditText(activity);
        et_for_pdescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        sliderExperience = view.findViewById(R.id.sliderExperience);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sliderExperience.setMin(0);
            sliderExperience.setMax(10);
        }
        sliderExperience.setProgress(0);

        tvYear = view.findViewById(R.id.tvYear);

        sliderExperience.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvYear.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()){
            case R.id.layout_for_startD:
                datePicker(-1,-1,-1);
                setValue = 1;
                break;
            case R.id.layout_for_finishD:
                if (!tv_for_startD.getText().toString().equalsIgnoreCase("")){
                    if (!checkBox) {
                        setValue = 2;
                        checkMange = 2;
                        datePicker(-1, -1, -1);
                    }else {
                        MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.finish_date_v));
                    }
                }else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.select_start_date));
                }
                break;
            case R.id.layout_for_stillThere:
                if (!checkBox) {
                    iv_for_checkBox.setImageResource(R.drawable.ic_checked_gray);
                    checkBox = true;
                    tv_for_finishD.setText("");
                }else {
                    iv_for_checkBox.setImageResource(R.drawable.ic_uncheck_gray);
                    checkBox = false;
                }
                break;
            case R.id.layout_for_address:
                PermissionAll permissionAll = new PermissionAll();
                if (permissionAll.checkLocationPermission(activity)) {
                    addressClick();
                    layout_for_address.setEnabled(false);
                }
                break;
            case R.id.btn_for_next:
                validation();
                break;
            case R.id.layout_for_startDP:
                datePicker(-1,-1,-1);
                setValue = 3;
                tv_for_finishDP.setText("");
                break;
            case R.id.layout_for_finishDP:
                if (!tv_for_startDP.getText().toString().trim().isEmpty()) {
                    setValue = 4;
                    checkMange = 4;
                    datePicker(-1, -1, -1);
                }else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.select_start_date));
                }
                break;
            case R.id.layout_for_currentRole:
                gonVisi(1);
                if (!opnClo) {
                    layout_for_cRole.setVisibility(View.VISIBLE);
                    iv_for_currentRole.setImageResource(R.drawable.ic_up_arrow);
                    opnClo = true;
                } else {
                    opnClo = false;
                    layout_for_cRole.setVisibility(View.GONE);
                    iv_for_currentRole.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_nextRole:
                gonVisi(2);
                if (!opnClo2) {
                    layout_for_nextR.setVisibility(View.VISIBLE);
                    iv_for_nextRole.setImageResource(R.drawable.ic_up_arrow);
                    opnClo2 = true;
                } else {
                    opnClo2 = false;
                    layout_for_nextR.setVisibility(View.GONE);
                    iv_for_nextRole.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.layout_for_previousRole:
                gonVisi(3);
                if (!opnClo3) {
                    layout_for_pRole.setVisibility(View.VISIBLE);
                    iv_for_previousRole.setImageResource(R.drawable.ic_up_arrow);
                    opnClo3 = true;
                } else {
                    opnClo3 = false;
                    layout_for_pRole.setVisibility(View.GONE);
                    iv_for_previousRole.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.iv_for_add:
                addPreviousRole(1);
                break;

            case R.id.card_for_pRole1:
                layout_for_preRole.setVisibility(View.VISIBLE);
                try {
                    roleUpdate(0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                tempUpdateIndex = 0;
                index = 0;
                setVisibilty();
                break;
            case R.id.card_for_pRole2:
                layout_for_preRole.setVisibility(View.VISIBLE);
                try {
                    roleUpdate(1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                index = 1;
                tempUpdateIndex = 1;
                setVisibilty();
                break;
            case R.id.card_for_pRole3:
                layout_for_preRole.setVisibility(View.VISIBLE);
                try {
                    roleUpdate(2);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                index = 2;
                tempUpdateIndex = 2;
                setVisibilty();
                break;
            case R.id.layout_for_jobTittle:
                spinnerDialog.showSpinerDialog();
                break;
            case R.id.layout_for_jobTittle2:
                spinnerDialog2.showSpinerDialog();
                break;
            case R.id.layout_for_aofs:
                spinnerDialog3.showSpinerDialog();
                break;
        }
    }

    public void onSubmit(){
        activity.Isuserfilldata =false;
        validation();
    }

    private void setVisibilty(){
        switch (index){
            case 0:
                if (roleArrayList.size() == 3){
                    card_for_pRole2.setVisibility(View.VISIBLE);
                    card_for_pRole3.setVisibility(View.VISIBLE);
                }else if (roleArrayList.size() == 2){
                    card_for_pRole2.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (roleArrayList.size() == 2){
                    card_for_pRole1.setVisibility(View.VISIBLE);
                }else if (roleArrayList.size() == 3){
                    card_for_pRole1.setVisibility(View.VISIBLE);
                    card_for_pRole3.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if (roleArrayList.size() == 3){
                    card_for_pRole1.setVisibility(View.VISIBLE);
                    card_for_pRole2.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void addPreviousRole(int data){
        String companyName = et_for_compyName.getText().toString().trim();
        String companyTitle = jobTitleId2.trim();
        String experience = tvYear.getText().toString().trim();
        String pdescription = et_for_pdescription.getText().toString().trim();

        if (data != 2) {
            if (companyTitle.equalsIgnoreCase("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.pc_title));
            } else if (companyName.equalsIgnoreCase("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.pc_name));
            } else if (experience.isEmpty() && !experience.equals("0")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.select_sdate));
            } else {setData(companyName,companyTitle,pdescription,experience,data);}
        }else {setData(companyName,companyTitle,pdescription,experience, data);}
    }

    private void setData(String companyName, String companyTitle, String pdescription, String experience, int data){

        PreviousRole previousRole = new PreviousRole();
        previousRole.previous_job_title = companyTitle;
        previousRole.previous_company_name = companyName;
        previousRole.experience = experience;

        if (activity.check.equals("Edit")){
            if (roleArrayList.isEmpty()){
                previousRole.previous_role_id = "";
            }else{
                previousRole.previous_role_id = (tempUpdateIndex==-1)?"":roleArrayList.get(tempUpdateIndex).previous_role_id;
                tempUpdateIndex = -1;
            }
        }else {
            previousRole.previous_role_id = "";
        }
        String enCodedStatusCode = null;
        try {
            enCodedStatusCode = URLEncoder.encode(pdescription, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        previousRole.previous_description = enCodedStatusCode==null?"":enCodedStatusCode;

        if (index == -1) {
            if (!et_for_compyName.getText().toString().isEmpty()) {
                roleArrayList.add(previousRole);
            }
            if (roleArrayList.size() == 1) {
                card_for_pRole1.setVisibility(View.VISIBLE);
                tv_for_role1.setText(roleArrayList.get(0).previous_company_name);
            } else if (roleArrayList.size() == 2) {
                card_for_pRole2.setVisibility(View.VISIBLE);
                tv_for_role2.setText(roleArrayList.get(1).previous_company_name);
            } else if (roleArrayList.size() == 3) {
                card_for_pRole3.setVisibility(View.VISIBLE);
                tv_for_role3.setText(roleArrayList.get(2).previous_company_name);
                layout_for_preRole.setVisibility(View.GONE);
            }
        } else {
            switch (index) {
                case 0:
                    roleArrayList.set(index, previousRole);
                    if (roleArrayList.size() == 3) {
                        layout_for_preRole.setVisibility(View.GONE);
                    }
                    card_for_pRole1.setVisibility(View.VISIBLE);
                    tv_for_role1.setText(companyName);
                    index = -1;
                    break;
                case 1:
                    roleArrayList.set(index, previousRole);
                    card_for_pRole2.setVisibility(View.VISIBLE);
                    tv_for_role2.setText(companyName);
                    if (roleArrayList.size() == 3) layout_for_preRole.setVisibility(View.GONE);
                    index = -1;
                    break;
                case 2:
                    roleArrayList.set(index, previousRole);
                    card_for_pRole3.setVisibility(View.VISIBLE);
                    tv_for_role3.setText(roleArrayList.get(2).previous_company_name);
                    if (roleArrayList.size() == 3) layout_for_preRole.setVisibility(View.GONE);
                    index = -1;
                    break;
            }
        }

        tv_for_jobTitle2.setText("");
        et_for_compyName.setText("");
        jobTitleId2 = "";
        et_for_pdescription.setText("");
        sliderExperience.setProgress(0);
    }

    private void gonVisi(int i){
        switch (i){
            case 1:
                layout_for_nextR.setVisibility(View.GONE);
                layout_for_pRole.setVisibility(View.GONE);
                iv_for_nextRole.setImageResource(R.drawable.ic_down_arrow);
                iv_for_previousRole.setImageResource(R.drawable.ic_down_arrow);
                break;
            case 2:
                layout_for_cRole.setVisibility(View.GONE);
                layout_for_pRole.setVisibility(View.GONE);
                iv_for_previousRole.setImageResource(R.drawable.ic_down_arrow);
                iv_for_currentRole.setImageResource(R.drawable.ic_down_arrow);
                break;
            case 3:
                layout_for_cRole.setVisibility(View.GONE);
                layout_for_nextR.setVisibility(View.GONE);
                iv_for_nextRole.setImageResource(R.drawable.ic_down_arrow);
                iv_for_currentRole.setImageResource(R.drawable.ic_down_arrow);
                break;
        }
    }

    private void roleUpdate(int index) throws UnsupportedEncodingException {
        switch (index){
            case 0:
                card_for_pRole1.setVisibility(View.GONE);
                break;
            case 1:
                card_for_pRole2.setVisibility(View.GONE);
                break;
            case 2:
                card_for_pRole3.setVisibility(View.GONE);
                break;
        }

        et_for_compyName.setText(roleArrayList.get(index).previous_company_name);
        tvYear.setText(roleArrayList.get(index).experience);
        int possition = Integer.parseInt(roleArrayList.get(index).experience);
        sliderExperience.setProgress(possition);

        String previous_description = URLDecoder.decode(roleArrayList.get(index).previous_description, "UTF-8");
        et_for_pdescription.setText(previous_description);

        for (int i=0; arrayList3.size() > i ; i ++){
            if (arrayList3.get(i).jobTitleId.equals(roleArrayList.get(index).previous_job_title)){
                tv_for_jobTitle2.setText(arrayList3.get(i).jobTitleName);
                jobTitleId2 = arrayList3.get(i).jobTitleId;
                return;
            }
        }
    }

    private void hideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void validation(){

        cCompanyName = et_for_companyName.getText().toString().trim();
        startDate = tv_for_startD.getText().toString().trim();
        finshdate =  tv_for_finishD.getText().toString().trim();

        if (jobTitleId.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.ctitle));
        }else if (cCompanyName.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.current_cname));
        }else if (startDate.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.c_start_date));
        }else if (!checkBox){
            if (finshdate.equalsIgnoreCase("")){
                MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.c_finish_date));
            }else {otherValidation();}
        }else {otherValidation();}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (IndiEditProfileActivity) context;
        if(activity instanceof EditListener)
            listener = activity;
    }

    private void otherValidation(){

        address = tv_for_address.getText().toString().trim();
        String companyName = et_for_compyName.getText().toString().trim();
        String experience = tvYear.getText().toString().trim();

        if (availability.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.weeks_select));
        }else if (salary.isEmpty()){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.select_salary));
        }else if (employmentType.isEmpty()){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.select_emp_type));
        }else if (interestId.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.select_aofi));
        }else if (address.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.add_location));
        }else if (roleArrayList == null) {
            if (jobTitleId2.equals("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.pjobtitile));
            } else if (companyName.equalsIgnoreCase("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.pcompany_name));
            } else if (experience.isEmpty() && !experience.equals("0")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, getString(R.string.select_sdate));
            } else {
                addPreviousRole(1);
                updateExperience();
            }
        }else {
            addPreviousRole(2);
            updateExperience();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_for_interest:
                JobTitle jobTitles1 = arrayList2.get(position);
                interestId = jobTitles1.jobTitleId;
                break;
            case R.id.sp_for_weeklist:
                Weeks weeks = weekList.get(position);
                availability = weeks.week;
                activity.Isuserfilldata= true;
                break;
            case R.id.sp_for_salary:
                Weeks salarys = salaryList.get(position);
                salary = salarys.id;
                break;
            case R.id.sp_for_empType:
                Weeks empType = empTypeList.get(position);
                employmentType = empType.week;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void datePicker(int i1, int i2, int i3) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        if (i1 != -1) {
            day = i1;
            month = i2 - 1;
            year = i3;
        }
        DatePickerDialog datepickerdialog = new DatePickerDialog(activity, R.style.DefaultNumberPickerTheme, this, year, month, day);

        if (checkMange == 2 | checkMange == 4) {
            String givenDateString;
            if (checkMange == 2) {
                givenDateString = tv_for_startD.getText().toString().trim().replace("-", " ");
                checkMange = -1;
            } else {
                givenDateString = tv_for_startDP.getText().toString().trim().replace("-", " ");
                checkMange = -1;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy", Locale.US);
            try {
                Date mDate = sdf.parse(givenDateString);
                long timeInMilliseconds = mDate.getTime();
                datepickerdialog.getDatePicker().setMinDate(timeInMilliseconds);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        datepickerdialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datepickerdialog.getWindow().setBackgroundDrawableResource(R.color.white);
        datepickerdialog.show();

    }

    private void getlist() {
        cusDialogProg = new CusDialogProg(activity);
        cusDialogProg.show();

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list",false) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("job_title");

                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("jobTitleId");
                            jobTitles.jobTitleName = object.getString("jobTitleName");
                            arrayList.add(jobTitles);
                            arrayList3.add(jobTitles);
                        }
                        spinnerDialog = new SpinnerDialog(activity, arrayList, getString(R.string.area_of_specialty));
                        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                jobTitleId = job.jobTitleId;
                                tv_for_jobTitle.setText(job.jobTitleName);

                            }
                        });
                        spinnerDialog2 = new SpinnerDialog(activity, arrayList3, getString(R.string.area_of_specialty));
                        spinnerDialog2.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                jobTitleId2 = job.jobTitleId;
                                tv_for_jobTitle2.setText(job.jobTitleName);

                            }
                        });

                        final JSONArray speciality = result.getJSONArray("speciality_list");
                        for (int i = 0; i < speciality.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = speciality.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("specializationId");
                            jobTitles.jobTitleName = object.getString("specializationName");
                            arrayList2.add(jobTitles);
                        }
                        spinnerDialog3 = new SpinnerDialog(activity, arrayList2, getString(R.string.area_of_specialtys));
                        spinnerDialog3.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(JobTitle job) {
                                interestId = job.jobTitleId;
                                tv_for_aofs.setText(job.jobTitleName);

                            }
                        });

                        Weeks week0 = new Weeks();
                        week0.week = "";
                        weekList.add(week0);
                        JSONObject week = result.getJSONObject("availability_list");
                        Weeks week5 = new Weeks();
                        week5.week = week.getString("immediate");
                        weekList.add(week5);
                        Weeks week1 = new Weeks();
                        week1.week = week.getString("1-4");
                        weekList.add(week1);
                        Weeks week2 = new Weeks();
                        week2.week = week.getString("5-8");
                        weekList.add(week2);
                        Weeks week3 = new Weeks();
                        week3.week = week.getString("9-12");
                        weekList.add(week3);
                        Weeks week4 = new Weeks();
                        week4.week = week.getString("12+");
                        weekList.add(week4);
                        weekSpAdapter.notifyDataSetChanged();


                        JSONArray jsonArray = result.getJSONArray("salary_range_list");

                        for (int i =0 ; i< jsonArray.length();i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Weeks week6 = new Weeks();
                           if (i==0)week6.week = object.getString(week6.id = "any");
                           if (i==1)week6.week = object.getString(week6.id ="0-20000");
                           if (i==2)week6.week = object.getString(week6.id ="20000-40000");
                           if (i==3)week6.week = object.getString(week6.id ="40000-60000");
                           if (i==4)week6.week = object.getString(week6.id ="60000-80000");
                           if (i==5)week6.week = object.getString(week6.id ="80000-100000");
                           if (i==6)week6.week = object.getString(week6.id ="100000-120000");
                           if (i==7)week6.week = object.getString(week6.id ="120000-150000");
                           if (i==8)week6.week = object.getString(week6.id ="150000-200000");
                           if (i==9)week6.week = object.getString(week6.id ="200000-99999999");
                            salaryList.add(week6);
                        }
                        salarySpAdapter.notifyDataSetChanged();


                        JSONObject emplyementType = result.getJSONObject("emplyement_type");
                        empTypeList.add(week0);
                        Weeks week14 = new Weeks();
                        week14.week = emplyementType.getString("full_time");
                        empTypeList.add(week14);
                        Weeks week15 = new Weeks();
                        week15.week = emplyementType.getString("part_time");
                        empTypeList.add(week15);
                        Weeks week16 = new Weeks();
                        week16.week = emplyementType.getString("casual");
                        empTypeList.add(week16);
                        Weeks week17 = new Weeks();
                        week17.week = emplyementType.getString("contract");
                        empTypeList.add(week17);
                        empTypeSpAdapter.notifyDataSetChanged();

                        showPrefilledData();
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

    public void updateExperience() {
        Gson gson = new GsonBuilder().create();
        final String array = gson.toJson(roleArrayList);
        Log.e("Json",array);

        new VolleyGetPost(activity, AllAPIs.EXPERIENCE, true, "Experience",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("success")) {

                        String jobTitle = tv_for_jobTitle.getText().toString().trim();
                        UserInfo userInfo = Uconnekt.session.getUserInfo();
                        userInfo.jobTitleName = jobTitle;
                        FirebaseLogin firebaseLogin = new FirebaseLogin();
                        firebaseLogin.firebaseLogin(userInfo,activity,false, cusDialogProg,false,false, false);
                        FirebaseDatabase.getInstance().getReference().child("users").child(Uconnekt.session.getUserInfo().userId).child("jobTitleName").setValue(userInfo.jobTitleName);
                        if(listener!=null) listener.onSwitchFragment(2);
                    } else {
                        MyCustomMessage.getInstance(activity).snackbar(mainlayout,message);
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
                params.put("current_job_title", jobTitleId);
                params.put("current_company", cCompanyName);
                params.put("current_start_date", startDate);
                params.put("current_finish_date",  checkBox?"":finshdate);
                String one=(et_for_cdescription!=null)?et_for_cdescription.getText().toString():"";
                String two=(et_for_pdescription!=null)?et_for_pdescription.getText().toString():"";
                String  enCodedStatusCode = null,enCodedStatusCode2 = null;
                try {
                    enCodedStatusCode = URLEncoder.encode(one, "UTF-8");
                    enCodedStatusCode2 = URLEncoder.encode(two, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.put("current_description", enCodedStatusCode == null?"":enCodedStatusCode);
                params.put("previous_description",  enCodedStatusCode2 == null?"":enCodedStatusCode2);
                if (array != null) params.put("previous_role",  array.toString());
                params.put("next_availability", availability);
                params.put("next_speciality", interestId);
                params.put("next_location", address);
                params.put("employementType", employmentType);
                params.put("expectedSalary", salary);
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
        if (Constant.NETWORK_CHECK == 1) {
            getlist();
        }
        Constant.NETWORK_CHECK = 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int mmonth = month + 1;
        switch (setValue){
            case 1:
                tv_for_startD.setText((dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth )+ "-" + (mmonth < 10 ? "0" + mmonth : "" + mmonth) + "-" + year);
                break;
            case 2:
                tv_for_finishD.setText((dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth )+ "-" + (mmonth < 10 ? "0" + mmonth : "" + mmonth) + "-" + year);
                break;
            case 3:
                tv_for_startDP.setText((dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth )+ "-" + (mmonth < 10 ? "0" + mmonth : "" + mmonth) + "-" + year);
                break;
            case 4:
                tv_for_finishDP.setText((dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth )+ "-" + (mmonth < 10 ? "0" + mmonth : "" + mmonth) + "-" + year);
                break;
        }
    }

    private void addressClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(activity);
            startActivityForResult(intent, Constant.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    } // method for address button click

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            layout_for_address.setEnabled(true);

            if (mainlayout != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(mainlayout.getWindowToken(), 0);
            }

            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(activity, data);

                String placename = String.valueOf(place.getAddress());
                tv_for_address.setText(placename);
            }
        }

    } // onActivityResult

}
