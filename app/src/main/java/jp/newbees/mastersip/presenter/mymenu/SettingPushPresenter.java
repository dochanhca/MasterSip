package jp.newbees.mastersip.presenter.mymenu;

import android.content.Context;

import jp.newbees.mastersip.model.SettingPushItem;
import jp.newbees.mastersip.network.GetSettingPushTask;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SettingPushTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 5/11/17.
 */

public class SettingPushPresenter extends BasePresenter {

    private SettingPushView settingPushView;

    public interface SettingPushView {
        void didGetSettingPush(SettingPushItem settingPushItem);

        void didGetSettingPushError(int errorCode, String errorMessage);

        void didSettingPush();

        void didSettingPushError(int errorCode, String errorMessage);
    }

    public SettingPushPresenter(Context context, SettingPushView settingPushView) {
        super(context);
        this.settingPushView = settingPushView;
    }

    public void getSettingPush() {
        GetSettingPushTask getSettingPushTask = new GetSettingPushTask(getContext());
        requestToServer(getSettingPushTask);
    }

    public void settingPush(SettingPushItem settingPushItem) {
        SettingPushTask settingPushTask = new SettingPushTask(getContext(), settingPushItem);
        requestToServer(settingPushTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetSettingPushTask) {
            settingPushView.didGetSettingPush(((GetSettingPushTask) task).getDataResponse());
        } else if (task instanceof SettingPushTask) {
            settingPushView.didSettingPush();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetSettingPushTask) {
            settingPushView.didGetSettingPushError(errorCode, errorMessage);
        } else if (task instanceof SettingPushTask) {
            settingPushView.didSettingPushError(errorCode, errorMessage);
        }
    }
}
