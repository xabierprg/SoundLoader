package com.example.soundloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

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
        ArrayList<Song> mySongs = new ArrayList<>();
        for(File f : findFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))) {
             mySongs.add(new Song(f));
        }
        Collections.reverse(mySongs);

        RecyclerView recycler = findViewById(R.id.songList);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AdapterData adapter = new AdapterData(mySongs);
        adapter.setOnClickListener(view -> {
            MediaPlayerManager.playSong(this,
                    mySongs.get(recycler.getChildAdapterPosition(view)).getFileUri(),
                    recycler.getChildAdapterPosition(view),
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