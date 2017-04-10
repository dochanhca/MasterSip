package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import jp.newbees.mastersip.model.FollowItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetListFollowersTask;
import jp.newbees.mastersip.network.api.GetListFollowingTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 4/10/17.
 */

public class FollowPresenter extends BasePresenter {

    private final FollowPresenterView view;

    public interface FollowPresenterView {
        void didLoadListFollow(FollowItem data);
        void didLoadDataError(int errorCode, String errorMessage);
    }

    public FollowPresenter(Context context, FollowPresenterView view) {
        super(context);
        this.view = view;
    }

    public final void getListFollowers() {
        GetListFollowersTask task = new GetListFollowersTask(getContext());
        this.requestToServer(task);
    }

    public void getListFollowing() {
        GetListFollowingTask task = new GetListFollowingTask(getContext());
        this.requestToServer(task);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        view.didLoadListFollow((FollowItem) task.getDataResponse());
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.didLoadDataError(errorCode, errorMessage);
    }
}
