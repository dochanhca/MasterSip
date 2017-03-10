package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.event.call.CallEvent;
import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.HangUpForGirlEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 1/24/17.
 */

public abstract class BaseActionCallPresenter extends BasePresenter {

    public BaseActionCallPresenter(Context context) {
        super(context);
    }

    protected abstract void onCalleeRejectCall(BusyCallEvent busyCallEvent);

    protected abstract void onHangUpForGirl();

    protected abstract void onCoinChangedAfterHangUp(CoinChangedEvent event);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBusyCallEvent(BusyCallEvent busyCallEvent) {
        onCalleeRejectCall(busyCallEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHangUpForGirlEvent(HangUpForGirlEvent event) {
        onHangUpForGirl();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangedEvent(CoinChangedEvent event) {
        onCoinChangedAfterHangUp(event);
    }

    protected void handleResponseCheckCall(BaseTask task) {
        HashMap<String, Object> result = (HashMap<String, Object>) task.getDataResponse();
        int callType = (int) result.get(CheckCallTask.CALL_TYPE);
        UserItem callee = (UserItem) result.get(CheckCallTask.CALLEE);

        String callId = (String) result.get(CheckCallTask.CALL_ID);
        ConfigManager.getInstance().setCallId(callId);

        String roomId = (String) result.get(CheckCallTask.ROOM_FREE);
        if (callType == Constant.API.VOICE_CALL) {
            ConfigManager.getInstance().setCurrentCallee(callee, callId);
            makeVoiceCall(roomId);
        }
    }

    /**
     * Make a voice call
     */
    private void makeVoiceCall(String roomId) {
        EventBus.getDefault().post(new CallEvent(Constant.API.VOICE_CALL, roomId));
    }

    /**
     * Check callee before make a voice call
     *
     * @param callee
     */
    public final void checkVoiceCall(UserItem callee) {
        this.checkCall(callee, Constant.API.VOICE_CALL);
    }

    /**
     * Check callee before make a video call
     *
     * @param userItem
     */
    public final void checkVideoCall(UserItem userItem) {
//        String callee = userItem.getSipItem().getExtension();
//        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CALL, callee));
    }

    /**
     * Check callee before make a video chat call.
     *
     * @param userItem
     */
    public final void checkVideoChatCall(UserItem userItem) {
//        String callee = userItem.getSipItem().getExtension();
//        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CHAT_CALL, callee));
    }

    /**
     * Send request check call to server
     *
     * @param callee
     * @param callType
     */
    private void checkCall(UserItem callee, int callType) {
        UserItem caller = getCurrentUserItem();
        CheckCallTask checkCallTask = new CheckCallTask(context, caller, callee, callType, Constant.API.CALL_FROM_OTHER);
        requestToServer(checkCallTask);
    }

    public final void registerEvent() {
        EventBus.getDefault().register(this);
    }

    public final void unRegisterEvent() {
        EventBus.getDefault().unregister(this);
    }
}
