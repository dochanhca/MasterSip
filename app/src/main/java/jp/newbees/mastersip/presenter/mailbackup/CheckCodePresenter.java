package jp.newbees.mastersip.presenter.mailbackup;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCodeTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by thangit14 on 2/14/17.
 */
public class CheckCodePresenter extends BasePresenter {
    private CheckCodeListener checkCodeListener;

    public interface CheckCodeListener {
        void onCheckCodeSuccessful();

        void onCheckCodeError(int errorCode, String errorMessage);

    }

    public CheckCodePresenter(Context context, CheckCodeListener checkCodeListener) {
        super(context);
        this.checkCodeListener = checkCodeListener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof CheckCodeTask) {
            checkCodeListener.onCheckCodeSuccessful();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof CheckCodeTask) {
            checkCodeListener.onCheckCodeError(errorCode, errorMessage);
        }
    }

    public void checkCode(String code) {
        CheckCodeTask checkCodeTask = new CheckCodeTask(context, code);
        requestToServer(checkCodeTask);
    }
}
