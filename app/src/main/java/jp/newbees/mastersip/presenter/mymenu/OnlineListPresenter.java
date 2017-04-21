package jp.newbees.mastersip.presenter.mymenu;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

import jp.newbees.mastersip.event.SettingOnlineChangedEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetOnlineListTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 4/21/17.
 */

public class OnlineListPresenter extends BasePresenter {

    private OnlineListView onlineListView;
    private boolean isLoadMore;
    private int next = 0;

    public interface OnlineListView {
        void didLoadOnlineList(List<UserItem> userItems);

        void didLoadMoreOnlineList(List<UserItem> userItems);

        void didLoadOnlineListError(int errorCode, String errorMessage);
    }

    public OnlineListPresenter(Context context, OnlineListView onlineListView) {
        super(context);
        this.onlineListView = onlineListView;
    }

    @Subscribe(sticky =  true)
    public void onSettingOnlineChangedEvent(SettingOnlineChangedEvent event) {
        loadData();
    }

    public final void registerEvent() {
        EventBus.getDefault().register(this);
    }

    public final void unRegisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    public void loadData() {
        next = 0;
        isLoadMore = false;
        loadOnlineList();
    }

    public void loadMoreData() {
        isLoadMore = true;
        loadOnlineList();
    }

    private void loadOnlineList() {
        GetOnlineListTask getOnlineListTask = new GetOnlineListTask(context, next);
        requestToServer(getOnlineListTask);
    }

    private void getData(GetOnlineListTask task) {
        Map<String, Object> result = task.getDataResponse();
        this.next = (int) result.get(GetOnlineListTask.NEXT);
        List<UserItem> userItems = (List<UserItem>) result.get(GetOnlineListTask.LIST_USER);
        if (isLoadMore) {
            onlineListView.didLoadMoreOnlineList(userItems);
        } else {
            onlineListView.didLoadOnlineList(userItems);
        }
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetOnlineListTask) {
            getData((GetOnlineListTask) task);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetOnlineListTask) {
            onlineListView.didLoadOnlineListError(errorCode, errorMessage);
        }
    }
}
