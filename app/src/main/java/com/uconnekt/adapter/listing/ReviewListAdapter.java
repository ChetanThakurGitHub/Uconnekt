package com.uconnekt.adapter.listing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.uconnekt.ui.individual.activity.ReviewActivity;

import java.util.ArrayList;


public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ReviewList> reviewLists;
    private String userId;

    public ReviewListAdapter(Context context, ArrayList<ReviewList> reviewLists, String userId){
        this.context = context;
        this.reviewLists = reviewLists;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_layout,null);
        return new ReviewListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapter.ViewHolder holder, int position) {
        ReviewList reviewList = reviewLists.get(position);
        Picasso.with(context).load(reviewList.profileImage).into(holder.iv_for_profile);
        holder.tv_for_fullName.setText(reviewList.fullName);
        holder.tv_for_comment.setText(reviewList.comments);
        holder.ratingBar.setRating(Float.parseFloat(reviewList.rating));
    }

    @Override
    public int getItemCount() {
        return (reviewLists.size() > 20)?20:reviewLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_for_profile;
        private TextView tv_for_fullName,tv_for_comment;
        private RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_for_profile = itemView.findViewById(R.id.iv_for_profile);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tv_for_comment = itemView.findViewById(R.id.tv_for_comment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!userId.isEmpty()) {
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("USERID", userId);
                context.startActivity(intent);
            }
        }
    }
}
