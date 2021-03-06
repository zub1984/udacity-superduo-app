package barqsoft.footballscores.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

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

public class DatabaseContract
{

    //URI data
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";

    public static final String PATH = "scores";
    public static final String PATH_MOST_RECENT = "mostRecent";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String SCORES_TABLE = "scores_table";

    public static final class scores_table implements BaseColumns
    {
        // column names
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String HOME_ID_COL = "home_id";
        public static final String HOME_LOGO_COL = "home_logo";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_COL = "away";
        public static final String AWAY_ID_COL = "away_id";
        public static final String AWAY_LOGO_COL = "away_logo";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_DAY = "match_day";
        public static final String MATCH_ID = "match_id";


        //public static Uri SCORES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH)
                //.build();

        //Types
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static Uri buildScoreWithLeague()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("league").build();
        }

        public static Uri buildScoreWithId()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
        }

        public static Uri buildScoreWithDate()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
        }

        public static Uri buildMostRecentScore() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOST_RECENT).build();
        }
    }

}
