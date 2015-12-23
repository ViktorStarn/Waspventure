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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Handles scrolling credits.
 *
 * Created by Viktor Stärn on 2015-08-30.
 */
public class ScrollingCredits {

    private float x = 0;
    private String credits;


    /**
     * Constructs scrolling credits.
     */
    public ScrollingCredits (String credits)
    {
        this.credits = credits;
    }

    /**
     * Updates scrolling credits.
     */
    public void update()
    {
        final int MOVESPEED = -4;
        x+=MOVESPEED;
        if(x<(-GamePanel.WIDTH - (2*getStringWidth(credits))))      //reset position of scrolling text to x = 0 if x has become less than the screen width - 2 times the length of the string
        {
            x=0;
        }
    }

    /**
     * Draw scrolling credits.
     *
     * @param canvas Canvas
     */
    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(credits, x + GamePanel.WIDTH + 100, GamePanel.HEIGHT - 40, paint);
    }

    /**
     * Returns length of the string containing the credits.
     *
     * @param string Credits string
     * @return Length of string
     */
    public float getStringWidth(String string)
    {
        float calculatedStringWidth;
        Paint paint1 = new Paint();
        calculatedStringWidth = paint1.measureText(string);
        return calculatedStringWidth;
    }


}

