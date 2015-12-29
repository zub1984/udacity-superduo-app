package barqsoft.footballscores.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract.scores_table;
import barqsoft.footballscores.utils.FootballUtils;
import barqsoft.footballscores.utils.Utility;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {
    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();

        //DatabaseUtils.dumpCursor(cursor);

        String homeTeamName = cursor.getString(cursor.getColumnIndex(scores_table.HOME_COL));
        mHolder.home_name.setText(homeTeamName);
        // use glide to load image as it's little fast to load team logo.
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(scores_table.HOME_LOGO_COL)))
                .placeholder(R.drawable.football)
                .error(R.drawable.football)
                .into(mHolder.home_crest);
        mHolder.home_crest.setContentDescription(homeTeamName);

        /*mHolder.home_crest.setImageResource(Utility.getTeamCrestByTeamName(
                cursor.getString(cursor.getColumnIndex(scores_table.HOME_LOGO_COL))));*/

        String awayTeamName = cursor.getString(cursor.getColumnIndex(scores_table.AWAY_COL));
        mHolder.away_name.setText(awayTeamName);
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(scores_table.AWAY_LOGO_COL)))
                .placeholder(R.drawable.football)
                .error(R.drawable.football)
                .into(mHolder.away_crest);
        mHolder.away_crest.setContentDescription(awayTeamName);


        /*mHolder.away_crest.setImageResource(Utility.getTeamCrestByTeamName(
                cursor.getString(cursor.getColumnIndex(scores_table.AWAY_LOGO_COL))
        ));*/

        mHolder.date.setText(cursor.getString(cursor.getColumnIndex(scores_table.TIME_COL)));

        mHolder.score.setText(Utility.getScores(cursor.getInt(cursor.getColumnIndex(scores_table.HOME_GOALS_COL)),
                cursor.getInt(cursor.getColumnIndex(scores_table.AWAY_GOALS_COL))));

        mHolder.match_id = cursor.getDouble(cursor.getColumnIndex(scores_table.MATCH_ID));


        //Log.v("TAG_2_FIND_NAME", mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() + " id " + String.valueOf(mHolder.match_id));
        //Log.v("LOG_TAG", String.valueOf(detail_match_id));

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        if (mHolder.match_id == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));

            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);

            match_day.setText(Utility.getMatchDay(cursor.getInt(cursor.getColumnIndex(scores_table.MATCH_DAY)),
                    cursor.getInt(cursor.getColumnIndex(scores_table.LEAGUE_COL))));

            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(FootballUtils.getLeagueName(context, cursor.getInt(cursor.getColumnIndex(scores_table.LEAGUE_COL))));
            //league.setText(Utility.getLeague(cursor.getInt(cursor.getColumnIndex(scores_table.LEAGUE_COL))));

            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }


    }

    public Intent createShareIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    public static class ViewHolder
    {
       @Bind(R.id.home_name)
        TextView home_name;
        @Bind(R.id.away_name)
        TextView away_name;
        @Bind(R.id.score_textview)
        TextView score;
        @Bind(R.id.data_textview)
        TextView date;
        @Bind(R.id.home_crest)
        ImageView home_crest;
        @Bind(R.id.away_crest)
        ImageView away_crest;

        public double match_id;

        public ViewHolder(@NonNull View view)
        {
            ButterKnife.bind(this,view);
        }
    }


}
