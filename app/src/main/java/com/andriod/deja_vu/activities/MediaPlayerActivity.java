package com.andriod.deja_vu.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andriod.deja_vu.ActualTime;
import com.andriod.deja_vu.CurrentTime;
import com.andriod.deja_vu.PlacePlayed;
import com.andriod.flashback_music_ui.R;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class MediaPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private String current_song_path;
    private Location currentLocation = new Location("");
    private String currentAddress = "";
    private Queue<String> trackList;
    private static int mode;
    private LocalDateTime localDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_media_player);

        // example use of getting mock time from shared preference
        SharedPreferences sharedPreferences = getSharedPreferences("Time", MODE_PRIVATE);
        String current_time =  sharedPreferences.getString("mocktime", null);
        if (current_time == null){

            localDateTime = LocalDateTime.now();
            current_time = localDateTime.toString();
            Log.d("current_time from sharedPref", "is null");
            Log.d(" so set time to", current_time);
        }
        else{
            localDateTime = LocalDateTime.parse(current_time);
            Log.d("current_time from sharedPref", localDateTime.toString());
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1C2331")));

        mode = getIntent().getIntExtra("mode", 0);
        switch (mode) {
            case 0: Log.d("In switch mode: ", "mode is 0, which means is not set yet.");
                break;

            case 1:
                actionBar.setTitle("Play Song");
                playSongList();
                break;

            case 2:
                actionBar.setTitle("Play Album");
                trackListGenerator();
                playAlbumList();
                break;

            case 3:
                actionBar.setTitle("Play Flashback");
                trackListGenerator();
                playFlashbackList();
                break;

            case 4:
                actionBar.setTitle("Vibe Mode");
                playVibeList();
                break;

            default: Log.d("In switch mode: ", "mode is even not 0 lol");
                break;
        }

    }

    private void trackListGenerator () {
        switch (mode) {
            case 2:
                String current_album = getIntent().getStringExtra("albumName");
                File current_dir = new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs");
                trackList = new LinkedList<>();
                File[] albumFiles = current_dir.listFiles();
                for (File file : albumFiles) {
                    //Log.d(“the file is”, file.getName());

                    if (file.getName().contains(".mp3")) {
                        SharedPreferences sharedPreferences = getSharedPreferences(file.getName(), MODE_PRIVATE);

                        if (sharedPreferences.getString("album", null).equals(current_album)) {
                            if (!(sharedPreferences.getInt("status", -2) == -1)) {
                                trackList.offer(file.getName());
                            }
                        }
                        else{
                            //Log.d(“NO ALBUM”, “next”);

                        }
                    }
                }
                break;
            case 3:
                PriorityQueue<String> pq = new PriorityQueue<>(new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        SharedPreferences s1 = getSharedPreferences(s, MODE_PRIVATE);
                        SharedPreferences s2 = getSharedPreferences(t1, MODE_PRIVATE);

                        if (s1.getInt("flashbackScore", 0) > s2.getInt("flashbackScore", 0)) {
                            return -1;
                        }
                        else if (s1.getInt("flashbackScore", 0) < s2.getInt("flashbackScore", 0)) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    }
                });

                File[] files = new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs").listFiles();
                for (File file : files) {
                    if (!file.isDirectory() && file.getName().contains(".mp3")) {
                        SharedPreferences sharedPreferences = getSharedPreferences(file.getName(), MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (sharedPreferences.getInt("status", -2) == -1) {
                            continue;
                        }

                        float longitude = sharedPreferences.getFloat("longitude", 0);
                        float latitude = sharedPreferences.getFloat("latitude", 0);
                        String dateTime = sharedPreferences.getString("time", null);
                        int status = sharedPreferences.getInt("status", -2);

                        editor.putInt("flashbackScore", evaluateFlashbackScore((int)longitude, (int)latitude, dateTime, status, sharedPreferences));
                        editor.apply();
                        pq.offer(file.getName());
                    }
                }

                trackList = new LinkedList<>();
                while (!pq.isEmpty()) {
                    String curr = trackList.peek();
                    SharedPreferences sharedPreferences = getSharedPreferences(curr, MODE_PRIVATE);
                    Log.d("Current Flashback Song Score is", ((Integer) sharedPreferences.getInt("flashbackScore", 0)).toString());
                    trackList.offer(pq.poll());
                }
                break;

            default:
                Log.d("In trackListGenerator switch statement: ", "mode is neither 2 nor 3");
                break;
        }
    }

    private void playSongList () {
        launchSong(getIntent().getStringExtra("fileName"));
    }

    private void playAlbumList () {
        launchSong(trackList.poll());
    }

    private void playFlashbackList () {
        launchSong(trackList.poll());
    }

    private void playVibeList () {
        launchSong(getIntent().getStringExtra("songName"));

        TextView textView = findViewById(R.id.textView2);

        String whoAmI;
        SharedPreferences s1 = getSharedPreferences("friends", MODE_PRIVATE);
        ArrayList<String> friendsList = new ArrayList<>();
        Set<String> dummy = new HashSet<>();
        dummy.add("dummy");
        friendsList.addAll(s1.getStringSet("list", dummy));
        boolean flag1 = false;
        boolean flag2 = false;
        String lastPlayedPerson = getIntent().getStringExtra("lastPlayed");
        for (int i = 0; i < friendsList.size(); i++) {
            if (friendsList.get(i).contains(" ")) {
                whoAmI = friendsList.get(i);
                for (int j = 0; j < whoAmI.length(); j++) {
                    if (whoAmI.charAt(j) == ' ') {
                        whoAmI = whoAmI.substring(0, j);
                        if (whoAmI.equals(lastPlayedPerson)) {
                            textView.setText("Last Played by: You");
                            flag1 = true;
                            break;
                        }
                    }
                }
            }

            if (friendsList.get(i).equals(lastPlayedPerson)) {
                textView.setText("Last Played by: " + lastPlayedPerson);
                flag2 = true;
                break;
            }
        }

        if (!flag1 && !flag2) {
            textView.setText("Last Played by: Stephen Hawking:)");
        }
    }


    private void launchSong (String fileName) {
        current_song_path = "/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + fileName;
        final SharedPreferences sharedPreferences = getSharedPreferences(fileName, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        TextView songName = findViewById(R.id.SongName);
        songName.setText("Song Name: " + sharedPreferences.getString("title",null));
        TextView albumName = findViewById(R.id.album);
        albumName.setText("Album Name: " + sharedPreferences.getString("album", null));
        TextView artistName = findViewById(R.id.artist);
        artistName.setText("Artist Name: " + sharedPreferences.getString("artist", null));
        TextView dateTime = findViewById(R.id.LocationDate);
        String location = sharedPreferences.getString("location",null);
        if (location == null) {
            dateTime.setText("First time played");
        }
        else {
            dateTime.setText(sharedPreferences.getString("location", null) + " / " + sharedPreferences.getString("time", null));
        }

        final Button toggleButton = (Button) findViewById(R.id.ToggleButton);
        int songStatus = sharedPreferences.getInt("status", -2);
        if(songStatus == 1){
            Log.d("SONG_FAVORITED", "Song is favorited");
            toggleButton.setTag(1);
            toggleButton.setBackgroundResource(R.drawable.like);
            toggleButton.getLayoutParams().width = 92;
            toggleButton.getLayoutParams().height = 80;
        }
        else if(songStatus == -1){
            Log.d("SONG_DISLIKED", "Song is disliked");
            toggleButton.setTag(-1);
            toggleButton.setBackgroundResource(R.drawable.dislike);
            toggleButton.getLayoutParams().width = 92;
            toggleButton.getLayoutParams().height = 80;
        }
        else if (songStatus == 0){
            Log.d("SONG_NEUTRAL", "Song is neutral");
            toggleButton.setTag(0);
            toggleButton.setBackgroundResource(R.drawable.neutral);
            toggleButton.getLayoutParams().width = 92;
            toggleButton.getLayoutParams().height = 80;
        }
        else {
            Log.d("Error for toggle button!!!!!!!!!!!!!!", "");
        }

        toggleButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final int status =(Integer) v.getTag();
                if(status == 0) {
                    //mPlayer.start();
                    toggleButton.setBackgroundResource(R.drawable.like);
                    toggleButton.getLayoutParams().width = 92;
                    toggleButton.getLayoutParams().height = 80;
                    editor.putInt("status", 1);
                    editor.apply();

                    v.setTag(1); //pause
                }
                else if(status == 1){
                    toggleButton.setBackgroundResource(R.drawable.dislike);
                    toggleButton.getLayoutParams().width = 92;
                    toggleButton.getLayoutParams().height = 80;
                    editor.putInt("status", -1);
                    editor.apply();

                    v.setTag(-1);
                }
                else if (status == -1){
                    toggleButton.setBackgroundResource(R.drawable.neutral);
                    toggleButton.getLayoutParams().width = 92;
                    toggleButton.getLayoutParams().height = 80;
                    editor.putInt("status", 0);
                    editor.apply();

                    v.setTag(0); //pause
                }
                else {
                    Log.d("error", "setting toggle button");
                }
            }
        });

        /*-------------------------LOCATION and TIME stuff-----------------------------------*/


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LOCATION: ", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                currentLocation = new Location(location);
                double longitude = currentLocation.getLongitude();
                double latitude = currentLocation.getLatitude();
                editor.putFloat("longitude", ((float) longitude));
                editor.putFloat("latitude", (float)latitude);
                editor.apply();

                String whoAmI = "";
                SharedPreferences s1 = getSharedPreferences("friends", MODE_PRIVATE);
                ArrayList<String> friendsList = new ArrayList<>();
                Set<String> dummy = new HashSet<>();
                dummy.add("dummy");
                friendsList.addAll(s1.getStringSet("list", dummy));
                CurrentTime currentTimeGG = new ActualTime();
                for (int i = 0; i < friendsList.size(); i++) {
                    if (friendsList.get(i).contains(" ")) {
                        whoAmI = friendsList.get(i);
                    }
                }

                for (int i = 0; i < whoAmI.length(); i++) {
                    if (whoAmI.charAt(i) == ' ') {
                        whoAmI = whoAmI.substring(0, i);
                        break;
                    }
                }
                PlacePlayed.savePlacePlayedWithNoDuplicates(location);
                final String inputTitle = sharedPreferences.getString("title", null);
                final String inputUri = sharedPreferences.getString("uri", null);
                final String inputPerson = whoAmI;
                final String fileName = sharedPreferences.getString("fileName", null);
                PlacePlayed.updatePlacesPlayedForThisSongAndLocation(location, inputTitle, inputUri, inputPerson, currentTimeGG, fileName);



                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    Log.d("Geocoder", "Trying geocoder");
                    List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (null != listAddresses && listAddresses.size() > 0) {
                        Log.d("Geocoder", "Obtained address");
                        currentAddress = listAddresses.get(0).getLocality();
                        LocalDateTime currentTime = localDateTime;
                        editor.putString("location", currentAddress);
                        editor.putString("time", currentTime.toString());
                        editor.apply();
                        Log.d("current address", currentAddress);
                        Log.d("current date time", currentTime.toString());
                    }
                    else {
                        Log.d("Error getting the name of city", "via long and lat");
                    }
                } catch (IOException e) {
                    Log.d("Geocoder", "EXCEPTION");
                    e.printStackTrace();
                }
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
        locationManager.requestSingleUpdate(locationProvider, locationListener, null);

        editor.apply();
        loadMedia(current_song_path);
    }

    public void loadMedia(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("SONG_LOADING", "song is successfully loaded");

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mp){
                mediaPlayer.start();
            }

        });


        Button playButton = (Button) findViewById(R.id.PlayButton);
        playButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                    }
                }
        );

        Button pauseButton = (Button) findViewById(R.id.PauseButton);
        pauseButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                        }
                    }

                }
        );

        Button resetButton = (Button) findViewById(R.id.ResetButton);
        resetButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if (mode == 1) {
                            mediaPlayer.reset();
                            launchSong(getIntent().getStringExtra("fileName"));
                        }
                        else if (mode == 4) {
                            mediaPlayer.reset();
                            launchSong(getIntent().getStringExtra("songName"));
                        }
                        else {
                            if (trackList.isEmpty()) {
                                if (mode == 2) {
                                    Toast.makeText(getApplicationContext(), "Reach the end of the album!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Reach the end of the flashback list!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mediaPlayer.release();
                                if (mode == 2) {
                                    playAlbumList();
                                }
                                else {
                                    playFlashbackList();
                                }
                            }
                        }
                    }
                }
        );

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mode == 1) {
                    mediaPlayer.reset();
                    launchSong(getIntent().getStringExtra("fileName"));
                }
                else if (mode == 4) {
                    mediaPlayer.reset();
                    launchSong(getIntent().getStringExtra("songName"));
                }
                else {
                    if (trackList.isEmpty()) {
                        if (mode == 2) {
                            Toast.makeText(getApplicationContext(), "Reach the end of the album!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Reach the end of the flashback list!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mediaPlayer.release();
                        if (mode == 2) {
                            playAlbumList();
                        }
                        else {
                            playFlashbackList();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private ArrayList<String> getListFiles(File parentDir) {
        ArrayList<String> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if(file.getName().endsWith(".mp3")){
                inFiles.add(file.getName());

                Log.d("filename", file.getName());
            }

        }
        return inFiles;
    }

    private int evaluateFlashbackScore(final int longitude, final int latitude, final String dateTime, int status, final SharedPreferences sharedPreferences) {
        int flashbackScore = 0;
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("location stuff", "lcoation changed");
                int currentLongitude = (int) location.getLongitude();
                int currentLatitude = (int)location.getLatitude();
                int locationScore = similarityOfLocation(longitude, currentLongitude, latitude, currentLatitude);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("locationScore", locationScore);

                // check if user wants to mock time
                SharedPreferences sharedPreferences = getSharedPreferences("Time", MODE_PRIVATE);
                String current_time =  sharedPreferences.getString("mocktime", null);
                if (current_time == null){
                    localDateTime = LocalDateTime.now();
                }
                else{
                    localDateTime = LocalDateTime.parse(current_time);
                }
                //LocalDateTime currentTime = LocalDateTime.now();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                LocalDateTime datetime = LocalDateTime.parse(dateTime, formatter);

                editor.putInt("dateTimeScore", similarityOfDateTime(localDateTime, datetime));
                editor.apply();
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
        locationManager.requestSingleUpdate(locationProvider, locationListener, null);

        if (status == 1) {
            flashbackScore += 10;
        }

        flashbackScore += sharedPreferences.getInt("dateTimeScore", 0);
        flashbackScore += sharedPreferences.getInt("locationScore", 0);
        return flashbackScore;
    }

    private int similarityOfDateTime (LocalDateTime t1, LocalDateTime t2) {
        if (!(t1.getYear() == t2.getYear())) {
            return 0;
        }

        if (!(t1.getMonth() == t2.getMonth())) {
            return 1;
        }

        if (!(t1.getDayOfMonth() == t2.getDayOfMonth())) {
            return 2;
        }

        if (!(t1.getHour() == t2.getHour())) {
            return 3;
        }

        if (!(t1.getMinute() == t2.getMinute())) {
            return 4;
        }

        if (!(t1.getSecond() == t2.getSecond())) {
            return 5;
        }

        return 6;
    }

    private int similarityOfLocation (int long1, int long2, int lat1, int lat2) {
        int diffLong = Math.abs(long1 - long2);
        int diffLat = Math.abs(lat1 - lat2);
        int result = 0;

        if (diffLong == 0) {
            result += 5;
        }
        else if (diffLong > 30) {
            result +=4;
        }
        else if (diffLong > 60) {
            result += 3;
        }
        else if (diffLong > 90) {
            result += 2;
        }
        else {
            result += 1;
        }

        if (diffLat == 0) {
            result += 5;
        }
        else if (diffLat > 30) {
            result +=4;
        }
        else if (diffLat > 60) {
            result += 3;
        }
        else if (diffLat > 90) {
            result += 2;
        }
        else {
            result += 1;
        }

        return result;
    }
}