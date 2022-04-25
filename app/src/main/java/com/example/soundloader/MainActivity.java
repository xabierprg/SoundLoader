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
import firebase.com.protolitewrapper.BuildConfig;


public class MainActivity extends AppCompatActivity {

    EditText etUrl;
    Button btnDownload;
    static NotificationManager manager;
    static NotificationCompat.Builder builder;
    static PendingIntent resultPendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDownload = findViewById(R.id.btnDownload);
        etUrl = findViewById(R.id.etUrl);

        ColorDrawable actionBarColor = new ColorDrawable(Color.parseColor("#8EC6C6"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(actionBarColor);
        actionBar.setTitle((Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>")));

        createNotificationChannel();

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etUrl.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Insert a youtube url", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "Starting the download...", Toast.LENGTH_LONG).show();
                YoutubeAudioExtractor.downloadAudio(etUrl.getText().toString());
                etUrl.setText("");
            }

        });

    }

    // Create the notification creator module.
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "download";
            String description = "download finished";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat
                    .Builder(this, "1")
                    .setSmallIcon(R.drawable.ic_baseline_arrow_downward_24);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

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
    public boolean isFinishing() {
        manager.cancelAll();
        YoutubeAudioExtractor.songPath.delete();
        return super.isFinishing();
    }

}