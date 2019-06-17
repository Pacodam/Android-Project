package com.stucom.franmorenoalc.bonk.game.characters;

import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.engine.SpriteSequence;

public class Boss extends GameObject {

    // Boss specific attributes
    private int x0, y, incX;
    private Bonk bonk;

    public Boss(Game game, int x0, int y, Bonk bonk) {
        super(game, x0, y );
        this.x0 = x0;
        this.y = y;
        this.incX = 1;
        this.bonk = bonk;
        this.addTag("boss");
        this.addSpriteSequence(0, 20);
    }


    // The crab moves horizontally between x0 and x1
    @Override public void physics(long deltaTime) {
        /*
        this.x += incX;
        if (x < bonk) x++;
        if (x >= bonkX) x--; */
        this.x += incX;
        if (x < bonk.getX()) incX = 1;
        if (x >= bonk.getX()) incX = -1;

    }

    /*// Constructor
    public Boss(Game game, int x0, int x1, int y) {
        super(game, x0, y - 5);
        this.x0 = x0;
        this.x1 = x1;
        this.incX = 1;
        this.addTag("boss");
        this.addSpriteSequence(0, 20);
    }*/


   /* // The collision rect around the crab will consider the pincers' position
    @Override public void updateCollisionRect() {
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        int currentSpriteIndex = spriteSequence.getCurrentSpriteIndex();
        int top = y + 8 - ((currentSpriteIndex < 6) ? 8 : 0);
        int bottom = y + 22 + ((currentSpriteIndex >= 6) ? 8 : 0);
        collisionRect.set(x, top, x + 32, bottom);
    }*/
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 44, y + 48 );
    }


}
