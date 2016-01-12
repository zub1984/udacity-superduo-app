package barqsoft.footballscores.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import barqsoft.footballscores.R;

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
public class Utility {

    public static final int CHAMPIONS_LEAGUE = 405;

    public static String getMatchDay(int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return "Group Stages, Matchday : 6";
            } else if (match_day == 7 || match_day == 8) {
                return "First Knockout round";
            } else if (match_day == 9 || match_day == 10) {
                return "QuarterFinal";
            } else if (match_day == 11 || match_day == 12) {
                return "SemiFinal";
            } else {
                return "Final";
            }
        } else {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return " - ";
        } else {
            return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
        }
    }

    //http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Set the content description on a remote view
     *
     * @param views       RemoteViews
     * @param viewId      int
     * @param description String
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static void setImageContentDescription(RemoteViews views, int viewId, String description) {
        views.setContentDescription(viewId, description);
    }


    /**
     * Scale a bitmap and return the scaled version
     *
     * @param context Context
     * @param bitmap  Bitmal
     * @param height  int
     * @return Bitmap
     */
    /*public static Bitmap scaleBitmapImage(Context context, Bitmap bitmap, int height) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int newHeight = (int) (height * densityMultiplier);
        int newWidth = (int) (newHeight * bitmap.getWidth() / ((double) bitmap.getHeight()));
        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return bitmap;
    }*/

    /**
     * function to get today locale date.
     *
     * @return string of formatted date.
     */
    public static String getTodayLocaleDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return simpleDateFormat.format(date);
    }


    /**
     * function to set pager tab left and right padding for 16dp
     */
    public static void setPagerTabStrip(View rootView, Context context) {
        PagerTabStrip strip = (PagerTabStrip) rootView.findViewById(R.id.pager_header);
        strip.setTabIndicatorColor(ContextCompat.getColor(context, android.R.color.white));
        strip.setPadding(
                (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin),
                0,
                (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin),
                0);
    }

    /**
     * Check if we are on a device in RTL mode
     * http://stackoverflow.com/questions/14402329/disable-actionbar-rtl-direction
     *
     * @return boolean
     */
    public static boolean isRtlMode(Context context) {
        boolean rtl = false;
        // check direction depending on the api level device supports
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // if device supports API 17 or higher we can use getLayoutDirection
            Configuration config = context.getResources().getConfiguration();
            if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                rtl = true;
            }
        } else {
            // else get language codes
            Set<String> lang = new HashSet<>();
            lang.add("ar");
            lang.add("dv");
            lang.add("fa");
            lang.add("ha");
            lang.add("he");
            lang.add("iw");
            lang.add("ji");
            lang.add("ps");
            lang.add("ur");
            lang.add("yi");
            Set<String> RTL = Collections.unmodifiableSet(lang);
            Locale locale = Locale.getDefault();
            rtl = RTL.contains(locale.getLanguage());
        }
        return rtl;
    }

    public static String builtURI(String uri) {
        return Uri.parse(uri).buildUpon().toString();
    }

}
