package com.uconnekt.ui.common_activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ServerValue;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.employer.activity.DocFullViewActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class DocViewActivity extends AppCompatActivity implements View.OnClickListener{

    private WebView webView;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_view);

        Bundle bundle = getIntent().getExtras();
        Boolean check = bundle.getBoolean("title");

        webView = findViewById(R.id.webView);
        dialog = new ProgressDialog(DocViewActivity.this);
        dialog.setMessage("Loading please wait.....");
        dialog.setCancelable(false);

        initView(check);
    }


    private void initView(Boolean check) {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(getString(check?R.string.term_and_conditions:R.string.privacy_policy));
        tandc();

    }

    private void tandc(){
        new VolleyGetPost(this, AllAPIs.TANDC, false, "TANDC", false) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String link = object.getString("data");
                    if (status.equals("success"))initWebView(link);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {

            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken",Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new DocViewActivity.MyWebViewClient());
        webView.loadUrl(url);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowContentAccess(true);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);
    }

    private class MyWebViewClient extends WebViewClient {

        private MyWebViewClient() {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            dialog.dismiss();
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
        switch (v.getId()) {
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
