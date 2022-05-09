package com.example.soundloader.Models;

import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class Song {

    private final String name;
    private final Uri fileUri;

    public Song(File fileLocation) {
        this.fileUri = Uri.fromFile(fileLocation);
        this.name = fileLocation.getPath()
                .replace(".mp3","")
                .replace(".wav","")
                .replace(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getPath() + "/", "");
    }

    /**
     * Get the file uri.
     */
    public Uri getFileUri() {
        return fileUri;
    }

    /**
     * Get the file name.
     */
    public String getName() {
        return name;
    }

}
