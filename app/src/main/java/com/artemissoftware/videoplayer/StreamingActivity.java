package com.artemissoftware.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class StreamingActivity extends AppCompatActivity {

    private static final String TAG = "StreamingActivity";

    ProgressDialog pDialog;

    VideoView videoview;
    String vid_url ="https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        init();

        pDialog.show();

        try {
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(videoview);

            Uri video = Uri.parse(vid_url);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        videoview.requestFocus();

    }

    private void init(){

        videoview = (VideoView) findViewById(R.id.videoView);
        videoview.setOnPreparedListener(mediaPlayer_OnPreparedListener);
        videoview.setOnCompletionListener(mediaPlayer_OnCompletionListener);


        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Video Stream");
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
    }



    MediaPlayer.OnPreparedListener mediaPlayer_OnPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {

            pDialog.dismiss();
            videoview.start();
        }
    };


    MediaPlayer.OnCompletionListener mediaPlayer_OnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            finish();
        }
    };

}
