package com.stucom.franmorenoalc;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class QuantActivity extends AppCompatActivity {

    /* La Activity despliega un texto, una imagen y un Button que redirige
     a una Google MapsActivity que muestra la ubicación del centro Stucom.
      *  */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quant);

        Button btnMap = findViewById(R.id.gmapButton);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(  QuantActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }



}
