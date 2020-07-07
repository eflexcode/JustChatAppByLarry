package com.example.justchat;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class App extends Application {

    public static final String MESSAGE_ID = "messageID";
    public static final String CHANNEL_NAME = "messaging channel";

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(MESSAGE_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.createNotificationChannel(notificationChannel);

        }


    }
}
