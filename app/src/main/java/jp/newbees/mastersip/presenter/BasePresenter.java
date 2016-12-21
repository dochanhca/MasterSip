package jp.newbees.mastersip.presenter;

import com.android.volley.Response;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.TaskManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/12/16.
 */

public abstract class BasePresenter {

    protected void requestToServer(final BaseTask task){

        TaskManager.requestTask(task, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                didResponseTask(task);
            }
        }, new BaseTask.ErrorListener() {
            @Override
            public void onError(int errorCode, String errorMessage) {
                if (errorCode == Constant.Error.INVALID_TOKEN) {
                    handleInvalidToken();
                } else {
                    didErrorRequestTask(task,errorCode,errorMessage);
                }
            }
        });
    }

    private void handleInvalidToken(){

    }

    protected abstract void didResponseTask(BaseTask task);

    protected abstract void didErrorRequestTask(BaseTask task,int errorCode,String errorMessage);
}
