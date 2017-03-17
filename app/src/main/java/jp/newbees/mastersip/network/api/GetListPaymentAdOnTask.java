package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by ducpv on 3/16/17.
 */

public class GetListPaymentAdOnTask extends BaseTask<List<PaymentAdOnItem>> {

    public GetListPaymentAdOnTask(Context context) {
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
        return Constant.API.PAYMENT_PACKAGE_LIST;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected List<PaymentAdOnItem> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parsePaymentPackageList(jData);
    }
}
