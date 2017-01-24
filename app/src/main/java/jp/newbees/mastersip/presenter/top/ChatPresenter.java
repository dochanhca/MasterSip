package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import com.android.volley.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;

import jp.newbees.mastersip.eventbus.SendingReadMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.network.api.SendTextMessageTask;
import jp.newbees.mastersip.network.api.UpdateStateMessageTask;
import jp.newbees.mastersip.network.api.UploadFileForChatTask;
import jp.newbees.mastersip.presenter.call.BaseActionCallPresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 1/11/17.
 */

public class ChatPresenter extends BaseActionCallPresenter implements BaseUploadTask.ErrorListener, Response.Listener<BaseChatItem> {

    private ChatPresenterListener chatPresenterListener;
    private SendingReadMessageToServerListener sendingReadMessageToServerListener;
    private UploadImageToServerListener uploadImageToServerListener;

    public ChatPresenter(Context context, ChatPresenterListener chatPresenterListener,
                         SendingReadMessageToServerListener sendingReadMessageToServerListener,
                         UploadImageToServerListener uploadImageToServerListener) {
        super(context);
        this.chatPresenterListener = chatPresenterListener;
        this.sendingReadMessageToServerListener = sendingReadMessageToServerListener;
        this.uploadImageToServerListener = uploadImageToServerListener;
    }

    public interface ChatPresenterListener {
        void didSendChatToServer(BaseChatItem baseChatItem);

        void didChatError(int errorCode, String errorMessage);

    }

    public interface SendingReadMessageToServerListener {
        void didSendingReadMessageToServer(BaseChatItem baseChatItem);

        void didSendingReadMessageToServerError(int errorCode, String errorMessage);
    }

    public interface UploadImageToServerListener {
        void didUploadImageToServer(ImageChatItem imageChatItem);

        void didUploadImageToServerError(int errorCode, String errorMessage);
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
        EventBus.getDefault().post(new SendingReadMessageEvent(baseChatItem, currentUser, sender));
    }

    public final void sendFile(String receiverExtension, int typeUpload, InputStream file) {
        UserItem sender = getCurrentUserItem();
        UploadFileForChatTask uploadFileForChatTask = new UploadFileForChatTask(context, receiverExtension,
                sender, typeUpload, file);
        uploadFileForChatTask.request(this, this);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask) {
            BaseChatItem result = ((SendTextMessageTask) task).getDataResponse();
            chatPresenterListener.didSendChatToServer(result);
        } else if (task instanceof UpdateStateMessageTask) {
            BaseChatItem result = ((UpdateStateMessageTask) task).getDataResponse();
            sendingReadMessageToServerListener.didSendingReadMessageToServer(result);
        } else if (task instanceof CheckCallTask) {
            handleResponseCheckCall(task);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SendTextMessageTask) {
            chatPresenterListener.didChatError(errorCode, errorMessage);
        } else if (task instanceof UpdateStateMessageTask) {
            sendingReadMessageToServerListener.didSendingReadMessageToServerError(errorCode, errorMessage);
        } else if (task instanceof CheckCallTask) {
            Logger.e(TAG, errorMessage);
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
        uploadImageToServerListener.didUploadImageToServerError(errorCode, errorMessage);
    }

    /**
     * Upload image success
     *
     * @param response
     */
    @Override
    public void onResponse(BaseChatItem response) {
        uploadImageToServerListener.didUploadImageToServer((ImageChatItem) response);
    }

    public boolean isMessageOfCurrentUser(UserItem user, UserItem currentUser) {
        return currentUser.getSipItem().getExtension().equalsIgnoreCase(user.getSipItem().getExtension());
    }
}
