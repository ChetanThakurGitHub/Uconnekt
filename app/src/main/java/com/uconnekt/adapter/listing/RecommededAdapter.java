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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.model.RecommendedList;
import com.uconnekt.ui.employer.activity.ProfileActivity;
import com.uconnekt.ui.individual.activity.IndiProfileActivity;
import com.uconnekt.util.Utils;

import java.util.ArrayList;

public class RecommededAdapter extends RecyclerView.Adapter<RecommededAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RecommendedList> recommendedLists;
    private String userId;
    private int myReco = 0;
    private Boolean isMove;

    public RecommededAdapter(Context context, ArrayList<RecommendedList> recommendedLists, String userId, int i, boolean isMove){
        this.context = context;
        this.recommendedLists = recommendedLists;
        this.userId = userId;
        this.myReco = i;
        this.isMove = isMove;
    }

    @NonNull
    @Override
    public RecommededAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (!userId.equals(Uconnekt.session.getUserInfo().userId)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_list_layout, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_chat_list_layout, parent, false);
        }
        return new RecommededAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendedList recommendedList = recommendedLists.get(position);
        Picasso.with(context).load(recommendedList.profileImage).into(holder.iv_for_profile);
        if (myReco == 0){
        if (Uconnekt.session.getUserInfo().fullName.equals(recommendedList.fullName)){
            holder.tv_for_rName.setText("Recommeded by you");
        }else {
            holder.tv_for_rName.setText(recommendedList.fullName.isEmpty()?"NA":"You are recommended by "+"\""+recommendedList.fullName+"\"");
        }
        }else {
            holder.tv_for_rName.setText(recommendedList.fullName.isEmpty()?"NA":"You recommended "+"\""+recommendedList.fullName+"\"");
        }
        holder.tv_for_date.setText(recommendedList.created_on.isEmpty()?"NA": Utils.parseDateToddMMyyyy(recommendedList.created_on).substring(0,10));


    }


    @Override
    public int getItemCount() {
        return recommendedLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_for_profile;
        private TextView tv_for_rName,tv_for_date;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_for_profile = itemView.findViewById(R.id.iv_for_profile);
            tv_for_rName = itemView.findViewById(R.id.tv_for_rName);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            if (isMove)itemView.findViewById(R.id.cardview).setOnClickListener(this);
            if (userId.equals(Uconnekt.session.getUserInfo().userId)) {
                itemView.findViewById(R.id.card_for_chat).setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.cardview:
                    if (Uconnekt.session.getUserInfo().userType.equals("individual")){
                    if (recommendedLists.get(getAdapterPosition()).recommend_by.equals(Uconnekt.session.getUserInfo().userId)) {
                        Intent intent = new Intent(context, IndiProfileActivity.class);
                        intent.putExtra("UserId", recommendedLists.get(getAdapterPosition()).recommend_for);
                        context.startActivity(intent);
                    }else {
                        Intent intent = new Intent(context, IndiProfileActivity.class);
                        intent.putExtra("UserId", recommendedLists.get(getAdapterPosition()).recommend_by);
                        context.startActivity(intent);
                    }
                    }else {
                        if (recommendedLists.get(getAdapterPosition()).recommend_by.equals(Uconnekt.session.getUserInfo().userId)) {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("UserId", recommendedLists.get(getAdapterPosition()).recommend_for);
                            context.startActivity(intent);
                        }else {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("UserId", recommendedLists.get(getAdapterPosition()).recommend_by);
                            context.startActivity(intent);
                        }
                    }
                    break;
                case R.id.card_for_chat:
                    Intent intent = new Intent(context,ChatActivity.class);
                    if (Uconnekt.session.getUserInfo().userType.equals("individual")){
                        if (recommendedLists.get(getAdapterPosition()).recommend_by.equals(Uconnekt.session.getUserInfo().userId)) {
                            intent.putExtra("USERID", recommendedLists.get(getAdapterPosition()).recommend_for);
                        }else {
                            intent.putExtra("USERID", recommendedLists.get(getAdapterPosition()).recommend_by);
                        }
                    }else {
                        if (recommendedLists.get(getAdapterPosition()).recommend_by.equals(Uconnekt.session.getUserInfo().userId)) {
                            intent.putExtra("USERID", recommendedLists.get(getAdapterPosition()).recommend_for);
                        }else {
                            intent.putExtra("USERID", recommendedLists.get(getAdapterPosition()).recommend_by);
                        }
                    }
                    context.startActivity(intent);
                    break;
            }
        }
    }
}
