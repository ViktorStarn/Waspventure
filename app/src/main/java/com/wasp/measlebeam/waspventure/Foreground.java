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
 * Handles game foreground.
 *
 * Created by Viktor Stärn on 2015-08-29.
 */
public class Foreground {

    private int x = 0;
    private Bitmap image;

    /**
     * Constructs foreground object.
     *
     * @param res Bitmap image
     */
    public Foreground (Bitmap res)
    {
        image = res;
    }

    /**
     * Updates foreground object.
     */
    public void update()                                                //scroll the foreground image (start over when edge of screen is reached)
    {
        final int MOVESPEED = -5;                                       //foreground move speed
        x+=MOVESPEED;

        if(x<-GamePanel.WIDTH)
        {
            x=0;
        }
    }

    /**
     * Draws foreground object.
     *
     * @param canvas Canvas
     */
    public void draw(Canvas canvas)                                     //draw scrolling images
    {
        int y = 0;
        canvas.drawBitmap(image, x, y, null);                           //draw first scrolling image
        if(x<0)                                                         //start drawing second scrolling image where the first ends
        {
            canvas.drawBitmap(image, x+GamePanel.WIDTH-1, y, null);
        }
    }


}
