package com.stucom.franmorenoalc.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Player {

    //attributes created with registry
    private String token;
    private String email;

    //player attributes
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("from")
    private String from;
    @SerializedName("totalScore")
    private int totalScore;
    @SerializedName("lastLevel")
    private int lastLevel;
    @SerializedName("lastScore")
    private int lastScore;
    //@SerializedName("scores")
   // private List<Score> scores;

    public Player() {
    }

    public Player(String token, String email, int id, String name, String image, String from, int totalScore, int lastLevel, int lastScore) {
        this.token = token;
        this.email = email;
        this.id = id;
        this.name = name;
        this.image = image;
        this.from = from;
        this.totalScore = totalScore;
        this.lastLevel = lastLevel;
        this.lastScore = lastScore;
        //this.scores = scores;
    }

    //getters
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getFrom() { return from; }
    public int getTotalScore() { return totalScore; }
    public int getLastLevel() { return lastLevel; }
    public int getLastScore() { return lastScore; }
    //public List<Score> getScores() { return scores; }

    //setters
    public void setToken(String token) { this.token = token; }
    public void setEmail(String email) { this.email = email; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }
    public void setFrom(String from) { this.from = from; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public void setLastLevel(int lastLevel) { this.lastLevel = lastLevel; }
    public void setLastScore(int lastScore) { this.lastScore = lastScore; }
    //public void setScores(List<Score> scores) { this.scores = scores; }


    public void loadFromPrefs(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        this.token = prefs.getString("playerToken", null);
        this.name = prefs.getString("playerName", "");
        this.email = prefs.getString("playerEmail", "");
        this.image = prefs.getString("playerAvatar", null);
    }

    public void saveToPrefs(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("playerToken", token);
        prefsEditor.putString("playerName", name);
        prefsEditor.putString("playerEmail", email);
        prefsEditor.putString("playerAvatar", image);
        prefsEditor.apply();
    }
}
