package com.uconnekt.adapter.listing;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.uconnekt.R;
import com.uconnekt.model.SpecialityList;
import com.uconnekt.ui.employer.fragment.MapFragment;
import com.uconnekt.ui.employer.fragment.SearchFragment;
import com.uconnekt.ui.individual.fragment.IndiMapFragment;
import com.uconnekt.ui.individual.fragment.IndiSearchFragment;

import java.util.ArrayList;

public class SpecialityListAdapter extends RecyclerView.Adapter<SpecialityListAdapter.ViewHolder> {

    private ArrayList<SpecialityList> specialityLists;
    private Fragment activity;

    public SpecialityListAdapter(ArrayList<SpecialityList> specialityLists, Fragment activity){
        this.specialityLists =specialityLists;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SpecialityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_speciality_list,parent,false);
        return new SpecialityListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialityListAdapter.ViewHolder holder, int position) {
        SpecialityList specialityList = specialityLists.get(position);

        holder.tv_for_specializationName.setText(specialityList.specializationName);
    }

    @Override
    public int getItemCount() {
        return specialityLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_for_specializationName;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_for_specializationName = itemView.findViewById(R.id.tv_for_specializationName);
            itemView.findViewById(R.id.layout_for_click).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout_for_click:

                    if (activity instanceof IndiSearchFragment){
                        ((IndiSearchFragment)activity).specialtyId= specialityLists.get(getAdapterPosition()).specializationId;
                        ((IndiSearchFragment)activity).tv_for_speName.setText(specialityLists.get(getAdapterPosition()).specializationName);
                        ((IndiSearchFragment)activity).layout_for_list.setVisibility(View.GONE);
                        ((IndiSearchFragment)activity).goneVisi = false;
                        ((IndiSearchFragment)activity).iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                        ((IndiSearchFragment)activity).searchLists.clear();
                        ((IndiSearchFragment)activity).getList(specialityLists.get(getAdapterPosition()).specializationId, "", "", "", "");
                        ((IndiSearchFragment)activity).offset = 0;
                        ((IndiSearchFragment)activity).mSwipeRefreshLayout.setRefreshing(true);
                    }else if (activity instanceof SearchFragment){
                        ((SearchFragment)activity).jobTitleId= specialityLists.get(getAdapterPosition()).specializationId;
                        ((SearchFragment)activity).tv_for_speName.setText(specialityLists.get(getAdapterPosition()).specializationName);
                        ((SearchFragment)activity).layout_for_list.setVisibility(View.GONE);
                        ((SearchFragment)activity).goneVisi = false;
                        ((SearchFragment)activity).iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                        ((SearchFragment)activity).searchLists.clear();
                        ((SearchFragment)activity).getList("", specialityLists.get(getAdapterPosition()).specializationId, "", "", "", "", "");
                        ((SearchFragment)activity).offset = 0;
                        ((SearchFragment)activity).mSwipeRefreshLayout.setRefreshing(true);
                    }else if (activity instanceof IndiMapFragment){
                        ((IndiMapFragment)activity).tv_for_speName.setText(specialityLists.get(getAdapterPosition()).specializationName);
                        ((IndiMapFragment)activity).layout_for_list.setVisibility(View.GONE);
                        ((IndiMapFragment)activity).goneVisi = false;
                        ((IndiMapFragment)activity).iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                        ((IndiMapFragment)activity).searchLists.clear();
                        ((IndiMapFragment)activity).map.clear();
                        ((IndiMapFragment)activity).getList(specialityLists.get(getAdapterPosition()).specializationId, "", "", "", 0.0, 0.0, "");
                       /* ((IndiMapFragment)activity).offset = 0;
                        ((IndiMapFragment)activity).mSwipeRefreshLayout.setRefreshing(true);*/
                    }else if (activity instanceof MapFragment){
                        ((MapFragment)activity).jobTitleId= specialityLists.get(getAdapterPosition()).specializationId;
                        ((MapFragment)activity).tv_for_speName.setText(specialityLists.get(getAdapterPosition()).specializationName);
                        ((MapFragment)activity).layout_for_list.setVisibility(View.GONE);
                        ((MapFragment)activity).goneVisi = false;
                        ((MapFragment)activity).iv_for_arrow.setImageResource(R.drawable.ic_down_arrow);
                        ((MapFragment)activity).searchLists.clear();
                        ((MapFragment)activity).map.clear();
                        ((MapFragment)activity).getList("", specialityLists.get(getAdapterPosition()).specializationId, "", "", "", "", 0.0, 0.0, "" + "");
                        /*((MapFragment)activity).offset = 0;
                        ((MapFragment)activity).mSwipeRefreshLayout.setRefreshing(true);*/
                    }
                    break;
            }
        }
    }
}
