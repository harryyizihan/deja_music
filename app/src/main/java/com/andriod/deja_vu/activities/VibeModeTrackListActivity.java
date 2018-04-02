package com.andriod.deja_vu.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

import com.andriod.deja_vu.AlbumDownloader;
import com.andriod.deja_vu.MockTime;
import com.andriod.deja_vu.SongDownloader;
import com.andriod.deja_vu.SongInfo;
import com.andriod.flashback_music_ui.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nullable;

public class VibeModeTrackListActivity extends AppCompatActivity {

    private ArrayList<String> songList;
    private ListView songView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibe_mode_track_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("VibeList");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1C2331")));

        requestLocation();
        Log.d("request location", "done");

        SharedPreferences songsPlayedNearHere = getSharedPreferences("SongsPlayedNearHere", MODE_PRIVATE);
        final ArrayList<String> songSet = new ArrayList<>();
        Set<String> dummySet = new HashSet<>();
        dummySet.add("dummy");
        songSet.addAll(songsPlayedNearHere.getStringSet("songsPlayedNearHere", dummySet));

        Log.d("songSet stuff", songSet.toString());
/*
        for (int i = 0; i < songSet.size(); i++) {
            storeSavedSongInfo(songSet.get(i));
        }
*/
        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<>();
        songList.addAll(songSet);

