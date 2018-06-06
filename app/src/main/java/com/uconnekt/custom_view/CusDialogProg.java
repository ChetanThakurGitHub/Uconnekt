package com.uconnekt.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.uconnekt.R;

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

}
