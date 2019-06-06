package com.stucom.franmorenoalc.bonk.game.characters;

import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.engine.SpriteSequence;

public class Door extends GameObject {

    public Door(Game game, int x, int y){
        super(game, x, y);
        this.addTag("door");
        this.addSpriteSequence(0, 11);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
    }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 30, y + 30);
    }


}
