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
import com.uconnekt.ui.authentication.email_authentication.EmailVerificationActivity;
import com.uconnekt.ui.employer.employer_profile.EmpProfileActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.edit_profile.IndiEditProfileActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;

public class FirebaseLogin {

    private FirebaseAuth auth;

    private void writeToDBProfiles(FirebaseData firebaseData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("users/" + firebaseData.userId);
        myRef.setValue(firebaseData);
    }

    public void firebaseLogin(final UserInfo userDetails, final Activity loginActivity, final Boolean isChecked, final CusDialogProg cusDialogProg, final Boolean pUpdate, final boolean isFinish, final boolean move) {

        auth = FirebaseAuth.getInstance();

        String id = userDetails.userId;
        final String email = id + "@uconnekt.com";
        final String password = "123456";

        //added
        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.fullName = userDetails.fullName;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.userType = userDetails.userType;
        firebaseData.userId = userDetails.userId;
        firebaseData.profileImage = userDetails.profileImage;
        firebaseData.specializationName = userDetails.specializationName;
        firebaseData.jobTitleName = userDetails.jobTitleName;
        firebaseData.rating = userDetails.rating;
        firebaseData.company_logo = userDetails.company_logo;
        //

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // there was an error
                            firebaseRagistration(userDetails,loginActivity,isChecked,cusDialogProg,pUpdate,isFinish);

                        } else {

                            //added
                            writeToDBProfiles(firebaseData);
                            //
                            Uconnekt.session.createSession(userDetails);
                            cusDialogProg.dismiss();

                            if (!isChecked) {
                                Uconnekt.session.logoutMyPre();
                            }

                            if (pUpdate) {
                                allIntent(userDetails,loginActivity);
                            }else {
                                if (isFinish)loginActivity.finish();
                                if (move)loginActivity.startActivity(new Intent(loginActivity,HomeActivity.class));
                            }
                        }
                    }
                });
    }

    private void firebaseRagistration(final UserInfo userDetails, final Activity loginActivity, final Boolean isChecked, final CusDialogProg cusDialogProg, final Boolean pUpdate, final Boolean isFinish) {
        auth = FirebaseAuth.getInstance();

        String id = userDetails.userId;
        final String email = id + "@uconnekt.com";
        final String password = "123456";

        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.fullName = userDetails.fullName;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.userType = userDetails.userType;
        firebaseData.userId = userDetails.userId;
        firebaseData.profileImage = userDetails.profileImage;
        firebaseData.specializationName = userDetails.specializationName;
        firebaseData.jobTitleName = userDetails.jobTitleName;
        firebaseData.rating = userDetails.rating;
        firebaseData.company_logo = userDetails.company_logo;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( loginActivity  , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            firebaseLogin(userDetails, loginActivity, isChecked, cusDialogProg,pUpdate,isFinish, false);

                        } else {

                            writeToDBProfiles(firebaseData);

                            Uconnekt.session.createSession(userDetails);
                            cusDialogProg.dismiss();


                            if (!isChecked) {
                                Uconnekt.session.logoutMyPre();
                            }
                            if (pUpdate) {
                                allIntent(userDetails,loginActivity);
                            }else {
                                if (isFinish)loginActivity.finish();
                            }
                        }
                    }
                });
    }

    private void allIntent(UserInfo userDetails, Activity loginActivity){
        if (userDetails.isVerified.equals("1")){
            if (userDetails.userType.equals("business")) {
                if (userDetails.isProfile.equals("1")) {
                    Intent intent = new Intent(loginActivity, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    loginActivity.startActivity(intent);
                } else {
                    Intent intent = new Intent(loginActivity, EmpProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    loginActivity.startActivity(intent);
                }
            } else {
                if (userDetails.isProfile.equals("1")) {
                    Intent intent = new Intent(loginActivity, JobHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    loginActivity.startActivity(intent);
                } else {
                    Intent intent = new Intent(loginActivity, IndiEditProfileActivity.class);
                    intent.putExtra("FROM", "First");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    loginActivity.startActivity(intent);
                }
            }
        }else {
            Intent intent = new Intent(loginActivity, EmailVerificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            loginActivity.startActivity(intent);
        }
    }
}
