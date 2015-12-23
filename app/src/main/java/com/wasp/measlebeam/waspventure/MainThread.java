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
import android.view.SurfaceHolder;

/**
 * Handles the main execution thread.
 *
 * Created by Viktor Stärn on 2015-08-29.
 */
public class MainThread extends Thread {

    private double averageFPS;
    private final SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    /**
     * 4.	Constructs MainThread. Instantiates android.view.SurfaceHolder class and
     *      GamePanel class (Surface View).
     *
     * @param surfaceHolder surfaceHolder object
     * @param gamePanel gamePanel object
     */
    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    /**
     * 5.	The instantiation of GamePanel class (Surface View) calls surfaceCreated method in
     *      GamePanel class which in turn instantiates the background and foreground classes and
     *      starts the MainThread thread by calling the run method in MainThread class.
     *
     * 6.	The MainThread run method fetches the canvas, the Surface View bitmap, on which to
     *      draw the back- and foreground.
     *
     * 7.	The MainThread run method calls the GamePanel class methods update and draw which
     *      updates the canvas with new information and then draws the information on to the canvas.
     */
    @Override
    public void run()
    {
        int FPS = 30;
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/FPS;

        while(running)
        {
            startTime = System.nanoTime();
            canvas = null;

            try                                                         //try locking the canvas for pixel editing
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    this.gamePanel.update();                            //update canvas
                    this.gamePanel.draw(canvas);                        //draw canvas
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally
            {
                if(canvas!=null)
                {
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch (Exception e) {e.printStackTrace();}
                }
            }

            timeMillis = (System.nanoTime()-startTime)/1000000;
            waitTime = targetTime - timeMillis;

            if(waitTime>0) {
                try
                {
                    this.sleep(waitTime);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if(frameCount == FPS)                                        //cap FPS at 30
            {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
//                System.out.println(averageFPS);
            }
        }
    }

    public void setRunning (boolean b){
        running = b;
    }

    public void threadSleep(int sleepTime){
        try
        {
            this.sleep(sleepTime);
        }
        catch (InterruptedException e) { e.printStackTrace(); }
    }


}
