package com.example.soundloader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.soundloader.Adapters.AdapterData;
import com.example.soundloader.Managers.DialogManager;
import com.example.soundloader.YtDownload.LaunchYtDownload;
import com.example.soundloader.Managers.MediaPlayerManager;
import com.example.soundloader.Notifications.MusicNotification;
import com.example.soundloader.R;
import com.example.soundloader.Services.ClearService;
import com.example.soundloader.Models.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    ArrayList<Song> mySongs;
    boolean isPlaying = false;

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

        if(verifyStoragePermissions(this)) {
            displaySongs();
        }

        musicNotificationControl();

        FloatingActionButton fab = findViewById(R.id.downloadFloatingButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, DownloadActivity.class);
            startActivity(intent);
        });

        SwipeRefreshLayout swipeList = findViewById(R.id.swipeList);
        swipeList.setOnRefreshListener(() -> {
            if(verifyStoragePermissions(this)) {
                displaySongs();
            }
            swipeList.setRefreshing(false);
        });

    }

    /**
     * Display the main page song list.
     */
    public void displaySongs() {
        mySongs = new ArrayList<>();
        for(File f : findFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))) {
             mySongs.add(new Song(f));
        }
        Collections.reverse(mySongs);

        RecyclerView recycler = findViewById(R.id.songList);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AdapterData adapter = new AdapterData(mySongs);
        adapter.setOnClickListener(view -> {
                isPlaying = true;
                MediaPlayerManager.setSongPosition(recycler.getChildAdapterPosition(view));
                MediaPlayerManager.playSong(this,
                    mySongs.get(MediaPlayerManager.getSongPosition()).getFileUri(),
                    MediaPlayerManager.getSongPosition(),
                    mySongs);
        });
        recycler.setAdapter(adapter);
    }

    /**
     * Search the songs for the main page list.
     */
    public ArrayList<File> findFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singlefile : files) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findFiles(singlefile));
                } else {
                    if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                        arrayList.add(singlefile);
                    }
                }
            }
        }
        return arrayList;
    }

    protected void musicNotificationControl() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");
                System.out.println(action);

                switch (action) {
                    case MusicNotification.ACTION_PREVIOUS:
                        if(MediaPlayerManager.getSongPosition() != 0) {
                            MediaPlayerManager.previusSong(context);
                        }
                        break;
                    case MusicNotification.ACTION_PLAY:
                        if(isPlaying) {
                            MediaPlayerManager.pauseSong(context);
                            isPlaying = false;
                        } else {
                            MediaPlayerManager.resumeSong(context);
                            isPlaying = true;
                        }
                        break;
                    case MusicNotification.ACTION_NEXT:
                        MediaPlayerManager.nextSong(context);
                        break;
                    case MusicNotification.ACTION_DELETE:
                        MediaPlayerManager.stopMusic();
                        break;
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));

    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     * MainActivity version of the method
     */
    protected boolean verifyStoragePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            DialogManager.createPermissionsDialogMain(this);
            return false;
        } else {
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            return true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_info, menu);
        menu.getItem(0).setOnMenuItemClickListener(menuItem -> {
            DialogManager.createAboutDialog(this);
            return false;
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(DownloadActivity.downloadThreads != null) {
            for (LaunchYtDownload thread : DownloadActivity.downloadThreads) {
                if (thread.getThread().isAlive()) {
                    thread.killDownloadProcess();
                }
            }
        }

        MusicNotification.destroyNotification();

    }

}