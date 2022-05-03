package com.example.soundloader;

import android.app.AlertDialog;
import android.content.Context;

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
