package barqsoft.footballscores.utils;

import barqsoft.footballscores.data.DatabaseContract;

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
public class Constants {
    public static final int CONNECTION_AND_READ_TIME_OUT = 5;

    public static final String TEAM_ID = "team_id";
    public static final String TEAM_LOGO = "team_logo";

    public static final String FETCH_INFO = "football_info";

    // unique key for sending the clicked fixtures date
    public static String LATEST_FIXTURE_SCORES_DATE = "barqsoft.footballscores.latestfixture.DATE";

    public static String SCORES_MATCH_ID = "barqsoft.footballscores.todaysfixtures.MATCH_ID";

    public static final String ACTION_FOOTBALL_SCORE_UPDATED = DatabaseContract.CONTENT_AUTHORITY + ".ACTION_FOOTBALL_SCORE_UPDATED";

    public static final String ACTION_FOOTBALL_SCORE_UPDATE_RECEIVED = "ACTION_FOOTBALL_SCORE_UPDATE_RECEIVED";

    public static String FOOTBALL_SCORES_HASH_TAG = "#Football_Scores";
}
