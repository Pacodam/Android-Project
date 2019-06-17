package com.stucom.franmorenoalc.bonk.game;

import android.content.SharedPreferences;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.Handler;
import android.view.KeyEvent;

import com.stucom.franmorenoalc.R;
import com.stucom.franmorenoalc.bonk.engine.Game;
import com.stucom.franmorenoalc.bonk.engine.GameEngine;
import com.stucom.franmorenoalc.bonk.engine.GameObject;
import com.stucom.franmorenoalc.bonk.engine.OnContactListener;
import com.stucom.franmorenoalc.bonk.engine.TiledScene;
import com.stucom.franmorenoalc.bonk.engine.Touch;
import com.stucom.franmorenoalc.bonk.game.characters.Bonk;
import com.stucom.franmorenoalc.bonk.game.characters.Door;
import com.stucom.franmorenoalc.bonk.game.characters.Firework1;
import com.stucom.franmorenoalc.bonk.game.characters.Firework2;
import com.stucom.franmorenoalc.bonk.game.characters.Firework3;
import com.stucom.franmorenoalc.bonk.game.characters.Speed;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


// A fully playable tiled scene
class Scene03 extends TiledScene implements OnContactListener {
    String token;
    static SharedPreferences prefs;
    // We keep a specific reference to the player
    private Bonk bonk;
    // Used for specific painting
    private Paint paintKeySymbol, paintKeyBackground, paintScore, paintPause, paintCircle, paintGoodbye;


    //door coordinates to make appear when coins are recollected
    private Door door;

    // Constructor
    Scene03(Game game, int score) {
        super(game);
        // Load the bitmap set for this game
        GameEngine gameEngine = game.getGameEngine();
        gameEngine.loadBitmapSet(R.raw.sprites2, R.raw.sprites_info, R.raw.sprites_seq);
        //getToken();
        // Create the main character (player)
        bonk = new Bonk(game, 0, 0);
        bonk.setScore(score);
        this.add(bonk);
        door = new Door(game,464,432 );
        this.add(door);
        door.isOpened();
        // Set the follow camera to the player
        this.setCamera(bonk);
        // The screen will hold 16 rows of tiles (16px height each)
        this.setScaledHeight(16 * 16);
        // Pre-loading of sound effects
        game.getAudio().loadSoundFX(new int[]{ R.raw.coin, R.raw.die, R.raw.pause, R.raw.boycry, R.raw.door_open } );
        // Load the scene tiles from resource
        this.loadFromFile(R.raw.mini03);
        // Add contact listeners by tag names
        this.addContactListener("bonk","door",this);
        this.addContactListener("bonk","speed",this);
        // Prepare the painters for drawing
        paintKeyBackground = new Paint();
        paintKeyBackground.setColor(Color.argb(20, 0, 0, 0));
        paintKeySymbol = new Paint();
        paintKeySymbol.setColor(Color.GRAY);
        paintKeySymbol.setTextSize(10);
        //paintScore = new Paint(paintKeySymbol);
        //Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.dseg);
        //paintScore.setTypeface(typeface);
        //paintScore.setColor(Color.WHITE);
        paintCircle = new Paint();
        paintCircle.setColor(Color.argb(40, 0, 0, 0));
        paintPause = new Paint(paintCircle);
        paintPause.setColor(Color.WHITE);
        paintPause.setTextSize(15);

        paintGoodbye = new Paint();
        paintGoodbye.setColor(Color.WHITE);
        paintGoodbye.setTextSize(30);
        paintScore = new Paint(paintGoodbye);
        paintScore.setColor(Color.WHITE);
        paintScore.setTextSize(15);




    }



    // Overrides the base parser adding specific syntax for coins and crabs
    @Override
    protected GameObject parseLine(String cmd, String args) {
        // Lines beginning with "COIN"
        if (cmd.equals("F1")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Firework1(game, coinX, coinY);
        }
        if (cmd.equals("F2")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Firework2(game, coinX, coinY);
        }
        if (cmd.equals("F3")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Firework3(game, coinX, coinY);
        }

        if(cmd.equals("MUSHROOM")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int mushroomX = Integer.parseInt(parts2[0].trim()) * 16;
            int mushroomY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Speed(game, mushroomX, mushroomY);
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

        //contact between Bonk and door == next level!
        if (tag2.equals("door")) {
            game.stopMusic();
            //bonk.setScore(300);
            //save score to shared prefs
            SharedPreferences prefs = getGameEngine().receiveContext().getSharedPreferences(getGameEngine().receiveContext().getPackageName(), MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("score", String.valueOf(bonk.getScore()));
            ed.apply();
            getGameEngine().returnMenu();
        }
        //contact between Bonk and mushroom == speed up and jump up
        else if (tag2.equals("speed")) {
            object2.removeFromScene();
            bonk.setJump(-30);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bonk.setJump(-11);
                }
            }, 10000);
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
        int xx = getScaledWidth() / 2;
        int yy = (int) ((getScaledHeight() / 2) - ((paintScore.descent() + paintScore.ascent()) / 2));
        //Log.d("flx", "xx " + xx + " yy " + yy);
        String score = String.format(Locale.getDefault(), "%06d", bonk.getScore());
        canvas.drawText("Congratulations!", xx , 20, paintScore);
        canvas.drawText("You made " + score + " points",  xx, 40, paintScore);



        if(game.isPaused()){
            xx = getScaledWidth() / 2;
            yy = (int) ((getScaledHeight() / 2) - ((paintScore.descent() + paintScore.ascent()) / 2));
            //Log.d("flx", "xx " + xx + " yy " + yy);
            canvas.drawCircle(xx, yy, 50, paintCircle);
            canvas.drawText("| |", xx - 10,yy, paintPause);
            //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        }

    }

   /* public void getToken() {
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        token = prefs.getString("token", null);
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
    }*/


/*
    public void drawPause(Canvas canvas, int x, int y) {
        src.left = 0;
        src.top = 0;
        src.right = 32;
        src.bottom = 32;
        dst.left = x;
        dst.top = y;
        dst.right = dst.left + TILE_SIZE;
        dst.bottom = dst.top + TILE_SIZE;
        canvas.drawBitmap(worm, src, dst, paint);
    } */
}
