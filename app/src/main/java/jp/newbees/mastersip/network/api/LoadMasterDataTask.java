package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.MasterDataItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 5/10/17.
 */

public class LoadMasterDataTask extends BaseTask<MasterDataItem>{

    public LoadMasterDataTask(Context context) {
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
        return Constant.API.MASTER_DATA;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected MasterDataItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parseMasterData(jData);
    }
}
