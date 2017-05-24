package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by ducpv on 5/24/17.
 */

public class GetListReportReasonTask extends BaseTask<List<SelectionItem>> {

    private int type;

    public GetListReportReasonTask(Context context, int type) {
        super(context);
        this.type = type;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.TYPE, type);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.REPORT_LIST + "/" + type;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected List<SelectionItem> didResponse(JSONObject data) throws JSONException {
        JSONArray jData = data.getJSONArray(Constant.JSON.DATA);
        return JSONUtils.parseReportReasonList(jData);
    }
}
