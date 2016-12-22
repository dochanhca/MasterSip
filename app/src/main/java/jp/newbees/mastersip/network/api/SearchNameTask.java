package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vietbq on 12/22/16.
 */

public class SearchNameTask extends BaseTask<HashMap<String, Object>> {
    public SearchNameTask(Context context) {
        super(context);
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        return null;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected int getMethod() {
        return 0;
    }

    @Override
    protected HashMap<String, Object> didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
