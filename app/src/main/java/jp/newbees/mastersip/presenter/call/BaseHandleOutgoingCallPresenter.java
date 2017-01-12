package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 1/11/17.
 */

public class BaseHandleOutgoingCallPresenter extends BasePresenter {
    private OutgoingCallView view;

    public BaseHandleOutgoingCallPresenter(Context context, OutgoingCallView view) {
        super(context);
        this.view = view;
    }


    public void endCall() {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.END_CALL));
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public final void registerEvents() {
        EventBus.getDefault().register(this);
    }

    public final void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onReceivingCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.CONNECTED_CALL:
                handleCallConnected();
                break;
            case ReceivingCallEvent.END_CALL:
                handleCallEnd();
                break;
            default:
                break;
        }
    }

    private void handleCallEnd() {
        view.onCallEnd();
    }

    private void handleCallConnected() {
        view.onCallConnected();
    }


    public interface OutgoingCallView {
        public void onCallConnected();

        public void onCallEnd();
    }
}
