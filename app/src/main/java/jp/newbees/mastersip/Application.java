package jp.newbees.mastersip;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by vietbq on 12/12/16.
 */

public class Application extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        initConfigs();
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
    }

    /**
     * Initial all configs for Application
     * such as Networking, Database, VoIP ...
     */
    private void initConfigs() {
        ConfigManager.initConfig(this);
    }
}
