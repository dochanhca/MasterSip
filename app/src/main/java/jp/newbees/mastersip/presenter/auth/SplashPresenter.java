package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

/**
 * Created by ducpv on 2/17/17.
 */

public class SplashPresenter extends RegisterPresenterBase {

    private SplashView splashView;

    public interface SplashView {
        void didLoginVoIP();

        void didLoginVoIPError(String errorMessage);
    }

    public SplashPresenter(Context context, SplashView splashView) {
        super(context);
        this.splashView = splashView;
    }

    @Override
    protected void onDidRegisterVoIPSuccess() {
        splashView.didLoginVoIP();
    }

    @Override
    protected void onDidRegisterVoIPError(int errorCode, String errorMessage) {
        splashView.didLoginVoIPError(errorMessage);
    }
}
