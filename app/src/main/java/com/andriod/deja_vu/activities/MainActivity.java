package com.andriod.deja_vu.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.andriod.deja_vu.ActualTime;
import com.andriod.deja_vu.CurrentTime;
import com.andriod.deja_vu.MockTime;
import com.andriod.deja_vu.PlacePlayed;
import com.andriod.deja_vu.SongInfo;
import com.andriod.flashback_music_ui.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Main Menu");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1C2331")));

        showPermissionPreview();
        //testDataBase();
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //testSongsPlayedNearHere();

        // PLAY SONG
        Button launchPlaySongActivity = (Button) findViewById(R.id.bt_play_song);
        launchPlaySongActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity();
            }
        });

        // PLAY ALBUM
        Button launchPlayAlbumActivity = (Button) findViewById(R.id.bt_play_album);
        launchPlayAlbumActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlbumListActivity.class);
                startActivity(intent);
            }
        });

        // PLAY FLASHBACK
        Button launchFlashbackActivity = (Button) findViewById(R.id.bt_play_flashback);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
                intent.putExtra("mode", 3);
                startActivity(intent);
            }
        });
    }

    /* -----------------GET LOCATION PERMISSION FROM USER----------------------------- */
    private void showPermissionPreview() {
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mLayout,
                    "Already got permission for location, let's flashback!",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            // Permission is missing and must be requested.
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, "Is it OK for iFlashback to access your location?",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_LOCATION);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, "Permission Unavailable...emmm maybe close the Airplane mode and turn on the Wi-Fi?", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout, "iFlashback now can access location!",
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(mLayout, "Deny the access of location! Well, what can I say?",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /* -------------------------------------------------------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        MenuItem itemSwitch = menu.findItem(R.id.mySwitch);
        itemSwitch.setActionView(R.layout.use_switch);
        final Button sw = menu.findItem(R.id.mySwitch).getActionView().findViewById(R.id.action_switch);
        sw.setText("utils");
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity2();
            }
        });



        return true;
    }

    public void launchActivity2 () {
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }

    public void launchActivity() {
        Intent intent = new Intent(this, PlaySongListActivity.class);
        startActivity(intent);
    }
/*
    public void testDataBase() {
        //final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference ref = firebaseDatabase.getReference().child("PastPlacesPlayed");

        //TIMES
        CurrentTime time1 = new ActualTime();
        LocalDateTime mockTime = LocalDateTime.of(2018, 4, 20, 4, 20, 20);
        LocalDateTime mockTime2 = LocalDateTime.of(2018, 9, 13, 2, 0, 20);
        CurrentTime time2 = new MockTime(mockTime);
        CurrentTime time3 = new MockTime(mockTime2);

        //SONGINFO
        String song1Title = "Song 1 Title";
        SongInfo song1 = new SongInfo(song1Title, "https://songsource.com/song1.mp3", "Seth", "Harry", time1);
        String song2Title = "Song 2 Title";
        SongInfo song2 = new SongInfo(song2Title, "https://songsource.com/song2.mp3", "Cathy", "William", time2);
        String song3Title = "Song 3 Title";
        SongInfo song3 = new SongInfo(song3Title,"https://songsource.com/song3.mp3", "Alexsei", time3);
        String song4Title = "Still Dre";
        SongInfo song4 = new SongInfo(song4Title, "https://songsource.com/stilldre.mp3", "Seth", "Esteban", time2);

        //SAVESONGS
        song1.saveSongInfo();
        song2.saveSongInfo();
        song3.saveSongInfo();
        song4.saveSongInfo();

        //LOCATIONS
        Location locationA = new Location("locationA");
        locationA.setLatitude(65.7891234);
        locationA.setLongitude(120.456789);
        Location locationB = new Location("locationB");
        locationB.setLatitude(13.567);
        locationB.setLongitude(65.123456789);
        Location locationC = new Location("locationC");
        locationC.setLatitude(42.000);
        locationC.setLongitude(69.696969);

        //SONGSPLAYEDHERE
        ArrayList<String> songList1 = new ArrayList<String>();
        songList1.add(song1Title);
        songList1.add(song2Title);
        songList1.add(song3Title);
        ArrayList<String> songList2 = new ArrayList<String>();
        songList2.add(song1Title);
        songList2.add(song3Title);

        //HashMap<String, SongInfo> songList1 = new HashMap<>();
        //songList1.put(song1Title, song1);
        //songList1.put(song2Title, song2);
        //songList1.put(song3Title, song3);


        //PLACES PLAYED
        PlacePlayed place1 = new PlacePlayed();
        place1.setLocation(locationA);
        place1.setSongsPlayedHere(songList1);

        PlacePlayed place2 = new PlacePlayed();
        place2.setLocation(locationB);
        place2.setSongsPlayedHere(songList2);


        /* WORKS
        ref.push().setValue(place1);
        ref.push().setValue(place2);
        */
/*
        String id1 = place1.updatePlacePlayed();
        String id2 = place2.updatePlacePlayed();
        Log.d("TEST_DATABASE", "ID 1 is " + id1);
        Log.d("TEST_DATABASE", "ID 2 is " + id2);

        //ADD a new song
        //PASSED
        place1.updatePlacePlayedForThisSong(song4Title, "https://songsource.com/stilldre.mp3", "Seth", time2);
        //Update a song with a newlistener and new time


        //PASSED
        place1.updatePlacePlayedForThisSong(song1Title, "https://songsource.com/song1.mp3", "Harry", time2);

        //FAILED
        place1.updatePlacePlayedForThisSong(song4Title, "https://songsource.com/stilldre.mp3", "Esteban", time3);
        /* WORKS
        place1.setPastSongs(songList2);
        place1.updatePlacePlayed(ref, id1);
        */


        //place1.findSong(song1Title);



//    }

    /*
    public void saveSongsPlayedNearHere() {

        LocationListener locationListenerGeneratePlaylist = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LOCATION: ", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                findSongsPlayedNearHere(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
            Log.d("Location", "Permission Granted VibeModeTrackListActivity");
            //return;
        } else {
            Log.d("Location", "Permission Denied");
        }


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestSingleUpdate(locationProvider, locationListenerGeneratePlaylist, null);
        //locationManager.requestLocationUpdates(locationProvider, 120000, 0, locationListener);
    }
*/
/*
    public void findSongsPlayedNearHere(final Location currentLocation) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("PastPlacesPlayed");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
//                        List<String> songsPlayedAtThisLocation = key.child("songsPlayedHere").getValue(ArrayList.class);
//                        for(String title : songsPlayedAtThisLocation) {
//                            Log.d("FindSongsPlayedNearHere", "Adding " + title);
//                            songTitles.add(title);
//                        }
                    }
                }
                SharedPreferences songsPlayedNearHere = getSharedPreferences("SongsPlayedNearHere", MODE_PRIVATE);
                SharedPreferences.Editor editor = songsPlayedNearHere.edit();
                editor.putStringSet("songsPlayedNearHere", songTitles);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void testSongsPlayedNearHere() {
        Location location = new Location("test");
        location.setLongitude(120.456789);
        location.setLatitude(65.7891234);
        findSongsPlayedNearHere(location);

        SharedPreferences songsPlayedNearHere = getSharedPreferences("SongsPlayedNearHere", MODE_PRIVATE);
        Set<String> songSet = songsPlayedNearHere.getStringSet("songsPlayedNearHere", null);
        for(String title: songSet) {
            Log.d("FindSongsPlayedNearHere", "Saved: " + title);
        }
    }
*/
/*
    public SongInfo getSavedSongInfo(final String songTitle) {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("Song Info");

        //Get the song info and update it or save it
        Query queryRef = ref.orderByChild("songTitle").equalTo(songTitle);
        queryRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences songInfoStorage = getSharedPreferences("SongInfoStorage", MODE_PRIVATE);
                SharedPreferences.Editor songInfoEditor = songInfoStorage.edit();

                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    Log.d("getSavedSongInfo", "Song not found");
                    songInfoEditor.putBoolean("NotFound", true);
                }
                else {
                    Log.d("getSavedSongInfo", "Song found");
                    DataSnapshot snapshot = dataSnapshot.child(songTitle);
                    DataSnapshot timeSnapShot = snapshot.child("currentTime").child("localDateTime");
                    String songName = snapshot.child("songTitle").getValue().toString();
                    String url = snapshot.child("url").getValue().toString();
                    String lastListener = snapshot.hasChild("lastListener") ?  snapshot.child("lastListener").getValue().toString() : null;
                    String otherListener = snapshot.hasChild("otherListener") ? snapshot.child("otherListener").getValue().toString() : null;

                    boolean hasTime = true;
                    if(!snapshot.hasChild("currentTime")) {
                        hasTime = false;
                    }
                    int year       = hasTime ? Integer.parseInt(timeSnapShot.child("year").getValue().toString()) : 0;
                    int monthValue = hasTime ? Integer.parseInt(timeSnapShot.child("monthValue").getValue().toString()) : 1;
                    int dayOfMonth = hasTime ? Integer.parseInt(timeSnapShot.child("dayOfMonth").getValue().toString()) : 1;
                    int hour       = hasTime ? Integer.parseInt(timeSnapShot.child("hour").getValue().toString()) : 1;
                    int minute     = hasTime ? Integer.parseInt(timeSnapShot.child("minute").getValue().toString()) : 0;
                    int second     = hasTime ? Integer.parseInt(timeSnapShot.child("second").getValue().toString()) : 0;

                    songInfoEditor.putBoolean("NotFound", false);
                    songInfoEditor.putString("songTitle", songName);
                    songInfoEditor.putString("url", url);
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
        SharedPreferences songInfoStorage = getSharedPreferences("SongInfoStorage", MODE_PRIVATE);
        boolean notFound = songInfoStorage.getBoolean("NotFound", false);
        if(notFound) {
            return null;
        }
        else {
            String songName = songInfoStorage.getString("songTitle", null);
            String url = songInfoStorage.getString("url", null);
            String lastListener = songInfoStorage.getString("lastListener", null);
            String otherListener = songInfoStorage.getString("otherListener", null);
            int year = songInfoStorage.getInt("year", 0);
            int monthValue = songInfoStorage.getInt("monthValue", 1);
            int dayOfMonth = songInfoStorage.getInt("dayOfMonth", 1);
            int hour = songInfoStorage.getInt("hour", 1);
            int minute = songInfoStorage.getInt("minute", 0);
            int second = songInfoStorage.getInt("second", 0);

            LocalDateTime localDateTime =  LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second);
            MockTime time = new MockTime(localDateTime);

            return new SongInfo(songTitle, url, lastListener, otherListener, time);

        }
    }
    */
}
