package jp.newbees.mastersip.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by thangit14 on 3/29/17.
 */

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks{
    private static ArrayList<Activity> activities = new ArrayList<>();
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
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

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }

    public static int getNumberOfActivity() {
        return activities.size();
    }

}
