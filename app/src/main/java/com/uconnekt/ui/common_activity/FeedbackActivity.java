package com.uconnekt.ui.common_activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
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
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private RatingBar ratingBar;
    private String ratingNo = "";
    private Boolean check = false;
    private LinearLayout mainlayout;
    private EditText etSubject,etFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                ratingNo = rating+"";
            }
        });
    }

    private void initView() {
        mainlayout = findViewById(R.id.mainlayout);
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setOnClickListener(this);iv_for_backIco.setVisibility(View.VISIBLE);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(R.string.feedback);
        ratingBar = findViewById(R.id.ratingBar);
        etSubject = findViewById(R.id.etSubject);
        etFeedback = findViewById(R.id.etFeedback);
        final TextView tvNumber = findViewById(R.id.tvNumber);
        findViewById(R.id.tvSubmit).setOnClickListener(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 200 - s.length();
                tvNumber.setText(text+" characters");
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etFeedback.addTextChangedListener(textWatcher);

        EditText etSubject = new EditText(this);
        etSubject.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        EditText etFeedback = new EditText(this);
        etFeedback.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.tvSubmit:
                Validation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void Validation() {
        String subject = etSubject.getText().toString().trim();
        String feedback = etFeedback.getText().toString().trim();
        if (ratingNo.isEmpty()||ratingNo.equals("0.0")){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.add_ratting));
        }else if (subject.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.add_subject));
        }else if (feedback.isEmpty()){
            MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.add_feedback));
        }else {
            if (!check)addFeedback(subject,feedback);
        }
    }

    private void addFeedback(final String subject, final String feedback){
        check = true;
        new VolleyGetPost(this, AllAPIs.ADD_FEEDBACK, true, "ADD_FEEDBACK", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("success")){
                        finish();
                    }else {
                        MyCustomMessage.getInstance(FeedbackActivity.this).snackbar(mainlayout,message);
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
                params.put("rating",ratingNo);
                params.put("message",feedback);
                params.put("subject",subject);
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
