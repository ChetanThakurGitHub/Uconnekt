package com.uconnekt.adapter.listing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.model.ViewList;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.util.Utils;

import java.util.ArrayList;


public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ViewList> favourites;

    public ViewAdapter(Context context, ArrayList<ViewList> favourites){
        this.context = context;
        this.favourites = favourites;
    }

    @NonNull
    @Override
    public ViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list_layout,null);
        return new ViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapter.ViewHolder holder, int position) {
        ViewList favourite = favourites.get(position);
        Picasso.with(context).load(favourite.profileImage).into(holder.iv_for_profile);
        holder.tv_for_fullName.setText(favourite.fullName.isEmpty()?"NA":favourite.fullName);
        holder.tv_for_aofs.setText(favourite.jobTitleName);
        holder.tv_for_date.setText(favourite.crd.isEmpty()?"NA": Utils.parseDateToddMMyyyy(favourite.crd).substring(0,10));
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_for_profile;
        private TextView tv_for_fullName,tv_for_aofs,tv_for_date;
        private CardView card_for_chat;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_for_profile = itemView.findViewById(R.id.iv_for_profile);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            tv_for_aofs = itemView.findViewById(R.id.tv_for_aofs);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            card_for_chat = itemView.findViewById(R.id.card_for_chat);
            card_for_chat.setOnClickListener(this);
            itemView.findViewById(R.id.cardview).setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.card_for_chat:
                    MyCustomMessage.getInstance(context).customToast("Under development mode....");
                    break;
                case R.id.cardview:

                    break;
            }
        }
    }
}
