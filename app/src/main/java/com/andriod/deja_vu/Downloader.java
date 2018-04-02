package com.andriod.deja_vu;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

/**
 * Created by Yuying Li on 2018/3/16.
 */

public abstract class Downloader {
    public long downloadReference;
    DownloadManager downloadManager;
    public BroadcastReceiver recieverDownloadComplete;
    public Context context;

    public Downloader(Context aContext){
        context = aContext;
    }


    public void setDownloadDescription(final Uri uri){}

    public void checkStatus(int status,int reason, final Uri uri){}

    /* Download song by a URL */
    public long download(final Uri uri){

        setDownloadDescription(uri);

        Toast.makeText(context, "Start downloading!", Toast.LENGTH_LONG).show();
        // toast notification for completed download
        recieverDownloadComplete = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                long ref = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if(downloadReference == ref){
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(ref);
                    Cursor cursor = downloadManager.query(query);
                    cursor.moveToFirst();

                    int columnIndex =  cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));

                    checkStatus(status,reason,uri);
                    Log.d("downloader", "checkstatus done!!!!!!!!!!!!!!!");

                }
            }
        };

        context.registerReceiver(recieverDownloadComplete, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        return downloadReference;
    }


}
