package barqsoft.footballscores.utils;

import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by laptop on 12/28/2015.
 */
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
