package com.stucom.franmorenoalc.bonk.game.characters;

import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;

public class Firework3 extends GameObject {

    public Firework3(Game game, int x, int y){
        super(game,x,y);
        this.addTag("f3");
        this.addSpriteSequence(0,19);
    }

    @Override public void updateCollisionRect() {

    }
}
