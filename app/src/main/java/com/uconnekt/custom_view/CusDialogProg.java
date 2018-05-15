package com.uconnekt.custom_view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.uconnekt.R;
import com.uconnekt.singleton.MyCustomMessage;

import java.util.Timer;
import java.util.TimerTask;

public class CusDialogProg extends Dialog {

    public CusDialogProg(Context context) {
        super(context, R.style.ProgressBarTheme);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialogue_layout);

        Animation animation3 = AnimationUtils.loadAnimation(context, R.anim.gone_visibale);
        Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.gone_visibale2);

        this.findViewById(R.id.iv_for_wifi).setAnimation(animation2);
        this.findViewById(R.id.iv_for_moon).setAnimation(animation3);

    }


  /*  private Context context;
    @SuppressLint("StaticFieldLeak")
    private static CusDialogProg instance;

    *//**
     * @param context as parent context
     *//*
    public CusDialogProg(Context context) {
        this.context = context;
    }

 *//*
    public synchronized static CusDialogProg getInstance(Context context) {
        if (instance == null) {
            instance = new CusDialogProg(context);
        }
        return instance;
    }*//*

    public Dialog myDialog() {
        Dialog pDialog = new Dialog(context);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogue_layout,null);
        pDialog.setContentView(view);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Animation animation3 = AnimationUtils.loadAnimation(context, R.anim.gone_visibale);
        Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.gone_visibale2);

        view.findViewById(R.id.iv_for_wifi).setAnimation(animation2);
        view.findViewById(R.id.iv_for_moon).setAnimation(animation3);

        pDialog.show();
        return pDialog;
    }
*/

}
