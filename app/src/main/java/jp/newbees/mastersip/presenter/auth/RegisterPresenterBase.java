package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.RegisterVoIPEvent;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.RegisterFCMTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/9/17.
 */

public abstract class RegisterPresenterBase extends BasePresenter {

    public RegisterPresenterBase(Context context) {
        super(context);
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public void loginVoIP() {
        if (LinphoneService.isRunning()) {
            Logger.e(tag, "Linphone Service is ready");
            handleLoginVoIPSuccess();
            return;
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Logger.e(tag, "Start Linphone Service");
        LinphoneService.startLinphone(context);
    }

    /**
     * @param event listener Register VoIP response
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRegisterVoIPEvent(RegisterVoIPEvent event) {
        Logger.e(tag, "onRegisterVoIPEvent receive: " + event.getResponseCode());
        if (event.getResponseCode() == RegisterVoIPEvent.REGISTER_SUCCESS) {
            handleLoginVoIPSuccess();
        } else {
            stopLinphoneService();
            onDidRegisterVoIPError(Constant.Error.VOIP_ERROR, "Error RegisterVoIP");
        }
        EventBus.getDefault().unregister(this);
    }

    private void handleLoginVoIPSuccess() {
        saveLoginState(true);
        onDidRegisterVoIPSuccess();
        sendFCMTokenToServer();
    }

    private void sendFCMTokenToServer() {
        String tokenID = FirebaseInstanceId.getInstance().getToken();
        RegisterFCMTask registerFCMTask = new RegisterFCMTask(context, tokenID);
        requestToServer(registerFCMTask);
    }

    private void stopLinphoneService() {
        LinphoneService.stopLinphone(context);
    }

    private void saveLoginState(boolean loginState) {
        ConfigManager.getInstance().saveLoginFlag(loginState);

    }

    protected abstract void onDidRegisterVoIPSuccess();

    protected abstract void onDidRegisterVoIPError(int errorCode, String errorMessage);
}
