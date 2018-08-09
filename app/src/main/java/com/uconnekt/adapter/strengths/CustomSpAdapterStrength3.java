package com.uconnekt.adapter.strengths;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.model.JobTitle;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.ui.individual.edit_profile.fragment.EditBasicInfoFragment;

import java.util.ArrayList;

public class CustomSpAdapterStrength3 extends BaseAdapter {

    private EditBasicInfoFragment fragment;
    private ArrayList<JobTitle> arrayList;
    private int view;
    private IndiEditProfileActivity activity;

    public CustomSpAdapterStrength3(ArrayList<JobTitle> arrayList, int view, EditBasicInfoFragment fragment, IndiEditProfileActivity activity) {
        this.arrayList = arrayList;
        this.view = view;
        this.fragment = fragment;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View row, ViewGroup parent) {

        ViewHolder holder;
//fragment.spValue2 != -1 &&fragment.spValue3 != -1 && 
        if (fragment.spStrength1 == position |fragment.spStrength2 == position) {
            row = new View(activity);
            row.setTag("Extra");
            return row;
        }

        if (row == null || row.getTag().equals("Extra")) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            row = inflater.inflate(view, parent, false);

            holder = new ViewHolder();
            holder.tv_VFname = row.findViewById(R.id.text1);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final JobTitle item = arrayList.get(position);
        holder.tv_VFname.setText(item.jobTitleName);
        return row;
    }
    class ViewHolder {
        TextView tv_VFname;
    }
}
