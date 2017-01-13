package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 12/20/16.
 */

public class GetProvinceTask extends BaseTask {

    private final LatLng location;

    public GetProvinceTask(Context context, LatLng latLng) {
        super(context);
        this.location = latLng;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.LONG,location.longitude);
        jParam.put(Constant.JSON.LAT, location.latitude);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.GET_PROVINCE;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected SelectionItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);

        int provinceId = jData.getInt(Constant.JSON.K_RECEIVE_PROVINCE_ID);
        String provinceName = jData.getString(Constant.JSON.K_PROVINCE_NAME);

        SelectionItem selectionItem = new SelectionItem(provinceId, provinceName);
        return selectionItem;
    }
}
