package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import java.util.Map;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Constant;

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

    public void checkCall(String caller, String receiver, int callType, int kind) {
//        CheckCallTask checkCallTask = new CheckCallTask(context, caller, receiver, callType, kind,
//                callWaitId);
//        requestToServer(checkCallTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof CheckCallTask) {
            Map<String, Object> data = (Map<String, Object>) task.getDataResponse();

            if (data.get(Constant.JSON.K_CALL_WAIT_ID) != null) {
                this.callWaitId = (String) data.get(Constant.JSON.K_CALL_WAIT_ID);
            }
            view.didCheckCall(data);

        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.didCheckCallError(errorCode, errorMessage);
    }
}
