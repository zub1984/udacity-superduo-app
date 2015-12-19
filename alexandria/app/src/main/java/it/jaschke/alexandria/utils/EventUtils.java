package it.jaschke.alexandria.utils;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by laptop on 12/11/2015.
 */
public class EventUtils {

    public static boolean keyEvent(int actionId, KeyEvent event) {
        boolean flag = false;
        if (
                ((null != event) && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)
                ) {
            flag = true;
        }
        return flag;
    }


    /**
     * Hide the soft keyboard from an activity
     * @param activity Activity
     */
    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
