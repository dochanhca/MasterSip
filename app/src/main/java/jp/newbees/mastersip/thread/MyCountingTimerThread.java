package jp.newbees.mastersip.thread;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created by thangit14 on 3/16/17.
 * use for invisible target after BREAK_TIME
 */

public class MyCountingTimerThread implements Runnable {
    public static final int BREAK_TIME = 5;
    private Handler handler;

    private long startTime;
    private long updateTime;

    private boolean isRunning;

    public MyCountingTimerThread(Handler handler) {
        this.handler = handler;
        startTime = SystemClock.uptimeMillis();
        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            updateTime = (SystemClock.uptimeMillis() - startTime) / 1000;
            if (updateTime >= BREAK_TIME) {
                sendMessage();
                reset();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendMessage() {
        Message message = Message.obtain();
        handler.sendMessage(message);
    }

    public void reset() {
        startTime = SystemClock.uptimeMillis();
        updateTime = 0;
    }

    public void turnOffCounting() {
        isRunning = false;
    }
}
