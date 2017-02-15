package jp.newbees.mastersip.presenter.mailbackup;

import android.content.Context;

import jp.newbees.mastersip.model.MailBackupItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.RegisterMailBackupTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by thangit14 on 2/14/17.
 */
public class RegisterEmailBackupPresenter extends BasePresenter {

    public interface RegisterEmailBackupListener {
        void onRegisterEmailBackupSuccess();

        void onRegisterEmailBackupError(int errorCode, String errorMessage);
    }

    private final RegisterEmailBackupListener listener;

    public RegisterEmailBackupPresenter(Context context, RegisterEmailBackupListener listener) {
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

    public void registerEmail(MailBackupItem mailBackupItem) {
        RegisterMailBackupTask registerMailBackupTask = new RegisterMailBackupTask(context, mailBackupItem);
        requestToServer(registerMailBackupTask);
    }
}
