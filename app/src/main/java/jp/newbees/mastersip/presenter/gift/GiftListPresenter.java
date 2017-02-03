package jp.newbees.mastersip.presenter.gift;

import android.content.Context;

import java.util.List;

import jp.newbees.mastersip.model.GiftChatItem;
import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetListGiftTask;
import jp.newbees.mastersip.network.api.SendGiftTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Constant;

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
        if (task instanceof GetListGiftTask) {
            giftListView.didLoadGiftsList((List<GiftItem>) task.getDataResponse());
        }else if(task instanceof SendGiftTask) {
            giftListView.didSendGiftSuccess(((SendGiftTask) task).getDataResponse());
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetListGiftTask) {
            giftListView.didLoadGiftsListFailure(errorCode, errorMessage);
        }else if (task instanceof SendGiftTask) {
            handSendGiftFailure(errorCode, errorMessage);
        }
    }

    private void handSendGiftFailure(int errorCode, String errorMessage) {
        if (errorCode == Constant.Error.NOT_ENOUGH_POINT) {
            giftListView.didSendGiftErrorCauseNotEnoughPoint();
        }else {
            giftListView.didLoadGiftsListFailure(errorCode, errorMessage);
        }
    }

    public void sendGiftToUser(UserItem userItem, GiftItem currentGiftSelected) {
        SendGiftTask sendGiftTask = new SendGiftTask(getContext(), userItem, currentGiftSelected);
        requestToServer(sendGiftTask);
    }

    public interface GiftListView {
        void didLoadGiftsList(List<GiftItem> giftItems);
        void didLoadGiftsListFailure(int errorCode, String errorMessage);
        void didSendGiftSuccess(GiftChatItem giftChatItem);
        void didSendGiftFailure(int errorCode, String errorMessage);
        void didSendGiftErrorCauseNotEnoughPoint();
    }
}
