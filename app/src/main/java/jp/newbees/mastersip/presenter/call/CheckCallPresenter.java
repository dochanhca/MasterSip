package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import java.util.Map;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 1/6/17.
 */

public class CheckCallPresenter extends BasePresenter {

    private String callWaitId = null;

    private View view;

    public interface View {
        void didCheckCall(Map<String, Object> result);

        void didCheckCallError(int errorCode, String errorMessage);

    }

    public CheckCallPresenter(Context context, View view) {
        super(context);
        this.view = view;
    }


    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.didCheckCallError(errorCode, errorMessage);
    }
}
