package com.stucom.franmorenoalc.bonk.game.characters;

import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.engine.SpriteSequence;

public class Firework1 extends GameObject {

    public Firework1(Game game, int x, int y){
        super(game,x,y);
        this.addTag("f1");
        this.addSpriteSequence(0,17);
    }




    @Override public void updateCollisionRect() {

    }


}
