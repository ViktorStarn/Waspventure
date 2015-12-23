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

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Handles SQLite highscore database.
 *
 * Created by Viktor Stärn on 2015-09-11.
 */
public class Database extends SQLiteOpenHelper {

    static final String DBNAME = "highScoreDB";
    static final String HIGHSCORES = "HIGHSCORES";
    static final String KEY = "PK";
    static final String HIGHSCORE = "HIGHSCORE";

    /**
     * Constructs database object, takes context as parameter. Changing the 4th parameter sent to the
     * super class will cause the onUpgrade()-method run. Do this if you've made changes to the
     * database and wish to recreate it.
     *
     * @param context Context
     */
    public Database(Context context)
    {
        super(context, DBNAME, null, 5);
    }

    /**
     * Creates new database tables if they don't already exist in the database. Takes database
     * as parameter.
     *
     * @param db Database object
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + HIGHSCORES + " (" +
                    KEY + " INTEGER PRIMARY KEY , " +
                    HIGHSCORE + " TEXT" +
                    ")");

    ContentValues cv = new ContentValues();                 // Populates the database
    cv.put(KEY, 1);
    cv.put(HIGHSCORE, "0");
    db.insert(HIGHSCORES, null, cv);
    }


    /**
     * Deletes the existing table and calls the onCreate()-method again to reset the database.
     *
     * @param db Database object
     * @param oldVersion Old version
     * @param newVersion New version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+HIGHSCORES);
        onCreate(db);
    }

    /**
     * Reads and returns HighScore-value from database.
     *
     * @return Retrieved highscore
     */
    public String getHighScore()
    {
        String retrievedHighscore;
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor myCursor = myDB.rawQuery("SELECT " + HIGHSCORE + " FROM " + HIGHSCORES, null);
        myCursor.moveToFirst();
        retrievedHighscore = myCursor.getString(myCursor.getColumnIndex(HIGHSCORE));
        myCursor.close();
        return retrievedHighscore;
    }

    /**
     * Writes new highscore-value to database.
     *
     * @param highScore Highscore
     */
    public void setHighScore(String highScore)
    {
        String[] args={"1"};
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HIGHSCORE, highScore);
        myDB.update(HIGHSCORES, cv, KEY + " =? ", args);
    }


}


