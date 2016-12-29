package jp.newbees.mastersip.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.FilterUserTask;

/**
 * Created by vietbq on 12/21/16.
 */

public class TopPresenter extends BasePresenter {

    private final TopView view;
    private int nextPage;

    public TopPresenter(Context context, TopView topView) {
        super(context);
        this.view = topView;
    }

    public interface TopView {
        public void didFilterData(ArrayList<UserItem> userItems);
        public void didErrorFilterData(int errorCode, String errorMessage);
    }


    public final void requestFilterData(FilterItem filterItem){

    }



    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof FilterUserTask){
           HashMap<String, Object> data = ((FilterUserTask) task).getDataResponse();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }
}
