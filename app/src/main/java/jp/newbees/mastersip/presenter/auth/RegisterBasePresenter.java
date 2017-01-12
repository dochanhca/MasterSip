package jp.newbees.mastersip.presenter.auth;

import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.eventbus.RegisterVoIPEvent;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/9/17.
 */

public abstract class RegisterBasePresenter extends BasePresenter {
    public RegisterBasePresenter(Context context) {
        super(context);
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public void loginVoIP() {
        EventBus.getDefault().register(this);
        Intent intent = new Intent(context,LinphoneService.class);
        context.startService(intent);
    }

    /**
     * @param event listener Register VoIP response
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRegisterVoIPEvent(RegisterVoIPEvent event) {
        Logger.e(TAG, "onRegisterVoIPEvent receive: " + event.getResponseCode());
        if (event.getResponseCode() == RegisterVoIPEvent.REGISTER_SUCCESS) {
            saveLoginState(true);
            onDidRegisterVoIPSuccess();
        } else {
            saveLoginState(false);
            onDidRegisterVoIPError(Constant.Error.VOIP_ERROR,"Error RegisterVoIP");
        }
        EventBus.getDefault().unregister(this);
    }

    private void saveLoginState(boolean loginState) {
        ConfigManager.getInstance().saveLoginFlag(loginState);

    }

    protected abstract void onDidRegisterVoIPSuccess();
    protected abstract void onDidRegisterVoIPError(int errorCode, String errorMessage);
}
