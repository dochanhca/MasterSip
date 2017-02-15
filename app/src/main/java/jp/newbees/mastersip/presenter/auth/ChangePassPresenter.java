package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.ChangePasswordTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 2/15/17.
 */

public class ChangePassPresenter extends BasePresenter {

    private ChangePassView view;

    public interface ChangePassView {
        void didChangePass();

        void didChangePassError(int errorCode, String errorMessage);
    }

    public ChangePassPresenter(Context context, ChangePassView view) {
        super(context);
        this.view = view;
    }

    public void changePass(String email, String newPass, String code) {
        ChangePasswordTask changePasswordTask = new ChangePasswordTask(context, email, newPass, code);
        requestToServer(changePasswordTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof ChangePasswordTask) {
            view.didChangePass();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof ChangePasswordTask) {
            view.didChangePassError(errorCode, errorMessage);
        }
    }
}
