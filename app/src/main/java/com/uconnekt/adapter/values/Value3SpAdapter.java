package com.uconnekt.adapter.values;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.model.JobTitle;
import com.uconnekt.ui.individual.individual_profile.profile_fragrment.Basic_info.BasicInfoFragment;

import java.util.ArrayList;

public class Value3SpAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<JobTitle> arrayList;
    private BasicInfoFragment fragment;

    public Value3SpAdapter(Context context, ArrayList<JobTitle> arrayList, BasicInfoFragment fragment) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragment=fragment;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_sp_value3, parent, false);

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

    @Override
    public View getDropDownView(int i, View view, @NonNull ViewGroup viewGroup) {
        if (fragment.spValue1 != -1 &&fragment.spValue2 != -1 && fragment.spValue1 == i |fragment.spValue2 == i) {
            view = new View(context);
            view.setTag("Extra");
            return view;
        }

        JobTitle bean = arrayList.get(i);
        if (view == null || view.getTag().equals("Extra")) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.custom_sp_value3, viewGroup, false);
            view.setTag("");
        }

        TextView tvSpinnerName = view.findViewById(R.id.text1);

        tvSpinnerName.setText(bean.jobTitleName);
        // tvSpinnerName.setPadding((int) activity.getResources().getDimension(R.dimen._10sdp), 0, 0, 0);

        return view;
    }
}
