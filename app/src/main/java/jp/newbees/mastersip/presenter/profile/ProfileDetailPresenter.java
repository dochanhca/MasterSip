package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.presenter.call.BaseActionCallPresenter;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/11/17.
 */

public class ProfileDetailPresenter extends BaseActionCallPresenter {

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

    public interface ProfileDetailsView {
    }
}
