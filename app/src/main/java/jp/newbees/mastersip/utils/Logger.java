package jp.newbees.mastersip.utils;

import android.util.Log;

/**
 * Created by vietbq on 12/12/16.
 */

public final class Logger {

    public final static void e(String tag, String message){
        if (Constant.Application.DEBUG) {
            Log.e(tag, message);
        }
    }

    public final static void d(String tag, String message){
        if (Constant.Application.DEBUG) {
            Log.d(tag, message);
        }
    }

    public final static void w(String tag, String message){
        Logger.d(tag,message);
    }

    public final static void v(String tag, String message){
        Logger.d(tag,message);
    }

    public final static void i(String tag, String message){
        Logger.d(tag,message);
    }
}
