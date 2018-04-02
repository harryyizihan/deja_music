package com.andriod.deja_vu;

import android.media.MediaMetadataRetriever;

/**
 * Created by Yuying Li on 2018/3/16.
 */

public class ArtistSorter extends Sorter{
    public ArtistSorter(){
        super(MediaMetadataRetriever.METADATA_KEY_ARTIST, MediaMetadataRetriever.METADATA_KEY_ALBUM, MediaMetadataRetriever.METADATA_KEY_TITLE);
    }
}
