package com.uconnekt.ui.common_activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.util.Constant;

public class NetworkActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mainlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        initView();


    }

    private void initView(){
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(R.string.alert);
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        mainlayout = findViewById(R.id.mainlayout);
        findViewById(R.id.btn_for_tryAgain).setOnClickListener(this);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_backIco.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.btn_for_tryAgain:
                if (isNetworkAvailable()){
                    Constant.NETWORK_CHECK = 1;
                    onBackPressed();
                }else {
                    MyCustomMessage.getInstance(this).snackbar(mainlayout,getString(R.string.check_net_connection));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
