package barqsoft.footballscores.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract.scores_table;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.FootballUtils;
import barqsoft.footballscores.utils.Utility;
import butterknife.Bind;
import butterknife.ButterKnife;

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

public class ScoresAdapter extends CursorAdapter {
    public double detail_match_id = 0;

    @Bind(R.id.matchday_textview)
    TextView matchDayTextView;
    @Bind(R.id.league_textview)
    TextView leagueTextView;
    @Bind(R.id.share_button)
    Button shareButton;


    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        //DatabaseUtils.dumpCursor(cursor);
        String homeTeamName = cursor.getString(cursor.getColumnIndex(scores_table.HOME_COL));
        mHolder.home_name.setText(homeTeamName);

        //to avoid App crash if home team logo URL is blank
        String homeTeamLogoUri = cursor.getString(cursor.getColumnIndex(scores_table.HOME_LOGO_COL));
        if (null != homeTeamLogoUri && homeTeamLogoUri.length() > 0) {
            Picasso.with(context)
                    .load(Utility.builtURI(homeTeamLogoUri))
                    .placeholder(R.drawable.football)
                    .error(R.drawable.football)
                    .into(mHolder.home_crest);
            mHolder.home_crest.setContentDescription(homeTeamName);
        } else {
            mHolder.away_crest.setImageResource(R.drawable.football);
        }

        String awayTeamName = cursor.getString(cursor.getColumnIndex(scores_table.AWAY_COL));
        mHolder.away_name.setText(awayTeamName);
        String awayTeamLogoUri = cursor.getString(cursor.getColumnIndex(scores_table.AWAY_LOGO_COL));
        //fix the issue of app crash when away team logo URL is blank
        if (null != awayTeamLogoUri && awayTeamLogoUri.length() > 0) {
            Picasso.with(context)
                    .load(Utility.builtURI(awayTeamLogoUri))
                    .placeholder(R.drawable.football)
                    .error(R.drawable.football)
                    .into(mHolder.away_crest);
        } else {
            mHolder.away_crest.setImageResource(R.drawable.football);
        }

        mHolder.away_crest.setContentDescription(awayTeamName);
        mHolder.date.setText(cursor.getString(cursor.getColumnIndex(scores_table.TIME_COL)));
        mHolder.score.setText(Utility.getScores(cursor.getInt(cursor.getColumnIndex(scores_table.HOME_GOALS_COL)),
                cursor.getInt(cursor.getColumnIndex(scores_table.AWAY_GOALS_COL))));
        mHolder.match_id = cursor.getDouble(cursor.getColumnIndex(scores_table.MATCH_ID));

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = mHolder.detailsFragmentContainer;

        if (mHolder.match_id == detail_match_id) {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ButterKnife.bind(this, container);

            matchDayTextView.setText(Utility.getMatchDay(cursor.getInt(cursor.getColumnIndex(scores_table.MATCH_DAY)),
                    cursor.getInt(cursor.getColumnIndex(scores_table.LEAGUE_COL))));

            leagueTextView.setText(FootballUtils.getLeagueName(context, cursor.getInt(cursor.getColumnIndex(scores_table.LEAGUE_COL))));

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(createShareIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }


    }

    /**
     * function to create share action
     *
     * @param ShareText text message to share
     * @return Intent type of share action.
     */
    @SuppressLint("InlinedApi")
    public Intent createShareIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + Constants.FOOTBALL_SCORES_HASH_TAG);
        return shareIntent;
    }

    public static class ViewHolder {
        @Bind(R.id.home_name)
        TextView home_name;
        @Bind(R.id.away_name)
        TextView away_name;
        @Bind(R.id.score_textview)
        TextView score;
        @Bind(R.id.date_textview)
        TextView date;
        @Bind(R.id.home_crest)
        ImageView home_crest;
        @Bind(R.id.away_crest)
        ImageView away_crest;

        @Bind(R.id.details_fragment_container)
        FrameLayout detailsFragmentContainer;

        public double match_id;

        public ViewHolder(@NonNull View view) {
            ButterKnife.bind(this, view);
        }
    }


}
