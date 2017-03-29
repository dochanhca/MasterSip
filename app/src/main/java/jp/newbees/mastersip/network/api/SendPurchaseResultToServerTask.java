package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 3/2/17.
 */
public class SendPurchaseResultToServerTask extends BaseTask<Integer>{

    private final TopPresenter.PurchaseStatus purchaseStatus;
    private final String skuID;
    private final String transection;

    public SendPurchaseResultToServerTask(Context context, String skuID, String transection, TopPresenter.PurchaseStatus purchaseStatus) {
        super(context);
        this.purchaseStatus = purchaseStatus;
        this.skuID = skuID;
        this.transection = transection;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        String date = DateTimeUtils.getServerTime(Calendar.getInstance().getTime());
        return JSONUtils.genParamsToSendPurchaseResult(skuID, transection, purchaseStatus, date);
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.SEND_PURCHASE_RESULT;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Integer didResponse(JSONObject data) throws JSONException {
        return data.getJSONObject(Constant.JSON.DATA).getInt(Constant.JSON.POINT);
    }
}
