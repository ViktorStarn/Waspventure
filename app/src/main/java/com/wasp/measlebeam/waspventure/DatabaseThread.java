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

import android.content.Context;

/**
 * Handles database thread.
 *
 * Created by Viktor Stärn on 2015-12-16.
 */
public class DatabaseThread extends Thread {

    private int value = 0;
    private Context context;

    public DatabaseThread (Context context)
    {
        this.context = context;
    }

    /**
     * Communicates with database.
     */
    @Override
    public void run()
    {
        int databaseValue = retrieveHighScoreFromDatabase();
        if(databaseValue<value) {
            sendHighScoreToDatabase(value);
        }
        else {
            value = databaseValue;
        }
    }

    /**
     * Sends new highscore value to database.
     *
     * @param value Highscore value
     */
    private void sendHighScoreToDatabase(int value)
    {
        Database myDB = new Database(context);
        String sentHS = Integer.toString(value);
        myDB.setHighScore(sentHS);
        myDB.close();
    }

    /**
     * Retrieves highscore value from database.
     *
     * @return Retrieved highscore
     */
    private int retrieveHighScoreFromDatabase()
    {
        Database myDB = new Database(context);
        String retrievedHSString = myDB.getHighScore();
        int retrievedHSint = Integer.parseInt(retrievedHSString);
        myDB.close();
        return retrievedHSint;
    }

    /**
     * Updates the highscore value stored in the object.
     *
     * @param value Highscore value
     */
    public void updateDatabase(int value)
    {
        if(this.value<value) {
            this.value = value;
        }
    }

    public int getValue()
    {
        return value;
    }
}
