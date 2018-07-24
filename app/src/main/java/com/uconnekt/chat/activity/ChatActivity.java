package com.uconnekt.chat.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.adapter.ChatAdapter;
import com.uconnekt.chat.model.BlockUsers;
import com.uconnekt.chat.model.Chatting;
import com.uconnekt.chat.model.FirebaseData;
import com.uconnekt.chat.model.FullChatting;
import com.uconnekt.chat.model.MessageCount;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.fcm.FcmNotificationBuilder;
import com.uconnekt.helper.ImageRotator;
import com.uconnekt.helper.PermissionAll;
import com.uconnekt.helper.SendImageOnFirebase;
import com.uconnekt.singleton.MyCustomMessage;
import com.uconnekt.ui.employer.activity.RequestActivity;
import com.uconnekt.util.Constant;
import com.uconnekt.volleymultipart.VolleyGetPost;
import com.uconnekt.web_services.AllAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_for_send, iv_for_pickImage, iv_for_block;
    private EditText et_for_sendTxt;
    private LinearLayout layout_for_noChat;
    private RecyclerView recycler_view;
    private ArrayList<Chatting> chattings = new ArrayList<>();
    private CusDialogProg cusDialogProg;
    private ChatAdapter chatAdapter;
    public String uID, chatNode, blockBy = "", myID = "", interviewID = "";
    private DatabaseReference chatRef, databaseReference, msgCountRef, msgCountRefMy;
    private Uri imageUri, photoURI;
    private RelativeLayout mainlayout;
  //  private ArrayList<String> keys = new ArrayList<>();
    private boolean isCamera;
    private FirebaseData firebaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) uID = extras.getString("USERID");
        myID = Uconnekt.session.getUserInfo().userId;

        cusDialogProg = new CusDialogProg(this);

        initView();

        iv_for_send.setOnClickListener(this);
        iv_for_pickImage.setOnClickListener(this);

        chatAdapter = new ChatAdapter(chattings, this);
        recycler_view.setAdapter(chatAdapter);


        if (uID != null) {
            if (Integer.parseInt(uID) > Integer.parseInt(myID)) {
                chatNode = myID + "_" + uID;
            } else {
                chatNode = uID + "_" + myID;
            }
        }

        chatRef = FirebaseDatabase.getInstance().getReference().child("chat_rooms/" + chatNode);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("block_users/" + chatNode);
        msgCountRef = FirebaseDatabase.getInstance().getReference().child("massage_count/" + uID);
        msgCountRefMy = FirebaseDatabase.getInstance().getReference().child("massage_count/" + myID);
        msgCountRefMy.setValue(new MessageCount().setValue(0));




        getInterviewID();
        getBlockList();
        getDataFromUserTable();

        //messageCount();
    }

    private void getDataFromUserTable() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.hasChild(uID)) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(uID).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    firebaseData = dataSnapshot.getValue(FirebaseData.class);
                                    chatListColling(firebaseData);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else {
                        FirebaseData firebaseData = new FirebaseData();
                        chatListColling(firebaseData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void chatListColling(final FirebaseData firebaseData) {
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    cusDialogProg.show();
                    getMessageList();
                } else {
                    layout_for_noChat.setVisibility(View.VISIBLE);
                    getMessageList();
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
    }

    public void getMessageList() {
        chattings.clear();
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chatting messageOutput = dataSnapshot.getValue(Chatting.class);

                messageOutput.company_logo = firebaseData.company_logo;
                messageOutput.specializationName = firebaseData.specializationName;
                messageOutput.rating = firebaseData.rating;
                messageOutput.jobTitleName = firebaseData.jobTitleName;
                messageOutput.fullName = firebaseData.fullName;
                messageOutput.profileImage = firebaseData.profileImage;
                chattings.add(messageOutput);
                layout_for_noChat.setVisibility(View.GONE);
                //keys.add(dataSnapshot.getKey());
                //iv_for_deleteChat.setClickable(true);
                recycler_view.scrollToPosition(chattings.size() - 1);
                chatAdapter.notifyDataSetChanged();
                cusDialogProg.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                layout_for_noChat.setVisibility(View.GONE);
                recycler_view.scrollToPosition(chattings.size() - 1);
                //keys.add(dataSnapshot.getKey());
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

    private void initView() {
        mainlayout = findViewById(R.id.mainlayout);
        iv_for_send = findViewById(R.id.iv_for_send);
        et_for_sendTxt = findViewById(R.id.et_for_sendTxt);
        recycler_view = findViewById(R.id.recycler_view);
        iv_for_pickImage = findViewById(R.id.iv_for_pickImage);
        iv_for_block = findViewById(R.id.iv_for_block);
        layout_for_noChat = findViewById(R.id.layout_for_noChat);
        findViewById(R.id.iv_for_backIco).setOnClickListener(this);
        iv_for_block.setOnClickListener(this);
        if (Uconnekt.session.getUserInfo().userType.equals("business"))
            findViewById(R.id.iv_for_menu).setOnClickListener(this);
    }

    private void writeToDBProfiles(FullChatting chatModel) {

        DatabaseReference genkey = chatRef.push()/*.setValue(chatModel)*/;
        chatModel.noadKey = genkey.getKey();
        genkey.setValue(chatModel);
        String message;
        if (chatModel.message.contains("https://firebasestorage.googleapis.com/v0/b/uconnekt-bce51.appspot.com")) {
            message = "Image";
        } else {
            message = chatModel.message;
        }
        //sendPushNotificationToReceiver(message, myID, firebaseData.firebaseToken);
    }

    private void sendPushNotificationToReceiver(String message, String userID, String otherFirebaseToken) {
        FcmNotificationBuilder.initialize().title(Uconnekt.session.getUserInfo().fullName)
                .message(message).clickaction(Uconnekt.session.getUserInfo().userType).username(Uconnekt.session.getUserInfo().fullName)
                .receiverFirebaseToken(otherFirebaseToken)
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
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra("return-data", true);
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName()
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

            File file = new File(this.getExternalCacheDir(), UUID.randomUUID() + ".jpg");
            imageUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName()
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
                            chatModel.status = "0";
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
                String txt = et_for_sendTxt.getText().toString().trim();
                if (!txt.equals("")) {

                    FullChatting chatModel = new FullChatting();
                    chatModel.message = txt;
                    chatModel.timeStamp = ServerValue.TIMESTAMP;
                    chatModel.userId = myID;
                    chatModel.date = "";
                    chatModel.time = "";
                    chatModel.location = "";
                    chatModel.status = "0";

                    if (blockBy.equals("")) {
                        // messageCount();
                        writeToDBProfiles(chatModel);
                        et_for_sendTxt.setText("");
                    } else if (blockBy.equals(myID)) {
                        MyCustomMessage.getInstance(this).snackbar(mainlayout, "You blocked " + firebaseData.fullName + ". " + "Can't send any message.");
                    } else if (!blockBy.equals("")) {
                        MyCustomMessage.getInstance(this).snackbar(mainlayout, "You blocked " + Uconnekt.session.getUserInfo().fullName + ". " + "Can't send any message.");
                    }

                } else {
                    MyCustomMessage.getInstance(this).snackbar(mainlayout, "Please enter text");
                }
                break;
            case R.id.iv_for_pickImage:
                if (blockBy.equals("")) {
                    imageUri = null;
                    photoURI = null;
                    if (new PermissionAll().RequestMultiplePermission1(ChatActivity.this))
                        userImageClick();
                } else if (blockBy.equals(myID)) {
                    MyCustomMessage.getInstance(this).snackbar(mainlayout, "You blocked " + firebaseData.fullName + ". " + "Can't send any image.");
                } else if (!blockBy.equals("")) {
                    MyCustomMessage.getInstance(this).snackbar(mainlayout, "You blocked " + Uconnekt.session.getUserInfo().fullName + ". " + "Can't send any image.");
                }
                break;
            case R.id.iv_for_block:
                blockUserDialog();
                break;
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.iv_for_menu:
                Intent intent = new Intent(this, RequestActivity.class);
                intent.putExtra("USERID", uID);
                intent.putExtra("NODE", chatNode);
                startActivity(intent);
                break;
        }
    }

    private void blockUserDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
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

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
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

    private void getBlockList() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.ic_block);
                } else if (blockBy.equals(myID)) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.ic_block);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.ic_block);
                } else if (blockBy.equals(myID)) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.ic_block);
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

    private void getInterviewID() {
        FirebaseDatabase.getInstance().getReference().child("interview/" + myID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                interviewID = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                interviewID = dataSnapshot.getValue(String.class);
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

    public void interviewRequestAPI(){

        new VolleyGetPost(this, AllAPIs.A_D_REQUEST, true, "REQUESTUPDATE", true) {
            @Override
            public void onVolleyResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")){
                        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
                       // {"status":"success","message":"Interview request accepted."}

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
                params.put("action","1");
                params.put("interviewId",interviewID);
                return params;
            }

            @Override
            public Map<String, String> setHeaders(Map<String, String> params) {
                params.put("authToken",Uconnekt.session.getUserInfo().authToken);
                return params;
            }
        }.executeVolley();
    }


}
