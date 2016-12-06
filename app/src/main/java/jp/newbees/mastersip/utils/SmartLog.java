package jp.newbees.mastersip.utils;

import android.util.Log;

/**
 * Created by ducpv on 12/6/16.
 */

public class SmartLog {

    public static void logE(String tag, String message) {
        if (Config.DEBUG_MODE) {
            Log.e(tag, message);
        }
    }

    public static void logE(String tag, String message, Exception e) {
        if (Config.DEBUG_MODE) {
            Log.e(tag, message,e);
        }
    }

    public static void logD(String tag, String message) {
        if (Config.DEBUG_MODE) {
            Log.d(tag, message);
        }
    }
}
