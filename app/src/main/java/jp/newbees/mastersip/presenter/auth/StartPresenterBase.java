package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;

/**
 * Created by vietbq on 1/9/17.
 */

public class StartPresenterBase extends RegisterPresenterBase {
    private StartView startView;

    public interface StartView {
        void didLoginVoIP();
        void didErrorVoIP(String errorMessage);
    }

    public StartPresenterBase(Context context, StartView startView) {
        super(context);
        this.startView = startView;
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    @Override
    protected void onDidRegisterVoIPSuccess() {
        this.startView.didLoginVoIP();
    }

    @Override
    protected void onDidRegisterVoIPError(int errorCode, String errorMessage) {

    }

}
