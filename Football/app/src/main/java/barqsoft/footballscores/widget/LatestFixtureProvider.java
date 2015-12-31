package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import barqsoft.footballscores.service.FootballFetchService;
import barqsoft.footballscores.utils.Constants;

/**
 * Created by laptop on 12/31/2015.
 */
public class LatestFixtureProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // start the football data service to request the updated fixtures from the api
        Intent intent = new Intent(context, FootballFetchService.class);
        intent.setAction(Constants.FETCH_INFO);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // refresh the widget by starting the widget service that will reload the data from the database
        if (Constants.ACTION_FOOTBALL_SCORE_UPDATED.equals(intent.getAction())) {
            Intent latestFixture = new Intent(context, LatestFixtureService.class);
            latestFixture.setAction(Constants.ACTION_FOOTBALL_SCORE_UPDATE_RECEIVED);
            context.startService(latestFixture);
        }
    }
}