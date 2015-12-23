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
import android.graphics.Rect;

/**
 * Creates enemy birds for the wasp to avoid.
 *
 * Created by Viktor Stärn on 2015-09-12.
 */
public class EnemyBird extends GameObject {

    private int score;
    private int speed;
    Bitmap [] image1;
    Bitmap [] image2;
    private Animation animation = new Animation();

    /**
     * Constructs enemy bird object. Takes 2 different spritesheets, which are then used for 2 different animations of the enemy bird, as parameters.
     *
     * @param spritesheet1 Bitmap spritesheet for first animation
     * @param spritesheet2 Bitmap spritesheet for second animation
     * @param x x-axis start position of enemy bird
     * @param y y-axis start position of enemy bird
     * @param w Width of single frame
     * @param h Height of single frame
     * @param s Player score
     * @param numFrames1 Number of frames in first Bitmap spritesheet
     * @param numFrames2 Number of frames in second Bitmap spritesheet
     */
    public EnemyBird(Bitmap spritesheet1, Bitmap spritesheet2, int x, int y, int w, int h, int s, int numFrames1, int numFrames2)
    {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;
        speed = 7 + score/70;
        image1 = new Bitmap[numFrames1];        //create arrays to hold split Bitmap in preparation for animation
        image2 = new Bitmap[numFrames2];

        for(int i=0;i<image1.length;i++)        //populate arrays
        {
            image1[i] = Bitmap.createBitmap(spritesheet1, i*width, 0, width, height);
        }

        for(int j=0;j<image2.length;j++)
        {
            image2[j] = Bitmap.createBitmap(spritesheet2, j*width, 0, width, height);
        }

        animation.setFrames(image2);            //send one of the arrays to animation class
    }

    /**
     * Updates enemy bird object.
     */
    public void update()
    {
        x-=speed;

        if(score>500){                          //switch to the other spritesheet when score reaches 500
            animation.setFrames(image1);
        }

        animation.setDelay(100 - speed);
        animation.update();
    }

    /**
     * Draw enemy bird object.
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

    @Override
    public Rect getRectangle()
    {
        return new Rect(x, y+(height/3), x+width, y+height-(height/3));
    }


}
