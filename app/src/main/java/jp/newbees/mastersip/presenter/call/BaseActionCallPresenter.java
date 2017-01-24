package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import jp.newbees.mastersip.event.call.CallEvent;
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

    protected void handleResponseCheckCall(BaseTask task) {
        HashMap<String, Object> result = (HashMap<String, Object>) task.getDataResponse();
        int callType = (int) result.get(CheckCallTask.CALL_TYPE);
        UserItem callee = (UserItem) result.get(CheckCallTask.CALLEE);

        if (result.containsKey(CheckCallTask.WAITING_CALL_ID)) {
            String waitingCallID = (String) result.get(CheckCallTask.WAITING_CALL_ID);
            ConfigManager.getInstance().setWaitingCallId(waitingCallID);
        }

        if (callType == Constant.API.VOICE_CALL) {
            makeVoiceCall(callee);
        }
    }

    /**
     * Make a voice call
     *
     * @param callee
     */
    private void makeVoiceCall(UserItem callee) {
        ConfigManager.getInstance().setCurrentCallee(callee);
        String extension = callee.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VOICE_CALL, extension));
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
        String callee = userItem.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CALL, callee));
    }

    /**
     * Check callee before make a video chat call00.0.0.
     *
     * @param userItem
     */
    public final void checkVideoChatCall(UserItem userItem) {
        String callee = userItem.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CHAT_CALL, callee));
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
}
