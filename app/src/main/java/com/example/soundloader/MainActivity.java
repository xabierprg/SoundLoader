package com.example.soundloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), ClearService.class));

        displaySongs();

        FloatingActionButton fab = findViewById(R.id.downloadFloatingButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
            startActivity(intent);
        });

        SwipeRefreshLayout swipeList = findViewById(R.id.swipeList);
        swipeList.setOnRefreshListener(() -> {
            displaySongs();
            swipeList.setRefreshing(false);
        });

    }

    /**
     * Display the main page song list.
     */
    public void displaySongs() {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

        String[] items = new String[mySongs.size()];
        for (int i = mySongs.size()-1; i >= 0; i--) {
            items[(mySongs.size()-1)-i] = mySongs.get(i).getName()
                    .replace(".mp3", "")
                    .replace(".wav", "");
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);
        ListView listView = findViewById(R.id.songList);
        listView.setAdapter(myAdapter);
    }

    /**
     * Search the songs for the main page list.
     */
    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        if (files != null) {
            for (File singlefile : files) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findSong(singlefile));
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
    protected void onDestroy() {
        super.onDestroy();

        for(LaunchYtDownload thread : DownloadActivity.downloadThreads) {
            if(thread.getThread().isAlive()) {
                thread.killDownloadProcess();
            }
        }

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

}