package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.MicrophoneEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.event.call.SpeakerEvent;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 1/10/17.
 */

public class BaseHandleCallPresenter extends BasePresenter {
    private HandleCallView view;

    public BaseHandleCallPresenter(Context context, HandleCallView view) {
        super(context);
        this.view = view;
    }

    public final void acceptCall(){
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.ACCEPT_CALL));
    }

    public final void rejectCall() {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.REJECT_CALL));
    }

    public void endCall() {
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.END_CALL));
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

    public void registerEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }

    public interface HandleCallView {
        public void onCallConnected();
        public void onCallEnd();
    }
}
