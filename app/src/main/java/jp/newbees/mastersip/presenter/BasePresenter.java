package jp.newbees.mastersip.presenter;

import android.content.Context;

import com.android.volley.Response;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.TaskManager;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/12/16.
 */

public abstract class BasePresenter {

    protected Context context;
    protected String TAG;

    public BasePresenter(Context context){
        this.context = context;
        this.TAG = this.getClass().getSimpleName();
    }

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
                    Logger.e(TAG, errorCode + " : " + errorMessage);
                    didErrorRequestTask(task,errorCode,errorMessage);
                }
            }
        });
    }

    private void handleInvalidToken(){

    }

    protected final Context getContext() {
        return context;
    }

    protected UserItem getCurrentUserItem() {
        return ConfigManager.getInstance().getCurrentUser();
    }

    protected abstract void didResponseTask(BaseTask task);

    protected abstract void didErrorRequestTask(BaseTask task,int errorCode,String errorMessage);
}
