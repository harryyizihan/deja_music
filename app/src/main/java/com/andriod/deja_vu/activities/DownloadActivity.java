package com.andriod.deja_vu.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import android.widget.Toast;

import com.andriod.deja_vu.AlbumDownloader;
import com.andriod.deja_vu.CurrentTime;
import com.andriod.deja_vu.Downloader;
import com.andriod.deja_vu.MockTime;
import com.andriod.deja_vu.SongDownloader;
import com.andriod.flashback_music_ui.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DownloadActivity extends AppCompatActivity {

    private View mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mLayout = findViewById(R.id.main_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Download Entry Box");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1C2331")));

        // Download Button
        Button button = (Button)findViewById(R.id.button);
        final EditText editText = (EditText)findViewById(R.id.editText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText.getText().toString();
                Uri uri = Uri.parse(editText.getText().toString());
                if (s.contains("zip")) {
                    Log.d("It's a zipped file","");
                    AlbumDownloader albumDownloader = new AlbumDownloader(getApplicationContext());
                    albumDownloader.download(uri);
                }
                else if (s.contains("mp3")) {
                    SongDownloader songDownloader = new SongDownloader(getApplicationContext());
                    songDownloader.download(uri);
                    SharedPreferences sharedPreferences = getSharedPreferences(songDownloader.getSongName(), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("uri", uri.toString());
                    editor.apply();

                }
                else {
                    Toast.makeText(getApplicationContext(), "It's neither a mp3 nor zip file, try again!", Toast.LENGTH_LONG).show();
                }
            }
        });


        // Mock Time button
        Button timeButton = (Button)findViewById(R.id.mockTime);
        final EditText time = (EditText) findViewById(R.id.setTime);
        timeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("Time before", LocalDateTime.now().toString());
                String input_time = time.getText().toString();

                Log.d("Length of valid input", Integer.toString(input_time.length()));

                if(input_time != null && !input_time.isEmpty() && input_time.length() == 19){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                    LocalDateTime time = LocalDateTime.parse(input_time,formatter);

                    Log.d("Local Date time is Mocking time: ", time.toString());
                    CurrentTime mockTime = new MockTime(time);

                    // save mocked time to sharedPreferences (we can check if there is a mock time we should use when getting time)
                    SharedPreferences sharedPreferences = getSharedPreferences("Time", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("mocktime", mockTime.toString());
                    editor.apply();

                    Log.d("mockTime is now", mockTime.getLocalDateTime().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(),"You did not enter a valid time",Toast.LENGTH_LONG).show();
                    Log.d("entered time", "is null");
                }
            }
        });


        // unMock Time button
        Button unMockButton = (Button)findViewById(R.id.unMockTime);
        unMockButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                SharedPreferences sharedPreferences = getSharedPreferences("Time", MODE_PRIVATE);

                // if we have a mock time delete it
                if (sharedPreferences.getString("mocktime", null) != null){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("mocktime");
                    editor.apply();
                    Log.d("Stopped Mocking Time", LocalDateTime.now().toString());
                }

                time.setText("");

            }
        });


        Button googleSignIn = (Button) findViewById(R.id.button2);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadActivity.this, GoogleSignInActivity.class);
                startActivity(intent);
            }
        });
    }


}
