package com.shreeyesh.firebaseServices;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.shreeyesh.R;
import com.shreeyesh.shared_pref.SharedPreference;
import com.shreeyesh.ui.HomeActivity;
import com.shreeyesh.ui.activity.SelectUserActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context mContext;
    private static final String TAG = "FCM Service";
    public static final String CHANNEL_ID = "mychannelid";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String loginOrNot, userName;
    public static String MyPREFERENCES = "Fast Connect";

    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    int m;


    int notification = 0, gallery = 0;
    String notification_count, gallery_count;


    public MyFirebaseMessagingService() {
        // empty required
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        SharedPreference.putDeviceToken(getApplicationContext(),token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mContext = this;

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();
        loginOrNot = sharedpreferences.getString(SharedPreference.IS_LOGIN, "");
        userName = sharedpreferences.getString(SharedPreference.USER_NAME, "");

        //======== get share p count ==================
        countNotification();

        String title = null, description = null, redirectId = null;
        try {
            JSONObject json = new JSONObject(remoteMessage.getData().get("data"));

            title = json.getString("title");
            description = json.getString("description");
            redirectId = json.getString("redirect_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //========== count increment ========
        activityRedirectIdCount(redirectId);

//        EventBus.getDefault().post(new HomeActivity.MessageEvent(title,description));

        sendNotification(title, description, redirectId);

    }

    private void countNotification() {
        notification_count = sharedpreferences.getString(SharedPreference.NOTIFICATION_COUNT, "");
        if (notification_count != null && notification_count.equals("")) {
            String abhijit;
        } else {
            assert notification_count != null;
            notification = Integer.parseInt(notification_count);
        }

        gallery_count = sharedpreferences.getString(SharedPreference.GALLERY_COUNT, "");
        if (gallery_count != null && gallery_count.equals("")) {
            String abhijit;
        } else {
            assert gallery_count != null;
            gallery = Integer.parseInt(gallery_count);
        }
    }

    private void activityRedirectIdCount(String redirectId) {
        if (redirectId != null) {
            switch (redirectId) {
                case "1":
                    gallery = gallery + 1;
                    editor.putString(SharedPreference.GALLERY_COUNT, String.valueOf(gallery));
                    editor.apply();
                    break;
                case "2":
//                    replaceFragment(new HomeWorkStudentListFragment());
                    break;
                case "3":
//                    replaceFragment(new EventFragment());
                    break;
                case "4":
//                    replaceFragment(new AttendanceStdFragment());
                    break;
                case "5":
//                    replaceFragment(new TimeTableFragment());
                    break;
                case "6":
//                    replaceFragment(new FeesFragment());
                    break;
                case "7":
//                    replaceFragment(new DownloadNotesFragment());
                    break;
                case "8":
//                    replaceFragment(new LeaveFragment());
                    break;
                case "9":
//                    replaceFragment(new VideoTutorialsFragment());
                    break;
                case "10":
//                replaceFragment(new VideoTutorialsFragment());
                    break;
                case "11":
                    notification = notification + 1;
                    editor.putString(SharedPreference.NOTIFICATION_COUNT, String.valueOf(notification));
                    editor.apply();
                    break;
                case "12":

//                    replaceFragment(new NotificationFragment());
                    break;
            }
        }

    }

    private void sendNotification(String title, String description, String redirectId) {

        final String NOTIFICATION_CHANNEL_ID = "10001";
        Random random = new Random();
        m = random.nextInt(9999 - 1000) + 1000;
        long[] v = {500, 1000};

        Intent intent;

        if (loginOrNot.equalsIgnoreCase("true")) {
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra("TITLE", title);
            intent.putExtra("DESCRIPTION", description);
            intent.putExtra("REDIRECT_ID", redirectId);

        } else {
            intent = new Intent(this, SelectUserActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        PendingIntent  pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews notificationSmall = new RemoteViews(getPackageName(), R.layout.notificaton_small);
        RemoteViews notificationBig = new RemoteViews(getPackageName(), R.layout.notificaton_big);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notificationSmall.setTextViewText(R.id.tv_notification_name_small, "Hey " + userName + " New notification receive.");
        notificationSmall.setTextViewText(R.id.tv_notification_title_small, title);
        notificationSmall.setTextViewText(R.id.tv_notification_desc_small, description);

        notificationBig.setTextViewText(R.id.tv_notification_name_big, "Hey " + userName + " New notification receive.");
        notificationBig.setTextViewText(R.id.tv_notification_title_big, title);
        notificationBig.setTextViewText(R.id.tv_notification_desc_big, description);
        notificationBig.setImageViewResource(R.id.iv_notification_image_big, R.drawable.app_them);

        builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle("Hey " + userName + " " + title)
//                .setContentText(description)
                .setCustomContentView(notificationSmall)
                .setCustomBigContentView(notificationBig)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(v)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent);


        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_LOW);
//        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        builder.setChannelId(CHANNEL_ID);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }

        if (notificationManager != null) {
            notificationManager.notify(m, builder.build());
        }
    }
}
