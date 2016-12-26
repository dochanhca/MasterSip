package jp.newbees.mastersip.utils;

import android.content.Context;
import android.widget.Toast;

import jp.newbees.mastersip.R;


/**
 * Created by thanglh on 03/12/14.
 */
public class ToastExceptionVolleyHelper {
    private int errorCode;
    private String message;
    private Context mContext;

    public ToastExceptionVolleyHelper(Context context, int errorCode, String message) {
        this.mContext = context;
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private void showShortToast(int resourceId) {
        Toast.makeText(mContext, resourceId, Toast.LENGTH_SHORT).show();
    }

    public boolean showCommonError() {
        switch (errorCode) {
            case Constant.Error.NO_NETWORK:
                showShortToast(R.string.err_no_network);
                return  true;
            case Constant.Error.INVALID_TOKEN:
                showShortToast(R.string.err_invalid_token);
                return true;
            case Constant.Error.REQUEST_TIME_OUT:
                showShortToast(R.string.err_request_time_out);
                return true;
            case Constant.Error.SC_INTERNAL_SERVER_ERROR:
                showShortToast(R.string.err_server_error);
                return true;
            default:
                return false;
        }
    }
}
