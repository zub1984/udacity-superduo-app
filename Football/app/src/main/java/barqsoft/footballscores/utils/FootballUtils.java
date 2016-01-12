package barqsoft.footballscores.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.service.FootballFetchService;

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
public class FootballUtils {

    /**
     * function to get league id from soccer season href link
     *
     * @param context          of the application
     * @param soccerSeasonHref soccer season href link from football.org
     * @return int league id
     */
    public static int getLeagueId(Context context, String soccerSeasonHref) {
        final String SEASON_LINK = context.getString(R.string.api_base_url) + "/" + context.getString(R.string.api_seasons) + "/";
        soccerSeasonHref = soccerSeasonHref.replace(SEASON_LINK, "");
        return Integer.valueOf(soccerSeasonHref.trim());
    }

    /**
     * function to get league id from self href link
     *
     * @param context  of the application
     * @param selfHref self href link from football.org
     * @return int match id
     */
    public static int getMatchId(Context context, String selfHref) {
        final String MATCH_LINK = context.getString(R.string.api_base_url) + "/" + context.getString(R.string.api_fixtures) + "/";
        return Integer.valueOf(selfHref.replace(MATCH_LINK, "").trim());
    }


    /**
     * function to get homeTeam or awayTeam id from Team href link
     *
     * @param context  of the application
     * @param teamHref homeTeam or awayTeam href link from football.org
     * @return int Team id
     */
    public static int getTeamId(Context context, String teamHref) {
        final String TEAM_LINK = context.getString(R.string.api_base_url) + "/" + context.getString(R.string.api_teams) + "/";
        return Integer.valueOf(teamHref.replace(TEAM_LINK, "").trim());
    }


    /**
     * function to get homeTeam or awayTeam id from Team href link
     *
     * @param crestUrl crestUrl of team
     * @return String modified logo url path for .png image
     */
    public static String getTeamLogo(String crestUrl) {

        //  SVG original URL:   https://upload.wikimedia.org/wikipedia/de/d/d8/Heracles_Almelo.svg
        //  PNG 200px URL:	    https://upload.wikimedia.org/wikipedia/de/thumb/d/d8/Heracles_Almelo.svg/200px-Heracles_Almelo.svg.png
        String svgLogoUrl = crestUrl;
        String filename = svgLogoUrl.substring(svgLogoUrl.lastIndexOf("/") + 1);
        int wikipediaPathEndPos = svgLogoUrl.indexOf("/wikipedia/") + 11;
        String afterWikipediaPath = svgLogoUrl.substring(wikipediaPathEndPos);
        int insertPos = wikipediaPathEndPos + afterWikipediaPath.indexOf("/") + 1;
        String afterLanguageCodePath = svgLogoUrl.substring(insertPos);
        crestUrl = svgLogoUrl.substring(0, insertPos);
        crestUrl += "thumb/" + afterLanguageCodePath;
        crestUrl += "/200px-" + filename + ".png";
        return crestUrl;
    }


    /**
     * function to check if an int exists in int array
     *
     * @param array int[]
     * @param key   int
     * @return boolean
     */
    public static boolean isLeagueIdAvailable(final int[] array, final int key) {
        // loop and return true when found
        for (final int i : array) {
            if (i == key) return true;
        }
        return false;
    }

