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

import java.util.Random;

/**
 * Creates butterflies for the wasp to eat.
 *
 * Created by Viktor Stärn on 2015-09-01.
 */
public class Butterfly extends GameObject {

    private int row;
    private int counterRightOrLeft;                     //will keep track of how far the butterfly has been flying in either right or left direction
    private int counterUpOrDown;                        //will keep track of how far the butterfly has been flying in either up or down direction
    private boolean rightOrLeft;                        //will keep track of whether the butterfly is flying to the right or to the left
    private boolean upOrDown;                           //will keep track of whether the butterfly is flying up or down
    Bitmap [] image1;
    Bitmap [] image2;
    private Random random = new Random();
    private Animation animation = new Animation();

    /**
     * Constructs butterfly object. Takes 2 different spritesheets, which are then used for 2 different animations of the butterfly (left and right), as parameters.
     *
     * @param spritesheet1 Bitmap spritesheet for first animation
     * @param spritesheet2 Bitmap spritesheet for second animation
     * @param x x-axis start position of butterfly
     * @param y y-axis start position of butterfly
     * @param w Width of single frame
     * @param h Height of single frame
     * @param numFrames1 Number of frames in first Bitmap spritesheet
     * @param numFrames2 Number of frames in second Bitmap spritesheet
     */
    public Butterfly(Bitmap spritesheet1, Bitmap spritesheet2, int x, int y, int w, int h, int numFrames1, int numFrames2)
    {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        image1 = new Bitmap[numFrames1];                //create arrays to hold split Bitmap in preparation for animation
        image2 = new Bitmap[numFrames2];

        for(int i = 0; i<image1.length; i++) {          //populate arrays
            if(i%17==0&&i>0)row++;
            image1[i] = Bitmap.createBitmap(spritesheet1, (i-(17*row))*width, row*height, width, height);
        }

        row = 0;

        for(int i = 0; i<image2.length; i++) {
            if(i%17==0&&i>0)row++;
            image2[i] = Bitmap.createBitmap(spritesheet2, (i-(17*row))*width, row*height, width, height);
        }

        animation.setFrames(image1);                    //send one of the arrays to animation class
    }

    /**
     * Updates butterfly object.
     */
    public void update()
    {
        if(y<0) {                                       //if butterfly moves above screen edge, move butterfly to the bottom of the screen
            y = GamePanel.HEIGHT;
        }
        if(y>GamePanel.HEIGHT) {                        //if butterfly moves below screen edge, move butterfly to the top of the screen
            y = 0;
        }
        counterRightOrLeft++;                           //increment counters
        counterUpOrDown++;
        x-=7;
        if(rightOrLeft){                                //if rightOrLeft is true, move right
            x+=3;
            if(upOrDown) {                              //if upOrDown is also true, move up
                y-=3;
            }
            else {                                      //if upOrDown is false, move down
                y+=3;
            }
        }
        else {                                          //if rightOrLeft is false, move left
            if(upOrDown) {                              //if upOrDown is also true, move up
                y-=3;
            }
            else {                                      //if upOrDown is false, move down
                y+=3;
            }
        }
        if(counterRightOrLeft>5) {                      //if counterRightOrLeft has reached more than 5, make random choice of continuing in the same direction or changing direction
            rightOrLeft = random.nextBoolean();
            if(rightOrLeft) {                           //decide which spritesheet to use based on the new direction of the butterfly
                animation.setFrames(image2);
            }
            else {
                animation.setFrames(image1);
            }
            counterRightOrLeft = 0;                     //reset counter
        }
        if(counterUpOrDown>5) {                         //if counterUpOrDown has reached more than 5, make random choice of continuing in the same direction or changing direction
            upOrDown = random.nextBoolean();
            counterUpOrDown = 0;
        }
        animation.setDelay(50);
        animation.update();
    }

    /**
     * Draws butterfly object.
     *
     * @param canvas Canvas
     */
    public void draw(Canvas canvas)
    {
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public int getWidth()
    {
        return width - 10;
    }


}
