package com.andriod.deja_vu;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Seth D'Agostino on 3/5/2018.
 */

@IgnoreExtraProperties
public class SongInfo {

    private String songTitle;
    private String url;
    private String lastListener;
    private String otherListener;
    private CurrentTime currentTime;
    private String fileName;

    public SongInfo(String songTitle, String url, CurrentTime currentTime, String fileName, String lastListener) {
        this.songTitle = songTitle;
        this.url = url;
        this.lastListener = lastListener;
        this.otherListener = null;
        this.fileName = fileName;
        this.currentTime = currentTime;
    }
    public SongInfo (String songTitle, String url, String lastListener, String otherListener, CurrentTime currentTime, String fileName) {
        this.songTitle = songTitle;
        this.url = url;
        this.lastListener = lastListener;
        this.otherListener = otherListener;
        this.currentTime = currentTime;
        this.fileName = fileName;
    }

    public SongInfo(SongInfo other) {
        this.songTitle = other.getSongTitle();
        this.url = other.getUrl();
        this.lastListener = other.getLastListener();
        this.otherListener = other.getOtherListener();
        this.currentTime = other.getCurrentTime();
        this.fileName = other.getFileName();
    }

    public String getSongTitle() { return songTitle; }
    public String getUrl() { return url; }
    public String getLastListener() { return lastListener; }
    public String getOtherListener() { return otherListener; }
    public CurrentTime getCurrentTime() { return currentTime; }
    public  String getFileName () {return fileName;}

    public void setSongTitle(String songTitle) { this.songTitle = new String(songTitle); }
    public void setUrl(String songUrl) { url = new String(songUrl);}
    public void setLastListener(String newListener) { lastListener = new String(newListener);}
    public void setOtherListener(String differentListener) { otherListener = new String(differentListener);}
    public void setCurrentTime(CurrentTime currentTime) { this.currentTime = currentTime; }


    public void updateSongInfo(String newListener, CurrentTime currentTime) {
        updateListeners(newListener);
        setCurrentTime(currentTime);
    }

    public void updateListeners(String newListener) {
        if(lastListener == null) {
            setLastListener(newListener);
        }
        else if(otherListener == null) {
            setOtherListener(getLastListener());
            setLastListener(newListener);
        }
        else if(newListener.equals(lastListener))
            return;
        else if(newListener.equals(otherListener)) {
            setOtherListener(getLastListener());
            setLastListener(newListener);
        }
        else {
            setLastListener(newListener);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SongInfo){
            return ((SongInfo) o).getSongTitle() == getSongTitle() &&
                    ((SongInfo) o).getUrl() == getUrl() &&
                    ((SongInfo) o).getLastListener() == getLastListener() &&
                    ((SongInfo) o).getOtherListener() == getOtherListener() &&
                    ((SongInfo) o).getCurrentTime().equals(getCurrentTime());
        }
        else {
            return false;
        }

    }

    public void saveSongInfo() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference();
        ref.child("Song Info").child(songTitle).setValue(this);
        Log.d("SAVE_SONG_INFO", "Saving song info for " + songTitle);
    }

    public static void saveNewSongWithNoDuplicates(final String songTitle, final String url, final String fileName) {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("Song Info");

        //Get the song info and update it or save it
        Query queryRef = ref.orderByChild("songTitle").equalTo(songTitle);
        queryRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    Log.d("Save New Song With No Updates", "Song not found, adding new song");
                    SongInfo songInfo = new SongInfo(songTitle, url, null, fileName, null);
                    songInfo.saveSongInfo();
                }
                else {
                    Log.d("Save New Song With No Updates", "Song found, not adding a duplicate");
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Save New Song With No Updates", "Failed to read value.", error.toException());
            }
        });
    }

}
