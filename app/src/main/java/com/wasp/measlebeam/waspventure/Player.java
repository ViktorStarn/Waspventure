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

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Handles player object.
 *
 * Created by Viktor Stärn on 2015-08-30.
 */
public class Player extends GameObject {

    private int score;
    private double dya;                     //acceleration of rate of player position change
    private boolean up;                     //has screen been touched by player
    private boolean playing;
    private long startTime;
    private Animation animation = new Animation();

    /**
     * Constructs player object using a Bitmap, the size of the Bitmap and the number of frames
     * involved in animation of the Bitmap.
     *
     * @param spritesheet Bitmap spritesheet for animation
     * @param w Width of single frame
     * @param h Height of single frame
     * @param numFrames Number of frames in Bitmap spritesheet
     */
    public Player (Bitmap spritesheet, int w, int h, int numFrames)
    {
        x = 100;                            //set player x position to 100
        y = GamePanel.HEIGHT/2;             //set player y position to the middle of the screen
        dy = 0;                             //set rate of player position change to 0
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];    //create array to hold split Bitmap in preparation for animation

        for(int i=0;i<image.length;i++)            //populate array
        {
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(image);                //send array to animation class
        animation.setDelay(10);
        startTime = System.nanoTime();             //start score "timer"
    }

    public void setUp(boolean b)
    {
        up = b;
    }

    /**
     * Updates player object.
     */
    public void update()
    {
        animation.update();

        if(playing) {
            long elapsed = (System.nanoTime()-startTime)/1000000;
            if (elapsed>1000)
            {
                score++;                            //increment score
                startTime = System.nanoTime();      //reset score "timer"
            }

            if(up)
            {
                dy = (int)(dya-=1.1);               //increase upward acceleration of rate of player position change by 1.1 if player is touching the screen
            }
            else
            {
                dy = (int)(dya+=1.1);               //increase downward acceleration of rate of player position change by 1.1 if player has released the screen
            }

            if(dy>14) dy = 14;                      //cap downward rate of player position change at 14 pixels

            if(dy<-14) dy=-14;                      //cap upward rate of player position change at 14 pixels

            if(dya>10) dya=10;                      //cap downward acceleration of rate of player position change at 10 pixels

            if(dya<-10) dya=-10;                    //cap upward acceleration of rate of player position change at 10 pixels

            y+=dy*2;                                //translate rate of player position change into a player y position

            if(y>GamePanel.HEIGHT) y = 0;           //if player moves below screen edge, move player to the top of the screen

            if(y<0) y = GamePanel.HEIGHT;           //if player moves above screen edge, move player to the bottom of the screen
        }
    }

    /**
     * Draws player object.
     *
     * @param canvas Canvas
     */
    public void draw(Canvas canvas)
    {
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception e) { e.printStackTrace(); }
    }


    public int getScore() { return score; }
    public boolean getPlaying() { return playing; }
    public void setPlaying(boolean b) { this.playing = b; }
    public void resetDYA() { dya = 0; }
    public void resetDY() { dy = 0; }
    public void resetScore() { score = 0; }
    public void raiseScore(int raiseBy) { score+=raiseBy; }


}
