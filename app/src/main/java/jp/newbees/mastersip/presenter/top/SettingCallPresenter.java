package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.UpdateSettingCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/25/17.
 */

public class SettingCallPresenter extends BasePresenter {

    private final SettingCallView view;

    public interface SettingCallView {
        void didUpdateSettingCall();

        void didUpdateSettingCallFailure(int errorCode, String messageError);
    }

    public SettingCallPresenter(Context context, SettingCallView view) {
        super(context);
        this.view = view;
    }

    public void requestChangeCallSetting(SettingItem settingCall) {
        UpdateSettingCallTask updateSettingCallTask = new UpdateSettingCallTask(getContext(), settingCall);
        requestToServer(updateSettingCallTask);
    }

    private void saveSettingCall(SettingItem settingItem) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        userItem.setSettings(settingItem);
        ConfigManager.getInstance().saveUser(userItem);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof UpdateSettingCallTask) {
            SettingItem settingItem = ((UpdateSettingCallTask) task).getDataResponse();
            this.saveSettingCall(settingItem);
            view.didUpdateSettingCall();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof UpdateSettingCallTask) {
            view.didUpdateSettingCallFailure(errorCode, errorMessage);
        }
    }
}
