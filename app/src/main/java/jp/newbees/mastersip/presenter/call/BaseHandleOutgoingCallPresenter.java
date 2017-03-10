package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/11/17.
 */

public class BaseHandleOutgoingCallPresenter extends BaseHandleCallPresenter {
    private OutgoingCallView view;

    public BaseHandleOutgoingCallPresenter(Context context, OutgoingCallView view) {
        super(context);
        this.view = view;
    }

    public void endCall(UserItem callee, int callType) {
        String caller = getCurrentUserItem().getSipItem().getExtension();
        String callID = ConfigManager.getInstance().getCallId();
        performCancelCall(caller, callee.getSipItem().getExtension(), callType, callID);
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.END_CALL));
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        // handle response task
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        // handle error task
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivingCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.OUTGOING_CONNECTED_CALL:
                handleCallConnected();
                break;
            case ReceivingCallEvent.RELEASE_CALL:
            case ReceivingCallEvent.END_CALL:
                handleCallEnd();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCoinChanged(int coin) {
        view.onCoinChanged(coin);
    }

    private void handleCallEnd() {
        view.onCallEnd();
    }

    private void handleCallConnected() {
        view.onCallConnected();
    }

    public interface OutgoingCallView {
        void onCallConnected();

        void onCallEnd();

        void onCoinChanged(int coint);
    }
}
