package jp.newbees.mastersip.network.api;

/**
 * Created by vietbq on 12/14/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thanglh on 13/11/2014.
 */
public abstract class BaseUploadTask<T extends Object> {
    private static final int NETWORK_TIME_OUT = 30000;
    private Request<T> mRequest;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private SharedPreferences sharedPreferences;

    private MultipartEntityBuilder mEntityBuilder;
    private int REQUEST_OK = 0;

    protected BaseUploadTask(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public final void request(final Response.Listener<T> listener, final ErrorListener errorListener) {
        buildMultipartEntity();

        mRequest = new Request<T>(genMethod(), genURL(), new Response.ErrorListener() {
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
//                headers.put("app-session-id", session);
//                headers.put("os", "" + 2);

//                try {
//                    data = ResGuildApplication.CONFIG.USER_AGENT.getBytes("UTF-8");
//                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);
//                    Log.e("TAG", "BASE 64 =" + base64);
//                    headers.put("agent", URLEncoder.encode(base64, "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    headers.put("agent", "Android");
//                }

//                headers.put("udid", ResGuildApplication.CONFIG.DEVICE_UUID);
//                headers.put("locale", ResGuildApplication.CONFIG.CURRENT_LOCAL);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                HttpEntity httpEntity = mEntityBuilder.build();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    httpEntity.writeTo(bos);
                } catch (IOException e) {
                    VolleyLog.e("IOException writing to ByteArrayOutputStream");
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
            protected void deliverResponse(T data) {
                listener.onResponse(data);
            }
        };
        mRequestQueue = Volley.newRequestQueue(mContext);
        mRequest.setRetryPolicy(new DefaultRetryPolicy(NETWORK_TIME_OUT, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mRequest);
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

    private void buildMultipartEntity() {
        mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.addBinaryBody(getNameEntity(), getInputStream(), ContentType.create("image/jpeg"), getFileName());
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
        HashMap<String,String>params = genBodyParam();
        if (null!=params){
            Set<String> keySet = params.keySet();
            for (Iterator<String> key = keySet.iterator();key.hasNext();){
                String name = key.next();
                String value = params.get(name);
                mEntityBuilder.addTextBody(name,value);
            }
        }
        setCommonParams();
    }

    private void setCommonParams() {
        Gson gson = new Gson();
        String jUser = sharedPreferences.getString(Constant.Application.USER_ITEM, null);
        String registerToken = sharedPreferences.getString(Constant.Application.REGISTER_TOKEN, "");
        UserItem userItem;
        if (jUser != null) {
            Type type = new TypeToken<UserItem>() {
            }.getType();
            userItem = gson.fromJson(jUser, type);
            mEntityBuilder.addTextBody(Constant.JSON.kClientAuthID, userItem.getUserId());
            mEntityBuilder.addTextBody(Constant.JSON.kRegisterToken, registerToken);
        }
    }

    protected abstract String getNameEntity();

    protected abstract T didResponse(JSONObject data) throws JSONException;

    public abstract String genURL();

    public abstract int genMethod();

    protected abstract InputStream getInputStream();

    protected abstract String getFileName();

    @Nullable
    protected abstract HashMap<String, String> genBodyParam();

    public interface ErrorListener {
        public void onErrorListener(int errorCode, String errorMessage);
    }
}