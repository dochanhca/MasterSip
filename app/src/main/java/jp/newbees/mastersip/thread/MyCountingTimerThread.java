package jp.newbees.mastersip.thread;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 3/16/17.
 * use for invisible target after breakTime
 */

public class MyCountingTimerThread implements Runnable {
    private int breakTime;
    private String id;
    private Handler handler;

    private long startTime;
    private int updateTime;

    private volatile boolean isRunning;

    public MyCountingTimerThread(Handler handler, String id, int breakTime) {
        Logger.e("MyCountingTimerThread","create");
        this.handler = handler;
        startTime = SystemClock.uptimeMillis();
        isRunning = true;
        updateTime = 0;
        this.breakTime = breakTime;
        this.id = id;
    }

    public MyCountingTimerThread(Handler handler) {
        this(handler, "No ID", 1);
    }

    public MyCountingTimerThread(Handler handler, int breakTime) {
        this(handler, "No ID", breakTime);
    }

    @Override
    public void run() {
        Logger.e("MyCountingTimerThread","run...");
        while (isRunning) {
            updateTime = (int) ((SystemClock.uptimeMillis() - startTime) / 1000);
            Logger.e("MyCountingTimerThread","counting... update time =  "+updateTime);
            if (updateTime >= breakTime) {
                sendMessage(updateTime);
            }
            try {
                Logger.e("MyCountingTimerThread","sleep");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendMessage(int updateTime) {
        Message message = Message.obtain();
        message.obj = id;
        message.what =  updateTime;
        handler.sendMessage(message);
    }

    public void reset() {
        startTime = SystemClock.uptimeMillis();
        updateTime = 0;
    }

    public void turnOffCounting() {
        Logger.e("MyCountingTimerThread","turn off");
        isRunning = false;
    }
}
