package jp.newbees.mastersip.network.api;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class SipError extends VolleyError {

    private int errorCode;
    private String errorMessage = "";

    public SipError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public SipError(VolleyError volleyError) {
        if (volleyError instanceof TimeoutError) {
            this.errorCode = Constant.Error.REQUEST_TIMEOUT;
            this.errorMessage = volleyError.getMessage();
        } else if (isServerProblem(volleyError)) {
            this.errorCode = volleyError.networkResponse.statusCode;
            this.errorMessage = volleyError.getMessage();
        } else if (isNetworkAvailable(volleyError)) {
            this.errorCode = Constant.Error.NO_NETWORK;
            this.errorMessage = volleyError.getMessage();
        } else {
            if (null != volleyError.networkResponse) {
                this.errorCode = volleyError.networkResponse.statusCode;
            } else {
                this.errorCode = Constant.Error.UNKNOWN_ERROR;
            }
            this.errorMessage = volleyError.getClass().toString() +
                    (volleyError.getMessage() == null ? "" : " /\n " + volleyError.getMessage() + " /\n " + volleyError.getCause().getCause().getMessage());
        }
    }

    private static boolean isServerProblem(VolleyError error) {
        return (error instanceof ServerError || error instanceof AuthFailureError);
    }

    private boolean isNetworkAvailable(VolleyError volleyError) {
        if (volleyError instanceof NetworkError || volleyError instanceof NoConnectionError)
            return true;
        else
            return false;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}