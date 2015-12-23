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
 * Handles game object collisions.
 *
 * Created by Viktor Stärn on 2015-08-31.
 */
public class Collision {

    private int x;
    private int y;
    private int height;
    private int width;
    private Animation animation = new Animation();

    /**
     * Constructs collision object.
     *
     * @param spritesheet Bitmap spritesheet for animation
     * @param x x-axis position of collision
     * @param y y-axis position of collision
     * @param h Height of single frame
     * @param w Width of single frame
     * @param numFramesRow Number of frames per row in Bitmap spritesheet
     * @param numFrames Number of frames in Bitmap spritesheet
     */
    public Collision (Bitmap spritesheet, int x, int y, int h, int w, int numFramesRow, int numFrames)
    {
        this.x = x;
        this.y = y;
        height = h;
        width = w;
        int row = 0;
        Bitmap[] image = new Bitmap[numFrames];             //create array to hold split Bitmap in preparation for animation

        for(int i = 0; i<image.length; i++) {               //populate array
            if(i%numFramesRow==0&&i>0)row++;
            image[i] = Bitmap.createBitmap(spritesheet, (i-(numFramesRow*row))*width, row*height, width, height);
        }
        animation.setFrames(image);                         //send array to animation class
        animation.setDelay(10);
    }

    /**
     * Updates collision object.
     */
    public void update()
    {
        if(!animation.playedOnce()){
            animation.update();
        }
    }

    /**
     * Draws collision object.
     *
     * @param canvas Canvas
     */
    public void draw(Canvas canvas)
    {
        if(!animation.playedOnce()){
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }
    }

    public int getHeight() { return height; }

    public int getWidth() { return width; }


}
