package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/10/17.
 * use for handler incoming for Incoming video, chat, voice activity
 */

public class BaseHandleIncomingCallPresenter extends BaseHandleCallPresenter {
    private IncomingCallView view;

    public BaseHandleIncomingCallPresenter(Context context, IncomingCallView view) {
        super(context, view);
        this.view = view;
    }

    public void startIncomingVideoCall() {
        useFrontCamera(false);
        enableCamera(true);
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
            case ReceivingCallEvent.STREAMING_CALL:
                view.onStreamingConnected();
                break;
            default:
                Logger.e(tag, "Do not handle this event" + receivingCallEvent.getCallEvent());
                break;
        }
    }

    public interface IncomingCallView extends CallView{
        void onStreamingConnected();
    }
}
