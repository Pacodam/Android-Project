package com.stucom.franmorenoalc.bonk.game;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.stucom.franmorenoalc.R;
import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameEngine;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.engine.OnContactListener;
import com.stucom.franmorenoalc.bonk.engine.TiledScene;
import com.stucom.franmorenoalc.bonk.engine.Touch;
import com.stucom.franmorenoalc.bonk.game.characters.Bonk;
import com.stucom.franmorenoalc.bonk.game.characters.Boss;
import com.stucom.franmorenoalc.bonk.game.characters.Coin;
import com.stucom.franmorenoalc.bonk.game.characters.Crab;
import com.stucom.franmorenoalc.bonk.game.characters.Door;
import com.stucom.franmorenoalc.bonk.game.characters.Speed;

import java.util.Locale;


// A fully playable tiled scene
class Scene01 extends TiledScene implements OnContactListener {
    // We keep a specific reference to the player
    private Bonk bonk;
    // Used for specific painting
    private Paint paintKeySymbol, paintKeyBackground, paintScore, paintPause, paintCircle;


    //door coordinates to make appear when coins are recollected
    private Door door;
    // Constructor
    Scene01(Game game) {
        super(game);
        // Load the bitmap set for this game
        GameEngine gameEngine = game.getGameEngine();
        gameEngine.loadBitmapSet(R.raw.spritesdef, R.raw.sprites_info, R.raw.sprites_seq);

        // Create the main character (player)
        bonk = new Bonk(game, 0, 0);
        this.add(bonk);
        door = new Door(game,1072,384 );
        this.add(door);
        // Set the follow camera to the player
        this.setCamera(bonk);
        // The screen will hold 16 rows of tiles (16px height each)
        this.setScaledHeight(16 * 16);
        // Pre-loading of sound effects
        game.getAudio().loadSoundFX(new int[]{ R.raw.coin, R.raw.die, R.raw.pause, R.raw.boycry, R.raw.door_open } );
        // Load the scene tiles from resource
        this.loadFromFile(R.raw.mini);
        // Add contact listeners by tag names
        this.addContactListener("bonk", "enemy", this);
        this.addContactListener("bonk", "coin", this);
        this.addContactListener("bonk","door",this);
        this.addContactListener("bonk","speed",this);
        this.addContactListener("bonk","boss",this);
        // Prepare the painters for drawing
        paintKeyBackground = new Paint();
        paintKeyBackground.setColor(Color.argb(20, 0, 0, 0));
        paintKeySymbol = new Paint();
        paintKeySymbol.setColor(Color.GRAY);
        paintKeySymbol.setTextSize(10);
        paintScore = new Paint(paintKeySymbol);
        //Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.dseg);
        //paintScore.setTypeface(typeface);
        paintScore.setColor(Color.BLUE);
        paintCircle = new Paint();
        paintCircle.setColor(Color.argb(40, 0, 0, 0));
        paintPause = new Paint(paintCircle);
        paintPause.setColor(Color.WHITE);
        paintPause.setTextSize(35);

        bonk.setCoins(10);
    }


    // Overrides the base parser adding specific syntax for coins and crabs
    @Override
    protected GameObject parseLine(String cmd, String args) {
        // Lines beginning with "COIN"
        if (cmd.equals("COIN")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Coin(game, coinX, coinY);
        }
        // Lines beginning with "CRAB"
        if (cmd.equals("CRAB")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 3) return null;
            int crabX0 = Integer.parseInt(parts2[0].trim()) * 16;
            int crabX1 = Integer.parseInt(parts2[1].trim()) * 16;
            int crabY = Integer.parseInt(parts2[2].trim()) * 16;
            return new Crab(game, crabX0, crabX1, crabY);
        }
        if(cmd.equals("DOOR")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            //Door door = new Door(game, doorX, doorY);
            //return new Door(game, doorX, doorY);
        }
        if(cmd.equals("MUSHROOM")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int doorX = Integer.parseInt(parts2[0].trim()) * 16;
            int doorY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Speed(game, doorX, doorY);
        }
        if (cmd.equals("BOSS")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int bossx = Integer.parseInt(parts2[0].trim()) * 16;
            int bossy = Integer.parseInt(parts2[1].trim()) * 16;
            return new Boss(game, bossx, bossy, bonk);
        }

