package com.toilets.go.utills;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.toilets.go.MainActivity;
import com.toilets.go.R;
import com.toilets.go.ui.ProviderHome.ProviderHomeActivity;
import com.toilets.go.ui.UserSide.HomeUserAct;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ravindra Birla on 06,February,2023
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMessagingService";

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceivedonMessageReceived: " + remoteMessage.toString());
        Log.d(TAG, "onMessageReceivedonMessageReceived: " + remoteMessage.getData());
        Log.d(TAG, "onMessageReceivedonMessageReceived: " + remoteMessage.getMessageId());
        Log.d(TAG, "onMessageReceivedonMessageReceived: " + remoteMessage.getMessageType());
        Log.d(TAG, "onMessageReceivedonMessageReceived: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "onMessageReceivedonMessageReceived: " + remoteMessage.getNotification().getBody());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
              //  scheduleJob();
            } else {
                // Handle message within 10 seconds
            //    handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        try
        {
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "payload:" + remoteMessage.getData());
                Map<String,String> map = remoteMessage.getData();
                try {
                    sendNotification("","");
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
            Log.d(TAG, "c: " + remoteMessage.getNotification());
            try {
                sendNotification(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
    }

    @Override
    public void onSendError(@NonNull String msgId, @NonNull Exception exception) {
        super.onSendError(msgId, exception);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    protected Intent getStartCommandIntent(Intent originalIntent) {
        return super.getStartCommandIntent(originalIntent);
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

    private void sendNotification( String title ,String message  ) throws JSONException {
        Intent intent = new Intent();

       /* JSONObject jsonObject = null;
        jsonObject = new JSONObject(map);
        String key = jsonObject.getString("key");
        String key1 = jsonObject.getString("message");
        boolean showText = true;

        if(key.equalsIgnoreCase("call purchased"))
        {
            try {
                intent = new Intent(this, HomeUserAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("key","notification");
                if(Util.appInForeground(this))
                {
                    Intent intent1 = new Intent("filter_string");
//                  intent.putExtra("key", "My Data");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
                }
            }catch (Exception e)
            {
            }

        }else
            if(key.equalsIgnoreCase("Notification To User"))
        {
            intent = new Intent(this, HomeUserAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key","notification");
            if(Util.appInForeground(this))
            {
                Intent intent1 = new Intent("filter_string");
//                intent.putExtra("key", "My Data");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
            }
        }

        if(showText)
        {*/
if (Util.appInForeground(this)){
    if(title.equalsIgnoreCase("Booking"))
    {
        intent = new Intent(this, HomeUserAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("key","notification");
        if(Util.appInForeground(this))
        {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                boolean result= Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isInteractive()|| Build.VERSION.SDK_INT< Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isScreenOn();

                if (!result){
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MH24_SCREENLOCK");
                    wl.acquire(10000);
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MH24_SCREENLOCK");
                    wl_cpu.acquire(10000);
                }

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
                if (!isScreenOn) {
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
                    wl.acquire(3000); //set your time in milliseconds
                }
                    Intent intent1 = new Intent("Booking");
                    intent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
            /*    Intent serviceIntent = new Intent(getApplicationContext(),
                        HeadsUpNotificationService.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("inititator", inititator);
                mBundle.putString("call_type",call_type);
                mBundle.putString("userimage",userimage);
                mBundle.putString("channelName",channelName);
                mBundle.putString("token",token);
                serviceIntent.putExtras(mBundle);
                ContextCompat.startForegroundService(getApplicationContext(),
                        serviceIntent);*/

            }
            else {

                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                boolean result= Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive()|| Build.VERSION.SDK_INT< Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isScreenOn();

                if (!result){
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MH24_SCREENLOCK");
                    wl.acquire(10000);
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MH24_SCREENLOCK");
                    wl_cpu.acquire(10000);
                }

//                PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MH24_SCREENLOCK");
//                wl.acquire(10000);
//                PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MH24_SCREENLOCK");
//                wl_cpu.acquire(10000);

//                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
//                if (!isScreenOn) {
//                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
//                    wl.acquire(3000); //set your time in milliseconds
//                }
/*
                Intent serviceIntent = new Intent(getApplicationContext(),
                        HeadsUpNotificationService.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("inititator", userName);
                mBundle.putString("call_type",userName);
                mBundle.putString("channelName",channelName);
                mBundle.putString("token",token);
                mBundle.putString("userimage",userimage);
                serviceIntent.putExtras(mBundle);
                ContextCompat.startForegroundService(getApplicationContext()
                        , serviceIntent);*/

                    Intent intent1 = new Intent("Booking");
                    intent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
                    //put whatever data you want to send, if any

                }


        }
    }
}else {
    intent = new Intent(this, ProviderHomeActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
}
            Log.e(TAG, "sendNotification: ---------  "+ title);
            Log.e(TAG, "sendNotification: ---------  "+ message);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_IMMUTABLE);
            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setPriority(Notification.PRIORITY_DEFAULT)
                             .setContentTitle( title)
                            .setContentText( message)
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
    //}

}
