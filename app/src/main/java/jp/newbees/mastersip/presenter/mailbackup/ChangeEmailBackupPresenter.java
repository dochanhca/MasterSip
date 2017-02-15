package jp.newbees.mastersip.presenter.mailbackup;

import android.content.Context;

import jp.newbees.mastersip.model.MailBackupItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.ChangeMailBackupTask;
import jp.newbees.mastersip.network.api.RegisterMailBackupTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by thangit14 on 2/14/17.
 */
public class ChangeEmailBackupPresenter extends BasePresenter{

    public interface ChangeEmailBackupListener {
        void onRegisterEmailBackupSuccess();

        void onRegisterEmailBackupError(int errorCode, String errorMessage);
    }

    private final ChangeEmailBackupListener listener;

    public ChangeEmailBackupPresenter(Context context, ChangeEmailBackupListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof RegisterMailBackupTask) {
            listener.onRegisterEmailBackupSuccess();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof RegisterMailBackupTask) {
            listener.onRegisterEmailBackupError(errorCode,errorMessage);
        }
    }

    public void changeEmail(MailBackupItem mailBackupItem) {
        ChangeMailBackupTask task = new ChangeMailBackupTask(context);
        requestToServer(task);
    }
}