        // Test the common basic parser
        return super.parseLine(cmd, args);
    }

    // User input processing
    @Override
    public void processInput() {
        // Iterate over all the queued touch events
        Touch touch;
        while ((touch = game.getGameEngine().consumeTouch()) != null) {
            // Convert the X,Y to percentages of screen
            int x = touch.getX() * 100 / getScreenWidth();
            int y = touch.getY() * 100 / getScreenHeight();
            // Bottom-left corner (left-right)
            if ((y > 75) && (x < 40)) {
                if (!touch.isTouching()) bonk.stopLR();     // STOP
                else if (x < 20) bonk.goLeft();             // LEFT
                else bonk.goRight();                        // RIGHT
            }
            // Bottom-right corner (jump)
            else if ((y > 75) && (x > 80) ) {               // JUMP
                if (touch.isDown()) bonk.jump();
            }
            // Rest of screen (pause)
            else if (touch.isDown()) {                      // TOGGLE PAUSE
                if (game.isPaused()) game.resume();
                else game.pause();
            }
        }

        // Process the computer's keyboard if the game is run inside an emulator
        int keycode;
        while ((keycode = game.getGameEngine().consumeKeyTouch()) != KeyEvent.KEYCODE_UNKNOWN) {
            switch (keycode) {
                case KeyEvent.KEYCODE_Z:                    // LEFT
                    bonk.goLeft();
                    break;
                case KeyEvent.KEYCODE_X:                    // RIGHT
                    bonk.goRight();
                    break;
                case KeyEvent.KEYCODE_M:                    // JUMP
                    bonk.jump();
                    break;
                case KeyEvent.KEYCODE_P:                    // TOGGLE PAUSE
                    if (game.isPaused()) game.resume();
                    else game.pause();
                    break;
            }
        }
    }

    /*
    Contact detection listener: A contact has been detected and must be processed
    The object1 (based on tag1) overlapped with object2 (based on tag2)
    */
    @Override
    public void onContact(String tag1, GameObject object1, String tag2, GameObject object2) {
        Log.d("flx", "Contact between a " + tag1 + " and " + tag2);
        // Contact between Bonk and a coin
        if (tag2.equals("coin")) {
            this.getGame().getAudio().playSoundFX(0);
            object2.removeFromScene();
            bonk.addScore(10);
            bonk.quitCoin();
            if(bonk.getLeftCoins() == 0){
                //Door door = new Door(game, doorX, doorY);
                this.getGame().getAudio().playSoundFX(4);
                door.isOpened();
            }

        }
        // Contact between Bonk and an enemy
        else if (tag2.equals("enemy")) {
            if(bonk.getLives() > 0){
                this.getGame().getAudio().playSoundFX(3);
                bonk.touched();
                object2.removeFromScene();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bonk.quitLives();
                        bonk.reset(0,0);
                    }
                }, 2000);
            }
            else {
                this.getGame().getAudio().playSoundFX(1);
                object2.removeFromScene();
                bonk.die();
                getGameEngine().gameOverDialog(bonk.getScore());
            }
        }
        else if (tag2.equals("boss")) {
            if(bonk.getLives() > 0){
                this.getGame().getAudio().playSoundFX(3);
                bonk.touched();
                object2.removeFromScene();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bonk.quitLives();
                        bonk.reset(0,0);
                    }
                }, 2000);
            }
            else {
                this.getGame().getAudio().playSoundFX(1);
                object2.removeFromScene();
                bonk.die();
                getGameEngine().gameOverDialog(bonk.getScore());
            }
        }

        //contact between Bonk and door == next level!
        else if (tag2.equals("door")) {
            //this.getGame().getAudio().playSoundFX(1);
            //GameActivity gm = (GameActivity) getContext();
            if(door.getState() == 1) {
                game.stopMusic();
                game.loadMusic(R.raw.papaya);
                game.loadScene(new Scene02(game, bonk));

            }else{
                getGameEngine().alertLeftCoins(bonk.getLeftCoins());
            }

        }
        //contact between Bonk and mushroom == speed up and jump up
        else if (tag2.equals("speed")) {
            object2.removeFromScene();
            bonk.setJump(-30);
            bonk.setRemainingJump(10);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bonk.setJump(-11);
                }
            }, 20000);
        }
    }





    // Overrides the basic draw by adding the translucent keyboard and the score
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Translucent keyboard on top
        canvas.save();
        canvas.scale(getScale() * getScaledWidth() / 100, getScale() * getScaledHeight() / 100);

        canvas.drawRect(1, 76, 19, 99, paintKeyBackground);
        canvas.drawText("«", 8, 92, paintKeySymbol);
        canvas.drawRect(21, 76, 39, 99, paintKeyBackground);
        canvas.drawText("»", 28, 92, paintKeySymbol);
        canvas.drawRect(81, 76, 99, 99, paintKeyBackground);
        canvas.drawText("^", 88, 92, paintKeySymbol);
        canvas.restore();



        // Score on top-right corner
        canvas.scale(getScale(), getScale());
        paintScore.setTextSize(10);

        canvas.drawText("<< EXIT",  10, 10, paintScore);

        String score = String.format(Locale.getDefault(), "%06d", bonk.getScore());
        canvas.drawText(score, getScaledWidth() - 50, 10, paintScore);
        canvas.drawText("LIVES: " + bonk.getLives(), getScaledWidth() - 50 , 20, paintScore);

        //remaining coins to get
        canvas.drawText("COINS LEFT: " + bonk.getLeftCoins(), 80,10, paintScore);


        if(game.isPaused()){
            int xx = getScaledWidth() / 2;
            int yy = (int) ((getScaledHeight() / 2) - ((paintScore.descent() + paintScore.ascent()) / 2));
            //Log.d("flx", "xx " + xx + " yy " + yy);
            canvas.drawCircle(xx, yy, 50, paintCircle);
            canvas.drawText("| |", xx - 10,yy, paintPause);
            //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        }

        if(bonk.getJumpVelocity() > -11) {
            canvas.drawText(String.valueOf(bonk.getRemainingJump()), 80, 20, paintScore);
        }

    }




}
