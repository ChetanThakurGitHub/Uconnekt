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
import com.uconnekt.adapter.listing.EmpHistoryAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.activity.ChatActivity;
import com.uconnekt.chat.model.Chatting;
import com.uconnekt.chat.model.EmpChatHistory;
import com.uconnekt.chat.model.FirebaseData;
import com.uconnekt.chat.model.History;
import com.uconnekt.ui.employer.home.HomeActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ChatFragment extends Fragment {

    private EmpHistoryAdapter empHistoryAdapter;
    private HomeActivity activity;
    private ArrayList<EmpChatHistory> empChatHistorie = new ArrayList<>();
    private HashMap<String, EmpChatHistory> hashMap = new HashMap<>();
    private LinearLayout layout_for_noData;
    private static final String ARG_PARAM1 = "param1",ARG_PARAM2 = "param2";

    public static ChatFragment newInstance(String notificationId, String userId) {
        ChatFragment fragment = new ChatFragment();
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
            if (type.equals("Interview_offered_action.")){
                Intent intent = new Intent(activity,ChatActivity.class);
                intent.putExtra("USERID",userID);
                activity.startActivity(intent);
            }else if (type.equals("Interview_request_delete.")|type.equals("chat")){
                Intent intent = new Intent(activity,ChatActivity.class);
                intent.putExtra("USERID",userID);
                activity.startActivity(intent);
            }
        }
        empHistoryAdapter = new EmpHistoryAdapter(empChatHistorie,activity,this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat2, container, false);

        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);
        layout_for_noData = view.findViewById(R.id.layout_for_noData);
        recycler_view.setAdapter(empHistoryAdapter);

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
                    for (int i = 0; i < empChatHistorie.size(); i++) {
                        if (empChatHistorie.get(i).userId != null) {
                            if (empChatHistorie.get(i).userId.equals(dataSnapshot.getKey())) {
                                empChatHistorie.remove(i);
                            }
                        }
                    }
                    shortList(empChatHistorie);
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
                    FirebaseData messageOutput = dataSnapshot.getValue(FirebaseData.class);

                    EmpChatHistory empChatHistory = new EmpChatHistory();
                    if (messageOutput.fullName!=null)empChatHistory.fullName = messageOutput.fullName;
                    empChatHistory.jobTitleName = messageOutput.jobTitleName;
                    empChatHistory.profileImage = messageOutput.profileImage;
                    empChatHistory.userId = messageOutput.userId;
                    empChatHistory.timeStamp = history.timeStamp;
                    empChatHistory.message = history.message;
                    empChatHistory.readUnread = history.readUnread;
                    empChatHistory.deleteTime = history.deleteTime;

                    Long deleteTime = (Long)empChatHistory.deleteTime;
                    if (deleteTime!=null) {
                        if ((Long) empChatHistory.timeStamp > deleteTime) hashMap.put(dataSnapshot.getKey(),empChatHistory);
                    }else hashMap.put(dataSnapshot.getKey(),empChatHistory);

                    Collection<EmpChatHistory> demoValues = hashMap.values();
                    empChatHistorie.clear();
                    empChatHistorie.addAll(demoValues);
                    shortList(empChatHistorie);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void shortList(ArrayList<EmpChatHistory> empChatHistorie) {
        Collections.sort(empChatHistorie,new Comparator<EmpChatHistory>(){
            @Override
            public int compare(EmpChatHistory a1, EmpChatHistory a2) {
                if (a1.timeStamp == null || a2.timeStamp == null)
                    return -1;
                else {
                    Long long1 = Long.valueOf(String.valueOf(a1.timeStamp));
                    Long long2 = Long.valueOf(String.valueOf(a2.timeStamp));
                    return long2.compareTo(long1);
                }
            }
        });
        layout_for_noData.setVisibility(empChatHistorie.size() == 0?View.VISIBLE:View.GONE);
        empHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

}
