package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SubscribeOnlineNotifyTask;
import jp.newbees.mastersip.network.api.UnsubscribeOnlineNotifyTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 4/20/17.
 */

public class SettingOnlinePresenter extends BasePresenter {

    private SettingOnlineView settingOnlineView;

    public SettingOnlinePresenter(Context context, SettingOnlineView view) {
        super(context);
        this.settingOnlineView = view;
    }

    public interface SettingOnlineView {
        void didOnOnlineNotifySuccess();

        void didOffOnlineNotifySuccess();

        void didSettingOnlineNotifyError(int errorCode, String errorMessage);
    }

    public void onOnlineNotify(String destUserId) {
        SubscribeOnlineNotifyTask task = new SubscribeOnlineNotifyTask(getContext(), destUserId);
        requestToServer(task);
    }

    public void offOnlineNotify(String destUserId) {
        UnsubscribeOnlineNotifyTask task = new UnsubscribeOnlineNotifyTask(getContext(), destUserId);
        requestToServer(task);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SubscribeOnlineNotifyTask) {
            settingOnlineView.didOnOnlineNotifySuccess();
        } else if (task instanceof UnsubscribeOnlineNotifyTask) {
            settingOnlineView.didOffOnlineNotifySuccess();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SubscribeOnlineNotifyTask || task instanceof UnsubscribeOnlineNotifyTask) {
            settingOnlineView.didSettingOnlineNotifyError(errorCode, errorMessage);
        }
    }
}
