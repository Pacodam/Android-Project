package com.stucom.franmorenoalc.bonk.game.characters;

import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.game.GameOptionsActivity;

public class Firework2 extends GameObject {

    public Firework2(Game game, int x, int y){
        super(game,x,y);
        this.addTag("f2");
        this.addSpriteSequence(0,18);
    }

    @Override public void updateCollisionRect() {

    }
}
