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

/**
 * Handles game object animation.
 *
 * Created by Viktor Stärn on 2015-09-09.
 */
public class Animation {

    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;
    private Bitmap[] frames;

    /**
     * Sets frames to use in animation.
     *
     * @param frames Frames
     */
    public void setFrames(Bitmap[] frames)
    {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
    }

    public void setDelay(long d) { delay = d; }
    public void setFrame(int i) { currentFrame = i; }

    /**
     * Updates animation.
     */
    public void update()
    {
        long elapsed = (System.nanoTime()-startTime)/1000000;

        if(elapsed>delay)
        {
            currentFrame++;
            startTime = System.nanoTime();
        }

        if(currentFrame == frames.length)
        {
            currentFrame = 0;
            playedOnce = true;
        }
    }

    public Bitmap getImage()
    {
        return frames[currentFrame];
    }

    public int getFrame() { return currentFrame; }
    public boolean playedOnce() { return playedOnce; }


}
