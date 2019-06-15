package com.stucom.franmorenoalc.bonk.game;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.stucom.franmorenoalc.R;

public class GameOptionsActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);

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
