package com.uconnekt.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uconnekt.R;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.ui.authentication.splash.SplashActivity;
import com.uconnekt.ui.employer.activity.TrackInterviewActivity;
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.activity.TrakProgressActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    // Random r = new Random();
  //  private NotificationTarget notificationTarget;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "getFrom : " + remoteMessage.getFrom());
        Log.d(TAG, "getData : " + remoteMessage.getData().toString());
        Log.d(TAG, "getNotification : " + remoteMessage.getNotification().toString());

        if (Uconnekt.session.getUserInfo().isNotify.equals("1") && Constant.CHAT == 0) {
            if (remoteMessage.getData() != null) {
                notificationHandle(remoteMessage);
            }
        }else {
            Intent intent = new Intent(this, (Uconnekt.session.getUserInfo().userType.equals("business"))?TrackInterviewActivity.class:TrakProgressActivity.class);
            sendBroadcast(intent);
        }
    }

    private void notificationHandle(RemoteMessage remoteMessage) {

       // delete//{mutable-content=true, profile_image=http://dev.uconnekt.com.au/uploads/profile/placeholder.png, reference_id=349, body=Interview request has been deleted  by thakur, type=Interview_request_delete., sound=default, title=Action on request., click_action=individual}// calsel//{mutable-content=true, profile_image=http://dev.uconnekt.com.au/uploads/profile/placeholder.png, reference_id=349, body=Interview request has been deleted  by thakur, type=Interview_request_delete., sound=default, title=Action on request., click_action=individual}
       //view profile {mutable-content=true, profile_image=http://www.dev.uconnekt.com.au/uploads/profile/medium/71c2e03f6eeabe121aea8a9e306db1d9.jpg, reference_id=349, body=Chetan thakur viewed your profile, type=profile_view, sound=default, title=Viewed your profile, click_action=business}
        //{other_key=true, profile_image=http://www.uconnekt.com.au/uploads/profile/medium/9ab7c31f85fd887073a1dc689ff7fc45.jpg, reference_id=5, username=Chetan Thakur, body=chetan thkafjlasdfjlkfjslfj, type=chat, title=Chetan Thakur, click_action=individual}

        String type = remoteMessage.getData().get("type");
        String click_action = remoteMessage.getData().get("click_action");
        String profile_image = remoteMessage.getData().get("profile_image");
        String reference_id = remoteMessage.getData().get("reference_id");


        if (type != null && !type.equals("")) {
            if (click_action.equals("business")){
                switch (type) {
                    case "user_review": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "1";
                        sendBroadCast();
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "user_favourites": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "2";
                        sendBroadCast();
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "user_recommends": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "3";
                        sendBroadCast();
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "Request_action.": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "8";
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "chat": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "11";
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "profile_view": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "13";
                        sendBroadCast();
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                }
            }else {
                switch (type) {
                    case "user_recommends": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "4";
                        sendBroadCast();
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "user_favourites": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "5";
                        sendBroadCast();
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "profile_view": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "6";
                        sendBroadCast();
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "interview_request.": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "7";
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "Interview_request_delete.": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "9";
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "Interview_offered_action.": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "10";
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                    case "chat": {
                        String body = remoteMessage.getData().get("body");
                        String title = remoteMessage.getData().get("title");
                        String intentType = "12";
                        sendNotificationAddReminder(body, title, intentType, profile_image, reference_id);
                        break;
                    }
                }
            }
        }
    }

    private void sendBroadCast(){
        Intent intent =  new Intent("ProfileCountBroadcast");
        sendBroadcast(intent);
    }

    private void sendNotificationAddReminder(String body, String title, String intentType, String profile_image, String reference_id) {
        Intent intent = null;

        if (Uconnekt.session.isLoggedIn()) {
            switch (intentType) {
                case "1":
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("type", "user_review");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "2":
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("type", "user_favourites");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "3":
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("type", "user_recommends");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "4":
                    intent = new Intent(this, JobHomeActivity.class);
                    intent.putExtra("type", "user_recommends");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "5":
                    intent = new Intent(this, JobHomeActivity.class);
                    intent.putExtra("type", "user_favourites");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "6":
                    intent = new Intent(this, JobHomeActivity.class);
                    intent.putExtra("type", "user_view");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "7":
                    intent = new Intent(this, JobHomeActivity.class);
                    intent.putExtra("type", "interview_request.");
                    intent.putExtra("userId", reference_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "8":
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("type", "Request_action.");
                    intent.putExtra("userId", reference_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "9":
                    intent = new Intent(this, JobHomeActivity.class);
                    intent.putExtra("type", "Interview_request_delete.");
                    intent.putExtra("userId", reference_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "10":
                    intent = new Intent(this, JobHomeActivity.class);
                    intent.putExtra("type", "Interview_offered_action.");
                    intent.putExtra("userId", reference_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "11":
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("type", "chat");
                    intent.putExtra("userId", reference_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "12":
                    intent = new Intent(this, JobHomeActivity.class);
                    intent.putExtra("type", "chat");
                    intent.putExtra("userId", reference_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "13":
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("type", "profile_view");
                    intent.putExtra("userId", reference_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }

        } else {
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        int num = (int) System.currentTimeMillis();

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationChannel mChannel;

  /*      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.new_app_ico))
                .setSmallIcon(R.drawable.ic_notifications_ico)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificaitonSound)
                .setContentIntent(pendingIntent); */

        //custom notification .....

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.customnotification);
        PendingIntent resultIntent = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap bitmap = getBitmapFromURL(profile_image);

        remoteViews.setImageViewBitmap(R.id.iv_for_profile, bitmap);
        if (intentType.equals("11")|intentType.equals("12")){
            remoteViews.setTextViewText(R.id.title, title);
        }else {
            remoteViews.setTextViewText(R.id.title, getString(R.string.app_name));
        }

        remoteViews.setTextViewText(R.id.text, body);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notifications_ico)
                .setTicker(body)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(resultIntent)
                .setSound(defaultSoundUri)
                .setNumber(1)
                .setContent(remoteViews);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        Notification notification = notificationBuilder.build();
        int notificatoinCount = +1;
        ShortcutBadger.applyNotification(getApplicationContext(), notification, notificatoinCount);
        assert notificationManager != null;
        notificationManager.notify(num, notification);
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
           // myBitmap = getRoundedCornerBitmap(myBitmap);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 100;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

       /* if (intentType.equals("7")) {
            NotificationCompat.Builder notificationBuilder1 = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.new_app_ico))
                    .setSmallIcon(R.drawable.ic_notifications_ico)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(this.body)
                    .setAutoCancel(true)
                    .setSound(notificaitonSound)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager1.notify(0, notificationBuilder1.build());
        }*/

}
