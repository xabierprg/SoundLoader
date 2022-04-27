package com.example.soundloader;

import android.content.Context;
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

public class LaunchYtDownload {

    public YoutubeJExtractor youtubeJExtractor;
    public Thread thread;
    public VideoPlayerConfig videoData;
    public String downloadError;
    public File songPath;
    public String ytUrl;
    public Context ctx;
    public CreateNotificationContext cnc;
    public int notificationId;

    public LaunchYtDownload(String ytUrl, Context ctx, int notificationId) {
        this.ytUrl = ytUrl;
        this.ctx = ctx;
        this.notificationId = notificationId;
    }

    /**
     * Downloads the mp3 file from the yt url.
     */
    public void downloadAudio() {
        downloadError = "";

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                youtubeJExtractor = new YoutubeJExtractor();
                videoData = null;

                cnc = new CreateNotificationContext(ctx, notificationId);

                try {
                    videoData = youtubeJExtractor.extract(ytUrl.replace("https://youtu.be/", ""));
                    List<AdaptiveAudioStream> audioStreams = videoData.getStreamingData().getAdaptiveAudioStreams();
                    URLConnection conn = new URL(audioStreams.get(0).getUrl()).openConnection();
                    InputStream is = conn.getInputStream();

                    String fileName = sanitizeFilename(videoData.getVideoDetails().getTitle());

                    File downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    songPath = new File(downloadFile.getPath() + "/" + fileName + ".mp3");

                    OutputStream outstream = new FileOutputStream(songPath);
                    byte[] buffer = new byte[4096];
                    int len;

                    cnc.getBuilder().setContentText("Downloading " + fileName + ".mp3")
                            .setOngoing(true);
                    cnc.getManager().notify(1, cnc.getBuilder().build());


                    while ((len = is.read(buffer)) > 0) {
                        outstream.write(buffer, 0, len);
                    }

                    cnc.getBuilder()
                            .setContentText("Completed download!")
                            .setProgress(0,0,false)
                            .setOngoing(false);
                    cnc.getManager().notify(1, cnc.getBuilder().build());

                    outstream.close();
                } catch (ExtractionException | YoutubeRequestException | VideoIsUnavailable | IOException e) {
                    cnc.destroyNotifications();
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

    // Destroy the yt download thread
    public void killDownloadProcess() {
        cnc.destroyNotifications();
    }

}
