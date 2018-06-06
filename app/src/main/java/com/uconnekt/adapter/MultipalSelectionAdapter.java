package com.uconnekt.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.model.Speciality;
import com.uconnekt.ui.employer.employer_profile.EmpProfileActivity;

import java.util.List;

// this adapter not use in the app

public class MultipalSelectionAdapter extends RecyclerView.Adapter<MultipalSelectionAdapter.ViewHolder> {

    private List<Speciality> list;
    private EmpProfileActivity activity;
    private String tagId="";

    public MultipalSelectionAdapter(List<Speciality> list,EmpProfileActivity activity){
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list,parent,false);
        return new MultipalSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Speciality speciality = list.get(position);

        holder.tv_for_SpecialyName.setText(speciality.name);

        holder.imgCheck.setImageResource(speciality.isCheck?R.drawable.ic_checked:R.drawable.ic_uncheck);

        holder.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!speciality.isCheck){
                    holder.imgCheck.setImageResource(R.drawable.ic_checked);
                    list.get(position).isCheck=true;
                }else{
                    holder.imgCheck.setImageResource(R.drawable.ic_uncheck);
                    list.get(position).isCheck=false;
                }
                String result="";
                for (Speciality s:list){
                    String data=s.isCheck?s.name+",":"";
                    result=result+data;
                }

                tagId = speciality.id+",";
                if (speciality.isCheck){
                        activity.area_of_specialization=activity.area_of_specialization+speciality.id+",";
                }else {
                    if (activity.area_of_specialization.contains(tagId)) {
                        activity.area_of_specialization = activity.area_of_specialization.replace(tagId, "");
                    }
                }

                if (result.endsWith(",")) {
                    result = result.substring(0, result.length() - 1);
                }

                activity.tvTags.setText(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView tv_for_SpecialyName;
        private ImageView imgCheck;
        ViewHolder(View itemView) {
            super(itemView);
            tv_for_SpecialyName = itemView.findViewById(R.id.tv_for_SpecialyName);
            imgCheck = itemView.findViewById(R.id.imgCheck);
        }
    }
}
