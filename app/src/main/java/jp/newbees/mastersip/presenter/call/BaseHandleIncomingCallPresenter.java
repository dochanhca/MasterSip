package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.FlashedEvent;
import jp.newbees.mastersip.event.call.MicrophoneEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.event.call.SpeakerEvent;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CancelCallTask;
import jp.newbees.mastersip.network.api.JoinCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/10/17.
 */

public class BaseHandleIncomingCallPresenter extends BasePresenter {
    private IncomingCallView view;

    public BaseHandleIncomingCallPresenter(Context context, IncomingCallView view) {
        super(context);
        this.view = view;
    }

    public final void acceptCall(String calId) {
        JoinCallTask joinCallTask = new JoinCallTask(context, calId);
        requestToServer(joinCallTask);
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.ACCEPT_CALL));
    }

    public final void rejectCall(String caller, int callType, String calId) {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.REJECT_CALL));
        performCancelCall(caller, callType, calId);
    }

    private void performCancelCall(String caller, int callType, String calId) {
        String callee = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        CancelCallTask cancelCallTask = new CancelCallTask(context, caller, callee, callType, calId);
        requestToServer(cancelCallTask);
    }

    public void endCall(String caller, int callType, String calId) {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.END_CALL));
        performCancelCall(caller, callType, calId);
    }

    public final void enableSpeaker(boolean enable) {
        EventBus.getDefault().post(new SpeakerEvent(enable));
    }

    public final void muteMicrophone(boolean mute) {
        EventBus.getDefault().post(new MicrophoneEvent(mute));
    }

    @Override
    protected void didResponseTask(BaseTask task) {
    }

    @Override

    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onReceivingCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.INCOMING_CONNECTED_CALL:
                handleCallConnected();
                break;
            case ReceivingCallEvent.RELEASE_CALL:
            case ReceivingCallEvent.END_CALL:
                handleCallEnd();
                break;
            case ReceivingCallEvent.FLASHED_CALL:
                handleFlashedCall();
                break;
            default:
                break;
        }
    }

    private void handleFlashedCall() {
        view.onFlashedCall();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCoinChangedEvent(CoinChangedEvent event) {
        view.onCoinChanged(event);
    }

    private void handleCallEnd() {
        view.onCallEnd();
    }

    private void handleCallConnected() {
        view.onCallConnected();
    }

    public void registerEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }

    public void checkFlashCall() {
        EventBus.getDefault().post(new FlashedEvent());
    }

    public interface IncomingCallView {
        void onCallConnected();

        void onCallEnd();

        void onCoinChanged(CoinChangedEvent event);

        void onFlashedCall();
    }
}
