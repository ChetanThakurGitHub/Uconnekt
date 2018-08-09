package com.uconnekt.adapter.listing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.chat.history.IndiChatFragment;
import com.uconnekt.chat.model.Chatting;
import com.uconnekt.chat.model.History;
import com.uconnekt.chat.model.IndiChatHistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class IndiHistoryAdapter extends RecyclerView.Adapter<IndiHistoryAdapter.ViewHolder> {
    private ArrayList<IndiChatHistory> indiChatHistories;
    private Context context;
    private IndiChatFragment indiChatFragment;

    public IndiHistoryAdapter(ArrayList<IndiChatHistory> indiChatHistories, Context context, IndiChatFragment indiChatFragment){
        this.indiChatHistories = indiChatHistories;
        this.context = context;
        this.indiChatFragment = indiChatFragment;
    }

    @NonNull
    @Override
    public IndiHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_chat_layout,parent,false);
        return new  IndiHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndiHistoryAdapter.ViewHolder holder, int position) {
        IndiChatHistory indiChatHistory = indiChatHistories.get(position);
        setData(indiChatHistory,holder);
    }

    @Override
    public int getItemCount() {
        return indiChatHistories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_profile_image,iv_company_logo;
        private TextView tv_for_fullName,tv_for_specializationName,tv_for_message,tv_for_date;
        private RatingBar ratingBar;
        private View view_for_readUnread;
        public ViewHolder(View itemView) {
            super(itemView);
            view_for_readUnread = itemView.findViewById(R.id.view_for_readUnread);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image);
            iv_company_logo = itemView.findViewById(R.id.iv_company_logo);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            tv_for_specializationName = itemView.findViewById(R.id.tv_for_specializationName);
            tv_for_message = itemView.findViewById(R.id.tv_for_message);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            itemView.findViewById(R.id.delete_layout).setOnClickListener(this);
            itemView.findViewById(R.id.layout).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout:
                    Intent intent = new Intent(context,ChatActivity.class);
                    intent.putExtra("USERID",indiChatHistories.get(getAdapterPosition()).userId);
                    context.startActivity(intent);
                    break;
                case R.id.delete_layout:
                    String myID = Uconnekt.session.getUserInfo().userId;
                    IndiChatHistory indiChatHistory = indiChatHistories.get(getAdapterPosition());

                    History history = new History();
                    //history.deleteby = myID;
                    history.deleteTime = ServerValue.TIMESTAMP;
                    history.timeStamp = indiChatHistory.timeStamp;
                    history.message = indiChatHistory.message;
                    history.userId = indiChatHistory.userId;
                    history.readUnread = "0";

                    FirebaseDatabase.getInstance().getReference().child("history").child(myID).child(indiChatHistory.userId).setValue(history);
                    indiChatFragment.getMessageList();



                   /* DeleteChat deleteChat = new DeleteChat();
                    deleteChat.deleteby = myID;
                    deleteChat.timeStamp = ServerValue.TIMESTAMP;

                    String chatNode = (Integer.parseInt(uID) > Integer.parseInt(myID))?myID + "_" + uID:uID + "_" + myID;
                    FirebaseDatabase.getInstance().getReference().child("delete_chat").child(chatNode).setValue(deleteChat);*/

                    /*deleteRef.setValue(deleteChat);
                    chattings.clear();
                    chatAdapter.notifyDataSetChanged();*/
                    break;
            }
        }
    }


    private void setData(IndiChatHistory indiChatHistory, ViewHolder holder){
        holder.view_for_readUnread.setVisibility(indiChatHistory.readUnread.equals("1")?View.VISIBLE:View.GONE);
        holder.tv_for_specializationName.setTextColor(Color.parseColor(indiChatHistory.readUnread.equals("1")?"#000000":"#999999"));

        if (indiChatHistory.profileImage != null && !indiChatHistory.profileImage.equals("")) {
            Picasso.with(context).load(indiChatHistory.profileImage).into(holder.iv_profile_image);
        } else {
            Picasso.with(context).load(R.drawable.user).into(holder.iv_profile_image);
        }

        if (indiChatHistory.company_logo != null && !indiChatHistory.company_logo.equals("")) {
            Picasso.with(context).load(indiChatHistory.company_logo).into(holder.iv_company_logo);
        } else {
            Picasso.with(context).load(R.drawable.user).into(holder.iv_company_logo);
        }

        holder.tv_for_fullName.setText(indiChatHistory.fullName);
        holder.tv_for_date.setText(time(indiChatHistory));
        holder.tv_for_specializationName.setText(indiChatHistory.specializationName.equals("") ? "NA" : indiChatHistory.specializationName);
        holder.tv_for_message.setText(indiChatHistory.message);
        holder.ratingBar.setRating(indiChatHistory.rating.isEmpty() ? 0 : Float.parseFloat(indiChatHistory.rating));
    }


    /**
     * @return time
     * @param indiChatHistory list
     */
    private String time(IndiChatHistory indiChatHistory) {
        String time = null;
        try {
            long timeStamp = (long) indiChatHistory.timeStamp;

            @SuppressLint("SimpleDateFormat")
            DateFormat f = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.mmm'Z'");
            System.out.println(f.format(timeStamp));

            String currentString = f.format(timeStamp);
            time = currentString.substring(0,10);
        /*    int hourOfDay = Integer.parseInt(CurrentString.substring(11, 13));
            int minute = Integer.parseInt(CurrentString.substring(14, 16));

            String status, minutes;

            if (hourOfDay > 12) {
                hourOfDay -= 12;
                status = "PM";
            } else if (hourOfDay == 0) {
                hourOfDay += 12;
                status = "AM";
            } else if (hourOfDay == 12) {
                status = "PM";
            } else {
                status = "AM";
            }

            minutes = (minute < 10) ? "0" + minute : String.valueOf(minute);
            time = hourOfDay + ":" + minutes + " " + status;*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

}
