package com.uconnekt.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.chat.model.Chatting;
import com.uconnekt.chat.model.FullChatting;
import com.uconnekt.chat.model.History;
import com.uconnekt.ui.employer.activity.ProfileActivity;
import com.uconnekt.ui.individual.activity.IndiProfileActivity;
import com.uconnekt.util.Utils;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chatting> chattings;
    private Context mContext;
    private Chatting chatting;
    private  ViewHolder viewHolder;

    public ChatAdapter(ArrayList<Chatting> chattings, Context mContext) {
        this.chattings = chattings;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
        viewHolder = new ChatAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {
        viewholderCheck(holder, position);
    }

    @Override
    public int getItemCount() {
        return chattings.size();
    }

    /**
     * Image zoom view and click
     * @param mContext activity context
     * @param position This is position
     */

    private void zoomImageDialog(Context mContext, int position) {

        final Dialog dialog = new Dialog(this.mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_zoomimage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        PhotoView iv_for_image = dialog.findViewById(R.id.iv_for_image);

        Chatting chat = chattings.get(position);
        if (chat.message != null && !chat.message.equals("")) {
            Picasso.with(mContext).load(chat.message).into(iv_for_image);
        }
        dialog.show();
    }

    /**
     * all ids find in a viewHolder
     */

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RelativeLayout layout_for_sender, layout_for_reciver, indiView, busView;
        private TextView tv_for_senderTxt, tv_for_senderTime, tv_for_reciverTxt, tv_for_reciverTime,
                tv_senderName, tv_reciverName, tv_for_address, tv_for_address2, tv_for_detail,
                tv_for_detail2,tv_for_btn,tv_for_rSend;
        private ImageView iv_for_sender, iv_for_reciver, sender_Pic, reciver_Pic;
        private LinearLayout layout_for_btn;

        ViewHolder(View itemView) {
            super(itemView);

            layout_for_sender = itemView.findViewById(R.id.layout_for_sender);
            layout_for_reciver = itemView.findViewById(R.id.layout_for_reciver);
            tv_for_reciverTime = itemView.findViewById(R.id.tv_for_reciverTime);
            tv_for_senderTime = itemView.findViewById(R.id.tv_for_senderTime);
            tv_for_senderTxt = itemView.findViewById(R.id.tv_for_senderTxt);
            tv_for_reciverTxt = itemView.findViewById(R.id.tv_for_reciverTxt);
            iv_for_sender = itemView.findViewById(R.id.iv_for_sender);
            iv_for_reciver = itemView.findViewById(R.id.iv_for_reciver);
            tv_senderName = itemView.findViewById(R.id.tv_senderName);
            sender_Pic = itemView.findViewById(R.id.sender_Pic);
            reciver_Pic = itemView.findViewById(R.id.reciver_Pic);
            tv_reciverName = itemView.findViewById(R.id.tv_reciverName);
            indiView = itemView.findViewById(R.id.indiView);
            busView = itemView.findViewById(R.id.busView);
            tv_for_address = itemView.findViewById(R.id.tv_for_address);
            tv_for_address2 = itemView.findViewById(R.id.tv_for_address2);
            tv_for_detail = itemView.findViewById(R.id.tv_for_detail);
            tv_for_detail2 = itemView.findViewById(R.id.tv_for_detail2);
            tv_for_btn = itemView.findViewById(R.id.tv_for_btn);
            layout_for_btn = itemView.findViewById(R.id.layout_for_btn);
            tv_for_rSend = itemView.findViewById(R.id.tv_for_rSend);

            itemView.findViewById(R.id.btn_for_decline).setOnClickListener(this);
            itemView.findViewById(R.id.btn_for_accept).setOnClickListener(this);
            tv_for_rSend.setOnClickListener(this);
            iv_for_sender.setOnClickListener(this);
            iv_for_reciver.setOnClickListener(this);
            tv_for_btn.setOnClickListener(this);
            reciver_Pic.setOnClickListener(this);
            tv_reciverName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_for_sender:
                    zoomImageDialog(mContext, getAdapterPosition());
                    break;
                case R.id.iv_for_reciver:
                    zoomImageDialog(mContext, getAdapterPosition());
                    break;
                case R.id.btn_for_accept:
                    interviewRequestAPI(chattings.get(getAdapterPosition()),1);
                    break;
                case R.id.btn_for_decline:
                    interviewRequestAPI(chattings.get(getAdapterPosition()),2);
                    break;
                case R.id.reciver_Pic:
                    profileView(getAdapterPosition());
                    break;
                case R.id.tv_reciverName:
                    profileView(getAdapterPosition());
                    break;
            }
        }
    }

    private void profileView(int adapterPosition){
        if (Uconnekt.session.getUserInfo().userType.equals("individual")) {
            Intent intent = new Intent(mContext, IndiProfileActivity.class);
            intent.putExtra("UserId", String.valueOf(chattings.get(adapterPosition).userId));
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            intent.putExtra("UserId", String.valueOf(chattings.get(adapterPosition).userId));
            mContext.startActivity(intent);
        }
    }

    private void history(String userId, int i){
        String myId = Uconnekt.session.getUserInfo().userId;

        History history = new History();
        history.message = i==1?"Accepted interview":"Decline interview";
        history.timeStamp = ServerValue.TIMESTAMP;
        history.userId = userId;
        history.deleteTime = ((ChatActivity)mContext).deleteTime;
        history.readUnread = "0";
        FirebaseDatabase.getInstance().getReference().child("history").child(myId).child(userId).setValue(history);

        History history2 = new History();
        history2.message = i==1?"Accepted interview":"Decline interview";
        history2.timeStamp = ServerValue.TIMESTAMP;
        history2.userId = myId;
        history2.deleteTime = ((ChatActivity)mContext).oppDeleteTime;
        history2.readUnread = "1";
        FirebaseDatabase.getInstance().getReference().child("history").child(userId).child(myId).setValue(history2);
    }

    /**
     * @return time
     */
    private String time() {
        String time = null;
        try {
            long timeStamp = (long) chatting.timeStamp;

            @SuppressLint("SimpleDateFormat")
            DateFormat f = new SimpleDateFormat("dd MMM, yyyy'T'HH:mm:ss.mmm'Z'");
            //f.setTimeZone(TimeZone.getTimeZone("GMT+10"));

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            f.setTimeZone(tz);

            //System.out.println(f.format(timeStamp));

            String CurrentString = f.format(timeStamp);
            String date = CurrentString.substring(0,12);
            int hourOfDay = Integer.parseInt(CurrentString.substring(13, 15));
            int minute = Integer.parseInt(CurrentString.substring(16, 18));

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
            time = ((hourOfDay<10)?"0"+hourOfDay:hourOfDay) + ":" + minutes + " " + status;
            time = date +" at "+time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * All the check inside the method for chat
     * @param holder   viewholder
     * @param position position of item
     */
    @SuppressLint("SetTextI18n")
    private void viewholderCheck(ViewHolder holder, int position) {

        chatting = chattings.get(position);
        String status = chatting.status;

        if (chatting.location.isEmpty()) {
            holder.indiView.setVisibility(View.GONE);
            holder.busView.setVisibility(View.GONE);
            if (chatting.userId.equals(Uconnekt.session.getUserInfo().userId)) {
                holder.layout_for_reciver.setVisibility(View.GONE);
                holder.layout_for_sender.setVisibility(View.VISIBLE);
                holder.tv_senderName.setText(Uconnekt.session.getUserInfo().fullName);
                Picasso.with(mContext).load(Uconnekt.session.getUserInfo().profileImage).into(holder.sender_Pic);
                if (chatting.message.contains("https://firebasestorage.googleapis.com/v0/b/uconnekt-bce51.appspot.com")) {
                    holder.tv_for_senderTxt.setVisibility(View.GONE);
                    holder.iv_for_sender.setVisibility(View.VISIBLE);

                    if (chatting.message != null && !chatting.message.equals("")) {
                        Picasso.with(mContext).load(chatting.message).placeholder(R.drawable.ic_background).into(holder.iv_for_sender);
                    } else {
                        Picasso.with(mContext).load(R.drawable.ic_background).fit().into(holder.iv_for_sender);
                    }
                } else {
                    holder.iv_for_sender.setVisibility(View.GONE);
                    holder.tv_for_senderTxt.setVisibility(View.VISIBLE);

                   /* //extra code for testing
                    Spannable name = new SpannableString(chatting.message);
                    name.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.darkgray)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tv_for_senderTxt.setText(name);
                    Spannable like = new SpannableString(time());
                    like.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.yellow)), 0, like.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tv_for_senderTxt.append("\n"+like);*/

                    holder.tv_for_senderTxt.setText(chatting.message);
                }
                holder.tv_for_senderTime.setText(time());

            } else {
                holder.layout_for_reciver.setVisibility(View.VISIBLE);
                holder.layout_for_sender.setVisibility(View.GONE);
                holder.tv_reciverName.setText(chatting.fullName);
                Picasso.with(mContext).load(chatting.profileImage).into(holder.reciver_Pic);
                if (chatting.message.contains("https://firebasestorage.googleapis.com/v0/b/uconnekt-bce51.appspot.com")) {
                    holder.tv_for_reciverTxt.setVisibility(View.GONE);
                    holder.iv_for_reciver.setVisibility(View.VISIBLE);
                    if (chatting.message != null && !chatting.message.equals("")) {
                        Picasso.with(mContext).load(chatting.message).placeholder(R.drawable.ic_background).into(holder.iv_for_reciver);
                    } else {
                        Picasso.with(mContext).load(R.drawable.ic_background).fit().into(holder.iv_for_reciver);
                    }
                } else {
                    holder.iv_for_reciver.setVisibility(View.GONE);
                    holder.tv_for_reciverTxt.setVisibility(View.VISIBLE);
                    holder.tv_for_reciverTxt.setText(chatting.message);
                }
                holder.tv_for_reciverTime.setText(time());
            }
        } else {
            holder.layout_for_reciver.setVisibility(View.GONE);
            holder.layout_for_sender.setVisibility(View.GONE);
            ((ChatActivity)mContext).deleteNode = chatting.noadKey;
            if (!status.equals("0")){
                holder.layout_for_btn.setVisibility(View.GONE);
                holder.tv_for_btn.setVisibility(View.VISIBLE);
                if (status.equals("1")){
                    holder.tv_for_btn.setText("Accepted interview");
                    holder.tv_for_rSend.setText("Accepted interview");
                }else {
                    holder.tv_for_btn.setText("Declined interview");
                    holder.tv_for_rSend.setText("Declined interview");
                }
            }else {
                holder.tv_for_rSend.setText("");
                holder.layout_for_btn.setVisibility(View.VISIBLE);
                holder.tv_for_btn.setVisibility(View.GONE);
            }

            if (Uconnekt.session.getUserInfo().userType.equals("individual")) {
                holder.indiView.setVisibility(View.VISIBLE);
                holder.busView.setVisibility(View.GONE);
                holder.tv_for_address.setText(chatting.location);
                holder.tv_for_detail.setText(chatting.fullName+ " has invited you to an interview with "+chatting.interviewerName +" on " + Utils.formatDate(chatting.date, "yyyy-MM-dd", "dd-MM-yyyy") + " at " + chatting.time);
            } else {
                holder.busView.setVisibility(View.VISIBLE);
                holder.indiView.setVisibility(View.GONE);
                holder.tv_for_address2.setText(chatting.location);
                holder.tv_for_detail2.setText("You have invited "+chatting.fullName +" to an interview with " +chatting.interviewerName+" on "+ Utils.formatDate(chatting.date, "yyyy-MM-dd", "dd-MM-yyyy") + " at " + chatting.time);
            }
        }
    }

    private void interviewRequestAPI(final Chatting chatting, final int i) {
        new VolleyGetPost(((ChatActivity) mContext), AllAPIs.A_D_REQUEST, true, "REQUESTUPDATE", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")){

                        FullChatting fullChatting = new FullChatting();
                        fullChatting.status = String.valueOf(i);
                        FirebaseDatabase.getInstance().getReference()
                                .child("chat_rooms/" + ((ChatActivity) mContext).chatNode).child(chatting.noadKey)
                                .child("status").setValue(fullChatting.status);

                        viewHolder.layout_for_btn.setVisibility(View.GONE);
                        viewHolder.tv_for_btn.setVisibility(View.VISIBLE);
                        viewHolder.tv_for_btn.setText(fullChatting.status.equals("1")?"Accepted interview":"Decline interview");

                        if (i==1)Utils.setAlarm((ChatActivity) mContext,chatting.date,chatting.time,chatting.userId);

                        history(chatting.userId,i);
                    }else {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetError() {

            }

            @Override
            public Map<String, String> setParams(Map<String, String> params) {
                params.put("action", i+"");
                params.put("interviewId", ((ChatActivity) mContext).interviewID);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

}
