package com.uconnekt.adapter.listing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.model.IndiSearchList;
import com.uconnekt.ui.individual.fragment.IndiProfileFragment;
import com.uconnekt.ui.individual.home.JobHomeActivity;

import java.util.ArrayList;

public class IndiSearchAdapter extends RecyclerView.Adapter<IndiSearchAdapter.ViewHolder> {

    private ArrayList<IndiSearchList> indiSearchList;
    private Context context;

    public IndiSearchAdapter(ArrayList<IndiSearchList> indiSearchList,Context context){
        this.indiSearchList = indiSearchList;
        this.context = context;
    }

    @NonNull
    @Override
    public IndiSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_list,parent,false);
        return new IndiSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndiSearchAdapter.ViewHolder holder, int position) {
            IndiSearchList indiSearch =  indiSearchList.get(position);

            if (indiSearch.profileImage != null && !indiSearch.profileImage.equals("")) {
                Picasso.with(context).load(indiSearch.profileImage).into(holder.iv_profile_image);
            }else {
                Picasso.with(context).load(R.drawable.user).into(holder.iv_profile_image);
            }

            if (indiSearch.company_logo != null && ! indiSearch.company_logo.equals("")){
                Picasso.with(context).load(indiSearch.company_logo).into(holder.iv_company_logo);
            }else {
                Picasso.with(context).load(R.drawable.user).into(holder.iv_company_logo);
            }
            holder.iv_for_fullName.setText(indiSearch.fullName.isEmpty()?"NA":indiSearch.fullName);
            holder.tv_for_businessName.setText(indiSearch.businessName.isEmpty()?"NA":indiSearch.businessName);
            holder.tv_for_specializationName.setText(indiSearch.specializationName.isEmpty()?"NA":indiSearch.specializationName);
            holder.tv_for_address.setText(indiSearch.address.isEmpty()?"NA":indiSearch.address);
            holder.ratingBar.setRating(indiSearch.rating.isEmpty()?0:Float.parseFloat(indiSearch.rating));
    }


    @Override
    public int getItemCount() {
        return indiSearchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_profile_image,iv_company_logo;
        private TextView iv_for_fullName,tv_for_businessName,tv_for_specializationName,tv_for_address;
        private RatingBar ratingBar;
        private CardView card_for_list;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image);
            iv_company_logo = itemView.findViewById(R.id.iv_company_logo);
            iv_for_fullName = itemView.findViewById(R.id.iv_for_fullName);
            tv_for_businessName = itemView.findViewById(R.id.tv_for_businessName);
            tv_for_specializationName = itemView.findViewById(R.id.tv_for_specializationName);
            tv_for_address = itemView.findViewById(R.id.tv_for_address);
            card_for_list = itemView.findViewById(R.id.card_for_list);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            card_for_list.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.card_for_list:
                    ((JobHomeActivity)context).addFragment(IndiProfileFragment.newInstance(indiSearchList.get(getAdapterPosition())));
                    break;
            }
        }
    }
}


