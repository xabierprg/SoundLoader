package com.example.soundloader;

import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class Song {

    private String name;
    private final Uri fileUri;

    public Song(File fileLocation) {
        this.fileUri = Uri.fromFile(fileLocation);
        this.name = fileLocation.getPath().replace(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getPath() + "/", "");
    }

    public Song(String name) {
        this.fileUri = Uri.fromFile(new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + name + ".mp3"));
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
