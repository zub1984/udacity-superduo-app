package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract.scores_table;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.FootballUtils;
import barqsoft.footballscores.utils.Utility;


/**
 * Created by laptop on 12/31/2015.
 */
public class LatestFixtureService extends IntentService {
    private static final String LOG_TAG = LatestFixtureService.class.getSimpleName();

    public LatestFixtureService() {
        super("LatestFixtureService");
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        if (Constants.ACTION_FOOTBALL_SCORE_UPDATE_RECEIVED.equals(intent.getAction())) {
            // get most recent fixture
            Uri uri = scores_table.buildMostRecentScore();
            Cursor cursor = getContentResolver().query(
                    uri,
                    null,
                    null,
                    new String[]{Utility.getTodayLocaleDate()},
                    scores_table.DATE_COL + " DESC, " + scores_table.TIME_COL + " DESC"
            );

            // manage the cursor
            if (cursor == null) return;
            else if (!cursor.moveToFirst()) {
                cursor.close();
                return;
            }

            // find the active instances of our widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            ComponentName latestFixtureWidget = new ComponentName(this, LatestFixtureProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(latestFixtureWidget);
            // loop through all our active widget instances
            for (int widgetId : appWidgetIds)
                setLatestFixtureView(cursor, appWidgetManager, widgetId);
            cursor.close();
        }
    }


    private void setLatestFixtureView(Cursor cursor, AppWidgetManager appWidgetManager, int widgetId) {

        final RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.latest_fixture_widget);
        // set the latest fixture view information
        FootballUtils.setFixtureView(getApplicationContext(), views,cursor);
        processIntent(cursor, views);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * onclick on the widget launch the app and pass fixture details for date and position selection
     *
     * @param cursor of database having most recent fixture details
     * @param views  RemoteViews
     */
    private void processIntent(Cursor cursor, RemoteViews views) {
        Intent launchIntent = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();

        extras.putString(Constants.LATEST_FIXTURE_SCORES_DATE, cursor.getString(cursor.getColumnIndex(scores_table.DATE_COL)));
        extras.putInt(Constants.SCORES_MATCH_ID, cursor.getInt(cursor.getColumnIndex(scores_table.MATCH_ID)));

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, launchIntent, Intent.FILL_IN_ACTION);
        views.setOnClickPendingIntent(R.id.latest_fixture_widget, pIntent);
    }

}


