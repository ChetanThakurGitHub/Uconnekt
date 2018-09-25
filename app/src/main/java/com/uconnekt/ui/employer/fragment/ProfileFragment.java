package com.uconnekt.ui.employer.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.ui.common_activity.NetworkActivity;
import com.uconnekt.ui.employer.activity.BasicInfoActivity;
import com.uconnekt.ui.employer.activity.ProfileActivity;
import com.uconnekt.ui.employer.activity.ResumeActivity;
import com.uconnekt.ui.employer.activity.experience.ExpActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.activity.FavouriteActivity;
import com.uconnekt.ui.individual.activity.RecommendedActivity;
import com.uconnekt.ui.individual.activity.ViewActivity;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

import static com.uconnekt.util.Constant.CALLING;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private HomeActivity activity;
    private static final String ARG_PARAM1 = "param1";
    private String userId = "",profileImage = "",fullName = "",jobTitleName = "",specializationName = "",address = "",profileUrl = "",phone= "",email = "";
    private ImageView iv_for_favourite,iv_for_recommend,iv_profile_image,profile;
    private int favourite_count = 0,recommend_count = 0,check = 0;
    private TextView tv_for_bio,tv_for_review_count,tv_for_favourite_count,tv_for_recomend,
            tv_for_aofs,tv_for_address,tv_for_company,tv_for_fullName;
    private FloatingActionMenu float_menu;

    public static ProfileFragment newInstance(String userID) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            userId =  getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);
        activity.setToolbarIcon(2);
        initView(view);
        if (check == 0){view();check = 1;}

        iv_for_favourite.setOnClickListener(this);
        iv_for_recommend.setOnClickListener(this);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALLING: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                        Intent intent4 = new Intent(Intent.ACTION_CALL);
                        intent4.setData(Uri.parse("tel:" + phone));
                        startActivity(intent4);
                    }
                } else {
                    Toast.makeText(activity, "Deny calling permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void callingIntent(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void initView(View view){
        profile = view.findViewById(R.id.profile);
        iv_profile_image = view.findViewById(R.id.iv_profile_image);
        tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        tv_for_company = view.findViewById(R.id.tv_for_company);
        tv_for_address = view.findViewById(R.id.tv_for_address);
        tv_for_aofs = view.findViewById(R.id.tv_for_aofs);
        iv_for_favourite = view.findViewById(R.id.iv_for_favourite);
        iv_for_recommend = view.findViewById(R.id.iv_for_recommend);
        tv_for_bio = view.findViewById(R.id.tv_for_bio);
        tv_for_review_count = view.findViewById(R.id.tv_for_review_count);
        tv_for_favourite_count = view.findViewById(R.id.tv_for_favourite_count);
        tv_for_recomend = view.findViewById(R.id.tv_for_recomend);
        activity.findViewById(R.id.iv_for_share).setOnClickListener(this);
        view.findViewById(R.id.layout_for_basicInfo).setOnClickListener(this);
        view.findViewById(R.id.layout_for_Experience).setOnClickListener(this);
        view.findViewById(R.id.layout_for_Resume).setOnClickListener(this);
        view.findViewById(R.id.tv_for_favourite).setOnClickListener(this);
        view.findViewById(R.id.tv_for_recomendTxt).setOnClickListener(this);
        view.findViewById(R.id.tv_for_views).setOnClickListener(this);
        iv_profile_image.setOnClickListener(this);

        float_menu = view.findViewById(R.id.float_menu);
        view.findViewById(R.id.fab_chat).setOnClickListener(this);
        view.findViewById(R.id.fab_call).setOnClickListener(this);
        view.findViewById(R.id.fab_email).setOnClickListener(this);
    }

    private void setData(String profileImage, String fullName, String jobTitleName, String specializationName, String address) {
        String backImage = profileImage.replace("/medium","");
        Picasso.with(activity).load(backImage).into(profile);
        Picasso.with(activity).load(profileImage).into(iv_profile_image);
        tv_for_fullName.setText(fullName.isEmpty()?"NA":fullName);
        tv_for_company.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (HomeActivity) context;
    }

    private void apiCalling(){
        new VolleyGetPost(activity, AllAPIs.PROFILE+"user_id="+userId,false,"Profile",true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONObject object = jsonObject.getJSONObject("profile");
                        String bio = URLDecoder.decode(object.getString("bio"), "UTF-8");
                        profileImage = object.getString("profileImage");
                        fullName = object.getString("fullName");
                        phone = object.getString("phone");
                        email = object.getString("email");
                        jobTitleName = object.getString("jobTitleName");
                        specializationName = object.getString("specializationName");
                        address = object.getString("address");
                        profileUrl = object.getString("profileUrl");
                        tv_for_bio.setText(bio.isEmpty()?"NA":bio);
                        String view_count = jsonObject.getString("view_count");
                        tv_for_review_count.setText(view_count.isEmpty()?"0":view_count);
                        String favourite = jsonObject.getString("favourite_count");
                        if (!favourite.isEmpty())favourite_count = Integer.parseInt(favourite);
                        String recommend = jsonObject.getString("recommend_count");
                        if (!favourite.isEmpty())recommend_count = Integer.parseInt(recommend);
                        String is_favourite = jsonObject.getString("is_favourite");
                        iv_for_favourite.setImageResource(is_favourite.equals("1")?R.drawable.ic_love:R.drawable.ic_like_blank);
                        String is_recommend = jsonObject.getString("is_recommend");
                        iv_for_recommend.setImageResource(is_recommend.equals("1")?R.drawable.ic_thumbs:R.drawable.ic_like);
                        tv_for_favourite_count.setText(favourite_count==0?"0":favourite_count+"");
                        tv_for_recomend.setText(recommend_count==0?"0":recommend_count+"");
                        setData(profileImage,fullName,jobTitleName,specializationName,address);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {
                startActivity(new Intent(activity, NetworkActivity.class));

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

    private void favourite(){
        new VolleyGetPost(activity, AllAPIs.FACOURITES, true, "Favourite", true) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        String isFavourite = jsonObject.getString("isFavourite");

                        iv_for_favourite.setImageResource(isFavourite.equals("1")?R.drawable.ic_love:R.drawable.ic_like_blank);

                        if (isFavourite.equals("1")){
                            favourite_count = favourite_count + 1;
                            tv_for_favourite_count.setText(favourite_count+"");
                        }else {
                            favourite_count = favourite_count - 1;
                            tv_for_favourite_count.setText(favourite_count+"");
                        }

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
                params.put("favourite_for",userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void recommend(){
        new VolleyGetPost(activity, AllAPIs.RECOMMEND, true, "Recommend", true) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        String isRecommend = jsonObject.getString("isRecommend");
                        iv_for_recommend.setImageResource(isRecommend.equals("1")?R.drawable.ic_thumbs:R.drawable.ic_like);

                        if (isRecommend.equals("1")){
                            recommend_count = recommend_count + 1;
                            tv_for_recomend.setText(recommend_count+"");
                        }else {
                            recommend_count = recommend_count - 1;
                            tv_for_recomend.setText(recommend_count+"");
                        }
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
                params.put("recommend_for",userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void view(){
        new VolleyGetPost(activity, AllAPIs.VIEW, true, "Recommend", true) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        apiCalling();
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
                params.put("view_for",userId);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    @Override
    public void onClick(View v) {
        activity.hideKeyboard();
        switch (v.getId()){
            case R.id.iv_for_favourite:
                favourite();
                break;
            case R.id.iv_for_recommend:
                recommend();
                break;
            case R.id.layout_for_basicInfo:
                Intent intent = new Intent(activity,BasicInfoActivity.class);
                intent.putExtra("USERID",userId);
                activity.startActivity(intent);
                break;
            case R.id.layout_for_Experience:
                intent = new Intent(activity,ExpActivity.class);
                intent.putExtra("USERID",userId);
                activity.startActivity(intent);
                break;
            case R.id.layout_for_Resume:
                intent = new Intent(activity,ResumeActivity.class);
                intent.putExtra("USERID",userId);
                activity.startActivity(intent);
                break;
            case R.id.tv_for_favourite:
                intent = new Intent(activity,FavouriteActivity.class);
                intent.putExtra("USERID",userId);
                activity.startActivity(intent);
                break;
            case R.id.tv_for_recomendTxt:
                intent = new Intent(activity,RecommendedActivity.class);
                intent.putExtra("USERID",userId);
                activity.startActivity(intent);
                break;
            case R.id.iv_for_share:
                deletelDailog();
                break;
            case R.id.tv_for_views:
                intent = new Intent(activity,ViewActivity.class);
                intent.putExtra("USERID",userId);
                activity.startActivity(intent);
                break;
            case R.id.iv_profile_image:
                zoomImageDialog();
                break;
            case R.id.fab_call:
                float_menu.close(false);
                PermissionAll permissionAll = new PermissionAll();
                if (permissionAll.checkCallingPermission(activity))
                    callingIntent();
                break;
            case R.id.fab_chat:
                float_menu.close(false);
                intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("USERID", userId);
                startActivity(intent);
                break;
            case R.id.fab_email:
                float_menu.close(false);
                sharOnEmail(email);
                break;
        }
    }


    private void zoomImageDialog() {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_zoomimage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        PhotoView iv_for_image = dialog.findViewById(R.id.iv_for_image);

        if (profileImage != null && !profileImage.equals("")) {
            Picasso.with(activity).load(profileImage).placeholder(R.drawable.ic_background).into(iv_for_image);
        } else {
            Picasso.with(activity).load(R.drawable.ic_background).fit().into(iv_for_image);
        }
        dialog.show();
    }

    private void deletelDailog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_indi_profile);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        final LinearLayout layout_for_share = dialog.findViewById(R.id.layout_for_share);
        CardView card_for_close = dialog.findViewById(R.id.card_for_close);
        CardView card_for_share = dialog.findViewById(R.id.card_for_share);
        dailogData(dialog);

        card_for_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        card_for_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        screenShot(layout_for_share,"Check this out “ Job seeker ” profile.");
                    }
                }).start();

            }
        });
        dialog.show();
    }

    private void dailogData(Dialog dialog){
        ImageView iv_profile_image = dialog.findViewById(R.id.iv_profile_image);
        TextView tv_for_fullName = dialog.findViewById(R.id.tv_for_fullName);
        TextView tv_for_company = dialog.findViewById(R.id.tv_for_company);
        TextView tv_for_aofs = dialog.findViewById(R.id.tv_for_aofs);
        TextView tv_for_address = dialog.findViewById(R.id.tv_for_address);
        TextView tv_for_review = dialog.findViewById(R.id.tv_for_review_count);
        TextView tv_for_favourite = dialog.findViewById(R.id.tv_for_favourite_count);
        TextView tv_for_recomended = dialog.findViewById(R.id.tv_for_recomend);
        TextView tv_for_bios = dialog.findViewById(R.id.tv_for_bio);

        Picasso.with(activity).load(profileImage).into(iv_profile_image);
        tv_for_fullName.setText(fullName.isEmpty()?"NA":fullName);
        tv_for_company.setText(jobTitleName.isEmpty()?"NA":jobTitleName);
        tv_for_address.setText(address.isEmpty()?"NA":address);
        tv_for_aofs.setText(specializationName.isEmpty()?"NA":specializationName);
        tv_for_review.setText(tv_for_review_count.getText().toString());
        tv_for_favourite.setText(tv_for_favourite_count.getText().toString());
        tv_for_recomended.setText(tv_for_recomend.getText().toString());
        tv_for_bios.setText(tv_for_bio.getText().toString());
    }

    /*.................................screenShot()...................................*/
    private void screenShot(LinearLayout scr_shot_view, final String text) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            File f = new File(Environment.getExternalStorageDirectory(), "ConnektUs/Shared Profiles");
            if (!f.exists()) f.mkdirs();
            String mPath = Environment.getExternalStorageDirectory().toString() + "/ConnektUs/Shared Profiles/" + now + ".png";
            scr_shot_view.setDrawingCacheEnabled(true);
            scr_shot_view.buildDrawingCache(true);
            final File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap bitmap = Bitmap.createBitmap(scr_shot_view.getDrawingCache());
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
            scr_shot_view.destroyDrawingCache();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    sharOnsocial(imageFile,text);
                }
            });

            //onShareClick(imageFile,text);
            //doShareLink(text,otherProfileInfo.UserDetail.profileUrl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sharOnsocial(File imageFile, String text) {
        Uri uri;
        Intent sharIntent = new Intent(Intent.ACTION_SEND);
        String ext = imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(ext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider",imageFile);
            sharIntent.setDataAndType(uri, type);
        } else {
            uri = Uri.fromFile(imageFile);
            sharIntent.setDataAndType(uri, type);
        }

        sharIntent.setType("image/png");
        //sharIntent.setType("text/plain");
        sharIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharIntent.putExtra(Intent.EXTRA_SUBJECT, "ConnektUs");
        sharIntent.putExtra(Intent.EXTRA_TEXT, text+"\n"+profileUrl);
        startActivity(Intent.createChooser(sharIntent, "Share:"));
    }


    private void sharOnEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: " + email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enter something");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hii android !");
        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

}
