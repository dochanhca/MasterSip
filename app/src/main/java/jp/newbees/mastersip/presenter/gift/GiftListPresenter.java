package jp.newbees.mastersip.presenter.gift;

import android.content.Context;

import java.util.List;

import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetListGiftTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 2/2/17.
 */

public class GiftListPresenter extends BasePresenter {

    private final GiftListView giftListView;

    public GiftListPresenter(Context context, GiftListView giftListView) {
        super(context);
        this.giftListView =  giftListView;
    }

    public void loadGiftList() {
        GetListGiftTask getListGiftTask = new GetListGiftTask(context);
        requestToServer(getListGiftTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        giftListView.didLoadGiftsList((List<GiftItem>) task.getDataResponse());
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        giftListView.didLoadGiftsListFailure(errorCode, errorMessage);
    }

    public interface GiftListView {
        void didLoadGiftsList(List<GiftItem> giftItems);
        void didLoadGiftsListFailure(int errorCode, String errorMessage);
    }
}
