package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract.scores_table;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.FootballUtils;
import barqsoft.footballscores.utils.Utility;

/**
 * Created by laptop on 1/1/2016.
 */
public class RemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = RemoteViewFactory.class.getSimpleName();
    private Context mContext;
    private Cursor data = null;

    public RemoteViewFactory(Context context) {
        mContext = context;
    }

    /**
     * Populate the view at the given position
     *
     * @param position number
     * @return RemoteViews
     */
    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.today_fixture_list_item);
        FootballUtils.setFixtureView(mContext, views, data);
        final Intent intentMessage = new Intent();
        final Bundle extras = new Bundle();
        extras.putString(Constants.LATEST_FIXTURE_SCORES_DATE, data.getString(data.getColumnIndex(scores_table.DATE_COL)));
        extras.putInt(Constants.SCORES_MATCH_ID, data.getInt(data.getColumnIndex(scores_table.MATCH_ID)));
        intentMessage.putExtras(extras);
        views.setOnClickFillInIntent(R.id.today_fixture_list_item, intentMessage);

        return views;
    }

    /**
     * Update the data when remote view is changed
     */
    @Override
    public void onDataSetChanged() {
        if (data != null) {
            data.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        // load today fixture
        data = mContext.getContentResolver().query(
                scores_table.buildScoreWithDate(),
                null,
                null,
                new String[]{Utility.getTodayLocaleDate()},
                scores_table.TIME_COL + " ASC, " + scores_table.HOME_COL + " ASC");

        // and restore the identity again
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item);
    }

    @Override
    public long getItemId(int position) {
        if (data.moveToPosition(position))
            return data.getLong(0);
        return position;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

}

