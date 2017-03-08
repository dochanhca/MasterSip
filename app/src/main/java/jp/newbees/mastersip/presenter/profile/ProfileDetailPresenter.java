package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.network.api.FollowUserTask;
import jp.newbees.mastersip.network.api.GetListUserPhotos;
import jp.newbees.mastersip.network.api.GetProfileDetailTask;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableVoiceCallTask;
import jp.newbees.mastersip.network.api.UnFollowUserTask;
import jp.newbees.mastersip.presenter.call.BaseActionCallPresenter;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailPresenter extends BaseActionCallPresenter {

    private final ProfileDetailItemView view;
    private int nextId = -1;
    private boolean isLoadMore = false;

    public interface ProfileDetailItemView {
        void didGetProfileDetail(UserItem userItem);

        void didGetProfileDetailError(String errorMessage, int errorCode);

        void didGetListPhotos(GalleryItem galleryItem);

        void didLoadMoreListPhotos(GalleryItem galleryItem);

        void didGetListPhotosError(String errorMessage, int errorCode);

        void didFollowUser();

        void didFollowUserError(String errorMessage, int errorCode);

        void didUnFollowUser();

        void didUnFollowUserError(String errorMessage, int errorCode);

        void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableVoiceCallTask.Type type);

        void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode);

        void didCheckCallError(String errorMessage, int errorCode);
    }

    public ProfileDetailPresenter(Context context, ProfileDetailItemView view) {
        super(context);
        this.view = view;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetProfileDetailTask) {
            UserItem userItem = (UserItem) task.getDataResponse();
            view.didGetProfileDetail(userItem);
        } else if (task instanceof GetListUserPhotos) {
            GalleryItem galleryItem = (GalleryItem) task.getDataResponse();
            getNextIdForLoadMore(galleryItem);
            if (isLoadMore) {
                view.didLoadMoreListPhotos(galleryItem);
            } else {
                view.didGetListPhotos(galleryItem);
            }
        } else if (task instanceof FollowUserTask) {
            view.didFollowUser();

        } else if (task instanceof UnFollowUserTask) {
            view.didUnFollowUser();
        } else if (task instanceof SendMessageRequestEnableVoiceCallTask) {
            SendMessageRequestEnableVoiceCallTask.Type type = ((SendMessageRequestEnableVoiceCallTask) task).getDataResponse();
            view.didSendMsgRequestEnableSettingCall(type);
        } else if (task instanceof CheckCallTask) {
            handleResponseCheckCall(task);
        }
    }

    private void getNextIdForLoadMore(GalleryItem galleryItem) {
        if (!"".equals(galleryItem.getNextId())) {
            nextId = Integer.parseInt(galleryItem.getNextId());
        } else {
            nextId = -1;
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetProfileDetailTask) {
            view.didGetProfileDetailError(errorMessage, errorCode);
        } else if (task instanceof GetListUserPhotos) {
            view.didGetListPhotosError(errorMessage, errorCode);
        } else if (task instanceof FollowUserTask) {
            view.didFollowUserError(errorMessage, errorCode);
        } else if (task instanceof UnFollowUserTask) {
            view.didUnFollowUserError(errorMessage, errorCode);
        } else if (task instanceof SendMessageRequestEnableVoiceCallTask) {
            view.didSendMsgRequestEnableSettingCallError(errorMessage, errorCode);
        } else if (task instanceof CheckCallTask) {
            view.didCheckCallError(errorMessage, errorCode);
        }
    }

    public void getProfileDetail(String userId) {
        GetProfileDetailTask getProfileDetailTask = new GetProfileDetailTask(context, userId);
        requestToServer(getProfileDetailTask);
    }

    public void getListPhotos(String userId) {
        nextId = -1;
        getListPhotos(false, userId);
    }

    private void getListPhotos(boolean isLoadMore, String userId) {
        this.isLoadMore = isLoadMore;

        GetListUserPhotos getListUserPhotos = new
                GetListUserPhotos(context, nextId, userId);
        requestToServer(getListUserPhotos);

    }

    public void loadMoreListPhotos(String userId) {
        getListPhotos(true, userId);
    }

    public final boolean canLoadMoreUser() {
        return (nextId > 0) ? true : false;
    }

    public void followUser(String destUserId) {
        FollowUserTask followUserTask = new FollowUserTask(context, destUserId);
        requestToServer(followUserTask);
    }

    public void unFollowUser(String destUserId) {
        UnFollowUserTask unFollowUserTask = new UnFollowUserTask(context, destUserId);
        requestToServer(unFollowUserTask);
    }

    public void sendMessageRequestEnableSettingCall(UserItem userItem, SendMessageRequestEnableVoiceCallTask.Type type) {
        SendMessageRequestEnableVoiceCallTask task = new SendMessageRequestEnableVoiceCallTask(context, userItem, type);
        requestToServer(task);
    }

    public String getMessageSendRequestSuccess(UserItem userItem,SendMessageRequestEnableVoiceCallTask.Type type) {
        String message = "";
        switch (type) {
            case VOICE:
                message = String.format(context.getString(R.string.message_request_enable_voice_success), userItem.getUsername());
                break;
            case VIDEO:
                message = String.format(context.getString(R.string.message_request_enable_video_success), userItem.getUsername());
                break;
            case VIDEO_CHAT:
                message = String.format(context.getString(R.string.message_request_enable_video_chat_success), userItem.getUsername());
                break;
            default:
                break;
        }
        return message;
    }
}
