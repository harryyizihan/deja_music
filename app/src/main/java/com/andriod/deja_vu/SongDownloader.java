package com.andriod.deja_vu;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.File;
import java.net.URL;

/**
 * Created by Yuying Li on 2018/3/16.
 */

public class SongDownloader extends Downloader{

    public String returnSongName;

    public SongDownloader(Context context){
        super(context);
    }

    public void setDownloadDescription(final Uri uri){
          downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
          DownloadManager.Request request = new DownloadManager.Request(uri);

          final String songName = URLUtil.guessFileName(uri.toString(),null,null);
          Log.d("songName", songName);

          // description of download
          request.setTitle(songName);
          returnSongName = songName;

          request.setDescription(uri.toString());
          request.setDestinationInExternalFilesDir(context,"Songs", songName);
          request.allowScanningByMediaScanner();
          request.setVisibleInDownloadsUi(true);

          // download begins when download manager is available
          downloadReference = downloadManager.enqueue(request);
    }

    public void checkStatus(int status, int reason, final Uri uri){
          switch (status){
              case DownloadManager.STATUS_SUCCESSFUL:
                  Toast.makeText(context, "Song Downloaded!", Toast.LENGTH_LONG).show();
                  MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                  mmr.setDataSource("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + returnSongName);
                  SongInfo.saveNewSongWithNoDuplicates(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), uri.toString(), returnSongName);
                  break;
              case DownloadManager.STATUS_PAUSED:
                  Toast.makeText(context, "Download Paused", Toast.LENGTH_LONG).show();
                  break;
              case DownloadManager.STATUS_FAILED:
                  Toast.makeText(context, "Download Failed:" + reason, Toast.LENGTH_LONG).show();
                  break;
          }
    }

    public String getSongName () {
        return returnSongName;
    }
}
