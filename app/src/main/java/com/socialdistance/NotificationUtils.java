package com.socialdistance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {


    private final String CHANNEL_ID = "SOCIAL DISTANCING";
    Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void createNotification(String textMsg) {

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, "SOCIAL DISTANCE", "This is is SOCIAL DISTANCE all notification channel.", NotificationManager.IMPORTANCE_DEFAULT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Social Distancing")
                .setContentText(textMsg)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(001, notification);

    }

    private void createNotificationChannel(String id, String name, String description, int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
