package com.stucom.franmorenoalc;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.franmorenoalc.views.UserScoreView;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class PlayActivity extends AppCompatActivity {

    private UserScoreView userScoreView;
    private int score;
    private int level;
    private String token;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        token = prefs.getString("token", null);

        userScoreView = findViewById(R.id.userScoreView);
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

    }

    /**
     * Al clicar en enviar la puntuacion obtenida se guarda
     */
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
