package com.stucom.franmorenoalc.model;

import android.content.Context;
import android.content.SharedPreferences;

public class Player {
    private String token;
    private String name;
    private String email;
    private String avatar;

    public Player() {
    }

    public String getToken() { return token;}
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAvatar() { return avatar; }

    public void setToken(String token) {this.token = token; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public void loadFromPrefs(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        this.token = prefs.getString("playerToken", null);
        this.name = prefs.getString("playerName", "");
        this.email = prefs.getString("playerEmail", "");
        this.avatar = prefs.getString("playerAvatar", null);
    }

    public void saveToPrefs(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("playerToken", token);
        prefsEditor.putString("playerName", name);
        prefsEditor.putString("playerEmail", email);
        prefsEditor.putString("playerAvatar", avatar);
        prefsEditor.apply();
    }
}
