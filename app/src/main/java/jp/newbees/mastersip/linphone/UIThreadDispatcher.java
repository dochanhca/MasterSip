package jp.newbees.mastersip.linphone;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by vietbq on 4/19/17.
 */

public class UIThreadDispatcher {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void dispatch(Runnable r) {
        mHandler.post(r);
    }
}
