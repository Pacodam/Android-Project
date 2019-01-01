package com.stucom.franmorenoalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.franmorenoalc.model.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RegistryActivity extends AppCompatActivity {


    //component vars
    static EditText editMail;
    Button verifyMail;
    static EditText editCode;
    Button verifyCode;
    //mail variable
    static String mail;
    //func is used to know where to redirect after registry
    String func;
    //desti is used to store the activity to redirect after registry
    Class<?> desti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        //per redireccionar després del registre hem de saber d'on venim, si de ranking,d'ajustaments o main, ho fem
        //recollint el extra de l'intent
        Intent i = getIntent();
        func = i.getStringExtra("func");
        //Toast.makeText(getApplicationContext(), func, Toast.LENGTH_SHORT).show();

        //agafem els elements
        editMail = findViewById(R.id.mailHint);
        verifyMail = findViewById(R.id.btnRegistro);
        editCode = findViewById(R.id.verifyHint);
        verifyCode = findViewById(R.id.buttonVerify);

        //amaguem l'edit del codi de verificació i el botó d'enviar. Al fer la primera crida amb exit la mostrarem de nou
        editCode.setVisibility(View.INVISIBLE);
        verifyCode.setVisibility(View.INVISIBLE);

        //quan es clica enviar mail s'activa la primera crida a registre. Si és exitosa, es faran visibles els elements de verificació
        verifyMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 cridaRegister1(); //crida registre 1 (enviar mail)
            }
        });

        //
        verifyCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cridaRegister2(); //crida registre 2 ( enviar codi verificació)
            }
        });


    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String mail = prefs.getString("mail", "");
        String code = prefs.getString("code", "");

        editMail.setText(mail);
        editCode.setText(code);
    }
    */

    /*
    @Override
    public void onPause() {

        String mail = editMail.getText().toString();
        String code = editCode.getText().toString();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("mail", mail);
        ed.putString("code", code);
        ed.apply();
        super.onPause();

    } */

    //final static String URL = "https://api.flx.cat/dam2game/register";

    /**
     * First register call to API. A mail is sent, a verify code is received on inbox
     */
    public  void cridaRegister1() {


        String URL = "https://api.flx.cat/dam2game/register";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override public void onResponse(String response) {
                String json = response.toString();
                Gson gson = new Gson();
                Type typeToken = new TypeToken<APIResponse>() {}.getType();
                APIResponse apiResponse = gson.fromJson(json, typeToken);
                if(apiResponse.getErrorCode() == 0){
                    mail = editMail.getText().toString(); //guardem el mail

                    //ahora se mostrarian los botones o sino un alert con el aviso para introducir el codigo verificacion
                    editCode.setVisibility(View.VISIBLE);
                    verifyCode.setVisibility(View.VISIBLE);

                    //amaguem editText de mail i buto
                    editMail.setVisibility(View.INVISIBLE);
                    verifyMail.setVisibility(View.INVISIBLE);

                }
                /*
                else{
                    editCode.setText("no esta bien");
                }*/

            }

            }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
               String message = error.toString();
               NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                }
                editCode.setText("ERROR " + message);
            }

        }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", editMail.getText().toString());
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);

    }

    //final static String URL = "https://api.flx.cat/dam2game/register";

    /**
     * Segona crida per verificació. S'envia el mail i el codi de verificació
     */
    public  void cridaRegister2() {


        String URL = "https://api.flx.cat/dam2game/register";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override public void onResponse(String response) {
                String json = response.toString();
                Gson gson = new Gson();
                Type typeToken = new TypeToken<APIResponse>() {}.getType();
                APIResponse apiResponse = gson.fromJson(json, typeToken);
                if(apiResponse.getErrorCode() == 0){
                    //Toast.makeText(getApplicationContext(), "aqui", Toast.LENGTH_SHORT).show();
                    //guardem el token a SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("mail", mail);
                    ed.putString("token", apiResponse.getData().toString());
                    ed.apply();
                    //recollim el lloc a on volem anar desde l'string func. Creem una class<?>
                    if(func.equals("ranking")){
                        desti = RankActivity.class;
                    }
                    else if(func.equals("adjustments")){
                        desti = AdjustmentsActivity.class;
                    }
                    else if(func.equals("main")){
                        desti = MainActivity.class;
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(RegistryActivity.this).create();
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Congratulations. Now you are registered");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Return", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent( RegistryActivity.this, desti);
                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialog.show();
                }
                /*
                else{
                    editCode.setText("no esta bien");
                }*/

            }

        }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                }
                editCode.setText("ERROR " + message);

            }

        }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", editMail.getText().toString());
                params.put("verify", editCode.getText().toString());
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);

    }






}
