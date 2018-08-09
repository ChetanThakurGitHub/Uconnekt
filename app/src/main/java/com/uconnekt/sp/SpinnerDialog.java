package com.uconnekt.sp;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.uconnekt.adapter.CustomSpAdapter;
import com.uconnekt.model.JobTitle;

import java.util.ArrayList;


public class SpinnerDialog{
    private ArrayList<JobTitle> items;
    private Activity context;
    private String dTitle;
    private OnSpinerItemClick onSpinerItemClick;
    private AlertDialog alertDialog;
    private CustomSpAdapter adapter;
    private ArrayList<JobTitle>tmpList = new ArrayList<>();

    public SpinnerDialog(Activity activity, ArrayList<JobTitle> items, String dialogTitle) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
    }

    public void bindOnSpinerListener(OnSpinerItemClick onSpinerItemClick1) {
        this.onSpinerItemClick = onSpinerItemClick1;
    }

    public void showSpinerDialog() {
        tmpList.clear();
        tmpList.addAll(items);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams")
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose =  v.findViewById(R.id.close);
        TextView title =  v.findViewById(R.id.spinerTitle);
        rippleViewClose.setText(R.string.close);
        title.setText(dTitle);
        final ListView listView =  v.findViewById(R.id.list);
        final EditText searchBox =  v.findViewById(R.id.searchBox);
        adapter = new CustomSpAdapter(context, tmpList,R.layout.custom_sp2);
        listView.setAdapter(adapter);
        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.setCancelable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onSpinerItemClick.onClick(tmpList.get(i));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                filter(s.toString());
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

    private synchronized void filter(String text){
        if (text.isEmpty()){
            tmpList.clear();
            tmpList.addAll(items);
            adapter.notifyDataSetChanged();
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
        adapter.notifyDataSetChanged();
    }

}
