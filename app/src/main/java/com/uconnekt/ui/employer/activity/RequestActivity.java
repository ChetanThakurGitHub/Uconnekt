package com.uconnekt.ui.employer.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.uconnekt.R;
import com.uconnekt.adapter.CustomSpAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.model.FullChatting;
import com.uconnekt.chat.model.Interview;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.model.JobTitle;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.util.Utils;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class RequestActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener ,DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Spinner sp_for_type;
    private TextView tv_for_name,tv_for_date,tv_for_address,tv_for_time;
    private ArrayList<JobTitle> arrayList = new ArrayList<>();
    private String type = "",userId = "",chatNode = "";
    private RelativeLayout layout_for_address;
    private Double latitude,longitude;
    private LinearLayout mainlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        initView();
        addVelues();
        Bundle extras = getIntent().getExtras();
        if(extras != null)userId = extras.getString("USERID");
        if(extras != null)chatNode = extras.getString("NODE");
    }

    private void addVelues(){
        for (int i = 0 ; i <= 2 ; i++) {
            JobTitle jobTitle = new JobTitle();
            if (i ==0 )jobTitle.jobTitleName = "";
            if (i ==1 )jobTitle.jobTitleName = "Employer";
            if (i ==2 )jobTitle.jobTitleName = "Recruiter";
            arrayList.add(jobTitle);
        }
        CustomSpAdapter customSpAdapter = new CustomSpAdapter(this, arrayList, R.layout.custom_sp_select);
        sp_for_type.setAdapter(customSpAdapter);
        customSpAdapter.notifyDataSetChanged();
        sp_for_type.setOnItemSelectedListener(this);
    }

    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setOnClickListener(this);iv_for_backIco.setVisibility(View.VISIBLE);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(R.string.request_interview);
        sp_for_type = findViewById(R.id.sp_for_type);
        tv_for_name = findViewById(R.id.tv_for_name);
        mainlayout = findViewById(R.id.mainlayout);
        tv_for_date = findViewById(R.id.tv_for_date);
        tv_for_address = findViewById(R.id.tv_for_address);
        tv_for_time = findViewById(R.id.tv_for_time);
        layout_for_address = findViewById(R.id.layout_for_address);
        findViewById(R.id.layout_for_date).setOnClickListener(this);
        findViewById(R.id.layout_for_time).setOnClickListener(this);
        layout_for_address.setOnClickListener(this);
        findViewById(R.id.btn_for_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.layout_for_date:
                datePicker(-1, -1, -1);
                break;
            case R.id.layout_for_time:
                timeDialogue();
                break;
            case R.id.layout_for_address:
                PermissionAll permissionAll = new PermissionAll();
                if (permissionAll.checkLocationPermission(this))addressClick();
                break;
            case R.id.btn_for_send:
                validation();
                break;
        }
    }

    private void validation(){
        String fullName = tv_for_name.getText().toString().trim();
        String date = tv_for_date.getText().toString().trim();
        String time = tv_for_time.getText().toString().trim();
        String address = tv_for_address.getText().toString().trim();
        if (type.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.interviewer_type));
        }else if (fullName.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.interviewer_name));
        }else if (date.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.date_V));
        }else if (time.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.time_v));
        }else if (address.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.location_V));
        }else {
            sendInterviewRequest(fullName,date,time,address);
        }
    }

    private void addressClick() {
        try {
            layout_for_address.setEnabled(false);
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
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
            if (layout_for_address != null) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(layout_for_address.getWindowToken(), 0);
            }
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                String placename = String.valueOf(place.getAddress());
                tv_for_address.setText(placename);
            }
        }
    } // onActivityResult

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_for_type:
                JobTitle jobTitle = arrayList.get(position);
                type = jobTitle.jobTitleName;
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
        DatePickerDialog datepickerdialog = new DatePickerDialog(this, R.style.DefaultNumberPickerTheme, this, year, month, day);

        datepickerdialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datepickerdialog.getWindow().setBackgroundDrawableResource(R.color.white);
        datepickerdialog.show();
    }

    private void timeDialogue() {
        Time time = new Time(System.currentTimeMillis());
        int hour = time.getHours();
        int minute = time.getMinutes();
        TimePickerDialog timePicker = new TimePickerDialog(this, R.style.DefaultNumberPickerTheme, this, hour, minute, DateFormat.is24HourFormat(this));
        timePicker.updateTime(hour, minute);
        timePicker.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int mmonth = month + 1;
        tv_for_date.setText(year + "-" + (mmonth < 10 ? "0" + mmonth : "" + mmonth) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth ));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Calendar datetime = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        Date selectedDate, currentDate;
        selectedDate = Utils.getDateFormat(tv_for_date.getText().toString(),"yyyy-MM-dd");

        currentDate = Calendar.getInstance().getTime();
        String[] selected = String.valueOf(selectedDate).split(" ");
        String[] cDate = String.valueOf(currentDate).split(" ");

        String Date1 = selected[0]+" "+selected[1]+" "+selected[2];
        String Date2 = cDate[0]+" "+cDate[1]+" "+cDate[2];
        String time = Utils.format12HourTime(hourOfDay+":"+minute,"HH:mm","hh:mm a");
        if (Date1.contains(Date2)) {
            // it's same
            if (datetime.getTimeInMillis() > c.getTimeInMillis()) {
                tv_for_time.setText(time);
            } else {
                // it's before current
                Toast.makeText(this, "You can't select previous time", Toast.LENGTH_SHORT).show();
                timeDialogue();
            }
        }else {
            tv_for_time.setText(time);
        }
    }

    private void sendInterviewRequest(final String fullName, final String date, final String time, final String address){
        new VolleyGetPost(this, AllAPIs.REQUEST_INTERVIEW, true, "sendInterviewRequest", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    JSONObject object = jsonObject.getJSONObject("data");
                    String interviewId = object.getString("interviewId");
                    if (status.equalsIgnoreCase("success")) {
                        sendDataOnFirebase(address,date,time,interviewId);
                        Toast.makeText(RequestActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();

                       /* "status":"success",
                                "message":"Interview request successfully done.",
                                "data":{
                            "interviewId":"12",
                                    "request_id":"8",
                                    "type":"Recruiter",
                                    "interviewer_name":"gbnnn",
                                    "location":"Gujarat, India",
                                    "latitude":"22.258652",
                                    "longitude":"71.1923805",
                                    "date":"2018-07-26",
                                    "time":"03:14 AM",
                                    "interview_status":"0",
                                    "request_for":"312",
                                    "request_by":"349",
                                    "request_offer_status":"0",
                                    "is_finished":"0"
                        }*/

                    }else {
                        Toast.makeText(RequestActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("type",type);
                params.put("interviewerName",fullName);
                params.put("location",address);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("date",date);
                params.put("time",time);
                params.put("requestedFor",userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void sendDataOnFirebase(String address, String date, String time, String interviewId){
        FullChatting chatModel = new FullChatting();
        chatModel.message = "";
        chatModel.timeStamp = ServerValue.TIMESTAMP;
        chatModel.userId = Uconnekt.session.getUserInfo().userId;
        chatModel.date = date;
        chatModel.time = time;
        chatModel.location = address;

        FirebaseDatabase.getInstance().getReference().child("chat_rooms/" + chatNode).push().setValue(chatModel);
        Interview interview = new Interview();
        interview.interviewId = interviewId;
        FirebaseDatabase.getInstance().getReference().child("interview/" + userId).setValue(interview);
    }

}
