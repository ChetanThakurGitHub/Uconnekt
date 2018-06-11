package com.uconnekt.ui.authentication.user_selection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.authentication.login.LoginActivity;
import com.uconnekt.util.Constant;

public class UserSelectionActivity extends AppCompatActivity implements View.OnClickListener,UserSelectionView{

    private RelativeLayout mainlayout;
    private UserSelectionPresenter userSelectionPresenter;
    private String userType = "";
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        initView();
        userSelectionPresenter = new UserSelectionPresenterImpl(this,new UserSelectionIntractorImpl());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_for_employer:
                setResours((LinearLayout) v, true);
                setResours((LinearLayout) findViewById(R.id.layout_for_jobSeeker), false);
                userType = "business";
                break;
            case R.id.layout_for_jobSeeker:
                setResours((LinearLayout) v, true);
                setResours((LinearLayout) findViewById(R.id.layout_for_employer), false);
                userType = "individual";
                break;
            case R.id.btn_for_continue:
                userSelectionPresenter.validationCondition(userType);
                break;
        }
    }

    private void initView(){
        findViewById(R.id.layout_for_employer).setOnClickListener(this);
        findViewById(R.id.layout_for_jobSeeker).setOnClickListener(this);
        findViewById(R.id.btn_for_continue).setOnClickListener(this);
        mainlayout = findViewById(R.id.mainlayout);

    }


    private void setResours(LinearLayout v, boolean isActive) {
        if(isActive){
            ((ImageView) v.getChildAt(0)).setImageResource(v.getId()==R.id.layout_for_employer? R.drawable.ic_appartment_active:R.drawable.ic_job_seekar_ico_active );
            ((TextView) v.getChildAt(1)).setTextColor(getResources().getColor(R.color.white));
            v.setBackgroundResource(R.color.yellow);
        }
        else {
            ((ImageView) v.getChildAt(0)).setImageResource(v.getId()==R.id.layout_for_employer? R.drawable.ic_appartment:R.drawable.ic_job_seekar_ico );
            ((TextView) v.getChildAt(1)).setTextColor(getResources().getColor(R.color.lightgray));
            v.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public void setUserSelectionError() {
        MyCustomMessage.getInstance(this).snackbar(mainlayout,getResources().getString(R.string.user_v));
    }

    @Override
    public void onMoveForward() {
        Intent intent = new Intent(this,LoginActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            MyCustomMessage.getInstance(this).snackbar(mainlayout, getResources().getString(R.string.for_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constant.BackPressed_Exit);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
