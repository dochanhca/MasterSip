package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.ResetPasswordTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 2/15/17.
 */

public class ResetPassPresenter extends BasePresenter {

    private ResetPassView resetPassView;

    public interface ResetPassView {
        void didResetPass();

        void didResetPassError(int errorCode, String errorMessage);
    }

    public ResetPassPresenter(Context context, ResetPassView resetPassView) {
        super(context);
        this.resetPassView = resetPassView;
    }

    public void resetPassword(String email) {
        ResetPasswordTask resetPasswordTask = new ResetPasswordTask(context, email);
        requestToServer(resetPasswordTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof ResetPasswordTask) {
            resetPassView.didResetPass();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof ResetPasswordTask) {
            resetPassView.didResetPassError(errorCode, errorMessage);
        }
    }
}
