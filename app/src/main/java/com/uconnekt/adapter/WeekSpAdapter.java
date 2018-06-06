package com.uconnekt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.model.Weeks;

import java.util.ArrayList;

public class WeekSpAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Weeks> arrayList;
    private int view;

    public WeekSpAdapter(Context context, ArrayList<Weeks> arrayList,int view) {
        this.context = context;
        this.arrayList = arrayList;
        this.view = view;
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
            assert inflater != null;
            row = inflater.inflate(view, parent, false);

            holder = new ViewHolder();
            holder.tv_VFname = row.findViewById(R.id.text1);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Weeks item = arrayList.get(position);
        holder.tv_VFname.setText(item.week);
        return row;
    }
    class ViewHolder {
        TextView tv_VFname;
    }
}
