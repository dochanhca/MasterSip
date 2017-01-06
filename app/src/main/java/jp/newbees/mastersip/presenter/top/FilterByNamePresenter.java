package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.FilterNameTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by ducpv on 12/30/16.
 */

public class FilterByNamePresenter extends BasePresenter {

    private FilterByNameView view;

    public interface FilterByNameView {
        void didFilterUser(List<UserItem> userItems);

        void didFilterUserError(int errorCode, String errorMessage);

    }

    public FilterByNamePresenter(Context context, FilterByNameView view) {
        super(context);
        this.view = view;
    }

    public void filterUserByName(String name) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        FilterNameTask filterUserTask = new FilterNameTask(context, userItem, 0, name);
        requestToServer(filterUserTask);
    }


    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof FilterNameTask) {
            HashMap<String, Object> data = ((FilterNameTask) task).getDataResponse();
            ArrayList<UserItem> users = (ArrayList<UserItem>) data.get(FilterNameTask.LIST_USER);
            view.didFilterUser(users);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.didFilterUserError(errorCode, errorMessage);
    }
}
