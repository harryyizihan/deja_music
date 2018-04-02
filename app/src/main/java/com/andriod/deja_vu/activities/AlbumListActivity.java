package com.andriod.deja_vu.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andriod.flashback_music_ui.R;

import java.io.File;
import java.util.ArrayList;

public class AlbumListActivity extends AppCompatActivity {
    private ListView songView;
    private ListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Play Album");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1C2331")));
        songView = (ListView) findViewById(R.id.song_list);

        final ArrayList<String> albumList = getAlbumList(new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs"));

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, albumList) {

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
                Log.d("Clicked On", albumList.get(i));
                Intent intent = new Intent(view.getContext(), MediaPlayerActivity.class);
                intent.putExtra("albumName", albumList.get(i));
                intent.putExtra("mode", 2);
                startActivity(intent);
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
    private ArrayList<String> getAlbumList(File parentDir) {
        ArrayList<String> allAlbums = new ArrayList<>();
        File[] files = parentDir.listFiles();
        SharedPreferences sharedPreferences;
        String temp_album;

        for (File file : files) {
            if (!file.isDirectory()) {
                //Log.d("file is", file.getName());
                sharedPreferences = getSharedPreferences(file.getName(), MODE_PRIVATE);

                if (sharedPreferences.contains("album")) {
                    //  Log.d("There is", "an Album!!");
                    temp_album = sharedPreferences.getString("album", null);

                    if (!allAlbums.contains(temp_album)){
                        //    Log.d("not","contained");
                        allAlbums.add(temp_album);

                    }
                }

            }
        }
        return allAlbums;
    }
}