package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by vietbq on 2/2/17.
 */

public class GetListGiftTask extends BaseTask<List<GiftItem>> {

    public static final String GIFTS_LIST = "GIFTS_LIST";
    public static final String NEXT_PAGE = "NEXT_PAGE";

    public GetListGiftTask(Context context) {
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
        return Constant.API.GIFTS_LIST;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected List<GiftItem> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        JSONArray jGifts = jData.getJSONArray(Constant.JSON.GIFTS);
        return JSONUtils.parseGiftsList(jGifts);
    }
}
