package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.UpdateProfileTask;

/**
 * Created by vietbq on 12/15/16.
 */

public class UpdateRegisterProfilePresenter extends RegisterPresenterBase {

    private UserItem userItem;

    public interface View {

        void onUpdateRegisterProfileSuccess(UserItem userItem);

        void onUpdateRegisterProfileFailure(int errorCode, String errorMessage);
    }

    private final Context context;
    private final View view;

    public UpdateRegisterProfilePresenter(Context context, View view) {
        super(context);
        this.context = context;
        this.view = view;
    }

    public final void updateRegisterProfile(UserItem userItem) {
        UpdateProfileTask updateProfileTask = new UpdateProfileTask(context, userItem);
        requestToServer(updateProfileTask);
    }


    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof UpdateProfileTask) {
            userItem = ((UpdateProfileTask) task).getDataResponse();
            loginVoIP();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.onUpdateRegisterProfileFailure(errorCode, errorMessage);
    }

    @Override
    protected void onDidRegisterVoIPSuccess() {
        view.onUpdateRegisterProfileSuccess(userItem);
    }

    @Override
    protected void onDidRegisterVoIPError(int errorCode, String errorMessage) {
        view.onUpdateRegisterProfileFailure(errorCode, errorMessage);
    }

}
