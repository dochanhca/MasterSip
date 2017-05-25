package jp.newbees.mastersip.presenter.mymenu;

import android.content.Context;

import java.util.List;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BlockUserTask;
import jp.newbees.mastersip.network.api.GetBlockListTask;
import jp.newbees.mastersip.network.api.UnBlockUserTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 5/25/17.
 */

public class BlockListPresenter extends BasePresenter {

    private BlockListView blockListView;

    public interface BlockListView {
        void didLoadBlockList(List<UserItem> userItems);

        void didLoadBlockListError(int errorCode, String errorMessage);

        void didSetUserBlocked(boolean isBlocked);

        void didSetUserBlockedError(int errorCode, String errorMessage);
    }

    public BlockListPresenter(Context context, BlockListView blockListView) {
        super(context);
        this.blockListView = blockListView;
    }

    public void loadData() {
        GetBlockListTask getBlockListTask = new GetBlockListTask(context);
        requestToServer(getBlockListTask);
    }

    public void setUserBlocked(String userId, boolean isBlocked) {
        if (isBlocked) {
            unBlockUser(userId);
        } else {
            blockUser(userId);
        }
    }

    private void blockUser(String userId) {
        BlockUserTask blockUserTask = new BlockUserTask(getContext(), userId);
        requestToServer(blockUserTask);
    }

    private void unBlockUser(String userId) {
        UnBlockUserTask unBlockUserTask = new UnBlockUserTask(getContext(), userId);
        requestToServer(unBlockUserTask);
    }

    private void getData(GetBlockListTask task) {
        List<UserItem> userItems = task.getDataResponse();
        blockListView.didLoadBlockList(userItems);

    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetBlockListTask) {
            getData((GetBlockListTask) task);
        } else if (task instanceof BlockUserTask) {
            blockListView.didSetUserBlocked(true);
        } else if (task instanceof UnBlockUserTask) {
            blockListView.didSetUserBlocked(false);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetBlockListTask) {
            blockListView.didLoadBlockListError(errorCode, errorMessage);
        } else if (task instanceof BlockUserTask || task instanceof UnBlockUserTask) {
            blockListView.didSetUserBlockedError(errorCode, errorMessage);
        }
    }
}
