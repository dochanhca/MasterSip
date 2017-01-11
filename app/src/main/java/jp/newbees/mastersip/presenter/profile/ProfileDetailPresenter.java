package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import jp.newbees.mastersip.event.call.CallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/11/17.
 */

public class ProfileDetailPresenter extends BasePresenter {

    private final ProfileDetailsView view;

    public ProfileDetailPresenter(Context context, ProfileDetailsView view) {
        super(context);
        this.view = view;
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public final void voiceCall(UserItem userItem) {
        String callee = userItem.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VOICE_CALL, callee));
    }

    public final void videoCall(UserItem userItem) {
        String callee = userItem.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CALL, callee));
    }

    public final void videoChatCall(UserItem userItem) {
        String callee = userItem.getSipItem().getExtension();
        EventBus.getDefault().post(new CallEvent(Constant.API.VIDEO_CHAT, callee));
    }

    public interface ProfileDetailsView {

    }
}
