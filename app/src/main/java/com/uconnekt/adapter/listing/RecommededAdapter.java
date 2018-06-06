package com.uconnekt.adapter.listing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.model.RecommendedList;
import com.uconnekt.util.Utils;

import java.util.ArrayList;

public class RecommededAdapter extends RecyclerView.Adapter<RecommededAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RecommendedList> recommendedLists;

    public RecommededAdapter(Context context, ArrayList<RecommendedList> recommendedLists){
        this.context = context;
        this.recommendedLists = recommendedLists;
    }

    @NonNull
    @Override
    public RecommededAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_list_layout,parent,false);
        return new RecommededAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendedList recommendedList = recommendedLists.get(position);
        Picasso.with(context).load(recommendedList.profileImage).into(holder.iv_for_profile);
        holder.tv_for_rName.setText(recommendedList.fullName.isEmpty()?"NA":recommendedList.fullName+" Recommeded you");
        holder.tv_for_date.setText(recommendedList.created_on.isEmpty()?"NA": Utils.parseDateToddMMyyyy(recommendedList.created_on).substring(0,10));
    }


    @Override
    public int getItemCount() {
        return recommendedLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_for_profile;
        private TextView tv_for_rName,tv_for_date;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_for_profile = itemView.findViewById(R.id.iv_for_profile);
            tv_for_rName = itemView.findViewById(R.id.tv_for_rName);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);

        }
    }
}
