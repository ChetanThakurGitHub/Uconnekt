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
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.chat.history.ChatFragment;
import com.uconnekt.chat.model.EmpChatHistory;
import com.uconnekt.chat.model.History;
import com.uconnekt.chat.model.IndiChatHistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class EmpHistoryAdapter extends RecyclerView.Adapter<EmpHistoryAdapter.ViewHolder> {
    private ArrayList<EmpChatHistory> empChatHistories;
    private Context context;
    private ChatFragment chatFragment;

    public EmpHistoryAdapter(ArrayList<EmpChatHistory> empChatHistories, Context context, ChatFragment chatFragment){
        this.empChatHistories = empChatHistories;
        this.context = context;
        this.chatFragment = chatFragment;
    }

    @NonNull
    @Override
    public EmpHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buisness_chat_layout,parent,false);
        return new  EmpHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpHistoryAdapter.ViewHolder holder, int position) {
        EmpChatHistory empChatHistory = empChatHistories.get(position);

        holder.view_for_readUnread.setVisibility(empChatHistory.readUnread.equals("1")?View.VISIBLE:View.GONE);
        holder.tv_for_jobTitle.setTextColor(Color.parseColor(empChatHistory.readUnread.equals("1")?"#000000":"#999999"));

        if (empChatHistory.profileImage != null && !empChatHistory.profileImage.equals("")) {
            Picasso.with(context).load(empChatHistory.profileImage).into(holder.iv_profile_image);
        }else {
            Picasso.with(context).load(R.drawable.user).into(holder.iv_profile_image);
        }

        holder.tv_for_fullName.setText(empChatHistory.fullName);
        holder.tv_for_date.setText(time(empChatHistory));
        holder.tv_for_jobTitle.setText(empChatHistory.jobTitleName.equals("")?"NA":empChatHistory.jobTitleName);
        holder.tv_for_message.setText(empChatHistory.message);

    }

    @Override
    public int getItemCount() {
        return empChatHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_profile_image;
        private TextView tv_for_fullName,tv_for_jobTitle,tv_for_message,tv_for_date;
        private View view_for_readUnread;
        public ViewHolder(View itemView) {
            super(itemView);
            view_for_readUnread = itemView.findViewById(R.id.view_for_readUnread);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            tv_for_jobTitle = itemView.findViewById(R.id.tv_for_jobTitle);
            tv_for_message = itemView.findViewById(R.id.tv_for_message);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            itemView.findViewById(R.id.delete_layout).setOnClickListener(this);
            itemView.findViewById(R.id.layout).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout:
                    Intent intent = new Intent(context,ChatActivity.class);
                    intent.putExtra("USERID",empChatHistories.get(getAdapterPosition()).userId);
                    context.startActivity(intent);
                    break;
                case R.id.delete_layout:
                    String myID = Uconnekt.session.getUserInfo().userId;
                    EmpChatHistory empChatHistory = empChatHistories.get(getAdapterPosition());

                    History history = new History();
                    history.deleteTime = ServerValue.TIMESTAMP;
                    history.timeStamp = empChatHistory.timeStamp;
                    history.message = empChatHistory.message;
                    history.userId = empChatHistory.userId;
                    history.readUnread = "0";

                    FirebaseDatabase.getInstance().getReference().child("history").child(myID).child(empChatHistory.userId).setValue(history);
                    chatFragment.getMessageList();
                    break;
            }
        }
    }


    /**
     * @return time
     * @param empChatHistory list
     */
    private String time(EmpChatHistory empChatHistory) {
        String time = null;
        try {
            long timeStamp = (long) empChatHistory.timeStamp;

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
