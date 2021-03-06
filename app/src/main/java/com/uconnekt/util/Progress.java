package com.uconnekt.util;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.uconnekt.R;


public class Progress {

    @SuppressLint("StaticFieldLeak")
    private static Progress progress;
    private View progressBarView;

    private Progress(Activity activity) {
        progressBarView = LayoutInflater.from(activity).inflate(R.layout.progress_bar_layout, null);
        activity.addContentView(progressBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
    }

    public static void show(Context context) {
        if (progress != null) {
            try {
                View removeView = ((Activity) context).findViewById(R.id.progressBar);
                if (removeView != null) removeView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                progress = new Progress((Activity) context);
                progress.progressBarView.setVisibility(View.VISIBLE);
            }
        }
        progress = new Progress((Activity) context);
        progress.progressBarView.setVisibility(View.VISIBLE);
    }

    public static void hide(Context context) {
        if (progress != null) {
            progress.progressBarView.setVisibility(View.GONE);
        }
        try {
            View removeView = ((Activity) context).findViewById(R.id.progressBar);
            if (removeView != null) {
                removeView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressOnly(Context context) {
        progress = new Progress((Activity) context);
        progress.progressBarView.setVisibility(View.VISIBLE);
        //  progress.progressBarView.findViewById(R.id.view).setVisibility(View.GONE);
    }

   /* public Progress setText(int resourceId) {
        ((TextView) progressBarView.findViewById(R.id.progress_bar_text)).setText(resourceId);
        return this;
    }

    public Progress setText(String text) {
        ((TextView) progressBarView.findViewById(R.id.progress_bar_text)).setText(text);
        return this;
    }*/

    public View getProgressBarView() {
        return progressBarView;
    }

    public void setProgressBarView(View progressBarView) {
        this.progressBarView = progressBarView;
    }

}
