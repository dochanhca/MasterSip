package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import java.util.Map;

import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.FilterUserTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 12/23/16.
 */

public class FilterUserPresenter extends BasePresenter {
    private String nextPage = "0";
    private boolean isLoadMore = false;

    private SearchView view;

    public interface SearchView {
        void didFilterUser(Map<String, Object> data);

        void didFilterUserError(int errorCode, String errorMessage);

        void didLoadMoreUser(Map<String, Object> data);
    }

    public FilterUserPresenter(Context context, SearchView searchView) {
        super(context);
        this.view = searchView;
    }

    public void filterUser(int typeSearch){
        nextPage = "0";
        filterUser(false, typeSearch);
    }

    private void filterUser(boolean isLoadMore, int typeSearch) {
        this.isLoadMore = isLoadMore;

        FilterItem filterItem = ConfigManager.getInstance().getFilterUser();
        filterItem.setFilterType(typeSearch);
        FilterUserTask filterUserTask = new FilterUserTask(context, filterItem, nextPage, getCurrentUserItem());
        requestToServer(filterUserTask);
    }

    public final void loadMoreUser(int typeSearch) {
        filterUser(true, typeSearch);
    }

    public final boolean canLoadMoreUser() {
        return (!nextPage.isEmpty() && !"0".equals(nextPage)) ? true : false;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof FilterUserTask) {
            Map<String, Object> data = ((FilterUserTask) task).getDataResponse();
            nextPage = (String) data.get(FilterUserTask.NEXT_PAGE);
            if (isLoadMore) {
                view.didLoadMoreUser(data);
            } else {
                view.didFilterUser(data);
            }
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.didFilterUserError(errorCode, errorMessage);
    }
}
