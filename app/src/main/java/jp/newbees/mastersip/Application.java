package jp.newbees.mastersip;

import jp.newbees.mastersip.network.api.ConfigManager;

/**
 * Created by vietbq on 12/12/16.
 */

public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        initConfigs();
    }

    /**
     * Initial all configs for Application
     * such as Networking, Database, VoIP ...
     */
    private void initConfigs() {
        ConfigManager.initConfig(this);
    }
}
