package com.uconnekt.ui.employer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ResumeActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView_resume,webView_cv;
    private ProgressDialog dialog;
    private String user_resume_url = "",user_cv_url = "",user_resume = "",user_cv = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        initView();
        String userId = "";
        Bundle extras = getIntent().getExtras();
        if(extras != null)userId = extras.getString("USERID");
        showPrefilledData(userId);

        dialog = new ProgressDialog(ResumeActivity.this);
        dialog.setMessage("Loading please wait.....");
        dialog.setCancelable(false);
    }

    private void initView() {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);tv_for_tittle.setText(R.string.resume);

        webView_resume = findViewById(R.id.webView_resume);
        webView_cv = findViewById(R.id.webView_cv);

        findViewById(R.id.layout_for_Resume).setOnClickListener(this);
        findViewById(R.id.layout_for_cv).setOnClickListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(String url) {
        webView_resume.setWebViewClient(new MyWebViewClient());
        webView_resume.loadUrl(url);
        webView_resume.getSettings().setBuiltInZoomControls(true);
        webView_resume.getSettings().setSupportZoom(true);
        webView_resume.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView_resume.getSettings().setJavaScriptEnabled(true);
        webView_resume.getSettings().setAllowFileAccess(true);
        webView_resume.getSettings().setDomStorageEnabled(true);
        webView_resume.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView_resume.getSettings().setLoadWithOverviewMode(true);
        webView_resume.getSettings().setUseWideViewPort(true);
        webView_resume.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView_resume.getSettings().setAllowContentAccess(true);
        webView_resume.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView2(String url) {
        webView_cv.setWebViewClient(new MyWebViewClient());
        webView_cv.loadUrl(url);
        webView_cv.getSettings().setBuiltInZoomControls(true);
        webView_cv.getSettings().setSupportZoom(true);
        webView_cv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView_cv.getSettings().setJavaScriptEnabled(true);
        webView_cv.getSettings().setAllowFileAccess(true);
        webView_cv.getSettings().setDomStorageEnabled(true);
        webView_cv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView_cv.getSettings().setLoadWithOverviewMode(true);
        webView_cv.getSettings().setUseWideViewPort(true);
        webView_cv.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView_cv.getSettings().setAllowContentAccess(true);
        webView_cv.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
    }

    private class MyWebViewClient extends WebViewClient {

        private MyWebViewClient() {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
           if (dialog != null)dialog.dismiss();
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.layout_for_Resume:
                Intent intent = new Intent(ResumeActivity.this,DocFullViewActivity.class);
                intent.putExtra("URL",user_resume_url);
                intent.putExtra("FileName",user_resume);
                intent.putExtra("RC","R");
                startActivity(intent);
                break;
            case R.id.layout_for_cv:
                intent = new Intent(ResumeActivity.this,DocFullViewActivity.class);
                intent.putExtra("URL",user_cv_url);
                intent.putExtra("FileName",user_cv);
                intent.putExtra("RC","C");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showPrefilledData(String userId){
        new VolleyGetPost(this, AllAPIs.INDI_PROFILE+userId, false, "getUserPersonalInfo", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject object = jsonObject.getJSONObject("resume");
                        user_resume = object.getString("user_resume");
                        user_cv = object.getString("user_cv");
                        user_resume_url = object.getString("user_resume_url");
                        user_cv_url = object.getString("user_cv_url");

                        if (!user_resume_url.isEmpty()){initWebView(user_resume_url);setLayoutVisibility(0);}
                        if (!user_cv_url.isEmpty()){initWebView2(user_cv_url);setLayoutVisibility(1);}
                        if (user_resume_url.isEmpty() && user_cv_url.isEmpty())setLayoutVisibility(2);
                    }

                } catch (JSONException e) {
                    setLayoutVisibility(2);
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                setLayoutVisibility(2);
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

    private void setLayoutVisibility(int i){
        switch (i){
            case 0:
                findViewById(R.id.tv_for_resume).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_for_Resume).setVisibility(View.VISIBLE);
                webView_resume.setVisibility(View.VISIBLE);
                break;
            case 1:
                findViewById(R.id.tv_for_cv).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_for_cv).setVisibility(View.VISIBLE);
                webView_cv.setVisibility(View.VISIBLE);
                break;
            case 2:
                findViewById(R.id.layout_for_noData).setVisibility(View.VISIBLE);
                break;
        }
    }

}