    /**
     * function to format and return date and time
     *
     * @param matchDate match date and time from server
     * @param isReal    real data flag
     * @return String formatted date and time separated by ","
     */
    public static String getMatchDateTime(String matchDate, boolean isReal, int counter) {

        String date = matchDate.substring(0, matchDate.indexOf("T"));
        String time = matchDate.substring(matchDate.indexOf("T") + 1, matchDate.indexOf("Z"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        // convert date and time to local date time
        try {
            SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd:HH:mm", Locale.US);
            newDate.setTimeZone(TimeZone.getDefault());
            date = newDate.format(simpleDateFormat.parse(date + time));
            time = date.substring(date.indexOf(":") + 1);
            date = date.substring(0, date.indexOf(":"));
            // change dummy data's date to match current date range
            if (!isReal) {
                Date dummyDate = new Date(System.currentTimeMillis() + ((counter - 2) * 86400000));
                SimpleDateFormat dummyDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                date = dummyDateFormat.format(dummyDate);
            }
        } catch (Exception e) {
            Log.e(FootballUtils.class.getSimpleName(), e.getMessage());
        }
        return date + "," + time;
    }


    /**
     * Get league name from league code
     *
     * @param leagueId int
     * @param context  Context
     * @return String
     */
    public static String getLeagueName(Context context, int leagueId) {
        int[] leagueCodes = context.getResources().getIntArray(R.array.league_codes);
        String[] leagueLabels = context.getResources().getStringArray(R.array.league_labels);
        // find position of the league code, we get the league label at same index
        for (int i = 0; i < leagueCodes.length; i++) {
            if (leagueCodes[i] == leagueId) {
                return leagueLabels[i];
            }
        }
        return context.getString(R.string.unknown_league_name);
    }

    /**
     * method to fetch football details using intent
     *
     * @param context  application context
     * @param activity from which we called.
     */
    public static void fetchFootballData(@NonNull Context context, @NonNull Activity activity) {
        if (Utility.isNetworkAvailable(context)) {
            Intent intent = new Intent(activity, FootballFetchService.class);
            intent.setAction(Constants.FETCH_INFO);
            activity.startService(intent);
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Asynchronously fulfills the request into the specified {@link RemoteViews} object with the
     * given {@code viewId}. This is used for loading bitmaps into all instances of a widget.
     */
    private static void setPicasso(@NonNull Context context, RemoteViews views, int viewId, @NonNull String imageUrl, int[] appWidgetIds) {
        // refer https://github.com/square/picasso/issues/587
        /*Picasso.with(context)
                .load(Utility.builtURI(imageUrl))
                .into(views, viewId, appWidgetIds);*/
        /**
         * To fix the issue of Picasso call from "RemoteViews getViewAt(int position)"
         * need to change the code as app is crashing for today widget while image loading Picasso bug
         * java.lang.IllegalStateException: Method call should happen from the main thread.
         *
         2nd option : use Glide for image loading on RemoteView.
         *
         */

        try {
            Bitmap logoBitmap = Picasso.with(context).load(Utility.builtURI(imageUrl)).get();
            views.setImageViewBitmap(viewId, logoBitmap);
        } catch (IOException e) {
            views.setImageViewResource(viewId, R.drawable.football);
            e.printStackTrace();
        }

    }


    /**
     * method to set fixture view information
     *
     * @param context      application context
     * @param views        remote view to set the data.
     * @param cursor       having information from database
     * @param appWidgetIds to display in remoteView
     */
    public static void setFixtureView(Context context, RemoteViews views, Cursor cursor, int[] appWidgetIds) {

        setPicasso(context, views, R.id.home_crest, cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_LOGO_COL)), appWidgetIds);

        String homeTeamName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        views.setTextViewText(R.id.home_name, homeTeamName);
        views.setTextColor(R.id.home_name, ContextCompat.getColor(context, R.color.secondary_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Utility.setImageContentDescription(views, R.id.home_crest, homeTeamName);
        }
        // score and match time
        views.setTextViewText(R.id.score_textview, Utility.getScores(
                cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL)),
                cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL))));
        views.setTextColor(R.id.score_textview, ContextCompat.getColor(context, R.color.secondary_text));
        views.setTextViewText(R.id.date_textview, cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL)));
        views.setTextColor(R.id.date_textview, ContextCompat.getColor(context, R.color.secondary_text));

        // away team logo and name
        setPicasso(context, views, R.id.away_crest, cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_LOGO_COL)), appWidgetIds);

        String awayTeamName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        views.setTextViewText(R.id.away_name, awayTeamName);
        views.setTextColor(R.id.away_name, ContextCompat.getColor(context, R.color.secondary_text));

        // set content description on away team logo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Utility.setImageContentDescription(views, R.id.away_crest, awayTeamName);
        }
    }

}
