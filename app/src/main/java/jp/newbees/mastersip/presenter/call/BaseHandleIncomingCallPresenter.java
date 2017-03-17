package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.FlashedEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.JoinCallTask;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/10/17.
 */

public class BaseHandleIncomingCallPresenter extends BaseHandleCallPresenter {
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
        String callee = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        performCancelCall(caller, callee, callType, calId);
    }

    public void endCall(String caller, int callType, String calId) {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.END_CALL));
        String callee = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        performCancelCall(caller, callee, callType, calId);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        // handle response task
    }

    @Override

    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        // handle error task
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

    private void handleCallEnd() {
        view.onCallEnd();
    }

    private void handleCallConnected() {
        view.onCallConnected();
    }

    public void checkFlashCall() {
        EventBus.getDefault().post(new FlashedEvent());
    }

    @Override
    protected void onCoinChanged(int coin) {
        view.onCoinChanged(coin);
    }

    @Override
    protected void onRunningOutOfCoin() {
        view.onRunningOutOfCoin();
    }

    public interface IncomingCallView {
        void onCallConnected();

        void onCallEnd();

        void onCoinChanged(int coint);

        void onFlashedCall();

        void onRunningOutOfCoin();
    }
}
