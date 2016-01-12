package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.FootballFetchService;
import barqsoft.footballscores.utils.Constants;

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

public class LatestFixtureProvider extends AppWidgetProvider {
    public static final String TAG = LatestFixtureProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        /*if (Constants.ACTION_FOOTBALL_SCORE_UPDATED.equals(intent.getAction())) {
            Log.i(TAG, "onReceive Called.");
        }*/
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Log.i(TAG, "onUpdate Called.");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent fetchIntent = new Intent(context, FootballFetchService.class);
        fetchIntent.setAction(Constants.FETCH_INFO);
        context.startService(fetchIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted Called");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled Called");
        super.onDisabled(context);
    }


    /**
     * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_UPDATE}
     * broadcasts when this AppWidget provider is being asked to provide
     * {@link android.widget.RemoteViews RemoteViews}
     * for a set of AppWidgets.
     *
     * @param context          The {@link android.content.Context Context} in which this receiver is
     *                         running.
     * @param appWidgetManager A {@link AppWidgetManager} object you can call {@link
     *                         AppWidgetManager#updateAppWidget} on.
     * @param appWidgetId      The appWidgetId for which an update is needed.
     * @see AppWidgetManager#ACTION_APPWIDGET_UPDATE
     */
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Build the intent to call the service
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.latest_fixture_widget);
        Intent intent = new Intent(context, LatestFixtureService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // Set unique data for each pending intent, otherwise the system will replace the existing pending intent
        intent.setAction(String.valueOf(appWidgetId));
        // To react to a click we have to use a pending intent as the onClickListener is executed by the home screen application
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.latest_fixture_widget, pendingIntent);
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        // Update the widgets via the service
        context.startService(intent);
    }

}