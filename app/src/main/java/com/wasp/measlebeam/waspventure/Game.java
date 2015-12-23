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

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.media.MediaPlayer;

/**
 * 1.	Creates an activity and a fullscreen window inside that activity (there can be
 *      many windows inside an activity and they’re handled by ”android.view.WindowManager”).
 *
 *      Created by Viktor Stärn on 2015-08-29.
 */
public class Game extends Activity {

    private GamePanel gamePanel;
    private MediaPlayer backgroundMusic;                //handles game music
    private boolean isMusicPlaying;

    /**
     * Creates Game activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);                          //turn title off

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,        //set to fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**
         * 2.	Makes room for a Surface View inside the fullscreen window inside the
         *      Game activity (GamePanel class, when instantiated, creates a Surface View object
         *      which implements the SurfaceHolder.Callback interface which provides the
         *      application with information on changes to the Surface View). 3 -> GamePanel.
         */
        gamePanel = new GamePanel(this);
        setContentView(gamePanel);
        gamePanel.setGame(this);
    }

    /**
     * Handles behaviour for pausing the game. Sets pause-flag and turns music off.
     */
    @Override
    protected synchronized void onPause() {
        super.onPause();

        gamePanel.setIsPaused(true);
        setMusic(false);
    }

    /**
     * Handles behaviour for resuming the game.
     */
    @Override
    protected void onResume(){
        super.onResume();
    }

    /**
     * Starts/stops game music.
     *
     * @param isMusicPlaying
     */
    public void setMusic(boolean isMusicPlaying){
        if(isMusicPlaying) {
            backgroundMusic = MediaPlayer.create(Game.this, R.raw.heroic);      //prepare/load game music
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
        else {
            backgroundMusic.release();                                          //stop music playback
        }
        this.isMusicPlaying = isMusicPlaying;
    }

    public boolean getMusic(){
        return isMusicPlaying;
    }


}
