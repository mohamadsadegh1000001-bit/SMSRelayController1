package com.example.smsrelay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "sms_relay_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SMS Relay Controller", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("اعلان‌های کنترلر رله");
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private NotificationCompat.Builder createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SMS Relay Controller")
            .setContentText("در حال اجرا...")
            .setSmallIcon(R.drawable.ic_home)
            .setPriority(NotificationCompat.PRIORITY_LOW);
    }

    public static void showNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_home)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}