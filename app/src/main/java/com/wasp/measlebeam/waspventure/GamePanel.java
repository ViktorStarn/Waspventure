/*
 * Copyright (c) 2015. Viktor Stärn
 *
 * This file is part of Waspventure.
 *
 * Waspventure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Waspventure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Waspventure.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Graphics licensed under the terms of the version 3.0 of the Creative
 * Commons Attribution-Share Alike license. © 2005-2013 Julien Jorge
 * <julien.jorge@stuff-o-matic.com>, music by Kaetemi, sound effects
 * by Dan Knoflicek. Downloaded from <http://opengameart.org>.
 */

package com.wasp.measlebeam.waspventure;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.Random;

/**
 * Handles the Surface View onto which the canvas is drawn.
 *
 * Created by Viktor Stärn on 2015-08-29.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private long enemyBirdStartTime;
    private long butterflyStartTime;
    private long startReset;
    private int highScore = 0;
    private int soundEnemyBirdCollision;                        //container for collision sound effect
    private int soundButterflyCollision;
    public static final int WIDTH = 856;                        //dimensions of back- and foreground graphical resources
    public static final int HEIGHT = 480;
    private boolean gameStarted;
    private boolean newGameCreated;
    private boolean reset;
    private boolean playerDisappear;
    private boolean scoreAscending;
    private boolean isPaused;
    private ArrayList<EnemyBird> enemyBirds;                    //for storing enemybird-objects
    private ArrayList<Butterfly> butterflies;                   //for storing butterfly-objects
    private ArrayList<AscendingScore> ascendingScores;          //for storing score notifications
    private Game game;
    private MainThread thread;
    private DatabaseThread dbThread;
    private Background bg;
    private Foreground fg;
    private Player player;
    private Collision collision;
    private ScrollingCredits scrollingCredits;
    private SoundPool sounds;                                   //handles game sound effects
    private Random random = new Random();

    /**
     * 3.	Constructs GamePanel, takes Game class context as parameter. 4 -> MainThread.
     *
     *      @param context Context
     */
    public GamePanel (Context context)
    {
        super(context);
        getHolder().addCallback(this);                  //add the callback to the surfaceholder to intercept events
        setFocusable(true);                             //make focusable

        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);                   //create soundpool object
        soundEnemyBirdCollision = sounds.load(context, R.raw.collision, 1);         //prepare/load collision sound effect
        soundButterflyCollision = sounds.load(context, R.raw.powerup, 1);           //prepare/load powerup sound effect

        String credits = "Graphics licensed under the terms of the version 3.0 of the Creative Commons Attribution-Share Alike license. © 2005-2013 Julien Jorge <julien.jorge@stuff-o-matic.com>, music by Kaetemi, sound effects by Dan Knoflicek. Downloaded from http://opengameart.org.";
        scrollingCredits = new ScrollingCredits(credits);

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.forestfar));                //Initialize background object
        fg = new Foreground(BitmapFactory.decodeResource(getResources(), R.drawable.forestnear));               //Initialize foreground object
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.waspflying), 49, 39, 4);    //Initialize player object
        enemyBirds = new ArrayList<>();                 //Initialize enemy bird array list
        butterflies = new ArrayList<>();                //Initialize butterfly array list
        ascendingScores = new ArrayList<>();            //Initialize ascending score notifications array list
        enemyBirdStartTime = System.nanoTime();         //Start "timer" for keeping track of when to deploy new enemy birds
        butterflyStartTime = System.nanoTime();         //Start "timer" for keeping track of when to deploy new enemy birds
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    /**
     * Try to stop the UI- and database threads when surface is destroyed.
     *
     * @param holder Surface Holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        while(retry){
            try {
                thread.setRunning(false);
                thread.join();                                              //wait for thread to die
            } catch (InterruptedException e){ e.printStackTrace(); }
            retry = false;
            thread = null;                                                  //prepare for garbage collection
        }

        retry = true;
        while(retry){
            try {
                dbThread.join();                                            //wait for thread to die
            } catch (InterruptedException e){ e.printStackTrace(); }
            retry = false;
            dbThread = null;                                                //prepare for garbage collection
        }

    }

    /**
     * Starts UI- and database threads. Creates UI thread using the Surface View Surface Holder
     * (used for setting the properties of the Surface View) and Surface View as parameters when
     * instantiating MainThread class.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if(dbThread==null){
            dbThread = new DatabaseThread(getContext());    //instantiate database thread
            dbThread.start();
        }
        else{
            dbThread.run();
        }

        thread = new MainThread(getHolder(), this);         //instantiate UI-thread
        thread.setRunning(true);
        thread.start();

        if(!isPaused && !game.getMusic()){                  //start playing game music if game is not paused and game music is not already playing
            game.setMusic(true);
        }

        if(highScore==0||highScore<dbThread.getValue()){    //ask database for stored highscore if local highscore is 0 or less than that of the databaseThread class
            thread.threadSleep(50);
            highScore = dbThread.getValue();
        }
    }

    /**
     * Handles user input (touch events). First touch starts or resumes the game. Subsequent
     * touches will cause the wasp to ascend, while releasing the touch will cause the wasp to
     * descend.
     *
     * @param event Touch event
     * @return true or super.onTouchEvent(event)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN)  //if screen is touched
        {
            if(!player.getPlaying() && newGameCreated && reset)
            {
                player.setPlaying(true);
            }
            if(player.getPlaying())
            {
                if(!gameStarted)gameStarted = true;
                if(reset)reset = false;
                player.setUp(true);                     //tell player object that screen has been touched
            }
            if(isPaused){                               //resumes game if screen is touched during pause
                isPaused=false;                         //clears pause-flag
                game.setMusic(true);                    //resumes music playback
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Updates game objects.
     */
    public void update()
    {
        if(!isPaused){                              //if not paused go ahead and update game objects
            bg.update();
            fg.update();
            player.update();
            if(player.getPlaying()) {               //if player is playing update enemy birds, butterflies, score notifications and collisions
                long enemyBirdsElapsed = (System.nanoTime() - enemyBirdStartTime)/1000000;
                if (enemyBirdsElapsed>(4000 - player.getScore()*3)) {           //decide if it's time to add another enemy bird based on time elapsed and player score
                    if (!gameStarted) {                                         //if game is not started, add first enemy bird
                        enemyBirds.add(new EnemyBird(BitmapFactory.decodeResource(getResources(), R.drawable.enemybird1), BitmapFactory.decodeResource(getResources(), R.drawable.enemybird2), WIDTH + 10, HEIGHT/2, 107, 81, player.getScore(), 4, 4));
                        gameStarted = true;
                    }
                    else if (enemyBirds.size()<5){                              //if game is started and there are less than 5 enemy birds already, add enemy bird
                        enemyBirds.add(new EnemyBird(BitmapFactory.decodeResource(getResources(), R.drawable.enemybird1), BitmapFactory.decodeResource(getResources(), R.drawable.enemybird2), WIDTH + 10, (int)(random.nextDouble()*HEIGHT), 107, 81, player.getScore(), 4, 4));
                    }
                    enemyBirdStartTime = System.nanoTime();                     //reset enemy bird "timer"
                }

                for(int i = 0; i<enemyBirds.size(); i++) {                      //check if player has collided with any of the enemy birds
                    enemyBirds.get(i).update();

                    if(collision(enemyBirds.get(i), player)) {
                        enemyBirds.remove(i);
                        player.setPlaying(false);
                        break;
                    }
                    if(enemyBirds.get(i).getX()<-100) {                         //remove off map enemy birds
                        enemyBirds.remove(i);
                        break;
                    }
                }

                long butterfliesElapsed = (System.nanoTime() - butterflyStartTime)/1000000;
                if (butterfliesElapsed>(2000 + random.nextInt(8000))) {         //decide if it's time to add another butterfly based on time elapsed
                    if (gameStarted) {                                          //if game is started, add one of the 5 different types of butterflies at random
                        int butterflyColor = random.nextInt(5);
                        switch (butterflyColor) {
                            case 0:
                                butterflies.add(new Butterfly(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_blue_left), BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_blue_right), WIDTH + 10, (int) (random.nextDouble() * HEIGHT), 30, 25,  24, 24));
                                break;
                            case 1:
                                butterflies.add(new Butterfly(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_yellow_left), BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_yellow_right), WIDTH + 10, (int) (random.nextDouble() * HEIGHT), 30, 25,  24, 24));
                                break;
                            case 2:
                                butterflies.add(new Butterfly(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_cyan_left), BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_cyan_right), WIDTH + 10, (int) (random.nextDouble() * HEIGHT), 30, 25,  24, 24));
                                break;
                            case 3:
                                butterflies.add(new Butterfly(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_gray_left), BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_gray_right), WIDTH + 10, (int) (random.nextDouble() * HEIGHT), 30, 25,  24, 24));
                                break;
                            case 4:
                                butterflies.add(new Butterfly(BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_orange_left), BitmapFactory.decodeResource(getResources(), R.drawable.butterfly_orange_right), WIDTH + 10, (int) (random.nextDouble() * HEIGHT), 30, 25,  24, 24));
                                break;
                        }

                    }
                    butterflyStartTime = System.nanoTime();                     //reset butterfly "timer"
                }

                for(int i = 0; i<butterflies.size(); i++) {                     //check if player has "collided" with any of the butterflies
                    butterflies.get(i).update();

                    if(collision(butterflies.get(i), player)) {
                        butterflies.remove(i);
                        collision = new Collision(BitmapFactory.decodeResource(getResources(), R.drawable.sparkle), player.getX(), player.getY(), 32, 32, 4, 16);
                        sounds.play(soundButterflyCollision, 1.0f, 1.0f, 0, 0, 1.5f);           //play collision sound effect
                        player.raiseScore(50);
                        scoreAscending = true;
                        ascendingScores.add(new AscendingScore(player, "500"));
                        break;
                    }
                    if(butterflies.get(i).getX()<-100) {                        //remove off map butterflies
                        butterflies.remove(i);
                        break;
                    }
                }
                collision.update();

                if(scoreAscending) {                                            //update ascending score notifications if scoreAscending is set
                    for(int i = 0;i<ascendingScores.size();i++) {
                        ascendingScores.get(i).update();
                        if(ascendingScores.get(i).getY()<-HEIGHT) {
                            ascendingScores.remove(i);
                        }
                        if(ascendingScores.size()==0) {
                            scoreAscending = false;
                        }
                    }

                }


            }
            else {                                                                              //if player is not playing update collision and scrolling credits
                if(!reset && !isPaused) {                                                       //if player is not playing because he/she hasn't started playing or was playing but has collided
                    newGameCreated = false;
                    startReset = System.nanoTime();
                    reset = true;
                    playerDisappear = true;
                    collision = new Collision(BitmapFactory.decodeResource(getResources(), R.drawable.airblast), player.getX()-60, player.getY()-60, 201, 201, 5, 10);
                    if(gameStarted) {                                                           //if player was playing but has collided
                        sounds.play(soundEnemyBirdCollision, 1.0f, 1.0f, 0, 0, 1.5f);           //play collision sound effect
                    }
                }
                collision.update();
                scrollingCredits.update();

                long resetElapsed = (System.nanoTime() - startReset)/1000000;
                if (resetElapsed > 2500 && !newGameCreated) {                                   //create new game if this hasn't been done already
                    newGame();
                }
            }
        }
    }

    /**
     * Takes 2 game objects and calculates whether their rectangles intersect, i.e. whether they
     * have collided.
     */
    public boolean collision(GameObject a, GameObject b)
    {
        return(Rect.intersects(a.getRectangle(), b.getRectangle()));
    }

    /**
     * Draws game objects.
     *
     * @param canvas Canvas
     */
    @Override
    public void draw(Canvas canvas)
    {
        if(canvas!=null)
        {
            super.draw(canvas);
        }
        final float ScaleFactorX = getWidth()/(WIDTH*1.f);      //determine scale factors
        final float ScaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas!=null)
        {
            final int savedState = canvas.save();               //save canvas state before scaling
            canvas.scale(ScaleFactorX, ScaleFactorY);           //scale according to screen size
            bg.draw(canvas);                                    //draw background object
            fg.draw(canvas);                                    //draw foreground object
            if(!playerDisappear){
                player.draw(canvas);                            //draw player
            }
            for(EnemyBird e: enemyBirds) {                      //draw enemy birds
                e.draw(canvas);
            }
            for(Butterfly b: butterflies) {                     //draw butterflies
                b.draw(canvas);
            }
            if (gameStarted) {
                collision.draw(canvas);                         //draw collision
                for(int i = 0;i<ascendingScores.size();i++) {
                    ascendingScores.get(i).drawText(canvas);    //draw score notifications
                }
            }
            drawText(canvas);                                   //draw text
            canvas.restoreToCount(savedState);                  //restore saved canvas (to avoid scaling an already scaled canvas during subsequent calls to draw()
        }
    }

    /**
     * Resets the game.
     */
    public void newGame()
    {
        playerDisappear = false;                                //show player
        player.resetDYA();
        player.resetDY();
        enemyBirds.clear();
        butterflies.clear();
        ascendingScores.clear();
        player.setY(HEIGHT / 2);                                //reset player position

        if(highScore==0&&player.getScore()>0) {
            highScore = player.getScore();
            dbThread.updateDatabase(highScore);
            dbThread.run();
        }
        else if(player.getScore()>highScore){
            highScore = player.getScore();
            dbThread.updateDatabase(highScore);
            dbThread.run();
        }

        player.resetScore();

        newGameCreated = true;
    }

    /**
     * Draws text.
     *
     * @param canvas Canvas
     */
    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("SCORE: " + (player.getScore() * 10), 10, HEIGHT - 10, paint);
        canvas.drawText("HIGH SCORE: " + highScore * 10, WIDTH - 315, HEIGHT - 10, paint);

        if(!player.getPlaying()&&newGameCreated&&reset) {           //draw welcome screen text if player is not yet playing
            Paint paint1 = new Paint();
            paint1.setColor(Color.BLACK);
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH / 2 - 50, HEIGHT / 2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH / 2 - 50, HEIGHT / 2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH / 2 - 50, HEIGHT/2 + 40, paint1);

            paint1.setColor(Color.YELLOW);
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH / 2 - 52, HEIGHT / 2 - 2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH / 2 - 52, HEIGHT / 2 + 18, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH / 2 - 52, HEIGHT/2 + 38, paint1);

            scrollingCredits.drawText(canvas);                      //draw scrolling credits if player is not yet playing
        }

        if(isPaused&&player.getPlaying()) {                         //draw pause screen text if game has lost focus
            Paint paint2 = new Paint();
            paint2.setColor(Color.BLACK);
            paint2.setTextSize(40);
            paint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PAUSED", WIDTH / 2 - 20, HEIGHT / 2, paint2);

            paint2.setTextSize(20);
            canvas.drawText("PRESS TO CONTINUE", WIDTH / 2 - 20, HEIGHT / 2 + 20, paint2);

            paint2.setColor(Color.YELLOW);
            paint2.setTextSize(40);
            paint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PAUSED", WIDTH / 2 - 22, HEIGHT / 2 - 2, paint2);

            paint2.setTextSize(20);
            canvas.drawText("PRESS TO CONTINUE", WIDTH / 2 - 22, HEIGHT / 2 + 18, paint2);
        }
    }

    public void setIsPaused(boolean isPaused){
        this.isPaused = isPaused;
    }

    public void setGame(Game game) {
        this.game = game;
    }


}
