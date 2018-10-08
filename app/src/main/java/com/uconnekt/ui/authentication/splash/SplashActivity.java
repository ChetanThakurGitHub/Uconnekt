package com.uconnekt.ui.authentication.splash;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.ui.authentication.email_authentication.EmailVerificationActivity;
import com.uconnekt.ui.authentication.user_selection.UserSelectionActivity;
import com.uconnekt.ui.employer.employer_profile.EmpProfileActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class SplashActivity extends AppCompatActivity implements SplashView {
    private Animation zoomOut;
    private FrameLayout uppFrame;
    private LinearLayout inImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initView();
        SplashPresenter splashPresenter = new SplashPresenterImpl(this, new SplashIntractorImpl(), this);
        splashPresenter.validationCondition(this);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        uppFrame.startAnimation(zoomOut);
        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                inImage.setVisibility(View.VISIBLE);
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(inImage, PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
                scaleDown.setDuration(800);
                scaleDown.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initView() {
        uppFrame = findViewById(R.id.uppFrame);
        inImage = findViewById(R.id.inImage);
        zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
    }


    @Override
    public void openLoginActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, UserSelectionActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
            }
        }, Constant.SPLESH_TIME);

    }

    @Override
    public void openMainActivity() {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(getIntent().getData() != null && !Uconnekt.session.getUserInfo().isVerified.equals("1")) {
                        profileView();
                    }else {
                        //Log.e("authToken",Uconnekt.session.getUserInfo().authToken);
                        Constant.CHAT = 0;
                        if (Uconnekt.session.getUserInfo().isVerified.equals("1")) {
                            if (Uconnekt.session.getUserInfo().userType.equals("business")) {
                                //startActivity(new Intent(SplashActivity.this,EmpProfileActivity.class));
                                if (Uconnekt.session.getUserInfo().isProfile.equals("0")) {
                                    startActivity(new Intent(SplashActivity.this, EmpProfileActivity.class));
                                } else {
                                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                }
                            } else {
                                //startActivity(new Intent(SplashActivity.this, IndiEditProfileActivity.class));
                                if (Uconnekt.session.getUserInfo().isProfile.equals("0")) {
                                    Intent intent = new Intent(SplashActivity.this, IndiEditProfileActivity.class);
                                    intent.putExtra("FROM", "First");
                                    startActivity(intent);
                                } else {
                                    startActivity(new Intent(SplashActivity.this, JobHomeActivity.class));
                                }
                            }
                        }else {
                            Intent intent = new Intent(SplashActivity.this, EmailVerificationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                    SplashActivity.this.finish();
                    overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
                }
            }, Constant.SPLESH_TIME);
        }

    private void profileView(){
        Map<String, String> map = Utils.getQueryString(getIntent().getData().toString());
    if (map.containsKey("token")){
        String email = map.get("email");
        String token = map.get("token");
            if (Uconnekt.session.getUserInfo().userId.equals(map.get("id"))){
                if(!Uconnekt.session.getUserInfo().isVerified.equals("1")) {
                    Intent intent = new Intent(SplashActivity.this, EmailVerificationActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("token", token);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "Email address is already verified", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Worng user", Toast.LENGTH_SHORT).show();
            }
    }else if (map.containsKey("id")) {
            String userType = map.get("userType");
            Intent intent;
            if (Uconnekt.session.getUserInfo().userType.equals("business")) {
                 if (userType.equals("business")){
                     Toast.makeText(SplashActivity.this, R.string.profile_check, Toast.LENGTH_SHORT).show();
                 }else {
                     intent = new Intent(this, HomeActivity.class);
                     sendData(map,intent);
                 }
            }else {
                if (!userType.equals("business")){
                    Toast.makeText(SplashActivity.this, R.string.profile_check, Toast.LENGTH_SHORT).show();
                }else {
                    intent = new Intent(this, JobHomeActivity.class);
                    sendData(map,intent);
                }
            }
        }
    }

    private void sendData(Map<String, String> map, Intent intent){
        try {
            byte[] data = Base64.decode(map.get("id"), Base64.DEFAULT);
            String decodedId = new String(data, "UTF-8");
            intent.putExtra("userId", decodedId);
            startActivity(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
