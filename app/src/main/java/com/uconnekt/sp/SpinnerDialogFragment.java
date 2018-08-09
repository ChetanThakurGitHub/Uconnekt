package com.uconnekt.sp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.adapter.strengths.CustomSpAdapterStrength1;
import com.uconnekt.adapter.strengths.CustomSpAdapterStrength2;
import com.uconnekt.adapter.strengths.CustomSpAdapterStrength3;
import com.uconnekt.adapter.values.CustomSpAdapterValue1;
import com.uconnekt.adapter.values.CustomSpAdapterValue2;
import com.uconnekt.adapter.values.CustomSpAdapterValue3;
import com.uconnekt.model.JobTitle;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.ui.individual.edit_profile.fragment.EditBasicInfoFragment;

import java.util.ArrayList;


public class SpinnerDialogFragment {
    private ArrayList<JobTitle> items;
    private EditBasicInfoFragment context;
    private IndiEditProfileActivity activity;
    private String dTitle;
    private OnSpinerFragItemClick onSpinerFragItemClick;
    private AlertDialog alertDialog;
    private CustomSpAdapterValue1 adapterValue1;
    private CustomSpAdapterValue2 adapterValue2;
    private CustomSpAdapterValue3 adapterValue3;
    private CustomSpAdapterStrength1 adapterStrength1;
    private CustomSpAdapterStrength2 adapterStrength2;
    private CustomSpAdapterStrength3 adapterStrength3;
    private ArrayList<JobTitle>tmpList = new ArrayList<>();

    public SpinnerDialogFragment(EditBasicInfoFragment activity, ArrayList<JobTitle> items, String dialogTitle, IndiEditProfileActivity indiEditProfileActivity) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.activity = indiEditProfileActivity;
    }

    public void bindOnSpinerListener(OnSpinerFragItemClick onSpinerFragItemClick) {
        this.onSpinerFragItemClick = onSpinerFragItemClick;
    }



    public void showSpinerDialogFragment(final int j) {
        tmpList.clear();
        tmpList.addAll(items);
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        @SuppressLint("InflateParams")
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose =  v.findViewById(R.id.close);
        TextView title =  v.findViewById(R.id.spinerTitle);
        rippleViewClose.setText(R.string.close);
        title.setText(dTitle);
        final ListView listView =  v.findViewById(R.id.list);
        final EditText searchBox =  v.findViewById(R.id.searchBox);

        switch (j){
            case 1:
                adapterValue1 = new CustomSpAdapterValue1(tmpList,R.layout.custom_sp2,context,activity);
                listView.setAdapter(adapterValue1);
                break;
            case 2:
                adapterValue2 = new CustomSpAdapterValue2(tmpList,R.layout.custom_sp2,context,activity);
                listView.setAdapter(adapterValue2);
                break;
            case 3:
                adapterValue3 = new CustomSpAdapterValue3(tmpList,R.layout.custom_sp2,context,activity);
                listView.setAdapter(adapterValue3);
                break;
            case 4:
                adapterStrength1 = new CustomSpAdapterStrength1(tmpList,R.layout.custom_sp2,context,activity);
                listView.setAdapter(adapterStrength1);
                break;
            case 5:
                adapterStrength2 = new CustomSpAdapterStrength2(tmpList,R.layout.custom_sp2,context,activity);
                listView.setAdapter(adapterStrength2);
                break;
            case 6:
                adapterStrength3 = new CustomSpAdapterStrength3(tmpList,R.layout.custom_sp2,context,activity);
                listView.setAdapter(adapterStrength3);
                break;

        }

        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.setCancelable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onSpinerFragItemClick.onClick(i,tmpList.get(i));
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                alertDialog.dismiss();
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString(),j);
            }
        });

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private synchronized void filter(String text, int j){
        if (text.isEmpty()){
            tmpList.clear();
            tmpList.addAll(items);
            switch (j){
                case 1:
                    adapterValue1.notifyDataSetChanged();
                    break;
                case 2:
                    adapterValue2.notifyDataSetChanged();
                    break;
                case 3:
                    adapterValue3.notifyDataSetChanged();
                    break;
                case 4:
                    adapterStrength1.notifyDataSetChanged();
                    break;
                case 5:
                    adapterStrength2.notifyDataSetChanged();
                    break;
                case 6:
                    adapterStrength3.notifyDataSetChanged();
                    break;
            }

            return;
        }
        ArrayList<JobTitle> temp = new ArrayList<>();
        for(JobTitle d: items){
            if(d.jobTitleName.toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        tmpList.clear();
        tmpList.addAll(temp);
        switch (j){
            case 1:
                adapterValue1.notifyDataSetChanged();
                break;
            case 2:
                adapterValue2.notifyDataSetChanged();
                break;
            case 3:
                adapterValue3.notifyDataSetChanged();
                break;
            case 4:
                adapterStrength1.notifyDataSetChanged();
                break;
            case 5:
                adapterStrength2.notifyDataSetChanged();
                break;
            case 6:
                adapterStrength3.notifyDataSetChanged();
                break;
        }
    }

}
