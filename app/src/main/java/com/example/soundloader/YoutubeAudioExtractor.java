package com.example.soundloader;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Environment;

import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.AdaptiveAudioStream;
import com.github.kotvertolet.youtubejextractor.models.newModels.VideoPlayerConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class YoutubeAudioExtractor {

    static YoutubeJExtractor youtubeJExtractor;
    static Thread thread;
    static VideoPlayerConfig videoData;
    static String downloadError;
    static File songPath;

    /**
     * Downloads the mp3 file from the yt url.
     * @param ytUrl String that contains the yt url.
     */
    public static void downloadAudio(String ytUrl) {
        downloadError = "";

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                youtubeJExtractor = new YoutubeJExtractor();
                videoData = null;

                try {
                    videoData = youtubeJExtractor.extract(ytUrl.replace("https://youtu.be/", ""));
                    List<AdaptiveAudioStream> audioStreams = videoData.getStreamingData().getAdaptiveAudioStreams();
                    URLConnection conn = new URL(audioStreams.get(0).getUrl()).openConnection();
                    InputStream is = conn.getInputStream();

                    String fileName =  sanitizeFilename(videoData.getVideoDetails().getTitle());

                    File downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    songPath = new File(downloadFile.getPath() + "/" + fileName + ".mp3");

                    OutputStream outstream = new FileOutputStream(songPath);
                    byte[] buffer = new byte[4096];
                    int len;

                    MainActivity.builder.setContentText("Downloading...")
                                        .setOngoing(true);
                    MainActivity.manager.notify(1,MainActivity.builder.build());


                    while ((len = is.read(buffer)) > 0) {
                        outstream.write(buffer, 0, len);
                    }

                    MainActivity.builder
                            .setContentText("Completed download!")
                            .setContentIntent(MainActivity.resultPendingIntent)
                            .setProgress(0,0,false)
                            .setOngoing(false);
                    MainActivity.manager.notify(1, MainActivity.builder.build());

                    outstream.close();
                } catch (ExtractionException | YoutubeRequestException | VideoIsUnavailable | IOException e) {
                    MainActivity.builder.setOngoing(false);
                    downloadError = e.toString();
                }
            }

        });

        thread.start();

    }

    /**
     * Replace some letters to make the file downloadable.
     * @param inputName String that contains the name of the mp3 android file.
     */
    public static String sanitizeFilename(String inputName) {
        return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_").replace("-","");
    }

}
