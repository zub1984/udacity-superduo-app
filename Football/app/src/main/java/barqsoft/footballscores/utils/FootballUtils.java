package barqsoft.footballscores.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.FootballFetchService;

/**
 * Created by laptop on 12/29/2015.
 */
public class FootballUtils {

    /**
     * function to get league id from soccer season href link
     * @param context of the application
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
     * @param context of the application
     * @param selfHref self href link from football.org
     * @return int match id
     */
    public static int getMatchId(Context context, String selfHref) {
        final String MATCH_LINK = context.getString(R.string.api_base_url) + "/" + context.getString(R.string.api_fixtures) + "/";
        return Integer.valueOf(selfHref.replace(MATCH_LINK, "").trim());
    }


    /**
     * function to get homeTeam or awayTeam id from Team href link
     * @param context of the application
     * @param teamHref homeTeam or awayTeam href link from football.org
     * @return int Team id
     */
    public static int getTeamId(Context context, String teamHref) {
        final String TEAM_LINK = context.getString(R.string.api_base_url) + "/" + context.getString(R.string.api_teams) + "/";
        return Integer.valueOf(teamHref.replace(TEAM_LINK, "").trim());
    }


    /**
     * function to get homeTeam or awayTeam id from Team href link
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
     * @param array int[]
     * @param key int
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
     * @param matchDate match date and time from server
     * @param isReal real data flag
     * @return String formatted date and time separated by ","
     */
    public static String getMatchDateTime(String matchDate,boolean isReal, int counter){

        String date = matchDate.substring(0, matchDate.indexOf("T"));
        String time = matchDate.substring(matchDate.indexOf("T") + 1, matchDate.indexOf("Z"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        // convert date and time to local date time
        try {
            SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd:HH:mm", Locale.US);
            newDate.setTimeZone(TimeZone.getDefault());
            date = newDate.format(simpleDateFormat.parse(date+time));
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
        return date+","+time;
    }


    /**
     * Get league name from league code
     * @param leagueId int
     * @param context Context
     * @return String
     */
    public static String getLeagueName(Context context, int leagueId) {
        int[] leagueCodes = context.getResources().getIntArray(R.array.league_codes);
        String[] leagueLabels = context.getResources().getStringArray(R.array.league_labels);
        // find position of the league code, we get the league label at same index
        for(int i=0; i < leagueCodes.length; i++) {
            if (leagueCodes[i] == leagueId) {
                return leagueLabels[i];
            }
        }
        return context.getString(R.string.unknown_league_name);
    }

    /**
     * method to fetch football details using intent
     * @param context application context
     * @param activity from which we called.
     * @return void
     */
    public static void fetchFootballData(@NonNull Context context,@NonNull Activity activity){
        if(Utility.isNetworkAvailable(context)){
            Intent intent = new Intent(activity, FootballFetchService.class);
            intent.setAction(Constants.FETCH_INFO);
            activity.startService(intent);
        }
        else{
            Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
