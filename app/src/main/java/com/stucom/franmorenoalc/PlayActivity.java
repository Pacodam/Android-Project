package com.stucom.franmorenoalc;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.franmorenoalc.views.UserScoreView;
import com.stucom.franmorenoalc.views.WormyView;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class PlayActivity extends AppCompatActivity
implements WormyView.WormyListener {

    // Sensors' related code

    private SensorManager sensorManager;

    private UserScoreView userScoreView;
    private int score;
    private int level;
    private String token;
    private SharedPreferences prefs;

    private WormyView wormyView;
    private TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        token = prefs.getString("token", null);

        wormyView = findViewById(R.id.wormyView);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        tvScore = findViewById(R.id.tvScore);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvScore.setText("0");
                wormyView.newGame();
            }
        });
        wormyView.setWormyListener(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

   /*
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Read the sensor's information
        float ax = sensorEvent.values[0];
        float ay = sensorEvent.values[1];
        float az = sensorEvent.values[2];
        *//*
        tvAccelerationX.setText(getString(R.string.accelerationX, ax));
        tvAccelerationY.setText(getString(R.string.accelerationY, ay));
        tvAccelerationZ.setText(getString(R.string.accelerationZ, az));
        accelerometerView.onSensorChanged(sensorEvent); *//*
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //accelerometerView.onAccuracyChanged(sensor, accuracy);
    }*/

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_A:
                wormyView.update(0, +10);
                break;
            case KeyEvent.KEYCODE_Q:
                wormyView.update(0, -10);
                break;
            case KeyEvent.KEYCODE_O:
                wormyView.update(-10, 0);
                break;
            case KeyEvent.KEYCODE_P:
                wormyView.update(+10, 0);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void scoreUpdated(View view, int score) {
        tvScore.setText(String.valueOf(score));
    }

    @Override
    public void gameLost(View view) {
        Toast.makeText(this, getString(R.string.you_lost), Toast.LENGTH_LONG).show();
    }

}
        /*//userScoreView = findViewById(R.id.userScoreView);
        findViewById(R.id.btnRandom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = (int)(Math.random()*1000000);
                userScoreView.setScore(score);
            }
        });

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //we send level value of 1 until the game is done on future
                level = 1;
                saveScore(token, 1, score);
            }
        });
*/



    /**
     * Al clicar en enviar la puntuacion obtenida se guarda
     *//*
    public  void saveScore(final String token, final int score, final int level) {


        String URL = "https://api.flx.cat/dam2game/user/score";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override public void onResponse(String response) {
                String json = response;
                Gson gson = new Gson();
                Type typeToken = new TypeToken<APIResponse>() {}.getType();
                APIResponse apiResponse = gson.fromJson(json, typeToken);
                if(apiResponse.getErrorCode() == 0){
                    Toast.makeText(getApplicationContext(), "Score saved!", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                }

            }

        }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("level", String.valueOf(level));
                params.put("score", String.valueOf(score));
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);

    }


}
*/