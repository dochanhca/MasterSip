package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/20/16.
 */

public class FilterUserTask extends BaseTask<HashMap<String, Object>> {
    public static final String NEXT_PAGE = "NEXT_PAGE";
    public static final String LIST_USER = "LIST_USER";
    private final FilterItem filterItem;
    private final String nextPage;
    private final UserItem userItem;

    /**
     * This API uses for filter user by conditions
     * @param context
     * @param filterItem
     * @param nextPage
     * @param userItem
     */
    public FilterUserTask(Context context, FilterItem filterItem, String nextPage, UserItem userItem) {
        super(context);
        this.filterItem = filterItem;
        this.userItem = userItem;
        this.nextPage = nextPage;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        if (filterItem.getMinAge() >= 18) {
            jParams.put(Constant.JSON.ABOVE_AGE, filterItem.getMinAge());
        }
        if (filterItem.getMaxAge() > 0) {
            jParams.put(Constant.JSON.BELOW_AGE, filterItem.getMaxAge());
        }
        int numberOfProvinces = filterItem.getLocations().size();
        if (numberOfProvinces > 0) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < numberOfProvinces; i++) {
                jsonArray.put(filterItem.getLocations().get(i).getSelectionItem().getId());
            }

            try {
                String result = java.net.URLDecoder.decode(jsonArray.toString(), "UTF-8");
                jParams.put(Constant.JSON.PROVINCES, result);
            } catch (UnsupportedEncodingException e) {
                Logger.e(TAG, e.getMessage());
            }
        }

        jParams.put(Constant.JSON.ORDER_BY, filterItem.getOrderBy().getId());
        jParams.put(Constant.JSON.LOGIN_24_HOUR_AGO, filterItem.isLogin24hours() ? 1 : 0);
        jParams.put(Constant.JSON.FILTER_TYPE, filterItem.getFilterType());

        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.FILTER_USER + "/" + userItem.getUserId() + "/" + nextPage;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected HashMap<String, Object> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        String nextPage = jData.getString(Constant.JSON.NEXT_PAGE);
        List<UserItem> userItems  = JSONUtils.parseUsers(jData);
        HashMap<String, Object> result = new HashMap<>();
        result.put(NEXT_PAGE, nextPage);
        result.put(LIST_USER, userItems);
        return result;
    }

}
