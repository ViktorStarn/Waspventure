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
 * Handles ascending score notifications.
 *
 * Created by Viktor Stärn on 2015-08-31.
 */
public class AscendingScore {

    private float y = 0;
    private int playerXposition;
    private int playerYposition;
    private String score;

    /**
     * Constructs ascending score notification object.
     */
    public AscendingScore (Player player, String score)
    {
        this.score = score;
        this.playerXposition = player.getX();
        this.playerYposition = player.getY();
    }

    /**
     * Updates ascending score notification object.
     */
    public void update()
    {
        final int MOVESPEED = -4;
        y += MOVESPEED;
    }

    /**
     * Draws ascending score notification object.
     *
     * @param canvas Canvas
     */
    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(score, playerXposition, y + playerYposition - 20, paint);
    }

    public float getY() {
        return y;
    }


}

