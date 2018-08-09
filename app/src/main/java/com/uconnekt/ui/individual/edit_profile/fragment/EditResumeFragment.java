package com.uconnekt.ui.individual.edit_profile.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.resume_picker.models.SortingTypes;
import com.uconnekt.resume_picker.utils.Orientation;
import com.uconnekt.resume_picker.view.FilePickerBuilder;
import com.uconnekt.resume_picker.view.FilePickerConst;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.AppHelper;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.volleymultipart.VolleyMultipartRequest;
import com.uconnekt.volleymultipart.VolleySingleton;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EditResumeFragment extends Fragment implements View.OnClickListener {

    private IndiEditProfileActivity activity;
    private CusDialogProg cusDialogProg;
    private LinearLayout mainlayout;
    private File myFile,myFile2;
    private PermissionAll permissionAll;
    private int setName = -1;
    private TextView tv_for_resume,tv_for_cv;
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_edit, container, false);

        initView(view);
        cusDialogProg = new CusDialogProg(activity);
        permissionAll = new PermissionAll();
        permissionAll.checkWriteStoragePermission(activity);
        showPrefilledData();

        return view;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void initView(View view) {
        view.findViewById(R.id.mainlayout).setOnClickListener(this);
        view.findViewById(R.id.card_for_resume).setOnClickListener(this);
        view.findViewById(R.id.card_for_cv).setOnClickListener(this);
        mainlayout = view.findViewById(R.id.mainlayout);
        tv_for_resume = view.findViewById(R.id.tv_for_resume);
        tv_for_cv = view.findViewById(R.id.tv_for_cv);

    }

    private void showPrefilledData(){
        new VolleyGetPost(activity, AllAPIs.SHOW_PREFILLED_DATA, false, "showPrefilledData", false) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject object = jsonObject.getJSONObject("resume");
                        String user_resume = object.getString("user_resume");
                        String user_cv = object.getString("user_cv");

                        tv_for_resume.setText(user_resume);
                        tv_for_cv.setText(user_cv);
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
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    public void onSubmit(){
        activity.Isuserfilldata = false;
        if (!activity.Isuserfilldata) {
            if (!activity.check.equals("Edit")) {
                startActivity(new Intent(getActivity(), JobHomeActivity.class));
               // activity.Isuserfilldata=true;
            }
            activity.finish();
        }else {
            MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.resume_validation));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (IndiEditProfileActivity) context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_for_next:
                if (Uconnekt.session.getUserInfo().isProfile.equals("1")) {
                    startActivity(new Intent(getActivity(), JobHomeActivity.class));
                    activity.finish();
                }else {
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.first));
                }
                break;
            case R.id.card_for_resume:
                permissionAll.checkWriteStoragePermission(activity);
                if (permissionAll.chackCameraPermission(activity)) onPickDoc();
                setName =1;
                break;
            case R.id.card_for_cv:
                permissionAll.checkWriteStoragePermission(activity);
                if (permissionAll.chackCameraPermission(activity)) onPickDoc();
                setName = 2;
                break;
        }
    }

    public void onPickDoc() {
        String[] zips = { ".docx" };
        String[] pdfs = { ".pdf" };
        int MAX_ATTACHMENT_COUNT = 1;
        int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(activity, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select doc")
                    .addFileSupport("PDF", pdfs, R.drawable.icon_file_pdf)
                    .addFileSupport("Docx", zips)
                    .enableDocSupport(false)
                    .enableSelectAll(true)
                    .sortDocumentsBy(SortingTypes.name)
                    .withOrientation(Orientation.UNSPECIFIED)
                    .pickFile(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

                    if (docPaths != null) {
                        String uriString = docPaths.toString();
                        String mainString = uriString.substring(1, uriString.length() - 1);
                        if (setName == 1) {
                            myFile = new File(mainString);
                            setName = -1;
                            docPaths.clear();
                            if (myFile != null) {
                                long fileSizeInBytes = myFile.length();
                                long fileSizeInKB = fileSizeInBytes / 1024;
                                long fileSizeInMB = fileSizeInKB / 1024;
                                if (fileSizeInMB > 12) {
                                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.resume_v));
                                }else {
                                    tv_for_resume.setText(myFile.getName());
                                    sendFile();
                                }
                            }
                        } else if (setName == 2) {
                            myFile2 = new File(mainString);
                            setName = -1;
                            docPaths.clear();
                            if (myFile2 != null) {
                                long fileSizeInBytes = myFile2.length();
                                long fileSizeInKB = fileSizeInBytes / 1024;
                                long fileSizeInMB = fileSizeInKB / 1024;
                                if (fileSizeInMB > 12) {
                                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,getString(R.string.resume_v));
                                }else {
                                    tv_for_cv.setText(myFile2.getName());
                                    sendFile();
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.NETWORK_CHECK = 0;
    }

    private void sendFile() {
        if (isNetworkAvailable()) {
            cusDialogProg.show();
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllAPIs.RESUME_CV, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            cusDialogProg.dismiss();
                            MyCustomMessage.getInstance(activity).snackbar(mainlayout,message);
                        } else {
                            MyCustomMessage.getInstance(activity).snackbar(mainlayout,message);
                            cusDialogProg.dismiss();
                        }

                    } catch (Exception t) {
                        t.printStackTrace();
                        cusDialogProg.dismiss();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    MyCustomMessage.getInstance(activity).snackbar(mainlayout,networkResponse+"");
                    cusDialogProg.dismiss();
                    error.printStackTrace();
                }
            }) {

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if (myFile != null) {
                        try {
                            params.put("resume", new DataPart(myFile.getName(), AppHelper.convertFileToByteArray(myFile), "pdf/docx"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if (myFile2 != null){
                        try {
                            params.put("cv", new DataPart(myFile2.getName(), AppHelper.convertFileToByteArray(myFile2), "pdf/docx"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("authToken", Uconnekt.session.getUserInfo().authToken);
                    return headers;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0,0f));
            VolleySingleton.getInstance(activity).addToRequestQueue(multipartRequest);
        } else {
            startActivity(new Intent(activity, NetworkActivity.class));
        }
    }
}
