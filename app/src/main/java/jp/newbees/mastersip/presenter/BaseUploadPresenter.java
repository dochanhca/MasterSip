package jp.newbees.mastersip.presenter;

import com.android.volley.Response;

import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.TaskManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 12/21/16.
 */

public abstract class BaseUploadPresenter {

    protected void requestToServer(final BaseUploadTask task){

        TaskManager.requestUploadTask(task, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                didResponseTask(task);
            }
        }, new BaseUploadTask.ErrorListener() {
            @Override
            public void onErrorListener(int errorCode, String errorMessage) {
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

    protected abstract void didResponseTask(BaseUploadTask task);

    protected abstract void didErrorRequestTask(BaseUploadTask task,int errorCode,String errorMessage);
}
