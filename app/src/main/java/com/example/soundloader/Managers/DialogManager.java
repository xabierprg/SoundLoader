package com.example.soundloader.Managers;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import com.example.soundloader.BuildConfig;
import com.example.soundloader.Models.Song;


public class DialogManager {

    /**
     * Create permission dialog with information about enabling permissions.
     */
    public static void createPermissionsDialogMain(Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("SoundLoader needs access to your media files to show you your song list. " +
                "Go to settings and enable the permission to access your local data.");
        builder.create();
        builder.show();
    }

    /**
     * Create permission dialog with information about enabling permissions.
     */
    public static void createPermissionsDialogDownload(Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("SoundLoader needs access to your media files to download youtube songs. " +
                "Go to settings and enable the permission to access your local data.");
        builder.create();
        builder.show();
    }

    /**
     * Create delete dialog to delete songs on long click.
     */
    public static void createDeleteSongDialog(Context ctx, Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("Confirm to delete the track.");
        builder.setPositiveButton("Delete", (dialogInterface, i) ->
                Toast.makeText(ctx, song.getFileLocation() + " deleted", Toast.LENGTH_LONG).show());
        builder.setNegativeButton("Cancel", (dialogInterface, i) ->
                Toast.makeText(ctx, "No song was deleted", Toast.LENGTH_SHORT).show());
        builder.create();
        builder.show();
    }

    /**
     * Create about dialog with version and dev information.
     */
    public static void createAboutDialog(Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(
                "SoundLoader \n" +
                        "Version: " + BuildConfig.VERSION_NAME + "\n" +
                        "Created by xabierprg");
        builder.create();
        builder.show();
    }

}