        //checkSongDownload(songList);



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
                storeSavedSongInfo(songSet.get(i));
                SongInfo temp = getSavedSongInfo();
                File file = new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + temp.getFileName());
                if (file.exists()) {
                    Intent intent = new Intent(view.getContext(), MediaPlayerActivity.class);
                    intent.putExtra("songName", temp.getFileName());
                    intent.putExtra("lastPlayed", temp.getLastListener());
                    intent.putExtra("mode", 4);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "This song is still downloading! Stay tuned", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void requestLocation() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Location currentLocation = new Location(location);
                Log.d("location", "location changed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                findSongsPlayedNearHere(currentLocation);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            Log.d("Location","Permission Granted");
        }else {
            Log.d("Location", "Permission Denied");
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        Log.d("locationProvide", locationProvider);
        locationManager.requestSingleUpdate(locationProvider, locationListener, null);
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
        Intent intent = new Intent(this, MediaPlayerActivity.class);
        intent.putExtra("mode",4);
        startActivity(intent);
    }

    public void findSongsPlayedNearHere(final Location currentLocation) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("PastPlacesPlayed");
        Log.d("Maybe execute here?????????????????"," yes u did");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Did I even execute here?????????????????","");
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                HashSet<String> songTitles = new HashSet<>();
                for(DataSnapshot key : keys) {
                    Log.d("FindSongsPlayedNearHere", "Key: " + key.getKey());
                    double longitude = Double.parseDouble(key.child("location").child("longitude").getValue().toString());
                    double latitude = Double.parseDouble(key.child("location").child("latitude").getValue().toString());
                    Log.d("FindSongsPlayedNearHere", "Latitude: " + latitude + " Longitude: " + longitude);
                    Location location = new Location("check");
                    location.setLongitude(longitude);
                    location.setLatitude(latitude);
                    if(currentLocation.distanceTo(location) < 305) {
                        Log.d("FindSongsPlayedNearHere", "Close");
                        for(DataSnapshot titleElement : key.child("songsPlayedHere").getChildren()) {
                            String title = titleElement.getValue().toString();
                            Log.d("FindSongsPlayedNearHere", "Adding " + title);
                            songTitles.add(title);
                        }
                    }
                }
                SharedPreferences songsPlayedNearHere = getSharedPreferences("SongsPlayedNearHere", MODE_PRIVATE);
                SharedPreferences.Editor editor = songsPlayedNearHere.edit();
                editor.putStringSet("songsPlayedNearHere", songTitles);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void storeSavedSongInfo(final String songTitle) {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("Song Info");

        //Get the song info and update it or save it
        Query queryRef = ref.orderByChild("songTitle").equalTo(songTitle);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences songInfoStorage = getSharedPreferences("SongInfoStorage", MODE_PRIVATE);
                SharedPreferences.Editor songInfoEditor = songInfoStorage.edit();

                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    Log.d("getSavedSongInfo", "Song not found");
                    songInfoEditor.putBoolean("NotFound", true);
                    songInfoEditor.apply();
                } else {
                    Log.d("getSavedSongInfo", "Song found");
                    //ArrayList<DataSnapshot> list = new ArrayList<>();
                    //list.addAll(dataSnapshot.getChildren());

                    DataSnapshot snapshot = dataSnapshot.child(songTitle);
                    DataSnapshot timeSnapShot = null;

                    Long i = dataSnapshot.getChildrenCount();
                    Log.d("children count", i.toString());

                    Log.d("buggggggg", dataSnapshot.toString());
                    String songName = dataSnapshot.getValue().toString();
                    Log.d("songName", songName);
                    String fileName = snapshot.hasChild("fileName") ? snapshot.child("fileName").getValue().toString() : null;
                    Log.d("fileName", fileName);
                    String url = snapshot.hasChild("url")? snapshot.child("url").getValue().toString() : null;
                    Log.d("url",url);
                    String lastListener = snapshot.hasChild("lastListener") ? snapshot.child("lastListener").getValue().toString() : null;
                    Log.d("lastListener", lastListener);
                    String otherListener = snapshot.hasChild("otherListener") ? snapshot.child("otherListener").getValue().toString() : null;

                    boolean hasTime = true;
                    if (!snapshot.hasChild("currentTime")) {
                        hasTime = false;
                    }
                    else {
                        timeSnapShot = snapshot.child("currentTime").child("localDateTime");
                    }
                    int year = hasTime ? Integer.parseInt(timeSnapShot.child("year").getValue().toString()) : 0;
                    int monthValue = hasTime ? Integer.parseInt(timeSnapShot.child("monthValue").getValue().toString()) : 1;
                    int dayOfMonth = hasTime ? Integer.parseInt(timeSnapShot.child("dayOfMonth").getValue().toString()) : 1;
                    int hour = hasTime ? Integer.parseInt(timeSnapShot.child("hour").getValue().toString()) : 1;
                    int minute = hasTime ? Integer.parseInt(timeSnapShot.child("minute").getValue().toString()) : 0;
                    int second = hasTime ? Integer.parseInt(timeSnapShot.child("second").getValue().toString()) : 0;

                    songInfoEditor.putBoolean("NotFound", false);
                    songInfoEditor.putString("songTitle", songName);
                    songInfoEditor.putString("url", url);
                    songInfoEditor.putString("fileName", fileName);
                    songInfoEditor.putString("lastListener", lastListener);
                    songInfoEditor.putString("otherListener", otherListener);
                    songInfoEditor.putInt("year", year);
                    songInfoEditor.putInt("monthValue", monthValue);
                    songInfoEditor.putInt("dayOfMonth", dayOfMonth);
                    songInfoEditor.putInt("hour", hour);
                    songInfoEditor.putInt("minute", minute);
                    songInfoEditor.putInt("second", second);
                    songInfoEditor.apply();

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("GetSavedSongInfo", "Failed to read value.", error.toException());
            }
        });
    }

    public SongInfo getSavedSongInfo() {
        SharedPreferences songInfoStorage = getSharedPreferences("SongInfoStorage", MODE_PRIVATE);
        boolean notFound = songInfoStorage.getBoolean("NotFound", false);

        Log.d("found it, fine.", "hahahahhahahaha");
        String songName = songInfoStorage.getString("songTitle", null);
        String url = songInfoStorage.getString("url", null);
        String lastListener = songInfoStorage.getString("lastListener", null);
        String otherListener = songInfoStorage.getString("otherListener", null);
        String fileName = songInfoStorage.getString("fileName", null);
        int year = songInfoStorage.getInt("year", 0);
        int monthValue = songInfoStorage.getInt("monthValue", 1);
        int dayOfMonth = songInfoStorage.getInt("dayOfMonth", 1);
        int hour = songInfoStorage.getInt("hour", 1);
        int minute = songInfoStorage.getInt("minute", 0);
        int second = songInfoStorage.getInt("second", 0);

        LocalDateTime localDateTime = LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second);
        MockTime time = new MockTime(localDateTime);

        return new SongInfo(songName, url, lastListener, otherListener, time, fileName);
    }

    private void checkSongDownload (ArrayList<String> list) {
        //Integer count = 0;
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            Log.d("passing into", list.get(i));
            storeSavedSongInfo(list.get(i));
            SongInfo temp = getSavedSongInfo();
            Log.d("checkSongDownload", "file name is ?" + temp.getFileName());
            File f = new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + temp.getFileName());
            if (!f.exists()) {
                String s = temp.getUrl();
                Uri uri = Uri.parse(s);
                if (s.contains(".mp3")) {
                    SongDownloader songDownloader = new SongDownloader(getApplicationContext());
                    songDownloader.download(uri);
                    //count++;
                }
                else if (s.contains(".zip") && set.add(s)){
                    AlbumDownloader albumDownloader = new AlbumDownloader(getApplicationContext());
                    albumDownloader.download(uri);
                }
            }
        }
        Toast.makeText(getApplicationContext(), "Now Downloading songs from remote source!", Toast.LENGTH_LONG).show();
    }
}