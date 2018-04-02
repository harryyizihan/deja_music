package com.andriod.deja_vu;

/**
 * Created by Yuying Li on 2018/3/16.
 */

import android.media.MediaMetadataRetriever;


public class AlbumSorter extends Sorter {
    public AlbumSorter(){
        super(MediaMetadataRetriever.METADATA_KEY_ALBUM, MediaMetadataRetriever.METADATA_KEY_ARTIST, MediaMetadataRetriever.METADATA_KEY_TITLE);
    }
}
