package com.uconnekt.ui.authentication.splash;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.ui.authentication.user_selection.UserSelectionActivity;
import com.uconnekt.ui.employer.employer_profile.EmpProfileActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;

public class SplashActivity extends AppCompatActivity implements SplashView {
    private Animation zoomOut;
    private FrameLayout uppFrame;
    private ImageView inImage;

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
    protected void onStart() {
        super.onStart();
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
                //Log.e("authToken",Uconnekt.session.getUserInfo().authToken);
                Constant.CHAT = 0;
               if (Uconnekt.session.getUserInfo().userType.equals("business")){
                    //startActivity(new Intent(SplashActivity.this,EmpProfileActivity.class));
                    if (Uconnekt.session.getUserInfo().isProfile.equals("0")) {
                        startActivity(new Intent(SplashActivity.this,EmpProfileActivity.class));
                    }else {
                        startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    }
                }else {
                  //startActivity(new Intent(SplashActivity.this, IndiEditProfileActivity.class));
                    if (Uconnekt.session.getUserInfo().isProfile.equals("0")) {
                        Intent intent = new Intent(SplashActivity.this,IndiEditProfileActivity.class);
                        intent.putExtra("FROM", "First");
                        startActivity(intent);
                    }else {
                        startActivity(new Intent(SplashActivity.this, JobHomeActivity.class));
                    }
                }SplashActivity.this.finish();
                overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
            }
        }, Constant.SPLESH_TIME);
    }
}
