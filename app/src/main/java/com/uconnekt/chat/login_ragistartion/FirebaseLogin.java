package com.uconnekt.chat.login_ragistartion;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.chat.model.FirebaseData;
import com.uconnekt.custom_view.CusDialogProg;
import com.uconnekt.model.UserInfo;
import com.uconnekt.ui.employer.employer_profile.EmpProfileActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.ui.individual.individual_profile.activity.JobProfileActivity;

public class FirebaseLogin {

    private FirebaseAuth auth;

    private void writeToDBProfiles(FirebaseData firebaseData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("users/" + firebaseData.uid);
        myRef.setValue(firebaseData);
    }

    public void firebaseLogin(final UserInfo userDetails, final Activity loginActivity, final Boolean isChecked, final CusDialogProg cusDialogProg) {

        auth = FirebaseAuth.getInstance();

        String id = userDetails.userId;
        final String email = id + "@uconneckt.com";
        final String password = "123456";

        //added
        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.name = userDetails.fullName;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.userType = userDetails.userType;
        firebaseData.uid = userDetails.userId;
        //

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // there was an error
                            firebaseRagistration(userDetails,loginActivity,isChecked,cusDialogProg);

                        } else {

                            //added
                            writeToDBProfiles(firebaseData);
                            //
                            Uconnekt.session.createSession(userDetails);
                            cusDialogProg.dismiss();

                            if (!isChecked) {
                                Uconnekt.session.logoutMyPre();
                            }
                            if (userDetails.userType.equals("business")) {
                                if (userDetails.isProfile.equals("1")) {
                                    Intent intent = new Intent(loginActivity, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }else {
                                    Intent intent = new Intent(loginActivity, EmpProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }
                            } else {
                                if (userDetails.isProfile.equals("1")){
                                    Intent intent = new Intent(loginActivity, JobHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }else {
                                    Intent intent = new Intent(loginActivity, JobProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }
                            }
                        }
                    }
                });
    }

    public void firebaseRagistration(final UserInfo userDetails, final Activity loginActivity, final Boolean isChecked, final CusDialogProg cusDialogProg) {
        auth = FirebaseAuth.getInstance();

        String id = userDetails.userId;
        final String email = id + "@uconneckt.com";
        final String password = "123456";

        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.name = userDetails.fullName;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.userType = userDetails.userType;
        firebaseData.uid = userDetails.userId;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( loginActivity  , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            firebaseLogin(userDetails, loginActivity, isChecked, cusDialogProg);

                        } else {

                            writeToDBProfiles(firebaseData);

                            Uconnekt.session.createSession(userDetails);
                            cusDialogProg.dismiss();

                            if (!isChecked) {
                                Uconnekt.session.logoutMyPre();
                            }
                            if (userDetails.userType.equals("business")) {
                                if (userDetails.isProfile.equals("1")) {
                                    Intent intent = new Intent(loginActivity, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }else {
                                    Intent intent = new Intent(loginActivity, EmpProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }
                            } else {
                                if (userDetails.isProfile.equals("1")){
                                    Intent intent = new Intent(loginActivity, JobHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }else {
                                    Intent intent = new Intent(loginActivity, JobProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    loginActivity.startActivity(intent);
                                }
                            }
                        }
                    }
                });
    }
}
