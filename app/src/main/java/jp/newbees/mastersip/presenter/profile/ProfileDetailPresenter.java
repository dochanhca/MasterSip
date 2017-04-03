package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.FilterUserTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by ducpv on 2/3/17.
 */

public class ProfileDetailPresenter extends BasePresenter {
    private ProfileView view;

    public ProfileDetailPresenter(Context context, ProfileView view) {
        super(context);
        this.view = view;
    }

    public interface ProfileView {
        void didLoadMoreUser(Map<String, Object> data);

        void didLoadUserError(int errorCode, String errorMessage);
    }

    public void loadMoreUser(String nextPage, int typeSearch) {
        FilterItem filterItem = ConfigManager.getInstance().getFilterUser();
        filterItem.setFilterType(typeSearch);
        FilterUserTask filterUserTask = new FilterUserTask(context, filterItem, nextPage, getCurrentUserItem());
        requestToServer(filterUserTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof FilterUserTask) {
            HashMap<String, Object> data = ((FilterUserTask) task).getDataResponse();
            view.didLoadMoreUser(data);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof FilterUserTask) {
            view.didLoadUserError(errorCode, errorMessage);
        }
    }
}
