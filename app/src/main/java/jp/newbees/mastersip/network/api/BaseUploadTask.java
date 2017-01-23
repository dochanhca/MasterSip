package jp.newbees.mastersip.network.api;

/**
 * Created by vietbq on 12/14/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thanglh on 13/11/2014.
 */
public abstract class BaseUploadTask<T extends Object> {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_FILE = 2;

    private static final int NETWORK_TIME_OUT = 30000;
    private Request<T> mRequest;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private SharedPreferences sharedPreferences;
    private T dataResponse;

    private final String authorization;
    private final String registerToken;

    private MultipartEntityBuilder mEntityBuilder;
    private int REQUEST_OK = 0;

    protected static String TAG;

    protected BaseUploadTask(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(Constant.Application.AUTHORIZATION, "");
        registerToken = sharedPreferences.getString(Constant.Application.REGISTER_TOKEN, "");
        TAG = getClass().getName();
    }

    public final void request(final Response.Listener<T> listener, final ErrorListener errorListener) {
        String url = genURL();

        Logger.e(TAG, "URL request : " + url);

        buildMultipartEntity();

        mRequest = new Request<T>(getMethod(), url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                /**
                 * if volleyError is a instance of VolleyErrorHelper ->error from sever
                 * else create new VolleyErrorHelper to handle error from Volley
                 */
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "multipart/form-data");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                HttpEntity httpEntity = mEntityBuilder.build();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    httpEntity.writeTo(bos);
                    Logger.e("REQUEST --> ", "json = " + getBodyContentType());
                } catch (IOException e) {
                    Logger.e("REQUEST --> upload multi part", "IOException writing to ByteArrayOutputStream");
                }
                return bos.toByteArray();
            }

            @Override
            public String getBodyContentType() {
                String contentTypeHeader = mEntityBuilder.build().getContentType().getValue();
                return contentTypeHeader;
            }

            @Override
            protected Response<T> parseNetworkResponse(NetworkResponse response) {
                String data = new String(response.data);
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
                BaseUploadTask.this.dataResponse = data;
                listener.onResponse(data);
            }
        };
        mRequestQueue = Volley.newRequestQueue(mContext);
        mRequest.setRetryPolicy(new DefaultRetryPolicy(NETWORK_TIME_OUT, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        mEntityBuilder = MultipartEntityBuilder.create();

        mEntityBuilder.addBinaryBody(getNameEntity(), getInputStream(), ContentType.MULTIPART_FORM_DATA, getFileName());
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
        Map<String, Object> params = genBodyParam();
        if (null != params) {
            Set<String> keySet = params.keySet();
            for (Iterator<String> key = keySet.iterator(); key.hasNext(); ) {
                String name = key.next();
                String value = String.valueOf(params.get(name));
                mEntityBuilder.addTextBody(name, value);
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

    protected String getVersion() {
        return Constant.API.VERSION;
    }

    protected abstract String getNameEntity();

//    protected abstract int getType();
//
//    protected abstract File getFile();

    protected abstract T didResponse(JSONObject data) throws JSONException;

    public abstract String getUrl();

    public abstract int getMethod();

    protected abstract InputStream getInputStream();

    protected abstract String getFileName();

    @Nullable
    protected abstract Map<String, Object> genBodyParam();

    public T getDataResponse() {
        return dataResponse;
    }

    public interface ErrorListener {
        public void onErrorListener(int errorCode, String errorMessage);
    }
}