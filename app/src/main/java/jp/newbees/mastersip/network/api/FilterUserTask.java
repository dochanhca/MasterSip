package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by vietbq on 12/20/16.
 */

public class FilterUserTask extends BaseTask<HashMap<String, Object>> {
    private static final String NEXT_PAGE = "NEXT_PAGE";
    private static final String LIST_USER = "LIST_USER";
    private final FilterItem filterItem;

    public FilterUserTask(Context context, FilterItem filterItem) {
        super(context);
        this.filterItem = filterItem;
    }

    @Nullable
    @Override
    protected JSONObject genBodyParam() throws JSONException {
        JSONObject jParams = new JSONObject();
        if (filterItem.getMinAge() >= 18) {
            jParams.put(Constant.JSON.kAboveAge,filterItem.getMinAge());
        }
        if (filterItem.getMaxAge() > 0 ){
            jParams.put(Constant.JSON.kAboveAge,filterItem.getMaxAge());
        }
        int numberOfProvinces = filterItem.getLocations().size();
        if (numberOfProvinces > 0) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < numberOfProvinces; i++) {
                jsonArray.put(filterItem.getLocations().get(i));
            }
            jParams.put(Constant.JSON.kProvinces, jsonArray);
        }
        jParams.put(Constant.JSON.kOrderBy,filterItem.getOrderBy().getId());
        jParams.put(Constant.JSON.kLogin24HourAgo,filterItem.isLogin24hours());
        jParams.put(Constant.JSON.kFilterType, filterItem.getFilterType());

        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.FILTER_USER;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected HashMap<String, Object> didResponse(JSONObject data) throws JSONException {
        int nextPage = data.getInt(Constant.JSON.kNextPage);
        ArrayList<UserItem> userItems  = JSONUtils.parseUsers(data);
        HashMap<String, Object> result = new HashMap<>();
        result.put(NEXT_PAGE, nextPage);
        result.put(LIST_USER,userItems);
        return result;
    }

}
