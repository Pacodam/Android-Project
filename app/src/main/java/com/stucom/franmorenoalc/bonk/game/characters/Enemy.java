package com.stucom.franmorenoalc.bonk.game.characters;


import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;

// This class only serves for tagging as "enemy" a collection of GameObjects
abstract public class Enemy extends GameObject {

    // Constructor
    public Enemy(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("enemy");
    }

}
