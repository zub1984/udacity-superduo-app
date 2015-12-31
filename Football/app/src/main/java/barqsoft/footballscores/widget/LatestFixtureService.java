package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract.scores_table;
import barqsoft.footballscores.utils.Constants;
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
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            // get most recent fixture
            Uri uri = scores_table.buildMostRecentScore();
            Cursor cursor = getContentResolver().query(
                    uri,
                    null,
                    null,
                    new String[]{simpleDateFormat.format(date)},
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

        setImageViewBitmap(
                views,
                R.id.homeCrest,
                cursor.getString(cursor.getColumnIndex(scores_table.HOME_LOGO_COL)));

        String homeTeamName = cursor.getString(cursor.getColumnIndex(scores_table.HOME_COL));
        views.setTextViewText(R.id.homeName, homeTeamName);
        views.setTextColor(R.id.homeName, ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setImageContentDescription(views, R.id.homeCrest, homeTeamName);
        }
        // score and match time
        views.setTextViewText(R.id.scoreTextView, Utility.getScores(
                cursor.getInt(cursor.getColumnIndex(scores_table.HOME_GOALS_COL)),
                cursor.getInt(cursor.getColumnIndex(scores_table.AWAY_GOALS_COL))));
        views.setTextColor(R.id.scoreTextView, ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));
        views.setTextViewText(R.id.dateTextView, cursor.getString(cursor.getColumnIndex(scores_table.TIME_COL)));
        views.setTextColor(R.id.dateTextView, ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));

        // away team logo and name
        setImageViewBitmap(
                views,
                R.id.awayCrest,
                cursor.getString(cursor.getColumnIndex(scores_table.AWAY_LOGO_COL)));

        String awayTeamName = cursor.getString(cursor.getColumnIndex(scores_table.AWAY_COL));
        views.setTextViewText(R.id.awayName, awayTeamName);
        views.setTextColor(R.id.awayName, ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));

        // set content description on away team logo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setImageContentDescription(views, R.id.awayCrest, awayTeamName);
        }
        processIntent(cursor, views);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * Set the content description on a remote view
     *
     * @param views       RemoteViews
     * @param viewId      int
     * @param description String
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setImageContentDescription(RemoteViews views, int viewId, String description) {
        views.setContentDescription(viewId, description);
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
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, launchIntent, Intent.FILL_IN_ACTION);
        views.setOnClickPendingIntent(R.id.latest_fixture_widget, pIntent);
    }

    /**
     * Load an image from a url using glide int a bitmap
     *
     * @param views    RemoteViews
     * @param viewId   int
     * @param imageUrl the URL path of the image to load
     */
    private void setImageViewBitmap(RemoteViews views, int viewId, String imageUrl) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(LatestFixtureService.this)
                    .load(imageUrl)
                    .asBitmap()
                    .error(R.drawable.football)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(LOG_TAG, getString(R.string.latest_fixture_image_load_error) + imageUrl);
        }

        // if bitmap loaded, update image view
        if (null != bitmap) {
            // scale the bitmap down because of the binder limit
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                bitmap = Utility.scaleBitmapImage(getApplicationContext(), bitmap, 150);
            }
            views.setImageViewBitmap(viewId, bitmap);
        }
    }

}


