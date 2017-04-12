package jp.newbees.mastersip.presenter.chatting;

import android.content.Context;

import com.android.volley.Response;

import java.io.InputStream;
import java.util.Map;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.FollowUserTask;
import jp.newbees.mastersip.network.api.GetChatHistoryTask;
import jp.newbees.mastersip.network.api.LoadChatHistoryResultItem;
import jp.newbees.mastersip.network.api.UnFollowUserTask;
import jp.newbees.mastersip.network.api.UploadFileForChatTask;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 1/11/17.
 */

public class ChatPresenter extends BasicChatPresenter implements BaseUploadTask.ErrorListener,
        Response.Listener<BaseChatItem>{

    private ChatListener chatPresenterListener;

    public ChatPresenter(Context context, ChatListener chatPresenterListener) {
        super(context, chatPresenterListener, chatPresenterListener);
        this.chatPresenterListener = chatPresenterListener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        super.didResponseTask(task);
        if (task instanceof GetChatHistoryTask) {
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
        super.didErrorRequestTask(task, errorCode, errorMessage);
        if (task instanceof GetChatHistoryTask) {
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
}
