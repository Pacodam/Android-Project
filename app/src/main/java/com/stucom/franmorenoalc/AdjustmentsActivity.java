package com.stucom.franmorenoalc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stucom.franmorenoalc.model.Player;

import java.io.File;
import java.io.IOException;

public class AdjustmentsActivity extends AppCompatActivity implements View.OnClickListener {


    EditText editName;
    TextView currentPlayer;
    Uri photoURI;
    Player player;
    ImageView photo;  //ImageView para colocar la fotografía de perfil


    //variables para el AlertDialog
    Button gallery, camera, delete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustments);

        //the needed views
        currentPlayer = findViewById(R.id.currentPlayer);
        editName = findViewById(R.id.yourName);
        photo = findViewById(R.id.yourPhoto);

        //buttons
        findViewById(R.id.uploadImage).setOnClickListener(this);
        findViewById(R.id.uploadGallery).setOnClickListener(this);
        findViewById(R.id.deleteImage).setOnClickListener(this);
        findViewById(R.id.saveSettings).setOnClickListener(this);
        findViewById(R.id.unregister).setOnClickListener(this);

    }


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
            if (ok) Log.d("flx", "Overwriting image");
        } catch (IOException e) {
            Log.e("flx", "Error creating image file " + photo);
            return;
        }
        Log.d("flx", "Writing photo to " + photo);
        // Pass the photo path to the Intent and start it
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoURI = FileProvider.getUriForFile(this, "com.stucom.flx.fileProvider", photo);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, AVATAR_FROM_CAMERA);
        }
        catch (IllegalArgumentException e) {
            Log.e("flx", e.getMessage());
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
        player.setAvatar(avatar);
        player.saveToPrefs(this);
    }

    public void saveSettings() {

    }

    public void deleteAccount() {

    }



}








