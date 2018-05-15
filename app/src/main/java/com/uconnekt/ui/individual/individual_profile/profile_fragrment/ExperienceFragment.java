package com.uconnekt.ui.individual.individual_profile.profile_fragrment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.uconnekt.R;
import com.uconnekt.adapter.CustomSpAdapter;
import com.uconnekt.adapter.WeekSpAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.JobTitle;
import com.uconnekt.model.PreviousRole;
import com.uconnekt.model.Weeks;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.individual_profile.activity.JobProfileActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.uconnekt.util.Constant.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.uconnekt.util.Constant.RESULT_OK;

public class ExperienceFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,DatePickerDialog.OnDateSetListener {

    private JobProfileActivity activity;
    private CustomSpAdapter customSpAdapter,specialitySpAdapter;
    private WeekSpAdapter weekSpAdapter;
    private ArrayList<JobTitle> arrayList,arrayList2;
    private ArrayList<PreviousRole> roleArrayList;
    private ArrayList<Weeks> weekList;
    private Spinner sp_for_jobTitle,sp_for_weeklist,sp_for_interest;
    private LinearLayout mainlayout,layout_for_cRole,layout_for_nextR,layout_for_pRole,layout_for_preRole;
    private TabLayout tablayout;
    private String jobTitleId = "",interestId = "",availability = "";
    private int setValue = -1,index = -1,checkMange = -1;
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText et_for_companyName,et_for_cdescription,et_for_pdescription,et_for_compyName,et_for_compyTitle;
    private TextView tv_for_startD,tv_for_finishD,tv_for_txt,tv_for_address,tv_for_startDP,tv_for_finishDP,
            tv_for_txt2,tv_for_role1,tv_for_role2,tv_for_role3;
    private ImageView iv_for_checkBox,iv_for_currentRole,iv_for_nextRole,iv_for_previousRole;
    private Boolean checkBox = false,opnClo = false,opnClo2 = false,opnClo3 =false;
    private PermissionAll permissionAll;
    private RelativeLayout layout_for_address;
    private CardView card_for_pRole1,card_for_pRole2,card_for_pRole3;

    public ExperienceFragment() {
        // Required empty public constructor
    }

    public static ExperienceFragment newInstance() {
        return new ExperienceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_experience, container, false);
        initView(view);

        roleArrayList = new ArrayList<>();

        permissionAll = new PermissionAll();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (permissionAll.checkLocationPermission(activity))location();

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        weekList = new ArrayList<>();
        customSpAdapter = new CustomSpAdapter(activity, arrayList,R.layout.custom_sp);
        specialitySpAdapter = new CustomSpAdapter(activity, arrayList2,R.layout.custom_sp_speciality);
        weekSpAdapter = new WeekSpAdapter(activity, weekList);
        sp_for_jobTitle.setAdapter(customSpAdapter);
        sp_for_interest.setAdapter(specialitySpAdapter);
        sp_for_weeklist.setAdapter(weekSpAdapter);

        getlist();

        sp_for_jobTitle.setOnItemSelectedListener(this);
        sp_for_interest.setOnItemSelectedListener(this);
        sp_for_weeklist.setOnItemSelectedListener(this);

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


