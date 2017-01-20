package jp.newbees.mastersip.presenter.top;

import android.content.Context;
import android.content.Intent;

import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.LogoutTask;
import jp.newbees.mastersip.network.api.MyProfileTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/19/17.
 */

public class MyMenuPresenter extends BasePresenter {

    private final MyMenuView menuView;

    public MyMenuPresenter(Context context, MyMenuView menuView) {
        super(context);
        this.menuView = menuView;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof LogoutTask){
            menuView.didLogout();
        }else if(task instanceof MyProfileTask) {
            UserItem userItem = ((MyProfileTask) task).getDataResponse();
            ConfigManager.getInstance().saveUser(userItem);
            menuView.didLoadMyProfile(userItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof LogoutTask) {
            menuView.didLogout();
        }
    }

    public final void requestMyMenuInfo(){
        MyProfileTask myProfileTask = new MyProfileTask(context);
        requestToServer(myProfileTask);
    }

    /**
     * Do not change this order to logout
     * 1. Stop Linphone Core
     * 2. Request Logout
     * 3. Remove all cached
     */
    public void requestLogout() {
        stopLinphoneService();
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        LogoutTask logoutTask = new LogoutTask(getContext(),userItem);
        requestToServer(logoutTask);
        ConfigManager.getInstance().resetSettings();
    }

    private void stopLinphoneService() {
        Intent intent = new Intent(getContext(), LinphoneService.class);
        getContext().stopService(intent);
    }

    public interface MyMenuView {
        void didLogout();

        void didLoadMyProfile(UserItem userItem);
    }
}
