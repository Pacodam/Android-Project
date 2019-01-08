package com.stucom.franmorenoalc;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlayActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        prefsEditor.clear();
        prefsEditor.commit();
    }
}
