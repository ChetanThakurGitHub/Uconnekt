package com.uconnekt.ui.employer.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uconnekt.R;
import com.uconnekt.util.Constant;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);
        initView(view);

        return view;
    }

    private void initView(View view){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
