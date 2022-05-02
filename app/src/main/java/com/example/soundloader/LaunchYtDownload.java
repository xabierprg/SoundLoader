package com.example.soundloader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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
import java.util.Objects;

public class LaunchYtDownload {

    public YoutubeJExtractor youtubeJExtractor;
    public Thread thread;
    public VideoPlayerConfig videoData;
    public String downloadError = "";
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

        thread = new Thread(() -> {
            youtubeJExtractor = new YoutubeJExtractor();
            videoData = null;

            cnc = new CreateNotificationContext(ctx, notificationId);

            try {
                videoData = youtubeJExtractor.extract(ytUrl.replace("https://youtu.be/", ""));
                List<AdaptiveAudioStream> audioStreams = Objects.requireNonNull(
                        videoData.getStreamingData()).getAdaptiveAudioStreams();
                URLConnection conn = new URL(audioStreams.get(0).getUrl()).openConnection();
                InputStream is = conn.getInputStream();

                String fileName = Objects.requireNonNull(videoData.getVideoDetails()).getTitle();

                File downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                songPath = new File(downloadFile.getPath() + "/" + fileName + ".mp3");

                cnc.getBuilder()
                        .setContentText("Downloading " + fileName + ".mp3")
                        .setOngoing(true);
                cnc.getManager().notify(notificationId, cnc.getBuilder().build());

                OutputStream os = new FileOutputStream(songPath);
                byte[] buffer = new byte[4096];
                int len;

                int cont = 0;
                double var = 0.25274725274725274725274725274725;
                int videolen = Integer.parseInt(Objects.requireNonNull(
                        Objects.requireNonNull(videoData.getVideoDetails()).getLengthSeconds()));

                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);

                    cnc.getBuilder()
                            .setContentText("Downloading " + fileName + ".mp3")
                            .setProgress(videolen,(int)(cont*var),false)
                            .setOnlyAlertOnce(true)
                            .setOngoing(true);
                    cnc.getManager().notify(notificationId, cnc.getBuilder().build());

                    cont++;
                }

                cnc.destroyNotification();

                cnc.getBuilder()
                        .setContentText("Download complete!")
                        .setProgress(0, 0, false)
                        .setOngoing(false);
                cnc.getManager().notify(notificationId, cnc.getBuilder().build());

                os.close();
            } catch (ExtractionException | YoutubeRequestException | VideoIsUnavailable | IOException e) {
                killDownloadProcess();
                downloadError += e.getMessage() + "\n";
                Log.e("Error", downloadError);
            }
        });

        thread.start();

    }

    /**
     * Destroy Notifications and download threads.
     */
    public void killDownloadProcess() {
        cnc.destroyNotification();
    }

}
