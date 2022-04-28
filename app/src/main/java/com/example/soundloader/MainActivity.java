package com.example.soundloader;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), ClearService.class));

        btnDownload = findViewById(R.id.btnDownload);
        etUrl = findViewById(R.id.etUrl);
        downloadThreads = new ArrayList<>();


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etUrl.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Insert a youtube url", Toast.LENGTH_LONG).show();
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
            }

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
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                createAboutDialog();
                return false;
            }
        });
        return true;
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