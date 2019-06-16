package com.stucom.franmorenoalc.bonk.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stucom.franmorenoalc.R;
import com.stucom.franmorenoalc.bonk.engine.Audio;

public class GameOptionsActivity extends Activity {

    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);
        
        mediaPlayer.create(GameOptionsActivity.this,R.raw.music);
        mediaPlayer.start();

        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String score = prefs.getString("score", "");
        if(score != null) {
            Log.d("flx", score);
        }
        //Buttons for different options of the app
        Button playButton = findViewById(R.id.btn_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOptionsActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        Button resumeButton = findViewById(R.id.btn_resume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOptionsActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }

}
