package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 12/23/16.
 */

public class SearchPresenter extends BasePresenter{

    public SearchPresenter(Context context) {
        super(context);
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }
}
