package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.FilterUserTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/23/16.
 */

public class SearchPresenter extends BasePresenter{
    private String nextPage = "0";

    private SearchView view;

    public interface SearchView {
        void didFilterUser(ArrayList<UserItem> userItems);

        void didFilterUserError(int errorCode, String errorMessage);
    }

    public SearchPresenter(Context context, SearchView searchView) {
        super(context);
        this.view = searchView;
    }

    public void filterUser(){
        FilterItem filterItem = ConfigManager.getInstance().getFilterUser();
        FilterUserTask filterUserTask = new FilterUserTask(context, filterItem, nextPage, getCurrentUserItem());
        requestToServer(filterUserTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof FilterUserTask) {
            HashMap<String, Object> data = ((FilterUserTask) task).getDataResponse();
            ArrayList<UserItem> users = (ArrayList<UserItem>) data.get(FilterUserTask.LIST_USER);
            nextPage = (String) data.get(FilterUserTask.NEXT_PAGE);

            view.didFilterUser(users);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        Logger.e("SearchPresenter", errorCode + " - " + errorMessage);
    }
}
