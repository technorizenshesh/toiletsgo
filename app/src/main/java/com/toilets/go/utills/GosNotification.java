package com.toilets.go.utills;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.toilets.go.MainActivity;
import com.toilets.go.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ravindra Birla on 01,February,2023
 */
 public  class GosNotification extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String notificationType, title, meetupId, messageId;
    private String message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try
        {
            Log.d(TAG, "onMessageReceived: "+remoteMessage.toString());

            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "payload:" + remoteMessage.getData());
                Map<String,String> map = remoteMessage.getData();
                try {
                    sendNotification("","",map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            Log.d(TAG, "onMessageReceived: "+e);
        }

        Log.d(TAG, "onMessageReceived for FCM");
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "c: " + remoteMessage.getData());
            try {
                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNotification(String message, String title,Map<String,String> map) throws JSONException {

        JSONObject jsonObject = null;
        jsonObject = new JSONObject(map);
        String key = jsonObject.getString("key");
        Intent intent = new Intent();
        String key1 = jsonObject.getString("message");
        boolean showText = true;


        if(key.equalsIgnoreCase("Notification To User"))
        {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key","notification");
            if(Util.appInForeground(this))
            {
                Intent intent1 = new Intent("filter_string");
//                intent.putExtra("key", "My Data");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
            }
        }
        else
        if(showText)
        {

            Log.e(TAG, "sendNotification: ---------  "+ title);
            Log.e(TAG, "sendNotification: ---------  "+ key1);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_IMMUTABLE);
            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.logo)
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setContentText( key1)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}

