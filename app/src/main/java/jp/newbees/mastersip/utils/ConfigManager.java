package jp.newbees.mastersip.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import jp.newbees.mastersip.BuildConfig;
import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.UserItem;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by vietbq on 12/12/16.
 */

final public class ConfigManager {
    private final RequestQueue requestQueue;
    private static ConfigManager instance;
    private final String deviceId;
    private final SharedPreferences sharedPreferences;

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

        //init font
        FontUtils.getInstance().initFonts(context);

        //Init SharePreference
        sharedPreferences = context.getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
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

    public void saveRegisterToken(String token){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.Application.REGISTER_TOKEN, token);
        editor.commit();
    }

    public void saveAuthId(String authId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.Application.AUTHORIZATION, authId);
        editor.commit();
    }

    public FilterItem getFilterUser() {
        String jFilter = sharedPreferences.getString(Constant.Application.SETTING_FILTER, null);
        Gson gson = new Gson();
        FilterItem filterItem;
        if (jFilter != null) {
            Type type = new TypeToken<FilterItem>() {
            }.getType();
            filterItem = gson.fromJson(jFilter, type);
        }else {
            filterItem = new FilterItem();
            this.saveFilterSetting(filterItem);
        }
        return filterItem;
    }

    public final void saveFilterSetting(FilterItem filterItem){
        Gson gson = new Gson();
        String jFilter = gson.toJson(filterItem);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.Application.SETTING_FILTER, jFilter);
        editor.commit();
    }

    public UserItem getCurrentUser() {
        Gson gson = new Gson();
        String jUser = sharedPreferences.getString(Constant.Application.USER_ITEM, null);
        UserItem userItem = new UserItem();
        if (jUser != null) {
            Type type = new TypeToken<UserItem>() {
            }.getType();
            userItem = gson.fromJson(jUser, type);
        }
        return userItem;
    }
}
