package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseTask<T extends Object> {

    private static final int NETWORK_TIME_OUT = 30000;
    private static final int REQUEST_OK = 0;
    private static final int ANDROID = 1;
    protected static String TAG;
    private SharedPreferences sharedPreferences;
    private final String authorization;
    private final String registerToken;
    private T dataResponse;

    public BaseTask(Context context) {
        sharedPreferences = context.getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
        authorization = ConfigManager.getInstance().getAuthId();
        registerToken = ConfigManager.getInstance().getRegisterToken();
        TAG = getClass().getSimpleName();
    }

    public final void request(final Response.Listener<T> listener, final ErrorListener errorListener) {
        String url = genURL();
        url += genParamURL();
        if (Constant.Application.DEBUG) {
            Logger.e(TAG, url);
        }
        Request<T> request = new Request<T>(getMethod(), url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SipError sipError;
                if (error instanceof SipError) {
                    sipError = (SipError) error;
                } else {
                    sipError = new SipError(error);
                }
                errorListener.onError(sipError.getErrorCode(), sipError.getErrorMessage());
            }
        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jParams;
                try {
                    jParams = genParams();
                    if (null == jParams) {
                        jParams = new JSONObject();
                    }
                    getCommonParams(jParams);
                } catch (JSONException e) {
                    Logger.e(TAG, e.getLocalizedMessage());
                    SipError sipError = new SipError(Constant.Error.PARSE_PARAM_ERROR, e.getMessage());
                    Response.error(sipError);
                    jParams = new JSONObject();
                }
                if (Constant.Application.SHOW_DATA_REQUEST) {
                    Logger.e(TAG, "Data request : " + jParams.toString());
                }
                return jParams.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-type", "application/json");
                addCommonHeaders(headers);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Response<T> parseNetworkResponse(NetworkResponse response) {
                String data = new String(response.data);
                Logger.e("API - " + TAG, data);
                T result;
                SipError sipError;
                try {
                    sipError = validData(data);
                    if (null == sipError) {
                        JSONObject jsonObject = new JSONObject(data);
                        result = didResponse(jsonObject);
                        return Response.success(result, getCacheEntry());
                    } else {
                        return Response.error(sipError);
                    }
                } catch (JSONException e) {
                    sipError = new SipError(Constant.Error.PARSE_ERROR, "Parse json error");
                    return Response.error(sipError);
                }
            }

            @Override
            protected void deliverResponse(T response) {
                BaseTask.this.dataResponse = response;
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(NETWORK_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ConfigManager.getInstance().getRequestQueue().add(request);
    }

    @NonNull
    private String genURL() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Constant.API.PROTOCOL)
                .append("://")
                .append(Constant.API.BASE_URL)
                .append("/")
                .append(Constant.API.PREFIX_URL)
                .append("/")
                .append(getVersion())
                .append("/").append(getUrl());

        if (!registerToken.isEmpty() && !authorization.isEmpty()) {
            urlBuilder.append("?").append(Constant.JSON.REGIST_TOKEN).append("=").append(registerToken)
                    .append("&").append(Constant.JSON.CLIENT_AUTH_ID).append("=").append(authorization)
                    .append("&").append(Constant.JSON.PLATFORM).append("=").append(ANDROID);
        }
        return urlBuilder.toString();
    }

    private String genParamURL() {
        StringBuilder urlBuilder = new StringBuilder();
        if (getMethod() == Request.Method.GET) {
            try {
                JSONObject jParams = genParams();
                if (jParams != null) {
                    for (Iterator<String> it = jParams.keys(); it.hasNext(); ) {
                        String key = it.next();
                        urlBuilder.append("&").append(key).append("=").append(jParams.get(key));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urlBuilder.toString();
    }

    private SipError validData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        int code = jsonObject.getInt(Constant.JSON.CODE);
        if (code != REQUEST_OK) {
            String message = jsonObject.getString(Constant.JSON.MESSAGE);
            return new SipError(code, message);
        }
        return null;
    }

    private void getCommonParams(JSONObject jParams) throws JSONException {
        Gson gson = new Gson();
        String jUser = sharedPreferences.getString(Constant.Application.USER_ITEM, null);
        UserItem userItem;
        if (jUser != null) {
            Type type = new TypeToken<UserItem>() {
            }.getType();
            userItem = gson.fromJson(jUser, type);
            jParams.put(Constant.JSON.CLIENT_AUTH_ID, userItem.getUserId());
            jParams.put(Constant.JSON.REGIST_TOKEN, this.registerToken);
            jParams.put(Constant.JSON.PLATFORM, ANDROID);
        }
    }

    private void addCommonHeaders(HashMap<String, String> jParams) {
        Gson gson = new Gson();
        String jUser = sharedPreferences.getString(Constant.Application.USER_ITEM, null);
        UserItem userItem;
        if (jUser != null) {
            Type type = new TypeToken<UserItem>() {
            }.getType();
            userItem = gson.fromJson(jUser, type);
            jParams.put(Constant.JSON.CLIENT_AUTH_ID, userItem.getUserId());
            jParams.put(Constant.JSON.REGIST_TOKEN, this.registerToken);
            jParams.put(Constant.JSON.PLATFORM, String.valueOf(ANDROID));
        }
    }

    protected String getVersion() {
        return Constant.API.VERSION;
    }

    @Nullable
    protected abstract JSONObject genParams() throws JSONException;

    @NonNull
    protected abstract String getUrl();

    protected abstract int getMethod();

    /**
     * Callback when get response from Server
     *
     * @param data JSON
     * @return Object that needs return to caller
     * @throws JSONException
     */
    protected abstract T didResponse(JSONObject data) throws JSONException;

    /**
     * This method support for get Device Id
     *
     * @return Device Id
     */
    protected final String getDeviceId() {
        return ConfigManager.getInstance().getDeviceId();
    }

    public T getDataResponse() {
        return dataResponse;
    }

    @FunctionalInterface
    public interface ErrorListener {
        void onError(int errorCode, String errorMessage);
    }
}