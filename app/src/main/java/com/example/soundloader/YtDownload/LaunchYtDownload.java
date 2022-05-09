package com.example.soundloader.YtDownload;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.soundloader.Notifications.CreateNotificationContext;
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

    private Thread thread;
    private File songPath;
    private final String ytUrl;
    private final Context ctx;
    private final CreateNotificationContext cnc;
    private final int notificationId;

    public LaunchYtDownload(String ytUrl, Context ctx, int notificationId) {
        this.ytUrl = ytUrl;
        this.ctx = ctx;
        this.notificationId = notificationId;

        cnc = new CreateNotificationContext(ctx, notificationId);
    }

    /**
     * Downloads the mp3 file from the yt url.
     */
    public void downloadAudio() {

        thread = new Thread(() -> {

            try {
                YoutubeJExtractor youtubeJExtractor = new YoutubeJExtractor();
                VideoPlayerConfig videoData = youtubeJExtractor.extract(ytUrl.replace("https://youtu.be/", ""));
                List<AdaptiveAudioStream> audioStreams = Objects.requireNonNull(
                        videoData.getStreamingData()).getAdaptiveAudioStreams();
                URLConnection conn = new URL(audioStreams.get(0).getUrl()).openConnection();
                InputStream is = conn.getInputStream();

                String fileName = Objects.requireNonNull(videoData.getVideoDetails()).getTitle();

                File downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                songPath = new File(downloadFile.getPath() + "/" + fileName + ".mp3");

                if(songPath.exists()) {
                    Looper.prepare();
                    Toast.makeText(ctx,
                            "This song already exists in your data. Delete it and try again",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                cnc.getBuilder()
                        .setContentText("Downloading " + fileName + ".mp3")
                        .setOngoing(true);
                cnc.getManager().notify(notificationId, cnc.getBuilder().build());

                OutputStream os = new FileOutputStream(songPath);
                byte[] buffer = new byte[4096];
                int len;

                int cont = 0;
                double cons = 0.25274725274725274725274725274725;
                int videolen = Integer.parseInt(Objects.requireNonNull(
                        Objects.requireNonNull(videoData.getVideoDetails()).getLengthSeconds()));

                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);

                    cnc.getBuilder()
                            .setContentText("Downloading " + fileName + ".mp3")
                            .setProgress(videolen,(int)(cont*cons),false)
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
                Log.e("Error", e.getMessage());
            }

        });

        thread.start();

    }

    /**
     * Destroy Notifications and download threads.
     */
    public void killDownloadProcess() {
        songPath.delete();
        cnc.destroyNotification();
    }

    /**
     * Get the thread.
     */
    public Thread getThread() {
        return thread;
    }

}
