package com.example.soundloader.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.soundloader.R;
import com.example.soundloader.Services.NotificationActionService;
import com.example.soundloader.Models.Song;

public class MusicNotification {

    public static final int CHANNEL_ID = 404;
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_DELETE = "actiondelete";
    public static NotificationManager notificationManager;
    public static Notification notification;
    public static boolean showing;

    /**
     * Create a notification channel.
     */
    public static void createNotification(Context ctx, Song song, int pos, int size, int playButton, String state) {
        createNotificationChannel(ctx);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
        MediaSessionCompat mediaSession = new MediaSessionCompat(ctx, "tag");


        Intent intentDelete = new Intent(ctx, NotificationActionService.class).setAction(ACTION_DELETE);
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(
                ctx, 0, intentDelete,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);



        PendingIntent pendingIntentPrevius;
        int drw_previous;


        Intent intentPrevius = new Intent(ctx, NotificationActionService.class).setAction(ACTION_PREVIOUS);
        pendingIntentPrevius = PendingIntent.getBroadcast(
                ctx, 0, intentPrevius, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        drw_previous = R.drawable.ic_baseline_skip_previous_24;

        Intent intentPlay = new Intent(ctx, NotificationActionService.class).setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(
                ctx, 0, intentPlay, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntentNext;
        int drw_next;

        if (pos == size) {
            pendingIntentNext = null;
            drw_next = 0;
        } else {
            Intent intentNext = new Intent(ctx, NotificationActionService.class).setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(
                    ctx, 0, intentNext, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            drw_next = R.drawable.ic_baseline_skip_next_24;
        }

        //create notification
        notification = new NotificationCompat.Builder(ctx, String.valueOf(CHANNEL_ID))
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources() ,R.mipmap.ic_launcher_foreground))
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle(state)
                .setContentText(song.getName())
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setColorized(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setDeleteIntent(pendingIntentDelete)
                .addAction(drw_previous, "Previous", pendingIntentPrevius)
                .addAction(playButton, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(mediaSession.getSessionToken())
                ).build();

        notificationManagerCompat.notify(CHANNEL_ID, notification);
        showing = true;
    }

    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager == null) {
            CharSequence name = "playsong";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(CHANNEL_ID), name, importance);
            notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public static void destroyNotification() {
        if (notificationManager == null) return;
        notificationManager.cancelAll();
        showing = false;
    }

    public static boolean getIsShowing() {
        return showing;
    }

}
