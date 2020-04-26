package com.artemissoftware.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private Button btn_stream_player, btn_stream_surface_player, btn_exo_player, btn_wowza_streaming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_stream_player = (Button) findViewById(R.id.btn_stream_player);
        btn_stream_player.setOnClickListener(btn_stream_player_OnClickListener);

        btn_stream_surface_player = (Button) findViewById(R.id.btn_stream_surface_player);
        btn_stream_surface_player.setOnClickListener(btn_stream_surface_player_OnClickListener);

        btn_exo_player = (Button) findViewById(R.id.btn_exo_player);
        btn_exo_player.setOnClickListener(btn_exo_player_OnClickListener);

        btn_exo_player = (Button) findViewById(R.id.btn_exo_player);
        btn_exo_player.setOnClickListener(btn_exo_player_OnClickListener);

        btn_wowza_streaming = (Button) findViewById(R.id.btn_wowza_streaming);
        btn_wowza_streaming.setOnClickListener(btn_wowza_streaming_OnClickListener);
    }


    Button.OnClickListener btn_stream_player_OnClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), StreamingActivity.class);
            startActivity(intent);
        }
    };

    Button.OnClickListener btn_stream_surface_player_OnClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), StreamingSurfaceActivity.class);
            startActivity(intent);
        }
    };

    Button.OnClickListener btn_exo_player_OnClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
            startActivity(intent);
        }
    };

    Button.OnClickListener btn_wowza_streaming_OnClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), LiveStreamingActivity.class);
            startActivity(intent);
        }
    };
}
