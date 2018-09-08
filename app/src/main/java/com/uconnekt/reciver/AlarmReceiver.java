package com.uconnekt.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.fcm.FcmNotificationBuilder;
import com.uconnekt.util.Constant;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        Bundle extras = arg1.getExtras();
        String value = extras.getString("ID");
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone ringtone = RingtoneManager.getRingtone(arg0, alarmUri);
        ringtone.play();
        sendPushNotificationToReceiver(value);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ringtone.stop();
            }
        }, 10000);
    }

    private void sendPushNotificationToReceiver(String value) {
        Constant.CHAT = 0;
        FcmNotificationBuilder.initialize().title("Reminder")
                .message("You've got an interview scheduled after 30 minutes").
                clickaction(Uconnekt.session.getUserInfo().userType)
                .username(Uconnekt.session.getUserInfo().fullName)
                .receiverFirebaseToken(FirebaseInstanceId.getInstance().getToken())
                .profilePic(Uconnekt.session.getUserInfo().profileImage)
                .uid(value).send();
    }

}
