package com.example.soundloader.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import com.example.soundloader.Managers.MediaPlayerManager;


public class MediaButtonActionService extends BroadcastReceiver {

    static final long CLICK_DELAY = 500;
    static long lastClick = 0; // oldValue
    static long currentClick = System.currentTimeMillis();

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (!intentAction.equals(Intent.ACTION_MEDIA_BUTTON)) return;

        KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) return;

        if (event.getAction() != KeyEvent.ACTION_DOWN) return;

        lastClick = currentClick;
        currentClick = System.currentTimeMillis();

        if (MediaPlayerManager.getMediaPlayer() != null) {
            if (currentClick - lastClick < CLICK_DELAY) {
                MediaPlayerManager.nextSong(context);
            } else {
                if (MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.pauseSong(context);
                } else {
                    MediaPlayerManager.resumeSong(context);
                }

            }

        }
        abortBroadcast();
    }

}
