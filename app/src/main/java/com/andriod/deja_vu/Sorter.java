package com.andriod.deja_vu;

import android.media.MediaMetadataRetriever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Yuying Li on 2018/3/16.
 */

public abstract class Sorter {
    private int first, second, third;
    public Sorter(int first_, int second_, int third_){
        first = first_;
        second = second_;
        third = third_;
    }
    public ArrayList<String> sort (ArrayList<String> files) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> result1 = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < files.size(); i++) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + files.get(i));
            result.add(mmr.extractMetadata(first) + "-"
                    + mmr.extractMetadata(second) + "-"
                    + mmr.extractMetadata(third));
            map.put(mmr.extractMetadata(first) + "-"
                    + mmr.extractMetadata(second) + "-"
                    + mmr.extractMetadata(third), files.get(i));
        }
        Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < result.size();i++) {
            result1.add(map.get(result.get(i)));
        }

        return result1;
    }
}
