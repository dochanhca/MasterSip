package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import com.android.volley.Response;

import java.io.InputStream;
import java.util.Map;

import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.FollowUserTask;
import jp.newbees.mastersip.network.api.GetChatHistoryTask;
import jp.newbees.mastersip.network.api.LoadChatHistoryResultItem;
import jp.newbees.mastersip.network.api.SendTextMessageTask;
import jp.newbees.mastersip.network.api.UnFollowUserTask;
import jp.newbees.mastersip.network.api.UpdateStateMessageTask;
import jp.newbees.mastersip.network.api.UploadFileForChatTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 1/11/17.
 */

public class ChatPresenter extends BasePresenter implements BaseUploadTask.ErrorListener,
        Response.Listener<BaseChatItem> {

    private ChatPresenterListener chatPresenterListener;

    public ChatPresenter(Context context, ChatPresenterListener chatPresenterListener) {
        super(context);
        this.chatPresenterListener = chatPresenterListener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask) {
            BaseChatItem result = ((SendTextMessageTask) task).getDataResponse();
            chatPresenterListener.didSendChatToServer(result);
        } else if (task instanceof UpdateStateMessageTask) {
            BaseChatItem result = ((UpdateStateMessageTask) task).getDataResponse();
            chatPresenterListener.didSendingReadMessageToServer(result);
        } else if (task instanceof GetChatHistoryTask) {
            LoadChatHistoryResultItem resultItem = ((GetChatHistoryTask) task).getDataResponse();
            chatPresenterListener.didLoadChatHistory(resultItem);
        } else if (task instanceof FollowUserTask) {
            chatPresenterListener.didFollowUser();
        } else if (task instanceof UnFollowUserTask) {
            chatPresenterListener.didUnFollowUser();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SendTextMessageTask) {
            chatPresenterListener.didChatError(errorCode, errorMessage);
        } else if (task instanceof UpdateStateMessageTask) {
            chatPresenterListener.didSendingReadMessageToServerError(errorCode, errorMessage);
        } else if (task instanceof GetChatHistoryTask) {
            chatPresenterListener.didLoadChatHistoryError(errorCode, errorMessage);
        } else if (task instanceof FollowUserTask) {
            chatPresenterListener.didFollowUserError(errorMessage, errorCode);
        } else if (task instanceof UnFollowUserTask) {
            chatPresenterListener.didUnFollowUserError(errorMessage, errorCode);
        }
    }


    /**
     * Upload image error
     *
     * @param errorCode
     * @param errorMessage
     */
    @Override
    public void onErrorListener(int errorCode, String errorMessage) {
        chatPresenterListener.didUploadImageToServerError(errorCode, errorMessage);
    }

    /**
     * Upload image success
     *
     * @param response
     */
    @Override
    public void onResponse(BaseChatItem response) {
        chatPresenterListener.didUploadImageToServer((ImageChatItem) response);
    }

    public final void sendText(String content, UserItem sendee) {
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        TextChatItem textChatItem = new TextChatItem(content, BaseChatItem.RoomType.ROOM_CHAT_CHAT, sender, sendee);
        SendTextMessageTask messageTask = new SendTextMessageTask(context, textChatItem);
        requestToServer(messageTask);
    }

    public final void sendingReadMessageToServer(BaseChatItem baseChatItem) {
        UpdateStateMessageTask updateStateMessageTask = new UpdateStateMessageTask(context, baseChatItem);
        requestToServer(updateStateMessageTask);
    }

    public void sendingReadMessageUsingLinPhone(BaseChatItem baseChatItem, UserItem sender) {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();
        LinphoneHandler.getInstance().sendReadMessageEvent(currentUser.getSipItem().getExtension(),
                sender.getSipItem().getExtension(), baseChatItem);
    }

    public void loadChatHistory(UserItem friendUser, int lastMessageId) {
        UserItem owner = ConfigManager.getInstance().getCurrentUser();
        GetChatHistoryTask getChatHistoryTask = new GetChatHistoryTask(context, owner.getUserId(),
                friendUser.getUserId(), lastMessageId);
        requestToServer(getChatHistoryTask);
    }

    public final void sendFile(String receiverExtension, int typeUpload, InputStream file) {
        UserItem sender = getCurrentUserItem();
        UploadFileForChatTask uploadFileForChatTask = new UploadFileForChatTask(context, receiverExtension,
                sender, typeUpload, file);
        uploadFileForChatTask.request(this, this);
    }


    public boolean isMessageOfCurrentUser(UserItem user, UserItem currentUser) {
        return currentUser.getSipItem().getExtension().equalsIgnoreCase(user.getSipItem().getExtension());
    }

    /**
     * follow and unfollow use
     *
     * @param userItem
     */
    public void doFollowUser(UserItem userItem) {
        if (userItem.getRelationshipItem().isFollowed() == RelationshipItem.FOLLOW) {
            unFollowUser(userItem.getUserId());
        } else {
            followUser(userItem.getUserId());
        }
    }

    /**
     * @param destUserId
     */
    private void followUser(String destUserId) {
        FollowUserTask followUserTask = new FollowUserTask(context, destUserId);
        requestToServer(followUserTask);
    }

    /**
     * @param destUserId
     */
    private void unFollowUser(String destUserId) {
        UnFollowUserTask unFollowUserTask = new UnFollowUserTask(context, destUserId);
        requestToServer(unFollowUserTask);
    }

    /**
     * @param currentUser
     * @param members
     * @return
     */
    public UserItem getUserHasRelationShipItem(UserItem currentUser, Map<String, UserItem> members) {
        return members.get(currentUser.getUserId());
    }

    public RelationshipItem setUserHasRelationShipItem(UserItem currentUser, Map<String, UserItem> members, int follow) {
        RelationshipItem relationshipItem = members.get(currentUser.getUserId()).getRelationshipItem();
        relationshipItem.setFollowed(follow);
        return relationshipItem;
    }

    public interface ChatPresenterListener {

        void didSendChatToServer(BaseChatItem baseChatItem);

        void didChatError(int errorCode, String errorMessage);

        void didSendingReadMessageToServer(BaseChatItem baseChatItem);

        void didSendingReadMessageToServerError(int errorCode, String errorMessage);

        void didLoadChatHistory(LoadChatHistoryResultItem resultItem);

        void didLoadChatHistoryError(int errorCode, String errorMessage);

        void didUploadImageToServer(ImageChatItem imageChatItem);

        void didUploadImageToServerError(int errorCode, String errorMessage);

        void didFollowUser();

        void didFollowUserError(String errorMessage, int errorCode);

        void didUnFollowUser();

        void didUnFollowUserError(String errorMessage, int errorCode);

    }
}
