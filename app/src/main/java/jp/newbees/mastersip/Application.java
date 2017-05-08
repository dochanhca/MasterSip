package jp.newbees.mastersip;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.fabric.sdk.android.Fabric;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by vietbq on 12/12/16.
 */

public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        initConfigs();
        registerActivityLifecycleCallbacks(MyLifecycleHandler.getInstance());
    }

    /**
     * Initial all configs for Application
     * such as Networking, Database, VoIP ...
     */
    private void initConfigs() {
        ConfigManager.initConfig(this);
    }
}
