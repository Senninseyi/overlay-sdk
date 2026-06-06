package com.senninseyi.overlay_sdk.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.senninseyi.overlay_sdk.OverlayManager;
import com.senninseyi.overlay_sdk.OverlaySDK;

public class OverlayService extends Service {

    private static final String CHANNEL_ID = "overlay_service_channel";
    private static final int NOTIFICATION_ID = 1001;
    private boolean foregroundStarted;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Overlay Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        OverlaySDK.initialize(this);
        OverlayManager.getInstance(this).onServiceCreated();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!startForegroundSafely()) {
            stopSelf();
            return START_NOT_STICKY;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        OverlayManager.getInstance(this).onServiceDestroyed();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean startForegroundSafely() {
        if (foregroundStarted) {
            return true;
        }

        Notification notification = buildForegroundNotification();

        try {
            startForeground(NOTIFICATION_ID, notification);
            foregroundStarted = true;
            return true;
        } catch (RuntimeException exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    && exception instanceof android.app.ForegroundServiceStartNotAllowedException) {
                return false;
            }
            throw exception;
        }
    }

    private Notification buildForegroundNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Overlay SDK")
                .setContentText("Overlay Service is running")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
}
