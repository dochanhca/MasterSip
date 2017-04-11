package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.FollowItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by vietbq on 4/10/17.
 */

public class GetListFollowersTask extends BaseTask<FollowItem> {
    public GetListFollowersTask(Context context) {
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
        return Constant.API.LIST_FOLLOWERS;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected FollowItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parseFollowerItem(jData);
    }
}