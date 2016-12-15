package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.RegisterTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 12/13/16.
 */

public class RegisterPresenter extends BasePresenter {

    public interface RegisterView {
        /**
         * Callback when register User VoIP OK
         * @param userItem
         */
        public void onRegistered(UserItem userItem);

        /**
         * Failure when requester VoIP Account
         * @param errorCode
         * @param errorMessage
         */
        public void onRegisterFailure(int errorCode, String errorMessage);


    }

    private final Context context;
    private final RegisterView view;

    public RegisterPresenter(Context context, RegisterView view){
        this.context = context;
        this.view = view;
    }

    public final void registerUser(UserItem userItem){
        RegisterTask registerTask = new RegisterTask(context, userItem);
        requestToServer(registerTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof RegisterTask){
            UserItem userItem = (UserItem) task.getDataResponse();
            view.onRegistered(userItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.onRegisterFailure(errorCode,errorMessage);
    }

    public final void saveUser(UserItem userItem){

    }

}
