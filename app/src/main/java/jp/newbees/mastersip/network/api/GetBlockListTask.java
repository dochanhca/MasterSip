package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by ducpv on 5/25/17.
 */

public class GetBlockListTask extends BaseTask<List<UserItem>> {

    public GetBlockListTask(Context context) {
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
        return Constant.API.BLOCK_LIST;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected List<UserItem> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parseListBlockUser(jData);
    }
}
