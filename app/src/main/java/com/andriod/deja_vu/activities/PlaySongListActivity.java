package com.andriod.deja_vu.activities;

import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andriod.deja_vu.AlbumSorter;
import com.andriod.deja_vu.Sorter;
import com.andriod.deja_vu.TitleSorter;
import com.andriod.flashback_music_ui.R;

import static com.google.common.io.Files.move;

public class PlaySongListActivity extends AppCompatActivity {

    private ArrayList<String> songList;
    private ListView songView;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_play_song);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Play Song");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1C2331")));

        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<>();

        final ArrayList<String> files = getListFiles(new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs"));

        songList = storeSongInfo(files);
        Collections.sort(songList, String.CASE_INSENSITIVE_ORDER);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.parseColor("#A9A9A9"));
                return view;
            }
        };
        songView.setAdapter(adapter);
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), MediaPlayerActivity.class);
                intent.putExtra("fileName", songList.get(i));
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });

        final Button albumSort = (Button) findViewById(R.id.album);
        albumSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songList.clear();
                Sorter albumSort = new AlbumSorter();
                songList.addAll(albumSort.sort(files));
                adapter.notifyDataSetChanged();
                for (int i = 0; i < songList.size(); i++) {
                    Log.d("Print out songlist", songList.get(i));
                }
            }
        });

        Button titleSort = (Button) findViewById(R.id.button_title);
        titleSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songList.clear();
                Sorter titleSort = new TitleSorter();
                songList.addAll(titleSort.sort(files));
                adapter.notifyDataSetChanged();
            }
        });

        final Button artistSort = (Button) findViewById(R.id.artist);
        artistSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songList.clear();
                Sorter artistSort = new AlbumSorter();
                songList.addAll(artistSort.sort(files));
                adapter.notifyDataSetChanged();
            }
        });

        final Button favoriteSort = findViewById(R.id.favorite);
        favoriteSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songList.clear();
                songList.addAll(favoriteSort(files));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        MenuItem itemSwitch = menu.findItem(R.id.mySwitch);
        itemSwitch.setActionView(R.layout.use_switch);
        final Button sw = menu.findItem(R.id.mySwitch).getActionView().findViewById(R.id.action_switch);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Now entering Vibe Mode!",Toast.LENGTH_SHORT).show();
                launchActivity();
            }
        });

        return true;
    }

    public void launchActivity() {
        Intent intent = new Intent(this, VibeModeTrackListActivity.class);
        startActivity(intent);
    }


    private ArrayList<String> getListFiles(File parentDir) {
        ArrayList<String> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                for (File insideFile : file.listFiles())
                {
                    try {
                        move(insideFile, new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + insideFile.getName()));
                    } catch (Exception e) {
                        // error
                    }
                    inFiles.add(insideFile.getName());
                }
            }
            else if (file.getName().endsWith(".mp3")){
                inFiles.add(file.getName());
                Log.d("we are now adding", file.getName());
            }
            else {
                // nothing
            }
        }
        return inFiles;
    }


    private ArrayList<String> storeSongInfo (ArrayList<String> songList) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < songList.size(); i++) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + songList.get(i));
            SharedPreferences sharedPreferences = getSharedPreferences(songList.get(i), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("title", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            //Log.d("ggggggggggggggggggggg",  mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            editor.putString("artist", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            //Log.d("ggggggggggggggggggggg",  mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            editor.putString("album", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            //Log.d("ggggggggggggggggggggg",  mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

            if (!sharedPreferences.contains("status")) {
                editor.putInt("status", 0);
            }
            editor.apply();
            result.add(songList.get(i));

            //MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + songList.get(i));
            SharedPreferences s1 = getSharedPreferences(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), MODE_PRIVATE);
            SharedPreferences.Editor editor1 = s1.edit();
            editor1.putString("fileName", songList.get(i));
            editor1.apply();
        }

        return result;
    }

    private ArrayList<String> favoriteSort (ArrayList<String> files) {
        ArrayList<String> result = new ArrayList<>();
        PriorityQueue<String> pq = new PriorityQueue<>(files.size(), new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                Log.d("Now comparing ", s + " and " + t1);
                SharedPreferences sharedPreferences = getSharedPreferences(s, MODE_PRIVATE);
                SharedPreferences sharedPreferences1 = getSharedPreferences(t1, MODE_PRIVATE);
                Integer i = sharedPreferences.getInt("status", 0);
                Integer j = sharedPreferences1.getInt("status", 0);
                Log.d("status s", i.toString());
                Log.d("status t1", j.toString());

                if (i > j) {
                    Log.d("return -1","return -1");
                    return -1;
                }
                else if (i < j) {
                    Log.d("return 1","return 1");
                    return 1;
                }
                else {
                    Log.d("return 0","return 0");
                    return 0;
                }
            }
        });

        for (int i = 0; i < files.size(); i++) {
            pq.offer(files.get(i));
        }

        for (int i = 0; i < files.size(); i++) {
            result.add(pq.poll());
        }

        return result;
    }
}