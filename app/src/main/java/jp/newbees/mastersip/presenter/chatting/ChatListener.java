package jp.newbees.mastersip.presenter.chatting;

import jp.newbees.mastersip.eventbus.ReceivingReadMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.network.api.LoadChatHistoryResultItem;

/**
 * Created by thangit14 on 4/10/17.
 */

public interface ChatListener extends ReceiveChatTextListener,SendChatTextListener {
    void didLoadChatHistory(LoadChatHistoryResultItem resultItem);

    void didLoadChatHistoryError(int errorCode, String errorMessage);

    void didUploadImageToServer(ImageChatItem imageChatItem);

    void didUploadImageToServerError(int errorCode, String errorMessage);

    void didFollowUser();

    void didFollowUserError(String errorMessage, int errorCode);

    void didUnFollowUser();

    void didUnFollowUserError(String errorMessage, int errorCode);

    void onStateMessageChange(ReceivingReadMessageEvent receivingReadMessageEvent);

    void didSendingReadMessageToServer(BaseChatItem baseChatItem);

    void didSendingReadMessageToServerError(int errorCode, String errorMessage);
}
