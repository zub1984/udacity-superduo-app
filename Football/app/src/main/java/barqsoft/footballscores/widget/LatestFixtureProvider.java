package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

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