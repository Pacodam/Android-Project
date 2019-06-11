package com.stucom.franmorenoalc.bonk.game.characters;

import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.engine.SpriteSequence;

public class Door extends GameObject {

    //constants
    private final static int DOOR_CLOSED = 0;
    private final static int DOOR_OPENED = 1;
    private static final int[] NEW_STATES = { DOOR_CLOSED,DOOR_OPENED};

    public Door(Game game, int x, int y){
        super(game,x,y);
        this.addTag("door");
        this.addSpriteSequence(0,14);
        this.addSpriteSequence(1,15);
    }

    public void changeState(int state){
        if (this.state == state) return;
        this.state = state;
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.reset();
    }

    public void isOpened(){ changeState(DOOR_OPENED);}
    public int getState() { return state;}

    /*
    public Door(Game game, int x, int y){
        super(game, x, y);
        this.addTag("door");
        this.addSpriteSequence(0, 14);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();

    } */

    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 30, y + 30);
    }



}
