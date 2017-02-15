package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.LoginEmailTask;

/**
 * Created by vietbq on 12/12/16.
 */

public class LoginPresenter extends RegisterPresenterBase {

    private LoginView loginView;

    public interface LoginView {
        void didLoginVoIP();

        void didLoginError(int errorCode, String errorMessage);
    }

    public LoginPresenter(Context context, LoginView loginView) {
        super(context);
        this.loginView = loginView;
    }

    public void loginByEmail(String email, String pass) {
        LoginEmailTask loginEmailTask = new LoginEmailTask(context, email, pass);
        requestToServer(loginEmailTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof LoginEmailTask) {
            this.loginVoIP();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof LoginEmailTask) {
            loginView.didLoginError(errorCode, errorMessage);
        }
    }

    @Override
    protected void onDidRegisterVoIPSuccess() {
        loginView.didLoginVoIP();
    }

    @Override
    protected void onDidRegisterVoIPError(int errorCode, String errorMessage) {
        loginView.didLoginError(errorCode, errorMessage);
    }
}
