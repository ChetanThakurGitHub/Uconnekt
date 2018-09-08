package com.uconnekt.chat.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uconnekt.R;
import com.uconnekt.adapter.listing.IndiHistoryAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.chat.model.FirebaseData;
import com.uconnekt.chat.model.History;
import com.uconnekt.chat.model.IndiChatHistory;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.ui.individual.home.JobHomeActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class IndiChatFragment extends Fragment {

    private IndiHistoryAdapter indiHistoryAdapter;
    private JobHomeActivity activity;
    private ArrayList<IndiChatHistory> indiChatHistories = new ArrayList<>();
    private HashMap<String, IndiChatHistory> hashMap = new HashMap<>();
    private LinearLayout layout_for_noData;
    private static final String ARG_PARAM1 = "param1",ARG_PARAM2 = "param2";
    private CusDialogProg cusDialogProg;

    public static IndiChatFragment newInstance(String notificationId, String userId) {
        IndiChatFragment fragment = new IndiChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, notificationId);
        args.putSerializable(ARG_PARAM2, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null){
            String type =  getArguments().getString(ARG_PARAM1);
            String userID =  getArguments().getString(ARG_PARAM2);
            if (type.equals("interview_request.")| type.equals("Interview_request_delete.")|type.equals("chat")|type.equals("Interview_offered_action.")){
               Intent intent = new Intent(activity,ChatActivity.class);
                intent.putExtra("USERID",userID);
                activity.startActivity(intent);
            }
        }
        indiHistoryAdapter = new IndiHistoryAdapter(indiChatHistories,activity,this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indi_chat, container, false);

        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);
        recycler_view.setAdapter(indiHistoryAdapter);

        cusDialogProg = new CusDialogProg(activity);
        cusDialogProg.show();

        try {
            chatListCalling();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    private void chatListCalling() {
        FirebaseDatabase.getInstance().getReference().child("history").child(Uconnekt.session.getUserInfo().userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    getMessageList();
                } else {
                    layout_for_noData.setVisibility(View.VISIBLE);
                    getMessageList();
                    cusDialogProg.dismiss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getMessageList(){
        hashMap.clear();
        FirebaseDatabase.getInstance().getReference().child("history").child(Uconnekt.session.getUserInfo().userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    final History history = dataSnapshot.getValue(History.class);
                    getDataFromUser(history);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    final History history = dataSnapshot.getValue(History.class);
                    getDataFromUser(history);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    indiChatHistories.clear();
                    hashMap.remove(dataSnapshot.getKey());
                    indiChatHistories.addAll(hashMap.values());
                    shortList(indiChatHistories);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getDataFromUser(final History history){
        FirebaseDatabase.getInstance().getReference().child("users").child(history.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    cusDialogProg.dismiss();
                    FirebaseData messageOutput = dataSnapshot.getValue(FirebaseData.class);
                    if (messageOutput != null) {
                        IndiChatHistory indiChatHistory = new IndiChatHistory();
                        indiChatHistory.fullName = messageOutput.fullName;
                        indiChatHistory.specializationName = messageOutput.specializationName;
                        indiChatHistory.profileImage = messageOutput.profileImage;
                        indiChatHistory.userId = messageOutput.userId;
                        indiChatHistory.rating = messageOutput.rating;
                        indiChatHistory.company_logo = messageOutput.company_logo;
                        indiChatHistory.timeStamp = history.timeStamp;
                        indiChatHistory.message = history.message;
                        indiChatHistory.readUnread = history.readUnread;
                        indiChatHistory.deleteTime = history.deleteTime;

                        Long deleteTime = (Long) indiChatHistory.deleteTime;
                        if (deleteTime != null) {
                            if ((Long) indiChatHistory.timeStamp > deleteTime)
                                hashMap.put(dataSnapshot.getKey(), indiChatHistory);
                        } else hashMap.put(dataSnapshot.getKey(), indiChatHistory);

                        Collection<IndiChatHistory> demoValues = hashMap.values();
                        indiChatHistories.clear();
                        indiChatHistories.addAll(demoValues);
                        cusDialogProg.dismiss();
                        shortList(indiChatHistories);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void shortList(ArrayList<IndiChatHistory> indiChatHistories) {
        Collections.sort(indiChatHistories,new Comparator<IndiChatHistory>(){
            @Override
            public int compare(IndiChatHistory a1, IndiChatHistory a2) {
                if (a1.timeStamp == null || a2.timeStamp == null)
                    return -1;
                else {
                    Long long1 = Long.valueOf(String.valueOf(a1.timeStamp));
                    Long long2 = Long.valueOf(String.valueOf(a2.timeStamp));
                    return long2.compareTo(long1);
                }
            }
        });
        layout_for_noData.setVisibility(indiChatHistories.size() == 0?View.VISIBLE:View.GONE);
        indiHistoryAdapter.notifyDataSetChanged();
    }
    @Override

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (JobHomeActivity) context;
    }
}
