package com.example.soundloader;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class CreateNotificationContext {

    public int id;
    public Context ctx;
    public NotificationManager manager;
    public NotificationCompat.Builder builder;

    public CreateNotificationContext(Context ctx, int id) {
        this.ctx = ctx;
        this.id = id;

        createNotificationChannel();
    }

    /**
     * Create a notification channel.
     */
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "download";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(id), name, importance);
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat
                    .Builder(ctx, String.valueOf(id))
                    .setSmallIcon(R.drawable.ic_baseline_arrow_downward_24);

            Intent notificationIntent = new Intent(ctx, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
            stackBuilder.addNextIntentWithParentStack(intent);
        }

    }

    /**
     * Destroy notification manger.
     */
    public void destroyNotifications() {
        manager.cancel(id);
    }

    public NotificationManager getManager() {
        return manager;
    }

    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

}
