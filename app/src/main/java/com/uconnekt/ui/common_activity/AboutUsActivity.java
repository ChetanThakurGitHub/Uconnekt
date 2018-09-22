package com.uconnekt.ui.common_activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initiView();
    }

    private void initiView(){
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(getString(R.string.about_us));
        setDataWithHrefLink();
    }

    private void setDataWithHrefLink(){
        TextView tvHref = findViewById(R.id.tvHref);
        tvHref.setClickable(true);
        tvHref.setMovementMethod(LinkMovementMethod.getInstance());
        //tvHref.setText(Html.fromHtml(getString(R.string.other_href)));
        tvHref.setText(Html.fromHtml("ConnektUs @ 2017–2018 <a href='https://www.connektus.com.au/'> ConnektUs.</a> All Rights Reserved."));

        TextView tvHref2 = findViewById(R.id.tvHref2);
        tvHref2.setClickable(true);
        tvHref2.setMovementMethod(LinkMovementMethod.getInstance());
        //tvHref2.setText(Html.fromHtml(getString(R.string.uconneckt_href)));
        tvHref2.setText(Html.fromHtml("Visit our website: <a href='https://www.connektus.com.au/'> www.connektus.com.au</a> for more about our app."));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
