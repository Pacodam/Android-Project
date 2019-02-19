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

    //ef4d11182aa423caf0cf52537da64a7b6624974d1548c7ed44dc4529b4af194c4f56b4ac3e4e19a20d9776d77f81d0ac0121cc7c1365ce795e6423122fee7970
    String token;
    String function;
    static Class<?> desti = RegistryActivity.class;
    static SharedPreferences prefs;
    static SharedPreferences.Editor prefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //miramos si hay un token guardado en sharedPreferences, si no lo hay obtendremos null
        getToken();
        



        //si no hay un token en SharedPreferences, aparecerá un alert dialog invitando a registrarse
        if(token == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.notRegistered);
            builder.setMessage(R.string.someFunctions)
                    .setCancelable(false)
                    .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MainActivity.this, desti);
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
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        prefsEditor = prefs.edit();
        //prefsEditor.clear();
        //prefsEditor.commit();
        //String mail = prefs.getString("mail", "");
        token = prefs.getString("token", null);
        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
    }



    /**
     * Alert Dialog appears when user clicks on adjustments or ranking when he is unregistered
     */
    public void alertRegistry() {
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(R.string.notAllowed);
        alertDialog.setMessage(getApplicationContext().getString(R.string.registration));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getApplicationContext().getString(R.string.register), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, desti);
                intent.putExtra("func", function);
                startActivity(intent);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getApplicationContext().getString(R.string.later), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }
}




