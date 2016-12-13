package jp.newbees.mastersip.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import jp.newbees.mastersip.utils.FontUtils;

/**
 * Created by ducpv on 12/8/16.
 */

public class MasterSipApplication extends Application {

    private static MasterSipApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        FontUtils.getInstance().initFonts(getApplicationContext());
    }

    public static synchronized MasterSipApplication getInstance() {
        return mInstance;
    }
}
