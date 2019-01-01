package com.stucom.franmorenoalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /* Esta Activity muestra el menu principal, con cuatro botones que conducen respectivamente
    a cuatro Activities.
     */

    String token;
    String function;
    Class<?> desti = RegistryActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //whe get the token from sharedPreferences (if exist)
        getToken();
        //token = "3c488b7ff21eacf4b5954275160fe5933f2aa36a6aa0cf31dbfe295e2063edb6be94d27e1b499f3b0f137e258d6594920fd512d2ed08df54b08d8258853559ac";

        //if there is not a token on SharedPreferences, an alert dialog appears requiring registry
        if(token == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You're not registered");
            builder.setMessage("Some functions may not work")
                    .setCancelable(false)
                    .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MainActivity.this, desti);
                            function = "main";
                            intent.putExtra("func", function);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


        //Buttons for different options of the app
        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });

        Button btnRank = findViewById(R.id.btnRank);
        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(token == null){
                    function = "ranking";
                    alertRegistry();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, RankActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button btnAdjs = findViewById(R.id.btnAdjustments);
        btnAdjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(token == null){
                    function = "adjustments";
                    alertRegistry();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, AdjustmentsActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button btnQuant = findViewById(R.id.btnQuant);
        btnQuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, QuantActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Saves token into String if exists. If not, the String will have null value.
     */
    public void getToken() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //String mail = prefs.getString("mail", "");
        token = prefs.getString("token", null);
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
    }



    /**
     * Alert Dialog appears when user clicks on adjustments or ranking when he is unregistered
     */
    public void alertRegistry() {
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Option not allowed");
        alertDialog.setMessage("You should register.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Register", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, desti);
                intent.putExtra("func", function);
                startActivity(intent);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Later", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }
}




