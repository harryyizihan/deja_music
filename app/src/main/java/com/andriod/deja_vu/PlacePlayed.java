package com.andriod.deja_vu;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Seth D'Agostino on 3/5/2018.
 */

public class PlacePlayed {

    Location location;                          //Location of this place
    //HashMap<String, SongInfo> pastSongs;
    ArrayList<String> songsPlayedHere;            //Set of song titles played here (no duplicates)
    String pushID;

    //HELPERS
    static SongInfo songInfo;                          //Helper variable

    public PlacePlayed() { pushID = null; }

    public PlacePlayed(Location location) {
        this.location = location;
        pushID = null;
    }

    public Location getLocation() { return location; }
    //public HashMap<String, SongInfo> getPastSongs() { return pastSongs; }
    public ArrayList<String> getSongsPlayedHere() { return songsPlayedHere; }
    public String getPushID() { return pushID; }

    public void setLocation(Location location) { this.location = new Location(location); }
   // public void setPastSongs(HashMap<String, SongInfo> pastSongList) { pastSongs = new HashMap(pastSongList); }
    public void setSongsPlayedHere(ArrayList<String> songsPlayedHere) { this.songsPlayedHere = new ArrayList<String>(songsPlayedHere);}
    public void setPushID(String pushID) { this.pushID = pushID; }

