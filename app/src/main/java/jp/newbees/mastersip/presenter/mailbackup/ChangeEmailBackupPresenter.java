package jp.newbees.mastersip.presenter.mailbackup;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.model.EmailBackupItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.ChangeEmailBackupTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 2/14/17.
 */
public class ChangeEmailBackupPresenter extends BasePresenter {

    public interface ChangeEmailBackupListener {
        void onChangeEmailBackupSuccess();

        void onChangeEmailBackupError(int errorCode, String errorMessage);
    }

    private final ChangeEmailBackupListener listener;

    public ChangeEmailBackupPresenter(Context context, ChangeEmailBackupListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof ChangeEmailBackupTask) {
            listener.onChangeEmailBackupSuccess();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof ChangeEmailBackupTask) {
            listener.onChangeEmailBackupError(errorCode, errorMessage);
        }
    }

    public void changeEmail(EmailBackupItem emailBackupItem) {
        ChangeEmailBackupTask task = new ChangeEmailBackupTask(context, emailBackupItem);
        requestToServer(task);
    }

    public String validateParam(TextView edtOldPass, HiraginoEditText edtEmail, HiraginoEditText edtPassword, HiraginoEditText edtRePassword) {
        String message;
        if (!Utils.isValidPassword(edtOldPass.getText().toString())) {
            message = getContext().getString(R.string.wrong_password_format);
        } else {
            message = Utils.validateEmailAndPassword(getContext()
                    , edtEmail.getText().toString()
                    , edtPassword.getText().toString()
                    , edtRePassword.getText().toString());
        }
        return message;
    }

    public void backToMyMenuFragment(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i <= count - 1; i++) {
                fragmentManager.popBackStackImmediate();
            }
        }
    }
}
