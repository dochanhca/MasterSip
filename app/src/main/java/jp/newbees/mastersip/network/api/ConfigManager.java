package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.facebook.FacebookSdk;

import jp.newbees.mastersip.BuildConfig;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by vietbq on 12/12/16.
 */

final public class ConfigManager {
    private final RequestQueue requestQueue;
    private static ConfigManager instance;
    private final String deviceId;

    public final static void initConfig(Context context){
        if (instance == null) {
            instance = new ConfigManager(context);
        }
    }

    public final static synchronized ConfigManager getInstance(){
        if (instance == null) {
            Logger.e("ConfigManager","ConfigManager Must call method initConfig first !!! ");
        }
        return instance;
    }


    private ConfigManager(Context context){
        FacebookSdk.sdkInitialize(context);
        Constant.API.initBaseURL();
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        //Init request queue for request data
//        requestQueue = Volley.newRequestQueue(context);
        requestQueue = new RequestQueue(cache, network);
        // Start the queue
        requestQueue.start();
        //Get device Id
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * @return Device ID
     */
    public String getDeviceId(){
        return deviceId;
    }

    public String getOSVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    public String getApplicationVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public String getDeviceInfo() {
        return android.os.Build.MODEL;
    }
}
