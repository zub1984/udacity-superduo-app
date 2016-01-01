package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.Constants;

/**
 * Created by laptop on 1/1/2016.
 */
public class TodayFixtureProvider extends AppWidgetProvider {

    /**
     * Set the layout and attach onclick handlers to launch the main activity
     * @param context Context
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetIds int[]
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.today_fixture_widget);
            views.setTextViewText(R.id.widget_title, context.getString(R.string.today_fixture_title));
            // create an intent that will launch the main activity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // set the adapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(appWidgetId, context, views);
            }

            // set the intent-template to be used when clicking a list view item that launches the main activity
            Intent templateIntent = new Intent(context, MainActivity.class);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent templatePendingIntent = PendingIntent.getActivity(context, 0, templateIntent, 0);
            views.setPendingIntentTemplate(R.id.widget_scores_list, templatePendingIntent);

            // set the view to show when we have no fixtures
            views.setEmptyView(R.id.widget_scores_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /**
     * When we receive an update from the cursor provider we need to inform the widget
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (Constants.ACTION_FOOTBALL_SCORE_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_scores_list);
        }
    }

    /**
     * Sets the remote adapter used to fill in the list items
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_scores_list, new Intent(context, TodayFixtureService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(int widgetId, Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(widgetId, R.id.widget_scores_list, new Intent(context, TodayFixtureService.class));
    }
}