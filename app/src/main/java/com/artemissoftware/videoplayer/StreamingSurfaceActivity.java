package com.artemissoftware.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;

import com.artemissoftware.videoplayer.constants.VideoUrl;

public class StreamingSurfaceActivity extends AppCompatActivity implements
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback,
        MediaController.MediaPlayerControl{

    private static final String TAG = "StreamingSurfaceActivit";

    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;

    private ProgressBar pg_bar_download;
    private String vidAddress = VideoUrl.VIDEO_2;


    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    private MediaController mediaController;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_surface);

        vidSurface = (SurfaceView) findViewById(R.id.surfView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);

        mediaController = new MediaController(this);

        pg_bar_download  = (ProgressBar) findViewById(R.id.pg_bar_download);
    }


    private void startVideoPlayback() {

        vidHolder.setFixedSize(mVideoWidth, mVideoHeight);
        mediaPlayer.start();
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vidHolder);
            mediaPlayer.setDataSource(vidAddress);
            mediaPlayer.prepare();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaController.setMediaPlayer(this);
            mediaController.setAnchorView(vidSurface);

            handler.post(new Runnable() {
                public void run() {
                    mediaController.setEnabled(true);
                    mediaController.show();
                }
            });

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        pg_bar_download.setVisibility(View.VISIBLE);
        pg_bar_download.setMax(100);
        pg_bar_download.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        pg_bar_download.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }

        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }



    @Override
    public void onPrepared(MediaPlayer mp) {

        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }






    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }




    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }


    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        mediaController.hide();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mediaController.show();
        return false;
    }



    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() { return mediaPlayer.getDuration(); }

    public int getCurrentPosition() { return mediaPlayer.getCurrentPosition(); }

    public void seekTo(int i) { mediaPlayer.seekTo(i); }

    public boolean isPlaying() { return mediaPlayer.isPlaying(); }

    public int getBufferPercentage() { return 0; }

    public boolean canPause() { return true; }

    public boolean canSeekBackward() { return true; }

    public boolean canSeekForward() { return true; }


    @Override
    public int getAudioSessionId() {
        return 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.video_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.option_download:

                mediaPlayer.pause();
                pg_bar_download.setVisibility(View.VISIBLE);
                new DownloadVideoTask(getApplicationContext(), pg_bar_download).execute(vidAddress);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
