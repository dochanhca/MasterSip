package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.ChangePasswordTask;
import jp.newbees.mastersip.network.api.LoginEmailTask;

/**
 * Created by ducpv on 2/15/17.
 */

public class ChangePassPresenter extends RegisterPresenterBase {

    private ChangePassView view;
    private String password;
    private String email;

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
        this.email = email;
        this.password = newPass;
        requestToServer(changePasswordTask);
    }

    public void loginByEmail(String email, String pass) {
        LoginEmailTask loginEmailTask = new LoginEmailTask(context, email, pass);
        requestToServer(loginEmailTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof ChangePasswordTask) {
            loginByEmail(email, password);
        } else if (task instanceof LoginEmailTask) {
            loginVoIP();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof ChangePasswordTask || task instanceof LoginEmailTask) {
            view.didChangePassError(errorCode, errorMessage);
        }
    }

    @Override
    protected void onDidRegisterVoIPSuccess() {
        view.didChangePass();
    }

    @Override
    protected void onDidRegisterVoIPError(int errorCode, String errorMessage) {
        view.didChangePassError(errorCode, errorMessage);
    }
}
