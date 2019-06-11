package com.stucom.franmorenoalc.bonk.game.characters;

import android.graphics.Rect;

import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.engine.Scene;
import com.stucom.franmorenoalc.bonk.engine.SpriteSequence;
import com.stucom.franmorenoalc.bonk.engine.TiledScene;


// The main character (player)
@SuppressWarnings("unused")
public class Bonk extends GameObject {
    // Bonk specific attributes
    private int vx;         // vel-X is 1 or 2 (boosted velocity)
    private int vy;
    private boolean isJumping;
    private int score;
    private int lives;
    private int coinsLeft;

    // Useful constants
    private static  int MAX_VELOCITY = 8;
    private static  int JUMP_VELOCITY = -11;
    private static final int PAD_LEFT = 2;
    private static final int PAD_TOP = 0;
    private static final int COL_WIDTH = 20;
    private static final int COL_HEIGHT = 32;

    private final static int STATE_STANDING_FRONT = 0;
    private final static int STATE_WALKING_LEFT = 1;
    private final static int STATE_WALKING_RIGHT = 2;
    private final static int STATE_DEAD = 3;
    private final static int STATE_JUMPING_LEFT = 4;
    private final static int STATE_JUMPING_RIGHT = 5;
    private final static int STATE_FALLING_LEFT = 6;
    private final static int STATE_FALLING_RIGHT = 7;
    private final static int STATE_JUMPING_FRONT = 8;
    private final static int STATE_TOUCHED = 9;
    private final static int STATE_SUPERBONK = 10;



    // State change matrix depending on movement direction
    private static final int[] NEW_STATES = {
        STATE_JUMPING_LEFT, STATE_JUMPING_FRONT, STATE_JUMPING_RIGHT,
        STATE_WALKING_LEFT, STATE_STANDING_FRONT, STATE_WALKING_RIGHT,
        STATE_FALLING_LEFT, STATE_JUMPING_FRONT, STATE_FALLING_RIGHT, STATE_TOUCHED, STATE_SUPERBONK
    };

    // Constructor
    public Bonk(Game game, int x, int y) {
        super(game, x, y);
        this.reset(x, y);
        this.addTag("bonk");
        this.lives = 3;
        for (int i = 0; i < 9; i++) {
            this.addSpriteSequence(i, i); // The first 0-8 states are indexed animations 0-8
        }
        this.addSpriteSequence(9,12);
    }

    //Lifes Remaining
    public int getLives(){ return lives;}

    //Quit live
    public void quitLives(){ this.lives = this.lives - 1;}

    // Score related
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void addScore(int score) { this.score += score; }

    public void setCoins(int coins){ this.coinsLeft = coins;}
    public void quitCoin(){ this.coinsLeft--;}
    public int getLeftCoins(){ return coinsLeft;}

    // Reset Bonk to a known position
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.vx = 2;
    }

    // Change state method
    private void changeState(int state) {
        if (this.state == state) return;
        this.state = state;
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.reset();
    }

    // Dying is exactly state 3
    public boolean isDead() { return (state == STATE_DEAD); }

    // And kill him is exactly change its state to 3
    public void die() {
        changeState(STATE_DEAD);
    }

    // touched by enemy, loss of life, state 9
    public void touched(){
        changeState(STATE_TOUCHED);
    }

    // User input helper methods
    private boolean left, right, jump;
    public void goLeft() { left = true; right = false; }
    public void goRight() { left = false; right = true; }
    public void stopLR() { left = right = false; }
    public void jump() { jump = true; }
    private void clearJump() { jump = false; }
    private boolean isLeft() { return left; }
    private boolean isRight() { return right; }
    private boolean isJump() { return jump; }

    @Override public void physics(long deltaTime) {
        // If died, no physics
        if (state == STATE_DEAD) return;

        // Analyze user input
        int vx = 0;
        if (this.isLeft()) vx = -this.vx;
        else if (this.isRight()) vx = this.vx;
        if (this.isJump()) {
            if (!isJumping) {       // Avoid double jumps
                vy = JUMP_VELOCITY;
                isJumping = true;
            }
            this.clearJump();
        }

        // Apply physics and tests to scene walls and grounds (only if it's in a TiledScene scene)
        Scene scene = game.getScene();
        if (!(scene instanceof TiledScene)) return;
        TiledScene tiledScene = (TiledScene) scene;
        // 1) detect wall to right
        int newX = x + vx;
        int newY = y;
        if (vx > 0) {
            int col = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int r1 = (newY + PAD_TOP) / 16;
            int r2 = (newY + PAD_TOP + COL_HEIGHT - 1) / 16;
            for (int row = r1; row <= r2; row++) {
                if (tiledScene.isWall(row, col)) {
                    newX = col * 16 - PAD_LEFT - COL_WIDTH - 1;
                    break;
                }
            }
        }
        // 2) detect wall to left
        if (vx < 0) {
            int col = (newX + PAD_LEFT) / 16;
            int r1 = (newY + PAD_TOP) / 16;
            int r2 = (newY + PAD_TOP + COL_HEIGHT - 1) / 16;
            for (int row = r1; row <= r2; row++) {
                if (tiledScene.isWall(row, col)) {
                    newX = (col + 1) * 16 - PAD_LEFT;
                    break;
                }
            }
        }
        // 3) detect ground
        // physics (try fall and detect ground)
        vy++; if (vy > MAX_VELOCITY) vy = MAX_VELOCITY;
        newY = y + vy;
        if (vy >= 0) {
            int c1 = (newX + PAD_LEFT) / 16;
            int c2 = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int row = (newY + PAD_TOP + COL_HEIGHT) / 16;
            for (int col = c1; col <= c2; col++) {
                if (tiledScene.isGround(row, col)) {
                    newY = row * 16 - PAD_TOP - COL_HEIGHT;
                    vy = 0;
                    isJumping = false;
                    break;
                }
            }
        }
        // 4) detect ceiling
        if (vy < 0) {
            int c1 = (newX + PAD_LEFT) / 16;
            int c2 = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int row = (newY + PAD_TOP) / 16;
            for (int col = c1; col <= c2; col++) {
                if (tiledScene.isWall(row, col)) {
                    newY = (row + 1) * 16 - PAD_TOP;
                    vy = 0;
                    break;
                }
            }
        }

        // Apply resulting physics
        x = newX;
        y = newY;

        // Apply screen limits
        x = Math.max(x, -PAD_LEFT);
        x = Math.min(x, tiledScene.getSceneFullWidth() - COL_WIDTH);
        y = Math.min(y, tiledScene.getSceneFullHeight() - COL_HEIGHT);

        // Decide the out state
        int c = (vx < 0) ? 0 : ((vx == 0) ? 1 : 2);
        int r = (vy < 0) ? 0 : ((vy == 0) ? 1 : 2);
        changeState(NEW_STATES[r * 3 + c]);
    }

    // The collision rect is only valid while alive
    @Override public Rect getCollisionRect() {
        return (state == STATE_DEAD) ? null : collisionRect;
    }

    // Updates the collision rect around the character
    @Override public void updateCollisionRect() {
        collisionRect.set(
                x + PAD_LEFT,
                y + PAD_TOP,
                x + PAD_LEFT + COL_WIDTH,
                y + PAD_TOP + COL_HEIGHT
        );
    }

    public void superBonk(){
        this.MAX_VELOCITY = 15;
        this.JUMP_VELOCITY = -30;

    }
}
