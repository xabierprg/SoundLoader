package com.example.soundloader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MusicNotification {

    public static final int CHANNEL_ID = 404;
    public static final String ACTION_PREVIUS = "actionprevius";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static NotificationManager notificationManager;
    public static Notification notification;

    /**
     * Create a notification channel.
     */
    public static void createNotification(Context ctx, Song song, int pos, int size, int playButton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "playsong";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(CHANNEL_ID), name, importance);
            notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
            MediaSession mediaSession = new MediaSession(ctx, "tag");

            PendingIntent pendingIntentPrevius;
            int drw_previus;

            if (pos == 0) {
                pendingIntentPrevius = null;
                drw_previus = 0;
            } else {
                Intent intentPrevius = new Intent(ctx, NotificationActionService.class).setAction(ACTION_PREVIUS);
                pendingIntentPrevius = PendingIntent.getBroadcast(
                        ctx, 0, intentPrevius, PendingIntent.FLAG_IMMUTABLE);
                drw_previus = R.drawable.ic_baseline_skip_previous_24;
            }

            Intent intentPlay = new Intent(ctx, NotificationActionService.class).setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(
                    ctx, 0, intentPlay, PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentNext;
            int drw_next;

            if (pos == size) {
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(ctx, NotificationActionService.class).setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(
                        ctx, 0, intentNext, PendingIntent.FLAG_IMMUTABLE);
                drw_next = R.drawable.ic_baseline_skip_next_24;
            }

            //create notification
            notification = new NotificationCompat.Builder(ctx, String.valueOf(CHANNEL_ID))
                    .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                    .setContentTitle("Playing...")
                    .setContentText(song.getName())
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(drw_previus, "Previus", pendingIntentPrevius)
                    .addAction(playButton, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new NotificationCompat.Style() {})
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

            notificationManagerCompat.notify(CHANNEL_ID, notification);
        }

    }

    public static void destroyNotification() {
        notificationManager.cancel(CHANNEL_ID);
    }


}
