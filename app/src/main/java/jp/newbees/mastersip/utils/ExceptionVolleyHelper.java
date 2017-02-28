package jp.newbees.mastersip.utils;

import android.content.Context;
import android.widget.Toast;

import jp.newbees.mastersip.R;


/**
 * Created by thanglh on 03/12/14.
 */
public class ExceptionVolleyHelper {
    private int errorCode;
    private String message;
    private Context mContext;

    public ExceptionVolleyHelper(Context context, int errorCode, String message) {
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
            case Constant.Error.INVALID_TOKEN:
                showShortToast(R.string.err_invalid_token);
                return true;
            case Constant.Error.REQUEST_TIME_OUT:
                showShortToast(R.string.err_request_time_out);
                return true;
            case Constant.Error.SC_INTERNAL_SERVER_ERROR:
                showShortToast(R.string.err_server_error);
                return true;
            case Constant.Error.WRONG_ORDER_BY_CODE:
                showShortToast(R.string.err_wrong_order_by_code);
                return true;
            case Constant.Error.EMAIL_IS_NOT_EXIST:
                showShortToast(R.string.err_mail_is_not_registered);
                return true;
            case Constant.Error.EMAIL_OR_PASS_IS_WRONG:
                showShortToast(R.string.err_wrong_email_or_pass);
                return true;
            case Constant.Error.RESET_CODE_IS_NOT_EXIST:
            case Constant.Error.RESET_CODE_IS_NOT_MATCH:
            case Constant.Error.RESET_CODE_INVALID:
            case Constant.Error.WRONG_CODE_RESET_PASS:
                showShortToast(R.string.err_invalid_code);
                return true;
            default:
                return false;
        }
    }
}
