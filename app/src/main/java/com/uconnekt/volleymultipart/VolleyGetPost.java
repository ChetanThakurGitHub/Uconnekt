package com.uconnekt.volleymultipart;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uconnekt.R;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.session.Session;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.DatabaseActivity;
import com.uconnekt.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class VolleyGetPost {

    private String TAG;
    private int retryTime=20000;
    private String url;
    private Boolean isMethodPost;
    private Activity activity;
    private CusDialogProg cusDialogProg;
    private Boolean loader;


    private void dismissLoader(){
        if (cusDialogProg != null)cusDialogProg.dismiss();
    }

    public VolleyGetPost(Activity activity, String url,Boolean isMethodPost,String TAG,Boolean loader){
        this.activity=activity;
        this.url=url;
        this.isMethodPost=isMethodPost;
        this.TAG=TAG;
        this.loader = loader;
        if (loader) cusDialogProg = new CusDialogProg(activity);
    }

    public void executeVolley(){
        int methodType=(isMethodPost)?Request.Method.POST:Request.Method.GET;

        if (Utils.isNetworkAvailable(activity)) {
            Utils.hideKeyboard(activity);
            if (loader)cusDialogProg.show();
            StringRequest stringRequest = new StringRequest(methodType, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "onResponse: " + response);
                            dismissLoader();
                            onVolleyResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissLoader();
                            volleyErrorHandle(error);
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    return setHeaders(header);
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    return setParams(params);
                }
            };

            VolleySingleton.getInstance(activity).addToRequestQueue(stringRequest, TAG);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    retryTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }else{
            onNetError();
        }
    }

    private void volleyErrorHandle(VolleyError error){
        NetworkResponse networkResponse = error.networkResponse;
        String errorMessage = "Unknown error";
        if (networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                errorMessage = "Request timeout";
            } else if (error.getClass().equals(NoConnectionError.class)) {
                errorMessage = "Failed to connect server, please try again";
            }
            MyCustomMessage.getInstance(activity).showCustomAlert("Alert",errorMessage);
        } else {
            String result = new String(networkResponse.data);
            try {
                JSONObject response = new JSONObject(result);
                String status = response.getString("responseCode");
                String message = response.getString("message");

                Log.e("Error Status", "" + status);
                Log.e("Error Message", message);

                if (status.equals("300")) {
                    String isActive= response.getString("isActive");
                    if (isActive.equals("0")){  //inactive
                        MyCustomMessage.getInstance(activity).showLogoutAlert(activity.getResources().getString(R.string.session_expired), activity.getResources().getString(R.string.inactive));
                    }else {
                        MyCustomMessage.getInstance(activity).showLogoutAlert(activity.getResources().getString(R.string.session_expired), activity.getResources().getString(R.string.your_session_is_expired_please_login_again));
                    }
                    Session.getInstance().logout(activity);
                    return;
                } else if (networkResponse.statusCode == 404) {
                    errorMessage = "Resource not found";
                }  else if (networkResponse.statusCode == 500) {
                    errorMessage = message + "Oops! Something went wrong";
                }else {
                    errorMessage = ServerResponseCode.getmeesageCode(networkResponse.statusCode);
                }
                activity.startActivity(new Intent(activity, DatabaseActivity.class));
                // MyCustomMessage.getInstance(activity).showCustomAlert("Alert",errorMessage);
            } catch (JSONException e) {
                activity.startActivity(new Intent(activity, DatabaseActivity.class));
                // MyCustomMessage.getInstance(activity).showCustomAlert("Alert",activity.getResources().getString(R.string.something_wrong));
                e.printStackTrace();
            }
        }

    }

    /***
     * @param retryTime set the second for 30 sec pass 30000
     */
    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    /***
     * @param response use this response
     */
    public abstract void onVolleyResponse(String response);

    /**
     * This method will be executed if internet connection is not there
     * Don't forget to dismiss the progressbar in this (if there)
     */
    public abstract void onNetError();

    /***
     * @param params you just need to call params.put(Key,Value)
     * @return params Do not Return null if method is post
     */
    public abstract Map<String, String> setParams(Map<String, String> params);

    /***
     * @param params you just need to call params.put(Key,Value)
     * @return params Do not return null
     */
    public abstract Map<String, String> setHeaders(Map<String, String> params);

}
