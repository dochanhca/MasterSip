package jp.newbees.mastersip.network.api;

import com.android.volley.Response;

/**
 * Created by vietbq on 12/15/16.
 */

public class TaskManager {
    public final static void requestTask(BaseTask task, final Response.Listener listener, final BaseTask.ErrorListener errorListener){
       task.request(listener,errorListener);
    }
}
