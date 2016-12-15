package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;


/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseTask<RESULT_DATA extends Object> {

    private static final int NETWORK_TIME_OUT = 30000;
    private static final int REQUEST_OK = 0;
    protected static String TAG;
    private static Context context;
    private Request<RESULT_DATA> request;
    //    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String authorization;
    private final String registerToken;
    private RESULT_DATA dataResponse;

    public BaseTask(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(Constant.Application.AUTHORIZATION, "");
        registerToken = sharedPreferences.getString(Constant.Application.REGISTER_TOKEN, "");
        TAG = getClass().getName();
    }

    final void request(final Response.Listener<RESULT_DATA> listener, final ErrorListener errorListener) {
        String url = "http://" + Constant.API.BASE_URL + "/" + Constant.API.PREFIX_URL + "/" + Constant.API.VERSION + "/" + getUrl();
        Logger.d(TAG, "URL request : " + url);
        request = new Request<RESULT_DATA>(getMethod(), url, new Response.ErrorListener() {
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
                JSONObject jParams = null;
                try {
                    jParams = genBodyParam();
                    if (null == jParams) {
                        jParams = new JSONObject();
                    }
                    getCommonParams(jParams);
                } catch (JSONException e) {
                    Logger.e(TAG,e.getLocalizedMessage());
                    SipError sipError = new SipError(Constant.Error.PARSE_PARAM_ERROR, e.getMessage());
                    Response.error(sipError);
                }
                if (Constant.Application.SHOW_DATA_REQUEST) {
                    Logger.d(TAG,jParams.toString());
                }
                byte[] body = jParams.toString().getBytes();
                return body;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-type", "application/json");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Response<RESULT_DATA> parseNetworkResponse(NetworkResponse response) {
                String data = new String(response.data);
                RESULT_DATA result = null;
                SipError sipError;
                try {
                    sipError = validData(data);
                    if (null == sipError) {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONObject jData = jsonObject.getJSONObject(Constant.JSON.kData);
                        result = didResponse(jData);
                        return Response.success(result, getCacheEntry());
                    } else {
                        return Response.error(sipError);
                    }
                } catch (JSONException e) {
                    sipError = new SipError(Constant.Error.PARSE_ERROR, "parse json error");
                    return Response.error(sipError);
                }
            }

            @Override
            protected void deliverResponse(RESULT_DATA response) {
                BaseTask.this.dataResponse = response;
                listener.onResponse(response);
            }
        };

//      HttpsTrustManager.allowAllSSL();
        request.setRetryPolicy(new DefaultRetryPolicy(NETWORK_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ConfigManager.getInstance().getRequestQueue().add(request);
    }

    private SipError validData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        int code = jsonObject.getInt(Constant.JSON.kCode);
        if (code != REQUEST_OK) {
            String message = jsonObject.getString(Constant.JSON.kMessage);
            SipError sipError = new SipError(code, message);
            return sipError;
        }else {
            return null;
        }
    }

    private void getCommonParams(JSONObject jParams) throws JSONException {
        Gson gson = new Gson();
        String jUser = sharedPreferences.getString(Constant.Application.USER_ITEM, null);
        String registerToken = sharedPreferences.getString(Constant.Application.REGISTER_TOKEN, "");
        UserItem userItem;
        if (jUser != null) {
            Type type = new TypeToken<UserItem>() {
            }.getType();
            userItem = gson.fromJson(jUser, type);
            jParams.put(Constant.JSON.kClientAuthID, userItem.getUserId());
            jParams.put(Constant.JSON.kRegisterToken, registerToken);
        }
    }

    protected boolean hasValueForKey(JSONObject jsonObject, String key, int position) {
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    protected abstract JSONObject genBodyParam() throws JSONException;

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
    protected abstract RESULT_DATA didResponse(JSONObject data) throws JSONException;

    /**
     * This method support for get Device Id
     *
     * @return Device Id
     */
    protected final String getDeviceId() {
        return ConfigManager.getInstance().getDeviceId();
    }

    public RESULT_DATA getDataResponse() {
        return dataResponse;
    }

    public interface ErrorListener {
        void onError(int errorCode, String errorMessage);
    }

    protected String getRegisterToken() {
        return registerToken;
    }
}