package com.andriod.deja_vu;


/**
 * Created by Yuying Li on 2018/3/16.
 */

import android.media.MediaMetadataRetriever;

public class TitleSorter extends Sorter{
    public TitleSorter(){
        super(MediaMetadataRetriever.METADATA_KEY_TITLE, MediaMetadataRetriever.METADATA_KEY_ARTIST, MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }
}