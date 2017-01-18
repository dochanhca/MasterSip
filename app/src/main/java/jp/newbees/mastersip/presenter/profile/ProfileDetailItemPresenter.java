package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetProfileDetailTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailItemPresenter extends BasePresenter {

    private final ProfileDetailItemView view;

    public interface ProfileDetailItemView {
        void didGetProfileDetail(UserItem userItem);

        void didGetProfileDetailError(String errorMessage, int errorCode);
    }

    public ProfileDetailItemPresenter(Context context, ProfileDetailItemView view) {
        super(context);
        this.view = view;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetProfileDetailTask) {
            UserItem userItem = (UserItem) task.getDataResponse();
            view.didGetProfileDetail(userItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetProfileDetailTask) {
            view.didGetProfileDetailError(errorMessage, errorCode);
        }
    }

    public void getProfileDetail(String userId) {
        GetProfileDetailTask getProfileDetailTask = new GetProfileDetailTask(context, userId);
        requestToServer(getProfileDetailTask);
    }
}
