package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

import static jp.newbees.mastersip.utils.Constant.API.SEARCH_BY_NAME_URL;

/**
 * Created by vietbq on 12/22/16.
 */

public class FilterNameTask extends BaseTask<HashMap<String, Object>> {

    public static final String NEXT_PAGE = "NEXT_PAGE";
    public static final String LIST_USER = "LIST_USER";

    private final UserItem userItem;
    private final int page;
    private final String name;

    public FilterNameTask(Context context, UserItem userItem, int page, String name) {
        super(context);
        this.userItem = userItem;
        this.page = page;
        this.name = name;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.kNextPage, page);
        jParams.put(Constant.JSON.kName, name);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        String url = String.format("%s/%s", SEARCH_BY_NAME_URL, userItem.getUserId());
        return url;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected HashMap<String, Object> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        String nextPage = jData.getString(Constant.JSON.kNextPage);
        List<UserItem> userItems = JSONUtils.parseUsers(jData);
        HashMap<String, Object> result = new HashMap<>();
        result.put(NEXT_PAGE, nextPage);
        result.put(LIST_USER, userItems);
        return result;
    }
}
