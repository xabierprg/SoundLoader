package com.example.soundloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    LaunchYtDownload downloader;
    ArrayList<LaunchYtDownload> downloadThreads;
    EditText etUrl;
    Button btnDownload;
    int notificationId = 1;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), ClearService.class));

        verifyStoragePermissions(this);

        btnDownload = findViewById(R.id.btnDownload);
        downloadThreads = new ArrayList<>();

        etUrl = findViewById(R.id.etUrl);
        etUrl.requestFocus();


        btnDownload.setOnClickListener(view -> {
            if(etUrl.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Insert a youtube url", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(getApplicationContext(), "Starting the download...", Toast.LENGTH_LONG).show();

            downloader = new LaunchYtDownload(
                    etUrl.getText().toString(),
                    MainActivity.this,
                    notificationId);

            notificationId++;
            downloader.downloadAudio();
            downloadThreads.add(downloader);
            etUrl.setText("");
        });

    }

    /**
     * Create about dialog with version and dev information.
     */
    public void createAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                    "SoundLoader \n" +
                    "Version: " + BuildConfig.VERSION_NAME + "\n" +
                    "Created by xabierprg");
        builder.create();
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_info, menu);
        menu.getItem(0).setOnMenuItemClickListener(menuItem -> {
            createAboutDialog();
            return false;
        });
        return true;
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Killing download process.
        for(LaunchYtDownload lyd : downloadThreads) {
            lyd.killDownloadProcess();
        }

    }

}