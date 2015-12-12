package it.jaschke.alexandria.utils;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

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
}
