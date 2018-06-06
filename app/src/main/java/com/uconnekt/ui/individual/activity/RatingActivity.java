package com.uconnekt.ui.individual.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class RatingActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_for_comment;
    private TextView tv_for_txt;
    private RatingBar ratingBar;
    private String ratingNo = "",userId = "";
    private ImageView iv_for_checkBox;
    private Boolean checkUncheck = false;
    private LinearLayout mainlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Constant.API = 1;

        Bundle extras = getIntent().getExtras();
        if(extras != null) userId = extras.getString("USERID");
        initView();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 200 - s.length();
                tv_for_txt.setText(text+" characters");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        et_for_comment.addTextChangedListener(textWatcher);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                ratingNo = rating+"";
            }
        });
    }

    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        findViewById(R.id.layout_for_checkBox).setOnClickListener(this);
        findViewById(R.id.tv_for_submit).setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.rate);
        et_for_comment = findViewById(R.id.et_for_comment);
        tv_for_txt = findViewById(R.id.tv_for_txt);
        ratingBar = findViewById(R.id.ratingBar);
        iv_for_checkBox = findViewById(R.id.iv_for_checkBox);
        mainlayout = findViewById(R.id.mainlayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.tv_for_submit:
                Validation();
                break;
            case R.id.layout_for_checkBox:
                if (!checkUncheck){
                    iv_for_checkBox.setImageResource(R.drawable.ic_checked_rate);
                    checkUncheck = true;
                }else {
                    iv_for_checkBox.setImageResource(R.drawable.ic_uncheck_rate);
                    checkUncheck = false;
                }
                break;
        }
    }

    private void Validation() {
        if (ratingNo.isEmpty()||ratingNo.equals("0.0")){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.add_ratting));
        }else if (et_for_comment.getText().toString().trim().equals("")){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.add_comment));
        }else {
            addRating();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void addRating(){
        new VolleyGetPost(this, AllAPIs.REVIEW, true, "Rating", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                     String status = jsonObject.getString("status");
                     String message = jsonObject.getString("message");
                     if (status.equals("success")){
                         finish();
                     }else {
                         MyCustomMessage.getInstance(RatingActivity.this).snackbar(mainlayout,message);
                     }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(RatingActivity.this, NetworkActivity.class));
            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("review_for",userId);
                params.put("rating",ratingNo);
                params.put("comments",et_for_comment.getText().toString().trim());
                params.put("is_anonymous",checkUncheck?"1":"0");
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
