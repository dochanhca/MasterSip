package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.MicrophoneEvent;
import jp.newbees.mastersip.event.call.SpeakerEvent;
import jp.newbees.mastersip.event.call.VideoCallEvent;
import jp.newbees.mastersip.network.api.CancelCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 3/10/17.
 */

public abstract class BaseHandleCallPresenter extends BasePresenter {

    public BaseHandleCallPresenter(Context context) {
        super(context);
    }

    protected abstract void onCoinChanged(int coin);

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCoinChangedEvent(CoinChangedEvent event) {
        onCoinChanged(event.getCoin());
    }

    protected void performCancelCall(String caller, String callee, int callType, String calId) {
        CancelCallTask cancelCallTask = new CancelCallTask(context, caller, callee, callType, calId);
        requestToServer(cancelCallTask);
    }

    public final void enableSpeaker(boolean enable) {
        EventBus.getDefault().post(new SpeakerEvent(enable));
    }

    public final void muteMicrophone(boolean mute) {
        EventBus.getDefault().post(new MicrophoneEvent(mute));
    }

    public final void switchCamera() {
        EventBus.getDefault().post(new VideoCallEvent(VideoCallEvent.VideoEvent.SWITCH_CAMERA));
    }

    public final void userFrontCamera() {
        EventBus.getDefault().post(new VideoCallEvent(VideoCallEvent.VideoEvent.SWITCH_CAMERA));
    }

    public void registerEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }
}
