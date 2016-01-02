package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.data.DatabaseContract.scores_table;

/*Copyright (C) 2015  Mohammad Jubair Khan (zub1984.kn@gmail.com) - Football Scores Project of Udacity Nanodegree course.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 3;
    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
       /* final String CreateScoresTable = "CREATE TABLE " + DatabaseContract.SCORES_TABLE + " ("
                + scores_table._ID + " INTEGER PRIMARY KEY,"
                + scores_table.DATE_COL + " TEXT NOT NULL,"
                + scores_table.TIME_COL + " INTEGER NOT NULL,"
                + scores_table.HOME_COL + " TEXT NOT NULL,"
                + scores_table.AWAY_COL + " TEXT NOT NULL,"
                + scores_table.LEAGUE_COL + " INTEGER NOT NULL,"
                + scores_table.HOME_GOALS_COL + " TEXT NOT NULL,"
                + scores_table.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + scores_table.MATCH_ID + " INTEGER NOT NULL,"
                + scores_table.MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE ("+scores_table.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";*/

        final String CreateScoresTable = "CREATE TABLE "+ DatabaseContract.SCORES_TABLE +" ("+
                scores_table._ID +" INTEGER PRIMARY KEY,"+
                scores_table.DATE_COL +" TEXT NOT NULL,"+
                scores_table.TIME_COL +" INTEGER NOT NULL,"+
                scores_table.HOME_COL +" TEXT NOT NULL,"+
                scores_table.HOME_ID_COL +" INTEGER NOT NULL,"+
                scores_table.HOME_LOGO_COL +" TEXT,"+
                scores_table.HOME_GOALS_COL +" TEXT NOT NULL,"+
                scores_table.AWAY_COL +" TEXT NOT NULL,"+
                scores_table.AWAY_ID_COL +" INTEGER NOT NULL,"+
                scores_table.AWAY_LOGO_COL +" TEXT,"+
                scores_table.AWAY_GOALS_COL +" TEXT NOT NULL,"+
                scores_table.LEAGUE_COL +" INTEGER NOT NULL,"+
                scores_table.MATCH_ID +" INTEGER NOT NULL,"+
                scores_table.MATCH_DAY +" INTEGER NOT NULL,"+
                " UNIQUE ("+ scores_table.MATCH_ID +") ON CONFLICT REPLACE);";

        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);
        onCreate(db);
    }
}
