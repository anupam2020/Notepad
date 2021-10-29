package com.sbdev.notepad_project;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();

        final String CHANNEL_ID="HEADS_UP_NOTIFICATION";

        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"Heads Up Notification", NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        Notification.Builder notification=new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.color_note)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1,notification.build());



    }
}