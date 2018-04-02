package com.andriod.deja_vu;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by Seth D'Agostino on 3/10/2018.
 */

public class AllPlacesPlayed {
    HashMap<String, PlacePlayed> pastPlacesPlayed;

    public AllPlacesPlayed() {
        pastPlacesPlayed = new HashMap<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference placePlayedRef = firebaseDatabase.getReference().child("PastPlacesPlayed");

    }

    public AllPlacesPlayed(HashMap<String, PlacePlayed> pastPlacesPlayed) {
        this.pastPlacesPlayed = new HashMap<>(pastPlacesPlayed);
    }


}
