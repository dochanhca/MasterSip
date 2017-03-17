package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.MicrophoneEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.event.call.RunOutOfCoinEvent;
import jp.newbees.mastersip.event.call.SpeakerEvent;
import jp.newbees.mastersip.event.call.VideoCallEvent;
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
        view.onCoinChanged(event.getTotal());
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
        EventBus.getDefault().post(new SpeakerEvent(enable));
    }

    public final void muteMicrophone(boolean mute) {
        EventBus.getDefault().post(new MicrophoneEvent(mute));
    }

    public final void switchCamera() {
        EventBus.getDefault().post(new VideoCallEvent(VideoCallEvent.VideoEvent.SWITCH_CAMERA));
    }

    public final void useFrontCamera() {
        EventBus.getDefault().post(new VideoCallEvent(VideoCallEvent.VideoEvent.USE_FRONT_CAMERA));
    }

    public final void enableCamera(boolean enable) {
        EventBus.getDefault().post(new VideoCallEvent(enable ?
                VideoCallEvent.VideoEvent.ENABLE_CAMERA : VideoCallEvent.VideoEvent.DISABLE_CAMERA));
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

    public void endCall(UserItem callee, int callType) {
        String caller = getCurrentUserItem().getSipItem().getExtension();
        String callID = ConfigManager.getInstance().getCallId();
        performCancelCall(caller, callee.getSipItem().getExtension(), callType, callID);
        EventBus.getDefault().post(new SendingCallEvent(SendingCallEvent.END_CALL));
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
