package com.uconnekt.adapter.listing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.model.ReviewList;
import com.uconnekt.util.Utils;

import java.util.ArrayList;

public class ReviewFullListAdapter extends RecyclerView.Adapter<ReviewFullListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ReviewList> reviewLists;

    public ReviewFullListAdapter(Context context, ArrayList<ReviewList> reviewLists){
        this.context = context;
        this.reviewLists = reviewLists;
    }

    @NonNull
    @Override
    public ReviewFullListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_full_list_layout,null);
        return new ReviewFullListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewFullListAdapter.ViewHolder holder, int position) {
        ReviewList reviewList = reviewLists.get(position);
        Picasso.with(context).load(reviewList.profileImage).into(holder.iv_for_profile);
        holder.tv_for_fullName.setText(reviewList.fullName);
        holder.tv_for_comment.setText(reviewList.comments);
        holder.ratingBar.setRating(Float.parseFloat(reviewList.rating));
        holder.tv_for_aofs.setText(reviewList.specializationName);
        holder.tv_for_date.setText(reviewList.created_on.isEmpty()?"NA": Utils.parseDateToddMMyyyy(reviewList.created_on).substring(0,10));
    }

    @Override
    public int getItemCount() {
        return reviewLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_for_profile;
        private TextView tv_for_fullName,tv_for_comment,tv_for_aofs,tv_for_date;
        private RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_for_profile = itemView.findViewById(R.id.iv_for_profile);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tv_for_comment = itemView.findViewById(R.id.tv_for_comment);
            tv_for_aofs = itemView.findViewById(R.id.tv_for_aofs);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
        }
    }
}
