package com.uconnekt.fcm;

import android.annotation.SuppressLint;
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
import com.uconnekt.ui.employer.home.HomeActivity;
import com.uconnekt.ui.individual.home.JobHomeActivity;
import com.uconnekt.util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
   // Random r = new Random();
  //  private NotificationTarget notificationTarget;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "getFrom : " + remoteMessage.getFrom().toString());
        Log.d(TAG, "getData : " + remoteMessage.getData().toString());
        Log.d(TAG, "getNotification : " + remoteMessage.getNotification().toString());

        String sdklfj = Uconnekt.session.getUserInfo().isNotify;

        if (Uconnekt.session.getUserInfo().isNotify.equals("1") && Constant.CHAT == 0) {
            if (remoteMessage.getData() != null) {
                notificationHandle(remoteMessage);
            }
        }
    }

    private void notificationHandle(RemoteMessage remoteMessage) {

        //  getData : {profile_image=http://dev.uconnekt.com.au/uploads/profile/placeholder.png, reference_id=305, body=Chetan Thakur posted a review, type=user_review, sound=default, title=Reviewed your profile, click_action=business}

        // getData : {reference_id=305, body=Chetan Thakur added to favourites, type=user_favourites, sound=default, title=Added to favourites, click_action=business}
        //bus    // getData : {reference_id=305, body=Chetan Thakur posted a review, type=user_review, sound=default, title=Reviewed your profile, click_action=business}
        //getData : {reference_id=516, body=Chetan Thakur added to favourites, type=user_favourites, sound=default, title=Added to favourites, click_action=business}
        //getData : {reference_id=305, body=Chetan Thakur recommended you, type=user_recommends, sound=default, title=Recommended you, click_action=business}

        //INDI // getData : {reference_id=305, body=Chetan Thakur recommended you, type=user_recommends, sound=default, title=Recommended you, click_action=individual}
        //getData : {reference_id=306, body=Chetan Thakur recommended you, type=user_recommends, sound=default, title=Recommended you, click_action=individual}
        // getData : {reference_id=306, body=Chetan Thakur recommended you, type=user_recommends, sound=default, title=Recommended you, click_action=individual}

        String type = remoteMessage.getData().get("type");
        String click_action = remoteMessage.getData().get("click_action");
        String profile_image = remoteMessage.getData().get("profile_image");
        String reference_id = remoteMessage.getData().get("reference_id");

        if (type != null && !type.equals("")) {
            if (click_action.equals("business")){
                if (type.equals("user_review")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "1";
                    sendNotificationAddReminder(body,title ,intentType,profile_image,reference_id);
                } else if (type.equals("user_favourites")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "2";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                } else if (type.equals("user_recommends")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "3";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("Interview_offered_action.")){
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "8";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("chat")){
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "11";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }
            }else {
                if (type.equals("user_recommends")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "4";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("user_favourites")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "5";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("profile_view")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "6";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("interview_request.")){
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "7";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("Interview_request_delete.")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "9";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("Request_action.")) {
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "10";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }else if (type.equals("chat")){
                    String body = remoteMessage.getData().get("body");
                    String title = remoteMessage.getData().get("title");
                    String intentType = "12";
                    sendNotificationAddReminder(body,title ,intentType, profile_image, reference_id);
                }
            }
        }
    }

    private void sendNotificationAddReminder(String body, String title, String intentType, String profile_image, String reference_id) {
        Intent intent = null;

        if (Uconnekt.session.isLoggedIn()) {
            if (intentType.equals("1")) {
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("type", "user_review");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("2")) {
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("type", "user_favourites");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("3")) {
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("type", "user_recommends");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("4")) {
                intent = new Intent(this, JobHomeActivity.class);
                intent.putExtra("type", "user_recommends");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("5")) {
                intent = new Intent(this, JobHomeActivity.class);
                intent.putExtra("type", "user_favourites");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("6")) {
                intent = new Intent(this, JobHomeActivity.class);
                intent.putExtra("type", "user_view");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("7")){
                intent = new Intent(this, JobHomeActivity.class);
                intent.putExtra("type", "interview_request.");
                intent.putExtra("userId", reference_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("8")){
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("type", "Interview_offered_action.");
                intent.putExtra("userId", reference_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else if (intentType.equals("9")){
                intent = new Intent(this, JobHomeActivity.class);
                intent.putExtra("type", "Interview_request_delete.");
                intent.putExtra("userId", reference_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else if (intentType.equals("10")){
                intent = new Intent(this, JobHomeActivity.class);
                intent.putExtra("type", "Interview_offered_action.");
                intent.putExtra("userId", reference_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else if (intentType.equals("11")){
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("type", "chat");
                intent.putExtra("userId", reference_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else if (intentType.equals("12")){
                intent = new Intent(this, JobHomeActivity.class);
                intent.putExtra("type", "chat");
                intent.putExtra("userId", reference_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

        } else {
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        int num = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivities(this, num, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        Uri notificaitonSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

  /*      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.new_app_ico))
                .setSmallIcon(R.drawable.ic_notifications_ico)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificaitonSound)
                .setContentIntent(pendingIntent)*/
        ;

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
                .setContentIntent(resultIntent)
                .setSound(defaultSoundUri)
                .setContent(remoteViews);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(num, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap = getRoundedCornerBitmap(myBitmap);
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




   /* public void customNotificationRequest(String title, String userType, int requestId) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        // Using RemoteViews to bind custom layouts into Notification
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.customnotification);

        Intent intent = new Intent(this, MainActivityChefOrCook.class);
        intent.putExtra("from", "notification");
        intent.putExtra("requestId", String.valueOf(requestId));
        // intent.putExtra("dataMap", (Serializable) dataMap);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent resultIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.statusbar_icon)
                .setTicker(title)
                // .setAutoCancel(true)
                .setContentIntent(resultIntent)
                .setSound(defaultSoundUri)
                .setContent(remoteViews);
        remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.app_icon);
        remoteViews.setTextViewText(R.id.title, "Ovengo");
        remoteViews.setTextViewText(R.id.text, title);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(iUniqueId, mNotificationBuilder.build());
    }*/


}
