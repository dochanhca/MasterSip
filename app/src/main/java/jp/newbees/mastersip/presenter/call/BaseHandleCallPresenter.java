package jp.newbees.mastersip.presenter.call;

import android.content.Context;
import android.view.SurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.RunOutOfCoinEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.CancelCallTask;
import jp.newbees.mastersip.network.api.JoinCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by ducpv on 3/10/17.
 */

public abstract class BaseHandleCallPresenter extends BasePresenter {

    private CallView view;

    public BaseHandleCallPresenter(Context context, CallView callView) {
        super(context);
        this.view = callView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangedEvent(CoinChangedEvent event) {
        view.onCoinChanged(event.getCoin());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRunOutOfCoinEvent(RunOutOfCoinEvent event) {
        view.onRunningOutOfCoin();
    }

    protected void performCancelCall(String caller, String callee, int callType, String calId) {
        CancelCallTask cancelCallTask = new CancelCallTask(context, caller, callee, callType, calId);
        requestToServer(cancelCallTask);
    }

    public final void enableSpeaker(boolean enable) {
        LinphoneHandler.getInstance().enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        LinphoneHandler.getInstance().muteMicrophone(mute);
    }

    public final void switchCamera(SurfaceView mCaptureView) {
        LinphoneHandler.getInstance().switchCamera(mCaptureView);
    }

    public final void useFrontCamera() {
        LinphoneHandler.getInstance().userFrontCamera();
    }

    public final void enableCamera(boolean enable) {
        LinphoneHandler.getInstance().enableVideo(enable);
    }

    public final void acceptCall(String calId) throws LinphoneCoreException {
        JoinCallTask joinCallTask = new JoinCallTask(context, calId);
        requestToServer(joinCallTask);
        LinphoneHandler.getInstance().acceptCall();
    }

    public final void rejectCall(String caller, int callType, String calId) {
        LinphoneHandler.getInstance().rejectCall();
        String callee = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        performCancelCall(caller, callee, callType, calId);
    }

    public void endCall(String caller, int callType, String calId) {
        LinphoneHandler.getInstance().endCall();
        String callee = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        performCancelCall(caller, callee, callType, calId);
    }

    public void endCall(UserItem callee, int callType) {
        LinphoneHandler.getInstance().endCall();
        String caller = getCurrentUserItem().getSipItem().getExtension();
        String callID = ConfigManager.getInstance().getCallId();
        performCancelCall(caller, callee.getSipItem().getExtension(), callType, callID);
    }

    protected void handleCallEnd() {
        view.onCallEnd();
    }

    protected void handleCallConnected() {
        view.onCallConnected();
    }

    public void registerEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }

    public interface CallView {
        void onCallConnected();

        void onCallEnd();

        void onCoinChanged(int coin);

        void onRunningOutOfCoin();
    }
}
