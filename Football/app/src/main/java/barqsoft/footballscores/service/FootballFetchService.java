package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.FootballUtils;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FootballFetchService extends IntentService {
    public static final String LOG_TAG = FootballFetchService.class.getSimpleName();

    private static int[] LEAGUE_CODES;
    private ArrayList<ContentValues> mTeamsList;

    public FootballFetchService() {
        super("FootballFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(null!=intent){
            if(Constants.FETCH_INFO.equals(intent.getAction())){
                LEAGUE_CODES = getResources().getIntArray(R.array.leagues_selected);
                mTeamsList = new ArrayList<>();

                getTeamDetails();
                // load fixtures for previous 2 days - and next 3 days including current day
                getData("p2");
                getData("n3");
            }
        }
    }

    /**
     * get team details for defined leagues in array.xml file and store team id & logo url in a ArrayList
     */
    private void getTeamDetails() {
        // for each league get the teams
        for (final int code : LEAGUE_CODES) {
            try{
                String queryTeamsUrl = Uri.parse(getString(R.string.api_base_url)).buildUpon()
                        .appendPath(getString(R.string.api_seasons))
                        .appendPath(Integer.toString(code))
                        .appendPath(getString(R.string.api_teams))
                        .build().toString();

                processTeams(callApi(queryTeamsUrl));
            }
            catch(IOException e){
                Log.v(LOG_TAG, "[getTeamDetails] - IOException");
            }

        }
    }

    /**
     * Convert loaded json teams data to list of team id and team logo url
     *
     * @param teamsString String
     */
    private void processTeams(String teamsString) {
        final String TEAMS = getString(R.string.api_teams);
        final String LINKS = "_links";
        final String SELF = "self";
        final String CREST_URL = "crestUrl";
        try {
            JSONArray teams = new JSONObject(teamsString).getJSONArray(TEAMS);
            if (teams.length() > 0) {
                for (int i = 0; i < teams.length(); i++) {
                    JSONObject team = teams.getJSONObject(i);
                    // extract team id from self href links
                    int teamId= FootballUtils.getTeamId(
                            getApplicationContext(),
                            team.getJSONObject(LINKS).getJSONObject(SELF).getString("href"));

                    // get the crestUrl
                    String teamLogoUrl = team.getString(CREST_URL);
                     if (teamLogoUrl != null && teamLogoUrl.endsWith(".svg"))
                         teamLogoUrl = FootballUtils.getTeamLogo(teamLogoUrl);

                    ContentValues teamValues = new ContentValues();
                    teamValues.put(Constants.TEAM_ID, teamId);
                    teamValues.put(Constants.TEAM_LOGO, teamLogoUrl);
                    mTeamsList.add(teamValues);
                }
            } else {
                Log.e(LOG_TAG, "No teams found");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Query the football-data api for matches in given timeFrame
     *
     * @param timeFrame String
     */
    private void getData(String timeFrame) {
        try{
            String queryFixturesUrl = Uri.parse(getString(R.string.api_base_url)).buildUpon()
                    .appendPath(getString(R.string.api_fixtures))
                    .appendQueryParameter(getString(R.string.api_param_timeframe), timeFrame)
                    .build().toString();

            getMatchFixtures(callApi(queryFixturesUrl));
        }
        catch(IOException e){
            Log.v(LOG_TAG, "[getData] - IOException");
        }

    }

    /**
     * Convert api fixtures json data to array of matches
     *
     * @param fixturesString String
     */
    private void getMatchFixtures(String fixturesString) {

        // indicator for real or dummy data
        boolean isReal = true;

        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";
        final String HOME_TEAM_ID = "homeTeam";
        final String AWAY_TEAM_ID = "awayTeam";

        try {
            JSONArray fixtures = new JSONObject(fixturesString).getJSONArray(FIXTURES);

            // load dummy data if no matches found
            if (fixtures.length() == 0) {
                fixtures = new JSONObject(getString(R.string.dummy_data)).getJSONArray(FIXTURES);
                isReal = false;
            }

            List<ContentValues> fixturesList = new ArrayList<>();

            for (int i = 0; i < fixtures.length(); i++) {

                // get the match details
                JSONObject fixture = fixtures.getJSONObject(i);

                int league = FootballUtils.getLeagueId(
                        getApplicationContext(),
                        fixture.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).getString("href"));

                // only include matches from selected leagues
                if (FootballUtils.isLeagueIdAvailable(LEAGUE_CODES, league)) {

                    int matchId = FootballUtils.getMatchId(
                            getApplicationContext(),
                            fixture.getJSONObject(LINKS).getJSONObject(SELF).getString("href")
                            );

                    // extract the home team id from href in links.homeTeam
                    int homeTeamId= FootballUtils.getTeamId(getApplicationContext(),
                            fixture.getJSONObject(LINKS).getJSONObject(HOME_TEAM_ID).getString("href")
                    );

                    int awayTeamId= FootballUtils.getTeamId(getApplicationContext(),
                            fixture.getJSONObject(LINKS).getJSONObject(AWAY_TEAM_ID).getString("href")
                    );

                    // increment the match id to make it unique for dummy data
                    if (!isReal) {
                        matchId = matchId + i;
                    }

                    String dateTime= FootballUtils.getMatchDateTime(fixture.getString(MATCH_DATE), isReal, i);
                    // create content values object containing the match details
                    ContentValues fixtureValues = new ContentValues();
                    fixtureValues.put(DatabaseContract.scores_table.MATCH_ID, matchId);
                    fixtureValues.put(DatabaseContract.scores_table.DATE_COL, dateTime.split(",")[0]);
                    fixtureValues.put(DatabaseContract.scores_table.TIME_COL, dateTime.split(",")[1]);
                    fixtureValues.put(DatabaseContract.scores_table.HOME_COL, fixture.getString(HOME_TEAM));
                    fixtureValues.put(DatabaseContract.scores_table.HOME_ID_COL, homeTeamId);
                    fixtureValues.put(DatabaseContract.scores_table.HOME_LOGO_COL, getTeamLogoById(homeTeamId));

                    fixtureValues.put(DatabaseContract.scores_table.HOME_GOALS_COL, fixture.getJSONObject(RESULT).getString(HOME_GOALS));
                    fixtureValues.put(DatabaseContract.scores_table.AWAY_COL, fixture.getString(AWAY_TEAM));

                    fixtureValues.put(DatabaseContract.scores_table.AWAY_ID_COL, awayTeamId);
                    fixtureValues.put(DatabaseContract.scores_table.AWAY_LOGO_COL, getTeamLogoById(awayTeamId));

                    fixtureValues.put(DatabaseContract.scores_table.AWAY_GOALS_COL, fixture.getJSONObject(RESULT).getString(AWAY_GOALS));
                    fixtureValues.put(DatabaseContract.scores_table.LEAGUE_COL, league);
                    fixtureValues.put(DatabaseContract.scores_table.MATCH_DAY, fixture.getString(MATCH_DAY));

                    fixturesList.add(fixtureValues);
                }
            }

            // bulk insert the data in database
            if (fixturesList.size() > 0) {
                int recordInserted= getApplicationContext().getContentResolver().bulkInsert(
                        DatabaseContract.BASE_CONTENT_URI, fixturesList.toArray(new ContentValues[fixturesList.size()]));
                Log.v(LOG_TAG,"recordInserted:"+recordInserted);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Exception here in processFixtures: " + e.getMessage());
        }
    }


    /**
     * Get the team logo url from given team id
     *
     * @param teamId int
     * @return String
     */
    private String getTeamLogoById(int teamId) {
        String logoURL="";
        for (ContentValues team : mTeamsList) {
            if (team.getAsInteger(Constants.TEAM_ID).equals(teamId)) {
                logoURL = team.getAsString(Constants.TEAM_LOGO);
            }
        }
        return logoURL;
    }


    /**
     * fetch details from API server.
     *
     * @param builtUri    to make API call.
     */
    private String callApi(String builtUri)throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(builtUri)
                .get()
                .addHeader("X-Auth-Token", getString(R.string.api_key))
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}

