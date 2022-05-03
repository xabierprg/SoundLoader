package com.example.soundloader;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaPlayerManager {

    private static MediaPlayer mediaPlayer;

    public static void playSong(Context ctx, Uri uri) {
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stopSong();
            }
        }

        mediaPlayer = MediaPlayer.create(ctx, uri);
        mediaPlayer.start();
    }

    public static void stopSong() {
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }

    }

}
