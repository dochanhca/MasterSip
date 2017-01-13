package jp.newbees.mastersip.thread;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;


/**
 * Created by ducpv on 1/5/17.
 */

public class CountingTimeThread implements Runnable {
    private TextView txtTimer;
    private Handler timerHandler;

    private long startTime = 0L;
    long timeInMilliseconds = 0L;

    //    long timeSwapBuff = 0L; Using time swap buff if need stop and restart couting
    long updatedTime = 0L;

    public CountingTimeThread(TextView txtTimer, Handler handler) {
        this.txtTimer = txtTimer;
        this.timerHandler = handler;
        startTime = SystemClock.uptimeMillis();
    }

    @Override
    public void run() {
        timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

        updatedTime = timeInMilliseconds;

        int secs = (int) (updatedTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;

        txtTimer.setText(String.format("%02d", mins) + ":"
                + String.format("%02d", secs));
        timerHandler.postDelayed(this, 0);
    }
}
