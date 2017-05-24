package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 5/24/17.
 */

public class ReportTask extends BaseTask<Void> {

    private String desUserId;
    private int type;
    private int reportDetailId;
    private String imagePath;

    public ReportTask(Context context, String desUserId, int type, int reportDetailId, String imagePath) {
        super(context);
        this.desUserId = desUserId;
        this.type = type;
        this.reportDetailId = reportDetailId;
        this.imagePath = imagePath;
    }

    public ReportTask(Context context, String desUserId, int type, int reportDetailId) {
        super(context);
        this.desUserId = desUserId;
        this.type = type;
        this.reportDetailId = reportDetailId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.REPORTEE_ID, desUserId);
        jParam.put(Constant.JSON.REPORT_TYPE, type);
        jParam.put(Constant.JSON.REPORT_DETAIL_ID, reportDetailId);
        if (type != Constant.API.REPORT_PROFILE)
            jParam.put(Constant.JSON.IMAGE_PATH, imagePath);

        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.REPORT;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Void didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
