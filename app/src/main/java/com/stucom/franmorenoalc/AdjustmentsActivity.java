package com.stucom.franmorenoalc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stucom.franmorenoalc.model.Player;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdjustmentsActivity extends AppCompatActivity implements View.OnClickListener {


    EditText editName;
    TextView currentPlayer;
    Uri photoURI;
    ImageView photo;  //ImageView para colocar la fotografía de perfil
    Player player;
    String token;
    String mail;

    //the image encoded to Base64
    String encodedAvatar;
    String avatar;

    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    //variables para el AlertDialog
    Button gallery, camera, delete;

    /*If the player clicks on save settings, if no changes where made (new username and/or photo), then
   the app alerts with a message saying that there is nothing to save. If player enters an username
   or an image, then changesMade will be 1.
    */
    int changesMade = 0;

    /* On activity create, the token and mail from the user (stored on registry) is recovered from sharedpreferences.
     * Then, we call to a method that connects to the API and get any other info from the player if exists (avatar, etc).
      * After that previous step, we get the widgets (buttons, edittexts, etc, and alow the actual player to upload new
      * photos or delete, also he can delete the account (general or partial deleting). The email address is showed but not
      * allowed to modify until unregistry */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustments);

        //the needed views. We don't use the edit mail view, because this cannot be modified until unregistering.
        currentPlayer = findViewById(R.id.currentPlayer);
        editName = findViewById(R.id.yourName);
        photo = findViewById(R.id.yourPhoto);

        //buttons
        findViewById(R.id.uploadImage).setOnClickListener(this);
        findViewById(R.id.uploadGallery).setOnClickListener(this);
        findViewById(R.id.deleteImage).setOnClickListener(this);
        findViewById(R.id.saveSettings).setOnClickListener(this);
        findViewById(R.id.unregister).setOnClickListener(this);

        //we load token and mail stored in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mail = prefs.getString("mail", null);
        token = prefs.getString("token", null);
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();


        //load of player data from the server (if there is any data present)
        playerDataFromAPI();

    }


    /* On resume we reload the actual player using the loadFromPrefs method. We leave this
    commented, to focus solely on the api connection
     */
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String mail = prefs.getString("mail", "");
        editName.setText(name);

    }


    @Override
    public void onPause() {
        String name = editName.getText().toString();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("name", name);
        ed.apply();
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        // All buttons come here, so we decide based on their ids
        switch(view.getId()) {
            case R.id.uploadImage: getAvatarFromCamera(); break;
            case R.id.uploadGallery: getAvatarFromGallery(); break;
            case R.id.deleteImage: deleteAvatar(); break;
            case R.id.saveSettings: saveSettings(); break;
            case R.id.unregister: deleteAccount();
        }
    }

    // Needed for onActivityResult()
    private static final int AVATAR_FROM_GALLERY = 1;
    private static final int AVATAR_FROM_CAMERA = 2;

    public void deleteAvatar() {
        // In this case simply clear the image by pasing null
        setAvatarImage(null, true);
    }

    public void getAvatarFromGallery() {
        // Call the Open Document intent searching for images
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, AVATAR_FROM_GALLERY);
    }

    public void getAvatarFromCamera() {
        // Prepare for storage (see FileProvider background documentation)
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Always this path
        File photo = new File(storageDir, "photo.jpg");
        try {
            boolean ok = photo.createNewFile();
            if (ok) Log.d("franmorenoalc", "Overwriting image");
        } catch (IOException e) {
            Log.e("franmorenoalc", "Error creating image file " + photo);
            return;
        }
        Log.d("franmorenoalc", "Writing photo to " + photo);
        // Pass the photo path to the Intent and start it
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoURI = FileProvider.getUriForFile(this, "com.stucom.franmorenoalc.fileProvider", photo);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, AVATAR_FROM_CAMERA);
        }
        catch (IllegalArgumentException e) {
            Log.e("franmorenoalc", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Skip cancels & errors
        if (resultCode != RESULT_OK) return;

        if (requestCode == AVATAR_FROM_GALLERY) {
            // coming from gallery, the URI is in the intent's data
            photoURI = data.getData();
        }
        // if camera, no action needed, as we set the URI when the intent was created

        // now set the avatar
        String avatar = (photoURI == null) ? null : photoURI.toString();
        setAvatarImage(avatar, true);

        try {
            encodedAvatar = Base64.encodeToString(readBytes(photoURI), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] readBytes(Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public void setAvatarImage(String avatar, boolean saveToSharedPreferences) {
        Log.d("flx", "PlayerAvatar = " + avatar);
        if (avatar == null) {
            // if null, set the default "unknown" avatar picture
            photo.setImageResource(R.drawable.your_photo);
        }
        else {
            // the URI must be valid, so we set it to the ImageView
            Uri uri = Uri.parse(avatar);
            photo.setImageURI(uri);
        }
        if (!saveToSharedPreferences) return;
        // comply if a save to prefs was requested
        player.setImage(avatar);
        player.saveToPrefs(this);
    }

    public void saveSettings() {
        String URL = "https://api.flx.cat/dam2game/user";
        StringRequest request = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override public void onResponse(String response) {
                String json = response.toString();
                Gson gson = new Gson();
                Type typeToken = new TypeToken<APIResponse>() {}.getType();
                APIResponse apiResponse = gson.fromJson(json, typeToken);
                Toast.makeText(getApplicationContext(),R.string.dataSaved,Toast.LENGTH_SHORT).show();
                currentPlayer.setText(editName.getText().toString());
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
                params.put("name", editName.getText().toString());
                params.put("image", encodedAvatar);

                return params;
            }
        };

        MyVolley.getInstance(this).add(request);
    }

    public void deleteAccount() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.deleteAccount);
        builder.setMessage(R.string.confirmDelete)
                .setCancelable(false)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAccountType();
                    }
                })
                .setNegativeButton(R.string.notSure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void deleteAccountType(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.deleteAccount);
        builder.setMessage(R.string.deleteType)
                .setCancelable(false)
                .setPositiveButton(R.string.deleteAll, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getApplicationContext(), "click1", Toast.LENGTH_SHORT).show();
                        deleteFromApi("true");
                    }
                })
                .setNegativeButton(R.string.unregisterDel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getApplicationContext(), "click2", Toast.LENGTH_SHORT).show();
                        deleteFromApi(null);
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void deleteFromApi(final String mustDelete){
        String URL = "https://api.flx.cat/dam2game/unregister";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override public void onResponse(String response) {
                String json = response.toString();
                Gson gson = new Gson();
                Type typeToken = new TypeToken<APIResponse>() {}.getType();
                APIResponse apiResponse = gson.fromJson(json, typeToken);
                Toast.makeText(getApplicationContext(), json, Toast.LENGTH_SHORT).show();
                if(apiResponse.getErrorCode() == 0){
                    //esborrem el token del SharedPreferences
                    MainActivity.prefsEditor.clear();
                    MainActivity.prefsEditor.commit();

                    if(mustDelete != null) {
                        alertBeforeUnregistry1();
                    }
                    else{
                        alertBeforeUnregistry2();
                    }
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
                if(mustDelete.equals("true")) {
                    params.put("must_delete", mustDelete);
                }
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);

    }

    /**
     * Alert Dialog appears when user clicks on adjustments or ranking when he is unregistered
     */
    public void alertBeforeUnregistry1() {
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(AdjustmentsActivity.this).create();
        alertDialog.setTitle(getApplicationContext().getString(R.string.deleteSuccess));
        alertDialog.setMessage(getApplicationContext().getString(R.string.deleteMsg));
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getApplicationContext().getString(R.string.retMenu), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AdjustmentsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }

    public void alertBeforeUnregistry2() {
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(AdjustmentsActivity.this).create();
        alertDialog.setTitle(getApplicationContext().getString(R.string.unregisterSuccess));
        alertDialog.setMessage(getApplicationContext().getString(R.string.unregisterMsg));
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getApplicationContext().getString(R.string.retMenu), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AdjustmentsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }


    /**
     * For the adjustments activity we only need the avatar and the name from the player.
     * If the user got previously uploaded name and avatar, we download at first from the
     * API and show in the current Activity.
     */
    public void playerDataFromAPI() {

        String URL = "https://api.flx.cat/dam2game/user?token="+ token;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(getApplicationContext(), "onResponse", Toast.LENGTH_SHORT).show();
                String json = response.toString();
                Gson gson = new Gson();
                Type typeToken = new TypeToken<APIResponse<Player>>() {}.getType();
                APIResponse<Player> apiResponse = gson.fromJson(json, typeToken);
                if(apiResponse.getErrorCode() == 0) {
                    player = apiResponse.getData();
                    currentPlayer.setText(player.getName());
                    if(player.getImage() != null) {
                        Picasso.get().load(player.getImage()).into(photo);
                    }
                    //Toast.makeText(getApplicationContext(), player2.getImage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

        });

        MyVolley.getInstance(this).add(request);

    }








}








