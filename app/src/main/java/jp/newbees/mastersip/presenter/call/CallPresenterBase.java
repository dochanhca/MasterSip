package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/10/17.
 */

public class CallPresenterBase extends BasePresenter {
    private CallView view;

    public CallPresenterBase(Context context, CallView view) {
        super(context);
        this.view = view;
    }

    public final void acceptVoiceCall() {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.ACCEPT_CALL));
    }

    public void rejectCall() {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.REJECT_CALL));
    }

    public final void muteMicrophone(boolean mute) {

    }

    public final void enableSpeaker(boolean enable) {

    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }



    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.OUTGOING_CALL:
                onOutgoingCall();
                break;
            case ReceivingCallEvent.CONNECTED_CALL:
            case ReceivingCallEvent.STREAMING_CALL:
                onStartStreamCall();
                break;
            case ReceivingCallEvent.END_CALL:
                onEndCall();
                break;
        }
    }

    private void onEndCall() {

    }

    private void onStartStreamCall() {

    }



    protected void onOutgoingCall() {
        // TO DO
    }


    public interface CallView {
        void incomingVoiceCall(UserItem caller);

        void incomingVideoCall(UserItem caller);

        void incomingVideoChatCall(UserItem caller);
    }
}
