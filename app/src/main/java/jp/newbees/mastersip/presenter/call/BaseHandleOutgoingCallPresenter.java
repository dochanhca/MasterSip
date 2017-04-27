package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.network.api.BaseTask;

/**
 * Created by vietbq on 1/11/17.
 */

public class BaseHandleOutgoingCallPresenter extends BaseHandleCallPresenter {

    public BaseHandleOutgoingCallPresenter(Context context, CallView view) {
        super(context, view);
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
    public void onReceivingOutgoingCallEvent(ReceivingCallEvent event) {
        if (event.getCallEvent() == ReceivingCallEvent.OUTGOING_CONNECTED_CALL) {
            super.handleCallConnected();
        }
    }
}
