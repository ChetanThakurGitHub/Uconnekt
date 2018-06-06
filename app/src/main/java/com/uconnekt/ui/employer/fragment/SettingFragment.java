package com.uconnekt.ui.employer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;

public class SettingFragment extends Fragment {



    public SettingFragment() {
        // Required empty public constructor
    }


    public static SettingFragment newInstance() {
        return new SettingFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        view.findViewById(R.id.layout_for_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uconnekt.session.logout(getActivity());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