    private void initView(View view) {
        view.findViewById(R.id.mainlayout).setOnClickListener(this);
        view.findViewById(R.id.layout_for_startD).setOnClickListener(this);
        view.findViewById(R.id.layout_for_finishD).setOnClickListener(this);
        view.findViewById(R.id.layout_for_stillThere).setOnClickListener(this);
        view.findViewById(R.id.layout_for_address).setOnClickListener(this);
        view.findViewById(R.id.layout_for_startDP).setOnClickListener(this);
        view.findViewById(R.id.layout_for_finishDP).setOnClickListener(this);
        view.findViewById(R.id.layout_for_currentRole).setOnClickListener(this);
        view.findViewById(R.id.layout_for_nextRole).setOnClickListener(this);
        view.findViewById(R.id.layout_for_previousRole).setOnClickListener(this);
        view.findViewById(R.id.iv_for_add).setOnClickListener(this);
        sp_for_jobTitle = view.findViewById(R.id.sp_for_jobTitle);
        card_for_pRole2 = view.findViewById(R.id.card_for_pRole2);
        tv_for_role3 = view.findViewById(R.id.tv_for_role3);
        card_for_pRole3 = view.findViewById(R.id.card_for_pRole3);
        tv_for_role2 = view.findViewById(R.id.tv_for_role2);
        tv_for_role1 = view.findViewById(R.id.tv_for_role1);
        card_for_pRole1 = view.findViewById(R.id.card_for_pRole1);
        layout_for_preRole = view.findViewById(R.id.layout_for_preRole);
        et_for_compyTitle = view.findViewById(R.id.et_for_compyTitle);
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
        sp_for_interest = view.findViewById(R.id.sp_for_interest);
        iv_for_checkBox = view.findViewById(R.id.iv_for_checkBox);
        et_for_cdescription = view.findViewById(R.id.et_for_cdescription);
        sp_for_weeklist = view.findViewById(R.id.sp_for_weeklist);
        mainlayout = view.findViewById(R.id.mainlayout);
        et_for_companyName = view.findViewById(R.id.et_for_companyName);
        tablayout = activity.findViewById(R.id.tablayout);
        activity.findViewById(R.id.btn_for_next).setOnClickListener(this);
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
                        MyCustomMessage.getInstance(activity).snackbar(mainlayout,"You can't select finish date because you are still there");
                    }
                }else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select start date first");
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
                addressClick();
                break;
            case R.id.btn_for_next:
                validation();
                break;
            case R.id.layout_for_startDP:
                datePicker(-1,-1,-1);
                setValue = 3;
                break;
            case R.id.layout_for_finishDP:
                if (!tv_for_startDP.getText().toString().trim().equalsIgnoreCase("")) {
                    setValue = 4;
                    checkMange = 4;
                    datePicker(-1, -1, -1);
                }else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select start date first");
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
                roleUpdate(0);
                index = 0;
                break;
            case R.id.card_for_pRole2:
                layout_for_preRole.setVisibility(View.VISIBLE);
                roleUpdate(1);
                index = 1;
                break;
            case R.id.card_for_pRole3:
                layout_for_preRole.setVisibility(View.VISIBLE);
                roleUpdate(2);
                index = 2;
                break;
        }
    }

    private void addPreviousRole(int data){
        String companyName = et_for_compyName.getText().toString().trim();
        String companyTitle = et_for_compyTitle.getText().toString().trim();
        String startDateP = tv_for_startDP.getText().toString().trim();
        String finishDateP = tv_for_finishDP.getText().toString().trim();
        String pdescription = et_for_pdescription.getText().toString().trim();

       if (data != 2) {
           if (companyTitle.equalsIgnoreCase("")) {
               MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please add previous company title");
           } else if (companyName.equalsIgnoreCase("")) {
               MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please add previous company name");
           } else if (startDateP.equalsIgnoreCase("")) {
               MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please select previous role start date");
           } else if (finishDateP.equalsIgnoreCase("")) {
               MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please select previous role finish date");
           } else if (startDateP.equalsIgnoreCase(finishDateP)) {
               MyCustomMessage.getInstance(activity).snackbar(mainlayout, "start date and finish date con't be same");
           }else {
               setData(companyName,companyTitle,startDateP,finishDateP,pdescription);
           }
       }else {
           setData(companyName,companyTitle,startDateP,finishDateP,pdescription);
       }

    }

    private void setData(String companyName, String companyTitle, String startDateP, String finishDateP, String pdescription){


        PreviousRole previousRole = new PreviousRole();
        previousRole.previous_job_title = companyTitle;
        previousRole.previous_company_name = companyName;
        previousRole.previous_start_date = startDateP;
        previousRole.previous_finish_date = finishDateP;
        previousRole.previous_description = pdescription;

        if (index == -1) {
            roleArrayList.add(previousRole);
            if (roleArrayList.size() == 1) {
                card_for_pRole1.setVisibility(View.VISIBLE);
                tv_for_role1.setText(companyName);
            } else if (roleArrayList.size() == 2) {
                card_for_pRole2.setVisibility(View.VISIBLE);
                tv_for_role2.setText(companyName);
            } else if (roleArrayList.size() == 3) {
                card_for_pRole3.setVisibility(View.VISIBLE);
                tv_for_role3.setText(companyName);
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
                    tv_for_role3.setText(companyName);
                    if (roleArrayList.size() == 3) layout_for_preRole.setVisibility(View.GONE);
                    index = -1;
                    break;
            }
        }

        et_for_compyName.setText("");
        et_for_compyTitle.setText("");
        tv_for_startDP.setText("");
        tv_for_finishDP.setText("");
        et_for_pdescription.setText("");
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

    private void roleUpdate(int index){

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
        et_for_compyTitle.setText(roleArrayList.get(index).previous_job_title);
        tv_for_startDP.setText(roleArrayList.get(index).previous_start_date);
        tv_for_finishDP.setText(roleArrayList.get(index).previous_finish_date);
        et_for_pdescription.setText(roleArrayList.get(index).previous_description);

       /* String companyName = et_for_compyName.getText().toString().trim();
        String companyTitle = et_for_compyTitle.getText().toString().trim();
        String startDateP = tv_for_startDP.getText().toString().trim();
        String finishDateP = tv_for_finishDP.getText().toString().trim();
        String pdescription = et_for_pdescription.getText().toString().trim();

        if (companyTitle.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select company title");
        }else if (companyName.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select company name");
        }else if (startDateP.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select start date");
        }else if (finishDateP.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select finish date");
        }else if (startDateP.equalsIgnoreCase(finishDateP)){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"start date and finish date con't be same");
        }else {

            PreviousRole previousRole = new PreviousRole();
            previousRole.previous_job_title = companyTitle;
            previousRole.previous_company_name = companyName;
            previousRole.previous_start_date = startDateP;
            previousRole.previous_finish_date = finishDateP;
            previousRole.previous_description = pdescription;

            switch (index){
                case 0:
                    roleArrayList.add(index,previousRole);
                    card_for_pRole1.setVisibility(View.VISIBLE);
                    tv_for_role1.setText(companyName);
                    break;
                case 1:
                    roleArrayList.add(index,previousRole);
                    card_for_pRole2.setVisibility(View.VISIBLE);
                    tv_for_role2.setText(companyName);
                    break;
                case 2:
                    roleArrayList.add(index,previousRole);
                    card_for_pRole3.setVisibility(View.VISIBLE);
                    tv_for_role3.setText(companyName);
                    break;
            }


            MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Your previous role information added");
            et_for_compyName.setText("");
            et_for_compyTitle.setText("");
            tv_for_startDP.setText("");
            tv_for_finishDP.setText("");
            et_for_pdescription.setText("");*/
        //}
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

        String cCompanyName = et_for_companyName.getText().toString().trim();
        String startDate = tv_for_startD.getText().toString().trim();
        String finshdate =  tv_for_finishD.getText().toString().trim();

        if (jobTitleId.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select current job title");
        }else if (cCompanyName.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please add current company name");
        }else if (startDate.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select current role start date");
        }else if (!checkBox){
            if (finshdate.equalsIgnoreCase("")){
                MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select current role finish date");
            }else {
                otherValidation(cCompanyName,startDate,finshdate);
            }
        }else {
            otherValidation(cCompanyName, startDate, finshdate);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobProfileActivity) context;
    }

    private void otherValidation(String cCompanyName, String startDate, String finshdate){

        String address = tv_for_address.getText().toString().trim();
        String companyName = et_for_compyName.getText().toString().trim();
        String companyTitle = et_for_compyTitle.getText().toString().trim();
        String startDateP = tv_for_startDP.getText().toString().trim();
        String finishDateP = tv_for_finishDP.getText().toString().trim();

        if (availability.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select availability in weeks");
        }else if (interestId.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please select area of interest in next role");
        }else if (address.equalsIgnoreCase("")){
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,"Please add address");
        }else if (roleArrayList == null) {

            if (companyTitle.equalsIgnoreCase("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please add previous job title");
            } else if (companyName.equalsIgnoreCase("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please add previous company name");
            } else if (startDateP.equalsIgnoreCase("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please select previous role start date");
            } else if (finishDateP.equalsIgnoreCase("")) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, "Please select previous role finish date");
            } else if (startDateP.equalsIgnoreCase(finishDateP)) {
                MyCustomMessage.getInstance(activity).snackbar(mainlayout, "start date and finish date con't be same");
            } else {
                addPreviousRole(1);
                updateExperience(cCompanyName, startDate, finshdate, address);

            }
        }else {
            addPreviousRole(2);
            updateExperience(cCompanyName, startDate, finshdate, address);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_for_jobTitle:
                JobTitle jobTitles = arrayList.get(position);
                jobTitleId = jobTitles.jobTitleId;
                break;
            case R.id.sp_for_interest:
                JobTitle jobTitles1 = arrayList.get(position);
                interestId = jobTitles1.jobTitleId;
                break;
            case R.id.sp_for_weeklist:
                Weeks weeks = weekList.get(position);
                availability = weeks.week;
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
            }else {
                givenDateString = tv_for_startDP.getText().toString().trim().replace("-", " ");
                checkMange = -1;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy",Locale.US);
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

        new VolleyGetPost(activity, AllAPIs.EMPLOYER_PROFILE, false, "list",true) {

            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        arrayList.clear();
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray results = result.getJSONArray("job_title");
                        JobTitle jobTitle = new JobTitle();
                        jobTitle.jobTitleId = "";
                        jobTitle.jobTitleName = "";
                        arrayList.add(jobTitle);
                        arrayList2.add(jobTitle);
                        for (int i = 0; i < results.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = results.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("jobTitleId");
                            jobTitles.jobTitleName = object.getString("jobTitleName");
                            arrayList.add(jobTitles);
                        }
                        customSpAdapter.notifyDataSetChanged();

                        JSONArray speciality = result.getJSONArray("speciality_list");
                        for (int i = 0; i < speciality.length(); i++) {
                            JobTitle jobTitles = new JobTitle();
                            JSONObject object = speciality.getJSONObject(i);
                            jobTitles.jobTitleId = object.getString("specializationId");
                            jobTitles.jobTitleName = object.getString("specializationName");
                            arrayList2.add(jobTitles);
                        }
                        specialitySpAdapter.notifyDataSetChanged();

                        Weeks week0 = new Weeks();
                        week0.week = "";
                        weekList.add(week0);
                        JSONObject week = result.getJSONObject("availability_list");
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

    private void updateExperience(final String cCompanyName, final String startDate, final String finshdate, final String address) {

        Gson gson = new GsonBuilder().create();
        final JsonArray array = gson.toJsonTree(roleArrayList).getAsJsonArray();

        new VolleyGetPost(activity, AllAPIs.EXPERIENCE, true, "Experience",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("success")) {

                        if (tablayout.getSelectedTabPosition() < 2) {
                            tablayout.getTabAt(tablayout.getSelectedTabPosition() + 1).select();
                        }

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
                params.put("current_finish_date",  finshdate);
                params.put("current_description",  et_for_cdescription.getText().toString());
                params.put("previous_description",  et_for_pdescription.getText().toString());
                params.put("previous_role",  array.toString());
                params.put("next_availability", availability);
                params.put("next_speciality", interestId);
                params.put("next_location", address);
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
                tv_for_startD.setText(dayOfMonth + "-" + mmonth + "-" + year);
                break;
            case 2:
                tv_for_finishD.setText(dayOfMonth + "-" + mmonth + "-" + year);
                break;
            case 3:
                tv_for_startDP.setText(dayOfMonth + "-" + mmonth + "-" + year);
                break;
            case 4:
                tv_for_finishDP.setText(dayOfMonth + "-" + mmonth + "-" + year);
                break;
        }
    }

    /*location zoom start*/
    private void latlong(Double latitude, Double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        String address = addresses.get(0).getAddressLine(0);
       /* String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();*/

        tv_for_address.setText(address);

    } // latlog to address find

    private void addressClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(activity);
            startActivityForResult(intent, Constant.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    } // method for address button click

    private void location() {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionAll.checkLocationPermission(activity);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Double latitude = Double.valueOf(String.valueOf(location.getLatitude()));
                                Double longitude = Double.valueOf(String.valueOf(location.getLongitude()));

                                try {
                                    latlong(latitude, longitude);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

    }

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

                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;


                String placename = String.valueOf(place.getAddress());
                tv_for_address.setText(placename);
            }
        }

    } // onActivityResult

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        location();
                    }
                } else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.parmission));
                }
            }
            break;
        }
    }
      /*location zoom end*/

}
