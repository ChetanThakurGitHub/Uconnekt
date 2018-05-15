package com.uconnekt.chat.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uconnekt.R;
import com.uconnekt.chat.adapter.ChatAdapter;
import com.uconnekt.chat.model.Chatting;
import com.uconnekt.chat.model.MessageCount;
import com.uconnekt.helper.ImageRotator;
import com.uconnekt.helper.SendImageOnFirebase;
import com.uconnekt.util.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private ImageView iv_for_send, iv_for_pickImage, iv_for_deleteChat, iv_for_block;
    private EditText et_for_sendTxt;
    private TextView tv_for_noChat;
    private RecyclerView recycler_view;
    //private Session session;
    private ArrayList<Chatting> chattings;
    private ChatAdapter chatAdapter;
    private String fullname, uID, chatNode, OtherFirebaseToken, blockBy = "";
    private DatabaseReference chatRef, databaseReference, msgCountRef, msgCountRefMy;
    private Uri imageUri, photoURI;
    private FirebaseStorage storage;
    private ArrayList<String> keys;
    private Dialog pDialog;
    private boolean isCamera;
    private CoordinatorLayout coordinateLay;
    private String my = "2";

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        storage = FirebaseStorage.getInstance();
        keys = new ArrayList<>();
        //session = new Session(getContext());
        initView(view);

        iv_for_send.setOnClickListener(this);
        iv_for_pickImage.setOnClickListener(this);
       /* iv_for_deleteChat.setOnClickListener(this);*/
       /* iv_for_block.setOnClickListener(this);*/

        chattings = new ArrayList<>();
        chatAdapter = new ChatAdapter(chattings, getContext());
        recycler_view.setAdapter(chatAdapter);

     /*   if (keys.size() < 0) {
            iv_for_deleteChat.setClickable(true);
        } else {
            iv_for_deleteChat.setClickable(false);
        }
*/

        uID = "1";
        fullname = "Chetan Thakur";

        chatNode = "1_2";

       /* if (uID != null) {
            if (Integer.parseInt(uID) > Integer.parseInt(session.getUserID())) {
                chatNode = session.getUserID() + "_" + uID;
            } else {
                chatNode = uID + "_" + session.getUserID();
            }
        }*/

        chatRef = FirebaseDatabase.getInstance().getReference().child("chat_rooms/" + chatNode);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("block_users/" + chatNode);
        msgCountRef = FirebaseDatabase.getInstance().getReference().child("massage_count/" + uID);
        msgCountRefMy = FirebaseDatabase.getInstance().getReference().child("massage_count/" + 2);
        msgCountRefMy.setValue(new MessageCount().setValue(0));
        //((CaretakerHomeActivity)getContext()).navigationAdapter.notifyDataSetChanged();

        //getBlockList();
        getMessageList();

        FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("firebaseToken").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OtherFirebaseToken = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //messageCount();



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getMessageList() {
        chattings.clear();

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chatting messageOutput = dataSnapshot.getValue(Chatting.class);

                if (messageOutput.deleteby.equals("") || messageOutput.deleteby.equals(uID)) {
                    chattings.add(messageOutput);
                    tv_for_noChat.setVisibility(View.GONE);
                    keys.add(dataSnapshot.getKey());
                    //iv_for_deleteChat.setClickable(true);
                }
                recycler_view.scrollToPosition(chattings.size() - 1);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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

    private void initView(View view) {
        iv_for_send = view.findViewById(R.id.iv_for_send);
        et_for_sendTxt = view.findViewById(R.id.et_for_sendTxt);
        recycler_view = view.findViewById(R.id.recycler_view);
        iv_for_pickImage = view.findViewById(R.id.iv_for_pickImage);
        tv_for_noChat = view.findViewById(R.id.tv_for_noChat);
    }

    private void writeToDBProfiles(Chatting chatModel) {

        chatRef.push().setValue(chatModel);
        String message;
        if (chatModel.message.contains("tbicaretaker-e76f6.appspot.com")) {
            message = "Image";
        } else {
            message = chatModel.message;
        }
        //sendPushNotificationToReceiver(message, "2", OtherFirebaseToken);
    }

    private void userImageClick() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_take_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout layout_for_camera = dialog.findViewById(R.id.layout_for_camera);
        LinearLayout layout_for_gallery = dialog.findViewById(R.id.layout_for_gallery);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        EnableRuntimePermission();

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
                getActivity().startActivityForResult(i, Constant.GALLERY);
                isCamera = false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void picImage(Dialog dialog) {
        if (!SendImageOnFirebase.appManifestContainsPermission(getContext(), Manifest.permission.CAMERA) || SendImageOnFirebase.hasCameraAccess(getContext())) {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra("return-data", true);
            Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName()
                    + ".fileprovider", SendImageOnFirebase.getTemporalFile(getContext()));
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            getActivity().startActivityForResult(takePhotoIntent, Constant.CAMERA);
            isCamera = true;
            dialog.dismiss();
        }
    }

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            Toast.makeText(getContext(), "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA}, Constant.RequestPermissionCode);
        }
    } // camera parmission

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.GALLERY && resultCode == Constant.RESULT_OK && null != data) {
            imageUri = data.getData();
            if (imageUri != null) {
                uploadImage();
            }
        } else if (requestCode == Constant.CAMERA && resultCode == Constant.RESULT_OK) {

            Bitmap bm;
            File imageFile = SendImageOnFirebase.getTemporalFile(getContext());
            photoURI = Uri.fromFile(imageFile);

            bm = SendImageOnFirebase.getImageResized(getContext(), photoURI);
            int rotation = ImageRotator.getRotation(getContext(), photoURI, isCamera);
            bm = ImageRotator.rotate(bm, rotation);

            File file = new File(getActivity().getExternalCacheDir(), UUID.randomUUID() + ".jpg");
            imageUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()
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
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading....");
            progressDialog.show();

            StorageReference storageReference = storage.getReference();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Uri fireBaseUri = taskSnapshot.getDownloadUrl();
                            Chatting chatModel = new Chatting();
                            chatModel.message = fireBaseUri.toString();
                            chatModel.timeStamp = ServerValue.TIMESTAMP;
                            chatModel.uid = my;
                            chatModel.firebaseToken = FirebaseInstanceId.getInstance().getToken();
                            chatModel.name = "Chetan Thakur";
                            chatModel.deleteby = "";

                            writeToDBProfiles(chatModel);


                            imageUri = null;
                            photoURI = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    Chatting chatModel = new Chatting();
                    chatModel.message = txt;
                    chatModel.timeStamp = ServerValue.TIMESTAMP;
                    chatModel.uid = my;
                    chatModel.firebaseToken = FirebaseInstanceId.getInstance().getToken();
                    chatModel.name = "Chetan Thakur";
                    chatModel.deleteby = "";

                    if (blockBy.equals("")) {
                       // messageCount();
                        writeToDBProfiles(chatModel);
                        et_for_sendTxt.setText("");
                    } else if (blockBy.equals(my)) {
                        //Constant.snackbarTop(coordinateLay, "You blocked " + fullname + ". " + "Can't send any message.");
                    } else if (!blockBy.equals("")) {
                        //Constant.snackbarTop(coordinateLay, "You are blocked by " + session.getFullName() + ". " + "Can't send any message.");
                    }

                } else {
                    //Constant.snackbarTop(coordinateLay, getResources().getString(R.string.enter_text));
                }
                break;
            case R.id.iv_for_pickImage:
                if (blockBy.equals("")) {
                    imageUri = null;
                    photoURI = null;
                    userImageClick();
                } else if (blockBy.equals(my)) {
                   // Constant.snackbarTop(coordinateLay, "You blocked " + fullname + ". " + "Can't send any image.");
                } else if (!blockBy.equals("")) {
                 //   Constant.snackbarTop(coordinateLay, "You are blocked by " + session.getFullName() + ". " + "Can't send any image.");
                }
                break;
            /*case R.id.iv_for_deleteChat:
                chatDeleteDialog();
                break;
            case R.id.iv_for_block:
                blockUserDialog();
                break;*/
        }
    }
}
