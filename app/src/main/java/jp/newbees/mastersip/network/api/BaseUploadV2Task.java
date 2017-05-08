package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/23/17.
 */

public abstract class BaseUploadV2Task<T extends Object>  {
    private static final int NETWORK_TIME_OUT = 30000;
    private MultiPartRequest<T> mRequest;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private T dataResponse;

    private final String authorization;
    private final String registerToken;

    private static final int REQUEST_OK = 0;

    protected static String TAG;

    protected BaseUploadV2Task(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(Constant.Application.AUTHORIZATION, "");
        registerToken = sharedPreferences.getString(Constant.Application.REGISTER_TOKEN, "");
        TAG = getClass().getName();
    }

    /**
     * @param listener
     * @param errorListener
     * @param progressListener
     */
    public final void request(final Response.Listener<T> listener, final BaseUploadTask.ErrorListener errorListener, Response.ProgressListener progressListener) {
        String url = genURL();
        Logger.e(TAG, "URL request : " + url);

        mRequest = new MultiPartRequest<T>(getMethod(), url,listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                SipError sipError;
                if (volleyError instanceof SipError) {
                    sipError = (SipError) volleyError;
                } else {
                    sipError = new SipError(volleyError);
                }
                errorListener.onErrorListener(sipError.getErrorCode(), sipError.getErrorMessage());
            }
        }) {

            @Override
            protected Response<T> parseNetworkResponse(NetworkResponse response) {
                String data = new String(response.data);
                Logger.e("API - " + TAG, data);
                T result = null;
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
                    sipError = new SipError(Constant.Error.PARSE_ERROR, "parse json error");
                    return Response.error(sipError);
                }
            }

            @Override
            protected void deliverResponse(T data) {
                BaseUploadV2Task.this.dataResponse = data;
                listener.onResponse(data);
            }
        };

        RequestQueue mRequestQueue = ConfigManager.getInstance().getRequestQueue();
        mRequest.setRetryPolicy(new DefaultRetryPolicy(NETWORK_TIME_OUT, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequest.addFile("file", getFilePath());
        buildMultipartEntity();
        if (progressListener != null) {
            mRequest.setOnProgressListener(progressListener);
        }
        mRequestQueue.add(mRequest);
    }

    private SipError validData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        int code = jsonObject.getInt(Constant.JSON.CODE);
        if (code != REQUEST_OK) {
            String message = jsonObject.getString(Constant.JSON.MESSAGE);
            SipError sipError = new SipError(code, message);
            return sipError;
        } else {
            return null;
        }
    }

    private void buildMultipartEntity() {
        Map<String, Object> params = genBodyParam();
        if (null != params) {
            Set<String> keySet = params.keySet();
            for (Iterator<String> key = keySet.iterator(); key.hasNext(); ) {
                String name = key.next();
                String value = String.valueOf(params.get(name));
                mRequest.addStringParam(name, value);
            }
        }
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
                    .append("&").append(Constant.JSON.CLIENT_AUTH_ID).append("=").append(authorization);
        }
        return urlBuilder.toString();
    }

    protected String getVersion(){
        return Constant.API.VERSION;
    }

    protected abstract T didResponse(JSONObject data) throws JSONException;

    public abstract String getUrl();

    public abstract int getMethod();

    @Nullable
    protected abstract Map<String, Object> genBodyParam();

    public T getDataResponse() {
        return dataResponse;
    }

    public abstract String getFilePath();

}
