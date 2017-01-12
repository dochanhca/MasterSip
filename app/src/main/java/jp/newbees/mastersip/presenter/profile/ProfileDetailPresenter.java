package jp.newbees.mastersip.presenter.profile;

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
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/11/17.
 */

public class ProfileDetailPresenter extends BasePresenter {

    private final ProfileDetailsView view;
    private String callWaitId = null;

    public ProfileDetailPresenter(Context context, ProfileDetailsView view) {
        super(context);
        this.view = view;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof CheckCallTask) {
            handleResponseCheckCall(task);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        Logger.e(TAG, errorMessage);
    }

    private void handleResponseCheckCall(BaseTask task) {
        HashMap<String, Object> result = (HashMap<String, Object>) task.getDataResponse();
        int callType = (int) result.get(CheckCallTask.CALL_TYPE);
        UserItem callee = (UserItem) result.get(CheckCallTask.CALLEE);
        if (callType == Constant.API.VOICE_CALL) {
            makeVoiceCall(callee);
        }
    }

    /**
     * Make a voice call
     * @param callee
     */
    private void makeVoiceCall(UserItem callee) {
        ConfigManager.getInstance().setCurrentCallee(callee);
        String extension = callee.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VOICE_CALL, extension));
    }

    /**
     * Check callee before make a voice call
     * @param callee
     */
    public final void checkVoiceCall(UserItem callee) {
        this.checkCall(callee, Constant.API.VOICE_CALL);
    }

    /**
     * Check callee before make a video call
     * @param userItem
     */
    public final void checkVideoCall(UserItem userItem) {
        String callee = userItem.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CALL, callee));
    }

    /**
     * Check callee before make a video chat call
     * @param userItem
     */
    public final void checkVideoChatCall(UserItem userItem) {
        String callee = userItem.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CHAT_CALL, callee));
    }

    /**
     * Send request check call to server
     * @param callee
     * @param callType
     */
    private void checkCall(UserItem callee, int callType) {
        UserItem caller = getCurrentUserItem();
        CheckCallTask checkCallTask = new CheckCallTask(context, caller, callee, callType, Constant.API.CALL_FROM_OTHER);
        requestToServer(checkCallTask);
    }

    public interface ProfileDetailsView {

    }
}
