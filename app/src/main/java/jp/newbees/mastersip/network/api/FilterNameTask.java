package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

import static jp.newbees.mastersip.utils.Constant.API.SEARCH_BY_NAME_URL;

/**
 * Created by vietbq on 12/22/16.
 */

public class FilterNameTask extends BaseTask<HashMap<String, Object>> {

    public static final String NEXT_PAGE = "NEXT_PAGE";
    public static final String LIST_USER = "LIST_USER";

    private final UserItem userItem;
    private final int page;
    private String name;

    public FilterNameTask(Context context, UserItem userItem, int page, String name) {
        super(context);
        this.userItem = userItem;
        this.page = page;
        this.name = name;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        try {
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, e.getMessage());
        }
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.NEXT_PAGE, page);
        jParams.put(Constant.JSON.NAME, name);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return String.format("%s/%s", SEARCH_BY_NAME_URL, userItem.getUserId());
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected HashMap<String, Object> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        String nextPage = jData.getString(Constant.JSON.NEXT_PAGE);
        List<UserItem> userItems = JSONUtils.parseUsersForFilterByName(jData);
        HashMap<String, Object> result = new HashMap<>();
        result.put(NEXT_PAGE, nextPage);
        result.put(LIST_USER, userItems);
        return result;
    }
}
