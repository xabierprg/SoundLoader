package com.example.soundloader;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayList;

public class MediaPlayerManager {

    private static MediaPlayer mediaPlayer;
    private static ArrayList<Song> trackList;
    private static int songPosition;

    public static void playSong(Context ctx, Uri uri, int songPos, ArrayList<Song> songs) {
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stopSong();
            }
        }

        trackList = songs;
        songPosition = songPos;
        mediaPlayer = MediaPlayer.create(ctx, uri);
        mediaPlayer.start();
        MusicNotification.createNotification(
                ctx,trackList.get(songPos),
                songPosition, trackList.size(),
                R.drawable.ic_baseline_play_arrow_24);
    }

    public static void stopSong() {
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }

    }

    public static void previusSong() {

    }

}
