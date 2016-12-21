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

    private final LatLng latLng;

    public GetProvinceTask(Context context, LatLng latLng) {
        super(context);
        this.latLng = latLng;
    }

    @Nullable
    @Override
    protected JSONObject genBodyParam() throws JSONException {
        return null;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.GET_PROVINCE
                + "?" + Constant.JSON.kLong + "=" + latLng.longitude
                + "&" + Constant.JSON.kLat + "=" + latLng.latitude;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected SelectionItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.kData);

        int provinceId = jData.getInt(Constant.JSON.kReceiveProvinceId);
        String provinceName = jData.getString(Constant.JSON.kProvinceName);

        SelectionItem selectionItem = new SelectionItem(provinceId, provinceName);
        return selectionItem;
    }
}
