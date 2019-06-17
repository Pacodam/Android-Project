package com.stucom.franmorenoalc.bonk.game;

import com.stucom.franmorenoalc.R;
import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameEngine;


// This game is a Game instance
public class BonkGame extends Game {



    // Constructor
    BonkGame(GameEngine gameEngine) {
        super(gameEngine);
    }

    // Method to be called when the game is first started
    @Override
    public void start() {
        // When the game is loaded, the Scene01 is presented to the user
        Scene01 scene = new Scene01(this);
        this.loadScene(scene);
        // Background music
        this.loadMusic(R.raw.papaya2);
    }


    // Method to be called when the game is being closed
    @Override
    public void stop() {
        // Nothing special for now
    }

    // Method to be called when the game returns from pause
    @Override
    public void resume() {
        super.resume();
        getAudio().startMusic();
    }

    // Method to be called when the game goes to pause
    @Override
    public void pause() {
        super.pause();
        getAudio().stopMusic();
    }



}
