package com.uconnekt.adapter.listing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.model.Favourite;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.employer.activity.ProfileActivity;
import com.uconnekt.ui.individual.activity.IndiProfileActivity;
import com.uconnekt.util.Utils;

import java.util.ArrayList;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Favourite> favourites;
    private String userId = "";
    private boolean isMove;

    public FavoriteAdapter(Context context, ArrayList<Favourite> favourites, String userId, boolean isMove){
        this.context = context;
        this.favourites = favourites;
        this.userId = userId;
        this.isMove = isMove;
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list_layout,null);
        return new FavoriteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        Favourite favourite = favourites.get(position);
        Picasso.with(context).load(favourite.profileImage).into(holder.iv_for_profile);
        holder.tv_for_fullName.setText(favourite.fullName.isEmpty()?"NA":favourite.fullName);
        holder.tv_for_aofs.setText(favourite.jobTitleName);
        holder.tv_for_date.setText(favourite.created_on.isEmpty()?"NA": Utils.parseDateToddMMyyyy(favourite.created_on).substring(0,10));
        holder.card_for_chat.setVisibility((userId.equals(Uconnekt.session.getUserInfo().userId))?View.VISIBLE:View.GONE);
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
            if (isMove)itemView.findViewById(R.id.cardview).setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.card_for_chat:
                    Intent intent = new Intent(context,ChatActivity.class);
                    if (Uconnekt.session.getUserInfo().userType.equals("individual")) {
                        if (favourites.get(getAdapterPosition()).favourite_by.equals(Uconnekt.session.getUserInfo().userId)) {
                            intent.putExtra("USERID", favourites.get(getAdapterPosition()).favourite_for);
                        } else {
                            intent.putExtra("USERID", favourites.get(getAdapterPosition()).favourite_by);
                        }
                    }else {
                        if (favourites.get(getAdapterPosition()).favourite_by.equals(Uconnekt.session.getUserInfo().userId)) {
                            intent.putExtra("USERID", favourites.get(getAdapterPosition()).favourite_for);
                        } else {
                            intent.putExtra("USERID", favourites.get(getAdapterPosition()).favourite_by);
                        }
                    }
                    context.startActivity(intent);
                    break;
                case R.id.cardview:
                    if (Uconnekt.session.getUserInfo().userType.equals("individual")) {
                        if (favourites.get(getAdapterPosition()).favourite_by.equals(Uconnekt.session.getUserInfo().userId)) {
                            intent = new Intent(context, IndiProfileActivity.class);
                            intent.putExtra("UserId", favourites.get(getAdapterPosition()).favourite_for);
                            context.startActivity(intent);
                        } else {
                            intent = new Intent(context, IndiProfileActivity.class);
                            intent.putExtra("UserId", favourites.get(getAdapterPosition()).favourite_by);
                            context.startActivity(intent);
                        }
                    }else {
                        if (favourites.get(getAdapterPosition()).favourite_by.equals(Uconnekt.session.getUserInfo().userId)) {
                            intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("UserId", favourites.get(getAdapterPosition()).favourite_for);
                            context.startActivity(intent);
                        } else {
                            intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("UserId", favourites.get(getAdapterPosition()).favourite_by);
                            context.startActivity(intent);
                        }
                    }

                   // context.startActivity(new Intent(context, IndiProfileActivity.class));

                   /* if (context instanceof FavouriteActivity){

                    }else {
                        if (context instanceof HomeActivity) {
                            ((HomeActivity) context).addFragment(ProfileFragment.newInstance(favourites.get(getAdapterPosition()).favourite_by));
                        } else {
                            ((JobHomeActivity) context).addFragment(IndiProfileFragment.newInstance(favourites.get(getAdapterPosition()).favourite_by));
                        }
                    }*/
                    break;
            }
        }
    }
}