    public static void savePlacePlayedWithNoDuplicates(final Location currentLocation) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("PastPlacesPlayed");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for(DataSnapshot key : keys) {
                    Log.d("SavePlacePlayedWithNoDuplicates", "Key: " + key.getKey());
                    double longitude = Double.parseDouble(key.child("location").child("longitude").getValue().toString());
                    double latitude = Double.parseDouble(key.child("location").child("latitude").getValue().toString());
                    Log.d("SavePlacePlayedWithNoDuplicates", "Latitude: " + latitude + " Longitude: " + longitude);
                    Location location = new Location("check");
                    location.setLongitude(longitude);
                    location.setLatitude(latitude);
                    if(currentLocation.distanceTo(location) < 305) {
                        Log.d("SavePlacePlayedWithNoDuplicates", "Found a duplicate");
                        return;
                    }
                }
                PlacePlayed newPlacePlayed = new PlacePlayed(currentLocation);
                newPlacePlayed.savePlacePlayed();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("SavePlacePlayedWithNoDuplicates", "Failed to read value.", databaseError.toException());
            }
        });

    }

    public static void updatePlacesPlayedForThisSongAndLocation(final Location currentLocation, final String songTitle, final String url, final String currentListener, final CurrentTime currentTime, final String fileName) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("PastPlacesPlayed");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for(DataSnapshot key : keys) {
                    Log.d("UpdatePlacesPlayedForThisSongAndLocation", "Key: " + key.getKey());
                    double longitude = Double.parseDouble(key.child("location").child("longitude").getValue().toString());
                    double latitude = Double.parseDouble(key.child("location").child("latitude").getValue().toString());
                    Log.d("UpdatePlacesPlayedForThisSongAndLocation", "Latitude: " + latitude + " Longitude: " + longitude);
                    Location location = new Location("check");
                    location.setLongitude(longitude);
                    location.setLatitude(latitude);
                    ArrayList<String> songsPlayedNearHere = new ArrayList<>();
                    songsPlayedNearHere.add(songTitle);
                    if(currentLocation.distanceTo(location) < 305) {
                        Log.d("UpdatePlacesPlayedForThisSongAndLocation", "Found a nearby location");
                        String pushID = key.child("pushID").getValue().toString();
                        Log.d("UpdatePlacesPlayedForThisSongAndLocation", "It's pushID is: " + pushID);

                        if(key.hasChild("songsPlayedHere")) {
                            Log.d("UpdatePlacesPlayedForThisSongAndLocation", "Getting list of songs played here");
                            for (DataSnapshot titleElement : key.child("songsPlayedHere").getChildren()) {
                                String title = titleElement.getValue().toString();
                                if (!songsPlayedNearHere.contains(title)) {
                                    Log.d("UpdatePlacesPlayedForThisSongAndLocation", "Adding " + title);
                                    songsPlayedNearHere.add(title);
                                }

                            }
                        }
                        PlacePlayed matchingPlacePlayed = new PlacePlayed(currentLocation);
                        matchingPlacePlayed.setPushID(pushID);
                        matchingPlacePlayed.setSongsPlayedHere(songsPlayedNearHere);
                        matchingPlacePlayed.updatePlacePlayedForThisSong(songTitle, url, currentListener, currentTime, fileName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("UpdatePlacesPlayedForThisSongAndLocation", "Failed to read value.", databaseError.toException());
            }
        });
    }


    public void updatePlacePlayedForThisSong(final String songTitle, final String url, final String currentListener, final CurrentTime currentTime, final String fileName) {

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("Song Info");
        if(!songsPlayedHere.contains(songTitle)) {
            Log.d("PLACE_PLAYED_UPDATE_PLACE_PLAYED_FOR_THIS_SONG", "Adding " + songTitle);
            songsPlayedHere.add(songTitle);
        }

        //Get the song info and update it or save it
        Query queryRef = ref.orderByChild("songTitle").equalTo(songTitle);
        songInfo = null;
        queryRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    Log.d("PLACE_PLAYED_UPDATE_PLACE_PLAYED_FOR_THIS_SONG", "Song not found");
                    songInfo = new SongInfo(songTitle, url, currentTime, fileName, currentListener);
                    songInfo.saveSongInfo();
                }
                else {
                    DataSnapshot snapshot = dataSnapshot.child(songTitle);

                    String songName = snapshot.child("songTitle").getValue().toString();
                    String url = snapshot.child("url").getValue().toString();
                    String lastListener = snapshot.hasChild("lastListener") ?  snapshot.child("lastListener").getValue().toString() : null;
                    String otherListener = snapshot.hasChild("otherListener") ? snapshot.child("otherListener").getValue().toString() : null;
                    String fileName = snapshot.hasChild("fileName") ? snapshot.child("fileName").getValue().toString() : null;
//                    MockTime time;
//                    if(!snapshot.hasChild("currentTime"))
//                        time = null;
//                    else {
//                        DataSnapshot timeSnapShot = snapshot.child("currentTime").child("localDateTime");
//                        int year = Integer.parseInt(timeSnapShot.child("year").getValue().toString());
//                        int monthValue = Integer.parseInt(timeSnapShot.child("monthValue").getValue().toString());
//                        int dayOfMonth = Integer.parseInt(timeSnapShot.child("dayOfMonth").getValue().toString());
//                        int hour = Integer.parseInt(timeSnapShot.child("hour").getValue().toString());
//                        int minute = Integer.parseInt(timeSnapShot.child("minute").getValue().toString());
//                        int second = Integer.parseInt(timeSnapShot.child("second").getValue().toString());
//                        Log.d("FINDSONG", "year: " + year + " monthValue: " + monthValue + " dayOfMonth: " + dayOfMonth + " hour: " + hour + " minute: " + minute + " second: " + second);
//                        LocalDateTime localDateTime = LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second);
//                        time = new MockTime(localDateTime);
//                    }

                    songInfo = new SongInfo(songTitle, url, lastListener, otherListener, null, fileName);
                    Log.d("FINDSONG", "LONG WAY " + songName + " " + url + " " + lastListener + " " + otherListener);
                    Log.d("FINDSONG", "songInfo object: " + songInfo.getUrl());
                    Log.d("FINDSONG", dataSnapshot.getValue().toString());
                    songInfo.updateSongInfo(currentListener, currentTime);
                    songInfo.saveSongInfo();
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("PLACE_PLAYED_UPDATE_PLACE_PLAYED_FOR_THIS_SONG", "Failed to read value.", error.toException());
            }
        });

        if(songInfo == null) {
            Log.d("PLACE_PLAYED_UPDATE_PLACE_PLAYED_FOR_THIS_SONG", "Song is null");
        }
        else {
            Log.d("PLACE_PLAYED_UPDATE_PLACE_PLAYED_FOR_THIS_SONG", "Song is not null");
        }

        songInfo = null;
        updatePlacePlayed();
    }


    public boolean matchesLocation(Location location) {
        return location.distanceTo(getLocation()) < 305;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PlacePlayed) {
            return ((PlacePlayed) o).getLocation().equals(location) &&
                    ((PlacePlayed) o).getSongsPlayedHere().equals(songsPlayedHere);
        }
        else {
            return false;
        }
    }

    public String savePlacePlayed() {
        Log.d("PLACEPLAYED", "Saving place played");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("PastPlacesPlayed");
        DatabaseReference pushedRef = ref.push();
        setPushID(pushedRef.getKey());
        pushedRef.setValue(this);
        return pushID;
    }

    public String updatePlacePlayed() {
        if(pushID == null) {
            savePlacePlayed();
            return pushID;
        }
        Log.d("PLACEPLAYED", "Updating place played");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference().child("PastPlacesPlayed").child(pushID);
        ref.setValue(this);
        return pushID;
    }
}
