package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.network.api.FilterUserTask;
import jp.newbees.mastersip.presenter.call.BaseCenterOutgoingCallPresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 2/3/17.
 */

public class ProfileDetailPresenter extends BaseCenterOutgoingCallPresenter {
    private ProfileView view;

    public ProfileDetailPresenter(Context context, ProfileView view, OutgoingCallListener outgoingCallListener) {
        super(context, outgoingCallListener);
        this.view = view;
    }

    public interface ProfileView {
        void didLoadMoreUser(Map<String, Object> data);

        void didLoadUserError(int errorCode, String errorMessage);

//        void didCalleeRejectCall(String calleeExtension);

        void didCheckCallError(String errorMessage, int errorCode);
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
        } else if (task instanceof CheckCallTask) {
            handleResponseCheckCall(task);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof FilterUserTask) {
            view.didLoadUserError(errorCode, errorMessage);
        } else if (task instanceof CheckCallTask) {
            view.didCheckCallError(errorMessage, errorCode);
        }
    }
}
