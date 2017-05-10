package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.DeleteImageTask;
import jp.newbees.mastersip.network.api.UpdateProfileTask;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 12/15/16.
 */

public class UpdateRegisterProfilePresenter extends RegisterPresenterBase {

    private UserItem userItem;
    private final Context context;
    private final View view;
    private boolean needLoginVoIP;

    public interface View {

        void onUpdateRegisterProfileSuccess(UserItem userItem);

        void onUpdateRegisterProfileFailure(int errorCode, String errorMessage);

        void onDeleteAvatarSuccess();

        void onDeleteAvatarFailure(int errorCode, String errorMessage);
    }

    public UpdateRegisterProfilePresenter(Context context, View view) {
        super(context);
        this.context = context;
        this.view = view;
    }

    public final void updateRegisterProfile(UserItem userItem, boolean needLoginVoIP) {
        this.needLoginVoIP = needLoginVoIP;
        UpdateProfileTask updateProfileTask = new UpdateProfileTask(context, userItem);
        requestToServer(updateProfileTask);
    }

    public void deleteAvatar() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        DeleteImageTask deleteImageTask = new DeleteImageTask(getContext(), userItem, userItem.getAvatarItem());
        requestToServer(deleteImageTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof UpdateProfileTask) {
            userItem = ((UpdateProfileTask) task).getDataResponse();
            if (needLoginVoIP) {
                loginVoIP();
            } else {
                view.onUpdateRegisterProfileSuccess(userItem);
            }
        } else if (task instanceof DeleteImageTask) {
            view.onDeleteAvatarSuccess();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof  UpdateProfileTask) {
            view.onUpdateRegisterProfileFailure(errorCode, errorMessage);
        } else if (task instanceof DeleteImageTask) {
            view.onDeleteAvatarSuccess();
        }
    }

    @Override
    protected void onDidRegisterVoIPSuccess() {
        this.saveInfoUser();
        view.onUpdateRegisterProfileSuccess(userItem);
    }

    @Override
    protected void onDidRegisterVoIPError(int errorCode, String errorMessage) {
        view.onUpdateRegisterProfileFailure(errorCode, errorMessage);
    }

    private void saveInfoUser(){
        ConfigManager.getInstance().saveUser(userItem);
    }
}
