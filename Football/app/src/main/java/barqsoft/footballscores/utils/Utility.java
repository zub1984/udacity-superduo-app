package barqsoft.footballscores.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utility {

    public static final int CHAMPIONS_LEAGUE = 362;

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
     * Scale a bitmap and return the scaled version
     *
     * @param context Context
     * @param bitmap  Bitmal
     * @param height  int
     * @return Bitmap
     */
    public static Bitmap scaleBitmapImage(Context context, Bitmap bitmap, int height) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int newHeight = (int) (height * densityMultiplier);
        int newWidth = (int) (newHeight * bitmap.getWidth() / ((double) bitmap.getHeight()));
        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return bitmap;
    }
}
