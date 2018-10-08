package com.uconnekt.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.adapter.ChatAdapter;
import com.uconnekt.chat.model.BlockUsers;
import com.uconnekt.chat.model.Chatting;
import com.uconnekt.chat.model.FirebaseData;
import com.uconnekt.chat.model.FullChatting;
import com.uconnekt.chat.model.History;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.fcm.FcmNotificationBuilder;
import com.uconnekt.helper.ImageRotator;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.helper.SendImageOnFirebase;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.base.BaseActivity;
import com.uconnekt.ui.employer.activity.RequestActivity;
import com.uconnekt.ui.employer.activity.TrackInterviewActivity;
import com.uconnekt.ui.individual.activity.TrakProgressActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_for_send;
    private ImageView iv_for_pickImage;
    private EditText et_for_sendTxt;
    private LinearLayout layout_for_noChat;
    private RecyclerView recycler_view;
    private ArrayList<Chatting> chattings = new ArrayList<>();
    private HashMap<String, Chatting> listmap = new HashMap<>();
    private CusDialogProg cusDialogProg;
    private ChatAdapter chatAdapter;
    public String uID, chatNode, blockBy = "", myID = "", interviewID = "",is_finished = "",deleteNode = "",type = "";
    private DatabaseReference chatRef, databaseReference ;
    private Uri imageUri, photoURI;
    private RelativeLayout mainlayout,layoutTyping;
    private boolean isCamera,ischeck = true,isTyping= false;
    public FirebaseData firebaseData;
    public Object deleteTime,oppDeleteTime;
    private int totalCount = 0, tempCount = 0, listIndex = 0, increment = 0;
    private String lastIndexmessagekey= "";
    private SwipeRefreshLayout chat_swipe;
    private PopupMenu popup;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Constant.CHAT = 1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) uID = extras.getString("USERID");
        myID = Uconnekt.session.getUserInfo().userId;

        getDeleteTime();
        getOppDeleteTime();

        cusDialogProg = new CusDialogProg(this);
        cusDialogProg.show();

        initView();

        iv_for_send.setOnClickListener(this);
        iv_for_pickImage.setOnClickListener(this);

        chatAdapter = new ChatAdapter(chattings, this);
        recycler_view.setAdapter(chatAdapter);

        chat_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ischeck = true;
                if (lastIndexmessagekey != null) {
                    if (totalCount >= tempCount) {
                        increment += 10;
                        getListPagination(lastIndexmessagekey);
                    } else {
                        chat_swipe.setEnabled(false);
                    }
                } else {
                    chat_swipe.setRefreshing(false);
                }
            }
        });

        if (uID != null) chatNode = (Integer.parseInt(uID) > Integer.parseInt(myID))?myID + "_" + uID:uID + "_" + myID;

        chatRef = FirebaseDatabase.getInstance().getReference().child("chat_rooms/" + chatNode);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("block_users/" + chatNode);

        getMsgCount();
        getInterviewID();
        getBlockList();
        getDataFromUserTable();
        isTyping();
        getTypingData();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0){
                    setIsTypingStatus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        et_for_sendTxt.addTextChangedListener(textWatcher);
    }

    private void getDeleteTime(){
        FirebaseDatabase.getInstance().getReference().child("history").child(myID).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    FirebaseDatabase.getInstance().getReference().child("history").child(myID).child(uID).child("readUnread").setValue("0");
                    History history = dataSnapshot.getValue(History.class);
                    if (history.deleteTime != null)
                        deleteTime = history.deleteTime;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getOppDeleteTime(){
        FirebaseDatabase.getInstance().getReference().child("history").child(uID).child(myID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("deleteTime")) {
                    oppDeleteTime = dataSnapshot.getValue();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("deleteTime")) {
                    oppDeleteTime = dataSnapshot.getValue();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;
    }

    private void getDataFromUserTable() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.hasChild(uID)) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(uID).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    firebaseData = dataSnapshot.getValue(FirebaseData.class);
                                    chatListCalling();
                                    ImageView iv_for_profile = findViewById(R.id.iv_for_profile);
                                    TextView tv_for_typing = findViewById(R.id.tv_for_typing);
                                    if (firebaseData.profileImage != null)Picasso.with(ChatActivity.this).load(firebaseData.profileImage).into(iv_for_profile);
                                    tv_for_typing.setText(firebaseData.fullName + " is typing...");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else {
                        firebaseData = new FirebaseData();
                        chatListCalling();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void chatListCalling() {
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    getMessageList();
                } else {
                    layout_for_noChat.setVisibility(View.VISIBLE);
                    getMessageList();
                    cusDialogProg.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        hideKeyboard();
        Constant.CHAT = 0;
        getDeleteTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.CHAT = 0;

    }

    public void getMessageList() {
        chattings.clear();
        chatRef.limitToLast(1).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null){
                    getListPagination(dataSnapshot.getKey());
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) getListPagination(dataSnapshot.getKey());
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getListPagination(String dataSnapshot){

        chatRef.orderByKey().endAt(dataSnapshot).limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null){
                    tempCount += 1;
                    getList(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null)getList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                chattings.clear();
                listmap.remove(dataSnapshot.getKey());
                chattings.addAll(listmap.values());
                layout_for_noChat.setVisibility(chattings.size() == 0?View.VISIBLE:View.GONE);
                shortList();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void  getList(DataSnapshot dataSnapshot) {
        chattings.clear();
        Chatting messageOutput = dataSnapshot.getValue(Chatting.class);

        messageOutput.company_logo = firebaseData.company_logo;
        messageOutput.specializationName = firebaseData.specializationName;
        messageOutput.rating = firebaseData.rating;
        messageOutput.jobTitleName = firebaseData.jobTitleName;
        messageOutput.fullName = firebaseData.fullName;
        messageOutput.profileImage = firebaseData.profileImage;

        if (deleteTime != null) {
            if ((Long) messageOutput.timeStamp > (Long) deleteTime){
                if (ischeck) {
                    lastIndexmessagekey = dataSnapshot.getKey();
                    ischeck = false;
                    listIndex = increment;
                }
                listmap.put(dataSnapshot.getKey(), messageOutput);
            }
        }else {
            if (ischeck) {
                lastIndexmessagekey = dataSnapshot.getKey();
                ischeck = false;
                listIndex = increment;
            }
            listmap.put(dataSnapshot.getKey(), messageOutput);
        }

        Collection<Chatting> demoValues = listmap.values();
        chattings.addAll(demoValues);

        layout_for_noChat.setVisibility((chattings.size() == 0)?View.VISIBLE:View.GONE);

        if (listIndex == 0) {
            recycler_view.scrollToPosition(chattings.size() - 1);
        } else if (chattings.size() != (totalCount - 10)) {
            recycler_view.scrollToPosition(20);
        } else if (chattings.size() == totalCount - 10) {
            chat_swipe.setEnabled(false);
        }

        cusDialogProg.dismiss();
        shortList();
    }

    private void shortList() {
        Collections.sort(chattings,new Comparator<Chatting>(){
            @Override
            public int compare(Chatting a1, Chatting a2) {
                if (a1.timeStamp == null || a2.timeStamp == null)
                    return -1;
                else {
                    Long long1 = Long.valueOf(String.valueOf(a1.timeStamp));
                    Long long2 = Long.valueOf(String.valueOf(a2.timeStamp));
                    return long1.compareTo(long2);
                }
            }
        });
        recycler_view.scrollToPosition(chattings.size() - 1);
        chatAdapter.notifyDataSetChanged();
        chat_swipe.setRefreshing(false);
    }

    private void initView() {
        mainlayout = findViewById(R.id.mainlayout);
        iv_for_send = findViewById(R.id.iv_for_send);
        et_for_sendTxt = findViewById(R.id.et_for_sendTxt);
        recycler_view = findViewById(R.id.recycler_view);
        iv_for_pickImage = findViewById(R.id.iv_for_pickImage);
        layout_for_noChat = findViewById(R.id.layout_for_noChat);
        findViewById(R.id.iv_for_backIco).setOnClickListener(this);

        chat_swipe = findViewById(R.id.chat_swipe);
        chat_swipe.setColorSchemeResources(R.color.yellow);

        ImageView iv_for_menu = findViewById(R.id.iv_for_menu);
        iv_for_menu.setOnClickListener(this);
        popup = new PopupMenu(this, iv_for_menu);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

        layoutTyping = findViewById(R.id.layoutTyping);

        EditText et_for_sendTxt = new EditText(this);
        et_for_sendTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isTyping = false;
            FirebaseDatabase.getInstance().getReference().child("typing").child(chatNode).child(myID).child("isTyping").setValue(0);
            // Log.e("Chat","set is typing to false");
        }
    };

    private void setIsTypingStatus() {
        if (!isTyping){
            // Log.e("Chat","set is typing to fcm");
            FirebaseDatabase.getInstance().getReference().child("typing").child(chatNode).child(myID).child("isTyping").setValue(1);
        }
        isTyping = true;
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 3000);

    }


    private void writeToDBProfiles(FullChatting chatModel) {

        DatabaseReference genkey = chatRef.push();
        chatModel.noadKey = genkey.getKey();
        genkey.setValue(chatModel);

        String message = chatModel.message.contains("https://firebasestorage.googleapis.com")?"Image":chatModel.message;

        History history = new History();
        history.message = message;
        history.timeStamp = chatModel.timeStamp;
        history.userId = uID;
        history.deleteTime = deleteTime;
        history.readUnread = "0";
        FirebaseDatabase.getInstance().getReference().child("history").child(myID).child(uID).setValue(history);

        History history2 = new History();
        history2.message = message;
        history2.timeStamp = chatModel.timeStamp;
        history2.userId = myID;
        history2.deleteTime = oppDeleteTime;
        history2.readUnread = "1";
        FirebaseDatabase.getInstance().getReference().child("history").child(uID).child(myID).setValue(history2);

        sendPushNotificationToReceiver(message, myID);
    }

    private void sendPushNotificationToReceiver(String message, String userID) {
        FcmNotificationBuilder.initialize().title(Uconnekt.session.getUserInfo().fullName)
                .message(message).clickaction(Uconnekt.session.getUserInfo().userType.equals("business")?"individual":"business")
                .username(Uconnekt.session.getUserInfo().fullName)
                .receiverFirebaseToken(firebaseData.firebaseToken)
                .profilePic(Uconnekt.session.getUserInfo().profileImage)
                .uid(userID).send();
    }

    private void userImageClick() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_take_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout layout_for_camera = dialog.findViewById(R.id.layout_for_camera);
        LinearLayout layout_for_gallery = dialog.findViewById(R.id.layout_for_gallery);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        layout_for_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picImage(dialog);
            }
        });
        layout_for_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constant.GALLERY);
                isCamera = false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void picImage(Dialog dialog) {
        if (!SendImageOnFirebase.appManifestContainsPermission(this, Manifest.permission.CAMERA) || SendImageOnFirebase.hasCameraAccess(this)) {
            Intent takePhotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra("return-data", true);
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()
                    + ".fileprovider", SendImageOnFirebase.getTemporalFile(this));
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(takePhotoIntent, Constant.CAMERA);
            isCamera = true;
            dialog.dismiss();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.GALLERY && resultCode == Constant.RESULT_OK && null != data) {
            imageUri = data.getData();
            if (imageUri != null) {
                uploadImage();
            }
        } else if (requestCode == Constant.CAMERA && resultCode == Constant.RESULT_OK) {

            Bitmap bm;
            File imageFile = SendImageOnFirebase.getTemporalFile(this);
            photoURI = Uri.fromFile(imageFile);

            bm = SendImageOnFirebase.getImageResized(this, photoURI);
            int rotation = ImageRotator.getRotation(this, photoURI, isCamera);
            bm = ImageRotator.rotate(bm, rotation);

            File file = new File(getExternalCacheDir(), UUID.randomUUID() + ".jpg");
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()
                    + ".fileprovider", file);

            if (file != null) {
                try {
                    OutputStream outStream;
                    outStream = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 80, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (imageUri != null) {
                uploadImage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    } // onActivityResult

    private void uploadImage() {
        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading....");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Uri fireBaseUri = taskSnapshot.getDownloadUrl();
                            FullChatting chatModel = new FullChatting();
                            chatModel.message = fireBaseUri.toString();
                            chatModel.timeStamp = ServerValue.TIMESTAMP;
                            chatModel.userId = myID;
                            chatModel.date = "";
                            chatModel.time = "";
                            chatModel.location = "";
                            chatModel.status = "";
                            writeToDBProfiles(chatModel);
                            imageUri = null;
                            photoURI = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_for_send:
                sendMessage();
                break;
            case R.id.iv_for_pickImage:
                if (blockBy.equals("")) {
                    imageUri = null;
                    photoURI = null;
                    if (new PermissionAll().RequestMultiplePermission1(ChatActivity.this))
                        userImageClick();
                } else if (blockBy.equals(myID)) {
                    MyCustomMessage.getInstance(this).snackbar(mainlayout, "Your unable to contact "+firebaseData.fullName+"."+" Please seek an alternate method");
                } else if (!blockBy.equals("")) {
                    MyCustomMessage.getInstance(this).snackbar(mainlayout, "Your unable to contact "+firebaseData.fullName+"."+" Please seek an alternate method");
                }
                break;
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.iv_for_menu:
                menuClick();
                break;
        }
    }

    private void sendMessage(){
        String txt = et_for_sendTxt.getText().toString().trim();
        if (!txt.equals("")) {

            FullChatting chatModel = new FullChatting();
            chatModel.message = txt;
            chatModel.timeStamp = ServerValue.TIMESTAMP;
            chatModel.userId = myID;
            chatModel.date = "";
            chatModel.time = "";
            chatModel.location = "";
            chatModel.status = "";

            if (blockBy.equals("")) {
                writeToDBProfiles(chatModel);
                et_for_sendTxt.setText("");
            } else if (blockBy.equals(myID)) {
                MyCustomMessage.getInstance(this).snackbar(mainlayout, "Your unable to contact "+firebaseData.fullName+"."+" Please seek an alternate method");
            } else if (!blockBy.equals("")) {
                MyCustomMessage.getInstance(this).snackbar(mainlayout, "Your unable to contact "+firebaseData.fullName+"."+" Please seek an alternate method");
            }

        } else {
            MyCustomMessage.getInstance(this).snackbar(mainlayout, "Please enter text");
        }
    }

    private void menuClick() {
        popup.getMenu().findItem(R.id.request).setVisible(Uconnekt.session.getUserInfo().userType.equals("business"));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.track:
                        if (Uconnekt.session.getUserInfo().userType.equals("business")) {
                            Intent intent = new Intent(ChatActivity.this, TrackInterviewActivity.class);
                            intent.putExtra("requestBy", uID);
                            intent.putExtra("interviewID", interviewID);
                            intent.putExtra("deleteNode", deleteNode);
                            intent.putExtra("chatNode", chatNode);
                            if (chattings.size() > 0)
                                intent.putExtra("startChat", String.valueOf(chattings.get(0).timeStamp));
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(ChatActivity.this, TrakProgressActivity.class);
                            intent.putExtra("requestBy", uID);
                            if (chattings.size() > 0)
                                intent.putExtra("startChat", String.valueOf(chattings.get(0).timeStamp));
                            startActivity(intent);
                        }
                        break;
                    case R.id.request:
                        if (!is_finished.equals("0")){
                            Intent intent = new Intent(ChatActivity.this, RequestActivity.class);
                            intent.putExtra("USERID", uID);
                            intent.putExtra("NODE", chatNode);
                            startActivity(intent);
                        }else {
                            Toast.makeText(ChatActivity.this, "Already sent interview request", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.block:
                        blockUserDialog();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void blockUserDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lWindowParams);

        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);
        Button btn_for_no = dialog.findViewById(R.id.btn_for_no);
        final TextView tv_for_txt = dialog.findViewById(R.id.tv_for_txt);

        final BlockUsers blockUsers = new BlockUsers();
        if (blockBy.equals("")) {
            blockUsers.blockedBy = myID;
            tv_for_txt.setText(R.string.block_user);
        } else if (blockBy.equals("Both")) {
            blockUsers.blockedBy = uID;
            tv_for_txt.setText(R.string.unblock_user);
        } else if (blockBy.equals(myID)) {
            blockUsers.blockedBy = "";
            tv_for_txt.setText(R.string.unblock_user);
        } else if (blockBy.equals(uID)) {
            blockUsers.blockedBy = "Both";
            tv_for_txt.setText(R.string.block_user);
        }

        btn_for_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.setValue(blockUsers);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void blockCustom(DataSnapshot dataSnapshot){

        blockBy = dataSnapshot.getValue(String.class);
        if (blockBy.equals("")) {
            popup.getMenu().findItem(R.id.block).setTitle("Block User");
        } else if (blockBy.equals(myID)) {
            popup.getMenu().findItem(R.id.block).setTitle("Unblock User");
        } else if (blockBy.equals("Both")) {
            popup.getMenu().findItem(R.id.block).setTitle("Unblock User");
        } else if (blockBy.equals(uID)) {
            popup.getMenu().findItem(R.id.block).setTitle("Block User");
        }
    }

    private void getBlockList() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                blockCustom(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                blockCustom(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getInterviewID() {
        FirebaseDatabase.getInstance().getReference().child("interview/" + chatNode).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) interviewID = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null)  interviewID = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackAndRequestCheck();
    }

    public void trackAndRequestCheck(){
        new VolleyGetPost(this, AllAPIs.TRACK_PROCESS, true, "Process", false) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")){
                        JSONObject object1 = object.getJSONObject("data");
                        is_finished = object1.getString("is_finished");
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
                params.put("requestBy",myID);
                params.put("requestFor", uID);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken", Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }

    private void getMsgCount() {
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalCount = (int) dataSnapshot.getChildrenCount() + 10;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isTyping(){
        mainlayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mainlayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mainlayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    FirebaseDatabase.getInstance().getReference().child("typing").child(chatNode).child(myID).child("isTyping").setValue(0);
                    recycler_view.scrollToPosition(chattings.size() - 1);
                    chatAdapter.notifyDataSetChanged();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("typing").child(chatNode).child(myID).child("isTyping").setValue(0);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("typing").child(chatNode).child(myID).child("isTyping").setValue(0);
        getDeleteTime();
    }

    private void getTypingData(){
        FirebaseDatabase.getInstance().getReference().child("typing").child(chatNode).child(uID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null){
                    int typing = dataSnapshot.getValue(Integer.class);
                    scrollShowHide(typing);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null){
                    int typing = dataSnapshot.getValue(Integer.class);
                    scrollShowHide(typing);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void scrollShowHide(final int typing){
        layoutTyping.setVisibility(typing==1?View.VISIBLE:View.GONE);
        recycler_view.scrollToPosition(chattings.size() - 1);
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !layoutTyping.isShown()) {
                    layoutTyping.setVisibility(typing==1?View.VISIBLE:View.GONE);
                } else if (dy < 0 ) {
                    layoutTyping.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}