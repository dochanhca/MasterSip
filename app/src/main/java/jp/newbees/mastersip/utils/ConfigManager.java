package jp.newbees.mastersip.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.cache.DiskBasedCache;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import jp.newbees.mastersip.BuildConfig;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.UserItem;

import static com.facebook.FacebookSdk.getCacheDir;
import static jp.newbees.mastersip.utils.Constant.API.BASE_URL;

/**
 * Created by vietbq on 12/12/16.
 */

final public class ConfigManager {
    private static final String WAITING_ID = "WAITING_ID";
    private final RequestQueue requestQueue;
    private static ConfigManager instance;
    private final String deviceId;
    private final SharedPreferences sharedPreferences;
    private String domain;
    private int imageDrawableCalleeId = -1;
    private HashMap<String, UserItem> callUsers;
    private int currentCallType;
    private int imageDrawableCallerId = -1;

    private int unReadMessage;
    private int currentTabInRootNavigater;
    private String currentCallId;
    private HashMap<String, Integer> callStatus;
    public final static int CALL_STATE_WAITING = 1;
    public final static int CALL_STATE_CONNECTED = 2;
    private int startServiceFrom;

    public final static void initConfig(Context context) {
        if (instance == null) {
            instance = new ConfigManager(context);
        }
    }

    public final static synchronized ConfigManager getInstance() {
        if (instance == null) {
            Logger.e("ConfigManager", "ConfigManager Must call method initConfig first !!! ");
        }
        return instance;
    }


    private ConfigManager(Context context) {
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
        domain = BASE_URL;
        callUsers = new HashMap<>();
        callStatus = new HashMap<>();
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * @return Device ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    public String getOSVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }

    public String getApplicationVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public String getDeviceInfo() {
        return android.os.Build.MODEL;
    }

    public void saveRegisterToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.Application.REGISTER_TOKEN, token);
        editor.commit();
    }

    public String getRegisterToken() {
        return sharedPreferences.getString(Constant.Application.REGISTER_TOKEN, "");
    }

    public String getAuthId() {
        return sharedPreferences.getString(Constant.Application.AUTHORIZATION, "");
    }

    public void saveAuthId(String authId) {
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
        } else {
            filterItem = new FilterItem();
            this.saveFilterSetting(filterItem);
        }
        return filterItem;
    }

    public final void saveFilterSetting(FilterItem filterItem) {
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

    public void saveUser(UserItem userItem) {
        Gson gson = new Gson();
        String jUser = gson.toJson(userItem);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.Application.USER_ITEM, jUser);
        editor.commit();
    }

    public final void removeUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.Application.USER_ITEM, null);
        editor.commit();
    }

    public int getImageCalleeDefault() {
        if (imageDrawableCalleeId == -1) {

            imageDrawableCalleeId = getCurrentUser().getGender()
                    == UserItem.MALE
                    ? R.drawable.ic_girl_default
                    : R.drawable.ic_boy_default;
        }
        return imageDrawableCalleeId;
    }

    public String getDomain() {
        return domain;
    }

    public void saveLoginFlag(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.Application.LOGIN_FLAG, flag);
        editor.commit();
    }

    public void resetSettings() {
        imageDrawableCalleeId = -1;
        imageDrawableCallerId = -1;
        callUsers.clear();
        clearUser();
        LoginManager.getInstance().logOut();
    }

    private void clearUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.commit();
    }

    public boolean firstTimeAskingForPermission(String permission) {
        return false;
    }

    public UserItem getCurrentCallee(String callId) {
        UserItem callee = callUsers.get(callId);
        return callee;
    }

    public int getCurrentCallType() {
        return currentCallType;
    }

    public void setCurrentCallUser(UserItem callUser, String roomId) {
        callUsers.put(roomId, callUser);
    }

    public String getCallId() {
        return this.currentCallId;
    }

    public void setCallId(String callId) {
        Logger.e("ConfigManager", "Call ID saved : " + callId);
        this.currentCallId = callId;
    }

    public void removeCurrentCall() {
        if (inCall()) {
            callUsers.remove(getCallId());
        }
        this.currentCallId = null;
    }

//    public final void updateEndCallStatus(boolean status) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(Constant.Application.LAST_STATUS_END_CALL, status);
//        editor.commit();
//    }

    public boolean inCall() {
        if (this.currentCallId == null) {
            return false;
        }
        return true;
    }

    public void setCurrentCallType(int callType) {
        this.currentCallType = callType;
    }

    public int getImageCallerDefault() {
        if (imageDrawableCallerId == -1) {

            imageDrawableCallerId = getCurrentUser().getGender()
                    == UserItem.MALE
                    ? R.drawable.ic_boy_default
                    : R.drawable.ic_girl_default;
        }
        return imageDrawableCallerId;
    }

    public void setUnreadMessage(int unReadMessage) {
        this.unReadMessage = unReadMessage;
    }

    public int getUnreadMessage() {
        return unReadMessage;
    }

    public int getCurrentTabInRootNavigater() {
        return currentTabInRootNavigater;
    }

    public void setCurrentTabInRootNavigater(int currentTabInRootNavigater) {
        this.currentTabInRootNavigater = currentTabInRootNavigater;
    }

    public void saveBackupEmail(String email) {
        UserItem userItem = getCurrentUser();
        userItem.setEmail(email);
        saveUser(userItem);
    }

    public void saveFirstTimeChattingFlag() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.Application.CHATTING_FLAG, true);
        editor.commit();
    }

    public boolean getFirstTimeChattingFlag() {
        return sharedPreferences.getBoolean(Constant.Application.CHATTING_FLAG, false);
    }

    public UserItem getCalleeByRoomId(String roomId) throws NullPointerException {
        return this.callUsers.get(roomId);
    }

    public void setCallState(String callId, int callState) {
        if (callStatus.keySet().size() >= 1) {
            callStatus.clear();
        }
        callStatus.put(callId, callState);
    }

    public int getCallState(String callId) {
        return callStatus.get(callId);
    }

    public void startServiceFrom(int startFromPushNotification) {
        this.startServiceFrom = startFromPushNotification;
    }

    public int getStartServiceFrom() {
        return startServiceFrom;
    }
}
