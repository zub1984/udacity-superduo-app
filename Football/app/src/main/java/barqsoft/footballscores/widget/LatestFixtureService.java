package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
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


/*Copyright (C) 2015  Mohammad Jubair Khan (zub1984.kn@gmail.com) - Football Scores Project of Udacity Nano degree course.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

public class LatestFixtureService extends IntentService {
    //private static final String LOG_TAG = LatestFixtureService.class.getSimpleName();

    public LatestFixtureService() {
        super("LatestFixtureService");
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        //Log.i(LOG_TAG, "onHandleIntent Service Called");

        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return;
        }

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

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        setLatestFixtureView(cursor, appWidgetManager, appWidgetId);
        cursor.close();


    }


    /**
     * https://github.com/square/picasso/issues/587
     * To fix the issue of Picasso call from "RemoteViews getViewAt(int position)"
     * need to change the code as app is crashing for today widget while image loading, it's Picasso bug
     * java.lang.IllegalStateException: Method call should happen from the main thread.
     * <p/>
     * 2nd option : use Glide for image loading on RemoteView.
     */

    /*@Override
    public void onStart(Intent intent, int startId) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return;
        }

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

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        setLatestFixtureView(cursor, appWidgetManager, appWidgetId);
        cursor.close();
    }*/
    private void setLatestFixtureView(Cursor cursor, AppWidgetManager appWidgetManager, int widgetId) {
        final RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.latest_fixture_widget);
        FootballUtils.setFixtureView(getApplicationContext(), views, cursor);
        processIntent(cursor, views);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * onclick on the widget launch the app and pass fixture details for date and position selection to MainActivity
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
