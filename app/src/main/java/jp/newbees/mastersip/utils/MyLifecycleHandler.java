package jp.newbees.mastersip.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by thangit14 on 3/29/17.
 */

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private ArrayList<Activity> activities = new ArrayList<>();
    private int resumed;
    private int paused;
    private int started;
    private int stopped;

    public Handler mHandler = new Handler();
    private boolean mActive = false;
    private InactivityChecker mLastChecker;
    private ActivityMonitorListener activityMonitorListener;

    private MyLifecycleHandler() {
    }

    private static MyLifecycleHandler instance;

    public static MyLifecycleHandler getInstance() {
        if (instance == null) {
            instance = new MyLifecycleHandler();
        }
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
        checkActivity();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        checkActivity();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
    }

    public boolean isApplicationVisible() {
        return started > stopped;
    }

    public boolean isApplicationInForeground() {
        return resumed > paused;
    }

    public int getNumberOfActivity() {
        return activities.size();
    }

    public void registerActivityMonitorListener(ActivityMonitorListener activityMonitorListener) {
        MyLifecycleHandler.this.activityMonitorListener = activityMonitorListener;
    }

    public void unregisterActivityMonitorListener() {
        MyLifecycleHandler.this.activityMonitorListener = null;
    }

    class InactivityChecker implements Runnable {
        private boolean isCanceled;

        public void cancel() {
            isCanceled = true;
        }

        @Override
        public void run() {
            synchronized (MyLifecycleHandler.this) {
                if (!isCanceled) {
                    if (getRunningActivities() == 0 && mActive) {
                        mActive = false;
                        if (activityMonitorListener != null) {
                            activityMonitorListener.onBackgroundMode();
                        }
                    }
                }
            }
        }
    }

    private void checkActivity() {
        if (getRunningActivities() == 0) {
            if (mActive) startInactivityChecker();
        } else if (getRunningActivities() > 0) {
            if (!mActive) {
                mActive = true;
                if (activityMonitorListener != null) {
                    activityMonitorListener.onForegroundMode();
                }
            }
            if (mLastChecker != null) {
                mLastChecker.cancel();
                mLastChecker = null;
            }
        }
    }

    void startInactivityChecker() {
        if (mLastChecker != null) mLastChecker.cancel();
        mHandler.postDelayed((mLastChecker = new InactivityChecker()), 1000);
    }

    private int getRunningActivities() {
        return started - stopped;
    }

    public interface ActivityMonitorListener{
        void onForegroundMode();
        void onBackgroundMode();
    }
}
