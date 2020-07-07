package com.example.justchat.cloudmessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.justchat.App;
import com.example.justchat.MessageActivity;
import com.example.justchat.MessageActivityChat;
import com.example.justchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingServiceClass extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid());

        Token token1 = new Token(token);

        databaseReference.setValue(token1);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            String senderId = remoteMessage.getData().get("senderId");
            String senderName = remoteMessage.getData().get("senderName");
            String message = remoteMessage.getData().get("message");

            Intent intent = new Intent(this, MessageActivityChat.class);
            intent.putExtra("messageUId", senderId);
            Bundle bundle = new Bundle();
            bundle.putString("messageUId", senderId);
            intent.putExtras(bundle);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 34, intent, PendingIntent.FLAG_ONE_SHOT);

            if (Build.VERSION.SDK_INT >= 26) {
                Notification.Builder builder = new Notification.Builder(this, App.MESSAGE_ID)
                        .setSmallIcon(R.drawable.ic_small)
                        .setContentTitle(senderName)
                        .setAutoCancel(true)
                        .setColor(Color.parseColor("#007fff"))
                        .setContentText(message)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            } else {
                Notification.Builder builder = null;

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                builder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_small)
                        .setContentTitle(senderName)
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentText(message)
                        .setContentIntent(pendingIntent);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    builder.setColor(Color.parseColor("#007fff"));
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            }

        }
    }
}
