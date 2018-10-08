package com.uconnekt.adapter.listing;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
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

    // This object helps you save/restore the open/close state of each view
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public EmpHistoryAdapter(ArrayList<EmpChatHistory> empChatHistories, Context context, ChatFragment chatFragment){
        this.empChatHistories = empChatHistories;
        this.context = context;
        this.chatFragment = chatFragment;
        viewBinderHelper.setOpenOnlyOne(true);
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

        viewBinderHelper.bind(holder.swipe_layout, empChatHistories.get(position).userId);
    }

    @Override
    public int getItemCount() {
        return empChatHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_profile_image;
        private TextView tv_for_fullName,tv_for_jobTitle,tv_for_message,tv_for_date;
        private View view_for_readUnread;
        private SwipeRevealLayout swipe_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            view_for_readUnread = itemView.findViewById(R.id.view_for_readUnread);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image);
            tv_for_fullName = itemView.findViewById(R.id.tv_for_fullName);
            tv_for_jobTitle = itemView.findViewById(R.id.tv_for_jobTitle);
            tv_for_message = itemView.findViewById(R.id.tv_for_message);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            swipe_layout = itemView.findViewById(R.id.swipe_layout);
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
                    deleteDialog(getAdapterPosition(),swipe_layout);

                    break;
            }
        }
    }

    private void deleteDialog(final int adapterPosition, final SwipeRevealLayout swipe_layout) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);


        TextView tv_for_txt = dialog.findViewById(R.id.tv_for_txt);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.delete);
        tv_for_txt.setText(R.string.delete_txt);

        dialog.findViewById(R.id.btn_for_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_for_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipe_layout.close(true);
                String myID = Uconnekt.session.getUserInfo().userId;
                EmpChatHistory empChatHistory = empChatHistories.get(adapterPosition);

                History history = new History();
                history.deleteTime = ServerValue.TIMESTAMP;
                history.timeStamp = empChatHistory.timeStamp;
                history.message = empChatHistory.message;
                history.userId = empChatHistory.userId;
                history.readUnread = "0";

                FirebaseDatabase.getInstance().getReference().child("history").child(myID).child(empChatHistory.userId).setValue(history);
                chatFragment.getMessageList();
                dialog.dismiss();
            }
        });
        dialog.show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

}
