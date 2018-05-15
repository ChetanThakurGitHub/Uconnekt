package com.uconnekt.pagination;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.uconnekt.R;


/**
 * Created by hemant on 12/05/18.
 */

public class FooterLoader extends RecyclerView.ViewHolder {

   public ProgressBar mProgressBar;

    public FooterLoader(View itemView) {
        super(itemView);
       mProgressBar=itemView.findViewById(R.id.progressbar);
    }
}
