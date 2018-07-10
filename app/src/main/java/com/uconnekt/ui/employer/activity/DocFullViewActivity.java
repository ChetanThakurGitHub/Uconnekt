
package com.uconnekt.ui.employer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.singleton.MyCustomMessage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DocFullViewActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private ProgressDialog dialog;
    private String url = "",FileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_full_view);
        String rAndC = "";
        url = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) url = bundle.getString("URL");
        if (bundle != null) FileName = bundle.getString("FileName");
        if (bundle != null) rAndC = bundle.getString("RC");
        initView(rAndC);

        webView = findViewById(R.id.webView);
        dialog = new ProgressDialog(DocFullViewActivity.this);
        dialog.setMessage("Loading please wait.....");
        dialog.setCancelable(false);
        initWebView(url);
    }

    private void initView(String rAndC) {
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        ImageView iv_for_download = findViewById(R.id.iv_for_download);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_backIco.setOnClickListener(this);
        iv_for_download.setVisibility(View.VISIBLE);
        iv_for_download.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(rAndC.equals("R") ? "Resume" : "CV");
    }


    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new DocFullViewActivity.MyWebViewClient());
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
            case R.id.iv_for_download:
                new DownloadFileFromURL().execute(url);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File f = new File(Environment.getExternalStorageDirectory(), "Uconnekt/Resumes");
                if (!f.exists()) {
                    f.mkdirs();
                }

                // Output stream
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()
                        + "/Uconnekt/Resumes/"+FileName);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
               e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String file_url) {
            dialog.dismiss();
            MyCustomMessage.getInstance(DocFullViewActivity.this).snackbar(webView,getString(R.string.download_completed));
        }

    }
}
