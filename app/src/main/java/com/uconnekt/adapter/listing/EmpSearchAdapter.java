package com.uconnekt.adapter.listing;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.model.BusiSearchList;
import com.uconnekt.ui.employer.fragment.ProfileFragment;
import com.uconnekt.ui.employer.home.HomeActivity;

import java.util.ArrayList;

public class EmpSearchAdapter extends RecyclerView.Adapter<EmpSearchAdapter.ViewHolder> {

    private ArrayList<BusiSearchList> busiSearchLists;
    private Context context;

    public EmpSearchAdapter(ArrayList<BusiSearchList> busiSearchLists,Context context){
        this.busiSearchLists = busiSearchLists;
        this.context = context;
    }

    @NonNull
    @Override
    public EmpSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_list,parent,false);
        return new EmpSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpSearchAdapter.ViewHolder holder, int position) {
        BusiSearchList busiSearchList = busiSearchLists.get(position);

        if (busiSearchList.profileImage != null && !busiSearchList.profileImage.equals("")) {
            Picasso.with(context).load(busiSearchList.profileImage).into(holder.iv_profile_image);
        }else {
            Picasso.with(context).load(R.drawable.user).into(holder.iv_profile_image);
        }

        holder.tv_for_fullName.setText(busiSearchList.fullName.isEmpty()?"NA":busiSearchList.fullName);
        holder.tv_for_jobTitle.setText(busiSearchList.jobTitleName.isEmpty()?"NA":busiSearchList.jobTitleName);
        holder.tv_for_address.setText(busiSearchList.address.isEmpty()?"NA":busiSearchList.address);
    }

    @Override
    public int getItemCount() {
        return busiSearchLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_profile_image;
        private TextView tv_for_fullName,tv_for_jobTitle,tv_for_address;
        private LinearLayout mainlayout;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            tv_for_jobTitle = itemView.findViewById(R.id.tv_for_jobTitle);
            tv_for_address = itemView.findViewById(R.id.tv_for_address);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            mainlayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.mainlayout:
                    ((HomeActivity)context).addFragment(ProfileFragment.newInstance(busiSearchLists.get(getAdapterPosition()).userId));
                    break;
            }
        }
    }
}
