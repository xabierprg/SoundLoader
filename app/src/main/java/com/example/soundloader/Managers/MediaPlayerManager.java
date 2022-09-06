package com.example.soundloader.Managers;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import com.example.soundloader.Notifications.MusicNotification;
import com.example.soundloader.R;
import com.example.soundloader.Models.Song;

import java.util.ArrayList;

public class MediaPlayerManager {

    private static MediaPlayer mediaPlayer;
    private static ArrayList<Song> trackList;
    private static int songPosition;

    public static void playSong(Context ctx, Uri uri, int songPos, ArrayList<Song> songs) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                pauseSong(ctx);
            }
        }

        trackList = songs;
        songPosition = songPos;
        mediaPlayer = MediaPlayer.create(ctx, uri);
        mediaPlayer.start();
        MusicNotification.createNotification(
                ctx, trackList.get(songPosition),
                songPosition, trackList.size(),
                R.drawable.ic_baseline_pause_24, "Playing...");

        mediaPlayer.setOnCompletionListener(mediaPlayer -> MediaPlayerManager.nextSong(ctx));

    }

    public static void restartSong(Context ctx) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
        }
    }

    public static void pauseSong(Context ctx) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                MusicNotification.createNotification(
                        ctx, trackList.get(songPosition),
                        songPosition, trackList.size(),
                        R.drawable.ic_baseline_play_arrow_24, "Paused");
            }

        }

    }

    public static void resumeSong(Context ctx) {
        if (mediaPlayer != null && MusicNotification.getIsShowing()) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                MusicNotification.createNotification(
                        ctx, trackList.get(songPosition),
                        songPosition, trackList.size(),
                        R.drawable.ic_baseline_pause_24, "Playing...");
            }

        }

    }

    public static void nextSong(Context ctx) {
        if (mediaPlayer != null && songPosition != trackList.size()-1) {
            mediaPlayer.stop();
            songPosition++;
            mediaPlayer = MediaPlayer.create(ctx, trackList.get(songPosition).getFileUri());
            mediaPlayer.start();
            MusicNotification.createNotification(
                    ctx, trackList.get(songPosition),
                    songPosition, trackList.size(),
                    R.drawable.ic_baseline_pause_24, "Playing...");
        }

    }

    public static void previusSong(Context ctx) {
        if (mediaPlayer != null && songPosition != 0) {
            mediaPlayer.stop();
            songPosition--;
            mediaPlayer = MediaPlayer.create(ctx, trackList.get(songPosition).getFileUri());
            mediaPlayer.start();
            MusicNotification.createNotification(
                    ctx, trackList.get(songPosition),
                    songPosition, trackList.size(),
                    R.drawable.ic_baseline_pause_24, "Playing...");
        }

    }

    public static void stopMusic() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            MusicNotification.destroyNotification();
        }
    }

    public static int getSongPosition() {
        return songPosition;
    }

    public static void setSongPosition(int songPosition) {
        MediaPlayerManager.songPosition = songPosition;
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

}
