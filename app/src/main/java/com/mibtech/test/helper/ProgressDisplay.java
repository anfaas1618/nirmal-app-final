package com.mibtech.test.helper;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ProgressDisplay {

    static ProgressBar mProgressBar;

    public ProgressDisplay(Context context) {

        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content)
                .getRootView();

        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        mProgressBar.setIndeterminate(true);


        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(context);
        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);
        layout.addView(rl, params);
        hideProgress();
    }


    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
