package com.stucom.franmorenoalc.bonk.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.stucom.franmorenoalc.MainActivity;
import com.stucom.franmorenoalc.R;
import com.stucom.franmorenoalc.RegistryActivity;
import com.stucom.franmorenoalc.bonk.engine.Audio;

public class GameOptionsActivity extends Activity {

    static String function;
    static String token;
    static Class<?> desti = RegistryActivity.class;
    static SharedPreferences prefs;
    static SharedPreferences.Editor prefsEditor;
    MediaPlayer mp;
    String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);

        mp = new MediaPlayer();
        mp.release();
        mp=MediaPlayer.create(this,R.raw.music2);
        mp.start();
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        score = prefs.getString("score", null);
        //saving the score (only if token exists!)
        if(score != null) {
            Log.d("flx", score);
            saveScore();
        }
        //Buttons for different options of the app
        Button playButton = findViewById(R.id.btn_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                Intent intent = new Intent(GameOptionsActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        Button resumeButton = findViewById(R.id.btn_resume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                Intent intent = new Intent(GameOptionsActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        Button menuButton = findViewById(R.id.btn_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                Intent intent = new Intent(GameOptionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveScore(){
        getToken();
        //si no hay un token en SharedPreferences, aparecerá un alert dialog invitando a registrarse
        if(token == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.notRegistered);
            builder.setMessage("Required login to save stats")
                    .setCancelable(false)
                    .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(GameOptionsActivity.this, desti);
                            function = "main";
                            intent.putExtra("func", function);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            //here should be used volley to save data, for now only toast
            Toast.makeText(getApplicationContext(), "Score saved", Toast.LENGTH_SHORT).show();
            prefs.edit().remove("score").apply();
        }

    }

    public void getToken() {
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        prefsEditor = prefs.edit();
        token = prefs.getString("token", null);
    }

}
