package com.andriod.deja_vu;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Yuying Li on 2018/3/9.
 */

public class AlbumDownloader extends Downloader{


    public String returnAlbumName;

    private String zipFile;
    private String unzipDestination;


    public AlbumDownloader(Context context){
        super(context);
    }

    public void setDownloadDescription(final Uri uri){
        downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        final String albumName = URLUtil.guessFileName(uri.toString(),null,null);
        Log.d("albumName", albumName);

        request.setTitle(albumName);
        returnAlbumName = albumName;
        request.setDescription(uri.toString());
        request.setDestinationInExternalFilesDir(context, "ZippedAlbums", albumName);
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        downloadReference = downloadManager.enqueue(request);
    }

    public void checkStatus(int status, int reason, final Uri uri){
        switch (status){
            case DownloadManager.STATUS_SUCCESSFUL:
                Toast toast= Toast.makeText(context,"Album download Complete",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP,25,400);
                toast.show();
                zipFile = "/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/ZippedAlbums/" + returnAlbumName;
                unzipDestination = "/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + returnAlbumName;
                unzip(zipFile, unzipDestination);
                Log.d("zip", "passed unzip");
                Log.d("zip", Environment.getExternalStorageDirectory().getAbsolutePath().toString());

                File file = new File("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs");
                File[] files = file.listFiles();
                for (File f : files) {
                    if (!f.isDirectory()) {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource("/storage/emulated/0/Android/data/com.andriod.flashback_music_ui/files/Songs/" + f.getName());
                        SongInfo.saveNewSongWithNoDuplicates(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), uri.toString(), f.getName());
                    }
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                Toast.makeText(context, "Download Paused", Toast.LENGTH_LONG).show();
                break;
            case DownloadManager.STATUS_FAILED:
                Toast.makeText(context, "Download Failed:" + reason, Toast.LENGTH_LONG).show();
                break;
        }
    }


    public boolean unzip(String zipFile, String unzipDestination) {

        ZipInputStream zis = null;
        int BUFFER_SIZE = 8192;

        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((ze = zis.getNextEntry()) != null) {
                String fileName = ze.getName();
                fileName = fileName.substring(fileName.indexOf("/") + 1);
                File file = new File(unzipDestination, fileName);
                File dir = ze.isDirectory() ? file : file.getParentFile();

                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Invalid path: " + dir.getAbsolutePath());

                if (ze.isDirectory()) continue;

                FileOutputStream fout = new FileOutputStream(file);
                try {
                        while ((count = zis.read(buffer)) != -1)

                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }

            }
        } catch (IOException ioe) {
            Log.d("i", ioe.getMessage());
            return false;
        } finally {
            if (zis != null)
                try {
                    zis.close();
                } catch (IOException e) {

                }
        }
        return true;
    }

}
